package net.sievert.jolcraft.config.custom.dwarf.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Map;

public record DwarfProfessionAttributesConfig(
        Map<ResourceLocation, Double> overrides
) {

    public static final String KEY_OVERRIDES =
            JolCraftStrings.plural(JolCraftDictionary.OVERRIDE);

    public static final DwarfProfessionAttributesConfig DEFAULTS =
            new DwarfProfessionAttributesConfig(Map.of(
                    idOf(Attributes.MAX_HEALTH), 30D,
                    idOf(Attributes.MOVEMENT_SPEED), 0.20D,
                    idOf(Attributes.FOLLOW_RANGE), 24D,
                    idOf(Attributes.ATTACK_DAMAGE), 3.0D
            ));

    public static final Codec<DwarfProfessionAttributesConfig> CODEC =
            RecordCodecBuilder.create(i -> i.group(
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE)
                            .optionalFieldOf(KEY_OVERRIDES, DEFAULTS.overrides())
                            .forGetter(DwarfProfessionAttributesConfig::overrides)
            ).apply(i, DwarfProfessionAttributesConfig::new));

    private static ResourceLocation idOf(Holder<Attribute> holder) {
        return holder.unwrapKey()
                .map(ResourceKey::location)
                .orElseThrow(() -> new IllegalStateException("Unregistered attribute: " + holder));
    }
}