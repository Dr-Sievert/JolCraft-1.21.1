package net.sievert.jolcraft.world.worldgen.structure.util;

import net.minecraft.world.level.block.Rotation;

public final class JolCraftStructureContext {

    private static final ThreadLocal<Rotation> ROTATION = new ThreadLocal<>();

    private JolCraftStructureContext() {}

    public static void setRotation(Rotation rotation) {
        ROTATION.set(rotation);
    }

    public static Rotation getRotation() {
        return ROTATION.get();
    }

    public static void clear() {
        ROTATION.remove();
    }
}