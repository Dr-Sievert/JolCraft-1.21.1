package net.sievert.jolcraft.world.sound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

public final class PlaySound {

    private PlaySound() {}

    // ---- Curse ----

    public static void curse(Player player) {
        JolCraftSoundHelper.player(player, JolCraftSounds.CURSE.get(), 0.8F, 1.0F);
    }

    // ---- Level Up ----

    public static void levelUp(Player player) {
        JolCraftSoundHelper.player(player, JolCraftSounds.LEVEL_UP.get());
    }

    // ---- Bottle Fill ----

    public static void bottleFill(Player player, Float volume, Float pitch) {
        JolCraftSoundHelper.player(player, SoundEvents.BOTTLE_FILL, volume, pitch);
    }

    // ---- Tomes ----

    public static void bookPut(Player player) {
        JolCraftSoundHelper.player(player, SoundEvents.BOOK_PUT,1.2F, 0.8F);
    }

    public static void bookPageTurn(Player player) {
        JolCraftSoundHelper.player(player, SoundEvents.BOOK_PAGE_TURN, 1.2F, 0.8F);
    }

    // ---- Dwarf Yes / No ----

    public static void dwarfYes(LivingEntity entity) {
        JolCraftSoundHelper.entity(entity, JolCraftSounds.DWARF_YES.get(), entity.level().random.nextFloat() * 0.2F + 0.8F);
    }

    public static void dwarfNo(LivingEntity entity) {
        JolCraftSoundHelper.entity(entity, JolCraftSounds.DWARF_NO.get(), entity.level().random.nextFloat() * 0.2F + 0.8F);
    }

    // ---- Villager Yes / No ----

    public static void villagerYes(LivingEntity entity) {
        JolCraftSoundHelper.entity(entity, SoundEvents.VILLAGER_YES);
    }

    public static void villagerNo(LivingEntity entity) {
        JolCraftSoundHelper.entity(entity, SoundEvents.VILLAGER_NO);
    }

    // ---- Strongbox ----

    public static void strongboxOpen(Level level, BlockPos pos) {
        JolCraftSoundHelper.block(level, pos, JolCraftSounds.STRONGBOX_OPEN.get(), 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    public static void strongboxClose(Level level, BlockPos pos) {
        JolCraftSoundHelper.block(level, pos, JolCraftSounds.STRONGBOX_CLOSE.get(), 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    public static void strongboxUnlock(Level level, BlockPos pos) {
        JolCraftSoundHelper.block(level, pos, JolCraftSounds.STRONGBOX_UNLOCK.get(), 1.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    public static void strongboxLockpick(Level level, BlockPos pos) {
        JolCraftSoundHelper.block(level, pos, JolCraftSounds.STRONGBOX_LOCKPICK.get(), 1.2F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    public static void strongboxLockpickBreak(Level level, BlockPos pos) {
        JolCraftSoundHelper.block(level, pos, JolCraftSounds.STRONGBOX_LOCKPICK_BREAK.get(), 1.5F, level.random.nextFloat() * 0.1F + 0.7F);
    }

}