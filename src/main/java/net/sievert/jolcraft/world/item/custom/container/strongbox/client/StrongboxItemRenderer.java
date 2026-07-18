package net.sievert.jolcraft.world.item.custom.container.strongbox.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.StrongboxBlockEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class StrongboxItemRenderer extends BlockEntityWithoutLevelRenderer {

    private final BlockEntityRenderDispatcher dispatcher;
    private final StrongboxBlockEntity blockEntity;

    public StrongboxItemRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );

        this.dispatcher =
                Minecraft.getInstance().getBlockEntityRenderDispatcher();

        this.blockEntity = new StrongboxBlockEntity(
                net.minecraft.core.BlockPos.ZERO,
                JolCraftBlocks.STRONGBOX.get().defaultBlockState()
        );
    }

    @Override
    public void renderByItem(
            @NotNull ItemStack stack,
            @NotNull ItemDisplayContext displayContext,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        this.dispatcher.renderItem(
                this.blockEntity,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );
    }
}