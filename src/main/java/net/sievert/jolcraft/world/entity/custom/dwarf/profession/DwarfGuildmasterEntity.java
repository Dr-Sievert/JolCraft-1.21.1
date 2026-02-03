package net.sievert.jolcraft.world.entity.custom.dwarf.profession;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.world.entity.custom.ai.goal.dwarf.*;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.DwarfInteractionHelper;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.trade.DwarfTrades;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfGuildmasterEntity extends AbstractDwarfEntity {

    public DwarfGuildmasterEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.CONTRACT_SIGNED.get()));
        this.instanceTrades = createRandomizedGuildmasterTrades();
        this.setProfession(DwarfProfession.GUILDMASTER);
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
    public boolean canReroll() { return false; }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_GUILDMASTER.get());
    }

    @Override
    public boolean neverEndorse() {
        return true;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public float getVoicePitch() {
        return 0.8F;
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
        this.goalSelector.addGoal(4, new DwarfUseItemGoal<>(this, PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING), SoundEvents.PLAYER_BURP, mob -> mob.getHealth() < mob.getMaxHealth(), 300));
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
        ItemStack itemstack = player.getItemInHand(hand);
        boolean client = this.level().isClientSide;

        int tier = DwarvenReputationHelper.getTier(player);
        int desiredLevel = Math.min(tier + 1, 5);
        int currentLevel = this.getVillagerData().getLevel();
        if (currentLevel < desiredLevel && !client) {
            if (this.getOffers().isEmpty()) {
                this.updateTrades();
            }
            for (int i = currentLevel; i < desiredLevel; i++) {
                this.increaseMerchantCareer();
            }
        }

        InteractionResult result = super.mobInteract(player, hand);
        if (result != InteractionResult.FAIL) return result;

        InteractionResult reputationGain = DwarfInteractionHelper.reputationGain(this, player, hand, itemstack);
        if (reputationGain != InteractionResult.FAIL) return reputationGain;

        PlaySound.dwarfNo(this);
        return InteractionResult.FAIL;
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedGuildmasterTrades() {
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(

                        //Novice
                        1,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsForGold(JolCraftItems.REPUTATION_TABLET_0.get(), 15, 1, 5, 0),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_HISTORIAN.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_MERCHANT.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_SCRAPPER.get(), 1, 1, 0, 0.05F)

                        },

                        //Apprentice
                        2,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_BREWMASTER.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_GUARD.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_KEEPER.get(), 1, 1, 0, 0.05F)
                        },

                        //Journeyman
                        3,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_ARTISAN.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_EXPLORER.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_MINER.get(), 1, 1, 0, 0.05F)
                        },

                        //Expert
                        4,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_ARCANIST.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_ALCHEMIST.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_PRIEST.get(), 1, 1, 0, 0.05F)
                        },

                        //Master
                        5,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_BLACKSMITH.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_CHAMPION.get(), 1, 1, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.CONTRACT_SIGNED.get(), 1, 30, JolCraftItems.CONTRACT_SMELTER.get(), 1, 1, 0, 0.05F)
                        }
                )
        );

    }

    @Override
    protected void updateTrades() {
        int level = this.getVillagerData().getLevel();
        if (instanceTrades != null) {
            DwarfTrades.ItemListing[] listings = instanceTrades.get(level);
            if (listings != null) {
                DwarfMerchantOffers offers = this.getOffers();
                for (DwarfTrades.ItemListing listing : listings) {
                    DwarfMerchantOffer offer = listing.getOffer(this, this.random);
                    if (offer != null) {
                        offers.add(offer);
                    }
                }
            }
        }
    }
}

