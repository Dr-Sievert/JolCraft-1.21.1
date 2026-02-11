package net.sievert.jolcraft.world.entity.client.model.object;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.entity.object.JolCraftEntityObjectIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.world.entity.client.util.object.RadiantRenderState;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RadiantModel extends EntityModel<RadiantRenderState>  {

    /** Layer location for Radiant model (register in client init). */
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(JolCraft.location(JolCraftEntityObjectIds.RADIANT), JolCraftDictionary.MAIN);

    public final ModelPart body;

    public RadiantModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
    }

    /**
     * Creates the mesh for the radiant (called by client setup).
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(2, 3)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, -6.0F)
        );
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(@NotNull RadiantRenderState state) {
        super.setupAnim(state);
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float tick = state.ageInTicks;
        float cycle = tick % 60f;
        float spin = (float) (Math.toRadians((cycle / 60f) * 360f));
        float bob = (float) Math.sin(tick * 0.12f) * 2f;

        this.body.xRot = spin;
        this.body.yRot = spin;
        this.body.zRot = 0f;

        this.body.x = 0f;
        this.body.y = 20.0f + bob;
        this.body.z = -6.0f;
    }

}
