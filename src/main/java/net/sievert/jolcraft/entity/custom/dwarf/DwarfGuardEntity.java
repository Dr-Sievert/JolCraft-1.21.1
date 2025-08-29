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
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.JolCraftSoundHelper;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.util.dwarf.trade.DwarfTrades;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfGuardEntity extends AbstractDwarfEntity {

    public DwarfGuardEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.instanceTrades = createRandomizedGuardTrades();
    }

    //Attributes
    public static AttributeSupplier.Builder createAttributes() {
        return DwarfGuardEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.TEMPT_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    //Core
    @Override
    public boolean canTrade() {
        // Only allow trading at master level (level 5)
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
    public ResourceLocation getProfessionId() {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_guard");
    }

    @Override
    public float getVoicePitch() {
        return 0.7F; // deeper voice for guards
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new DwarfBlockGoal(this));
        this.goalSelector.addGoal(2, new DwarfAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new DwarfRevengeGoal(this));
        this.goalSelector.addGoal(4, new DwarfUseItemGoal<>(this, PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING), SoundEvents.PLAYER_BURP, mob -> mob.getHealth() < mob.getMaxHealth(), 300));
        this.goalSelector.addGoal(5, new DwarfBreedGoal(this, 1.0, AbstractDwarfEntity.class));
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
        ItemStack stack = player.getItemInHand(hand);
        boolean client = this.level().isClientSide;

        // 🧠 Language check
        InteractionResult langCheck = this.languageCheck(player);
        if (langCheck != InteractionResult.SUCCESS) {
            return langCheck;
        }

        // Reputation check
        InteractionResult repCheck = this.reputationCheck(player, getRequiredTier());
        if (repCheck != InteractionResult.SUCCESS) {
            return repCheck;
        }

        // 1. Only custom-guard logic: armor hand-in
        EquipmentSlot slot = getSlotForArmor(stack);
        if (!isPerformingAction() && slot != null && this.getItemBySlot(slot).isEmpty()) {
            ItemStack armorCopy = stack.copyWithCount(1);
            ItemStack prevMain = this.getMainHandItem().copy();
            if (!client) {
                this.setItemSlot(EquipmentSlot.MAINHAND, armorCopy);
                JolCraftSoundHelper.playDwarfYes(this);
            }
            // Always call beginAction on BOTH SIDES
            beginAction(player, 40, ACTION_PROFESSION, armorCopy, prevMain, () -> {
                this.setInspecting(false);
                this.setItemSlot(slot, armorCopy);
                this.level().playSound(null, blockPosition(), JolCraftSounds.ARMOR_EQUIP_DEEPSLATE.get(), SoundSource.NEUTRAL, 1.0F, 1.05F);
                this.setItemSlot(EquipmentSlot.MAINHAND, JolCraftItems.DEEPSLATE_AXE.get().getDefaultInstance());
                if (!client) {
                    this.increaseMerchantCareer();
                    this.updateMerchantTimer = 40; // Block repeated hand-ins

                    if (this.currentActionPlayer != null) {
                        int newLevel = getVillagerData().getLevel();
                        Component rank = Component.translatable("merchant.level." + newLevel);
                        this.currentActionPlayer.displayClientMessage(
                                Component.translatable("tooltip.jolcraft.guard.promotion", rank)
                                        .withStyle(ChatFormatting.GRAY),
                                true
                        );
                    }
                    // Only shrink item on server and after successful promotion
                    if(!player.isCreative()){
                        player.getItemInHand(hand).shrink(1);
                    }
                }
            });
            return InteractionResult.SUCCESS;
        }



        // 2. Already has this armor slot? No!
        if (slot != null && !this.getItemBySlot(slot).isEmpty()) {
            if (!client)
                JolCraftSoundHelper.playDwarfNo(this);
            return InteractionResult.SUCCESS;
        }

        // 3. Guard-specific trade logic (if needed) — otherwise delete this section
        if (canTrade() && stack.isEmpty() && !this.isBaby() && (!player.getAbilities().instabuild || player.getInventory().getSelected().isEmpty())) {
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

        // 4. All other logic (contracts, tablets, promotions, endorsements, etc) goes to abstract!
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        // --- Guard Level-Up Tick Logic ---
        if (this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
            if (this.updateMerchantTimer == 0) {
                // Level-up finished: play effects (optional)
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                JolCraftSoundHelper.playDwarfYes(this);
            }
        }

        super.aiStep();
    }


    // Helper for armor slot
    @Nullable
    private EquipmentSlot getSlotForArmor(ItemStack stack) {
        if (stack.is(JolCraftItems.DEEPSLATE_HELMET.get())) return EquipmentSlot.HEAD;
        if (stack.is(JolCraftItems.DEEPSLATE_CHESTPLATE.get())) return EquipmentSlot.CHEST;
        if (stack.is(JolCraftItems.DEEPSLATE_LEGGINGS.get())) return EquipmentSlot.LEGS;
        if (stack.is(JolCraftItems.DEEPSLATE_BOOTS.get())) return EquipmentSlot.FEET;
        return null;
    }

    //Trades
    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedGuardTrades() {
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(
                        //Master
                        5,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.GoldForItems(JolCraftItems.AEGISCORE.get(), 1, 1, 0, 30),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.AEGISCORE.get(), 1, 30, JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), 1, 1, 0, 0.05F)
                        }
                )
        );
    }

    //Spawning
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (this.random.nextBoolean()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_AXE.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_WARHAMMER.get()));
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // Suppress if blocking
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


}