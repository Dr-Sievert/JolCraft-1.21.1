package net.sievert.jolcraft.world.recipe.custom.vanilla;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AttributeSmithingTrimRecipe extends SmithingTrimRecipe {

    private final Ingredient jolTemplate;
    private final Ingredient jolBase;
    private final Ingredient jolAddition;

    public AttributeSmithingTrimRecipe(
            Ingredient template,
            Ingredient base,
            Ingredient addition
    ) {
        super(template, base, addition);
        this.jolTemplate = template;
        this.jolBase = base;
        this.jolAddition = addition;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JolCraftRecipes.ATTRIBUTE_SMITHING_TRIM_SERIALIZER.get();
    }

    public Ingredient templateIngredient() {
        return jolTemplate;
    }

    public Ingredient baseIngredient() {
        return jolBase;
    }

    public Ingredient additionIngredient() {
        return jolAddition;
    }

    public static class Serializer implements RecipeSerializer<AttributeSmithingTrimRecipe> {
        private static final MapCodec<AttributeSmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC.fieldOf(JolCraftDictionary.TEMPLATE).forGetter(AttributeSmithingTrimRecipe::templateIngredient),
                        Ingredient.CODEC.fieldOf(JolCraftDictionary.BASE).forGetter(AttributeSmithingTrimRecipe::baseIngredient),
                        Ingredient.CODEC.fieldOf(JolCraftDictionary.ADDITION).forGetter(AttributeSmithingTrimRecipe::additionIngredient)
                ).apply(instance, AttributeSmithingTrimRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, AttributeSmithingTrimRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<AttributeSmithingTrimRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AttributeSmithingTrimRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static AttributeSmithingTrimRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient template = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient addition = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new AttributeSmithingTrimRecipe(template, base, addition);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, AttributeSmithingTrimRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.templateIngredient());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.baseIngredient());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.additionIngredient());
        }
    }
}
