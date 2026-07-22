package net.sievert.jolcraft.world.entity.client.util.dwarf.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.data.id.item.JolCraftMaterialIds;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class DwarfArmorLayer<T extends AbstractDwarfEntity> extends RenderLayer<T, DwarfModel<T>> {

    public DwarfArmorLayer(RenderLayerParent<T, DwarfModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull T dwarf,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        DwarfModel<T> model = this.getParentModel();
        model.setupAnim(dwarf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        Map<ModelPart, Boolean> previous = new IdentityHashMap<>();
        model.root().getAllParts().forEach(part -> previous.put(part, part.visible));

        try {
            renderArmorSlot(model, poseStack, buffer, packedLight, dwarf, EquipmentSlot.CHEST,
                    limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
            renderArmorSlot(model, poseStack, buffer, packedLight, dwarf, EquipmentSlot.LEGS,
                    limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
            renderArmorSlot(model, poseStack, buffer, packedLight, dwarf, EquipmentSlot.FEET,
                    limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
            renderArmorSlot(model, poseStack, buffer, packedLight, dwarf, EquipmentSlot.HEAD,
                    limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
        } finally {
            previous.forEach((part, visible) -> part.visible = visible);
        }
    }

    private static <T extends AbstractDwarfEntity> void renderArmorSlot(
            @NotNull DwarfModel<T> model,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull T dwarf,
            @NotNull EquipmentSlot slot,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack stack = dwarf.getItemBySlot(slot);
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armorItem)) return;
        if (armorItem.getEquipmentSlot() != slot) return;

        hideArmorModel(model);
        setArmorPartsVisible(model, slot);

        renderArmor(
                model,
                poseStack,
                buffer,
                packedLight,
                dwarf,
                stack,
                armorItem,
                slot,
                limbSwing,
                limbSwingAmount,
                partialTick,
                ageInTicks,
                netHeadYaw,
                headPitch
        );
    }

    private static <T extends AbstractDwarfEntity> void renderArmor(
            @NotNull DwarfModel<T> model,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull T dwarf,
            @NotNull ItemStack stack,
            @NotNull ArmorItem armorItem,
            @NotNull EquipmentSlot slot,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ArmorMaterial armorMaterial = armorItem.getMaterial().value();
        if (armorMaterial.layers().isEmpty()) return;

        IClientItemExtensions extensions = IClientItemExtensions.of(stack);

        extensions.setupModelAnimations(
                dwarf,
                stack,
                slot,
                model,
                limbSwing,
                limbSwingAmount,
                partialTick,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        ArmorMaterial.Layer layer = armorMaterial.layers().getFirst();

        int color = extensions.getArmorLayerTintColor(
                stack,
                dwarf,
                layer,
                0,
                extensions.getDefaultDyeColor(stack)
        );

        if (color == 0) return;

        renderArmorModel(
                model,
                poseStack,
                buffer,
                packedLight,
                dwarfArmorTexture(armorItem),
                color
        );

        if (stack.hasFoil()) {
            model.renderToBuffer(
                    poseStack,
                    buffer.getBuffer(RenderType.armorEntityGlint()),
                    packedLight,
                    OverlayTexture.NO_OVERLAY
            );
        }
    }

    private static void renderArmorModel(
            @NotNull Model model,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull ResourceLocation texture,
            int color
    ) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.armorCutoutNoCull(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }

    private static @NotNull ResourceLocation dwarfArmorTexture(@NotNull ArmorItem armorItem) {
        String material = armorItem.getMaterial().unwrapKey()
                .map(key -> key.location().getPath())
                .orElse(JolCraftMaterialIds.DEEPSLATE);

        return JolCraftTextures.mod(
                JolCraftTextures.dwarf(
                        JolCraftDirectoryIds.ARMOR,
                        JolCraftStrings.underscored(
                                JolCraftDirectoryIds.DWARF,
                                material,
                                JolCraftDirectoryIds.ARMOR
                        )
                )
        );
    }

    private static void hideArmorModel(@NotNull DwarfModel<?> model) {
        model.root().getAllParts().forEach(part -> part.visible = false);
        model.root().visible = true;
    }

    private static void setArmorPartsVisible(@NotNull DwarfModel<?> model, @NotNull EquipmentSlot slot) {
        model.root().visible = true;

        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.right_arm.visible = true;
                model.left_arm.visible = true;
                model.bodywear.visible = true;
                model.right_armwear.visible = true;
                model.left_armwear.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.legwear.visible = true;
            }
            case FEET -> {
                model.right_leg.visible = true;
                model.left_leg.visible = true;
                model.right_footwear.visible = true;
                model.left_footwear.visible = true;
            }
            default -> {
            }
        }
    }
}