package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyRewardRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record DwarfMerchantBountyRewards(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfMerchantBountyRewards(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        return DwarfProfession.MERCHANT.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        emitTier(output, tracking, DwarfMerchantData.Level.NOVICE, b -> {
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.GOLD_COIN.get(), 4, 6)
                    .build(), 10);

            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.RESTOCK_CRATE.get())
                    .build(), 2);

            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.REROLL_CRATE.get())
                    .build(), 1);
        });

        emitTier(output, tracking, DwarfMerchantData.Level.APPRENTICE, b -> {
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.GOLD_COIN.get(), 7, 10)
                    .build(), 8);

            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.RESTOCK_CRATE.get())
                    .build(), 2);

            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.REROLL_CRATE.get())
                    .build(), 1);
        });

        emitTier(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, b -> {
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.GOLD_COIN.get(), 12, 16)
                    .build(), 6);
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.RESTOCK_CRATE.get())
                    .build(), 2);
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.REROLL_CRATE.get())
                    .build(), 1);
        });

        emitTier(output, tracking, DwarfMerchantData.Level.EXPERT, b -> {
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.GOLD_COIN.get(), 20, 27)
                    .build(), 4);
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.RESTOCK_CRATE.get())
                    .build(), 2);
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.REROLL_CRATE.get())
                    .build(), 1);
        });

        emitTier(output, tracking, DwarfMerchantData.Level.MASTER, b -> {
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.GOLD_COIN.get(), 30, 39)
                    .build(), 2);
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.RESTOCK_CRATE.get())
                    .build(), 2);
            b.reward(ItemOutputBuilder.create()
                    .result(JolCraftItems.REROLL_CRATE.get())
                    .build(), 1);
        });
    }

    private void emitTier(
            @NotNull RecipeOutput output,
            JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level tier,
            @NotNull Consumer<BountyRewardRecipeBuilder> rewards
    ) {
        BountyRewardRecipeBuilder b = BountyRewardRecipeBuilder.create();

        b.bountyType(DwarfProfession.MERCHANT)
                .tier(tier)
                .sound(SoundEvents.VILLAGER_WORK_FISHERMAN);

        rewards.accept(b);

        emit(output, tracking, b.buildValidated());
    }
}