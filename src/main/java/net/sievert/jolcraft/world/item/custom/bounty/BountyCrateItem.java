package net.sievert.jolcraft.world.item.custom.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.world.item.component.custom.BountyData;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class BountyCrateItem extends AbstractBountyTaskItem {

    public BountyCrateItem(Properties properties) {
        super(properties);
    }

    // ---------------------------------------------------------------------
    // AbstractBountyTaskItem hooks
    // ---------------------------------------------------------------------

    @Override
    protected @NotNull String lockedTooltipKey() {
        return JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_LOCKED;
    }

    @Override
    protected boolean supportsAltTooltip(ItemStack stack) {
        BountyData data = getBountyDataOrNull(stack);
        return data != null;
    }

    @Override
    protected @NotNull String altTooltipKey(ItemStack stack) {
        return JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_COLLECT_ALT;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void appendHeaderLines(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        DwarfProfession type = BountyRecipe.getType(stack);
        DwarfMerchantData.Level tier = BountyRecipe.getTier(stack);

        if (type != null) {
            tooltip.add(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_BOUNTY_TYPE,
                            Component.translatable(
                                    AbstractLanguageKeys.entity(type.getId())
                            )
                    ).withStyle(ChatFormatting.GRAY)
            );
        }

        if (tier != null) {
            tooltip.add(
                    Component.translatable(
                            JolCraftLanguageKeys.TOOLTIP_BOUNTY_TIER,
                            Component.translatable(tier.langKey())
                    ).withStyle(ChatFormatting.GRAY)
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void appendInvalidLines(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        DwarfProfession type = BountyRecipe.getType(stack);
        DwarfMerchantData.Level tier = BountyRecipe.getTier(stack);

        if (type == null && tier == null) {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_INVALID)
                    .withStyle(ChatFormatting.RED));
        }
    }

    // ---------------------------------------------------------------------
    // Inventory interactions (UNCHANGED logic)
    // ---------------------------------------------------------------------

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {

        if (action == ClickAction.SECONDARY && other.isEmpty()) {
            BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
            int currentFilled = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);

            if (data != null && currentFilled > 0 && data.objective() instanceof BountyData.BountyObjective.ItemObjective(Holder<Item> item, int required)) {
                int toExtract = Math.min(64, currentFilled);
                ItemStack out = new ItemStack(item.value(), toExtract);
                access.set(out);

                int remaining = currentFilled - toExtract;
                stack.set(JolCraftDataComponents.BOUNTY_FILL.get(), remaining);
                stack.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), remaining >= required);

                pickupSound(player);
                return true;
            }
        }

        if (action == ClickAction.PRIMARY || action == ClickAction.SECONDARY) {
            int maxTransfer = action == ClickAction.PRIMARY ? Integer.MAX_VALUE : 1;
            boolean filled = tryFillCrate(stack, access.get(), access, maxTransfer);
            if (filled) pickupSound(player);
            return filled;
        }

        return false;
    }

    private boolean tryFillCrate(ItemStack crate, ItemStack target, SlotAccess access, int maxTransfer) {
        BountyData data = crate.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return false;

        if (!(data.objective() instanceof BountyData.BountyObjective.ItemObjective(Holder<Item> item, int required))) {
            return false;
        }

        if (!target.is(item)) return false;

        int currentFilled = crate.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);

        if (currentFilled > required) {
            return false;
        }

        int toTransfer = Math.min(required - currentFilled, Math.min(maxTransfer, target.getCount()));
        if (toTransfer <= 0) return false;

        int newAmount = currentFilled + toTransfer;
        crate.set(JolCraftDataComponents.BOUNTY_FILL.get(), newAmount);
        if (newAmount >= required) {
            crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
        }

        target.shrink(toTransfer);
        access.set(target.isEmpty() ? ItemStack.EMPTY : target);
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack crate = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(crate);

        BountyData data = crate.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return InteractionResultHolder.pass(crate);

        if (player.isShiftKeyDown()) {
            boolean ok = shiftExtractToInventoryOrDrop(player, crate, data);
            if (ok) {
                player.containerMenu.broadcastChanges();
                return InteractionResultHolder.success(crate);
            }
            return InteractionResultHolder.pass(crate);
        }

        if (!(data.objective() instanceof BountyData.BountyObjective.ItemObjective(Holder<Item> item, int required))) {
            return InteractionResultHolder.pass(crate);
        }

        int currentFilled = crate.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
        int needed = required - currentFilled;

        if (needed <= 0) {
            crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_FILLED).withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResultHolder.success(crate);
        }

        Inventory inv = player.getInventory();

        int collected = 0;
        boolean invChanged = false;

        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack s = inv.items.get(i);
            if (!s.is(item)) continue;

            int transfer = Math.min(needed - collected, s.getCount());
            if (transfer <= 0) continue;

            s.shrink(transfer);
            collected += transfer;
            invChanged = true;

            if (s.isEmpty()) {
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
            if (newAmount >= required) {
                crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
            }
            pickupSound(player);
        } else {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_NO_ITEMS).withStyle(ChatFormatting.GRAY),
                    true
            );
        }

        return InteractionResultHolder.success(crate);
    }

    private static boolean shiftExtractToInventoryOrDrop(Player player, ItemStack crate, BountyData data) {
        Level level = player.level();
        if (level.isClientSide) return false;

        if (!(data.objective() instanceof BountyData.BountyObjective.ItemObjective(Holder<Item> item, int required))) {
            return false;
        }

        int currentFilled = crate.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
        if (currentFilled <= 0) return false;

        int toGive = Math.min(64, currentFilled);
        ItemStack out = new ItemStack(item.value(), toGive);

        int remaining = currentFilled - toGive;
        crate.set(JolCraftDataComponents.BOUNTY_FILL.get(), remaining);
        crate.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), remaining >= required);

        player.getInventory().add(out);
        if (!out.isEmpty()) {
            player.drop(out, false);
        }

        pickupSound(player);
        return true;
    }

    private static void pickupSound(Player player) {
        PlaySound.itemPickup(player, 0.6F, 0.8F);
    }
}