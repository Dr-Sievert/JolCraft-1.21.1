package net.sievert.jolcraft.data.recipe.param.input.custom.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.input.base.InputParam;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement.ItemRequirements;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemIngredient;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.quantity.HasCount;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Item input with:
 * - conditions (gate)
 * - selector (items/tags + entry gates)
 * - count (min)
 * - requirements (components/enchantments)
 */
public record ItemInput(
        Conditions conditions,
        ItemSelector selector,
        IntRange count,
        ItemRequirements requirements
) implements InputParam<ItemInput, ItemStack>, HasCount, ConditionGate, RegistryIntrospectionSource {

    public static final ItemInput EMPTY =
            new ItemInput(Conditions.EMPTY, ItemSelector.EMPTY, IntRange.ONE, ItemRequirements.EMPTY);

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftParameterIds.INPUT));

    public static final byte DISC = 1;

    public static final ItemRequirements EMPTY_REQUIREMENTS = ItemRequirements.EMPTY;

    private Conditions conditionsSafe() {
        return conditions != null ? conditions : Conditions.EMPTY;
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditionsSafe();
    }

    private ItemSelector selectorSafe() {
        return selector != null ? selector : ItemSelector.EMPTY;
    }

    private IntRange countSafe() {
        return count != null ? count : IntRange.ONE;
    }

    private ItemRequirements requirementsSafe() {
        return requirements != null ? requirements : ItemRequirements.EMPTY;
    }

    @SuppressWarnings("deprecation")
    public static ItemInput one(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) return EMPTY;

        var holders = ingredient.items().toList();
        if (holders.isEmpty()) return EMPTY;

        ArrayList<ItemIngredient.Target> targets = new ArrayList<>(Math.min(holders.size(), 64));
        for (var h : holders) {
            if (h == null) continue;
            targets.add(new ItemIngredient.Target(Either.left(h)));
        }

        ItemIngredient ing = targets.isEmpty() ? ItemIngredient.EMPTY : new ItemIngredient(targets);
        return new ItemInput(Conditions.EMPTY, ItemSelector.of(ing), IntRange.ONE, EMPTY_REQUIREMENTS);
    }

    public static ItemInput one(ItemLike item) {
        return new ItemInput(Conditions.EMPTY, ItemSelector.of(item), IntRange.ONE, EMPTY_REQUIREMENTS);
    }

    private static final Codec<ItemInput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Conditions.CODEC
                            .optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(ItemInput::conditionsSafe),
                    ItemSelector.CODEC
                            .fieldOf(JolCraftParameterIds.SELECTOR)
                            .forGetter(ItemInput::selectorSafe),
                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(ItemInput::countSafe),
                    ItemRequirements.CODEC
                            .optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EMPTY_REQUIREMENTS)
                            .forGetter(ItemInput::requirementsSafe)
            ).apply(instance, ItemInput::new));

    public static final Codec<ItemInput> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        Conditions.STREAM_CODEC.encode(buf, value.conditionsSafe());
                        ItemSelector.STREAM_CODEC.encode(buf, value.selectorSafe());
                        IntRange.STREAM_CODEC.encode(buf, value.countSafe());
                        ItemRequirements.STREAM_CODEC.encode(buf, value.requirementsSafe());
                    },
                    buf -> new ItemInput(
                            Conditions.STREAM_CODEC.decode(buf),
                            ItemSelector.STREAM_CODEC.decode(buf),
                            IntRange.STREAM_CODEC.decode(buf),
                            ItemRequirements.STREAM_CODEC.decode(buf)
                    )
            );

    public static final ParamTypeDef<InputParam<?, ?>> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public boolean matches(@NotNull WorldContext ctx, @Nullable ItemStack subject) {
        if (subject == null || subject.isEmpty()) return false;
        if (!gatePasses(ctx)) return false;
        if (!selectorSafe().matches(ctx, subject)) return false;
        if (!requirementsSafe().matches(subject)) return false;
        if (!hasValidCountRange()) return false;
        return subject.getCount() >= countSafe().min();
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return RegistryIntrospectionSource.mergeByRegistry(List.of(selectorSafe(), requirementsSafe()));
    }

    @Override
    public @NotNull DataResult<ItemInput> validate() {
        if (conditions == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.CONDITIONS + "'");
        }
        if (selector == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        }
        if (count == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.COUNT + "'");
        }
        if (requirements == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.REQUIREMENTS + "'");
        }

        DataResult<Conditions> cv = conditionsSafe().validate();
        if (cv.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + ": " +
                    cv.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<ItemSelector> selectorRes = selectorSafe().validate();
        if (selectorRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.SELECTOR + ": " +
                    selectorRes.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<IntRange> countRes = IntRange.validateRange(countSafe());
        if (countRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": " +
                    countRes.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<ItemRequirements> reqRes = requirementsSafe().validate();
        if (reqRes.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.REQUIREMENTS + ": " +
                    reqRes.error().map(DataResult.Error::message).orElse(""));
        }

        if (!hasValidCountRange()) {
            return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": invalid count range");
        }

        return SelfValidating.ok(this);
    }
}
