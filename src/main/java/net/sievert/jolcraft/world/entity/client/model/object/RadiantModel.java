package net.sievert.jolcraft.world.entity.client.model.object;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.entity.object.JolCraftEntityObjectIds;
import net.sievert.jolcraft.data.id.model.JolCraftModelPartIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.entity.custom.object.RadiantEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RadiantModel extends HierarchicalModel<RadiantEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(JolCraft.location(JolCraftEntityObjectIds.RADIANT), JolCraftDictionary.MAIN);

    private final ModelPart root;
    public final ModelPart body;

    public RadiantModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild(JolCraftModelPartIds.Creature.BODY);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                JolCraftModelPartIds.Creature.BODY,
                CubeListBuilder.create()
                        .texOffs(2, 3)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, -6.0F)
        );

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(@NotNull RadiantEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float cycle = ageInTicks % 60.0F;
        float spin = (float) Math.toRadians((cycle / 60.0F) * 360.0F);
        float bob = (float) Math.sin(ageInTicks * 0.12F) * 2.0F;

        this.body.xRot = spin;
        this.body.yRot = spin;
        this.body.zRot = 0.0F;

        this.body.x = 0.0F;
        this.body.y = 20.0F + bob;
        this.body.z = -6.0F;
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}