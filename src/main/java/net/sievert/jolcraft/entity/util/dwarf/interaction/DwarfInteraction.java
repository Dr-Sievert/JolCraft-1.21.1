package net.sievert.jolcraft.entity.util.dwarf.interaction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import org.jetbrains.annotations.Nullable;

public interface DwarfInteraction {

    /**
     * Full signature
     */
    default InteractionResult handle(AbstractEntityEntity dwarf, Player player, @Nullable InteractionHand hand, @Nullable ItemStack itemstack) {
        return handle(dwarf, player);
    }

    /**
     * Minimal versions
     */

    default InteractionResult handle(AbstractEntityEntity dwarf, Player player) {
        return handle(dwarf, player, null, null);
    }

    default InteractionResult handle(AbstractEntityEntity dwarf, Player player, ItemStack stack) {
        return handle(dwarf, player, null, stack);
    }
}
