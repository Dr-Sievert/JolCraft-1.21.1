package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyTaskRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@SuppressWarnings("SameParameterValue")
public record DwarfMinerBountyTasks(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfMinerBountyTasks(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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

        emitBountyTier(output, tracking, DwarfMerchantData.Level.NOVICE, b -> {
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
        emitTier(output, tracking, DwarfMerchantData.Level.APPRENTICE, b -> {
            b.collect(Items.IRON_ORE, 4, 8, 4);
            b.collect(Items.COPPER_ORE, 4, 8, 4);
            b.collect(Items.DEEPSLATE_IRON_ORE, 4, 8, 3);
        });

        emitTier(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, b -> {
            b.collect(Items.GOLD_ORE, 3, 6, 3);
            b.collect(Items.EMERALD_ORE, 2, 4, 2);
        });

        emitTier(output, tracking, DwarfMerchantData.Level.EXPERT, b -> {
            b.collect(Items.DIAMOND_ORE, 1, 2, 2);
            b.collect(Items.DEEPSLATE_DIAMOND_ORE, 1, 2, 2);
        });

        emitTier(output, tracking, DwarfMerchantData.Level.MASTER, b -> {
            b.collect(Items.ANCIENT_DEBRIS, 1, 1, 1);
        });
         */
    }

    private void emitTier(
            RecipeOutput output,
            JolCraftDataTracking tracking,
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

        emit(output, tracking, b.buildValidated());
    }

    private void emitBountyTier(
            RecipeOutput output,
            JolCraftDataTracking tracking,
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

        emit(output, tracking, b.buildValidated());
    }
}