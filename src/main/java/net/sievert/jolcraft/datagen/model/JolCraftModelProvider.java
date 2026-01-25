package net.sievert.jolcraft.datagen.model;

import com.google.gson.JsonObject;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.Block;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.custom.crop.BarleyCropBlock;
import net.sievert.jolcraft.block.custom.crop.HopsCropBottomBlock;
import net.sievert.jolcraft.item.armor.JolCraftEquipmentAssets;
import net.sievert.jolcraft.item.JolCraftItems;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class JolCraftModelProvider extends ModelProvider {

    public JolCraftModelProvider(PackOutput output) {
        super(output, JolCraft.MOD_ID);

    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {

        //Core
        itemModels.generateFlatItem(JolCraftItems.DEV_KEY.get(), ModelTemplates.FLAT_ITEM);

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GOLD_COIN.get(), ModelTemplates.FLAT_ITEM, "coin");
        JolCraftModelHelper.generateCoinPouchModel(itemModels);

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DWARVEN_LEXICON.get(), ModelTemplates.FLAT_ITEM, "book");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ANCIENT_DWARVEN_LEXICON.get(), ModelTemplates.FLAT_ITEM, "book");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.REPUTATION_TABLET_0.get(), ModelTemplates.FLAT_ITEM, "tablet");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.REPUTATION_TABLET_1.get(), ModelTemplates.FLAT_ITEM, "tablet");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.REPUTATION_TABLET_2.get(), ModelTemplates.FLAT_ITEM, "tablet");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.REPUTATION_TABLET_3.get(), ModelTemplates.FLAT_ITEM, "tablet");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.REPUTATION_TABLET_4.get(), ModelTemplates.FLAT_ITEM, "tablet");

        //Materials & Crafting Ingredients

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.PARCHMENT.get(), ModelTemplates.FLAT_ITEM, "material/paper");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.QUILL_EMPTY.get(), ModelTemplates.FLAT_ITEM, "material/paper");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.QUILL_SMALL.get(), ModelTemplates.FLAT_ITEM, "material/paper");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.QUILL_HALF.get(), ModelTemplates.FLAT_ITEM, "material/paper");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.QUILL_FULL.get(), ModelTemplates.FLAT_ITEM, "material/paper");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.LEGENDARY_PAGE.get(), ModelTemplates.FLAT_ITEM, "material/paper");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MUFFHORN_MILK_BUCKET.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "material/entity");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MUFFHORN_FUR.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "material/entity");
        JolCraftModelHelper.createTrivialCube(blockModels, JolCraftBlocks.MUFFHORN_FUR_BLOCK.get(), "material/entity");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GEODE_SMALL.get(), ModelTemplates.FLAT_ITEM, "material/geode");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GEODE_MEDIUM.get(), ModelTemplates.FLAT_ITEM, "material/geode");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GEODE_LARGE.get(), ModelTemplates.FLAT_ITEM, "material/geode");
        JolCraftModelHelper.createTrivialCube(blockModels, JolCraftBlocks.GEODE_BLOCK.get(), "material/geode");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_PLATE.get(), ModelTemplates.FLAT_ITEM, "material/deepslate");
        JolCraftModelHelper.createTrivialCube(blockModels, JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get(), "material/deepslate");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_ROD.get(), ModelTemplates.FLAT_ITEM, "material/deepslate");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.IMPURE_MITHRIL.get(), ModelTemplates.FLAT_ITEM, "material/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.PURE_MITHRIL.get(), ModelTemplates.FLAT_ITEM, "material/mithril");
        JolCraftModelHelper.createTrivialCube(blockModels, JolCraftBlocks.PURE_MITHRIL_BLOCK.get(), "material/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_INGOT.get(), ModelTemplates.FLAT_ITEM, "material/mithril");
        JolCraftModelHelper.createTrivialCube(blockModels, JolCraftBlocks.MITHRIL_BLOCK.get(), "material/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_NUGGET.get(), ModelTemplates.FLAT_ITEM, "material/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_CHAINWEAVE.get(), ModelTemplates.FLAT_ITEM, "material/mithril");

        //Misc

        JolCraftModelHelper.createHearth(JolCraftBlocks.HEARTH.get(), blockModels);

        itemModels.generateFlatItem(JolCraftItems.LOCKPICK.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModels.generateFlatItem(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), ModelTemplates.FLAT_ITEM);

        //Crops

        JolCraftModelHelper.createVerdantFarmland(blockModels);
        blockModels.createTrivialCube(JolCraftBlocks.VERDANT_SOIL.get());

        blockModels.createCropBlock(JolCraftBlocks.BARLEY_CROP.get(), BarleyCropBlock.AGE,  0, 1, 2, 3, 4, 5, 6, 7);
        itemModels.generateFlatItem(JolCraftItems.BARLEY.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        blockModels.createRotatedPillarWithHorizontalVariant(
                JolCraftBlocks.BARLEY_BLOCK.get(),
                TexturedModel.COLUMN,
                TexturedModel.COLUMN_HORIZONTAL
        );

        blockModels.createPlantWithDefaultItem(JolCraftBlocks.DUSKCAP.get(), JolCraftBlocks.POTTED_DUSKCAP.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        JolCraftModelHelper.createFesterlingCrop(blockModels);
        blockModels.createPlantWithDefaultItem(JolCraftBlocks.FESTERLING.get(), JolCraftBlocks.POTTED_FESTERLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        JolCraftModelHelper.createTopCropBlock(
                blockModels,
                JolCraftBlocks.ASGARNIAN_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );
        blockModels.createCropBlock(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        JolCraftModelHelper.createTopCropBlock(
                blockModels,
                JolCraftBlocks.DUSKHOLD_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );
        blockModels.createCropBlock(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        JolCraftModelHelper.createTopCropBlock(
                blockModels,
                JolCraftBlocks.KRANDONIAN_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );
        blockModels.createCropBlock(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        JolCraftModelHelper.createTopCropBlock(
                blockModels,
                JolCraftBlocks.YANILLIAN_CROP_TOP.get(),
                0, 1, 2, 3, 4
        );
        blockModels.createCropBlock(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(), HopsCropBottomBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        itemModels.generateFlatItem(JolCraftItems.ASGARNIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DUSKHOLD_HOPS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.KRANDONIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.YANILLIAN_HOPS.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_BULBS.get(), ModelTemplates.FLAT_ITEM);
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

        //Eggs

        String dwarfEggPrimary = "aa7d66";

        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_SPAWN_EGG.get(),             dwarfEggPrimary, "4a342c");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG.get(), dwarfEggPrimary, "4f2144");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG.get(),   dwarfEggPrimary, "49652d");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_MERCHANT_SPAWN_EGG.get(),    dwarfEggPrimary, "842610");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG.get(),    dwarfEggPrimary, "764721");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG.get(),  dwarfEggPrimary, "806723");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_GUARD_SPAWN_EGG.get(),       dwarfEggPrimary, "333232");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_KEEPER_SPAWN_EGG.get(),      dwarfEggPrimary, "166b11");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_ARTISAN_SPAWN_EGG.get(),     dwarfEggPrimary, "2f286c");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_EXPLORER_SPAWN_EGG.get(),    dwarfEggPrimary, "0089a0");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_MINER_SPAWN_EGG.get(),       dwarfEggPrimary, "28351c");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG.get(),   dwarfEggPrimary, "89435e");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_ARCANIST_SPAWN_EGG.get(),    dwarfEggPrimary, "1e6c6a");
        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.DWARF_PRIEST_SPAWN_EGG.get(),      dwarfEggPrimary, "fff05a");

        JolCraftModelHelper.generateSpawnEgg(itemModels, JolCraftItems.MUFFHORN_SPAWN_EGG.get(), "723119", "4b1f12");

        //Ores
        blockModels.createRotatedPillarWithHorizontalVariant(
                JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(),
                TexturedModel.COLUMN,
                TexturedModel.COLUMN_HORIZONTAL
        );

        //Tools and Weapons
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "weapon/deepslate");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_WARHAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "weapon/deepslate");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/deepslate");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/deepslate");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/deepslate");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/deepslate");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/deepslate");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/deepslate");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "weapon/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_WARHAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "weapon/mithril");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_ARTISAN_HAMMER.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/mithril");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_CHISEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool/mithril");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.COPPER_SPANNER.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.IRON_SPANNER.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "tool");

        //Alchemy

        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_MORTAR_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.DEEPSLATE_PESTLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(JolCraftItems.MITHRIL_PESTLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModels.generateFlatItem(JolCraftItems.INVERIX.get(), ModelTemplates.FLAT_ITEM);

        //Brewing

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BARLEY_MALT.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "brewing");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.YEAST.get(), ModelTemplates.FLAT_ITEM, "brewing");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GLASS_MUG.get(), ModelTemplates.FLAT_ITEM, "brewing");

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

        //Bounty
        itemModels.generateFlatItem(JolCraftItems.BOUNTY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.BOUNTY_CRATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.RESTOCK_CRATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(JolCraftItems.REROLL_CRATE.get(), ModelTemplates.FLAT_ITEM);

        //Contracts
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_BLANK.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_WRITTEN.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_SIGNED.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GUILD_SIGIL.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_GUILDMASTER.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_MERCHANT.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_HISTORIAN.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_SCRAPPER.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_GUARD.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_BREWMASTER.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_KEEPER.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_MINER.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_EXPLORER.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_ALCHEMIST.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_ARCANIST.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_PRIEST.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_ARTISAN.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_CHAMPION.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_BLACKSMITH.get(), ModelTemplates.FLAT_ITEM, "contract");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.CONTRACT_SMELTER.get(), ModelTemplates.FLAT_ITEM, "contract");

        //Artisan
        blockModels.createTrivialBlock(JolCraftBlocks.LAPIDARY_BENCH.get(), TexturedModel.CUBE_TOP_BOTTOM);

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.AEGISCORE.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ASHFANG.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPMARROW.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.EARTHBLOOD.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.EMBERGLASS.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.FROSTVEIN.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GRIMSTONE.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.IRONHEART.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.LUMIERE.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MOONSHARD.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.RUSTAGATE.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SKYBURROW.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SUNGLEAM.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.VERDANITE.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.WOECRYSTAL.get(), ModelTemplates.FLAT_ITEM, "material/gem/uncut");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.AEGISCORE_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ASHFANG_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPMARROW_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.EARTHBLOOD_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.EMBERGLASS_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.FROSTVEIN_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GRIMSTONE_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.IRONHEART_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.LUMIERE_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MOONSHARD_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.RUSTAGATE_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SKYBURROW_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SUNGLEAM_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.VERDANITE_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.WOECRYSTAL_CUT.get(), ModelTemplates.FLAT_ITEM, "material/gem/cut");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.AEGISCORE_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ASHFANG_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPMARROW_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.EARTHBLOOD_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.EMBERGLASS_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.FROSTVEIN_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.GRIMSTONE_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.IRONHEART_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.LUMIERE_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MOONSHARD_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.RUSTAGATE_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SKYBURROW_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SUNGLEAM_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.VERDANITE_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.WOECRYSTAL_DUST.get(), ModelTemplates.FLAT_ITEM, "material/gem/dust");


        //Tomes
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DWARVEN_TOME_COMMON.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DWARVEN_TOME_RARE.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DWARVEN_TOME_EPIC.get(), JolCraftItems.DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");

        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), ModelTemplates.FLAT_ITEM, "book/tome");
        JolCraftModelHelper.generateLegendaryTomeModels(itemModels);

        //Scrapper
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SCRAP.get(), ModelTemplates.FLAT_ITEM, "material/scrap");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.SCRAP_HEAP.get(), ModelTemplates.FLAT_ITEM, "material/scrap");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_AMULET.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_BELT.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_COINS.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.DEEPSLATE_MUG.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_TABLET.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.EXPIRED_POTION.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.INGOT_MOULD.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.OLD_FABRIC.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.RUSTY_TONGS.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.MITHRIL_SALVAGE.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_MITHRIL_PLATE.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_MITHRIL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_DEEPSLATE_PLATES.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_DEEPSLATE_GEAR.get(), ModelTemplates.FLAT_ITEM, "material/salvage");
        JolCraftModelHelper.generateFlatItem(itemModels, JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get(), ModelTemplates.FLAT_ITEM, "material/salvage");

        //Trim

        List<ItemModelGenerators.TrimMaterialData> allTrimMaterials = new ArrayList<>();
        for (Map.Entry<String, ResourceKey<TrimMaterial>> entry : JolCraftModelHelper.VANILLA_TRIMS.entrySet()) {
            allTrimMaterials.add(new ItemModelGenerators.TrimMaterialData(entry.getKey(), entry.getValue(), Map.of()));
        }

        allTrimMaterials.addAll(JolCraftModelHelper.JOLCRAFT_TRIMS);

        JolCraftModelHelper.generateTrimmableArmorSetWithCustom(itemModels, "deepslate", JolCraftEquipmentAssets.DEEPSLATE_KEY, false);
        JolCraftModelHelper.generateTrimmableArmorSetWithCustom(itemModels, "mithril", JolCraftEquipmentAssets.MITHRIL_KEY, false);

        JolCraftModelHelper.generateArmorWithTrim(itemModels, "leather", EquipmentAssets.LEATHER, true);
        JolCraftModelHelper.generateArmorWithTrim(itemModels, "chainmail", EquipmentAssets.CHAINMAIL, false);
        JolCraftModelHelper.generateArmorWithTrim(itemModels, "iron", EquipmentAssets.IRON, false);
        JolCraftModelHelper.generateArmorWithTrim(itemModels, "golden", EquipmentAssets.GOLD, false);
        JolCraftModelHelper.generateArmorWithTrim(itemModels, "diamond", EquipmentAssets.DIAMOND,false);
        JolCraftModelHelper.generateArmorWithTrim(itemModels, "netherite", EquipmentAssets.NETHERITE, false);
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
                .filter(holder -> !holder.value().equals(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()))
                .filter(holder -> !holder.value().equals(JolCraftItems.DEEPSLATE_COMPASS.get()))
                .filter(holder -> !holder.value().equals(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()))
                .filter(holder -> !holder.value().equals(JolCraftItems.DWARVEN_BREW.get()));
    }
}
