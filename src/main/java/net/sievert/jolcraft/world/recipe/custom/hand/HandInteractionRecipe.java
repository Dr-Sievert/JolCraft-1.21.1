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
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.base.input.ItemInputAction;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.JolCraftRecipeOutputTypes;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record HandInteractionRecipe(
        ItemInput ingredientA,
        ItemInputAction actionA,
        ItemInput ingredientB,
        ItemInputAction actionB,
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

    private static final LootContextParamSet OUTPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(JolCraftRecipeContextParams.INPUT_ITEM)
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

        return (
                matchesOrdered(
                        serverLevel,
                        stackA,
                        stackB
                )
                        && actionsSatisfied(
                        stackA,
                        stackB
                )
        ) || (
                matchesOrdered(
                        serverLevel,
                        stackB,
                        stackA
                )
                        && actionsSatisfied(
                        stackB,
                        stackA
                )
        );
    }

    public boolean matchesOrdered(
            @NotNull ServerLevel level,
            @NotNull ItemStack stackA,
            @NotNull ItemStack stackB
    ) {
        return !stackA.isEmpty()
                && !stackB.isEmpty()
                && matchesInput(
                level,
                stackA,
                ingredientA
        )
                && matchesInput(
                level,
                stackB,
                ingredientB
        );
    }

    public boolean actionsSatisfied(
            @NotNull ItemStack stackA,
            @NotNull ItemStack stackB
    ) {
        return actionA.isSatisfied(stackA)
                && actionB.isSatisfied(stackB);
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

                                        ItemInputAction.CODEC
                                                .optionalFieldOf(
                                                        ACTION_A_KEY,
                                                        ItemInputAction.CATALYST
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

                                        ItemInputAction.CODEC
                                                .optionalFieldOf(
                                                        ACTION_B_KEY,
                                                        ItemInputAction.CATALYST
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

            ItemInputAction.STREAM_CODEC.encode(
                    buffer,
                    recipe.actionA()
            );

            ITEM_INPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.ingredientB()
            );

            ItemInputAction.STREAM_CODEC.encode(
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
                    ItemInputAction.STREAM_CODEC.decode(buffer),
                    ITEM_INPUT_STREAM_CODEC.decode(buffer),
                    ItemInputAction.STREAM_CODEC.decode(buffer),
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

            DataResult<ItemInputAction> actionAResult =
                    recipe.actionA().validate();

            if (actionAResult.error().isPresent()) {
                String message =
                        actionAResult.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid ingredient A action");

                return DataResult.error(() ->
                        ACTION_A_KEY
                                + " invalid: "
                                + message
                );
            }

            DataResult<ItemInputAction> actionBResult =
                    recipe.actionB().validate();

            if (actionBResult.error().isPresent()) {
                String message =
                        actionBResult.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid ingredient B action");

                return DataResult.error(() ->
                        ACTION_B_KEY
                                + " invalid: "
                                + message
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

                DataResult<Void> outputValidation =
                        RecipeValidation.validateOutput(
                                output,
                                OUTPUT_CONTEXT_PARAMS
                        );

                if (outputValidation.error().isPresent()) {
                    int invalidIndex = index;

                    String message =
                            outputValidation.error()
                                    .map(DataResult.Error::message)
                                    .orElse("invalid output");

                    return DataResult.error(() ->
                            RESULTS_KEY
                                    + "["
                                    + invalidIndex
                                    + "]: "
                                    + message
                    );
                }
            }

            DataResult<Void> successSoundValidation =
                    RecipeValidation.validateOutput(
                            recipe.successSound(),
                            OUTPUT_CONTEXT_PARAMS
                    );

            if (successSoundValidation.error().isPresent()) {
                String message =
                        successSoundValidation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid success sound");

                return DataResult.error(() ->
                        SUCCESS_SOUND_KEY
                                + ": "
                                + message
                );
            }

            DataResult<Void> failSoundValidation =
                    RecipeValidation.validateOutput(
                            recipe.failSound(),
                            OUTPUT_CONTEXT_PARAMS
                    );

            if (failSoundValidation.error().isPresent()) {
                String message =
                        failSoundValidation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid fail sound");

                return DataResult.error(() ->
                        FAIL_SOUND_KEY
                                + ": "
                                + message
                );
            }

            return DataResult.success(recipe);
        }
    }
}