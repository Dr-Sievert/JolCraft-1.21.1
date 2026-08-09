package net.sievert.jolcraft.world.entity.attachment.player.custom.reputation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.entity.attachment.base.JolCraftAttachmentHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public final class DwarvenReputationAttachmentHelper extends JolCraftAttachmentHelper<DwarvenReputationAttachment> {

    private static final DwarvenReputationAttachmentHelper INSTANCE = new DwarvenReputationAttachmentHelper();

    private DwarvenReputationAttachmentHelper() {}

    @Override
    protected @NotNull AttachmentType<DwarvenReputationAttachment> type() {
        return JolCraftAttachments.DWARVEN_REPUTATION.get();
    }

    public static DwarvenReputationAttachment get(Player player) {
        return INSTANCE.read(player);
    }

    public static void set(Player player, DwarvenReputationAttachment value) {
        INSTANCE.write(player, value);
    }

    public static void remove(Player player) {
        INSTANCE.clear(player);
    }

    public static boolean hasTier(Player player, int minTier) {
        if (player == null) {
            return false;
        }
        if (player.isCreative()) {
            return true;
        }
        return hasTierBypassCreative(player, minTier);
    }

    public static boolean hasTierBypassCreative(Player player, int minTier) {
        return player != null && get(player).getTierId() >= minTier;
    }

    public static boolean hasEndorsement(Player player, DwarfProfession profession) {
        if (player == null) {
            return false;
        }
        if (player.isCreative()) {
            return true;
        }
        return hasEndorsementBypassCreative(player, profession);
    }

    public static boolean hasEndorsementBypassCreative(Player player, DwarfProfession profession) {
        if (player == null || profession == null || profession == DwarfProfession.NONE) {
            return false;
        }
        return get(player).hasEndorsement(JolCraft.location(profession.getId()));
    }

    public static int getEndorsementCount(Player player) {
        return player == null ? 0 : get(player).getEndorsementCount();
    }

    public static Set<ResourceLocation> getEndorsements(Player player) {
        return player == null ? Set.of() : get(player).getEndorsements();
    }

    public static Set<DwarfProfession> getAllEndorsements(Player player) {
        return player == null ? Set.of() : toProfessions(get(player));
    }

    public static boolean addEndorsement(Player player, ResourceLocation professionId) {
        if (player == null || professionId == null) {
            return false;
        }
        if (player.level().isClientSide()) {
            return false;
        }

        DwarvenReputationAttachment current = get(player);
        DwarvenReputationAttachment updated = current.withAddedEndorsement(professionId);

        if (updated == current) {
            return false;
        }

        set(player, updated);
        return true;
    }

    public static boolean addEndorsement(Player player, DwarfProfession profession) {
        if (player == null || profession == null || profession == DwarfProfession.NONE) {
            return false;
        }
        return addEndorsement(player, JolCraft.location(profession.getId()));
    }

    public static int getTier(Player player) {
        return player == null ? DwarvenReputationAttachment.Tier.STRANGER.getId() : get(player).getTierId();
    }

    public static DwarvenReputationAttachment.Tier getTierEnum(Player player) {
        return player == null ? DwarvenReputationAttachment.Tier.STRANGER : get(player).getTier();
    }

    public static void setReputationTier(Player player, int tier) {
        if (player == null) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        DwarvenReputationAttachment current = get(player);
        DwarvenReputationAttachment updated = current.withTierId(tier);

        if (updated != current) {
            set(player, updated);
        }
    }

    public static void setTier(Player player, DwarvenReputationAttachment.Tier tier) {
        if (player == null) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        DwarvenReputationAttachment current = get(player);
        DwarvenReputationAttachment updated = current.withTier(tier);

        if (updated != current) {
            set(player, updated);
        }
    }

    public static boolean canAdvance(Player player) {
        if (player == null) {
            return false;
        }

        DwarvenReputationAttachment reputation = get(player);
        return DwarvenReputationAttachment.canAdvance(
                reputation.getTierId(),
                reputation.getEndorsementCount()
        );
    }

    public static String getTierLangKey(int tier) {
        return DwarvenReputationAttachment.Tier.fromId(tier).langKey();
    }

    private static Set<DwarfProfession> toProfessions(DwarvenReputationAttachment reputation) {
        return reputation.getEndorsements().stream()
                .map(id -> DwarfProfession.byId(id.getPath()))
                .filter(prof -> prof != DwarfProfession.NONE)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static int getMaxTier() {
        return DwarvenReputationAttachment.Tier.values().length - 1;
    }

    public static int getThresholdForTier(int tier) {
        return DwarvenReputationAttachment.getThresholdForTier(tier);
    }

    public static int getThresholdCount() {
        return DwarvenReputationAttachment.getThresholdCount();
    }
}