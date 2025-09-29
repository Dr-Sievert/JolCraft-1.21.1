package net.sievert.jolcraft.entity.client.model.dwarf;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfAnimationType;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfAnimations;
import org.jetbrains.annotations.NotNull;

public class DwarfGuardModel extends DwarfModel{

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_guard"), "main");

    private final ModelPart shield;

    public DwarfGuardModel(ModelPart root) {
        super(root);
        this.shield = this.left_arm.getChild("shield");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        DwarfModelHelper.baseDwarfModel(root);

        PartDefinition leftArm = root.getChild("left_arm");
        leftArm.addOrReplaceChild("shield",
                CubeListBuilder.create()
                        .texOffs(0, 105).addBox(-2.0F, -20.0F, -1.0F, 12.0F, 22.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(27, 115).addBox(3.75F, -14.0F, -0.75F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.25F, 11.5F, 11.0F, 1.5708F, 0.0F, -1.5708F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(DwarfRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(state.yRot, state.xRot);
        this.animateWalk(DwarfAnimations.DWARF_WALK, state.walkAnimationPos, state.walkAnimationSpeed, 2f, 2.5f);

        this.animate(state.idleAnimationState, DwarfAnimations.DWARF_IDLE, state.ageInTicks, 1f);

        for (DwarfAnimationType type : DwarfAnimationType.values()) {
            if (type == DwarfAnimationType.ATTACK) {
                this.animate(
                        state.animationStates.get(type),
                        DwarfAnimations.DWARF_ATTACK_AXE,
                        state.ageInTicks,
                        1f
                );
            } else {
                this.animate(
                        state.animationStates.get(type),
                        DwarfAnimations.getByType(type),
                        state.ageInTicks,
                        1f
                );
            }
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
    protected AnimationDefinition getAttackAnimationFor(DwarfRenderState state, DwarfAnimationType type) {
        if (type == DwarfAnimationType.ATTACK) return DwarfAnimations.DWARF_ATTACK_AXE;

        return super.getAttackAnimationFor(state, type);
    }

    @Override
    public void translateToHand(@NotNull HumanoidArm side, @NotNull PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.getArm(side).translateAndRotate(poseStack);
        poseStack.translate(-0.05F, -0.03F, 0.13F);

    }

}
