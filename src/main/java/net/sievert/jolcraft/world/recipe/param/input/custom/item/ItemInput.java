package net.sievert.jolcraft.world.recipe.param.input.custom.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.input.base.InputParam;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.requirement.ItemRequirements;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.selector.ItemIngredient;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemInput(
        Conditions conditions,
        ItemSelector selector,
        IntRange count,
        ItemRequirements requirements
) implements InputParam<ItemInput, ItemStack>, ConditionGate, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftParameterIds.INPUT));
    public static final byte DISC = 1;
    public static final ItemRequirements EMPTY_REQUIREMENTS = ItemRequirements.EMPTY;

    private record CanonicalRaw(
            Conditions conditions,
            Optional<Holder<Item>> item,
            Optional<TagKey<Item>> tag,
            IntRange count,
            ItemRequirements requirements
    ) {
        private CanonicalRaw {
            conditions = conditions != null ? conditions : Conditions.EMPTY;
            item = item != null ? item : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
            count = count != null ? count : IntRange.ONE;
            requirements = requirements != null ? requirements : EMPTY_REQUIREMENTS;
        }
    }

    private record VerboseRaw(
            Conditions conditions,
            ItemSelector selector,
            IntRange count,
            ItemRequirements requirements
    ) {
        private VerboseRaw {
            conditions = conditions != null ? conditions : Conditions.EMPTY;
            count = count != null ? count : IntRange.ONE;
            requirements = requirements != null ? requirements : EMPTY_REQUIREMENTS;
        }
    }

    public static @NotNull ItemInput one(@NotNull Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            throw new IllegalArgumentException("ingredient must not be empty");
        }

        ItemStack[] stacks = ingredient.getItems();
        if (stacks.length == 0) {
            throw new IllegalArgumentException("ingredient must not be empty");
        }

        ArrayList<ItemIngredient.Target> targets = new ArrayList<>(stacks.length);
        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            Holder<Item> holder = BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem());
            targets.add(new ItemIngredient.Target(Either.left(holder)));
        }

        if (targets.isEmpty()) {
            throw new IllegalArgumentException("ingredient must not be empty");
        }

        return new ItemInput(
                Conditions.EMPTY,
                ItemSelector.of(new ItemIngredient(targets)),
                IntRange.ONE,
                EMPTY_REQUIREMENTS
        );
    }

    public static @NotNull ItemInput one(@NotNull ItemLike item) {
        return new ItemInput(Conditions.EMPTY, ItemSelector.of(item), IntRange.ONE, EMPTY_REQUIREMENTS);
    }

    private static final Codec<CanonicalRaw> CANONICAL_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(CanonicalRaw::conditions),

                    ItemIngredient.Target.ITEM_HOLDER_CODEC
                            .optionalFieldOf(JolCraftParameterIds.ITEM)
                            .forGetter(CanonicalRaw::item),

                    TagKey.codec(net.minecraft.core.registries.Registries.ITEM)
                            .optionalFieldOf(JolCraftParameterIds.TAG)
                            .forGetter(CanonicalRaw::tag),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(CanonicalRaw::count),

                    ItemRequirements.CODEC
                            .optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EMPTY_REQUIREMENTS)
                            .forGetter(CanonicalRaw::requirements)
            ).apply(instance, CanonicalRaw::new));

    private static final Codec<VerboseRaw> VERBOSE_RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(VerboseRaw::conditions),

                    ItemSelector.CODEC
                            .fieldOf(JolCraftParameterIds.SELECTOR)
                            .forGetter(VerboseRaw::selector),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(VerboseRaw::count),

                    ItemRequirements.CODEC
                            .optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EMPTY_REQUIREMENTS)
                            .forGetter(VerboseRaw::requirements)
            ).apply(instance, VerboseRaw::new));

    public static final Codec<ItemInput> CODEC =
            ParamCodecContract.create(
                    Codec.either(CANONICAL_RAW_CODEC, VERBOSE_RAW_CODEC),
                    ItemInput::fromRaw,
                    ItemInput::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        Conditions.STREAM_CODEC.encode(buf, value.conditions());
                        ItemSelector.STREAM_CODEC.encode(buf, value.selector());
                        IntRange.STREAM_CODEC.encode(buf, value.count());
                        ItemRequirements.STREAM_CODEC.encode(buf, value.requirements());
                    },
                    buf -> new ItemInput(
                            Conditions.STREAM_CODEC.decode(buf),
                            ItemSelector.STREAM_CODEC.decode(buf),
                            IntRange.STREAM_CODEC.decode(buf),
                            ItemRequirements.STREAM_CODEC.decode(buf)
                    )
            );

    public static final ParamTypeDef<InputParam<?, ?>> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public ItemInput {
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        if (selector == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        }
        count = count != null ? count : IntRange.ONE;
        requirements = requirements != null ? requirements : ItemRequirements.EMPTY;
    }

    private static @NotNull DataResult<ItemInput> fromRaw(@NotNull Either<CanonicalRaw, VerboseRaw> raw) {
        if (raw.left().isPresent()) {
            CanonicalRaw canonical = raw.left().orElseThrow();

            boolean hasItem = canonical.item().isPresent();
            boolean hasTag = canonical.tag().isPresent();

            if (hasItem == hasTag) {
                return DataResult.error(() ->
                        "ItemInput requires exactly one of '" + JolCraftParameterIds.ITEM + "' or '" + JolCraftParameterIds.TAG + "' in canonical form");
            }

            ItemIngredient.Target target = hasItem
                    ? new ItemIngredient.Target(Either.left(canonical.item().orElseThrow()))
                    : new ItemIngredient.Target(Either.right(canonical.tag().orElseThrow()));

            return DataResult.success(new ItemInput(
                    canonical.conditions(),
                    ItemSelector.of(new ItemIngredient(List.of(target))),
                    canonical.count(),
                    canonical.requirements()
            ));
        }

        VerboseRaw verbose = raw.right().orElseThrow();
        if (verbose.selector() == null) {
            return DataResult.error(() -> JolCraftParameterIds.SELECTOR + " is required");
        }

        return DataResult.success(new ItemInput(
                verbose.conditions(),
                verbose.selector(),
                verbose.count(),
                verbose.requirements()
        ));
    }

    private record FlatSelection(
            Optional<Holder<Item>> item,
            Optional<TagKey<Item>> tag
    ) {}

    private static @Nullable FlatSelection tryFlattenSelector(@NotNull ItemSelector selector) {
        if (selector.conditions() != Conditions.EMPTY) {
            return null;
        }

        List<ItemSelector.Entry> entries = selector.entries();
        if (entries.size() != 1) {
            return null;
        }

        ItemSelector.Entry entry = entries.getFirst();
        if (entry.conditions() != Conditions.EMPTY) {
            return null;
        }

        List<ItemIngredient.Target> targets = entry.ingredient().targets();
        if (targets.size() != 1) {
            return null;
        }

        ItemIngredient.Target target = targets.getFirst();
        return new FlatSelection(target.target().left(), target.target().right());
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull ItemInput input) {
        FlatSelection flat = tryFlattenSelector(input.selector());

        if (flat != null) {
            return Either.left(new CanonicalRaw(
                    input.conditions(),
                    flat.item(),
                    flat.tag(),
                    input.count(),
                    input.requirements()
            ));
        }

        return Either.right(new VerboseRaw(
                input.conditions(),
                input.selector(),
                input.count(),
                input.requirements()
        ));
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions;
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean matches(@NotNull WorldContext ctx, @Nullable ItemStack subject) {
        if (subject == null || subject.isEmpty()) return false;
        if (!gatePasses(ctx)) return false;
        if (!selector.matches(ctx, subject)) return false;
        if (!requirements.matches(subject)) return false;
        if (!count.isPositiveRange()) return false;
        return subject.getCount() >= count.min();
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return RegistryIntrospectionSource.mergeByRegistry(List.of(selector, requirements));
    }

    @Override
    public @NotNull DataResult<ItemInput> validate() {
        DataResult<Conditions> cv = conditions.validate();
        if (cv.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + ": " + cv.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<ItemSelector> selectorRes = selector.validate();
        if (selectorRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.SELECTOR + ": " + selectorRes.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<IntRange> countRes = IntRange.validateRange(count);
        if (countRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": " + countRes.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<ItemRequirements> reqRes = requirements.validate();
        if (reqRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.REQUIREMENTS + ": " + reqRes.error().map(DataResult.Error::message).orElse(""));
        }

        if (!count.isPositiveRange()) {
            return ParamValidations.invalid(
                    JolCraftParameterIds.COUNT + ": invalid count range"
            );
        }

        return SelfValidating.ok(this);
    }
}