package net.sievert.jolcraft.world.entity.custom.object;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.ManagedLightBlock;
import net.sievert.jolcraft.world.block.entity.custom.ManagedLightBlockEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RadiantEntity extends Entity implements TraceableEntity {

    @Nullable
    private BlockPos currentLightPos = null;

    // === Owner Tracking ===
    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;

    // === Animation State ===
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    // --- Light Level ---
    private int radiantLightLevel = 15;

    public RadiantEntity(EntityType<? extends RadiantEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            if (idleAnimationTimeout <= 0) {
                idleAnimationTimeout = 120;
                idleAnimationState.start(this.tickCount);
            } else {
                --idleAnimationTimeout;
            }
            return;
        }

        BlockPos newPos = this.blockPosition();

        if (currentLightPos == null || !currentLightPos.equals(newPos)) {
            // Best-effort cleanup of the previous marker (not required for correctness)
            if (currentLightPos != null) {
                cleanupOwnedMarkerAt(currentLightPos);
            }

            tryPlaceOrUpdateMarkerAt(newPos);
            currentLightPos = newPos.immutable();
            return;
        }

        // Same blockpos: ensure light level matches our current configured level
        if (getRadiantLightLevel() > 0) {
            BlockState state = level().getBlockState(newPos);
            if (state.is(JolCraftBlocks.MANAGED_LIGHT.get())) {
                int cur = state.getValue(ManagedLightBlock.LEVEL);
                if (cur != getRadiantLightLevel()) {
                    boolean waterlogged = state.getValue(ManagedLightBlock.WATERLOGGED);
                    BlockState updated = state
                            .setValue(ManagedLightBlock.LEVEL, getRadiantLightLevel())
                            .setValue(ManagedLightBlock.WATERLOGGED, waterlogged);

                    level().setBlock(newPos, updated, 3);
                }

                // Ensure BE ownership is set (covers edge cases / migrations)
                BlockEntity be = level().getBlockEntity(newPos);
                if (be instanceof ManagedLightBlockEntity marker) {
                    marker.setOwner(this.getUUID());
                }
            } else {
                // Our marker got replaced; re-place if possible
                tryPlaceOrUpdateMarkerAt(newPos);
            }
        } else {
            // Light level is zero: remove our marker if we're standing in one we own
            cleanupOwnedMarkerAt(newPos);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && currentLightPos != null) {
            cleanupOwnedMarkerAt(currentLightPos);
        }
        super.remove(reason);
    }

    private void tryPlaceOrUpdateMarkerAt(BlockPos pos) {
        if (getRadiantLightLevel() <= 0) return;

        BlockState stateAt = level().getBlockState(pos);
        boolean isWater = stateAt.getFluidState().getType() == Fluids.WATER;

        if (!(stateAt.isAir() || isWater || stateAt.is(JolCraftBlocks.MANAGED_LIGHT.get()))) {
            return;
        }

        BlockState newState = JolCraftBlocks.MANAGED_LIGHT.get().defaultBlockState()
                .setValue(ManagedLightBlock.LEVEL, getRadiantLightLevel())
                .setValue(ManagedLightBlock.WATERLOGGED, isWater);

        if (!stateAt.is(JolCraftBlocks.MANAGED_LIGHT.get()) || stateAt != newState) {
            level().setBlock(pos, newState, 3);
        }

        BlockEntity be = level().getBlockEntity(pos);
        if (be instanceof ManagedLightBlockEntity marker) {
            marker.setOwner(this.getUUID());
        }
    }

    private void cleanupOwnedMarkerAt(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (!state.is(JolCraftBlocks.MANAGED_LIGHT.get())) return;

        BlockEntity be = level().getBlockEntity(pos);
        if (!(be instanceof ManagedLightBlockEntity marker)) return;

        UUID owner = marker.owner();
        if (owner == null || !owner.equals(this.getUUID())) return;

        boolean waterlogged = state.getValue(ManagedLightBlock.WATERLOGGED);
        level().setBlock(pos,
                waterlogged ? Fluids.WATER.defaultFluidState().createLegacyBlock() : Blocks.AIR.defaultBlockState(),
                3
        );
    }

    /** Returns the current light level emitted (0-15). */
    public int getRadiantLightLevel() {
        return radiantLightLevel;
    }

    /** Sets the light level emitted (0-15). Clamps to valid range. */
    public void setRadiantLightLevel(int level) {
        this.radiantLightLevel = Math.max(0, Math.min(15, level));
    }

    // === OWNER GET/SET ===
    @Override
    @Nullable
    public Entity getOwner() {
        if (cachedOwner != null && !cachedOwner.isRemoved()) {
            return cachedOwner;
        }
        if (ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            cachedOwner = serverLevel.getEntity(ownerUUID);
            return cachedOwner;
        }
        return null;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        } else {
            this.ownerUUID = null;
            this.cachedOwner = null;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        } else {
            this.ownerUUID = null;
            this.cachedOwner = null;
        }

        if (tag.contains("LightPos")) {
            this.currentLightPos = BlockPos.of(tag.getLong("LightPos"));
        } else {
            this.currentLightPos = null;
        }

        if (tag.contains("RadiantLightLevel")) {
            this.radiantLightLevel = Math.max(0, Math.min(15, tag.getInt("RadiantLightLevel")));
        } else {
            this.radiantLightLevel = 15;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }

        if (this.currentLightPos != null) {
            tag.putLong("LightPos", this.currentLightPos.asLong());
        }

        tag.putInt("RadiantLightLevel", this.radiantLightLevel);
    }

    // === INVULNERABILITY/PHYSICS ===
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override public boolean isNoGravity() { return true; }
    @Override public void move(MoverType type, Vec3 vec) {}
}