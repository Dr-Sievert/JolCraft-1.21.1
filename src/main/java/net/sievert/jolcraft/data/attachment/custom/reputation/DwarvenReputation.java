package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;

import java.util.Set;

public interface DwarvenReputation extends INBTSerializable<CompoundTag> {

    // ---------------------------------------------------------------------
    // Tier
    // ---------------------------------------------------------------------

    int getTierId();
    void setTierId(int tierId);

    default DwarvenReputationTier getTier() {
        return DwarvenReputationTier.fromId(getTierId());
    }

    default void setTier(DwarvenReputationTier tier) {
        setTierId((tier == null ? DwarvenReputationTier.STRANGER : tier).getId());
    }

    // ---------------------------------------------------------------------
    // Endorsements
    // ---------------------------------------------------------------------

    /**
     * Immutable snapshot. Never returns a mutable backing set.
     */
    Set<ResourceLocation> getEndorsements();

    /**
     * Only supported write path.
     */
    boolean addEndorsement(ResourceLocation id);

    boolean hasEndorsement(ResourceLocation professionId);

    default boolean addEndorsement(DwarfProfession profession) {
        if (profession == null || profession == DwarfProfession.NONE) return false;
        return addEndorsement(JolCraft.location(profession.getId()));
    }

    default boolean hasEndorsement(DwarfProfession profession) {
        if (profession == null || profession == DwarfProfession.NONE) return false;
        return hasEndorsement(JolCraft.location(profession.getId()));
    }

    default int getEndorsementCount() {
        return getEndorsements().size();
    }

    // ---------------------------------------------------------------------
    // Access helper
    // ---------------------------------------------------------------------

    static DwarvenReputation get(Player player) {
        return player.getData(JolCraftAttachments.DWARVEN_REPUTATION.get());
    }
}