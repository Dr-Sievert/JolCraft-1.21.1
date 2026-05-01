package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntityAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen builder for {@link EntityAttributes}.
 *
 * S+:
 * - mutation never throws
 * - later calls overwrite earlier ones
 * - fail-closed build
 * - holder-based registry-safe API
 */
public final class EntityAttributesBuilder {

    private final List<EntityAttributes.Entry> entries = new ArrayList<>();

    private EntityAttributesBuilder() {}

    public static @NotNull EntityAttributesBuilder create() {
        return new EntityAttributesBuilder();
    }

    public static @NotNull EntityAttributesBuilder builder() {
        return create();
    }

    public @NotNull EntityAttributesBuilder attribute(@Nullable Holder<Attribute> attribute, double value) {
        if (attribute == null) {
            return this;
        }

        entries.removeIf(entry -> entry.attribute().equals(attribute));
        entries.add(new EntityAttributes.Entry(attribute, value));
        return this;
    }

    public @NotNull EntityAttributesBuilder add(@Nullable Holder<Attribute> attribute, double value) {
        return attribute(attribute, value);
    }

    public @NotNull EntityAttributesBuilder attributes(@Nullable EntityAttributes attributes) {
        entries.clear();
        if (attributes != null) {
            entries.addAll(attributes.entries());
        }
        return this;
    }

    public @NotNull EntityAttributesBuilder clear() {
        entries.clear();
        return this;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public @NotNull DataResult<EntityAttributes> build() {
        if (entries.isEmpty()) {
            return DataResult.success(EntityAttributes.EMPTY);
        }

        return new EntityAttributes(List.copyOf(entries)).validate();
    }

    public @Nullable EntityAttributes buildOrNull() {
        return build().result().orElse(null);
    }
}