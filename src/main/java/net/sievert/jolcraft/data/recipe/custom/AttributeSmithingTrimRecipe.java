package net.sievert.jolcraft.data.recipe.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.item.trim.JolCraftTrimAttributes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AttributeSmithingTrimRecipe extends SmithingTrimRecipe {

    public AttributeSmithingTrimRecipe(
            Optional<Ingredient> template,
            Optional<Ingredient> base,
            Optional<Ingredient> addition
    ) {
        super(template, base, addition);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack stack = super.assemble(input, registries);

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim != null) {
            JolCraftTrimAttributes.applyAttribute(stack, trim);
        }

        return stack;
    }

    @Override
    public RecipeSerializer<SmithingTrimRecipe> getSerializer() {
        return JolCraftRecipes.ATTRIBUTE_SMITHING_TRIM_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {

        @Override
        public MapCodec<SmithingTrimRecipe> codec() {
            return RecipeSerializer.SMITHING_TRIM.codec().xmap(
                    vanilla -> new AttributeSmithingTrimRecipe(
                            vanilla.templateIngredient(),
                            vanilla.baseIngredient(),
                            vanilla.additionIngredient()
                    ),
                    custom -> new SmithingTrimRecipe(
                            custom.templateIngredient(),
                            custom.baseIngredient(),
                            custom.additionIngredient()
                    )
            );
        }

        @SuppressWarnings("deprecation")
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> streamCodec() {
            return RecipeSerializer.SMITHING_TRIM.streamCodec().map(
                    vanilla -> new AttributeSmithingTrimRecipe(
                            vanilla.templateIngredient(),
                            vanilla.baseIngredient(),
                            vanilla.additionIngredient()
                    ),
                    custom -> new SmithingTrimRecipe(
                            custom.templateIngredient(),
                            custom.baseIngredient(),
                            custom.additionIngredient()
                    )
            );
        }
    }
}