package net.sievert.jolcraft.entity.util.dwarf.interaction.type;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class BreedInteraction implements DwarfInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        boolean client = dwarf.level().isClientSide;
        if (dwarf.isFood(itemstack)) {
            int i = dwarf.getAge();
            if (!client && i == 0 && dwarf.canFallInLove()) {
                dwarf.usePlayerItem(player, hand, itemstack);
                dwarf.setInLove(player);
                dwarf.playEatingSound();
                return InteractionResult.SUCCESS_SERVER;
            }
            if (dwarf.isBaby()) {
                dwarf.usePlayerItem(player, hand, itemstack);
                dwarf.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-i), true);
                dwarf.playEatingSound();
                return InteractionResult.SUCCESS;
            }
            if(client){return InteractionResult.CONSUME;}
            JolCraftSoundHelper.playDwarfNo(dwarf);
        }
        return InteractionResult.FAIL;
    }
}
