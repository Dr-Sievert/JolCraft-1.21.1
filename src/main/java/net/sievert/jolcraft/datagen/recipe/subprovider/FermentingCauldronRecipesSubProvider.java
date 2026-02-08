package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.recipe.custom.FermentingCauldronRecipe;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingCauldronRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    private static final String FOLDER = "fermenting_cauldron";

    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {

        fermentingFinalize(
                p,
                Items.SUGAR,
                null,
                1200,
                3,
                0x40B14A
        );

        fermentingExtract(
                p,
                Items.GLASS_BOTTLE,
                Items.SUGAR,
                new ItemStack(JolCraftItems.YEAST.get())
        );

        fermenting(
                p,
                JolCraftItems.BARLEY_MALT.get(),
                null,
                20,
                5,
                0x805d37
        );

        fermentingEffect(
                p,
                JolCraftItems.ASGARNIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x91706e,
                MobEffects.HEALTH_BOOST,
                6000,
                0
        );

        fermentingEffect(
                p,
                JolCraftItems.DUSKHOLD_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x817788,
                MobEffects.NIGHT_VISION,
                6000,
                0
        );

        fermentingEffect(
                p,
                JolCraftItems.KRANDONIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x6e918f,
                MobEffects.DAMAGE_BOOST,
                6000,
                0
        );

        fermentingEffect(
                p,
                JolCraftItems.YANILLIAN_HOPS.get(),
                JolCraftTags.Items.HOPS_BREW,
                20,
                5,
                0x54832e,
                MobEffects.MOVEMENT_SPEED,
                6000,
                0
        );

        fermentingFinalize(
                p,
                JolCraftItems.YEAST.get(),
                JolCraftTags.Items.HOPS,
                6000,
                60,
                0x9A652B
        );

        fermentingExtract(
                p,
                JolCraftItems.GLASS_MUG.get(),
                JolCraftItems.YEAST.get(),
                new ItemStack(JolCraftItems.DWARVEN_BREW.get())
        );
    }

    private static void fermenting(
            AbstractRecipeProvider p,
            ItemLike ingredient,
            @Nullable ItemLike validStateItem,
            int brewTicks,
            int bubbleTicks,
            int colorRgb
    ) {
        registerFermenting(
                p,
                ingredient,
                validStateItem == null ? null : Ingredient.of(validStateItem),
                null,
                validStateItem,
                brewTicks,
                bubbleTicks,
                colorRgb,
                null,
                false,
                null
        );
    }

    private static void fermentingFinalize(
            AbstractRecipeProvider p,
            ItemLike ingredient,
            @Nullable TagKey<Item> validStatesTag,
            int brewTicks,
            int bubbleTicks,
            int colorRgb
    ) {
        registerFermenting(
                p,
                ingredient,
                validStatesTag == null ? null : p.tagIngredient(validStatesTag),
                validStatesTag,
                null,
                brewTicks,
                bubbleTicks,
                colorRgb,
                null,
                true,
                null
        );
    }

    private static void fermentingEffect(
            AbstractRecipeProvider p,
            ItemLike ingredient,
            @Nullable TagKey<Item> validStatesTag,
            int brewTicks,
            int bubbleTicks,
            int colorRgb,
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        registerFermenting(
                p,
                ingredient,
                validStatesTag == null ? null : p.tagIngredient(validStatesTag),
                validStatesTag,
                null,
                brewTicks,
                bubbleTicks,
                colorRgb,
                FermentingCauldronRecipe.EffectData.fromHolder(effect, duration, amplifier),
                false,
                null
        );
    }

    private static void fermentingExtract(
            AbstractRecipeProvider p,
            ItemLike extractor,
            @Nullable ItemLike validStateItem,
            ItemStack result
    ) {
        registerFermenting(
                p,
                extractor,
                validStateItem == null ? null : Ingredient.of(validStateItem),
                null,
                validStateItem,
                1,
                1,
                0xFFFFFF,
                null,
                false,
                result
        );
    }

    private static void registerFermenting(
            AbstractRecipeProvider p,
            ItemLike ingredient,
            @Nullable Ingredient validStates,
            @Nullable TagKey<Item> validStatesTag,
            @Nullable ItemLike validStatesItem,
            int brewTicks,
            int bubbleTicks,
            int colorRgb,
            @Nullable FermentingCauldronRecipe.EffectData effect,
            boolean finalize,
            @Nullable ItemStack extract
    ) {
        String ingredientName = BuiltInRegistries.ITEM.getKey(ingredient.asItem()).getPath();
        String statesName = statesPart(validStatesTag, validStatesItem);

        boolean isExtract = extract != null && !extract.isEmpty();

        String idPath;
        if (isExtract) {
            String resultName = BuiltInRegistries.ITEM.getKey(extract.getItem()).getPath();
            idPath = ingredientName + "_extract_" + resultName;
        } else {
            idPath = ingredientName
                    + "_in_" + statesName
                    + (finalize ? "_finalize" : "");
        }

        ResourceLocation id = JolCraft.location(p.inFolder(FOLDER, idPath));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);

        int colorArgb = 0xFF000000 | (colorRgb & 0xFFFFFF);

        FermentingCauldronRecipe recipe = new FermentingCauldronRecipe(
                Ingredient.of(ingredient),
                validStates,
                brewTicks,
                bubbleTicks,
                colorArgb,
                effect,
                finalize,
                isExtract ? Objects.requireNonNull(extract).copy() : null
        );

        AdvancementHolder advancement = p.out().advancement()
                .addCriterion(p.hasName(ingredient), p.hasItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .build(JolCraft.location("recipes/" + p.inFolder(FOLDER, idPath)));

        p.out().accept(key, recipe, advancement);
    }

    private static String statesPart(@Nullable TagKey<Item> tag, @Nullable ItemLike item) {
        if (tag != null)  return tag.location().getPath();
        if (item != null) return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
        return "water_cauldron";
    }
}