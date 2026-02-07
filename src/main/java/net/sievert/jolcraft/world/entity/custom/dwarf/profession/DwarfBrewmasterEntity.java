package net.sievert.jolcraft.world.entity.custom.dwarf.profession;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.world.entity.custom.ai.goal.dwarf.*;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfTrades;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfBrewmasterEntity extends AbstractDwarfEntity {

    public DwarfBrewmasterEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.GLASS_MUG.get()));
    }

    @Override
    protected DwarfProfession getSpawnProfession() {
        return DwarfProfession.BREWMASTER;
    }

    @Override
    public boolean canTrade() { return true; }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_BREWMASTER.get());
    }

    @Override
    protected int getRequiredTier() {
        return 1;
    }

    @Nullable
    @Override
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_CLERIC;
    }

    @Nullable
    @Override
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_CLERIC;
    }

    @Override
    public float getVoicePitch() { return 0.9F; }

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
        PlaySound.dwarfNo(this);
        return InteractionResult.FAIL;
    }
}
