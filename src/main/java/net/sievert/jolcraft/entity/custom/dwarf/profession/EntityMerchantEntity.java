package net.sievert.jolcraft.entity.custom.dwarf.profession;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteractionHelper;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityMerchantEntity extends AbstractEntityEntity {

    public EntityMerchantEntity(EntityType<? extends AbstractEntityEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.COIN_POUCH.get()));
        this.instanceTrades = createRandomizedMerchantTrades();
        this.setProfession(DwarfProfession.MERCHANT);
    }

    @Override
    public boolean canTrade() {
        return true;
    }

    @Override
    public boolean hasRandomTrades(){ return true; }

    @Override
    public boolean canReroll() { return false; }


    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_MERCHANT.get());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FirePanicGoal(this, 1.3));
        this.targetSelector.addGoal(1, new DwarfNonPlayerAlertGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new DwarfAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new DwarfRevengeGoal(this));
        this.goalSelector.addGoal(3, new DwarfTradeWithPlayerGoal(this));
        this.goalSelector.addGoal(4, new DwarfLookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(5, new DwarfBreedGoal(this, 1.0, AbstractEntityEntity.class));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.25, stack -> stack.is(JolCraftItems.GOLD_COIN), false));
        this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new MoveToBlockGoal(this, 0.8, 8) {
            @Override
            protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                return level.getBlockState(pos).is(Blocks.COBBLED_DEEPSLATE);
            }
        });
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = super.mobInteract(player, hand);
        if (result != InteractionResult.FAIL) return result;
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult bounty = DwarfInteractionHelper.bounty(this, player, hand, itemstack, BountyType.MERCHANT);
        if (bounty != InteractionResult.FAIL) return bounty;
        InteractionResult bountyCrate = DwarfInteractionHelper.bountyCrate(this, player, hand, itemstack, BountyType.MERCHANT);
        if (bountyCrate != InteractionResult.FAIL) return bountyCrate;
        JolCraftSoundHelper.playDwarfNo(this);
        return InteractionResult.FAIL;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.shouldIncreaseLevel() && this.updateMerchantTimer <= 0) {
            if (this.shouldIncreaseLevel()) {
                this.increaseMerchantCareer();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                JolCraftSoundHelper.playDwarfYes(this);
                this.updateMerchantTimer = 40;
            }
        } else if (this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
        }
    }

    public static final Int2ObjectMap<DwarfTrades.ItemListing[]> BOUNTY_TRADES = AbstractEntityEntity.toIntMap(ImmutableMap.of(
            1, new DwarfTrades.ItemListing[] {
                    new DwarfTrades.ItemForItemWithData(
                            JolCraftItems.PARCHMENT.get(),
                            1,
                            JolCraftItems.BOUNTY.get(),
                            1,
                            1, 0, 0,
                            (stack) -> {
                                stack.set(JolCraftDataComponents.BOUNTY_TIER.get(), 1);
                                stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "merchant");
                            }
                    ),
            },
            2, new DwarfTrades.ItemListing[] {
                    new DwarfTrades.ItemForItemWithData(
                            JolCraftItems.PARCHMENT.get(),
                            1,
                            JolCraftItems.BOUNTY.get(),
                            1,
                            1, 0, 0,
                            (stack) -> {
                                stack.set(JolCraftDataComponents.BOUNTY_TIER.get(), 2);
                                stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "merchant");
                            }                    ),
            },
            3, new DwarfTrades.ItemListing[] {
                    new DwarfTrades.ItemForItemWithData(
                            JolCraftItems.PARCHMENT.get(),
                            1,
                            JolCraftItems.BOUNTY.get(),
                            1,
                            1, 0, 0,
                            (stack) -> {
                                stack.set(JolCraftDataComponents.BOUNTY_TIER.get(), 3);
                                stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "merchant");
                            }                    ),
            },
            4, new DwarfTrades.ItemListing[] {
                    new DwarfTrades.ItemForItemWithData(
                            JolCraftItems.PARCHMENT.get(),
                            1,
                            JolCraftItems.BOUNTY.get(),
                            1,
                            1, 0, 0,
                            (stack) -> {
                                stack.set(JolCraftDataComponents.BOUNTY_TIER.get(), 4);
                                stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "merchant");
                            }                    ),
            },
            5, new DwarfTrades.ItemListing[] {
                    new DwarfTrades.ItemForItemWithData(
                            JolCraftItems.PARCHMENT.get(),
                            1,
                            JolCraftItems.BOUNTY.get(),
                            1,
                            1, 0, 0,
                            (stack) -> {
                                stack.set(JolCraftDataComponents.BOUNTY_TIER.get(), 5);
                                stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "merchant");
                            }                    ),
            }
    ));

    public static final Int2ObjectMap<DwarfTrades.ItemListing[]> GENERAL_TRADES = AbstractEntityEntity.toIntMap(ImmutableMap.of(
            1, new DwarfTrades.ItemListing[]{
                    new DwarfTrades.ItemsForGold(Items.TORCH, 1, 2, 12, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.COAL, 1, 2, 5, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.FLINT, 1, 2, 5, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.COPPER_INGOT, 1, 2, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.COBBLED_DEEPSLATE, 1, 2, 12, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.IRON_NUGGET, 1, 2, 12, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.BRICK, 1, 2, 4, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.STRING, 1, 2, 3, 3, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.DEEPSLATE_MUG.get(), 1, 2, 3, 3, 1)
            },
            2, new DwarfTrades.ItemListing[]{
                    new DwarfTrades.ItemsForGold(Items.IRON_INGOT, 2, 3, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.LAPIS_LAZULI, 1, 2, 6, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.REDSTONE, 1, 2, 6, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.FEATHER, 1, 2, 3, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.LEATHER, 1, 2, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.WHITE_WOOL, 1, 2, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.PARCHMENT.get(), 1, 2, 3, 3, 1)
            },
            3, new DwarfTrades.ItemListing[]{
                    new DwarfTrades.ItemsForGold(Items.GOLD_INGOT, 5, 7, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.EMERALD, 2, 4, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.AMETHYST_SHARD, 1, 2, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.BLAZE_POWDER, 1, 2, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.SPIDER_EYE, 1, 2, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.GUNPOWDER, 1, 2, 2, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.BONE, 1, 2, 3, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.INK_SAC, 1, 2, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.QUILL_EMPTY.get(), 1, 2, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.CONTRACT_BLANK.get(), 1, 2, 1, 3, 1)
            },
            4, new DwarfTrades.ItemListing[]{
                    new DwarfTrades.ItemsForGold(Items.GOLDEN_APPLE, 4, 6, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.BOOK, 1, 2, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.CAULDRON, 10, 14, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.ITEM_FRAME, 1, 2, 1, 3, 1),
                    new DwarfTrades.ItemsForGold(Items.ENDER_PEARL, 2, 4, 1, 3, 1)
            },
            5, new DwarfTrades.ItemListing[]{
                    new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.SUNGLEAM_CUT, 1, 5, 15, JolCraftItems.RESTOCK_CRATE.get(), 1, 3, 0, 0),
                    new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.SUNGLEAM_CUT, 1, 5, 15, JolCraftItems.REROLL_CRATE.get(), 1, 3, 0, 0),
            }
    ));

    public static final Int2ObjectMap<DwarfTrades.ItemListing[]> GEM_TRADES = AbstractEntityEntity.toIntMap(ImmutableMap.of(
            5, new DwarfTrades.ItemListing[]{
                    new DwarfTrades.ItemsForGold(JolCraftItems.AEGISCORE.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.ASHFANG.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.DEEPMARROW.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.EARTHBLOOD.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.EMBERGLASS.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.FROSTVEIN.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.GRIMSTONE.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.IRONHEART.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.LUMIERE.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.MOONSHARD.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.RUSTAGATE.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.SKYBURROW.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.SUNGLEAM.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.VERDANITE.get(), 64, 64, 1, 1, 1),
                    new DwarfTrades.ItemsForGold(JolCraftItems.WOECRYSTAL.get(), 64, 64, 1, 1, 1)
            }
    ));

    private static DwarfTrades.ItemListing[] concatTradeArrays(DwarfTrades.ItemListing[]... arrays) {
        return Arrays.stream(arrays)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toArray(DwarfTrades.ItemListing[]::new);
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedMerchantTrades() {
        return AbstractEntityEntity.toIntMap(ImmutableMap.of(
                1, concatTradeArrays(BOUNTY_TRADES.get(1), GENERAL_TRADES.get(1)),
                2, concatTradeArrays(BOUNTY_TRADES.get(2), GENERAL_TRADES.get(2)),
                3, concatTradeArrays(BOUNTY_TRADES.get(3), GENERAL_TRADES.get(3)),
                4, concatTradeArrays(BOUNTY_TRADES.get(4), GENERAL_TRADES.get(4)),
                5, concatTradeArrays(BOUNTY_TRADES.get(5), GENERAL_TRADES.get(5), GEM_TRADES.get(5))
        ));
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createGeneralTrades(RandomSource random) {
        return GENERAL_TRADES;
    }

    @Override
    protected void updateTrades() {
        int level = this.getVillagerData().getLevel();

        for (int lvl = 1; lvl <= level; lvl++) {
            var bountyListings = BOUNTY_TRADES.get(lvl);
            if (bountyListings != null && bountyListings.length > 0) {
                boolean hasThisBounty = this.getOffers().stream().anyMatch(offer -> {
                    DwarfMerchantOffer test = bountyListings[0].getOffer(this, this.random);
                    return test != null &&
                            ItemStack.isSameItemSameComponents(offer.getResult(), test.getResult()) &&
                            ItemStack.isSameItemSameComponents(offer.getBaseCostA(), test.getBaseCostA());
                });
                if (!hasThisBounty) {
                    this.addOffersFromItemListings(this.getOffers(), bountyListings, 1);
                }
            }
        }

        var generalPool = GENERAL_TRADES.get(level);
        if (generalPool != null && generalPool.length > 0) {
            Set<Item> currentGeneral = new HashSet<>();
            for (DwarfMerchantOffer offer : this.getOffers()) {
                for (DwarfTrades.ItemListing listing : generalPool) {
                    DwarfMerchantOffer test = listing.getOffer(this, this.random);
                    if (test != null &&
                            ItemStack.isSameItemSameComponents(offer.getResult(), test.getResult()) &&
                            ItemStack.isSameItemSameComponents(offer.getBaseCostA(), test.getBaseCostA())) {
                        currentGeneral.add(offer.getResult().getItem());
                    }
                }
            }
            int toAdd = 2 - currentGeneral.size();
            if (toAdd > 0) {
                var shuffled = new ArrayList<>(List.of(generalPool));
                Collections.shuffle(shuffled, new Random(this.random.nextLong()));
                for (int i = 0, added = 0; i < shuffled.size() && added < toAdd; i++) {
                    var offer = shuffled.get(i).getOffer(this, this.random);
                    if (offer != null && !currentGeneral.contains(offer.getResult().getItem())) {
                        this.getOffers().add(offer);
                        currentGeneral.add(offer.getResult().getItem());
                        added++;
                    }
                }
            }
        }

        if (level == 5) {
            var gemTrades = GEM_TRADES.get(5);
            if (gemTrades != null && gemTrades.length > 0) {
                boolean hasGem = this.getOffers().stream().anyMatch(offer -> {
                    for (DwarfTrades.ItemListing listing : gemTrades) {
                        DwarfMerchantOffer test = listing.getOffer(this, this.random);
                        return test != null &&
                                ItemStack.isSameItemSameComponents(offer.getResult(), test.getResult()) &&
                                ItemStack.isSameItemSameComponents(offer.getBaseCostA(), test.getBaseCostA());
                    }
                    return false;
                });
                if (!hasGem) {
                    var shuffled = new ArrayList<>(List.of(gemTrades));
                    Collections.shuffle(shuffled, new Random(this.random.nextLong()));
                    var offer = shuffled.getFirst().getOffer(this, this.random);
                    if (offer != null) this.getOffers().add(offer);
                }
            }
        }
    }

    private boolean isCrateTrade(DwarfMerchantOffer offer) {
        return offer.getResult().is(JolCraftItems.RESTOCK_CRATE.get()) ||
                offer.getResult().is(JolCraftItems.REROLL_CRATE.get());
    }

    @Override
    public void crateRestock() {
        if (this.level().isClientSide) return;

        List<DwarfMerchantOffer> crateTrades = this.getOffers().stream()
                .filter(this::isCrateTrade)
                .map(DwarfMerchantOffer::copy)
                .toList();

        this.restock();
        this.getOffers().removeIf(this::isCrateTrade);
        this.getOffers().addAll(crateTrades);
    }

    @Override
    public void restock() {
        if (this.level().isClientSide) return;

        this.getOffers().clear();

        int level = this.getVillagerData().getLevel();

        for (int i = 1; i <= level; i++) {
            var bountyListings = BOUNTY_TRADES.get(i);
            if (bountyListings != null && bountyListings.length > 0) {
                this.addOffersFromItemListings(this.getOffers(), bountyListings, 1);
            }
        }

        var freshGeneralTrades = createGeneralTrades(this.random);
        for (int i = 1; i <= level; i++) {
            var generalPool = freshGeneralTrades.get(i);
            if (generalPool != null && generalPool.length > 0) {
                var shuffled = new ArrayList<>(List.of(generalPool));
                Collections.shuffle(shuffled, new Random(this.random.nextLong()));
                int numToAdd = Math.min(2, shuffled.size());
                for (int j = 0; j < numToAdd; j++) {
                    var offer = shuffled.get(j).getOffer(this, this.random);
                    if (offer != null) this.getOffers().add(offer);
                }
            }
        }

        if (level == 5) {
            var gemTrades = GEM_TRADES.get(5);
            if (gemTrades != null && gemTrades.length > 0) {
                var shuffled = new ArrayList<>(List.of(gemTrades));
                Collections.shuffle(shuffled, new Random(this.random.nextLong()));
                var offer = shuffled.getFirst().getOffer(this, this.random);
                if (offer != null) this.getOffers().add(offer);
            }
        }

        this.lastRestockGameTime = this.level().getGameTime();
        this.level().playSound(null, this.blockPosition(), Objects.requireNonNull(getRestockSound()), SoundSource.NEUTRAL, 1.0F, 1.05F);
    }

    @Override
    public void notifyTrade(DwarfMerchantOffer offer) {
        super.notifyTrade(offer);

        if (this.getTradingPlayer() instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.TRADED_WITH_VILLAGER);
            JolCraftCriteriaTriggers.TRADE_WITH_DWARF.trigger(serverPlayer, this);
        }
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> getAllJeiTrades() {
        Int2ObjectMap<DwarfTrades.ItemListing[]> out = new Int2ObjectOpenHashMap<>();
        for (int lvl = 1; lvl <= 5; lvl++) {
            List<DwarfTrades.ItemListing> all = new ArrayList<>();
            if (BOUNTY_TRADES.get(lvl) != null) all.addAll(List.of(BOUNTY_TRADES.get(lvl)));
            if (GENERAL_TRADES.get(lvl) != null) all.addAll(List.of(GENERAL_TRADES.get(lvl)));
            if (lvl == 5 && GEM_TRADES.get(5) != null) all.addAll(List.of(GEM_TRADES.get(5)));
            out.put(lvl, all.toArray(DwarfTrades.ItemListing[]::new));
        }
        return out;
    }
}
