package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.recipe.custom.LapidaryBenchRecipe;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public final class LapidaryRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    private static final String FOLDER = "lapidary_bench";

    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {

        // ------------------------------------------------------------
        // HAMMER → random uncut gems
        // ------------------------------------------------------------

        lapidaryHammer(p, JolCraftItems.GEODE_SMALL.get(),  JolCraftTags.Items.GEMS_UNCUT, 1, 2, 1);
        lapidaryHammer(p, JolCraftItems.GEODE_MEDIUM.get(), JolCraftTags.Items.GEMS_UNCUT, 2, 3, 1);
        lapidaryHammer(p, JolCraftItems.GEODE_LARGE.get(),  JolCraftTags.Items.GEMS_UNCUT, 3, 5, 1);

        // ------------------------------------------------------------
        // HAMMER → dust
        // ------------------------------------------------------------

        lapidaryHammer(p, JolCraftItems.AEGISCORE.get(),  new ItemStack(JolCraftItems.AEGISCORE_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.ASHFANG.get(),    new ItemStack(JolCraftItems.ASHFANG_DUST.get()),    1, 3, 1);
        lapidaryHammer(p, JolCraftItems.DEEPMARROW.get(), new ItemStack(JolCraftItems.DEEPMARROW_DUST.get()), 1, 3, 1);
        lapidaryHammer(p, JolCraftItems.EARTHBLOOD.get(), new ItemStack(JolCraftItems.EARTHBLOOD_DUST.get()), 1, 3, 1);
        lapidaryHammer(p, JolCraftItems.EMBERGLASS.get(), new ItemStack(JolCraftItems.EMBERGLASS_DUST.get()), 1, 3, 1);
        lapidaryHammer(p, JolCraftItems.FROSTVEIN.get(),  new ItemStack(JolCraftItems.FROSTVEIN_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.GRIMSTONE.get(),  new ItemStack(JolCraftItems.GRIMSTONE_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.IRONHEART.get(),  new ItemStack(JolCraftItems.IRONHEART_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.LUMIERE.get(),    new ItemStack(JolCraftItems.LUMIERE_DUST.get()),    1, 3, 1);
        lapidaryHammer(p, JolCraftItems.MOONSHARD.get(),  new ItemStack(JolCraftItems.MOONSHARD_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.RUSTAGATE.get(),  new ItemStack(JolCraftItems.RUSTAGATE_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.SKYBURROW.get(),  new ItemStack(JolCraftItems.SKYBURROW_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.SUNGLEAM.get(),   new ItemStack(JolCraftItems.SUNGLEAM_DUST.get()),   1, 3, 1);
        lapidaryHammer(p, JolCraftItems.VERDANITE.get(),  new ItemStack(JolCraftItems.VERDANITE_DUST.get()),  1, 3, 1);
        lapidaryHammer(p, JolCraftItems.WOECRYSTAL.get(), new ItemStack(JolCraftItems.WOECRYSTAL_DUST.get()), 1, 3, 1);

        // ------------------------------------------------------------
        // CHISEL → cut gems
        // ------------------------------------------------------------

        lapidaryChisel(p, JolCraftItems.AEGISCORE.get(),  JolCraftItems.AEGISCORE_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.ASHFANG.get(),    JolCraftItems.ASHFANG_CUT.get(),    1);
        lapidaryChisel(p, JolCraftItems.DEEPMARROW.get(), JolCraftItems.DEEPMARROW_CUT.get(), 1);
        lapidaryChisel(p, JolCraftItems.EARTHBLOOD.get(), JolCraftItems.EARTHBLOOD_CUT.get(), 1);
        lapidaryChisel(p, JolCraftItems.EMBERGLASS.get(), JolCraftItems.EMBERGLASS_CUT.get(), 1);
        lapidaryChisel(p, JolCraftItems.FROSTVEIN.get(),  JolCraftItems.FROSTVEIN_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.GRIMSTONE.get(),  JolCraftItems.GRIMSTONE_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.IRONHEART.get(),  JolCraftItems.IRONHEART_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.LUMIERE.get(),    JolCraftItems.LUMIERE_CUT.get(),    1);
        lapidaryChisel(p, JolCraftItems.MOONSHARD.get(),  JolCraftItems.MOONSHARD_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.RUSTAGATE.get(),  JolCraftItems.RUSTAGATE_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.SKYBURROW.get(),  JolCraftItems.SKYBURROW_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.SUNGLEAM.get(),   JolCraftItems.SUNGLEAM_CUT.get(),   1);
        lapidaryChisel(p, JolCraftItems.VERDANITE.get(),  JolCraftItems.VERDANITE_CUT.get(),  1);
        lapidaryChisel(p, JolCraftItems.WOECRYSTAL.get(), JolCraftItems.WOECRYSTAL_CUT.get(), 1);
    }

    public static void lapidaryHammer(
            AbstractRecipeProvider p,
            ItemLike input,
            ItemStack result,
            int minCount,
            int maxCount,
            int xp
    ) {
        register(
                p,
                input,
                LapidaryBenchRecipe.ToolType.HAMMER,
                minCount,
                maxCount,
                xp,
                name -> name + "_to_dust",
                () -> new LapidaryBenchRecipe(
                        Ingredient.of(input),
                        LapidaryBenchRecipe.ToolType.HAMMER,
                        result,
                        minCount,
                        maxCount,
                        xp
                )
        );
    }

    public static void lapidaryHammer(
            AbstractRecipeProvider p,
            ItemLike input,
            TagKey<Item> resultTag,
            int minCount,
            int maxCount,
            int xp
    ) {
        register(
                p,
                input,
                LapidaryBenchRecipe.ToolType.HAMMER,
                minCount,
                maxCount,
                xp,
                name -> name + "_to_random_" + resultTag.location().getPath(),
                () -> new LapidaryBenchRecipe(
                        Ingredient.of(input),
                        LapidaryBenchRecipe.ToolType.HAMMER,
                        resultTag,
                        minCount,
                        maxCount,
                        xp
                )
        );
    }

    public static void lapidaryChisel(
            AbstractRecipeProvider p,
            ItemLike input,
            ItemLike result,
            int xp
    ) {
        ItemStack stack = new ItemStack(result);
        register(
                p,
                input,
                LapidaryBenchRecipe.ToolType.CHISEL,
                stack.getCount(),
                stack.getCount(),
                xp,
                name -> "cut_" + name,
                () -> new LapidaryBenchRecipe(
                        Ingredient.of(input),
                        LapidaryBenchRecipe.ToolType.CHISEL,
                        stack,
                        stack.getCount(),
                        stack.getCount(),
                        xp
                )
        );
    }

    private static void register(
            AbstractRecipeProvider p,
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

        ResourceLocation id = JolCraft.location(p.inFolder(FOLDER, idPath));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);

        LapidaryBenchRecipe recipe = recipeFactory.get();

        AdvancementHolder advancement = p.out().advancement()
                .addCriterion(p.hasName(input), p.hasItem(input))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .build(JolCraft.location("recipes/" + p.inFolder(FOLDER, idPath)));

        p.out().accept(key, recipe, advancement);
    }
}