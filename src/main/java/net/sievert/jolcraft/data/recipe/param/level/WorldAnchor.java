package net.sievert.jolcraft.data.recipe.param.level;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Execution anchor for resolving a spatial reference from {@link WorldContext}.
 *
 * Fail-closed behavior:
 * - Unknown serialized value -> {@link #PLAYER}
 * - Missing required context field -> {@link #resolve(WorldContext)} returns null
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum WorldAnchor implements StringRepresentable, JolCraftEnumHelper.StringId {

    /**
     * Use {@link WorldContext#player()} position.
     */
    PLAYER(JolCraftParameterIds.PLAYER),

    /**
     * Use {@link WorldContext#entity()} position.
     */
    ENTITY(JolCraftParameterIds.ENTITY);

    public static final Codec<WorldAnchor> CODEC =
            StringRepresentable.fromEnum(WorldAnchor::values);

    private static final int MAX_ANCHOR_NAME = 32;

    public static final StreamCodec<RegistryFriendlyByteBuf, WorldAnchor> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> buf.writeUtf(value.getSerializedName(), MAX_ANCHOR_NAME),
                    buf -> fromSerializedName(buf.readUtf(MAX_ANCHOR_NAME))
            );

    private final String id;

    WorldAnchor(String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull String getSerializedName() {
        return id;
    }

    /**
     * Parse an anchor from its serialized name.
     *
     * Fail-closed:
     * - null / unknown -> {@link #PLAYER}
     */
    public static @NotNull WorldAnchor fromSerializedName(@Nullable String id) {
        return JolCraftEnumHelper.byStringId(WorldAnchor.class, id, PLAYER);
    }

    /**
     * Resolve this anchor to a {@link BlockPos} using the given context.
     *
     * Fail-closed:
     * - required field missing -> null
     */
    public @Nullable BlockPos resolve(@NotNull WorldContext ctx) {
        return switch (this) {
            case PLAYER -> ctx.player().blockPosition();
            case ENTITY -> ctx.entity() != null ? ctx.entity().blockPosition() : null;
        };
    }

    /**
     * Resolve this anchor to a centered Vec3 (block center).
     *
     * Fail-closed:
     * - missing required context -> null
     */
    public @Nullable Vec3 resolveCenter(@NotNull WorldContext ctx) {
        BlockPos pos = resolve(ctx);
        if (pos == null) {
            return null;
        }

        return new Vec3(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D
        );
    }

    /**
     * Resolve optional anchor to a centered Vec3 (block center),
     * falling back to player position if anchor is null or cannot resolve.
     */
    public static @NotNull Vec3 resolveCenterOrPlayer(@Nullable WorldAnchor anchor, @NotNull WorldContext ctx) {
        if (anchor != null) {
            Vec3 resolved = anchor.resolveCenter(ctx);
            if (resolved != null) {
                return resolved;
            }
        }

        BlockPos pos = ctx.player().blockPosition();
        return new Vec3(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D
        );
    }

    public static void encodeOptional(@NotNull RegistryFriendlyByteBuf buf, @Nullable WorldAnchor anchor) {
        if (anchor == null) {
            buf.writeBoolean(false);
            return;
        }

        buf.writeBoolean(true);
        STREAM_CODEC.encode(buf, anchor);
    }

    public static @Nullable WorldAnchor decodeOptional(@NotNull RegistryFriendlyByteBuf buf) {
        return buf.readBoolean() ? STREAM_CODEC.decode(buf) : null;
    }
}