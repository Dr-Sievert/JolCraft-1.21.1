package net.sievert.jolcraft.datagen.model;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.custom.crop.BarleyCropBlock;
import net.sievert.jolcraft.block.custom.crop.FesterlingCropBlock;
import net.sievert.jolcraft.block.custom.crop.HopsCropBottomBlock;
import net.sievert.jolcraft.block.custom.crop.HopsCropTopBlock;
import net.sievert.jolcraft.data.custom.lore.LoreRarity;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.item.armor.JolCraftEquipmentAssets;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.trim.JolCraftTrimMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.sievert.jolcraft.item.client.coin.CoinPouchAmountProperty;
import net.sievert.jolcraft.data.custom.lore.client.LoreKeyProperty;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JolCraftModelProvider extends ModelProvider {

    public JolCraftModelProvider(PackOutput output) {
        super(output, JolCraft.MOD_ID);

    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        //Core
        itemModels.generateFlatItem(JolCraftItems.DEV_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GOLD_COIN.get(), ModelTemplates.FLAT_ITEM);
        generateCoinPouchModel(itemModels);
        itemModels.generateFlatItem(JolCraftItems.DWARVEN_LEXICON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ANCIENT_DWARVEN_LEXICON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.PARCHMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.LOCKPICK.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModels.generateItemWithTintedOverlay(
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                new Dye(0xD3D3D3)
        );

        createHearth(JolCraftBlocks.HEARTH.get(), blockModels);

        //Mithril
        blockModels.createRotatedPillarWithHorizontalVariant(
                JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(),
                TexturedModel.COLUMN,
                TexturedModel.COLUMN_HORIZONTAL
        );
        blockModels.createTrivialCube(JolCraftBlocks.PURE_MITHRIL_BLOCK.get());
        blockModels.createTrivialCube(JolCraftBlocks.MITHRIL_BLOCK.get());
        itemModels.generateFlatItem(JolCraftItems.IMPURE_MITHRIL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.PURE_MITHRIL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_CHAINWEAVE.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(JolCraftItems.MITHRIL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_WARHAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        //Weapons and Tools
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_PLATE.get(), ModelTemplates.FLAT_ITEM);
        blockModels.createTrivialCube(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get());
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_ROD.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_WARHAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        //Custom Armor and Trim Materials/Templates
        List<ItemModelGenerators.TrimMaterialData> allTrimMaterials = new ArrayList<>();
        for (Map.Entry<String, ResourceKey<TrimMaterial>> entry : VANILLA_TRIMS.entrySet()) {
            allTrimMaterials.add(new ItemModelGenerators.TrimMaterialData(entry.getKey(), entry.getValue(), Map.of()));
        }

        allTrimMaterials.addAll(JOLCRAFT_TRIMS);

        generateTrimmableArmorSetWithCustom(itemModels, "deepslate", JolCraftEquipmentAssets.DEEPSLATE_KEY, false);

        generateTrimmableArmorSetWithCustom(itemModels, "mithril", JolCraftEquipmentAssets.MITHRIL_KEY, false);

        generateArmorWithTrim(itemModels, "leather", EquipmentAssets.LEATHER, true);
        generateArmorWithTrim(itemModels, "chainmail", EquipmentAssets.CHAINMAIL, false);
        generateArmorWithTrim(itemModels, "iron", EquipmentAssets.IRON, false);
        generateArmorWithTrim(itemModels, "golden", EquipmentAssets.GOLD, false);
        generateArmorWithTrim(itemModels, "diamond", EquipmentAssets.DIAMOND,false);
        generateArmorWithTrim(itemModels, "netherite", EquipmentAssets.NETHERITE, false);

        itemModels.generateFlatItem(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), ModelTemplates.FLAT_ITEM);

        //Alchemy

        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_MORTAR_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_PESTLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_PESTLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModels.generateFlatItem(JolCraftItems.INVERIX.get(), ModelTemplates.FLAT_ITEM);

        //Animal-related
        itemModels.generateFlatItem(JolCraftItems.MUFFHORN_MILK_BUCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MUFFHORN_FUR.get(), ModelTemplates.FLAT_ITEM);
        blockModels.createTrivialCube(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get());

        //Brewing
        itemModels.generateFlatItem(JolCraftItems.BARLEY.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        blockModels.createRotatedPillarWithHorizontalVariant(
                JolCraftBlocks.BARLEY_BLOCK.get(),
                TexturedModel.COLUMN,
                TexturedModel.COLUMN_HORIZONTAL
        );
        itemModels.generateFlatItem(JolCraftItems.BARLEY_MALT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ASGARNIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DUSKHOLD_HOPS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.KRANDONIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.YANILLIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.YEAST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GLASS_MUG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DWARVEN_BREW.get(), ModelTemplates.FLAT_ITEM);

        //Crops

        blockModels.createTrivialCube(JolCraftBlocks.VERDANT_SOIL.get());
        createVerdantFarmland(blockModels);

        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_BULBS.get(), ModelTemplates.FLAT_ITEM);

        //Bounty
        itemModels.generateFlatItem(JolCraftItems.BOUNTY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BOUNTY_CRATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.RESTOCK_CRATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.REROLL_CRATE.get(), ModelTemplates.FLAT_ITEM);

        //Contracts and related
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_BLANK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_WRITTEN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_SIGNED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GUILD_SIGIL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_GUILDMASTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_MERCHANT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_HISTORIAN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_SCRAPPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_GUARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_BREWMASTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_KEEPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_MINER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_EXPLORER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_ALCHEMIST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_ARCANIST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_PRIEST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_ARTISAN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_CHAMPION.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_BLACKSMITH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.CONTRACT_SMELTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.QUILL_EMPTY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.QUILL_SMALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.QUILL_HALF.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.QUILL_FULL.get(), ModelTemplates.FLAT_ITEM);


        //Gems
        blockModels.createTrivialBlock(JolCraftBlocks.LAPIDARY_BENCH.get(), TexturedModel.CUBE_TOP_BOTTOM);

        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_ARTISAN_HAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        blockModels.createTrivialCube(JolCraftBlocks.GEODE_BLOCK.get());

        itemModels.generateFlatItem(JolCraftItems.GEODE_SMALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GEODE_MEDIUM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GEODE_LARGE.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(JolCraftItems.AEGISCORE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ASHFANG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPMARROW.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.EARTHBLOOD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.EMBERGLASS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.FROSTVEIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GRIMSTONE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.IRONHEART.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.LUMIERE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MOONSHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.RUSTAGATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.SKYBURROW.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.SUNGLEAM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.VERDANITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.WOECRYSTAL.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(JolCraftItems.AEGISCORE_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ASHFANG_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPMARROW_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.EARTHBLOOD_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.EMBERGLASS_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.FROSTVEIN_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GRIMSTONE_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.IRONHEART_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.LUMIERE_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MOONSHARD_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.RUSTAGATE_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.SKYBURROW_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.SUNGLEAM_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.VERDANITE_CUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.WOECRYSTAL_CUT.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(JolCraftItems.AEGISCORE_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ASHFANG_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPMARROW_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.EARTHBLOOD_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.EMBERGLASS_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.FROSTVEIN_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.GRIMSTONE_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.IRONHEART_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.LUMIERE_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MOONSHARD_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.RUSTAGATE_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.SKYBURROW_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.SUNGLEAM_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.VERDANITE_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.WOECRYSTAL_DUST.get(), ModelTemplates.FLAT_ITEM);


        //Reputation
        itemModels.generateFlatItem(JolCraftItems.REPUTATION_TABLET_0.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.REPUTATION_TABLET_1.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.REPUTATION_TABLET_2.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.REPUTATION_TABLET_3.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.REPUTATION_TABLET_4.get(), ModelTemplates.FLAT_ITEM);


        //Tomes
        itemModels.generateFlatItem(JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DWARVEN_TOME_COMMON.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DWARVEN_TOME_RARE.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DWARVEN_TOME_EPIC.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.LEGENDARY_PAGE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM);
        generateLegendaryTomeModels(itemModels);

        //Tools and weapons
        itemModels.generateFlatItem(JolCraftItems.COPPER_SPANNER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.IRON_SPANNER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);


        //Salvage
        itemModels.generateFlatItem(JolCraftItems.SCRAP.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.SCRAP_HEAP.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_AMULET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_BELT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_COINS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_MUG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_TABLET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.EXPIRED_POTION.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.INGOT_MOULD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.OLD_FABRIC.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.RUSTY_TONGS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_SALVAGE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_MITHRIL_PLATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_MITHRIL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_DEEPSLATE_GEAR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get(), ModelTemplates.FLAT_ITEM);


        //Eggs

        String dwarfEggPrimary = "aa7d66";

        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("4a342c")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("4f2144")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("49652d")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_MERCHANT_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("842610")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("764721")
        );

        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("806723")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_GUARD_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("333232")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_KEEPER_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("166b11")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_ARTISAN_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("2f286c")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_EXPLORER_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("0089a0")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_MINER_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("28351c")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("89435e")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_ARCANIST_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("1e6c6a")
        );
        itemModels.generateSpawnEgg(
                JolCraftItems.DWARF_PRIEST_SPAWN_EGG.get(),
                eggColorPrimary(dwarfEggPrimary),
                eggColorSecondary("fff05a")
        );

        itemModels.generateSpawnEgg(
                JolCraftItems.MUFFHORN_SPAWN_EGG.get(),
                eggColorPrimary("723119"),
                eggColorSecondary("4b1f12")
        );

        //Crops

        blockModels.createPlantWithDefaultItem(JolCraftBlocks.DUSKCAP.get(), JolCraftBlocks.POTTED_DUSKCAP.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        createFesterlingCrop(blockModels);
        blockModels.createPlantWithDefaultItem(JolCraftBlocks.FESTERLING.get(), JolCraftBlocks.POTTED_FESTERLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        blockModels.createCropBlock(JolCraftBlocks.BARLEY_CROP.get(), BarleyCropBlock.AGE,  0, 1, 2, 3, 4, 5, 6, 7);

        blockModels.blockStateOutput.accept(new BlockStateGenerator() {
            @Override
            public JsonObject get() {
                JsonObject root = new JsonObject();
                JsonObject variants = new JsonObject();
                for (int age = 0; age <= 9; age++) {
                    variants.add("age=" + age, modelObj("block/deepslate_bulbs_crop_stage" + age));
                }
                root.add("variants", variants);
                return root;
            }

            @Override
            public @NotNull Block getBlock() {
                return JolCraftBlocks.DEEPSLATE_BULBS_CROP.get();
            }
        });

        createTopCropBlock(
                blockModels,
                JolCraftBlocks.ASGARNIAN_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );

        blockModels.createCropBlock(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        createTopCropBlock(
                blockModels,
                JolCraftBlocks.DUSKHOLD_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );

        blockModels.createCropBlock(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        createTopCropBlock(
                blockModels,
                JolCraftBlocks.KRANDONIAN_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );

        blockModels.createCropBlock(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        createTopCropBlock(
                blockModels,
                JolCraftBlocks.YANILLIAN_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );

        blockModels.createCropBlock(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        //Special

        blockModels.blockStateOutput.accept(new BlockStateGenerator() {
            @Override
            public JsonObject get() {
                JsonObject root = new JsonObject();
                JsonObject variants = new JsonObject();
                variants.add("level=1", modelObj("block/fermenting_cauldron_level1"));
                variants.add("level=2", modelObj("block/fermenting_cauldron_level2"));
                variants.add("level=3", modelObj("block/fermenting_cauldron_full"));
                root.add("variants", variants);
                return root;
            }
            @Override
            public @NotNull Block getBlock() {
                return JolCraftBlocks.FERMENTING_CAULDRON.get();
            }
        });
    }

    private static final List<ItemModelGenerators.TrimMaterialData> JOLCRAFT_TRIMS = List.of(
           //Armor = override
            new ItemModelGenerators.TrimMaterialData(
                    "deepslate",
                    JolCraftTrimMaterials.DEEPSLATE,
                    Map.of(JolCraftEquipmentAssets.DEEPSLATE_KEY, "deepslate_darker")
            ),
            new ItemModelGenerators.TrimMaterialData(
                    "mithril",
                    JolCraftTrimMaterials.MITHRIL,
                    Map.of(JolCraftEquipmentAssets.MITHRIL_KEY, "mithril_darker")
            ),
            // Gems (no override, use Map.of())
            new ItemModelGenerators.TrimMaterialData("aegiscore", JolCraftTrimMaterials.AEGISCORE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("ashfang", JolCraftTrimMaterials.ASHFANG, Map.of()),
            new ItemModelGenerators.TrimMaterialData("deepmarrow", JolCraftTrimMaterials.DEEPMARROW, Map.of()),
            new ItemModelGenerators.TrimMaterialData("earthblood", JolCraftTrimMaterials.EARTHBLOOD, Map.of()),
            new ItemModelGenerators.TrimMaterialData("emberglass", JolCraftTrimMaterials.EMBERGLASS, Map.of()),
            new ItemModelGenerators.TrimMaterialData("frostvein", JolCraftTrimMaterials.FROSTVEIN, Map.of()),
            new ItemModelGenerators.TrimMaterialData("grimstone", JolCraftTrimMaterials.GRIMSTONE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("ironheart", JolCraftTrimMaterials.IRONHEART, Map.of()),
            new ItemModelGenerators.TrimMaterialData("lumiere", JolCraftTrimMaterials.LUMIERE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("moonshard", JolCraftTrimMaterials.MOONSHARD, Map.of()),
            new ItemModelGenerators.TrimMaterialData("rustagate", JolCraftTrimMaterials.RUSTAGATE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("skyburrow", JolCraftTrimMaterials.SKYBURROW, Map.of()),
            new ItemModelGenerators.TrimMaterialData("sungleam", JolCraftTrimMaterials.SUNGLEAM, Map.of()),
            new ItemModelGenerators.TrimMaterialData("verdanite", JolCraftTrimMaterials.VERDANITE, Map.of()),
            new ItemModelGenerators.TrimMaterialData("woecrystal", JolCraftTrimMaterials.WOECRYSTAL, Map.of())
    );

    private void generateCoinPouchModel(ItemModelGenerators itemModels) {
        Item pouch = JolCraftItems.COIN_POUCH.get();

        ResourceLocation small = JolCraft.location("item/coin_pouch_small");
        ResourceLocation large = JolCraft.location("item/coin_pouch_large");
        ResourceLocation full  = JolCraft.location("item/coin_pouch_full");

        ModelTemplates.FLAT_ITEM.create(small, TextureMapping.layer0(small), itemModels.modelOutput);
        ModelTemplates.FLAT_ITEM.create(large,  TextureMapping.layer0(large),  itemModels.modelOutput);
        ModelTemplates.FLAT_ITEM.create(full,  TextureMapping.layer0(full),  itemModels.modelOutput);

        List<SelectItemModel.SwitchCase<Integer>> cases = List.of(
                ItemModelUtils.when(0,   ItemModelUtils.plainModel(small)),
                ItemModelUtils.when(1,   ItemModelUtils.plainModel(large)),
                ItemModelUtils.when(2,   ItemModelUtils.plainModel(full))
        );

        itemModels.itemModelOutput.accept(
                pouch,
                new SelectItemModel.Unbaked(
                        new SelectItemModel.UnbakedSwitch<>(CoinPouchAmountProperty.INSTANCE, cases),
                        Optional.of(ItemModelUtils.plainModel(small))
                )
        );
    }

    public void generateLegendaryTomeModels(ItemModelGenerators itemModels) {
        Item tomeItem = JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get();
        ResourceLocation baseModelLoc = ModelLocationUtils.getModelLocation(tomeItem);
        ResourceLocation fallbackTexture = TextureMapping.getItemTexture(tomeItem);

        ModelTemplates.FLAT_ITEM.create(baseModelLoc, TextureMapping.layer0(fallbackTexture), itemModels.modelOutput);
        ItemModel.Unbaked fallbackModel = ItemModelUtils.plainModel(baseModelLoc);

        Set<DwarfLoreKey> legendaryLoreKeys = DwarfLoreEntries.ALL.entrySet().stream()
                .filter(e -> e.getValue().rarity() == LoreRarity.LEGENDARY)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        List<SelectItemModel.SwitchCase<String>> switchCases = new ArrayList<>();

        for (DwarfLoreKey loreKey : legendaryLoreKeys) {
            String keyString = loreKey.name().toLowerCase(Locale.ROOT);
            String modelName = "item/ancient_dwarven_tome_legendary_" + keyString;
            ResourceLocation modelLoc = JolCraft.location(modelName);

            ModelTemplates.FLAT_ITEM.create(modelLoc, TextureMapping.layer0(modelLoc), itemModels.modelOutput);
            ItemModel.Unbaked model = ItemModelUtils.plainModel(modelLoc);

            switchCases.add(ItemModelUtils.when(keyString, model));
        }

        itemModels.itemModelOutput.accept(
                tomeItem,
                new SelectItemModel.Unbaked(
                        new SelectItemModel.UnbakedSwitch<>(LoreKeyProperty.INSTANCE, switchCases),
                        Optional.of(fallbackModel)
                )
        );
    }

    private void generateTrimmableItemWithCustomList(
            ItemModelGenerators itemModels,
            String baseName,
            ResourceKey<EquipmentAsset> key,
            boolean dyeable,
            List<ItemModelGenerators.TrimMaterialData> trimMaterialList) {

        for (String type : ARMOR_TYPES) {
            String fileName = baseName + "_" + type;

            ResourceLocation baseModelLocation = ResourceLocation.fromNamespaceAndPath(key.location().getNamespace(), "item/" + fileName);
            ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(key.location().getNamespace(), "item/" + fileName);
            ResourceLocation overlayTexture = ResourceLocation.fromNamespaceAndPath(key.location().getNamespace(), "item/" + fileName + "_overlay");

            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> list = new ArrayList<>(trimMaterialList.size());

            for (ItemModelGenerators.TrimMaterialData data : trimMaterialList) {
                ResourceLocation trimModelLoc = baseModelLocation.withSuffix("_" + data.name() + "_trim");

                String trimTextureName = data.name();
                if (baseName.equals(data.name())) {
                    trimTextureName += "_darker";
                }
                ResourceLocation trimTextureLocation = ResourceLocation.withDefaultNamespace("trims/items/" + type + "_trim_" + trimTextureName);

                ItemModel.Unbaked bakedModel;
                if (dyeable) {
                    itemModels.generateLayeredItem(
                            trimModelLoc,
                            textureLocation,
                            overlayTexture,
                            trimTextureLocation
                    );
                    bakedModel = ItemModelUtils.tintedModel(trimModelLoc, new Dye(-6265536));
                } else {
                    itemModels.generateLayeredItem(
                            trimModelLoc,
                            textureLocation,
                            trimTextureLocation
                    );
                    bakedModel = ItemModelUtils.plainModel(trimModelLoc);
                }
                list.add(ItemModelUtils.when(data.materialKey(), bakedModel));
            }

            ItemModel.Unbaked defaultModel;
            if (dyeable) {
                ModelTemplates.TWO_LAYERED_ITEM.create(baseModelLocation, TextureMapping.layered(textureLocation, overlayTexture), itemModels.modelOutput);
                defaultModel = ItemModelUtils.tintedModel(baseModelLocation, new Dye(-6265536));
            } else {
                ModelTemplates.FLAT_ITEM.create(baseModelLocation, TextureMapping.layer0(textureLocation), itemModels.modelOutput);
                defaultModel = ItemModelUtils.plainModel(baseModelLocation);
            }

            Item armorItem = getItemFromBaseName(baseName, type);
            itemModels.itemModelOutput.accept(
                    armorItem,
                    ItemModelUtils.select(new TrimMaterialProperty(), defaultModel, list)
            );
        }
    }

    public void generateTrimmableArmorSetWithCustom(
            ItemModelGenerators itemModels,
            String baseName,
            ResourceKey<EquipmentAsset> key,
            boolean dyeable
    ) {
        List<ItemModelGenerators.TrimMaterialData> allTrims = new ArrayList<>(ItemModelGenerators.TRIM_MATERIAL_MODELS);
        allTrims.addAll(JOLCRAFT_TRIMS);
        generateTrimmableItemWithCustomList(itemModels, baseName, key, dyeable, allTrims);
    }

    private void generateArmorWithTrim(
            ItemModelGenerators itemModels,
            String baseName,
            ResourceKey<EquipmentAsset> key,
            boolean dyeable) {

        List<ItemModelGenerators.TrimMaterialData> allTrimMaterials = new ArrayList<>();

        for (Map.Entry<String, ResourceKey<TrimMaterial>> entry : VANILLA_TRIMS.entrySet()) {
            allTrimMaterials.add(new ItemModelGenerators.TrimMaterialData(entry.getKey(), entry.getValue(), Map.of()));
        }

        allTrimMaterials.addAll(JOLCRAFT_TRIMS);

        for (String type : ARMOR_TYPES) {
            String fileName = baseName + "_" + type;

            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> selectCases = new ArrayList<>();

            for (ItemModelGenerators.TrimMaterialData trim : allTrimMaterials) {
                boolean isCustom = trim.materialKey().location().getNamespace().equals("jolcraft");
                boolean isVanillaArmor = baseName.equals("diamond") || baseName.equals("netherite") || baseName.equals("leather")
                        || baseName.equals("iron") || baseName.equals("golden") || baseName.equals("chainmail");
                String trimName = trim.name();

                ResourceLocation caseModelLoc;

                if (!isVanillaArmor && isCustom) {
                    caseModelLoc = JolCraft.location("item/" + fileName + "_" + trimName + "_trim");

                    ResourceLocation texture = JolCraft.location("item/" + fileName);
                    ResourceLocation overlay = JolCraft.location("item/" + fileName + "_overlay");
                    ResourceLocation trimTexture = JolCraft.location("trims/items/" + type + "_trim_" + trimName);

                    addTrimModelToList(
                            itemModels,
                            caseModelLoc,
                            texture,
                            overlay,
                            trim,
                            trimTexture,
                            selectCases,
                            dyeable
                    );
                } else if (isCustom) {
                    caseModelLoc = JolCraft.location("item/" + fileName);

                    ResourceLocation texture = ResourceLocation.withDefaultNamespace("item/" + fileName);
                    ResourceLocation overlay = ResourceLocation.withDefaultNamespace("item/" + fileName + "_overlay");
                    ResourceLocation trimTexture = ResourceLocation.withDefaultNamespace("trims/items/" + type + "_trim_" + trimName);

                    addTrimModelToList(
                            itemModels,
                            caseModelLoc,
                            texture,
                            overlay,
                            trim,
                            trimTexture,
                            selectCases,
                            dyeable
                    );
                } else {
                    caseModelLoc = ResourceLocation.withDefaultNamespace("item/" + fileName + "_" + trimName + "_trim");
                    ItemModel.Unbaked dummyModel = ItemModelUtils.plainModel(caseModelLoc);
                    selectCases.add(ItemModelUtils.when(trim.materialKey(), dummyModel));
                }
            }

            ResourceLocation fallbackModelLoc = (baseName.equals("diamond") || baseName.equals("netherite") || baseName.equals("leather")
                    || baseName.equals("iron") || baseName.equals("golden") || baseName.equals("chainmail"))
                    ? ResourceLocation.withDefaultNamespace("item/" + fileName)
                    : JolCraft.location("item/" + fileName);

            ItemModel.Unbaked fallbackModel = dyeable
                    ? ItemModelUtils.tintedModel(fallbackModelLoc, new Dye(-6265536))
                    : ItemModelUtils.plainModel(fallbackModelLoc);

            Item armorItem = getItemFromBaseName(baseName, type);
            itemModels.itemModelOutput.accept(
                    armorItem,
                    ItemModelUtils.select(new TrimMaterialProperty(), fallbackModel, selectCases)
            );
        }
    }

    private void addTrimModelToList(
            ItemModelGenerators itemModels,
            ResourceLocation baseModelLocation,
            ResourceLocation textureLocation,
            ResourceLocation overlayTexture,
            ItemModelGenerators.TrimMaterialData trim,
            ResourceLocation trimTextureLocation,
            List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> list,
            boolean dyeable) {

        ItemModel.Unbaked bakedModel;
        if (dyeable) {
            itemModels.generateLayeredItem(baseModelLocation.withSuffix("_" + trim.name() + "_trim"), textureLocation, overlayTexture, trimTextureLocation);
            bakedModel = ItemModelUtils.tintedModel(baseModelLocation.withSuffix("_" + trim.name() + "_trim"), new Dye(-6265536)); // Example color
        } else {
            itemModels.generateLayeredItem(baseModelLocation.withSuffix("_" + trim.name() + "_trim"), textureLocation, trimTextureLocation);
            bakedModel = ItemModelUtils.plainModel(baseModelLocation.withSuffix("_" + trim.name() + "_trim"));
        }

        list.add(ItemModelUtils.when(trim.materialKey(), bakedModel));
    }

    private static final String[] ARMOR_TYPES = {"helmet", "chestplate", "leggings", "boots"};

    private static final Map<String, ResourceKey<TrimMaterial>> VANILLA_TRIMS = Map.of(
            "quartz",   TrimMaterials.QUARTZ,
            "iron",     TrimMaterials.IRON,
            "netherite",TrimMaterials.NETHERITE,
            "redstone", TrimMaterials.REDSTONE,
            "copper",   TrimMaterials.COPPER,
            "gold",     TrimMaterials.GOLD,
            "emerald",  TrimMaterials.EMERALD,
            "diamond",  TrimMaterials.DIAMOND,
            "lapis",    TrimMaterials.LAPIS,
            "amethyst", TrimMaterials.AMETHYST
    );

    private Item getItemFromBaseName(String baseName, String type) {
        String itemName = baseName + "_" + type;

        ResourceLocation jolcraftLocation = JolCraft.location(itemName);
        Optional<Item> itemOptional = BuiltInRegistries.ITEM.getOptional(jolcraftLocation);

        if (itemOptional.isEmpty()) {
            ResourceLocation minecraftLocation = ResourceLocation.withDefaultNamespace(itemName);
            itemOptional = BuiltInRegistries.ITEM.getOptional(minecraftLocation);
        }

        return itemOptional.orElseThrow(() -> new IllegalStateException("Item not found: " + itemName));
    }

    private void createTopCropBlock(BlockModelGenerators blockModels, Block block, int... ageToVisualStageMapping) {
        if (HopsCropTopBlock.TOP_AGE.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException("Mismatch between age property values and visual stage mapping!");
        }

        Int2ObjectMap<ResourceLocation> visualStageModels = new Int2ObjectOpenHashMap<>();

        PropertyDispatch dispatch = PropertyDispatch.property(HopsCropTopBlock.TOP_AGE).generate(ageValue -> {
            int visualStage = ageToVisualStageMapping[ageValue];
            ResourceLocation modelId = visualStageModels.computeIfAbsent(
                    visualStage,
                    i -> blockModels.createSuffixedVariant(block, "_stage" + i, ModelTemplates.CROP, TextureMapping::crop)
            );
            return Variant.variant().with(VariantProperties.MODEL, modelId);
        });

        blockModels.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(dispatch));
    }

    public void createFesterlingCrop(BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.FESTERLING_CROP.get())
                        .with(
                                PropertyDispatch.property(FesterlingCropBlock.AGE)
                                        .generate(age -> Variant.variant()
                                                .with(
                                                        VariantProperties.MODEL,
                                                        blockModels.createSuffixedVariant(
                                                                JolCraftBlocks.FESTERLING_CROP.get(),
                                                                "_stage" + age,
                                                                ModelTemplates.CROSS,
                                                                TextureMapping::cross
                                                        )
                                                )
                                        )
                        )
        );
    }

    public void createVerdantFarmland(BlockModelGenerators blockModels) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(JolCraftBlocks.VERDANT_SOIL.get()))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(JolCraftBlocks.VERDANT_FARMLAND.get()));

        ResourceLocation model = ModelTemplates.FARMLAND.create(
                JolCraftBlocks.VERDANT_FARMLAND.get(),
                mapping,
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.VERDANT_FARMLAND.get())
                        .with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, model, model))
        );
    }

    private static VariantProperties.Rotation rotFromDegrees(int degrees) {
        return switch (degrees) {
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }

    private static int vanillaFacingY(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST  -> 90;
            case SOUTH -> 180;
            case WEST  -> 270;
            default    -> 0;
        };
    }

    public void createHearth(Block hearthBlock, BlockModelGenerators blockModels) {
        TextureMapping baseMapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(hearthBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(hearthBlock, "_front"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(hearthBlock, "_front"));

        ResourceLocation hearthModel = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(
                hearthBlock,
                baseMapping,
                blockModels.modelOutput
        );

        TextureMapping litMapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(hearthBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(hearthBlock, "_front_on"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(hearthBlock, "_top"))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(hearthBlock, "_front_on"));

        ResourceLocation hearthOnModel = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(
                hearthBlock,
                "_on",
                litMapping,
                blockModels.modelOutput
        );

        ResourceLocation chimney = JolCraft.location("block/hearth_chimney");

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(hearthBlock)
                        .with(
                                PropertyDispatch
                                        .properties(
                                                BlockStateProperties.DOUBLE_BLOCK_HALF,
                                                BlockStateProperties.LIT,
                                                BlockStateProperties.HORIZONTAL_FACING
                                        )
                                        .generate((half, lit, facing) -> {
                                            VariantProperties.Rotation xRot = VariantProperties.Rotation.R0;
                                            VariantProperties.Rotation yRot = rotFromDegrees(vanillaFacingY(facing));

                                            if (half == DoubleBlockHalf.LOWER) {
                                                return Variant.variant()
                                                        .with(VariantProperties.MODEL, lit ? hearthOnModel : hearthModel)
                                                        .with(VariantProperties.X_ROT, xRot)
                                                        .with(VariantProperties.Y_ROT, yRot);
                                            } else {
                                                return Variant.variant()
                                                        .with(VariantProperties.MODEL, chimney)
                                                        .with(VariantProperties.X_ROT, xRot)
                                                        .with(VariantProperties.Y_ROT, yRot);
                                            }
                                        })
                        )
        );
    }

    static int eggColor(String hex, int mask) {
        String s = hex.trim();
        if (s.startsWith("#")) s = s.substring(1);
        else if (s.startsWith("0x") || s.startsWith("0X")) s = s.substring(2);
        int rgb = Integer.parseInt(s, 16) & 0xFFFFFF;
        int r = Math.min(255, (((rgb >> 16) & 0xFF) * 255 + mask) / mask);
        int g = Math.min(255, (((rgb >> 8)  & 0xFF) * 255 + mask) / mask);
        int b = Math.min(255, (( rgb        & 0xFF) * 255 + mask) / mask);
        return (int)(0xFF000000L | (r << 16) | (g << 8) | b);
    }

    static int eggColorPrimary(String hex) {
        return eggColor(hex, 232);
    }

    static int eggColorSecondary(String hex) {
        return eggColor(hex, 222);
    }

    private static JsonObject modelObj(String path) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", JolCraft.MOD_ID + ":" + path);
        return obj;
    }

    @Override
    protected @NotNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.listElements()
                .filter(holder -> {
                    assert holder.getKey() != null;
                    return holder.getKey().location().getNamespace().equals(modId);
                })
                .filter(holder -> !holder.value().equals(JolCraftBlocks.DEEPSLATE_MORTAR.get()))
                .filter(holder -> !holder.value().equals(JolCraftBlocks.STRONGBOX.get()))
                .filter(holder -> !holder.value().equals(JolCraftBlocks.STRONGBOX_DUMMY.get()));
    }

    @Override
    protected @NotNull Stream<? extends Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(holder -> {
                    assert holder.getKey() != null;
                    return holder.getKey().location().getNamespace().equals(modId);
                })
                .filter(holder -> !holder.value().equals(JolCraftItems.STRONGBOX_ITEM.get()))
                .filter(holder -> !holder.value().equals(JolCraftItems.DEEPSLATE_COMPASS.get()))
                .filter(holder -> !holder.value().equals(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()));
    }


}
