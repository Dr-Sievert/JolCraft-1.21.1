package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item;

import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen-only builder for {@link ItemSpec}.
 *
 * Policy:
 * - No throwing, no logging.
 * - No validation here (ParamBuilder.buildValidated delegates to ItemSpec.validate()).
 * - Single-assignment for one-of substructures is handled by their own builders (e.g. ItemProducerBuilder).
 * - This builder is a simple assembler for (producer, count).
 */
public final class ItemSpecBuilder implements ParamBuilder<ItemSpec> {

    private ItemProducer producer;
    private IntRange count;

    private ItemSpecBuilder() {}

    public static ItemSpecBuilder create() {
        return new ItemSpecBuilder();
    }

    // ---------------------------------------------------------------------
    // SETTERS
    // ---------------------------------------------------------------------

    public ItemSpecBuilder producer(ItemProducer producer) {
        this.producer = producer;
        return this;
    }

    public ItemSpecBuilder producer(ItemProducerBuilder builder) {
        this.producer = builder != null ? builder.build() : null;
        return this;
    }

    public ItemSpecBuilder count(IntRange count) {
        this.count = count;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ItemSpec build() {
        return new ItemSpec(producer, count);
    }
}