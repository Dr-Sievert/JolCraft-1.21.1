package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.compass.DialItemColor;

import java.util.List;

public final class JeiInfoPageHelper {

    private JeiInfoPageHelper() {}

    public static List<JeiInfoPageRecipe> getAllInfoPages() {

        // Compass group
        ItemStack compassEmpty = JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get().getDefaultInstance();

        ItemStack compassDial = JolCraftItems.DEEPSLATE_COMPASS_DIAL.get().getDefaultInstance();
        compassDial.set(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR, new DialItemColor(0xFFFF0000));

        ItemStack compassCombined = JolCraftItems.DEEPSLATE_COMPASS.get().getDefaultInstance();
        compassCombined.set(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR, new DialItemColor(0xFFFF0000));

        return List.of(
                new JeiInfoPageRecipe(
                        JolCraftTags.Items.REPUTATION_TABLETS,
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_REPUTATION_TABLET)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.STRONGBOX_ITEM.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_STRONGBOX)
                ),
                new JeiInfoPageRecipe(
                        List.of(compassEmpty, compassDial, compassCombined),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_DEEPSLATE_COMPASS),
                        JolCraftItemIds.DEEPSLATE_COMPASS
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.COIN_POUCH.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_COIN_POUCH)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_DWARVEN_LEXICON)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.ANCIENT_DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_ANCIENT_DWARVEN_LEXICON)
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.HEARTH.get().asItem().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_HEARTH)
                ),
                JeiInfoPageRecipe.fromBlockTag(
                        JolCraftTags.Blocks.VERDANT,
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_VERDANT)
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.DUSKCAP.get().asItem().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_MUSHROOM)
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.FESTERLING.get().asItem().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_FESTERLING)
                )
        );
    }
}