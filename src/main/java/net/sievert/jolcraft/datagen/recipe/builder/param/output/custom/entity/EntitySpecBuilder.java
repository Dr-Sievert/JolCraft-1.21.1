package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
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
 * - no throwing
 * - fail-closed
 * - minimal surface
 */
public final class EntitySpecBuilder {

    private EntityProducer producer = EntityProducer.EMPTY;
    private IntRange count = IntRange.ONE;
    private @Nullable CompoundTag nbt;
    private @Nullable EntitySpawnConfig spawn;

    private EntitySpecBuilder() {}

    public static @NotNull EntitySpecBuilder builder() {
        return new EntitySpecBuilder();
    }

    // ---------------------------------------------------------------------
    // Producer
    // ---------------------------------------------------------------------

    public @NotNull EntitySpecBuilder entity(@Nullable Holder<EntityType<?>> entity) {
        this.producer = EntityProducer.entity(entity);
        return this;
    }

    public @NotNull EntitySpecBuilder tag(@Nullable TagKey<EntityType<?>> tag) {
        this.producer = EntityProducer.tag(tag);
        return this;
    }

    public @NotNull EntitySpecBuilder producer(@Nullable EntityProducer producer) {
        this.producer = producer != null ? producer : EntityProducer.EMPTY;
        return this;
    }

    // ---------------------------------------------------------------------
    // Count
    // ---------------------------------------------------------------------

    public @NotNull EntitySpecBuilder count(@Nullable IntRange count) {
        this.count = count != null ? count : IntRange.ONE;
        return this;
    }

    public @NotNull EntitySpecBuilder countFixed(int value) {
        this.count = IntRange.fixed(value);
        return this;
    }

    // ---------------------------------------------------------------------
    // Optional fields
    // ---------------------------------------------------------------------

    public @NotNull EntitySpecBuilder nbt(@Nullable CompoundTag nbt) {
        this.nbt = (nbt != null && nbt.isEmpty()) ? null : nbt;
        return this;
    }

    public @NotNull EntitySpecBuilder spawn(@Nullable EntitySpawnConfig spawn) {
        this.spawn = spawn;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public @NotNull DataResult<EntitySpec> build() {
        EntitySpec spec = new EntitySpec(producer, count, nbt, spawn);
        return spec.validate();
    }

    public @Nullable EntitySpec buildOrNull() {
        return build().result().orElse(null);
    }
}