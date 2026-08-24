package net.sievert.jolcraft.event.game.world.entity.npc;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

public final class JolCraftDwarfEvents {

    private JolCraftDwarfEvents() {}

    public static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof AbstractDwarfEntity dwarf
                && dwarf.canBlock()
                && event.getSource().getEntity() instanceof Monster monster) {
            if (event.getSource().getDirectEntity() instanceof Projectile) {
                dwarf.shouldBlock = true;
                dwarf.blockCooldownTicks = 75;
                event.setInvulnerable(true);
                return;
            }

            if (monster.isWithinMeleeAttackRange(dwarf)) {
                event.setInvulnerable(true);
                dwarf.shouldBlock = true;
                dwarf.blockCooldownTicks = 75;
            }
        }
    }

    public static void onDwarfHostileMobSpawn(FinalizeSpawnEvent event) {
        Mob entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (!(entity instanceof Zombie || entity instanceof Pillager)) return;

        entity.targetSelector.addGoal(
                3,
                new NearestAttackableTargetGoal<>(
                        entity,
                        AbstractDwarfEntity.class,
                        true
                )
        );
    }
}