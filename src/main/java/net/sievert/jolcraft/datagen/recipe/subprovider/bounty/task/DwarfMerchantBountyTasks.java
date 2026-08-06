package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.BountyTaskRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Consumer;

public record DwarfMerchantBountyTasks(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    private static final int DEFAULT_WEIGHT = 3;

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
                    collect(builder, Items.TORCH, 8, 16);
                    collect(builder, Items.COAL, 8, 12);
                    collect(builder, Items.FLINT, 6, 12);
                    collect(builder, Items.COPPER_INGOT, 3, 6);
                    collect(builder, Items.IRON_NUGGET, 14, 20);
                    collect(builder, Blocks.COBBLED_DEEPSLATE, 16, 32);

                    collect(builder, Items.WHEAT_SEEDS, 4, 8);
                    collect(builder, Items.BEETROOT_SEEDS, 3, 5);
                    collect(builder, Items.WHEAT, 6, 12);
                    collect(builder, Items.BEETROOT, 5, 8);
                    collect(builder, Items.BAKED_POTATO, 3, 5);
                    collect(builder, Items.APPLE, 1, 2);
                    collect(builder, Items.EGG, 3, 5);
                    collect(builder, Items.FEATHER, 3, 5);

                    collect(builder, Items.ALLIUM, 1, 3, 1);
                    collect(builder, Items.AZURE_BLUET, 1, 3, 1);
                    collect(builder, Items.BLUE_ORCHID, 1, 3, 1);
                    collect(builder, Items.CORNFLOWER, 1, 3, 1);
                    collect(builder, Items.DANDELION, 1, 3, 1);
                    collect(builder, Items.LILY_OF_THE_VALLEY, 1, 3, 1);
                    collect(builder, Items.OXEYE_DAISY, 1, 3, 1);
                    collect(builder, Items.POPPY, 1, 3, 1);
                    collect(builder, Items.ORANGE_TULIP, 1, 3, 1);
                    collect(builder, Items.PINK_TULIP, 1, 3, 1);
                    collect(builder, Items.RED_TULIP, 1, 3, 1);
                    collect(builder, Items.WHITE_TULIP, 1, 3, 1);

                    collect(builder, Items.STRING, 3, 5);
                    collect(builder, Items.PAPER, 4, 8);
                    collect(builder, Items.CHAIN, 3, 5);
                    collect(builder, JolCraftItems.QUILL_EMPTY, 1, 1);
                    collect(builder, JolCraftItems.GLASS_MUG, 1, 3);
                    collect(builder, Items.GLASS_BOTTLE, 3, 5);
                    collect(builder, Items.CANDLE, 2, 4);
                    collect(builder, Items.FLOWER_POT, 1, 3);

                    collect(builder, Items.BLACK_DYE, 1, 3, 1);
                    collect(builder, Items.BLUE_DYE, 1, 3, 1);
                    collect(builder, Items.BROWN_DYE, 1, 3, 1);
                    collect(builder, Items.CYAN_DYE, 1, 3, 1);
                    collect(builder, Items.GRAY_DYE, 1, 3, 1);
                    collect(builder, Items.GREEN_DYE, 1, 3, 1);
                    collect(builder, Items.LIGHT_BLUE_DYE, 1, 3, 1);
                    collect(builder, Items.LIGHT_GRAY_DYE, 1, 3, 1);
                    collect(builder, Items.LIME_DYE, 1, 3, 1);
                    collect(builder, Items.MAGENTA_DYE, 1, 3, 1);
                    collect(builder, Items.ORANGE_DYE, 1, 3, 1);
                    collect(builder, Items.PINK_DYE, 1, 3, 1);
                    collect(builder, Items.PURPLE_DYE, 1, 3, 1);
                    collect(builder, Items.RED_DYE, 1, 3, 1);
                    collect(builder, Items.WHITE_DYE, 1, 3, 1);
                    collect(builder, Items.YELLOW_DYE, 1, 3, 1);
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                builder -> {
                    collect(builder, Items.IRON_INGOT, 2, 4);
                    collect(builder, Blocks.DEEPSLATE_TILES, 16, 32);

                    collect(builder, JolCraftItems.BARLEY, 3, 5);
                    collect(builder, Items.CARROT, 3, 5);
                    collect(builder, Items.POTATO, 3, 5);
                    collect(builder, Items.MELON_SLICE, 3, 5);
                    collect(builder, Items.PUMPKIN, 1, 3);
                    collect(builder, Items.SUGAR_CANE, 5, 8);
                    collect(builder, Items.CACTUS, 3, 5);
                    collect(builder, Items.COCOA_BEANS, 3, 5);
                    collect(builder, Items.SWEET_BERRIES, 3, 5);
                    collect(builder, Items.GLOW_BERRIES, 3, 5);
                    collect(builder, Items.BREAD, 3, 5);
                    collect(builder, Items.COOKED_MUTTON, 3, 5);
                    collect(builder, Items.COOKED_RABBIT, 3, 5);
                    collect(builder, Items.COOKIE, 3, 5);

                    collect(builder, JolCraftBlocks.DUSKCAP, 1, 2);
                    collect(builder, JolCraftBlocks.FESTERLING, 1, 2);

                    collect(builder, Items.HONEYCOMB, 2, 4);
                    collect(builder, Items.HONEY_BOTTLE, 1, 2);
                    collect(builder, Items.BUCKET, 1, 1);
                    collect(builder, Items.FISHING_ROD, 1, 1);
                    collect(builder, Items.FLINT_AND_STEEL, 1, 1);
                    collect(builder, Items.BRUSH, 1, 1);
                    collect(builder, Items.LANTERN, 3, 5);
                    collect(builder, JolCraftItems.PARCHMENT, 2, 4);
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                builder -> {
                    collect(builder, Items.LAPIS_LAZULI, 6, 12);
                    collect(builder, Items.REDSTONE, 6, 12);
                    collect(builder, Items.QUARTZ, 5, 8);
                    collect(builder, Items.GLOWSTONE_DUST, 3, 5);
                    collect(builder, JolCraftItems.GEODE_SMALL, 1, 1);

                    collect(builder, Items.COOKED_BEEF, 3, 5);
                    collect(builder, Items.COOKED_CHICKEN, 3, 5);
                    collect(builder, Items.COOKED_PORKCHOP, 3, 5);
                    collect(builder, Items.NETHER_WART, 1, 3);
                    collect(builder, JolCraftItems.ASGARNIAN_HOPS, 3, 6);
                    collect(builder, JolCraftItems.DUSKHOLD_HOPS, 3, 6);
                    collect(builder, JolCraftItems.KRANDONIAN_HOPS, 3, 6);
                    collect(builder, JolCraftItems.YANILLIAN_HOPS, 3, 6);
                    collect(builder, JolCraftItems.DEEPSLATE_BULBS, 1, 3);

                    collect(builder, Items.BONE, 3, 5);
                    collect(builder, Items.GUNPOWDER, 1, 3);
                    collect(builder, Items.SPIDER_EYE, 1, 2);
                    collect(builder, Items.INK_SAC, 1, 2);
                    collect(builder, JolCraftItems.MUFFHORN_FUR, 1, 3);
                    collect(builder, JolCraftItems.MUFFHORN_MILK_BUCKET, 1, 1);
                    collect(builder, Items.LEATHER, 3, 5);
                    collect(builder, Items.SLIME_BALL, 2, 4);

                    collect(builder, JolCraftItems.INVERIX, 1, 2);
                    collect(builder, JolCraftItems.CONTRACT_BLANK, 1, 2);
                    collect(builder, JolCraftItems.LOCKPICK, 5, 10);
                    collect(builder, Items.BOOK, 2, 4);
                    collect(builder, Items.SPYGLASS, 1, 1);
                    collect(builder, Items.LEAD, 1, 2);
                    collect(builder, JolCraftItems.QUILL_FULL, 1, 1);
                    collect(builder, Items.ITEM_FRAME, 2, 4);
                    collect(builder, Items.PAINTING, 1, 2);
                    collect(builder, Items.SADDLE, 1, 1);
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                builder -> {
                    collect(builder, Items.GOLD_INGOT, 1, 2);
                    collect(builder, Items.EMERALD, 3, 5);
                    collect(builder, Items.AMETHYST_SHARD, 2, 4);
                    collect(builder, JolCraftItems.DEEPSLATE_PLATE, 2, 4);
                    collect(builder, Items.DIAMOND, 1, 2);
                    collect(builder, JolCraftItems.GEODE_MEDIUM, 1, 1);

                    collect(builder, Items.GHAST_TEAR, 1, 2);
                    collect(builder, Items.PHANTOM_MEMBRANE, 1, 2);
                    collect(builder, Items.ENDER_PEARL, 1, 3);
                    collect(builder, Items.BLAZE_POWDER, 1, 3);

                    collect(builder, JolCraftItems.DEEPSLATE_WARHAMMER, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_SWORD, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_PICKAXE, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_SHOVEL, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_AXE, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_HOE, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_HELMET, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_CHESTPLATE, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_LEGGINGS, 1, 1);
                    collect(builder, JolCraftItems.DEEPSLATE_BOOTS, 1, 1);

                    collect(builder, Items.GOLDEN_APPLE, 1, 1);
                    collect(builder, Items.CAULDRON, 1, 1);
                    collect(builder, Blocks.BOOKSHELF, 1, 1);
                    collect(builder, JolCraftItems.STRONGBOX_ITEM, 1, 1);
                    collect(builder, Items.EXPERIENCE_BOTTLE, 3, 6);
                    collect(builder, Items.NAME_TAG, 1, 1);

                    collect(builder, JolCraftItems.AEGISCORE_DUST, 1, 1);
                    collect(builder, JolCraftItems.ASHFANG_DUST, 1, 1);
                    collect(builder, JolCraftItems.DEEPMARROW_DUST, 1, 1);
                    collect(builder, JolCraftItems.EARTHBLOOD_DUST, 1, 1);
                    collect(builder, JolCraftItems.EMBERGLASS_DUST, 1, 1);
                    collect(builder, JolCraftItems.FROSTVEIN_DUST, 1, 1);
                    collect(builder, JolCraftItems.GRIMSTONE_DUST, 1, 1);
                    collect(builder, JolCraftItems.IRONHEART_DUST, 1, 1);
                    collect(builder, JolCraftItems.LUMIERE_DUST, 1, 1);
                    collect(builder, JolCraftItems.MOONSHARD_DUST, 1, 1);
                    collect(builder, JolCraftItems.SKYBURROW_DUST, 1, 1);
                    collect(builder, JolCraftItems.SUNGLEAM_DUST, 1, 1);
                    collect(builder, JolCraftItems.VERDANITE_DUST, 1, 1);
                    collect(builder, JolCraftItems.WOECRYSTAL_DUST, 1, 1);

                    collect(builder, JolCraftItems.UNIDENTIFIED_DWARVEN_TOME, 1, 1);
                }
        );

        emitTier(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                builder -> {
                    collect(builder, JolCraftItems.GEODE_LARGE, 1, 1);
                    collect(builder, Items.ECHO_SHARD, 1, 1);
                    collect(builder, Items.NETHERITE_SCRAP, 1, 1);
                    collect(builder, JolCraftItems.IMPURE_MITHRIL, 1, 1);

                    collect(builder, JolCraftItems.AEGISCORE, 1, 1);
                    collect(builder, JolCraftItems.ASHFANG, 1, 1);
                    collect(builder, JolCraftItems.DEEPMARROW, 1, 1);
                    collect(builder, JolCraftItems.EARTHBLOOD, 1, 1);
                    collect(builder, JolCraftItems.EMBERGLASS, 1, 1);
                    collect(builder, JolCraftItems.FROSTVEIN, 1, 1);
                    collect(builder, JolCraftItems.GRIMSTONE, 1, 1);
                    collect(builder, JolCraftItems.IRONHEART, 1, 1);
                    collect(builder, JolCraftItems.LUMIERE, 1, 1);
                    collect(builder, JolCraftItems.MOONSHARD, 1, 1);
                    collect(builder, JolCraftItems.SKYBURROW, 1, 1);
                    collect(builder, JolCraftItems.SUNGLEAM, 1, 1);
                    collect(builder, JolCraftItems.VERDANITE, 1, 1);
                    collect(builder, JolCraftItems.WOECRYSTAL, 1, 1);

                    collect(
                            builder,
                            JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME,
                            1,
                            1
                    );

                    collect(builder, JolCraftItems.LEGENDARY_PAGE, 2, 6);
                }
        );
    }

    private static void collect(
            @NotNull BountyTaskRecipeBuilder builder,
            @NotNull ItemLike item,
            int minimum,
            int maximum
    ) {
        collect(
                builder,
                item,
                minimum,
                maximum,
                DEFAULT_WEIGHT
        );
    }

    private static void collect(
            @NotNull BountyTaskRecipeBuilder builder,
            @NotNull ItemLike item,
            int minimum,
            int maximum,
            int weight
    ) {
        builder.collectWeighted(
                item,
                minimum,
                maximum,
                weight
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