package net.sievert.jolcraft.world.entity.client.model.object;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
import net.sievert.jolcraft.data.id.model.JolCraftModelPartIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public class LuminanceModel extends HierarchicalModel<AbstractClientPlayer> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    JolCraft.location(JolCraftAttributeIds.LUMINANCE),
                    JolCraftDictionary.MAIN
            );

    private static final float FULL_SPIN_CYCLE_TICKS = 60.0F;
    private static final float RADIANS_PER_TICK =
            (float) (Math.PI * 2.0D) / FULL_SPIN_CYCLE_TICKS;
    private static final float BOB_SPEED = 0.12F;
    private static final float BOB_AMOUNT = 2.0F;
    private static final float TWO_PI = (float) (Math.PI * 2.0D);

    private final Map<AbstractClientPlayer, SpinState> spinStates = new WeakHashMap<>();
    private final ModelPart root;
    public final ModelPart body;

    public LuminanceModel(ModelPart root) {
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
                PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(
            @NotNull AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        int lightLevel =
                player.level().getMaxLocalRawBrightness(player.blockPosition());

        SpinState state = spinStates.computeIfAbsent(
                player,
                ignored -> new SpinState(ageInTicks)
        );

        float elapsed = ageInTicks - state.lastAgeInTicks;
        if (elapsed < 0.0F) {
            elapsed = 0.0F;
        }

        state.rotation = (
                state.rotation
                        + elapsed
                        * RADIANS_PER_TICK
                        * (lightLevel / 15.0F)
        ) % TWO_PI;
        state.lastAgeInTicks = ageInTicks;

        this.body.xRot = state.rotation;
        this.body.yRot = state.rotation;
        this.body.zRot = 0.0F;
        this.body.y = (float) Math.sin(ageInTicks * BOB_SPEED) * BOB_AMOUNT;
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    private static final class SpinState {
        private float lastAgeInTicks;
        private float rotation;

        private SpinState(float ageInTicks) {
            this.lastAgeInTicks = ageInTicks;
        }
    }
}
