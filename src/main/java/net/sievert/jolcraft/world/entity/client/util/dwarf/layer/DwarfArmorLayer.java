package net.sievert.jolcraft.world.entity.client.util.dwarf.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfRenderState;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfArmorLayer extends RenderLayer<DwarfRenderState, DwarfModel> {

    public DwarfArmorLayer(RenderLayerParent<DwarfRenderState, DwarfModel> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer,
                       int packedLight,
                       DwarfRenderState state,
                       float yRot,
                       float xRot) {
        if (state.dwarf == null) return;

        DwarfModel model = this.getParentModel();
        model.setupAnim(state);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) continue;

            ItemStack stack = state.dwarf.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            if (equippable == null || equippable.slot() != slot) continue;

            ResourceKey<EquipmentAsset> assetKey = equippable.assetId().orElse(null);
            if (assetKey == null) continue;

            ResourceLocation texture = armorTexture(assetKey);

            setArmorPartsVisible(model, slot);

            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        model.setAllVisible(true);
    }

    private static @NotNull ResourceLocation armorTexture(@NotNull ResourceKey<EquipmentAsset> assetKey) {
        String material = assetKey.location().getPath();
        return JolCraft.location("textures/entity/dwarf/armor/dwarf_" + material + "_armor.png");
    }

    private static void setArmorPartsVisible(@NotNull DwarfModel model, @NotNull EquipmentSlot slot) {
        model.setAllVisible(false);

        model.getHead().getChild("hat").visible = slot == EquipmentSlot.HEAD;

        boolean chest = slot == EquipmentSlot.CHEST;
        model.body.getChild("bodywear").visible = chest;
        model.right_arm.getChild("right_armwear").visible = chest;
        model.left_arm.getChild("left_armwear").visible = chest;

        model.body.getChild("legwear").visible = slot == EquipmentSlot.LEGS;

        boolean feet = slot == EquipmentSlot.FEET;
        model.right_leg.getChild("right_footwear").visible = feet;
        model.left_leg.getChild("left_footwear").visible = feet;
    }
}