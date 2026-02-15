package net.sievert.jolcraft.data.recipe.custom.hand;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.util.JolCraftStrings;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class HandInteractionRecipe implements Recipe<HandInteractionRecipeInput> {

    // =====================================================================
    // Amount (int OR {min_count,max_count})
    // =====================================================================

    public record Amount(int min, int max) {
        public static Amount fixed(int value) { return new Amount(value, value); }

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
    // Rolls (int OR {min_count,max_count})
    // =====================================================================

    public record Rolls(int min, int max) {
        public static Rolls fixed(int value) { return new Rolls(value, value); }

        public int roll(RandomSource random) {
            return (min == max) ? min : (min + random.nextInt(max - min + 1));
        }

        private static final Codec<Rolls> OBJECT_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MIN, JolCraftDictionary.COUNT)).forGetter(Rolls::min),
                        Codec.INT.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAX, JolCraftDictionary.COUNT)).forGetter(Rolls::max)
                ).apply(inst, Rolls::new));

        public static final Codec<Rolls> CODEC =
                Codec.either(Codec.INT, OBJECT_CODEC).xmap(
                        e -> e.map(Rolls::fixed, r -> r),
                        r -> (r.min == r.max) ? Either.left(r.min) : Either.right(r)
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, Rolls> STREAM_CODEC =
                StreamCodec.of(
                        (buf, r) -> {
                            buf.writeVarInt(r.min);
                            buf.writeVarInt(r.max);
                        },
                        buf -> new Rolls(buf.readVarInt(), buf.readVarInt())
                );
    }

    // =====================================================================
    // IngredientChoice (item OR tag) + stable tag ordering
    // =====================================================================

    public sealed interface IngredientChoice permits IngredientChoice.ItemChoice, IngredientChoice.TagChoice {

        record ItemChoice(Holder<Item> item) implements IngredientChoice {}
        record TagChoice(TagKey<Item> tag) implements IngredientChoice {}

        record Raw(Optional<Holder<Item>> item, Optional<TagKey<Item>> tag) {}

        MapCodec<Raw> RAW_CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        RegistryFixedCodec.create(Registries.ITEM)
                                .optionalFieldOf(JolCraftDictionary.ITEM)
                                .forGetter(Raw::item),
                        TagKey.codec(Registries.ITEM)
                                .optionalFieldOf(JolCraftDictionary.TAG)
                                .forGetter(Raw::tag)
                ).apply(inst, Raw::new));

        Codec<IngredientChoice> CODEC =
                RAW_CODEC.codec().flatXmap(
                        raw -> {
                            boolean hasItem = raw.item().isPresent();
                            boolean hasTag = raw.tag().isPresent();

                            if (hasItem == hasTag) {
                                return DataResult.error(() -> "ingredient must define exactly one of 'item' or 'tag'");
                            }

                            return hasItem
                                    ? DataResult.success(new ItemChoice(raw.item().get()))
                                    : DataResult.success(new TagChoice(raw.tag().get()));
                        },
                        ing -> {
                            if (ing instanceof ItemChoice(Holder<Item> item)) {
                                return DataResult.success(new Raw(Optional.of(item), Optional.empty()));
                            }
                            if (ing instanceof TagChoice(TagKey<Item> tag)) {
                                return DataResult.success(new Raw(Optional.empty(), Optional.of(tag)));
                            }
                            return DataResult.error(() -> "Unknown IngredientChoice variant");
                        }
                );

        default boolean matches(ItemStack stack) {
            if (this instanceof ItemChoice(Holder<Item> item)) return stack.is(item);
            TagChoice tc = (TagChoice) this;
            return stack.is(tc.tag());
        }

        default ItemStack preview(HolderLookup.Provider registries, int count) {
            if (this instanceof ItemChoice(Holder<Item> item)) {
                return new ItemStack(item.value(), count);
            }

            TagChoice tc = (TagChoice) this;
            List<Holder<Item>> holders = resolveSortedTagHolders(registries, tc.tag());
            if (holders.isEmpty()) return ItemStack.EMPTY;

            Holder<Item> first = holders.getFirst();
            return new ItemStack(first.value(), count);
        }

        default ItemStack roll(HolderLookup.Provider registries, RandomSource random, int count) {
            if (this instanceof ItemChoice(Holder<Item> item)) {
                return new ItemStack(item.value(), count);
            }

            TagChoice tc = (TagChoice) this;
            List<Holder<Item>> holders = resolveSortedTagHolders(registries, tc.tag());
            if (holders.isEmpty()) return ItemStack.EMPTY;

            Holder<Item> chosen = holders.get(random.nextInt(holders.size()));
            return new ItemStack(chosen.value(), count);
        }

        private static List<Holder<Item>> resolveSortedTagHolders(HolderLookup.Provider registries, TagKey<Item> tag) {
            var itemLookup = registries.lookupOrThrow(Registries.ITEM);
            var setOpt = itemLookup.get(tag);
            return setOpt.map(holders -> holders.stream()
                    .sorted(Comparator.comparing(h -> {
                        if (h instanceof Holder.Reference<Item> ref) {
                            return ref.key().location().toString();
                        }
                        return "";
                    }))
                    .toList()).orElseGet(List::of);

        }

        // ---- STREAM ----
        // boolean isTag
        // if false: item ResourceLocation
        // if true : tag ResourceLocation
        StreamCodec<RegistryFriendlyByteBuf, IngredientChoice> STREAM_CODEC =
                StreamCodec.of(
                        (buf, ing) -> {
                            if (ing instanceof TagChoice(TagKey<Item> tag)) {
                                buf.writeBoolean(true);
                                buf.writeResourceLocation(tag.location());
                                return;
                            }

                            buf.writeBoolean(false);

                            ItemChoice ic = (ItemChoice) ing;
                            Registry<Item> items = buf.registryAccess().lookupOrThrow(Registries.ITEM);

                            Item value = ic.item().value();
                            ResourceLocation id = items.getKey(value);
                            if (id == null) {
                                throw new IllegalStateException("Unregistered item in IngredientChoice: " + value);
                            }

                            buf.writeResourceLocation(id);
                        },
                        buf -> {
                            boolean isTag = buf.readBoolean();
                            if (isTag) {
                                return new TagChoice(TagKey.create(Registries.ITEM, buf.readResourceLocation()));
                            }

                            Registry<Item> items = buf.registryAccess().lookupOrThrow(Registries.ITEM);

                            ResourceLocation id = buf.readResourceLocation();
                            Item value = items.getValue(id);
                            if (value == null) {
                                throw new IllegalStateException("Unknown item id in IngredientChoice: " + id);
                            }

                            return new ItemChoice(Holder.direct(value));
                        }
                );
    }

    // =====================================================================
    // Ingredient action (consume / damage / catalyst)
    // =====================================================================

    public enum IngredientActionType {
        CONSUME,
        DAMAGE,
        CATALYST;

        public String id() { return name().toLowerCase(); }
    }

    private static final Codec<IngredientActionType> ACTION_TYPE_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        String key = s.trim().toUpperCase().replace('-', '_').replace(' ', '_');
                        try {
                            return DataResult.success(IngredientActionType.valueOf(key));
                        } catch (IllegalArgumentException ex) {
                            return DataResult.error(() -> "Unknown action.type '" + s + "'. Valid: consume, damage, catalyst");
                        }
                    },
                    IngredientActionType::id
            );

    public record IngredientAction(IngredientActionType type, Optional<Integer> amount) {

        public static final Codec<IngredientAction> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        ACTION_TYPE_CODEC.fieldOf(JolCraftDictionary.TYPE).forGetter(IngredientAction::type),
                        Codec.INT.optionalFieldOf(JolCraftDictionary.AMOUNT).forGetter(IngredientAction::amount)
                ).apply(inst, IngredientAction::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IngredientAction> STREAM_CODEC =
                StreamCodec.of(
                        (buf, a) -> {
                            buf.writeEnum(a.type);
                            buf.writeBoolean(a.amount.isPresent());
                            a.amount.ifPresent(buf::writeVarInt);
                        },
                        buf -> {
                            IngredientActionType t = buf.readEnum(IngredientActionType.class);
                            Optional<Integer> amt = buf.readBoolean() ? Optional.of(buf.readVarInt()) : Optional.empty();
                            return new IngredientAction(t, amt);
                        }
                );
    }

    // =====================================================================
    // IngredientEntry (ingredient + action)
    // =====================================================================

    public record IngredientEntry(IngredientChoice ingredient, IngredientAction action) {
        public boolean matches(ItemStack stack) { return ingredient.matches(stack); }

        public static final Codec<IngredientEntry> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        IngredientChoice.CODEC.fieldOf(JolCraftDictionary.INGREDIENT).forGetter(IngredientEntry::ingredient),
                        IngredientAction.CODEC.fieldOf(JolCraftDictionary.ACTION).forGetter(IngredientEntry::action)
                ).apply(inst, IngredientEntry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IngredientEntry> STREAM_CODEC =
                StreamCodec.of(
                        (buf, e) -> {
                            IngredientChoice.STREAM_CODEC.encode(buf, e.ingredient);
                            IngredientAction.STREAM_CODEC.encode(buf, e.action);
                        },
                        buf -> new IngredientEntry(
                                IngredientChoice.STREAM_CODEC.decode(buf),
                                IngredientAction.STREAM_CODEC.decode(buf)
                        )
                );
    }

    // =====================================================================
    // ResultEntry (ingredient + amount + weight + hooks)
    // =====================================================================

    private static final Codec<ResourceKey<EnchantmentProvider>> ENCHANT_PROVIDER_CODEC =
            ResourceLocation.CODEC.xmap(
                    id -> ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, id),
                    ResourceKey::location
            );

    public record ResultEntry(
            IngredientChoice ingredient,
            Amount amount,
            int weight,
            Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider,
            Optional<String> stackModifierId,
            Optional<DataComponentPatch> resultPatch
    ) {
        public ItemStack preview(HolderLookup.Provider registries) {
            return ingredient.preview(registries, Math.max(1, amount.min()));
        }

        /**
         * IMPORTANT: returns BASE stack only (no enchant/mod/patch).
         * Your interaction engine applies transforms in order:
         * base -> enchantmentProvider -> stackModifier -> resultPatch
         */
        public ItemStack rollBase(HolderLookup.Provider registries, RandomSource random) {
            int count = Math.max(1, amount.roll(random));
            return ingredient.roll(registries, random, count);
        }

        public static final Codec<ResultEntry> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        IngredientChoice.CODEC.fieldOf(JolCraftDictionary.INGREDIENT).forGetter(ResultEntry::ingredient),
                        Amount.CODEC.fieldOf(JolCraftDictionary.AMOUNT).forGetter(ResultEntry::amount),
                        Codec.INT.fieldOf(JolCraftDictionary.WEIGHT).forGetter(ResultEntry::weight),

                        ENCHANT_PROVIDER_CODEC.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.ENCHANTMENT, JolCraftDictionary.PROVIDER))
                                .forGetter(ResultEntry::enchantmentProvider),
                        Codec.STRING.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.STACK, JolCraftDictionary.MODIFIER))
                                .forGetter(ResultEntry::stackModifierId),
                        DataComponentPatch.CODEC.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.RESULT, JolCraftDictionary.PATCH))
                                .forGetter(ResultEntry::resultPatch)
                ).apply(inst, ResultEntry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ResultEntry> STREAM_CODEC =
                StreamCodec.of(
                        (buf, e) -> {
                            IngredientChoice.STREAM_CODEC.encode(buf, e.ingredient);
                            Amount.STREAM_CODEC.encode(buf, e.amount);
                            buf.writeVarInt(e.weight);

                            buf.writeBoolean(e.enchantmentProvider.isPresent());
                            e.enchantmentProvider.ifPresent(k -> buf.writeResourceLocation(k.location()));

                            buf.writeBoolean(e.stackModifierId.isPresent());
                            e.stackModifierId.ifPresent(buf::writeUtf);

                            buf.writeBoolean(e.resultPatch.isPresent());
                            e.resultPatch.ifPresent(p -> DataComponentPatch.STREAM_CODEC.encode(buf, p));
                        },
                        buf -> {
                            IngredientChoice ing = IngredientChoice.STREAM_CODEC.decode(buf);
                            Amount amt = Amount.STREAM_CODEC.decode(buf);
                            int weight = buf.readVarInt();

                            Optional<ResourceKey<EnchantmentProvider>> ench =
                                    buf.readBoolean()
                                            ? Optional.of(ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, buf.readResourceLocation()))
                                            : Optional.empty();

                            Optional<String> stackMod = buf.readBoolean() ? Optional.of(buf.readUtf()) : Optional.empty();

                            Optional<DataComponentPatch> patch =
                                    buf.readBoolean()
                                            ? Optional.of(DataComponentPatch.STREAM_CODEC.decode(buf))
                                            : Optional.empty();

                            return new ResultEntry(ing, amt, weight, ench, stackMod, patch);
                        }
                );
    }

    // =====================================================================
    // SoundDefinition
    // =====================================================================

    public record SoundDefinition(Holder<SoundEvent> sound, float volume, float pitch) {

        public static final Codec<SoundDefinition> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        RegistryFixedCodec.create(Registries.SOUND_EVENT)
                                .fieldOf(JolCraftDictionary.SOUND)
                                .forGetter(SoundDefinition::sound),
                        Codec.FLOAT.fieldOf(JolCraftDictionary.VOLUME).forGetter(SoundDefinition::volume),
                        Codec.FLOAT.fieldOf(JolCraftDictionary.PITCH).forGetter(SoundDefinition::pitch)
                ).apply(inst, SoundDefinition::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SoundDefinition> STREAM_CODEC =
                StreamCodec.composite(
                        SoundEvent.STREAM_CODEC, SoundDefinition::sound,
                        StreamCodec.of(RegistryFriendlyByteBuf::writeFloat, RegistryFriendlyByteBuf::readFloat), SoundDefinition::volume,
                        StreamCodec.of(RegistryFriendlyByteBuf::writeFloat, RegistryFriendlyByteBuf::readFloat), SoundDefinition::pitch,
                        SoundDefinition::new
                );
    }

    // =====================================================================
    // Recipe fields
    // =====================================================================

    private final IngredientEntry ingredientA;
    private final IngredientEntry ingredientB;

    private final List<ResultEntry> results;

    private final float chance;              // default 1.0
    private final Rolls rolls;               // default 1
    private final boolean requireSneaking;   // default false

    private final Optional<SoundDefinition> successSound;
    private final Optional<SoundDefinition> failSound;

    public HandInteractionRecipe(
            IngredientEntry ingredientA,
            IngredientEntry ingredientB,
            List<ResultEntry> results,
            float chance,
            Rolls rolls,
            boolean requireSneaking,
            Optional<SoundDefinition> successSound,
            Optional<SoundDefinition> failSound
    ) {
        this.ingredientA = ingredientA;
        this.ingredientB = ingredientB;
        this.results = results;

        this.chance = chance;
        this.rolls = rolls;
        this.requireSneaking = requireSneaking;

        this.successSound = successSound;
        this.failSound = failSound;
    }

    // =====================================================================
    // Accessors
    // =====================================================================

    public IngredientEntry ingredientA() { return ingredientA; }
    public IngredientEntry ingredientB() { return ingredientB; }

    public List<ResultEntry> results() { return results; }

    public float chance() { return chance; }
    public Rolls rolls() { return rolls; }
    public boolean requireSneaking() { return requireSneaking; }

    public Optional<SoundDefinition> successSound() { return successSound; }
    public Optional<SoundDefinition> failSound() { return failSound; }

    // =====================================================================
    // Rolling helpers (engine uses these)
    // =====================================================================

    public int rollCount(RandomSource random) {
        int min = Math.max(1, rolls.min());
        int max = Math.max(min, rolls.max());
        return (min == max) ? min : (min + random.nextInt(max - min + 1));
    }

    /**
     * Weighted picks WITH replacement (duplicates allowed by design).
     */
    public List<ResultEntry> pickWeightedResults(RandomSource random, int picks) {
        List<ResultEntry> out = new ArrayList<>();
        if (results.isEmpty() || picks <= 0) return out;

        int total = 0;
        for (ResultEntry e : results) total += Math.max(0, e.weight);

        if (total <= 0) return out;

        for (int i = 0; i < picks; i++) {
            int roll = random.nextInt(total);
            int acc = 0;
            for (ResultEntry e : results) {
                acc += Math.max(0, e.weight);
                if (roll < acc) {
                    out.add(e);
                    break;
                }
            }
        }

        return out;
    }

    // =====================================================================
    // Recipe implementation
    // =====================================================================

    @Override
    public boolean matches(HandInteractionRecipeInput in, Level level) {
        if (level.isClientSide) return false;

        ItemStack main = in.mainHand();
        ItemStack off = in.offHand();

        return (ingredientA.matches(main) && ingredientB.matches(off))
                || (ingredientA.matches(off) && ingredientB.matches(main));
    }

    @Override
    public ItemStack assemble(HandInteractionRecipeInput in, HolderLookup.Provider registries) {
        if (results.isEmpty()) return ItemStack.EMPTY;
        return results.getFirst().preview(registries);
    }

    @Override
    public RecipeSerializer<? extends Recipe<HandInteractionRecipeInput>> getSerializer() {
        return JolCraftRecipes.HAND_INTERACTION_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<HandInteractionRecipeInput>> getType() {
        return JolCraftRecipes.HAND_INTERACTION_TYPE.get();
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

    public static final class Serializer implements RecipeSerializer<HandInteractionRecipe> {

        private static final MapCodec<Float> CHANCE_FIELD =
                Codec.FLOAT.optionalFieldOf(JolCraftDictionary.CHANCE, 1.0F);

        private static final MapCodec<Rolls> ROLLS_FIELD =
                Rolls.CODEC.optionalFieldOf(JolCraftStrings.plural(JolCraftDictionary.ROLL), Rolls.fixed(1));

        private static final MapCodec<Boolean> REQUIRE_SNEAKING_FIELD =
                Codec.BOOL.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.REQUIRE, JolCraftDictionary.SNEAK), false);

        public static final MapCodec<HandInteractionRecipe> CODEC =
                RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<HandInteractionRecipe> inst) -> inst.group(
                        IngredientEntry.CODEC.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "a"))
                                .forGetter(HandInteractionRecipe::ingredientA),
                        IngredientEntry.CODEC.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "b"))
                                .forGetter(HandInteractionRecipe::ingredientB),

                        ResultEntry.CODEC.listOf().fieldOf(JolCraftStrings.plural(JolCraftDictionary.RESULT))
                                .forGetter(HandInteractionRecipe::results),

                        CHANCE_FIELD.forGetter(HandInteractionRecipe::chance),
                        ROLLS_FIELD.forGetter(HandInteractionRecipe::rolls),
                        REQUIRE_SNEAKING_FIELD.forGetter(HandInteractionRecipe::requireSneaking),

                        SoundDefinition.CODEC.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.SUCCESS, JolCraftDictionary.SOUND))
                                .forGetter(HandInteractionRecipe::successSound),
                        SoundDefinition.CODEC.optionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.FAIL, JolCraftDictionary.SOUND))
                                .forGetter(HandInteractionRecipe::failSound)
                ).apply(inst, HandInteractionRecipe::new)).flatXmap(
                        Serializer::validate,
                        DataResult::success
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, HandInteractionRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<HandInteractionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HandInteractionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static DataResult<HandInteractionRecipe> validate(HandInteractionRecipe r) {
            if (r.ingredientA == null) return DataResult.error(() -> "ingredient_a is required");
            if (r.ingredientB == null) return DataResult.error(() -> "ingredient_b is required");

            // ---- ingredient_a / ingredient_b invariants (single pass) ----
            for (String which : List.of("ingredient_a", "ingredient_b")) {
                IngredientEntry entry = which.equals("ingredient_a") ? r.ingredientA : r.ingredientB;

                if (entry.ingredient == null) return DataResult.error(() -> which + ".ingredient is required");
                if (entry.action == null) return DataResult.error(() -> which + ".action is required");

                IngredientAction act = entry.action;
                if (act.type == null) return DataResult.error(() -> which + ".action.type is required");

                if (act.type == IngredientActionType.DAMAGE) {
                    if (act.amount.isEmpty()) return DataResult.error(() -> which + ".action.amount is required when action.type=damage");
                    if (act.amount.get() < 1) return DataResult.error(() -> which + ".action.amount must be >= 1");
                } else {
                    if (act.amount.isPresent()) {
                        return DataResult.error(() -> which + ".action.amount is only allowed when action.type=damage");
                    }
                }
            }

            // ---- results ----
            if (r.results == null || r.results.isEmpty()) {
                return DataResult.error(() -> "results must be a non-empty array");
            }

            // ---- chance ----
            if (r.chance < 0.0F || r.chance > 1.0F) {
                return DataResult.error(() -> "chance must be within [0.0, 1.0] (got " + r.chance + ")");
            }

            // ---- rolls ----
            if (r.rolls == null) return DataResult.error(() -> "rolls is required");
            int rollsMin = r.rolls.min();
            int rollsMax = r.rolls.max();
            if (rollsMin < 1 || rollsMax < rollsMin) {
                return DataResult.error(() -> "rolls must be >= 1 and max>=min");
            }

            // ---- fail_sound rule ----
            if (r.chance >= 1.0F && r.failSound.isPresent()) {
                return DataResult.error(() -> "fail_sound is not allowed when chance is 1.0");
            }

            // ---- per-result entry validation ----
            for (int i = 0; i < r.results.size(); i++) {
                final int idx = i;
                ResultEntry e = r.results.get(idx);

                if (e == null) return DataResult.error(() -> "results[" + idx + "] must not be null");
                if (e.ingredient == null) return DataResult.error(() -> "results[" + idx + "].ingredient is required");
                if (e.amount == null) return DataResult.error(() -> "results[" + idx + "].amount is required");

                if (e.amount.min() < 1 || e.amount.max() < e.amount.min()) {
                    return DataResult.error(() -> "results[" + idx + "].amount must be >= 1 and max>=min");
                }

                if (e.weight < 1) {
                    return DataResult.error(() -> "results[" + idx + "].weight must be >= 1 (got " + e.weight + ")");
                }

                if (e.stackModifierId.isPresent()) {
                    String raw = e.stackModifierId.get().trim();
                    if (raw.isEmpty()) {
                        return DataResult.error(() -> "results[" + idx + "].stack_modifier must not be blank when present");
                    }
                    if (ResourceLocation.tryParse(raw) == null) {
                        return DataResult.error(() -> "results[" + idx + "].stack_modifier must be a valid resource location (got '" + raw + "')");
                    }
                }
            }

            return DataResult.success(r);
        }

        // ---------------- STREAM ----------------

        private static void toNetwork(RegistryFriendlyByteBuf buf, HandInteractionRecipe r) {
            IngredientEntry.STREAM_CODEC.encode(buf, r.ingredientA);
            IngredientEntry.STREAM_CODEC.encode(buf, r.ingredientB);

            buf.writeVarInt(r.results.size());
            for (ResultEntry e : r.results) {
                ResultEntry.STREAM_CODEC.encode(buf, e);
            }

            buf.writeFloat(r.chance);
            Rolls.STREAM_CODEC.encode(buf, r.rolls);
            buf.writeBoolean(r.requireSneaking);

            buf.writeBoolean(r.successSound.isPresent());
            r.successSound.ifPresent(s -> SoundDefinition.STREAM_CODEC.encode(buf, s));

            buf.writeBoolean(r.failSound.isPresent());
            r.failSound.ifPresent(s -> SoundDefinition.STREAM_CODEC.encode(buf, s));
        }

        private static HandInteractionRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            IngredientEntry a = IngredientEntry.STREAM_CODEC.decode(buf);
            IngredientEntry b = IngredientEntry.STREAM_CODEC.decode(buf);

            int size = buf.readVarInt();
            List<ResultEntry> results = new ArrayList<>(Math.max(0, size));
            for (int i = 0; i < size; i++) {
                results.add(ResultEntry.STREAM_CODEC.decode(buf));
            }

            float chance = buf.readFloat();
            Rolls rolls = Rolls.STREAM_CODEC.decode(buf);
            boolean requireSneaking = buf.readBoolean();

            Optional<SoundDefinition> success = buf.readBoolean()
                    ? Optional.of(SoundDefinition.STREAM_CODEC.decode(buf))
                    : Optional.empty();

            Optional<SoundDefinition> fail = buf.readBoolean()
                    ? Optional.of(SoundDefinition.STREAM_CODEC.decode(buf))
                    : Optional.empty();

            return new HandInteractionRecipe(
                    a,
                    b,
                    results,
                    chance,
                    rolls,
                    requireSneaking,
                    success,
                    fail
            );
        }
    }
}