package net.sievert.jolcraft.entity.client.model.dwarf.profession;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import org.jetbrains.annotations.NotNull;

public class DwarfMinerModel extends DwarfModel {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_miner"), "main");

    private final ModelPart backpack;

    public DwarfMinerModel(ModelPart root) {
        super(root);
        this.backpack = this.body.getChild("backpack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        DwarfModelHelper.baseDwarfModel(root);

        PartDefinition body = root.getChild("body");
        body.addOrReplaceChild("backpack",
                CubeListBuilder.create().texOffs(0, 116).addBox(-5.0F, -7.0F, 3.0F, 10.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.0F, 0.0F)
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
        this.root.translateAndRotate(poseStack);
        this.getArm(side).translateAndRotate(poseStack);
        poseStack.translate(-0.05F, -0.03F, 0.13F);

    }


}

