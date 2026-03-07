package net.sievert.jolcraft.data.recipe.param.output.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.inventory.ItemInsertionHelper;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Output interpretation contract with overridable defaults.
 *
 * Callers may override only the parts they care about.
 * Default policy is "reasonable vanilla-like":
 * - Items: try give to player, drop if full
 * - Sounds: play at anchor or ctx.pos
 * - Particles: spawn at anchor or ctx.pos
 * - Effects: apply to player
 * - Text: show to player
 * - Entities: no-op by default
 */
public interface OutputHandler {

    default void handleItems(@NotNull WorldContext ctx, @NotNull Output.Items items) {
        if (!(ctx.player() instanceof ServerPlayer sp)) return;

        for (ItemStack s : items.stacksSafe()) {
            if (s == null || s.isEmpty()) continue;

            ItemInsertionHelper.tryInsertIntoInventoryOrDrop(sp, s);
        }
    }

    default void handleSounds(@NotNull WorldContext ctx, @NotNull Output.Sounds sounds) {
        if (!(ctx.player() instanceof ServerPlayer sp)) return;

        for (Output.Sound s : sounds.soundsSafe()) {
            if (s == null || s.sound() == null) continue;

            float vol = s.volume();
            float pit = s.pitch();
            if (!Float.isFinite(vol) || vol < 0.0F) continue;
            if (!Float.isFinite(pit) || pit <= 0.0F) continue;

            JolCraftSoundHelper.player(sp, s.sound().value(), vol, pit);
        }
    }

    default void handleParticles(@NotNull WorldContext ctx, @NotNull Output.Particles particles) {
        for (Output.Particle p : particles.particlesSafe()) {
            if (p == null || p.particle() == null) continue;

            int count = Math.max(0, p.count());
            if (count == 0) continue;

            Vec3 at = WorldAnchor.resolveCenterOrPlayer(p.anchor(), ctx);

            double sx = p.spreadX();
            double sy = p.spreadY();
            double sz = p.spreadZ();
            double speed = p.speed();

            if (!Double.isFinite(sx) || !Double.isFinite(sy) || !Double.isFinite(sz) || !Double.isFinite(speed)) {
                continue;
            }

            JolCraftParticleHelper.spawn(
                    ctx.level(),
                    p.particle(),
                    at.x, at.y, at.z,
                    count,
                    sx, sy, sz,
                    speed
            );
        }
    }

    default void handleEffects(@NotNull WorldContext ctx, @NotNull Output.Effects effects) {
        if (!(ctx.player() instanceof ServerPlayer sp)) return;

        for (Output.EffectSpec e : effects.effectsSafe()) {
            if (e == null || e.id() == null) continue;

            int dur = e.duration();
            int amp = e.amplifier();
            if (dur <= 0) continue;
            if (amp < 0) amp = 0;

            sp.addEffect(new MobEffectInstance(e.id(), dur, amp));
        }
    }

    default void handleText(@NotNull WorldContext ctx, @NotNull Output.Text text) {
        if (!(ctx.player() instanceof ServerPlayer sp)) return;

        for (Output.Message m : text.messagesSafe()) {
            if (m == null) continue;

            String raw = m.text();
            if (raw == null || raw.isBlank()) continue;

            Component c = Component.literal(raw);

            List<ChatFormatting> style = m.style();
            if (style != null && !style.isEmpty()) {
                for (ChatFormatting f : style) {
                    if (f != null) c = c.copy().withStyle(f);
                }
            }

            sp.displayClientMessage(c, m.overlay());
        }
    }

    default void handleEntity(@NotNull WorldContext ctx, @NotNull Output.EntitySpec entitySpec) {
        // no-op by default (caller decides interpretation/spawning)
    }

    default void handle(@NotNull WorldContext ctx, @NotNull Output out) {
        if (out instanceof Output.Empty) return;

        switch (out) {
            case Output.Items items -> handleItems(ctx, items);
            case Output.Sounds sounds -> handleSounds(ctx, sounds);
            case Output.Particles particles -> handleParticles(ctx, particles);
            case Output.Effects effects -> handleEffects(ctx, effects);
            case Output.Text text -> handleText(ctx, text);
            case Output.Entities entities -> {
                for (Output.EntitySpec e : entities.entitiesSafe()) {
                    if (e != null) handleEntity(ctx, e);
                }
            }

            //noinspection DataFlowIssue
            case Output.Empty ignored -> {}
        }
    }

    default void handleAll(@NotNull WorldContext ctx, @NotNull List<Output> outputs) {
        if (outputs.isEmpty()) return;
        for (Output o : outputs) {
            if (o != null) handle(ctx, o);
        }
    }
}