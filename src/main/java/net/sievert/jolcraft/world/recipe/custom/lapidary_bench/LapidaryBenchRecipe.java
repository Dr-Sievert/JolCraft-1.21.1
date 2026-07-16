package net.sievert.jolcraft.world.recipe.custom.lapidary_bench;

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
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.player.attachment.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.input.ItemInput;
import net.sievert.jolcraft.world.recipe.output.ItemOutput;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record LapidaryBenchRecipe(
        ItemInput input,
        ItemInput tool,
        ItemOutput result,
        SoundOutput sound,
        NumberProvider xp,
        NumberProvider toolDamage
) implements CustomRecipe<LapidaryRecipeInput> {

    private static final String TOOL_DAMAGE_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.TOOL,
                    JolCraftDictionary.DAMAGE
            );

    private static final LootContextParamSet INPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(JolCraftRecipeContextParams.INPUT_ITEM)
                    .build();

    @Override
    public boolean matches(
            @NotNull LapidaryRecipeInput recipeInput,
            @NotNull Level level
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        ItemStack inputStack = recipeInput.input();
        ItemStack toolStack = recipeInput.tool();

        if (inputStack.isEmpty() || toolStack.isEmpty()) {
            return false;
        }

        if (!matchesInput(serverLevel, inputStack, input)) {
            return false;
        }

        if (!matchesInput(serverLevel, toolStack, tool)) {
            return false;
        }

        boolean hammer =
                toolStack.is(JolCraftTags.Items.ARTISAN_HAMMERS);

        boolean chisel =
                toolStack.is(JolCraftTags.Items.CHISELS);

        if (hammer) {
            return inputStack.is(JolCraftTags.Items.GEODES)
                    || inputStack.is(JolCraftTags.Items.GEMS_UNCUT);
        }

        if (chisel) {
            return inputStack.is(JolCraftTags.Items.GEMS_UNCUT);
        }

        return false;
    }

    /**
     * Player-specific requirement which cannot be checked by
     * Recipe.matches(...), because vanilla does not provide the player there.
     */
    public boolean isUnlockedFor(
            @NotNull ServerPlayer player,
            @NotNull ItemStack toolStack
    ) {
        if (!toolStack.is(JolCraftTags.Items.CHISELS)) {
            return true;
        }

        return DwarfLoreAttachmentHelper.hasUnlock(
                player,
                DwarfLoreKey.ANCIENT_GEMCRAFT
        );
    }

    /**
     * Generates the recipe's item result.
     *
     * The caller decides whether generated stacks are inserted, dropped,
     * or otherwise handled.
     */
    public void generateResult(
            @NotNull LootContext context,
            @NotNull Consumer<ItemStack> output
    ) {
        result.generate(context, output);
    }

    public int rollXp(@NotNull LootContext context) {
        return Math.max(0, xp.getInt(context));
    }

    public int rollToolDamage(@NotNull LootContext context) {
        return Math.max(0, toolDamage.getInt(context));
    }

    public void generateSound(
            @NotNull LootContext context,
            @NotNull Consumer<SoundOutput.GeneratedSound> output
    ) {
        sound.generate(context, output);
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<LapidaryRecipeInput>>
    getSerializer() {
        return JolCraftRecipes.LAPIDARY_BENCH_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<LapidaryRecipeInput>>
    getType() {
        return JolCraftRecipes.LAPIDARY_BENCH_TYPE.get();
    }

    private static boolean matchesInput(
            ServerLevel level,
            ItemStack stack,
            ItemInput input
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
            implements RecipeSerializer<LapidaryBenchRecipe> {

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                ItemInput
                > ITEM_INPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(ItemInput.CODEC);

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

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                NumberProvider
                > NUMBER_PROVIDER_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        NumberProviders.CODEC
                );

        public static final MapCodec<LapidaryBenchRecipe> CODEC =
                RecordCodecBuilder.<LapidaryBenchRecipe>mapCodec(instance ->
                        instance.group(
                                ItemInput.CODEC
                                        .fieldOf(JolCraftDictionary.INPUT)
                                        .forGetter(LapidaryBenchRecipe::input),

                                ItemInput.CODEC
                                        .fieldOf(JolCraftDictionary.TOOL)
                                        .forGetter(LapidaryBenchRecipe::tool),

                                ItemOutput.CODEC
                                        .codec()
                                        .fieldOf(JolCraftDictionary.RESULT)
                                        .forGetter(LapidaryBenchRecipe::result),

                                SoundOutput.CODEC
                                        .codec()
                                        .fieldOf(JolCraftDictionary.SOUND)
                                        .forGetter(LapidaryBenchRecipe::sound),

                                NumberProviders.CODEC
                                        .optionalFieldOf(
                                                JolCraftDictionary.XP,
                                                ConstantValue.exactly(0.0F)
                                        )
                                        .forGetter(LapidaryBenchRecipe::xp),

                                NumberProviders.CODEC
                                        .optionalFieldOf(
                                                TOOL_DAMAGE_KEY,
                                                ConstantValue.exactly(1.0F)
                                        )
                                        .forGetter(LapidaryBenchRecipe::toolDamage)
                        ).apply(
                                instance,
                                LapidaryBenchRecipe::new
                        )
                ).flatXmap(
                        Serializer::validate,
                        DataResult::success
                );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                LapidaryBenchRecipe
                > STREAM_CODEC = StreamCodec.composite(
                ITEM_INPUT_STREAM_CODEC,
                LapidaryBenchRecipe::input,

                ITEM_INPUT_STREAM_CODEC,
                LapidaryBenchRecipe::tool,

                ITEM_OUTPUT_STREAM_CODEC,
                LapidaryBenchRecipe::result,

                SOUND_OUTPUT_STREAM_CODEC,
                LapidaryBenchRecipe::sound,

                NUMBER_PROVIDER_STREAM_CODEC,
                LapidaryBenchRecipe::xp,

                NUMBER_PROVIDER_STREAM_CODEC,
                LapidaryBenchRecipe::toolDamage,

                LapidaryBenchRecipe::new
        );

        @Override
        public @NotNull MapCodec<LapidaryBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<
                RegistryFriendlyByteBuf,
                LapidaryBenchRecipe
                > streamCodec() {
            return STREAM_CODEC;
        }

        public static DataResult<LapidaryBenchRecipe> validate(
                LapidaryBenchRecipe recipe
        ) {
            return RecipeValidation.validate(recipe)
                    .require(
                            recipe.input(),
                            JolCraftDictionary.INPUT
                    )
                    .require(
                            recipe.tool(),
                            JolCraftDictionary.TOOL
                    )
                    .require(
                            recipe.result(),
                            JolCraftDictionary.RESULT
                    )
                    .require(
                            recipe.sound(),
                            JolCraftDictionary.SOUND
                    )
                    .require(
                            recipe.xp(),
                            JolCraftDictionary.XP
                    )
                    .require(
                            recipe.toolDamage(),
                            TOOL_DAMAGE_KEY
                    )
                    .done();
        }
    }
}