package net.sievert.jolcraft.world.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.sound.JolCraftSoundIds;

import java.util.function.Supplier;

@SuppressWarnings("SameParameterValue")
public final class JolCraftSounds {

    private JolCraftSounds() {}

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, JolCraft.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ARMOR_EQUIP_DEEPSLATE =
            registerSoundEventHolder(JolCraftSoundIds.ARMOR_EQUIP_DEEPSLATE);

    // Random
    public static final Supplier<SoundEvent> LEVEL_UP = registerSoundEvent(JolCraftSoundIds.LEVEL_UP);

    // Blocks
    public static final Supplier<SoundEvent> STRONGBOX_OPEN = registerSoundEvent(JolCraftSoundIds.STRONGBOX_OPEN);
    public static final Supplier<SoundEvent> STRONGBOX_CLOSE = registerSoundEvent(JolCraftSoundIds.STRONGBOX_CLOSE);
    public static final Supplier<SoundEvent> STRONGBOX_LOCKPICK = registerSoundEvent(JolCraftSoundIds.STRONGBOX_LOCKPICK);
    public static final Supplier<SoundEvent> STRONGBOX_LOCKPICK_BREAK = registerSoundEvent(JolCraftSoundIds.STRONGBOX_LOCKPICK_BREAK);
    public static final Supplier<SoundEvent> STRONGBOX_UNLOCK = registerSoundEvent(JolCraftSoundIds.STRONGBOX_UNLOCK);
    public static final Supplier<SoundEvent> GEM_CUT = registerSoundEvent(JolCraftSoundIds.GEM_CUT);

    // Items
    public static final Supplier<SoundEvent> COIN_STACK = registerSoundEvent(JolCraftSoundIds.COIN_STACK);
    public static final Supplier<SoundEvent> COIN_SINGLE = registerSoundEvent(JolCraftSoundIds.COIN_SINGLE);

    // Entity
    public static final Supplier<SoundEvent> DWARF_AMBIENT = registerSoundEvent(JolCraftSoundIds.DWARF_AMBIENT);
    public static final Supplier<SoundEvent> DWARF_HURT = registerSoundEvent(JolCraftSoundIds.DWARF_HURT);
    public static final Supplier<SoundEvent> DWARF_DEATH = registerSoundEvent(JolCraftSoundIds.DWARF_DEATH);
    public static final Supplier<SoundEvent> DWARF_YES = registerSoundEvent(JolCraftSoundIds.DWARF_YES);
    public static final Supplier<SoundEvent> DWARF_NO = registerSoundEvent(JolCraftSoundIds.DWARF_NO);
    public static final Supplier<SoundEvent> DWARF_TRADE = registerSoundEvent(JolCraftSoundIds.DWARF_TRADE);

    // Curse
    public static final Supplier<SoundEvent> CURSE = registerSoundEvent(JolCraftSoundIds.CURSE);

    private static Supplier<SoundEvent> registerSoundEvent(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(JolCraft.location(id)));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEventHolder(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(JolCraft.location(id)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}