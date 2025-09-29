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
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfAnimationType;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfAnimations;
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
    public final ModelPart right_eye;
    public final ModelPart left_eye;

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

        DwarfModelHelper.baseDwarfModel(partdefinition);

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

    @Override
    public void translateToHand(@NotNull HumanoidArm side, @NotNull PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.getArm(side).translateAndRotate(poseStack);
        poseStack.translate(-0.05F, -0.15F, 0.05F);
    }

    protected AnimationDefinition getAttackAnimationFor(DwarfRenderState state, DwarfAnimationType type) {
        return DwarfAnimations.getByType(type);
    }

    protected @NotNull ModelPart getArm(@NotNull HumanoidArm side) {
        return side == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
    }

    protected void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);
        this.head.yRot = headYaw * ((float)Math.PI / 180f);
        this.head.xRot = headPitch *  ((float)Math.PI / 180f);
    }

}