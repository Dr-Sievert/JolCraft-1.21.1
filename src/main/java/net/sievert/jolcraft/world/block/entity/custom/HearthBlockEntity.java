package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sievert.jolcraft.world.block.custom.HearthBlock;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class HearthBlockEntity extends BlockEntity {

    private static final String NBT_ACTIVE_PLAYERS = "ActivePlayers";

    private static final int TICK_INTERVAL = 200;

    private static final int EFFECT_DURATION = 300;
    private static final int EFFECT_AMPLIFIER = 0;
    private static final int RANGE = 10; // blocks
    private static final int RANGE_SQ = RANGE * RANGE;

    private final Set<UUID> activePlayers = new HashSet<>();

    public HearthBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.HEARTH.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);

        ListTag uuidList = new ListTag();
        for (UUID uuid : activePlayers) {
            uuidList.add(StringTag.valueOf(uuid.toString()));
        }
        tag.put(NBT_ACTIVE_PLAYERS, uuidList);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);

        activePlayers.clear();

        ListTag uuidList = tag.getList(NBT_ACTIVE_PLAYERS, 8); // 8 = String
        for (int i = 0; i < uuidList.size(); i++) {
            try {
                activePlayers.add(UUID.fromString(uuidList.getString(i)));
            } catch (Exception ignored) {
            }
        }
    }

    public boolean activateFor(UUID playerId) {
        boolean wasAdded = activePlayers.add(playerId);
        if (wasAdded) setChanged();
        return wasAdded;
    }

    public void tick() {
        if (!(this.level instanceof ServerLevel serverlevel)) return;
        if ((serverlevel.getGameTime() % TICK_INTERVAL) != 0L) return;

        boolean pruned = pruneInactive(serverlevel);
        if (pruned) setChanged();

        boolean anyValidBed = hasAnyValidBed(serverlevel);

        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();
        boolean lit = state.getValue(HearthBlock.LIT);

        // Turn off if no valid beds remain.
        if (!anyValidBed) {
            if (lit) {
                setLitBoth(serverlevel, pos, false);
                JolCraftSoundHelper.block(
                        serverlevel,
                        pos,
                        SoundEvents.FIRE_EXTINGUISH,
                        1.0F,
                        0.8F
                );
            }
            return;
        }

        // Turn on if at least one valid bed exists.
        if (!lit) {
            setLitBoth(serverlevel, pos, true);
            JolCraftSoundHelper.block(
                    level,
                    pos,
                    SoundEvents.BLAZE_SHOOT,
                    1.0F,
                    0.8F
            );
        }

        // Refresh state after potential changes above.
        state = serverlevel.getBlockState(pos);
        if (state.is(this.getBlockState().getBlock()) && state.getValue(HearthBlock.LIT)) {
            applyHomesteadEffect(serverlevel);
        }
    }

    private static void setLitBoth(ServerLevel level, BlockPos lowerPos, boolean lit) {
        BlockState lower = level.getBlockState(lowerPos);
        if (!(lower.getBlock() instanceof HearthBlock)) return;

        if (lower.getValue(HearthBlock.LIT) != lit) {
            level.setBlock(lowerPos, lower.setValue(HearthBlock.LIT, lit), 3);
        }

        BlockPos upperPos = lowerPos.above();
        BlockState upper = level.getBlockState(upperPos);
        if (upper.is(lower.getBlock()) && upper.hasProperty(HearthBlock.HALF) && upper.getValue(HearthBlock.HALF) == DoubleBlockHalf.UPPER) {
            if (upper.getValue(HearthBlock.LIT) != lit) {
                level.setBlock(upperPos, upper.setValue(HearthBlock.LIT, lit), 3);
            }
        }
    }

    private void applyHomesteadEffect(ServerLevel level) {
        MinecraftServer server = level.getServer();

        BlockPos hearthPos = this.getBlockPos();

        for (UUID uuid : activePlayers) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) continue; // only online players receive effect
            if (player.level() != level) continue;

            if (player.blockPosition().distSqr(hearthPos) <= RANGE_SQ) {
                player.addEffect(new MobEffectInstance(
                        JolCraftEffects.HOMESTEAD,
                        EFFECT_DURATION,
                        EFFECT_AMPLIFIER,
                        true,
                        true
                ));
            }
        }
    }

    private boolean hasAnyValidBed(ServerLevel level) {
        BlockPos hearthPos = this.getBlockPos();

        for (UUID uuid : activePlayers) {
            Player player = level.getPlayerByUUID(uuid);
            if (!(player instanceof ServerPlayer sp)) continue;

            BlockPos bedPos = sp.getRespawnPosition();
            if (bedPos == null) continue;
            if (!sp.getRespawnDimension().equals(level.dimension())) continue;

            BlockState bedState = level.getBlockState(bedPos);
            if (!(bedState.getBlock() instanceof BedBlock)) continue;

            if (bedPos.distSqr(hearthPos) <= RANGE_SQ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes entries that no longer have a valid bed position in this dimension within range.
     * This prevents the persisted UUID list from growing forever.
     */
    private boolean pruneInactive(ServerLevel level) {
        if (activePlayers.isEmpty()) return false;

        boolean changed = false;
        BlockPos hearthPos = this.getBlockPos();

        for (Iterator<UUID> it = activePlayers.iterator(); it.hasNext(); ) {
            UUID uuid = it.next();

            Player p = level.getPlayerByUUID(uuid);
            if (!(p instanceof ServerPlayer sp)) {
                it.remove();
                changed = true;
                continue;
            }

            BlockPos bedPos = sp.getRespawnPosition();
            if (bedPos == null) {
                it.remove();
                changed = true;
                continue;
            }

            if (!sp.getRespawnDimension().equals(level.dimension())) {
                it.remove();
                changed = true;
                continue;
            }

            BlockState bedState = level.getBlockState(bedPos);
            if (!(bedState.getBlock() instanceof BedBlock)) {
                it.remove();
                changed = true;
                continue;
            }

            if (bedPos.distSqr(hearthPos) > RANGE_SQ) {
                it.remove();
                changed = true;
            }
        }

        return changed;
    }
}
