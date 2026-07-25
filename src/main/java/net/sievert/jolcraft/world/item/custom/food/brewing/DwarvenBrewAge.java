package net.sievert.jolcraft.world.item.custom.food.brewing;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.event.game.world.JolCraftTimeHelper;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;

import java.util.Locale;

public enum DwarvenBrewAge implements JolCraftEnumHelper.StringId {
    FRESH,
    AGED,
    MATURED,
    VINTAGE;

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int amplifierBonus() {
        return ordinal();
    }

    public String translationKey() {
        return switch (this) {
            case FRESH -> JolCraftLanguageKeys.BREW_AGE_FRESH;
            case AGED -> JolCraftLanguageKeys.BREW_AGE_AGED;
            case MATURED -> JolCraftLanguageKeys.BREW_AGE_MATURED;
            case VINTAGE -> JolCraftLanguageKeys.BREW_AGE_VINTAGE;
        };
    }

    public static DwarvenBrewAge fromStack(ItemStack stack) {
        return fromTicks(
                stack.getOrDefault(
                        JolCraftDataComponents.BREW_AGE.get(),
                        0L
                )
        );
    }

    public static DwarvenBrewAge fromTicks(long ageTicks) {
        long age = Math.max(0L, ageTicks);

        if (age > JolCraftTimeHelper.TICKS_PER_DAY * 5L) {
            return VINTAGE;
        }

        if (age > JolCraftTimeHelper.TICKS_PER_DAY * 3L) {
            return MATURED;
        }

        if (age > JolCraftTimeHelper.TICKS_PER_DAY) {
            return AGED;
        }

        return FRESH;
    }

    public static DwarvenBrewAge byId(String id) {
        return JolCraftEnumHelper.byStringId(
                DwarvenBrewAge.class,
                id,
                FRESH
        );
    }
}