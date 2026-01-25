package net.sievert.jolcraft.network.proxy.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.language.AncientDwarvenLanguageImpl;
import net.sievert.jolcraft.data.custom.attachment.language.DwarvenLanguageImpl;
import net.sievert.jolcraft.data.custom.attachment.lore.LoreUnlockImpl;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.network.client.data.ClientAncientLanguageData;
import net.sievert.jolcraft.network.client.data.ClientLanguageData;
import net.sievert.jolcraft.network.client.data.ClientReputationData;
import net.sievert.jolcraft.network.client.data.ClientTomeUnlocksData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
@OnlyIn(Dist.CLIENT)
public final class JolCraftClientProxy implements JolCraftClientAccess {

    // ----------------------------
    // Cached "views" (rebuilt only when client data revisions change)
    // ----------------------------

    private final DwarvenLanguageImpl cachedLang = new DwarvenLanguageImpl();
    private int lastLangRevision = -1;

    private final AncientDwarvenLanguageImpl cachedAncientLang = new AncientDwarvenLanguageImpl();
    private int lastAncientLangRevision = -1;

    private final DwarvenReputationImpl cachedRep = new DwarvenReputationImpl();
    private int lastReputationRevision = -1;

    private LoreUnlockImpl<DwarfLoreKey> cachedLoreUnlock =
            new LoreUnlockImpl<>(DwarfLoreKey.class, Set.of());
    private int lastLoreRevision = -1;

    // ----------------------------
    // Attachments (client-cached views)
    // ----------------------------

    @Override
    public <T> T getAttachment(AttachmentType<T> type, Player player) {

        if (type == JolCraftAttachments.DWARVEN_LANGUAGE.get()) {
            int rev = ClientLanguageData.revision();
            if (rev != lastLangRevision) {
                lastLangRevision = rev;
                cachedLang.setKnowsLanguage(ClientLanguageData.knowsLanguage());
            }
            return (T) cachedLang;
        }

        if (type == JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get()) {
            int rev = ClientAncientLanguageData.revision();
            if (rev != lastAncientLangRevision) {
                lastAncientLangRevision = rev;
                cachedAncientLang.setKnowsLanguage(ClientAncientLanguageData.knowsLanguage());
            }
            return (T) cachedAncientLang;
        }

        if (type == JolCraftAttachments.DWARVEN_REP.get()) {
            int rev = ClientReputationData.revision();
            if (rev != lastReputationRevision) {
                lastReputationRevision = rev;

                cachedRep.setTier(ClientReputationData.getTier());
                cachedRep.getEndorsements().clear();

                EnumSet<DwarfProfession> endorsements = ClientReputationData.getAllEndorsements();
                if (endorsements != null && !endorsements.isEmpty()) {
                    cachedRep.getEndorsements().addAll(endorsements);
                }
            }
            return (T) cachedRep;
        }

        if (type == JolCraftAttachments.DWARF_LORE_UNLOCK.get()) {
            int rev = ClientTomeUnlocksData.revision();
            if (rev != lastLoreRevision) {
                lastLoreRevision = rev;

                Set<DwarfLoreKey> keys = new HashSet<>();
                List<String> unlocks = ClientTomeUnlocksData.getAllUnlocks();
                if (unlocks != null && !unlocks.isEmpty()) {
                    for (String key : unlocks) {
                        DwarfLoreKey resolved = LoreHelper.byNameIgnoreCase(DwarfLoreKey.class, key);
                        if (resolved != null) keys.add(resolved);
                    }
                }

                cachedLoreUnlock = new LoreUnlockImpl<>(DwarfLoreKey.class, keys);
            }
            return (T) cachedLoreUnlock;
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