package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.List;

public final class JeiInfoPageHelper {

    private JeiInfoPageHelper() {}

    public static List<JeiInfoPageRecipe> getRecipes() {

        return List.of(
                new JeiInfoPageRecipe(
                        JolCraftItems.DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_DWARVEN_LEXICON)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.ANCIENT_DWARVEN_LEXICON.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_ANCIENT_DWARVEN_LEXICON)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.COIN_POUCH.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_COIN_POUCH)
                ),
                new JeiInfoPageRecipe(
                        JolCraftTags.Items.PARTIAL_CONTRACTS,
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_CONTRACTS)
                ),
                new JeiInfoPageRecipe(
                        JolCraftTags.Items.REPUTATION_TABLETS,
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_REPUTATION_TABLET)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.STRONGBOX_ITEM.get().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_STRONGBOX)
                ),
                new JeiInfoPageRecipe(
                        List.of(
                                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get().getDefaultInstance(),
                                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get().getDefaultInstance(),
                                JolCraftItems.DEEPSLATE_COMPASS.get().getDefaultInstance()
                        ),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_DEEPSLATE_COMPASS),
                        JolCraftItemIds.DEEPSLATE_COMPASS
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.HEARTH.get().asItem().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_HEARTH)
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.FESTERLING.get().asItem().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_FESTERLING)
                ),
                new JeiInfoPageRecipe(
                        JolCraftItems.DEEPSLATE_BULBS.get().asItem().getDefaultInstance(),
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_BULBS)
                ),
                new JeiInfoPageRecipe(
                        JolCraftTags.Items.HOPS_SEEDS,
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_HOPS_SEEDS)
                ),
                JeiInfoPageRecipe.fromBlockTag(
                        JolCraftTags.Blocks.VERDANT,
                        Component.translatable(JolCraftLanguageKeys.JEI_INFO_VERDANT)
                ),
                new JeiInfoPageRecipe(
                        JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(),
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_MITHRIL_ORE)
                )
        );
    }
}