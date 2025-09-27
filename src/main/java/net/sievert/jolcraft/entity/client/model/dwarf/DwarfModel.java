package net.sievert.jolcraft.entity.client.model.dwarf;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.dwarf.DwarfAnimationType;
import net.sievert.jolcraft.entity.client.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.dwarf.DwarfAnimations;
import org.jetbrains.annotations.NotNull;

public class DwarfModel extends HumanoidModel<DwarfRenderState>{

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf"), "main");
    public final ModelPart body;
    public final ModelPart right_arm;
    public final ModelPart left_arm;
    public final ModelPart right_leg;
    public final ModelPart left_leg;
    public final ModelPart bodywear;
    public final ModelPart legwear;
    public final ModelPart right_armwear;
    public final ModelPart left_armwear;
    public final ModelPart right_footwear;
    public final ModelPart left_footwear;
    public final ModelPart head;
    public final ModelPart beard;
    public final ModelPart right_eyebrow;
    public final ModelPart left_eyebrow;
    public final ModelPart hat;
    private final ModelPart right_eye;
    private final ModelPart left_eye;

    public DwarfModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
        this.bodywear = this.body.getChild("bodywear");
        this.legwear = this.body.getChild("legwear");
        this.right_armwear = this.right_arm.getChild("right_armwear");
        this.left_armwear = this.left_arm.getChild("left_armwear");
        this.right_footwear = this.right_leg.getChild("right_footwear");
        this.left_footwear = this.left_leg.getChild("left_footwear");
        this.head = root.getChild("head");
        this.beard = this.head.getChild("beard");
        this.right_eyebrow = this.head.getChild("right_eyebrow");
        this.left_eyebrow = this.head.getChild("left_eyebrow");
        this.hat = this.head.getChild("hat");
        this.right_eye = this.head.getChild("right_eye");
        this.left_eye = this.head.getChild("left_eye");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 31).addBox(-6.0F, -6.0F, -3.0F, 12.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

        body.addOrReplaceChild("legwear", CubeListBuilder.create().texOffs(91, 44).addBox(-6.0F, 16.25F, -3.0F, 12.0F, 3.75F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -11.0F, 0.0F));

        body.addOrReplaceChild("bodywear", CubeListBuilder.create().texOffs(91, 27).addBox(-6.0F, 5.0F, -3.0F, 12.0F, 10.75F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 18).addBox(-3.8F, -0.01F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 5.0F, 0.0F));

        right_arm.addOrReplaceChild("right_armwear", CubeListBuilder.create().texOffs(110, 18).addBox(-3.8F, 3.99F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 18).addBox(-0.2F, -0.01F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 5.0F, 0.0F));

        left_arm.addOrReplaceChild("left_armwear", CubeListBuilder.create().texOffs(92, 18).addBox(-0.2F, -0.01F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(18, 49).addBox(-2.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 17.0F, 0.0F));

        right_leg.addOrReplaceChild("right_footwear", CubeListBuilder.create().texOffs(109, 54).addBox(-2.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 49).addBox(-3.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 17.0F, 0.0F));

        left_leg.addOrReplaceChild("left_footwear", CubeListBuilder.create().texOffs(91, 54).addBox(-3.0F, -0.5F, -2.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -2.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(36, 0).addBox(-5.0F, 1.0F, -2.0F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.15F))
                .texOffs(36, 8).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -2.0F));

        head.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(45, 22).addBox(-5.0F, -18.0F, -4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(47, 20).addBox(-4.0F, -17.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(49, 16).addBox(-3.0F, -14.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(47, 18).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(49, 14).addBox(-3.0F, -15.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(51, 12).addBox(-2.0F, -13.0F, -4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(53, 10).addBox(-1.0F, -12.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 2.0F));

        head.addOrReplaceChild("right_eyebrow", CubeListBuilder.create().texOffs(60, 10).addBox(-3.0F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.0F, -2.0F));

        head.addOrReplaceChild("left_eyebrow", CubeListBuilder.create().texOffs(60, 10).addBox(0.0F, -1.0F, -0.01F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -3.0F, -2.0F));

        head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(92, 0).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -3.0F, 2.0F));

        head.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(69, 10).addBox(-3.0F, -5.0F, -4.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 2.0F));

        head.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(73, 10).addBox(1.0F, -5.0F, -4.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(DwarfRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(state.yRot, state.xRot);
        this.animateWalk(DwarfAnimations.DWARF_WALK, state.walkAnimationPos, state.walkAnimationSpeed, 2f, 2.5f);

        this.animate(state.idleAnimationState, DwarfAnimations.DWARF_IDLE, state.ageInTicks, 1f);

        for (DwarfAnimationType type : DwarfAnimationType.values()) {
            this.animate(
                    state.animationStates.get(type),
                    getAttackAnimationFor(state, type),
                    state.ageInTicks,
                    1f
            );
        }

        this.hat.visible = !state.headEquipment.isEmpty();
        boolean hasChest = !state.chestEquipment.isEmpty();
        this.bodywear.visible = hasChest;
        this.right_armwear.visible = hasChest;
        this.left_armwear.visible = hasChest;
        this.legwear.visible = !state.legsEquipment.isEmpty();
        boolean hasBoots = !state.feetEquipment.isEmpty();
        this.right_footwear.visible = hasBoots;
        this.left_footwear.visible = hasBoots;
    }

    protected AnimationDefinition getAttackAnimationFor(DwarfRenderState state, DwarfAnimationType type) {
        return DwarfAnimations.getByType(type);
    }


    @Override
    public void translateToHand(@NotNull HumanoidArm side, @NotNull PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.getArm(side).translateAndRotate(poseStack);
        poseStack.translate(-0.05F, -0.15F, 0.05F);
    }

    protected void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);
        this.head.yRot = headYaw * ((float)Math.PI / 180f);
        this.head.xRot = headPitch *  ((float)Math.PI / 180f);
    }

    protected @NotNull ModelPart getArm(@NotNull HumanoidArm side) {
        return side == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
    }

}