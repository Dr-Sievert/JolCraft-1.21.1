package net.sievert.jolcraft.world.sound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

public class JolCraftSoundHelper {

    //Dwarf
    public static void playDwarfNo(LivingEntity entity) {
        playSound(entity, JolCraftSounds.DWARF_NO.get());
    }

    public static void playDwarfYes(LivingEntity entity) {
        playSound(entity, JolCraftSounds.DWARF_YES.get());
    }

    //Villager
    public static void playVillagerNo(LivingEntity entity) {
        playSound(entity, SoundEvents.VILLAGER_NO);
    }

    public static void playVillagerYes(LivingEntity entity) {
        playSound(entity, SoundEvents.VILLAGER_YES);
    }

    public static void playVillagerFisherman(LivingEntity entity) {
        playSound(entity, SoundEvents.VILLAGER_WORK_FISHERMAN);
    }

    public static void playSound(LivingEntity entity, SoundEvent sound) {
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        float pitch = entity.getVoicePitch();
        level.playSound(null, pos, sound, SoundSource.NEUTRAL, 1.0F, pitch);
    }
}
