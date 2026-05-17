package net.sievert.jolcraft.param.custom.item.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamContextMatching;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.Conditions;
import net.sievert.jolcraft.param.runtime.WorldContext;

import java.util.List;

public record ItemSelector(
        Conditions conditions,
        List<ItemEntry> entries
) implements ParamData<ItemSelector>, ParamContextMatching<ItemStack> {

    public ItemSelector {
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        entries = ParamValidations.sanitizeList(entries);
    }

    public static ItemSelector of(ItemIngredient ingredient) {
        return new ItemSelector(
                Conditions.EMPTY,
                List.of(ItemEntry.of(ingredient))
        );
    }

    public static ItemSelector of(ItemTarget target) {
        return of(ItemIngredient.of(target));
    }

    public static ItemSelector of(List<ItemTarget> targets) {
        return of(ItemIngredient.of(targets));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSimple() {
        return conditions == Conditions.EMPTY
                && entries.size() == 1
                && entries.getFirst().conditions() == Conditions.EMPTY;
    }

    public ItemIngredient simpleIngredient() {
        if (!isSimple()) {
            throw new IllegalStateException("ItemSelector is not simple");
        }

        return entries.getFirst().ingredient();
    }

    @Override
    public boolean matches(WorldContext ctx, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!conditions.matches(ctx)) return false;

        for (ItemEntry entry : entries) {
            if (!entry.conditions().matches(ctx)) continue;
            if (entry.ingredient().matches(stack)) return true;
        }

        return false;
    }

    @Override
    public Codec<ItemSelector> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemSelector> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<ItemSelector> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.child(this, conditions, JolCraftParameterIds.CONDITIONS),
                () -> ParamValidations.notEmpty(this, entries, JolCraftParameterIds.ENTRIES),
                () -> ParamValidations.children(this, entries, JolCraftParameterIds.ENTRIES)
        );
    }

    private static final Codec<ItemSelector> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(ItemSelector::conditions),
                    ItemEntry.CODEC.listOf().fieldOf(JolCraftParameterIds.ENTRIES)
                            .forGetter(ItemSelector::entries)
            ).apply(inst, ItemSelector::new));

    public static final Codec<ItemSelector> CODEC =
            ParamCodecs.validated(RAW_CODEC, ItemSelector::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSelector> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    Conditions.STREAM_CODEC,
                    ItemSelector::conditions,
                    ItemEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ItemSelector::entries,
                    ItemSelector::new
            ), ItemSelector::validate);
}