package net.sievert.jolcraft.data.recipe.custom.fermenting_cauldron;

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
import net.sievert.jolcraft.data.recipe.JolCraftRecipeValidation;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.CustomRecipe;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputDispatch;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.EffectOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

public record FermentingCauldronRecipe(
        ItemInput ingredient,
        ItemSelector lastIngredient,
        ItemOutput extract,
        EffectOutput effect,
        int brewTicks,
        int bubbleTicks,
        int brewColor,
        boolean finalizeBrew
) implements CustomRecipe<FermentingCauldronRecipeInput> {

    // ---------------------------------------------------------------------
    // Sentinel
    // ---------------------------------------------------------------------

    public static final FermentingCauldronRecipe EMPTY =
            new FermentingCauldronRecipe(
                    ItemInput.EMPTY,
                    ItemSelector.EMPTY,
                    ItemOutput.EMPTY,
                    EffectOutput.EMPTY,
                    1,
                    1,
                    0,
                    false
            );

    public FermentingCauldronRecipe {
        ingredient     = ingredient != null ? ingredient : ItemInput.EMPTY;
        lastIngredient = lastIngredient != null ? lastIngredient : ItemSelector.EMPTY;
        extract        = extract != null ? extract : ItemOutput.EMPTY;
        effect         = effect != null ? effect : EffectOutput.EMPTY;

        brewTicks   = Math.max(1, brewTicks);
        bubbleTicks = Math.max(1, bubbleTicks);
    }

    // ---------------------------------------------------------------------
    // Recipe
    // ---------------------------------------------------------------------

    @Override
    public boolean matches(@NotNull FermentingCauldronRecipeInput in, @NotNull Level level) {

        WorldContext ctx = in.ctx();

        if (!ingredient.matches(ctx, in.ingredient())) return false;

        if (lastIngredient != ItemSelector.EMPTY) {
            return !in.lastIngredient().isEmpty() && lastIngredient.matches(ctx, in.lastIngredient());
        }

        return in.lastIngredient().isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull FermentingCauldronRecipeInput in, HolderLookup.@NotNull Provider registries) {
        WorldContext ctx = in.ctx();
        if (ctx.level().isClientSide) return ItemStack.EMPTY;

        ItemOutput out = extract;
        if (out == null || out == ItemOutput.EMPTY) return ItemStack.EMPTY;

        ItemSpec spec = out.result();
        if (spec == null || spec == ItemSpec.EMPTY) return ItemStack.EMPTY;

        ItemStack stack = spec.create(ctx);
        if (stack.isEmpty()) return ItemStack.EMPTY;

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

    // =====================================================================
    // Serializer
    // =====================================================================

    public static final class Serializer implements RecipeSerializer<FermentingCauldronRecipe> {


        private static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> EXTRACT_STREAM_CODEC =
                StreamCodec.of(
                        OutputDispatch.STREAM_CODEC::encode,
                        buf -> {
                            OutputParam op = OutputDispatch.STREAM_CODEC.decode(buf);
                            OutputParam leaf = OutputParam.unwrap(op);
                            return (leaf instanceof ItemOutput io) ? io : ItemOutput.EMPTY;
                        }
                );

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
                                    if (t.startsWith("#")) t = t.substring(1);

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
                                                .optionalFieldOf(
                                                        LAST_INGREDIENT_KEY,
                                                        ItemSelector.EMPTY
                                                )
                                                .forGetter(FermentingCauldronRecipe::lastIngredient),

                                        OutputDispatch.CODEC
                                                .optionalFieldOf(
                                                        JolCraftDictionary.EXTRACT,
                                                        ItemOutput.EMPTY
                                                )
                                                .xmap(
                                                        op -> {
                                                            OutputParam leaf = OutputParam.unwrap(op);
                                                            return (leaf instanceof ItemOutput io) ? io : ItemOutput.EMPTY;
                                                        },
                                                        io -> io != null ? io : ItemOutput.EMPTY
                                                )
                                                .forGetter(FermentingCauldronRecipe::extract),

                                        EffectOutput.CODEC
                                                .optionalFieldOf(
                                                        JolCraftDictionary.EFFECT,
                                                        EffectOutput.EMPTY
                                                )
                                                .forGetter(FermentingCauldronRecipe::effect),

                                        POSITIVE_TICKS
                                                .fieldOf(BREW_TICKS_KEY)
                                                .forGetter(FermentingCauldronRecipe::brewTicks),

                                        POSITIVE_TICKS
                                                .fieldOf(BUBBLE_TICKS_KEY)
                                                .forGetter(FermentingCauldronRecipe::bubbleTicks),

                                        COLOR_CODEC
                                                .fieldOf(JolCraftDictionary.COLOR)
                                                .forGetter(FermentingCauldronRecipe::brewColor),

                                        Codec.BOOL
                                                .optionalFieldOf(JolCraftDictionary.FINALIZE, false)
                                                .forGetter(FermentingCauldronRecipe::finalizeBrew)

                                ).apply(inst, FermentingCauldronRecipe::new)
                ).validate(FermentingCauldronRecipe::validateRecipe);

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingCauldronRecipe> STREAM_CODEC =
                StreamCodec.composite(

                        ItemInput.STREAM_CODEC,
                        FermentingCauldronRecipe::ingredient,

                        ItemSelector.STREAM_CODEC,
                        FermentingCauldronRecipe::lastIngredient,

                        EXTRACT_STREAM_CODEC,
                        FermentingCauldronRecipe::extract,

                        EffectOutput.STREAM_CODEC,
                        FermentingCauldronRecipe::effect,

                        StreamCodec.of(
                                RegistryFriendlyByteBuf::writeVarInt,
                                RegistryFriendlyByteBuf::readVarInt
                        ),
                        FermentingCauldronRecipe::brewTicks,

                        StreamCodec.of(
                                RegistryFriendlyByteBuf::writeVarInt,
                                RegistryFriendlyByteBuf::readVarInt
                        ),
                        FermentingCauldronRecipe::bubbleTicks,

                        StreamCodec.of(
                                RegistryFriendlyByteBuf::writeInt,
                                RegistryFriendlyByteBuf::readInt
                        ),
                        FermentingCauldronRecipe::brewColor,

                        StreamCodec.of(
                                RegistryFriendlyByteBuf::writeBoolean,
                                RegistryFriendlyByteBuf::readBoolean
                        ),
                        FermentingCauldronRecipe::finalizeBrew,

                        FermentingCauldronRecipe::new
                );

        @Override
        public @NotNull MapCodec<FermentingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FermentingCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    public static @NotNull DataResult<FermentingCauldronRecipe> validateRecipe(FermentingCauldronRecipe r) {

        // ---- recipe required ----
        DataResult<FermentingCauldronRecipe> rr = JolCraftRecipeValidation.requireRecipe(r);
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

        var v = JolCraftRecipeValidation.validate(recipe)

                .requireValid(recipe.ingredient(), JolCraftDictionary.INGREDIENT)

                .rule(recipe.brewTicks() >= 1, () -> "brew_ticks must be >= 1")
                .rule(recipe.bubbleTicks() >= 1, () -> "bubble_ticks must be >= 1");

        if (recipe.lastIngredient() != ItemSelector.EMPTY) {
            v.check(recipe.lastIngredient().validate().map(x -> recipe), lastIngredientKey);
        }

        if (recipe.extract() != ItemOutput.EMPTY) {
            v.check(recipe.extract().validate().map(x -> recipe), JolCraftDictionary.EXTRACT);
        }

        ItemOutput out = r.extract();
        if (out.transforms().requiresInputSource()) {
            return DataResult.error(() ->
                    "this recipe type does not support input-sourced component transforms");
        }

        v.rule(
                recipe.lastIngredient() != ItemSelector.EMPTY || recipe.extract() == ItemOutput.EMPTY,
                () -> "extract requires last_ingredient (water cauldron cannot extract)"
        );

        if (recipe.effect() != EffectOutput.EMPTY) {
            v.check(recipe.effect().validate().map(x -> recipe), JolCraftDictionary.EFFECT);
        }

        return v.done();
    }
}