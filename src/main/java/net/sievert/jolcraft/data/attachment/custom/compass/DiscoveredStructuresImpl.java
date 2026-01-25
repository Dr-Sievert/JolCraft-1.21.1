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
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiscoveredStructuresImpl implements DiscoveredStructures {
    private final Set<GlobalPos> discovered = new HashSet<>();

    @Override
    public boolean addDiscovered(GlobalPos pos) {
        return discovered.add(pos);
    }

    @Override
    public boolean isDiscovered(GlobalPos pos) {
        return discovered.contains(pos);
    }

    @Override
    public List<GlobalPos> getDiscovered() {
        return List.copyOf(discovered); // immutable list
    }

    private int discoveryScore = 0;

    public int getScore() {
        return discoveryScore;
    }

    public void addScore(int amount) {
        this.discoveryScore += amount;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (GlobalPos pos : discovered) {
            CompoundTag t = new CompoundTag();
            t.putString("dim", pos.dimension().location().toString());
            t.putLong("pos", pos.pos().asLong());
            list.add(t);
        }
        tag.put("discovered", list);
        tag.putInt("score", discoveryScore);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        discovered.clear();
        ListTag list = tag.getList("discovered", Tag.TAG_COMPOUND);
        for (Tag base : list) {
            CompoundTag t = (CompoundTag) base;
            String dimStr = t.getString("dim");
            long posLong = t.getLong("pos");
            ResourceLocation dimRL = ResourceLocation.tryParse(dimStr);
            if (dimRL == null) continue;
            ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimRL);
            discovered.add(GlobalPos.of(dimKey, BlockPos.of(posLong)));
        }
        discoveryScore = tag.getInt("score");
    }

}
