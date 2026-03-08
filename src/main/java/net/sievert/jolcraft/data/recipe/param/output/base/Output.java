package net.sievert.jolcraft.data.recipe.param.output.base;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpawnConfig;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
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
 *
 * Positioning/spread policy:
 * - Sounds and particles are payload-only.
 * - World placement is resolved by the caller/interpreter.
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

    enum EffectTarget implements JolCraftEnumHelper.StringId {

        PLAYER(JolCraftParameterIds.PLAYER),
        ENTITY(JolCraftParameterIds.ENTITY);

        private final String id;

        EffectTarget(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public static EffectTarget byId(String id) {
            return JolCraftEnumHelper.byStringId(EffectTarget.class, id, PLAYER);
        }
    }

    /**
     * Single mob effect instance.
     */
    record EffectSpec(
            Holder<MobEffect> id,
            int duration,
            int amplifier,
            EffectTarget target
    ) {}

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
     * Single sound payload.
     *
     * Position is caller-owned.
     */
    record Sound(
            @NotNull Holder<SoundEvent> sound,
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
     * Single particle payload.
     *
     * Position and spread are caller-owned.
     */
    record Particle(
            ParticleOptions particle,
            int count,
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
     */
    record EntitySpec(
            Holder<EntityType<?>> type,
            int count,
            @Nullable BlockPos pos,
            @Nullable CompoundTag nbt,
            @Nullable EntitySpawnConfig spawnConfig
    ) {}

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) {
            if (t != null) safe.add(t);
        }
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}