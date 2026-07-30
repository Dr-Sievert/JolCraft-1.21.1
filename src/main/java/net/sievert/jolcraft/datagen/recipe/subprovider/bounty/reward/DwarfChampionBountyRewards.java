package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.BountyRewardRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record DwarfChampionBountyRewards(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfChampionBountyRewards(
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
        return DwarfProfession.CHAMPION.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.NOVICE,
                builder -> builder.reward(
                        give(
                                JolCraftItems.GOLD_COIN.get(),
                                4,
                                6
                        ),
                        10
                )
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                builder -> builder.reward(
                        give(
                                JolCraftItems.GOLD_COIN.get(),
                                7,
                                10
                        ),
                        8
                )
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                builder -> builder.reward(
                        give(
                                JolCraftItems.GOLD_COIN.get(),
                                12,
                                16
                        ),
                        6
                )
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                builder -> builder.reward(
                        give(
                                JolCraftItems.GOLD_COIN.get(),
                                20,
                                27
                        ),
                        4
                )
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                builder -> builder.reward(
                        give(
                                JolCraftItems.GOLD_COIN.get(),
                                30,
                                39
                        ),
                        2
                )
        );
    }

    private static @NotNull
    LootPoolSingletonContainer.Builder<?> give(
            @NotNull Item item,
            int min,
            int max
    ) {
        return LootItem.lootTableItem(item)
                .apply(
                        SetItemCountFunction.setCount(
                                countProvider(
                                        min,
                                        max
                                )
                        )
                );
    }

    private static @NotNull NumberProvider countProvider(
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

    private void emitTier(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level tier,
            @NotNull Consumer<BountyRewardRecipeBuilder> rewards
    ) {
        BountyRewardRecipeBuilder builder =
                BountyRewardRecipeBuilder.create()
                        .bountyType(
                                DwarfProfession.CHAMPION
                        )
                        .tier(tier)
                        .sound(
                                SoundEvents.VILLAGER_WORK_FISHERMAN
                        );

        rewards.accept(builder);

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }
}

