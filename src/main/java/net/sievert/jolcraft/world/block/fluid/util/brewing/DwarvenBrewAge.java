package net.sievert.jolcraft.world.block.fluid.util.brewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.event.game.world.time.JolCraftTimeHelper;
import net.sievert.jolcraft.util.JolCraftEnumHelper;

import java.util.Locale;

/**
 * Represents the age tiers applied to dwarven brew.
 */
public enum DwarvenBrewAge implements JolCraftEnumHelper.StringId {

    FRESH(
            0L,
            0,
            JolCraftLanguageKeys.BREW_AGE_FRESH
    ),
    AGED(
            JolCraftTimeHelper.TICKS_PER_DAY,
            1,
            JolCraftLanguageKeys.BREW_AGE_AGED
    ),
    MATURED(
            JolCraftTimeHelper.TICKS_PER_DAY * 3L,
            2,
            JolCraftLanguageKeys.BREW_AGE_MATURED
    ),
    VINTAGE(
            JolCraftTimeHelper.TICKS_PER_DAY * 5L,
            3,
            JolCraftLanguageKeys.BREW_AGE_VINTAGE
    );

    public static final Codec<DwarvenBrewAge> CODEC =
            Codec.STRING.comapFlatMap(
                    DwarvenBrewAge::decode,
                    DwarvenBrewAge::getId
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, DwarvenBrewAge> STREAM_CODEC =
            StreamCodec.of(
                    FriendlyByteBuf::writeEnum,
                    buffer -> buffer.readEnum(DwarvenBrewAge.class)
            );

    private final long thresholdTicks;
    private final int amplifierBonus;
    private final String translationKey;

    DwarvenBrewAge(
            long thresholdTicks,
            int amplifierBonus,
            String translationKey
    ) {
        this.thresholdTicks = thresholdTicks;
        this.amplifierBonus = amplifierBonus;
        this.translationKey = translationKey;
    }

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int amplifierBonus() {
        return amplifierBonus;
    }

    public String translationKey() {
        return translationKey;
    }

    /**
     * Resolves the brew age stored in an item's fluid contents.
     */
    public static DwarvenBrewAge fromStack(ItemStack stack) {
        return DwarvenBrewFluidHelper.findContainedBrew(stack)
                .map(DwarvenBrewFluidHelper::getAge)
                .map(DwarvenBrewAge::fromTicks)
                .orElse(FRESH);
    }

    /**
     * Resolves the highest age tier reached by the supplied age.
     */
    public static DwarvenBrewAge fromTicks(long ageTicks) {
        long age = Math.max(
                0L,
                ageTicks
        );

        DwarvenBrewAge resolved = FRESH;

        for (DwarvenBrewAge value : values()) {
            if (age < value.thresholdTicks) {
                break;
            }

            resolved = value;
        }

        return resolved;
    }

    /**
     * Returns the next age threshold after the supplied age.
     */
    public static long nextThreshold(long currentAge) {
        long age = Math.max(
                0L,
                currentAge
        );

        for (DwarvenBrewAge value : values()) {
            if (value.thresholdTicks > age) {
                return value.thresholdTicks;
            }
        }

        return age;
    }

    public long thresholdTicks() {
        return thresholdTicks;
    }

    private static DataResult<DwarvenBrewAge> decode(String id) {
        DwarvenBrewAge age = JolCraftEnumHelper.byStringIdNullable(
                DwarvenBrewAge.class,
                id,
                null
        );

        return age == null
                ? DataResult.error(() -> "Unknown dwarven brew age: " + id)
                : DataResult.success(age);
    }

    public static DwarvenBrewAge byId(String id) {
        return JolCraftEnumHelper.byStringId(
                DwarvenBrewAge.class,
                id,
                FRESH
        );
    }
}
