package net.sievert.jolcraft.data.custom.attachment.rep;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DwarvenReputationImpl implements DwarvenReputation {

    private int tier = 0;
    private final Set<ResourceLocation> endorsements = new HashSet<>();

    private static final int[] ENDORSEMENT_THRESHOLDS = {2, 5, 9, 14};

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = tier;
    }

    @Override
    public Set<ResourceLocation> getEndorsements() {
        return endorsements;
    }

    @Override
    public void addEndorsement(ResourceLocation professionId) {
        endorsements.add(professionId);
    }

    @Override
    public boolean hasEndorsement(ResourceLocation professionId) {
        return endorsements.contains(professionId);
    }

    public static int getThresholdCount() {
        return ENDORSEMENT_THRESHOLDS.length;
    }

    public static int getThresholdForTier(int tier) {
        return (tier >= 0 && tier < ENDORSEMENT_THRESHOLDS.length)
                ? ENDORSEMENT_THRESHOLDS[tier]
                : Integer.MAX_VALUE;
    }

    public static boolean canAdvance(int currentTier, int endorsements) {
        return currentTier < ENDORSEMENT_THRESHOLDS.length
                && endorsements >= getThresholdForTier(currentTier);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tier", tier);

        ListTag endorsementList = new ListTag();
        for (ResourceLocation id : endorsements) {
            endorsementList.add(StringTag.valueOf(id.toString()));
        }
        tag.put("endorsements", endorsementList);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        this.tier = tag.getInt("tier");
        this.endorsements.clear();

        ListTag endorsementList = tag.getList("endorsements", 8); // 8 = StringTag
        for (int i = 0; i < endorsementList.size(); i++) {
            String idString = endorsementList.getString(i);
            ResourceLocation id = ResourceLocation.tryParse(idString);
            if (id != null) {
                endorsements.add(id);
            } else {
                JolCraft.LOGGER.warn("Failed to parse endorsement ID: '{}'", idString);
            }
        }
    }

    public static final Codec<DwarvenReputationImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tier").forGetter(rep -> rep.tier),
            ResourceLocation.CODEC.listOf()
                    .xmap(HashSet::new, ArrayList::new)
                    .fieldOf("endorsements")
                    .forGetter(rep -> new HashSet<>(rep.endorsements))
    ).apply(instance, (tier, endorsementSet) -> {
        DwarvenReputationImpl impl = new DwarvenReputationImpl();
        impl.tier = tier;
        impl.endorsements.addAll(endorsementSet);
        return impl;
    }));
}
