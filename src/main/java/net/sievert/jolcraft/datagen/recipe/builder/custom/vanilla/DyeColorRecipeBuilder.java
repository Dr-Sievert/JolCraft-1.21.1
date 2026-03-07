package net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

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

    public static List<Named> buildAll(
            CraftingBookCategory category,
            Item baseItem
    ) {
        String itemName = itemName(baseItem);

        ArrayList<Named> out = new ArrayList<>(DyeColor.values().length + 1);

        for (DyeColor color : DyeColor.values()) {
            ComponentPreservingShapelessRecipeBuilder b = dye(category, baseItem, color);

            String file = JolCraftStrings.underscored(
                    JolCraftDictionary.DYE,
                    itemName,
                    dyeName(color)
            );
            out.add(new Named(file, b));
        }

        ComponentPreservingShapelessRecipeBuilder remove = removeDye(category, baseItem);
        String removeFile = JolCraftStrings.underscored(
                JolCraftDictionary.REMOVE,
                JolCraftDictionary.DYE,
                itemName
        );
        out.add(new Named(removeFile, remove));

        return List.copyOf(out);
    }

    public static ComponentPreservingShapelessRecipeBuilder dye(
            CraftingBookCategory category,
            Item baseItem,
            DyeColor dyeColor
    ) {
        DataComponentPatch.Builder patch = DataComponentPatch.builder();
        patch.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeColor.getTextureDiffuseColor(), true));

        return ComponentPreservingShapelessRecipeBuilder
                .create(category, Ingredient.of(baseItem))
                .result(new net.minecraft.world.item.ItemStack(baseItem))
                .ingredient(Ingredient.of(DyeItem.byColor(dyeColor)))
                .patch(patch.build());
    }

    public static ComponentPreservingShapelessRecipeBuilder removeDye(
            CraftingBookCategory category,
            Item baseItem
    ) {
        DataComponentPatch.Builder patch = DataComponentPatch.builder();
        patch.remove(DataComponents.DYED_COLOR);

        return ComponentPreservingShapelessRecipeBuilder
                .create(category, Ingredient.of(baseItem))
                .result(new net.minecraft.world.item.ItemStack(baseItem))
                .ingredient(Ingredient.of(Items.WATER_BUCKET))
                .requireBaseHas(DataComponents.DYED_COLOR)
                .patch(patch.build());
    }

    public static String dyeName(DyeColor color) {
        return color.getName();
    }

    @SuppressWarnings("deprecation")
    private static String itemName(Item item) {
        return item.builtInRegistryHolder()
                .key()
                .location()
                .getPath();
    }
}