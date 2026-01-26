package net.sievert.jolcraft.data.attachment.custom.hearth;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.sievert.jolcraft.data.attachment.util.BooleanFlagAttachmentImpl;
import org.jetbrains.annotations.NotNull;

public final class HearthImpl implements Hearth {

    private static final String TAG_LIT_THIS_DAY = "LitThisDay";
    private static final String TAG_LAST_RESET_DAY = "LastResetDay";

    private final BooleanFlagAttachmentImpl litThisDay =
            new BooleanFlagAttachmentImpl(TAG_LIT_THIS_DAY, false);

    private long lastResetDay = -1L;

    @Override
    public boolean hasLitThisDay() {
        return litThisDay.flag();
    }

    @Override
    public void setHasLitThisDay(boolean value) {
        litThisDay.setFlag(value);
    }

    @Override
    public long lastResetDay() {
        return lastResetDay;
    }

    @Override
    public void setLastResetDay(long day) {
        this.lastResetDay = day;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = litThisDay.serializeNBT(provider);
        tag.putLong(TAG_LAST_RESET_DAY, lastResetDay);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        litThisDay.deserializeNBT(provider, tag);
        lastResetDay = tag.contains(TAG_LAST_RESET_DAY) ? tag.getLong(TAG_LAST_RESET_DAY) : -1L;
    }
}