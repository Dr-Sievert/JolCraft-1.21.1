package net.sievert.jolcraft.data.recipe.param.input.base;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InputParam<T extends InputParam<T, S>, S> extends SelfValidating<T> {

    @NotNull ResourceLocation typeId();

    /**
     * Fail-closed matching against the provided context and explicit subject.
     *
     * Contract:
     * - ctx is never null.
     * - subject may be null: implementations MUST treat null as non-matching.
     * - No hidden subject sourcing (no ctx.stack() inside the interface contract).
     */
    boolean matches(@NotNull WorldContext ctx, @Nullable S subject);

    /**
     * Each concrete param exposes its codec for dispatch registration.
     */
    @NotNull Codec<T> codec();
}