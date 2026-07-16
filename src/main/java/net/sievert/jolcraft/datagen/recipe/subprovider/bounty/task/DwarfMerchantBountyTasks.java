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
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Consumer;

public record DwarfMerchantBountyTasks(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public DwarfMerchantBountyTasks(
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
        return DwarfProfession.MERCHANT.professionName();
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
                            Items.COAL,
                            5,
                            12,
                            4
                    );

                    builder.collectWeighted(
                            Items.FLINT,
                            5,
                            12,
                            4
                    );

                    builder.collectWeighted(
                            Items.COPPER_INGOT,
                            5,
                            12,
                            3
                    );

                    builder.collectWeighted(
                            Items.COBBLED_DEEPSLATE,
                            5,
                            12,
                            3
                    );

                    builder.collectWeighted(
                            Items.TORCH,
                            5,
                            12,
                            2
                    );

                    builder.collectWeighted(
                            Items.CLAY_BALL,
                            5,
                            12,
                            2
                    );

                    builder.collectWeighted(
                            Items.IRON_NUGGET,
                            5,
                            12,
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
                            Items.IRON_INGOT,
                            4,
                            8,
                            4
                    );

                    builder.collectWeighted(
                            Items.LAPIS_LAZULI,
                            4,
                            8,
                            3
                    );

                    builder.collectWeighted(
                            Items.REDSTONE,
                            4,
                            8,
                            3
                    );

                    builder.collectWeighted(
                            Items.GLOW_INK_SAC,
                            3,
                            6,
                            2
                    );

                    builder.collectWeighted(
                            Items.SPIDER_EYE,
                            3,
                            6,
                            2
                    );

                    builder.collectWeighted(
                            Items.GUNPOWDER,
                            3,
                            6,
                            2
                    );

                    builder.collectWeighted(
                            Items.BONE,
                            5,
                            9,
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
                            Items.GOLD_INGOT,
                            3,
                            6,
                            3
                    );

                    builder.collectWeighted(
                            Items.EMERALD,
                            2,
                            5,
                            2
                    );

                    builder.collectWeighted(
                            Items.AMETHYST_SHARD,
                            3,
                            6,
                            3
                    );

                    builder.collectWeighted(
                            Items.BLAZE_POWDER,
                            3,
                            6,
                            2
                    );

                    builder.collectWeighted(
                            Items.INK_SAC,
                            3,
                            6,
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
                            Items.ANVIL,
                            1,
                            1,
                            1
                    );

                    builder.collectWeighted(
                            Items.GOLDEN_APPLE,
                            1,
                            2,
                            2
                    );

                    builder.collectWeighted(
                            Items.BOOK,
                            1,
                            2,
                            3
                    );

                    builder.collectWeighted(
                            Items.CAULDRON,
                            1,
                            1,
                            2
                    );

                    builder.collectWeighted(
                            Items.ITEM_FRAME,
                            1,
                            3,
                            2
                    );

                    builder.collectWeighted(
                            Items.ENDER_PEARL,
                            1,
                            1,
                            1
                    );
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                builder -> {
                    builder.collectWeighted(
                            Items.NETHERITE_SCRAP,
                            1,
                            2,
                            2
                    );

                    builder.collectWeighted(
                            Items.HEART_OF_THE_SEA,
                            1,
                            1,
                            1
                    );

                    builder.collectWeighted(
                            Items.DRAGON_BREATH,
                            1,
                            2,
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
                                DwarfProfession.MERCHANT
                        )
                        .tier(tier)
                        .result(
                                JolCraftItems.BOUNTY_CRATE.get()
                        )
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