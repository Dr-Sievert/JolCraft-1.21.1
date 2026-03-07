package net.sievert.jolcraft.data.recipe.custom.hand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.CustomOutputRecipe;
import net.sievert.jolcraft.data.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputDispatch;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
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

    public static final HandInteractionRecipe EMPTY =
            new HandInteractionRecipe(
                    ItemInput.EMPTY,
                    ItemIngredientAction.CATALYST,
                    ItemInput.EMPTY,
                    ItemIngredientAction.CATALYST,
                    Outputs.EMPTY,
                    SoundOutput.EMPTY,
                    SoundOutput.EMPTY,
                    false
            );

    @Override
    public boolean matches(@NotNull HandInteractionRecipeInput in, Level level) {
        if (level.isClientSide) return false;

        WorldContext ctx = in.ctx();
        ItemStack a = in.ingredientA();
        ItemStack b = in.ingredientB();

        boolean direct =
                ingredientA.matches(ctx, a) &&
                        ingredientB.matches(ctx, b) &&
                        ItemIngredientAction.isSatisfied(a, actionA) &&
                        ItemIngredientAction.isSatisfied(b, actionB);

        if (direct) return true;

        return ingredientA.matches(ctx, b) &&
                ingredientB.matches(ctx, a) &&
                ItemIngredientAction.isSatisfied(b, actionA) &&
                ItemIngredientAction.isSatisfied(a, actionB);
    }

    @Override
    public @NotNull List<Output> roll(@NotNull HandInteractionRecipeInput input, @NotNull WorldContext ctx) {
        Outputs r = output != null ? output : Outputs.EMPTY;
        return r.generateResolved(ctx, input);
    }

    @Override
    public RecipeSerializer<? extends Recipe<HandInteractionRecipeInput>> getSerializer() {
        return JolCraftRecipes.HAND_INTERACTION_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<HandInteractionRecipeInput>> getType() {
        return JolCraftRecipes.HAND_INTERACTION_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<HandInteractionRecipe> {

        private static final Codec<Outputs> OUTPUT_CODEC =
                Outputs.codecShorthand(OutputDispatch.CODEC);

        public static final MapCodec<HandInteractionRecipe> CODEC =
                RecordCodecBuilder.mapCodec(
                        (RecordCodecBuilder.Instance<HandInteractionRecipe> inst) ->
                                inst.group(

                                        ItemInput.CODEC
                                                .fieldOf(JolCraftStrings.underscored(
                                                        JolCraftDictionary.INGREDIENT, "a"))
                                                .forGetter(HandInteractionRecipe::ingredientA),

                                        ItemIngredientAction.CODEC
                                                .optionalFieldOf(JolCraftStrings.underscored(
                                                        JolCraftDictionary.ACTION, "a"), ItemIngredientAction.CATALYST)
                                                .forGetter(HandInteractionRecipe::actionA),

                                        ItemInput.CODEC
                                                .fieldOf(JolCraftStrings.underscored(
                                                        JolCraftDictionary.INGREDIENT, "b"))
                                                .forGetter(HandInteractionRecipe::ingredientB),

                                        ItemIngredientAction.CODEC
                                                .optionalFieldOf(JolCraftStrings.underscored(
                                                        JolCraftDictionary.ACTION, "b"), ItemIngredientAction.CATALYST)
                                                .forGetter(HandInteractionRecipe::actionB),

                                        OUTPUT_CODEC
                                                .fieldOf(JolCraftStrings.plural(
                                                        JolCraftDictionary.RESULT))
                                                .forGetter(HandInteractionRecipe::output),

                                        SoundOutput.CODEC
                                                .fieldOf(JolCraftStrings.underscored(
                                                        JolCraftDictionary.SUCCESS,
                                                        JolCraftDictionary.SOUND))
                                                .forGetter(HandInteractionRecipe::successSound),

                                        SoundOutput.CODEC
                                                .fieldOf(JolCraftStrings.underscored(
                                                        JolCraftDictionary.FAIL,
                                                        JolCraftDictionary.SOUND))
                                                .forGetter(HandInteractionRecipe::failSound),

                                        Codec.BOOL
                                                .optionalFieldOf(
                                                        JolCraftStrings.underscored(
                                                                JolCraftDictionary.REQUIRE,
                                                                JolCraftDictionary.SNEAK),
                                                        false)
                                                .forGetter(HandInteractionRecipe::requireSneaking)

                                ).apply(inst, HandInteractionRecipe::new)
                ).validate(Serializer::validate);

        public static final StreamCodec<RegistryFriendlyByteBuf, HandInteractionRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemInput.STREAM_CODEC, HandInteractionRecipe::ingredientA,
                        ItemIngredientAction.STREAM_CODEC, HandInteractionRecipe::actionA,
                        ItemInput.STREAM_CODEC, HandInteractionRecipe::ingredientB,
                        ItemIngredientAction.STREAM_CODEC, HandInteractionRecipe::actionB,
                        Outputs.STREAM_CODEC, HandInteractionRecipe::output,
                        SoundOutput.STREAM_CODEC, HandInteractionRecipe::successSound,
                        SoundOutput.STREAM_CODEC, HandInteractionRecipe::failSound,
                        ByteBufCodecs.BOOL, HandInteractionRecipe::requireSneaking,
                        HandInteractionRecipe::new
                );

        @Override
        public MapCodec<HandInteractionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HandInteractionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static DataResult<HandInteractionRecipe> validate(HandInteractionRecipe r) {
            DataResult<HandInteractionRecipe> result =
                    r.ingredientA.validate().map(v -> r);

            result = result.flatMap(x -> r.actionA.validate().map(v -> r));
            result = result.flatMap(x -> r.ingredientB.validate().map(v -> r));
            result = result.flatMap(x -> r.actionB.validate().map(v -> r));
            result = result.flatMap(x -> r.output.validate().map(v -> r));
            result = result.flatMap(x -> r.successSound.validate().map(v -> r));
            result = result.flatMap(x -> r.failSound.validate().map(v -> r));

            return result;
        }
    }
}