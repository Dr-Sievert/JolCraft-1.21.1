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


public class DwarfExplorerModel extends DwarfModel{

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_explorer"), "main");


    public DwarfExplorerModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        DwarfModelHelper.baseDwarfModel(root);

        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("hat",
                CubeListBuilder.create()
                        .texOffs(92, 0).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.25F)) // base hat
                        .texOffs(0, 115).addBox(-5.26F, -8.25F, -4.0F, 0.01F, 5.0F, 8.0F, new CubeDeformation(0.0F)), // explorer extra
                PartPose.offset(0.0F, -3.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(DwarfRenderState state) {
        super.setupAnim(state);

        this.head.getChild("hat").visible = true;
        this.body.getChild("bodywear").visible = true;
        //this.body.getChild("legwear").visible = true;
        this.rightArm.getChild("right_armwear").visible = true;
        this.leftArm.getChild("left_armwear").visible = true;
        //this.rightLeg.getChild("right_footwear").visible = true;
        //this.leftLeg.getChild("left_footwear").visible = true;
    }

    @Override
    public void translateToHand(@NotNull HumanoidArm side, @NotNull PoseStack poseStack) {
        if (side == HumanoidArm.LEFT) {
            this.root.translateAndRotate(poseStack);
            this.getArm(side).translateAndRotate(poseStack);
            poseStack.translate(0.1F, -0.15F, 0.0F);
        } else {
            super.translateToHand(side, poseStack);
        }
    }
}


