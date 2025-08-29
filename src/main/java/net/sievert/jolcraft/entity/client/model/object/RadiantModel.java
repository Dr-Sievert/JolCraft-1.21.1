package net.sievert.jolcraft.entity.client.model.object;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.object.RadiantRenderState;
import org.jetbrains.annotations.NotNull;

/**
 * Model for RadiantEntity – single "body" cube, fully animation-driven.
 */
public class RadiantModel extends EntityModel<RadiantRenderState>  {

    /** Layer location for Radiant model (register in client init). */
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "radiant"), "main"
    );

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
        // Blockbench export: centered at (0,20,-6), 2x2x2 cube.
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

        // Manual example: spin & bob (replace with your logic/math)
        float tick = state.ageInTicks; // smooth
        float cycle = tick % 60f;      // 60 ticks loop
        float spin = (float) (Math.toRadians((cycle / 60f) * 360f)); // 0 to 2PI
        float bob = (float) Math.sin(tick * 0.12f) * 2f;

        // Rotate
        this.body.xRot = spin;
        this.body.yRot = spin;
        this.body.zRot = 0f;

        // Position: base + bobbing
        this.body.x = 0f;            // default x
        this.body.y = 20.0f + bob;   // base y + bob
        this.body.z = -6.0f;         // default z

        // If you want to follow your original keyframes, do the math for each one,
        // or use a big switch/case, or interpolate by hand.
    }

}
