package net.sievert.jolcraft.integration.jei.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.IdentityHashMap;
import java.util.Map;

public final class JeiEntityRenderer {

    private static final float CAMERA_PITCH = -10.0F;
    private static final float MIN_DIMENSION = 0.25F;
    private static final float VERTICAL_OFFSET = 0.10F;

    private static final Map<EntityType<?>, LivingEntity> LIVING_CACHE =
            new IdentityHashMap<>();

    private static @Nullable ClientLevel cachedLevel;

    private JeiEntityRenderer() {
    }

    public static @Nullable LivingEntity createLiving(
            @NotNull EntityType<?> entityType
    ) {
        ClientLevel level =
                Minecraft.getInstance().level;

        if (level == null) {
            clearCache();
            return null;
        }

        if (cachedLevel != level) {
            LIVING_CACHE.clear();
            cachedLevel =
                    level;
        }

        LivingEntity cached =
                LIVING_CACHE.get(
                        entityType
                );

        if (cached != null) {
            return cached;
        }

        Entity entity =
                entityType.create(
                        level
                );

        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }

        LIVING_CACHE.put(
                entityType,
                livingEntity
        );

        return livingEntity;
    }

    private static void clearCache() {
        LIVING_CACHE.clear();
        cachedLevel =
                null;
    }

    public static void renderToBounds(
            @NotNull GuiGraphics graphics,
            @NotNull LivingEntity entity,
            float centerX,
            float bottomY,
            float targetSize,
            float bodyRotation,
            float pitch
    ) {
        float largestDimension =
                Math.max(
                        entity.getBbWidth(),
                        entity.getBbHeight()
                );

        render(
                graphics,
                entity,
                centerX,
                bottomY,
                targetSize
                        / Math.max(
                        largestDimension,
                        MIN_DIMENSION
                ),
                bodyRotation,
                bodyRotation,
                pitch,
                true
        );
    }

    public static void render(
            @NotNull GuiGraphics graphics,
            @NotNull LivingEntity entity,
            float centerX,
            float bottomY,
            float scale,
            float bodyRotation,
            float headRotation,
            float pitch
    ) {
        render(
                graphics,
                entity,
                centerX,
                bottomY,
                scale,
                bodyRotation,
                headRotation,
                pitch,
                true
        );
    }

    public static void render(
            @NotNull GuiGraphics graphics,
            @NotNull LivingEntity entity,
            float centerX,
            float bottomY,
            float scale,
            float bodyRotation,
            float headRotation,
            float pitch,
            boolean synchronizePreviousRotations
    ) {
        Quaternionf pose =
                new Quaternionf()
                        .rotateZ(
                                (float) Math.PI
                        );

        Quaternionf camera =
                new Quaternionf()
                        .rotateX(
                                CAMERA_PITCH
                                        * (float) (
                                        Math.PI / 180.0F
                                )
                        );

        entity.yBodyRot =
                bodyRotation;

        if (synchronizePreviousRotations) {
            entity.yBodyRotO =
                    bodyRotation;
        }

        entity.setYRot(
                bodyRotation
        );

        if (synchronizePreviousRotations) {
            entity.yRotO =
                    bodyRotation;
        }

        entity.yHeadRot =
                headRotation;

        entity.yHeadRotO =
                headRotation;

        entity.setXRot(
                pitch
        );

        if (synchronizePreviousRotations) {
            entity.xRotO =
                    pitch;
        }

        Vector3f translate =
                new Vector3f(
                        0.0F,
                        entity.getBbHeight()
                                * VERTICAL_OFFSET,
                        0.0F
                );

        InventoryScreen.renderEntityInInventory(
                graphics,
                centerX,
                bottomY,
                scale,
                translate,
                pose,
                camera,
                entity
        );
    }
}
