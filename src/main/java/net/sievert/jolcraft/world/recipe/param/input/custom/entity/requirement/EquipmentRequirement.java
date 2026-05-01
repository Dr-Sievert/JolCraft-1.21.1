package net.sievert.jolcraft.world.recipe.param.input.custom.entity.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record EquipmentRequirement(EquipmentSlot slot, ItemInput item) implements SelfValidating<EquipmentRequirement> {

    private static final Codec<EquipmentSlot> SLOT_CODEC =
            Codec.STRING.comapFlatMap(name -> {
                if (name == null || name.isEmpty()) {
                    return DataResult.error(() -> "missing '" + JolCraftParameterIds.SLOT + "'");
                }
                return DataResult.success(EquipmentSlot.byName(name));
            }, EquipmentSlot::getName);

    private static final Codec<EquipmentRequirement> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    SLOT_CODEC.fieldOf(JolCraftParameterIds.SLOT).forGetter(EquipmentRequirement::slot),
                    ItemInput.CODEC.fieldOf(JolCraftParameterIds.ITEM).forGetter(EquipmentRequirement::item)
            ).apply(instance, EquipmentRequirement::new));

    public static final Codec<EquipmentRequirement> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_SLOT_NAME = 64;

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        buf.writeUtf(req.slot().getName());
                        ItemInput.STREAM_CODEC.encode(buf, req.item());
                    },
                    buf -> new EquipmentRequirement(
                            EquipmentSlot.byName(buf.readUtf(MAX_SLOT_NAME)),
                            ItemInput.STREAM_CODEC.decode(buf)
                    )
            );

    public EquipmentRequirement {
        if (slot == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.SLOT + "'");
        }
        if (item == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ITEM + "'");
        }
    }

    @Override
    public @NotNull DataResult<EquipmentRequirement> validate() {
        DataResult<ItemInput> iv = item.validate();
        Optional<DataResult.Error<ItemInput>> err = iv.error();
        return err.<DataResult<EquipmentRequirement>>map(e ->
                SelfValidating.invalid(JolCraftParameterIds.ITEM + " invalid: " + e.message())
        ).orElseGet(() -> SelfValidating.ok(this));
    }

    public boolean matches(@NotNull WorldContext ctx, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        ItemStack stack = living.getItemBySlot(slot);
        return item.matches(ctx, stack);
    }
}