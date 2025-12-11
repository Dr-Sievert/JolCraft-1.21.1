package net.sievert.jolcraft.entity.custom.dwarf.profession;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
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
import net.sievert.jolcraft.data.JolCraftComponents;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfHistorianEntity extends AbstractDwarfEntity {

    public DwarfHistorianEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.DWARVEN_TOME.get()));
        this.instanceTrades = createRandomizedHistorianTrades();
        this.setProfession(DwarfProfession.HISTORIAN);
    }

    @Override
    public boolean canTrade() {
        return true;
    }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_HISTORIAN.get());
    }

    @Override
    public float getVoicePitch() {
        return 1.1F;
    }

    @Nullable
    @Override
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_LIBRARIAN;
    }

    @Nullable
    @Override
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_LIBRARIAN;
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
        InteractionResult result = super.mobInteract(player, hand);
        if (result != InteractionResult.FAIL) return result;
        JolCraftSoundHelper.playDwarfNo(this);
        return InteractionResult.FAIL;
    }

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedHistorianTrades() {
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(


                        //Novice
                        1,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.GoldForItems(JolCraftItems.DWARVEN_TOME_COMMON.get(), 1, 10, 5, 3),
                                new DwarfTrades.GoldForItems(JolCraftItems.DWARVEN_TOME_UNCOMMON.get(), 1, 10, 35, 6),
                                new DwarfTrades.GoldForItems(JolCraftItems.DWARVEN_TOME_RARE.get(), 1, 10, 75, 10),
                                new DwarfTrades.GoldForItems(JolCraftItems.DWARVEN_TOME_EPIC.get(), 1, 10, 125, 22),
                                new DwarfTrades.ItemsForGold(JolCraftItems.PARCHMENT.get(), 1, 2, 1, 3, 6, 1),
                        },

                        //Apprentice
                        2,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.GoldForItems(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), 1, 10, 5, 6),
                                new DwarfTrades.GoldForItems(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), 1, 10, 35, 8),
                                new DwarfTrades.GoldForItems(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), 1, 10, 75, 14),
                                new DwarfTrades.GoldForItems(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), 1, 10, 125, 28),
                                new DwarfTrades.GoldForItems(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 1, 10, 250, 35),
                                new DwarfTrades.ItemsForGold(JolCraftItems.CONTRACT_BLANK.get(), 2, 4, 1, 2, 5, 1),
                        },

                        //Journeyman
                        3,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsForGold(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get(), 8, 1, 3, 1),
                                new DwarfTrades.ItemsForGold(JolCraftItems.QUILL_EMPTY.get(), 1, 3, 1, 2, 6, 1)

                        },

                        //Expert
                        4,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsForGold(JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), 13, 1, 3, 1),
                                new DwarfTrades.ItemsForGold(Items.INK_SAC, 3, 6, 1, 2, 6, 1)
                        },

                        //Master
                        5,
                        new DwarfTrades.ItemListing[]{
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON, 1, 1, JolCraftItems.LEGENDARY_PAGE.get(), 1, 100, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON, 1, 2, JolCraftItems.LEGENDARY_PAGE.get(), 2, 100, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE, 1, 3, JolCraftItems.LEGENDARY_PAGE.get(), 3, 100, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC, 1, 4, JolCraftItems.LEGENDARY_PAGE.get(), 4, 100, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY, 1, 5, JolCraftItems.LEGENDARY_PAGE.get(), 5, 100, 0, 0.05F),
                                new DwarfTrades.ItemsAndGoldToItems(JolCraftItems.LEGENDARY_PAGE, 10, 15, JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get(), 1, 10, 0, 0.05F),

                                new DwarfTrades.ItemsAndGoldToItemsWithData(
                                        JolCraftItems.LEGENDARY_PAGE.get(), 20,
                                        30,
                                        JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 1,
                                        1, 1, 0F,
                                        (stack) -> stack.set(JolCraftComponents.LORE_KEY, "mithril_forge_technique")
                                ),

                                new DwarfTrades.ItemsAndGoldToItemsWithData(
                                        JolCraftItems.LEGENDARY_PAGE.get(), 20,
                                        30,
                                        JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 1,
                                        1, 1, 0F,
                                        (stack) -> stack.set(JolCraftComponents.LORE_KEY, "coin_press_manual")
                                )

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