package net.sievert.jolcraft.entity.custom.dwarf;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.entity.ai.goal.*;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.JolCraftSoundHelper;
import net.sievert.jolcraft.util.dwarf.bounty.BountyData;
import net.sievert.jolcraft.util.dwarf.bounty.BountyGenerator;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.util.dwarf.trade.DwarfTrades;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfMinerEntity extends AbstractDwarfEntity {

    public DwarfMinerEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_PICKAXE.get()));
        this.instanceTrades = createRandomizedMinerTrades();
    }

    //Attributes
    public static AttributeSupplier.Builder createAttributes() {
        return DwarfMinerEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30d)
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
    public boolean canReroll() { return false; }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_MINER.get());
    }

    @Override
    protected int getRequiredTier() {
        return 2;
    }

    @Override
    public ResourceLocation getProfessionId() {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_miner");
    }

    @Nullable
    @Override
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_MASON;
    }

    @Nullable
    @Override
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_MASON;
    }

    @Override
    public float getVoicePitch() { return 1.1F; }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FirePanicGoal(this, 1.3));
        this.targetSelector.addGoal(2, new DwarfNonPlayerAlertGoal(this).setAlertOthers());
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

        // Reputation check
        InteractionResult repCheck = this.reputationCheck(player, getRequiredTier());
        if (repCheck != InteractionResult.SUCCESS) {
            return repCheck;
        }


        // 🎯 Bounty crate turn-in (must be complete)
        if (itemstack.is(JolCraftItems.BOUNTY_CRATE.get())) {
            Boolean complete = itemstack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get());
            String type = itemstack.get(JolCraftDataComponents.BOUNTY_TYPE.get());
            boolean isMiner = "miner".equals(type);

            if(!isMiner){
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
            boolean isMiner = "miner".equals(type);
            if(!isMiner){
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

                        crate.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "miner");
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

        // Check if the Miner can level up and if the timer is expired
        if (this.shouldIncreaseLevel() && this.updateMerchantTimer <= 0) {
            // Check if the Miner has enough XP to level up
            if (this.shouldIncreaseLevel()) {
                // Increase the Miner's level if XP is enough
                this.increaseMerchantCareer();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                JolCraftSoundHelper.playDwarfYes(this);
                // Set the delay to 40 ticks (similar to vanilla)
                this.updateMerchantTimer = 40; // Reset the timer after leveling up
            }
        } else if (this.updateMerchantTimer > 0) {
            // Countdown the timer only if the Miner is not performing an action or trading
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
            this.spawnColoredParticles(0.2F, 0.6F, 1.0F, 0.7F, 10, 1.0D);

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

    //Trades
    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedMinerTrades() {
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(
                1, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.ItemForItemWithData(
                                JolCraftItems.PARCHMENT.get(),
                                1,
                                JolCraftItems.BOUNTY.get(),
                                1,
                                1, 0, 0,
                                (stack) -> {
                                    stack.set(JolCraftDataComponents.BOUNTY_TIER.get(), 1);
                                    stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "miner");
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
                                    stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "miner");
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
                                    stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "miner");
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
                                    stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "miner");
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
                                    stack.set(JolCraftDataComponents.BOUNTY_TYPE.get(), "miner");
                                }                    ),
                }
        ));
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

}


