package net.sievert.jolcraft.network.proxy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguage;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientDwarvenLanguage;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfLoreUnlock;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.network.data.client.ClientDeliriumData;
import net.sievert.jolcraft.network.packet.s2c.*;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
@OnlyIn(Dist.CLIENT)
public final class JolCraftClientProxy implements JolCraftClientAccess {

    @Override
    public <T> T getAttachment(AttachmentType<T> type, Player player) {
        if (player == null) return null;
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
            dwarfMenu.setCanRestock(packet.canRestock());
            return;
        }

        JolCraftLogs.debug(
                JolCraftLogTags.NETWORK,
                "Ignored dwarf offers packet (packetContainerId={} currentContainerId={} menu={})",
                packet.containerId(),
                menu.containerId,
                menu.getClass().getSimpleName()
        );
    }


    @Override
    public void apply(ClientboundDwarfTomeUnlocksPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarfLoreUnlock unlock = player.getData(JolCraftAttachments.DWARF_TOME_UNLOCKS.get());

        AtomicInteger invalid = new AtomicInteger();

        Set<DwarfLoreKey> snapshot =
                (packet.unlocks() == null) ? Set.of() :
                        packet.unlocks().stream()
                                .filter(s -> s != null && !s.isBlank())
                                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                                .map(s -> {
                                    try {
                                        return DwarfLoreKey.valueOf(s);
                                    } catch (IllegalArgumentException ignored) {
                                        return null;
                                    }
                                })
                                .filter(k -> {
                                    if (k == null) invalid.getAndIncrement();
                                    return k != null;
                                })
                                .collect(Collectors.toUnmodifiableSet());

        if (invalid.get() > 0) {
            JolCraftLogs.warn(
                    JolCraftLogTags.NETWORK,
                    "Lore unlock sync had {} invalid key(s) (ignored)",
                    invalid
            );
        }

        unlock.setUnlocks(snapshot);
    }


    @Override
    public void apply(ClientboundDeliriumCursePacket packet) {
        ClientDeliriumData.start(packet.durationTicks());
    }

    @Override
    public void apply(ClientboundDwarvenLanguagePacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarvenLanguage lang = player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get());
        lang.setHasLanguage(packet.knowsLanguage());
    }

    @Override
    public void apply(ClientboundAncientDwarvenLanguagePacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        AncientDwarvenLanguage lang = player.getData(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get());
        lang.setHasLanguage(packet.knowsLanguage());
    }

    @Override
    public void apply(ClientboundDwarvenReputationPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarvenReputationImpl rep = player.getData(JolCraftAttachments.DWARVEN_REPUTATION.get());
        rep.setTierId(packet.tier());
    }

    @Override
    public void apply(ClientboundDwarvenEndorsementsPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarvenReputationImpl rep = player.getData(JolCraftAttachments.DWARVEN_REPUTATION.get());

        Set<ResourceLocation> ids = EnumSet.copyOf(packet.endorsements()).stream()
                .filter(p -> p != null && p != DwarfProfession.NONE)
                .map(p -> JolCraft.location(p.getId()))
                .collect(Collectors.toUnmodifiableSet());

        rep.setEndorsements(ids);
    }
}