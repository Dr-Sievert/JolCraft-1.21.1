package net.sievert.jolcraft.world.entity.custom.object;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.ManagedLightBlock;
import net.sievert.jolcraft.world.block.entity.custom.ManagedLightBlockEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RadiantEntity extends Entity implements TraceableEntity {

    private static final String LIGHT_LEVEL = "RadiantLightLevel";

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    @Nullable
    private BlockPos currentLightPos = null;

    // Owner tracking
    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;

    // Client animation
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    // Light level (server authoritative; synced by handler via setRadiantLightLevel)
    private int radiantLightLevel = 15;

    // Follow state (server-only)
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
            tickClientAnimation();
            return;
        }

        if(currentLightPos == null) return;

        Player owner = getOwnerPlayer();
        if (owner == null) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ENTITY,
                    "RadiantEntity {} discarded: missing owner ownerUUID={} lightPos={} dim={}",
                    getUUID(), ownerUUID, JolCraftLogs.roundedPos(currentLightPos), level().dimension().location()
            );
            discard();
            return;
        }

        serverFollowTick(owner);
        serverLightTick();
    }

    private void tickClientAnimation() {
        if (idleAnimationTimeout <= 0) {
            idleAnimationTimeout = 120;
            idleAnimationState.start(this.tickCount);
        } else {
            --idleAnimationTimeout;
        }
    }

    @Nullable
    private Player getOwnerPlayer() {
        Entity owner = getOwner();
        return (owner instanceof Player p) ? p : null;
    }

    private void serverLightTick() {
        BlockPos newPos = this.blockPosition();

        if (currentLightPos == null || !currentLightPos.equals(newPos)) {
            if (currentLightPos != null) cleanupOwnedMarkerAt(currentLightPos);

            tryPlaceOrUpdateMarkerAt(newPos);
            currentLightPos = newPos.immutable();
            return;
        }

        if (radiantLightLevel > 0) {
            BlockState state = level().getBlockState(newPos);

            if (state.is(JolCraftBlocks.MANAGED_LIGHT.get())) {
                int cur = state.getValue(ManagedLightBlock.LEVEL);
                if (cur != radiantLightLevel) {
                    boolean waterlogged = state.getValue(ManagedLightBlock.WATERLOGGED);
                    BlockState updated = state
                            .setValue(ManagedLightBlock.LEVEL, radiantLightLevel)
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
        boolean stationary = current.equals(lastOwnerPos);
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

        long now = level().getGameTime();
        long cooldownTicks = 20L * 5L; // 5 seconds

        // fast follow only after 1 second stationary + onGround
        boolean allowFastFollow = (stationaryTicks >= 20) && player.onGround();

        if (!allowFastFollow) {
            // cooldown between "catch-up" teleports while moving
            if (now - lastFollowGameTick < cooldownTicks) return;

            // only catch up if outside radius
            if (horizontalDistSq <= (radius * radius)) return;

            lastFollowGameTick = now;
        } else {
            if (withinRadius) return;
        }

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
        if (radiantLightLevel <= 0) return;

        BlockState stateAt = level().getBlockState(pos);
        boolean isWater = stateAt.getFluidState().getType() == Fluids.WATER;

        if (!(stateAt.isAir() || isWater || stateAt.is(JolCraftBlocks.MANAGED_LIGHT.get()))) {
            return;
        }

        BlockState newState = JolCraftBlocks.MANAGED_LIGHT.get().defaultBlockState()
                .setValue(ManagedLightBlock.LEVEL, radiantLightLevel)
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
        level().setBlock(
                pos,
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
        if (tag.hasUUID(JolCraftDictionary.OWNER)) {
            this.ownerUUID = tag.getUUID(JolCraftDictionary.OWNER);
        } else {
            this.ownerUUID = null;
        }
        this.cachedOwner = null;

        if (tag.contains(JolCraftDictionary.POSITION)) {
            this.currentLightPos = BlockPos.of(tag.getLong(JolCraftDictionary.POSITION));
        } else {
            this.currentLightPos = null;
        }

        if (tag.contains(LIGHT_LEVEL)) {
            this.radiantLightLevel = Math.max(0, Math.min(15, tag.getInt(LIGHT_LEVEL)));
        } else {
            this.radiantLightLevel = 15;
        }

        this.lastOwnerPos = null;
        this.stationaryTicks = 0;
        this.lastFollowGameTick = Long.MIN_VALUE;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null) {
            tag.putUUID(JolCraftDictionary.OWNER, ownerUUID);
        }
        if (currentLightPos != null) {
            tag.putLong(JolCraftDictionary.POSITION, currentLightPos.asLong());
        }
        tag.putInt(LIGHT_LEVEL, radiantLightLevel);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void move(MoverType type, Vec3 vec) {}
}