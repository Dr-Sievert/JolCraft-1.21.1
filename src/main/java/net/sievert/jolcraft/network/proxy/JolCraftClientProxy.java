package net.sievert.jolcraft.network.proxy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.network.data.client.ClientDeliriumData;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumCursePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public final class JolCraftClientProxy implements JolCraftClientAccess {

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
    public void apply(ClientboundDeliriumCursePacket packet) {
        ClientDeliriumData.start(packet.durationTicks());
    }
}