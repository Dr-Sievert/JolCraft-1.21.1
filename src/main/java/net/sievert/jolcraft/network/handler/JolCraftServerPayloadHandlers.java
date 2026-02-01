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
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;

import java.util.Optional;

public final class JolCraftServerPayloadHandlers {

    private JolCraftServerPayloadHandlers() {}

    public static void handleServerboundDwarfSelectTrade(
            ServerboundDwarfSelectTradePacket packet,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (!(player instanceof ServerPlayer sp)) return;

            if (!(sp.containerMenu instanceof DwarfMerchantMenu menu)) return;

            int selected = packet.item();

            var offers = menu.getOffers();
            if (selected < 0 || selected >= offers.size()) return;

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
            if (!JolCraft.MOD_ID.equals(soundId.getNamespace())) return;

            var level = sp.serverLevel();

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

            BlockPos pos = BlockPos.containing(packet.x(), packet.y(), packet.z());
            if (!level.isLoaded(pos)) return;

            double maxDist = packet.overrideLimiter() ? 512.0D : 32.0D;
            if (sp.distanceToSqr(packet.x(), packet.y(), packet.z()) > (maxDist * maxDist)) return;

            double vx = packet.vx();
            double vy = packet.vy();
            double vz = packet.vz();
            if (!Double.isFinite(vx) || !Double.isFinite(vy) || !Double.isFinite(vz)) return;

            level.sendParticles(
                    packet.particle(),
                    packet.overrideLimiter(),
                    packet.alwaysShow(),
                    packet.x(), packet.y(), packet.z(),
                    0,
                    vx, vy, vz,
                    1.0D
            );
        });
    }
}