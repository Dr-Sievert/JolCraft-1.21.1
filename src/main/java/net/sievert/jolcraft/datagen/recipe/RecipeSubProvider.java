package net.sievert.jolcraft.datagen.recipe;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.builder.JolCraftOrderedEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataExecutor;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface RecipeSubProvider extends JolCraftSubDataProvider<RecipeOutput> {

    default void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {}

    @Override
    default void run(
            @NotNull RecipeOutput target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        RecipeOutput trackedOutput = new RecipeOutput() {
            @Override
            public void accept(
                    @NotNull ResourceLocation id,
                    @NotNull Recipe<?> recipe,
                    @Nullable AdvancementHolder advancement,
                    @NotNull ICondition... conditions
            ) {
                target.accept(id, recipe, advancement, conditions);
                tracking.record(RecipeSubProvider.this, id.toString());
            }

            @Override
            public @NotNull Advancement.Builder advancement() {
                return target.advancement();
            }
        };

        registerRecipes(
                trackedOutput,
                recipeProvider().lookups(),
                tracking
        );
    }

    default void emit(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DataResult<JolCraftDataEmission<RecipeOutput>> built
    ) {
        JolCraftDataExecutor.execute(
                output,
                this,
                List.of(built.getOrThrow(IllegalStateException::new)),
                tracking,
                false
        );
    }

    default void emitOrdered(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull JolCraftOrderedEmissionBuilder<RecipeOutput> builder
    ) {
        JolCraftDataExecutor.executeOrdered(
                output,
                this,
                List.of(builder),
                tracking,
                false
        );
    }

    default @NotNull JolCraftRecipeProvider recipeProvider() {
        for (JolCraftDataProvider<?> current : chain()) {
            if (current instanceof JolCraftRecipeProvider recipeProvider) {
                return recipeProvider;
            }
        }

        throw new IllegalStateException("No JolCraftRecipeProvider found in provider chain: " + name());
    }
}