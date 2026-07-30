package net.sievert.jolcraft.datagen.recipe.subprovider.bounty;

import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.DwarfChampionBountyRewards;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.DwarfKeeperBountyRewards;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.DwarfMerchantBountyRewards;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.DwarfMinerBountyRewards;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DwarfBountyRewardSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfBountyRewardSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        return JolCraftDictionary.REWARD;
    }

    @Override
    public @NotNull List<? extends JolCraftSubDataProvider<RecipeOutput>> subProviders() {
        return List.of(
                new DwarfChampionBountyRewards(this),
                new DwarfKeeperBountyRewards(this),
                new DwarfMerchantBountyRewards(this),
                new DwarfMinerBountyRewards(this)
        );
    }
}
