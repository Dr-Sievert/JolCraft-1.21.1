package net.sievert.jolcraft.data.attachment.custom.reputation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DwarvenReputationImpl implements DwarvenReputation {

    private static final String NBT_TIER = "tier";
    private static final String NBT_ENDORSEMENTS = "endorsements";

    private int tier;
    private final Set<ResourceLocation> endorsements = new HashSet<>();

    public static final int[] ENDORSEMENT_THRESHOLDS = {1, 3, 6, 10};

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = tier;
    }

    /**
     * Immutable snapshot. Never exposes the backing set.
     */
    @Override
    public Set<ResourceLocation> getEndorsements() {
        return Set.copyOf(this.endorsements);
    }

    /**
     * Adds a single endorsement id.
     * Returns true only if the set changed.
     * Rejects null and unknown profession ids.
     */
    @Override
    public boolean addEndorsement(ResourceLocation id) {
        if (id == null) return false;
        if (DwarfProfession.byId(id.getPath()) == DwarfProfession.NONE) return false;
        return this.endorsements.add(id);
    }

    @Override
    public boolean hasEndorsement(ResourceLocation professionId) {
        return professionId != null && endorsements.contains(professionId);
    }

    /**
     * Replaces the full endorsement set with the provided ids.
     * This is the supported way to apply a server snapshot (e.g. from S2C sync),
     * without leaking or exposing the mutable backing set.
     * Returns true only if the effective contents changed.
     */
    public void setEndorsements(Set<ResourceLocation> ids) {
        Set<ResourceLocation> normalized = new HashSet<>();
        if (ids != null && !ids.isEmpty()) {
            for (ResourceLocation id : ids) {
                if (id == null) continue;
                if (DwarfProfession.byId(id.getPath()) == DwarfProfession.NONE) continue;
                normalized.add(id);
            }
        }

        if (this.endorsements.equals(normalized)) return;

        this.endorsements.clear();
        this.endorsements.addAll(normalized);
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
        tag.putInt(NBT_TIER, tier);

        ListTag endorsementList = new ListTag();
        for (ResourceLocation profId : endorsements) {
            endorsementList.add(StringTag.valueOf(profId.toString()));
        }
        tag.put(NBT_ENDORSEMENTS, endorsementList);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        this.tier = tag.getInt(NBT_TIER);

        Set<ResourceLocation> parsed = new HashSet<>();
        ListTag endorsementList = tag.getList(NBT_ENDORSEMENTS, 8); // 8 = StringTag
        for (int i = 0; i < endorsementList.size(); i++) {
            String idString = endorsementList.getString(i);
            ResourceLocation profId = ResourceLocation.tryParse(idString);
            if (profId == null) {
                JolCraft.LOGGER.warn("Failed to parse endorsement profession id: '{}'", idString);
                continue;
            }
            if (DwarfProfession.byId(profId.getPath()) == DwarfProfession.NONE) {
                JolCraft.LOGGER.warn("Unknown endorsement profession id: '{}'", idString);
                continue;
            }
            parsed.add(profId);
        }

        // Use the controlled setter so we keep one rule for normalization/change detection.
        this.setEndorsements(parsed);
    }

    public static final Codec<DwarvenReputationImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf(NBT_TIER).forGetter(rep -> rep.tier),
            Codec.STRING.listOf()
                    .xmap(
                            list -> list.stream()
                                    .map(ResourceLocation::tryParse)
                                    .filter(id -> id != null && DwarfProfession.byId(id.getPath()) != DwarfProfession.NONE)
                                    .collect(Collectors.toSet()),
                            set -> set.stream()
                                    .map(ResourceLocation::toString)
                                    .collect(Collectors.toList())
                    )
                    .fieldOf(NBT_ENDORSEMENTS)
                    .forGetter(rep -> Set.copyOf(rep.endorsements))
    ).apply(instance, (tier, endorsementSet) -> {
        DwarvenReputationImpl impl = new DwarvenReputationImpl();
        impl.tier = tier;
        impl.setEndorsements(endorsementSet);
        return impl;
    }));
}