package net.sievert.jolcraft.entity.custom.dwarf.profession;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.entity.ai.goal.*;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteractionHelper;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfMinerEntity extends AbstractDwarfEntity {

    public DwarfMinerEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_PICKAXE.get()));
        this.instanceTrades = createRandomizedMinerTrades();
        this.setProfession(DwarfProfession.MINER);
    }

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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = super.mobInteract(player, hand);
        if (result != InteractionResult.FAIL) return result;
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult bounty = DwarfInteractionHelper.bounty(this, player, hand, itemstack, BountyType.MINER);
        if (bounty != InteractionResult.FAIL) return bounty;
        InteractionResult bountyCrate = DwarfInteractionHelper.bountyCrate(this, player, hand, itemstack, BountyType.MINER);
        if (bountyCrate != InteractionResult.FAIL) return bountyCrate;
        JolCraftSoundHelper.playDwarfNo(this);
        return InteractionResult.FAIL;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.shouldIncreaseLevel() && this.updateMerchantTimer <= 0) {
            if (this.shouldIncreaseLevel()) {
                this.increaseMerchantCareer();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
                JolCraftSoundHelper.playDwarfYes(this);
                this.updateMerchantTimer = 40;
            }
        } else if (this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
        }
    }

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
}


