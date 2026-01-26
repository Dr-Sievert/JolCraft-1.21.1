package net.sievert.jolcraft.data.attachment.custom.lore;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public final class DwarfLoreUnlockImpl implements DwarfLoreUnlock {

    private static final String TAG_UNLOCKS = "unlocks";

    private final EnumSet<DwarfLoreKey> unlocks = EnumSet.noneOf(DwarfLoreKey.class);

    @Override
    public Set<DwarfLoreKey> getUnlocks() {
        return Set.copyOf(unlocks);
    }

    @Override
    public void addUnlock(DwarfLoreKey id) {
        if (id != null) {
            unlocks.add(id);
        }
    }

    @Override
    public boolean hasUnlock(DwarfLoreKey id) {
        return id != null && unlocks.contains(id);
    }

    @Override
    public void setUnlocks(Set<DwarfLoreKey> snapshot) {
        unlocks.clear();
        if (snapshot != null) {
            unlocks.addAll(snapshot);
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (DwarfLoreKey key : unlocks) {
            list.add(StringTag.valueOf(key.name().toLowerCase(Locale.ROOT)));
        }

        tag.put(TAG_UNLOCKS, list);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        unlocks.clear();

        ListTag list = tag.getList(TAG_UNLOCKS, CompoundTag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            String s = list.getString(i);
            if (s.isEmpty()) continue;

            try {
                unlocks.add(DwarfLoreKey.valueOf(s.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}