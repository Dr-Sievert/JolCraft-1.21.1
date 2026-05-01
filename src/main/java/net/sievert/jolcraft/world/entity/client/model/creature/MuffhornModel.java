package net.sievert.jolcraft.world.entity.client.model.creature;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.entity.creature.JolCraftCreatureIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MuffhornModel<T extends Entity> extends QuadrupedModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(JolCraft.location(JolCraftCreatureIds.MUFFHORN), JolCraftDictionary.MAIN);

    private final ModelPart fur_body;
    private final ModelPart head;
    private final ModelPart fur_head;
    private final ModelPart fur_right_hind_leg;
    private final ModelPart fur_left_hind_leg;
    private final ModelPart fur_right_front_leg;
    private final ModelPart fur_left_front_leg;

    public MuffhornModel(ModelPart root) {
        super(root, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);

        ModelPart body = root.getChild("body");
        this.fur_body = body.getChild("fur_body");

        this.head = root.getChild("head");
        this.fur_head = this.head.getChild("fur_head");
        this.head.getChild("right_horn");
        this.head.getChild("left_horn");

        ModelPart right_hind_leg = root.getChild("right_hind_leg");
        this.fur_right_hind_leg = right_hind_leg.getChild("fur_right_hind_leg");

        ModelPart left_hind_leg = root.getChild("left_hind_leg");
        this.fur_left_hind_leg = left_hind_leg.getChild("fur_left_hind_leg");

        ModelPart right_front_leg = root.getChild("right_front_leg");
        this.fur_right_front_leg = right_front_leg.getChild("fur_right_front_leg");

        ModelPart left_front_leg = root.getChild("left_front_leg");
        this.fur_left_front_leg = left_front_leg.getChild("fur_left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(52, 44).addBox(-6.0F, -5.0F, -10.0F, 12.0F, 11.0F, 26.0F, new CubeDeformation(0.0F))
                        .texOffs(72, 32).addBox(-5.0F, -8.0F, -10.0F, 10.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(79, 28).addBox(-5.0F, -7.0F, -2.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(79, 25).addBox(-5.0F, -6.0F, -1.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 6.0F, 2.0F)
        );

        body.addOrReplaceChild(
                "fur_body",
                CubeListBuilder.create()
                        .texOffs(52, 82).addBox(-6.0F, -5.0F, -10.0F, 12.0F, 20.0F, 26.0F, new CubeDeformation(0.75F)),
                PartPose.ZERO
        );

        PartDefinition right_hind_leg = partdefinition.addOrReplaceChild(
                "right_hind_leg",
                CubeListBuilder.create()
                        .texOffs(0, 47).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.01F, 12.0F, 15.0F)
        );

        right_hind_leg.addOrReplaceChild(
                "fur_right_hind_leg",
                CubeListBuilder.create()
                        .texOffs(112, 0).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.ZERO
        );

        PartDefinition left_hind_leg = partdefinition.addOrReplaceChild(
                "left_hind_leg",
                CubeListBuilder.create()
                        .texOffs(0, 47).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(4.01F, 12.0F, 15.0F)
        );

        left_hind_leg.addOrReplaceChild(
                "fur_left_hind_leg",
                CubeListBuilder.create()
                        .texOffs(112, 0).mirror().addBox(-2.0F, -0.5F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
                PartPose.ZERO
        );

        PartDefinition right_front_leg = partdefinition.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(0, 47).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.01F, 12.0F, -6.0F)
        );

        right_front_leg.addOrReplaceChild(
                "fur_right_front_leg",
                CubeListBuilder.create()
                        .texOffs(112, 0).addBox(-2.0F, -0.5F, -1.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.5F)),
                PartPose.ZERO
        );

        PartDefinition left_front_leg = partdefinition.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create()
                        .texOffs(0, 47).mirror().addBox(-2.0F, -1.0F, -1.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(4.01F, 12.0F, -6.0F)
        );

        left_front_leg.addOrReplaceChild(
                "fur_left_front_leg",
                CubeListBuilder.create()
                        .texOffs(112, 0).mirror().addBox(-2.0F, -0.5F, -1.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
                PartPose.ZERO
        );

        PartDefinition head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -5.0F, -6.0F, 10.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(1, 15).addBox(-3.0F, -3.0F, -12.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 5.0F, -8.0F)
        );

        head.addOrReplaceChild(
                "fur_head",
                CubeListBuilder.create()
                        .texOffs(33, 0).addBox(-5.0F, -5.0F, -6.0F, 10.0F, 8.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.ZERO
        );

        head.addOrReplaceChild(
                "right_horn",
                CubeListBuilder.create()
                        .texOffs(0, 28).addBox(-7.0F, -3.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 30).addBox(-8.0F, -4.0F, -3.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(-9.0F, -5.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 34).addBox(-10.0F, -6.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 36).addBox(-11.0F, -7.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 38).addBox(-11.0F, -9.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.ZERO
        );

        head.addOrReplaceChild(
                "left_horn",
                CubeListBuilder.create()
                        .texOffs(0, 28).addBox(5.0F, -3.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 30).addBox(5.0F, -4.0F, -3.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(7.0F, -5.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 34).addBox(8.0F, -6.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 36).addBox(9.0F, -7.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 38).addBox(10.0F, -9.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.ZERO
        );

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        float clampedX = Mth.clamp(headPitch, -20.0F, 15.0F);
        float clampedY = Mth.clamp(netHeadYaw, -30.0F, 30.0F);

        this.head.xRot = clampedX * ((float) Math.PI / 180.0F);
        this.head.yRot = clampedY * ((float) Math.PI / 180.0F);
    }

    public void setFurVisible(boolean visible) {
        this.fur_body.visible = visible;
        this.fur_head.visible = visible;
        this.fur_right_hind_leg.visible = visible;
        this.fur_left_hind_leg.visible = visible;
        this.fur_right_front_leg.visible = visible;
        this.fur_left_front_leg.visible = visible;
    }

    public ModelPart getHead() {
        return this.head;
    }
}