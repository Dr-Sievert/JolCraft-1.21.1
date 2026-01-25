package net.sievert.jolcraft.data.attachment.custom.compass;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;

import java.util.List;

public interface DiscoveredStructures extends INBTSerializable<CompoundTag> {

    /**
     * Adds a new discovered structure location. Returns true if added (not already present).
     */
    boolean addDiscovered(GlobalPos pos);

    /**
     * Checks if this position has already been discovered.
     */
    boolean isDiscovered(GlobalPos pos);

    /**
     * Returns all discovered positions (usually as an immutable copy).
     */
    List<GlobalPos> getDiscovered();

    /**
     * Returns this player's DiscoveredStructures attachment.
     */
    static DiscoveredStructures get(Player player) {
        return player.getData(JolCraftAttachments.DISCOVERED_STRUCTURES.get());
    }
}
