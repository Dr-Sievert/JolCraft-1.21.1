package net.sievert.jolcraft.world.item.client.property;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemPropertyIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.client.property.custom.CoinPouchAmount;
import net.sievert.jolcraft.world.item.client.property.custom.LoreKey;
import net.sievert.jolcraft.world.item.client.property.custom.RewardCrateTheme;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public final class JolCraftItemProperties {

    /**
     * In 1.21.1, item model predicate values should stay inside [0, 1].
     *
     * We keep all custom predicate values in a small deterministic range
     * near zero to avoid collisions with vanilla-style thresholds.
     */
    private static final float CUSTOM_INDEX_BASE = 0.0123F;
    private static final float CUSTOM_INDEX_STEP = 0.0001F;

    private static final Map<ResourceLocation, Set<String>> KEYS = new LinkedHashMap<>();

    private JolCraftItemProperties() {}

    // ---------------------------------------------------------
    // Contract
    // ---------------------------------------------------------

    public interface Property {

        @NotNull ResourceLocation key();

        default void bootstrap() {}

        float value(
                @NotNull ItemStack stack,
                @Nullable ClientLevel level,
                @Nullable LivingEntity entity,
                int seed
        );
    }

    // ---------------------------------------------------------
    // Registry logic
    // ---------------------------------------------------------

    public static void register() {
        int props = 0;

        // Custom

        props += register(
                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(),
                new LoreKey()
        );

        props += register(
                JolCraftItems.COIN_POUCH.get(),
                new CoinPouchAmount()
        );

        props += register(
                JolCraftItems.REWARD_CRATE.get(),
                new RewardCrateTheme()
        );

        // Vanilla-like

        props += register(
                JolCraftItems.DEEPSLATE_COMPASS.get(),
                JolCraft.location(
                        JolCraftItemPropertyIds.DEEPSLATE_COMPASS_ANGLE
                ),
                new CompassItemPropertyFunction(
                        (level, stack, entity) ->
                                stack.get(
                                        JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET.get()
                                )
                )
        );

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Registered {} item properties",
                props
        );
    }

    private static int register(
            @NotNull Item item,
            @NotNull ResourceLocation key,
            @NotNull ItemPropertyFunction property
    ) {
        ItemProperties.register(
                item,
                key,
                property
        );

        return 1;
    }

    private static int register(
            @NotNull Item item,
            @NotNull Property property
    ) {
        property.bootstrap();

        ItemProperties.register(
                item,
                property.key(),
                property::value
        );

        return 1;
    }

    public static void registerKey(
            @NotNull ResourceLocation propertyId,
            @NotNull String key
    ) {
        KEYS.computeIfAbsent(
                propertyId,
                ignored -> new LinkedHashSet<>()
        ).add(key);
    }

    public static float value(
            @NotNull ResourceLocation propertyId,
            @NotNull String key
    ) {
        Set<String> keys =
                KEYS.get(propertyId);

        if (keys == null || !keys.contains(key)) {
            throw new IllegalStateException(
                    "Unregistered item property key: "
                            + propertyId
                            + " -> "
                            + key
            );
        }

        int index = 0;

        for (String registeredKey : keys) {
            if (registeredKey.equals(key)) {
                float value =
                        CUSTOM_INDEX_BASE
                                + (index * CUSTOM_INDEX_STEP);

                validateInRange(
                        propertyId,
                        key,
                        value
                );

                return value;
            }

            index++;
        }

        throw new IllegalStateException(
                "Could not resolve item property key: "
                        + propertyId
                        + " -> "
                        + key
        );
    }

    public static void validate(
            @NotNull ResourceLocation propertyId
    ) {
        Set<String> keys =
                KEYS.get(propertyId);

        if (keys == null || keys.isEmpty()) {
            return;
        }

        int index = 0;

        for (String key : keys) {
            float value =
                    CUSTOM_INDEX_BASE
                            + (index * CUSTOM_INDEX_STEP);

            validateInRange(
                    propertyId,
                    key,
                    value
            );

            index++;
        }
    }

    private static void validateInRange(
            @NotNull ResourceLocation propertyId,
            @NotNull String key,
            float value
    ) {
        if (value < 0.0F || value > 1.0F) {
            throw new IllegalStateException(
                    "Item property value out of range [0,1] for "
                            + propertyId
                            + " -> "
                            + key
                            + ": "
                            + value
            );
        }
    }
}