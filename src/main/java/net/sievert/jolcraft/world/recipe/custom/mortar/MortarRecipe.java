package net.sievert.jolcraft.world.recipe.custom.mortar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.attachment.player.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record MortarRecipe(
        List<ItemInput> inputs,
        ItemOutput result,
        SoundOutput sound,
        int grindingWork,
        int toolDamage
) implements CustomRecipe<MortarRecipeInput> {

    public static final int MAX_GRINDING_WORK = Short.MAX_VALUE;

    private static final String INPUTS_KEY =
            JolCraftStrings.plural(
                    JolCraftDictionary.INPUT
            );

    private static final String GRIND_PROGRESS_KEY = JolCraftStrings.underscored(JolCraftDictionary.GRIND, JolCraftDictionary.PROGRESS);

    private static final String TOOL_DAMAGE_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.TOOL,
                    JolCraftDictionary.DAMAGE
            );

    private static final LootContextParamSet INPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(
                            JolCraftRecipeContextParams.INPUT_ITEM
                    )
                    .build();

    private static final LootContextParamSet OUTPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .optional(LootContextParams.THIS_ENTITY)
                    .optional(LootContextParams.ORIGIN)
                    .build();

    public MortarRecipe {
        inputs = List.copyOf(inputs);
    }

    @Override
    public boolean matches(
            @NotNull MortarRecipeInput recipeInput,
            @NotNull Level level
    ) {
        return resolveInputs(
                recipeInput,
                level
        ).isPresent();
    }

    public @NotNull Optional<List<MatchedInput>> resolveInputs(
            @NotNull MortarRecipeInput recipeInput,
            @NotNull Level level
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        if (!recipeInput.tool().is(
                JolCraftTags.Items.PESTLES
        )) {
            return Optional.empty();
        }

        List<SuppliedInput> suppliedInputs =
                getSuppliedInputs(recipeInput);

        if (suppliedInputs.size() != inputs.size()) {
            return Optional.empty();
        }

        int[] matchedRecipeInputs =
                new int[suppliedInputs.size()];

        Arrays.fill(
                matchedRecipeInputs,
                -1
        );

        int[][] matchingCounts =
                new int[suppliedInputs.size()][inputs.size()];

        for (int suppliedIndex = 0;
             suppliedIndex < suppliedInputs.size();
             suppliedIndex++) {
            ItemStack suppliedStack =
                    suppliedInputs.get(suppliedIndex).stack();

            for (int recipeIndex = 0;
                 recipeIndex < inputs.size();
                 recipeIndex++) {
                matchingCounts[suppliedIndex][recipeIndex] =
                        minimumMatchingCount(
                                serverLevel,
                                suppliedStack,
                                inputs.get(recipeIndex)
                        );
            }
        }

        if (!matchesUnordered(
                matchingCounts,
                new boolean[inputs.size()],
                matchedRecipeInputs,
                0
        )) {
            return Optional.empty();
        }

        List<MatchedInput> resolvedInputs =
                new ArrayList<>(suppliedInputs.size());

        for (int suppliedIndex = 0;
             suppliedIndex < suppliedInputs.size();
             suppliedIndex++) {
            SuppliedInput suppliedInput =
                    suppliedInputs.get(suppliedIndex);

            int requiredCount =
                    matchingCounts[suppliedIndex]
                            [matchedRecipeInputs[suppliedIndex]];

            resolvedInputs.add(
                    new MatchedInput(
                            suppliedInput.slot(),
                            requiredCount
                    )
            );
        }

        return Optional.of(
                List.copyOf(resolvedInputs)
        );
    }

    /**
     * Player-specific requirement which cannot be checked by
     * Recipe.matches(...), because vanilla does not provide the player there.
     */
    public boolean isUnlockedFor(
            @NotNull ServerPlayer player
    ) {
        if (inputs.size() <= 1) {
            return true;
        }

        return DwarfLoreAttachmentHelper.hasUnlock(
                player,
                DwarfLoreKey.ALCHEMY_RECIPES
        );
    }

    public void generateResult(
            @NotNull LootContext context,
            @NotNull MortarRecipeInput input,
            @NotNull Consumer<ItemStack> output
    ) {
        result.generate(
                context,
                input,
                output
        );
    }

    public void generateSound(
            @NotNull LootContext context,
            @NotNull MortarRecipeInput input,
            @NotNull Consumer<SoundOutput.GeneratedSound> output
    ) {
        sound.generate(
                context,
                input,
                output
        );
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<MortarRecipeInput>>
    getSerializer() {
        return JolCraftRecipes.MORTAR_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<MortarRecipeInput>>
    getType() {
        return JolCraftRecipes.MORTAR_TYPE.get();
    }

    private boolean matchesUnordered(
            int[][] matchingCounts,
            boolean[] usedRecipeInputs,
            int[] matchedRecipeInputs,
            int suppliedIndex
    ) {
        if (suppliedIndex >= matchingCounts.length) {
            return true;
        }

        for (int recipeIndex = 0;
             recipeIndex < matchingCounts[suppliedIndex].length;
             recipeIndex++) {

            if (usedRecipeInputs[recipeIndex]) {
                continue;
            }

            if (matchingCounts[suppliedIndex][recipeIndex] <= 0) {
                continue;
            }

            usedRecipeInputs[recipeIndex] = true;
            matchedRecipeInputs[suppliedIndex] = recipeIndex;

            if (matchesUnordered(
                    matchingCounts,
                    usedRecipeInputs,
                    matchedRecipeInputs,
                    suppliedIndex + 1
            )) {
                return true;
            }

            usedRecipeInputs[recipeIndex] = false;
            matchedRecipeInputs[suppliedIndex] = -1;
        }

        return false;
    }

    private static List<SuppliedInput> getSuppliedInputs(
            MortarRecipeInput input
    ) {
        List<SuppliedInput> suppliedInputs =
                new ArrayList<>(3);

        if (!input.input1().isEmpty()) {
            suppliedInputs.add(
                    new SuppliedInput(
                            0,
                            input.input1()
                    )
            );
        }

        if (!input.input2().isEmpty()) {
            suppliedInputs.add(
                    new SuppliedInput(
                            1,
                            input.input2()
                    )
            );
        }

        if (!input.input3().isEmpty()) {
            suppliedInputs.add(
                    new SuppliedInput(
                            2,
                            input.input3()
                    )
            );
        }

        return suppliedInputs;
    }

    private static boolean matchesInput(
            ServerLevel level,
            ItemStack stack,
            ItemInput input
    ) {
        LootContext context =
                JolCraftRecipeContexts.create(
                        level,
                        INPUT_CONTEXT_PARAMS,
                        builder -> builder.withParameter(
                                JolCraftRecipeContextParams.INPUT_ITEM,
                                stack
                        )
                );

        return input.condition().test(context);
    }

    private static int minimumMatchingCount(
            ServerLevel level,
            ItemStack stack,
            ItemInput input
    ) {
        for (int count = 1;
             count <= stack.getCount();
             count++) {
            ItemStack candidate =
                    stack.copyWithCount(count);

            if (matchesInput(
                    level,
                    candidate,
                    input
            )) {
                return count;
            }
        }

        return 0;
    }

    public record MatchedInput(
            int slot,
            int count
    ) {

        public MatchedInput {
            if (slot < 0 || slot > 2) {
                throw new IllegalArgumentException(
                        "Mortar input slot must be between 0 and 2"
                );
            }

            if (count <= 0) {
                throw new IllegalArgumentException(
                        "Mortar input count must be positive"
                );
            }
        }
    }

    private record SuppliedInput(
            int slot,
            ItemStack stack
    ) {
    }

    public static final class Serializer
            implements RecipeSerializer<MortarRecipe> {

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                List<ItemInput>
                > INPUTS_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        ItemInput.CODEC.listOf()
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
                SoundOutput
                > SOUND_OUTPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        SoundOutput.CODEC.codec()
                );

        public static final MapCodec<MortarRecipe> CODEC =
                RecordCodecBuilder.<MortarRecipe>mapCodec(instance ->
                        instance.group(
                                ItemInput.CODEC
                                        .listOf()
                                        .fieldOf(INPUTS_KEY)
                                        .forGetter(MortarRecipe::inputs),

                                ItemOutput.CODEC
                                        .codec()
                                        .fieldOf(JolCraftDictionary.RESULT)
                                        .forGetter(MortarRecipe::result),

                                SoundOutput.CODEC
                                        .codec()
                                        .fieldOf(JolCraftDictionary.SOUND)
                                        .forGetter(MortarRecipe::sound),

                                Codec.intRange(1, MAX_GRINDING_WORK)
                                        .fieldOf(GRIND_PROGRESS_KEY)
                                        .forGetter(MortarRecipe::grindingWork),

                                Codec.intRange(0, Integer.MAX_VALUE)
                                        .optionalFieldOf(
                                                TOOL_DAMAGE_KEY,
                                                1
                                        )
                                        .forGetter(MortarRecipe::toolDamage)
                        ).apply(
                                instance,
                                MortarRecipe::new
                        )
                ).flatXmap(
                        Serializer::validate,
                        DataResult::success
                );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                MortarRecipe
                > STREAM_CODEC = StreamCodec.composite(
                INPUTS_STREAM_CODEC,
                MortarRecipe::inputs,

                ITEM_OUTPUT_STREAM_CODEC,
                MortarRecipe::result,

                SOUND_OUTPUT_STREAM_CODEC,
                MortarRecipe::sound,

                ByteBufCodecs.VAR_INT,
                MortarRecipe::grindingWork,

                ByteBufCodecs.VAR_INT,
                MortarRecipe::toolDamage,

                MortarRecipe::new
        );

        @Override
        public @NotNull MapCodec<MortarRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<
                RegistryFriendlyByteBuf,
                MortarRecipe
                > streamCodec() {
            return STREAM_CODEC;
        }

        public static DataResult<MortarRecipe> validate(
                MortarRecipe recipe
        ) {
            if (recipe.inputs().isEmpty()) {
                return DataResult.error(() ->
                        INPUTS_KEY
                                + ": at least one input is required"
                );
            }

            if (recipe.inputs().size() > 3) {
                return DataResult.error(() ->
                        INPUTS_KEY
                                + ": no more than three inputs are allowed"
                );
            }

            if (recipe.grindingWork() < 1
                    || recipe.grindingWork() > MAX_GRINDING_WORK) {
                return DataResult.error(() ->
                        GRIND_PROGRESS_KEY
                                + ": must be between 1 and "
                                + MAX_GRINDING_WORK
                );
            }

            if (recipe.toolDamage() < 0) {
                return DataResult.error(() ->
                        TOOL_DAMAGE_KEY
                                + ": cannot be negative"
                );
            }

            DataResult<MortarRecipe> base =
                    RecipeValidation.validate(recipe)
                            .require(
                                    recipe.result(),
                                    JolCraftDictionary.RESULT
                            )
                            .require(
                                    recipe.sound(),
                                    JolCraftDictionary.SOUND
                            )
                            .done();

            if (base.error().isPresent()) {
                return base;
            }

            DataResult<Void> resultValidation =
                    RecipeValidation.validateOutput(
                            recipe.result(),
                            OUTPUT_CONTEXT_PARAMS
                    );

            if (resultValidation.error().isPresent()) {
                String message =
                        resultValidation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid result output");

                return DataResult.error(() ->
                        JolCraftDictionary.RESULT
                                + ": "
                                + message
                );
            }

            DataResult<Void> soundValidation =
                    RecipeValidation.validateOutput(
                            recipe.sound(),
                            OUTPUT_CONTEXT_PARAMS
                    );

            if (soundValidation.error().isPresent()) {
                String message =
                        soundValidation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid sound output");

                return DataResult.error(() ->
                        JolCraftDictionary.SOUND
                                + ": "
                                + message
                );
            }

            return DataResult.success(recipe);
        }
    }
}
