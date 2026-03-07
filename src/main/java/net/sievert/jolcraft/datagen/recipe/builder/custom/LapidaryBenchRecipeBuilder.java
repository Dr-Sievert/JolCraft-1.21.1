package net.sievert.jolcraft.datagen.recipe.builder.custom;

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
@SuppressWarnings({"UnusedReturnValue"})
public final class LapidaryBenchRecipeBuilder implements RecipeBuilder {

    private final List<String> errors = new ArrayList<>();

    private ItemInput input = ItemInput.EMPTY;
    private ItemSelector tool = ItemSelector.EMPTY;
    private ItemOutput result = ItemOutput.EMPTY;
    private SoundOutput sound = SoundOutput.EMPTY;
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

    public @NotNull LapidaryBenchRecipeBuilder sound(@Nullable SoundOutput s) {
        if (s == null) {
            errors.add("sound is null");
            this.sound = SoundOutput.EMPTY;
            return this;
        }
        this.sound = s;
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

    public @NotNull LapidaryBenchRecipeBuilder toolDamage(@Nullable IntRange dmg) {
        if (dmg == null) {
            errors.add("toolDamage is null");
            this.toolDamage = IntRange.fixed(1);
            return this;
        }
        this.toolDamage = dmg;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build + validate -> emission
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<RecipeEmission> buildValidated() {

        // ---- file name (fail-closed) ----
        String toolTok = toolTokenFailClosed(tool);
        String inTok   = tokenFromItemSourceFailClosed(input, JolCraftDictionary.INPUT);
        String resTok  = tokenFromItemSourceFailClosed(result, JolCraftDictionary.RESULT);

        DataResult<String> nameBuilt = RecipeFileNameBuilder.create()
                .word(toolTok)
                .word(inTok)
                .word(JolCraftDictionary.INTO)
                .word(resTok)
                .build();

        if (!errors.isEmpty()) {
            String partial = nameBuilt.result().orElse("");
            String msg = "recipeName: " + String.join("; ", errors) +
                    (nameBuilt.error().isPresent() ? ("; " + nameBuilt.error().get().message()) : "");
            nameBuilt = DataResult.error(() -> msg, partial);
        }

        // ---- recipe ----
        LapidaryBenchRecipe r = new LapidaryBenchRecipe(
                (input == null) ? ItemInput.EMPTY : input,
                (tool == null) ? ItemSelector.EMPTY : tool,
                (result == null) ? ItemOutput.EMPTY : result,
                (sound == null) ? SoundOutput.EMPTY : sound,
                (xp == null) ? IntRange.ZERO : xp,
                (toolDamage == null) ? IntRange.fixed(1) : toolDamage
        );

        // ---- validate (mirror recipe serializer validation) ----
        DataResult<LapidaryBenchRecipe> validated = validateRecipeLikeSerializer(r);

        DataResult<LapidaryBenchRecipe> recipeResult =
                (!errors.isEmpty() && validated.error().isEmpty())
                        ? DataResult.error(() -> "builder: " + String.join("; ", errors), r)
                        : validated;

        // ---- combine -> emission ----
        return nameBuilt.flatMap(name ->
                recipeResult.flatMap(validRecipe ->
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

    private @NotNull DataResult<LapidaryBenchRecipe> validateRecipeLikeSerializer(@NotNull LapidaryBenchRecipe r) {

        DataResult<LapidaryBenchRecipe> v = r.input().validate().map(x -> r);
        v = v.flatMap(x -> r.tool().validate().map(y -> r));
        v = v.flatMap(x -> r.result().validate().map(y -> r));
        v = v.flatMap(x -> r.sound().validate().map(y -> r));

        DataResult<IntRange> xpV = IntRange.validateRange(r.xp());
        if (xpV.error().isPresent()) {
            String msg = xpV.error().get().message();
            return DataResult.error(() -> JolCraftDictionary.XP + " invalid: " + msg);
        }
        if (r.xp().min() < 0) {
            return DataResult.error(() -> "xp must have min >= 0");
        }

        String toolDamageKey = JolCraftStrings.underscored(JolCraftDictionary.TOOL, JolCraftDictionary.DAMAGE);
        DataResult<IntRange> tdV = IntRange.validateRange(r.toolDamage());
        if (tdV.error().isPresent()) {
            String msg = tdV.error().get().message();
            return DataResult.error(() -> toolDamageKey + " invalid: " + msg);
        }
        if (r.toolDamage().min() < 0) {
            return DataResult.error(() -> toolDamageKey + " must have min >= 0");
        }

        return v;
    }

    // ---------------------------------------------------------------------
    // Naming helpers
    // ---------------------------------------------------------------------

    private static @Nullable String holderPath(@NotNull Holder<Item> h) {
        ResourceLocation id = h.unwrapKey().map(ResourceKey::location).orElse(null);
        return id != null ? id.getPath() : null;
    }

    private @NotNull String tokenFromItemSourceFailClosed(
            @Nullable RegistryIntrospectionSource src,
            @NotNull String fallback
    ) {
        if (src == null) {
            errors.add(fallback + " is null (for naming)");
            return fallback;
        }

        Optional<Holder<Item>> concrete = src.singleConcrete(Registries.ITEM);
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

        Optional<TagKey<Item>> tag = src.singleTag(Registries.ITEM);
        if (tag.isPresent()) {
            ResourceLocation id = tag.get().location();
            return id.getPath();
        }

        errors.add(fallback + " must be exactly one item or exactly one tag (for naming)");
        return fallback;
    }

    private @NotNull String toolTokenFailClosed(@Nullable ItemSelector sel) {
        if (sel == null || sel == ItemSelector.EMPTY) {
            errors.add("tool is missing (for naming)");
            return JolCraftDictionary.TOOL;
        }

        Optional<TagKey<Item>> tag = sel.singleTag(Registries.ITEM);
        if (tag.isPresent()) {
            if (tag.get().equals(JolCraftTags.Items.ARTISAN_HAMMERS))
                return JolCraftDictionary.HAMMER;

            if (tag.get().equals(JolCraftTags.Items.CHISELS))
                return JolCraftDictionary.CHISEL;

            return tag.get().location().getPath();
        }

        Optional<Holder<Item>> h = sel.singleConcrete(Registries.ITEM);
        if (h.isPresent()) {
            Holder<Item> holder = h.get();

            if (holder.is(JolCraftTags.Items.ARTISAN_HAMMERS))
                return JolCraftDictionary.HAMMER;

            if (holder.is(JolCraftTags.Items.CHISELS))
                return JolCraftDictionary.CHISEL;

            ResourceLocation id = holder.unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);

            return (id != null) ? id.getPath() : JolCraftDictionary.TOOL;
        }

        errors.add("tool must be exactly one item or exactly one tag (for naming)");
        return JolCraftDictionary.TOOL;
    }
}