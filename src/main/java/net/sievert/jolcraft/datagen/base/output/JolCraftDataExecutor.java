package net.sievert.jolcraft.datagen.base.output;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftOrderedEmissionBuilder;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class JolCraftDataExecutor {

    private JolCraftDataExecutor() {}

    public static <TTarget> void execute(
            @NotNull TTarget target,
            @NotNull JolCraftDataProvider<?> provider,
            @NotNull Iterable<? extends JolCraftDataEmission<TTarget>> emissions,
            @NotNull JolCraftDataTracking tracking
    ) {
        Objects.requireNonNull(target, JolCraftParameterIds.TARGET);
        Objects.requireNonNull(provider, JolCraftDictionary.PROVIDER);
        Objects.requireNonNull(emissions, JolCraftStrings.plural(JolCraftDictionary.EMISSION));
        Objects.requireNonNull(tracking, JolCraftDictionary.TRACK);

        for (JolCraftDataEmission<TTarget> emission : emissions) {
            Objects.requireNonNull(emission, "emissions contains null emission");

            String path = JolCraftDataPathResolver.resolvePath(provider, emission.fileName());
            emission.save(target, path);
            tracking.record(provider, path);
        }
    }

    public static <TTarget> void executeOrdered(
            @NotNull TTarget target,
            @NotNull JolCraftDataProvider<?> provider,
            @NotNull Iterable<? extends JolCraftOrderedEmissionBuilder<TTarget>> orderedEmissionBuilders,
            @NotNull JolCraftDataTracking tracking
    ) {
        Objects.requireNonNull(target, JolCraftParameterIds.TARGET);
        Objects.requireNonNull(provider, JolCraftDictionary.PROVIDER);
        Objects.requireNonNull(orderedEmissionBuilders, JolCraftStrings.plural(JolCraftDictionary.EMISSION));
        Objects.requireNonNull(tracking, JolCraftDictionary.TRACK);

        for (JolCraftOrderedEmissionBuilder<TTarget> orderedEmissionBuilder : orderedEmissionBuilders) {
            Objects.requireNonNull(
                    orderedEmissionBuilder,
                    "orderedEmissionBuilders contains null builder"
            );

            assignOrder(orderedEmissionBuilder, tracking);

            DataResult<JolCraftDataEmission<TTarget>> built = Objects.requireNonNull(
                    orderedEmissionBuilder.buildValidated(),
                    "orderedEmissionBuilder.buildValidated() returned null"
            );

            JolCraftDataEmission<TTarget> emission = built.getOrThrow(IllegalStateException::new);

            String path = JolCraftDataPathResolver.resolvePath(provider, emission.fileName());
            emission.save(target, path);
            tracking.record(provider, path);
        }
    }

    private static void assignOrder(
            @NotNull JolCraftOrderedEmissionBuilder<?> orderedEmissionBuilder,
            @NotNull JolCraftDataTracking tracking
    ) {
        if (orderedEmissionBuilder.order() != 0) {
            return;
        }

        String key = Objects.requireNonNull(
                orderedEmissionBuilder.orderKey(),
                "orderedEmissionBuilder.orderKey()"
        );

        int next = tracking.nextOrder(key);
        orderedEmissionBuilder.setOrder(next);
    }
}