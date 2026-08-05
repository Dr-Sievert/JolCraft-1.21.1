package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
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

import java.util.List;
import java.util.Locale;
import java.util.Set;

public record DwarfKeeperBountyTasks(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    private static final int SMALL_MIN = 8;
    private static final int SMALL_MAX = SMALL_MIN * 2;

    private static final int LARGE_MIN = SMALL_MAX;
    private static final int LARGE_MAX = SMALL_MIN * 4;

    private static final int RARE_MIN = 1;
    private static final int RARE_MAX = 2;

    private static final float NOVICE_MIN_SCALE = 1.0F;
    private static final float NOVICE_MAX_SCALE = 1.0F;

    private static final float APPRENTICE_MIN_SCALE = 1.5F;
    private static final float APPRENTICE_MAX_SCALE = 1.5F;

    private static final float JOURNEYMAN_MIN_SCALE = 2.0F;
    private static final float JOURNEYMAN_MAX_SCALE = 2.0F;

    private static final float EXPERT_MIN_SCALE = 3.0F;
    private static final float EXPERT_MAX_SCALE = 3.0F;

    private static final float MASTER_MIN_SCALE = 5.0F;
    private static final float MASTER_MAX_SCALE = 5.0F;

    private static final int CURRENT_LEVEL_WEIGHT = 2;
    private static final int PREVIOUS_LEVEL_WEIGHT = 1;

    private static final Set<ItemLike> LARGE_CONTRACTS = Set.of(
            Items.WHEAT,
            Items.BAMBOO,
            JolCraftItems.BARLEY.get(),
            Items.BEETROOT,
            Items.CARROT,
            Items.POTATO,
            Items.NETHER_WART,
            Items.CRIMSON_FUNGUS,
            Items.WARPED_FUNGUS,
            Items.CHORUS_FRUIT,
            JolCraftItems.DEEPSLATE_BULBS.get()
    );

    private static final Set<ItemLike> RARE_CONTRACTS = Set.of(
            Items.TORCHFLOWER,
            Items.PITCHER_PLANT
    );

    private static final List<FarmingContract> CONTRACTS = List.of(
            contract(Items.WHEAT, DwarfMerchantData.Level.NOVICE),
            contract(JolCraftItems.BARLEY.get(), DwarfMerchantData.Level.NOVICE),
            contract(Items.RED_MUSHROOM, DwarfMerchantData.Level.NOVICE),
            contract(Items.BROWN_MUSHROOM, DwarfMerchantData.Level.NOVICE),

            contract(Items.BEETROOT, DwarfMerchantData.Level.APPRENTICE),
            contract(Items.CARROT, DwarfMerchantData.Level.APPRENTICE),
            contract(Items.POTATO, DwarfMerchantData.Level.APPRENTICE),
            contract(Items.SUGAR_CANE, DwarfMerchantData.Level.APPRENTICE),
            contract(JolCraftBlocks.FESTERLING.get(), DwarfMerchantData.Level.APPRENTICE),

            contract(Items.PUMPKIN, DwarfMerchantData.Level.JOURNEYMAN),
            contract(Items.MELON, DwarfMerchantData.Level.JOURNEYMAN),
            contract(Items.COCOA_BEANS, DwarfMerchantData.Level.JOURNEYMAN),
            contract(Items.SWEET_BERRIES, DwarfMerchantData.Level.JOURNEYMAN),
            contract(Items.GLOW_BERRIES, DwarfMerchantData.Level.JOURNEYMAN),
            contract(Items.CACTUS, DwarfMerchantData.Level.JOURNEYMAN),
            contract(JolCraftBlocks.DUSKCAP.get(), DwarfMerchantData.Level.JOURNEYMAN),

            contract(Items.NETHER_WART, DwarfMerchantData.Level.EXPERT),
            contract(Items.CRIMSON_FUNGUS, DwarfMerchantData.Level.EXPERT),
            contract(Items.WARPED_FUNGUS, DwarfMerchantData.Level.EXPERT),
            contract(JolCraftItems.ASGARNIAN_HOPS.get(), DwarfMerchantData.Level.EXPERT),
            contract(JolCraftItems.DUSKHOLD_HOPS.get(), DwarfMerchantData.Level.EXPERT),
            contract(JolCraftItems.KRANDONIAN_HOPS.get(), DwarfMerchantData.Level.EXPERT),
            contract(JolCraftItems.YANILLIAN_HOPS.get(), DwarfMerchantData.Level.EXPERT),

            contract(Items.CHORUS_FRUIT, DwarfMerchantData.Level.MASTER),
            contract(Items.TORCHFLOWER, DwarfMerchantData.Level.MASTER),
            contract(Items.PITCHER_PLANT, DwarfMerchantData.Level.MASTER),
            contract(JolCraftItems.DEEPSLATE_BULBS.get(), DwarfMerchantData.Level.MASTER)
    );

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
        for (DwarfMerchantData.Level level : DwarfMerchantData.Level.values()) {
            emitTier(output, tracking, level);
        }
    }

    private void emitTier(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level
    ) {
        BountyTaskRecipeBuilder builder = BountyTaskRecipeBuilder.create()
                .id(level.name().toLowerCase(Locale.ROOT))
                .bountyType(DwarfProfession.KEEPER)
                .tier(level)
                .sound1(SoundEvents.VILLAGER_WORK_CARTOGRAPHER)
                .sound2(SoundEvents.VILLAGER_WORK_FISHERMAN);

        CONTRACTS.stream()
                .filter(contract -> contract.unlockLevel().getId() <= level.getId())
                .forEach(contract -> addContract(builder, contract, level));

        emit(
                output,
                tracking,
                builder.buildValidated()
        );
    }

    private static void addContract(
            @NotNull BountyTaskRecipeBuilder builder,
            @NotNull FarmingContract contract,
            @NotNull DwarfMerchantData.Level currentLevel
    ) {
        ItemLike item = contract.item();

        int minimum;
        int maximum;

        if (RARE_CONTRACTS.contains(item)) {
            minimum = RARE_MIN;
            maximum = RARE_MAX;
        } else if (LARGE_CONTRACTS.contains(item)) {
            minimum = LARGE_MIN;
            maximum = LARGE_MAX;
        } else {
            minimum = SMALL_MIN;
            maximum = SMALL_MAX;
        }

        boolean previousLevelContract =
                contract.unlockLevel().getId() < currentLevel.getId();

        if (previousLevelContract) {
            minimum = scaleAmount(
                    minimum,
                    minimumScale(currentLevel)
            );

            maximum = scaleAmount(
                    maximum,
                    maximumScale(currentLevel)
            );
        }

        int weight = contract.unlockLevel() == currentLevel
                && currentLevel != DwarfMerchantData.Level.NOVICE
                ? CURRENT_LEVEL_WEIGHT
                : PREVIOUS_LEVEL_WEIGHT;

        builder.collectWeighted(
                item,
                minimum,
                maximum,
                weight
        );
    }

    private static float minimumScale(
            @NotNull DwarfMerchantData.Level level
    ) {
        return switch (level) {
            case NOVICE -> NOVICE_MIN_SCALE;
            case APPRENTICE -> APPRENTICE_MIN_SCALE;
            case JOURNEYMAN -> JOURNEYMAN_MIN_SCALE;
            case EXPERT -> EXPERT_MIN_SCALE;
            case MASTER -> MASTER_MIN_SCALE;
        };
    }

    private static float maximumScale(
            @NotNull DwarfMerchantData.Level level
    ) {
        return switch (level) {
            case NOVICE -> NOVICE_MAX_SCALE;
            case APPRENTICE -> APPRENTICE_MAX_SCALE;
            case JOURNEYMAN -> JOURNEYMAN_MAX_SCALE;
            case EXPERT -> EXPERT_MAX_SCALE;
            case MASTER -> MASTER_MAX_SCALE;
        };
    }

    private static int scaleAmount(
            int amount,
            float scale
    ) {
        return Math.round(amount * scale);
    }

    private static @NotNull FarmingContract contract(
            @NotNull ItemLike item,
            @NotNull DwarfMerchantData.Level unlockLevel
    ) {
        return new FarmingContract(item, unlockLevel);
    }

    private record FarmingContract(
            @NotNull ItemLike item,
            @NotNull DwarfMerchantData.Level unlockLevel
    ) {
    }
}