package net.sievert.jolcraft.data.util.attachment;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.unlock.TomeUnlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundTomeUnlocksPacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import java.util.Set;

public class TomeUnlockHelper {

    // --- Known Unlock IDs ---
    public static final String MITHRIL_FORGING = "mithril_forge_technique";
    public static final String BREW_MULTIPLE_HOPS = "forgotten_brew_formulas";
    public static final String CUTTING_GEMS = "ancient_gemcraft";
    public static final String COIN_PRESSING = "coin_press_manual";
    public static final String ALCHEMY = "alchemy_recipes";

    /**
     * Checks if player is creative OR has the unlock (side-safe).
     */
    public static boolean hasUnlock(Player player, String unlockId) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        TomeUnlock unlock = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.TOME_UNLOCK.get(), player);
        return unlock != null && unlock.hasUnlock(unlockId);
    }

    /**
     * Checks if player has the unlock (bypasses creative, side-safe).
     */
    public static boolean hasUnlockBypassCreative(Player player, String unlockId) {
        if (player == null) return false;
        TomeUnlock unlock = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.TOME_UNLOCK.get(), player);
        return unlock != null && unlock.hasUnlock(unlockId);
    }

    /**
     * Grants an unlock to a player.
     * Only call this on the server. Also syncs the client view.
     */
    public static void grantUnlock(Player player, String unlockId) {
        if (player == null) return;
        TomeUnlock unlock = player.getData(JolCraftAttachments.TOME_UNLOCK.get());
        unlock.addUnlock(unlockId);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundTomeUnlocksPacket(unlock.getUnlocks()));
        }
    }

    /**
     * Gets all unlocks for a player (side-safe).
     */
    public static Set<String> getAllUnlocks(Player player) {
        if (player == null) return Set.of();
        TomeUnlock unlock = JolCraftProxy.get(player.level()).getAttachment(JolCraftAttachments.TOME_UNLOCK.get(), player);
        return unlock != null ? unlock.getUnlocks() : Set.of();
    }

    // --- CLIENT ONLY: For local player convenience ---

    @OnlyIn(Dist.CLIENT)
    public static boolean hasUnlockClient(String unlockId) {
        Player player = Minecraft.getInstance().player;
        return hasUnlock(player, unlockId);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasUnlockClientBypassCreative(String unlockId) {
        Player player = Minecraft.getInstance().player;
        return hasUnlockBypassCreative(player, unlockId);
    }

    @OnlyIn(Dist.CLIENT)
    public static Set<String> getAllUnlocksClient() {
        Player player = Minecraft.getInstance().player;
        return getAllUnlocks(player);
    }
}
