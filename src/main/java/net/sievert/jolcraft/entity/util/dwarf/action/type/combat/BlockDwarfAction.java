package net.sievert.jolcraft.entity.util.dwarf.action.type.combat;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfAction;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

/**
 * Action for handling dwarf blocking behavior and associated particle effects.
 * Handles its own particle timers and resets when finished.
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
        Vec3 look = dwarf.getLookAngle().normalize();
        double forwardOffset = 1.0D;
        double leftOffset = -0.4D;
        Vec3 left = new Vec3(-look.z, 0, look.x).normalize();

        double px = dwarf.getX() + look.x * forwardOffset + left.x * leftOffset;
        double py = dwarf.getY() + 1.2D;
        double pz = dwarf.getZ() + look.z * forwardOffset + left.z * leftOffset;

        Vec3 blockParticlePos = new Vec3(px, py, pz);
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
