package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntityProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpawnConfig;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpec;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen builder for {@link EntitySpec}.
 *
 * S+:
 * - mutation never throws
 * - fail-closed build
 * - minimal surface
 */
public final class EntitySpecBuilder {

    private Holder<EntityType<?>> entity;
    private TagKey<EntityType<?>> tag;
    private IntRange count = IntRange.ONE;
    private @Nullable CompoundTag nbt;
    private @Nullable EntitySpawnConfig spawn;

    private EntitySpecBuilder() {}

    public static @NotNull EntitySpecBuilder create() {
        return new EntitySpecBuilder();
    }

    public static @NotNull EntitySpecBuilder builder() {
        return create();
    }

    public @NotNull EntitySpecBuilder entity(@Nullable Holder<EntityType<?>> entity) {
        this.entity = entity;
        this.tag = null;
        return this;
    }

    public @NotNull EntitySpecBuilder tag(@Nullable TagKey<EntityType<?>> tag) {
        this.tag = tag;
        this.entity = null;
        return this;
    }

    public @NotNull EntitySpecBuilder producer(@Nullable EntityProducer producer) {
        if (producer == null) {
            this.entity = null;
            this.tag = null;
            return this;
        }

        this.entity = producer.entityOpt().orElse(null);
        this.tag = producer.tagOpt().orElse(null);
        return this;
    }

    public @NotNull EntitySpecBuilder clearProducer() {
        this.entity = null;
        this.tag = null;
        return this;
    }

    public @NotNull EntitySpecBuilder count(@Nullable IntRange count) {
        this.count = count != null ? count : IntRange.ONE;
        return this;
    }

    public @NotNull EntitySpecBuilder countFixed(int value) {
        this.count = IntRange.fixed(value);
        return this;
    }

    public @NotNull EntitySpecBuilder nbt(@Nullable CompoundTag nbt) {
        this.nbt = (nbt != null && nbt.isEmpty()) ? null : nbt;
        return this;
    }

    public @NotNull EntitySpecBuilder spawn(@Nullable EntitySpawnConfig spawn) {
        this.spawn = spawn;
        return this;
    }

    public @NotNull DataResult<EntitySpec> build() {
        boolean hasEntity = entity != null;
        boolean hasTag = tag != null;

        if (hasEntity == hasTag) {
            return DataResult.error(() ->
                    "EntitySpecBuilder requires exactly one of '" +
                            JolCraftParameterIds.ENTITY + "' or '" +
                            JolCraftParameterIds.TAG + "'"
            );
        }

        EntityProducer producer = hasEntity
                ? EntityProducer.entity(entity)
                : EntityProducer.tag(tag);

        return new EntitySpec(producer, count, nbt, spawn).validate();
    }

    public @Nullable EntitySpec buildOrNull() {
        return build().result().orElse(null);
    }
}