package net.sievert.jolcraft.data.recipe.custom.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.JolCraftRecipeValidation;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ComponentPreservingShapelessRecipe implements CraftingRecipe {

    private static final DataComponentPatch EMPTY_PATCH = DataComponentPatch.builder().build();

    private final String group;
    private final CraftingBookCategory category;

    private final Ingredient base;
    private final List<Ingredient> ingredients;
    private final ItemStack result;
    private final List<DataComponentType<?>> keep;

    /** Components that must be present on the base stack (e.g. DYED_COLOR for remove-dye). */
    private final List<DataComponentType<?>> baseRequire;

    /** Patch applied after preserving components (set/remove/etc). */
    private final DataComponentPatch patch;

    @Nullable
    private PlacementInfo placementInfo;

    public static final ComponentPreservingShapelessRecipe EMPTY =
            new ComponentPreservingShapelessRecipe(
                    "",
                    CraftingBookCategory.MISC,
                    null,
                    List.of(),
                    ItemStack.EMPTY,
                    List.of(),
                    List.of(),
                    EMPTY_PATCH
            );

    public ComponentPreservingShapelessRecipe(
            String group,
            CraftingBookCategory category,
            Ingredient base,
            List<Ingredient> ingredients,
            ItemStack result,
            List<DataComponentType<?>> keep,
            List<DataComponentType<?>> baseRequire,
            DataComponentPatch patch
    ) {
        this.group = group == null ? "" : group;
        this.category = category != null ? category : CraftingBookCategory.MISC;

        this.base = base;

        if (ingredients == null || ingredients.isEmpty()) {
            this.ingredients = List.of();
        } else {
            ArrayList<Ingredient> tmp = new ArrayList<>(ingredients.size());
            for (Ingredient i : ingredients) if (i != null) tmp.add(i);
            this.ingredients = tmp.isEmpty() ? List.of() : List.copyOf(tmp);
        }

        this.result = result != null ? result : ItemStack.EMPTY;

        if (keep == null || keep.isEmpty()) {
            this.keep = List.of();
        } else {
            ArrayList<DataComponentType<?>> tmp = new ArrayList<>(keep.size());
            for (DataComponentType<?> t : keep) if (t != null) tmp.add(t);
            this.keep = tmp.isEmpty() ? List.of() : List.copyOf(tmp);
        }

        if (baseRequire == null || baseRequire.isEmpty()) {
            this.baseRequire = List.of();
        } else {
            ArrayList<DataComponentType<?>> tmp = new ArrayList<>(baseRequire.size());
            for (DataComponentType<?> t : baseRequire) if (t != null) tmp.add(t);
            this.baseRequire = tmp.isEmpty() ? List.of() : List.copyOf(tmp);
        }

        this.patch = patch != null ? patch : EMPTY_PATCH;
    }

    @Override
    public @NotNull RecipeSerializer<ComponentPreservingShapelessRecipe> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override public @NotNull String group() { return group; }
    @Override public @NotNull CraftingBookCategory category() { return category; }

    public Ingredient base() { return base; }
    public List<Ingredient> ingredients() { return ingredients; }
    public ItemStack result() { return result; }
    public List<DataComponentType<?>> keep() { return keep; }
    public List<DataComponentType<?>> baseRequire() { return baseRequire; }
    public DataComponentPatch patch() { return patch; }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (placementInfo == null) {
            ArrayList<Ingredient> list = new ArrayList<>(1 + ingredients.size());
            list.add(base);
            list.addAll(ingredients);
            placementInfo = PlacementInfo.create(list);
        }
        return placementInfo;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        if (base == null) return false;
        if (input.ingredientCount() != (1 + ingredients.size())) return false;

        int baseIdx = -1;

        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (s.isEmpty()) continue;

            if (base.test(s)) {
                if (baseIdx != -1) return false;
                baseIdx = i;
            }
        }

        if (baseIdx < 0) return false;

        ItemStack baseStack = input.getItem(baseIdx);
        if (!baseRequire.isEmpty() && !baseHasAllRequired(baseStack)) return false;

        var remaining = new ArrayList<ItemStack>(ingredients.size());
        for (int i = 0; i < input.size(); i++) {
            if (i == baseIdx) continue;

            ItemStack s = input.getItem(i);
            if (!s.isEmpty()) remaining.add(s);
        }

        if (remaining.size() != ingredients.size()) return false;
        if (ingredients.isEmpty()) return true;
        if (ingredients.size() == 1) return ingredients.getFirst().test(remaining.getFirst());

        return RecipeMatcher.findMatches(remaining, ingredients) != null;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean baseHasAllRequired(ItemStack baseStack) {
        for (DataComponentType<?> t : baseRequire) {
            if (t == null) return false;
            if (baseStack.get(t) == null) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider lookup) {
        if (base == null) return ItemStack.EMPTY;
        if (result.isEmpty()) return ItemStack.EMPTY;

        ItemStack baseStack = findBaseStack(input);
        if (baseStack.isEmpty()) return ItemStack.EMPTY;
        if (!baseRequire.isEmpty() && !baseHasAllRequired(baseStack)) return ItemStack.EMPTY;

        ItemStack out = result.copy();

        if (keep.isEmpty()) {
            out.applyComponents(baseStack.getComponentsPatch());
        } else {
            for (DataComponentType<?> t : keep) {
                if (t == null) continue;
                copyOneUnchecked(t, baseStack, out);
            }
        }

        if (patch != null) {
            out.applyComponents(patch);
        }

        return out;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void copyOneUnchecked(DataComponentType type, ItemStack from, ItemStack to) {
        Object v = from.get(type);
        if (v != null) {
            to.set(type, v);
        }
    }

    private ItemStack findBaseStack(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (s.isEmpty()) continue;
            if (base.test(s)) return s;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        ArrayList<SlotDisplay> ing = new ArrayList<>(1 + ingredients.size());
        ing.add(base.display());
        for (Ingredient i : ingredients) ing.add(i.display());

        return List.of(new ShapelessCraftingRecipeDisplay(
                ing,
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(net.minecraft.world.item.Items.CRAFTING_TABLE)
        ));
    }

    // ---------------------------------------------------------------------
    // Serializer
    // ---------------------------------------------------------------------

    public static final class Serializer implements RecipeSerializer<ComponentPreservingShapelessRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        private static final String KEY_BASE = JolCraftParameterIds.BASE;
        private static final String KEY_INGREDIENTS = JolCraftParameterIds.INGREDIENTS;
        private static final String KEY_RESULT = JolCraftParameterIds.RESULT;
        private static final String KEY_KEEP = JolCraftParameterIds.KEEP;

        private static final String KEY_BASE_REQUIRE = JolCraftStrings.underscored(JolCraftParameterIds.BASE, JolCraftParameterIds.REQUIREMENTS);
        private static final String KEY_PATCH = JolCraftParameterIds.PATCH;

        private static final int MAX_INGREDIENTS =
                Math.max(1, ShapedRecipePattern.getMaxWidth() * ShapedRecipePattern.getMaxHeight());

        private static final MapCodec<ComponentPreservingShapelessRecipe> CODEC =
                RecordCodecBuilder.<ComponentPreservingShapelessRecipe>mapCodec(inst -> inst.group(
                                Codec.STRING.optionalFieldOf(JolCraftParameterIds.GROUP, "")
                                        .forGetter(ComponentPreservingShapelessRecipe::group),

                                CraftingBookCategory.CODEC.fieldOf(JolCraftParameterIds.CATEGORY)
                                        .orElse(CraftingBookCategory.MISC)
                                        .forGetter(ComponentPreservingShapelessRecipe::category),

                                Ingredient.CODEC.fieldOf(KEY_BASE)
                                        .forGetter(ComponentPreservingShapelessRecipe::base),

                                Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(0, MAX_INGREDIENTS - 1))
                                        .fieldOf(KEY_INGREDIENTS)
                                        .forGetter(ComponentPreservingShapelessRecipe::ingredients),

                                ItemStack.STRICT_CODEC.fieldOf(KEY_RESULT)
                                        .forGetter(ComponentPreservingShapelessRecipe::result),

                                DataComponentType.PERSISTENT_CODEC.listOf()
                                        .optionalFieldOf(KEY_KEEP, List.of())
                                        .forGetter(ComponentPreservingShapelessRecipe::keep),

                                DataComponentType.PERSISTENT_CODEC.listOf()
                                        .optionalFieldOf(KEY_BASE_REQUIRE, List.of())
                                        .forGetter(ComponentPreservingShapelessRecipe::baseRequire),

                                DataComponentPatch.CODEC
                                        .optionalFieldOf(KEY_PATCH, EMPTY_PATCH)
                                        .forGetter(ComponentPreservingShapelessRecipe::patch)
                        ).apply(inst, ComponentPreservingShapelessRecipe::new))
                        .flatXmap(Serializer::validate, DataResult::success);

        public static final StreamCodec<RegistryFriendlyByteBuf, ComponentPreservingShapelessRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, r -> r.group,
                        CraftingBookCategory.STREAM_CODEC, r -> r.category,
                        Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
                        ItemStack.STREAM_CODEC, r -> r.result,
                        DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.keep,
                        DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.baseRequire,
                        DataComponentPatch.STREAM_CODEC, r -> r.patch,
                        (group, category, base, ingredients, result, keep, baseReq, patch) -> {
                            ComponentPreservingShapelessRecipe built =
                                    new ComponentPreservingShapelessRecipe(group, category, base, ingredients, result, keep, baseReq, patch);
                            return validate(built).error().isPresent() ? ComponentPreservingShapelessRecipe.EMPTY : built;
                        }
                );

        @Override public @NotNull MapCodec<ComponentPreservingShapelessRecipe> codec() { return CODEC; }
        @Override public @NotNull StreamCodec<RegistryFriendlyByteBuf, ComponentPreservingShapelessRecipe> streamCodec() { return STREAM_CODEC; }

        public static @NotNull DataResult<ComponentPreservingShapelessRecipe> validate(
                @Nullable ComponentPreservingShapelessRecipe r
        ) {
            DataResult<ComponentPreservingShapelessRecipe> rr = JolCraftRecipeValidation.requireRecipe(r);
            var rrErr = rr.error();
            if (rrErr.isPresent()) {
                String msg = rrErr.map(DataResult.Error::message).orElse("recipe is null");
                return DataResult.error(() -> msg);
            }

            ComponentPreservingShapelessRecipe recipe = rr.result().orElse(null);
            if (recipe == null) return DataResult.error(() -> "recipe is null");

            var v = JolCraftRecipeValidation.validate(recipe)
                    .require(recipe.base(), KEY_BASE)
                    .require(recipe.ingredients(), KEY_INGREDIENTS)
                    .require(recipe.result(), KEY_RESULT)
                    .require(recipe.keep(), KEY_KEEP)
                    .require(recipe.baseRequire(), KEY_BASE_REQUIRE)
                    .require(recipe.patch(), KEY_PATCH);

            DataResult<ComponentPreservingShapelessRecipe> base = v.done();
            if (base.error().isPresent()) return base;

            if (recipe.base() == null || recipe.base().isEmpty()) {
                return DataResult.error(() -> KEY_BASE + " cannot be empty");
            }

            if (recipe.result() == null || recipe.result().isEmpty()) {
                return DataResult.error(() -> KEY_RESULT + " cannot be empty");
            }

            List<Ingredient> ings = recipe.ingredients();
            for (int i = 0; i < ings.size(); i++) {
                Ingredient ing = ings.get(i);
                if (ing == null || ing.isEmpty()) {
                    int idx = i;
                    return DataResult.error(() -> KEY_INGREDIENTS + " contains empty at index " + idx);
                }
                if (ing.equals(recipe.base())) {
                    return DataResult.error(() -> "ingredients must not contain base");
                }
            }

            List<DataComponentType<?>> keep = recipe.keep();
            for (int i = 0; i < keep.size(); i++) {
                if (keep.get(i) == null) {
                    int idx = i;
                    return DataResult.error(() -> KEY_KEEP + " contains null at index " + idx);
                }
            }

            List<DataComponentType<?>> req = recipe.baseRequire();
            for (int i = 0; i < req.size(); i++) {
                if (req.get(i) == null) {
                    int idx = i;
                    return DataResult.error(() -> KEY_BASE_REQUIRE + " contains null at index " + idx);
                }
            }

            if (recipe.patch() == null) {
                return DataResult.error(() -> KEY_PATCH + " cannot be null");
            }

            return DataResult.success(recipe);
        }
    }
}