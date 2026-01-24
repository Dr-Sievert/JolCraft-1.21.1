package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
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
import net.sievert.jolcraft.recipe.custom.FermentingCauldronRecipe;
import net.sievert.jolcraft.recipe.custom.LapidaryBenchRecipe;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

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

    @SuppressWarnings("deprecation")
    @Override
    protected void buildRecipes() {

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_MORTAR_ITEM.get())
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_mortar");


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
                .save(output, ResourceKey.create(Registries.RECIPE, JolCraft.location("barley_malt_from_smelting")));

        SimpleCookingRecipeBuilder.smoking(
                        Ingredient.of(JolCraftItems.BARLEY.get()),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        100
                ).unlockedBy("has_barley", has(JolCraftItems.BARLEY.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, JolCraft.location("barley_malt_from_smoking")));

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

        modShapeless(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PLATE.get(), 1)
                .requires(JolCraftItems.DEEPSLATE_BULBS.get())
                .requires(Items.IRON_INGOT)
                .unlockedBy("has_deepslate_bulbs", has(JolCraftItems.DEEPSLATE_BULBS.get()))
                .save(output, "deepslate_plate_from_bulbs");

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

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PESTLE.get())
                .pattern("X ")
                .pattern(" B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_pestle_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PESTLE.get())
                .pattern(" X")
                .pattern("B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_deepslate_plate", has(JolCraftItems.DEEPSLATE_PLATE.get())).save(output, "deepslate_pestle_left");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_PESTLE.get())
                .pattern("X ")
                .pattern(" B")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_pestle_right");

        modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_PESTLE.get())
                .pattern(" X")
                .pattern("B ")
                .define('B', JolCraftItems.MITHRIL_INGOT.get())
                .define('X', JolCraftItems.DEEPSLATE_ROD.get())
                .unlockedBy("has_mithril_ingot", has(JolCraftItems.MITHRIL_INGOT.get())).save(output, "mithril_pestle_left");

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

        //Lapidary Bench
        lapidary(JolCraftItems.GEODE_SMALL.get(),  LapidaryBenchRecipe.ToolType.HAMMER, JolCraftTags.Items.GEMS_UNCUT, 1, 2, 1);
        lapidary(JolCraftItems.GEODE_MEDIUM.get(), LapidaryBenchRecipe.ToolType.HAMMER, JolCraftTags.Items.GEMS_UNCUT, 2, 3, 1);
        lapidary(JolCraftItems.GEODE_LARGE.get(),  LapidaryBenchRecipe.ToolType.HAMMER, JolCraftTags.Items.GEMS_UNCUT, 3, 5, 1);
        lapidary(JolCraftItems.AEGISCORE.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.AEGISCORE_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.ASHFANG.get(),    LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.ASHFANG_DUST.get()),    1, 3, 1);
        lapidary(JolCraftItems.DEEPMARROW.get(), LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.DEEPMARROW_DUST.get()), 1, 3, 1);
        lapidary(JolCraftItems.EARTHBLOOD.get(), LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.EARTHBLOOD_DUST.get()), 1, 3, 1);
        lapidary(JolCraftItems.EMBERGLASS.get(), LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.EMBERGLASS_DUST.get()), 1, 3, 1);
        lapidary(JolCraftItems.FROSTVEIN.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.FROSTVEIN_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.GRIMSTONE.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.GRIMSTONE_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.IRONHEART.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.IRONHEART_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.LUMIERE.get(),    LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.LUMIERE_DUST.get()),    1, 3, 1);
        lapidary(JolCraftItems.MOONSHARD.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.MOONSHARD_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.RUSTAGATE.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.RUSTAGATE_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.SKYBURROW.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.SKYBURROW_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.SUNGLEAM.get(),   LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.SUNGLEAM_DUST.get()),   1, 3, 1);
        lapidary(JolCraftItems.VERDANITE.get(),  LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.VERDANITE_DUST.get()),  1, 3, 1);
        lapidary(JolCraftItems.WOECRYSTAL.get(), LapidaryBenchRecipe.ToolType.HAMMER, new ItemStack(JolCraftItems.WOECRYSTAL_DUST.get()), 1, 3, 1);
        lapidary(JolCraftItems.AEGISCORE.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.AEGISCORE_CUT.get()),  1);
        lapidary(JolCraftItems.ASHFANG.get(),    LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.ASHFANG_CUT.get()),    1);
        lapidary(JolCraftItems.DEEPMARROW.get(), LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.DEEPMARROW_CUT.get()), 1);
        lapidary(JolCraftItems.EARTHBLOOD.get(), LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.EARTHBLOOD_CUT.get()), 1);
        lapidary(JolCraftItems.EMBERGLASS.get(), LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.EMBERGLASS_CUT.get()), 1);
        lapidary(JolCraftItems.FROSTVEIN.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.FROSTVEIN_CUT.get()),  1);
        lapidary(JolCraftItems.GRIMSTONE.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.GRIMSTONE_CUT.get()),  1);
        lapidary(JolCraftItems.IRONHEART.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.IRONHEART_CUT.get()),  1);
        lapidary(JolCraftItems.LUMIERE.get(),    LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.LUMIERE_CUT.get()),    1);
        lapidary(JolCraftItems.MOONSHARD.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.MOONSHARD_CUT.get()),  1);
        lapidary(JolCraftItems.RUSTAGATE.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.RUSTAGATE_CUT.get()),  1);
        lapidary(JolCraftItems.SKYBURROW.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.SKYBURROW_CUT.get()),  1);
        lapidary(JolCraftItems.SUNGLEAM.get(),   LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.SUNGLEAM_CUT.get()),   1);
        lapidary(JolCraftItems.VERDANITE.get(),  LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.VERDANITE_CUT.get()),  1);
        lapidary(JolCraftItems.WOECRYSTAL.get(), LapidaryBenchRecipe.ToolType.CHISEL, new ItemStack(JolCraftItems.WOECRYSTAL_CUT.get()), 1);

        //Fermenting Cauldron
        fermentingFinalize(
                Items.SUGAR,
                null,
                1200,
                3,
                0x40B14A
        );

        fermentingExtract(
                Items.GLASS_BOTTLE,
                Items.SUGAR,
                new ItemStack(JolCraftItems.YEAST.get())
        );

        fermenting(
                JolCraftItems.BARLEY_MALT.get(),
                null,
                20,
                5,
                0xB16A1D
        );
        fermentingEffect(
                JolCraftItems.ASGARNIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x6B5352,
                MobEffects.HEALTH_BOOST,
                6000,
                0
        );

        fermentingEffect(
                JolCraftItems.DUSKHOLD_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x5F5864,
                MobEffects.NIGHT_VISION,
                6000,
                0
        );

        fermentingEffect(
                JolCraftItems.KRANDONIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x526B69,
                MobEffects.DAMAGE_BOOST,
                6000,
                0
        );

        fermentingEffect(
                JolCraftItems.YANILLIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x2B4318,
                MobEffects.MOVEMENT_SPEED,
                6000,
                0
        );

        fermentingFinalize(
                JolCraftItems.YEAST.get(),
                JolCraftTags.Items.HOPS,
                6000,
                60,
                0x9A652B
        );

        fermentingExtract(
                JolCraftItems.GLASS_MUG.get(),
                JolCraftItems.YEAST.get(),
                new ItemStack(JolCraftItems.DWARVEN_BREW.get())
        );
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
                        ResourceKey.create(Registries.RECIPE, JolCraft.location("smithing_trim_forge"))
                )
        );
    }

    public static List<TrimTemplate> allBonusTrimTemplates() {
        List<TrimTemplate> vanillaTemplates = VanillaRecipeProvider.smithingTrims()
                .map(vanillaTrim -> {
                    ResourceLocation vanillaId = vanillaTrim.id().location();
                    ResourceLocation newId = JolCraft.location("bonus_" + vanillaId.getPath());
                    ResourceKey<Recipe<?>> newKey = ResourceKey.create(Registries.RECIPE, newId);
                    return new TrimTemplate(vanillaTrim.template(), newKey);
                })
                .toList();

        List<TrimTemplate> customTemplates = List.of(
                new TrimTemplate(
                        JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                        ResourceKey.create(Registries.RECIPE, JolCraft.location("bonus_smithing_trim_forge"))
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
                                    JolCraft.location("_from_smelting_" + getItemName(ingredient))
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
                                    JolCraft.location("_from_blasting_" + getItemName(ingredient))
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
        ResourceLocation unpackedRL = JolCraft.location(unpackedName);
        ResourceLocation packedRL = JolCraft.location(packedName);

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

    private void lapidary(
            ItemLike input,
            LapidaryBenchRecipe.ToolType toolType,
            ItemStack result,
            int xp
    ) {
        lapidary(input, toolType, result, result.getCount(), result.getCount(), xp);
    }

    private void lapidary(
            ItemLike input,
            LapidaryBenchRecipe.ToolType toolType,
            ItemStack result,
            int minCount,
            int maxCount,
            int xp
    ) {
        registerLapidary(
                input,
                toolType,
                minCount,
                maxCount,
                xp,
                inputName -> switch (toolType) {
                    case HAMMER -> inputName + "_to_dust";
                    case CHISEL -> "cut_" + inputName;
                },
                () -> new LapidaryBenchRecipe(
                        Ingredient.of(input),
                        toolType,
                        result,
                        minCount,
                        maxCount,
                        xp
                )
        );
    }

    private void lapidary(
            ItemLike input,
            LapidaryBenchRecipe.ToolType toolType,
            TagKey<Item> resultTag,
            int minCount,
            int maxCount,
            int xp
    ) {
        registerLapidary(
                input,
                toolType,
                minCount,
                maxCount,
                xp,
                inputName -> {
                    String tagName = resultTag.location().getPath();

                    return switch (toolType) {
                        case HAMMER -> inputName + "_to_random_" + tagName;
                        case CHISEL -> "cut_" + inputName;
                    };
                },
                () -> new LapidaryBenchRecipe(
                        Ingredient.of(input),
                        toolType,
                        resultTag,
                        minCount,
                        maxCount,
                        xp
                )
        );
    }

    private void registerLapidary(
            ItemLike input,
            LapidaryBenchRecipe.ToolType toolType,
            int minCount,
            int maxCount,
            int xp,
            Function<String, String> idPathFn,
            Supplier<LapidaryBenchRecipe> recipeFactory
    ) {
        String inputName = BuiltInRegistries.ITEM.getKey(input.asItem()).getPath();
        String idPath = idPathFn.apply(inputName);

        ResourceLocation id = JolCraft.location("lapidary_bench/" + idPath);
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);

        LapidaryBenchRecipe recipe = recipeFactory.get();

        AdvancementHolder advancement = this.output.advancement()
                .addCriterion(getHasName(input), this.has(input))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .build(JolCraft.location("recipes/lapidary_bench/" + idPath));

        this.output.accept(key, recipe, advancement);
    }

    private void fermenting(
            ItemLike ingredient,
            @Nullable ItemLike validStateItem,
            int brewTicks,
            int bubbleTicks,
            int colorRgb
    ) {
        registerFermenting(
                ingredient,
                validStateItem == null ? null : Ingredient.of(validStateItem),
                null,
                validStateItem,
                brewTicks,
                bubbleTicks,
                colorRgb,
                null,
                false,
                null
        );
    }

    private void fermentingFinalize(
            ItemLike ingredient,
            @Nullable TagKey<Item> validStatesTag,
            int brewTicks,
            int bubbleTicks,
            int colorRgb
    ) {
        registerFermenting(
                ingredient,
                validStatesTag == null ? null : ingredientFromTag(validStatesTag),
                validStatesTag,
                null,
                brewTicks,
                bubbleTicks,
                colorRgb,
                null,
                true,
                null
        );
    }

    private void fermentingEffect(
            ItemLike ingredient,
            @Nullable TagKey<Item> validStatesTag,
            int brewTicks,
            int bubbleTicks,
            int colorRgb,
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        registerFermenting(
                ingredient,
                validStatesTag == null ? null : ingredientFromTag(validStatesTag),
                validStatesTag,
                null,
                brewTicks,
                bubbleTicks,
                colorRgb,
                FermentingCauldronRecipe.EffectData.fromHolder(effect, duration, amplifier),
                false,
                null
        );
    }

    private void fermentingExtract(
            ItemLike extractor,
            @Nullable ItemLike validStateItem,
            ItemStack result
    ) {
        registerFermenting(
                extractor,
                validStateItem == null ? null : Ingredient.of(validStateItem),
                null,
                validStateItem,
                1,
                1,
                0xFFFFFF,
                null,
                false,
                result
        );
    }

    private void registerFermenting(
            ItemLike ingredient,
            @Nullable Ingredient validStates,
            @Nullable TagKey<Item> validStatesTag,
            @Nullable ItemLike validStatesItem,
            int brewTicks,
            int bubbleTicks,
            int colorRgb,
            @Nullable FermentingCauldronRecipe.EffectData effect,
            boolean finalize,
            @Nullable ItemStack extract
    ) {
        String ingredientName = BuiltInRegistries.ITEM.getKey(ingredient.asItem()).getPath();
        String statesName = statesPart(validStatesTag, validStatesItem);

        boolean isExtract = extract != null && !extract.isEmpty();

        String idPath = ingredientName
                + "_in_" + statesName
                + (isExtract ? "_extract" : "")
                + (finalize ? "_finalize" : "");

        ResourceLocation id = JolCraft.location("fermenting_cauldron/" + idPath);
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);

        int colorArgb = 0xFF000000 | (colorRgb & 0xFFFFFF);

        FermentingCauldronRecipe recipe = new FermentingCauldronRecipe(
                Ingredient.of(ingredient),
                validStates,
                brewTicks,
                bubbleTicks,
                colorArgb,
                effect,
                finalize,
                isExtract ? extract.copy() : null
        );

        AdvancementHolder advancement = this.output.advancement()
                .addCriterion(getHasName(ingredient), this.has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .build(JolCraft.location("recipes/fermenting_cauldron/" + idPath));

        this.output.accept(key, recipe, advancement);
    }

    private Ingredient ingredientFromTag(TagKey<Item> tag) {
        HolderLookup.RegistryLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);
        HolderSet.Named<Item> set = items.getOrThrow(tag);
        return Ingredient.of(set);
    }

    private static String statesPart(@Nullable TagKey<Item> tag, @Nullable ItemLike item) {
        if (tag != null)  return tag.location().getPath();
        if (item != null) return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
        return "water_cauldron";
    }
}
