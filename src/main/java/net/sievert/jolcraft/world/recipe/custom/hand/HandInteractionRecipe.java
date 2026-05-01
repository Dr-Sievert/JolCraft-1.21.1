package net.sievert.jolcraft.world.recipe.custom.hand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.base.CustomOutputRecipe;
import net.sievert.jolcraft.world.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.world.recipe.custom.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.world.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.world.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record HandInteractionRecipe(
        ItemInput ingredientA,
        ItemIngredientAction actionA,
        ItemInput ingredientB,
        ItemIngredientAction actionB,
        Outputs output,
        SoundOutput successSound,
        SoundOutput failSound,
        boolean requireSneaking
) implements CustomOutputRecipe<HandInteractionRecipeInput, List<Output>> {

    public static final String SOURCE_INGREDIENT_A =
            JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "a");

    public static final String SOURCE_INGREDIENT_B =
            JolCraftStrings.underscored(JolCraftDictionary.INGREDIENT, "b");

    public static final String ACTION_A =
            JolCraftStrings.underscored(JolCraftDictionary.ACTION, "a");

    public static final String ACTION_B =
            JolCraftStrings.underscored(JolCraftDictionary.ACTION, "b");

    public static final String RESULTS_KEY =
            JolCraftStrings.plural(JolCraftDictionary.RESULT);

    public static final String SUCCESS_SOUND_KEY =
            JolCraftStrings.underscored(JolCraftDictionary.SUCCESS, JolCraftDictionary.SOUND);

    public static final String FAIL_SOUND_KEY =
            JolCraftStrings.underscored(JolCraftDictionary.FAIL, JolCraftDictionary.SOUND);

    public static final String REQUIRE_SNEAKING_KEY =
            JolCraftStrings.underscored(JolCraftDictionary.REQUIRE, JolCraftDictionary.SNEAK);

    @Override
    public boolean matches(@NotNull HandInteractionRecipeInput in, Level level) {
        if (level.isClientSide) {
            return false;
        }

        WorldContext ctx = in.ctx();

        if (requireSneaking) {
            Player player = ctx.player();
            if (player == null || !player.isShiftKeyDown()) {
                return false;
            }
        }

        ItemStack a = in.ingredientA();
        ItemStack b = in.ingredientB();

        boolean direct =
                ingredientA.matches(ctx, a) &&
                        ingredientB.matches(ctx, b) &&
                        ItemIngredientAction.isSatisfied(a, actionA) &&
                        ItemIngredientAction.isSatisfied(b, actionB);

        if (direct) {
            return true;
        }

        return ingredientA.matches(ctx, b) &&
                ingredientB.matches(ctx, a) &&
                ItemIngredientAction.isSatisfied(b, actionA) &&
                ItemIngredientAction.isSatisfied(a, actionB);
    }

    @Override
    public @NotNull List<Output> roll(@NotNull HandInteractionRecipeInput input, @NotNull WorldContext ctx) {
        return output.generateResolved(ctx, input);
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<HandInteractionRecipeInput>> getSerializer() {
        return JolCraftRecipes.HAND_INTERACTION_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<HandInteractionRecipeInput>> getType() {
        return JolCraftRecipes.HAND_INTERACTION_TYPE.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }

    public static @NotNull DataResult<HandInteractionRecipe> validateRecipe(HandInteractionRecipe recipe) {
        DataResult<HandInteractionRecipe> base = RecipeValidation.validate(recipe)
                .requireValid(recipe.ingredientA(), SOURCE_INGREDIENT_A)
                .require(recipe.actionA(), ACTION_A)
                .requireValid(recipe.ingredientB(), SOURCE_INGREDIENT_B)
                .require(recipe.actionB(), ACTION_B)
                .requireValid(recipe.output(), RESULTS_KEY)
                .requireValid(recipe.successSound(), SUCCESS_SOUND_KEY)
                .requireValid(recipe.failSound(), FAIL_SOUND_KEY)
                .done();

        if (base.error().isPresent()) {
            return base;
        }

        if (!recipe.output().hasAnyEntries()) {
            return DataResult.error(() ->
                    RESULTS_KEY + " must contain at least one output entry"
            );
        }

        return DataResult.success(recipe);
    }

    public static final class Serializer implements RecipeSerializer<HandInteractionRecipe> {

        private static final Codec<Outputs> OUTPUT_CODEC =
                Outputs.codecShorthand(OutputParam.CODEC);

        private static final StreamCodec<RegistryFriendlyByteBuf, Boolean> BOOL_STREAM_CODEC =
                StreamCodec.of(
                        RegistryFriendlyByteBuf::writeBoolean,
                        RegistryFriendlyByteBuf::readBoolean
                );

        public static final MapCodec<HandInteractionRecipe> CODEC =
                RecordCodecBuilder.mapCodec(
                        (RecordCodecBuilder.Instance<HandInteractionRecipe> inst) ->
                                inst.group(
                                        ItemInput.CODEC
                                                .fieldOf(SOURCE_INGREDIENT_A)
                                                .forGetter(HandInteractionRecipe::ingredientA),

                                        ItemIngredientAction.CODEC
                                                .optionalFieldOf(ACTION_A, ItemIngredientAction.CATALYST)
                                                .forGetter(HandInteractionRecipe::actionA),

                                        ItemInput.CODEC
                                                .fieldOf(SOURCE_INGREDIENT_B)
                                                .forGetter(HandInteractionRecipe::ingredientB),

                                        ItemIngredientAction.CODEC
                                                .optionalFieldOf(ACTION_B, ItemIngredientAction.CATALYST)
                                                .forGetter(HandInteractionRecipe::actionB),

                                        OUTPUT_CODEC
                                                .fieldOf(RESULTS_KEY)
                                                .forGetter(HandInteractionRecipe::output),

                                        SoundOutput.CODEC
                                                .fieldOf(SUCCESS_SOUND_KEY)
                                                .forGetter(HandInteractionRecipe::successSound),

                                        SoundOutput.CODEC
                                                .fieldOf(FAIL_SOUND_KEY)
                                                .forGetter(HandInteractionRecipe::failSound),

                                        Codec.BOOL
                                                .optionalFieldOf(REQUIRE_SNEAKING_KEY, false)
                                                .forGetter(HandInteractionRecipe::requireSneaking)
                                ).apply(inst, HandInteractionRecipe::new)
                ).validate(HandInteractionRecipe::validateRecipe);

        public static final StreamCodec<RegistryFriendlyByteBuf, HandInteractionRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            ItemInput.STREAM_CODEC.encode(buf, recipe.ingredientA());
                            ItemIngredientAction.STREAM_CODEC.encode(buf, recipe.actionA());
                            ItemInput.STREAM_CODEC.encode(buf, recipe.ingredientB());
                            ItemIngredientAction.STREAM_CODEC.encode(buf, recipe.actionB());
                            Outputs.STREAM_CODEC.encode(buf, recipe.output());
                            SoundOutput.STREAM_CODEC.encode(buf, recipe.successSound());
                            SoundOutput.STREAM_CODEC.encode(buf, recipe.failSound());
                            BOOL_STREAM_CODEC.encode(buf, recipe.requireSneaking());
                        },
                        buf -> new HandInteractionRecipe(
                                ItemInput.STREAM_CODEC.decode(buf),
                                ItemIngredientAction.STREAM_CODEC.decode(buf),
                                ItemInput.STREAM_CODEC.decode(buf),
                                ItemIngredientAction.STREAM_CODEC.decode(buf),
                                Outputs.STREAM_CODEC.decode(buf),
                                SoundOutput.STREAM_CODEC.decode(buf),
                                SoundOutput.STREAM_CODEC.decode(buf),
                                BOOL_STREAM_CODEC.decode(buf)
                        )
                );

        @Override
        public @NotNull MapCodec<HandInteractionRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, HandInteractionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}