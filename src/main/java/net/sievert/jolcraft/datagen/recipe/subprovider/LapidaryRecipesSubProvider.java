package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.LapidaryBenchRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.item.selector.ItemIngredientBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public final class LapidaryRecipesSubProvider implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftBlockIds.LAPIDARY_BENCH;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        // ------------------------------------------------------------
        // HAMMER → random uncut gems (from geodes)
        // ------------------------------------------------------------

        hammerToTag(executor, JolCraftItems.GEODE_SMALL.get(), JolCraftTags.Items.GEMS_UNCUT, 1, 2, 1);
        hammerToTag(executor, JolCraftItems.GEODE_MEDIUM.get(), JolCraftTags.Items.GEMS_UNCUT, 2, 3, 1);
        hammerToTag(executor, JolCraftItems.GEODE_LARGE.get(), JolCraftTags.Items.GEMS_UNCUT, 3, 5, 1);

        // ------------------------------------------------------------
        // HAMMER → dust
        // ------------------------------------------------------------

        hammerToItem(executor, JolCraftItems.AEGISCORE.get(), JolCraftItems.AEGISCORE_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.ASHFANG.get(), JolCraftItems.ASHFANG_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.DEEPMARROW.get(), JolCraftItems.DEEPMARROW_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.EARTHBLOOD.get(), JolCraftItems.EARTHBLOOD_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.EMBERGLASS.get(), JolCraftItems.EMBERGLASS_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.FROSTVEIN.get(), JolCraftItems.FROSTVEIN_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.GRIMSTONE.get(), JolCraftItems.GRIMSTONE_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.IRONHEART.get(), JolCraftItems.IRONHEART_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.LUMIERE.get(), JolCraftItems.LUMIERE_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.MOONSHARD.get(), JolCraftItems.MOONSHARD_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.RUSTAGATE.get(), JolCraftItems.RUSTAGATE_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.SKYBURROW.get(), JolCraftItems.SKYBURROW_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.SUNGLEAM.get(), JolCraftItems.SUNGLEAM_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.VERDANITE.get(), JolCraftItems.VERDANITE_DUST.get(), 1, 3, 1);
        hammerToItem(executor, JolCraftItems.WOECRYSTAL.get(), JolCraftItems.WOECRYSTAL_DUST.get(), 1, 3, 1);

        // ------------------------------------------------------------
        // CHISEL → cut gems
        // ------------------------------------------------------------

        chiselToItem(executor, JolCraftItems.AEGISCORE.get(), JolCraftItems.AEGISCORE_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.ASHFANG.get(), JolCraftItems.ASHFANG_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.DEEPMARROW.get(), JolCraftItems.DEEPMARROW_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.EARTHBLOOD.get(), JolCraftItems.EARTHBLOOD_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.EMBERGLASS.get(), JolCraftItems.EMBERGLASS_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.FROSTVEIN.get(), JolCraftItems.FROSTVEIN_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.GRIMSTONE.get(), JolCraftItems.GRIMSTONE_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.IRONHEART.get(), JolCraftItems.IRONHEART_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.LUMIERE.get(), JolCraftItems.LUMIERE_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.MOONSHARD.get(), JolCraftItems.MOONSHARD_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.RUSTAGATE.get(), JolCraftItems.RUSTAGATE_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.SKYBURROW.get(), JolCraftItems.SKYBURROW_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.SUNGLEAM.get(), JolCraftItems.SUNGLEAM_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.VERDANITE.get(), JolCraftItems.VERDANITE_CUT.get(), 1);
        chiselToItem(executor, JolCraftItems.WOECRYSTAL.get(), JolCraftItems.WOECRYSTAL_CUT.get(), 1);
    }

    private static void hammerToItem(
            RecipeEmissionExecutor executor,
            ItemLike input,
            ItemLike dust,
            int minCount,
            int maxCount,
            int xp
    ) {
        executor.emit(
                LapidaryBenchRecipeBuilder.create()
                        .input(
                                ItemInputBuilder.create()
                                        .item(input)
                                        .count(IntRange.ONE)
                                        .build()
                        )
                        .tool(hammerTool())
                        .result(itemResult(dust, minCount, maxCount))
                        .sound(hammerGemCrushSound())
                        .xp(IntRange.fixed(xp))
                        .toolDamage(IntRange.ONE)
                        .buildValidated()
        );
    }

    private static void hammerToTag(
            RecipeEmissionExecutor executor,
            ItemLike input,
            TagKey<Item> resultTag,
            int minCount,
            int maxCount,
            int xp
    ) {
        executor.emit(
                LapidaryBenchRecipeBuilder.create()
                        .input(
                                ItemInputBuilder.create()
                                        .item(input)
                                        .count(IntRange.ONE)
                                        .build()
                        )
                        .tool(hammerTool())
                        .result(tagResult(resultTag, minCount, maxCount))
                        .sound(hammerGeodeBreakSound())
                        .xp(IntRange.fixed(xp))
                        .toolDamage(IntRange.ONE)
                        .buildValidated()
        );
    }

    private static void chiselToItem(
            RecipeEmissionExecutor executor,
            ItemLike input,
            ItemLike cutGem,
            int xp
    ) {
        executor.emit(
                LapidaryBenchRecipeBuilder.create()
                        .input(
                                ItemInputBuilder.create()
                                        .item(input)
                                        .count(IntRange.ONE)
                                        .build()
                        )
                        .tool(chiselTool())
                        .result(itemResult(cutGem, 1, 1))
                        .sound(chiselGemCutSound())
                        .xp(IntRange.fixed(xp))
                        .toolDamage(IntRange.ONE)
                        .buildValidated()
        );
    }

    private static ItemSelector hammerTool() {
        return ItemSelector.of(
                ItemIngredientBuilder.create()
                        .tag(JolCraftTags.Items.ARTISAN_HAMMERS)
                        .build()
        );
    }

    private static ItemSelector chiselTool() {
        return ItemSelector.of(
                ItemIngredientBuilder.create()
                        .tag(JolCraftTags.Items.CHISELS)
                        .build()
        );
    }

    private static SoundOutput hammerGeodeBreakSound() {
        return SoundOutput.of(SoundEvents.DEEPSLATE_BREAK, 0.8F, 1.5F);
    }

    private static SoundOutput hammerGemCrushSound() {
        return SoundOutput.of(SoundEvents.AMETHYST_BLOCK_BREAK, 0.8F, 1.5F);
    }

    private static SoundOutput chiselGemCutSound() {
        return SoundOutput.of(JolCraftSounds.GEM_CUT.get(), 0.8F, 1.0F);
    }

    private static ItemOutput itemResult(ItemLike item, int minCount, int maxCount) {
        return new ItemOutput(
                new ItemSpec(
                        ItemProducer.item(item.asItem()),
                        new IntRange(minCount, maxCount)
                ),
                ItemTransforms.EMPTY
        );
    }

    private static ItemOutput tagResult(TagKey<Item> tag, int minCount, int maxCount) {
        return new ItemOutput(
                new ItemSpec(
                        ItemProducer.tag(tag),
                        new IntRange(minCount, maxCount)
                ),
                ItemTransforms.EMPTY
        );
    }
}