package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.item.client.compass.DialItemColor;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.item.JolCraftItems;

import java.util.ArrayList;
import java.util.List;

public class InfoPageHelper {

    public static List<InfoPageRecipe> getAllInfoPages() {

        //Compass group
        ItemStack compassEmpty = JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get().getDefaultInstance();

        ItemStack compassDial = JolCraftItems.DEEPSLATE_COMPASS_DIAL.get().getDefaultInstance();
        compassDial.set(JolCraftDataComponents.DIAL_COLOR, new DialItemColor(0xFFFF0000));

        ItemStack compassCombined = JolCraftItems.DEEPSLATE_COMPASS.get().getDefaultInstance();
        compassCombined.set(JolCraftDataComponents.DIAL_COLOR, new DialItemColor(0xFFFF0000));

        return List.of(
                new InfoPageRecipe(
                        JolCraftTags.Items.REPUTATION_TABLETS,
                        Component.translatable("jei.jolcraft.info_page.reputation_tablet")
                ),
                new InfoPageRecipe(
                        JolCraftItems.STRONGBOX_ITEM.get().getDefaultInstance(),
                        Component.translatable("jei.jolcraft.info_page.strongbox")
                ),
                new InfoPageRecipe(
                        List.of(compassEmpty, compassDial, compassCombined),
                        Component.translatable("jei.jolcraft.info_page.deepslate_compass"),
                        "compass"
                ),
                new InfoPageRecipe(
                        JolCraftItems.COIN_POUCH.get().getDefaultInstance(),
                        Component.translatable("jei.jolcraft.info_page.coin_pouch")
                ),
                new InfoPageRecipe(
                        JolCraftItems.DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable("jei.jolcraft.info_page.dwarven_lexicon")
                ),
                new InfoPageRecipe(
                        JolCraftItems.ANCIENT_DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable("jei.jolcraft.info_page.ancient_dwarven_lexicon")
                ),
                new InfoPageRecipe(
                        JolCraftBlocks.HEARTH.get().asItem().getDefaultInstance(),
                        Component.translatable("jei.jolcraft.info_page.hearth")
                ),
                InfoPageRecipe.fromBlockTag(
                        JolCraftTags.Blocks.VERDANT,
                        Component.translatable("jei.jolcraft.info_page.verdant")
                ),
                new InfoPageRecipe(
                        JolCraftBlocks.DUSKCAP.get().asItem().getDefaultInstance(),
                        Component.translatable("jei.jolcraft.info_page.mushroom")
                ),
                new InfoPageRecipe(
                        JolCraftBlocks.FESTERLING.get().asItem().getDefaultInstance(),
                        Component.translatable("jei.jolcraft.info_page.festerling")
                )
        );
    }

    public static List<ItemStack> getAllStacksForTag(TagKey<Item> tag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            stacks.add(new ItemStack(holder.value()));
        }
        return stacks;
    }

}
