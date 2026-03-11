package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyTaskRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class DwarfMerchantBountyTasks implements RecipeSubProvider {

    @Override
    public String folder() {
        return DwarfProfession.MERCHANT.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        emitTier(executor, DwarfMerchantData.Level.NOVICE, b -> {
            b.collectWeighted(Items.COAL, 5, 12, 4);
            b.collectWeighted(Items.FLINT, 5, 12, 4);
            b.collectWeighted(Items.COPPER_INGOT, 5, 12, 3);
            b.collectWeighted(Items.COBBLED_DEEPSLATE, 5, 12, 3);
            b.collectWeighted(Items.TORCH, 5, 12, 2);
            b.collectWeighted(Items.CLAY_BALL, 5, 12, 2);
            b.collectWeighted(Items.IRON_NUGGET, 5, 12, 2);
        });

        emitTier(executor, DwarfMerchantData.Level.APPRENTICE, b -> {
            b.collectWeighted(Items.IRON_INGOT, 4, 8, 4);
            b.collectWeighted(Items.LAPIS_LAZULI, 4, 8, 3);
            b.collectWeighted(Items.REDSTONE, 4, 8, 3);
            b.collectWeighted(Items.GLOW_INK_SAC, 3, 6, 2);
            b.collectWeighted(Items.SPIDER_EYE, 3, 6, 2);
            b.collectWeighted(Items.GUNPOWDER, 3, 6, 2);
            b.collectWeighted(Items.BONE, 5, 9, 3);
        });

        emitTier(executor, DwarfMerchantData.Level.JOURNEYMAN, b -> {
            b.collectWeighted(Items.GOLD_INGOT, 3, 6, 3);
            b.collectWeighted(Items.EMERALD, 2, 5, 2);
            b.collectWeighted(Items.AMETHYST_SHARD, 3, 6, 3);
            b.collectWeighted(Items.BLAZE_POWDER, 3, 6, 2);
            b.collectWeighted(Items.INK_SAC, 3, 6, 2);
        });

        emitTier(executor, DwarfMerchantData.Level.EXPERT, b -> {
            b.collectWeighted(Items.ANVIL, 1, 1, 1);
            b.collectWeighted(Items.GOLDEN_APPLE, 1, 2, 2);
            b.collectWeighted(Items.BOOK, 1, 2, 3);
            b.collectWeighted(Items.CAULDRON, 1, 1, 2);
            b.collectWeighted(Items.ITEM_FRAME, 1, 3, 2);
            b.collectWeighted(Items.ENDER_PEARL, 1, 1, 1);
        });

        emitTier(executor, DwarfMerchantData.Level.MASTER, b -> {
            b.collectWeighted(Items.NETHERITE_SCRAP, 1, 2, 2);
            b.collectWeighted(Items.HEART_OF_THE_SEA, 1, 1, 1);
            b.collectWeighted(Items.DRAGON_BREATH, 1, 2, 2);
        });
    }

    private void emitTier(
            RecipeEmissionExecutor executor,
            DwarfMerchantData.Level tier,
            Consumer<BountyTaskRecipeBuilder> objectives
    ) {
        BountyTaskRecipeBuilder b = BountyTaskRecipeBuilder.create();

        b.bountyType(DwarfProfession.MERCHANT)
                .tier(tier)
                .result(JolCraftItems.BOUNTY_CRATE.get())
                .sound1(SoundEvents.VILLAGER_WORK_CARTOGRAPHER)
                .sound2(SoundEvents.VILLAGER_WORK_FISHERMAN);

        objectives.accept(b);

        executor.emit(b.buildValidated());
    }
}