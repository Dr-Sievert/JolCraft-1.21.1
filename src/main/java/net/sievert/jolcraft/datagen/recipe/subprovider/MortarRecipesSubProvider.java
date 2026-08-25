package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.MortarRecipeBuilder;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import org.jetbrains.annotations.NotNull;

public record MortarRecipesSubProvider(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public MortarRecipesSubProvider(
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
        return JolCraftBlockIds.MORTAR;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .id("test_3")
                        .input(ItemInput.item(Items.COBBLESTONE))
                        .input(ItemInput.item(Items.DIRT))
                        .input(ItemInput.item(Items.SAND))
                        .result(ItemOutput.item(
                                LootItem.lootTableItem(
                                        Items.DIAMOND
                                )
                        ))
                        .sound(SoundOutput.sound(
                                JolCraftSounds.MORTAR_GRIND,
                                SoundSource.BLOCKS
                        ))
                        .grindingWork(100)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .id("test_2")
                        .input(ItemInput.item(Items.DIRT))
                        .input(ItemInput.item(Items.SAND))
                        .result(ItemOutput.item(
                                LootItem.lootTableItem(
                                        Items.GOLD_INGOT
                                )
                        ))
                        .sound(SoundOutput.sound(
                                JolCraftSounds.MORTAR_GRIND,
                                SoundSource.BLOCKS
                        ))
                        .grindingWork(100)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .id("test_1")
                        .input(ItemInput.item(Items.DIRT))
                        .result(ItemOutput.item(
                                LootItem.lootTableItem(
                                        Items.IRON_INGOT
                                )
                        ))
                        .sound(SoundOutput.sound(
                                JolCraftSounds.MORTAR_GRIND,
                                SoundSource.BLOCKS
                        ))
                        .grindingWork(100)
                        .toolDamage(1)
                        .buildValidated()
        );
    }
}
