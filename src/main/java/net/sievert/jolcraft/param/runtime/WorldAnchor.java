package net.sievert.jolcraft.param.runtime;

import net.minecraft.core.BlockPos;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Resolves a position from a {@link WorldContext}.
 *
 * Contracts:
 * - WorldContext guarantees at least one of player/entity exists
 * - Forced resolution does NOT fallback
 * - Auto resolution prefers entity over player
 */
public enum WorldAnchor implements JolCraftEnumHelper.StringId {

    PLAYER(JolCraftParameterIds.PLAYER),
    ENTITY(JolCraftParameterIds.ENTITY);

    private final String id;

    WorldAnchor(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /* ------------------------------------------------------------ */
    /* Forced anchor                                                */
    /* ------------------------------------------------------------ */

    /**
     * Resolves strictly from this anchor.
     * No fallback.
     */
    public @Nullable BlockPos resolveAnchor(@NotNull WorldContext ctx) {
        return switch (this) {
            case PLAYER -> ctx.player() != null ? ctx.player().blockPosition() : null;
            case ENTITY -> ctx.entity() != null ? ctx.entity().blockPosition() : null;
        };
    }

    /* ------------------------------------------------------------ */
    /* Automatic runtime resolution                                 */
    /* ------------------------------------------------------------ */

    /**
     * entity -> player
     */
    public static @Nullable BlockPos auto(@NotNull WorldContext ctx) {
        return resolve(ctx, null, null);
    }

    /**
     * manual -> entity -> player
     */
    public static @Nullable BlockPos auto(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual
    ) {
        return resolve(ctx, manual, null);
    }

    /* ------------------------------------------------------------ */
    /* Forced anchor helpers                                        */
    /* ------------------------------------------------------------ */

    /**
     * forced anchor only (no fallback)
     */
    public static @Nullable BlockPos forced(
            @NotNull WorldContext ctx,
            @NotNull WorldAnchor anchor
    ) {
        return anchor.resolveAnchor(ctx);
    }

    /**
     * manual -> forced anchor
     */
    public static @Nullable BlockPos forced(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual,
            @NotNull WorldAnchor anchor
    ) {
        return manual != null ? manual : anchor.resolveAnchor(ctx);
    }

    /* ------------------------------------------------------------ */
    /* Core resolver                                                */
    /* ------------------------------------------------------------ */

    /**
     * entity -> player
     */
    public static @Nullable BlockPos resolve(@NotNull WorldContext ctx) {
        return resolve(ctx, null, null);
    }

    /**
     * manual -> entity -> player
     */
    public static @Nullable BlockPos resolve(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual
    ) {
        return resolve(ctx, manual, null);
    }

    /**
     * forced anchor only
     */
    public static @Nullable BlockPos resolve(
            @NotNull WorldContext ctx,
            @NotNull WorldAnchor anchor
    ) {
        return anchor.resolveAnchor(ctx);
    }

    /**
     * Resolution order:
     *
     * manual -> forced anchor -> entity -> player
     */
    public static @Nullable BlockPos resolve(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual,
            @Nullable WorldAnchor anchor
    ) {
        if (manual != null) return manual;

        if (anchor != null) return anchor.resolveAnchor(ctx);

        if (ctx.entity() != null) return ctx.entity().blockPosition();
        if (ctx.player() != null) return ctx.player().blockPosition();

        return null;
    }
}