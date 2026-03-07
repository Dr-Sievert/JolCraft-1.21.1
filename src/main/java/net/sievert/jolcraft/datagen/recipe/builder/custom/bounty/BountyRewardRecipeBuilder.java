package net.sievert.jolcraft.datagen.recipe.builder.custom.bounty;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTier;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyType;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmission;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeFileNameBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyRewardRecipeBuilder implements RecipeBuilder {

    private final List<String> errors = new ArrayList<>();

    private BountyType bountyType = BountyType.UNKNOWN;
    private BountyTier tier = BountyTier.UNKNOWN;

    private Outputs rewards = Outputs.EMPTY;

    private @Nullable SoundOutput sound = null;

    private BountyRewardRecipeBuilder() {}

    public static @NotNull BountyRewardRecipeBuilder create() {
        return new BountyRewardRecipeBuilder();
    }

    public @NotNull BountyRewardRecipeBuilder bountyType(@Nullable BountyType type) {
        if (type == null) {
            errors.add("bountyType is null");
            this.bountyType = BountyType.UNKNOWN;
            return this;
        }

        this.bountyType = type;
        return this;
    }

    public @NotNull BountyRewardRecipeBuilder tier(@Nullable BountyTier tier) {
        if (tier == null) {
            errors.add("tier is null");
            this.tier = BountyTier.UNKNOWN;
            return this;
        }

        this.tier = tier;
        return this;
    }

    public @NotNull BountyRewardRecipeBuilder reward(@NotNull OutputParam param) {
        this.rewards = this.rewards.merge(Outputs.wrapSingle(param));
        return this;
    }

    public @NotNull BountyRewardRecipeBuilder sound(@Nullable SoundOutput sound) {
        this.sound = sound;
        return this;
    }

    @Override
    public @NotNull DataResult<RecipeEmission> buildValidated() {
        DataResult<String> nameBuilt = RecipeFileNameBuilder.create()
                .word(tier.name().toLowerCase(Locale.ROOT))
                .word(bountyType.getId())
                .word(JolCraftStrings.plural(JolCraftRecipeIds.BOUNTY_REWARD))
                .build();

        BountyRewardRecipe recipe = new BountyRewardRecipe(
                bountyType,
                tier,
                rewards,
                sound
        );

        DataResult<BountyRewardRecipe> validated =
                BountyRewardRecipe.validateRecipe(recipe);

        DataResult<BountyRewardRecipe> recipeResult =
                (!errors.isEmpty() && validated.error().isEmpty())
                        ? DataResult.error(() -> "builder: " + String.join("; ", errors), recipe)
                        : validated;

        return nameBuilt.flatMap(name ->
                recipeResult.flatMap(validRecipe ->
                        RecipeEmission.of(
                                JolCraftRecipeIds.BOUNTY_REWARD,
                                name,
                                (RecipeOutput out, ResourceKey<Recipe<?>> id) ->
                                        out.accept(id, validRecipe, null)
                        )
                )
        );
    }
}