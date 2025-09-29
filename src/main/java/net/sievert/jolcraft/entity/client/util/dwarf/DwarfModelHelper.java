package net.sievert.jolcraft.entity.client.util.dwarf;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;

public class DwarfModelHelper {

    private DwarfModelHelper() {}

    /**
     * Builds and attaches all standard Dwarf model parts to the provided root.
     */
    public static void baseDwarfModel(PartDefinition partdefinition) {
        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 31).addBox(-6.0F, -6.0F, -3.0F, 12.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 11.0F, 0.0F));

        body.addOrReplaceChild("legwear",
                CubeListBuilder.create().texOffs(91, 44).addBox(-6.0F, 16.25F, -3.0F, 12.0F, 3.75F, 6.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -11.0F, 0.0F));

        body.addOrReplaceChild("bodywear",
                CubeListBuilder.create().texOffs(91, 27).addBox(-6.0F, 5.0F, -3.0F, 12.0F, 10.75F, 6.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(16, 18).addBox(-3.8F, -0.01F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-6.0F, 5.0F, 0.0F));

        right_arm.addOrReplaceChild("right_armwear",
                CubeListBuilder.create().texOffs(110, 18).addBox(-3.8F, 3.99F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(0, 18).addBox(-0.2F, -0.01F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(6.0F, 5.0F, 0.0F));

        left_arm.addOrReplaceChild("left_armwear",
                CubeListBuilder.create().texOffs(92, 18).addBox(-0.2F, -0.01F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(18, 49).addBox(-2.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-3.0F, 17.0F, 0.0F));

        right_leg.addOrReplaceChild("right_footwear",
                CubeListBuilder.create().texOffs(109, 54).addBox(-2.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 49).addBox(-3.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(3.0F, 17.0F, 0.0F));

        left_leg.addOrReplaceChild("left_footwear",
                CubeListBuilder.create().texOffs(91, 54).addBox(-3.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -2.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(36, 0).addBox(-5.0F, 1.0F, -2.0F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.15F))
                        .texOffs(36, 8).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 3.0F, -2.0F));

        head.addOrReplaceChild("beard",
                CubeListBuilder.create().texOffs(45, 22).addBox(-5.0F, -18.0F, -4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 20).addBox(-4.0F, -17.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(49, 16).addBox(-3.0F, -14.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 18).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(49, 14).addBox(-3.0F, -15.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(51, 12).addBox(-2.0F, -13.0F, -4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 10).addBox(-1.0F, -12.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 21.0F, 2.0F));

        head.addOrReplaceChild("right_eyebrow",
                CubeListBuilder.create().texOffs(60, 10).addBox(-3.0F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.0F, -3.0F, -2.0F));

        head.addOrReplaceChild("left_eyebrow",
                CubeListBuilder.create().texOffs(60, 10).addBox(0.0F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, -3.0F, -2.0F));

        head.addOrReplaceChild("hat",
                CubeListBuilder.create().texOffs(92, 0).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -3.0F, 2.0F));

        head.addOrReplaceChild("right_eye",
                CubeListBuilder.create().texOffs(69, 10).addBox(-3.0F, -5.0F, -4.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F));

        head.addOrReplaceChild("left_eye",
                CubeListBuilder.create().texOffs(73, 10).addBox(1.0F, -5.0F, -4.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F));
    }

    public static void visibleOuterLayer(DwarfModel model) {
        model.head.getChild("hat").visible = true;
        model.body.getChild("bodywear").visible = true;
        model.body.getChild("legwear").visible = true;
        model.rightArm.getChild("right_armwear").visible = true;
        model.leftArm.getChild("left_armwear").visible = true;
        model.rightLeg.getChild("right_footwear").visible = true;
        model.leftLeg.getChild("left_footwear").visible = true;
    }

}