package net.sievert.jolcraft.entity.client.model.dwarf.profession;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfKeeperModel extends DwarfModel {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(JolCraft.location("dwarf_keeper"), "main");

    public final ModelPart sack;

    public DwarfKeeperModel(ModelPart root) {
        super(root);
        this.sack = this.body.getChild("sack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        DwarfModelHelper.baseDwarfModel(root);

        PartDefinition body = root.getChild("body");
        body.addOrReplaceChild("sack",
                CubeListBuilder.create()
                        .texOffs(0, 113).addBox(-5.0F, -5.0F, 3.26F, 10.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("hat",
                CubeListBuilder.create()
                        .texOffs(0, 97).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.25F))
                        .texOffs(0, 104).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.25F))
                        .texOffs(18, 116).addBox(-7.0F, -4.0F, -6.0F, 14.0F, 0.01F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.0F, 2.0F)
        );

        return LayerDefinition.create(meshdefinition, 128, 128);
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
            poseStack.translate(0.05F, -0.05F, 0.05F);
        } else {
            super.translateToHand(side, poseStack);
        }
    }

}

