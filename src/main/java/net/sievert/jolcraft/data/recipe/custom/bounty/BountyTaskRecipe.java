package net.sievert.jolcraft.data.recipe.custom.bounty;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.resources.RegistryFixedCodec;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.bounty.BountyData;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyTaskRecipe implements Recipe<BountyRecipeInput> {

    // =====================================================================
    // Task
    // =====================================================================

    public enum Task {
        COLLECT,
        SLAY;

        public String id() {
            return name().toLowerCase();
        }
    }

    private static final Codec<Task> TASK_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        String key = s.trim().toUpperCase().replace('-', '_').replace(' ', '_');
                        try {
                            return DataResult.success(Task.valueOf(key));
                        } catch (IllegalArgumentException ex) {
                            return DataResult.error(() -> "Unknown objective.task '" + s + "'. Valid: collect, slay");
                        }
                    },
                    Task::id
            );

    // =====================================================================
    // Amount (int OR {min_count,max_count})
    // =====================================================================

    public record Amount(int min, int max) {

        public static Amount fixed(int value) {
            return new Amount(value, value);
        }

        public int roll(RandomSource random) {
            return (min == max) ? min : (min + random.nextInt(max - min + 1));
        }

        private static final Codec<Amount> OBJECT_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MIN, JolCraftDictionary.COUNT)).forGetter(Amount::min),
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAX, JolCraftDictionary.COUNT)).forGetter(Amount::max)
                ).apply(inst, Amount::new));

        public static final Codec<Amount> CODEC =
                Codec.either(Codec.INT, OBJECT_CODEC).xmap(
                        e -> e.map(Amount::fixed, a -> a),
                        a -> (a.min == a.max) ? Either.left(a.min) : Either.right(a)
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, Amount> STREAM_CODEC =
                StreamCodec.of(
                        (buf, a) -> {
                            buf.writeVarInt(a.min);
                            buf.writeVarInt(a.max);
                        },
                        buf -> new Amount(buf.readVarInt(), buf.readVarInt())
                );
    }

    // =====================================================================
    // Task ingredient (item OR entity)
    // =====================================================================

    @SuppressWarnings("deprecation")
    public sealed interface TaskIngredient permits TaskIngredient.ItemIngredient, TaskIngredient.EntityIngredient {

        record ItemIngredient(Holder<Item> item) implements TaskIngredient {}
        record EntityIngredient(Holder<EntityType<?>> entity) implements TaskIngredient {}

        record Raw(Optional<Holder<Item>> item, Optional<Holder<EntityType<?>>> entity) {}

        MapCodec<Raw> RAW_CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        RegistryFixedCodec.create(Registries.ITEM)
                                .optionalFieldOf(JolCraftDictionary.ITEM)
                                .forGetter(Raw::item),
                        RegistryFixedCodec.create(Registries.ENTITY_TYPE)
                                .optionalFieldOf(JolCraftDictionary.ENTITY)
                                .forGetter(Raw::entity)
                ).apply(inst, Raw::new));

        Codec<TaskIngredient> CODEC =
                RAW_CODEC.codec().flatXmap(
                        raw -> {
                            boolean hasItem = raw.item().isPresent();
                            boolean hasEntity = raw.entity().isPresent();

                            if (hasItem == hasEntity) {
                                return DataResult.error(() -> "objective.ingredient must define exactly one of 'item' or 'entity'");
                            }

                            return hasItem
                                    ? DataResult.success(new ItemIngredient(raw.item().get()))
                                    : DataResult.success(new EntityIngredient(raw.entity().get()));
                        },
                        ing -> {
                            if (ing instanceof ItemIngredient(Holder<Item> item)) {
                                return DataResult.success(new Raw(Optional.of(item), Optional.empty()));
                            }
                            if (ing instanceof EntityIngredient(Holder<EntityType<?>> entity)) {
                                return DataResult.success(new Raw(Optional.empty(), Optional.of(entity)));
                            }
                            return DataResult.error(() -> "Unknown TaskIngredient variant");
                        }
                );

        StreamCodec<RegistryFriendlyByteBuf, TaskIngredient> STREAM_CODEC =
                StreamCodec.of(
                        (buf, ing) -> {
                            if (ing instanceof EntityIngredient(Holder<EntityType<?>> entity)) {
                                buf.writeBoolean(true);
                                ResourceLocation id = entity.unwrapKey()
                                        .orElseThrow(() -> new IllegalStateException("Unkeyed entity holder in TaskIngredient"))
                                        .location();
                                buf.writeResourceLocation(id);
                                return;
                            }

                            buf.writeBoolean(false);

                            ItemIngredient ii = (ItemIngredient) ing;
                            ResourceLocation id = ii.item().unwrapKey()
                                    .orElseThrow(() -> new IllegalStateException("Unkeyed item holder in TaskIngredient"))
                                    .location();

                            buf.writeResourceLocation(id);
                        },
                        buf -> {
                            boolean isEntity = buf.readBoolean();
                            ResourceLocation id = buf.readResourceLocation();

                            if (isEntity) {
                                Registry<EntityType<?>> reg = buf.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE);
                                EntityType<?> value = reg.getValue(id);
                                if (value == null) {
                                    throw new IllegalStateException("Unknown entity id in TaskIngredient: " + id);
                                }
                                Holder<EntityType<?>> holder = value.builtInRegistryHolder();
                                return new EntityIngredient(holder);
                            }

                            Registry<Item> reg = buf.registryAccess().lookupOrThrow(Registries.ITEM);
                            Item value = reg.getValue(id);
                            if (value == null) {
                                throw new IllegalStateException("Unknown item id in TaskIngredient: " + id);
                            }
                            return new ItemIngredient(value.builtInRegistryHolder());
                        }
                );
    }

    // =====================================================================
    // Objective wrapper (task + ingredient + amount-range)
    // =====================================================================

    public record TaskObjective(Task task, TaskIngredient ingredient, Amount amount) {

        public BountyData.BountyObjective preview() {
            return toBountyObjective(amount.min());
        }

        public BountyData.BountyObjective roll(RandomSource random) {
            return toBountyObjective(amount.roll(random));
        }

        private BountyData.BountyObjective toBountyObjective(int fixedAmount) {
            return switch (task) {
                case COLLECT -> {
                    TaskIngredient.ItemIngredient ii = (TaskIngredient.ItemIngredient) ingredient;
                    yield new BountyData.BountyObjective.ItemObjective(ii.item(), fixedAmount);
                }
                case SLAY -> {
                    TaskIngredient.EntityIngredient ei = (TaskIngredient.EntityIngredient) ingredient;
                    yield new BountyData.BountyObjective.EntityObjective(ei.entity(), fixedAmount);
                }
            };
        }

        public static final Codec<TaskObjective> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        TASK_CODEC.fieldOf(JolCraftDictionary.TASK).forGetter(TaskObjective::task),
                        TaskIngredient.CODEC.fieldOf(JolCraftDictionary.INGREDIENT).forGetter(TaskObjective::ingredient),
                        Amount.CODEC.fieldOf(JolCraftDictionary.AMOUNT).forGetter(TaskObjective::amount)
                ).apply(inst, TaskObjective::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TaskObjective> STREAM_CODEC =
                StreamCodec.of(
                        (buf, o) -> {
                            buf.writeEnum(o.task);
                            TaskIngredient.STREAM_CODEC.encode(buf, o.ingredient);
                            Amount.STREAM_CODEC.encode(buf, o.amount);
                        },
                        buf -> new TaskObjective(
                                buf.readEnum(Task.class),
                                TaskIngredient.STREAM_CODEC.decode(buf),
                                Amount.STREAM_CODEC.decode(buf)
                        )
                );
    }

    // =====================================================================
    // Recipe fields
    // =====================================================================

    private final Holder<Item> result; // NEW: output item (count always 1)
    private final BountyType bountyType;
    private final int tier; // 1..5
    private final int weight;
    private final TaskObjective objective;

    public BountyTaskRecipe(Holder<Item> result, BountyType bountyType, int tier, int weight, TaskObjective objective) {
        this.result = result;
        this.bountyType = bountyType;
        this.tier = tier;
        this.weight = weight;
        this.objective = objective;
    }

    public Holder<Item> result() { return result; }
    public BountyType bountyType() { return bountyType; }
    public int tier() { return tier; }
    public int weight() { return weight; }
    public TaskObjective objective() { return objective; }


    // =====================================================================
    // Recipe implementation
    // =====================================================================

    @Override
    public boolean matches(BountyRecipeInput in, Level level) {
        if (level.isClientSide) return false;

        ItemStack base = in.redeemStack();
        if (base.isEmpty()) return false;

        if (!base.is(JolCraftItems.BOUNTY.get())) return false;

        return in.type() == bountyType && in.tier().getValue() == tier;
    }

    @Override
    public ItemStack assemble(BountyRecipeInput in, HolderLookup.Provider registries) {
        // NEW: output is declared result item (always count 1)
        ItemStack out = new ItemStack(result.value(), 1);

        out.set(JolCraftDataComponents.BOUNTY_TYPE.get(), bountyType.getId());
        out.set(JolCraftDataComponents.BOUNTY_TIER.get(), tier);

        // assemble() has no RandomSource, so we store a deterministic preview objective (min).
        out.set(JolCraftDataComponents.BOUNTY_DATA.get(), new BountyData(objective.preview()));

        out.set(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
        out.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), false);

        return out;
    }

    @Override
    public RecipeSerializer<? extends Recipe<BountyRecipeInput>> getSerializer() {
        return JolCraftRecipes.BOUNTY_TASK_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<BountyRecipeInput>> getType() {
        return JolCraftRecipes.BOUNTY_TASK_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    // =====================================================================
    // Serializer (CODEC + STREAM_CODEC)
    // =====================================================================

    @SuppressWarnings("deprecation")
    public static final class Serializer implements RecipeSerializer<BountyTaskRecipe> {

        public static final MapCodec<BountyTaskRecipe> CODEC =
                RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<BountyTaskRecipe> inst) -> inst.group(
                        // NEW: result item (no count)
                        RegistryFixedCodec.create(Registries.ITEM)
                                .fieldOf(JolCraftDictionary.RESULT)
                                .forGetter(BountyTaskRecipe::result),

                        BountyRecipe.BOUNTY_TYPE_CODEC
                                .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.BOUNTY, JolCraftDictionary.TYPE))
                                .forGetter(BountyTaskRecipe::bountyType),

                        Codec.INT
                                .fieldOf(JolCraftDictionary.TIER)
                                .forGetter(BountyTaskRecipe::tier),

                        Codec.INT
                                .optionalFieldOf(JolCraftDictionary.WEIGHT, 1)
                                .forGetter(BountyTaskRecipe::weight),

                        TaskObjective.CODEC
                                .fieldOf(JolCraftDictionary.OBJECTIVE)
                                .forGetter(BountyTaskRecipe::objective)
                ).apply(inst, BountyTaskRecipe::new)).validate(Serializer::validate);

        public static final StreamCodec<RegistryFriendlyByteBuf, BountyTaskRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<BountyTaskRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BountyTaskRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static DataResult<BountyTaskRecipe> validate(BountyTaskRecipe r) {
            if (r.result == null) {
                return DataResult.error(() -> "result is required");
            }
            if (r.result.value() == Items.AIR) {
                return DataResult.error(() -> "result must not be air");
            }

            Item res = r.result.value();
            if (res != JolCraftItems.BOUNTY.get() && res != JolCraftItems.BOUNTY_CRATE.get()) {
                return DataResult.error(() -> "result must be jolcraft:bounty or jolcraft:bounty_crate (got "
                        + r.result.unwrapKey().map(k -> k.location().toString()).orElse("<unkeyed>") + ")");
            }

            var typeOk = BountyRecipe.validateType(r.bountyType);
            var typeError = typeOk.error();
            if (typeError.isPresent()) {
                return DataResult.error(typeError.get()::message);
            }

            var tierOk = BountyRecipe.validateTier(r.tier);
            var tierError = tierOk.error();
            if (tierError.isPresent()) {
                return DataResult.error(tierError.get()::message);
            }

            if (r.weight < 1) {
                return DataResult.error(() -> "weight must be >= 1 (got " + r.weight + ")");
            }

            if (r.objective == null) {
                return DataResult.error(() -> "objective is required");
            }

            if (r.objective.ingredient == null) {
                return DataResult.error(() -> "objective.ingredient is required");
            }

            Amount a = r.objective.amount;
            if (a.min() < 1 || a.max() < a.min()) {
                return DataResult.error(() -> "objective.amount must be >= 1 and max>=min");
            }

            return switch (r.objective.task) {
                case COLLECT -> {
                    if (!(r.objective.ingredient instanceof TaskIngredient.ItemIngredient(Holder<Item> item))) {
                        yield DataResult.error(() -> "objective.task=collect requires objective.ingredient.item");
                    }

                    if (item.value() == Items.AIR) {
                        yield DataResult.error(() -> "objective.ingredient.item must not be air");
                    }

                    yield DataResult.success(r);
                }
                case SLAY -> {
                    if (!(r.objective.ingredient instanceof TaskIngredient.EntityIngredient)) {
                        yield DataResult.error(() -> "objective.task=slay requires objective.ingredient.entity");
                    }
                    yield DataResult.success(r);
                }
            };
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BountyTaskRecipe r) {
            // NEW: result item id
            ResourceLocation resultId = r.result.unwrapKey()
                    .orElseThrow(() -> new IllegalStateException("Unkeyed item holder in BountyTaskRecipe.result"))
                    .location();
            buf.writeResourceLocation(resultId);

            buf.writeUtf(r.bountyType.getId());
            buf.writeVarInt(r.tier);
            buf.writeVarInt(r.weight);

            TaskObjective.STREAM_CODEC.encode(buf, r.objective);
        }

        private static BountyTaskRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            // NEW: read result item id
            ResourceLocation resultId = buf.readResourceLocation();
            Registry<Item> itemReg = buf.registryAccess().lookupOrThrow(Registries.ITEM);
            Item resultItem = itemReg.getValue(resultId);
            if (resultItem == null) {
                throw new IllegalStateException("Unknown result item id in BountyTaskRecipe: " + resultId);
            }
            Holder<Item> result = resultItem.builtInRegistryHolder();

            BountyType type = BountyType.fromString(buf.readUtf());
            int tier = buf.readVarInt();
            int weight = buf.readVarInt();

            TaskObjective objective = TaskObjective.STREAM_CODEC.decode(buf);

            if (type == null) type = BountyType.UNKNOWN;

            return new BountyTaskRecipe(result, type, tier, weight, objective);
        }
    }
}