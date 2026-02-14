package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.item.util.coin.CoinPouchHelper;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DwarfMerchantOffer {

    private static final String DEMAND = JolCraftDictionary.DEMAND;
    private static final String USES = JolCraftStrings.plural(JolCraftDictionary.USE);
    private static final String REWARD_XP = JolCraftStrings.underscored(JolCraftDictionary.REWARD, JolCraftDictionary.XP);
    private static final String SPECIAL_PRICE_DIFFERENCE = JolCraftStrings.underscored(JolCraftDictionary.SPECIAL, JolCraftDictionary.PRICE, JolCraftDictionary.DIFFERENCE);

    public static final Codec<DwarfMerchantOffer> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                            DwarfItemCost.CODEC.fieldOf(JolCraftStrings.underscored(JolCraftDictionary.COST, "a")).forGetter(o -> o.baseCostA),
                            DwarfItemCost.CODEC.lenientOptionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.COST, "b")).forGetter(o -> o.costB),
                            ItemStack.CODEC.fieldOf(JolCraftDictionary.RESULT).forGetter(o -> o.result),

                            Codec.INT.lenientOptionalFieldOf(USES, 0).forGetter(o -> o.uses),
                            Codec.INT.lenientOptionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.MAX, JolCraftStrings.plural(JolCraftDictionary.USE)), 4).forGetter(o -> o.maxUses),

                            Codec.BOOL.lenientOptionalFieldOf(REWARD_XP, Boolean.TRUE).forGetter(o -> o.rewardExp),
                            Codec.INT.lenientOptionalFieldOf(SPECIAL_PRICE_DIFFERENCE, 0).forGetter(o -> o.specialPriceDiff),

                            Codec.INT.lenientOptionalFieldOf(DEMAND, 0).forGetter(o -> o.demand),

                            Codec.FLOAT.lenientOptionalFieldOf(JolCraftStrings.underscored(JolCraftDictionary.PRICE, JolCraftDictionary.MULTIPLIER), 0.0F).forGetter(o -> o.priceMultiplier),
                            Codec.INT.lenientOptionalFieldOf(JolCraftDictionary.XP, 1).forGetter(o -> o.xp)
                    ).apply(inst, DwarfMerchantOffer::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DwarfMerchantOffer> STREAM_CODEC =
            StreamCodec.of(DwarfMerchantOffer::writeToStream, DwarfMerchantOffer::createFromStream);

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
            DwarfItemCost baseCostA,
            Optional<DwarfItemCost> costB,
            ItemStack result,
            int uses,
            int maxUses,
            int xp,
            float priceMultiplier,
            int demand
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
        int j = Math.max(0, Mth.floor((float) (i * this.demand) * this.priceMultiplier));
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
        int requiredA = this.getModifiedCostCount(this.baseCostA);

        if (!this.baseCostA.test(playerOfferA, requiredA)) {
            return false;
        }

        if (this.costB.isPresent()) {
            DwarfItemCost costB = this.costB.get();
            return costB.test(playerOfferB, costB.count());
        }

        return playerOfferB.isEmpty();
    }

    public boolean take(ItemStack playerOfferA, ItemStack playerOfferB) {
        int requiredA = this.getModifiedCostCount(this.baseCostA);

        // validate both sides before mutating anything
        if (!this.baseCostA.test(playerOfferA, requiredA)) {
            return false;
        }

        Optional<DwarfItemCost> costBOpt = this.costB;
        if (costBOpt.isPresent()) {
            DwarfItemCost costB = costBOpt.get();
            if (!costB.test(playerOfferB, costB.count())) {
                return false;
            }
        } else if (!playerOfferB.isEmpty()) {
            return false;
        }

        // snapshot A + B for rollback (counts and pouch coins if applicable)
        int aCountBefore = playerOfferA.getCount();
        int bCountBefore = playerOfferB.getCount();

        int aCoinsBefore = -1;
        int bCoinsBefore = -1;

        if (this.baseCostA.isCoinCost() && playerOfferA.getItem() instanceof CoinPouchItem) {
            aCoinsBefore = CoinPouchHelper.getCoins(playerOfferA);
        }

        if (costBOpt.isPresent()) {
            DwarfItemCost costB = costBOpt.get();
            if (costB.isCoinCost() && playerOfferB.getItem() instanceof CoinPouchItem) {
                bCoinsBefore = CoinPouchHelper.getCoins(playerOfferB);
            }
        }

        // pay A
        if (!this.baseCostA.take(playerOfferA, requiredA)) {
            return false;
        }

        // pay B (rollback A if it fails)
        if (costBOpt.isPresent()) {
            DwarfItemCost costB = costBOpt.get();
            if (!costB.take(playerOfferB, costB.count())) {
                // rollback A
                if (aCoinsBefore >= 0) {
                    CoinPouchHelper.setCoins(playerOfferA, aCoinsBefore);
                } else {
                    int delta = aCountBefore - playerOfferA.getCount();
                    if (delta > 0) {
                        playerOfferA.grow(delta);
                    }
                }

                // rollback B (defensive)
                if (bCoinsBefore >= 0) {
                    CoinPouchHelper.setCoins(playerOfferB, bCoinsBefore);
                } else {
                    int delta = bCountBefore - playerOfferB.getCount();
                    if (delta > 0) {
                        playerOfferB.grow(delta);
                    }
                }

                return false;
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
        DwarfItemCost itemCostA = DwarfItemCost.STREAM_CODEC.decode(buffer);
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        Optional<DwarfItemCost> costB = DwarfItemCost.OPTIONAL_STREAM_CODEC.decode(buffer);
        boolean outOfStock = buffer.readBoolean();
        int uses = buffer.readInt();
        int maxUses = buffer.readInt();
        int xp = buffer.readInt();
        int specialPrice = buffer.readInt();
        float priceMultiplier = buffer.readFloat();
        int demand = buffer.readInt();

        DwarfMerchantOffer offer = new DwarfMerchantOffer(itemCostA, costB, result, uses, maxUses, xp, priceMultiplier, demand);
        if (outOfStock) {
            offer.setToOutOfStock();
        }
        offer.setSpecialPriceDiff(specialPrice);
        return offer;
    }
}