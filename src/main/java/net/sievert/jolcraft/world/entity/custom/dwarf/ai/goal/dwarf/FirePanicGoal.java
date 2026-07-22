package net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import java.util.EnumSet;

public class FirePanicGoal extends Goal
{
    private static final int HORIZONTAL_SEARCH_RANGE = 5;
    private static final int VERTICAL_SEARCH_RANGE = 2;

    private final AbstractDwarfEntity dwarf;
    private final double speedModifier;
    private double randPosX;
    private double randPosY;
    private double randPosZ;

    public FirePanicGoal(AbstractDwarfEntity dwarf, double speedModifier)
    {
        this.dwarf = dwarf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if(!this.dwarf.isOnFire())
            return false;

        BlockPos blockpos = this.findClosestWaterPos();
        if(blockpos == null)
            return this.findRandomPosition();

        this.randPosX = blockpos.getX();
        this.randPosY = blockpos.getY();
        this.randPosZ = blockpos.getZ();
        return true;
    }

    @Override
    public void start()
    {
        this.dwarf.getNavigation().moveTo(this.randPosX, this.randPosY, this.randPosZ, this.speedModifier);
    }

    @Override
    public boolean canContinueToUse()
    {
        return !this.dwarf.getNavigation().isDone();
    }

    private boolean findRandomPosition()
    {
        Vec3 randomPos = DefaultRandomPos.getPos(this.dwarf, 5, 4);
        if(randomPos == null)
            return false;
        this.randPosX = randomPos.x;
        this.randPosY = randomPos.y;
        this.randPosZ = randomPos.z;
        return true;
    }

    @Nullable
    private BlockPos findClosestWaterPos()
    {
        Level level = this.dwarf.level();
        BlockPos entityPos = this.dwarf.blockPosition();
        return BlockPos.findClosestMatch(entityPos, HORIZONTAL_SEARCH_RANGE, VERTICAL_SEARCH_RANGE, pos -> {
            return level.getFluidState(pos).is(FluidTags.WATER);
        }).orElse(null);
    }
}