package net.sievert.jolcraft.world.recipe.custom.hand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.base.ItemIngredientAction;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.input.ItemInput;
import net.sievert.jolcraft.world.recipe.output.JolCraftRecipeOutputTypes;
import net.sievert.jolcraft.world.recipe.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record HandInteractionRecipe(
        ItemInput ingredientA,
        ItemIngredientAction actionA,
        ItemInput ingredientB,
        ItemIngredientAction actionB,
        List<RecipeOutput> outputs,
        SoundOutput successSound,
        SoundOutput failSound,
        boolean requireSneaking
) implements CustomRecipe<HandInteractionRecipeInput> {

    public static final String INGREDIENT_A_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.INGREDIENT,
                    "a"
            );

    public static final String INGREDIENT_B_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.INGREDIENT,
                    "b"
            );

    public static final String ACTION_A_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.ACTION,
                    "a"
            );

    public static final String ACTION_B_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.ACTION,
                    "b"
            );

    public static final String RESULTS_KEY =
            JolCraftStrings.plural(
                    JolCraftDictionary.RESULT
            );

    public static final String SUCCESS_SOUND_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.SUCCESS,
                    JolCraftDictionary.SOUND
            );

    public static final String FAIL_SOUND_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.FAIL,
                    JolCraftDictionary.SOUND
            );

    public static final String REQUIRE_SNEAKING_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.REQUIRE,
                    JolCraftDictionary.SNEAK
            );

    private static final LootContextParamSet INPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(
                            JolCraftRecipeContextParams.INPUT_ITEM
                    )
                    .build();

    public HandInteractionRecipe {
        Objects.requireNonNull(
                ingredientA,
                INGREDIENT_A_KEY
        );

        Objects.requireNonNull(
                actionA,
                ACTION_A_KEY
        );

        Objects.requireNonNull(
                ingredientB,
                INGREDIENT_B_KEY
        );

        Objects.requireNonNull(
                actionB,
                ACTION_B_KEY
        );

        outputs = List.copyOf(outputs);

        Objects.requireNonNull(
                successSound,
                SUCCESS_SOUND_KEY
        );

        Objects.requireNonNull(
                failSound,
                FAIL_SOUND_KEY
        );
    }

    @Override
    public boolean matches(
            @NotNull HandInteractionRecipeInput input,
            @NotNull Level level
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        ItemStack stackA = input.ingredientA();
        ItemStack stackB = input.ingredientB();

        if (stackA.isEmpty() || stackB.isEmpty()) {
            return false;
        }

        boolean direct =
                matchesInput(
                        serverLevel,
                        stackA,
                        ingredientA
                )
                        && matchesInput(
                        serverLevel,
                        stackB,
                        ingredientB
                )
                        && actionA.isSatisfied(stackA)
                        && actionB.isSatisfied(stackB);

        if (direct) {
            return true;
        }

        return matchesInput(
                serverLevel,
                stackB,
                ingredientA
        )
                && matchesInput(
                serverLevel,
                stackA,
                ingredientB
        )
                && actionA.isSatisfied(stackB)
                && actionB.isSatisfied(stackA);
    }

    @Override
    public @NotNull RecipeSerializer<
            ? extends Recipe<HandInteractionRecipeInput>
            > getSerializer() {
        return JolCraftRecipes.HAND_INTERACTION_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<
            ? extends Recipe<HandInteractionRecipeInput>
            > getType() {
        return JolCraftRecipes.HAND_INTERACTION_TYPE.get();
    }

    private static boolean matchesInput(
            @NotNull ServerLevel level,
            @NotNull ItemStack stack,
            @NotNull ItemInput input
    ) {
        LootContext context = JolCraftRecipeContexts.create(
                level,
                INPUT_CONTEXT_PARAMS,
                builder -> builder.withParameter(
                        JolCraftRecipeContextParams.INPUT_ITEM,
                        stack
                )
        );

        return input.condition().test(context);
    }

    public static final class Serializer
            implements RecipeSerializer<HandInteractionRecipe> {

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                ItemInput
                > ITEM_INPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        ItemInput.CODEC
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                List<RecipeOutput>
                > OUTPUT_LIST_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        JolCraftRecipeOutputTypes.LIST_CODEC
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                SoundOutput
                > SOUND_OUTPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        SoundOutput.CODEC.codec()
                );

        public static final MapCodec<HandInteractionRecipe> CODEC =
                RecordCodecBuilder
                        .<HandInteractionRecipe>mapCodec(instance ->
                                instance.group(
                                        ItemInput.CODEC
                                                .fieldOf(
                                                        INGREDIENT_A_KEY
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::ingredientA
                                                ),

                                        ItemIngredientAction.CODEC
                                                .optionalFieldOf(
                                                        ACTION_A_KEY,
                                                        ItemIngredientAction.CATALYST
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::actionA
                                                ),

                                        ItemInput.CODEC
                                                .fieldOf(
                                                        INGREDIENT_B_KEY
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::ingredientB
                                                ),

                                        ItemIngredientAction.CODEC
                                                .optionalFieldOf(
                                                        ACTION_B_KEY,
                                                        ItemIngredientAction.CATALYST
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::actionB
                                                ),

                                        JolCraftRecipeOutputTypes.LIST_CODEC
                                                .fieldOf(
                                                        RESULTS_KEY
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::outputs
                                                ),

                                        SoundOutput.CODEC
                                                .codec()
                                                .fieldOf(
                                                        SUCCESS_SOUND_KEY
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::successSound
                                                ),

                                        SoundOutput.CODEC
                                                .codec()
                                                .fieldOf(
                                                        FAIL_SOUND_KEY
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::failSound
                                                ),

                                        Codec.BOOL
                                                .optionalFieldOf(
                                                        REQUIRE_SNEAKING_KEY,
                                                        false
                                                )
                                                .forGetter(
                                                        HandInteractionRecipe::requireSneaking
                                                )
                                ).apply(
                                        instance,
                                        HandInteractionRecipe::new
                                )
                        ).flatXmap(
                                Serializer::validate,
                                DataResult::success
                        );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                HandInteractionRecipe
                > STREAM_CODEC = StreamCodec.of(
                Serializer::encode,
                Serializer::decode
        );

        private static void encode(
                RegistryFriendlyByteBuf buffer,
                HandInteractionRecipe recipe
        ) {
            ITEM_INPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.ingredientA()
            );

            ItemIngredientAction.STREAM_CODEC.encode(
                    buffer,
                    recipe.actionA()
            );

            ITEM_INPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.ingredientB()
            );

            ItemIngredientAction.STREAM_CODEC.encode(
                    buffer,
                    recipe.actionB()
            );

            OUTPUT_LIST_STREAM_CODEC.encode(
                    buffer,
                    recipe.outputs()
            );

            SOUND_OUTPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.successSound()
            );

            SOUND_OUTPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.failSound()
            );

            buffer.writeBoolean(
                    recipe.requireSneaking()
            );
        }

        private static HandInteractionRecipe decode(
                RegistryFriendlyByteBuf buffer
        ) {
            return new HandInteractionRecipe(
                    ITEM_INPUT_STREAM_CODEC.decode(buffer),
                    ItemIngredientAction.STREAM_CODEC.decode(buffer),
                    ITEM_INPUT_STREAM_CODEC.decode(buffer),
                    ItemIngredientAction.STREAM_CODEC.decode(buffer),
                    OUTPUT_LIST_STREAM_CODEC.decode(buffer),
                    SOUND_OUTPUT_STREAM_CODEC.decode(buffer),
                    SOUND_OUTPUT_STREAM_CODEC.decode(buffer),
                    buffer.readBoolean()
            );
        }

        @Override
        public @NotNull MapCodec<HandInteractionRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<
                RegistryFriendlyByteBuf,
                HandInteractionRecipe
                > streamCodec() {
            return STREAM_CODEC;
        }

        public static DataResult<HandInteractionRecipe> validate(
                HandInteractionRecipe recipe
        ) {
            DataResult<HandInteractionRecipe> base =
                    RecipeValidation.validate(recipe)
                            .require(
                                    recipe.ingredientA(),
                                    INGREDIENT_A_KEY
                            )
                            .require(
                                    recipe.actionA(),
                                    ACTION_A_KEY
                            )
                            .require(
                                    recipe.ingredientB(),
                                    INGREDIENT_B_KEY
                            )
                            .require(
                                    recipe.actionB(),
                                    ACTION_B_KEY
                            )
                            .require(
                                    recipe.outputs(),
                                    RESULTS_KEY
                            )
                            .require(
                                    recipe.successSound(),
                                    SUCCESS_SOUND_KEY
                            )
                            .require(
                                    recipe.failSound(),
                                    FAIL_SOUND_KEY
                            )
                            .rule(
                                    !recipe.outputs().isEmpty(),
                                    () -> RESULTS_KEY
                                            + " must contain at least one output"
                            )
                            .done();

            if (base.error().isPresent()) {
                return base;
            }

            DataResult<ItemIngredientAction> actionAResult =
                    recipe.actionA().validate();

            if (actionAResult.error().isPresent()) {
                String message = actionAResult.error()
                        .map(DataResult.Error::message)
                        .orElse("invalid ingredient A action");

                return DataResult.error(() ->
                        ACTION_A_KEY + " invalid: " + message
                );
            }

            DataResult<ItemIngredientAction> actionBResult =
                    recipe.actionB().validate();

            if (actionBResult.error().isPresent()) {
                String message = actionBResult.error()
                        .map(DataResult.Error::message)
                        .orElse("invalid ingredient B action");

                return DataResult.error(() ->
                        ACTION_B_KEY + " invalid: " + message
                );
            }

            for (int index = 0;
                 index < recipe.outputs().size();
                 index++) {

                RecipeOutput output =
                        recipe.outputs().get(index);

                if (output == null) {
                    int invalidIndex = index;

                    return DataResult.error(() ->
                            RESULTS_KEY
                                    + "["
                                    + invalidIndex
                                    + "] is required"
                    );
                }
            }

            return DataResult.success(recipe);
        }
    }
}