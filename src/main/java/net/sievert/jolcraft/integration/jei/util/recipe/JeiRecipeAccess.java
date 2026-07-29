package net.sievert.jolcraft.integration.jei.util.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class JeiRecipeAccess {

    private JeiRecipeAccess() {
    }

    public static boolean isAvailable() {
        return Minecraft.getInstance().level != null;
    }

    public static <
            I extends RecipeInput,
            T extends Recipe<I>
            > @NotNull List<RecipeHolder<T>> getSorted(
            @NotNull RecipeType<T> recipeType
    ) {
        ClientLevel level =
                Minecraft.getInstance().level;

        if (level == null) {
            return List.of();
        }

        List<RecipeHolder<T>> recipes =
                new ArrayList<>(
                        level.getRecipeManager()
                                .getAllRecipesFor(
                                        recipeType
                                )
                );

        recipes.sort(
                Comparator.comparing(
                        RecipeHolder::id
                )
        );

        return List.copyOf(
                recipes
        );
    }

    public static <
            I extends RecipeInput,
            T extends Recipe<I>
            > @NotNull List<T> getSortedValues(
            @NotNull RecipeType<T> recipeType
    ) {
        return getSorted(
                recipeType
        ).stream()
                .map(RecipeHolder::value)
                .toList();
    }

    public static <
            I extends RecipeInput,
            T extends Recipe<I>
            > @NotNull List<RecipeHolder<T>> getSortedMatching(
            @NotNull RecipeType<T> recipeType,
            @NotNull Predicate<T> predicate
    ) {
        return getSorted(
                recipeType
        ).stream()
                .filter(
                        holder -> predicate.test(
                                holder.value()
                        )
                )
                .toList();
    }

    public static <
            I extends RecipeInput,
            T extends Recipe<I>,
            R
            > @NotNull List<R> translateSorted(
            @NotNull RecipeType<T> recipeType,
            @NotNull Function<RecipeHolder<T>, ? extends Collection<R>> translator
    ) {
        return translate(
                getSorted(
                        recipeType
                ),
                translator
        );
    }

    public static <
            I extends RecipeInput,
            T extends Recipe<I>,
            R
            > @NotNull List<R> translate(
            @NotNull Iterable<RecipeHolder<T>> holders,
            @NotNull Function<RecipeHolder<T>, ? extends Collection<R>> translator
    ) {
        Objects.requireNonNull(
                holders,
                "holders"
        );

        Objects.requireNonNull(
                translator,
                "translator"
        );

        List<R> result =
                new ArrayList<>();

        for (RecipeHolder<T> holder : holders) {
            try {
                Collection<R> translated =
                        Objects.requireNonNull(
                                translator.apply(
                                        holder
                                ),
                                "JEI recipe translator returned null"
                        );

                result.addAll(
                        translated
                );
            } catch (RuntimeException exception) {
                logTranslationFailure(
                        holder,
                        exception
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private static void logTranslationFailure(
            @NotNull RecipeHolder<?> holder,
            @NotNull RuntimeException exception
    ) {
        String message =
                exception.getMessage();

        JolCraftLogs.warn(
                JolCraftLogTags.RECIPE,
                "Skipping JEI recipe {}: {}",
                holder.id(),
                message != null
                        ? message
                        : exception.getClass()
                        .getName()
        );
    }
}
