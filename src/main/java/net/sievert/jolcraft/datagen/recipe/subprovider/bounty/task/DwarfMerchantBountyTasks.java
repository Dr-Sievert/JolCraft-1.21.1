package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTier;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyType;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyTaskRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
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

        emitTier(executor, BountyTier.NOVICE, b -> {
            b.collect(Items.COAL, 5, 12);
            b.collect(Items.FLINT, 5, 12);
            b.collect(Items.COPPER_INGOT, 5, 12);
            b.collect(Items.COBBLED_DEEPSLATE, 5, 12);
            b.collect(Items.TORCH, 5, 12);
            b.collect(Items.CLAY_BALL, 5, 12);
            b.collect(Items.IRON_NUGGET, 5, 12);
        });

        emitTier(executor, BountyTier.APPRENTICE, b -> {
            b.collect(Items.IRON_INGOT, 4, 8);
            b.collect(Items.LAPIS_LAZULI, 4, 8);
            b.collect(Items.REDSTONE, 4, 8);
            b.collect(Items.GLOW_INK_SAC, 3, 6);
            b.collect(Items.SPIDER_EYE, 3, 6);
            b.collect(Items.GUNPOWDER, 3, 6);
            b.collect(Items.BONE, 5, 9);
        });

        emitTier(executor, BountyTier.JOURNEYMAN, b -> {
            b.collect(Items.GOLD_INGOT, 3, 6);
            b.collect(Items.EMERALD, 2, 5);
            b.collect(Items.AMETHYST_SHARD, 3, 6);
            b.collect(Items.BLAZE_POWDER, 3, 6);
            b.collect(Items.INK_SAC, 3, 6);
        });

        emitTier(executor, BountyTier.EXPERT, b -> {
            b.collect(Items.ANVIL, 1, 1);
            b.collect(Items.GOLDEN_APPLE, 1, 2);
            b.collect(Items.BOOK, 1, 2);
            b.collect(Items.CAULDRON, 1, 1);
            b.collect(Items.ITEM_FRAME, 1, 3);
            b.collect(Items.ENDER_PEARL, 1, 1);
        });

        emitTier(executor, BountyTier.MASTER, b -> {
            b.collect(Items.NETHERITE_SCRAP, 1, 2);
            b.collect(Items.HEART_OF_THE_SEA, 1, 1);
            b.collect(Items.DRAGON_BREATH, 1, 2);
        });
    }

    private void emitTier(
            RecipeEmissionExecutor executor,
            BountyTier tier,
            Consumer<BountyTaskRecipeBuilder> objectives
    ) {

        BountyTaskRecipeBuilder b = BountyTaskRecipeBuilder.create();

        b.bountyType(BountyType.MERCHANT)
                .tier(tier)
                .result(JolCraftItems.BOUNTY_CRATE.get())
                .sound1(SoundEvents.VILLAGER_WORK_CARTOGRAPHER)
                .sound2(SoundEvents.VILLAGER_WORK_FISHERMAN);

        objectives.accept(b);

        executor.emit(b.buildValidated());
    }
}