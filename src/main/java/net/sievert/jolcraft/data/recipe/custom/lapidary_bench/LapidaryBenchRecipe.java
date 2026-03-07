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

/**
 * Lapidary Bench:
 * - tool gating via ItemSelector (must be either hammer-tag set OR chisel-tag set)
 * - recipe-driven tool damage (IntRange)
 * - recipe-driven sound (SoundOutput, non-null sentinel supported)
 * - recipe-driven xp (IntRange)
 */
public record LapidaryBenchRecipe(
        ItemInput input,
        ItemSelector tool,
        ItemOutput result,
        SoundOutput sound,
        IntRange xp,
        IntRange toolDamage
) implements CustomRecipe<LapidaryRecipeInput> {

    // ---------------------------------------------------------------------
    // Sentinel
    // ---------------------------------------------------------------------

    public static final LapidaryBenchRecipe EMPTY = new LapidaryBenchRecipe(
            ItemInput.EMPTY,
            ItemSelector.EMPTY,
            ItemOutput.EMPTY,
            SoundOutput.EMPTY,
            IntRange.ZERO,
            IntRange.ZERO
    );

    public LapidaryBenchRecipe(
            ItemInput input,
            ItemSelector tool,
            ItemOutput result,
            SoundOutput sound,
            IntRange xp,
            IntRange toolDamage
    ) {
        this.input = input != null ? input : ItemInput.EMPTY;
        this.tool = tool != null ? tool : ItemSelector.EMPTY;
        this.result = result != null ? result : ItemOutput.EMPTY;
        this.sound = sound != null ? sound : SoundOutput.EMPTY;
        this.xp = xp != null ? xp : IntRange.ZERO;
        this.toolDamage = toolDamage != null ? toolDamage : IntRange.ZERO;
    }

    // ---------------------------------------------------------------------
    // Recipe implementation
    // ---------------------------------------------------------------------

    @Override
    public boolean matches(@NotNull LapidaryRecipeInput in, Level level) {
        if (level.isClientSide) return false;

        WorldContext ctx = in.ctx();

        ItemStack inputStack = in.input();
        ItemStack toolStack = in.tool();

        if (inputStack.isEmpty() || toolStack.isEmpty()) return false;
        if (!input.matches(ctx, inputStack)) return false;
        if (!tool.matches(ctx, toolStack)) return false;

        boolean isHammer = toolStack.is(JolCraftTags.Items.ARTISAN_HAMMERS);
        boolean isChisel = toolStack.is(JolCraftTags.Items.CHISELS);

        if (isChisel) {
            if (!DwarfTomeUnlockHelper.hasUnlock(ctx.player(), DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                return false;
            }
        }

        if (isHammer) {
            return inputStack.is(JolCraftTags.Items.GEODES) || inputStack.is(JolCraftTags.Items.GEMS_UNCUT);
        }

        if (isChisel) {
            return inputStack.is(JolCraftTags.Items.GEMS_UNCUT);
        }

        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull LapidaryRecipeInput in, HolderLookup.@NotNull Provider registries) {
        WorldContext ctx = in.ctx();
        if (ctx.level().isClientSide) return ItemStack.EMPTY;

        ItemStack inputStack = in.input();
        ItemStack toolStack = in.tool();
        if (inputStack.isEmpty() || toolStack.isEmpty()) return ItemStack.EMPTY;

        ItemOutput out = result;
        ItemSpec spec = out.result();

        ItemStack stack = spec.create(ctx);
        if (stack.isEmpty()) return ItemStack.EMPTY;

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
                            return (leaf instanceof ItemOutput io) ? io : ItemOutput.EMPTY;
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
                                .xmap(
                                        op -> {
                                            OutputParam leaf = OutputParam.unwrap(op);
                                            return (leaf instanceof ItemOutput io) ? io : ItemOutput.EMPTY;
                                        },
                                        io -> io != null ? io : ItemOutput.EMPTY
                                )
                                .forGetter(LapidaryBenchRecipe::result),

                        SoundOutput.CODEC
                                .optionalFieldOf(JolCraftDictionary.SOUND, SoundOutput.EMPTY)
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
                        (input, tool, result, sound, xp, toolDamage) -> {
                            LapidaryBenchRecipe built = new LapidaryBenchRecipe(input, tool, result, sound, xp, toolDamage);
                            return validate(built).error().isPresent() ? LapidaryBenchRecipe.EMPTY : built;
                        }
                );

        @Override
        public @NotNull MapCodec<LapidaryBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, LapidaryBenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static @NotNull DataResult<LapidaryBenchRecipe> validate(LapidaryBenchRecipe r) {

            var toolDamageKey = JolCraftStrings.underscored(
                    JolCraftDictionary.TOOL,
                    JolCraftDictionary.DAMAGE
            );

            // ---- recipe required ----
            DataResult<LapidaryBenchRecipe> rr = JolCraftRecipeValidation.requireRecipe(r);
            var rrErr = rr.error();
            if (rrErr.isPresent()) {
                String msg = rrErr.map(DataResult.Error::message).orElse("recipe is null");
                return DataResult.error(() -> msg);
            }

            LapidaryBenchRecipe recipe = rr.result().orElse(null);
            if (recipe == null) {
                return DataResult.error(() -> "recipe is null");
            }

            // ---- required + Validatable fields ----
            var v = JolCraftRecipeValidation.validate(recipe)
                    .requireValid(recipe.input(), JolCraftDictionary.INPUT)
                    .requireValid(recipe.tool(), JolCraftDictionary.TOOL)
                    .requireValid(recipe.result(), JolCraftDictionary.RESULT)
                    .requireValid(recipe.sound(), JolCraftDictionary.SOUND)
                    .require(recipe.xp(), JolCraftDictionary.XP)
                    .require(recipe.toolDamage(), toolDamageKey);

            DataResult<LapidaryBenchRecipe> base = v.done();
            if (base.error().isPresent()) return base;

            ItemOutput out = r.result();
            if (out.transforms().requiresInputSource()) {
                return DataResult.error(() ->
                        "this recipe type does not support input-sourced component transforms");
            }

            IntRange xp = recipe.xp();
            IntRange toolDamage = recipe.toolDamage();

            var xpRes = IntRange.validateRange(xp);
            var xpErr = xpRes.error();
            if (xpErr.isPresent()) {
                String msg = xpErr.map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> JolCraftDictionary.XP + " invalid: " + msg);
            }
            if (xp.min() < 0) return DataResult.error(() -> "xp must have min >= 0");

            var tdRes = IntRange.validateRange(toolDamage);
            var tdErr = tdRes.error();
            if (tdErr.isPresent()) {
                String msg = tdErr.map(DataResult.Error::message).orElse("invalid");
                return DataResult.error(() -> toolDamageKey + " invalid: " + msg);
            }
            if (toolDamage.min() < 0) {
                return DataResult.error(() -> toolDamageKey + " must have min >= 0");
            }

            return DataResult.success(recipe);
        }
    }
}