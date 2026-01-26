package net.sievert.jolcraft.data.attachment.custom.lore;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;

import java.util.Set;

public interface DwarfLoreUnlock extends INBTSerializable<CompoundTag> {

    /**
     * Must return an unmodifiable snapshot (no backing-set leaks).
     */
    Set<DwarfLoreKey> getUnlocks();

    /**
     * Adds an unlock (no-op if already present).
     */
    void addUnlock(DwarfLoreKey id);

    boolean hasUnlock(DwarfLoreKey id);

    /**
     * Snapshot replace. Must not leak backing collections.
     * Callers provide a snapshot, impl normalizes it.
     */
    void setUnlocks(Set<DwarfLoreKey> unlocks);

    default boolean setUnlocksIfChanged(Set<DwarfLoreKey> unlocks) {
        if (getUnlocks().equals(unlocks)) return false;
        setUnlocks(unlocks);
        return true;
    }

    default boolean addUnlockIfAbsent(DwarfLoreKey id) {
        if (hasUnlock(id)) return false;
        addUnlock(id);
        return true;
    }

    static DwarfLoreUnlock get(Player player) {
        return player.getData(JolCraftAttachments.DWARF_LORE_UNLOCK.get());
    }
}