package net.sievert.jolcraft.entity.util.dwarf.bounty;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponentType;
import net.sievert.jolcraft.data.JolCraftDataComponents;

public class BountyHelper {

    /**
     * Gets the bounty tier as an enum. Returns NOVICE if missing or invalid.
     */
    public static BountyTier getBountyTier(ItemStack stack) {
        DataComponentType<Integer> comp = JolCraftDataComponents.BOUNTY_TIER.get();
        int value = stack.getOrDefault(comp, BountyTier.UNKNOWN.getValue());
        return BountyTier.fromValue(value);
    }

    /**
     * Gets the bounty type as an enum. Returns MINER if missing or invalid.
     */
    public static BountyType getBountyType(ItemStack stack) {
        DataComponentType<String> comp = JolCraftDataComponents.BOUNTY_TYPE.get();
        String id = stack.getOrDefault(comp, BountyType.UNKNOWN.getId());
        return BountyType.fromString(id);
    }

    /**
     * Sets the bounty type on the stack using the enum.
     */
    public static void setBountyType(ItemStack stack, BountyType type) {
        DataComponentType<String> bountyTypeComponent = JolCraftDataComponents.BOUNTY_TYPE.get();
        stack.set(bountyTypeComponent, type.getId());
    }

    /**
     * Sets the bounty tier on the stack using the enum.
     */
    public static void setBountyTier(ItemStack stack, BountyTier tier) {
        DataComponentType<Integer> bountyTierComponent = JolCraftDataComponents.BOUNTY_TIER.get();
        stack.set(bountyTierComponent, tier.getValue());
    }
}
