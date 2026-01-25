package net.sievert.jolcraft.entity.client.util.dwarf;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;

@OnlyIn(Dist.CLIENT)
public final class DwarfModelHelper {

    private DwarfModelHelper() {}

    public static final String PART_BODY = "body";
    public static final String PART_RIGHT_ARM = "right_arm";
    public static final String PART_LEFT_ARM = "left_arm";
    public static final String PART_RIGHT_LEG = "right_leg";
    public static final String PART_LEFT_LEG = "left_leg";
    public static final String PART_HEAD = "head";

    public static final String PART_BODYWEAR = "bodywear";
    public static final String PART_LEGWEAR = "legwear";
    public static final String PART_RIGHT_ARMWEAR = "right_armwear";
    public static final String PART_LEFT_ARMWEAR = "left_armwear";
    public static final String PART_RIGHT_FOOTWEAR = "right_footwear";
    public static final String PART_LEFT_FOOTWEAR = "left_footwear";
    public static final String PART_BEARD = "beard";
    public static final String PART_RIGHT_EYEBROW = "right_eyebrow";
    public static final String PART_LEFT_EYEBROW = "left_eyebrow";
    public static final String PART_HAT = "hat";
    public static final String PART_RIGHT_EYE = "right_eye";
    public static final String PART_LEFT_EYE = "left_eye";

    public static final String PART_SHIELD = "shield";
    public static final String PART_BACKPACK = "backpack";
    public static final String PART_SACK = "sack";
    public static final String PART_GLASSES_MERCHANT = "glasses_merchant";
    public static final String PART_GLASSES_HISTORIAN = "glasses_historian";
    public static final String PART_HAT_KEEPER = "hat_keeper";
    public static final String PART_HAT_EXPLORER_EXTRA = "hat_explorer_extra";

    public static void baseDwarfModel(PartDefinition root) {
        PartDefinition body = root.addOrReplaceChild(PART_BODY,
                CubeListBuilder.create().texOffs(0, 31).addBox(-6.0F, -6.0F, -3.0F, 12.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 11.0F, 0.0F));

        body.addOrReplaceChild(PART_LEGWEAR,
                CubeListBuilder.create().texOffs(91, 44).addBox(-6.0F, 16.25F, -3.0F, 12.0F, 3.75F, 6.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -11.0F, 0.0F));

        body.addOrReplaceChild(PART_BODYWEAR,
                CubeListBuilder.create().texOffs(91, 27).addBox(-6.0F, 5.0F, -3.0F, 12.0F, 10.75F, 6.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition rightArm = root.addOrReplaceChild(PART_RIGHT_ARM,
                CubeListBuilder.create().texOffs(16, 18).addBox(-3.8F, -0.01F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-6.0F, 5.0F, 0.0F));

        rightArm.addOrReplaceChild(PART_RIGHT_ARMWEAR,
                CubeListBuilder.create().texOffs(110, 18).addBox(-3.8F, 3.99F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition leftArm = root.addOrReplaceChild(PART_LEFT_ARM,
                CubeListBuilder.create().texOffs(0, 18).addBox(-0.2F, -0.01F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(6.0F, 5.0F, 0.0F));

        leftArm.addOrReplaceChild(PART_LEFT_ARMWEAR,
                CubeListBuilder.create().texOffs(92, 18).addBox(-0.2F, -0.01F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rightLeg = root.addOrReplaceChild(PART_RIGHT_LEG,
                CubeListBuilder.create().texOffs(18, 49).addBox(-2.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-3.0F, 17.0F, 0.0F));

        rightLeg.addOrReplaceChild(PART_RIGHT_FOOTWEAR,
                CubeListBuilder.create().texOffs(109, 54).addBox(-2.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftLeg = root.addOrReplaceChild(PART_LEFT_LEG,
                CubeListBuilder.create().texOffs(0, 49).addBox(-3.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(3.0F, 17.0F, 0.0F));

        leftLeg.addOrReplaceChild(PART_LEFT_FOOTWEAR,
                CubeListBuilder.create().texOffs(91, 54).addBox(-3.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild(PART_HEAD,
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -2.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(36, 0).addBox(-5.0F, 1.0F, -2.0F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.15F))
                        .texOffs(36, 8).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 3.0F, -2.0F));

        head.addOrReplaceChild(PART_BEARD,
                CubeListBuilder.create().texOffs(45, 22).addBox(-5.0F, -18.0F, -4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 20).addBox(-4.0F, -17.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(49, 16).addBox(-3.0F, -14.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 18).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(49, 14).addBox(-3.0F, -15.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(51, 12).addBox(-2.0F, -13.0F, -4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 10).addBox(-1.0F, -12.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 21.0F, 2.0F));

        head.addOrReplaceChild(PART_RIGHT_EYEBROW,
                CubeListBuilder.create().texOffs(60, 10).addBox(-3.0F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.0F, -3.0F, -2.0F));

        head.addOrReplaceChild(PART_LEFT_EYEBROW,
                CubeListBuilder.create().texOffs(60, 10).addBox(0.0F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, -3.0F, -2.0F));

        head.addOrReplaceChild(PART_HAT,
                CubeListBuilder.create().texOffs(92, 0).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -3.0F, 2.0F));

        head.addOrReplaceChild(PART_RIGHT_EYE,
                CubeListBuilder.create().texOffs(69, 10).addBox(-3.0F, -5.0F, -4.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F));

        head.addOrReplaceChild(PART_LEFT_EYE,
                CubeListBuilder.create().texOffs(73, 10).addBox(1.0F, -5.0F, -4.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F));
    }

    public static void addAllProfessionExtras(PartDefinition root) {
        PartDefinition head = root.getChild(PART_HEAD);
        PartDefinition body = root.getChild(PART_BODY);
        PartDefinition leftArm = root.getChild(PART_LEFT_ARM);

        addExplorerHatExtra(head);
        addKeeperHat(head);
        addMerchantGlasses(head);
        addHistorianGlasses(head);
        addMinerBackpack(body);
        addKeeperSack(body);
        addGuardShield(leftArm);
    }

    public static void visibleOuterLayer(DwarfModel model) {
        model.hat.visible = true;
        model.bodywear.visible = true;
        model.legwear.visible = true;
        model.right_armwear.visible = true;
        model.left_armwear.visible = true;
        model.right_footwear.visible = true;
        model.left_footwear.visible = true;
    }

    private static void addExplorerHatExtra(PartDefinition head) {
        head.addOrReplaceChild(PART_HAT_EXPLORER_EXTRA,
                CubeListBuilder.create()
                        .texOffs(0, 115).addBox(-5.26F, -8.25F, -4.0F, 0.01F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.0F, 2.0F));
    }

    private static void addKeeperHat(PartDefinition head) {
        head.addOrReplaceChild(PART_HAT_KEEPER,
                CubeListBuilder.create()
                        .texOffs(0, 97).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.25F))
                        .texOffs(0, 104).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.25F))
                        .texOffs(18, 116).addBox(-7.0F, -4.0F, -6.0F, 14.0F, 0.01F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.0F, 2.0F));
    }

    private static void addMerchantGlasses(PartDefinition head) {
        head.addOrReplaceChild(PART_GLASSES_MERCHANT,
                CubeListBuilder.create()
                        .texOffs(80, 10).addBox(0.0F, -4.75F, -4.76F, 4.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F));
    }

    private static void addHistorianGlasses(PartDefinition head) {
        head.addOrReplaceChild(PART_GLASSES_HISTORIAN,
                CubeListBuilder.create()
                        .texOffs(76, 10).addBox(-3.0F, -3.75F, -4.76F, 2.0F, 1.0F, 0.01F, new CubeDeformation(0.0F))
                        .texOffs(78, 10).addBox(1.0F, -3.75F, -4.76F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F));
    }

    private static void addMinerBackpack(PartDefinition body) {
        body.addOrReplaceChild(PART_BACKPACK,
                CubeListBuilder.create()
                        .texOffs(0, 116).addBox(-5.0F, -7.0F, 3.0F, 10.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.0F, 0.0F));
    }

    private static void addKeeperSack(PartDefinition body) {
        body.addOrReplaceChild(PART_SACK,
                CubeListBuilder.create()
                        .texOffs(0, 113).addBox(-5.0F, -5.0F, 3.26F, 10.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
    }

    private static void addGuardShield(PartDefinition leftArm) {
        leftArm.addOrReplaceChild(PART_SHIELD,
                CubeListBuilder.create()
                        .texOffs(0, 105).addBox(-2.0F, -20.0F, -1.0F, 12.0F, 22.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(27, 115).addBox(3.75F, -14.0F, -0.75F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.25F, 11.5F, 11.0F, 1.5708F, 0.0F, -1.5708F));
    }
}