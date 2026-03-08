package net.sievert.jolcraft.data.recipe.param.output.custom.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ItemSpec(
        ItemProducer producer,
        IntRange count
) implements SelfValidating<ItemSpec>, RegistryIntrospectionSource {

    public static final ItemSpec EMPTY = new ItemSpec(ItemProducer.EMPTY, IntRange.ONE);

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<ItemSpec> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ItemProducer.CODEC
                            .fieldOf(JolCraftParameterIds.PRODUCER)
                            .forGetter(ItemSpec::producer),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(ItemSpec::count)
            ).apply(instance, ItemSpec::new));

    public static final Codec<ItemSpec> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSpec> STREAM_CODEC =
            StreamCodec.composite(
                    ItemProducer.STREAM_CODEC, ItemSpec::producer,
                    IntRange.STREAM_CODEC, ItemSpec::count,
                    ItemSpec::new
            );

    // ---------------------------------------------------------------------
    // CANONICAL
    // ---------------------------------------------------------------------

    public ItemSpec(ItemProducer producer, IntRange count) {
        this.producer = producer != null ? producer : ItemProducer.EMPTY;
        this.count = count != null ? count : IntRange.ONE;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return producer.introspections();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<ItemSpec> validate() {

        DataResult<ItemProducer> pv = producer.validate();
        if (pv.error().isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.PRODUCER + " invalid: " +
                            pv.error().map(DataResult.Error::message).orElse(""));
        }

        DataResult<IntRange> cv = IntRange.validateRange(count);
        if (cv.error().isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.COUNT + " invalid: " +
                            cv.error().map(DataResult.Error::message).orElse(""));
        }

        return DataResult.success(this);
    }

    // ---------------------------------------------------------------------
    // RUNTIME
    // ---------------------------------------------------------------------

    public @NotNull ItemStack create(@NotNull WorldContext ctx) {

        ItemStack stack = producer.create(ctx);
        if (stack.isEmpty()) return stack;

        int rolled = count.roll(ctx.random());
        if (rolled <= 0) return ItemStack.EMPTY;

        int max = Math.max(1, stack.getMaxStackSize());
        stack.setCount(Math.min(rolled, max));

        return stack;
    }

    // ---------------------------------------------------------------------
    // FACTORY
    // ---------------------------------------------------------------------

    public static ItemSpec of(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return EMPTY;

        return new ItemSpec(
                ItemProducer.item(stack.getItemHolder().value()),
                IntRange.ONE
        );
    }
}