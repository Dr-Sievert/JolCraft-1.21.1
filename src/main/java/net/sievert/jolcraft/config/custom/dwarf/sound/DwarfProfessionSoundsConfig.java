package net.sievert.jolcraft.config.custom.dwarf.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

import java.util.Optional;

public record DwarfProfessionSoundsConfig(
        Optional<ResourceLocation> restock,
        Optional<ResourceLocation> reroll
) {

    public static final String KEY_RESTOCK = JolCraftDictionary.RESTOCK;
    public static final String KEY_REROLL = JolCraftDictionary.REROLL;

    public static final DwarfProfessionSoundsConfig DEFAULTS =
            new DwarfProfessionSoundsConfig(Optional.empty(), Optional.empty());

    public static final Codec<DwarfProfessionSoundsConfig> CODEC =
            RecordCodecBuilder.create(i -> i.group(
                    ResourceLocation.CODEC.optionalFieldOf(KEY_RESTOCK).forGetter(DwarfProfessionSoundsConfig::restock),
                    ResourceLocation.CODEC.optionalFieldOf(KEY_REROLL).forGetter(DwarfProfessionSoundsConfig::reroll)
            ).apply(i, DwarfProfessionSoundsConfig::new));
}