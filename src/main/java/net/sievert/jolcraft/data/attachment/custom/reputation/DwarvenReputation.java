package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;

import java.util.Set;

public interface DwarvenReputation extends INBTSerializable<CompoundTag> {
    int getTier();
    void setTier(int tier);

    Set<DwarfProfession> getEndorsements();
    void addEndorsement(DwarfProfession profession);
    boolean hasEndorsement(DwarfProfession profession);

    default int getEndorsementCount() {
        return getEndorsements().size();
    }

    static DwarvenReputation get(Player player) {
        return player.getData(JolCraftAttachments.DWARVEN_REP.get());
    }
}
