package net.sievert.jolcraft.entity.custom.dwarf;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
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
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionHelper;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.data.DwarfData;
import net.sievert.jolcraft.network.packet.S2C.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.gui.custom.dwarf.DwarfMerchantMenu;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchant;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfBeardColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfEyeColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfVariant;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteractionHelper;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractDwarfEntity extends AgeableMob implements Npc, DwarfMerchant, DwarfData {

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
    }

    private static final Map<AbstractDwarfEntity, DwarfRenderState> CLIENT_RENDER_STATES = new WeakHashMap<>();

    public static DwarfRenderState getOrCreateClientRenderState(AbstractDwarfEntity entity) {
        return CLIENT_RENDER_STATES.computeIfAbsent(entity, e -> new DwarfRenderState());
    }

    protected final DwarfActionHelper actionHelper = new DwarfActionHelper();

    public DwarfActionHelper getActionHelper() {
        return this.actionHelper;
    }

    public static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BEARD_COLOR =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> EYE_COLOR =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> CURRENT_ACTION =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> CURRENT_ACTION_SUBTYPE =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.VILLAGER_DATA);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(BEARD_COLOR, 0);
        builder.define(EYE_COLOR, 0);
        builder.define(CURRENT_ACTION, DwarfActionType.IDLE.ordinal());
        builder.define(CURRENT_ACTION_SUBTYPE, -1);
        builder.define(DATA_VILLAGER_DATA, new VillagerData(
                VillagerType.PLAINS,
                VillagerProfession.NONE,
                1));
    }

    @Override
    public <T> void setData(EntityDataAccessor<T> accessor, T value) {
        this.entityData.set(accessor, value);
    }

    @Override
    public <T> T getData(EntityDataAccessor<T> accessor) {
        return this.entityData.get(accessor);
    }

    public boolean shouldBlock = false;
    public int blockCooldownTicks = 0;
    public boolean canBlock() {
        return blockCooldownTicks == 0;
    }

    @Override
    public void tick() {
        super.tick();
        actionHelper.tick(this);
        if (blockCooldownTicks > 0) blockCooldownTicks--;
        if (!this.level().isClientSide()) {
            System.out.println("Spawning test heart particle at " + this.blockPosition());
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.HEART,
                    this.getX(), this.getY() + 2, this.getZ(), 0, 0, 0);
        }
    }

    //Old

    @Nullable
    private Player tradingPlayer;
    @Nullable
    protected DwarfMerchantOffers offers;

    //Behavior

    public boolean canSign() {
        return true;
    }

    public boolean canEndorse() {
        return this.getVillagerData().getLevel() >= 1;
    }

    public boolean neverEndorse() { return false; }

    protected int getRequiredTier() {
        return 0;
    }

    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_SIGNED.get());
    }

    public ResourceLocation getProfessionId() {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "none");
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        InteractionResult langFilter = DwarfInteractionHelper.languageCheck(this, player);
        if (langFilter != InteractionResult.SUCCESS) return langFilter;

        InteractionResult repFilter = DwarfInteractionHelper.reputationCheck(this, player, getRequiredTier());
        if (repFilter != InteractionResult.SUCCESS) return repFilter;

        InteractionResult blacklistFilter = DwarfInteractionHelper.blacklistCheck(this, player, itemstack);
        if (blacklistFilter != InteractionResult.SUCCESS) return blacklistFilter;

        InteractionResult actionFilter = DwarfInteractionHelper.actionCheck(this, player);
        if (actionFilter != InteractionResult.SUCCESS) return actionFilter;

        InteractionResult breed = DwarfInteractionHelper.breed(this, player, hand, itemstack);
        if (breed != InteractionResult.FAIL) return breed;

        if(this.isBaby()) return InteractionResult.FAIL;

        if (itemstack.is(JolCraftItems.GOLD_COIN.get()) && this.canBePaid()) {
            this.setPaid(player);
            this.level().playSound(null, this.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.4F);
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }

        InteractionResult sign = DwarfInteractionHelper.sign(this, player, hand, itemstack);
        if (sign != InteractionResult.FAIL) return sign;

        InteractionResult promote = DwarfInteractionHelper.promote(this, player, hand, itemstack);
        if (promote != InteractionResult.FAIL) return promote;

        InteractionResult endorse = DwarfInteractionHelper.endorse(this, player, hand, itemstack);
        if (endorse != InteractionResult.FAIL) return endorse;

        if (canTrade() && itemstack.isEmpty() && (!player.getAbilities().instabuild || player.getInventory().getSelected().isEmpty()))
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

        return InteractionResult.FAIL;
    }

    @Override
    protected void customServerAiStep(ServerLevel serverlevel) {

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
            lastRestockGameTime = this.level().getGameTime();
        }
        super.customServerAiStep(serverlevel);
    }

    @Override
    public void aiStep() {
        super.aiStep();

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

        if (this.paidTicks > 0) {
            this.paidTicks--;
            if (this.paidTicks % 30 == 0) {
                this.spawnColoredParticles(1.0F, 0.84F, 0.0F, 1.0F, 3, 0.4D);
            }
        }
    }

    //Breeding
    protected int inLove;
    @Nullable
    protected UUID loveCause;

    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.BREAD);
    }

    public void playEatingSound() {
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

    public void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        if (player.level().isClientSide) return;
        if (player.isCreative()) return;
        if (stack.isEmpty() || stack.getCount() == 0) return;
        int initialCount = stack.getCount();
        UseRemainder useRemainder = stack.get(DataComponents.USE_REMAINDER);
        stack.consume(1, player);
        if (useRemainder != null) {
            ItemStack remainderStack = useRemainder.convertIntoRemainder(
                    stack,
                    initialCount,
                    false,
                    player::handleExtraItemsCreatedOnUse
            );
            player.setItemInHand(hand, remainderStack);
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

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CurrentAction", this.getEntityData().get(CURRENT_ACTION));
        compound.putInt("CurrentActionSubtype", this.getEntityData().get(CURRENT_ACTION_SUBTYPE));
        compound.putInt("InLove", this.inLove);
        compound.putInt("Variant", this.getTypeVariant());
        compound.putInt("Beard", this.getTypeBeard());
        compound.putInt("Eye", this.getTypeEye());
        if (this.loveCause != null) {
            compound.putUUID("LoveCause", this.loveCause);
        }
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
        this.paidTicks = compound.getInt("PaidTicks");
        this.paidCause = compound.hasUUID("PaidCause") ? compound.getUUID("PaidCause") : null;
        setData(VARIANT, compound.getInt("Variant"));
        setData(BEARD_COLOR, compound.getInt("Beard"));
        setData(EYE_COLOR, compound.getInt("Eye"));
        this.setAge(compound.getInt("Age"));
        this.forcedAge = compound.getInt("ForcedAge");
        if (compound.contains("VillagerData", 10)) {
            VillagerData.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("VillagerData"))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(data -> setData(DATA_VILLAGER_DATA, data));
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
        if (compound.contains("CurrentAction", 3)) {
            this.getEntityData().set(CURRENT_ACTION, compound.getInt("CurrentAction"));
        }
        if (compound.contains("CurrentActionSubtype", 3)) {
            this.getEntityData().set(CURRENT_ACTION_SUBTYPE, compound.getInt("CurrentActionSubtype"));
        }
    }

    //Paying
    protected int paidTicks;
    @Nullable
    protected UUID paidCause;
    public static final int MAX_PAID_TICKS = 20 * 60;

    public boolean needsPay() {
        return this.paidTicks <= 0;
    }

    public void setPaid(@Nullable Player player) {
        this.paidTicks = MAX_PAID_TICKS;
        if (player != null) {
            this.paidCause = player.getUUID();
        }
        this.level().broadcastEntityEvent(this, (byte)19);
    }

    public void resetPaid() {
        this.paidTicks = 0;
        this.paidCause = null;
    }

    public boolean canBePaid() {
        return this.paidTicks <= 0;
    }

    @Nullable
    public ServerPlayer getPaidCause() {
        if (this.paidCause == null) return null;
        Player player = this.level().getPlayerByUUID(this.paidCause);
        return player instanceof ServerPlayer ? (ServerPlayer)player : null;
    }

    // Particles
    public void spawnColoredParticles(float r, float g, float b, float scale, int count, double scatter) {
        if (!this.level().isClientSide()) return;

        int rgb = ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        DustParticleOptions dust = new DustParticleOptions(rgb, scale);

        Vec3 forward = this.getLookAngle().normalize();
        double baseX = this.getX() + forward.x * 0.6;
        double baseY = this.getY() + 1.8D;
        double baseZ = this.getZ() + forward.z * 0.5;

        for (int i = 0; i < count; i++) {
            double offsetX = baseX + (this.random.nextDouble() - 0.5D) * scatter;
            double offsetY = baseY + (this.random.nextDouble() - 0.5D) * scatter;
            double offsetZ = baseZ + (this.random.nextDouble() - 0.5D) * scatter;

            double velocityX = (this.random.nextDouble() - 0.5D) * 0.1D;
            double velocityY = this.random.nextDouble() * 0.1D;
            double velocityZ = (this.random.nextDouble() - 0.5D) * 0.1D;

            this.level().addParticle(dust, offsetX, offsetY, offsetZ, velocityX, velocityY, velocityZ);
        }
    }

    //Loot

    private boolean isSpecialDropItem(ItemStack stack) {
        return stack.is(JolCraftItems.CONTRACT_WRITTEN.get()) || stack.is(JolCraftItems.CONTRACT_SIGNED.get()) || stack.is(JolCraftItems.BOUNTY.get()) || stack.is(JolCraftItems.BOUNTY_CRATE.get());
    }

    private boolean shouldDropEquipment(ItemStack stack) {
        return stack.is(Items.DIAMOND);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        ItemStack mainHand = this.getMainHandItem();

        if (!mainHand.isEmpty()) {
            if (isSpecialDropItem(mainHand)) {
                this.spawnAtLocation(level, mainHand);
            } else if (shouldDropEquipment(mainHand)) {
                this.spawnAtLocation(level, mainHand);
            }
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
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
        if (this.level().isClientSide) return;
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
        return getData(DATA_VILLAGER_DATA);
    }

    public void setVillagerData(VillagerData villagerData) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != villagerData.getProfession()) {
            this.offers = null;
        }

        setData(DATA_VILLAGER_DATA, villagerData);
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
            this.level().playSound(null, this.blockPosition(), Objects.requireNonNull(getRestockSound()), SoundSource.NEUTRAL, 1.2F, 1.0F);
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
        this.level().playSound(null, this.blockPosition(), Objects.requireNonNull(getRerollSound()), SoundSource.NEUTRAL, 1.2F, 1.0F);
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


    public void increaseMerchantCareer() {
        if (this.level().isClientSide) return;
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
                                level,
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
        if (this.level().isClientSide) return;
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
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
        assert baby != null;
        baby.setVariant(variant);
        baby.setBeard(beard);
        baby.setEye(eye);
        return baby;
    }

    //Randomized traits
    public int getTypeVariant() {
        return getData(VARIANT);
    }

    public DwarfVariant getVariant() {
        return DwarfVariant.byId(getTypeVariant() & 255);
    }

    public void setVariant(DwarfVariant variant) {
        setData(VARIANT, variant.getId() & 255);
    }

    public int getTypeBeard() {
        return getData(BEARD_COLOR);
    }

    public DwarfBeardColor getBeard() {
        return DwarfBeardColor.byId(getTypeBeard() & 255);
    }

    public void setBeard(DwarfBeardColor beard) {
        setData(BEARD_COLOR, beard.getId() & 255);
    }

    public int getTypeEye() {
        return getData(EYE_COLOR);
    }

    public DwarfEyeColor getEye() {
        return DwarfEyeColor.byId(getTypeEye() & 255);
    }

    public void setEye(DwarfEyeColor eye) {
        setData(EYE_COLOR, eye.getId() & 255);
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
            Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(amount);
        }
    }

    public double getAttackDamage() {
        if (this.getMainHandItem().is(JolCraftItems.DEEPSLATE_WARHAMMER.get())) return 16.5D;
        if (this.getMainHandItem().is(JolCraftItems.DEEPSLATE_AXE.get())) return 9.5D;
        if (this.getMainHandItem().is(JolCraftItems.DEEPSLATE_PICKAXE.get())) return 4.5D;
        return  3.0D;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        ItemStack oldStack = this.getItemBySlot(slot);
        super.setItemSlot(slot, stack);
        if (slot == EquipmentSlot.MAINHAND && !ItemStack.matches(oldStack, stack)) {
            this.setCustomAttackDamage(this.getAttackDamage());
        }
    }

    @Nullable
    @Override
    public Entity teleport(TeleportTransition teleportTransition) {
        this.stopTrading();
        return super.teleport(teleportTransition);
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
        if (this.level().isClientSide) return;
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
    public boolean stillValid(Player player) {
        return this.getTradingPlayer() == player && this.isAlive() && player.canInteractWithEntity(this, 4.0);
    }

    public void restockBountiesOnly() {
        if (this.level().isClientSide) return;
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
