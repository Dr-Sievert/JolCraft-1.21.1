package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntityAttributes;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntityProducer;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntitySpawnConfig;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntitySpec;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
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
    private @Nullable Component name;
    private boolean nameVisible;
    private final EntityAttributesBuilder attributes = EntityAttributesBuilder.builder();
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

    public @NotNull EntitySpecBuilder name(@Nullable Component name) {
        this.name = name;
        return this;
    }

    public @NotNull EntitySpecBuilder clearName() {
        this.name = null;
        return this;
    }

    public @NotNull EntitySpecBuilder nameVisible(boolean nameVisible) {
        this.nameVisible = nameVisible;
        return this;
    }

    public @NotNull EntitySpecBuilder attributes(@Nullable EntityAttributes attributes) {
        this.attributes.attributes(attributes);
        return this;
    }

    public @NotNull EntitySpecBuilder attribute(@Nullable Holder<Attribute> attribute, double value) {
        this.attributes.attribute(attribute, value);
        return this;
    }

    public @NotNull EntitySpecBuilder clearAttributes() {
        this.attributes.clear();
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

        DataResult<EntityAttributes> attributesResult = attributes.build();
        var attributesError = attributesResult.error();
        return attributesError.<DataResult<EntitySpec>>map(entityAttributesError -> DataResult.error(() ->
                JolCraftParameterIds.ATTRIBUTES + " invalid: " + entityAttributesError.message())).orElseGet(() -> new EntitySpec(
                producer,
                count,
                name,
                nameVisible,
                attributesResult.result().orElse(EntityAttributes.EMPTY),
                spawn
        ).validate());

    }

    public @Nullable EntitySpec buildOrNull() {
        return build().result().orElse(null);
    }
}