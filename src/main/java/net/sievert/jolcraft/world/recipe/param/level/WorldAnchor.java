package net.sievert.jolcraft.world.recipe.param.level;

import net.minecraft.core.BlockPos;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

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

    /** Resolve this anchor only. */
    public @Nullable BlockPos resolveAnchor(@NotNull WorldContext ctx) {
        return switch (this) {
            case PLAYER -> ctx.player() != null ? ctx.player().blockPosition() : null;
            case ENTITY -> ctx.entity() != null ? ctx.entity().blockPosition() : null;
        };
    }

    /* ------------------------------------------------------------ */
    /* Automatic runtime resolution                                 */
    /* ------------------------------------------------------------ */

    /** entity -> player -> null */
    public static @Nullable BlockPos auto(@NotNull WorldContext ctx) {
        return resolve(ctx, null, null);
    }

    /** manual -> entity -> player -> null */
    public static @Nullable BlockPos auto(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual
    ) {
        return resolve(ctx, manual, null);
    }

    /* ------------------------------------------------------------ */
    /* Forced anchor helpers                                        */
    /* ------------------------------------------------------------ */

    /** entity/player only (no automatic fallback) */
    public static @Nullable BlockPos forced(
            @NotNull WorldContext ctx,
            @NotNull WorldAnchor anchor
    ) {
        return resolve(ctx, null, anchor);
    }

    /** manual -> forced anchor -> null */
    public static @Nullable BlockPos forced(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual,
            @NotNull WorldAnchor anchor
    ) {
        return resolve(ctx, manual, anchor);
    }

    /* ------------------------------------------------------------ */
    /* Core resolver                                                */
    /* ------------------------------------------------------------ */

    /**
     * Resolution order:
     *
     * entity -> player -> null
     */
    public static @Nullable BlockPos resolve(@NotNull WorldContext ctx) {
        return resolve(ctx, null, null);
    }

    /**
     * Resolution order:
     *
     * manual -> entity -> player -> null
     */
    public static @Nullable BlockPos resolve(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual
    ) {
        return resolve(ctx, manual, null);
    }

    /**
     * Resolution order:
     *
     * forced anchor -> null
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
     * manual -> forced anchor -> entity -> player -> null
     */
    public static @Nullable BlockPos resolve(
            @NotNull WorldContext ctx,
            @Nullable BlockPos manual,
            @Nullable WorldAnchor anchor
    ) {
        if (manual != null) {
            return manual;
        }

        if (anchor != null) {
            return anchor.resolveAnchor(ctx);
        }

        if (ctx.entity() != null) {
            return ctx.entity().blockPosition();
        }

        if (ctx.player() != null) {
            return ctx.player().blockPosition();
        }

        return null;
    }
}