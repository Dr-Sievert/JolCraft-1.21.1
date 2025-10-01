package net.sievert.jolcraft.data.custom.attachment.reputation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DwarvenReputationImpl implements DwarvenReputation {

    private int tier = 0;
    private final Set<DwarfProfession> endorsements = new HashSet<>();

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
    public Set<DwarfProfession> getEndorsements() {
        return endorsements;
    }

    @Override
    public void addEndorsement(DwarfProfession profession) {
        endorsements.add(profession);
    }

    @Override
    public boolean hasEndorsement(DwarfProfession profession) {
        return endorsements.contains(profession);
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
        for (DwarfProfession prof : endorsements) {
            endorsementList.add(StringTag.valueOf(prof.getId()));
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
            DwarfProfession prof = DwarfProfession.byId(idString);
            if (prof != DwarfProfession.NONE) {
                endorsements.add(prof);
            } else {
                JolCraft.LOGGER.warn("Failed to parse endorsement profession: '{}'", idString);
            }
        }
    }

    public static final Codec<DwarvenReputationImpl> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tier").forGetter(rep -> rep.tier),
            Codec.STRING.listOf()
                    .xmap(
                            list -> list.stream()
                                    .map(DwarfProfession::byId)
                                    .filter(prof -> prof != DwarfProfession.NONE)
                                    .collect(Collectors.toSet()),
                            set -> set.stream()
                                    .map(DwarfProfession::getId)
                                    .collect(Collectors.toList())
                    )
                    .fieldOf("endorsements")
                    .forGetter(rep -> new HashSet<>(rep.endorsements))
    ).apply(instance, (tier, endorsementSet) -> {
        DwarvenReputationImpl impl = new DwarvenReputationImpl();
        impl.tier = tier;
        impl.endorsements.addAll(endorsementSet);
        return impl;
    }));
}
