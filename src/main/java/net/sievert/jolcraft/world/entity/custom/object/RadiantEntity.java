package net.sievert.jolcraft.world.entity.custom.object;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftAttributes;
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

    // --- Follow state (server-only) ---
    @Nullable private BlockPos lastOwnerPos = null;
    private int stationaryTicks = 0;
    private long lastFollowGameTick = Long.MIN_VALUE;

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

        Entity owner = getOwner();
        if (owner instanceof Player player) {
            // Follow logic lives here now (NOT in event handler)
            serverFollowTick(player);
        }

        // Marker placement/maintenance (your existing behavior)
        BlockPos newPos = this.blockPosition();

        if (currentLightPos == null || !currentLightPos.equals(newPos)) {
            if (currentLightPos != null) cleanupOwnedMarkerAt(currentLightPos);

            tryPlaceOrUpdateMarkerAt(newPos);
            currentLightPos = newPos.immutable();
            return;
        }

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

                BlockEntity be = level().getBlockEntity(newPos);
                if (be instanceof ManagedLightBlockEntity marker) {
                    marker.setOwner(this.getUUID());
                }
            } else {
                tryPlaceOrUpdateMarkerAt(newPos);
            }
        } else {
            cleanupOwnedMarkerAt(newPos);
        }
    }

    private void serverFollowTick(Player player) {
        // Stationary tracking (blockpos-based)
        BlockPos current = player.blockPosition();
        boolean stationary = (current.equals(lastOwnerPos));
        stationaryTicks = stationary ? (stationaryTicks + 1) : 0;
        lastOwnerPos = current;

        // Radius computed from owner's radiant attribute (same rule as your handler)
        double radiant = player.getAttributeValue(JolCraftAttributes.RADIANT);
        int percent = (int) (radiant * 100);
        int nearest25 = (percent / 25) * 25;
        int radius = 1 + (nearest25 / 25);

        double dx = this.getX() - player.getX();
        double dz = this.getZ() - player.getZ();
        double dy = this.getY() - player.getY();
        double horizontalDistSq = dx * dx + dz * dz;

        boolean withinY = dy >= 0 && dy <= 4;
        boolean withinRadius = horizontalDistSq <= (radius * radius) && withinY;

        final long now = level().getGameTime();
        final long COOLDOWN_TICKS = 20L * 5L; // 5 seconds

        // fast follow only after 1 second stationary + onGround
        boolean allowFastFollow = (stationaryTicks >= 20) && player.onGround();

        if (!allowFastFollow) {
            // 5s cooldown between "catch-up" teleports while moving
            if (now - lastFollowGameTick < COOLDOWN_TICKS) return;

            // Spatial rule unchanged: only catch up if outside radius
            if (horizontalDistSq <= (radius * radius)) return;

            // consume cooldown
            lastFollowGameTick = now;
        } else {
            // Fast follow: same as before
            if (withinRadius) return;
        }

        // Teleport close to player (only if target space is air/water)
        double px = player.getX();
        double py = player.getY() + player.getBbHeight() + 0.5;
        double pz = player.getZ();

        BlockPos targetPos = BlockPos.containing(px, py, pz);
        BlockState targetState = level().getBlockState(targetPos);

        if (targetState.isAir() || targetState.getFluidState().getType() == Fluids.WATER) {
            this.setPos(px, py, pz);
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

    public int getRadiantLightLevel() {
        return radiantLightLevel;
    }

    public void setRadiantLightLevel(int level) {
        this.radiantLightLevel = Math.max(0, Math.min(15, level));
    }

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

        // follow state is runtime-only (intentionally not saved)
        this.lastOwnerPos = null;
        this.stationaryTicks = 0;
        this.lastFollowGameTick = Long.MIN_VALUE;
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

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override public boolean isNoGravity() { return true; }
    @Override public void move(MoverType type, Vec3 vec) {}
}