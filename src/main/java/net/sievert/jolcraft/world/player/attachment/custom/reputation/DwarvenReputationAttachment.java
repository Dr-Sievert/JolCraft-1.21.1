package net.sievert.jolcraft.world.player.attachment.custom.reputation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftSyncedAttachment;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DwarvenReputationAttachment extends JolCraftSyncedAttachment<DwarvenReputationAttachment> {

    public enum Tier implements JolCraftEnumHelper.IntId {
        STRANGER(0, JolCraftDictionary.STRANGER),
        KNOWN_FACE(1, JolCraftStrings.underscored(JolCraftDictionary.KNOWN, JolCraftDictionary.FACE)),
        TRUSTED(2, JolCraftDictionary.TRUSTED),
        RESPECTED(3, JolCraftDictionary.RESPECTED),
        BLOOD_KIN(4, JolCraftStrings.underscored(JolCraftDictionary.BLOOD, JolCraftDictionary.KIN));

        private final int id;
        private final String serializedName;

        Tier(int id, String serializedName) {
            this.id = id;
            this.serializedName = serializedName;
        }

        @Override
        public int getId() {
            return id;
        }

        public String getSerializedName() {
            return serializedName;
        }

        public String langKey() {
            return AbstractLanguageKeys.mod(
                    JolCraftStrings.dotted(
                            JolCraftStrings.underscored(
                                    JolCraftAttachmentIds.DWARVEN_REPUTATION,
                                    JolCraftDictionary.TIER
                            ),
                            String.valueOf(id)
                    )
            );
        }

        public static Tier fromId(int id) {
            return JolCraftEnumHelper.byIntIdExact(Tier.class, id, STRANGER);
        }
    }

    public static final int[] ENDORSEMENT_THRESHOLDS = {1, 3, 6, 10};

    private static final String TAG_ENDORSEMENTS = JolCraftStrings.plural(JolCraftDictionary.ENDORSEMENT);

    public static final Codec<DwarvenReputationAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT
                            .fieldOf(JolCraftDictionary.TIER)
                            .forGetter(DwarvenReputationAttachment::getTierId),
                    Codec.STRING.listOf()
                            .fieldOf(TAG_ENDORSEMENTS)
                            .forGetter(DwarvenReputationAttachment::endorsementStrings)
            ).apply(instance, DwarvenReputationAttachment::fromSerialized));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, DwarvenReputationAttachment> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    DwarvenReputationAttachment::getTierId,
                    ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                    DwarvenReputationAttachment::endorsementStrings,
                    DwarvenReputationAttachment::fromSerialized
            );

    private final int tierId;
    private final Set<ResourceLocation> endorsements;

    public DwarvenReputationAttachment() {
        this(Tier.STRANGER.getId(), Set.of());
    }

    public DwarvenReputationAttachment(int tierId, Set<ResourceLocation> endorsements) {
        this.tierId = Tier.fromId(tierId).getId();
        this.endorsements = normalizeEndorsements(endorsements);
    }

    public int getTierId() {
        return tierId;
    }

    public Tier getTier() {
        return Tier.fromId(tierId);
    }

    public Set<ResourceLocation> getEndorsements() {
        return endorsements;
    }

    public boolean hasEndorsement(ResourceLocation professionId) {
        return professionId != null && endorsements.contains(professionId);
    }

    public int getEndorsementCount() {
        return endorsements.size();
    }

    public DwarvenReputationAttachment withTierId(int tierId) {
        int normalizedTierId = Tier.fromId(tierId).getId();
        return this.tierId == normalizedTierId
                ? this
                : new DwarvenReputationAttachment(normalizedTierId, endorsements);
    }

    public DwarvenReputationAttachment withTier(Tier tier) {
        return withTierId((tier == null ? Tier.STRANGER : tier).getId());
    }

    public DwarvenReputationAttachment withAddedEndorsement(ResourceLocation id) {
        if (!isValidProfessionId(id) || endorsements.contains(id)) {
            return this;
        }

        Set<ResourceLocation> updated = new HashSet<>(endorsements);
        updated.add(id);
        return new DwarvenReputationAttachment(tierId, updated);
    }

    public DwarvenReputationAttachment withEndorsements(Set<ResourceLocation> ids) {
        Set<ResourceLocation> normalized = normalizeEndorsements(ids);
        return endorsements.equals(normalized)
                ? this
                : new DwarvenReputationAttachment(tierId, normalized);
    }

    private List<String> endorsementStrings() {
        return endorsements.stream()
                .map(ResourceLocation::toString)
                .toList();
    }

    private static DwarvenReputationAttachment fromSerialized(int tierId, List<String> endorsementStrings) {
        Set<ResourceLocation> parsed = new HashSet<>();

        for (String idString : endorsementStrings) {
            ResourceLocation profId = ResourceLocation.tryParse(idString);
            if (profId == null) {
                JolCraftLogs.debug(
                        JolCraftLogTags.ATTACHMENT,
                        "Failed to parse endorsement profession: '{}'",
                        idString
                );
                continue;
            }

            if (!isValidProfessionId(profId)) {
                JolCraftLogs.debug(
                        JolCraftLogTags.ATTACHMENT,
                        "Unknown endorsement profession: '{}'",
                        idString
                );
                continue;
            }

            parsed.add(profId);
        }

        return new DwarvenReputationAttachment(tierId, parsed);
    }

    private static boolean isValidProfessionId(ResourceLocation id) {
        return id != null && DwarfProfession.byId(id.getPath()) != DwarfProfession.NONE;
    }

    private static Set<ResourceLocation> normalizeEndorsements(Set<ResourceLocation> ids) {
        Set<ResourceLocation> normalized = new HashSet<>();

        if (ids != null && !ids.isEmpty()) {
            for (ResourceLocation id : ids) {
                if (isValidProfessionId(id)) {
                    normalized.add(id);
                }
            }
        }

        return Set.copyOf(normalized);
    }

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

    @Override
    public Codec<DwarvenReputationAttachment> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, DwarvenReputationAttachment> streamCodec() {
        return STREAM_CODEC;
    }
}