package net.sievert.jolcraft.entity.custom.dwarf;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.sound.JolCraftSoundHelper;
import net.sievert.jolcraft.util.dwarf.bounty.BountyData;
import net.sievert.jolcraft.entity.ai.goal.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.util.dwarf.bounty.BountyGenerator;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.util.dwarf.trade.DwarfTrades;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfMerchantEntity extends AbstractDwarfEntity {

    public DwarfMerchantEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.COIN_POUCH.get()));
        this.instanceTrades = createRandomizedMerchantTrades();
    }

    //Attributes
    public static AttributeSupplier.Builder createAttributes() {
        return DwarfMerchantEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.TEMPT_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);

    }

    //Behavior
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
    public ResourceLocation getProfessionId() {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_merchant");
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
        this.goalSelector.addGoal(5, new DwarfBreedGoal(this, 1.0, AbstractDwarfEntity.class));
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

    public ItemStack getBountyCrateItem() {
        return new ItemStack(JolCraftItems.BOUNTY_CRATE.get());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 🧠 Language check - ensures only players who know the Dwarvish language can interact
        InteractionResult langCheck = this.languageCheck(player);
        if (langCheck != InteractionResult.SUCCESS) {
            return langCheck;
        }


        // 🎯 Bounty crate turn-in (must be complete)
        if (itemstack.is(JolCraftItems.BOUNTY_CRATE.get())) {
            Boolean complete = itemstack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get());
            String type = itemstack.get(JolCraftDataComponents.BOUNTY_TYPE.get());
            boolean isMerchant = "merchant".equals(type);

            if(!isMerchant){
                JolCraftSoundHelper.playDwarfNo(this);
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.bounty_crate.wrong_type").withStyle(ChatFormatting.GRAY), true);
                return InteractionResult.SUCCESS;
            }

            if (complete == null || !complete) {
                JolCraftSoundHelper.playDwarfNo(this);
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.bounty_crate.not_complete").withStyle(ChatFormatting.GRAY), true);
                return InteractionResult.SUCCESS;
            }



            // Both sides: hand swap and animation sync
            ItemStack prevMainHand = this.getMainHandItem().copy();
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
            this.usePlayerItem(player, hand, itemstack);
            JolCraftSoundHelper.playDwarfYes(this);

            // Begin multi-tick action
            beginAction(player, 40, ACTION_BOUNTY_CRATE_TURNIN, itemstack, prevMainHand, () -> {
                this.setInspecting(false);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

                if (!this.level().isClientSide && this.currentActionPlayer != null) {
                    // Reward logic (server only)
                    ItemStack saved = this.usedItem;
                    if (saved.is(JolCraftItems.BOUNTY_CRATE.get()) &&
                            Boolean.TRUE.equals(saved.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()))) {

                        BountyData data = saved.get(JolCraftDataComponents.BOUNTY_DATA.get());
                        if (data != null) {
                            Vec3 start = this.position().add(0.0, this.getEyeHeight(), 0.0);
                            Vec3 target = this.currentActionPlayer.position().add(0.0, this.currentActionPlayer.getBbHeight() * 0.5, 0.0);
                            Vec3 velocity = target.subtract(start).normalize().scale(0.4);
                            List<ItemStack> rewards = BountyGenerator.getReward(data, this.getRandom());
                            for (ItemStack reward : rewards) {
                                if (!reward.isEmpty()) {
                                    ItemEntity thrownReward = new ItemEntity(this.level(), start.x, start.y, start.z, reward);
                                    thrownReward.setDeltaMovement(velocity);
                                    thrownReward.setPickUpDelay(10);
                                    this.level().addFreshEntity(thrownReward);
                                }
                            }

                            int xp = switch (data.tier()) {
                                case 1 -> 10;
                                case 2 -> 35;
                                case 3 -> 50;
                                case 4 -> 65;
                                default -> 0;
                            };
                            this.dwarfXp += xp;
                            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 1.0, this.getZ(), 3 + this.getRandom().nextInt(3)));
                            this.level().playSound(null, this.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8F, 1.2F);
                            this.level().playSound(null, this.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.7F);
                        }
                        this.restockBountiesOnly();
                        this.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
                        this.previousMainHandItem = ItemStack.EMPTY;
                    }
                }
            });

            return InteractionResult.SUCCESS_SERVER;
        }

        // 📦 Bounty note submission (for getting a new crate)
        if (itemstack.is(JolCraftItems.BOUNTY.get())) {
            String type = itemstack.get(JolCraftDataComponents.BOUNTY_TYPE.get());
            boolean isMerchant = "merchant".equals(type);
            if(!isMerchant){
                JolCraftSoundHelper.playDwarfNo(this);
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.bounty.wrong_type").withStyle(ChatFormatting.GRAY), true);
                return InteractionResult.SUCCESS;
            }

            // Both sides: hand swap and animation sync
            ItemStack prevMainHand = this.getMainHandItem().copy();
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.BOUNTY.get()));
            this.usePlayerItem(player, hand, itemstack);
            JolCraftSoundHelper.playDwarfYes(this);

            beginAction(player, 40, ACTION_BOUNTY_NOTE_SUBMIT, itemstack, prevMainHand, () -> {
                this.setInspecting(false);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

                if (!this.level().isClientSide && this.currentActionPlayer != null) {
                    ItemStack saved = this.usedItem;
                    if (saved.is(JolCraftItems.BOUNTY.get())) {
                        Vec3 start = this.position().add(0.0, this.getEyeHeight(), 0.0);
                        Vec3 target = this.currentActionPlayer.position().add(0.0, this.currentActionPlayer.getBbHeight() * 0.5, 0.0);
                        Vec3 velocity = target.subtract(start).normalize().scale(0.4);

                        ItemStack crate = this.getBountyCrateItem();
                        int merchantTier = this.getVillagerData().getLevel();

                        crate.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "merchant");
                        crate.set(JolCraftDataComponents.BOUNTY_TIER.get(), merchantTier);
                        crate.set(JolCraftDataComponents.BOUNTY_DATA.get(), BountyGenerator.generate(crate, random));


                        ItemEntity thrown = new ItemEntity(this.level(), start.x, start.y, start.z, crate);
                        thrown.setDeltaMovement(velocity);
                        thrown.setPickUpDelay(10);
                        this.level().addFreshEntity(thrown);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.8F);
                    }
                    this.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
                    this.previousMainHandItem = ItemStack.EMPTY;
                }
            });

            return InteractionResult.SUCCESS_SERVER;
        }

        // Call parent for all other interactions (contracts, trades, etc)
        return super.mobInteract(player, hand);
    }



    @Override
    public void aiStep() {
        super.aiStep();

        // Check if the Merchant can level up and if the timer is expired
        if (this.shouldIncreaseLevel() && this.updateMerchantTimer <= 0) {
            // Check if the Merchant has enough XP to level up
            if (this.shouldIncreaseLevel()) {
                // Increase the Merchant's level if XP is enough
                this.increaseMerchantCareer();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                JolCraftSoundHelper.playDwarfYes(this);
                // Set the delay to 40 ticks (similar to vanilla)
                this.updateMerchantTimer = 40; // Reset the timer after leveling up
            }
        } else if (this.updateMerchantTimer > 0) {
            // Countdown the timer only if the Merchant is not performing an action or trading
            --this.updateMerchantTimer;
        }

        // Per-tick bounty action logic
        if (ACTION_BOUNTY_CRATE_TURNIN.equals(currentActionId)) {
            this.setInspecting(true);
            if (currentActionTicks == 25) {
                JolCraftSoundHelper.playVillagerFisherman(this);
            }
            if (currentActionTicks == 15) {
                JolCraftSoundHelper.playDwarfYes(this);
            }
        }
        if (ACTION_BOUNTY_NOTE_SUBMIT.equals(currentActionId)) {
            this.setInspecting(true);
            if (currentActionTicks == 25) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.NEUTRAL, 1.0F, 1.2F);
            }
            if (currentActionTicks == 15) {
                JolCraftSoundHelper.playVillagerFisherman(this);
            }
        }

        // Universal animation reset
        if (currentActionId == null) {
            this.setInspecting(false);
        }
    }

    //Particles
    @Override
    protected void tickAction() {
        super.tickAction();

        if (ACTION_BOUNTY_CRATE_TURNIN.equals(currentActionId) && currentActionTicks <= 10 && currentActionTicks > 0) {
            this.spawnColoredParticles(1.0F, 0.84F, 0.0F, 0.5F, 10, 1.0D);

            if (currentActionTicks == 10) {
                this.level().playLocalSound(
                        this.getX(),
                        this.getY() + 1.0D,
                        this.getZ(),
                        SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR,
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.2F,
                        false
                );
            }
        }
    }

    // --- Bounty Trades ---
    public static final Int2ObjectMap<DwarfTrades.ItemListing[]> BOUNTY_TRADES = AbstractDwarfEntity.toIntMap(ImmutableMap.of(
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



    // --- General Trades ---
    public static final Int2ObjectMap<DwarfTrades.ItemListing[]> GENERAL_TRADES = AbstractDwarfEntity.toIntMap(ImmutableMap.of(
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

    // --- Gem Trades ---
    public static final Int2ObjectMap<DwarfTrades.ItemListing[]> GEM_TRADES = AbstractDwarfEntity.toIntMap(ImmutableMap.of(
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
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(
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

        // 1. Add bounty trade for this level if not already present
        for (int lvl = 1; lvl <= level; lvl++) {
            var bountyListings = BOUNTY_TRADES.get(lvl);
            if (bountyListings != null && bountyListings.length > 0) {
                // Only add this bounty if not present yet (matches result item and price)
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

        // 2. Add up to 2 general trades for this level, but only if missing
        var generalPool = GENERAL_TRADES.get(level);
        if (generalPool != null && generalPool.length > 0) {
            // Count how many general trades from this pool are present
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

        // 3. At master, add 1 gem trade if not already present
        if (level == 5) {
            var gemTrades = GEM_TRADES.get(5);
            if (gemTrades != null && gemTrades.length > 0) {
                // Check if a gem trade already exists
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

        // 1. Save all current crate trades
        List<DwarfMerchantOffer> crateTrades = this.getOffers().stream()
                .filter(this::isCrateTrade)
                .map(DwarfMerchantOffer::copy) // Defensive copy (optional, for safety)
                .toList();

        // 2. Perform a normal restock (which will clear everything and re-add new trades)
        this.restock();

        // 3. Remove any crate trades that got rebuilt (if any)
        this.getOffers().removeIf(this::isCrateTrade);

        // 4. Re-add the original crate trades (preserving uses/availability)
        this.getOffers().addAll(crateTrades);

        // (Optional: update restock time & sound, or just let restock() handle it)
    }

    @Override
    public void restock() {
        if (this.level().isClientSide) return;

        this.getOffers().clear();

        int level = this.getVillagerData().getLevel();

        // --- 🟨 Add 1 Bounty trade per level ---
        for (int i = 1; i <= level; i++) {
            var bountyListings = BOUNTY_TRADES.get(i);
            if (bountyListings != null && bountyListings.length > 0) {
                // Only add the first bounty per level (usually only one anyway)
                this.addOffersFromItemListings(this.getOffers(), bountyListings, 1);
            }
        }

        // --- 🟩 Add 2 random General trades per level ---
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

        // --- 💎 Add 1 Gem trade at Master only ---
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

    public void restockBountiesOnly() {
        if (this.getOffers().isEmpty()) return;

        boolean restocked = false;
        for (DwarfMerchantOffer offer : this.getOffers()) {
            if (offer.getResult().is(JolCraftItems.BOUNTY.get()) && offer.needsRestock()) {
                offer.resetUses();
                restocked = true;
            }
        }

        if (restocked) {
            this.playSound(SoundEvents.VILLAGER_WORK_CARTOGRAPHER, 1.0F, 1.0F);
        }
    }


    @Override
    public void notifyTrade(DwarfMerchantOffer offer) {
        super.notifyTrade(offer); // handles XP, sound, stats, and default advancement

        if (this.getTradingPlayer() instanceof ServerPlayer serverPlayer) {
            // Fire specific advancement for Merchant
            serverPlayer.awardStat(Stats.TRADED_WITH_VILLAGER); // optional if not already done in super
            JolCraftCriteriaTriggers.TRADE_WITH_DWARF.trigger(serverPlayer, this);
        }
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> getAllJeiTrades() {
        // Merge all trade sources relevant for display
        Int2ObjectMap<DwarfTrades.ItemListing[]> out = new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>();
        for (int lvl = 1; lvl <= 5; lvl++) {
            List<DwarfTrades.ItemListing> all = new ArrayList<>();
            if (BOUNTY_TRADES.get(lvl) != null) all.addAll(List.of(BOUNTY_TRADES.get(lvl)));
            if (GENERAL_TRADES.get(lvl) != null) all.addAll(List.of(GENERAL_TRADES.get(lvl)));
            if (lvl == 5 && GEM_TRADES.get(5) != null) all.addAll(List.of(GEM_TRADES.get(5)));
            out.put(lvl, all.toArray(DwarfTrades.ItemListing[]::new));
        }
        return out;
    }

    //Sound
    @Override
    public float getVoicePitch() {
        return 1.0F;
    }


}
