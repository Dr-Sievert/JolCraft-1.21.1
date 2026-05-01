package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntityProducer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builder for {@link EntityProducer}.
 *
 * Contract:
 * - exactly one of entity/tag
 * - mutation never throws
 * - strict build via {@link #build()}
 */
public final class EntityProducerBuilder {

    private static final String KEY_ENTITY = JolCraftParameterIds.ENTITY;
    private static final String KEY_TAG = JolCraftParameterIds.TAG;

    private Holder<EntityType<?>> entity;
    private TagKey<EntityType<?>> tag;

    private EntityProducerBuilder() {}

    public static @NotNull EntityProducerBuilder create() {
        return new EntityProducerBuilder();
    }

    public static @NotNull EntityProducerBuilder builder() {
        return create();
    }

    // ---------------------------------------------------------------------
    // Mutators
    // ---------------------------------------------------------------------

    public @NotNull EntityProducerBuilder entity(@Nullable Holder<EntityType<?>> entity) {
        this.entity = entity;
        this.tag = null;
        return this;
    }

    public @NotNull EntityProducerBuilder tag(@Nullable TagKey<EntityType<?>> tag) {
        this.tag = tag;
        this.entity = null;
        return this;
    }

    public @NotNull EntityProducerBuilder clear() {
        this.entity = null;
        this.tag = null;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    /**
     * Strict builder validation + build.
     * Fail-closed: returns an error if invalid; never throws.
     */
    public @NotNull DataResult<EntityProducer> build() {
        boolean hasEntity = entity != null;
        boolean hasTag = tag != null;

        int count = (hasEntity ? 1 : 0) + (hasTag ? 1 : 0);
        if (count != 1) {
            return DataResult.error(() ->
                    "EntityProducerBuilder must set exactly one of '" + KEY_ENTITY + "' or '" + KEY_TAG + "'"
            );
        }

        if (hasEntity) {
            return DataResult.success(EntityProducer.entity(entity));
        }

        return DataResult.success(EntityProducer.tag(tag));
    }

    /**
     * Convenience for call sites that want a value directly.
     * Invalid state throws with the builder validation message.
     */
    public @NotNull EntityProducer buildOrThrow() {
        DataResult<EntityProducer> result = build();
        if (result.result().isPresent()) {
            return result.result().orElseThrow();
        }

        String msg = result.error()
                .map(DataResult.Error::message)
                .orElse("invalid EntityProducerBuilder state");

        throw new IllegalStateException(msg);
    }
}