package net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.custom.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.world.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record FermentingCauldronRecipe(
        ItemInput ingredient,
        Optional<ItemSelector> lastIngredient,
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

    public FermentingCauldronRecipe {
        if (ingredient == null) {
            throw new IllegalArgumentException("ingredient is required");
        }

        lastIngredient = lastIngredient != null ? lastIngredient : Optional.empty();
        extract = extract != null ? extract : Optional.empty();
        effect = effect != null ? effect : Optional.empty();

        if (brewTicks < 1) {
            brewTicks = DEFAULT_BREW_TICKS;
        }
        if (bubbleTicks < 1) {
            bubbleTicks = DEFAULT_BUBBLE_TICKS;
        }
    }

    @Override
    public boolean matches(@NotNull FermentingCauldronRecipeInput in, @NotNull Level level) {
        if (level.isClientSide) {
            return false;
        }

        WorldContext ctx = in.ctx();

        if (!ingredient.matches(ctx, in.ingredient())) {
            return false;
        }

        return lastIngredient.map(selector ->
                !in.lastIngredient().isEmpty() && selector.matches(ctx, in.lastIngredient())
        ).orElseGet(() -> in.lastIngredient().isEmpty());
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull FermentingCauldronRecipeInput in,
            HolderLookup.@NotNull Provider registries
    ) {
        WorldContext ctx = in.ctx();
        if (ctx.level().isClientSide) {
            return ItemStack.EMPTY;
        }

        if (extract.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemOutput out = extract.get();
        ItemSpec spec = out.result();

        ItemStack stack = spec.create(ctx);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        out.transforms().apply(ctx, stack);
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<FermentingCauldronRecipeInput>> getSerializer() {
        return JolCraftRecipes.FERMENTING_CAULDRON_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<FermentingCauldronRecipeInput>> getType() {
        return JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return ItemStack.EMPTY;
    }

    public static final class Serializer implements RecipeSerializer<FermentingCauldronRecipe> {

        private static final Codec<Integer> POSITIVE_TICKS =
                Codec.intRange(1, Integer.MAX_VALUE);

        private static final Codec<Integer> COLOR_CODEC =
                Codec.either(Codec.INT, Codec.STRING).comapFlatMap(
                        either -> either.map(
                                DataResult::success,
                                s -> {
                                    if (s == null || s.isBlank()) {
                                        return DataResult.error(() -> "Invalid color");
                                    }

                                    String t = s.trim();
                                    if (t.startsWith("#")) {
                                        t = t.substring(1);
                                    }

                                    if (t.length() != 6 && t.length() != 8) {
                                        return DataResult.error(() -> "Invalid color: " + s);
                                    }

                                    try {
                                        return DataResult.success((int) Long.parseLong(t, 16));
                                    } catch (NumberFormatException e) {
                                        return DataResult.error(() -> "Invalid color: " + s);
                                    }
                                }
                        ),
                        Either::left
                );

        private static final Codec<ItemOutput> EXTRACT_CODEC =
                OutputParam.CODEC.comapFlatMap(
                        Serializer::requireItemOutputResult,
                        io -> io
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Integer> VAR_INT_STREAM_CODEC =
                StreamCodec.of(
                        RegistryFriendlyByteBuf::writeVarInt,
                        RegistryFriendlyByteBuf::readVarInt
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Integer> INT_STREAM_CODEC =
                StreamCodec.of(
                        RegistryFriendlyByteBuf::writeInt,
                        RegistryFriendlyByteBuf::readInt
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Boolean> BOOL_STREAM_CODEC =
                StreamCodec.of(
                        RegistryFriendlyByteBuf::writeBoolean,
                        RegistryFriendlyByteBuf::readBoolean
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> EXTRACT_STREAM_CODEC =
                StreamCodec.of(
                        OutputParam.STREAM_CODEC::encode,
                        buf -> requireItemOutput(OutputParam.STREAM_CODEC.decode(buf))
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemOutput>> OPTIONAL_EXTRACT_STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> {
                            BOOL_STREAM_CODEC.encode(buf, value.isPresent());
                            value.ifPresent(v -> EXTRACT_STREAM_CODEC.encode(buf, v));
                        },
                        buf -> BOOL_STREAM_CODEC.decode(buf)
                                ? Optional.of(EXTRACT_STREAM_CODEC.decode(buf))
                                : Optional.empty()
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Optional<EffectOutput>> OPTIONAL_EFFECT_STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> {
                            BOOL_STREAM_CODEC.encode(buf, value.isPresent());
                            value.ifPresent(v -> EffectOutput.STREAM_CODEC.encode(buf, v));
                        },
                        buf -> BOOL_STREAM_CODEC.decode(buf)
                                ? Optional.of(EffectOutput.STREAM_CODEC.decode(buf))
                                : Optional.empty()
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemSelector>> OPTIONAL_LAST_INGREDIENT_STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> {
                            BOOL_STREAM_CODEC.encode(buf, value.isPresent());
                            value.ifPresent(v -> ItemSelector.STREAM_CODEC.encode(buf, v));
                        },
                        buf -> BOOL_STREAM_CODEC.decode(buf)
                                ? Optional.of(ItemSelector.STREAM_CODEC.decode(buf))
                                : Optional.empty()
                );

        private static final String LAST_INGREDIENT_KEY =
                JolCraftStrings.underscored(JolCraftDictionary.LAST, JolCraftDictionary.INGREDIENT);

        private static final String BREW_TICKS_KEY =
                JolCraftStrings.underscored(JolCraftDictionary.BREW, JolCraftStrings.plural(JolCraftDictionary.TICK));

        private static final String BUBBLE_TICKS_KEY =
                JolCraftStrings.underscored(JolCraftDictionary.BUBBLE, JolCraftStrings.plural(JolCraftDictionary.TICK));

        public static final MapCodec<FermentingCauldronRecipe> CODEC =
                RecordCodecBuilder.mapCodec(
                        (RecordCodecBuilder.Instance<FermentingCauldronRecipe> inst) ->
                                inst.group(
                                        ItemInput.CODEC
                                                .fieldOf(JolCraftDictionary.INGREDIENT)
                                                .forGetter(FermentingCauldronRecipe::ingredient),

                                        ItemSelector.CODEC
                                                .optionalFieldOf(LAST_INGREDIENT_KEY)
                                                .forGetter(FermentingCauldronRecipe::lastIngredient),

                                        EXTRACT_CODEC
                                                .optionalFieldOf(JolCraftDictionary.EXTRACT)
                                                .forGetter(FermentingCauldronRecipe::extract),

                                        EffectOutput.CODEC
                                                .optionalFieldOf(JolCraftDictionary.EFFECT)
                                                .forGetter(FermentingCauldronRecipe::effect),

                                        POSITIVE_TICKS
                                                .optionalFieldOf(BREW_TICKS_KEY, DEFAULT_BREW_TICKS)
                                                .forGetter(FermentingCauldronRecipe::brewTicks),

                                        POSITIVE_TICKS
                                                .optionalFieldOf(BUBBLE_TICKS_KEY, DEFAULT_BUBBLE_TICKS)
                                                .forGetter(FermentingCauldronRecipe::bubbleTicks),

                                        COLOR_CODEC
                                                .optionalFieldOf(JolCraftDictionary.COLOR, DEFAULT_BREW_COLOR)
                                                .forGetter(FermentingCauldronRecipe::brewColor),

                                        Codec.BOOL
                                                .optionalFieldOf(JolCraftDictionary.FINALIZE, DEFAULT_FINALIZE_BREW)
                                                .forGetter(FermentingCauldronRecipe::finalizeBrew)
                                ).apply(inst, FermentingCauldronRecipe::new)
                ).validate(FermentingCauldronRecipe::validateRecipe);

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingCauldronRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            ItemInput.STREAM_CODEC.encode(buf, recipe.ingredient());
                            OPTIONAL_LAST_INGREDIENT_STREAM_CODEC.encode(buf, recipe.lastIngredient());
                            OPTIONAL_EXTRACT_STREAM_CODEC.encode(buf, recipe.extract());
                            OPTIONAL_EFFECT_STREAM_CODEC.encode(buf, recipe.effect());
                            VAR_INT_STREAM_CODEC.encode(buf, recipe.brewTicks());
                            VAR_INT_STREAM_CODEC.encode(buf, recipe.bubbleTicks());
                            INT_STREAM_CODEC.encode(buf, recipe.brewColor());
                            BOOL_STREAM_CODEC.encode(buf, recipe.finalizeBrew());
                        },
                        buf -> new FermentingCauldronRecipe(
                                ItemInput.STREAM_CODEC.decode(buf),
                                OPTIONAL_LAST_INGREDIENT_STREAM_CODEC.decode(buf),
                                OPTIONAL_EXTRACT_STREAM_CODEC.decode(buf),
                                OPTIONAL_EFFECT_STREAM_CODEC.decode(buf),
                                VAR_INT_STREAM_CODEC.decode(buf),
                                VAR_INT_STREAM_CODEC.decode(buf),
                                INT_STREAM_CODEC.decode(buf),
                                BOOL_STREAM_CODEC.decode(buf)
                        )
                );

        @Override
        public @NotNull MapCodec<FermentingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FermentingCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static @NotNull DataResult<ItemOutput> requireItemOutputResult(OutputParam param) {
            if (param instanceof ItemOutput io) {
                return DataResult.success(io);
            }
            return DataResult.error(() ->
                    "extract must decode to item_output for fermenting cauldron recipes"
            );
        }

        private static @NotNull ItemOutput requireItemOutput(OutputParam param) {
            return requireItemOutputResult(param)
                    .result()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "extract must decode to item_output for fermenting cauldron recipes"
                            ));
        }
    }

    public static @NotNull DataResult<FermentingCauldronRecipe> validateRecipe(FermentingCauldronRecipe r) {
        DataResult<FermentingCauldronRecipe> rr = RecipeValidation.requireRecipe(r);
        var rrErr = rr.error();
        if (rrErr.isPresent()) {
            String msg = rrErr.map(DataResult.Error::message).orElse("recipe is null");
            return DataResult.error(() -> msg);
        }

        FermentingCauldronRecipe recipe = rr.result().orElse(null);
        if (recipe == null) {
            return DataResult.error(() -> "recipe is null");
        }

        final String lastIngredientKey =
                JolCraftStrings.underscored(JolCraftDictionary.LAST, JolCraftDictionary.INGREDIENT);

        var v = RecipeValidation.validate(recipe)
                .requireValid(recipe.ingredient(), JolCraftDictionary.INGREDIENT)
                .rule(recipe.brewTicks() >= 1, () -> "brew_ticks must be >= 1")
                .rule(recipe.bubbleTicks() >= 1, () -> "bubble_ticks must be >= 1");

        if (recipe.lastIngredient().isPresent()) {
            v.check(recipe.lastIngredient().get().validate().map(x -> recipe), lastIngredientKey);
        }

        if (recipe.extract().isPresent()) {
            v.check(recipe.extract().get().validate().map(x -> recipe), JolCraftDictionary.EXTRACT);
        }

        if (recipe.extract().isPresent() && recipe.extract().get().transforms().requiresInputSource()) {
            return DataResult.error(() ->
                    "this recipe type does not support input-sourced component transforms");
        }

        v.rule(
                recipe.lastIngredient().isPresent() || recipe.extract().isEmpty(),
                () -> "extract requires last_ingredient (water cauldron cannot extract)"
        );

        if (recipe.effect().isPresent()) {
            v.check(recipe.effect().get().validate().map(x -> recipe), JolCraftDictionary.EFFECT);
        }

        return v.done();
    }
}