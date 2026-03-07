package net.sievert.jolcraft.world.entity.custom.dwarf.goal;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfAttackGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfBlockGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfBreedGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfFollowParentGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfLookAtTradingPlayerGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfNonPlayerAlertGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfRevengeGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfTradeWithPlayerGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfUseItemGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.FirePanicGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfGoals {

    @FunctionalInterface
    public interface Configurator {
        void configure(AbstractDwarfEntity dwarf, Tracker t);
    }

    public static final class Tracker {
        private final AbstractDwarfEntity dwarf;
        private final List<Goal> goals = new ArrayList<>();
        private final List<Goal> targets = new ArrayList<>();

        private Tracker(final AbstractDwarfEntity dwarf) {
            this.dwarf = dwarf;
        }

        public void goal(int priority, Goal goal) {
            this.dwarf.goalSelector.addGoal(priority, goal);
            this.goals.add(goal);
        }

        public void target(int priority, Goal goal) {
            this.dwarf.targetSelector.addGoal(priority, goal);
            this.targets.add(goal);
        }

        private void clearFromSelectors() {
            for (Goal g : this.goals) {
                this.dwarf.goalSelector.removeGoal(g);
            }
            for (Goal g : this.targets) {
                this.dwarf.targetSelector.removeGoal(g);
            }
            this.goals.clear();
            this.targets.clear();
        }
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private static final Map<AbstractDwarfEntity, Tracker> TRACKERS = new WeakHashMap<>();

    private static final Map<DwarfProfession, Configurator> CONFIGURATORS =
            new EnumMap<>(DwarfProfession.class);

    static {
        final Configurator defaultTradingCombat = (dwarf, t) -> {
            t.goal(0, new FloatGoal(dwarf));
            t.goal(1, new FirePanicGoal(dwarf, 1.3));
            t.target(2, new DwarfNonPlayerAlertGoal(dwarf).setAlertOthers());
            t.goal(2, new DwarfAttackGoal(dwarf, 1.2D, true));
            t.goal(3, new DwarfRevengeGoal(dwarf));
            t.goal(3, new DwarfTradeWithPlayerGoal(dwarf));
            t.goal(4, new DwarfLookAtTradingPlayerGoal(dwarf));
            t.goal(5, new DwarfBreedGoal(dwarf, 1.0, AbstractDwarfEntity.class));
            t.goal(6, new TemptGoal(dwarf, 1.25, stack -> stack.is(JolCraftItems.GOLD_COIN), false));
            t.goal(6, new OpenDoorGoal(dwarf, true));
            t.goal(7, new LookAtPlayerGoal(dwarf, Player.class, 6.0F));
            t.goal(7, new WaterAvoidingRandomStrollGoal(dwarf, 1.0));
            t.goal(8, new InteractGoal(dwarf, Player.class, 3.0F, 1.0F));
            t.goal(9, new RandomLookAroundGoal(dwarf));
            t.goal(9, new MoveToBlockGoal(dwarf, 0.8, 8) {
                @Override
                protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                    return level.getBlockState(pos).is(Blocks.COBBLED_DEEPSLATE);
                }
            });
        };

        // NONE: like old base dwarf (adds FollowParent) + alert at prio 2 (same as default)
        CONFIGURATORS.put(DwarfProfession.NONE, (dwarf, t) -> {
            t.goal(0, new FloatGoal(dwarf));
            t.goal(1, new FirePanicGoal(dwarf, 1.3));
            t.target(2, new DwarfNonPlayerAlertGoal(dwarf).setAlertOthers());
            t.goal(2, new DwarfAttackGoal(dwarf, 1.2D, true));
            t.goal(3, new DwarfRevengeGoal(dwarf));
            t.goal(3, new DwarfTradeWithPlayerGoal(dwarf));
            t.goal(4, new DwarfLookAtTradingPlayerGoal(dwarf));
            t.goal(5, new DwarfBreedGoal(dwarf, 1.0, AbstractDwarfEntity.class));
            t.goal(6, new TemptGoal(dwarf, 1.25, stack -> stack.is(JolCraftItems.GOLD_COIN), false));
            t.goal(6, new DwarfFollowParentGoal(dwarf, 1.25));
            t.goal(6, new OpenDoorGoal(dwarf, true));
            t.goal(7, new LookAtPlayerGoal(dwarf, Player.class, 6.0F));
            t.goal(7, new WaterAvoidingRandomStrollGoal(dwarf, 1.0));
            t.goal(8, new InteractGoal(dwarf, Player.class, 3.0F, 1.0F));
            t.goal(9, new RandomLookAroundGoal(dwarf));
            t.goal(9, new MoveToBlockGoal(dwarf, 0.8, 8) {
                @Override
                protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                    return level.getBlockState(pos).is(Blocks.COBBLED_DEEPSLATE);
                }
            });
        });

        // MERCHANT: same as default, but alert is priority 1 (matches old DwarfMerchantEntity)
        CONFIGURATORS.put(DwarfProfession.MERCHANT, (dwarf, t) -> {
            t.goal(0, new FloatGoal(dwarf));
            t.goal(1, new FirePanicGoal(dwarf, 1.3));
            t.target(1, new DwarfNonPlayerAlertGoal(dwarf).setAlertOthers());
            t.goal(2, new DwarfAttackGoal(dwarf, 1.2D, true));
            t.goal(3, new DwarfRevengeGoal(dwarf));
            t.goal(3, new DwarfTradeWithPlayerGoal(dwarf));
            t.goal(4, new DwarfLookAtTradingPlayerGoal(dwarf));
            t.goal(5, new DwarfBreedGoal(dwarf, 1.0, AbstractDwarfEntity.class));
            t.goal(6, new TemptGoal(dwarf, 1.25, stack -> stack.is(JolCraftItems.GOLD_COIN), false));
            t.goal(6, new OpenDoorGoal(dwarf, true));
            t.goal(7, new LookAtPlayerGoal(dwarf, Player.class, 6.0F));
            t.goal(7, new WaterAvoidingRandomStrollGoal(dwarf, 1.0));
            t.goal(8, new InteractGoal(dwarf, Player.class, 3.0F, 1.0F));
            t.goal(9, new RandomLookAroundGoal(dwarf));
            t.goal(9, new MoveToBlockGoal(dwarf, 0.8, 8) {
                @Override
                protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                    return level.getBlockState(pos).is(Blocks.COBBLED_DEEPSLATE);
                }
            });
        });

        // GUILDMASTER: default + healing potion use-item goal at priority 4
        CONFIGURATORS.put(DwarfProfession.GUILDMASTER, (dwarf, t) -> {
            t.goal(0, new FloatGoal(dwarf));
            t.goal(1, new FirePanicGoal(dwarf, 1.3));
            t.target(2, new DwarfNonPlayerAlertGoal(dwarf).setAlertOthers());
            t.goal(2, new DwarfAttackGoal(dwarf, 1.2D, true));
            t.goal(3, new DwarfRevengeGoal(dwarf));
            t.goal(3, new DwarfTradeWithPlayerGoal(dwarf));
            t.goal(4, new DwarfUseItemGoal<>(
                    dwarf,
                    PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING),
                    SoundEvents.PLAYER_BURP,
                    mob -> mob.getHealth() < mob.getMaxHealth(),
                    300
            ));
            t.goal(4, new DwarfLookAtTradingPlayerGoal(dwarf));
            t.goal(5, new DwarfBreedGoal(dwarf, 1.0, AbstractDwarfEntity.class));
            t.goal(6, new TemptGoal(dwarf, 1.25, stack -> stack.is(JolCraftItems.GOLD_COIN), false));
            t.goal(6, new OpenDoorGoal(dwarf, true));
            t.goal(7, new LookAtPlayerGoal(dwarf, Player.class, 6.0F));
            t.goal(7, new WaterAvoidingRandomStrollGoal(dwarf, 1.0));
            t.goal(8, new InteractGoal(dwarf, Player.class, 3.0F, 1.0F));
            t.goal(9, new RandomLookAroundGoal(dwarf));
            t.goal(9, new MoveToBlockGoal(dwarf, 0.8, 8) {
                @Override
                protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                    return level.getBlockState(pos).is(Blocks.COBBLED_DEEPSLATE);
                }
            });
        });

        // GUARD: matches old DwarfGuardEntity
        CONFIGURATORS.put(DwarfProfession.GUARD, (dwarf, t) -> {
            t.goal(0, new FloatGoal(dwarf));
            t.goal(1, new DwarfBlockGoal(dwarf));
            t.goal(2, new DwarfAttackGoal(dwarf, 1.2D, true));
            t.goal(3, new DwarfRevengeGoal(dwarf));
            t.goal(4, new DwarfUseItemGoal<>(
                    dwarf,
                    PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING),
                    SoundEvents.PLAYER_BURP,
                    mob -> mob.getHealth() < mob.getMaxHealth(),
                    300
            ));
            t.goal(5, new DwarfBreedGoal(dwarf, 1.0, AbstractDwarfEntity.class));
            t.goal(6, new TemptGoal(dwarf, 1.25, stack -> stack.is(JolCraftItems.GOLD_COIN), false));
            t.goal(7, new OpenDoorGoal(dwarf, true));
            t.goal(8, new LookAtPlayerGoal(dwarf, Player.class, 6.0F));
            t.goal(9, new WaterAvoidingRandomStrollGoal(dwarf, 1.0));
            t.goal(10, new RandomLookAroundGoal(dwarf));
            t.goal(10, new MoveToBlockGoal(dwarf, 0.8, 8) {
                @Override
                protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                    return level.getBlockState(pos).is(Blocks.COBBLED_DEEPSLATE);
                }
            });

            t.target(1, new DwarfNonPlayerAlertGoal(dwarf).setAlertOthers());
            t.target(2, new NearestAttackableTargetGoal<>(dwarf, Raider.class, false));
            t.target(2, new NearestAttackableTargetGoal<>(dwarf, AbstractSkeleton.class, false));
            t.target(2, new NearestAttackableTargetGoal<>(dwarf, Zombie.class, false));
            t.target(2, new NearestAttackableTargetGoal<>(dwarf, AbstractPiglin.class, false));
        });

        // Everything else currently matches "default"
        for (DwarfProfession p : DwarfProfession.values()) {
            if (!CONFIGURATORS.containsKey(p)) {
                CONFIGURATORS.put(p, defaultTradingCombat);
            }
        }
    }

    private DwarfGoals() {}

    // -------------------------------------------------------------------------
    // API
    // -------------------------------------------------------------------------

    /**
     * Called by the entity's {@code registerGoals()}.
     * This should do a clean build for the current profession.
     */
    public static void registerGoals(AbstractDwarfEntity dwarf) {
        rebuildGoals(dwarf, dwarf.getProfession());
    }

    /**
     * Called when profession changes / is assigned on the server.
     * Removes previously-added goals (only those we added) and applies the new set.
     */
    public static void rebuildGoals(AbstractDwarfEntity dwarf, DwarfProfession profession) {
        Objects.requireNonNull(dwarf, "dwarf");

        Tracker tracker = TRACKERS.get(dwarf);
        if (tracker == null) {
            tracker = new Tracker(dwarf);
            TRACKERS.put(dwarf, tracker);
        } else {
            tracker.clearFromSelectors();
        }

        Configurator configurator = CONFIGURATORS.get(profession);
        if (configurator == null) {
            configurator = CONFIGURATORS.get(DwarfProfession.NONE);
        }
        if (configurator != null) {
            configurator.configure(dwarf, tracker);
        }
    }
}