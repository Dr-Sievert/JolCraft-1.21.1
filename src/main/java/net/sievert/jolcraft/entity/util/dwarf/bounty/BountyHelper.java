package net.sievert.jolcraft.entity.util.dwarf.bounty;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponentType;
import net.sievert.jolcraft.data.JolCraftDataComponents;

public class BountyHelper {

    public static int getBountyTier(ItemStack stack) {
        DataComponentType<Integer> bountyTierComponent = JolCraftDataComponents.BOUNTY_TIER.get();
        return stack.getOrDefault(bountyTierComponent, 0);
    }

    public static String getBountyType(ItemStack stack) {
        DataComponentType<String> bountyTypeComponent = JolCraftDataComponents.BOUNTY_TYPE.get();
        return stack.getOrDefault(bountyTypeComponent, "");
    }
}