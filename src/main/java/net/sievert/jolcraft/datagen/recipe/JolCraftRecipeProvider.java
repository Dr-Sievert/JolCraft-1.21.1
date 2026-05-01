package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
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
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftRecipeProvider
        extends RecipeProvider
        implements JolCraftMainDataProvider<RecipeOutput> {

    private @Nullable HolderLookup.Provider registries;
    private @Nullable JolCraftDataLookups lookups;
    private final PackOutput packOutput;

    private @Nullable HolderLookup.RegistryLookup<RecipeSerializer<?>> serializers;
    private final Map<RecipeSerializer<?>, String> serializerIds = new IdentityHashMap<>();
    private final Map<String, Integer> recipeTypeCounts = new LinkedHashMap<>();


    public JolCraftRecipeProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries);
        this.packOutput = output;
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.RECIPE;
    }

    @Override
    public @NotNull String id() {
        return domain().getId();
    }

    @Override
    public @NotNull List<? extends JolCraftSubDataProvider<RecipeOutput>> subProviders() {
        return List.of(
                new CompassRecipesSubProvider(this),
                new DwarfBountyRecipesSubProvider(this),
                new DwarfTradeRecipesSubProvider(this),
                new EquipmentRecipesSubProvider(this),
                new FermentingCauldronRecipesSubProvider(this),
                new HandInteractionRecipesSubProvider(this),
                new LapidaryRecipesSubProvider(this),
                new MaterialRecipesSubProvider(this),
                new MiscRecipesSubProvider(this),
                new ToolRecipesSubProvider(this),
                new TrimRecipesSubProvider(this)
        );
    }

    public @NotNull JolCraftDataLookups lookups() {
        if (lookups == null) {
            throw new IllegalStateException("Recipe lookups not initialized");
        }
        return lookups;
    }

    public @NotNull HolderLookup.Provider registries() {
        if (registries == null) {
            throw new IllegalStateException("Recipe registries not initialized");
        }
        return registries;
    }


    @Override
    protected void buildRecipes(
            @NotNull RecipeOutput recipeOutput,
            @NotNull HolderLookup.Provider registries
    ) {
        this.registries = registries;
        this.lookups = new JolCraftDataLookups(registries);
        this.serializers = registries.lookupOrThrow(Registries.RECIPE_SERIALIZER);

        this.serializerIds.clear();
        this.recipeTypeCounts.clear();
        indexSerializers();

        JolCraftDataTracking tracking = createTracking();

        RecipeOutput countedOutput = new RecipeOutput() {
            @Override
            public void accept(
                    @NotNull ResourceLocation location,
                    @NotNull Recipe<?> recipe,
                    @Nullable AdvancementHolder advancement,
                    @NotNull ICondition... conditions
            ) {
                recipeOutput.accept(location, recipe, advancement, conditions);

                String serializerId = serializerId(recipe.getSerializer());
                recipeTypeCounts.merge(serializerId, 1, Integer::sum);
            }

            @Override
            public @NotNull Advancement.Builder advancement() {
                return recipeOutput.advancement();
            }
        };

        generateSelfAndChildren(countedOutput, this.packOutput, null, null, tracking);

        recipeTypeCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        JolCraftDataTracking.logExplicitCount(
                                this,
                                entry.getValue(),
                                entry.getKey() + " " + JolCraftStrings.plural(JolCraftDictionary.RECIPE)
                        )
                );

        JolCraftDataTracking.logExplicitCount(
                this,
                tracking.totalCount(),
                JolCraftStrings.plural(JolCraftDictionary.RECIPE)
        );

        this.serializers = null;
        this.lookups = null;
        this.registries = null;
    }

    private void indexSerializers() {
        HolderLookup.RegistryLookup<RecipeSerializer<?>> lookup = this.serializers;
        if (lookup == null) {
            throw new IllegalStateException("Recipe serializers not initialized");
        }

        lookup.listElements().forEach(reference -> {
            RecipeSerializer<?> serializer = reference.value();
            String id = reference.key().location().toString();
            serializerIds.put(serializer, id);
        });
    }

    private @NotNull String serializerId(@NotNull RecipeSerializer<?> serializer) {
        String id = serializerIds.get(serializer);
        if (id == null) {
            throw new IllegalStateException(
                    "Unregistered recipe serializer: " + serializer.getClass().getName()
            );
        }
        return id;
    }
}