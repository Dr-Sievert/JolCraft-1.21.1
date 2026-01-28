package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.datagen.language.subprovider.JeiLangSubProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.client.compass.DialItemColor;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.item.JolCraftItems;

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
                        Component.translatable(JeiLangSubProvider.JEI_INFO_REPUTATION_TABLET)
                ),
                new InfoPageRecipe(
                        JolCraftItems.STRONGBOX_ITEM.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_STRONGBOX)
                ),
                new InfoPageRecipe(
                        List.of(compassEmpty, compassDial, compassCombined),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_DEEPSLATE_COMPASS),
                        "compass"
                ),
                new InfoPageRecipe(
                        JolCraftItems.COIN_POUCH.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_COIN_POUCH)
                ),
                new InfoPageRecipe(
                        JolCraftItems.DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_DWARVEN_LEXICON)
                ),
                new InfoPageRecipe(
                        JolCraftItems.ANCIENT_DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_ANCIENT_DWARVEN_LEXICON)
                ),
                new InfoPageRecipe(
                        JolCraftBlocks.HEARTH.get().asItem().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_HEARTH)
                ),
                InfoPageRecipe.fromBlockTag(
                        JolCraftTags.Blocks.VERDANT,
                        Component.translatable(JeiLangSubProvider.JEI_INFO_VERDANT)
                ),
                new InfoPageRecipe(
                        JolCraftBlocks.DUSKCAP.get().asItem().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_MUSHROOM)
                ),
                new InfoPageRecipe(
                        JolCraftBlocks.FESTERLING.get().asItem().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_FESTERLING)
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
