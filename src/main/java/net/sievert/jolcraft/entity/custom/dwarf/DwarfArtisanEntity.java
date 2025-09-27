package net.sievert.jolcraft.entity.custom.dwarf;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.entity.ai.goal.*;
import net.sievert.jolcraft.entity.ai.goal.dwarf.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfArtisanEntity extends AbstractDwarfEntity {

    public DwarfArtisanEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.DEEPSLATE_CHISEL.get()));
        this.instanceTrades = createRandomizedArtisanTrades();
    }

    //Attributes
    public static AttributeSupplier.Builder createAttributes() {
        return DwarfArtisanEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.TEMPT_RANGE, 16D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    //Behavior
    @Override
    public boolean canTrade() {
        return true;
    }

    @Override
    public ItemStack getSignedContractItem() {
        return new ItemStack(JolCraftItems.CONTRACT_ARTISAN.get());
    }

    @Override
    protected int getRequiredTier() {
        return 2;
    }

    @Override
    public ResourceLocation getProfessionId() {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf_artisan");
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

        // Call parent for all other interactions (contracts, trades, etc)
        return super.mobInteract(player, hand);
    }

    //Trades
    public static Int2ObjectMap<DwarfTrades.ItemListing[]> createRandomizedArtisanTrades() {
        return AbstractDwarfEntity.toIntMap(ImmutableMap.of(
                // Novice
                1, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.GoldForItems(JolCraftItems.AEGISCORE.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.ASHFANG.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.DEEPMARROW.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.EARTHBLOOD.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.EMBERGLASS.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.FROSTVEIN.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.GRIMSTONE.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.IRONHEART.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.LUMIERE.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.MOONSHARD.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.RUSTAGATE.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.SKYBURROW.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.SUNGLEAM.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.VERDANITE.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(JolCraftItems.WOECRYSTAL.get(), 1, 10, 10, 8, 15),
                        new DwarfTrades.GoldForItems(Items.DIAMOND, 1, 10, 5, 8, 15),
                        new DwarfTrades.GoldForItems(Items.EMERALD, 1, 10, 5, 4, 8),
                        new DwarfTrades.GoldForItems(Items.AMETHYST_SHARD, 2, 4, 10, 5, 2, 5),
                        new DwarfTrades.GoldForItems(Items.LAPIS_LAZULI, 3, 5, 10, 5, 2, 5),
                        new DwarfTrades.GoldForItems(Items.PRISMARINE_SHARD, 3, 5, 10, 5, 2, 5),
                        new DwarfTrades.GoldForItems(Items.QUARTZ, 3, 5, 10, 5, 2, 5)


                },
                // Apprentice
                2, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.ItemsForGold(JolCraftBlocks.LAPIDARY_BENCH.get().asItem(), 10, 20, 1, 3, 10),
                },
                // Journeyman
                3, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.ItemsForGold(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get(), 2, 4, 1, 3, 10),
                },
                // Expert
                4, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.ItemsForGold(JolCraftItems.DEEPSLATE_CHISEL.get(), 2, 4, 1, 3, 10),
                },
                // Master
                5, new DwarfTrades.ItemListing[] {
                        new DwarfTrades.ItemsAndGoldToItemsWithData(
                                JolCraftItems.LEGENDARY_PAGE.get(), 20,
                                30,
                                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 1,
                                1, 0, 0F,
                                (stack) -> stack.set(JolCraftDataComponents.LORE_LINE_ID, "ancient_gemcraft")
                        ),
                }
        ));
    }


}

