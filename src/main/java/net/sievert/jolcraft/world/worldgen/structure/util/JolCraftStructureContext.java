package net.sievert.jolcraft.world.worldgen.structure.util;

import net.minecraft.world.level.block.Rotation;

public final class JolCraftStructureContext {

    private static final ThreadLocal<Boolean> ACTIVE =
            ThreadLocal.withInitial(() -> false);

    private static final ThreadLocal<Rotation> ROTATION =
            new ThreadLocal<>();

    private JolCraftStructureContext() {}

    public static void activate() {
        ACTIVE.set(true);
    }

    public static boolean isActive() {
        return ACTIVE.get();
    }

    public static void setRotation(Rotation rotation) {
        ROTATION.set(rotation);
    }

    public static Rotation getRotation() {
        return ROTATION.get();
    }

    public static void clear() {
        ACTIVE.remove();
        ROTATION.remove();
    }
}