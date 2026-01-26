package net.sievert.jolcraft.data.attachment.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public final class BooleanFlagAttachmentImpl {

    private final String tagKey;
    private boolean value;

    public BooleanFlagAttachmentImpl(String tagKey, boolean defaultValue) {
        this.tagKey = tagKey;
        this.value = defaultValue;
    }

    public boolean flag() {
        return value;
    }

    public boolean setFlagIfChanged(boolean value) {
        if (this.value == value) return false;
        this.value = value;
        return true;
    }

    public void setFlag(boolean value) {
        this.value = value;
    }

    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(tagKey, value);
        return tag;
    }

    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        this.value = tag.getBoolean(tagKey);
    }
}