package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen-only builder for {@link ItemProducer}.
 *
 * Policy:
 * - Single-assignment mode selection: first selection wins, subsequent mode calls are ignored.
 * - No throwing, no logging.
 * - Fail-closed assembly:
 *   - If a selected mode is missing required inputs, build() returns {@link ItemProducer#EMPTY}.
 *   - This keeps builder output sentinel-clean and avoids emitting known-invalid shapes.
 * - No domain validation here (ParamBuilder.buildValidated delegates to ItemProducer.validate()).
 */
public final class ItemProducerBuilder implements ParamBuilder<ItemProducer> {

    private enum Kind { ITEM, TAG, MAP }

    private Kind kind;

    private Holder<Item> item;
    private TagKey<Item> tag;

    private TagKey<Structure> structureTag;
    private Holder<MapDecorationType> decoration;
    private String displayNameKey;

    private ItemProducerBuilder() {}

    public static ItemProducerBuilder create() {
        return new ItemProducerBuilder();
    }

    // ---------------------------------------------------------------------
    // MODE SELECTION
    // ---------------------------------------------------------------------

    public ItemProducerBuilder item(Holder<Item> item) {
        if (this.kind != null) return this;
        this.kind = Kind.ITEM;
        this.item = item;
        return this;
    }

    public ItemProducerBuilder tag(TagKey<Item> tag) {
        if (this.kind != null) return this;
        this.kind = Kind.TAG;
        this.tag = tag;
        return this;
    }

    public ItemProducerBuilder map(TagKey<Structure> structureTag,
                                   Holder<MapDecorationType> decoration,
                                   String displayNameKey) {
        if (this.kind != null) return this;
        this.kind = Kind.MAP;
        this.structureTag = structureTag;
        this.decoration = decoration;

        // Canonicalize name key (fail-closed on blank).
        String k = displayNameKey == null ? "" : displayNameKey.trim();
        this.displayNameKey = k.isEmpty() ? null : k;

        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ItemProducer build() {
        if (kind == null) return ItemProducer.EMPTY;

        return switch (kind) {
            case ITEM -> (item == null)
                    ? ItemProducer.EMPTY
                    : ItemProducer.item(item.value());

            case TAG -> (tag == null)
                    ? ItemProducer.EMPTY
                    : ItemProducer.tag(tag);

            case MAP -> (structureTag == null || decoration == null || displayNameKey == null)
                    ? ItemProducer.EMPTY
                    : ItemProducer.map(structureTag, decoration, displayNameKey);
        };
    }
}