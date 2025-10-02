package net.sievert.jolcraft.entity.custom.dwarf.profession;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.item.client.compass.DialItemColor;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.util.StructureGroupColorHelper;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.data.util.attachment.DiscoveredStructuresHelper;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfExplorerEntity extends AbstractDwarfEntity {

    public DwarfExplorerEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
        this.instanceTrades = createRandomizedExplorerTrades();
        this.setProfession(DwarfProfession.EXPLORER);
    }

    private int lastUnlockedLevel = 0;

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LastUnlockedLevel", lastUnlockedLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lastUnlockedLevel = tag.getInt("LastUnlockedLevel");
    }

    @Override
    public boolean canTrade() {
        return true;
    }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_EXPLORER.get());
    }

    @Override
    protected int getRequiredTier() {
        return 2;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_CARTOGRAPHER;
    }

    @Nullable
    @Override
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_CARTOGRAPHER;
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
        if (!this.level().isClientSide) {
            int playerScore = DiscoveredStructuresHelper.getDiscoveryScore(player);
            int currentLevel = this.getVillagerData().getLevel();
            int targetLevel = getLevelForScore(playerScore);
            if (targetLevel > currentLevel) {
                this.setVillagerData(this.getVillagerData().setLevel(targetLevel));
                this.updateTrades();
                JolCraftSoundHelper.playDwarfYes(this);
            }
        }
        InteractionResult result = super.mobInteract(player, hand);
        if (result != InteractionResult.FAIL) return result;
        JolCraftSoundHelper.playDwarfNo(this);
        return InteractionResult.FAIL;
    }


    public static final int[] SCORE_THRESHOLDS = { 0, 10, 70, 150, 250 };

    public static int getLevelForScore(int score) {
        for (int i = SCORE_THRESHOLDS.length - 1; i >= 0; i--) {
            if (score >= SCORE_THRESHOLDS[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedExplorerTrades() {
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(
                // Novice
                1, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.ItemsForGold(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(), 5, 10,  1, 3, 0),
                        new DwarfTrades.ItemsAndGoldToItemsWithData(
                                Items.REDSTONE, 1,
                                5,
                                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(), 1,
                                3, 0, 0F,
                                (stack) -> {
                                    String group = "dwarven_structures";
                                    stack.set(JolCraftDataComponents.STRUCTURE_GROUP, group);
                                    stack.set(JolCraftDataComponents.DIAL_COLOR, new DialItemColor(StructureGroupColorHelper.getColor(group)));
                                }
                        ),


                },
                // Apprentice
                2, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.ItemsAndGoldToItemsWithData(
                                Items.REDSTONE, 1,
                                5,
                                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(), 1,
                                3, 0, 0F,
                                (stack) -> {
                                    String group = "ancient_structures";
                                    stack.set(JolCraftDataComponents.STRUCTURE_GROUP, group);
                                    stack.set(JolCraftDataComponents.DIAL_COLOR, new DialItemColor(StructureGroupColorHelper.getColor(group)));
                                }
                        ),
                },
                // Journeyman
                3, new DwarfTrades.ItemListing[] {
                },
                // Expert
                4, new DwarfTrades.ItemListing[] {
                },
                // Master
                5, new DwarfTrades.ItemListing[] {
                }
        ));
    }

    @Override
    protected void updateTrades() {
        int level = this.getVillagerData().getLevel();

        if (instanceTrades == null) return;

        if (lastUnlockedLevel == 0 || this.getOffers().isEmpty()) {
            this.getOffers().clear();
            for (int i = 1; i <= level; i++) {
                DwarfTrades.ItemListing[] listings = instanceTrades.get(i);
                if (listings != null) {
                    for (DwarfTrades.ItemListing trade : listings) {
                        DwarfMerchantOffer offer = trade.getOffer(this, this.random);
                        if (offer != null) {
                            this.getOffers().add(offer);
                        }
                    }
                }
            }
        } else {
            for (int i = lastUnlockedLevel + 1; i <= level; i++) {
                DwarfTrades.ItemListing[] listings = instanceTrades.get(i);
                if (listings != null) {
                    for (DwarfTrades.ItemListing trade : listings) {
                        DwarfMerchantOffer offer = trade.getOffer(this, this.random);
                        if (offer != null) {
                            this.getOffers().add(offer);
                        }
                    }
                }
            }
        }

        lastUnlockedLevel = level;
    }
}

