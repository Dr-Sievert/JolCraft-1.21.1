package net.sievert.jolcraft.world.player.attachment.custom.hearth;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.world.player.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftAttachmentHelper;
import net.sievert.jolcraft.event.game.world.JolCraftTimeHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HearthAttachmentHelper extends JolCraftAttachmentHelper<HearthAttachment> {

    private static final HearthAttachmentHelper INSTANCE = new HearthAttachmentHelper();

    private HearthAttachmentHelper() {}

    @Override
    protected @NotNull AttachmentType<HearthAttachment> type() {
        return JolCraftAttachments.HEARTH.get();
    }

    public static HearthAttachment get(ServerPlayer player) {
        return INSTANCE.read(player);
    }

    public static void set(ServerPlayer player, HearthAttachment value) {
        INSTANCE.write(player, value);
    }

    public static void remove(ServerPlayer player) {
        INSTANCE.clear(player);
    }

    public static long lastLitDay(ServerPlayer player) {
        return get(player).lastLitDay();
    }

    public static boolean hasLitToday(ServerPlayer player) {
        if (player == null) return false;
        long day = JolCraftTimeHelper.day(player);
        return get(player).lastLitDay() == day;
    }

    public static void setLastLitToday(ServerPlayer player) {
        if (player == null) return;

        long day = JolCraftTimeHelper.day(player);
        set(player, get(player).withLastLitDay(day));
    }

    public static void clearLastLitDay(ServerPlayer player) {
        if (player == null) return;
        set(player, get(player).clearLastLitDay());
    }

    public static @Nullable BlockPos activeHearthPos(ServerPlayer player) {
        return player == null ? null : get(player).activeHearthPos();
    }

    public static boolean hasActiveHearth(ServerPlayer player) {
        return player != null && get(player).hasActiveHearth();
    }

    public static boolean isActiveHearth(ServerPlayer player, BlockPos pos) {
        return player != null && get(player).isActiveHearth(pos);
    }

    public static void setActiveHearthPos(ServerPlayer player, BlockPos pos) {
        if (player == null || pos == null) return;
        set(player, get(player).withActiveHearthPos(pos));
    }

    public static void clearActiveHearthPos(ServerPlayer player) {
        if (player == null) return;
        set(player, get(player).clearActiveHearthPos());
    }
}