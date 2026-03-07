package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.item.util.coin.CoinPouchHelper;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PayCoinInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (!dwarf.canBePaid() || !stack.is(JolCraftTags.Items.COINS)) {
            return InteractionResult.PASS;
        }

        // Coin pouch payment (consume exactly 1 internal coin, do NOT consume pouch item)
        if (stack.getItem() instanceof CoinPouchItem) {
            int coins = CoinPouchHelper.getCoins(stack);
            if (coins <= 0) {
                return InteractionResult.PASS;
            }

            dwarf.setPaid(player);
            playSingleCoinSound(dwarf);
            if(!player.isCreative()){
                CoinPouchHelper.setCoins(stack, coins - 1);
            }
            player.setItemInHand(hand, stack);
            player.awardStat(JolCraftStats.COINS_SPENT.get(), 1);

            return InteractionResult.SUCCESS;
        }

        // Raw coin payment
        if (stack.is(JolCraftItems.GOLD_COIN.get())) {

            dwarf.setPaid(player);
            playSingleCoinSound(dwarf);
            dwarf.usePlayerItem(player, hand, stack);
            player.awardStat(JolCraftStats.COINS_SPENT.get(), 1);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void playSingleCoinSound(AbstractDwarfEntity dwarf) {
        JolCraftSoundHelper.entity(
                dwarf,
                JolCraftSounds.COIN_SINGLE.get(),
                0.8F + dwarf.level().random.nextFloat() * 0.2F,
                1.0F + dwarf.level().random.nextFloat() * 0.2F
        );
    }
}