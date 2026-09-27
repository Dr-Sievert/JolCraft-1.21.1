package net.sievert.jolcraft.world.entity.attachment.player.custom.hearth;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.world.entity.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.entity.attachment.base.JolCraftAttachmentHelper;
import net.sievert.jolcraft.world.util.JolCraftTimeHelper;
import net.sievert.jolcraft.world.block.custom.HearthBlock;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
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

    public static @Nullable GlobalPos activeHearthPos(ServerPlayer player) {
        if (player == null) return null;
        return get(player).activeHearthPos();
    }

    public static boolean hasActiveHearth(ServerPlayer player) {
        return player != null && get(player).hasActiveHearth();
    }

    public static boolean isActiveHearth(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos) {
        GlobalPos active = activeHearthPos(player);
        return active != null && active.equals(GlobalPos.of(dimension, pos));
    }

    public static void setActiveHearthPos(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos) {
        if (player == null || pos == null) return;
        set(player, get(player).withActiveHearthPos(GlobalPos.of(dimension, pos)));
    }

    public static void clearActiveHearthPos(ServerPlayer player) {
        if (player == null) return;
        set(player, get(player).clearActiveHearthPos());
    }

    public static void validateActiveHearth(ServerPlayer player) {
        GlobalPos active = activeHearthPos(player);
        if (active == null) return;
        ServerLevel level = player.server.getLevel(active.dimension());
        if (level == null || (level.hasChunkAt(active.pos()) && ownedHearth(player, level, active.pos()) == null)) {
            clearActiveHearthPos(player);
        }
    }

    private static @Nullable HearthBlockEntity ownedHearth(ServerPlayer player, ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof HearthBlock)
                || state.getValue(HearthBlock.HALF) != DoubleBlockHalf.LOWER) return null;
        return level.getBlockEntity(pos) instanceof HearthBlockEntity hearth
                && player.getUUID().equals(hearth.getOwner()) ? hearth : null;
    }

    public static boolean isInActiveHearthRange(ServerPlayer player) {
        GlobalPos active = activeHearthPos(player);
        if (active == null || !active.dimension().equals(player.level().dimension())
                || player.blockPosition().distSqr(active.pos()) > HearthBlockEntity.RADIUS_SQ) return false;
        ServerLevel level = player.serverLevel();
        if (!level.hasChunkAt(active.pos())) return false;
        return ownedHearth(player, level, active.pos()) != null
                && level.getBlockState(active.pos()).getValue(HearthBlock.LIT);
    }

    public static void maintainHomesteadEffect(ServerPlayer player) {
        if (player.hasEffect(JolCraftEffects.HOMESTEAD) && !isInActiveHearthRange(player)) {
            player.removeEffect(JolCraftEffects.HOMESTEAD);
        }
    }
}
