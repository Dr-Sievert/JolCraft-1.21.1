package net.sievert.jolcraft.network.proxy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientDwarvenLanguageImpl;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageImpl;
import net.sievert.jolcraft.data.attachment.custom.lore.LoreUnlockImpl;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.custom.lore.util.LoreHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.network.data.*;
import net.sievert.jolcraft.network.packet.s2c.*;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused"})
@OnlyIn(Dist.CLIENT)
public final class JolCraftClientProxy implements JolCraftClientAccess {

    private final DwarvenLanguageImpl cachedLang = new DwarvenLanguageImpl();
    private int lastLangRevision = -1;

    private final AncientDwarvenLanguageImpl cachedAncientLang = new AncientDwarvenLanguageImpl();
    private int lastAncientLangRevision = -1;

    private final DwarvenReputationImpl cachedRep = new DwarvenReputationImpl();
    private int lastReputationRevision = -1;

    private LoreUnlockImpl<DwarfLoreKey> cachedLoreUnlock =
            new LoreUnlockImpl<>(DwarfLoreKey.class, Set.of());
    private int lastLoreRevision = -1;

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

    // ----------------------------
    // Clientbound packet application
    // ----------------------------

    @Override
    public void apply(ClientboundParticlePacket packet) {
        var mc = Minecraft.getInstance();
        if (mc.level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel) {
            clientLevel.addParticle(
                    packet.particle(),
                    packet.overrideLimiter(),
                    packet.alwaysShow(),
                    packet.x(), packet.y(), packet.z(),
                    packet.vx(), packet.vy(), packet.vz()
            );
        }
    }

    @Override
    public void apply(ClientboundPlaySoundPacket packet) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        var optHolder = BuiltInRegistries.SOUND_EVENT.get(packet.soundId());
        if (optHolder.isEmpty()) return;

        var sound = optHolder.get().value();
        player.level().playLocalSound(
                packet.x(), packet.y(), packet.z(),
                sound,
                packet.source(),
                packet.volume(),
                packet.pitch(),
                false
        );
    }

    @Override
    public void apply(ClientboundDwarfMerchantOffersPacket packet) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        var menu = mc.player.containerMenu;
        if (packet.containerId() == menu.containerId && menu instanceof DwarfMerchantMenu dwarfMenu) {
            dwarfMenu.setOffers(packet.offers());
            dwarfMenu.setXp(packet.dwarfXp());
            dwarfMenu.setMerchantLevel(packet.dwarfLevel());
            dwarfMenu.setShowProgressBar(packet.showProgress());
            dwarfMenu.setshowLevel(packet.showLevel());
            dwarfMenu.setCanRestock(packet.canRestock());
        }
    }

    @Override
    public void apply(ClientboundLoreUnlocksPacket packet) {
        ClientTomeUnlocksData.setUnlocks(List.copyOf(packet.unlocks()));
    }

    @Override
    public void apply(ClientboundDeliriumPacket packet) {
        ClientDeliriumData.setMuffleTicks(packet.durationTicks());
    }

    @Override
    public void apply(ClientboundLanguagePacket packet) {
        ClientLanguageData.setKnows(packet.knowsLanguage());
    }

    @Override
    public void apply(ClientboundAncientLanguagePacket packet) {
        ClientAncientLanguageData.setKnows(packet.knowsLanguage());
    }

    @Override
    public void apply(ClientboundReputationPacket packet) {
        ClientReputationData.setTier(packet.tier());
    }

    @Override
    public void apply(ClientboundEndorsementsPacket packet) {
        ClientReputationData.setEndorsements(EnumSet.copyOf(packet.endorsements()));
    }
}