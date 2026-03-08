package net.sievert.jolcraft.datagen.recipe.builder.custom.lapidary_bench;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemSelector;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmission;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeFileNameBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Datagen-only fluent builder for {@link LapidaryBenchRecipe}.
 *
 * Contract:
 * - never throws
 * - never saves
 * - name via {@link RecipeFileNameBuilder}
 * - validation mirrors recipe serializer validation: params validate + IntRange checks
 * - returns {@link RecipeEmission} (fileName + deferred save action)
 *
 * Naming policy (deterministic, fail-closed):
 * <tool>_<input>_into_<result>
 *
 * Notes:
 * - For naming, we try to extract singleConcrete ITEM ids; otherwise we fall back to "input/tool/result".
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("UnusedReturnValue")
public final class LapidaryBenchRecipeBuilder implements RecipeBuilder {

    private final List<String> errors = new ArrayList<>();

    private ItemInput input = ItemInput.EMPTY;
    private ItemSelector tool = ItemSelector.EMPTY;
    private ItemOutput result = ItemOutput.EMPTY;
    private @Nullable SoundOutput sound;
    private IntRange xp = IntRange.ZERO;
    private IntRange toolDamage = IntRange.fixed(1);

    private LapidaryBenchRecipeBuilder() {}

    public static @NotNull LapidaryBenchRecipeBuilder create() {
        return new LapidaryBenchRecipeBuilder();
    }

    // ---------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------

    public @NotNull LapidaryBenchRecipeBuilder input(@Nullable ItemInput in) {
        if (in == null) {
            errors.add("input is null");
            this.input = ItemInput.EMPTY;
            return this;
        }

        this.input = in;
        return this;
    }

    public @NotNull LapidaryBenchRecipeBuilder tool(@Nullable ItemSelector sel) {
        if (sel == null) {
            errors.add("tool is null");
            this.tool = ItemSelector.EMPTY;
            return this;
        }

        this.tool = sel;
        return this;
    }

    public @NotNull LapidaryBenchRecipeBuilder result(@Nullable ItemOutput out) {
        if (out == null) {
            errors.add("result is null");
            this.result = ItemOutput.EMPTY;
            return this;
        }

        this.result = out;
        return this;
    }

    public @NotNull LapidaryBenchRecipeBuilder sound(@Nullable SoundOutput sound) {
        if (sound == null) {
            errors.add("sound is null");
            this.sound = null;
            return this;
        }

        this.sound = sound;
        return this;
    }

    public @NotNull LapidaryBenchRecipeBuilder xp(@Nullable IntRange xp) {
        if (xp == null) {
            errors.add("xp is null");
            this.xp = IntRange.ZERO;
            return this;
        }

        this.xp = xp;
        return this;
    }

    public @NotNull LapidaryBenchRecipeBuilder toolDamage(@Nullable IntRange damage) {
        if (damage == null) {
            errors.add("toolDamage is null");
            this.toolDamage = IntRange.fixed(1);
            return this;
        }

        this.toolDamage = damage;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build + validate -> emission
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<RecipeEmission> buildValidated() {
        String toolToken = toolTokenFailClosed(tool);
        String inputToken = tokenFromItemSourceFailClosed(input, JolCraftDictionary.INPUT);
        String resultToken = tokenFromItemSourceFailClosed(result, JolCraftDictionary.RESULT);

        DataResult<String> nameBuilt = RecipeFileNameBuilder.create()
                .word(toolToken)
                .word(inputToken)
                .word(JolCraftDictionary.INTO)
                .word(resultToken)
                .build();

        if (sound == null) {
            errors.add("sound is required");
        }

        if (!errors.isEmpty()) {
            String msg = "builder: " + String.join("; ", errors) +
                    (nameBuilt.error().isPresent() ? ("; " + nameBuilt.error().get().message()) : "");
            return DataResult.error(() -> msg, null);
        }

        LapidaryBenchRecipe recipe = new LapidaryBenchRecipe(
                input,
                tool,
                result,
                sound,
                xp,
                toolDamage
        );

        return nameBuilt.flatMap(name ->
                validateRecipeLikeSerializer(recipe).flatMap(validRecipe ->
                        RecipeEmission.of(
                                JolCraftRecipeIds.LAPIDARY_BENCH,
                                name,
                                (RecipeOutput outAccept, ResourceKey<Recipe<?>> id) ->
                                        outAccept.accept(id, validRecipe, null)
                        )
                )
        );
    }

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    private @NotNull DataResult<LapidaryBenchRecipe> validateRecipeLikeSerializer(@NotNull LapidaryBenchRecipe recipe) {
        DataResult<LapidaryBenchRecipe> resultValidation = recipe.input().validate().map(x -> recipe);
        resultValidation = resultValidation.flatMap(x -> recipe.tool().validate().map(y -> recipe));
        resultValidation = resultValidation.flatMap(x -> recipe.result().validate().map(y -> recipe));
        resultValidation = resultValidation.flatMap(x -> recipe.sound().validate().map(y -> recipe));

        if (resultValidation.error().isPresent()) {
            return resultValidation;
        }

        DataResult<IntRange> xpValidation = IntRange.validateRange(recipe.xp());
        if (xpValidation.error().isPresent()) {
            String msg = xpValidation.error().get().message();
            return DataResult.error(() -> JolCraftDictionary.XP + " invalid: " + msg);
        }

        if (recipe.xp().min() < 0) {
            return DataResult.error(() -> "xp must have min >= 0");
        }

        String toolDamageKey = JolCraftStrings.underscored(JolCraftDictionary.TOOL, JolCraftDictionary.DAMAGE);
        DataResult<IntRange> toolDamageValidation = IntRange.validateRange(recipe.toolDamage());
        if (toolDamageValidation.error().isPresent()) {
            String msg = toolDamageValidation.error().get().message();
            return DataResult.error(() -> toolDamageKey + " invalid: " + msg);
        }

        if (recipe.toolDamage().min() < 0) {
            return DataResult.error(() -> toolDamageKey + " must have min >= 0");
        }

        if (recipe.result().transforms().requiresInputSource()) {
            return DataResult.error(() ->
                    "this recipe type does not support input-sourced component transforms");
        }

        return DataResult.success(recipe);
    }

    // ---------------------------------------------------------------------
    // Naming helpers
    // ---------------------------------------------------------------------

    private @NotNull String tokenFromItemSourceFailClosed(
            @Nullable RegistryIntrospectionSource source,
            @NotNull String fallback
    ) {
        if (source == null) {
            errors.add(fallback + " is null (for naming)");
            return fallback;
        }

        Optional<Holder<Item>> concrete = source.singleConcrete(Registries.ITEM);
        if (concrete.isPresent()) {
            ResourceLocation id = concrete.get()
                    .unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);

            if (id == null) {
                errors.add(fallback + " has no registry key (for naming)");
                return fallback;
            }

            return id.getPath();
        }

        Optional<TagKey<Item>> tag = source.singleTag(Registries.ITEM);
        if (tag.isPresent()) {
            return tag.get().location().getPath();
        }

        errors.add(fallback + " must be exactly one item or exactly one tag (for naming)");
        return fallback;
    }

    private @NotNull String toolTokenFailClosed(@Nullable ItemSelector selector) {
        if (selector == null || selector == ItemSelector.EMPTY) {
            errors.add("tool is missing (for naming)");
            return JolCraftDictionary.TOOL;
        }

        Optional<TagKey<Item>> tag = selector.singleTag(Registries.ITEM);
        if (tag.isPresent()) {
            if (tag.get().equals(JolCraftTags.Items.ARTISAN_HAMMERS)) {
                return JolCraftDictionary.HAMMER;
            }

            if (tag.get().equals(JolCraftTags.Items.CHISELS)) {
                return JolCraftDictionary.CHISEL;
            }

            return tag.get().location().getPath();
        }

        Optional<Holder<Item>> concrete = selector.singleConcrete(Registries.ITEM);
        if (concrete.isPresent()) {
            Holder<Item> holder = concrete.get();

            if (holder.is(JolCraftTags.Items.ARTISAN_HAMMERS)) {
                return JolCraftDictionary.HAMMER;
            }

            if (holder.is(JolCraftTags.Items.CHISELS)) {
                return JolCraftDictionary.CHISEL;
            }

            ResourceLocation id = holder.unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);

            return (id != null) ? id.getPath() : JolCraftDictionary.TOOL;
        }

        errors.add("tool must be exactly one item or exactly one tag (for naming)");
        return JolCraftDictionary.TOOL;
    }
}