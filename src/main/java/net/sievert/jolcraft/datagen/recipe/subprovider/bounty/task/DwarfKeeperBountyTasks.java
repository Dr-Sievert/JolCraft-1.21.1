package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.BountyTaskRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Consumer;

public record DwarfKeeperBountyTasks(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfKeeperBountyTasks(
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
        return DwarfProfession.KEEPER.professionName();
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
                builder -> {
                    builder.collectWeighted(
                            Items.WHEAT,
                            5,
                            12,
                            4
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                builder -> {
                    builder.collectWeighted(
                            Items.WHEAT_SEEDS,
                            5,
                            12,
                            4
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                builder -> {

                    builder.collectWeighted(
                            Items.BEETROOT_SEEDS,
                            5,
                            12,
                            3
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                builder -> {

                    builder.collectWeighted(
                            Items.MELON_SEEDS,
                            5,
                            12,
                            3
                    );

                    builder.collectWeighted(
                            Items.PUMPKIN_SEEDS,
                            5,
                            12,
                            2
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                builder -> {
                    builder.collectWeighted(
                            Items.TORCHFLOWER_SEEDS,
                            5,
                            12,
                            2
                    );
                }
        );
    }

    private void emitTier(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level tier,
            @NotNull Consumer<BountyTaskRecipeBuilder> objectives
    ) {
        BountyTaskRecipeBuilder builder =
                BountyTaskRecipeBuilder.create()
                        .id(
                                tier.name()
                                        .toLowerCase(Locale.ROOT)
                        )
                        .bountyType(
                                DwarfProfession.KEEPER
                        )
                        .tier(tier)
                        .sound1(
                                SoundEvents.VILLAGER_WORK_CARTOGRAPHER
                        )
                        .sound2(
                                SoundEvents.VILLAGER_WORK_FISHERMAN
                        );

        objectives.accept(builder);

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }
}
