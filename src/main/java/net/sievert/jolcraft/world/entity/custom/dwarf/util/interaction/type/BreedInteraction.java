package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.type;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteraction;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public class BreedInteraction implements DwarfInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        boolean client = dwarf.level().isClientSide();

        if (!dwarf.isFood(itemstack)) {
            return InteractionResult.FAIL;
        }

        int age = dwarf.getAge();

        if (!client && age == 0 && dwarf.canFallInLove()) {
            dwarf.usePlayerItem(player, hand, itemstack);
            dwarf.setInLove(player);
            dwarf.playEatingSound();
            return InteractionResult.SUCCESS_SERVER;
        }

        if (dwarf.isBaby()) {
            dwarf.usePlayerItem(player, hand, itemstack);
            dwarf.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-age), true);
            dwarf.playEatingSound();
            return InteractionResult.SUCCESS;
        }

        if (client) {
            return InteractionResult.CONSUME;
        }

        PlaySound.dwarfNo(dwarf);
        return InteractionResult.FAIL;
    }
}
