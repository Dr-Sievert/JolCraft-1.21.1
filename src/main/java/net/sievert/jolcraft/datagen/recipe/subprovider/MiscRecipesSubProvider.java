package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.build.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MiscRecipesSubProvider implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftDictionary.MISC;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.DEEPSLATE_MORTAR_ITEM.get())
                )
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.PARCHMENT.get())
                )
                .pattern("B")
                .pattern("B")
                .pattern("B")
                .define('B', Items.PAPER)
                .unlockedByHas(Items.PAPER)
                .save(output);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.CONTRACT_BLANK.get())
                )
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', Items.PAPER)
                .unlockedByHas(Items.PAPER)
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.CONTRACT_WRITTEN.get())
                )
                .requires(JolCraftItems.CONTRACT_BLANK.get())
                .requires(JolCraftTags.Items.INK_AND_QUILLS)
                .unlockedByHas(JolCraftItems.CONTRACT_BLANK.get())
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.CONTRACT_GUILDMASTER.get())
                )
                .requires(JolCraftItems.GUILD_SIGIL.get())
                .requires(JolCraftItems.CONTRACT_SIGNED.get())
                .unlockedByHas(JolCraftItems.CONTRACT_SIGNED.get())
                .save(output);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.GLASS_MUG.get())
                )
                .pattern("B ")
                .pattern("BB")
                .pattern("B ")
                .define('B', Items.GLASS)
                .unlockedByHas(Items.GLASS)
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.QUILL_EMPTY.get())
                )
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .unlockedByHas(Items.FEATHER)
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                )
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .requires(Items.INK_SAC)
                .unlockedByHas(Items.INK_SAC)
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                )
                .requires(JolCraftItems.QUILL_EMPTY.get())
                .requires(Items.INK_SAC)
                .unlockedByHas(JolCraftItems.QUILL_EMPTY.get())
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.QUILL_FULL.getId().getPath() + "_refill")
                        )
                );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.COIN_POUCH.get())
                )
                .pattern("XBX")
                .pattern("B B")
                .pattern("BBB")
                .define('B', Items.LEATHER)
                .define('X', Items.STRING)
                .unlockedByHas(JolCraftItems.GOLD_COIN.get())
                .save(output);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.STRONGBOX_ITEM.get())
                )
                .pattern("BXB")
                .pattern("X X")
                .pattern("BXB")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Items.DEEPSLATE_TILES)
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                )
                .pattern("  B")
                .pattern(" B ")
                .pattern("B  ")
                .define('B', Items.IRON_NUGGET)
                .unlockedByHas(Items.IRON_NUGGET)
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.LOCKPICK.getId().getPath() + "_right")
                        )
                );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                )
                .pattern("B  ")
                .pattern(" B ")
                .pattern("  B")
                .define('B', Items.IRON_NUGGET)
                .unlockedByHas(Items.IRON_NUGGET)
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.LOCKPICK.getId().getPath() + "_left")
                        )
                );

        VanillaRecipeBuilder.Storage.nineBlock(
                items,
                output,
                RecipeCategory.MISC,
                JolCraftItems.SCRAP.get(),
                RecipeCategory.MISC,
                JolCraftItems.SCRAP_HEAP.get(),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftItems.SCRAP_HEAP.getId().getPath())
                ),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftItems.SCRAP.getId().getPath())
                )
        );

        VanillaRecipeBuilder.Storage.nineBlock(
                items,
                output,
                RecipeCategory.MISC,
                JolCraftItems.BARLEY.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.BARLEY_BLOCK.get(),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftBlocks.BARLEY_BLOCK.getId().getPath())
                ),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftItems.BARLEY.getId().getPath())
                )
        );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                )
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MUFFHORN_FUR.get())
                .unlockedByHas(JolCraftItems.MUFFHORN_FUR.get())
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.MUFFHORN_FUR.get(), 4)
                )
                .requires(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                .unlockedByHas(JolCraftItems.MUFFHORN_FUR.get())
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftBlocks.VERDANT_SOIL.get())
                )
                .requires(Blocks.MUD)
                .requires(JolCraftItems.VERDANITE_DUST.get())
                .unlockedByHas(JolCraftItems.VERDANITE_DUST.get())
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                )
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.COAL)
                .unlockedByHas(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.INVERIX.getId().getPath() + "_coal")
                        )
                );

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                )
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.CHARCOAL)
                .unlockedByHas(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.INVERIX.getId().getPath() + "_charcoal")
                        )
                );

        VanillaRecipeBuilder.Cooking.smelting(
                        JolCraftItems.BARLEY.get(),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        200
                )
                .unlockedByHas(JolCraftItems.BARLEY.get())
                .save(output);

        VanillaRecipeBuilder.Cooking.smoking(
                        JolCraftItems.BARLEY.get(),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        100
                )
                .unlockedByHas(JolCraftItems.BARLEY.get())
                .save(output);
    }
}