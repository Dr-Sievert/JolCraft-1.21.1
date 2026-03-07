package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTier;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyType;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyTaskRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("SameParameterValue")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfMinerBountyTasks implements RecipeSubProvider {

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

        // --------------------------
        // NOVICE
        // --------------------------

        emitTier(executor, BountyTier.NOVICE, b -> {
            b.slay(EntityType.ZOMBIE, 1, 3);

            /*
            b.collect(Items.STONE, 8, 15);
            b.collect(Items.GRANITE, 8, 15);
            b.collect(Items.DIORITE, 8, 15);
            b.collect(Items.ANDESITE, 8, 15);
            b.collect(Items.TUFF, 8, 15);
             */
        });

        /*
        // APPRENTICE
        emitTier(executor, BountyTier.APPRENTICE, b -> {
            b.collect(Items.IRON_ORE, 4, 8);
            b.collect(Items.COPPER_ORE, 4, 8);
            b.collect(Items.DEEPSLATE_IRON_ORE, 4, 8);
        });

        // JOURNEYMAN
        emitTier(executor, BountyTier.JOURNEYMAN, b -> {
            b.collect(Items.GOLD_ORE, 3, 6);
            b.collect(Items.EMERALD_ORE, 2, 4);
        });

        // EXPERT
        emitTier(executor, BountyTier.EXPERT, b -> {
            b.collect(Items.DIAMOND_ORE, 1, 2);
            b.collect(Items.DEEPSLATE_DIAMOND_ORE, 1, 2);
        });

        // MASTER
        emitTier(executor, BountyTier.MASTER, b -> {
            b.collect(Items.ANCIENT_DEBRIS, 1, 1);
        });
         */
    }

    private void emitTier(
            RecipeEmissionExecutor executor,
            BountyTier tier,
            java.util.function.Consumer<BountyTaskRecipeBuilder> objectives
    ) {

        BountyTaskRecipeBuilder b = BountyTaskRecipeBuilder.create();

        b.bountyType(BountyType.MINER)
                .tier(tier)
                .result(JolCraftItems.BOUNTY_CRATE.get());

        objectives.accept(b);

        executor.emit(b.buildValidated());
    }
}