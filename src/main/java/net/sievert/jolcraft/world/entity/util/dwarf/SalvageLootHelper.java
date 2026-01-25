package net.sievert.jolcraft.world.entity.util.dwarf;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.data.JolCraftTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SalvageLootHelper {

    private static final Random RANDOM = new Random();

    public static List<ItemStack> generateSalvageLoot(ItemStack salvageItem) {
        List<ItemStack> loot = new ArrayList<>();

        // Always give base scrap
        loot.add(new ItemStack(JolCraftItems.SCRAP.get()));

        // --- GENERAL SALVAGE ---
        if (salvageItem.is(JolCraftTags.Items.GENERAL_SALVAGE)) {
           return loot;
        }

        // --- TEXTILE SALVAGE ---
        if (salvageItem.is(JolCraftTags.Items.TEXTILE_SALVAGE)) {
            loot.add(new ItemStack(JolCraftItems.SCRAP.get(), 1 + RANDOM.nextInt(2)));
            if (RANDOM.nextFloat() < 0.35f)
                loot.add(new ItemStack(Items.STRING));
            if (RANDOM.nextFloat() < 0.15f)
                loot.add(new ItemStack(Items.LEATHER));
        }

        // --- REDSTONE SALVAGE ---
        if (salvageItem.is(JolCraftTags.Items.REDSTONE_SALVAGE)) {
            loot.add(new ItemStack(JolCraftItems.SCRAP.get(), 1 + RANDOM.nextInt(3)));
            if (RANDOM.nextFloat() < 0.30f)
                loot.add(new ItemStack(Items.REDSTONE));
            if (RANDOM.nextFloat() < 0.05f)
                loot.add(new ItemStack(JolCraftItems.SCRAP_HEAP.get()));
        }

        // --- IRON SALVAGE ---
        if (salvageItem.is(JolCraftTags.Items.IRON_SALVAGE)) {
            loot.add(new ItemStack(JolCraftItems.SCRAP.get(), 1 + RANDOM.nextInt(4)));
            if (RANDOM.nextFloat() < 0.50f)
                loot.add(new ItemStack(Items.IRON_NUGGET, 2 + RANDOM.nextInt(3)));
            if (RANDOM.nextFloat() < 0.15f)
                loot.add(new ItemStack(Items.IRON_INGOT));
            if (RANDOM.nextFloat() < 0.1f)
                loot.add(new ItemStack(JolCraftItems.SCRAP_HEAP.get()));
        }


        // --- DEEPSLATE SALVAGE ---
        if (salvageItem.is(JolCraftTags.Items.TEXTILE_SALVAGE)) {
            loot.add(new ItemStack(JolCraftItems.SCRAP.get(), 1 + RANDOM.nextInt(4)));
            if (RANDOM.nextFloat() < 0.15f)
                loot.add(new ItemStack(JolCraftItems.DEEPSLATE_PLATE.get()));
        }

        // --- GOLD SALVAGE ---
        if (salvageItem.is(JolCraftTags.Items.GOLD_SALVAGE)) {
            loot.add(new ItemStack(JolCraftItems.SCRAP.get(), 1 + RANDOM.nextInt(5)));
            if (RANDOM.nextFloat() < 0.50f)
                loot.add(new ItemStack(Items.GOLD_NUGGET, 2 + RANDOM.nextInt(3)));
            if (RANDOM.nextFloat() < 0.15f)
                loot.add(new ItemStack(Items.GOLD_INGOT));
            if (RANDOM.nextFloat() < 0.2f)
                loot.add(new ItemStack(JolCraftItems.SCRAP_HEAP.get()));
        }

        // --- MITHRIL SALVAGE ---
        if (salvageItem.is(JolCraftTags.Items.MITHRIL_SALVAGE)) {
            loot.add(new ItemStack(JolCraftItems.SCRAP.get(), 1 + RANDOM.nextInt(10)));
            if (RANDOM.nextFloat() < 0.3f)
                loot.add(new ItemStack(JolCraftItems.SCRAP_HEAP.get()));
            if (RANDOM.nextFloat() < 0.15f)
                loot.add(new ItemStack(JolCraftItems.MITHRIL_NUGGET.get(), 1 + RANDOM.nextInt(4)));
        }

        return loot;
    }
}
