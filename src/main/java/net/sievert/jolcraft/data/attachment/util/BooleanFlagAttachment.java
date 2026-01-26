package net.sievert.jolcraft.data.attachment.util;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface BooleanFlagAttachment extends INBTSerializable<CompoundTag> {
    boolean flag();
    void setFlag(boolean value);
}