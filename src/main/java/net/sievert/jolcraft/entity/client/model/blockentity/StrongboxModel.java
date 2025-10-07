package net.sievert.jolcraft.entity.client.model.blockentity;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;

@OnlyIn(Dist.CLIENT)
public class StrongboxModel extends Model {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(JolCraft.location("strongbox"), "main");

    private static final String BOTTOM = "bottom";
    private static final String LID = "lid";
    private static final String LOCK = "lock";
    private final ModelPart lid;
    private final ModelPart lock;

    public StrongboxModel(ModelPart root) {
        super(root, RenderType::entitySolid);
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 1.0F, -5.0F, 14.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(-7.0F, 0.0F, -5.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 30).addBox(-7.0F, 0.0F, 4.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(6.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(18, 32).addBox(-7.0F, 0.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        bottom.addOrReplaceChild("hinge", CubeListBuilder.create().texOffs(48, 48).addBox(3.0F, -1.0F, 4.65F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 48).addBox(-5.0F, -1.0F, 4.65F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition lid = partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 17).addBox(-7.0F, -1.0F, -10.0F, 14.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(30, 28).addBox(-7.0F, 0.0F, -10.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 30).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 32).addBox(6.0F, 0.0F, -9.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 41).addBox(-7.0F, 0.0F, -9.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 5.0F));

        PartDefinition lock = partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(48, 61).addBox(-1.0F, 0.0F, -10.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 5.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(float openness) {
        this.lid.xRot = -(openness * (float) (Math.PI / 2));
        this.lock.xRot = this.lid.xRot;
    }

}
