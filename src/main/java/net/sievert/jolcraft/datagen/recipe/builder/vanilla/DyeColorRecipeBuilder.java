package net.sievert.jolcraft.datagen.recipe.builder.vanilla;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DyeColorRecipeBuilder {

    private DyeColorRecipeBuilder() {}

    public record Named(
            String file,
            ComponentPreservingShapelessRecipeBuilder builder
    ) {}

    public static @NotNull List<Named> buildAll(
            CraftingBookCategory category,
            Item baseItem,
            List<Ingredient> removeIngredients
    ) {
        String itemName = itemName(baseItem);

        ArrayList<Named> out = new ArrayList<>(DyeColor.values().length + 1);

        for (DyeColor color : DyeColor.values()) {
            ComponentPreservingShapelessRecipeBuilder builder = dye(category, baseItem, color);

            String file = JolCraftStrings.underscored(
                    JolCraftDictionary.DYE,
                    itemName,
                    dyeName(color)
            );
            out.add(new Named(file, builder));
        }

        ComponentPreservingShapelessRecipeBuilder removeBuilder =
                removeDye(category, baseItem, removeIngredients);

        String removeFile = JolCraftStrings.underscored(
                JolCraftDictionary.REMOVE,
                JolCraftDictionary.DYE,
                itemName
        );
        out.add(new Named(removeFile, removeBuilder));

        return List.copyOf(out);
    }

    public static @NotNull List<Named> buildAll(
            CraftingBookCategory category,
            Item baseItem,
            Ingredient removeIngredient
    ) {
        return buildAll(category, baseItem, List.of(removeIngredient));
    }

    public static @NotNull ComponentPreservingShapelessRecipeBuilder dye(
            CraftingBookCategory category,
            Item baseItem,
            DyeColor dyeColor
    ) {
        DataComponentPatch.Builder set = DataComponentPatch.builder();
        set.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextureDiffuseColor(), true));

        return ComponentPreservingShapelessRecipeBuilder
                .create(category, Ingredient.of(baseItem))
                .result(new ItemStack(baseItem))
                .ingredient(Ingredient.of(DyeItem.byColor(dyeColor)))
                .set(set.build());
    }

    public static @NotNull ComponentPreservingShapelessRecipeBuilder removeDye(
            CraftingBookCategory category,
            Item baseItem,
            List<Ingredient> removeIngredients
    ) {
        ComponentPreservingShapelessRecipeBuilder builder =
                ComponentPreservingShapelessRecipeBuilder
                        .create(category, Ingredient.of(baseItem))
                        .result(new ItemStack(baseItem))
                        .requireBaseHas(DataComponents.DYED_COLOR)
                        .remove(DataComponents.DYED_COLOR);

        builder.ingredients(removeIngredients);

        return builder;
    }

    public static @NotNull ComponentPreservingShapelessRecipeBuilder removeDye(
            CraftingBookCategory category,
            Item baseItem,
            Ingredient removeIngredient
    ) {
        return removeDye(category, baseItem, List.of(removeIngredient));
    }

    public static @NotNull String dyeName(DyeColor color) {
        return color.getName();
    }

    @SuppressWarnings("deprecation")
    private static @NotNull String itemName(@Nullable Item item) {
        if (item == null) {
            return JolCraftDictionary.UNKNOWN;
        }

        return item.builtInRegistryHolder()
                .key()
                .location()
                .getPath();
    }
}