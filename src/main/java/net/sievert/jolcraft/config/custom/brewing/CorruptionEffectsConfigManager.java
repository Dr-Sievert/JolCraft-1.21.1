package net.sievert.jolcraft.config.custom.brewing;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class CorruptionEffectsConfigManager
        extends SimplePreparableReloadListener<CorruptionEffectsConfig> {

    public static final ResourceLocation FILE =
            JolCraft.location(
                    JolCraftStrings.slashed(
                            JolCraftDictionary.CONFIG,
                            JolCraftDictionary.BREWING,
                            JolCraftStrings.underscored(
                                    JolCraftDictionary.CORRUPTION,
                                    JolCraftStrings.plural(JolCraftDictionary.EFFECT)
                            )
                    ) + ".json"
            );

    public static final CorruptionEffectsConfigManager INSTANCE =
            new CorruptionEffectsConfigManager();

    private volatile CorruptionEffectsConfig config = new CorruptionEffectsConfig(List.of());

    private CorruptionEffectsConfigManager() {}

    public List<CorruptionEffectsConfig.WeightedEffect> entries() {
        return config.effects();
    }

    public boolean hasEligibleEffect(
            @Nullable Holder<MobEffect> excludedEffect
    ) {
        for (CorruptionEffectsConfig.WeightedEffect entry : config.effects()) {
            if (!entry.effect().getEffect().equals(excludedEffect)) {
                return true;
            }
        }

        return false;
    }

    public Optional<MobEffectInstance> roll(
            @Nullable Holder<MobEffect> excludedEffect
    ) {
        List<CorruptionEffectsConfig.WeightedEffect> eligible =
                new ArrayList<>();

        long totalWeight = 0L;

        for (CorruptionEffectsConfig.WeightedEffect entry : config.effects()) {
            if (entry.effect().getEffect().equals(excludedEffect)) {
                continue;
            }

            eligible.add(entry);
            totalWeight += entry.weight();
        }

        if (eligible.isEmpty() || totalWeight <= 0L) {
            return Optional.empty();
        }

        long roll =
                ThreadLocalRandom.current()
                        .nextLong(
                                totalWeight
                        );

        for (CorruptionEffectsConfig.WeightedEffect entry : eligible) {
            roll -= entry.weight();

            if (roll < 0L) {
                return Optional.of(
                        entry.copyEffect()
                );
            }
        }

        return Optional.of(
                eligible.getLast()
                        .copyEffect()
        );
    }

    @Override
    protected @NotNull CorruptionEffectsConfig prepare(
            ResourceManager manager,
            @NotNull ProfilerFiller profiler
    ) {
        Optional<Resource> resource =
                manager.getResource(
                        FILE
                );

        if (resource.isEmpty()) {
            return CorruptionEffectsConfig.defaults();
        }

        try (Reader reader = resource.orElseThrow().openAsReader()) {
            JsonElement json =
                    JsonParser.parseReader(
                            reader
                    );

            return CorruptionEffectsConfig.CODEC.parse(
                            getRegistryLookup().createSerializationContext(
                                    JsonOps.INSTANCE
                            ),
                            json
                    )
                    .getOrThrow(error ->
                            new IllegalStateException(
                                    "Failed to parse corruption effects config '"
                                            + FILE
                                            + "': "
                                            + error
                            )
                    );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to read corruption effects config '"
                            + FILE
                            + "'",
                    exception
            );
        }
    }

    @Override
    protected void apply(
            @NotNull CorruptionEffectsConfig prepared,
            @NotNull ResourceManager manager,
            @NotNull ProfilerFiller profiler
    ) {
        config = prepared;
    }
}
