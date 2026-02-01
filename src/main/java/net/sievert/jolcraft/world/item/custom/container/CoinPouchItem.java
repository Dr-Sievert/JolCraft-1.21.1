package net.sievert.jolcraft.world.item.custom.container;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.util.coin.CoinPouchTooltip;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CoinPouchItem extends Item {

    public static final int MAX_COINS = 999;

    private static final int BAR_COLOR = ARGB.color(255, 232, 193, 67);

    public CoinPouchItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pouch, ItemStack cursor, Slot slot, ClickAction action, Player player, SlotAccess access) {

        if (action == ClickAction.SECONDARY && cursor.isEmpty()) {
            int current = pouch.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
            if (current > 0) {
                int toGive = Math.min(64, current);
                ItemStack out = new ItemStack(JolCraftItems.GOLD_COIN.get(), toGive);
                pouch.set(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), current - toGive);
                access.set(out);

                if (toGive == 1) playSingleSound(player);
                else playStackSound(player);

                broadcastChangesOnContainerMenu(player);
                player.awardStat(Stats.ITEM_USED.get(this));
                return true;
            }
        }

        ItemStack slotStack = access.get();

        if (isGoldCoin(slotStack)) {
            int current = pouch.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
            int addable = 0;
            if (action == ClickAction.PRIMARY) {
                addable = Math.min(MAX_COINS - current, slotStack.getCount());
            } else if (action == ClickAction.SECONDARY) {
                addable = Math.min(MAX_COINS - current, 1);
            }
            if (addable > 0) {
                boolean wasEmpty = (current == 0);
                pouch.set(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), current + addable);
                slotStack.shrink(addable);
                access.set(slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);

                if (addable == 1 && wasEmpty) {
                    playPouchInsertSound(player);
                } else if (addable == 1) {
                    playSingleSound(player);
                } else {
                    playStackSound(player);
                }

                broadcastChangesOnContainerMenu(player);
            } else {
                playInsertFailSound(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack pouch = player.getItemInHand(hand);
        int current = pouch.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
        int canAdd = MAX_COINS - current;
        if (canAdd > 0) {
            int added = tryConsumeGoldCoinsFromInventory(player, canAdd);
            if (added > 0) {
                boolean wasEmpty = (current == 0);
                pouch.set(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), current + added);

                if (added == 1 && wasEmpty) {
                    playPouchInsertSound(player);
                } else if (added == 1) {
                    playSingleSound(player);
                } else {
                    playStackSound(player);
                }

                broadcastChangesOnContainerMenu(player);
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResult.SUCCESS;
            }
        }
        playInsertFailSound(player);
        return InteractionResult.PASS;
    }

    private boolean isGoldCoin(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == JolCraftItems.GOLD_COIN.get();
    }

    private int tryConsumeGoldCoinsFromInventory(Player player, int max) {
        int removed = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (isGoldCoin(slotStack)) {
                int toTake = Math.min(slotStack.getCount(), max - removed);
                slotStack.shrink(toTake);
                removed += toTake;
                if (removed >= max) break;
            }
        }
        return removed;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        int amount = stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
        return amount > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int amount = stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
        return Math.min(13, (int) ((amount / (float) MAX_COINS) * 13));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        int amount = stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
        return amount > 0
                ? Optional.of(new CoinPouchTooltip(amount))
                : Optional.empty();
    }

    private void playStackSound(Player player) {
        JolCraftSoundHelper.player(
                player,
                JolCraftSounds.COIN_STACK.get(),
                0.8F + player.level().random.nextFloat() * 0.2F,
                1.0F + player.level().random.nextFloat() * 0.2F
        );
    }

    private void playSingleSound(Player player) {
        JolCraftSoundHelper.player(
                player,
                JolCraftSounds.COIN_SINGLE.get(),
                0.8F + player.level().random.nextFloat() * 0.2F,
                1.0F + player.level().random.nextFloat() * 0.2F
        );
    }

    private void playPouchInsertSound(Player player) {
        JolCraftSoundHelper.player(player, SoundEvents.BUNDLE_INSERT, 0.8F, 1.3F);
    }

    private void playInsertFailSound(Player player) {
        JolCraftSoundHelper.player(player, SoundEvents.BUNDLE_INSERT_FAIL, 0.6F, 1.3F);
    }


    private void broadcastChangesOnContainerMenu(Player player) {
        AbstractContainerMenu menu = player.containerMenu;
        menu.slotsChanged(player.getInventory());
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        stack.set(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
    }

}
