package net.sievert.jolcraft.data.attachment.custom.compass;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.util.JolCraftLogs;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class DiscoveredStructuresImpl implements DiscoveredStructures {

    private static final String TAG_DISCOVERED = "discovered";
    private static final String TAG_SCORE = "score";
    private static final String TAG_DIM = "dim";
    private static final String TAG_POS = "pos";

    private final Set<GlobalPos> discovered = new HashSet<>();
    private int discoveryScore;

    @Override
    public boolean addDiscovered(GlobalPos pos) {
        return pos != null && discovered.add(pos);
    }

    @Override
    public boolean isDiscovered(GlobalPos pos) {
        return pos != null && discovered.contains(pos);
    }

    @Override
    public Set<GlobalPos> getDiscovered() {
        return Set.copyOf(discovered);
    }

    @Override
    public int getScore() {
        return discoveryScore;
    }

    @Override
    public void addScore(int amount) {
        if (amount == 0) return;
        discoveryScore += amount;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (GlobalPos pos : discovered) {
            CompoundTag t = new CompoundTag();
            t.putString(TAG_DIM, pos.dimension().location().toString());
            t.putLong(TAG_POS, pos.pos().asLong());
            list.add(t);
        }

        tag.put(TAG_DISCOVERED, list);
        tag.putInt(TAG_SCORE, discoveryScore);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        discovered.clear();

        ListTag list = tag.getList(TAG_DISCOVERED, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);

            ResourceLocation dimRL = ResourceLocation.tryParse(t.getString(TAG_DIM));
            if (dimRL == null) {
                JolCraftLogs.debug("Invalid dimension getId in discovered structure NBT: {}", t.getString(TAG_DIM));
                continue;
            }
            ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimRL);
            BlockPos pos = BlockPos.of(t.getLong(TAG_POS));

            discovered.add(GlobalPos.of(dimKey, pos));
        }

        discoveryScore = tag.getInt(TAG_SCORE);
    }
}