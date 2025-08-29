package net.sievert.jolcraft.item.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.util.attachment.DwarvenLanguageHelper;
import net.sievert.jolcraft.util.dwarf.bounty.BountyData;
import net.sievert.jolcraft.util.dwarf.bounty.BountyHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BountyCrateItem extends Item implements IItemExtension {
    public BountyCrateItem(Properties properties) {
        super(properties);
    }

    private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F);  // Green (Completed)
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);  // Red (In Progress)

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {

        // Extract whatever has been filled (even partially) if right-click and cursor is empty
        if (action == ClickAction.SECONDARY && other.isEmpty()) {
            BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
            int currentFilled = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);

            if (data != null && currentFilled > 0) {
                Item targetItem = BuiltInRegistries.ITEM.get(data.targetItem())
                        .map(Holder::value)
                        .orElse(null);

                if (targetItem != null) {
                    int toExtract = Math.min(64, currentFilled); // up to a full stack max
                    ItemStack out = new ItemStack(targetItem, toExtract);
                    access.set(out);

                    int remaining = currentFilled - toExtract;
                    stack.set(JolCraftDataComponents.BOUNTY_FILL.get(), remaining);
                    stack.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), remaining >= data.requiredCount());

                    player.level().playSound(
                            null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6f, 1.2f
                    );
                    return true;
                }
            }
        }

        if (action == ClickAction.PRIMARY || action == ClickAction.SECONDARY) {
            int maxTransfer = action == ClickAction.PRIMARY ? Integer.MAX_VALUE : 1;
            boolean filled = tryFillCrate(stack, access.get(), access, maxTransfer);
            if (filled) {
                player.level().playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        0.6f,
                        1.2f
                );
            }
            return filled;
        }
        return false;
    }


    private boolean tryFillCrate(ItemStack crate, ItemStack target, SlotAccess access, int maxTransfer) {
        BountyData data = crate.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return false;

        Item targetItem = BuiltInRegistries.ITEM.get(data.targetItem())
                .map(Holder::value)
                .orElse(null);
        if (targetItem == null || !target.is(targetItem)) return false;

        int currentFilled = crate.has(JolCraftDataComponents.BOUNTY_FILL.get())
                ? crate.get(JolCraftDataComponents.BOUNTY_FILL.get())
                : 0;

        int toTransfer = Math.min(data.requiredCount() - currentFilled, Math.min(maxTransfer, target.getCount()));
        if (toTransfer <= 0) return false;

        int newAmount = currentFilled + toTransfer;
        crate.set(JolCraftDataComponents.BOUNTY_FILL.get(), newAmount);
        if (newAmount >= data.requiredCount()) {
            crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
        }

        target.shrink(toTransfer);
        access.set(target.isEmpty() ? ItemStack.EMPTY : target);
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack crate = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BountyData data = crate.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return InteractionResult.PASS;

        int currentFilled = crate.has(JolCraftDataComponents.BOUNTY_FILL.get())
                ? crate.get(JolCraftDataComponents.BOUNTY_FILL.get())
                : 0;

        int needed = data.requiredCount() - currentFilled;
        if (needed <= 0) {
            crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
            player.displayClientMessage(Component.translatable("tooltip.jolcraft.bounty_crate.filled").withStyle(ChatFormatting.GRAY), true);
            return InteractionResult.SUCCESS;
        }

        Item targetItem = BuiltInRegistries.ITEM.get(data.targetItem())
                .map(Holder::value)
                .orElse(null);
        if (targetItem == null) return InteractionResult.PASS;

        int collected = 0;
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.is(targetItem)) {
                int transfer = Math.min(needed - collected, stack.getCount());
                stack.shrink(transfer);
                collected += transfer;

                if (stack.isEmpty()) {
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                }
                if (collected >= needed) break;
            }
        }

        if (collected > 0) {
            int newAmount = currentFilled + collected;
            crate.set(JolCraftDataComponents.BOUNTY_FILL.get(), newAmount);
            if (newAmount >= data.requiredCount()) {
                crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
            }

            player.displayClientMessage(Component.translatable("tooltip.jolcraft.bounty_crate.filled_some", collected).withStyle(ChatFormatting.GRAY), true);
            level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6f, 1.2f);
        } else {
            player.displayClientMessage(Component.translatable("tooltip.jolcraft.bounty_crate.no_items").withStyle(ChatFormatting.GRAY), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // Retrieve the current amount filled in the crate
        int currentFilled = stack.has(JolCraftDataComponents.BOUNTY_FILL.get())
                ? stack.get(JolCraftDataComponents.BOUNTY_FILL.get())
                : 0;
        // If the crate has any items filled, show the progress bar
        return currentFilled > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        // Retrieve the total required count to fill the crate
        BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return 0;

        int requiredCount = data.requiredCount();
        int currentFilled = stack.has(JolCraftDataComponents.BOUNTY_FILL.get())
                ? stack.get(JolCraftDataComponents.BOUNTY_FILL.get())
                : 0;

        // Calculate the progress as a fraction (current filled / required count)
        double progress = (double) currentFilled / requiredCount;

        // Calculate the width of the progress bar (scale it to fit within 0 to 13)
        return Math.min(13, (int) (progress * 13));  // 13 is the max bar width
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Retrieve the current amount filled in the crate
        int currentFilled = stack.has(JolCraftDataComponents.BOUNTY_FILL.get())
                ? stack.get(JolCraftDataComponents.BOUNTY_FILL.get())
                : 0;
        // If the crate is completely filled, set the bar color to green, else use red
        return currentFilled == Objects.requireNonNull(stack.get(JolCraftDataComponents.BOUNTY_DATA.get())).requiredCount()
                ? FULL_BAR_COLOR : BAR_COLOR;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvishClient();

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.jolcraft.bounty_crate")
                    .withStyle(ChatFormatting.GRAY));

        } else {
            if (knowsLanguage) {
                BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
                if (data != null) {
                    ResourceLocation targetItem = data.targetItem();
                    int count = data.requiredCount();
                    int tier = data.tier();

                    Component itemName = Component.translatable(targetItem.toLanguageKey("item"));
                    if (itemName.getString().equals(targetItem.toLanguageKey("item"))) {
                        Optional<Item> itemOpt = BuiltInRegistries.ITEM.getOptional(targetItem);
                        if (itemOpt.isPresent()) {
                            itemName = itemOpt.get().getDefaultInstance().getHoverName();
                        }
                    }

                    String type = BountyHelper.getBountyType(stack);
                    if (type.isEmpty()) {
                        tooltip.add(Component.translatable("tooltip.jolcraft.bounty.type.invalid").withStyle(ChatFormatting.RED));
                    } else {
                        tooltip.add(Component.translatable("tooltip.jolcraft.bounty.type").append(Component.translatable("entity.jolcraft.dwarf_" + type)).withStyle(ChatFormatting.GRAY));
                    }

                    String tierName = switch (tier) {
                        case 1 -> "Novice";
                        case 2 -> "Apprentice";
                        case 3 -> "Journeyman";
                        case 4 -> "Expert";
                        case 5 -> "Master";
                        default -> "Unknown";
                    };
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty_crate.tier", tierName)
                            .withStyle(ChatFormatting.GRAY));

                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty_crate.target")
                            .append(itemName)
                            .withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty_crate.count", count)
                            .withStyle(ChatFormatting.GRAY));

                    if (stack.has(JolCraftDataComponents.BOUNTY_COMPLETE.get()) &&
                            Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()))) {
                        tooltip.add(Component.translatable("tooltip.jolcraft.bounty_crate.complete")
                                .withStyle(ChatFormatting.GREEN));
                    }
                } else {
                    tooltip.add(Component.translatable("tooltip.jolcraft.bounty_crate.invalid")
                            .withStyle(ChatFormatting.RED));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.jolcraft.bounty_crate.locked")
                        .withStyle(ChatFormatting.GRAY));
            }

            Component shiftKey = Component.literal("Shift").withStyle(ChatFormatting.BLUE);
            tooltip.add(Component.translatable("tooltip.jolcraft.shift", shiftKey)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }


}
