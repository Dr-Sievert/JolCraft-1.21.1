package net.sievert.jolcraft.entity.custom.animal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IShearable;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.item.JolCraftItems;

import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MuffhornEntity extends Animal implements IShearable {

    private long nextMilkTick = 0;
    private int regrowTicks = 0;

    private static final EntityDimensions BABY_DIMENSIONS = JolCraftEntities.MUFFHORN.get().getDimensions().scale(0.5F).withEyeHeight(0.665F);

    public MuffhornEntity(EntityType<? extends MuffhornEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25,
                stack -> stack.is(JolCraftItems.BARLEY.get()) || stack.is(JolCraftBlocks.BARLEY_BLOCK.get().asItem()),
                false
        ));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    private static final EntityDataAccessor<Boolean> SHEARED =
            SynchedEntityData.defineId(MuffhornEntity.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHEARED, false);
    }

    public boolean isSheared() {
        return this.entityData.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(SHEARED, sheared);
        if (sheared) {
            setRegrowTicks(12000);
        }
    }

    public void setRegrowTicks(int ticks) {
        this.regrowTicks = Math.max(0, ticks);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sheared", this.isSheared());
        tag.putInt("RegrowTicks", this.regrowTicks);
        tag.putLong("NextMilkTick", this.nextMilkTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSheared(tag.getBoolean("Sheared"));
        this.regrowTicks = tag.getInt("RegrowTicks");
        this.nextMilkTick = tag.getLong("NextMilkTick");
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (this.isSheared() && !this.isBaby() && --regrowTicks <= 0) {
                this.setSheared(false);
            }
        }
    }

    @Override
    public boolean isShearable(Player player, ItemStack shears, Level level, BlockPos pos) {
        return !this.isSheared() && !this.isBaby();
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos) {
        this.setSheared(true);
        if (!level.isClientSide()) {
            level.playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 0.7F);
            return List.of(new ItemStack(JolCraftItems.MUFFHORN_FUR.get(), 1 + this.random.nextInt(2)));
        }
        return List.of();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(JolCraftBlocks.BARLEY_BLOCK.get().asItem());

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.TEMPT_RANGE, 16d);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.COW_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COW_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.COW_STEP, 0.3F, 0.7F);
    }

    @Override
    protected void playEatingSound() {
        this.playSound(SoundEvents.HORSE_EAT, 1.0F, this.getVoicePitch());
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby()
                ? 1.2F + this.random.nextFloat() * 0.2F
                : 0.5F + this.random.nextFloat() * 0.15F;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(Items.BUCKET) && !this.isBaby()) {
            if (!this.level().isClientSide) {
                long gameTime = this.level().getGameTime();
                int cooldownTicks = 12000;
                if (gameTime >= this.nextMilkTick) {
                    this.nextMilkTick = gameTime + cooldownTicks;
                    player.playSound(SoundEvents.COW_MILK, 1.0F, 0.9F);
                    ItemStack result = ItemUtils.createFilledResult(itemstack, player, JolCraftItems.MUFFHORN_MILK_BUCKET.get().getDefaultInstance());
                    player.setItemInHand(hand, result);
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (itemstack.is(JolCraftItems.BARLEY.get()) && this.isSheared() && !this.isBaby()) {
            if (!this.level().isClientSide) {
                this.usePlayerItem(player, hand, itemstack);
                this.playEatingSound();
                int reduction = (int) (this.regrowTicks * 0.2F);
                this.setRegrowTicks(this.regrowTicks - reduction);
            }
            return InteractionResult.SUCCESS;
        }
        else {
            return super.mobInteract(player, hand);
        }
    }

    @Nullable
    public MuffhornEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return JolCraftEntities.MUFFHORN.get().create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
    }

}

