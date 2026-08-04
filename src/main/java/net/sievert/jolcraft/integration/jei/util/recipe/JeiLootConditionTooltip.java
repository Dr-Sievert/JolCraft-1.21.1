package net.sievert.jolcraft.integration.jei.util.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawableHelper;
import net.sievert.jolcraft.integration.jei.util.gui.JeiTextures;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

public final class JeiLootConditionTooltip {

    private static final long ROTATION_INTERVAL_MILLIS = 1500L;

    private static final String LOOT_CONDITION_DESCRIPTION_CATEGORY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LOOT,
                    JolCraftDictionary.CONDITION
            );

    private static final IDrawable CONDITION_OVERLAY =
            JeiDrawableHelper.sprite(
                    JeiTextures.UNSEEN_NOTIFICATION,
                    JeiTextures.UNSEEN_NOTIFICATION_SIZE,
                    JeiTextures.UNSEEN_NOTIFICATION_SIZE
            );

    private JeiLootConditionTooltip() {}

    public static void add(
            @NotNull IRecipeSlotBuilder slot,
            @NotNull JeiItemOutcome outcome
    ) {
        if (!outcome.hasConditions()) {
            return;
        }

        slot.setOverlay(
                CONDITION_OVERLAY,
                10,
                10
        );

        slot.addRichTooltipCallback(
                (recipeSlot, tooltip) -> {
                    for (LootItemCondition condition : outcome.conditions()) {
                        append(
                                tooltip,
                                condition
                        );
                    }
                }
        );
    }

    private static void append(
            @NotNull ITooltipBuilder tooltip,
            @NotNull LootItemCondition condition
    ) {
        if (condition instanceof LocationCheck locationCheck
                && appendLocation(
                tooltip,
                locationCheck
        )) {
            return;
        }

        ResourceLocation type =
                BuiltInRegistries.LOOT_CONDITION_TYPE
                        .getKey(
                                condition.getType()
                        );

        Component conditionName =
                type == null
                        ? Component.literal(
                        condition.getClass()
                                .getSimpleName()
                )
                        : registryName(
                        LOOT_CONDITION_DESCRIPTION_CATEGORY,
                        type
                );

        addLine(
                tooltip,
                JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_CONDITION,
                conditionName
        );

        Component details =
                encodedDetails(
                        condition
                );

        if (details != null) {
            addLine(
                    tooltip,
                    JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_DETAILS,
                    details
            );
        }
    }

    private static boolean appendLocation(
            @NotNull ITooltipBuilder tooltip,
            @NotNull LocationCheck condition
    ) {
        Optional<LocationPredicate> optional =
                condition.predicate();

        if (optional.isEmpty()) {
            addLine(
                    tooltip,
                    JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_LOCATION,
                    Component.translatable(
                            JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_ANY
                    )
            );

            return true;
        }

        LocationPredicate predicate =
                optional.get();

        boolean described = false;

        described |= appendHolders(
                tooltip,
                JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_BIOME,
                predicate.biomes(),
                location -> registryName(
                        Registries.BIOME.location()
                                .getPath(),
                        location
                )
        );

        described |= appendHolders(
                tooltip,
                JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_STRUCTURE,
                predicate.structures(),
                location -> registryName(
                        Registries.STRUCTURE.location()
                                .getPath(),
                        location
                )
        );

        if (predicate.dimension().isPresent()) {
            addLine(
                    tooltip,
                    JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_DIMENSION,
                    registryName(
                            Registries.DIMENSION.location()
                                    .getPath(),
                            predicate.dimension()
                                    .get()
                                    .location()
                    )
            );

            described = true;
        }

        described |= appendPresent(
                tooltip,
                JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_POSITION,
                predicate.position()
        );

        described |= appendPresent(
                tooltip,
                JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_LIGHT,
                predicate.light()
        );

        described |= appendPresent(
                tooltip,
                JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_BLOCK,
                predicate.block()
        );

        described |= appendPresent(
                tooltip,
                JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_FLUID,
                predicate.fluid()
        );

        if (predicate.smokey().isPresent()) {
            addBoolean(
                    tooltip,
                    JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_SMOKEY,
                    predicate.smokey()
                            .get()
            );

            described = true;
        }

        if (predicate.canSeeSky().isPresent()) {
            addBoolean(
                    tooltip,
                    JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_CAN_SEE_SKY,
                    predicate.canSeeSky()
                            .get()
            );

            described = true;
        }

        if (!condition.offset().equals(BlockPos.ZERO)) {
            BlockPos offset =
                    condition.offset();

            addLine(
                    tooltip,
                    JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_OFFSET,
                    Component.translatable(
                            JolCraftLanguageKeys.JEI_TOOLTIP_LOOT_CONDITION_COORDINATES,
                            offset.getX(),
                            offset.getY(),
                            offset.getZ()
                    )
            );

            described = true;
        }

        return described;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <T> boolean appendHolders(
            @NotNull ITooltipBuilder tooltip,
            @NotNull String labelKey,
            @NotNull Optional<HolderSet<T>> optional,
            @NotNull Function<ResourceLocation, Component> nameFactory
    ) {
        if (optional.isEmpty()) {
            return false;
        }

        List<Component> names =
                optional.get()
                        .stream()
                        .map(Holder::unwrapKey)
                        .flatMap(Optional::stream)
                        .map(key ->
                                nameFactory.apply(
                                        key.location()
                                )
                        )
                        .toList();

        addLine(
                tooltip,
                labelKey,
                names.isEmpty()
                        ? Component.translatable(
                        JolCraftLanguageKeys
                                .JEI_TOOLTIP_LOOT_CONDITION_RESTRICTED
                )
                        : rotating(names)
        );

        return true;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static boolean appendPresent(
            @NotNull ITooltipBuilder tooltip,
            @NotNull String labelKey,
            @NotNull Optional<?> value
    ) {
        if (value.isEmpty()) {
            return false;
        }

        addLine(
                tooltip,
                labelKey,
                Component.translatable(
                        JolCraftLanguageKeys
                                .JEI_TOOLTIP_LOOT_CONDITION_RESTRICTED
                )
        );

        return true;
    }

    private static @Nullable Component encodedDetails(
            @NotNull LootItemCondition condition
    ) {
        ClientLevel level =
                Minecraft.getInstance().level;

        if (level == null) {
            return null;
        }

        JsonObject encoded =
                LootItemCondition.DIRECT_CODEC
                        .encodeStart(
                                level.registryAccess()
                                        .createSerializationContext(
                                                JsonOps.INSTANCE
                                        ),
                                condition
                        )
                        .result()
                        .filter(JsonElement::isJsonObject)
                        .map(JsonElement::getAsJsonObject)
                        .orElse(null);

        if (encoded == null) {
            return null;
        }

        List<Component> details =
                new ArrayList<>();

        for (String key : encoded.keySet()) {
            if (key.equals(
                    JolCraftDictionary.CONDITION
            )) {
                continue;
            }

            details.add(
                    Component.translatable(
                            JolCraftLanguageKeys
                                    .JEI_TOOLTIP_LOOT_CONDITION_DETAIL,
                            translatedOrFallback(
                                    JolCraftLanguageKeys
                                            .jeiLootConditionField(
                                                    key
                                            ),
                                    key
                            ),
                            format(
                                    key,
                                    encoded.get(key)
                            )
                    )
            );
        }

        return details.isEmpty()
                ? null
                : join(details);
    }

    private static @NotNull Component format(
            @NotNull String key,
            @NotNull JsonElement value
    ) {
        if (value.isJsonArray()) {
            JsonArray values =
                    value.getAsJsonArray();

            return values.isEmpty()
                    ? Component.translatable(
                    JolCraftLanguageKeys
                            .JEI_TOOLTIP_LOOT_CONDITION_NONE
            )
                    : format(
                    key,
                    values.get(
                            rotatingIndex(
                                    values.size()
                            )
                    )
            );
        }

        if (!value.isJsonPrimitive()) {
            return Component.literal(
                    value.toString()
            );
        }

        JsonPrimitive primitive =
                value.getAsJsonPrimitive();

        if (primitive.isBoolean()) {
            return Component.translatable(
                    primitive.getAsBoolean()
                            ? JolCraftLanguageKeys
                            .JEI_TOOLTIP_LOOT_CONDITION_YES
                            : JolCraftLanguageKeys
                            .JEI_TOOLTIP_LOOT_CONDITION_NO
            );
        }

        if (primitive.isNumber()) {
            double number =
                    primitive.getAsDouble();

            if (key.toLowerCase(Locale.ROOT)
                    .contains(
                            JolCraftDictionary.CHANCE
                    )) {
                return Component.translatable(
                        JolCraftLanguageKeys
                                .JEI_TOOLTIP_LOOT_CONDITION_PERCENT,
                        String.format(
                                Locale.ROOT,
                                "%.2f",
                                number * 100.0D
                        )
                );
            }

            return Component.literal(
                    primitive.getAsString()
            );
        }

        String raw =
                primitive.getAsString();

        ResourceLocation location =
                ResourceLocation.tryParse(
                        raw
                );

        return Component.literal(
                location == null
                        ? raw
                        : location.toString()
        );
    }

    private static @NotNull Component join(
            @NotNull List<Component> values
    ) {
        MutableComponent result =
                Component.empty();

        for (int index = 0;
             index < values.size();
             index++) {
            if (index > 0) {
                result.append(
                        Component.translatable(
                                JolCraftLanguageKeys
                                        .JEI_TOOLTIP_LOOT_CONDITION_SEPARATOR
                        )
                );
            }

            result.append(
                    values.get(index)
            );
        }

        return result;
    }

    private static @NotNull Component registryName(
            @NotNull String descriptionCategory,
            @NotNull ResourceLocation location
    ) {
        return translatedOrFallback(
                Util.makeDescriptionId(
                        descriptionCategory,
                        location
                ),
                location.toString()
        );
    }

    private static @NotNull Component translatedOrFallback(
            @NotNull String translationKey,
            @NotNull String fallback
    ) {
        return Language.getInstance()
                .has(translationKey)
                ? Component.translatable(
                translationKey
        )
                : Component.literal(
                fallback
        );
    }

    private static <T> @NotNull T rotating(
            @NotNull List<T> values
    ) {
        return values.get(
                rotatingIndex(
                        values.size()
                )
        );
    }

    private static int rotatingIndex(
            int size
    ) {
        return (int) (
                Util.getMillis()
                        / ROTATION_INTERVAL_MILLIS
                        % size
        );
    }

    private static void addBoolean(
            @NotNull ITooltipBuilder tooltip,
            @NotNull String labelKey,
            boolean value
    ) {
        addLine(
                tooltip,
                labelKey,
                Component.translatable(
                        value
                                ? JolCraftLanguageKeys
                                .JEI_TOOLTIP_LOOT_CONDITION_YES
                                : JolCraftLanguageKeys
                                .JEI_TOOLTIP_LOOT_CONDITION_NO
                )
        );
    }

    private static void addLine(
            @NotNull ITooltipBuilder tooltip,
            @NotNull String labelKey,
            @NotNull Component value
    ) {
        tooltip.add(
                Component.translatable(
                        JolCraftLanguageKeys
                                .JEI_TOOLTIP_LOOT_CONDITION_LINE,
                        Component.translatable(
                                        labelKey
                                )
                                .withStyle(
                                        ChatFormatting.GRAY
                                ),
                        value.copy()
                                .withStyle(
                                        ChatFormatting.YELLOW
                                )
                )
        );
    }
}