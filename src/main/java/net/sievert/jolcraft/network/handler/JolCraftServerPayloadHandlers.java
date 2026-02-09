package net.sievert.jolcraft.network.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.c2s.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.network.packet.c2s.ServerboundPlaySoundPacket;
import net.sievert.jolcraft.network.packet.c2s.ServerboundSpawnParticlePacket;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class JolCraftServerPayloadHandlers {

    private JolCraftServerPayloadHandlers() {}

    private static final class PerTickLimiter {
        private static final class Counter {
            long tick;
            int used;
        }

        private final Map<UUID, Counter> counters = new HashMap<>();

        /** @return true if this player has exceeded the per-tick limit (i.e., action should be blocked). */
        boolean isRateLimited(ServerPlayer player, long currentTick, int maxPerTick) {
            UUID id = player.getUUID();
            Counter c = counters.computeIfAbsent(id, k -> new Counter());
            if (c.tick != currentTick) {
                c.tick = currentTick;
                c.used = 0;
            }

            if (c.used >= maxPerTick) return true; // BLOCK
            c.used++;
            return false; // ALLOW
        }
    }

    private static final PerTickLimiter PARTICLE_LIMITER = new PerTickLimiter();
    private static final PerTickLimiter SOUND_LIMITER = new PerTickLimiter();

    private static final int MAX_PARTICLE_PACKETS_PER_TICK = 6;
    private static final int MAX_SOUND_PACKETS_PER_TICK = 2;


    public static void handleServerboundDwarfSelectTrade(
            ServerboundDwarfSelectTradePacket packet,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (!(player instanceof ServerPlayer sp)) return;

            if (!(sp.containerMenu instanceof DwarfMerchantMenu menu)){
                JolCraftLogs.debug(
                        JolCraftLogTags.NETWORK,
                        "Ignored dwarf trade select (wrong menu: {} player={})",
                        player.containerMenu.getClass().getSimpleName(),
                        player.getGameProfile().getName()
                );

                return;
            }

            int selected = packet.item();

            var offers = menu.getOffers();
            if (selected < 0 || selected >= offers.size()){
                JolCraftLogs.debug(
                        JolCraftLogTags.NETWORK,
                        "Rejected dwarf trade select (idx={} offers={} player={})",
                        selected, offers.size(), player.getGameProfile().getName()
                );
                return;
            }

            if (!menu.stillValid(sp)) return;

            var trader = menu.getTrader();
            var tradingPlayer = trader.getTradingPlayer();
            if (tradingPlayer != null && tradingPlayer != sp) return;

            menu.setSelectionHint(selected);
            menu.tryMoveItems(selected);
        });
    }

    public static void handleServerboundPlayWorldSound(
            ServerboundPlaySoundPacket packet,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (!(player instanceof ServerPlayer sp)) return;

            ResourceLocation soundId = packet.soundId();
            if (!JolCraft.MOD_ID.equals(soundId.getNamespace())){
                JolCraftLogs.warn(
                        JolCraftLogTags.NETWORK,
                        "Rejected sound packet (non-mod namespace: {}) from {}",
                        soundId,
                        player.getGameProfile().getName()
                );

                return;
            }

            var level = sp.serverLevel();

            long tick = level.getGameTime();
            if (SOUND_LIMITER.isRateLimited(sp, tick, MAX_SOUND_PACKETS_PER_TICK)){
                JolCraftLogs.debug(
                        JolCraftLogTags.NETWORK,
                        "Rate-limited sound packet from {}",
                        player.getGameProfile().getName()
                );

                return;
            }

            BlockPos pos = BlockPos.containing(packet.x(), packet.y(), packet.z());
            if (!level.isLoaded(pos)) return;

            final double maxDist = JolCraftNetworking.DEFAULT_RADIUS;
            if (sp.distanceToSqr(packet.x(), packet.y(), packet.z()) > (maxDist * maxDist)) return;

            var lookup = level.registryAccess().lookupOrThrow(Registries.SOUND_EVENT);
            Optional<Holder.Reference<SoundEvent>> opt = lookup.get(soundId);
            if (opt.isEmpty()) return;

            SoundEvent sound = opt.get().value();

            float volume = packet.volume();
            float pitch = packet.pitch();
            if (!Float.isFinite(volume) || !Float.isFinite(pitch)) return;

            volume = Mth.clamp(volume, 0.0F, 4.0F);
            pitch = Mth.clamp(pitch, 0.5F, 2.0F);

            level.playSound(null, packet.x(), packet.y(), packet.z(), sound, packet.source(), volume, pitch);
        });
    }

    public static void handleServerboundSpawnWorldParticle(
            ServerboundSpawnParticlePacket packet,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (!(player instanceof ServerPlayer sp)) return;

            var level = sp.serverLevel();

            long tick = level.getGameTime();
            if (PARTICLE_LIMITER.isRateLimited(sp, tick, MAX_PARTICLE_PACKETS_PER_TICK)){
                JolCraftLogs.debug(
                        JolCraftLogTags.NETWORK,
                        "Rate-limited particle packet from {}",
                        player.getGameProfile().getName()
                );

                return;
            }

            BlockPos pos = BlockPos.containing(packet.x(), packet.y(), packet.z());
            if (!level.isLoaded(pos)) return;

            boolean overrideLimiter = packet.overrideLimiter();
            if (overrideLimiter && !sp.hasPermissions(2)) {
                JolCraftLogs.warn(
                        JolCraftLogTags.NETWORK,
                        "Rejected particle overrideLimiter from {}",
                        sp.getGameProfile().getName()
                );
                overrideLimiter = false;
            }

            double maxDist = overrideLimiter ? 512.0D : 32.0D;

            if (sp.distanceToSqr(packet.x(), packet.y(), packet.z()) > (maxDist * maxDist)) return;

            double vx = packet.vx();
            double vy = packet.vy();
            double vz = packet.vz();
            if (!Double.isFinite(vx) || !Double.isFinite(vy) || !Double.isFinite(vz)) return;

            level.sendParticles(
                    packet.particle(),
                    overrideLimiter,
                    packet.alwaysShow(),
                    packet.x(), packet.y(), packet.z(),
                    0,
                    vx, vy, vz,
                    1.0D
            );
        });
    }
}