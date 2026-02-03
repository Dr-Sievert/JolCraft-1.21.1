package net.sievert.jolcraft.world.entity.custom.util.dwarf.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.item.util.coin.CoinPouchHelper;

import java.util.Optional;
import java.util.function.UnaryOperator;

public record DwarfItemCost(Holder<Item> item, int count, DataComponentPredicate components, ItemStack itemStack) {

    public static final Codec<DwarfItemCost> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Item.CODEC.fieldOf("id").forGetter(DwarfItemCost::item),
                    ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(DwarfItemCost::count),
                    DataComponentPredicate.CODEC.optionalFieldOf("components", DataComponentPredicate.EMPTY).forGetter(DwarfItemCost::components)
            ).apply(instance, DwarfItemCost::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DwarfItemCost> STREAM_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<DwarfItemCost>> OPTIONAL_STREAM_CODEC;

    public DwarfItemCost(ItemLike itemLike) {
        this(itemLike, 1);
    }

    @SuppressWarnings("deprecation")
    public DwarfItemCost(ItemLike itemLike, int count) {
        this(itemLike.asItem().builtInRegistryHolder(), count, DataComponentPredicate.EMPTY);
    }

    public DwarfItemCost(Holder<Item> item, int count, DataComponentPredicate components) {
        this(item, count, components, createStack(item, count, components));
    }

    private static ItemStack createStack(Holder<Item> item, int count, DataComponentPredicate predicate) {
        return new ItemStack(item, count, predicate.asPatch());
    }

    public DwarfItemCost withComponents(UnaryOperator<DataComponentPredicate.Builder> builder) {
        return new DwarfItemCost(this.item, this.count, builder.apply(DataComponentPredicate.builder()).build());
    }

    public boolean test(ItemStack stack) {
        // Check if the stack matches the item and count requirement
        if (stack.is(this.item) && stack.getCount() >= this.count && this.components.test(stack)) {
            return true;
        }
        // If it's a CoinPouchItem, check if the pouch has enough coins (only if the cost is for GOLD_COIN)
        if (stack.getItem() instanceof CoinPouchItem && item.value() == JolCraftItems.GOLD_COIN.get()) {
            int coins = CoinPouchHelper.getCoins(stack);
            return coins >= this.count;
        }

        return false;
    }

    public static ItemCost toVanillaCost(DwarfItemCost src) {
        return new ItemCost(src.item(), src.count(), src.components());
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.holderRegistry(Registries.ITEM),
                DwarfItemCost::item,
                ByteBufCodecs.VAR_INT,
                DwarfItemCost::count,
                DataComponentPredicate.STREAM_CODEC,
                DwarfItemCost::components,
                DwarfItemCost::new
        );
        OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);
    }
}
