package net.sievert.jolcraft.world.entity.client.model.dwarf;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfModelHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class AbstractHumanoidHierarchicalModel<T extends LivingEntity> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {

    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    protected final ModelPart root;

    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    protected AbstractHumanoidHierarchicalModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild(DwarfModelHelper.PART_HEAD);
        this.hat = this.head.getChild(DwarfModelHelper.PART_HAT);
        this.body = root.getChild(DwarfModelHelper.PART_BODY);
        this.rightArm = root.getChild(DwarfModelHelper.PART_RIGHT_ARM);
        this.leftArm = root.getChild(DwarfModelHelper.PART_LEFT_ARM);
        this.rightLeg = root.getChild(DwarfModelHelper.PART_RIGHT_LEG);
        this.leftLeg = root.getChild(DwarfModelHelper.PART_LEFT_LEG);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public @NotNull ModelPart getHead() {
        return this.head;
    }

    @Override
    public void translateToHand(@NotNull HumanoidArm side, @NotNull PoseStack poseStack) {
        this.getArm(side).translateAndRotate(poseStack);
    }

    protected @NotNull ModelPart getArm(@NotNull HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    protected void applyAnimation(@NotNull AnimationDefinition definition, long accumulatedTime, float scale) {
        KeyframeAnimations.animate(this, definition, accumulatedTime, scale, ANIMATION_VECTOR_CACHE);
    }

    public void forwardAnimation(@NotNull AnimationState state, @NotNull AnimationDefinition definition, float ageInTicks, float speed) {
        state.updateTime(ageInTicks, speed);
        state.ifStarted(started -> this.applyAnimation(definition, started.getAccumulatedTime(), 1.0F));
    }
}