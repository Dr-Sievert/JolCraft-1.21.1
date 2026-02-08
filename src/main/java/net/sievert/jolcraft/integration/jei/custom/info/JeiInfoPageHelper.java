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

public final class JeiInfoPageHelper {

    public static List<JeiInfoPageRecipe> getAllInfoPages() {

        //Compass group
        ItemStack compassEmpty = JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get().getDefaultInstance();

        ItemStack compassDial = JolCraftItems.DEEPSLATE_COMPASS_DIAL.get().getDefaultInstance();
        compassDial.set(JolCraftDataComponents.DIAL_COLOR, new DialItemColor(0xFFFF0000));

        ItemStack compassCombined = JolCraftItems.DEEPSLATE_COMPASS.get().getDefaultInstance();
        compassCombined.set(JolCraftDataComponents.DIAL_COLOR, new DialItemColor(0xFFFF0000));

        return List.of(
                new JeiInfoPageRecipe(
                        JolCraftTags.Items.REPUTATION_TABLETS,
                        Component.translatable(JeiLangSubProvider.JEI_INFO_REPUTATION_TABLET)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.STRONGBOX_ITEM.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_STRONGBOX)
                ),
                new JeiInfoPageRecipe(
                        List.of(compassEmpty, compassDial, compassCombined),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_DEEPSLATE_COMPASS),
                        "compass"
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.COIN_POUCH.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_COIN_POUCH)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_DWARVEN_LEXICON)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.ANCIENT_DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_ANCIENT_DWARVEN_LEXICON)
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.HEARTH.get().asItem().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_HEARTH)
                ),
                JeiInfoPageRecipe.fromBlockTag(
                        JolCraftTags.Blocks.VERDANT,
                        Component.translatable(JeiLangSubProvider.JEI_INFO_VERDANT)
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.DUSKCAP.get().asItem().getDefaultInstance(),
                        Component.translatable(JeiLangSubProvider.JEI_INFO_MUSHROOM)
                ),
                new JeiInfoPageRecipe(
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
