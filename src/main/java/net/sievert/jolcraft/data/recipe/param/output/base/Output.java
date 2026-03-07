package net.sievert.jolcraft.data.recipe.param.output.base;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpawnConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime-only produced output envelope.
 *
 * Not a param:
 * - no CODEC / STREAM_CODEC
 * - no validation
 *
 * Purpose:
 * - Pools need ONE return type while supporting heterogeneous outputs (items/sound/xp/etc).
 */
public sealed interface Output permits
        Output.Empty,
        Output.Items,
        Output.Effects,
        Output.Sounds,
        Output.Particles,
        Output.Text,
        Output.Entities {

    Output EMPTY = Empty.INSTANCE;

    /**
     * No output (convenience singleton).
     */
    record Empty() implements Output {
        public static final Empty INSTANCE = new Empty();
    }

    /**
     * Produced item stacks.
     */
    record Items(List<ItemStack> stacks) implements Output {

        public Items {
            stacks = (stacks == null || stacks.isEmpty()) ? List.of() : sanitizeList(stacks);
        }

        public @NotNull List<ItemStack> stacksSafe() {
            return stacks != null ? stacks : List.of();
        }
    }

    /**
     * Produced mob effects to apply.
     */
    record Effects(List<EffectSpec> effectSpecs) implements Output {

        public Effects {
            effectSpecs = (effectSpecs == null || effectSpecs.isEmpty()) ? List.of() : sanitizeList(effectSpecs);
        }

        public @NotNull List<EffectSpec> effectsSafe() {
            return effectSpecs != null ? effectSpecs : List.of();
        }
    }

    /**
     * Single mob effect instance.
     */
    record EffectSpec(Holder<MobEffect> id, int duration, int amplifier) {}

    /**
     * Produced sound events to play.
     */
    record Sounds(List<Sound> sounds) implements Output {

        public Sounds {
            sounds = (sounds == null || sounds.isEmpty()) ? List.of() : sanitizeList(sounds);
        }

        public @NotNull List<Sound> soundsSafe() {
            return sounds != null ? sounds : List.of();
        }
    }

    /**
     * Single sound instance.
     *
     * If anchor != null, runtime may auto-play using {@link WorldAnchor#resolve(WorldContext)}.
     * If anchor == null, caller can decide how/where to play.
     */
    record Sound(
            Holder<SoundEvent> sound,
            @Nullable WorldAnchor anchor,
            float volume,
            float pitch
    ) {}

    /**
     * Produced particles to spawn.
     */
    record Particles(List<Particle> particles) implements Output {

        public Particles {
            particles = (particles == null || particles.isEmpty()) ? List.of() : sanitizeList(particles);
        }

        public @NotNull List<Particle> particlesSafe() {
            return particles != null ? particles : List.of();
        }
    }

    /**
     * Single particle spawn instruction.
     *
     * If anchor != null, runtime may auto-spawn using {@link WorldAnchor#resolve(WorldContext)}.
     * If anchor == null, caller can decide where/how to spawn.
     */
    record Particle(
            ParticleOptions particle,
            int count,
            @Nullable WorldAnchor anchor,
            float spreadX,
            float spreadY,
            float spreadZ,
            float speed
    ) {}

    /**
     * Produced text/messages to display.
     */
    record Text(List<Message> messages) implements Output {

        public Text {
            messages = (messages == null || messages.isEmpty()) ? List.of() : sanitizeList(messages);
        }

        public @NotNull List<Message> messagesSafe() {
            return messages != null ? messages : List.of();
        }
    }

    /**
     * Single message instruction.
     */
    record Message(String text, List<ChatFormatting> style, boolean overlay) {}

    /**
     * Produced entity instructions (generic).
     *
     * Can be interpreted as:
     * - "spawn these entities" (future)
     * - "require killing these entities" (bounty objective)
     *
     * Interpretation is decided by the caller.
     */
    record Entities(List<EntitySpec> entities) implements Output {

        public Entities {
            entities = (entities == null || entities.isEmpty()) ? List.of() : sanitizeList(entities);
        }

        public @NotNull List<EntitySpec> entitiesSafe() {
            return entities != null ? entities : List.of();
        }
    }

    /**
     * Single entity instruction.
     *
     * If anchor != null, runtime may resolve a position using {@link WorldAnchor#resolve(WorldContext)}.
     * If anchor == null, caller can decide where/how to use it.
     *
     * spawnConfig is optional metadata for future spawn behavior.
     * nbt is optional entity data payload for future spawning.
     */
    record EntitySpec(
            Holder<EntityType<?>> type,
            int count,
            @Nullable WorldAnchor anchor,
            @Nullable CompoundTag nbt,
            @Nullable EntitySpawnConfig spawnConfig
    ) {}

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}