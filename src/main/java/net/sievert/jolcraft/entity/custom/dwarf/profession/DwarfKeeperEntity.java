package net.sievert.jolcraft.entity.custom.dwarf.profession;

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
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractEntityEntity;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfKeeperEntity extends AbstractEntityEntity {

    public DwarfKeeperEntity(EntityType<? extends AbstractEntityEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.BARLEY.get()));
        this.instanceTrades = createRandomizedKeeperTrades();
        this.setProfession(DwarfProfession.KEEPER);
    }

    @Override
    public boolean canTrade() {
        return true;
    }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_KEEPER.get());
    }

    @Override
    protected int getRequiredTier() {
        return 1;
    }

    @Nullable
    @Override
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_FARMER;
    }

    @Nullable
    @Override
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_FARMER;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FirePanicGoal(this, 1.3));
        this.targetSelector.addGoal(2, new DwarfNonPlayerAlertGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new DwarfAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new DwarfRevengeGoal(this));
        this.goalSelector.addGoal(3, new DwarfTradeWithPlayerGoal(this));
        this.goalSelector.addGoal(4, new DwarfLookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(5, new DwarfBreedGoal(this, 1.0, AbstractEntityEntity.class));
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
        JolCraftSoundHelper.playDwarfNo(this);
        return InteractionResult.FAIL;
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedKeeperTrades() {
        return AbstractEntityEntity.toIntMap(ImmutableMap.of(

                        //Novice
                        1,

                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsForGold(JolCraftItems.BARLEY_SEEDS.get(), 1, 2, 1, 3, 10, 1),

                        },

                        //Apprentice
                        2,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.GoldForItems(JolCraftItems.BARLEY.get(), 15, 22, 10, 25, 1, 2),

                        },

                        //Journeyman
                        3,
                        new DwarfTrades.ItemListing[]{

                                new DwarfTrades.GoldForItems(JolCraftItems.MUFFHORN_FUR.get(), 1, 15, 5, 2, 4),
                                new DwarfTrades.GoldForItems(JolCraftItems.MUFFHORN_MILK_BUCKET.get(), 1, 10, 30, 3, 5),
                        },

                        //Expert
                        4,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.GoldForItems(JolCraftItems.DEEPSLATE_BULBS.get(), 1, 2, 10, 30, 3, 5),

                        },

                        //Master
                        5,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsForGold(Items.BONE_MEAL, 2, 5, 3, 5, 5, 1),
                                new DwarfTrades.ItemsForGold(JolCraftItems.DEEPSLATE_BULBS.get(), 5, 9, 1, 5, 0),
                                new DwarfTrades.ItemsForGold(JolCraftItems.ASGARNIAN_SEEDS.get(), 5, 1, 3, 0),
                                new DwarfTrades.ItemsForGold(JolCraftItems.DUSKHOLD_SEEDS.get(), 5, 1, 3, 0),
                                new DwarfTrades.ItemsForGold(JolCraftItems.KRANDONIAN_SEEDS.get(), 5, 1, 3, 0),
                                new DwarfTrades.ItemsForGold(JolCraftItems.YANILLIAN_SEEDS.get(), 5, 1, 3, 0),
                        }
                )
        );
    }
}

