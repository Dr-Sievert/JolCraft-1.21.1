package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyRewardRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record DwarfMinerBountyRewards(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfMinerBountyRewards(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        return DwarfProfession.MINER.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        geodes(output, tracking, DwarfMerchantData.Level.NOVICE,
                4, 2, 1,
                a(),
                a(),
                a());

        geodes(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                4, 3, 1,
                a(1, 2),
                a(),
                a());

        geodes(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                3, 2, 1,
                a(1, 3),
                a(1, 2),
                a());

        geodes(output, tracking, DwarfMerchantData.Level.EXPERT,
                2, 2, 1,
                a(2, 3),
                a(1, 3),
                a());

        geodes(output, tracking, DwarfMerchantData.Level.MASTER,
                1, 2, 2,
                a(3, 4),
                a(2, 3),
                a(1, 2));
    }

    private record Amt(int min, int max) {
    }

    private static @NotNull Amt a() {
        return new Amt(1, 1);
    }

    private static @NotNull Amt a(int min, int max) {
        return new Amt(min, max);
    }

    private static @NotNull ItemOutput give(@NotNull Item item, @NotNull Amt amt) {
        return new ItemOutput(
                new ItemSpec(
                        ItemProducer.item(item),
                        new IntRange(amt.min(), amt.max())
                ),
                ItemTransforms.EMPTY
        );
    }

    private void geodes(
            @NotNull RecipeOutput output,
            JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level tier,
            int smallW,
            int medW,
            int largeW,
            @NotNull Amt smallAmt,
            @NotNull Amt medAmt,
            @NotNull Amt largeAmt
    ) {
        emitTier(output, tracking, tier, b -> {
            b.reward(give(JolCraftItems.GEODE_SMALL.get(), smallAmt), smallW);
            b.reward(give(JolCraftItems.GEODE_MEDIUM.get(), medAmt), medW);
            b.reward(give(JolCraftItems.GEODE_LARGE.get(), largeAmt), largeW);
        });
    }

    private void emitTier(
            @NotNull RecipeOutput output,
            JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level tier,
            @NotNull Consumer<BountyRewardRecipeBuilder> rewards
    ) {
        BountyRewardRecipeBuilder b = BountyRewardRecipeBuilder.create();

        b.bountyType(DwarfProfession.MINER)
                .tier(tier)
                .sound(SoundEvents.BASALT_BREAK);

        rewards.accept(b);

        emit(output, tracking, b.buildValidated());
    }
}