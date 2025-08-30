package net.sievert.jolcraft.entity.custom.dwarf;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.custom.attachment.rep.DwarvenReputation;
import net.sievert.jolcraft.data.custom.attachment.rep.DwarvenReputationImpl;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.sound.JolCraftSoundHelper;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.entity.ai.goal.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.util.attachment.DwarvenReputationHelper;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.util.dwarf.trade.DwarfTrades;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfGuildmasterEntity extends AbstractDwarfEntity {

    public DwarfGuildmasterEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.CONTRACT_SIGNED.get()));
        this.instanceTrades = createRandomizedGuildmasterTrades();

    }

    //Data

    private int lastUnlockedLevel = 0;

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LastUnlockedLevel", lastUnlockedLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lastUnlockedLevel = tag.getInt("LastUnlockedLevel");
    }

    //Attributes
    public static AttributeSupplier.Builder createAttributes() {
        return DwarfEntity.createLivingAttributes()
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
    public boolean canReroll() { return false; }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_GUILDMASTER.get());
    }

    @Override
    public boolean neverEndorse(Player player) {
        return true;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public float getVoicePitch() {
        return 0.8F; // deeper voice for guildmaster
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FirePanicGoal(this, 1.3));
        this.targetSelector.addGoal(2, new DwarfNonPlayerAlertGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new DwarfAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new DwarfRevengeGoal(this));
        this.goalSelector.addGoal(3, new DwarfTradeWithPlayerGoal(this));
        this.goalSelector.addGoal(4, new DwarfUseItemGoal<>(this, PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING), SoundEvents.PLAYER_BURP, mob -> mob.getHealth() < mob.getMaxHealth(), 300));
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean client = this.level().isClientSide;

        // 🧠 Language check
        InteractionResult langCheck = this.languageCheck(player);
        if (langCheck != InteractionResult.SUCCESS) {
            return langCheck;
        }

        // Sync Guildmaster level with player tier (for AI or display, use standard rep)
        int tier = DwarvenReputationHelper.getTier(player);
        int desiredLevel = Math.min(tier + 1, 5);
        int currentLevel = this.getVillagerData().getLevel();

        if (currentLevel < desiredLevel && !client) {
            if (this.getOffers().isEmpty()) {
                this.updateTrades();
            }
            for (int i = currentLevel; i < desiredLevel; i++) {
                this.increaseMerchantCareer();
            }
        }

        if (!itemstack.is(JolCraftTags.Items.REPUTATION_TABLETS)) {
            return super.mobInteract(player, hand);
        }

        int maxTier = DwarvenReputationImpl.getThresholdCount();

        int strictTier = client
                ? DwarvenReputationHelper.getClientTier()
                : DwarvenReputationHelper.getTierBypassCreative(player);

        int strictEndorsementCount = client
                ? DwarvenReputationHelper.getClientEndorsementCount()
                : DwarvenReputationHelper.getEndorsementCountBypassCreative(player);

        if (strictTier >= maxTier) {
            if (client) {
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.reputation.max_tier")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            JolCraftSoundHelper.playDwarfNo(this);
            return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        if (!DwarvenReputationImpl.canAdvance(strictTier, strictEndorsementCount)) {
            if (!client) {
                int needed = DwarvenReputationImpl.getThresholdForTier(strictTier);
                player.displayClientMessage(Component.translatable(
                        "tooltip.jolcraft.reputation.not_enough_endorsements", needed, strictEndorsementCount
                ).withStyle(ChatFormatting.GRAY), true);
            }
            JolCraftSoundHelper.playDwarfNo(this);
            return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        if (this.isPerformingAction()) {
            if (client) {
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.dwarf.busy")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            JolCraftSoundHelper.playDwarfNo(this);
            return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        if (this.needsPay()) {
            if (!client) {
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.dwarf.not_paid")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            JolCraftSoundHelper.playDwarfNo(this);
            return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        Item correctTablet = switch (strictTier) {
            case 0 -> JolCraftItems.REPUTATION_TABLET_0.get();
            case 1 -> JolCraftItems.REPUTATION_TABLET_1.get();
            case 2 -> JolCraftItems.REPUTATION_TABLET_2.get();
            case 3 -> JolCraftItems.REPUTATION_TABLET_3.get();
            default -> null;
        };

        if (correctTablet == null || !itemstack.is(correctTablet)) {
            if (client) {
                player.displayClientMessage(Component.translatable("tooltip.jolcraft.reputation.wrong_tablet")
                        .withStyle(ChatFormatting.GRAY), true);
            }
            JolCraftSoundHelper.playDwarfNo(this);
            return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        // ✅ Valid advancement – process endorsement animation
        ItemStack tabletUsed = itemstack.copy();
        this.previousMainHandItem = this.getMainHandItem().copy();
        this.setItemSlot(EquipmentSlot.MAINHAND, tabletUsed.copy());
        this.usePlayerItem(player, hand, itemstack);
        JolCraftSoundHelper.playDwarfYes(this);

        this.usedItem = tabletUsed;
        beginAction(player, 40, AbstractDwarfEntity.ACTION_REPUTATION_ENDORSEMENT, tabletUsed, previousMainHandItem, () -> {
            this.setInspecting(false);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

            if (!this.level().isClientSide && this.currentActionPlayer instanceof ServerPlayer serverPlayer) {
                DwarvenReputation repFinal = serverPlayer.getData(JolCraftAttachments.DWARVEN_REP.get());
                if (repFinal.getTier() == strictTier) {
                    DwarvenReputationHelper.setReputationTier(serverPlayer, strictTier + 1);
                    JolCraftCriteriaTriggers.REPUTATION_TIER.trigger(serverPlayer);

                    int newTier = repFinal.getTier();
                    if (this.getVillagerData().getLevel() <= newTier && newTier <= 5) {
                        this.increaseMerchantCareer();
                    }

                    serverPlayer.displayClientMessage(Component.translatable("tooltip.jolcraft.reputation.level_up")
                            .withStyle(ChatFormatting.GOLD), true);

                    ItemStack nextTablet = switch (newTier) {
                        case 1 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_1.get());
                        case 2 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_2.get());
                        case 3 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_3.get());
                        case 4 -> new ItemStack(JolCraftItems.REPUTATION_TABLET_4.get());
                        default -> ItemStack.EMPTY;
                    };

                    nextTablet.set(JolCraftDataComponents.REP_ENDORSEMENTS.get(), repFinal.getEndorsementCount());
                    nextTablet.set(JolCraftDataComponents.REP_TIER.get(), repFinal.getTier());
                    nextTablet.set(JolCraftDataComponents.REP_OWNER.get(), this.currentActionPlayer.getName().getString());

                    Vec3 start = this.position().add(0.0, this.getEyeHeight(), 0.0);
                    Vec3 target = this.currentActionPlayer.position().add(0.0, this.currentActionPlayer.getBbHeight() * 0.5, 0.0);
                    Vec3 velocity = target.subtract(start).normalize().scale(0.4);

                    ItemEntity thrown = new ItemEntity(this.level(), start.x, start.y, start.z, nextTablet);
                    thrown.setDeltaMovement(velocity);
                    thrown.setPickUpDelay(10);
                    this.level().addFreshEntity(thrown);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.8F);

                    this.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
                    this.previousMainHandItem = ItemStack.EMPTY;
                }
            }
        });

        return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void tick() {
        super.tick();
        if (ACTION_REPUTATION_ENDORSEMENT.equals(currentActionId)) {
            if (currentActionTicks == 2) {
                this.level().playSound(null, this.blockPosition(), JolCraftSounds.LEVEL_UP.get(), SoundSource.NEUTRAL, 1.2F, 1.0F);
                this.spawnColoredParticles(0.35F, 0.35F, 0.35F, 1.25F, 64, 2.5D);
            }
        }
    }

    //Trades
    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedGuildmasterTrades() {
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(

                        //Novice
                        1,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsForGold(JolCraftItems.REPUTATION_TABLET_0.get(), 15, 1, 5, 0),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_MERCHANT.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_HISTORIAN.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_SCRAPPER.get(), 1, 1, 0, 0.05F)

                        },

                        //Apprentice
                        2,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_BREWMASTER.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_GUARD.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_KEEPER.get(), 1, 1, 0, 0.05F)
                        },

                        //Journeyman
                        3,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_ARTISAN.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_EXPLORER.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_MINER.get(), 1, 1, 0, 0.05F)
                        },

                        //Expert
                        4,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_ARCANIST.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_ALCHEMIST.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_PRIEST.get(), 1, 1, 0, 0.05F)
                        },

                        //Master
                        5,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_BLACKSMITH.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_CHAMPION.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_SMELTER.get(), 1, 1, 0, 0.05F)
                        }
                )
        );

    }

    @Override
    protected void updateTrades() {
        int level = this.getVillagerData().getLevel();
        if (instanceTrades != null) {
            DwarfTrades.ItemListing[] listings = instanceTrades.get(level);
            if (listings != null) {
                DwarfMerchantOffers offers = this.getOffers();
                for (DwarfTrades.ItemListing listing : listings) {
                    DwarfMerchantOffer offer = listing.getOffer(this, this.random);
                    if (offer != null) {
                        offers.add(offer);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_CARTOGRAPHER;
    }

    @Nullable
    @Override
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_CARTOGRAPHER;
    }



}

