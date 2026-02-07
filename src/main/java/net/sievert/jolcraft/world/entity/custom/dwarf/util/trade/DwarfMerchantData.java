package net.sievert.jolcraft.world.entity.custom.dwarf.util.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DwarfMerchantData(int level) {

    public enum Level {
        NOVICE(1),
        APPRENTICE(2),
        JOURNEYMAN(3),
        EXPERT(4),
        MASTER(5);

        private final int level;

        Level(int level) {
            this.level = level;
        }

        public int id() {
            return level;
        }
    }

    public static final int MIN_MERCHANT_LEVEL = 1;
    public static final int MAX_MERCHANT_LEVEL = 5;

    private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};

    public static final Codec<DwarfMerchantData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("level").orElse(MIN_MERCHANT_LEVEL).forGetter(DwarfMerchantData::level)).apply(instance, DwarfMerchantData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DwarfMerchantData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    DwarfMerchantData::level,
                    DwarfMerchantData::new
            );

    public DwarfMerchantData(int level) {
        this.level = clampLevel(level);
    }

    public DwarfMerchantData setLevel(int level) {
        return new DwarfMerchantData(level);
    }

    public static int getMinXpPerLevel(int level) {
        return canLevelUp(level) ? NEXT_LEVEL_XP_THRESHOLDS[level - 1] : 0;
    }

    public static int getMaxXpPerLevel(int level) {
        return canLevelUp(level) ? NEXT_LEVEL_XP_THRESHOLDS[level] : 0;
    }

    public static boolean canLevelUp(int level) {
        return level >= MIN_MERCHANT_LEVEL && level < MAX_MERCHANT_LEVEL;
    }

    private static int clampLevel(int level) {
        if (level < MIN_MERCHANT_LEVEL) return MIN_MERCHANT_LEVEL;
        return Math.min(level, MAX_MERCHANT_LEVEL);
    }
}