package net.sievert.jolcraft.data.attachment.custom.compass;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;

import java.util.Set;

public interface DiscoveredStructures extends INBTSerializable<CompoundTag> {

    /**
     * Adds a newly discovered structure position.
     * @return true if added (not already present)
     */
    boolean addDiscovered(GlobalPos pos);

    /**
     * Checks if the position has already been discovered.
     */
    boolean isDiscovered(GlobalPos pos);

    /**
     * Returns an unmodifiable snapshot of discovered positions.
     * No backing-collection leaks.
     */
    Set<GlobalPos> getDiscovered();

    /**
     * Discovery score used for progression (e.g., Explorer dwarf XP transfer).
     */
    int getScore();

    /**
     * Adds to the discovery score.
     * @return true if any change was applied (amount != 0)
     */
    boolean addScore(int amount);

    static DiscoveredStructures get(Player player) {
        return player.getData(JolCraftAttachments.DISCOVERED_STRUCTURES.get());
    }
}