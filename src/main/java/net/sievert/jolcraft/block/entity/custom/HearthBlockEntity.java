package net.sievert.jolcraft.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sievert.jolcraft.block.custom.HearthBlock;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.effect.JolCraftEffects;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class HearthBlockEntity extends BlockEntity {

    // Set of UUIDs for players who have activated this hearth
    private final Set<UUID> activePlayers = new HashSet<>();

    public HearthBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.HEARTH.get(), pos, state);
    }

    // --- Save/load NBT ---
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        // Save UUIDs as string list
        ListTag uuidList = new ListTag();
        for (UUID uuid : activePlayers) {
            uuidList.add(StringTag.valueOf(uuid.toString()));
        }
        tag.put("ActivePlayers", uuidList);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        activePlayers.clear();
        ListTag uuidList = tag.getList("ActivePlayers", 8); // 8 = String
        for (int i = 0; i < uuidList.size(); i++) {
            try {
                activePlayers.add(UUID.fromString(uuidList.getString(i)));
            } catch (Exception ignored) {}
        }
    }

    // Add a player to the hearth, returns true if new, false if already present
    public boolean activateFor(UUID playerId) {
        boolean wasAdded = activePlayers.add(playerId);
        setChanged();
        return wasAdded;
    }

    // Remove a player from the hearth (if needed, e.g., on logout)
    public boolean deactivateFor(UUID playerId) {
        boolean wasRemoved = activePlayers.remove(playerId);
        setChanged();
        return wasRemoved;
    }

    // Is the hearth currently lit (at least one active player)?
    public boolean isLit() {
        return !activePlayers.isEmpty();
    }

    // Utility: Get the current set (copy for safety)
    public Set<UUID> getActivePlayers() {
        return Set.copyOf(activePlayers);
    }
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;
        if (this.level.getGameTime() % 200 != 0) return;

        // Only give regeneration if the hearth is lit
        if (this.getBlockState().getValue(HearthBlock.LIT)) {
            for (UUID uuid : activePlayers) {
                ServerPlayer player = Objects.requireNonNull(this.level.getServer()).getPlayerList().getPlayer(uuid);
                if (player == null) continue;
                if (!player.level().dimension().equals(this.level.dimension())) continue; // Same dimension only

                double distSq = player.blockPosition().distSqr(this.getBlockPos());
                if (distSq <= 100) { // 10 block radius (10*10)
                    player.addEffect(new MobEffectInstance(JolCraftEffects.HOMESTEAD, 300, 0, true, true));
                }
            }
        }

        boolean anyValidBed = false;

        for (UUID uuid : activePlayers) {
            Player player = this.level.getPlayerByUUID(uuid);
            if (!(player instanceof ServerPlayer serverPlayer)) continue;

            BlockPos bedPos = serverPlayer.getRespawnPosition();
            if (bedPos != null && serverPlayer.getRespawnDimension().equals(this.level.dimension())) {
                BlockState bedState = this.level.getBlockState(bedPos);
                if (bedState.getBlock() instanceof BedBlock) {
                    double distSq = bedPos.distSqr(this.getBlockPos());
                    if (distSq <= 100) {
                        anyValidBed = true;
                        break; // At least one valid bed is enough
                    }
                }
            }
        }

        BlockState state = this.getBlockState();
        if (!anyValidBed && state.getValue(HearthBlock.LIT)) {
            this.level.setBlock(this.getBlockPos(), state.setValue(HearthBlock.LIT, false), 3);
            this.level.playSound(null, this.getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.8f);

        }
        if (anyValidBed && !state.getValue(HearthBlock.LIT)) {
            this.level.setBlock(this.getBlockPos(), state.setValue(HearthBlock.LIT, true), 3);
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0f, 0.8f);
        }
    }


}
