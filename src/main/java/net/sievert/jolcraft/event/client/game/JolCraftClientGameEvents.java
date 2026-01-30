package net.sievert.jolcraft.event.client.game;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.network.data.ClientDeliriumData;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class JolCraftClientGameEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        int prevMuffleTicks = ClientDeliriumData.getAndStorePreviousTicks();
        int currentMuffleTicks = ClientDeliriumData.getMuffleTicks();

        if (prevMuffleTicks == 0 && currentMuffleTicks > 0) {
            if (mc.player != null && mc.level != null) {
                mc.player.playSound(
                        SoundEvents.AMBIENT_CAVE.value(),
                        0.7F + mc.level.random.nextFloat() * 0.4F,
                        0.8F + mc.level.random.nextFloat() * 0.4F
                );
            }
        }

        ClientDeliriumData.tick();
    }

    @SubscribeEvent
    public static void onPlaySound(PlayLevelSoundEvent event) {
        if (ClientDeliriumData.getMuffleTicks() > 0) {
            var soundHolder = event.getSound();
            if (soundHolder == null) return;

            var soundKeyOpt = soundHolder.unwrapKey();
            if (soundKeyOpt.isEmpty()) return;
            var soundLocation = soundKeyOpt.get().location();

            var caveKeyOpt = SoundEvents.AMBIENT_CAVE.unwrapKey();
            if (caveKeyOpt.isEmpty()) return;
            var caveLocation = caveKeyOpt.get().location();

            if (soundLocation.equals(caveLocation)) return;

            event.setNewVolume(event.getOriginalVolume() * 0.3F);
        }
    }

}

