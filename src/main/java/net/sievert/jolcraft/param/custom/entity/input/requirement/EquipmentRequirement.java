package net.sievert.jolcraft.param.custom.entity.input.requirement;

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
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.item.input.ItemInput;
import net.sievert.jolcraft.param.runtime.WorldContext;

public record EquipmentRequirement(
        EquipmentSlot slot,
        ItemInput item
) implements ParamData<EquipmentRequirement> {

    private static final int MAX_SLOT_NAME = 64;

    private static final Codec<EquipmentSlot> SLOT_CODEC = Codec.STRING.comapFlatMap(EquipmentRequirement::slotFromName, EquipmentSlot::getName);

    public static final Codec<EquipmentRequirement> CODEC =
            ParamCodecs.validated(
                    RecordCodecBuilder.create(inst -> inst.group(
                            SLOT_CODEC.fieldOf(JolCraftParameterIds.SLOT)
                                    .forGetter(EquipmentRequirement::slot),
                            ItemInput.CODEC.fieldOf(JolCraftParameterIds.ITEM)
                                    .forGetter(EquipmentRequirement::item)
                    ).apply(inst, EquipmentRequirement::new)),
                    EquipmentRequirement::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentRequirement> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, req) -> {
                        buf.writeUtf(req.slot().getName(), MAX_SLOT_NAME);
                        ItemInput.STREAM_CODEC.encode(buf, req.item());
                    },
                    buf -> new EquipmentRequirement(
                            slotFromName(buf.readUtf(MAX_SLOT_NAME))
                                    .getOrThrow(IllegalArgumentException::new),
                            ItemInput.STREAM_CODEC.decode(buf)
                    )
            ), EquipmentRequirement::validate);

    public EquipmentRequirement {
        if (slot == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.SLOT + "'");
        }

        if (item == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ITEM + "'");
        }
    }

    private static DataResult<EquipmentSlot> slotFromName(String name) {
        if (name == null || name.isBlank()) {
            return ParamValidations.invalid("missing '" + JolCraftParameterIds.SLOT + "'");
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getName().equals(name)) {
                return ParamValidations.ok(slot);
            }
        }

        return ParamValidations.invalid("Unknown equipment slot: " + name);
    }

    public boolean matches(WorldContext ctx, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;

        ItemStack stack = living.getItemBySlot(slot);
        return item.matches(ctx, stack);
    }

    @Override
    public DataResult<EquipmentRequirement> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.notNull(this, slot, JolCraftParameterIds.SLOT),
                () -> ParamValidations.child(this, item, JolCraftParameterIds.ITEM)
        );
    }

    @Override
    public Codec<EquipmentRequirement> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EquipmentRequirement> streamCodec() {
        return STREAM_CODEC;
    }
}