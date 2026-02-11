package net.sievert.jolcraft.world.item.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftPotionIds;
import net.sievert.jolcraft.world.effect.JolCraftEffects;

public final class JolCraftPotions {

    private JolCraftPotions(){}

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, JolCraft.MOD_ID);

    // Beneficial

    public static final Holder<Potion> ANCIENT_MEMORY = POTIONS.register(JolCraftPotionIds.ANCIENT_MEMORY,
            () -> new Potion(JolCraftPotionIds.ANCIENT_MEMORY,
                    new MobEffectInstance(JolCraftEffects.ANCIENT_MEMORY, 1200, 0)));

    public static final Holder<Potion> LONG_ANCIENT_MEMORY = POTIONS.register(JolCraftPotionIds.LONG_ANCIENT_MEMORY,
            () -> new Potion(JolCraftPotionIds.LONG_ANCIENT_MEMORY,
                    new MobEffectInstance(JolCraftEffects.ANCIENT_MEMORY, 2400, 0)));

    public static final Holder<Potion> LOCKPICKING = POTIONS.register(JolCraftPotionIds.LOCKPICKING,
            () -> new Potion(JolCraftPotionIds.LOCKPICKING,
                    new MobEffectInstance(JolCraftEffects.LOCKPICKING, 300, 0)));

    public static final Holder<Potion> LONG_LOCKPICKING = POTIONS.register(JolCraftPotionIds.LONG_LOCKPICKING,
            () -> new Potion(JolCraftPotionIds.LONG_LOCKPICKING,
                    new MobEffectInstance(JolCraftEffects.LOCKPICKING, 600, 0)));

    public static final Holder<Potion> STRONG_LOCKPICKING = POTIONS.register(JolCraftPotionIds.STRONG_LOCKPICKING,
            () -> new Potion(JolCraftPotionIds.STRONG_LOCKPICKING,
                    new MobEffectInstance(JolCraftEffects.LOCKPICKING, 300, 1)));

    public static final Holder<Potion> DWARVEN_HASTE = POTIONS.register(JolCraftPotionIds.DWARVEN_HASTE,
            () -> new Potion(JolCraftPotionIds.DWARVEN_HASTE,
                    new MobEffectInstance(JolCraftEffects.DWARVEN_HASTE, 3000, 0)));

    public static final Holder<Potion> LONG_DWARVEN_HASTE = POTIONS.register(JolCraftPotionIds.LONG_DWARVEN_HASTE,
            () -> new Potion(JolCraftPotionIds.LONG_DWARVEN_HASTE,
                    new MobEffectInstance(JolCraftEffects.DWARVEN_HASTE, 6000, 0)));

    public static final Holder<Potion> STRONG_DWARVEN_HASTE = POTIONS.register(JolCraftPotionIds.STRONG_DWARVEN_HASTE,
            () -> new Potion(JolCraftPotionIds.STRONG_DWARVEN_HASTE,
                    new MobEffectInstance(JolCraftEffects.DWARVEN_HASTE, 3000, 1)));

    // Harmful

    public static final Holder<Potion> CURSED_WOUND = POTIONS.register(JolCraftPotionIds.CURSED_WOUND,
            () -> new Potion(JolCraftPotionIds.CURSED_WOUND,
                    new MobEffectInstance(JolCraftEffects.CURSED_WOUND, 600, 0)));

    public static final Holder<Potion> DELIRIUM_CURSE = POTIONS.register(JolCraftPotionIds.DELIRIUM_CURSE,
            () -> new Potion(JolCraftPotionIds.DELIRIUM_CURSE,
                    new MobEffectInstance(JolCraftEffects.DELIRIUM_CURSE, 3000, 0)));

    public static final Holder<Potion> CORROSION = POTIONS.register(JolCraftPotionIds.CORROSION,
            () -> new Potion(JolCraftPotionIds.CORROSION,
                    new MobEffectInstance(JolCraftEffects.CORROSION, 300, 0)));

    public static final Holder<Potion> LONG_CORROSION = POTIONS.register(JolCraftPotionIds.LONG_CORROSION,
            () -> new Potion(JolCraftPotionIds.LONG_CORROSION,
                    new MobEffectInstance(JolCraftEffects.CORROSION, 600, 0)));

    public static final Holder<Potion> STRONG_CORROSION = POTIONS.register(JolCraftPotionIds.STRONG_CORROSION,
            () -> new Potion(JolCraftPotionIds.STRONG_CORROSION,
                    new MobEffectInstance(JolCraftEffects.CORROSION, 300, 1)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}