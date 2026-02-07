package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class DwarfMerchantOffers extends ArrayList<DwarfMerchantOffer> {
    public static final Codec<DwarfMerchantOffers> CODEC = DwarfMerchantOffer.CODEC
            .listOf()
            .optionalFieldOf("Recipes", List.of())
            .xmap(DwarfMerchantOffers::new, Function.identity())
            .codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, DwarfMerchantOffers> STREAM_CODEC = DwarfMerchantOffer.STREAM_CODEC
            .apply(ByteBufCodecs.collection(DwarfMerchantOffers::new));

    public DwarfMerchantOffers() {
    }

    private DwarfMerchantOffers(int size) {
        super(size);
    }

    private DwarfMerchantOffers(Collection<DwarfMerchantOffer> offers) {
        super(offers);
    }

    @Nullable
    public DwarfMerchantOffer getRecipeFor(ItemStack stackA, ItemStack stackB, int index) {
        if (index >= 0 && index < this.size()) {
            DwarfMerchantOffer merchantoffer1 = this.get(index);
            return merchantoffer1.satisfiedBy(stackA, stackB) ? merchantoffer1 : null;
        } else {
            for (DwarfMerchantOffer merchantoffer : this) {
                if (merchantoffer.satisfiedBy(stackA, stackB)) {
                    return merchantoffer;
                }
            }

            return null;
        }
    }

    public DwarfMerchantOffers copy() {
        DwarfMerchantOffers merchantoffers = new DwarfMerchantOffers(this.size());

        for (DwarfMerchantOffer merchantoffer : this) {
            merchantoffers.add(merchantoffer.copy());
        }

        return merchantoffers;
    }
}
