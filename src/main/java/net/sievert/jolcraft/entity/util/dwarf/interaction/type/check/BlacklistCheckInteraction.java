package net.sievert.jolcraft.entity.util.dwarf.interaction.type.check;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteraction;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

public class BlacklistCheckInteraction implements DwarfInteraction {

    private boolean blacklistedProperties(AbstractEntityEntity dwarf) {
        return !dwarf.isAlive() || dwarf.isTrading();
    }

    private boolean blacklistedItems(ItemStack stack) {
        return stack.is(JolCraftTags.Items.DWARF_SPAWN_EGGS);
    }

    private boolean isBlacklisted(AbstractEntityEntity dwarf, ItemStack stack) {
        return blacklistedProperties(dwarf) || blacklistedItems(stack);
    }

    @Override
    public InteractionResult handle(AbstractEntityEntity dwarf, Player player, ItemStack stack) {
        if (isBlacklisted(dwarf, stack)) {
            JolCraftSoundHelper.playDwarfNo(dwarf);
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

}
