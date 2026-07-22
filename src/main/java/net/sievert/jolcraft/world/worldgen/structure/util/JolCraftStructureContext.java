package net.sievert.jolcraft.world.worldgen.structure.util;

import net.minecraft.world.level.block.Rotation;

import java.util.List;

public final class JolCraftStructureContext {

    private static final ThreadLocal<Boolean> ACTIVE =
            ThreadLocal.withInitial(() -> false);

    private static final ThreadLocal<Rotation> ROTATION =
            new ThreadLocal<>();

    private static final ThreadLocal<List<SinglePlacementPart>> SINGLE_PLACEMENT_PARTS =
            ThreadLocal.withInitial(List::of);

    private JolCraftStructureContext() {}

    public static void activate(List<SinglePlacementPart> singlePlacementParts) {
        ACTIVE.set(true);
        SINGLE_PLACEMENT_PARTS.set(List.copyOf(singlePlacementParts));
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

    public static List<SinglePlacementPart> getSinglePlacementParts() {
        return SINGLE_PLACEMENT_PARTS.get();
    }

    public static void clear() {
        ACTIVE.remove();
        ROTATION.remove();
        SINGLE_PLACEMENT_PARTS.remove();
    }
}