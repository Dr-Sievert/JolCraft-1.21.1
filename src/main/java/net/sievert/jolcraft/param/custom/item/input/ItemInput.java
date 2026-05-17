package net.sievert.jolcraft.param.custom.item.input;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.base.RegistryTarget;
import net.sievert.jolcraft.param.custom.condition.Conditions;
import net.sievert.jolcraft.param.custom.input.InputParam;
import net.sievert.jolcraft.param.custom.item.input.requirement.ItemRequirements;
import net.sievert.jolcraft.param.custom.item.input.selection.ItemIngredient;
import net.sievert.jolcraft.param.custom.item.input.selection.ItemSelector;
import net.sievert.jolcraft.param.custom.item.input.selection.ItemTarget;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.ArrayList;
import java.util.Optional;

public record ItemInput(
        Conditions conditions,
        ItemSelector selector,
        IntRange count,
        ItemRequirements requirements
) implements InputParam<ItemStack>, ParamData<ItemInput> {

    public static final String KEY =
            JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftParameterIds.INPUT);

    public static final ResourceLocation TYPE_ID = JolCraft.location(KEY);

    private static final ItemRequirements EMPTY_REQUIREMENTS = ItemRequirements.EMPTY;

    private record Raw(
            Conditions conditions,
            Optional<ItemSelector> selector,
            Optional<RegistryTarget<Item>> item,
            Optional<ItemIngredient> items,
            IntRange count,
            ItemRequirements requirements
    ) {
        private Raw {
            conditions = conditions == null ? Conditions.EMPTY : conditions;
            selector = selector == null ? Optional.empty() : selector;
            item = item == null ? Optional.empty() : item;
            items = items == null ? Optional.empty() : items;
            count = count == null ? IntRange.ONE : count;
            requirements = requirements == null ? EMPTY_REQUIREMENTS : requirements;
        }
    }

    private static final Codec<Raw> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Raw::conditions),
                    ItemSelector.CODEC.optionalFieldOf(JolCraftParameterIds.SELECTOR)
                            .forGetter(Raw::selector),
                    ParamCodecs.registryTargetValue(Registries.ITEM).optionalFieldOf(JolCraftParameterIds.ITEM)
                            .forGetter(Raw::item),
                    ItemIngredient.CODEC.optionalFieldOf(JolCraftStrings.plural(JolCraftParameterIds.ITEM))
                            .forGetter(Raw::items),
                    IntRange.POSITIVE_CODEC.optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(Raw::count),
                    ItemRequirements.CODEC.optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EMPTY_REQUIREMENTS)
                            .forGetter(Raw::requirements)
            ).apply(inst, Raw::new));

    public static final Codec<ItemInput> CODEC =
            ParamCodecs.validated(
                    RAW_CODEC.flatXmap(ItemInput::fromRaw, input -> ParamValidations.ok(input.toRaw())),
                    ItemInput::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInput> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    Conditions.STREAM_CODEC,
                    ItemInput::conditions,
                    ItemSelector.STREAM_CODEC,
                    ItemInput::selector,
                    IntRange.STREAM_CODEC,
                    ItemInput::count,
                    ItemRequirements.STREAM_CODEC,
                    ItemInput::requirements,
                    ItemInput::new
            ), ItemInput::validate);

    public ItemInput {
        conditions = conditions == null ? Conditions.EMPTY : conditions;
        count = count == null ? IntRange.ONE : count;
        requirements = requirements == null ? EMPTY_REQUIREMENTS : requirements;

        if (selector == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        }
    }

    @Override
    public String key() {
        return KEY;
    }

    public static ItemInput one(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            throw new IllegalArgumentException("ingredient must not be empty");
        }

        ArrayList<ItemTarget> targets = new ArrayList<>();

        for (ItemStack stack : ingredient.getItems()) {
            if (stack != null && !stack.isEmpty()) {
                targets.add(target(BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem())));
            }
        }

        if (targets.isEmpty()) {
            throw new IllegalArgumentException("ingredient must not be empty");
        }

        return new ItemInput(Conditions.EMPTY, ItemSelector.of(targets), IntRange.ONE, EMPTY_REQUIREMENTS);
    }

    public static ItemInput one(ItemLike item) {
        if (item == null) {
            throw new IllegalArgumentException("item must not be null");
        }

        return new ItemInput(
                Conditions.EMPTY,
                ItemSelector.of(target(BuiltInRegistries.ITEM.wrapAsHolder(item.asItem()))),
                IntRange.ONE,
                EMPTY_REQUIREMENTS
        );
    }

    private static DataResult<ItemInput> fromRaw(Raw raw) {
        int sources = 0;
        if (raw.selector().isPresent()) sources++;
        if (raw.item().isPresent()) sources++;
        if (raw.items().isPresent()) sources++;

        if (sources != 1) {
            return ParamValidations.invalid(
                    "requires exactly one of '" + JolCraftParameterIds.SELECTOR
                            + "', '" + JolCraftParameterIds.ITEM
                            + "', or '" + JolCraftStrings.plural(JolCraftParameterIds.ITEM) + "'"
            );
        }

        ItemSelector selector = raw.selector()
                .orElseGet(() -> raw.items()
                        .map(ItemSelector::of)
                        .orElseGet(() -> ItemSelector.of(target(raw.item().orElseThrow()))));

        return new ItemInput(raw.conditions(), selector, raw.count(), raw.requirements()).validate();
    }

    private Raw toRaw() {
        ItemIngredient flat = tryFlatten(selector);

        if (flat == null) {
            return new Raw(
                    conditions,
                    Optional.of(selector),
                    Optional.empty(),
                    Optional.empty(),
                    count,
                    requirements
            );
        }

        if (flat.isSingleTarget()) {
            return new Raw(
                    conditions,
                    Optional.empty(),
                    Optional.of(flat.singleTarget().target()),
                    Optional.empty(),
                    count,
                    requirements
            );
        }

        return new Raw(
                conditions,
                Optional.empty(),
                Optional.empty(),
                Optional.of(flat),
                count,
                requirements
        );
    }

    private static ItemIngredient tryFlatten(ItemSelector selector) {
        if (!selector.isSimple()) return null;
        return selector.simpleIngredient();
    }

    private static ItemTarget target(Holder<Item> item) {
        return target(new RegistryTarget<>(Either.left(item)));
    }

    private static ItemTarget target(RegistryTarget<Item> target) {
        return new ItemTarget(target);
    }

    @Override
    public boolean matches(WorldContext ctx, ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && count.isPositiveRange()
                && stack.getCount() >= count.min()
                && conditions.matches(ctx)
                && selector.matches(ctx, stack)
                && requirements.matches(stack);
    }

    public boolean isEmpty() {
        return selector.entries().isEmpty()
                && requirements.isEmpty()
                && count.equals(IntRange.ONE)
                && conditions.isEmpty();
    }

    @Override
    public Codec<ItemInput> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemInput> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<ItemInput> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.child(this, conditions, JolCraftParameterIds.CONDITIONS),
                () -> ParamValidations.child(this, selector, JolCraftParameterIds.SELECTOR),
                () -> ParamValidations.child(this, requirements, JolCraftParameterIds.REQUIREMENTS),
                () -> ParamValidations.wrap(this, IntRange.validatePositiveRange(count), JolCraftParameterIds.COUNT)
        );
    }
}