package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen-only builder for {@link ItemProducer}.
 *
 * Policy:
 * - Single-assignment mode selection: first selection wins, subsequent mode calls are ignored.
 * - Mutation never throws.
 * - Strict build: required mode data must be present.
 * - No domain validation here beyond structural builder completeness.
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

    public ItemProducerBuilder map(
            TagKey<Structure> structureTag,
            Holder<MapDecorationType> decoration,
            String displayNameKey
    ) {
        if (this.kind != null) return this;
        this.kind = Kind.MAP;
        this.structureTag = structureTag;
        this.decoration = decoration;

        String k = displayNameKey == null ? "" : displayNameKey.trim();
        this.displayNameKey = k.isEmpty() ? null : k;

        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ItemProducer build() {
        if (kind == null) {
            throw new IllegalStateException("Missing required item producer kind");
        }

        return switch (kind) {
            case ITEM -> {
                if (item == null) {
                    throw new IllegalStateException("Missing required field '" + JolCraftParameterIds.ITEM + "'");
                }
                yield ItemProducer.holder(item);
            }

            case TAG -> {
                if (tag == null) {
                    throw new IllegalStateException("Missing required field '" + JolCraftParameterIds.TAG + "'");
                }
                yield ItemProducer.tag(tag);
            }

            case MAP -> {
                if (structureTag == null) {
                    throw new IllegalStateException("Missing required field 'structure_tag'");
                }
                if (decoration == null) {
                    throw new IllegalStateException("Missing required field 'map_decoration'");
                }
                if (displayNameKey == null) {
                    throw new IllegalStateException("Missing required field 'display_name_key'");
                }
                yield ItemProducer.map(structureTag, decoration, displayNameKey);
            }
        };
    }
}