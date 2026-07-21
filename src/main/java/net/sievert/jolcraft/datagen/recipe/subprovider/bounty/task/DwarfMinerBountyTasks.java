package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
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

@SuppressWarnings("SameParameterValue")
public record DwarfMinerBountyTasks(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public DwarfMinerBountyTasks(
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
        return DwarfProfession.MINER.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        emitBountyTier(
                output,
                tracking,
                DwarfMerchantData.Level.NOVICE,
                builder -> {
                    builder.slayWeighted(
                            EntityType.ZOMBIE,
                            1,
                            3,
                            2
                    );

                    builder.collectWeighted(
                            Items.STONE,
                            8,
                            15,
                            4
                    );

                    builder.collectWeighted(
                            Items.GRANITE,
                            8,
                            15,
                            3
                    );

                    builder.collectWeighted(
                            Items.DIORITE,
                            8,
                            15,
                            3
                    );

                    builder.collectWeighted(
                            Items.ANDESITE,
                            8,
                            15,
                            3
                    );

                    builder.collectWeighted(
                            Items.TUFF,
                            8,
                            15,
                            2
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                builder -> {
                    builder.collectWeighted(
                            Items.IRON_ORE,
                            4,
                            8,
                            4
                    );

                    builder.collectWeighted(
                            Items.COPPER_ORE,
                            4,
                            8,
                            4
                    );

                    builder.collectWeighted(
                            Items.DEEPSLATE_IRON_ORE,
                            4,
                            8,
                            3
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                builder -> {
                    builder.collectWeighted(
                            Items.GOLD_ORE,
                            3,
                            6,
                            3
                    );

                    builder.collectWeighted(
                            Items.EMERALD_ORE,
                            2,
                            4,
                            2
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                builder -> {
                    builder.collectWeighted(
                            Items.DIAMOND_ORE,
                            1,
                            2,
                            2
                    );

                    builder.collectWeighted(
                            Items.DEEPSLATE_DIAMOND_ORE,
                            1,
                            2,
                            2
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                builder -> builder.collectWeighted(
                        Items.ANCIENT_DEBRIS,
                        1,
                        1,
                        1
                )
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
                                "crate_"
                                        + tier.name()
                                        .toLowerCase(Locale.ROOT)
                        )
                        .bountyType(
                                DwarfProfession.MINER
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

    private void emitBountyTier(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level tier,
            @NotNull Consumer<BountyTaskRecipeBuilder> objectives
    ) {
        BountyTaskRecipeBuilder builder =
                BountyTaskRecipeBuilder.create()
                        .id(
                                "bounty_"
                                        + tier.name()
                                        .toLowerCase(Locale.ROOT)
                        )
                        .bountyType(
                                DwarfProfession.MINER
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