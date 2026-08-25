package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.vanilla.ComponentPreservingShapelessRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
import org.jetbrains.annotations.NotNull;

public record MiscRecipesSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    private static final String FOLDER = JolCraftDictionary.MISC;

    public MiscRecipesSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    @Override
    public @NotNull String id() {
        return folder();
    }

    @Override
    public @NotNull String folder() {
        return FOLDER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        VanillaRecipeBuilder.Storage.nineBlock(
                lookups.items(),
                output,
                folder(),
                RecipeCategory.MISC,
                JolCraftItems.VITRIOL.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.VITRIOL_BLOCK.get()
        );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BREAD)
                )
                .pattern("BBB")
                .define('B', JolCraftItems.BARLEY)
                .unlockedByHas(JolCraftItems.BARLEY)
                .save(output, folder(), "bread_from_barley");

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.GUILD_SIGIL.get())
                )
                .requires(Items.PURPLE_DYE)
                .requires(Items.HONEYCOMB)
                .requires(JolCraftItems.GUILD_SIGIL_MOULD)
                .unlockedByHas(JolCraftItems.GUILD_SIGIL_MOULD)
                .save(output, folder(), JolCraftItems.GUILD_SIGIL.get());

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CYAN_DYE)
                )
                .requires(JolCraftBlocks.CYANELLA)
                .unlockedByHas(JolCraftBlocks.CYANELLA)
                .save(output, folder(), JolCraftStrings.underscored(JolCraftBlockIds.CYANELLA, JolCraftDictionary.DYE));

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE)
                )
                .requires(JolCraftBlocks.SKYBELL)
                .unlockedByHas(JolCraftBlocks.SKYBELL)
                .save(output, folder(), JolCraftStrings.underscored(JolCraftBlockIds.SKYBELL, JolCraftDictionary.DYE));

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftBlocks.HEARTH)
                )
                .pattern("BXB")
                .pattern("BZB")
                .pattern("B B")
                .define('B', Blocks.DEEPSLATE_TILES)
                .define('X', Blocks.DEEPSLATE_TILE_WALL)
                .define('Z', JolCraftItems.DEEPSLATE_PLATE)
                .unlockedByHas(Blocks.DEEPSLATE_TILES)
                .save(output, folder(), JolCraftBlocks.HEARTH.get());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftBlocks.LAPIDARY_BENCH)
                )
                .pattern("XBX")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', Blocks.POLISHED_DEEPSLATE)
                .define('X', JolCraftItems.DEEPSLATE_PLATE)
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE)
                .save(output, folder(), JolCraftBlocks.LAPIDARY_BENCH);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.MORTAR_ITEM.get())
                )
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output, folder(), JolCraftItems.MORTAR_ITEM.get());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.PARCHMENT.get())
                )
                .pattern("B")
                .pattern("B")
                .pattern("B")
                .define('B', Items.PAPER)
                .unlockedByHas(Items.PAPER)
                .save(output, folder(), JolCraftItems.PARCHMENT.get());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.CONTRACT_BLANK.get())
                )
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', Items.PAPER)
                .unlockedByHas(Items.PAPER)
                .save(output, folder(), JolCraftItems.CONTRACT_BLANK.get());

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.CONTRACT_WRITTEN.get())
                )
                .requires(JolCraftItems.CONTRACT_BLANK.get())
                .requires(JolCraftTags.Items.INK_AND_QUILLS)
                .unlockedByHas(JolCraftItems.CONTRACT_BLANK.get())
                .save(output, folder(), JolCraftItems.CONTRACT_WRITTEN.get());

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.CONTRACT_GUILDMASTER.get())
                )
                .requires(JolCraftItems.GUILD_SIGIL.get())
                .requires(JolCraftItems.CONTRACT_SIGNED.get())
                .unlockedByHas(JolCraftItems.CONTRACT_SIGNED.get())
                .save(output, folder(), JolCraftItems.CONTRACT_GUILDMASTER.get());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.GLASS_MUG.get(), 3)
                )
                .pattern(" B")
                .pattern("BB")
                .pattern(" B")
                .define('B', Items.GLASS)
                .unlockedByHas(Items.GLASS)
                .save(output, folder(), JolCraftItems.GLASS_MUG.getId().getPath() + "_right");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.GLASS_MUG.get(), 3)
                )
                .pattern("B ")
                .pattern("BB")
                .pattern("B ")
                .define('B', Items.GLASS)
                .unlockedByHas(Items.GLASS)
                .save(output, folder(), JolCraftItems.GLASS_MUG.getId().getPath() + "_left");

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.QUILL_EMPTY.get())
                )
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .unlockedByHas(Items.FEATHER)
                .save(output, folder(), JolCraftItems.QUILL_EMPTY.get());

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                )
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .requires(Items.INK_SAC)
                .unlockedByHas(Items.INK_SAC)
                .save(output, folder(), JolCraftItems.QUILL_FULL.getId().getPath() + "_from_glass_feather_ink");

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                )
                .requires(JolCraftItems.QUILL_EMPTY.get())
                .requires(Items.INK_SAC)
                .unlockedByHas(JolCraftItems.QUILL_EMPTY.get())
                .save(output, folder(), JolCraftItems.QUILL_FULL.getId().getPath() + "_refill");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.COIN_POUCH.get())
                )
                .pattern("XBX")
                .pattern("B B")
                .pattern("BBB")
                .define('B', Items.LEATHER)
                .define('X', Items.STRING)
                .unlockedByHas(JolCraftItems.GOLD_COIN.get())
                .save(output, folder(), JolCraftItems.COIN_POUCH.get());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.STRONGBOX_ITEM.get())
                )
                .pattern("BXB")
                .pattern("X X")
                .pattern("BXB")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Items.DEEPSLATE_TILES)
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output, folder(), JolCraftItems.STRONGBOX_ITEM.get());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                )
                .pattern("  B")
                .pattern(" B ")
                .pattern("B  ")
                .define('B', Items.IRON_NUGGET)
                .unlockedByHas(Items.IRON_NUGGET)
                .save(output, folder(), JolCraftItems.LOCKPICK.getId().getPath() + "_right");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                )
                .pattern("B  ")
                .pattern(" B ")
                .pattern("  B")
                .define('B', Items.IRON_NUGGET)
                .unlockedByHas(Items.IRON_NUGGET)
                .save(output, folder(), JolCraftItems.LOCKPICK.getId().getPath() + "_left");

        VanillaRecipeBuilder.Storage.nineBlock(
                lookups.items(),
                output,
                folder(),
                RecipeCategory.MISC,
                JolCraftItems.SCRAP.get(),
                RecipeCategory.MISC,
                JolCraftItems.SCRAP_HEAP.get()
        );

        VanillaRecipeBuilder.Storage.nineBlock(
                lookups.items(),
                output,
                folder(),
                RecipeCategory.MISC,
                JolCraftItems.BARLEY.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.BARLEY_BLOCK.get()
        );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                )
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MUFFHORN_FUR.get())
                .unlockedByHas(JolCraftItems.MUFFHORN_FUR.get())
                .save(output, folder(), JolCraftBlocks.MUFFHORN_FUR_BLOCK.get());

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.MUFFHORN_FUR.get(), 4)
                )
                .requires(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                .unlockedByHas(JolCraftItems.MUFFHORN_FUR.get())
                .save(output, folder(), JolCraftItems.MUFFHORN_FUR.getId().getPath() + "s_from_block");

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftBlocks.VERDANT_SOIL.get(), 4)
                )
                .requires(Blocks.MUD, 4)
                .requires(JolCraftItems.VERDANITE_DUST.get())
                .unlockedByHas(JolCraftItems.VERDANITE_DUST.get())
                .save(output, folder(), JolCraftBlocks.VERDANT_SOIL.get());

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                )
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.COAL)
                .unlockedByHas(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .save(output, folder(), JolCraftItems.INVERIX.getId().getPath() + "_coal");

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                )
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.CHARCOAL)
                .unlockedByHas(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .save(output, folder(), JolCraftItems.INVERIX.getId().getPath() + "_charcoal");

        yeastCulture(
                output,
                waterBottleIngredient(),
                Items.BROWN_MUSHROOM,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );

        yeastCulture(
                output,
                yeastCultureIngredient(
                        DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
                ),
                Items.FERMENTED_SPIDER_EYE,
                DwarvenBrewFluidHelper.BREWING_SPEED_1_5
        );

        yeastCulture(
                output,
                yeastCultureIngredient(
                        DwarvenBrewFluidHelper.BREWING_SPEED_1_5
                ),
                Items.NETHER_WART,
                DwarvenBrewFluidHelper.BREWING_SPEED_2_0
        );

        yeastCulture(
                output,
                yeastCultureIngredient(
                        DwarvenBrewFluidHelper.BREWING_SPEED_2_0
                ),
                JolCraftItems.EMBERGLASS_DUST.get(),
                DwarvenBrewFluidHelper.BREWING_SPEED_2_5
        );

        yeastCulture(
                output,
                yeastCultureIngredient(
                        DwarvenBrewFluidHelper.BREWING_SPEED_2_5
                ),
                Items.DRAGON_BREATH,
                DwarvenBrewFluidHelper.BREWING_SPEED_3_0
        );

        VanillaRecipeBuilder.Cooking.smelting(
                        JolCraftItems.BARLEY.get(),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        200
                )
                .unlockedByHas(JolCraftItems.BARLEY.get())
                .save(output, folder());

        VanillaRecipeBuilder.Cooking.smoking(
                        JolCraftItems.BARLEY.get(),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        100
                )
                .unlockedByHas(JolCraftItems.BARLEY.get())
                .save(output, folder());
    }

    private void yeastCulture(
            @NotNull RecipeOutput output,
            @NotNull Ingredient base,
            @NotNull ItemLike ingredient,
            float brewingSpeed
    ) {
        ComponentPreservingShapelessRecipeBuilder.create(
                        CraftingBookCategory.MISC,
                        base
                )
                .result(
                        JolCraftBrewingItems.createYeastCultureStack(
                                JolCraftItems.YEAST_CULTURE.get(),
                                brewingSpeed
                        )
                )
                .clearCopiedComponents()
                .suppressRemainder(
                        base
                )
                .ingredient(
                        Ingredient.of(
                                ingredient
                        )
                )
                .unlocks(
                        hasName(
                                ingredient
                        ),
                        hasItem(
                                ingredient
                        )
                )
                .save(
                        output,
                        JolCraft.location(
                                JolCraftStrings.slashed(
                                        folder(),
                                        JolCraftItems.YEAST_CULTURE.getId().getPath()
                                                + "_"
                                                + itemPath(ingredient)
                                )
                        )
                );
    }

    private static Ingredient waterBottleIngredient() {
        return DataComponentIngredient.of(
                false,
                PotionContents.createItemStack(
                        Items.POTION,
                        Potions.WATER
                )
        );
    }

    private static Ingredient yeastCultureIngredient(
            float brewingSpeed
    ) {
        return DataComponentIngredient.of(
                false,
                JolCraftBrewingItems.createYeastCultureStack(
                        JolCraftItems.YEAST_CULTURE.get(),
                        brewingSpeed
                )
        );
    }

    private static String hasName(
            @NotNull ItemLike item
    ) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.HAS,
                itemPath(
                        item
                )
        );
    }

    private static Criterion<?> hasItem(
            @NotNull ItemLike item
    ) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(
                item
        );
    }

    private static String itemPath(
            @NotNull ItemLike item
    ) {
        return BuiltInRegistries.ITEM
                .getKey(
                        item.asItem()
                )
                .getPath()
                .replace('/', '_');
    }

}
