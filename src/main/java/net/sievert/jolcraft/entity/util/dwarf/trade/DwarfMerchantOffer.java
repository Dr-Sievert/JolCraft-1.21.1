package net.sievert.jolcraft.entity.util.dwarf.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.item.custom.merchant.CoinPouchItem;

public class DwarfMerchantOffer {
    public static final Codec<DwarfMerchantOffer> CODEC = RecordCodecBuilder.create(
            p_324269_ -> p_324269_.group(
                            DwarfItemCost.CODEC.fieldOf("buy").forGetter(p_330121_ -> p_330121_.baseCostA),
                            DwarfItemCost.CODEC.lenientOptionalFieldOf("buyB").forGetter(p_330120_ -> p_330120_.costB),
                            ItemStack.CODEC.fieldOf("sell").forGetter(p_324095_ -> p_324095_.result),
                            Codec.INT.lenientOptionalFieldOf("uses", 0).forGetter(p_324003_ -> p_324003_.uses),
                            Codec.INT.lenientOptionalFieldOf("maxUses", 4).forGetter(p_323849_ -> p_323849_.maxUses),
                            Codec.BOOL.lenientOptionalFieldOf("rewardExp", Boolean.TRUE).forGetter(p_323485_ -> p_323485_.rewardExp),
                            Codec.INT.lenientOptionalFieldOf("specialPrice", 0).forGetter(p_324423_ -> p_324423_.specialPriceDiff),
                            Codec.INT.lenientOptionalFieldOf("demand", 0).forGetter(p_324040_ -> p_324040_.demand),
                            Codec.FLOAT.lenientOptionalFieldOf("priceMultiplier", 0.0F).forGetter(p_323953_ -> p_323953_.priceMultiplier),
                            Codec.INT.lenientOptionalFieldOf("xp", 1).forGetter(p_324202_ -> p_324202_.xp)
                    )
                    .apply(p_324269_, DwarfMerchantOffer::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, DwarfMerchantOffer> STREAM_CODEC = StreamCodec.of(
            DwarfMerchantOffer::writeToStream, DwarfMerchantOffer::createFromStream
    );
    /**
     * The first input for this offer.
     */
    private final DwarfItemCost baseCostA;
    /**
     * The second input for this offer.
     */
    private final Optional<DwarfItemCost> costB;
    /**
     * The output of this offer.
     */
    private final ItemStack result;
    private int uses;
    private final int maxUses;
    private final boolean rewardExp;
    private int specialPriceDiff;
    private int demand;
    private final float priceMultiplier;
    private final int xp;

    private DwarfMerchantOffer(
            DwarfItemCost baseCostA,
            Optional<DwarfItemCost> costB,
            ItemStack result,
            int uses,
            int maxUses,
            boolean rewardExp,
            int specialPriceDiff,
            int demand,
            float priceMultiplier,
            int xp
    ) {
        this.baseCostA = baseCostA;
        this.costB = costB;
        this.result = result;
        this.uses = uses;
        this.maxUses = maxUses;
        this.rewardExp = rewardExp;
        this.specialPriceDiff = specialPriceDiff;
        this.demand = demand;
        this.priceMultiplier = priceMultiplier;
        this.xp = xp;
    }

    public DwarfMerchantOffer(DwarfItemCost baseCostA, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        this(baseCostA, Optional.empty(), result, maxUses, xp, priceMultiplier);
    }

    public DwarfMerchantOffer(DwarfItemCost baseCostA, Optional<DwarfItemCost> costB, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        this(baseCostA, costB, result, 0, maxUses, xp, priceMultiplier);
    }

    public DwarfMerchantOffer(DwarfItemCost baseCostA, Optional<DwarfItemCost> costB, ItemStack result, int uses, int maxUses, int xp, float priceMultiplier) {
        this(baseCostA, costB, result, uses, maxUses, xp, priceMultiplier, 0);
    }

    public DwarfMerchantOffer(
            DwarfItemCost baseCostA, Optional<DwarfItemCost> costB, ItemStack result, int uses, int maxUses, int xp, float priceMultiplier, int demand
    ) {
        this(baseCostA, costB, result, uses, maxUses, true, 0, demand, priceMultiplier, xp);
    }

    private DwarfMerchantOffer(DwarfMerchantOffer other) {
        this(
                other.baseCostA,
                other.costB,
                other.result.copy(),
                other.uses,
                other.maxUses,
                other.rewardExp,
                other.specialPriceDiff,
                other.demand,
                other.priceMultiplier,
                other.xp
        );
    }

    public ItemStack getBaseCostA() {
        return this.baseCostA.itemStack();
    }

    public ItemStack getCostA() {
        return this.baseCostA.itemStack().copyWithCount(this.getModifiedCostCount(this.baseCostA));
    }

    private int getModifiedCostCount(DwarfItemCost itemCost) {
        int i = itemCost.count();
        int j = Math.max(0, Mth.floor((float)(i * this.demand) * this.priceMultiplier));
        return Mth.clamp(i + j + this.specialPriceDiff, 1, itemCost.itemStack().getMaxStackSize());
    }

    public ItemStack getCostB() {
        return this.costB.map(DwarfItemCost::itemStack).orElse(ItemStack.EMPTY);
    }

    public DwarfItemCost getItemCostA() {
        return this.baseCostA;
    }

    public Optional<DwarfItemCost> getItemCostB() {
        return this.costB;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void updateDemand() {
        this.demand = this.demand + this.uses - (this.maxUses - this.uses);
    }

    public ItemStack assemble() {
        return this.result.copy();
    }

    public int getUses() {
        return this.uses;
    }

    public void resetUses() {
        this.uses = 0;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void increaseUses() {
        this.uses++;
    }

    public int getDemand() {
        return this.demand;
    }

    public void addToSpecialPriceDiff(int add) {
        this.specialPriceDiff += add;
    }

    public void resetSpecialPriceDiff() {
        this.specialPriceDiff = 0;
    }

    public int getSpecialPriceDiff() {
        return this.specialPriceDiff;
    }

    public void setSpecialPriceDiff(int price) {
        this.specialPriceDiff = price;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getXp() {
        return this.xp;
    }

    public boolean isOutOfStock() {
        return this.uses >= this.maxUses;
    }

    public void setToOutOfStock() {
        this.uses = this.maxUses;
    }

    public boolean needsRestock() {
        return this.uses > 0;
    }

    public boolean shouldRewardExp() {
        return this.rewardExp;
    }

    public boolean satisfiedBy(ItemStack playerOfferA, ItemStack playerOfferB) {
        // Check if the cost is for GOLD_COIN and either slot contains a CoinPouchItem
        boolean pouchInFirstSlot = playerOfferA.getItem() instanceof CoinPouchItem;
        boolean pouchInSecondSlot = playerOfferB.getItem() instanceof CoinPouchItem;

        // If playerOfferA contains a CoinPouchItem, check if it has enough coins
        if (pouchInFirstSlot) {
            int coinsInPouch = CoinPouchHelper.getCoins(playerOfferA);
            if (coinsInPouch < this.getModifiedCostCount(this.baseCostA)) {
                return false; // Not enough coins in the pouch
            }
        }
        // If playerOfferB contains a CoinPouchItem, check if it has enough coins
        else if (pouchInSecondSlot) {
            int coinsInPouch = CoinPouchHelper.getCoins(playerOfferB);
            if (coinsInPouch < this.getModifiedCostCount(this.baseCostA)) {
                return false; // Not enough coins in the pouch
            }
        }
        // If neither slot contains a CoinPouchItem, just check item count for playerOfferA
        else if (!this.baseCostA.test(playerOfferA) || playerOfferA.getCount() < this.getModifiedCostCount(this.baseCostA)) {
            return false; // Check if the offer has enough items
        }

        // Handle costB (if any)
        // No second item required
        // Check second item
        return this.costB.map(dwarfItemCost -> dwarfItemCost.test(playerOfferB) && playerOfferB.getCount() >= dwarfItemCost.count()).orElseGet(playerOfferB::isEmpty);
    }



    public boolean take(ItemStack playerOfferA, ItemStack playerOfferB) {

        // Check if the offer is satisfied
        if (!this.satisfiedBy(playerOfferA, playerOfferB)) {
            return false;
        }

        // Shrink item stack A (unless it is a coin pouch)
        int costA = this.getModifiedCostCount(this.baseCostA);
        if (playerOfferA.getItem() instanceof CoinPouchItem) {
            // Handle shrinking the coins inside the coin pouch if necessary
            int currentCoins = CoinPouchHelper.getCoins(playerOfferA);
            if (currentCoins >= costA) {
                CoinPouchHelper.setCoins(playerOfferA, currentCoins - costA);
            }
        } else {
            // Handle non-coin pouch items normally
            if (playerOfferA.getCount() >= costA) {
                playerOfferA.shrink(costA);
            }
        }

        // Shrink item stack B if applicable
        if (!this.getCostB().isEmpty()) {
            int costB = this.getCostB().getCount();
            if (playerOfferB.getItem() instanceof CoinPouchItem) {
                // Handle shrinking the coins inside the coin pouch if necessary
                int currentCoinsB = CoinPouchHelper.getCoins(playerOfferB);
                if (currentCoinsB >= costB) {
                    CoinPouchHelper.setCoins(playerOfferB, currentCoinsB - costB);
                }
            } else {
                // Handle non-coin pouch items normally
                if (playerOfferB.getCount() >= costB) {
                    playerOfferB.shrink(costB);
                }
            }
        }

        return true;
    }


    public DwarfMerchantOffer copy() {
        return new DwarfMerchantOffer(this);
    }

    private static void writeToStream(RegistryFriendlyByteBuf buffer, DwarfMerchantOffer offer) {
        DwarfItemCost.STREAM_CODEC.encode(buffer, offer.getItemCostA());
        ItemStack.STREAM_CODEC.encode(buffer, offer.getResult());
        DwarfItemCost.OPTIONAL_STREAM_CODEC.encode(buffer, offer.getItemCostB());
        buffer.writeBoolean(offer.isOutOfStock());
        buffer.writeInt(offer.getUses());
        buffer.writeInt(offer.getMaxUses());
        buffer.writeInt(offer.getXp());
        buffer.writeInt(offer.getSpecialPriceDiff());
        buffer.writeFloat(offer.getPriceMultiplier());
        buffer.writeInt(offer.getDemand());
    }

    public static DwarfMerchantOffer createFromStream(RegistryFriendlyByteBuf buffer) {
        DwarfItemCost itemcost = DwarfItemCost.STREAM_CODEC.decode(buffer);
        ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
        Optional<DwarfItemCost> optional = DwarfItemCost.OPTIONAL_STREAM_CODEC.decode(buffer);
        boolean flag = buffer.readBoolean();
        int i = buffer.readInt();
        int j = buffer.readInt();
        int k = buffer.readInt();
        int l = buffer.readInt();
        float f = buffer.readFloat();
        int i1 = buffer.readInt();
        DwarfMerchantOffer merchantoffer = new DwarfMerchantOffer(itemcost, optional, itemstack, i, j, k, f, i1);
        if (flag) {
            merchantoffer.setToOutOfStock();
        }

        merchantoffer.setSpecialPriceDiff(l);
        return merchantoffer;
    }


}
