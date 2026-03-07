package net.sievert.jolcraft.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * Generic item insertion helper.
 *
 * Contract:
 * - NEVER mutates the passed {@link ItemStack}.
 * - Slot-only and Slot->Inventory methods are atomic: either all items insert, or nothing changes.
 * - Slot->Inventory->Drop inserts what fits and drops the remainder via {@link ServerPlayer#drop(ItemStack, boolean)}.
 * - When a {@link Slot} is provided, respects {@link Slot#mayPlace(ItemStack)} and {@link Slot#getMaxStackSize(ItemStack)}.
 * - When only a {@link Container} + index is provided, no per-slot eligibility rules exist (caller controls routing).
 * - "Inventory" means player inventory items only: indices [0, 36). No armor/offhand.
 */
public final class ItemInsertionHelper {

    private ItemInsertionHelper() {}

    private static final int PLAYER_INV_START = 0;
    private static final int PLAYER_INV_END_EXCLUSIVE = 36;

    // ---------------------------------------------------------------------
    // 1️⃣ Slot only
    // ---------------------------------------------------------------------

    public static boolean tryInsertIntoSlot(Container container, int slotIndex, ItemStack stack) {
        if (container == null) return false;
        if (stack == null || stack.isEmpty()) return false;
        if (slotIndex < 0 || slotIndex >= container.getContainerSize()) return false;

        int count = stack.getCount();
        if (count <= 0) return false;

        if (simulateIntoContainerSlot(container, slotIndex, stack, count) != 0) return false;

        executeIntoContainerSlot(container, slotIndex, stack, count);
        container.setChanged();
        return true;
    }

    public static boolean tryInsertIntoSlot(Slot slot, ItemStack stack) {
        if (slot == null) return false;
        if (stack == null || stack.isEmpty()) return false;
        if (!slot.mayPlace(stack)) return false;

        int count = stack.getCount();
        if (count <= 0) return false;

        if (simulateIntoSlot(slot, stack, count) != 0) return false;

        executeIntoSlot(slot, stack, count);
        return true;
    }

    // ---------------------------------------------------------------------
    // 2️⃣ Slot → Inventory
    // ---------------------------------------------------------------------

    public static boolean tryInsertIntoSlotOrInventory(Container slotContainer, int slotIndex, Inventory inventory, ItemStack stack) {
        if (slotContainer == null || inventory == null) return false;
        if (stack == null || stack.isEmpty()) return false;
        if (slotIndex < 0 || slotIndex >= slotContainer.getContainerSize()) return false;

        int count = stack.getCount();
        if (count <= 0) return false;

        int remaining = count;
        remaining = simulateIntoContainerSlot(slotContainer, slotIndex, stack, remaining);
        remaining = simulateIntoInventoryRange(inventory, stack, remaining);

        if (remaining != 0) return false;

        int remExec = count;
        remExec = executeIntoContainerSlot(slotContainer, slotIndex, stack, remExec);
        executeIntoInventoryRange(inventory, stack, remExec);

        slotContainer.setChanged();
        inventory.setChanged();
        return true;
    }

    public static boolean tryInsertIntoSlotOrInventory(Slot slot, Inventory inventory, ItemStack stack) {
        if (slot == null || inventory == null) return false;
        if (stack == null || stack.isEmpty()) return false;
        if (!slot.mayPlace(stack)) return false;

        int count = stack.getCount();
        if (count <= 0) return false;

        int remaining = count;
        remaining = simulateIntoSlot(slot, stack, remaining);
        remaining = simulateIntoInventoryRange(inventory, stack, remaining);

        if (remaining != 0) return false;

        int remExec = count;
        remExec = executeIntoSlot(slot, stack, remExec);
        executeIntoInventoryRange(inventory, stack, remExec);

        inventory.setChanged();
        return true;
    }

    // ---------------------------------------------------------------------
    // 3️⃣ Slot → Inventory → Drop
    // ---------------------------------------------------------------------

    public static boolean tryInsertIntoSlotInventoryOrDrop(Container slotContainer, int slotIndex, Inventory inventory, ServerPlayer player, ItemStack stack) {
        if (slotContainer == null || inventory == null || player == null) return false;
        if (stack == null || stack.isEmpty()) return false;
        if (slotIndex < 0 || slotIndex >= slotContainer.getContainerSize()) return false;

        int count = stack.getCount();
        if (count <= 0) return false;

        int remaining = count;
        remaining = executeIntoContainerSlot(slotContainer, slotIndex, stack, remaining);
        remaining = executeIntoInventoryRange(inventory, stack, remaining);

        slotContainer.setChanged();
        inventory.setChanged();

        dropRemainder(player, stack, remaining);
        return true;
    }

    public static boolean tryInsertIntoSlotInventoryOrDrop(Slot slot, Inventory inventory, ServerPlayer player, ItemStack stack) {
        if (slot == null || inventory == null || player == null) return false;
        if (stack == null || stack.isEmpty()) return false;

        int count = stack.getCount();
        if (count <= 0) return false;

        int remaining = count;

        if (slot.mayPlace(stack)) {
            remaining = executeIntoSlot(slot, stack, remaining);
        }

        remaining = executeIntoInventoryRange(inventory, stack, remaining);
        inventory.setChanged();

        dropRemainder(player, stack, remaining);
        return true;
    }

    private static void dropRemainder(ServerPlayer player, ItemStack original, int remaining) {
        if (remaining <= 0) return;

        ItemStack drop = original.copyWithCount(remaining);
        if (!drop.isEmpty()) {
            player.drop(drop, false);
        }
    }

    // ---------------------------------------------------------------------
    // 4️⃣ Inventory → Drop
    // ---------------------------------------------------------------------

    public static boolean tryInsertIntoInventoryOrDrop(ServerPlayer player, ItemStack stack) {
        if (player == null) return false;
        if (stack == null || stack.isEmpty()) return false;

        int count = stack.getCount();
        if (count <= 0) return false;

        Inventory inventory = player.getInventory();

        int remaining = executeIntoInventoryRange(inventory, stack, count);
        inventory.setChanged();

        dropRemainder(player, stack, remaining);
        return true;
    }

    // ---------------------------------------------------------------------
    // Internal: Container slot
    // ---------------------------------------------------------------------

    private static int simulateIntoContainerSlot(Container container, int slotIndex, ItemStack incoming, int remaining) {
        if (remaining <= 0) return 0;

        ItemStack existing = container.getItem(slotIndex);
        int max = effectiveContainerSlotMax(container, existing, incoming);

        return simulateGenericSlot(existing, incoming, remaining, max);
    }

    private static int executeIntoContainerSlot(Container container, int slotIndex, ItemStack incoming, int remaining) {
        if (remaining <= 0) return 0;

        ItemStack existing = container.getItem(slotIndex);
        int max = effectiveContainerSlotMax(container, existing, incoming);

        return executeGenericSlot(existing, incoming, remaining, max, placed -> container.setItem(slotIndex, placed));
    }

    private static int effectiveContainerSlotMax(Container container, ItemStack existing, ItemStack incoming) {
        int max = Math.min(container.getMaxStackSize(), incoming.getMaxStackSize());
        if (!existing.isEmpty()) {
            max = Math.min(max, existing.getMaxStackSize());
        }
        return Math.max(max, 0);
    }

    // ---------------------------------------------------------------------
    // Internal: Slot object (respects mayPlace + per-slot max + setByPlayer)
    // ---------------------------------------------------------------------

    private static int simulateIntoSlot(Slot slot, ItemStack incoming, int remaining) {
        if (remaining <= 0) return 0;
        if (!slot.mayPlace(incoming)) return remaining;

        ItemStack existing = slot.getItem();
        int max = Math.max(slot.getMaxStackSize(incoming), 0);

        return simulateGenericSlot(existing, incoming, remaining, max);
    }

    private static int executeIntoSlot(Slot slot, ItemStack incoming, int remaining) {
        if (remaining <= 0) return 0;
        if (!slot.mayPlace(incoming)) return remaining;

        ItemStack existing = slot.getItem();
        int max = Math.max(slot.getMaxStackSize(incoming), 0);

        return executeGenericSlot(existing, incoming, remaining, max, slot::setByPlayer);
    }

    // ---------------------------------------------------------------------
    // Internal: generic single-slot math
    // ---------------------------------------------------------------------

    private static int simulateGenericSlot(ItemStack existing, ItemStack incoming, int remaining, int max) {
        if (remaining <= 0) return 0;
        if (max <= 0) return remaining;

        if (existing.isEmpty()) {
            int insert = Math.min(remaining, max);
            return remaining - insert;
        }

        if (!canMerge(existing, incoming)) return remaining;

        int space = max - existing.getCount();
        if (space <= 0) return remaining;

        int insert = Math.min(remaining, space);
        return remaining - insert;
    }

    private static int executeGenericSlot(ItemStack existing, ItemStack incoming, int remaining, int max, Consumer<ItemStack> setter) {
        if (remaining <= 0) return 0;
        if (max <= 0) return remaining;

        if (existing.isEmpty()) {
            int insert = Math.min(remaining, max);
            setter.accept(incoming.copyWithCount(insert));
            return remaining - insert;
        }

        if (!canMerge(existing, incoming)) return remaining;

        int space = max - existing.getCount();
        if (space <= 0) return remaining;

        int insert = Math.min(remaining, space);

        ItemStack grown = existing.copy();
        grown.grow(insert);
        setter.accept(grown);

        return remaining - insert;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean canMerge(ItemStack existing, ItemStack incoming) {
        return ItemStack.isSameItemSameComponents(existing, incoming) && existing.isStackable();
    }

    // ---------------------------------------------------------------------
    // Internal: inventory insertion
    // ---------------------------------------------------------------------

    private static int simulateIntoInventoryRange(Inventory inventory, ItemStack stack, int remaining) {
        return insertIntoInventoryRange(inventory, stack, remaining, false);
    }

    private static int executeIntoInventoryRange(Inventory inventory, ItemStack stack, int remaining) {
        return insertIntoInventoryRange(inventory, stack, remaining, true);
    }

    /**
     * Shared implementation for inventory insertion.
     * When execute=false, this is a pure simulation (no mutation).
     * When execute=true, it performs the actual insertion (mutates inventory.items).
     *
     * IMPORTANT: logic is intentionally identical to the previous two-pass implementation.
     */
    private static int insertIntoInventoryRange(Inventory inventory, ItemStack stack, int remaining, boolean execute) {
        int rem = remaining;
        if (rem <= 0) return 0;

        int end = Math.min(PLAYER_INV_END_EXCLUSIVE, inventory.items.size());

        for (int i = PLAYER_INV_START; i < end && rem > 0; i++) {
            ItemStack existing = inventory.items.get(i);
            if (existing.isEmpty()) continue;
            if (!canMerge(existing, stack)) continue;

            int max = Math.min(inventory.getMaxStackSize(), existing.getMaxStackSize());
            max = Math.min(max, stack.getMaxStackSize());

            int space = max - existing.getCount();
            if (space <= 0) continue;

            int insert = Math.min(rem, space);

            if (execute) {
                ItemStack grown = existing.copy();
                grown.grow(insert);
                inventory.items.set(i, grown);
            }

            rem -= insert;
        }

        for (int i = PLAYER_INV_START; i < end && rem > 0; i++) {
            ItemStack existing = inventory.items.get(i);
            if (!existing.isEmpty()) continue;

            int max = Math.min(inventory.getMaxStackSize(), stack.getMaxStackSize());
            int insert = Math.min(rem, max);
            if (insert <= 0) continue;

            if (execute) {
                inventory.items.set(i, stack.copyWithCount(insert));
            }

            rem -= insert;
        }

        return rem;
    }
}