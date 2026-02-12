package net.sievert.jolcraft.world.item.custom.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
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
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyData;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyTier;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyType;
import net.sievert.jolcraft.world.item.util.tooltip.TooltipHelper;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class BountyCrateItem extends Item implements IItemExtension {

    private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F);  // Green (Completed)
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);      // Red (In Progress)

    public BountyCrateItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {

        if (action == ClickAction.SECONDARY && other.isEmpty()) {
            BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
            int currentFilled = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);

            if (data != null && currentFilled > 0) {
                Item targetItem = resolveItem(player.level(), data.targetItem());
                if (targetItem == null) {
                    JolCraftLogs.warn(
                            JolCraftLogTags.ITEM,
                            "BountyCrate resolveItem failed (targetId={}, crate={})",
                            data.targetItem(),
                            stack.getItem()
                    );
                    return false;
                }

                int toExtract = Math.min(64, currentFilled);
                ItemStack out = new ItemStack(targetItem, toExtract);
                access.set(out);

                int remaining = currentFilled - toExtract;
                stack.set(JolCraftDataComponents.BOUNTY_FILL.get(), remaining);
                stack.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), remaining >= data.requiredCount());

                JolCraftSoundHelper.player(player, SoundEvents.ITEM_PICKUP, 0.6F, 1.2F);
                return true;
            }
        }

        if (action == ClickAction.PRIMARY || action == ClickAction.SECONDARY) {
            int maxTransfer = action == ClickAction.PRIMARY ? Integer.MAX_VALUE : 1;
            boolean filled = tryFillCrate(player.level(), stack, access.get(), access, maxTransfer);
            if (filled) {
                JolCraftSoundHelper.player(player, SoundEvents.ITEM_PICKUP, 0.6F, 1.2F);
            }
            return filled;
        }
        return false;
    }

    private boolean tryFillCrate(Level level, ItemStack crate, ItemStack target, SlotAccess access, int maxTransfer) {
        BountyData data = crate.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return false;

        Item targetItem = resolveItem(level, data.targetItem());
        if (targetItem == null) {
            JolCraftLogs.warn(
                    JolCraftLogTags.ITEM,
                    "BountyCrate resolveItem failed (targetId={}, crate={})",
                    data.targetItem(),
                    crate.getItem()
            );
            return false;
        }
        if (!target.is(targetItem)) return false;

        int currentFilled = crate.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);

        if (currentFilled > data.requiredCount()) {
            JolCraftLogs.warn(
                    JolCraftLogTags.ITEM,
                    "BountyCrate corrupt fill: currentFilled={} required={} (targetId={}, crate={})",
                    currentFilled,
                    data.requiredCount(),
                    data.targetItem(),
                    crate.getItem()
            );
            return false;
        }

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
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack crate = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BountyData data = crate.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return InteractionResult.PASS;

        int currentFilled = crate.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
        int needed = data.requiredCount() - currentFilled;

        if (needed <= 0) {
            crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_FILLED).withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        Item targetItem = resolveItem(level, data.targetItem());
        if (targetItem == null) {
            JolCraftLogs.warn(
                    JolCraftLogTags.ITEM,
                    "BountyCrate resolveItem failed on use (targetId={}, crate={})",
                    data.targetItem(),
                    crate.getItem()
            );
            return InteractionResult.PASS;
        }

        Inventory inv = player.getInventory();

        int collected = 0;
        boolean invChanged = false;

        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack stack = inv.items.get(i);
            if (!stack.is(targetItem)) continue;

            int transfer = Math.min(needed - collected, stack.getCount());
            if (transfer <= 0) continue;

            stack.shrink(transfer);
            collected += transfer;
            invChanged = true;

            if (stack.isEmpty()) {
                inv.items.set(i, ItemStack.EMPTY);
            }

            if (collected >= needed) break;
        }

        if (invChanged) {
            inv.setChanged();
            player.containerMenu.broadcastChanges();
        }

        if (collected > 0) {
            int newAmount = currentFilled + collected;
            crate.set(JolCraftDataComponents.BOUNTY_FILL.get(), newAmount);
            if (newAmount >= data.requiredCount()) {
                crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
            }

            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_FILLED_SOME, collected).withStyle(ChatFormatting.GRAY),
                    true
            );
            JolCraftSoundHelper.player(player, SoundEvents.ITEM_PICKUP, 0.6F, 1.2F);
        } else {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_NO_ITEMS).withStyle(ChatFormatting.GRAY),
                    true
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return 0;

        int requiredCount = data.requiredCount();
        int currentFilled = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);

        double progress = (double) currentFilled / requiredCount;
        return Math.min(13, (int) (progress * 13));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int currentFilled = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
        return currentFilled == Objects.requireNonNull(stack.get(JolCraftDataComponents.BOUNTY_DATA.get())).requiredCount()
                ? FULL_BAR_COLOR : BAR_COLOR;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(player);

        if (JolCraftProxy.access().isAltDown()) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            if (knowsLanguage) {
                BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
                if (data != null) {
                    ResourceLocation targetItem = data.targetItem();
                    int count = data.requiredCount();
                    int tierInt = data.tier();

                    Component itemName = Component.translatable(targetItem.toLanguageKey(JolCraftDictionary.ITEM));
                    if (itemName.getString().equals(targetItem.toLanguageKey(JolCraftDictionary.ITEM))) {
                        Item resolved = resolveItem(player.level(), targetItem);
                        if (resolved != null) {
                            itemName = resolved.getDefaultInstance().getHoverName();
                        }
                    }

                    BountyType type = BountyHelper.getBountyType(stack);
                    BountyTier tier = BountyTier.fromValue(tierInt);

                    if (type == BountyType.UNKNOWN || tier == BountyTier.UNKNOWN) {
                        tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_INVALID)
                                .withStyle(ChatFormatting.RED));
                    } else{
                        tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_TYPE)
                                .append(Component.translatable(JolCraftStrings.dotted(JolCraftDictionary.ENTITY, JolCraft.MOD_ID, type.getId())))
                                .withStyle(ChatFormatting.GRAY));

                        tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_TIER, tier.getDisplayName())
                                .withStyle(ChatFormatting.GRAY));
                    }

                    tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_TARGET)
                            .append(itemName)
                            .withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COUNT, count)
                            .withStyle(ChatFormatting.GRAY));

                    if (Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()))) {
                        tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COMPLETE)
                                .withStyle(ChatFormatting.GREEN));
                    }
                } else {
                    tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_INVALID)
                            .withStyle(ChatFormatting.RED));
                }
            } else {
                tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_LOCKED)
                        .withStyle(ChatFormatting.GRAY));
            }

            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_HOLD_KEY, TooltipHelper.altKey())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    private static Item resolveItem(Level level, ResourceLocation id) {
        Registry<Item> items = level.registryAccess().lookupOrThrow(Registries.ITEM);
        return items.getValue(id);
    }
}
