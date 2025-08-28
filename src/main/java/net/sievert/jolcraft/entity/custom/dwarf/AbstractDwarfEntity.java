package net.sievert.jolcraft.entity.custom.dwarf;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.*;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.custom.attachment.rep.DwarvenReputationImpl;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.network.packet.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.screen.custom.dwarf.DwarfMerchantMenu;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchant;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.util.dwarf.trade.DwarfTrades;
import net.sievert.jolcraft.sound.JolCraftSoundHelper;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.ai.goal.dwarf.DwarfBlockGoal;
import net.sievert.jolcraft.entity.client.dwarf.DwarfAnimationType;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfBeardColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfEyeColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfVariant;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.network.packet.ClientboundDwarfEndorseAnimationPacket;
import net.sievert.jolcraft.network.packet.ClientboundEndorsementsPacket;
import net.sievert.jolcraft.network.packet.ClientboundReputationPacket;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.util.attachment.DwarvenReputationHelper;
import net.sievert.jolcraft.util.attachment.DwarvenLanguageHelper;

import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class AbstractDwarfEntity extends AgeableMob implements Npc, DwarfMerchant {

    @Nullable
    private Player tradingPlayer;
    @Nullable
    protected DwarfMerchantOffers offers;

    public AbstractDwarfEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setLeftHanded(false);
        this.setDropChance(EquipmentSlot.HEAD, 0.0F);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
        this.setDropChance(EquipmentSlot.LEGS, 0.0F);
        this.setDropChance(EquipmentSlot.FEET, 0.0F);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);

        for (DwarfAnimationType type : DwarfAnimationType.values()) {
            animationStates.put(type, new AnimationState());
            hasStartedFlags.put(type, false);
        }
    }

    //Animations
    public final AnimationState idleAnimationState = new AnimationState();
    public int idleAnimationTimeout = 0;

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }
    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setInspecting(boolean inspecting) {
        this.entityData.set(INSPECTING, inspecting);
    }
    public boolean isInspecting() {
        return this.entityData.get(INSPECTING);
    }

    public void setBlocking(boolean blocking) {
        this.entityData.set(BLOCKING, blocking);
    }
    public boolean isBlocking() {
        return this.entityData.get(BLOCKING);
    }

    public void setDrinking(boolean drinking) {
        this.entityData.set(DRINKING, drinking);
    }
    public boolean isDrinking() {
        return this.entityData.get(DRINKING);
    }

    private final EnumMap<DwarfAnimationType, AnimationState> animationStates = new EnumMap<>(DwarfAnimationType.class);
    private final EnumMap<DwarfAnimationType, Boolean> hasStartedFlags = new EnumMap<>(DwarfAnimationType.class);

    public AnimationState getAnimationState(DwarfAnimationType type) {
        return animationStates.get(type);
    }

    private boolean isAnimationActive(DwarfAnimationType type) {
        return switch (type) {
            case ATTACK, ATTACK_AXE -> this.isAttacking(); // Both attack types share the same flag for now
            case BLOCK -> this.isBlocking();
            case DRINK -> this.isDrinking();
            case INSPECT -> this.isInspecting();
        };
    }

    protected void setupAnimationStates() {
        // Handle idle as before (the only "ticking" animation)
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 90;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        // Handle all one-shot animations in one loop
        for (DwarfAnimationType type : DwarfAnimationType.values()) {
            boolean active = isAnimationActive(type);
            AnimationState state = animationStates.get(type);
            boolean hasStarted = hasStartedFlags.get(type);

            if (active) {
                if (!hasStarted) {
                    state.start(this.tickCount);
                    hasStartedFlags.put(type, true);
                }
            } else {
                hasStartedFlags.put(type, false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide()) {
            this.setupAnimationStates();

            //Blocking particles
            if (hasStartedFlags.get(DwarfAnimationType.BLOCK)) {
                if (blockParticlePos == null) {
                    // Capture particle spawn location at start
                    Vec3 look = this.getLookAngle().normalize();
                    double forwardOffset = 1.0D;
                    double leftOffset = -0.4D;
                    Vec3 left = new Vec3(-look.z, 0, look.x).normalize();

                    double px = this.getX() + look.x * forwardOffset + left.x * leftOffset;
                    double py = this.getY() + 1.2D;
                    double pz = this.getZ() + look.z * forwardOffset + left.z * leftOffset;

                    blockParticlePos = new Vec3(px, py, pz);
                    blockParticleTicks = 10; // spawn for 10 ticks
                }

                if (blockParticleTicks-- > 0 && blockParticlePos != null) {
                    for (int i = 0; i < 5; i++) { // spawn multiple per tick for visual density
                        double scatterRange = 0.15D; // how far they can spread from center

                        double offsetX = blockParticlePos.x + (this.random.nextDouble() - 0.5) * 2.0 * scatterRange;
                        double offsetY = blockParticlePos.y + (this.random.nextDouble() - 0.5) * 2.0 * scatterRange;
                        double offsetZ = blockParticlePos.z + (this.random.nextDouble() - 0.5) * 2.0 * scatterRange;

                        double velocityX = (this.random.nextDouble() - 0.5) * 0.1;
                        double velocityY = (this.random.nextDouble()) * 0.1; // small upward boost
                        double velocityZ = (this.random.nextDouble() - 0.5) * 0.1;

                        DustParticleOptions dust = new DustParticleOptions(-2233622, 0.5F);
                        this.level().addParticle(dust, offsetX, offsetY, offsetZ, velocityX, velocityY, velocityZ);
                    }
                }
            } else {
                // Reset when animation ends
                blockParticlePos = null;
                blockParticleTicks = 0;
            }

        }

    }

    //Behavior
    public boolean canSign() {
        return true;
    }

    public boolean canEndorse(Player player) {
        // Default: only allow if profession level is 5 (master)
        return this.getVillagerData().getLevel() >= 1;
    }

    public boolean neverEndorse(Player player) {
        return false;
    }

    // --- Action-related state ---
    protected Player currentActionPlayer;
    protected int currentActionTicks;
    protected ResourceLocation currentActionId = null;
    protected Runnable finishActionCallback;
    protected boolean performingAction = false;

    // For item snapshotting (what the player handed in, and the dwarf's previous item)
    protected ItemStack previousMainHandItem = ItemStack.EMPTY;
    protected ItemStack usedItem = ItemStack.EMPTY;

    // --- Action starter ---
    protected void beginAction(Player player, int ticks, ResourceLocation actionId, ItemStack usedItem, ItemStack previousMainHandItem, Runnable onFinish) {
        setNoAi(true);
        this.currentActionPlayer = player;
        this.currentActionTicks = ticks;
        this.currentActionId = actionId;
        this.usedItem = usedItem.copy(); // always defensive copy!
        this.previousMainHandItem = previousMainHandItem.copy();
        this.finishActionCallback = onFinish;
        this.performingAction = true;
    }

    // --- Action ticker ---
    protected void tickAction() {
        if (performingAction && currentActionTicks > 0) {
            currentActionTicks--;
            if (currentActionTicks == 0) {
                finishAction();
            }
        }
    }

    // --- Action finisher ---
    protected void finishAction() {
        setNoAi(false);
        this.performingAction = false;
        if (finishActionCallback != null) {
            finishActionCallback.run();
            finishActionCallback = null;
        }
        currentActionPlayer = null;
        usedItem = ItemStack.EMPTY;
        previousMainHandItem = ItemStack.EMPTY;
        currentActionId = null;
    }

    // --- Getters/setters ---
    protected boolean isPerformingAction() { return performingAction; }

    //Actions list

    //Common
    public static final ResourceLocation ACTION_CONTRACT_SIGNING =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "contract_signing");

    public static final ResourceLocation ACTION_PROFESSION =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "profession");

    public static final ResourceLocation ACTION_PROFESSION_PROMOTION =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "profession_promotion");

    public static final ResourceLocation ACTION_REPUTATION_ENDORSEMENT =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "reputation_endorsement");

    //Guildmaster

    //Merchant
    public static final ResourceLocation ACTION_BOUNTY_CRATE_TURNIN =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "bounty_crate_turnin");

    public static final ResourceLocation ACTION_BOUNTY_NOTE_SUBMIT =
            ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "bounty_note_submit");

    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_SIGNED.get());
    }

    public ResourceLocation getProfessionId() {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "none");
    }

    //Blocks dwarf state
    protected boolean blacklistedProperties() {
        return !this.isAlive() || this.isTrading();
    }

    //Blocks all interactions globally (e.g., spawn eggs)
    protected boolean isGloballyBlacklistedItem(ItemStack stack) {
        return stack.is(JolCraftTags.Items.DWARF_SPAWN_EGGS);
    }

    protected boolean isBusyOrBlacklisted(Player player, ItemStack stack) {
        return blacklistedProperties() || isGloballyBlacklistedItem(stack) || isPerformingAction();
    }

    // Items handled in the abstract class (e.g., contract, food)
    protected boolean isCommonHandledItem(ItemStack stack) {
        return stack.is(JolCraftItems.CONTRACT_WRITTEN.get()) ||
                this.isFood(stack);
    }

    // Items *delegated* to subclass (e.g., bounty crate, bounty scroll)
    protected boolean isSubclassHandledItem(ItemStack stack) {
        return stack.is(JolCraftItems.BOUNTY.get())
                || stack.is(JolCraftItems.BOUNTY_CRATE.get());
    }

    public InteractionResult languageCheck(Player player) {
        boolean client = this.level().isClientSide;
        boolean knowsLanguage = DwarvenLanguageHelper.knowsDwarvish(player);

        if (!knowsLanguage) {
            JolCraftSoundHelper.playDwarfNo(this);

            if (client) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.language.locked").withStyle(ChatFormatting.RED), true
                );
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    protected int getRequiredTier() {
        return 0; // Default to tier 0, override in subclasses
    }

    public InteractionResult reputationCheck(Player player, int requiredTier) {
        boolean client = this.level().isClientSide;
        boolean hasTier = client
                ? DwarvenReputationHelper.hasClientTier(requiredTier)
                : DwarvenReputationHelper.hasTierServer(player, requiredTier);

        if (!hasTier) {
            JolCraftSoundHelper.playDwarfNo(this);

            if (client) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.reputation.locked", requiredTier)
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean client = this.level().isClientSide;

        // 🧠 Language check - ensures only players who know the Dwarvish language can interact
        InteractionResult langCheck = this.languageCheck(player);
        if (langCheck != InteractionResult.SUCCESS) {
            return langCheck;
        }

        InteractionResult repCheck = this.reputationCheck(player, getRequiredTier());
        if (repCheck != InteractionResult.SUCCESS) {
            return repCheck;
        }


        // 🛑 Block if dwarf is dead, trading, holding a spawn egg, or performing an action
        if (this.isBusyOrBlacklisted(player, itemstack)) {
            if (this.isPerformingAction() && this.level().isClientSide) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.dwarf.busy").withStyle(ChatFormatting.GRAY), true
                );
            }else{
                JolCraftSoundHelper.playDwarfNo(this);
            }
            return InteractionResult.FAIL; // ❌ Block interaction if any condition is met
        }

        // 🔄 Delegate to subclass for specific item handling (e.g., bounty crates, etc.)
        if (isSubclassHandledItem(itemstack)) {
            return InteractionResult.CONSUME; // ✅ Subclass consumes the interaction, no further action
        }

        // 🍞 Breeding or feeding dwarf (based on itemstack)
        if (this.isFood(itemstack)) {
            int i = this.getAge();

            // 🐺 Start love mode if dwarf is an adult and can fall in love
            if (!client && i == 0 && this.canFallInLove()) {
                this.usePlayerItem(player, hand, itemstack);
                this.setInLove(player);
                this.playEatingSound();
                return InteractionResult.SUCCESS_SERVER; // ✅ Valid breeding initiated on the server
            }

            // 👶 If the dwarf is a baby, age it up and trigger eating sound
            if (this.isBaby()) {
                this.usePlayerItem(player, hand, itemstack);
                this.ageUp(getSpeedUpSecondsWhenFeeding(-i), true);
                this.playEatingSound();
                return InteractionResult.SUCCESS; // ✅ Client handles particles/sound
            }

            // ✅ Client side consumes the item and displays the correct animation
            if (client) {
                return InteractionResult.CONSUME;
            }

            // ❌ If the dwarf is neither in love nor a baby, play sound indicating invalid action
            JolCraftSoundHelper.playDwarfNo(this);
        }

        // 🔇 Fallback to generic common interaction logic
        return handleCommonInteractions(player, hand);
    }


    // ✅ Common interactions (if nothing else is handled)
    public InteractionResult handleCommonInteractions(Player player, InteractionHand hand) {
        // Check if the dwarf is currently performing an action (like processing a bounty crate)
        if (this.isPerformingAction()) {
            return InteractionResult.FAIL; // Block any interactions if an action is in progress
        }

        ItemStack itemstack = player.getItemInHand(hand);

        // 🛑 Common interactions if none of above interactions were triggered

        //Paid
        if (itemstack.is(JolCraftItems.GOLD_COIN.get()) && this.canBePaid() && !this.isBaby()) {
            this.setPaid(player);
            this.level().playSound(null, this.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.4F);
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }


        // 🖊️ Contract signing action
        if (itemstack.is(JolCraftItems.CONTRACT_WRITTEN.get()) && !this.isBaby()) {
            boolean client = this.level().isClientSide;

            // 1️⃣ Cannot sign (wrong dwarf type)
            if (!canSign()) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.dwarf.cannot_sign").withStyle(ChatFormatting.GRAY), true
                    );
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // 2️⃣ Not paid yet
            if (!this.isPaid()) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY), true
                    );
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // ✅ All clear: proceed with contract signing
            this.usePlayerItem(player, hand, itemstack);
            JolCraftSoundHelper.playDwarfYes(this);
            ItemStack prevMainHand = this.getMainHandItem().copy();
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.CONTRACT_WRITTEN.get()));

            beginAction(player, 40, ACTION_CONTRACT_SIGNING, itemstack, prevMainHand, () -> {
                this.setInspecting(false);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

                if (!this.level().isClientSide && this.currentActionPlayer != null) {
                    // Throw the signed contract to the player
                    Vec3 start = this.position().add(0.0, this.getEyeHeight(), 0.0);
                    Vec3 target = this.currentActionPlayer.position().add(0.0, this.currentActionPlayer.getBbHeight() * 0.5, 0.0);
                    Vec3 velocity = target.subtract(start).normalize().scale(0.4);

                    ItemEntity thrown = new ItemEntity(this.level(), start.x, start.y, start.z, this.getSignedContractItem());
                    thrown.setDeltaMovement(velocity);
                    thrown.setPickUpDelay(10);
                    this.level().addFreshEntity(thrown);

                    this.level().playSound(null, this.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.8F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
                    this.previousMainHandItem = ItemStack.EMPTY;
                }
            });

            return InteractionResult.SUCCESS_SERVER;
        }


        // 🪄 Profession Promotion with Signed Contract
        if (itemstack.is(JolCraftTags.Items.SIGNED_CONTRACTS) && !this.isBaby()) {
            boolean client = this.level().isClientSide;

            // 1 Not promotable type (wrong dwarf)
            if (!canPromoteToProfession()) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.dwarf.cannot_promote").withStyle(ChatFormatting.GRAY), true
                    );
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // 2 Not paid yet
            if (!this.isPaid()) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY), true
                    );
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // ✅ All clear: proceed with profession promotion
            this.previousMainHandItem = itemstack.copy(); // Save contract for animation & later use
            this.usePlayerItem(player, hand, itemstack);  // Remove contract from player
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy()); // Show contract in dwarf's hand
            this.level().playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.5F);

            // Start the promotion action
            beginAction(player, 40, ACTION_PROFESSION_PROMOTION, itemstack, previousMainHandItem, () -> {
                this.setInspecting(false);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

                if (!this.level().isClientSide && this.currentActionPlayer != null) {
                    this.transformToProfession(); // Transform to new profession
                }
                this.previousMainHandItem = ItemStack.EMPTY;
            });

            return InteractionResult.SUCCESS_SERVER;
        }

        // 🗿 Tablet endorsement (works both sides for animation)
        if (itemstack.is(JolCraftTags.Items.REPUTATION_TABLETS) && !this.isBaby()) {
            ResourceLocation profId = this.getProfessionId();
            boolean client = this.level().isClientSide;
            boolean hasEndorsement = client
                    ? DwarvenReputationHelper.hasClientEndorsementBypassCreative(profId)
                    : DwarvenReputationHelper.hasEndorsementServerBypassCreative(player, profId);

            // Send to subclass if special case
            if (this instanceof DwarfGuildmasterEntity) {
                return InteractionResult.CONSUME;
            }

            // 1️⃣ Already endorsed? Instantly block.
            if (hasEndorsement) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.reputation.already_endorsed").withStyle(ChatFormatting.GRAY), true);
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // 2️⃣a. This dwarf can NEVER endorse (Guildmaster, PlainDwarf, etc.)
            if (this.neverEndorse(player)) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.reputation.never_endorse").withStyle(ChatFormatting.GRAY), true);
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // 2️⃣b. Can endorse in principle, but player doesn't meet conditions
            if (!this.canEndorse(player)) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.reputation.cannot_endorse").withStyle(ChatFormatting.GRAY), true);
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // 3️⃣ Paid check
            if (!this.isPaid()) {
                if (!client) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.dwarf.not_paid").withStyle(ChatFormatting.GRAY), true);
                }
                JolCraftSoundHelper.playDwarfNo(this);
                return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            // ✅ Proceed with animation and endorsement
            ItemStack prevMainHand = this.getMainHandItem().copy();
            ItemStack tabletUsed = itemstack.copy();
            this.setItemSlot(EquipmentSlot.MAINHAND, tabletUsed.copy());
            this.usePlayerItem(player, hand, itemstack);
            JolCraftSoundHelper.playDwarfYes(this);

            beginAction(player, 40, ACTION_REPUTATION_ENDORSEMENT, tabletUsed, prevMainHand, () -> {
                this.setInspecting(false);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

                if (!this.level().isClientSide && this.currentActionPlayer != null) {
                    ResourceLocation profIdFinish = this.getProfessionId();
                    DwarvenReputationImpl repFinish = this.currentActionPlayer.getData(JolCraftAttachments.DWARVEN_REP.get());

                    boolean added = false;
                    if (repFinish != null && !repFinish.hasEndorsement(profIdFinish)) {
                        repFinish.addEndorsement(profIdFinish);
                        added = true;

                        if (this.currentActionPlayer instanceof ServerPlayer serverPlayer) {
                            JolCraftCriteriaTriggers.ENDORSEMENT_GAIN.trigger(serverPlayer, profIdFinish);
                        }
                    }

                    if (this.currentActionPlayer instanceof ServerPlayer serverPlayer) {
                        JolCraftNetworking.sendToClient(serverPlayer,
                                new ClientboundReputationPacket(repFinish.getTier()));
                        JolCraftNetworking.sendToClient(serverPlayer,
                                new ClientboundEndorsementsPacket(repFinish.getEndorsements()));
                    }

                    if (added) {
                        // Throw updated tablet
                        ItemStack updatedTablet = this.usedItem.copy();
                        updatedTablet.set(JolCraftDataComponents.REP_ENDORSEMENTS.get(), repFinish.getEndorsementCount());
                        updatedTablet.set(JolCraftDataComponents.REP_TIER.get(), repFinish.getTier());
                        updatedTablet.set(JolCraftDataComponents.REP_OWNER.get(), this.currentActionPlayer.getName().getString());

                        Vec3 start = this.position().add(0.0, this.getEyeHeight(), 0.0);
                        Vec3 target = this.currentActionPlayer.position().add(0.0, this.currentActionPlayer.getBbHeight() * 0.5, 0.0);
                        Vec3 velocity = target.subtract(start).normalize().scale(0.4);

                        ItemEntity thrown = new ItemEntity(this.level(), start.x, start.y, start.z, updatedTablet);
                        thrown.setDeltaMovement(velocity);
                        thrown.setPickUpDelay(10);
                        this.level().addFreshEntity(thrown);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.8F);
                    }

                    this.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
                    this.previousMainHandItem = ItemStack.EMPTY;
                }
            });

            if (!client) {
                JolCraftNetworking.sendToNearbyClients(
                        this.level(), this.blockPosition(), 32,
                        new ClientboundDwarfEndorseAnimationPacket(this.getId())
                );
            }

            return client ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }


        // 💼 Trade (only if hand empty and a baby)
        if (canTrade()
                && itemstack.isEmpty()
                && !this.isBaby()
                && (player.getAbilities() == null || !player.getAbilities().instabuild || player.getInventory().getSelected().isEmpty()))
        {
            if (hand == InteractionHand.MAIN_HAND) {
                player.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (!this.level().isClientSide) {
                if (this.getOffers().isEmpty()) {
                    return InteractionResult.FAIL;
                }

                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), this.getVillagerData().getLevel());
            }

            return InteractionResult.SUCCESS;
        }
        JolCraftSoundHelper.playDwarfNo(this);
        return InteractionResult.FAIL; // ❌ Block any fallback actions
    }

    @Override
    protected void customServerAiStep(ServerLevel p_376777_) {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        if (this.assignProfessionWhenSpawned) {
            this.assignProfessionWhenSpawned = false;
        }

        if (!this.isTrading() && this.updateMerchantTimer > 0) {
            this.updateMerchantTimer--;
            if (this.updateMerchantTimer <= 0) {
                if (this.increaseProfessionLevelOnUpdate) {
                    this.increaseMerchantCareer();
                    this.increaseProfessionLevelOnUpdate = false;

                }
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                JolCraftSoundHelper.playDwarfYes(this);
            }
        }
        if (this.shouldRestock()) {
            this.restock();
            lastRestockGameTime = this.level().getGameTime(); // Reset timer
        }
        super.customServerAiStep(p_376777_);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickBlockCooldown();
        tickAction();

        if (this.level().isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level().addParticle(
                            ParticleTypes.HAPPY_VILLAGER,
                            this.getRandomX(1.0),
                            this.getRandomY() + 0.5,
                            this.getRandomZ(1.0),
                            0.0, 0.0, 0.0
                    );
                }
                this.forcedAgeTimer--;
            }
        }

        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        if (this.inLove > 0) {
            this.inLove--;
            if (this.inLove % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
            }
        }

        // Paid mode ticking
        if (this.paidTicks > 0) {
            this.paidTicks--;
            // Every 30 ticks, spawn a little gold sparkle/coin effect
            if (this.paidTicks % 30 == 0) {
                this.spawnColoredParticles(1.0F, 0.84F, 0.0F, 1.0F, 3, 0.4D);
            }
        }


        // Contract signing: do per-tick effects if this is the current action
        if (ACTION_CONTRACT_SIGNING.equals(currentActionId)) {
            this.setInspecting(true); // Show "inspecting" animation state (signing)

            // Paid status: always reset at the start of animation
            if (currentActionTicks == 39) {
                this.resetPaid();
            }

            // Sound effects at specific ticks
            if (currentActionTicks == 25 || currentActionTicks == 15) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.NEUTRAL, 1.0F, 1.2F);
            }

        }

        //Profession promotion
        if (ACTION_PROFESSION_PROMOTION.equals(currentActionId)) {
            this.setInspecting(true); // You could use a different animation flag if you want

            if (this.currentActionTicks == 39) {
                // First subtle gray poof
                this.spawnColoredParticles(0.35F, 0.35F, 0.35F, 0.7F, 16, 0.5D);
                this.resetPaid();
            }

            if (currentActionTicks == 20) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.5F);
                this.spawnColoredParticles(0.35F, 0.35F, 0.35F, 0.8F, 24, 0.7D);
            }
            if (currentActionTicks == 2) {
                this.level().playSound(null, this.blockPosition(), JolCraftSounds.POOF.get(), SoundSource.NEUTRAL, 1.5F, 1.0F);
                this.spawnColoredParticles(0.35F, 0.35F, 0.35F, 1.25F, 64, 2.5D);
            }
        }

        // Tablet endorsement per-tick logic
        if (ACTION_REPUTATION_ENDORSEMENT.equals(currentActionId)) {
            this.setInspecting(true);
            if (currentActionTicks == 39) {
                this.resetPaid();
            }
            if (currentActionTicks == 25 || currentActionTicks == 15) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.NEUTRAL, 1.2F, 0.6F);
            }
        }

        // Profession interaction (guard, etc)
        if (ACTION_PROFESSION.equals(currentActionId)) {
            this.setInspecting(true);
        }

        // Universal animation reset: runs if NO action is running
        if (currentActionId == null) {
            this.setInspecting(false);
            // Add other animation flags to reset here as you add more actions!
        }

    }

    public static final Set<EntityType<?>> PROMOTABLE_DWARF_TYPES = Set.of(JolCraftEntities.DWARF.get());


    public boolean canPromoteToProfession() {
        return PROMOTABLE_DWARF_TYPES.contains(this.getType()) && this.isAlive() && !this.isBaby();
    }

    @Nullable
    public EntityType<? extends AbstractDwarfEntity> resolveProfessionType(ItemStack contractStack) {
        if (contractStack == null || contractStack.isEmpty()) return null;
        return CONTRACT_TO_PROFESSION.get(contractStack.getItem());
    }

    public static final Map<Item, EntityType<? extends AbstractDwarfEntity>> CONTRACT_TO_PROFESSION = Map.ofEntries(
            Map.entry(JolCraftItems.CONTRACT_GUILDMASTER.get(), JolCraftEntities.DWARF_GUILDMASTER.get()),
            // Tier 1
            Map.entry(JolCraftItems.CONTRACT_MERCHANT.get(), JolCraftEntities.DWARF_MERCHANT.get()),
            Map.entry(JolCraftItems.CONTRACT_HISTORIAN.get(), JolCraftEntities.DWARF_HISTORIAN.get()),
            Map.entry(JolCraftItems.CONTRACT_SCRAPPER.get(), JolCraftEntities.DWARF_SCRAPPER.get()),
            // Tier 2
            Map.entry(JolCraftItems.CONTRACT_GUARD.get(), JolCraftEntities.DWARF_GUARD.get()),
            Map.entry(JolCraftItems.CONTRACT_BREWMASTER.get(), JolCraftEntities.DWARF_BREWMASTER.get()),
            Map.entry(JolCraftItems.CONTRACT_KEEPER.get(), JolCraftEntities.DWARF_KEEPER.get()),

            // Tier 3
            Map.entry(JolCraftItems.CONTRACT_ARTISAN.get(), JolCraftEntities.DWARF_ARTISAN.get()),
            Map.entry(JolCraftItems.CONTRACT_EXPLORER.get(), JolCraftEntities.DWARF_EXPLORER.get()),
            Map.entry(JolCraftItems.CONTRACT_MINER.get(), JolCraftEntities.DWARF_MINER.get())

            /*
            // Tier 4
            Map.entry(JolCraftItems.CONTRACT_ARCANIST.get(), JolCraftEntities.DWARF_ARCANIST.get()),
            Map.entry(JolCraftItems.CONTRACT_PRIEST.get(), JolCraftEntities.DWARF_PRIEST.get()),
            Map.entry(JolCraftItems.CONTRACT_ALCHEMIST.get(), JolCraftEntities.DWARF_ALCHEMIST.get()),
            // Tier 5
            Map.entry(JolCraftItems.CONTRACT_CHAMPION.get(), JolCraftEntities.DWARF_CHAMPION.get()),
            Map.entry(JolCraftItems.CONTRACT_BLACKSMITH.get(), JolCraftEntities.DWARF_BLACKSMITH.get()),
            Map.entry(JolCraftItems.CONTRACT_SMELTER.get(), JolCraftEntities.DWARF_SMELTER.get())

            */

    );

    public void transformToProfession() {
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();

            EntityType<? extends AbstractDwarfEntity> professionType = resolveProfessionType(this.previousMainHandItem);


            if (professionType != null) {
                // This is the best usage for NeoForge 1.21.x
                Entity entity = professionType.create(
                        serverLevel,
                        null, // Consumer<T>
                        this.blockPosition(),
                        EntitySpawnReason.CONVERSION,
                        false, // shouldOffsetY
                        false  // shouldOffsetYMore
                );

                if (entity instanceof AbstractDwarfEntity newDwarf) {
                    newDwarf.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

                    //Preserve data
                    newDwarf.setBeard(this.getBeard());
                    newDwarf.setEye(this.getEye());

                    serverLevel.addFreshEntity(newDwarf);
                    this.discard();
                }
            }
        }
    }

    //Blocking
    protected Vec3 blockParticlePos = null;
    protected int blockParticleTicks = 0;

    protected boolean shouldStartBlocking = false;

    public int blockCooldown = 0;

    public boolean isBlockCooldownReady() {
        return blockCooldown <= 0;
    }

    public void setBlockCooldown(int ticks) {
        this.blockCooldown = ticks;
    }

    public void tickBlockCooldown() {
        if (this.goalSelector.getAvailableGoals().stream().anyMatch(
                goal -> goal.getGoal() instanceof DwarfBlockGoal
        )) {
            if (blockCooldown > 0) blockCooldown--;
        }
    }

    public void markForBlocking() {
        if(!this.isDrinking()){
            this.shouldStartBlocking = true;
        }
    }

    public boolean consumeBlockFlag() {
        if (this.shouldStartBlocking) {
            this.shouldStartBlocking = false;
            return true;
        }
        return false;
    }

    //Breeding
    protected int inLove;
    @Nullable
    protected UUID loveCause;

    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.BREAD);
    }

    protected void playEatingSound() {
        this.playSound(SoundEvents.PLAYER_BURP, 1.0F, this.getVoicePitch());
    }

    public boolean canFallInLove() {
        return this.inLove <= 0;
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public void setInLoveTime(int inLove) {
        this.inLove = inLove;
    }

    public int getInLoveTime() {
        return this.inLove;
    }

    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        int i = stack.getCount();
        UseRemainder useremainder = stack.get(DataComponents.USE_REMAINDER);
        stack.consume(1, player);
        if (useremainder != null) {
            ItemStack itemstack = useremainder.convertIntoRemainder(stack, i, player.hasInfiniteMaterials(), player::handleExtraItemsCreatedOnUse);
            player.setItemInHand(hand, itemstack);
        }
    }

    public void setInLove(@Nullable Player player) {
        this.inLove = 600;
        if (player != null) {
            this.loveCause = player.getUUID();
        }

        this.level().broadcastEntityEvent(this, (byte)18);
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        } else {
            Player player = this.level().getPlayerByUUID(this.loveCause);
            return player instanceof ServerPlayer ? (ServerPlayer)player : null;
        }
    }


    @Override
    public void handleEntityEvent(byte id) {
        if (id == 18) {
            for (int i = 0; i < 7; i++) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
            }
        }
        if (id == 19) {
            // Gold coins or sparkle (paid)
            this.spawnColoredParticles(1.0F, 0.84F, 0.0F, 1.0F, 7, 0.5D);
        }
        else {
            super.handleEntityEvent(id);
        }
    }

    public boolean canMate(AbstractDwarfEntity partner) {
        if (partner == this) {
            return false;
        } else {
            return partner instanceof AbstractDwarfEntity && this.isInLove() && partner.isInLove();
        }
    }

    public void spawnChildFromBreeding(ServerLevel level, AbstractDwarfEntity partner) {
        AgeableMob ageablemob = this.getBreedOffspring(level, partner);
        final net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, partner, ageablemob);
        final boolean cancelled = NeoForge.EVENT_BUS.post(event).isCanceled();
        ageablemob = event.getChild();
        if (cancelled) {
            this.setAge(6000);
            partner.setAge(6000);
            this.resetLove();
            partner.resetLove();
            return;
        }
        if (ageablemob != null) {
            ageablemob.setBaby(true);
            ageablemob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.finalizeSpawnChildFromBreeding(level, partner, ageablemob);
            level.addFreshEntityWithPassengers(ageablemob);
        }
    }

    public void finalizeSpawnChildFromBreeding(ServerLevel level, AbstractDwarfEntity dwarf, @Nullable AgeableMob baby) {
        this.setAge(6000);
        dwarf.setAge(6000);
        this.resetLove();
        dwarf.resetLove();
        level.broadcastEntityEvent(this, (byte)18);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            level.addFreshEntity(new ExperienceOrb(level, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    protected void actuallyHurt(ServerLevel p_376120_, DamageSource p_341676_, float p_341648_) {
        this.resetLove();
        super.actuallyHurt(p_376120_, p_341676_, p_341648_);
    }

    //Entity Data Accessors
    public static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BEARD_COLOR =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> EYE_COLOR =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> INSPECTING =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> BLOCKING =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> DRINKING =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.VILLAGER_DATA);

    //Data
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(BEARD_COLOR, 0);
        builder.define(EYE_COLOR, 0);
        builder.define(ATTACKING, false);
        builder.define(INSPECTING, false);
        builder.define(BLOCKING, false);
        builder.define(DRINKING, false);
        builder.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("InLove", this.inLove);
        compound.putInt("Variant", this.getTypeVariant());
        compound.putInt("Beard", this.getTypeBeard());
        compound.putInt("Eye", this.getTypeEye());
        if (this.loveCause != null) {
            compound.putUUID("LoveCause", this.loveCause);
        }
        // 🪙 Paid status
        compound.putInt("PaidTicks", this.paidTicks);
        if (this.paidCause != null) {
            compound.putUUID("PaidCause", this.paidCause);
        }
        compound.putInt("Age", this.getAge());
        compound.putInt("ForcedAge", this.forcedAge);
        VillagerData.CODEC
                .encodeStart(NbtOps.INSTANCE, this.getVillagerData())
                .resultOrPartial(LOGGER::error)
                .ifPresent(p_35454_ -> compound.put("VillagerData", p_35454_));
        compound.putInt("Xp", this.dwarfXp);
        if (this.assignProfessionWhenSpawned) {
            compound.putBoolean("AssignProfessionWhenSpawned", true);
        }
        if (!this.level().isClientSide) {
            DwarfMerchantOffers merchantoffers = this.getOffers();
            if (!merchantoffers.isEmpty()) {
                compound.put(
                        "Offers", DwarfMerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), merchantoffers).getOrThrow()
                );
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.inLove = compound.getInt("InLove");
        this.loveCause = compound.hasUUID("LoveCause") ? compound.getUUID("LoveCause") : null;
        // 🪙 Paid status
        this.paidTicks = compound.getInt("PaidTicks");
        this.paidCause = compound.hasUUID("PaidCause") ? compound.getUUID("PaidCause") : null;
        this.entityData.set(VARIANT, compound.getInt("Variant"));
        this.entityData.set(BEARD_COLOR, compound.getInt("Beard"));
        this.entityData.set(EYE_COLOR, compound.getInt("Eye"));
        this.setAge(compound.getInt("Age"));
        this.forcedAge = compound.getInt("ForcedAge");
        if (compound.contains("VillagerData", 10)) {
            VillagerData.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("VillagerData"))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(p_323354_ -> this.entityData.set(DATA_VILLAGER_DATA, p_323354_));
        }
        if (compound.contains("Xp", 3)) {
            this.dwarfXp = compound.getInt("Xp");
        }
        if (compound.contains("AssignProfessionWhenSpawned")) {
            this.assignProfessionWhenSpawned = compound.getBoolean("AssignProfessionWhenSpawned");
        }
        if (compound.contains("Offers")) {
            DwarfMerchantOffers.CODEC
                    .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("Offers"))
                    .resultOrPartial(Util.prefix("Failed to load offers: ", LOGGER::warn))
                    .ifPresent(p_323775_ -> this.offers = p_323775_);
        }
    }

    //Paying
    protected int paidTicks;
    @Nullable
    protected UUID paidCause;
    public static final int MAX_PAID_TICKS = 20 * 60; // 1 min

    public boolean isPaid() {
        return this.paidTicks > 0;
    }

    public void setPaid(@Nullable Player player) {
        this.paidTicks = MAX_PAID_TICKS;
        if (player != null) {
            this.paidCause = player.getUUID();
        }
        this.level().broadcastEntityEvent(this, (byte)19); // 19 = custom code for "paid"
    }

    public void resetPaid() {
        this.paidTicks = 0;
        this.paidCause = null;
    }

    public boolean canBePaid() {
        return this.paidTicks <= 0;
    }

    public int getPaidTicks() {
        return this.paidTicks;
    }

    @Nullable
    public ServerPlayer getPaidCause() {
        if (this.paidCause == null) return null;
        Player player = this.level().getPlayerByUUID(this.paidCause);
        return player instanceof ServerPlayer ? (ServerPlayer)player : null;
    }

    // Particles
    protected void spawnColoredParticles(float r, float g, float b, float scale, int count, double scatter) {
        if (!this.level().isClientSide()) return;

        int rgb = ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        DustParticleOptions dust = new DustParticleOptions(rgb, scale);

        Vec3 forward = this.getLookAngle().normalize(); // Get the direction the dwarf is facing
        // Position particles above the dwarf's head and slightly back in their looking direction
        double baseX = this.getX() + forward.x * 0.6;  // Move back by half a block (smaller scale)
        double baseY = this.getY() + 1.8D; // Raised height (above the dwarf's head)
        double baseZ = this.getZ() + forward.z * 0.5;  // Move back by half a block (smaller scale)

        // Spawn particles in a scattered cloud
        for (int i = 0; i < count; i++) {
            // Apply randomness to x, y, z positions to scatter particles in a cloud
            double offsetX = baseX + (this.random.nextDouble() - 0.5D) * scatter; // Spread in X direction
            double offsetY = baseY + (this.random.nextDouble() - 0.5D) * scatter; // Spread in Y direction (vertical)
            double offsetZ = baseZ + (this.random.nextDouble() - 0.5D) * scatter; // Spread in Z direction

            // Random velocity for particles
            double velocityX = (this.random.nextDouble() - 0.5D) * 0.1D;
            double velocityY = this.random.nextDouble() * 0.1D;
            double velocityZ = (this.random.nextDouble() - 0.5D) * 0.1D;

            // Add the particle to the world
            this.level().addParticle(dust, offsetX, offsetY, offsetZ, velocityX, velocityY, velocityZ);
        }
    }

    //Loot

    // Helper: check for quest/contract items
    private boolean isSpecialDropItem(ItemStack stack) {
        return stack.is(JolCraftItems.CONTRACT_WRITTEN.get()) || stack.is(JolCraftItems.CONTRACT_SIGNED.get()) || stack.is(JolCraftItems.BOUNTY.get()) || stack.is(JolCraftItems.BOUNTY_CRATE.get());
    }

    // Helper: allow only certain gear to drop
    private boolean shouldDropEquipment(ItemStack stack) {
        // Only drop specific
        return stack.is(Items.DIAMOND);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        // Handle MAINHAND item
        ItemStack mainHand = this.getMainHandItem();

        if (!mainHand.isEmpty()) {
            if (isSpecialDropItem(mainHand)) {
                // Always drop special quest items (contract, bounty crate, etc)
                this.spawnAtLocation(level, mainHand);
            } else if (shouldDropEquipment(mainHand)) {
                // Drop only certain equipment (iron axe, etc)
                this.spawnAtLocation(level, mainHand);
            }
            // Otherwise: do not drop (e.g., remove from world)
            // Clear mainhand slot to avoid duplicate drops
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        // Optionally: call super to handle other drops (armor, offhand, etc) as usual
        super.dropCustomDeathLoot(level, source, recentlyHit);
    }


    //Trading and Villager Data
    public long lastRestockGameTime = 0L;
    public static final long RESTOCK_INTERVAL_TICKS = 6000L;
    @Nullable
    public Player lastTradedPlayer;
    public int dwarfXp;
    public boolean increaseLevelOnUpdate = false;
    public int updateMerchantTimer = 0;
    public static final Logger LOGGER = LogUtils.getLogger();
    public boolean increaseProfessionLevelOnUpdate = false;
    public boolean assignProfessionWhenSpawned;
    public Int2ObjectMap<DwarfTrades.ItemListing[]> instanceTrades;

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> toIntMap(Map<Integer, DwarfTrades.ItemListing[]> pMap) {
        return new Int2ObjectOpenHashMap<>(pMap);
    }

    public boolean canTrade() {
        return false;
    }

    public boolean hasRandomTrades(){ return false; }

    public boolean canReroll(){ return true; }

    protected void updateTrades() {
        int level = this.getVillagerData().getLevel();
        if (instanceTrades != null) {
            DwarfTrades.ItemListing[] listings = instanceTrades.get(level);
            if (listings != null) {
                this.addOffersFromItemListings(this.getOffers(), listings, listings.length);
            }
        }
    }

    @Override
    public int getVillagerXp() {
        return this.dwarfXp;
    }

    @Override
    public boolean showProgressBar() {
        return true;
    }

    public boolean showLevel() {
        return true;
    }

    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    public void setVillagerData(VillagerData villagerData) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != villagerData.getProfession()) {
            this.offers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, villagerData);
    }

    public boolean shouldIncreaseLevel() {
        int i = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(i) && this.dwarfXp >= VillagerData.getMaxXpPerLevel(i);
    }

    public boolean shouldRestock() {
        return this.level() instanceof ServerLevel serverLevel &&
                serverLevel.getGameTime() >= lastRestockGameTime + RESTOCK_INTERVAL_TICKS;
    }

    public void restock() {
        if (this.getOffers().isEmpty()) return;

        boolean needsRestock = false;
        for (DwarfMerchantOffer offer : this.getOffers()) {
            if (offer.needsRestock()) {
                offer.resetUses();
                needsRestock = true;
            }
        }
        if (needsRestock) {
            this.lastRestockGameTime = this.level().getGameTime();
            this.level().playSound(null, this.blockPosition(), getRestockSound(), SoundSource.NEUTRAL, 1.2F, 1.0F);
        }
    }

    public void crateRestock() {
        restock();
    }

    public void rerollTrades() {
        this.getOffers().clear();
        int originalLevel = this.getVillagerData().getLevel();
        for (int i = 1; i <= originalLevel; i++) {
            this.setVillagerData(this.getVillagerData().setLevel(i));
            this.updateTrades();
        }
        this.setVillagerData(this.getVillagerData().setLevel(originalLevel));
        this.level().playSound(null, this.blockPosition(), getRerollSound(), SoundSource.NEUTRAL, 1.2F, 1.0F);
    }

    protected void rewardTradeXp(DwarfMerchantOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.dwarfXp = this.dwarfXp + offer.getXp();
        this.lastTradedPlayer = this.getTradingPlayer();
        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            i += 5;
        }

        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    public void resendOffersToTradingPlayer() {
        DwarfMerchantOffers merchantOffers = this.getOffers();
        Player player = this.getTradingPlayer();
        if (player instanceof ServerPlayer serverPlayer && !merchantOffers.isEmpty()) {
            JolCraftNetworking.sendToClient(
                    serverPlayer,
                    new ClientboundDwarfMerchantOffersPacket(
                            serverPlayer.containerMenu.containerId,
                            merchantOffers,
                            this.getVillagerData().getLevel(),
                            this.getVillagerXp(),
                            this.showProgressBar(),
                            this.showLevel(),
                            this.canRestock()
                    )
            );
        }
    }


    protected void increaseMerchantCareer() {
        int current = this.getVillagerData().getLevel();
        if (VillagerData.canLevelUp(current)) {
            int next = current + 1;
            this.setVillagerData(this.getVillagerData().setLevel(next));
            this.updateTrades();
            this.resendOffersToTradingPlayer();
        }
    }

    @Override
    public void openTradingScreen(Player player, Component displayName, int level) {
        OptionalInt menuId = player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, accessingPlayer) -> new DwarfMerchantMenu(containerId, inventory, this),
                displayName
        ));

        if (menuId.isPresent() && !player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            DwarfMerchantOffers offers = this.getOffers();
            if (!offers.isEmpty()) {
                JolCraftNetworking.sendToClient(
                        serverPlayer,
                        new ClientboundDwarfMerchantOffersPacket(
                                menuId.getAsInt(),
                                offers,
                                level,                   // Pass level argument as dwarfLevel
                                this.getVillagerXp(),
                                this.showProgressBar(),
                                this.showLevel(),
                                this.canRestock()
                        )
                );
            }
        }
    }

    @Override
    public void notifyTrade(DwarfMerchantOffer offer) {
        Player player = this.getTradingPlayer();
        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(offer);
        if (player instanceof ServerPlayer serverPlayer) {
            player.awardStat(Stats.TRADED_WITH_VILLAGER);
            JolCraftCriteriaTriggers.TRADE_WITH_DWARF.trigger(serverPlayer, this);
        }
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    @Override
    public DwarfMerchantOffers getOffers() {
        if (this.level().isClientSide) {
            throw new IllegalStateException("Cannot load Villager offers on the client");
        } else {
            if (this.offers == null) {
                this.offers = new DwarfMerchantOffers();
                this.updateTrades();
            }

            return this.offers;
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.makeSound(this.getTradeUpdatedSound(!stack.isEmpty()));
        }
    }

    @Override
    public void overrideOffers(@Nullable DwarfMerchantOffers offers) {
    }

    @Override
    public void overrideXp(int xp) {
    }

    //Sounds
    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
            return this.isTrading() ? JolCraftSounds.DWARF_TRADE.get() : JolCraftSounds.DWARF_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return JolCraftSounds.DWARF_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return JolCraftSounds.DWARF_DEATH.get();
    }

    @Nullable
    @Override
    public SoundEvent getNotifyTradeSound() {
        return JolCraftSounds.DWARF_YES.get();
    }

    @Nullable
    protected SoundEvent getTradeUpdatedSound(boolean isYesSound) {
        return isYesSound ? JolCraftSounds.DWARF_YES.get() : JolCraftSounds.DWARF_NO.get();
    }

    @Nullable
    public void playCelebrateSound() {
        this.makeSound(JolCraftSounds.DWARF_YES.get());
    }

    @Nullable
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_FISHERMAN;
    }

    @Nullable
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_FISHERMAN;
    }


    //Spawning
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {return false;}

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData spawnGroupData) {
        DwarfVariant variant = Util.getRandom(DwarfVariant.values(), this.random);
        DwarfBeardColor beard = Util.getRandom(DwarfBeardColor.values(), this.random);
        DwarfEyeColor eye = Util.getRandom(DwarfEyeColor.values(), this.random);
        this.setVariant(variant);
        this.setBeard(beard);
        this.setEye(eye);
        this.setLeftHanded(false);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        DwarfEntity baby = JolCraftEntities.DWARF.get().create(level, EntitySpawnReason.BREEDING);
        DwarfVariant variant = Util.getRandom(DwarfVariant.values(), this.random);
        DwarfBeardColor beard = Util.getRandom(DwarfBeardColor.values(), this.random);
        DwarfEyeColor eye = Util.getRandom(DwarfEyeColor.values(), this.random);
        baby.setVariant(variant);
        baby.setBeard(beard);
        baby.setEye(eye);
        return baby;
    }

    //Randomized traits
    public int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public DwarfVariant getVariant() {
        return DwarfVariant.byId(this.getTypeVariant() & 255);
    }

    public void setVariant(DwarfVariant variant) {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    public int getTypeBeard() {
        return this.entityData.get(BEARD_COLOR);
    }

    public DwarfBeardColor getBeard() {
        return DwarfBeardColor.byId(this.getTypeBeard() & 255);
    }

    public void setBeard(DwarfBeardColor beard) {
        this.entityData.set(BEARD_COLOR, beard.getId() & 255);
    }

    public int getTypeEye() {
        return this.entityData.get(EYE_COLOR);
    }

    public DwarfEyeColor getEye() {
        return DwarfEyeColor.byId(this.getTypeEye() & 255);
    }

    public void setEye(DwarfEyeColor eye) {
        this.entityData.set(EYE_COLOR, eye.getId() & 255);
    }

    //Other
    @Override
    protected int getBaseExperienceReward(ServerLevel p_376688_) {
        return 1 + this.random.nextInt(3);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public void setCustomAttackDamage(double amount) {
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(amount);
        }
    }

    public double getAttackDamage() {
        // Return different values based on held item
        if (this.getMainHandItem().is(JolCraftItems.DEEPSLATE_WARHAMMER.get())) return 16.5D;
        if (this.getMainHandItem().is(JolCraftItems.DEEPSLATE_AXE.get())) return 9.5D;
        if (this.getMainHandItem().is(JolCraftItems.DEEPSLATE_PICKAXE.get())) return 4.5D;
        return  3.0D; // default (unarmed, or whatever else)
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        ItemStack oldStack = this.getItemBySlot(slot);
        super.setItemSlot(slot, stack); // <-- set the item first!
        if (slot == EquipmentSlot.MAINHAND && !ItemStack.matches(oldStack, stack)) {
            // Now update damage using the correct new item
            this.setCustomAttackDamage(this.getAttackDamage());
        }
    }

    @Nullable
    @Override
    public Entity teleport(TeleportTransition p_379715_) {
        this.stopTrading();
        return super.teleport(p_379715_);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        this.stopTrading();
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    protected void stopTrading() {
        this.setTradingPlayer(null);
    }

    protected void addOffersFromItemListings(DwarfMerchantOffers givenMerchantOffers, DwarfTrades.ItemListing[] newTrades, int maxNumbers) {
        ArrayList<DwarfTrades.ItemListing> arraylist = Lists.newArrayList(newTrades);
        int i = 0;

        while (i < maxNumbers && !arraylist.isEmpty()) {
            DwarfMerchantOffer merchantoffer = arraylist.remove(this.random.nextInt(arraylist.size())).getOffer(this, this.random);
            if (merchantoffer != null) {
                givenMerchantOffers.add(merchantoffer);
                i++;
            }
        }
    }

    @Override
    public boolean stillValid(Player p_383034_) {
        return this.getTradingPlayer() == p_383034_ && this.isAlive() && p_383034_.canInteractWithEntity(this, 4.0);
    }


}
