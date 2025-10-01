package net.sievert.jolcraft.data.custom.attachment.language;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class DwarvenLanguageImpl implements DwarvenLanguage {
    private boolean knowsLanguage = false;

    @Override
    public boolean knowsLanguage() {
        return knowsLanguage;
    }

    @Override
    public void setKnowsLanguage(boolean value) {
        this.knowsLanguage = value;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("KnowsLanguage", knowsLanguage);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag tag) {
        this.knowsLanguage = tag.getBoolean("KnowsLanguage");
    }
}
