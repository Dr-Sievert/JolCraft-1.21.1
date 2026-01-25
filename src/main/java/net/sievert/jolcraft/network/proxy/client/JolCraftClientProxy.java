package net.sievert.jolcraft.network.proxy.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.language.AncientDwarvenLanguageImpl;
import net.sievert.jolcraft.data.custom.attachment.language.DwarvenLanguageImpl;
import net.sievert.jolcraft.data.custom.attachment.lore.LoreUnlockImpl;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.network.client.data.ClientAncientLanguageData;
import net.sievert.jolcraft.network.client.data.ClientLanguageData;
import net.sievert.jolcraft.network.client.data.ClientReputationData;
import net.sievert.jolcraft.network.client.data.ClientTomeUnlocksData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
public final class JolCraftClientProxy implements JolCraftClientAccess {

    // ----------------------------
    // Attachments (client-cached views)
    // ----------------------------

    @Override
    public <T> T getAttachment(AttachmentType<T> type, Player player) {

        if (type == JolCraftAttachments.DWARVEN_LANGUAGE.get()) {
            DwarvenLanguageImpl lang = new DwarvenLanguageImpl();
            lang.setKnowsLanguage(ClientLanguageData.knowsLanguage());
            return (T) lang;
        }

        if (type == JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get()) {
            AncientDwarvenLanguageImpl lang = new AncientDwarvenLanguageImpl();
            lang.setKnowsLanguage(ClientAncientLanguageData.knowsLanguage());
            return (T) lang;
        }

        if (type == JolCraftAttachments.DWARVEN_REP.get()) {
            DwarvenReputationImpl rep = new DwarvenReputationImpl();
            rep.setTier(ClientReputationData.getTier());
            rep.getEndorsements().clear();
            rep.getEndorsements().addAll(ClientReputationData.getAllEndorsements());
            return (T) rep;
        }

        if (type == JolCraftAttachments.DWARF_LORE_UNLOCK.get()) {
            Set<DwarfLoreKey> keys = new HashSet<>();
            for (String key : ClientTomeUnlocksData.getAllUnlocks()) {
                DwarfLoreKey resolved = LoreHelper.byNameIgnoreCase(DwarfLoreKey.class, key);
                if (resolved != null) {
                    keys.add(resolved);
                }
            }
            return (T) new LoreUnlockImpl<>(DwarfLoreKey.class, keys);
        }

        return player.getData(type);
    }

    // ----------------------------
    // Tooltip / client input
    // ----------------------------

    @Override
    public boolean isAltDown() {
        return Screen.hasAltDown();
    }

    @Override
    public @Nullable Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public @NotNull Component getAltKeyComponent() {
        return InputConstants.getKey(InputConstants.KEY_LALT, -1)
                .getDisplayName().copy().withStyle(ChatFormatting.BLUE);
    }
}
