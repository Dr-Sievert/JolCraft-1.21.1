package net.sievert.jolcraft.entity.util.dwarf.action.type;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

/**
 * Action for handling dwarf blocking behavior and associated particle effects.
 * Handles its own particle timers and resets when finished.
 */
public class BlockDwarfAction implements DwarfAction {

    private Vec3 blockParticlePos = null;
    private int blockParticleTicks = 0;
    private boolean started = false;

    @Override
    public DwarfActionType getType() {
        return DwarfActionType.BLOCK;
    }

    @Override
    public void start(AbstractDwarfEntity dwarf) {
        started = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void tick(AbstractDwarfEntity dwarf) {

        if (blockParticlePos == null) {
            Vec3 look = dwarf.getLookAngle().normalize();
            double forwardOffset = 1.0D;
            double leftOffset = -0.4D;
            Vec3 left = new Vec3(-look.z, 0, look.x).normalize();

            double px = dwarf.getX() + look.x * forwardOffset + left.x * leftOffset;
            double py = dwarf.getY() + 1.2D;
            double pz = dwarf.getZ() + look.z * forwardOffset + left.z * leftOffset;

            blockParticlePos = new Vec3(px, py, pz);
            blockParticleTicks = 10;
        }

        if (blockParticleTicks-- > 0) {
            for (int i = 0; i < 5; i++) {
                double scatterRange = 0.15D;

                double offsetX = blockParticlePos.x + (dwarf.getRandom().nextDouble() - 0.5) * 2.0 * scatterRange;
                double offsetY = blockParticlePos.y + (dwarf.getRandom().nextDouble() - 0.5) * 2.0 * scatterRange;
                double offsetZ = blockParticlePos.z + (dwarf.getRandom().nextDouble() - 0.5) * 2.0 * scatterRange;

                double velocityX = (dwarf.getRandom().nextDouble() - 0.5) * 0.1;
                double velocityY = (dwarf.getRandom().nextDouble()) * 0.1;
                double velocityZ = (dwarf.getRandom().nextDouble() - 0.5) * 0.1;

                DustParticleOptions dust = new DustParticleOptions(-2233622, 0.5F);
                dwarf.level().addParticle(dust, offsetX, offsetY, offsetZ, velocityX, velocityY, velocityZ);
            }
        }
    }

    @Override
    public boolean isStopped() {
        return blockParticleTicks <= 0;
    }

    @Override
    public void stop(AbstractDwarfEntity dwarf) {
        blockParticlePos = null;
        blockParticleTicks = 0;
        started = false;
    }
}
