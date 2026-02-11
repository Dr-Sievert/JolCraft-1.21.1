package net.sievert.jolcraft.data.attachment.custom.language;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.sievert.jolcraft.data.attachment.util.BooleanFlagAttachmentImpl;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import org.jetbrains.annotations.NotNull;

public final class DwarvenLanguageImpl implements DwarvenLanguage {

    private final BooleanFlagAttachmentImpl flag = new BooleanFlagAttachmentImpl(JolCraftDataKeys.HAS_LANGUAGE, false);

    @Override
    public boolean hasLanguage() {
        return flag.flag();
    }

    @Override
    public void setHasLanguage(boolean value) {
        flag.setFlag(value);
    }

    @Override
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        return flag.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        flag.deserializeNBT(provider, tag);
    }
}
