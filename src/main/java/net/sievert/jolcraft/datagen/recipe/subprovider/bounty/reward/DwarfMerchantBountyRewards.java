package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTier;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyType;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.bounty.BountyRewardRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfMerchantBountyRewards implements RecipeSubProvider {

    private static final SoundOutput SOUND = SoundOutput.of(SoundEvents.VILLAGER_WORK_FISHERMAN);

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
            b.reward(coins(4, 6));
        });

        emitTier(executor, BountyTier.APPRENTICE, b -> {
            b.reward(coins(7, 10));
            b.reward(fixed(JolCraftItems.RESTOCK_CRATE.get()));
            b.reward(fixed(JolCraftItems.REROLL_CRATE.get()));
        });

        emitTier(executor, BountyTier.JOURNEYMAN, b -> {
            b.reward(coins(12, 16));
            b.reward(fixed(JolCraftItems.RESTOCK_CRATE.get()));
            b.reward(fixed(JolCraftItems.REROLL_CRATE.get()));
        });

        emitTier(executor, BountyTier.EXPERT, b -> {
            b.reward(coins(20, 27));
            b.reward(fixed(JolCraftItems.RESTOCK_CRATE.get()));
            b.reward(fixed(JolCraftItems.REROLL_CRATE.get()));
        });

        emitTier(executor, BountyTier.MASTER, b -> {
            b.reward(coins(30, 39));
            b.reward(fixed(JolCraftItems.RESTOCK_CRATE.get()));
            b.reward(fixed(JolCraftItems.REROLL_CRATE.get()));
        });
    }

    private void emitTier(
            RecipeEmissionExecutor executor,
            BountyTier tier,
            Consumer<BountyRewardRecipeBuilder> rewards
    ) {

        BountyRewardRecipeBuilder b = BountyRewardRecipeBuilder.create();

        b.bountyType(BountyType.MERCHANT)
                .tier(tier)
                .sound(SOUND);

        rewards.accept(b);

        executor.emit(b.buildValidated());
    }

    private static ItemOutput coins(int min, int max) {
        return new ItemOutput(
                new ItemSpec(
                        ItemProducer.item(JolCraftItems.GOLD_COIN.get()),
                        new IntRange(min, max)
                ),
                ItemTransforms.EMPTY
        );
    }

    private static ItemOutput fixed(Item item) {
        return new ItemOutput(
                new ItemSpec(
                        ItemProducer.item(item),
                        IntRange.ONE
                ),
                ItemTransforms.EMPTY
        );
    }
}