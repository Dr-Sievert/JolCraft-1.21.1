package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
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

public record DwarfChampionBountyTasks(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfChampionBountyTasks(
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
                builder -> {
                    builder.slayWeighted(
                            EntityType.ZOMBIE,
                            1,
                            3,
                            6
                    );

                    builder.slayWeighted(
                            EntityType.HUSK,
                            1,
                            3,
                            2
                    );


                    builder.slayWeighted(
                            EntityType.DROWNED,
                            1,
                            3,
                            2
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                builder -> {
                    builder.slayWeighted(
                            EntityType.SKELETON,
                            1,
                            3,
                            6
                    );

                    builder.slayWeighted(
                            EntityType.STRAY,
                            1,
                            3,
                            2
                    );

                    builder.slayWeighted(
                            EntityType.PILLAGER,
                            1,
                            3,
                            2
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                builder -> {
                    builder.slayWeighted(
                            EntityType.CREEPER,
                            1,
                            3,
                            6
                    );

                    builder.slayWeighted(
                            EntityType.BLAZE,
                            1,
                            3,
                            2
                    );

                    builder.slayWeighted(
                            EntityType.WITHER_SKELETON,
                            1,
                            3,
                            2
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                builder -> {
                    builder.slayWeighted(
                            EntityType.VINDICATOR,
                            1,
                            3,
                            1
                    );

                    builder.slayWeighted(
                            EntityType.WITHER_SKELETON,
                            1,
                            3,
                            1
                    );

                    builder.slayWeighted(
                            EntityType.PIGLIN_BRUTE,
                            1,
                            3,
                            4
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                builder -> {
                    builder.slayWeighted(
                            EntityType.ELDER_GUARDIAN,
                            1,
                            3
                    );

                    builder.slayWeighted(
                            EntityType.WITHER,
                            1,
                            3
                    );

                    builder.slayWeighted(
                            EntityType.WARDEN,
                            1,
                            2
                    );

                    builder.slayWeighted(
                            EntityType.ENDER_DRAGON,
                            1,
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
                                DwarfProfession.CHAMPION
                        )
                        .tier(tier)
                        .sound1(
                                SoundEvents.VILLAGER_WORK_CARTOGRAPHER
                        )
                        .sound2(
                                SoundEvents.VILLAGER_WORK_CARTOGRAPHER
                        );

        objectives.accept(builder);

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }
}

