package net.sievert.jolcraft.datagen.recipe.builder.custom.bounty;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmission;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeFileNameBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
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

    private @Nullable DwarfProfession bountyType;
    private @Nullable DwarfMerchantData.Level tier;
    private Outputs rewards = Outputs.EMPTY;

    private @Nullable SoundOutput sound;

    private BountyRewardRecipeBuilder() {}

    public static @NotNull BountyRewardRecipeBuilder create() {
        return new BountyRewardRecipeBuilder();
    }

    public @NotNull BountyRewardRecipeBuilder bountyType(@Nullable DwarfProfession type) {
        if (type == null) {
            errors.add("bountyType is null");
            this.bountyType = null;
            return this;
        }

        if (type == DwarfProfession.NONE) {
            errors.add("bountyType must not be NONE");
            this.bountyType = null;
            return this;
        }

        this.bountyType = type;
        return this;
    }

    public @NotNull BountyRewardRecipeBuilder tier(@Nullable DwarfMerchantData.Level tier) {
        if (tier == null) {
            errors.add("tier is null");
            this.tier = null;
            return this;
        }

        this.tier = tier;
        return this;
    }

    public @NotNull BountyRewardRecipeBuilder reward(@Nullable OutputParam param) {
        if (param == null) {
            errors.add("reward is null");
            return this;
        }

        this.rewards = this.rewards.merge(Outputs.wrapSingle(param));
        return this;
    }

    public @NotNull BountyRewardRecipeBuilder sound(@Nullable SoundEvent sound) {
        if (sound == null) {
            errors.add("sound is null");
            this.sound = null;
            return this;
        }

        var built = SoundOutputBuilder.create()
                .sound(sound)
                .buildValidated();

        if (built.error().isPresent()) {
            errors.add("sound invalid: " + built.error().map(DataResult.Error::message).orElse("invalid"));
            this.sound = null;
            return this;
        }

        this.sound = built.result().orElse(null);
        return this;
    }

    @Override
    public @NotNull DataResult<RecipeEmission> buildValidated() {
        if (bountyType == null) {
            errors.add("bountyType is required");
        }

        if (tier == null) {
            errors.add("tier is required");
        }

        if (sound == null) {
            errors.add("sound is required");
        }

        DataResult<String> nameBuilt = RecipeFileNameBuilder.create()
                .word(tierNameSafe())
                .word(JolCraftStrings.plural(JolCraftRecipeIds.BOUNTY_REWARD))
                .build();

        if (!errors.isEmpty()) {
            return nameBuilt.flatMap(name ->
                    DataResult.error(() -> "builder: " + String.join("; ", errors))
            );
        }

        DwarfProfession finalType = bountyType;
        DwarfMerchantData.Level finalTier = tier;
        SoundOutput finalSound = sound;

        if (finalType == null || finalTier == null || finalSound == null) {
            return DataResult.error(() -> "builder: missing required fields");
        }

        BountyRewardRecipe recipe = new BountyRewardRecipe(
                finalType,
                finalTier,
                rewards,
                finalSound
        );

        return nameBuilt.flatMap(name ->
                BountyRewardRecipe.validateRecipe(recipe).flatMap(validRecipe ->
                        RecipeEmission.of(
                                JolCraftRecipeIds.BOUNTY_REWARD,
                                name,
                                (RecipeOutput out, ResourceKey<Recipe<?>> id) ->
                                        out.accept(id, validRecipe, null)
                        )
                )
        );
    }

    private @NotNull String tierNameSafe() {
        if (tier == null) {
            return JolCraftDictionary.UNKNOWN;
        }

        return tier.name().toLowerCase(Locale.ROOT);
    }
}