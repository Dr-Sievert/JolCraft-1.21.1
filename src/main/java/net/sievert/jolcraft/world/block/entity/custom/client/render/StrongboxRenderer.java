package net.sievert.jolcraft.world.block.entity.custom.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.block.custom.StrongboxBlock;
import net.sievert.jolcraft.world.block.entity.custom.client.model.StrongboxModel;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class StrongboxRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
    private final BlockEntityRendererProvider.Context context;
    private static final ResourceLocation TEXTURE = JolCraft.location("textures/entity/block/strongbox.png");
    public final StrongboxModel model;

    public StrongboxRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new StrongboxModel(context.bakeLayer(StrongboxModel.LAYER_LOCATION));
        this.context = context;

    }

    @Override
    public void render(T tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.translate(0.5F, 0.75F, 0.5F);

        BlockState state = tileEntity.getBlockState();
        Direction facing = state.getValue(StrongboxBlock.FACING);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        poseStack.translate(0F, -0.75F, 0F);

        float openness = tileEntity.getOpenNess(partialTicks);
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        StrongboxModel freshModel = new StrongboxModel(context.bakeLayer(StrongboxModel.LAYER_LOCATION));
        freshModel.setupAnim(openness);
        freshModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }


    @Override
    public @NotNull AABB getRenderBoundingBox(T blockEntity) {
        return AABB.encapsulatingFullBlocks(blockEntity.getBlockPos().offset(-1, 0, -1), blockEntity.getBlockPos().offset(1, 1, 1));
    }
}
