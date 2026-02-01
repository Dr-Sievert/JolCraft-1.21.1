package net.sievert.jolcraft.world.entity.custom.dwarf.base;

import net.minecraft.Util;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfArtisanEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.EntityData;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.util.dwarf.trade.DwarfMerchant;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.entity.util.dwarf.variation.DwarfBeardColor;
import net.sievert.jolcraft.world.entity.util.dwarf.variation.DwarfEyeColor;
import net.sievert.jolcraft.world.entity.util.dwarf.variation.DwarfVariant;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.entity.util.dwarf.interaction.DwarfInteractionHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractDwarfEntity extends AbstractTradingEntity implements Npc, DwarfMerchant, EntityData {

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

    //General fields

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

    public DwarfProfession getProfession() {
        return DwarfProfession.byId(this.getData(PROFESSION));
    }

    public void setProfession(DwarfProfession profession) {
        this.setData(PROFESSION, profession.getId());
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel level) {
        return 1 + this.random.nextInt(3);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    //Data

    public static final EntityDataAccessor<String> PROFESSION =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.STRING);

    public static final EntityDataAccessor<Integer> CURRENT_ACTION =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> CURRENT_ACTION_SUBTYPE =
            SynchedEntityData.defineId(AbstractDwarfEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PROFESSION, "none");
        builder.define(CURRENT_ACTION, DwarfActionType.IDLE.ordinal());
        builder.define(CURRENT_ACTION_SUBTYPE, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Profession", this.getData(PROFESSION));
        compound.putInt("CurrentAction", this.getData(CURRENT_ACTION));
        compound.putInt("CurrentActionSubtype", this.getData(CURRENT_ACTION_SUBTYPE));
        compound.putInt("PaidTicks", this.paidTicks);
        if (this.paidCause != null) {
            compound.putUUID("PaidCause", this.paidCause);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Profession", 8)) {
            this.setData(PROFESSION, compound.getString("Profession"));
        }
        if (compound.contains("CurrentAction", 3)) {
            this.getEntityData().set(CURRENT_ACTION, compound.getInt("CurrentAction"));
        }
        if (compound.contains("CurrentActionSubtype", 3)) {
            this.getEntityData().set(CURRENT_ACTION_SUBTYPE, compound.getInt("CurrentActionSubtype"));
        }
        this.paidTicks = compound.getInt("PaidTicks");
        this.paidCause = compound.hasUUID("PaidCause") ? compound.getUUID("PaidCause") : null;
    }

    //Attributes

    public static AttributeSupplier.Builder createAttributes() {
        return DwarfArtisanEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.TEMPT_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    //Interact

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        InteractionResult blacklistFilter = DwarfInteractionHelper.blacklistCheck(this, player, itemstack);
        if (blacklistFilter != InteractionResult.FAIL) return blacklistFilter;

        InteractionResult langFilter = DwarfInteractionHelper.languageCheck(this, player);
        if (langFilter != InteractionResult.FAIL) return langFilter;

        InteractionResult repFilter = DwarfInteractionHelper.reputationCheck(this, player, getRequiredTier());
        if (repFilter != InteractionResult.FAIL) return repFilter;

        InteractionResult actionFilter = DwarfInteractionHelper.actionCheck(this, player);
        if (actionFilter != InteractionResult.FAIL) return actionFilter;

        InteractionResult breed = DwarfInteractionHelper.breed(this, player, hand, itemstack);
        if (breed != InteractionResult.FAIL) return breed;

        if(this.isBaby()) return InteractionResult.FAIL;

        if (itemstack.is(JolCraftItems.GOLD_COIN.get()) && this.canBePaid()) {
            this.setPaid(player);
            JolCraftSoundHelper.entity(this, SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.4F);
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
                    return InteractionResult.SUCCESS;
                }
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), this.getVillagerData().getLevel());
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    //Tick

    @Override
    public void tick() {
        super.tick();
        actionHelper.tick(this);
        if (blockCooldownTicks > 0) blockCooldownTicks--;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    JolCraftParticleHelper.spawn(
                            this.level(),
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
                if (!this.level().isClientSide) {
                    JolCraftParticleHelper.spawn(this.level(), ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
                }
            }
        }

        if (this.paidTicks > 0) {
            this.paidTicks--;
            if (this.paidTicks % 30 == 0) {
                this.spawnColoredParticles(1.0F, 0.84F, 0.0F, 1.0F, 3, 0.4D);
            }
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel serverlevel) {

        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        if (!this.isTrading() && this.updateMerchantTimer > 0) {
            this.updateMerchantTimer--;
            if (this.updateMerchantTimer <= 0) {
                if (this.increaseProfessionLevelOnUpdate) {
                    this.increaseMerchantCareer();
                    this.increaseProfessionLevelOnUpdate = false;

                }
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                PlaySound.dwarfYes(this);
            }
        }
        if (this.shouldRestock()) {
            this.restock();
            lastRestockGameTime = this.level().getGameTime();
        }
        super.customServerAiStep(serverlevel);
    }

    //Combat

    public boolean shouldBlock = false;

    public int blockCooldownTicks = 0;

    public boolean canBlock() {
        return blockCooldownTicks == 0;
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

    //Animation

    private static final Map<AbstractDwarfEntity, DwarfRenderState> CLIENT_RENDER_STATES = new WeakHashMap<>();

    public static DwarfRenderState getOrCreateClientRenderState(AbstractDwarfEntity entity) {
        return CLIENT_RENDER_STATES.computeIfAbsent(entity, e -> new DwarfRenderState());
    }

    protected final DwarfActionHelper actionHelper = new DwarfActionHelper();

    public DwarfActionHelper getActionHelper() {
        return this.actionHelper;
    }

    //Pay

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

    // Particle

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

    public void spawnColoredParticles(float r, float g, float b, float scale, int count, double scatter) {
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
            JolCraftParticleHelper.spawn(
                    this.level(),
                    dust,
                    offsetX,
                    offsetY,
                    offsetZ,
                    velocityX,
                    velocityY,
                    velocityZ
            );
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

    //Sound

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

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? 1.5F : 1.0F;
    }

    //Spawn

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {return false;}

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        DwarfVariant variant = Util.getRandom(DwarfVariant.values(), this.random);
        DwarfBeardColor beard = Util.getRandom(DwarfBeardColor.values(), this.random);
        DwarfEyeColor eye = Util.getRandom(DwarfEyeColor.values(), this.random);
        this.setData(VARIANT, variant.getId());
        this.setData(BEARD_COLOR, beard.getId());
        this.setData(EYE_COLOR, eye.getId());
        this.setLeftHanded(false);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }
}
