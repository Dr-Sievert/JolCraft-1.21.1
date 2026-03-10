package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyRewardRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfMinerBountyRewards implements RecipeSubProvider {

    @Override
    public String folder() {
        return DwarfProfession.MINER.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        geodes(executor, DwarfMerchantData.Level.NOVICE,
                4, 2, 1,
                a(),
                a(),
                a());

        geodes(executor, DwarfMerchantData.Level.APPRENTICE,
                4, 3, 1,
                a(1, 2),
                a(),
                a());

        geodes(executor, DwarfMerchantData.Level.JOURNEYMAN,
                3, 2, 1,
                a(1, 3),
                a(1, 2),
                a());

        geodes(executor, DwarfMerchantData.Level.EXPERT,
                2, 2, 1,
                a(2, 3),
                a(1, 3),
                a());

        geodes(executor, DwarfMerchantData.Level.MASTER,
                1, 2, 2,
                a(3, 4),
                a(2, 3),
                a(1, 2));
    }

    private record Amt(int min, int max) {}

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
            @NotNull RecipeEmissionExecutor executor,
            @NotNull DwarfMerchantData.Level tier,
            int smallW,
            int medW,
            int largeW,
            @NotNull Amt smallAmt,
            @NotNull Amt medAmt,
            @NotNull Amt largeAmt
    ) {
        emitTier(executor, tier, b -> {
            for (int i = 0; i < smallW; i++) {
                b.reward(give(JolCraftItems.GEODE_SMALL.get(), smallAmt));
            }

            for (int i = 0; i < medW; i++) {
                b.reward(give(JolCraftItems.GEODE_MEDIUM.get(), medAmt));
            }

            for (int i = 0; i < largeW; i++) {
                b.reward(give(JolCraftItems.GEODE_LARGE.get(), largeAmt));
            }
        });
    }

    private void emitTier(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull DwarfMerchantData.Level tier,
            @NotNull Consumer<BountyRewardRecipeBuilder> rewards
    ) {
        BountyRewardRecipeBuilder b = BountyRewardRecipeBuilder.create();

        b.bountyType(DwarfProfession.MINER)
                .tier(tier)
                .sound(SoundEvents.BASALT_BREAK);

        rewards.accept(b);

        executor.emit(b.buildValidated());
    }
}