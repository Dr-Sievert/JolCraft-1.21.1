package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.lore.LoreRarity;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.client.LoreKey;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.datagen.client.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public final class DwarfModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String SUB_BOOK = JolCraftDictionary.BOOK;

    private static final String SUB_TOME = JolCraftStrings.slashed(
            JolCraftDictionary.BOOK,
            JolCraftDictionary.TOME
    );

    private static final String SUB_TABLET = JolCraftDictionary.TABLET;

    private static final String SUB_CONTRACT = JolCraftDictionary.CONTRACT;


    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DWARVEN_LEXICON.get(), ModelTemplates.FLAT_ITEM, SUB_BOOK);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.ANCIENT_DWARVEN_LEXICON.get(), ModelTemplates.FLAT_ITEM, SUB_BOOK);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.REPUTATION_TABLET_0.get(), ModelTemplates.FLAT_ITEM, SUB_TABLET);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.REPUTATION_TABLET_1.get(), ModelTemplates.FLAT_ITEM, SUB_TABLET);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.REPUTATION_TABLET_2.get(), ModelTemplates.FLAT_ITEM, SUB_TABLET);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.REPUTATION_TABLET_3.get(), ModelTemplates.FLAT_ITEM, SUB_TABLET);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.REPUTATION_TABLET_4.get(), ModelTemplates.FLAT_ITEM, SUB_TABLET);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_BLANK.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_WRITTEN.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_SIGNED.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.GUILD_SIGIL.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_GUILDMASTER.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_MERCHANT.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_HISTORIAN.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_SCRAPPER.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_GUARD.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_BREWMASTER.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_KEEPER.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_MINER.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_EXPLORER.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_ALCHEMIST.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_ARCANIST.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_PRIEST.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_ARTISAN.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_CHAMPION.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_BLACKSMITH.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.CONTRACT_SMELTER.get(), ModelTemplates.FLAT_ITEM, SUB_CONTRACT);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DWARVEN_TOME_COMMON.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DWARVEN_TOME_RARE.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.DWARVEN_TOME_EPIC.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        AbstractModelProvider.generateFlatItem(items, JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);

        AbstractModelProvider.generateFlatItem(items, JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, SUB_TOME);
        generateLegendaryTomeModels(items);
    }

    public static void generateLegendaryTomeModels(ItemModelGenerators itemModels) {
        Item tomeItem = JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get();
        ResourceLocation baseModelLoc = ModelLocationUtils.getModelLocation(tomeItem);

        ResourceLocation baseTexture = JolCraft.location("item/" + SUB_TOME + "/ancient_dwarven_tome");
        ModelTemplates.FLAT_ITEM.create(baseModelLoc, TextureMapping.layer0(baseTexture), itemModels.modelOutput);

        ItemModel.Unbaked fallbackModel = ItemModelUtils.plainModel(baseModelLoc);

        Set<DwarfLoreKey> legendaryLoreKeys = DwarfLoreEntries.ALL.entrySet().stream()
                .filter(e -> e.getValue().rarity() == LoreRarity.LEGENDARY)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();

        for (DwarfLoreKey loreKey : legendaryLoreKeys) {
            String keyString = loreKey.name().toLowerCase(Locale.ROOT);
            String modelName = "item/book/tome/ancient_dwarven_tome_legendary_" + keyString;
            ResourceLocation modelLoc = JolCraft.location(modelName);

            ModelTemplates.FLAT_ITEM.create(modelLoc, TextureMapping.layer0(modelLoc), itemModels.modelOutput);
            ItemModel.Unbaked model = ItemModelUtils.plainModel(modelLoc);

            switchCases.add(ItemModelUtils.when(keyString, model));
        }

        itemModels.itemModelOutput.accept(
                tomeItem,
                new SelectItemModel.Unbaked(
                        new SelectItemModel.UnbakedSwitch<>(LoreKey.INSTANCE, switchCases),
                        Optional.of(fallbackModel)
                )
        );
    }
}