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

public record ItemInput(
        Conditions conditions,
        ItemSelector selector,
        IntRange count,
        ItemRequirements requirements
) implements InputParam<ItemInput, ItemStack>, HasCount, ConditionGate, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftParameterIds.INPUT));
    public static final byte DISC = 1;
    public static final ItemRequirements EMPTY_REQUIREMENTS = ItemRequirements.EMPTY;

    @SuppressWarnings("deprecation")
    public static ItemInput one(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) throw new IllegalArgumentException("ingredient must not be empty");
        var holders = ingredient.items().toList();
        if (holders.isEmpty()) throw new IllegalArgumentException("ingredient must not be empty");
        ArrayList<ItemIngredient.Target> targets = new ArrayList<>(holders.size());
        for (var h : holders) if (h != null) targets.add(new ItemIngredient.Target(Either.left(h)));
        return new ItemInput(Conditions.EMPTY, ItemSelector.of(new ItemIngredient(targets)), IntRange.ONE, EMPTY_REQUIREMENTS);
    }

    public static ItemInput one(ItemLike item) {
        return new ItemInput(Conditions.EMPTY, ItemSelector.of(item), IntRange.ONE, EMPTY_REQUIREMENTS);
    }

    private static final Codec<ItemInput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(ItemInput::conditions),
                    ItemSelector.CODEC.fieldOf(JolCraftParameterIds.SELECTOR).forGetter(ItemInput::selector),
                    IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE).forGetter(ItemInput::count),
                    ItemRequirements.CODEC.optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EMPTY_REQUIREMENTS).forGetter(ItemInput::requirements)
            ).apply(instance, ItemInput::new));

    public static final Codec<ItemInput> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInput> STREAM_CODEC = StreamCodec.of(
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

    public static final ParamTypeDef<InputParam<?, ?>> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public ItemInput {
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        if (selector == null) throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        count = count != null ? count : IntRange.ONE;
        requirements = requirements != null ? requirements : ItemRequirements.EMPTY;
    }

    @Override public @NotNull Conditions conditions() { return conditions; }
    @Override public @NotNull ResourceLocation typeId() { return TYPE_ID; }

    @Override
    public boolean matches(@NotNull WorldContext ctx, @Nullable ItemStack subject) {
        if (subject == null || subject.isEmpty()) return false;
        if (!gatePasses(ctx)) return false;
        if (!selector.matches(ctx, subject)) return false;
        if (!requirements.matches(subject)) return false;
        if (!hasValidCountRange()) return false;
        return subject.getCount() >= count.min();
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return RegistryIntrospectionSource.mergeByRegistry(List.of(selector, requirements));
    }

    @Override
    public @NotNull DataResult<ItemInput> validate() {
        DataResult<Conditions> cv = conditions.validate();
        if (cv.error().isPresent()) return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + ": " + cv.error().map(DataResult.Error::message).orElse(""));
        DataResult<ItemSelector> selectorRes = selector.validate();
        if (selectorRes.error().isPresent()) return SelfValidating.invalid(JolCraftParameterIds.SELECTOR + ": " + selectorRes.error().map(DataResult.Error::message).orElse(""));
        DataResult<IntRange> countRes = IntRange.validateRange(count);
        if (countRes.error().isPresent()) return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": " + countRes.error().map(DataResult.Error::message).orElse(""));
        DataResult<ItemRequirements> reqRes = requirements.validate();
        if (reqRes.error().isPresent()) return SelfValidating.invalid(JolCraftParameterIds.REQUIREMENTS + ": " + reqRes.error().map(DataResult.Error::message).orElse(""));
        if (!hasValidCountRange()) return SelfValidating.invalid(JolCraftParameterIds.COUNT + ": invalid count range");
        return SelfValidating.ok(this);
    }
}
