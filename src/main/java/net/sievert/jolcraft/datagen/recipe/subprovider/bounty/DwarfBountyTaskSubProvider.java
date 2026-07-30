package net.sievert.jolcraft.datagen.recipe.subprovider.bounty;

import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.DwarfChampionBountyTasks;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.DwarfKeeperBountyTasks;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.DwarfMerchantBountyTasks;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.DwarfMinerBountyTasks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DwarfBountyTaskSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfBountyTaskSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        return JolCraftDictionary.TASK;
    }

    @Override
    public @NotNull List<? extends JolCraftSubDataProvider<RecipeOutput>> subProviders() {
        return List.of(
                new DwarfChampionBountyTasks(this),
                new DwarfKeeperBountyTasks(this),
                new DwarfMerchantBountyTasks(this),
                new DwarfMinerBountyTasks(this)
        );
    }
}
