package net.sievert.jolcraft.data.custom.attachment.lore;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LoreUnlockImpl<K extends Enum<K>> implements LoreUnlock<K> {
    private final Set<K> unlocks = new HashSet<>();
    private final Class<K> keyClass;

    public LoreUnlockImpl(Class<K> keyClass) {
        this.keyClass = keyClass;
    }

    public LoreUnlockImpl(Class<K> keyClass, Collection<K> initialUnlocks) {
        this.keyClass = keyClass;
        this.unlocks.addAll(initialUnlocks);
    }

    @Override
    public Set<K> getUnlocks() {
        return Collections.unmodifiableSet(unlocks);
    }

    @Override
    public void addUnlock(K id) {
        unlocks.add(id);
    }

    @Override
    public boolean hasUnlock(K id) {
        return unlocks.contains(id);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (K id : unlocks) {
            list.add(StringTag.valueOf(id.name().toLowerCase(Locale.ROOT)));
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
                try {
                    unlocks.add(Enum.valueOf(keyClass, id.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException ex) {
                    JolCraft.LOGGER.warn("Unknown LoreUnlock key: " + id);
                }
            } else {
                JolCraft.LOGGER.warn("Empty LoreUnlock string found during load.");
            }
        }
    }
}
