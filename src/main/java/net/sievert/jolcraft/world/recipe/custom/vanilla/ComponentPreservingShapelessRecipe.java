package net.sievert.jolcraft.world.recipe.custom.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class ComponentPreservingShapelessRecipe implements CraftingRecipe {

    private static final DataComponentPatch EMPTY_PATCH = DataComponentPatch.builder().build();

    private final String group;
    private final CraftingBookCategory category;

    private final Ingredient base;
    private final List<Ingredient> ingredients;
    private final ItemStack result;

    /** Copy only these from base when removeAll = true. */
    private final List<DataComponentType<?>> keep;

    /** Remove these from copied base components when removeAll = false. */
    private final List<DataComponentType<?>> remove;

    /** Components that must be present on the base stack. */
    private final List<DataComponentType<?>> baseRequire;

    /** If true, copy nothing from base except explicit keep whitelist. */
    private final boolean removeAll;

    /** Set/update components after copy stage. */
    private final DataComponentPatch set;

    public static final ComponentPreservingShapelessRecipe EMPTY =
            new ComponentPreservingShapelessRecipe(
                    "",
                    CraftingBookCategory.MISC,
                    null,
                    List.of(),
                    ItemStack.EMPTY,
                    List.of(),
                    List.of(),
                    List.of(),
                    false,
                    EMPTY_PATCH
            );

    public ComponentPreservingShapelessRecipe(
            String group,
            CraftingBookCategory category,
            Ingredient base,
            List<Ingredient> ingredients,
            ItemStack result,
            List<DataComponentType<?>> keep,
            List<DataComponentType<?>> remove,
            List<DataComponentType<?>> baseRequire,
            boolean removeAll,
            DataComponentPatch set
    ) {
        this.group = group == null ? "" : group;
        this.category = category != null ? category : CraftingBookCategory.MISC;
        this.base = base;
        this.ingredients = sanitizeIngredients(ingredients);
        this.result = result != null ? result : ItemStack.EMPTY;
        this.keep = sanitizeComponentTypes(keep);
        this.remove = sanitizeComponentTypes(remove);
        this.baseRequire = sanitizeComponentTypes(baseRequire);
        this.removeAll = removeAll;
        this.set = set != null ? set : EMPTY_PATCH;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return JolCraftRecipes.COMPONENT_PRESERVING_SHAPELESS_SERIALIZER.get();
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return category;
    }

    public Ingredient base() {
        return base;
    }

    public List<Ingredient> ingredients() {
        return ingredients;
    }

    public ItemStack result() {
        return result;
    }

    public List<DataComponentType<?>> keep() {
        return keep;
    }

    public List<DataComponentType<?>> remove() {
        return remove;
    }

    public List<DataComponentType<?>> baseRequire() {
        return baseRequire;
    }

    public boolean removeAll() {
        return removeAll;
    }

    public DataComponentPatch set() {
        return set;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        if (this.base != null && !this.base.isEmpty()) {
            list.add(this.base);
        }

        list.addAll(this.ingredients);
        return list;
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

        ArrayList<ItemStack> remaining = new ArrayList<>(ingredients.size());
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

        if (removeAll) {
            if (!keep.isEmpty()) {
                for (DataComponentType<?> t : keep) {
                    if (t == null) continue;
                    copyOneUnchecked(t, baseStack, out);
                }
            }
        } else if (!remove.isEmpty()) {
            HashSet<DataComponentType<?>> removeSet = new HashSet<>(remove.size());
            for (DataComponentType<?> t : remove) {
                if (t != null) removeSet.add(t);
            }

            for (TypedDataComponent<?> typed : baseStack.getComponents()) {
                if (!removeSet.contains(typed.type())) {
                    copyTypedUnchecked(typed, out);
                }
            }
        } else if (!keep.isEmpty()) {
            for (DataComponentType<?> t : keep) {
                if (t == null) continue;
                copyOneUnchecked(t, baseStack, out);
            }
        } else {
            out.applyComponents(baseStack.getComponentsPatch());
        }

        if (!set.isEmpty()) {
            out.applyComponents(set);
        }

        return out;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= (1 + ingredients.size());
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return result.copy();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void copyOneUnchecked(DataComponentType type, ItemStack from, ItemStack to) {
        Object v = from.get(type);
        if (v != null) {
            to.set(type, v);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void copyTypedUnchecked(TypedDataComponent<?> typed, ItemStack output) {
        output.set((DataComponentType) typed.type(), typed.value());
    }

    private ItemStack findBaseStack(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (s.isEmpty()) continue;
            if (base.test(s)) return s;
        }
        return ItemStack.EMPTY;
    }

    private static @NotNull List<Ingredient> sanitizeIngredients(@Nullable List<Ingredient> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<Ingredient> out = new ArrayList<>(in.size());
        for (Ingredient i : in) {
            if (i != null) out.add(i);
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    private static @NotNull List<DataComponentType<?>> sanitizeComponentTypes(@Nullable List<DataComponentType<?>> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<DataComponentType<?>> out = new ArrayList<>(in.size());
        for (DataComponentType<?> t : in) {
            if (t != null) out.add(t);
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    public static final class Serializer implements RecipeSerializer<CraftingRecipe> {

        private static final String KEY_BASE = JolCraftDictionary.BASE;
        private static final String KEY_INGREDIENTS = JolCraftStrings.plural(JolCraftDictionary.INGREDIENT);
        private static final String KEY_RESULT = JolCraftDictionary.RESULT;
        private static final String KEY_KEEP = JolCraftDictionary.KEEP;
        private static final String KEY_REMOVE = JolCraftDictionary.REMOVE;
        private static final String KEY_SET = JolCraftDictionary.SET;
        private static final String KEY_REMOVE_ALL =
                JolCraftStrings.underscored(JolCraftDictionary.REMOVE, JolCraftDictionary.ALL);
        private static final String KEY_BASE_REQUIRE =
                JolCraftStrings.underscored(JolCraftDictionary.BASE, JolCraftStrings.plural(JolCraftDictionary.REQUIREMENT));

        private static final int MAX_INGREDIENTS =
                Math.max(1, ShapedRecipePattern.getMaxWidth() * ShapedRecipePattern.getMaxHeight());

        private static final MapCodec<ComponentPreservingShapelessRecipe> CODEC =
                RecordCodecBuilder.<ComponentPreservingShapelessRecipe>mapCodec(inst -> inst.group(
                                Codec.STRING.optionalFieldOf(JolCraftDictionary.GROUP, "")
                                        .forGetter(ComponentPreservingShapelessRecipe::getGroup),

                                CraftingBookCategory.CODEC.optionalFieldOf(JolCraftDictionary.CATEGORY, CraftingBookCategory.MISC)
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
                                        .optionalFieldOf(KEY_REMOVE, List.of())
                                        .forGetter(ComponentPreservingShapelessRecipe::remove),

                                DataComponentType.PERSISTENT_CODEC.listOf()
                                        .optionalFieldOf(KEY_BASE_REQUIRE, List.of())
                                        .forGetter(ComponentPreservingShapelessRecipe::baseRequire),

                                Codec.BOOL.optionalFieldOf(KEY_REMOVE_ALL, false)
                                        .forGetter(ComponentPreservingShapelessRecipe::removeAll),

                                DataComponentPatch.CODEC.optionalFieldOf(KEY_SET, EMPTY_PATCH)
                                        .forGetter(ComponentPreservingShapelessRecipe::set)
                        ).apply(inst, ComponentPreservingShapelessRecipe::new))
                        .flatXmap(Serializer::validate, DataResult::success);

        private static final StreamCodec<RegistryFriendlyByteBuf, ComponentPreservingShapelessRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            ByteBufCodecs.STRING_UTF8.encode(buf, recipe.group);
                            CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category);
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.base);
                            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.ingredients);
                            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                            DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.keep);
                            DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.remove);
                            DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.baseRequire);
                            ByteBufCodecs.BOOL.encode(buf, recipe.removeAll);
                            DataComponentPatch.STREAM_CODEC.encode(buf, recipe.set);
                        },
                        buf -> {
                            String group = ByteBufCodecs.STRING_UTF8.decode(buf);
                            CraftingBookCategory category = CraftingBookCategory.STREAM_CODEC.decode(buf);
                            Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                            List<Ingredient> ingredients = Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                            List<DataComponentType<?>> keep = DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                            List<DataComponentType<?>> remove = DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                            List<DataComponentType<?>> baseRequire = DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                            boolean removeAll = ByteBufCodecs.BOOL.decode(buf);
                            DataComponentPatch set = DataComponentPatch.STREAM_CODEC.decode(buf);

                            ComponentPreservingShapelessRecipe built =
                                    new ComponentPreservingShapelessRecipe(
                                            group,
                                            category,
                                            base,
                                            ingredients,
                                            result,
                                            keep,
                                            remove,
                                            baseRequire,
                                            removeAll,
                                            set
                                    );

                            return validate(built).error().isPresent()
                                    ? ComponentPreservingShapelessRecipe.EMPTY
                                    : built;
                        }
                );

        @Override
        public @NotNull MapCodec<CraftingRecipe> codec() {
            return CODEC.xmap(
                    recipe -> recipe,
                    recipe -> (ComponentPreservingShapelessRecipe) recipe
            );
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CraftingRecipe> streamCodec() {
            return STREAM_CODEC.map(
                    recipe -> recipe,
                    recipe -> (ComponentPreservingShapelessRecipe) recipe
            );
        }

        public static @NotNull DataResult<ComponentPreservingShapelessRecipe> validate(
                @Nullable ComponentPreservingShapelessRecipe r
        ) {
            DataResult<ComponentPreservingShapelessRecipe> rr = RecipeValidation.requireRecipe(r);
            var rrErr = rr.error();
            if (rrErr.isPresent()) {
                String msg = rrErr.map(DataResult.Error::message).orElse("recipe is null");
                return DataResult.error(() -> msg);
            }

            ComponentPreservingShapelessRecipe recipe = rr.result().orElse(null);
            if (recipe == null) return DataResult.error(() -> "recipe is null");

            var v = RecipeValidation.validate(recipe)
                    .require(recipe.base(), KEY_BASE)
                    .require(recipe.ingredients(), KEY_INGREDIENTS)
                    .require(recipe.result(), KEY_RESULT)
                    .require(recipe.keep(), KEY_KEEP)
                    .require(recipe.remove(), KEY_REMOVE)
                    .require(recipe.baseRequire(), KEY_BASE_REQUIRE)
                    .require(recipe.set(), KEY_SET);

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

            List<DataComponentType<?>> remove = recipe.remove();
            for (int i = 0; i < remove.size(); i++) {
                if (remove.get(i) == null) {
                    int idx = i;
                    return DataResult.error(() -> KEY_REMOVE + " contains null at index " + idx);
                }
            }

            List<DataComponentType<?>> req = recipe.baseRequire();
            for (int i = 0; i < req.size(); i++) {
                if (req.get(i) == null) {
                    int idx = i;
                    return DataResult.error(() -> KEY_BASE_REQUIRE + " contains null at index " + idx);
                }
            }

            if (recipe.removeAll()) {
                if (!recipe.remove().isEmpty()) {
                    return DataResult.error(() -> "'" + KEY_REMOVE + "' is not allowed when '" + KEY_REMOVE_ALL + "' is true");
                }
            } else {
                if (!recipe.keep().isEmpty() && !recipe.remove().isEmpty()) {
                    return DataResult.error(() -> "'" + KEY_KEEP + "' and '" + KEY_REMOVE + "' cannot be used together");
                }
            }

            if (recipe.set() == null) {
                return DataResult.error(() -> KEY_SET + " cannot be null");
            }

            for (var e : recipe.set().entrySet()) {
                if (e == null) continue;
                var vEntry = e.getValue();
                if (vEntry == null || vEntry.isEmpty()) {
                    return DataResult.error(() -> "'" + KEY_SET + "' must not remove components; use '" + KEY_REMOVE + "' / '" + KEY_REMOVE_ALL + "' instead");
                }
            }

            return DataResult.success(recipe);
        }
    }
}