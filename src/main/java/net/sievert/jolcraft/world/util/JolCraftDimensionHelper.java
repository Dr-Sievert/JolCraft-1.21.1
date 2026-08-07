package net.sievert.jolcraft.world.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class JolCraftDimensionHelper {

    private JolCraftDimensionHelper() {}

    public static boolean isDimension(Level level, ResourceKey<Level> dimension) {
        return level.dimension() == dimension;
    }

    public static boolean isDimension(Entity entity, ResourceKey<Level> dimension) {
        return isDimension(entity.level(), dimension);
    }

    public static boolean isOverworld(Level level) {
        return isDimension(level, Level.OVERWORLD);
    }

    public static boolean isOverworld(Entity entity) {
        return isOverworld(entity.level());
    }

    public static boolean isNether(Level level) {
        return isDimension(level, Level.NETHER);
    }

    public static boolean isNether(Entity entity) {
        return isNether(entity.level());
    }

    public static boolean isEnd(Level level) {
        return isDimension(level, Level.END);
    }

    public static boolean isEnd(Entity entity) {
        return isEnd(entity.level());
    }

    public static boolean hasDayNightCycle(Level level) {
        return !level.dimensionType().hasFixedTime();
    }

    public static boolean hasDayNightCycle(Entity entity) {
        return hasDayNightCycle(entity.level());
    }
}