package net.sievert.jolcraft.world.entity.custom.object;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.custom.ManagedLightBlock;
import net.sievert.jolcraft.world.block.entity.custom.ManagedLightBlockEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RadiantEntity extends Entity implements TraceableEntity {

    private static final String LIGHT_LEVEL = "RadiantLightLevel";

    @Nullable
    private BlockPos currentLightPos;

    @Nullable
    private UUID ownerUUID;

    @Nullable
    private Entity cachedOwner;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private int radiantLightLevel = 15;

    @Nullable
    private BlockPos lastOwnerPos;

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

        Player owner = getOwnerPlayer();
        if (owner == null) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ENTITY,
                    "RadiantEntity {} discarded: missing owner ownerUUID={} lightPos={} dim={}",
                    getUUID(),
                    ownerUUID,
                    currentLightPos != null ? JolCraftLogs.roundedPos(currentLightPos) : "null",
                    level().dimension().location()
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
        return owner instanceof Player player ? player : null;
    }

    private void serverLightTick() {
        BlockPos newPos = blockPosition();

        if (currentLightPos == null || !currentLightPos.equals(newPos)) {
            if (currentLightPos != null) {
                cleanupOwnedMarkerAt(currentLightPos);
            }

            tryPlaceOrUpdateMarkerAt(newPos);
            currentLightPos = newPos.immutable();
            return;
        }

        if (radiantLightLevel <= 0) {
            cleanupOwnedMarkerAt(newPos);
            return;
        }

        BlockState state = level().getBlockState(newPos);
        if (!state.is(JolCraftBlocks.MANAGED_LIGHT.get())) {
            tryPlaceOrUpdateMarkerAt(newPos);
            return;
        }

        boolean waterlogged = state.getValue(ManagedLightBlock.WATERLOGGED);
        BlockState updated = state
                .setValue(ManagedLightBlock.LEVEL, radiantLightLevel)
                .setValue(ManagedLightBlock.WATERLOGGED, waterlogged);

        if (!state.equals(updated)) {
            level().setBlock(newPos, updated, 3);
        }

        BlockEntity be = level().getBlockEntity(newPos);
        if (be instanceof ManagedLightBlockEntity marker) {
            marker.setOwner(getUUID());
        }
    }

    private void serverFollowTick(Player player) {
        BlockPos currentOwnerPos = player.blockPosition();
        boolean stationary = currentOwnerPos.equals(lastOwnerPos);
        stationaryTicks = stationary ? stationaryTicks + 1 : 0;
        lastOwnerPos = currentOwnerPos;

        int pieces = Mth.clamp((int) Math.round(player.getAttributeValue(JolCraftAttributes.RADIANT)), 0, 4);
        int radius = 1 + pieces;

        double dx = getX() - player.getX();
        double dz = getZ() - player.getZ();
        double dy = getY() - player.getY();
        double horizontalDistSq = dx * dx + dz * dz;

        boolean withinY = dy >= 0.0D && dy <= 4.0D;
        boolean withinRadius = horizontalDistSq <= (radius * radius) && withinY;

        long now = level().getGameTime();
        long cooldownTicks = 20L * 5L;

        boolean allowFastFollow = stationaryTicks >= 20 && player.onGround();

        if (!allowFastFollow) {
            if (now - lastFollowGameTick < cooldownTicks) return;
            if (horizontalDistSq <= (radius * radius)) return;

            lastFollowGameTick = now;
        } else if (withinRadius) {
            return;
        }

        double px = player.getX();
        double py = player.getY() + player.getBbHeight() + 0.5D;
        double pz = player.getZ();

        BlockPos targetPos = BlockPos.containing(px, py, pz);
        BlockState targetState = level().getBlockState(targetPos);

        if (targetState.isAir() || targetState.getFluidState().getType() == Fluids.WATER) {
            setPos(px, py, pz);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide() && currentLightPos != null) {
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

        if (!stateAt.equals(newState)) {
            level().setBlock(pos, newState, 3);
        }

        BlockEntity be = level().getBlockEntity(pos);
        if (be instanceof ManagedLightBlockEntity marker) {
            marker.setOwner(getUUID());
        }
    }

    private void cleanupOwnedMarkerAt(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (!state.is(JolCraftBlocks.MANAGED_LIGHT.get())) return;

        BlockEntity be = level().getBlockEntity(pos);
        if (!(be instanceof ManagedLightBlockEntity marker)) return;

        UUID markerOwner = marker.owner();
        if (markerOwner == null || !markerOwner.equals(getUUID())) return;

        boolean waterlogged = state.getValue(ManagedLightBlock.WATERLOGGED);
        level().setBlock(
                pos,
                waterlogged
                        ? Fluids.WATER.defaultFluidState().createLegacyBlock()
                        : Blocks.AIR.defaultBlockState(),
                3
        );
    }

    public void setRadiantLightLevel(int level) {
        radiantLightLevel = Mth.clamp(level, 0, 15);
    }

    @Override
    @Nullable
    public Entity getOwner() {
        if (cachedOwner != null && !cachedOwner.isRemoved()) {
            return cachedOwner;
        }

        if (ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            cachedOwner = serverLevel.getEntity(ownerUUID);
            if (cachedOwner != null && cachedOwner.isRemoved()) {
                cachedOwner = null;
            }
            return cachedOwner;
        }

        return null;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwner(@Nullable Entity owner) {
        if (owner == null) {
            ownerUUID = null;
            cachedOwner = null;
            return;
        }

        ownerUUID = owner.getUUID();
        cachedOwner = owner;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        ownerUUID = tag.hasUUID(JolCraftDictionary.OWNER)
                ? tag.getUUID(JolCraftDictionary.OWNER)
                : null;
        cachedOwner = null;

        currentLightPos = tag.contains(JolCraftDictionary.POSITION)
                ? BlockPos.of(tag.getLong(JolCraftDictionary.POSITION))
                : null;

        radiantLightLevel = tag.contains(LIGHT_LEVEL)
                ? Mth.clamp(tag.getInt(LIGHT_LEVEL), 0, 15)
                : 15;

        lastOwnerPos = null;
        stationaryTicks = 0;
        lastFollowGameTick = Long.MIN_VALUE;
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
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void move(MoverType type, Vec3 vec) {}
}