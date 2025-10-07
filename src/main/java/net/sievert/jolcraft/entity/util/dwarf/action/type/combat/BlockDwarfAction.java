package net.sievert.jolcraft.entity.util.dwarf.action.type.combat;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.network.util.JolCraftParticleHelper;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

/**
 * Action for handling dwarf blocking behavior and associated particle effects.
 */
public class BlockDwarfAction implements DwarfAction {

    protected AbstractDwarfEntity dwarf;
    private int ticksRemaining = 0;

    public BlockDwarfAction (AbstractDwarfEntity dwarf){
        this.dwarf = dwarf;
    }

    @Override
    public DwarfActionType getType() {
        return DwarfActionType.BLOCK;
    }

    @Override
    public void start() {
        this.ticksRemaining = 15;
        this.spawnBlockParticles(dwarf);
        dwarf.level().playSound(null, dwarf.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    private void spawnBlockParticles(AbstractDwarfEntity dwarf) {
        float yawDeg = dwarf.yBodyRot;
        float yawRad = yawDeg * Mth.DEG_TO_RAD;

        Vec3 forward = new Vec3(-Mth.sin(yawRad), 0.0D, Mth.cos(yawRad)).normalize();
        Vec3 left = new Vec3(-forward.z, 0.0D, forward.x).normalize();

        double px = dwarf.getX() + forward.x + left.x * (-0.4D);
        double py = dwarf.getY() + 1.2D;
        double pz = dwarf.getZ() + forward.z + left.z * (-0.4D);

        DustParticleOptions dust = new DustParticleOptions(-2233622, 0.5F);

        for (int i = 0; i < 5; i++) {
            double scatter = 0.15D;

            double ox = px + (dwarf.getRandom().nextDouble() - 0.5D) * 2.0D * scatter;
            double oy = py + (dwarf.getRandom().nextDouble() - 0.5D) * 2.0D * scatter;
            double oz = pz + (dwarf.getRandom().nextDouble() - 0.5D) * 2.0D * scatter;

            double vx = (dwarf.getRandom().nextDouble() - 0.5D) * 0.1D;
            double vy = dwarf.getRandom().nextDouble() * 0.1D;
            double vz = (dwarf.getRandom().nextDouble() - 0.5D) * 0.1D;

            JolCraftParticleHelper.sendParticle(
                    dwarf.level(),
                    dwarf.blockPosition(),
                    dust,
                    false, false,
                    ox, oy, oz,
                    vx, vy, vz,
                    32.0D
            );
        }
    }
}
