package net.sievert.jolcraft.data.custom.attachment.hearth;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class HearthImpl implements Hearth {
    private boolean litThisDay = false;

    @Override
    public boolean hasLitThisDay() {
        return litThisDay;
    }

    @Override
    public void setLitThisDay(boolean value) {
        this.litThisDay = value;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("LitThisDay", litThisDay);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        this.litThisDay = tag.getBoolean("LitThisDay");
    }
}