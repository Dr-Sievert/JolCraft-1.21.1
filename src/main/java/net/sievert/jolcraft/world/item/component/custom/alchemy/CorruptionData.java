package net.sievert.jolcraft.world.item.component.custom.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.Optional;

public record CorruptionData(
        Holder<Potion> originalPotion,
        MobEffectInstance corruptionEffect,
        Optional<MobEffectInstance> replacedEffect
) {

    public static final Codec<CorruptionData> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Potion.CODEC
                                    .fieldOf("original_potion")
                                    .forGetter(CorruptionData::originalPotion),
                            MobEffectInstance.CODEC
                                    .fieldOf("corruption_effect")
                                    .forGetter(CorruptionData::corruptionEffect),
                            MobEffectInstance.CODEC
                                    .optionalFieldOf("replaced_effect")
                                    .forGetter(CorruptionData::replacedEffect)
                    ).apply(
                            instance,
                            CorruptionData::new
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, CorruptionData> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, data) -> {
                        Potion.STREAM_CODEC.encode(
                                buffer,
                                data.originalPotion()
                        );

                        MobEffectInstance.STREAM_CODEC.encode(
                                buffer,
                                data.corruptionEffect()
                        );

                        buffer.writeBoolean(
                                data.replacedEffect().isPresent()
                        );

                        data.replacedEffect().ifPresent(effect ->
                                MobEffectInstance.STREAM_CODEC.encode(
                                        buffer,
                                        effect
                                )
                        );
                    },
                    buffer -> {
                        Holder<Potion> originalPotion =
                                Potion.STREAM_CODEC.decode(
                                        buffer
                                );

                        MobEffectInstance corruptionEffect =
                                MobEffectInstance.STREAM_CODEC.decode(
                                        buffer
                                );

                        Optional<MobEffectInstance> replacedEffect =
                                buffer.readBoolean()
                                        ? Optional.of(
                                        MobEffectInstance.STREAM_CODEC.decode(
                                                buffer
                                        )
                                )
                                        : Optional.empty();

                        return new CorruptionData(
                                originalPotion,
                                corruptionEffect,
                                replacedEffect
                        );
                    }
            );

    public CorruptionData {
        corruptionEffect =
                new MobEffectInstance(
                        corruptionEffect
                );

        replacedEffect =
                replacedEffect.map(
                        MobEffectInstance::new
                );
    }
}