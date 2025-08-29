package net.sievert.jolcraft.data.custom.attachment.unlock;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TomeUnlockImpl implements TomeUnlock {
    private final Set<String> unlocks = new HashSet<>();

    @Override
    public Set<String> getUnlocks() {
        return unlocks;
    }

    @Override
    public void addUnlock(String id) {
        unlocks.add(id);
    }

    @Override
    public boolean hasUnlock(String id) {
        return unlocks.contains(id);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (String id : unlocks) {
            list.add(StringTag.valueOf(id));
        }
        tag.put("unlocks", list);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        this.unlocks.clear();
        ListTag list = tag.getList("unlocks", 8);
        for (int i = 0; i < list.size(); i++) {
            String id = list.getString(i).trim();
            if (!id.isEmpty()) {
                unlocks.add(id);
            } else {
                JolCraft.LOGGER.warn("Empty TomeUnlock string found during load.");
            }
        }
    }
}
