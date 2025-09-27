package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.datagen.recipe.builder.JolCraftRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.JolSmithingTrimRecipeBuilder;
import net.sievert.jolcraft.item.JolCraftItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JolCraftRecipeProvider extends RecipeProvider {
    public JolCraftRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new JolCraftRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "JolCraft Recipes";
        }
    }

    @Override
    protected void buildRecipes() {

        modShaped(RecipeCategory.MISC, JolCraftItems.COIN_POUCH.get())
                .pattern("XBX")
                .pattern("B B")
                .pattern("BBB")
                .define('B', Items.LEATHER)
                .define('X', Items.STRING)
                .unlockedBy("has_gold_coin", has(JolCraftItems.GOLD_COIN.get())).save(output, "coin_pouch");

        modShaped(RecipeCategory.MISC, JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MUFFHORN_FUR.get())
                .unlockedBy("has_muffhorn_fur", has(JolCraftItems.MUFFHORN_FUR.get())).save(output, "muffhorn_fur_block");

        modShapeless(RecipeCategory.MISC, JolCraftItems.MUFFHORN_FUR.get(), 4)
                .requires(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                .unlockedBy("has_muffhorn_fur", has(JolCraftItems.MUFFHORN_FUR.get())).save(output, "muffhorn_fur");

        modShaped(RecipeCategory.MISC, JolCraftItems.PARCHMENT.get())
                .pattern("B")
                .pattern("B")
                .pattern("B")
                .define('B', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER)).save(output, "parchment");

        modShaped(RecipeCategory.MISC, JolCraftItems.CONTRACT_BLANK.get())
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER)).save(output, "contract_blank");

        modShapeless(RecipeCategory.MISC, JolCraftItems.CONTRACT_WRITTEN.get())
                .requires(JolCraftItems.CONTRACT_BLANK.get())
                .requires(JolCraftTags.Items.INK_AND_QUILLS)
                .unlockedBy("has_contract_blank", has(JolCraftItems.CONTRACT_BLANK.get())).save(output, "contract_written");

        modShapeless(RecipeCategory.MISC, JolCraftItems.CONTRACT_GUILDMASTER.get())
                .requires(JolCraftItems.GUILD_SIGIL.get())
                .requires(JolCraftItems.CONTRACT_SIGNED.get())
                .unlockedBy("has_contract_signed", has(JolCraftItems.CONTRACT_SIGNED.get())).save(output, "contract_guildmaster");

        modShaped(RecipeCategory.MISC, JolCraftItems.GLASS_MUG.get())
                .pattern("B ")
                .pattern("BB")
                .pattern("B ")
                .define('B', Items.GLASS)
                .unlockedBy("has_glass", has(Items.GLASS)).save(output, "glass_mug");

        modShapeless(RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .requires(Items.INK_SAC)
                .unlockedBy("has_ink", has(Items.INK_SAC)).save(output, "quill_full");

        modShapeless(RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                .requires(JolCraftItems.QUILL_EMPTY.get())
                .requires(Items.INK_SAC)
                .unlockedBy("has_quill_empty", has(JolCraftItems.QUILL_EMPTY.get())).save(output, "quill_full_refill");

        modShapeless(RecipeCategory.MISC, JolCraftItems.QUILL_EMPTY.get())
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .unlockedBy("has_feather", has(Items.FEATHER)).save(output, "quill_empty");

        nineBlockStorageRecipes(
                RecipeCategory.MISC,
                JolCraftItems.SCRAP.get(),
                RecipeCategory.MISC,
                JolCraftItems.SCRAP_HEAP.get()
        );

        nineBlockStorageRecipes(
                RecipeCategory.MISC,
                JolCraftItems.BARLEY.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.BARLEY_BLOCK.get()
        );

        modShapeless(RecipeCategory.MISC, JolCraftBlocks.VERDANT_SOIL.get())
                .requires(Blocks.MUD)
                .requires(JolCraftItems.VERDANITE_DUST.get())
                .unlockedBy("has_verdanite_dust", has(JolCraftItems.VERDANITE_DUST.get())).save(output, "verdant_soil");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(JolCraftItems.BARLEY.get()),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        200
                ).unlockedBy("has_barley", has(JolCraftItems.BARLEY.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "barley_malt_from_smelting")));

        SimpleCookingRecipeBuilder.smoking(
                        Ingredient.of(JolCraftItems.BARLEY.get()),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        100
                ).unlockedBy("has_barley", has(JolCraftItems.BARLEY.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "barley_malt_from_smoking")));

        modShaped(RecipeCategory.MISC, JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', JolCraftItems.IMPURE_MITHRIL.get())
                .unlockedBy("has_impure_mithril", has(JolCraftItems.IMPURE_MITHRIL.get())).save(output, "deepslate_mithril_ore");

        nineBlockStorageRecipes(
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.PURE_MITHRIL_BLOCK.get()
        );

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_INGOT.get())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedBy("has_mithril_nugget", has(JolCraftItems.MITHRIL_NUGGET.get()))
                .save(output, "mithril_ingot_from_nuggets");

        modShapeless(RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 9)
                .requires(JolCraftItems.MITHRIL_INGOT.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get()))
                .save(output, "mithril_nuggets_from_ingot");

        nineBlockStorageRecipes(
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.MITHRIL_BLOCK.get()
        );

        oreBlasting(
                List.of(JolCraftItems.IMPURE_MITHRIL.get()),
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                0.7F,
                200,
                "mithril_purification_from_impure"
        );

        oreBlasting(
                List.of(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get()),
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                0.7F,
                400,
                "mithril_purification_from_ore"
        );

        oreBlasting(
                List.of(JolCraftItems.PURE_MITHRIL.get()),
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                0.7F,
                100,
                "mithril_ingot_from_blasting"
        );

        oreSmelting(
                List.of(JolCraftItems.PURE_MITHRIL.get()),
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                0.7F,
                200,
                "mithril_ingot_from_smelting"
        );

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_chainweave");

        modShapeless(RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 6)
                .requires(JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .unlockedBy("has_mithril_nugget", has(JolCraftItems.MITHRIL_NUGGET.get()))
                .save(output, "mithril_nuggets_from_chainweave");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_SWORD.get())
                .pattern("B")
                .pattern("B")
                .pattern("X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_sword");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_WARHAMMER.get())
                .pattern("BB")
                .pattern("BB")
                .pattern(" X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_warhammer_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_WARHAMMER.get())
                .pattern("BB")
                .pattern("BB")
                .pattern("X ")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_warhammer_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_PICKAXE.get())
                .pattern("BBB")
                .pattern(" X ")
                .pattern(" X ")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_pickaxe");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_SHOVEL.get())
                .pattern("B")
                .pattern("X")
                .pattern("X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_shovel");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_AXE.get())
                .pattern("BB")
                .pattern("BX")
                .pattern(" X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_axe_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_AXE.get())
                .pattern("BB")
                .pattern("XB")
                .pattern("X ")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_axe_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_HOE.get())
                .pattern("BB")
                .pattern(" X")
                .pattern(" X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_hoe_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_HOE.get())
                .pattern("BB")
                .pattern("X ")
                .pattern("X ")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_hoe_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_HELMET.get())
                .pattern("BBB")
                .pattern("X X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_helmet");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_CHESTPLATE.get())
                .pattern("B B")
                .pattern("XXX")
                .pattern("XXX")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_chestplate");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_LEGGINGS.get())
                .pattern("BBB")
                .pattern("X X")
                .pattern("X X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_leggings");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_BOOTS.get())
                .pattern("B B")
                .pattern("B B")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_boots");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PLATE.get())
                .pattern("BXB")
                .define('B', Items.IRON_INGOT)
                .define('X', JolCraftItems.DEEPSLATE_BULBS.get())
                .unlockedBy("has_deepslate_bulbs", has(JolCraftItems.DEEPSLATE_BULBS.get())).save(output, "deepslate_plate_from_bulbs");

        nineBlockStorageRecipes(
                RecipeCategory.MISC,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get()
        );

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_ROD.get(), 4)
                .pattern("B")
                .pattern("B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_rod");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_SWORD.get())
                .pattern("B")
                .pattern("B")
                .pattern("X")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_sword");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_WARHAMMER.get())
                .pattern("BB")
                .pattern("BB")
                .pattern(" X")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_warhammer_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_WARHAMMER.get())
                .pattern("BB")
                .pattern("BB")
                .pattern("X ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_warhammer_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PICKAXE.get())
                .pattern("BBB")
                .pattern(" X ")
                .pattern(" X ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_pickaxe");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_SHOVEL.get())
                .pattern("B")
                .pattern("X")
                .pattern("X")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_shovel");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_AXE.get())
                .pattern("BB")
                .pattern("BX")
                .pattern(" X")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_axe_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_AXE.get())
                .pattern("BB")
                .pattern("XB")
                .pattern("X ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_axe_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_HOE.get())
                .pattern("BB")
                .pattern(" X")
                .pattern(" X")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_hoe_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_HOE.get())
                .pattern("BB")
                .pattern("X ")
                .pattern("X ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_hoe_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_HELMET.get())
                .pattern("BBB")
                .pattern("B B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_helmet");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_CHESTPLATE.get())
                .pattern("B B")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_chestplate");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_LEGGINGS.get())
                .pattern("BBB")
                .pattern("B B")
                .pattern("B B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_leggings");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_BOOTS.get())
                .pattern("B B")
                .pattern("B B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_boots");

        modShaped(RecipeCategory.MISC, JolCraftItems.STRONGBOX_ITEM.get())
                .pattern("BXB")
                .pattern("X X")
                .pattern("BXB")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Items.DEEPSLATE_TILES)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "strongbox");

        modShaped(RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                .pattern("  B")
                .pattern(" B ")
                .pattern("B  ")
                .define('B', Items.IRON_NUGGET)
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET)).save(output, "lockpick_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                .pattern("B  ")
                .pattern(" B ")
                .pattern("  B")
                .define('B', Items.IRON_NUGGET)
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET)).save(output, "lockpick_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get())
                .pattern("B")
                .pattern("X")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_artisan_hammer");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_ARTISAN_HAMMER.get())
                .pattern("B")
                .pattern("X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_artisan_hammer");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_CHISEL.get())
                .pattern(" B")
                .pattern("X ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_chisel_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_CHISEL.get())
                .pattern("B ")
                .pattern(" X")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_chisel_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_CHISEL.get())
                .pattern(" B")
                .pattern("X ")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_chisel_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_CHISEL.get())
                .pattern("B ")
                .pattern(" X")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_chisel_left");

        modShapeless(RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.COAL)
                .unlockedBy("has_muffhorn_milk_bucket", has(JolCraftItems.MUFFHORN_MILK_BUCKET.get())).save(output, "inverix_coal");

        modShapeless(RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.CHARCOAL)
                .unlockedBy("has_muffhorn_milk_bucket", has(JolCraftItems.MUFFHORN_MILK_BUCKET.get())).save(output, "inverix_charcoal");

        modShaped(RecipeCategory.MISC, JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), 2)
                .pattern("BXB")
                .pattern("BAB")
                .pattern("BBB")
                .define('B', Items.DIAMOND)
                .define('X', JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .define('A', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_forge_armor_trim_smithing_template", has(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get())).save(output, "forge_armor_trim_smithing_template");

        customTrimTemplates().forEach(trim ->
                trimSmithing(trim.template(), trim.id())
        );

        allBonusTrimTemplates().forEach(trim ->
                bonusTrimSmithing(trim.template(), trim.id())
        );

        modShaped(RecipeCategory.MISC, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .pattern(" B ")
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output);

        for (int i = 0; i < DYES.size(); i++) {
            Item dyeItem = DYES.get(i);
            DyeColor dyeColor = DyeColor.values()[i];
            int colorInt = dyeColor.getFireworkColor();

            ItemStack dyedCompass = new ItemStack(
                    JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get().builtInRegistryHolder(),
                    1,
                    DataComponentPatch.builder().set(DataComponents.DYED_COLOR, new DyedItemColor(colorInt, true)).build()
            );

            modShapeless(RecipeCategory.MISC, dyedCompass.getItem())
                    .requires(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                    .requires(dyeItem)
                    .unlockedBy("has_empty_deepslate_compass", has(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()))
                    .unlockedBy("has_" + dyeColor.getName() + "_dye", has(dyeItem))
                    .save(this.output, "jolcraft:empty_deepslate_compass_" + dyeColor.getName());
        }

        modShapeless(RecipeCategory.MISC, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .requires(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .unlockedBy("has_empty_deepslate_compass", has(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()))
                .save(this.output, "jolcraft:empty_deepslate_compass_remove_dye");

        for (int i = 0; i < DYES.size(); i++) {
            Item dyeItem = DYES.get(i);
            DyeColor dyeColor = DyeColor.values()[i];
            int colorInt = dyeColor.getFireworkColor();

            ItemStack dyedCompass = new ItemStack(
                    JolCraftItems.DEEPSLATE_COMPASS.get().builtInRegistryHolder(),
                    1,
                    DataComponentPatch.builder().set(DataComponents.DYED_COLOR, new DyedItemColor(colorInt, true)).build()
            );

            modShapeless(RecipeCategory.MISC, dyedCompass.getItem())
                    .requires(JolCraftItems.DEEPSLATE_COMPASS.get())
                    .requires(dyeItem)
                    .unlockedBy("has_deepslate_compass", has(JolCraftItems.DEEPSLATE_COMPASS.get()))
                    .unlockedBy("has_" + dyeColor.getName() + "_dye", has(dyeItem))
                    .save(this.output, "jolcraft:deepslate_compass_" + dyeColor.getName());
        }

        modShapeless(RecipeCategory.MISC, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .requires(JolCraftItems.DEEPSLATE_COMPASS.get())
                .unlockedBy("has_deepslate_compass", has(JolCraftItems.DEEPSLATE_COMPASS.get()))
                .save(this.output, "jolcraft:deepslate_compass_remove_dial");



    }

    protected void bonusTrimSmithing(Item templateItem, ResourceKey<Recipe<?>> key) {
        JolSmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        tag(ItemTags.TRIMMABLE_ARMOR),
                        tag(JolCraftTags.Items.BONUS_TRIM_MATERIALS),
                        RecipeCategory.MISC
                )
                .unlocks("has_bonus_trim_material", has(JolCraftTags.Items.BONUS_TRIM_MATERIALS))
                .save(output, key);
    }

    public record TrimTemplate(Item template, ResourceKey<Recipe<?>> id) { }

    public static List<TrimTemplate> customTrimTemplates() {
        return List.of(
                new TrimTemplate(
                        JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                        ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "smithing_trim_forge"))
                )
        );
    }

    public static List<TrimTemplate> allBonusTrimTemplates() {
        List<TrimTemplate> vanillaTemplates = VanillaRecipeProvider.smithingTrims()
                .map(vanillaTrim -> {
                    ResourceLocation vanillaId = vanillaTrim.id().location();
                    ResourceLocation newId = ResourceLocation.fromNamespaceAndPath(
                            JolCraft.MOD_ID,
                            "bonus_" + vanillaId.getPath()
                    );
                    ResourceKey<Recipe<?>> newKey = ResourceKey.create(Registries.RECIPE, newId);
                    return new TrimTemplate(vanillaTrim.template(), newKey);
                })
                .toList();

        List<TrimTemplate> customTemplates = List.of(
                new TrimTemplate(
                        JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                        ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "bonus_smithing_trim_forge"))
                )
        );

        List<TrimTemplate> combined = new ArrayList<>(vanillaTemplates);
        combined.addAll(customTemplates);

        return combined;
    }

    public ShapedRecipeBuilder createShapedBuilder(RecipeCategory category, ItemLike result, int count) {
        return shaped(category, result, count);
    }

    protected JolCraftRecipeBuilder modShaped(RecipeCategory category, ItemLike result, int count) {
        return new JolCraftRecipeBuilder(createShapedBuilder(category, result, count), JolCraft.MOD_ID);
    }

    protected JolCraftRecipeBuilder modShaped(RecipeCategory category, ItemLike result) {
        return modShaped(category, result, 1);
    }

    protected JolCraftRecipeBuilder modShapeless(RecipeCategory category, ItemLike result, int count) {
        return new JolCraftRecipeBuilder(shapeless(category, result, count), JolCraft.MOD_ID);
    }

    protected JolCraftRecipeBuilder modShapeless(RecipeCategory category, ItemLike result) {
        return modShapeless(category, result, 1);
    }

    @Override
    protected void oreSmelting(List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String name) {
        for (ItemLike ingredient : ingredients) {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), category, result, experience, cookingTime)
                    .group(name)
                    .unlockedBy(getHasName(ingredient), this.has(ingredient))
                    .save(output,
                            ResourceKey.create(
                                    Registries.RECIPE,
                                    ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, name + "_from_smelting_" + getItemName(ingredient))
                            )
                    );
        }
    }

    @Override
    protected void oreBlasting(List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String name) {
        for (ItemLike ingredient : ingredients) {
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredient), category, result, experience, cookingTime)
                    .group(name)
                    .unlockedBy(getHasName(ingredient), this.has(ingredient))
                    .save(output,
                            ResourceKey.create(
                                    Registries.RECIPE,
                                    ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, name + "_from_blasting_" + getItemName(ingredient))
                            )
                    );
        }
    }

    protected void nineBlockStorageRecipes(
            RecipeCategory unpackedCategory,
            ItemLike unpacked,
            RecipeCategory packedCategory,
            ItemLike packed,
            String packedName,
            @Nullable String packedGroup,
            String unpackedName,
            @Nullable String unpackedGroup
    ) {
        ResourceLocation unpackedRL = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, unpackedName);
        ResourceLocation packedRL = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, packedName);

        this.shapeless(unpackedCategory, unpacked, 9)
                .requires(packed)
                .group(unpackedGroup)
                .unlockedBy(getHasName(packed), this.has(packed))
                .save(this.output, ResourceKey.create(Registries.RECIPE, unpackedRL));

        this.shaped(packedCategory, packed)
                .define('#', unpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(packedGroup)
                .unlockedBy(getHasName(unpacked), this.has(unpacked))
                .save(this.output, ResourceKey.create(Registries.RECIPE, packedRL));
    }

    private static final List<Item> DYES = List.of(
            Items.BLACK_DYE,
            Items.BLUE_DYE,
            Items.BROWN_DYE,
            Items.CYAN_DYE,
            Items.GRAY_DYE,
            Items.GREEN_DYE,
            Items.LIGHT_BLUE_DYE,
            Items.LIGHT_GRAY_DYE,
            Items.LIME_DYE,
            Items.MAGENTA_DYE,
            Items.ORANGE_DYE,
            Items.PINK_DYE,
            Items.PURPLE_DYE,
            Items.RED_DYE,
            Items.YELLOW_DYE,
            Items.WHITE_DYE
    );

}
