package net.sievert.jolcraft.world.entity.custom.dwarf.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;

import java.util.Optional;

public record DwarfItemCost(Holder<Item> item, int count, DataComponentPredicate components, ItemStack itemStack) {

    private static final Codec<Holder<Item>> ITEM_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.ITEM);

    public static final Codec<DwarfItemCost> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ITEM_HOLDER_CODEC
                                    .fieldOf(JolCraftDictionary.ID)
                                    .forGetter(DwarfItemCost::item),

                            ExtraCodecs.POSITIVE_INT
                                    .optionalFieldOf(JolCraftDictionary.AMOUNT, 1)
                                    .forGetter(DwarfItemCost::count),

                            DataComponentPredicate.CODEC
                                    .optionalFieldOf(
                                            JolCraftStrings.plural(JolCraftDictionary.COMPONENT),
                                            DataComponentPredicate.EMPTY
                                    )
                                    .forGetter(DwarfItemCost::components)
                    ).apply(instance, DwarfItemCost::new)
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, DwarfItemCost> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.holderRegistry(Registries.ITEM),
                    DwarfItemCost::item,
                    ByteBufCodecs.VAR_INT,
                    DwarfItemCost::count,
                    DataComponentPredicate.STREAM_CODEC,
                    DwarfItemCost::components,
                    DwarfItemCost::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<DwarfItemCost>> OPTIONAL_STREAM_CODEC =
            STREAM_CODEC.apply(ByteBufCodecs::optional);

    @SuppressWarnings("deprecation")
    public DwarfItemCost(ItemLike itemLike, int count) {
        this(itemLike.asItem().builtInRegistryHolder(), count, DataComponentPredicate.EMPTY);
    }

    /**
     * 3-arg constructor used by CODEC/STREAM_CODEC.
     */
    public DwarfItemCost(Holder<Item> item, int count, DataComponentPredicate components) {
        this(item, count, components, createStack(item, count, components));
    }

    private static ItemStack createStack(Holder<Item> item, int count, DataComponentPredicate predicate) {
        return new ItemStack(item, count, predicate.asPatch());
    }

    /**
     * True if this cost is a "coin" cost (any item in the COINS tag).
     */
    public boolean isCoinCost() {
        return this.item.is(JolCraftTags.Items.COINS);
    }

    public int maxAllowedCount() {
        return isCoinCost()
                ? CoinPouchItem.MAX_COINS
                : this.itemStack.getMaxStackSize();
    }

    public int requiredCount() {
        return Math.max(
                1,
                Math.min(
                        this.count,
                        maxAllowedCount()
                )
        );
    }

    public boolean test(ItemStack stack) {
        return test(stack, requiredCount());
    }

    public boolean test(ItemStack stack, int requiredCount) {
        if (requiredCount <= 0) return true;

        if (isCoinCost() && stack.getItem() instanceof CoinPouchItem) {
            return stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0) >= requiredCount;
        }

        return stack.is(this.item) && stack.getCount() >= requiredCount && this.components.test(stack);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean take(ItemStack stack, int requiredCount) {
        if (requiredCount <= 0) {
            return true;
        }

        if (!test(stack, requiredCount)) {
            return false;
        }

        if (isCoinCost() && stack.getItem() instanceof CoinPouchItem) {
            int coins = stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
            int remaining = Math.max(0, coins - requiredCount);
            stack.set(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), remaining);
            return true;
        }

        stack.shrink(requiredCount);
        return true;
    }
}