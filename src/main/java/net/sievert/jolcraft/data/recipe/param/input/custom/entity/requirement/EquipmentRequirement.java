package net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Atomic entity requirement: entity must have an item matching the given {@link ItemInput}
 * in a given equipment slot.
 * JSON:
 * { "slot": "mainhand", "item": { ... ItemInput ... } }
 *
 * Option 1 (S+ strict):
 * - No invalid/sentinel instances.
 * - slot/item required and non-null.
 * - Stream assumes valid instances.
 *
 * Runtime matches(...) is total + fail-closed.
 */
public record EquipmentRequirement(EquipmentSlot slot, ItemInput item) implements SelfValidating<EquipmentRequirement> {

    private static final Codec<EquipmentSlot> SLOT_CODEC =
            Codec.STRING.comapFlatMap(name -> {
                if (name == null || name.isEmpty()) {
                    return DataResult.error(() -> "missing '" + JolCraftParameterIds.SLOT + "'");
                }

                EquipmentSlot slot = EquipmentSlot.byName(name);

                return DataResult.success(slot);
            }, EquipmentSlot::getName);

    private static final Codec<EquipmentRequirement> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    SLOT_CODEC.fieldOf(JolCraftParameterIds.SLOT).forGetter(EquipmentRequirement::slot),
                    ItemInput.CODEC.fieldOf(JolCraftParameterIds.ITEM).forGetter(EquipmentRequirement::item)
            ).apply(instance, EquipmentRequirement::new));

    public static final Codec<EquipmentRequirement> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_SLOT_NAME = 64;

    /**
     * Stream:
     * - Stable slot names (NOT ordinals).
     * - Strict: assumes valid instances (non-null slot/item).
     * - No validate() calls here (no allocations).
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        buf.writeUtf(req.slot.getName());
                        ItemInput.STREAM_CODEC.encode(buf, req.item);
                    },
                    buf -> {
                        String slotName = buf.readUtf(MAX_SLOT_NAME);
                        EquipmentSlot s = EquipmentSlot.byName(slotName);
                        ItemInput it = ItemInput.STREAM_CODEC.decode(buf);
                        return new EquipmentRequirement(s, it);
                    }
            );

    @Override
    public @NotNull DataResult<EquipmentRequirement> validate() {
        if (slot == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.SLOT + "'");
        }

        if (item == null) {
            return SelfValidating.invalid("missing required field '" + JolCraftParameterIds.ITEM + "'");
        }

        DataResult<ItemInput> iv = item.validate();
        Optional<DataResult.Error<ItemInput>> err = iv.error();
        return err.<DataResult<EquipmentRequirement>>map(e ->
                SelfValidating.invalid(JolCraftParameterIds.ITEM + " invalid: " + e.message())
        ).orElseGet(() -> SelfValidating.ok(this));
    }

    public boolean matches(@NotNull WorldContext ctx, Entity entity) {
        if (entity == null) return false;
        if (!(entity instanceof LivingEntity living)) return false;
        if (slot == null || item == null) return false;

        ItemStack stack = living.getItemBySlot(slot);
        return item.matches(ctx, stack);
    }
}