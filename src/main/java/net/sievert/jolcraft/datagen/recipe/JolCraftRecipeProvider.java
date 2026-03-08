package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.subprovider.CompassRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.DwarfBountyRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.DwarfTradeRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.EquipmentRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.FermentingCauldronRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.HandInteractionRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.LapidaryRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.MaterialRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.MiscRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.ToolRecipesSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.TrimRecipesSubProvider;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftRecipeProvider extends RecipeProvider {

    public JolCraftRecipeProvider(
            HolderLookup.Provider provider,
            RecipeOutput output
    ) {
        super(provider, output);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(
                PackOutput packOutput,
                CompletableFuture<HolderLookup.Provider> provider
        ) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(
                HolderLookup.Provider provider,
                RecipeOutput recipeOutput
        ) {
            return new JolCraftRecipeProvider(
                    provider,
                    new CountingRecipeOutput(
                            recipeOutput,
                            provider.lookupOrThrow(Registries.RECIPE_SERIALIZER)
                    )
            );
        }

        @Override
        public String getName() {
            return "JolCraft Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        CountingRecipeOutput counting = (CountingRecipeOutput) this.output;
        RecipeEmissionExecutor root = new RecipeEmissionExecutor(counting);

        List<RecipeSubProvider> subs = new ArrayList<>();
        subs.add(new CompassRecipesSubProvider());
        subs.add(new DwarfBountyRecipesSubProvider());
        subs.add(new DwarfTradeRecipesSubProvider());
        subs.add(new EquipmentRecipesSubProvider());
        subs.add(new FermentingCauldronRecipesSubProvider());
        subs.add(new HandInteractionRecipesSubProvider());
        subs.add(new LapidaryRecipesSubProvider());
        subs.add(new MaterialRecipesSubProvider());
        subs.add(new MiscRecipesSubProvider());
        subs.add(new ToolRecipesSubProvider());
        subs.add(new TrimRecipesSubProvider());

        HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);

        for (RecipeSubProvider sub : subs) {
            sub.registerRecipes(
                    root.scoped(sub.folder()),
                    this.output,
                    items
            );
        }

        counting.counts().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e ->
                        JolCraftLogs.debug(
                                JolCraftLogTags.DATAGEN,
                                "Added {} recipes of type: {}",
                                e.getValue(),
                                e.getKey()
                        )
                );

        JolCraftLogs.info(
                JolCraftLogTags.DATAGEN,
                "Total recipes generated: {}",
                counting.total()
        );
    }

    private static final class CountingRecipeOutput implements RecipeOutput {

        private final RecipeOutput delegate;
        private final HolderLookup.RegistryLookup<RecipeSerializer<?>> serializers;
        private final Map<RecipeSerializer<?>, String> serializerIds = new java.util.IdentityHashMap<>();
        private final Map<String, Integer> perType = new HashMap<>();
        private final Map<ResourceKey<Recipe<?>>, Recipe<?>> seen = new HashMap<>();

        private int total = 0;

        private CountingRecipeOutput(
                RecipeOutput delegate,
                HolderLookup.RegistryLookup<RecipeSerializer<?>> serializers
        ) {
            this.delegate = delegate;
            this.serializers = serializers;
            indexSerializers();
        }

        int total() {
            return total;
        }

        Map<String, Integer> counts() {
            return Map.copyOf(perType);
        }

        @Override
        public void accept(
                ResourceKey<Recipe<?>> key,
                Recipe<?> recipe,
                @Nullable AdvancementHolder advancement,
                ICondition... conditions
        ) {
            if (seen.containsKey(key)) {
                throw new IllegalStateException("Duplicate recipe id detected: " + key.location());
            }

            seen.put(key, recipe);
            delegate.accept(key, recipe, advancement, conditions);

            String serializerId = serializerId(recipe.getSerializer());
            perType.merge(serializerId, 1, Integer::sum);
            total++;
        }

        @Override
        public Advancement.Builder advancement() {
            return delegate.advancement();
        }

        @Override
        public void includeRootAdvancement() {
            delegate.includeRootAdvancement();
        }

        private void indexSerializers() {
            serializers.listElements().forEach(reference -> {
                RecipeSerializer<?> serializer = reference.value();
                String id = reference.key().location().toString();
                serializerIds.put(serializer, id);
            });
        }

        private String serializerId(RecipeSerializer<?> serializer) {
            String id = serializerIds.get(serializer);
            if (id == null) {
                throw new IllegalStateException(
                        "Unregistered recipe serializer: " + serializer.getClass().getName()
                );
            }
            return id;
        }
    }
}