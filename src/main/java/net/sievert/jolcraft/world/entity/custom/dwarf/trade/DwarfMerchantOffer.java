package net.sievert.jolcraft.world.entity.custom.dwarf.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;

import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DwarfMerchantOffer {

    private static final String DEMAND = JolCraftDictionary.DEMAND;
    private static final String USES =
            JolCraftStrings.plural(JolCraftDictionary.USE);
    private static final String REWARD_XP =
            JolCraftStrings.underscored(
                    JolCraftDictionary.REWARD,
                    JolCraftDictionary.XP
            );
    private static final String SPECIAL_PRICE_DIFFERENCE =
            JolCraftStrings.underscored(
                    JolCraftDictionary.SPECIAL,
                    JolCraftDictionary.PRICE,
                    JolCraftDictionary.DIFFERENCE
            );
    private static final String MAX_USES =
            JolCraftStrings.underscored(
                    JolCraftDictionary.MAX,
                    JolCraftStrings.plural(
                            JolCraftDictionary.USE
                    )
            );
    private static final String PRICE_MULTIPLIER =
            JolCraftStrings.underscored(
                    JolCraftDictionary.PRICE,
                    JolCraftDictionary.MULTIPLIER
            );
    private static final String RECIPE_ID = "recipe_id";

    private static final Codec<DwarfTradeRecipe.TradeGroup>
            TRADE_GROUP_CODEC =
            Codec.STRING.comapFlatMap(
                    value -> {
                        if (value == null) {
                            return DataResult.error(
                                    () -> "group is null"
                            );
                        }

                        return DwarfTradeRecipe.TradeGroup
                                .fromSerialized(value);
                    },
                    group -> group.name()
                            .toLowerCase(Locale.ROOT)
            );

    public static final Codec<DwarfMerchantOffer> CODEC =
            RecordCodecBuilder.create(
                    instance -> instance.group(
                            DwarfItemCost.CODEC.fieldOf(
                                            JolCraftStrings.underscored(
                                                    JolCraftDictionary.COST,
                                                    "a"
                                            )
                                    )
                                    .forGetter(
                                            offer -> offer.baseCostA
                                    ),

                            DwarfItemCost.CODEC.lenientOptionalFieldOf(
                                            JolCraftStrings.underscored(
                                                    JolCraftDictionary.COST,
                                                    "b"
                                            )
                                    )
                                    .forGetter(
                                            offer -> offer.costB
                                    ),

                            ItemStack.CODEC.fieldOf(
                                            JolCraftDictionary.RESULT
                                    )
                                    .forGetter(
                                            offer -> offer.result
                                    ),

                            Codec.INT.fieldOf(USES)
                                    .forGetter(
                                            offer -> offer.uses
                                    ),

                            Codec.INT.fieldOf(MAX_USES)
                                    .forGetter(
                                            offer -> offer.maxUses
                                    ),

                            Codec.BOOL.fieldOf(REWARD_XP)
                                    .forGetter(
                                            offer -> offer.rewardExp
                                    ),

                            Codec.INT.fieldOf(
                                            SPECIAL_PRICE_DIFFERENCE
                                    )
                                    .forGetter(
                                            offer ->
                                                    offer.specialPriceDiff
                                    ),

                            Codec.INT.fieldOf(DEMAND)
                                    .forGetter(
                                            offer -> offer.demand
                                    ),

                            Codec.FLOAT.fieldOf(
                                            PRICE_MULTIPLIER
                                    )
                                    .forGetter(
                                            offer ->
                                                    offer.priceMultiplier
                                    ),

                            Codec.INT.fieldOf(
                                            JolCraftDictionary.XP
                                    )
                                    .forGetter(
                                            offer -> offer.xp
                                    ),

                            ResourceLocation.CODEC.fieldOf(RECIPE_ID)
                                    .forGetter(
                                            offer ->
                                                    offer.sourceRecipeId
                                    ),

                            TRADE_GROUP_CODEC.fieldOf(
                                            JolCraftDictionary.GROUP
                                    )
                                    .forGetter(
                                            offer -> offer.tradeGroup
                                    )
                    ).apply(
                            instance,
                            DwarfMerchantOffer::new
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            DwarfMerchantOffer
            > STREAM_CODEC =
            StreamCodec.of(
                    DwarfMerchantOffer::writeToStream,
                    DwarfMerchantOffer::createFromStream
            );

    private final DwarfItemCost baseCostA;
    private final Optional<DwarfItemCost> costB;
    private final ItemStack result;

    private int uses;
    private final int maxUses;
    private final boolean rewardExp;
    private int specialPriceDiff;
    private int demand;
    private final float priceMultiplier;
    private final int xp;
    private final ResourceLocation sourceRecipeId;
    private final DwarfTradeRecipe.TradeGroup tradeGroup;

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
            int xp,
            ResourceLocation sourceRecipeId,
            DwarfTradeRecipe.TradeGroup tradeGroup
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
        this.sourceRecipeId = sourceRecipeId;
        this.tradeGroup = tradeGroup;
    }

    public DwarfMerchantOffer(
            DwarfItemCost baseCostA,
            Optional<DwarfItemCost> costB,
            ItemStack result,
            int uses,
            int maxUses,
            int xp,
            float priceMultiplier,
            int demand,
            ResourceLocation sourceRecipeId,
            DwarfTradeRecipe.TradeGroup tradeGroup
    ) {
        this(
                baseCostA,
                costB,
                result,
                uses,
                maxUses,
                true,
                0,
                demand,
                priceMultiplier,
                xp,
                sourceRecipeId,
                tradeGroup
        );
    }

    private DwarfMerchantOffer(
            DwarfMerchantOffer other
    ) {
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
                other.xp,
                other.sourceRecipeId,
                other.tradeGroup
        );
    }

    public ItemStack getBaseCostA() {
        return this.baseCostA.itemStack()
                .copyWithCount(
                        this.getBaseCostCount(
                                this.baseCostA
                        )
                );
    }

    public ItemStack getCostA() {
        return this.baseCostA.itemStack()
                .copyWithCount(
                        this.getModifiedCostCount(
                                this.baseCostA
                        )
                );
    }

    private int getBaseCostCount(
            DwarfItemCost itemCost
    ) {
        return itemCost.requiredCount();
    }

    private int getModifiedCostCount(
            DwarfItemCost itemCost
    ) {
        int count = this.getBaseCostCount(
                itemCost
        );

        int demandAdjustment = Math.max(
                0,
                Mth.floor(
                        (float) (
                                count
                                        * this.demand
                        )
                                * this.priceMultiplier
                )
        );

        return Mth.clamp(
                count
                        + demandAdjustment
                        + this.specialPriceDiff,
                1,
                itemCost.maxAllowedCount()
        );
    }

    public ItemStack getCostB() {
        return this.costB.map(itemCost ->
                        itemCost.itemStack()
                                .copyWithCount(
                                        this.getBaseCostCount(
                                                itemCost
                                        )
                                )
                )
                .orElse(
                        ItemStack.EMPTY
                );
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

    public void updateDemand() {
        this.demand =
                this.demand
                        + this.uses
                        - (
                        this.maxUses
                                - this.uses
                );
    }

    public void addToSpecialPriceDiff(
            int add
    ) {
        this.specialPriceDiff += add;
    }

    public void resetSpecialPriceDiff() {
        this.specialPriceDiff = 0;
    }

    public int getSpecialPriceDiff() {
        return this.specialPriceDiff;
    }

    public void setSpecialPriceDiff(
            int price
    ) {
        this.specialPriceDiff = price;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getXp() {
        return this.xp;
    }

    public ResourceLocation getSourceRecipeId() {
        return this.sourceRecipeId;
    }

    public DwarfTradeRecipe.TradeGroup getTradeGroup() {
        return this.tradeGroup;
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

    public ItemStack assemble() {
        return this.result.copy();
    }

    public boolean satisfiedBy(
            ItemStack playerOfferA,
            ItemStack playerOfferB
    ) {
        int requiredA =
                this.getModifiedCostCount(
                        this.baseCostA
                );

        if (!this.baseCostA.test(
                playerOfferA,
                requiredA
        )) {
            return false;
        }

        if (this.costB.isPresent()) {
            DwarfItemCost secondCost =
                    this.costB.get();

            return secondCost.test(
                    playerOfferB,
                    this.getBaseCostCount(
                            secondCost
                    )
            );
        }

        return playerOfferB.isEmpty();
    }

    public boolean take(
            ItemStack playerOfferA,
            ItemStack playerOfferB
    ) {
        int requiredA =
                this.getModifiedCostCount(
                        this.baseCostA
                );

        if (!this.baseCostA.test(
                playerOfferA,
                requiredA
        )) {
            return false;
        }

        Optional<DwarfItemCost> secondCostOption =
                this.costB;

        if (secondCostOption.isPresent()) {
            DwarfItemCost secondCost =
                    secondCostOption.get();

            if (!secondCost.test(
                    playerOfferB,
                    this.getBaseCostCount(
                            secondCost
                    )
            )) {
                return false;
            }
        } else if (!playerOfferB.isEmpty()) {
            return false;
        }

        int offerACountBefore =
                playerOfferA.getCount();
        int offerBCountBefore =
                playerOfferB.getCount();

        int offerACoinsBefore = -1;
        int offerBCoinsBefore = -1;

        if (
                this.baseCostA.isCoinCost()
                        && playerOfferA.getItem()
                        instanceof CoinPouchItem
        ) {
            offerACoinsBefore =
                    playerOfferA.getOrDefault(
                            JolCraftDataComponents
                                    .COIN_POUCH_AMOUNT
                                    .get(),
                            0
                    );
        }

        if (secondCostOption.isPresent()) {
            DwarfItemCost secondCost =
                    secondCostOption.get();

            if (
                    secondCost.isCoinCost()
                            && playerOfferB.getItem()
                            instanceof CoinPouchItem
            ) {
                offerBCoinsBefore =
                        playerOfferB.getOrDefault(
                                JolCraftDataComponents
                                        .COIN_POUCH_AMOUNT
                                        .get(),
                                0
                        );
            }
        }

        if (!this.baseCostA.take(
                playerOfferA,
                requiredA
        )) {
            return false;
        }

        if (secondCostOption.isPresent()) {
            DwarfItemCost secondCost =
                    secondCostOption.get();

            if (!secondCost.take(
                    playerOfferB,
                    this.getBaseCostCount(
                            secondCost
                    )
            )) {
                if (offerACoinsBefore >= 0) {
                    playerOfferA.set(
                            JolCraftDataComponents
                                    .COIN_POUCH_AMOUNT
                                    .get(),
                            offerACoinsBefore
                    );
                } else {
                    int delta =
                            offerACountBefore
                                    - playerOfferA.getCount();

                    if (delta > 0) {
                        playerOfferA.grow(delta);
                    }
                }

                if (offerBCoinsBefore >= 0) {
                    playerOfferB.set(
                            JolCraftDataComponents
                                    .COIN_POUCH_AMOUNT
                                    .get(),
                            offerBCoinsBefore
                    );
                } else {
                    int delta =
                            offerBCountBefore
                                    - playerOfferB.getCount();

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

    private static void writeToStream(
            RegistryFriendlyByteBuf buffer,
            DwarfMerchantOffer offer
    ) {
        DwarfItemCost.STREAM_CODEC.encode(
                buffer,
                offer.getItemCostA()
        );
        ItemStack.STREAM_CODEC.encode(
                buffer,
                offer.getResult()
        );
        DwarfItemCost.OPTIONAL_STREAM_CODEC.encode(
                buffer,
                offer.getItemCostB()
        );

        buffer.writeInt(
                offer.getUses()
        );
        buffer.writeInt(
                offer.getMaxUses()
        );
        buffer.writeBoolean(
                offer.shouldRewardExp()
        );
        buffer.writeInt(
                offer.getSpecialPriceDiff()
        );
        buffer.writeInt(
                offer.getDemand()
        );
        buffer.writeFloat(
                offer.getPriceMultiplier()
        );
        buffer.writeInt(
                offer.getXp()
        );
        buffer.writeResourceLocation(
                offer.getSourceRecipeId()
        );
        buffer.writeUtf(
                offer.getTradeGroup().name()
        );
    }

    public static DwarfMerchantOffer createFromStream(
            RegistryFriendlyByteBuf buffer
    ) {
        DwarfItemCost itemCostA =
                DwarfItemCost.STREAM_CODEC.decode(
                        buffer
                );
        ItemStack result =
                ItemStack.STREAM_CODEC.decode(
                        buffer
                );
        Optional<DwarfItemCost> costB =
                DwarfItemCost.OPTIONAL_STREAM_CODEC.decode(
                        buffer
                );

        int uses = buffer.readInt();
        int maxUses = buffer.readInt();
        boolean rewardExp =
                buffer.readBoolean();
        int specialPrice =
                buffer.readInt();
        int demand = buffer.readInt();
        float priceMultiplier =
                buffer.readFloat();
        int xp = buffer.readInt();

        ResourceLocation sourceRecipeId =
                buffer.readResourceLocation();

        DwarfTradeRecipe.TradeGroup tradeGroup =
                decodeTradeGroup(
                        buffer.readUtf()
                );

        return new DwarfMerchantOffer(
                itemCostA,
                costB,
                result,
                uses,
                maxUses,
                rewardExp,
                specialPrice,
                demand,
                priceMultiplier,
                xp,
                sourceRecipeId,
                tradeGroup
        );
    }

    private static DwarfTradeRecipe.TradeGroup decodeTradeGroup(
            String value
    ) {
        DataResult<DwarfTradeRecipe.TradeGroup> result =
                DwarfTradeRecipe.TradeGroup
                        .fromSerialized(value);

        return result.result()
                .orElse(
                        DwarfTradeRecipe.TradeGroup.MAIN
                );
    }
}