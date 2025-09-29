package net.sievert.jolcraft.entity.client.model.dwarf;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import org.jetbrains.annotations.NotNull;

public class DwarfScrapperModel extends DwarfModel{

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_scrapper"), "main");

    public DwarfScrapperModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(DwarfRenderState state) {
        super.setupAnim(state);
        DwarfModelHelper.visibleOuterLayer(this);
    }

    @Override
    public void translateToHand(@NotNull HumanoidArm side, @NotNull PoseStack poseStack) {
        if (side == HumanoidArm.LEFT) {
            this.root.translateAndRotate(poseStack);
            this.getArm(side).translateAndRotate(poseStack);
            poseStack.translate(0.05F, -0.15F, 0.05F);
        } else {
            super.translateToHand(side, poseStack);
        }
    }


}
