package net.sievert.jolcraft.world.player.attachment.custom.lore;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.world.player.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftAttachmentHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class DwarfLoreAttachmentHelper extends JolCraftAttachmentHelper<DwarfLoreAttachment> {

    private static final DwarfLoreAttachmentHelper INSTANCE =
            new DwarfLoreAttachmentHelper();

    private DwarfLoreAttachmentHelper() {}

    @Override
    protected @NotNull AttachmentType<DwarfLoreAttachment> type() {
        return JolCraftAttachments.DWARF_LORE.get();
    }

    public static DwarfLoreAttachment get(Player player) {
        return INSTANCE.read(player);
    }

    public static void set(Player player, DwarfLoreAttachment value) {
        INSTANCE.write(player, value);
    }

    public static boolean hasUnlock(Player player, DwarfLoreKey key) {
        if (player == null || key == null) return false;
        if (player.isCreative()) return true;
        return hasUnlockBypassCreative(player, key);
    }

    public static boolean hasUnlockBypassCreative(Player player, DwarfLoreKey key) {
        return player != null && key != null && get(player).hasUnlock(key);
    }

    public static Set<DwarfLoreKey> getAllUnlocks(Player player) {
        return player == null ? Set.of() : get(player).getUnlocks();
    }

    public static void addUnlock(Player player, DwarfLoreKey key) {
        if (player == null || key == null) return;

        DwarfLoreAttachment current = get(player);
        DwarfLoreAttachment updated = current.withUnlock(key);

        if (updated != current) {
            set(player, updated);
        }
    }
}