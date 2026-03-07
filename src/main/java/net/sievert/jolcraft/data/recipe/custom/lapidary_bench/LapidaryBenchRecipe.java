package net.sievert.jolcraft.data.recipe.custom.lapidary_bench;

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
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfTomeUnlockHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.recipe.JolCraftRecipeValidation;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.CustomRecipe;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputDispatch;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

public record LapidaryBenchRecipe(
        ItemInput input,
        ItemSelector tool,
        ItemOutput result,
        SoundOutput sound,
        IntRange xp,
        IntRange toolDamage
) implements CustomRecipe<LapidaryRecipeInput> {

    @Override
    public boolean matches(@NotNull LapidaryRecipeInput in, Level level) {
        if (level.isClientSide) {
            return false;
        }

        WorldContext ctx = in.ctx();

        ItemStack inputStack = in.input();
        ItemStack toolStack = in.tool();

        if (inputStack.isEmpty() || toolStack.isEmpty()) {
            return false;
        }

        if (!input.matches(ctx, inputStack)) {
            return false;
        }

        if (!tool.matches(ctx, toolStack)) {
            return false;
        }

        boolean isHammer = toolStack.is(JolCraftTags.Items.ARTISAN_HAMMERS);
        boolean isChisel = toolStack.is(JolCraftTags.Items.CHISELS);

        if (isChisel) {
            if (!DwarfTomeUnlockHelper.hasUnlock(ctx.player(), DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                return false;
            }
        }

        if (isHammer) {
            return inputStack.is(JolCraftTags.Items.GEODES)
                    || inputStack.is(JolCraftTags.Items.GEMS_UNCUT);
        }

        if (isChisel) {
            return inputStack.is(JolCraftTags.Items.GEMS_UNCUT);
        }

        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull LapidaryRecipeInput in, HolderLookup.@NotNull Provider registries) {
        WorldContext ctx = in.ctx();
        if (ctx.level().isClientSide) {
            return ItemStack.EMPTY;
        }

        ItemStack inputStack = in.input();
        ItemStack toolStack = in.tool();
        if (inputStack.isEmpty() || toolStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemOutput out = result;
        ItemSpec spec = out.result();

        ItemStack stack = spec.create(ctx);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        out.transforms().apply(ctx, stack);
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<LapidaryRecipeInput>> getSerializer() {
        return JolCraftRecipes.LAPIDARY_BENCH_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<LapidaryRecipeInput>> getType() {
        return JolCraftRecipes.LAPIDARY_BENCH_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<LapidaryBenchRecipe> {

        private static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> RESULT_STREAM_CODEC =
                StreamCodec.of(
                        OutputDispatch.STREAM_CODEC::encode,
                        buf -> {
                            OutputParam op = OutputDispatch.STREAM_CODEC.decode(buf);
                            OutputParam leaf = OutputParam.unwrap(op);
                            if (leaf instanceof ItemOutput io) {
                                return io;
                            }
                            throw new IllegalStateException("Lapidary bench result must decode to item_output");
                        }
                );

        public static final MapCodec<LapidaryBenchRecipe> CODEC =
                RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<LapidaryBenchRecipe> inst) -> inst.group(
                        ItemInput.CODEC
                                .fieldOf(JolCraftDictionary.INPUT)
                                .forGetter(LapidaryBenchRecipe::input),

                        ItemSelector.CODEC
                                .fieldOf(JolCraftDictionary.TOOL)
                                .forGetter(LapidaryBenchRecipe::tool),

                        OutputDispatch.CODEC
                                .fieldOf(JolCraftDictionary.RESULT)
                                .flatXmap(
                                        op -> {
                                            OutputParam leaf = OutputParam.unwrap(op);
                                            if (leaf instanceof ItemOutput io) {
                                                return DataResult.success(io);
                                            }
                                            return DataResult.error(() ->
                                                    "result must be item_output for lapidary bench recipes"
                                            );
                                        },
                                        DataResult::success
                                )
                                .forGetter(LapidaryBenchRecipe::result),

                        SoundOutput.CODEC
                                .fieldOf(JolCraftDictionary.SOUND)
                                .forGetter(LapidaryBenchRecipe::sound),

                        IntRange.CODEC
                                .optionalFieldOf(JolCraftDictionary.XP, IntRange.ZERO)
                                .forGetter(LapidaryBenchRecipe::xp),

                        IntRange.CODEC
                                .optionalFieldOf(
                                        JolCraftStrings.underscored(JolCraftDictionary.TOOL, JolCraftDictionary.DAMAGE),
                                        IntRange.fixed(1)
                                )
                                .forGetter(LapidaryBenchRecipe::toolDamage)
                ).apply(inst, LapidaryBenchRecipe::new)).flatXmap(
                        Serializer::validate,
                        DataResult::success
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, LapidaryBenchRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ItemInput.STREAM_CODEC, LapidaryBenchRecipe::input,
                        ItemSelector.STREAM_CODEC, LapidaryBenchRecipe::tool,
                        RESULT_STREAM_CODEC, LapidaryBenchRecipe::result,
                        SoundOutput.STREAM_CODEC, LapidaryBenchRecipe::sound,
                        IntRange.STREAM_CODEC, LapidaryBenchRecipe::xp,
                        IntRange.STREAM_CODEC, LapidaryBenchRecipe::toolDamage,
                        LapidaryBenchRecipe::new
                );

        @Override
        public @NotNull MapCodec<LapidaryBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, LapidaryBenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static @NotNull DataResult<LapidaryBenchRecipe> validate(LapidaryBenchRecipe recipe) {
            String toolDamageKey = JolCraftStrings.underscored(
                    JolCraftDictionary.TOOL,
                    JolCraftDictionary.DAMAGE
            );

            DataResult<LapidaryBenchRecipe> base = JolCraftRecipeValidation.validate(recipe)
                    .requireValid(recipe.input(), JolCraftDictionary.INPUT)
                    .requireValid(recipe.tool(), JolCraftDictionary.TOOL)
                    .requireValid(recipe.result(), JolCraftDictionary.RESULT)
                    .requireValid(recipe.sound(), JolCraftDictionary.SOUND)
                    .require(recipe.xp(), JolCraftDictionary.XP)
                    .require(recipe.toolDamage(), toolDamageKey)
                    .done();

            if (base.error().isPresent()) {
                return base;
            }

            ItemOutput out = recipe.result();
            if (out.transforms().requiresInputSource()) {
                return DataResult.error(() ->
                        "this recipe type does not support input-sourced component transforms");
            }

            IntRange xp = recipe.xp();
            IntRange toolDamage = recipe.toolDamage();

            DataResult<IntRange> xpRes = IntRange.validateRange(xp);
            if (xpRes.error().isPresent()) {
                String msg = xpRes.error().map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> JolCraftDictionary.XP + " invalid: " + msg);
            }

            if (xp.min() < 0) {
                return DataResult.error(() -> "xp must have min >= 0");
            }

            DataResult<IntRange> tdRes = IntRange.validateRange(toolDamage);
            if (tdRes.error().isPresent()) {
                String msg = tdRes.error().map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> toolDamageKey + " invalid: " + msg);
            }

            if (toolDamage.min() < 0) {
                return DataResult.error(() -> toolDamageKey + " must have min >= 0");
            }

            return DataResult.success(recipe);
        }
    }
}