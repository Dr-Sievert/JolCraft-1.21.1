package net.sievert.jolcraft.world.player.attachment.custom.language;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.world.player.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftAttachmentHelper;
import net.sievert.jolcraft.data.id.font.JolCraftFontIds;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import org.jetbrains.annotations.NotNull;

public final class LanguageAttachmentHelper extends JolCraftAttachmentHelper<LanguageAttachment> {

    private static final LanguageAttachmentHelper INSTANCE = new LanguageAttachmentHelper();

    private LanguageAttachmentHelper() {}

    @Override
    protected @NotNull AttachmentType<LanguageAttachment> type() {
        return JolCraftAttachments.LANGUAGE.get();
    }

    public static LanguageAttachment get(Player player) {
        return INSTANCE.read(player);
    }

    public static void set(Player player, LanguageAttachment value) {
        INSTANCE.write(player, value);
    }

    public static void remove(Player player) {
        INSTANCE.clear(player);
    }

    public static boolean knowsLanguage(Player player, LanguageType type) {
        if (player == null) {
            return false;
        }
        if (player.isCreative()) {
            return true;
        }
        return knowsLanguageBypassCreative(player, type);
    }

    public static boolean knowsLanguageBypassCreative(Player player, LanguageType type) {
        if (player == null) {
            return false;
        }

        if (type == LanguageType.ANCIENT_DWARVEN && player.hasEffect(JolCraftEffects.ANCIENT_MEMORY)) {
            return true;
        }

        return get(player).hasLanguage(type);
    }

    public static void setKnowsLanguage(Player player, LanguageType type, boolean value) {
        if (player == null) {
            return;
        }

        LanguageAttachment current = get(player);
        LanguageAttachment updated = current.withLanguage(type, value);

        if (updated != current) {
            set(player, updated);
        }
    }

    public static void grantLanguage(Player player, LanguageType type) {
        setKnowsLanguage(player, type, true);
    }

    public static boolean knowsDwarvish(Player player) {
        return knowsLanguage(player, LanguageType.DWARVEN);
    }

    public static boolean knowsDwarvishBypassCreative(Player player) {
        return knowsLanguageBypassCreative(player, LanguageType.DWARVEN);
    }

    public static boolean knowsAncientDwarvish(Player player) {
        return knowsLanguage(player, LanguageType.ANCIENT_DWARVEN);
    }

    public static boolean knowsAncientDwarvishBypassCreative(Player player) {
        return knowsLanguageBypassCreative(player, LanguageType.ANCIENT_DWARVEN);
    }

    public static void setKnowsDwarvish(Player player, boolean value) {
        setKnowsLanguage(player, LanguageType.DWARVEN, value);
    }

    public static void setKnowsAncientDwarvish(Player player, boolean value) {
        setKnowsLanguage(player, LanguageType.ANCIENT_DWARVEN, value);
    }

    public static void grantDwarvish(Player player) {
        grantLanguage(player, LanguageType.DWARVEN);
    }

    public static void grantAncientDwarvish(Player player) {
        grantLanguage(player, LanguageType.ANCIENT_DWARVEN);
    }

    /**
     * Returns readable text if the player has Ancient Memory (effect or permanent),
     * otherwise applies SGA rune font.
     */
    public static Component getAncientText(Player player, Component readable) {
        if (knowsAncientDwarvish(player)) {
            return readable;
        }
        return readable.copy().withStyle(style -> style.withFont(JolCraftFontIds.SGA));
    }
}