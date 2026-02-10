package net.sievert.jolcraft.data.attachment.custom.reputation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DwarvenReputationImpl implements DwarvenReputation {

    private static final String NBT_TIER = "tier";
    private static final String NBT_ENDORSEMENTS = "endorsements";

    /**
     * Stored as int for NBT/network friendliness. Always clamped via {@link DwarvenReputationTier}.
     */
    private int tierId = DwarvenReputationTier.STRANGER.id();

    private final Set<ResourceLocation> endorsements = new HashSet<>();

    public static final int[] ENDORSEMENT_THRESHOLDS = {1, 3, 6, 10};

    // ---------------------------------------------------------------------
    // Tier
    // ---------------------------------------------------------------------

    @Override
    public int getTierId() {
        return tierId;
    }

    @Override
    public void setTierId(int tierId) {
        this.tierId = DwarvenReputationTier.fromId(tierId).id();
    }

    // ---------------------------------------------------------------------
    // Endorsements
    // ---------------------------------------------------------------------

    /**
     * Immutable snapshot. Never exposes the backing set.
     */
    @Override
    public Set<ResourceLocation> getEndorsements() {
        return Set.copyOf(this.endorsements);
    }

    /**
     * Adds a single endorsement getId.
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

    // ---------------------------------------------------------------------
    // Threshold helpers
    // ---------------------------------------------------------------------

    public static int getThresholdCount() {
        return ENDORSEMENT_THRESHOLDS.length;
    }

    public static int getThresholdForTier(int tierId) {
        return (tierId >= 0 && tierId < ENDORSEMENT_THRESHOLDS.length)
                ? ENDORSEMENT_THRESHOLDS[tierId]
                : Integer.MAX_VALUE;
    }

    public static boolean canAdvance(int currentTierId, int endorsementCount) {
        return currentTierId < ENDORSEMENT_THRESHOLDS.length
                && endorsementCount >= getThresholdForTier(currentTierId);
    }

    // ---------------------------------------------------------------------
    // NBT
    // ---------------------------------------------------------------------

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_TIER, this.tierId);

        ListTag endorsementList = new ListTag();
        for (ResourceLocation profId : endorsements) {
            endorsementList.add(StringTag.valueOf(profId.toString()));
        }
        tag.put(NBT_ENDORSEMENTS, endorsementList);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        this.setTierId(tag.getInt(NBT_TIER));

        Set<ResourceLocation> parsed = new HashSet<>();
        ListTag endorsementList = tag.getList(NBT_ENDORSEMENTS, 8);
        for (int i = 0; i < endorsementList.size(); i++) {
            String idString = endorsementList.getString(i);
            ResourceLocation profId = ResourceLocation.tryParse(idString);
            if (profId == null) {
                JolCraftLogs.debug(JolCraftLogTags.ATTACHMENT,
                        "Failed to parse endorsement profession getId: '{}'",
                        idString
                );
                continue;
            }
            if (DwarfProfession.byId(profId.getPath()) == DwarfProfession.NONE) {
                JolCraftLogs.debug(JolCraftLogTags.ATTACHMENT,
                        "Unknown endorsement profession getId: '{}'",
                        idString
                );
                continue;
            }
            parsed.add(profId);
        }

        // Controlled setter keeps one rule for normalization/change detection.
        this.setEndorsements(parsed);
    }

    // ---------------------------------------------------------------------
    // Codec
    // ---------------------------------------------------------------------

    public static final Codec<DwarvenReputationImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf(NBT_TIER).forGetter(rep -> rep.tierId),
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
    ).apply(instance, (tierId, endorsementSet) -> {
        DwarvenReputationImpl impl = new DwarvenReputationImpl();
        impl.setTierId(tierId);
        impl.setEndorsements(endorsementSet);
        return impl;
    }));
}