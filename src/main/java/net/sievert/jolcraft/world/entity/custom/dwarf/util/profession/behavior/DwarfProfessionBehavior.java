package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.behavior;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

@FunctionalInterface
public interface DwarfProfessionBehavior {
    void onBeforeTradeScreen(AbstractDwarfEntity dwarf, Player player, InteractionHand hand);
}