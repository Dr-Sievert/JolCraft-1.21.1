package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.LapidaryBenchRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.input.ItemInput;
import net.sievert.jolcraft.world.recipe.output.ItemOutput;
import net.sievert.jolcraft.world.recipe.output.ItemOutputs;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import net.sievert.jolcraft.world.recipe.output.SoundOutputs;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record LapidaryRecipesSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public LapidaryRecipesSubProvider(
            @NotNull JolCraftDataProvider<RecipeOutput> parent
    ) {
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
        return JolCraftBlockIds.LAPIDARY_BENCH;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        // ------------------------------------------------------------
        // HAMMER → random uncut gems from geodes
        // ------------------------------------------------------------

        hammerToTag(
                output,
                tracking,
                JolCraftItems.GEODE_SMALL.get(),
                JolCraftTags.Items.GEMS_UNCUT,
                1,
                2,
                1
        );

        hammerToTag(
                output,
                tracking,
                JolCraftItems.GEODE_MEDIUM.get(),
                JolCraftTags.Items.GEMS_UNCUT,
                2,
                3,
                1
        );

        hammerToTag(
                output,
                tracking,
                JolCraftItems.GEODE_LARGE.get(),
                JolCraftTags.Items.GEMS_UNCUT,
                3,
                5,
                1
        );

        // ------------------------------------------------------------
        // HAMMER → dust
        // ------------------------------------------------------------

        hammerToItem(output, tracking, JolCraftItems.AEGISCORE.get(), JolCraftItems.AEGISCORE_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.ASHFANG.get(), JolCraftItems.ASHFANG_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.DEEPMARROW.get(), JolCraftItems.DEEPMARROW_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.EARTHBLOOD.get(), JolCraftItems.EARTHBLOOD_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.EMBERGLASS.get(), JolCraftItems.EMBERGLASS_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.FROSTVEIN.get(), JolCraftItems.FROSTVEIN_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.GRIMSTONE.get(), JolCraftItems.GRIMSTONE_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.IRONHEART.get(), JolCraftItems.IRONHEART_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.LUMIERE.get(), JolCraftItems.LUMIERE_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.MOONSHARD.get(), JolCraftItems.MOONSHARD_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.RUSTAGATE.get(), JolCraftItems.RUSTAGATE_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.SKYBURROW.get(), JolCraftItems.SKYBURROW_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.SUNGLEAM.get(), JolCraftItems.SUNGLEAM_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.VERDANITE.get(), JolCraftItems.VERDANITE_DUST.get(), 1, 3, 1);
        hammerToItem(output, tracking, JolCraftItems.WOECRYSTAL.get(), JolCraftItems.WOECRYSTAL_DUST.get(), 1, 3, 1);

        // ------------------------------------------------------------
        // CHISEL → cut gems
        // ------------------------------------------------------------

        chiselToItem(output, tracking, JolCraftItems.AEGISCORE.get(), JolCraftItems.AEGISCORE_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.ASHFANG.get(), JolCraftItems.ASHFANG_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.DEEPMARROW.get(), JolCraftItems.DEEPMARROW_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.EARTHBLOOD.get(), JolCraftItems.EARTHBLOOD_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.EMBERGLASS.get(), JolCraftItems.EMBERGLASS_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.FROSTVEIN.get(), JolCraftItems.FROSTVEIN_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.GRIMSTONE.get(), JolCraftItems.GRIMSTONE_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.IRONHEART.get(), JolCraftItems.IRONHEART_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.LUMIERE.get(), JolCraftItems.LUMIERE_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.MOONSHARD.get(), JolCraftItems.MOONSHARD_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.RUSTAGATE.get(), JolCraftItems.RUSTAGATE_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.SKYBURROW.get(), JolCraftItems.SKYBURROW_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.SUNGLEAM.get(), JolCraftItems.SUNGLEAM_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.VERDANITE.get(), JolCraftItems.VERDANITE_CUT.get(), 1);
        chiselToItem(output, tracking, JolCraftItems.WOECRYSTAL.get(), JolCraftItems.WOECRYSTAL_CUT.get(), 1);
    }

    private void hammerToItem(
            RecipeOutput output,
            JolCraftDataTracking tracking,
            ItemLike input,
            ItemLike dust,
            int minCount,
            int maxCount,
            int xp
    ) {
        emit(
                output,
                tracking,
                LapidaryBenchRecipeBuilder.create()
                        .id(recipeId(
                                input,
                                "hammer_to",
                                dust
                        ))
                        .input(ItemInput.item(input))
                        .tool(hammerTool())
                        .result(itemResult(
                                dust,
                                minCount,
                                maxCount
                        ))
                        .sound(hammerGemCrushSound())
                        .xp(ConstantValue.exactly(xp))
                        .toolDamage(ConstantValue.exactly(1.0F))
                        .buildValidated()
        );
    }

    private void hammerToTag(
            RecipeOutput output,
            JolCraftDataTracking tracking,
            ItemLike input,
            TagKey<Item> resultTag,
            int minCount,
            int maxCount,
            int xp
    ) {
        emit(
                output,
                tracking,
                LapidaryBenchRecipeBuilder.create()
                        .id(recipeId(
                                input,
                                "hammer_to",
                                resultTag.location().getPath()
                        ))
                        .input(ItemInput.item(input))
                        .tool(hammerTool())
                        .result(tagResult(
                                resultTag,
                                minCount,
                                maxCount
                        ))
                        .sound(hammerGeodeBreakSound())
                        .xp(ConstantValue.exactly(xp))
                        .toolDamage(ConstantValue.exactly(1.0F))
                        .buildValidated()
        );
    }

    private void chiselToItem(
            RecipeOutput output,
            JolCraftDataTracking tracking,
            ItemLike input,
            ItemLike cutGem,
            int xp
    ) {
        emit(
                output,
                tracking,
                LapidaryBenchRecipeBuilder.create()
                        .id(recipeId(
                                input,
                                "chisel_to",
                                cutGem
                        ))
                        .input(ItemInput.item(input))
                        .tool(chiselTool())
                        .result(itemResult(
                                cutGem,
                                1,
                                1
                        ))
                        .sound(chiselGemCutSound())
                        .xp(ConstantValue.exactly(xp))
                        .toolDamage(ConstantValue.exactly(1.0F))
                        .buildValidated()
        );
    }
    private static ItemInput hammerTool() {
        return ItemInput.tag(
                JolCraftTags.Items.ARTISAN_HAMMERS
        );
    }

    private static ItemInput chiselTool() {
        return ItemInput.tag(
                JolCraftTags.Items.CHISELS
        );
    }

    private static SoundOutput hammerGeodeBreakSound() {
        return SoundOutputs.sound(
                SoundEvents.DEEPSLATE_BREAK,
                SoundSource.BLOCKS,
                ConstantValue.exactly(0.8F),
                ConstantValue.exactly(1.5F)
        );
    }

    private static SoundOutput hammerGemCrushSound() {
        return SoundOutputs.sound(
                SoundEvents.AMETHYST_BLOCK_BREAK,
                SoundSource.BLOCKS,
                ConstantValue.exactly(0.8F),
                ConstantValue.exactly(1.5F)
        );
    }

    private static SoundOutput chiselGemCutSound() {
        return SoundOutputs.sound(
                JolCraftSounds.GEM_CUT,
                SoundSource.BLOCKS,
                ConstantValue.exactly(0.8F),
                ConstantValue.exactly(1.0F)
        );
    }

    private static ItemOutput itemResult(
            ItemLike item,
            int minCount,
            int maxCount
    ) {
        return ItemOutputs.item(
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(
                                count(minCount, maxCount)
                        ))
        );
    }

    private static ItemOutput tagResult(
            TagKey<Item> tag,
            int minCount,
            int maxCount
    ) {
        return ItemOutputs.item(
                TagEntry.expandTag(tag)
                        .apply(SetItemCountFunction.setCount(
                                count(minCount, maxCount)
                        ))
        );
    }

    private static NumberProvider count(
            int min,
            int max
    ) {
        if (min == max) {
            return ConstantValue.exactly(min);
        }

        return UniformGenerator.between(
                min,
                max
        );
    }

    private static String recipeId(
            ItemLike input,
            String operation,
            ItemLike result
    ) {
        return recipeId(
                input,
                operation,
                BuiltInRegistries.ITEM
                        .getKey(result.asItem())
                        .getPath()
        );
    }

    private static String recipeId(
            ItemLike input,
            String operation,
            String result
    ) {
        String inputId = BuiltInRegistries.ITEM
                .getKey(input.asItem())
                .getPath();

        return inputId
                + "_"
                + operation
                + "_"
                + result.replace('/', '_');
    }
}