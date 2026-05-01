package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.ManagedLightBlock;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.entity.custom.object.RadiantEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ManagedLightBlockEntity extends BlockEntity implements TickingBlockEntity {

    private static final String TAG_OWNER = JolCraftDictionary.OWNER;

    @Nullable
    private UUID owner;

    private int tickCooldown = 0;

    public ManagedLightBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.MANAGED_LIGHT.get(), pos, state);
    }

    public void setOwner(@NotNull UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public @Nullable UUID owner() {
        return owner;
    }

    public void tickServer() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (--this.tickCooldown > 0) {
            return;
        }
        this.tickCooldown = 20;

        if (this.owner == null) {
            cleanupSelf(serverLevel, "no_owner");
            return;
        }

        BlockPos pos = this.getBlockPos();

        AABB box = AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)).inflate(0.25D);

        boolean valid = !serverLevel.getEntitiesOfClass(
                RadiantEntity.class,
                box,
                entity -> this.owner.equals(entity.getUUID()) && entity.blockPosition().equals(pos)
        ).isEmpty();

        if (!valid) {
            cleanupSelf(serverLevel, "missing_radiant");
        }
    }

    private void cleanupSelf(ServerLevel level, String reason) {
        BlockState state = getBlockState();
        if (!state.is(JolCraftBlocks.MANAGED_LIGHT.get())) return;

        JolCraftLogs.debug(
                JolCraftLogTags.BLOCK_ENTITY,
                "ManagedLight cleanup ({}) owner={} pos={} dim={}",
                reason,
                owner,
                JolCraftLogs.roundedPos(this),
                level.dimension().location()
        );

        boolean waterlogged = state.getValue(ManagedLightBlock.WATERLOGGED);
        BlockState replacement = waterlogged
                ? Fluids.WATER.defaultFluidState().createLegacyBlock()
                : Blocks.AIR.defaultBlockState();

        level.setBlock(worldPosition, replacement, 3);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        if (owner != null) {
            tag.putUUID(TAG_OWNER, owner);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.owner = tag.hasUUID(TAG_OWNER) ? tag.getUUID(TAG_OWNER) : null;
    }
}