package net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.bounty;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyGenerator;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public class BountyTaskAction extends InspectDwarfAction {

    private int ticksRemaining = 0;
    private ItemStack plannedResult = ItemStack.EMPTY;

    public BountyTaskAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.BOUNTY;
    }

    @Override
    public void start() {
        this.ticksRemaining = 40;

        if (!dwarf.level().isClientSide && dwarf.level() instanceof ServerLevel level) {
            ItemStack redeemStack = this.itemstack;
            if (!redeemStack.isEmpty()) {
                this.plannedResult = BountyGenerator.Task.roll(level, redeemStack);
            }
        }

        startInspect(dwarf, player, hand, itemstack);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;

        if (ticksRemaining == 25) {
            JolCraftSoundHelper.entity(
                    dwarf,
                    SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                    1.0F,
                    1.2F
            );
        }

        if (ticksRemaining == 15) {
            if (!plannedResult.isEmpty() && plannedResult.is(JolCraftItems.BOUNTY_CRATE.get())) {
                JolCraftSoundHelper.entity(
                        dwarf,
                        SoundEvents.VILLAGER_WORK_FISHERMAN,
                        1.0F,
                        1.0F
                );
            } else {
                JolCraftSoundHelper.entity(
                        dwarf,
                        SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                        1.0F,
                        1.2F
                );
            }
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        if (!(dwarf.level() instanceof ServerLevel)) return;
        if (plannedResult.isEmpty()) return;
        dwarf.usePlayerItem(player, hand, itemstack);
        throwItem(dwarf, player, plannedResult);
    }
}