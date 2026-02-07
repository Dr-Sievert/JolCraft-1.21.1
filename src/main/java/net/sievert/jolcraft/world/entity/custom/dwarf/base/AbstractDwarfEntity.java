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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.attribute.DwarfAttributes;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.loadout.DwarfLoadouts;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.util.EntityData;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfBeardColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfEyeColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfVariant;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

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
        return this.getMerchantLevel() >= 1;
    }

    public boolean neverEndorse() { return false; }

    public int getRequiredTier() {
        return 0;
    }

    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_SIGNED.get());
    }

    @OnlyIn(Dist.CLIENT)
    private DwarfProfession clientRenderProfession = DwarfProfession.NONE;

    @OnlyIn(Dist.CLIENT)
    public DwarfProfession getClientRenderProfession() {
        return this.clientRenderProfession;
    }

    @OnlyIn(Dist.CLIENT)
    public void setProfessionClient(DwarfProfession profession) {
        this.clientRenderProfession = profession;
    }

    public DwarfProfession getProfession() {
        return DwarfProfession.byId(this.getData(PROFESSION));
    }

    protected DwarfProfession getSpawnProfession() {
        return DwarfProfession.NONE;
    }

    public void setProfession(@Nullable DwarfProfession profession) {
        if (profession == null) profession = DwarfProfession.NONE;

        DwarfProfession current = this.getProfession();
        if (current == profession) {
            if (!this.level().isClientSide) {
                DwarfAttributes.applyTo(this, profession);
            }
            return;
        }

        this.setData(PROFESSION, profession.getId());

        if (!this.level().isClientSide) {
            DwarfAttributes.applyTo(this, profession);
        }
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
        if (!this.level().isClientSide) {
            DwarfAttributes.applyTo(this, this.getProfession());
        }
    }

    //Attributes

    public static AttributeSupplier.Builder createAttributes() {
        return DwarfAttributes.createBase();
    }

    //Interact

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return DwarfInteractions.dispatch(
                new DwarfInteractions.DwarfInteractionContext(
                        this,
                        player,
                        hand,
                        player.getItemInHand(hand),
                        this.level(),
                        this.level().isClientSide
                )
        );
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
                    this.level().addParticle(
                            ParticleTypes.HAPPY_VILLAGER,
                            this.getX() + random.nextGaussian() * 0.2,
                            this.getY() + 0.5 + random.nextDouble(),
                            this.getZ() + random.nextGaussian() * 0.2,
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
                    while (this.shouldIncreaseLevel()) {
                        this.increaseMerchantCareer();
                    }
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

    public static DwarfRenderState getOrCreate(AbstractDwarfEntity entity) {
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
            if (this.level().isClientSide) {
                for (int i = 0; i < 7; i++) {
                    double d0 = this.random.nextGaussian() * 0.02;
                    double d1 = this.random.nextGaussian() * 0.02;
                    double d2 = this.random.nextGaussian() * 0.02;

                    this.level().addParticle(
                            ParticleTypes.HEART,
                            this.getRandomX(1.0),
                            this.getRandomY() + 0.5,
                            this.getRandomZ(1.0),
                            d0, d1, d2
                    );
                }
            }
            return;
        }

        if (id == 19) {
            if (this.level().isClientSide) {
                int rgb = ((int)(1.0F * 255) << 16) | ((int)(0.84F * 255) << 8) | (int)(0.0F * 255);
                DustParticleOptions dust = new DustParticleOptions(rgb, 1.0F);

                Vec3 forward = this.getLookAngle().normalize();
                double baseX = this.getX() + forward.x * 0.6;
                double baseY = this.getY() + 1.8D;
                double baseZ = this.getZ() + forward.z * 0.5;

                int count = 7;
                double scatter = 0.5D;

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
            return;
        }

        super.handleEntityEvent(id);
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        EntitySpawnReason spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {

        if (!this.level().isClientSide) {
            this.setProfession(this.getSpawnProfession());
        }

        DwarfVariant variant = Util.getRandom(DwarfVariant.values(), this.random);
        DwarfBeardColor beard = Util.getRandom(DwarfBeardColor.values(), this.random);
        DwarfEyeColor eye = Util.getRandom(DwarfEyeColor.values(), this.random);
        this.setData(VARIANT, variant.getId());
        this.setData(BEARD_COLOR, beard.getId());
        this.setData(EYE_COLOR, eye.getId());
        this.setLeftHanded(false);

        SpawnGroupData out = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        if (!this.level().isClientSide) {
            DwarfLoadouts.applySpawnLoadout(this, level, difficulty, spawnType, out);
        }

        return out;
    }
}
