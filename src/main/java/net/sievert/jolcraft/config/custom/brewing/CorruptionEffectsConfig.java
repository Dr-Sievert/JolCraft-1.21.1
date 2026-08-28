package net.sievert.jolcraft.config.custom.brewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

import java.util.List;

public record CorruptionEffectsConfig(
        List<WeightedEffect> effects
) {

    public static final Codec<CorruptionEffectsConfig> CODEC =
            RecordCodecBuilder.<CorruptionEffectsConfig>create(instance ->
                    instance.group(
                            WeightedEffect.CODEC
                                    .listOf()
                                    .fieldOf("effects")
                                    .forGetter(
                                            CorruptionEffectsConfig::effects
                                    )
                    ).apply(
                            instance,
                            CorruptionEffectsConfig::new
                    )
            ).validate(config -> {
                for (WeightedEffect entry : config.effects()) {
                    if (entry.effect()
                            .getEffect()
                            .value()
                            .getCategory()
                            != MobEffectCategory.HARMFUL) {
                        return DataResult.error(() ->
                                "Corruption effect must be harmful: "
                                        + entry.effect()
                                        .getEffect()
                                        .getRegisteredName()
                        );
                    }
                }

                return DataResult.success(
                        config
                );
            });

    public CorruptionEffectsConfig {
        effects =
                effects == null
                        ? List.of()
                        : List.copyOf(effects);
    }

    public static CorruptionEffectsConfig defaults() {
        return new CorruptionEffectsConfig(
                List.of(
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.UNLUCK,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.BLINDNESS,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.DIG_SLOWDOWN,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.CONFUSION,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.POISON,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.MOVEMENT_SLOWDOWN,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.WEAKNESS,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        MobEffects.WITHER,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                5,
                                new MobEffectInstance(
                                        JolCraftEffects.CORROSION,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.DISARMED,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.SUPPRESSED,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.ATAXIA_CURSE,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.CURSED_WOUND,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.DELIRIUM_CURSE,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.FAMINE_CURSE,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.FRAILTY_CURSE,
                                        1200,
                                        0
                                )
                        ),
                        new WeightedEffect(
                                1,
                                new MobEffectInstance(
                                        JolCraftEffects.VITALITY_CURSE,
                                        1200,
                                        0
                                )
                        )
                )
        );
    }

    public record WeightedEffect(
            int weight,
            MobEffectInstance effect
    ) {

        public static final Codec<WeightedEffect> CODEC =
                RecordCodecBuilder.<WeightedEffect>create(instance ->
                        instance.group(
                                Codec.INT
                                        .fieldOf("weight")
                                        .forGetter(
                                                WeightedEffect::weight
                                        ),
                                MobEffectInstance.CODEC
                                        .fieldOf("effect")
                                        .forGetter(
                                                WeightedEffect::effect
                                        )
                        ).apply(
                                instance,
                                WeightedEffect::new
                        )
                ).validate(entry -> {
                    if (entry.weight() < 1) {
                        return DataResult.error(() ->
                                "Corruption effect weight must be at least 1"
                        );
                    }

                    return DataResult.success(
                            entry
                    );
                });

        public WeightedEffect {
            effect =
                    new MobEffectInstance(
                            effect
                    );
        }

        public MobEffectInstance copyEffect() {
            return new MobEffectInstance(
                    effect
            );
        }
    }
}