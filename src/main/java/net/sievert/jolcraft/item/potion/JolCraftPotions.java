package net.sievert.jolcraft.item.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.effect.JolCraftEffects;

public class JolCraftPotions {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, JolCraft.MOD_ID);

    //Beneficial

    public static final Holder<Potion> ANCIENT_MEMORY = POTIONS.register("ancient_memory",
            () -> new Potion("ancient_memory", new MobEffectInstance(JolCraftEffects.ANCIENT_MEMORY, 1200, 0)));

    public static final Holder<Potion> LONG_ANCIENT_MEMORY = POTIONS.register("long_ancient_memory",
            () -> new Potion("long_ancient_memory", new MobEffectInstance(JolCraftEffects.ANCIENT_MEMORY, 2400, 0)));

    public static final Holder<Potion> LOCKPICKING = POTIONS.register("lockpicking",
            () -> new Potion("lockpicking", new MobEffectInstance(JolCraftEffects.LOCKPICKING, 300, 0)));

    public static final Holder<Potion> LONG_LOCKPICKING = POTIONS.register("long_lockpicking",
            () -> new Potion("long_lockpicking", new MobEffectInstance(JolCraftEffects.LOCKPICKING, 600, 0)));

    public static final Holder<Potion> STRONG_LOCKPICKING = POTIONS.register("strong_lockpicking",
            () -> new Potion("strong_lockpicking", new MobEffectInstance(JolCraftEffects.LOCKPICKING, 300, 1)));

    public static final Holder<Potion> DWARVEN_HASTE = POTIONS.register("dwarven_haste",
            () -> new Potion("dwarven_haste", new MobEffectInstance(JolCraftEffects.DWARVEN_HASTE, 3000, 0)));

    public static final Holder<Potion> LONG_DWARVEN_HASTE = POTIONS.register("long_dwarven_haste",
            () -> new Potion("long_dwarven_haste", new MobEffectInstance(JolCraftEffects.DWARVEN_HASTE, 6000, 0)));

    public static final Holder<Potion> STRONG_DWARVEN_HASTE = POTIONS.register("strong_dwarven_haste",
            () -> new Potion("strong_dwarven_haste", new MobEffectInstance(JolCraftEffects.DWARVEN_HASTE, 3000, 1)));

    //Harmful

    public static final Holder<Potion> CURSED_WOUND = POTIONS.register("cursed_wound",
            () -> new Potion("cursed_wound", new MobEffectInstance(JolCraftEffects.CURSED_WOUND, 600, 0)));

    public static final Holder<Potion> DELIRIUM_CURSE = POTIONS.register("delirium_curse",
            () -> new Potion("delirium_curse", new MobEffectInstance(JolCraftEffects.DELIRIUM_CURSE, 3000, 0)));

    public static final Holder<Potion> CORROSION = POTIONS.register("corrosion",
            () -> new Potion("corrosion", new MobEffectInstance(JolCraftEffects.CORROSION, 600, 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}