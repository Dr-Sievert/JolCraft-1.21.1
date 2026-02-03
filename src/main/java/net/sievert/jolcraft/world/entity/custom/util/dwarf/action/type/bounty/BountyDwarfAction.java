package net.sievert.jolcraft.world.entity.custom.util.dwarf.action.type.bounty;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty.BountyGenerator;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty.BountyHelper;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty.BountyTier;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public class BountyDwarfAction extends InspectDwarfAction {

    public int ticksRemaining = 0;
    private final BountyType type = BountyHelper.getBountyType(itemstack);

    public BountyDwarfAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {return DwarfActionType.Subtype.BOUNTY;}

    @Override
    public void start() {
        this.ticksRemaining = 40;
        startInspect(dwarf, player, hand, itemstack);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;

        if (type != BountyType.MERCHANT && type != BountyType.MINER) return;

        if (ticksRemaining == 25) {
            JolCraftSoundHelper.entity(
                    dwarf,
                    SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                    1.0F,
                    1.2F
            );
        }

        if (ticksRemaining == 15) {
            JolCraftSoundHelper.entity(
                    dwarf,
                    SoundEvents.VILLAGER_WORK_FISHERMAN,
                    1.0F,
                    1.0F
            );
        }
    }


    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        ItemStack crate = new ItemStack(JolCraftItems.BOUNTY_CRATE.get());
        int merchantTier = dwarf.getVillagerData().getLevel();
        BountyHelper.setBountyType(crate, type);
        BountyHelper.setBountyTier(crate, BountyTier.fromValue(merchantTier));
        crate.set(JolCraftDataComponents.BOUNTY_DATA.get(), BountyGenerator.generate(crate, dwarf.getRandom()));
        throwItem(dwarf, player, crate);
    }
}