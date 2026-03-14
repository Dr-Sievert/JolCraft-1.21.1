package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyTaskRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@SuppressWarnings("SameParameterValue")
public final class DwarfMinerBountyTasks implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return DwarfProfession.MINER.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    ) {

        emitBountyTier(executor, DwarfMerchantData.Level.NOVICE, b -> {
            b.slayWeighted(EntityType.ZOMBIE, 1, 3, 2);

            /*
            b.collect(Items.STONE, 8, 15, 4);
            b.collect(Items.GRANITE, 8, 15, 3);
            b.collect(Items.DIORITE, 8, 15, 3);
            b.collect(Items.ANDESITE, 8, 15, 3);
            b.collect(Items.TUFF, 8, 15, 2);
             */
        });

        /*
        emitTier(executor, DwarfMerchantData.Level.APPRENTICE, b -> {
            b.collect(Items.IRON_ORE, 4, 8, 4);
            b.collect(Items.COPPER_ORE, 4, 8, 4);
            b.collect(Items.DEEPSLATE_IRON_ORE, 4, 8, 3);
        });

        emitTier(executor, DwarfMerchantData.Level.JOURNEYMAN, b -> {
            b.collect(Items.GOLD_ORE, 3, 6, 3);
            b.collect(Items.EMERALD_ORE, 2, 4, 2);
        });

        emitTier(executor, DwarfMerchantData.Level.EXPERT, b -> {
            b.collect(Items.DIAMOND_ORE, 1, 2, 2);
            b.collect(Items.DEEPSLATE_DIAMOND_ORE, 1, 2, 2);
        });

        emitTier(executor, DwarfMerchantData.Level.MASTER, b -> {
            b.collect(Items.ANCIENT_DEBRIS, 1, 1, 1);
        });
         */
    }

    private void emitTier(
            RecipeEmissionExecutor executor,
            DwarfMerchantData.Level tier,
            Consumer<BountyTaskRecipeBuilder> objectives
    ) {

        BountyTaskRecipeBuilder b = BountyTaskRecipeBuilder.create();

        b.bountyType(DwarfProfession.MINER)
                .tier(tier)
                .result(JolCraftItems.BOUNTY_CRATE.get())
                .sound1(SoundEvents.VILLAGER_WORK_CARTOGRAPHER)
                .sound2(SoundEvents.VILLAGER_WORK_CARTOGRAPHER);

        objectives.accept(b);

        executor.emit(b.buildValidated());
    }

    private void emitBountyTier(
            RecipeEmissionExecutor executor,
            DwarfMerchantData.Level tier,
            Consumer<BountyTaskRecipeBuilder> objectives
    ) {

        BountyTaskRecipeBuilder b = BountyTaskRecipeBuilder.create();

        b.bountyType(DwarfProfession.MINER)
                .tier(tier)
                .result(JolCraftItems.BOUNTY.get())
                .sound1(SoundEvents.VILLAGER_WORK_CARTOGRAPHER)
                .sound2(SoundEvents.VILLAGER_WORK_CARTOGRAPHER);

        objectives.accept(b);

        executor.emit(b.buildValidated());
    }
}