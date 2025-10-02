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

public class DwarfMerchantModel extends DwarfModel {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_merchant"), "main");

    public final ModelPart glasses;

    public DwarfMerchantModel(ModelPart root) {
        super(root);
        this.glasses = this.head.getChild("glasses");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        DwarfModelHelper.baseDwarfModel(root);

        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("glasses",
                CubeListBuilder.create()
                        .texOffs(80, 10).addBox(0.0F, -4.75F, -4.76F, 4.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F)
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
            poseStack.translate(0.05F, -0.15F, 0.05F);
        } else {
            super.translateToHand(side, poseStack);
        }
    }


}

