package net.sievert.jolcraft.data.attachment.custom.language.ancient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;

public interface AncientDwarvenLanguage extends INBTSerializable<CompoundTag> {
    boolean knowsLanguage();
    void setKnowsLanguage(boolean value);

    static AncientDwarvenLanguage get(Player player) {
        return player.getData(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get());
    }
}
