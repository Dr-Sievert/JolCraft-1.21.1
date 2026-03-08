package net.sievert.jolcraft.data.recipe.param.output.base;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpawnConfig;
import net.sievert.jolcraft.world.inventory.ItemInsertionHelper;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Output interpretation contract with overridable defaults.
 *
 * Implementors may override only the output types they care about.
 * The default behavior follows safe, vanilla-like semantics and
 * resolves spatial outputs through {@link WorldAnchor} or the
 * provided manual position.
 *
 * Default policies:
 *
 * • Items
 *   Attempts to give items to the player (ctx.player()).
 *   If the inventory is full or no player exists, items are dropped.
 *
 * • Sounds
 *   Played at the resolved position:
 *   manualPos → anchor → ctx.pos().
 *   Uses the provided SoundSource or a reasonable default.
 *
 * • Particles
 *   Spawned at the resolved position:
 *   manualPos → anchor → ctx.pos().
 *
 * • Effects
 *   Applied to the player (ctx.player()) if present.
 *
 * • Text
 *   Sent to the player (ctx.player()) if present.
 *
 * • Entities
 *   Spawned on the server at the resolved position:
 *   manualPos → anchor → ctx.pos().
 *
 * The dispatch entrypoints are:
 *
 *     handle(ctx, output)
 *     handle(ctx, output, manualPos)
 *     handle(ctx, output, manualPos, anchor)
 *     handle(ctx, output, manualPos, anchor, soundSource)
 *
 * The full signature exposes all spatial and sound controls.
 */
@SuppressWarnings("unused")
public interface OutputHandler {

    default void handleItems(@NotNull WorldContext ctx, @NotNull Output.Items items) {
        if (!(ctx.player() instanceof ServerPlayer sp)) return;

        for (ItemStack s : items.stacksSafe()) {
            if (s == null || s.isEmpty()) continue;

            ItemInsertionHelper.tryInsertIntoInventoryOrDrop(sp, s);
        }
    }

    default void handleSounds(
            @NotNull WorldContext ctx,
            @NotNull Output.Sounds sounds,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor,
            @Nullable SoundSource source
    ) {
        for (Output.Sound s : sounds.soundsSafe()) {
            if (s == null) continue;

            float vol = s.volume();
            float pit = s.pitch();
            if (!Float.isFinite(vol) || vol < 0.0F) continue;
            if (!Float.isFinite(pit) || pit <= 0.0F) continue;

            if (manualPos == null && anchor != null) {
                switch (anchor) {
                    case PLAYER -> {
                        Player player = ctx.player();
                        if (player != null) {
                            JolCraftSoundHelper.player(player, s.sound().value(), vol, pit);
                        }
                        continue;
                    }
                    case ENTITY -> {
                        Entity entity = ctx.entity();
                        if (entity instanceof LivingEntity living) {
                            JolCraftSoundHelper.entity(living, s.sound().value(), vol, pit);
                        }
                        continue;
                    }
                }
            }

            BlockPos pos = WorldAnchor.resolve(ctx, manualPos, anchor);
            if (pos == null) continue;

            JolCraftSoundHelper.position(
                    ctx.level(),
                    pos,
                    s.sound().value(),
                    source != null ? source : SoundSource.BLOCKS,
                    vol,
                    pit
            );
        }
    }

    default void handleParticles(
            @NotNull WorldContext ctx,
            @NotNull Output.Particles particles,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor
    ) {
        for (Output.Particle p : particles.particlesSafe()) {
            if (p == null || p.particle() == null) continue;

            int count = Math.max(0, p.count());
            if (count == 0) continue;

            double speed = p.speed();
            if (!Double.isFinite(speed) || speed < 0.0D) continue;

            BlockPos pos = WorldAnchor.resolve(ctx, manualPos, anchor);
            if (pos == null) continue;

            JolCraftParticleHelper.spawn(
                    ctx.level(),
                    p.particle(),
                    pos,
                    count,
                    0.0D, 0.0D, 0.0D,
                    speed
            );
        }
    }

    default void handleEffects(@NotNull WorldContext ctx, @NotNull Output.Effects effects) {
        for (Output.EffectSpec e : effects.effectsSafe()) {
            if (e == null || e.id() == null || e.target() == null) continue;

            int dur = e.duration();
            int amp = e.amplifier();
            if (dur <= 0) continue;
            if (amp < 0) amp = 0;

            switch (e.target()) {
                case PLAYER -> {
                    if (ctx.player() instanceof ServerPlayer sp) {
                        sp.addEffect(new MobEffectInstance(e.id(), dur, amp));
                    }
                }
                case ENTITY -> {
                    if (ctx.entity() instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(e.id(), dur, amp));
                    }
                }
            }
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

    default void handleEntity(
            @NotNull WorldContext ctx,
            @NotNull Output.EntitySpec entitySpec,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor
    ) {
        if (entitySpec.type() == null) return;

        int count = Math.max(0, entitySpec.count());
        if (count == 0) return;

        BlockPos pos = WorldAnchor.resolve(
                ctx,
                manualPos != null ? manualPos : entitySpec.pos(),
                anchor
        );
        if (pos == null) return;

        ServerLevel serverLevel = ctx.level();

        EntitySpawnConfig spawn = entitySpec.spawnConfig();

        int offsetX = spawn != null ? spawn.offsetX() : 0;
        int offsetY = spawn != null ? spawn.offsetY() : 0;
        int offsetZ = spawn != null ? spawn.offsetZ() : 0;

        BlockPos spawnPos = pos.offset(offsetX, offsetY, offsetZ);

        for (int i = 0; i < count; i++) {
            Entity entity = entitySpec.type().value().spawn(
                    serverLevel,
                    spawnPos,
                    EntitySpawnReason.EVENT
            );
            if (entity == null) continue;

            CompoundTag nbt = entitySpec.nbt();
            if (nbt != null && !nbt.isEmpty()) {
                entity.load(nbt);
                entity.moveTo(
                        spawnPos.getX() + 0.5D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5D,
                        entity.getYRot(),
                        entity.getXRot()
                );
            }

            if (spawn != null && entity instanceof Mob mob) {
                if (spawn.persistent()) {
                    mob.setPersistenceRequired();
                }
                if (spawn.noAi()) {
                    mob.setNoAi(true);
                }
            }
        }
    }

    default void handleEntities(
            @NotNull WorldContext ctx,
            @NotNull Output.Entities entities,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor
    ) {
        for (Output.EntitySpec e : entities.entitiesSafe()) {
            if (e != null) {
                handleEntity(ctx, e, manualPos, anchor);
            }
        }
    }

    default void handle(
            @NotNull WorldContext ctx,
            @NotNull Output out,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor,
            @Nullable SoundSource soundSource
    ) {
        if (out instanceof Output.Empty) return;

        switch (out) {
            case Output.Items items -> handleItems(ctx, items);
            case Output.Sounds sounds -> handleSounds(ctx, sounds, manualPos, anchor, soundSource);
            case Output.Particles particles -> handleParticles(ctx, particles, manualPos, anchor);
            case Output.Effects effects -> handleEffects(ctx, effects);
            case Output.Text text -> handleText(ctx, text);
            case Output.Entities entities -> handleEntities(ctx, entities, manualPos, anchor);

            //noinspection DataFlowIssue
            case Output.Empty ignored -> {}
        }
    }

    default void handle(
            @NotNull WorldContext ctx,
            @NotNull Output out,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor
    ) {
        handle(ctx, out, manualPos, anchor, null);
    }

    default void handle(
            @NotNull WorldContext ctx,
            @NotNull Output out,
            @Nullable BlockPos manualPos
    ) {
        handle(ctx, out, manualPos, null, null);
    }

    default void handle(@NotNull WorldContext ctx, @NotNull Output out) {
        handle(ctx, out, null, null, null);
    }

    default void handleAll(
            @NotNull WorldContext ctx,
            @NotNull List<Output> outputs,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor,
            @Nullable SoundSource soundSource
    ) {
        if (outputs.isEmpty()) return;

        for (Output o : outputs) {
            if (o != null) {
                handle(ctx, o, manualPos, anchor, soundSource);
            }
        }
    }

    default void handleAll(
            @NotNull WorldContext ctx,
            @NotNull List<Output> outputs,
            @Nullable BlockPos manualPos,
            @Nullable WorldAnchor anchor
    ) {
        handleAll(ctx, outputs, manualPos, anchor, null);
    }

    default void handleAll(
            @NotNull WorldContext ctx,
            @NotNull List<Output> outputs,
            @Nullable BlockPos manualPos
    ) {
        handleAll(ctx, outputs, manualPos, null, null);
    }

    default void handleAll(@NotNull WorldContext ctx, @NotNull List<Output> outputs) {
        handleAll(ctx, outputs, null, null, null);
    }
}