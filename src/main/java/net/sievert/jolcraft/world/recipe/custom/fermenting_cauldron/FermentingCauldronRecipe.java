package net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public record FermentingCauldronRecipe(
        ItemInput ingredient,
        Optional<ItemInput> lastIngredient,
        Optional<ItemOutput> extract,
        Optional<EffectOutput> effect,
        int brewTicks,
        int bubbleTicks,
        int brewColor,
        boolean finalizeBrew
) implements CustomRecipe<FermentingCauldronRecipeInput> {

    public static final int DEFAULT_BREW_TICKS = 1;
    public static final int DEFAULT_BUBBLE_TICKS = 1;
    public static final int DEFAULT_BREW_COLOR = -1;
    public static final boolean DEFAULT_FINALIZE_BREW = false;

    private static final String LAST_INGREDIENT_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LAST,
                    JolCraftDictionary.INGREDIENT
            );

    private static final String BREW_TICKS_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.BREW,
                    JolCraftStrings.plural(JolCraftDictionary.TICK)
            );

    private static final String BUBBLE_TICKS_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.BUBBLE,
                    JolCraftStrings.plural(JolCraftDictionary.TICK)
            );

    private static final LootContextParamSet INPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(JolCraftRecipeContextParams.INPUT_ITEM)
                    .build();

    public FermentingCauldronRecipe {
        Objects.requireNonNull(
                ingredient,
                JolCraftDictionary.INGREDIENT
        );

        lastIngredient = Objects.requireNonNullElse(
                lastIngredient,
                Optional.empty()
        );

        extract = Objects.requireNonNullElse(
                extract,
                Optional.empty()
        );

        effect = Objects.requireNonNullElse(
                effect,
                Optional.empty()
        );
    }

    @Override
    public boolean matches(
            @NotNull FermentingCauldronRecipeInput recipeInput,
            @NotNull Level level
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        ItemStack ingredientStack =
                recipeInput.ingredient();

        ItemStack lastIngredientStack =
                recipeInput.lastIngredient();

        if (ingredientStack.isEmpty()) {
            return false;
        }

        if (!matchesInput(
                serverLevel,
                ingredientStack,
                ingredient
        )) {
            return false;
        }

        if (lastIngredient.isPresent()) {
            if (lastIngredientStack.isEmpty()) {
                return false;
            }

            return matchesInput(
                    serverLevel,
                    lastIngredientStack,
                    lastIngredient.get()
            );
        }

        return lastIngredientStack.isEmpty();
    }

    /**
     * Generates extraction results.
     *
     * The cauldron executor decides whether generated stacks are inserted
     * into the player's inventory, returned to their hand, or dropped.
     */
    public void generateExtract(
            @NotNull LootContext context,
            @NotNull FermentingCauldronRecipeInput input,
            @NotNull Consumer<ItemStack> output
    ) {
        extract.ifPresent(value ->
                value.generate(
                        context,
                        input,
                        output
                )
        );
    }

    public void generateEffect(
            @NotNull LootContext context,
            @NotNull FermentingCauldronRecipeInput input,
            @NotNull Consumer<MobEffectInstance> output
    ) {
        effect.ifPresent(value ->
                value.generate(
                        context,
                        input,
                        output
                )
        );
    }

    public boolean hasExtract() {
        return extract.isPresent();
    }

    public boolean hasEffect() {
        return effect.isPresent();
    }

    @Override
    public @NotNull RecipeSerializer<
            ? extends Recipe<FermentingCauldronRecipeInput>
            > getSerializer() {
        return JolCraftRecipes.FERMENTING_CAULDRON_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<
            ? extends Recipe<FermentingCauldronRecipeInput>
            > getType() {
        return JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get();
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
            implements RecipeSerializer<FermentingCauldronRecipe> {

        private static final Codec<Integer> POSITIVE_TICKS =
                Codec.intRange(
                        1,
                        Integer.MAX_VALUE
                );

        private static final Codec<Integer> COLOR_CODEC =
                Codec.either(
                        Codec.INT,
                        Codec.STRING
                ).comapFlatMap(
                        either -> either.map(
                                DataResult::success,
                                Serializer::decodeColor
                        ),
                        Either::left
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                ItemInput
                > ITEM_INPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        ItemInput.CODEC
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                ItemOutput
                > ITEM_OUTPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        ItemOutput.CODEC.codec()
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                EffectOutput
                > EFFECT_OUTPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        EffectOutput.CODEC.codec()
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                Optional<ItemInput>
                > OPTIONAL_ITEM_INPUT_STREAM_CODEC =
                optional(ITEM_INPUT_STREAM_CODEC);

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                Optional<ItemOutput>
                > OPTIONAL_ITEM_OUTPUT_STREAM_CODEC =
                optional(ITEM_OUTPUT_STREAM_CODEC);

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                Optional<EffectOutput>
                > OPTIONAL_EFFECT_OUTPUT_STREAM_CODEC =
                optional(EFFECT_OUTPUT_STREAM_CODEC);

        public static final MapCodec<FermentingCauldronRecipe> CODEC =
                RecordCodecBuilder
                        .<FermentingCauldronRecipe>mapCodec(instance ->
                                instance.group(
                                        ItemInput.CODEC
                                                .fieldOf(
                                                        JolCraftDictionary.INGREDIENT
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::ingredient
                                                ),

                                        ItemInput.CODEC
                                                .optionalFieldOf(
                                                        LAST_INGREDIENT_KEY
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::lastIngredient
                                                ),

                                        ItemOutput.CODEC
                                                .codec()
                                                .optionalFieldOf(
                                                        JolCraftDictionary.EXTRACT
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::extract
                                                ),

                                        EffectOutput.CODEC
                                                .codec()
                                                .optionalFieldOf(
                                                        JolCraftDictionary.EFFECT
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::effect
                                                ),

                                        POSITIVE_TICKS
                                                .optionalFieldOf(
                                                        BREW_TICKS_KEY,
                                                        DEFAULT_BREW_TICKS
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::brewTicks
                                                ),

                                        POSITIVE_TICKS
                                                .optionalFieldOf(
                                                        BUBBLE_TICKS_KEY,
                                                        DEFAULT_BUBBLE_TICKS
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::bubbleTicks
                                                ),

                                        COLOR_CODEC
                                                .optionalFieldOf(
                                                        JolCraftDictionary.COLOR,
                                                        DEFAULT_BREW_COLOR
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::brewColor
                                                ),

                                        Codec.BOOL
                                                .optionalFieldOf(
                                                        JolCraftDictionary.FINALIZE,
                                                        DEFAULT_FINALIZE_BREW
                                                )
                                                .forGetter(
                                                        FermentingCauldronRecipe::finalizeBrew
                                                )
                                ).apply(
                                        instance,
                                        FermentingCauldronRecipe::new
                                )
                        ).flatXmap(
                                Serializer::validate,
                                DataResult::success
                        );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                FermentingCauldronRecipe
                > STREAM_CODEC = StreamCodec.of(
                Serializer::encode,
                Serializer::decode
        );

        @Override
        public @NotNull MapCodec<FermentingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<
                RegistryFriendlyByteBuf,
                FermentingCauldronRecipe
                > streamCodec() {
            return STREAM_CODEC;
        }

        public static DataResult<FermentingCauldronRecipe> validate(
                FermentingCauldronRecipe recipe
        ) {
            DataResult<FermentingCauldronRecipe> base =
                    RecipeValidation.validate(recipe)
                            .require(
                                    recipe.ingredient(),
                                    JolCraftDictionary.INGREDIENT
                            )
                            .require(
                                    recipe.lastIngredient(),
                                    LAST_INGREDIENT_KEY
                            )
                            .require(
                                    recipe.extract(),
                                    JolCraftDictionary.EXTRACT
                            )
                            .require(
                                    recipe.effect(),
                                    JolCraftDictionary.EFFECT
                            )
                            .rule(
                                    recipe.brewTicks() >= 1,
                                    () -> BREW_TICKS_KEY
                                            + " must be >= 1"
                            )
                            .rule(
                                    recipe.bubbleTicks() >= 1,
                                    () -> BUBBLE_TICKS_KEY
                                            + " must be >= 1"
                            )
                            .rule(
                                    recipe.lastIngredient().isPresent()
                                            || recipe.extract().isEmpty(),
                                    () -> JolCraftDictionary.EXTRACT
                                            + " requires "
                                            + LAST_INGREDIENT_KEY
                            )
                            .done();

            if (base.error().isPresent()) {
                return base;
            }

            if (recipe.extract().isPresent()) {
                DataResult<Void> extractValidation =
                        RecipeValidation.validateOutput(
                                recipe.extract().get(),
                                INPUT_CONTEXT_PARAMS
                        );

                if (extractValidation.error().isPresent()) {
                    String message =
                            extractValidation.error()
                                    .map(DataResult.Error::message)
                                    .orElse("invalid extract output");

                    return DataResult.error(() ->
                            JolCraftDictionary.EXTRACT
                                    + ": "
                                    + message
                    );
                }
            }

            if (recipe.effect().isPresent()) {
                DataResult<Void> effectValidation =
                        RecipeValidation.validateOutput(
                                recipe.effect().get(),
                                INPUT_CONTEXT_PARAMS
                        );

                if (effectValidation.error().isPresent()) {
                    String message =
                            effectValidation.error()
                                    .map(DataResult.Error::message)
                                    .orElse("invalid effect output");

                    return DataResult.error(() ->
                            JolCraftDictionary.EFFECT
                                    + ": "
                                    + message
                    );
                }
            }

            return DataResult.success(recipe);
        }

        private static void encode(
                RegistryFriendlyByteBuf buffer,
                FermentingCauldronRecipe recipe
        ) {
            ITEM_INPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.ingredient()
            );

            OPTIONAL_ITEM_INPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.lastIngredient()
            );

            OPTIONAL_ITEM_OUTPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.extract()
            );

            OPTIONAL_EFFECT_OUTPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.effect()
            );

            buffer.writeVarInt(recipe.brewTicks());
            buffer.writeVarInt(recipe.bubbleTicks());
            buffer.writeInt(recipe.brewColor());
            buffer.writeBoolean(recipe.finalizeBrew());
        }

        private static FermentingCauldronRecipe decode(
                RegistryFriendlyByteBuf buffer
        ) {
            return new FermentingCauldronRecipe(
                    ITEM_INPUT_STREAM_CODEC.decode(buffer),
                    OPTIONAL_ITEM_INPUT_STREAM_CODEC.decode(buffer),
                    OPTIONAL_ITEM_OUTPUT_STREAM_CODEC.decode(buffer),
                    OPTIONAL_EFFECT_OUTPUT_STREAM_CODEC.decode(buffer),
                    buffer.readVarInt(),
                    buffer.readVarInt(),
                    buffer.readInt(),
                    buffer.readBoolean()
            );
        }

        private static DataResult<Integer> decodeColor(
                String value
        ) {
            if (value == null || value.isBlank()) {
                return DataResult.error(() ->
                        "invalid color"
                );
            }

            String normalized = value.trim();

            if (normalized.startsWith("#")) {
                normalized = normalized.substring(1);
            }

            if (normalized.length() != 6
                    && normalized.length() != 8) {
                return DataResult.error(() ->
                        "invalid color: " + value
                );
            }

            try {
                return DataResult.success(
                        (int) Long.parseLong(
                                normalized,
                                16
                        )
                );
            } catch (NumberFormatException exception) {
                return DataResult.error(() ->
                        "invalid color: " + value
                );
            }
        }

        private static <T> StreamCodec<
                RegistryFriendlyByteBuf,
                Optional<T>
                > optional(
                StreamCodec<RegistryFriendlyByteBuf, T> codec
        ) {
            return StreamCodec.of(
                    (buffer, value) -> {
                        buffer.writeBoolean(value.isPresent());

                        value.ifPresent(element ->
                                codec.encode(buffer, element)
                        );
                    },
                    buffer -> {
                        if (!buffer.readBoolean()) {
                            return Optional.empty();
                        }

                        return Optional.of(
                                codec.decode(buffer)
                        );
                    }
            );
        }
    }
}