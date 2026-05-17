package net.sievert.jolcraft.param.custom.item.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamMatching;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.List;

public record ItemIngredient(List<ItemTarget> targets)
        implements ParamData<ItemIngredient>, ParamMatching<ItemStack> {

    public ItemIngredient {
        targets = targets == null ? List.of() : List.copyOf(targets);
    }

    public static ItemIngredient of(ItemTarget target) {
        return new ItemIngredient(List.of(target));
    }

    public static ItemIngredient of(List<ItemTarget> targets) {
        return new ItemIngredient(targets);
    }

    public boolean isSingleTarget() {
        return targets.size() == 1;
    }

    public ItemTarget singleTarget() {
        if (!isSingleTarget()) {
            throw new IllegalStateException("ItemIngredient does not contain exactly one target");
        }
        return targets.getFirst();
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        for (ItemTarget target : targets) {
            if (target.target().value().map(
                    holder -> stack.getItem() == holder.value(),
                    stack::is
            )) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Codec<ItemIngredient> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemIngredient> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<ItemIngredient> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.notEmpty(this, targets, JolCraftStrings.plural(JolCraftParameterIds.TARGET)),
                () -> ParamValidations.children(this, targets, JolCraftStrings.plural(JolCraftParameterIds.TARGET))
        );
    }

    public static final Codec<ItemIngredient> CODEC =
            ParamCodecs.validated(
                    ParamCodecs.single(ItemTarget.CODEC)
                            .xmap(ItemIngredient::new, ItemIngredient::targets),
                    ItemIngredient::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemIngredient> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    ItemTarget.STREAM_CODEC.apply(ByteBufCodecs.list())
                            .map(ItemIngredient::new, ItemIngredient::targets),
                    ItemIngredient::validate
            );
}