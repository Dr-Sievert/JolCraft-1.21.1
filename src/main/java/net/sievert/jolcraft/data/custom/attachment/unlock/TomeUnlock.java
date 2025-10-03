package net.sievert.jolcraft.data.custom.attachment.unlock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.data.JolCraftAttachments;

import java.util.Set;

public interface TomeUnlock<K extends Enum<K>> extends INBTSerializable<CompoundTag> {
    Set<K> getUnlocks();
    void addUnlock(K id);
    boolean hasUnlock(K id);

    @SuppressWarnings("unchecked")
    static <K extends Enum<K>> TomeUnlock<K> get(Player player, Class<K> keyClass) {
        return (TomeUnlock<K>) player.getData(JolCraftAttachments.DWARF_TOME_UNLOCK.get());
    }
}
