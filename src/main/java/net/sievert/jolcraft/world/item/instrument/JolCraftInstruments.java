package net.sievert.jolcraft.world.item.instrument;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Instrument;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.sound.JolCraftSounds;

public final class JolCraftInstruments {

    private static final int USE_DURATION = 120;
    private static final float RANGE = 256.0F;

    public static final DeferredRegister<Instrument> INSTRUMENTS =
            DeferredRegister.create(
                    Registries.INSTRUMENT,
                    JolCraft.MOD_ID
            );

    public static final DeferredHolder<Instrument, Instrument> WAR_HORN =
            INSTRUMENTS.register(
                    JolCraftItemIds.WAR_HORN,
                    () -> new Instrument(
                            JolCraftSounds.WAR_HORN,
                            USE_DURATION,
                            RANGE
                    )
            );

    private JolCraftInstruments() {}

    public static void register(IEventBus eventBus) {
        INSTRUMENTS.register(eventBus);
    }
}