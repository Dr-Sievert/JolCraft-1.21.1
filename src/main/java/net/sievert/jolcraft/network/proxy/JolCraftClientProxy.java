package net.sievert.jolcraft.network.proxy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.network.data.ClientDeliriumData;
import net.sievert.jolcraft.network.packet.s2c.ClientboundAncientLanguagePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundEndorsementsPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundLanguagePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundLoreUnlocksPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundParticlePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundPlaySoundPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundReputationPacket;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
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
    public void apply(ClientboundParticlePacket packet) {
        var mc = Minecraft.getInstance();
        if (mc.level instanceof ClientLevel clientLevel) {
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
        if (mc.level == null) return;

        var optHolder = BuiltInRegistries.SOUND_EVENT.get(packet.soundId());
        if (optHolder.isEmpty()) return;

        var sound = optHolder.get().value();
        mc.level.playLocalSound(
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
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarfLoreUnlock unlock = player.getData(JolCraftAttachments.DWARF_LORE_UNLOCK.get());

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
                                .filter(java.util.Objects::nonNull)
                                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        unlock.setUnlocks(snapshot);
    }

    @Override
    public void apply(ClientboundDeliriumPacket packet) {
        ClientDeliriumData.setMuffleTicks(packet.durationTicks());
    }

    @Override
    public void apply(ClientboundLanguagePacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarvenLanguage lang = player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get());
        lang.setHasLanguage(packet.knowsLanguage());
    }

    @Override
    public void apply(ClientboundAncientLanguagePacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        AncientDwarvenLanguage lang = player.getData(JolCraftAttachments.ANCIENT_DWARVEN_LANGUAGE.get());
        lang.setHasLanguage(packet.knowsLanguage());
    }

    @Override
    public void apply(ClientboundReputationPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarvenReputationImpl rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());
        rep.setTier(packet.tier());
    }

    @Override
    public void apply(ClientboundEndorsementsPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        DwarvenReputationImpl rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());

        Set<ResourceLocation> ids = EnumSet.copyOf(packet.endorsements()).stream()
                .filter(p -> p != null && p != DwarfProfession.NONE)
                .map(p -> JolCraft.location(p.id))
                .collect(Collectors.toUnmodifiableSet());

        rep.setEndorsements(ids);
    }
}