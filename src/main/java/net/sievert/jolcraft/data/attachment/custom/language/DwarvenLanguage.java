package net.sievert.jolcraft.data.attachment.custom.language;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;

public interface DwarvenLanguage extends INBTSerializable<CompoundTag> {

    boolean hasLanguage();

    void setHasLanguage(boolean value);

    default boolean setHasLanguageIfChanged(boolean value) {
        if (hasLanguage() == value) return false;
        setHasLanguage(value);
        return true;
    }

    static DwarvenLanguage get(Player player) {
        return player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get());
    }
}
