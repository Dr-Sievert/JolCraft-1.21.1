package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.entity.player.JolCraftStats;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PayCoinInteractionHandler
        implements DwarfInteractions.CoreInteraction {

    @Override
    public DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return DwarfInteractionOutcome.handled();
        }

        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        var stack = ctx.stack();

        if (!dwarf.canBePaid()
                || !stack.is(JolCraftTags.Items.COINS)) {

            return DwarfInteractionOutcome.pass();
        }

        /*
         * A pouch payment modifies the pouch's stored amount. It does not
         * consume the pouch itself.
         */
        if (stack.getItem() instanceof CoinPouchItem) {
            int coins = stack.getOrDefault(
                    JolCraftDataComponents.COIN_POUCH_AMOUNT.get(),
                    0
            );

            if (coins <= 0) {
                return DwarfInteractionOutcome.pass();
            }

            dwarf.setPaid(player);
            playSingleCoinSound(dwarf);

            if (!player.isCreative()) {
                stack.set(
                        JolCraftDataComponents.COIN_POUCH_AMOUNT.get(),
                        coins - 1
                );

                player.setItemInHand(
                        hand,
                        stack
                );
            }

            player.awardStat(
                    JolCraftStats.COINS_SPENT.get(),
                    1
            );

            return DwarfInteractionOutcome.handled();
        }

        /*
         * A loose gold coin is consumed centrally by
         * DwarfInteractions.commit().
         */
        if (stack.is(JolCraftItems.GOLD_COIN.get())) {
            dwarf.setPaid(player);
            playSingleCoinSound(dwarf);

            player.awardStat(
                    JolCraftStats.COINS_SPENT.get(),
                    1
            );

            return DwarfInteractionOutcome.consumeOne();
        }

        return DwarfInteractionOutcome.pass();
    }

    private void playSingleCoinSound(
            AbstractDwarfEntity dwarf
    ) {
        JolCraftSoundHelper.entity(
                dwarf,
                JolCraftSounds.COIN_SINGLE.get(),
                0.8F
                        + dwarf.level().random.nextFloat()
                        * 0.2F,
                1.0F
                        + dwarf.level().random.nextFloat()
                        * 0.2F
        );
    }
}