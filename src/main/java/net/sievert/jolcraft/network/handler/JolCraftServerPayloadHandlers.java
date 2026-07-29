package net.sievert.jolcraft.network.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;

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

        boolean isRateLimited(ServerPlayer player, long currentTick, int maxPerTick) {
            UUID id = player.getUUID();
            Counter c = counters.computeIfAbsent(id, k -> new Counter());
            if (c.tick != currentTick) {
                c.tick = currentTick;
                c.used = 0;
            }

            if (c.used >= maxPerTick) return true;
            c.used++;
            return false;
        }

        void remove(UUID id) {
            counters.remove(id);
        }
    }

    private static final PerTickLimiter PARTICLE_LIMITER = new PerTickLimiter();
    private static final PerTickLimiter SOUND_LIMITER = new PerTickLimiter();

    private static final int MAX_PARTICLE_PACKETS_PER_TICK = 2;
    private static final int MAX_SOUND_PACKETS_PER_TICK = 1;

    public static void cleanupPlayer(ServerPlayer player) {
        UUID id = player.getUUID();
        PARTICLE_LIMITER.remove(id);
        SOUND_LIMITER.remove(id);
    }

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
            String ns = soundId.getNamespace();

            if (!ns.equals(JolCraft.MOD_ID)) {

                JolCraftLogs.debug(
                        JolCraftLogTags.NETWORK,
                        "Blocked sound with unknown namespace '{}' for {}",
                        ns,
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

            final double maxDist = 16.0D;
            if (sp.distanceToSqr(packet.x(), packet.y(), packet.z()) > (maxDist * maxDist)) return;

            var lookup = level.registryAccess().lookupOrThrow(Registries.SOUND_EVENT);
            Optional<Holder.Reference<SoundEvent>> opt = lookup.get(ResourceKey.create(Registries.SOUND_EVENT, soundId));
            if (opt.isEmpty()) return;

            SoundEvent sound = opt.get().value();

            float volume = packet.volume();
            float pitch = packet.pitch();
            if (!Float.isFinite(volume) || !Float.isFinite(pitch)) return;

            volume = Mth.clamp(volume, 0.0F, 2.0F);
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
            if (PARTICLE_LIMITER.isRateLimited(sp, tick, MAX_PARTICLE_PACKETS_PER_TICK)) {
                JolCraftLogs.debug(
                        JolCraftLogTags.NETWORK,
                        "Rate-limited particle packet from {}",
                        player.getGameProfile().getName()
                );
                return;
            }

            BlockPos pos = BlockPos.containing(packet.x(), packet.y(), packet.z());
            if (!level.isLoaded(pos)) return;

            boolean overrideLimiter = packet.overrideLimiter() && sp.hasPermissions(2);
            double maxDist = overrideLimiter ? 64.0D : 16.0D;
            if (sp.distanceToSqr(packet.x(), packet.y(), packet.z()) > (maxDist * maxDist)) return;

            int count = Mth.clamp(packet.count(), 0, 64);

            double xDist = packet.xDist();
            double yDist = packet.yDist();
            double zDist = packet.zDist();
            double speed = packet.speed();

            if (!Double.isFinite(xDist) || !Double.isFinite(yDist) || !Double.isFinite(zDist)) return;
            if (!Double.isFinite(speed)) return;

            xDist = Mth.clamp(xDist, -4.0D, 4.0D);
            yDist = Mth.clamp(yDist, -4.0D, 4.0D);
            zDist = Mth.clamp(zDist, -4.0D, 4.0D);
            speed = Mth.clamp(speed, -2.0D, 2.0D);

            level.sendParticles(
                    packet.particle(),
                    packet.x(),
                    packet.y(),
                    packet.z(),
                    count,
                    xDist,
                    yDist,
                    zDist,
                    speed
            );
        });
    }
}