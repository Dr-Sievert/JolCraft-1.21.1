package net.sievert.jolcraft.world.recipe.param.output.base;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntityAttributes;
import net.sievert.jolcraft.world.recipe.param.output.custom.entity.EntitySpawnConfig;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public interface OutputHandler {

    default void handleItems(@NotNull WorldContext ctx, @NotNull Output.Items items) {
        if (!(ctx.player() instanceof ServerPlayer sp)) return;

        for (ItemStack s : items.stacksSafe()) {
            if (s == null || s.isEmpty()) continue;

            JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(sp, s);
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
            if (p == null) continue;

            int count = Math.max(0, p.count());
            if (count == 0) continue;

            double speed = p.speed();
            if (!Double.isFinite(speed) || speed < 0.0D) continue;

            double spreadX = p.spreadX();
            double spreadY = p.spreadY();
            double spreadZ = p.spreadZ();
            if (!Double.isFinite(spreadX) || spreadX < 0.0D) continue;
            if (!Double.isFinite(spreadY) || spreadY < 0.0D) continue;
            if (!Double.isFinite(spreadZ) || spreadZ < 0.0D) continue;

            BlockPos pos = WorldAnchor.resolve(ctx, manualPos, anchor);
            if (pos == null) continue;

            double x = pos.getX() + 0.5D + p.offsetX();
            double y = pos.getY() + 0.5D + p.offsetY();
            double z = pos.getZ() + 0.5D + p.offsetZ();

            JolCraftParticleHelper.spawn(
                    ctx.level(),
                    p.particle(),
                    x,
                    y,
                    z,
                    count,
                    spreadX,
                    spreadY,
                    spreadZ,
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

        int count = Math.max(0, entitySpec.count());
        if (count == 0) return;

        ServerLevel serverLevel = ctx.level();
        EntitySpawnConfig spawn = entitySpec.spawnConfig();

        BlockPos explicitPos = manualPos != null ? manualPos : entitySpec.pos();

        BlockPos basePos;
        if (spawn != null && spawn.forced()) {
            if (anchor != null) {
                basePos = WorldAnchor.forced(ctx, explicitPos, anchor);
            } else {
                basePos = explicitPos;
            }
        } else {
            basePos = WorldAnchor.resolve(ctx, explicitPos, anchor);
        }

        if (basePos == null) {
            return;
        }

        int radius = spawn != null ? Math.max(0, spawn.radius()) : 0;
        int offsetX = spawn != null ? spawn.offsetX() : 0;
        int offsetY = spawn != null ? spawn.offsetY() : 0;
        int offsetZ = spawn != null ? spawn.offsetZ() : 0;

        for (int i = 0; i < count; i++) {
            BlockPos spawnPos = basePos;

            if (radius > 0) {
                int dx = ctx.random().nextInt(radius * 2 + 1) - radius;
                int dz = ctx.random().nextInt(radius * 2 + 1) - radius;
                spawnPos = spawnPos.offset(dx, 0, dz);
            }

            spawnPos = spawnPos.offset(offsetX, offsetY, offsetZ);

            Entity entity = entitySpec.type().value().spawn(
                    serverLevel,
                    spawnPos,
                    MobSpawnType.EVENT
            );
            if (entity == null) continue;

            entity.moveTo(
                    spawnPos.getX() + 0.5D,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5D,
                    entity.getYRot(),
                    entity.getXRot()
            );

            if (entitySpec.name() != null) {
                entity.setCustomName(entitySpec.name());
            }
            entity.setCustomNameVisible(entitySpec.nameVisible());

            EntityAttributes attributes = entitySpec.attributes();
            if (!attributes.isEmpty() && entity instanceof LivingEntity living) {
                for (EntityAttributes.Entry entry : attributes.entries()) {
                    if (entry == null) {
                        continue;
                    }

                    AttributeInstance instance = living.getAttribute(entry.attribute());
                    if (instance == null) {
                        continue;
                    }

                    double sanitized = entry.attribute().value().sanitizeValue(entry.value());
                    instance.setBaseValue(sanitized);
                }
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
            if (e == null) continue;

            try {
                handleEntity(ctx, e, manualPos, anchor);
            } catch (Exception ex) {
                JolCraftLogs.error(
                        JolCraftLogTags.RECIPE,
                        "Failed handling entity output type={} count={} pos={} name={} nameVisible={} spawn={}",
                        e.type(),
                        e.count(),
                        e.pos(),
                        e.name(),
                        e.nameVisible(),
                        e.spawnConfig(),
                        ex
                );
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
            if (o == null) continue;

            try {
                handle(ctx, o, manualPos, anchor, soundSource);
            } catch (Exception ex) {
                JolCraftLogs.error(
                        JolCraftLogTags.RECIPE,
                        " Failed handling output class={} manualPos={} anchor={}",
                        o.getClass().getName(),
                        manualPos,
                        anchor,
                        ex
                );
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