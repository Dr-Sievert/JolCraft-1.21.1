package net.sievert.jolcraft.param.custom.item.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.Conditions;

import java.util.Optional;

public record ItemEntry(
        Conditions conditions,
        ItemIngredient ingredient
) implements ParamData<ItemEntry> {

    public ItemEntry {
        conditions = conditions != null ? conditions : Conditions.EMPTY;

        if (ingredient == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ITEMS + "'");
        }
    }

    private record Raw(
            Conditions conditions,
            Optional<ItemTarget> item,
            Optional<ItemIngredient> items
    ) {
        private Raw {
            conditions = conditions == null ? Conditions.EMPTY : conditions;
            item = item == null ? Optional.empty() : item;
            items = items == null ? Optional.empty() : items;
        }
    }

    public static ItemEntry of(ItemIngredient ingredient) {
        return new ItemEntry(Conditions.EMPTY, ingredient);
    }

    public static ItemEntry of(ItemTarget target) {
        return of(ItemIngredient.of(target));
    }

    private static final Codec<Raw> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Raw::conditions),
                    ItemTarget.CODEC.optionalFieldOf(JolCraftParameterIds.ITEM)
                            .forGetter(Raw::item),
                    ItemIngredient.CODEC.optionalFieldOf(JolCraftParameterIds.ITEMS)
                            .forGetter(Raw::items)
            ).apply(inst, Raw::new));

    public static final Codec<ItemEntry> CODEC =
            ParamCodecs.validated(
                    RAW_CODEC.flatXmap(ItemEntry::fromRaw, entry -> ParamValidations.ok(entry.toRaw())),
                    ItemEntry::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEntry> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    Conditions.STREAM_CODEC,
                    ItemEntry::conditions,
                    ItemIngredient.STREAM_CODEC,
                    ItemEntry::ingredient,
                    ItemEntry::new
            ), ItemEntry::validate);

    private static DataResult<ItemEntry> fromRaw(Raw raw) {
        boolean hasItem = raw.item().isPresent();
        boolean hasItems = raw.items().isPresent();

        if (hasItem == hasItems) {
            return ParamValidations.invalid(
                    "requires exactly one of '" + JolCraftParameterIds.ITEM
                            + "' or '" + JolCraftParameterIds.ITEMS + "'"
            );
        }

        ItemIngredient ingredient = hasItem
                ? ItemIngredient.of(raw.item().orElseThrow())
                : raw.items().orElseThrow();

        return new ItemEntry(raw.conditions(), ingredient).validate();
    }

    private Raw toRaw() {
        if (ingredient.isSingleTarget()) {
            return new Raw(
                    conditions,
                    Optional.of(ingredient.singleTarget()),
                    Optional.empty()
            );
        }

        return new Raw(
                conditions,
                Optional.empty(),
                Optional.of(ingredient)
        );
    }

    @Override
    public Codec<ItemEntry> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemEntry> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<ItemEntry> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.child(this, conditions, JolCraftParameterIds.CONDITIONS),
                () -> ParamValidations.child(this, ingredient, JolCraftParameterIds.ITEMS)
        );
    }
}