package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import java.util.Set;

public interface DwarvenReputation extends INBTSerializable<CompoundTag> {

    int getTier();
    void setTier(int tier);

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
        return addEndorsement(JolCraft.location(profession.id));
    }

    default boolean hasEndorsement(DwarfProfession profession) {
        if (profession == null || profession == DwarfProfession.NONE) return false;
        return hasEndorsement(JolCraft.location(profession.id));
    }

    default int getEndorsementCount() {
        return getEndorsements().size();
    }

    static DwarvenReputation get(Player player) {
        return player.getData(JolCraftAttachments.DWARVEN_REP.get());
    }
}
