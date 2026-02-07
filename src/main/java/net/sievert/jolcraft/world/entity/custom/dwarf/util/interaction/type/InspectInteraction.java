package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.type;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteraction;

public abstract class InspectInteraction implements DwarfInteraction {

    @Override
    public InteractionResult handle(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        return InteractionResult.FAIL;
    }

}
