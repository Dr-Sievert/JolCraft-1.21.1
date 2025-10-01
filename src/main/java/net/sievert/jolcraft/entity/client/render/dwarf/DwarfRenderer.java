package net.sievert.jolcraft.entity.client.render.dwarf;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.util.dwarf.*;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.client.util.dwarf.animation.DwarfAnimationHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfArmorLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfEyeLayer;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfVariant;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DwarfRenderer<T extends AbstractDwarfEntity> extends HumanoidMobRenderer<T, DwarfRenderState, DwarfModel> {

    public DwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfModel(context.bakeLayer(DwarfModel.LAYER_LOCATION)), 0.4f);
        this.addLayer(new DwarfArmorLayer(this));
        this.addLayer(new DwarfBeardLayer(this));
        this.addLayer(new DwarfEyeLayer(this));
    }

    public DwarfRenderer(EntityRendererProvider.Context context, DwarfModel model) {
        super(context, model, 0.4f);
    }

    private static final Map<DwarfVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DwarfVariant.class), map -> {
                map.put(DwarfVariant.GREY,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_grey.png"));
                map.put(DwarfVariant.BLUE,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_blue.png"));
                map.put(DwarfVariant.GREEN,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_green.png"));
                map.put(DwarfVariant.RED,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_red.png"));
                map.put(DwarfVariant.PURPLE,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_purple.png"));
                map.put(DwarfVariant.WHITE,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_white.png"));
                map.put(DwarfVariant.YELLOW,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_yellow.png"));
            });

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return LOCATION_BY_VARIANT.get(entity.variant);
    }

    @Override
    public void render(DwarfRenderState renderState, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (renderState.isBaby) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            poseStack.scale(0.9f, 0.9f, 0.9f);
        }
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull DwarfRenderState createRenderState() {
        return new DwarfRenderState();
    }

    @Override
    public void extractRenderState(@NotNull T entity, @NotNull DwarfRenderState reused, float partialTick) {
        super.extractRenderState(entity, reused, partialTick);

        DwarfRenderState persistent = AbstractDwarfEntity.getOrCreateClientRenderState(entity);

        persistent.currentActionType    = DwarfActionHelper.getCurrentActionType(entity);
        persistent.currentActionSubtype = DwarfActionHelper.getCurrentActionSubType(entity);
        DwarfAnimationHelper.updateAnimationStates(persistent, persistent.currentActionType, entity.tickCount);
        persistent.ageInTicks = entity.tickCount + partialTick;

        reused.currentActionType    = persistent.currentActionType;
        reused.currentActionSubtype = persistent.currentActionSubtype;
        reused.ageInTicks           = persistent.ageInTicks;

        reused.dwarf          = entity;
        reused.variant        = entity.getVariant();
        reused.beard          = entity.getBeard();
        reused.eye            = entity.getEye();
        reused.useItemHand    = entity.getUsedItemHand();
        reused.ticksUsingItem = entity.getTicksUsingItem();
        reused.isUsingItem    = entity.isUsingItem();
        reused.headEquipment  = entity.getItemBySlot(EquipmentSlot.HEAD);
        reused.chestEquipment = entity.getItemBySlot(EquipmentSlot.CHEST);
        reused.legsEquipment  = entity.getItemBySlot(EquipmentSlot.LEGS);
        reused.feetEquipment  = entity.getItemBySlot(EquipmentSlot.FEET);

        reused.animationStates.clear();
        reused.animationStates.putAll(persistent.animationStates);
    }

}
