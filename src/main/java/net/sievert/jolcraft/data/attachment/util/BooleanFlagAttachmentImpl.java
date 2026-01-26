package net.sievert.jolcraft.data.attachment.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class BooleanFlagAttachmentImpl implements BooleanFlagAttachment {

    private final String tagKey;
    private boolean value;

    public BooleanFlagAttachmentImpl(String tagKey, boolean defaultValue) {
        this.tagKey = tagKey;
        this.value = defaultValue;
    }

    @Override
    public boolean flag() {
        return value;
    }

    @Override
    public void setFlag(boolean value) {
        this.value = value;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(tagKey, value);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag tag) {
        this.value = tag.getBoolean(tagKey);
    }
}
