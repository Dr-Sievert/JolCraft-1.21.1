package net.sievert.jolcraft.entity.custom.dwarf.profession;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteractionHelper;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityGuardEntity extends AbstractEntityEntity {

    public EntityGuardEntity(EntityType<? extends AbstractEntityEntity> entityType, Level level) {
        super(entityType, level);
        this.instanceTrades = createRandomizedGuardTrades();
        this.setProfession(DwarfProfession.GUARD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGuardEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.TEMPT_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public boolean canTrade() {
        return this.getVillagerData().getLevel() == 5;
    }

    @Override
    public boolean canReroll(){ return false; }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_GUARD.get());
    }

    @Override
    protected int getRequiredTier() {
        return 1;
    }

    @Override
    public float getVoicePitch() {
        return 0.7F;
    }


    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.isBlocking()) {
            return null;
        }
        return JolCraftSounds.DWARF_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_WEAPONSMITH;
    }

    @Nullable
    @Override
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_WEAPONSMITH;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new DwarfBlockGoal(this));
        this.goalSelector.addGoal(2, new DwarfAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new DwarfRevengeGoal(this));
        this.goalSelector.addGoal(4, new DwarfUseItemGoal<>(this, PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING), SoundEvents.PLAYER_BURP, mob -> mob.getHealth() < mob.getMaxHealth(), 300));
        this.goalSelector.addGoal(5, new DwarfBreedGoal(this, 1.0, AbstractEntityEntity.class));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.25, stack -> stack.is(JolCraftItems.GOLD_COIN), false));
        this.goalSelector.addGoal(7, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new MoveToBlockGoal(this, 0.8, 8) {
            @Override
            protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                return level.getBlockState(pos).is(Blocks.COBBLED_DEEPSLATE);
            }
        });
        this.targetSelector.addGoal(1, new DwarfNonPlayerAlertGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Raider.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Zombie.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, false));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = super.mobInteract(player, hand);
        if (result != InteractionResult.FAIL) return result;
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult guardEquip = DwarfInteractionHelper.guardEquip(this, player, hand, itemstack);
        if (guardEquip != InteractionResult.FAIL) return guardEquip;
        JolCraftSoundHelper.playDwarfNo(this);
        return InteractionResult.FAIL;
    }

    @Override
    public void aiStep() {
        if (this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
            if (this.updateMerchantTimer == 0) {
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                JolCraftSoundHelper.playDwarfYes(this);
            }
        }
        super.aiStep();
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedGuardTrades() {
        return AbstractEntityEntity.toIntMap(ImmutableMap.of(
                        //Master
                        5,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.GoldForItems(JolCraftItems.AEGISCORE.get(), 1, 1, 0, 30),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.AEGISCORE.get(), 1, 30, JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), 1, 1, 0, 0.05F)
                        }
                )
        );
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (this.random.nextBoolean()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_AXE.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_WARHAMMER.get()));
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }
}