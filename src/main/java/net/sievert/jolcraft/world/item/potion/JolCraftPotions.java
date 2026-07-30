package net.sievert.jolcraft.world.item.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftPotionIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

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

    public static final Holder<Potion> STRONG_LUCK = POTIONS.register(JolCraftPotionIds.STRONG_LUCK,
            () -> new Potion(JolCraftPotionIds.STRONG_LUCK,
                    new MobEffectInstance(MobEffects.LUCK, 6000, 1)));

    // Harmful

    public static final Holder<Potion> ATAXIA_CURSE = POTIONS.register(JolCraftPotionIds.ATAXIA_CURSE,
            () -> new Potion(JolCraftPotionIds.ATAXIA_CURSE,
                    new MobEffectInstance(JolCraftEffects.ATAXIA_CURSE, 3000, 0)));

    public static final Holder<Potion> CURSED_WOUND = POTIONS.register(JolCraftPotionIds.CURSED_WOUND,
            () -> new Potion(JolCraftPotionIds.CURSED_WOUND,
                    new MobEffectInstance(JolCraftEffects.CURSED_WOUND, 600, 0)));

    public static final Holder<Potion> DELIRIUM_CURSE = POTIONS.register(JolCraftPotionIds.DELIRIUM_CURSE,
            () -> new Potion(JolCraftPotionIds.DELIRIUM_CURSE,
                    new MobEffectInstance(JolCraftEffects.DELIRIUM_CURSE, 3000, 0)));

    public static final Holder<Potion> FAMINE_CURSE = POTIONS.register(JolCraftPotionIds.FAMINE_CURSE,
            () -> new Potion(JolCraftPotionIds.FAMINE_CURSE,
                    new MobEffectInstance(JolCraftEffects.FAMINE_CURSE, 3000, 0)));

    public static final Holder<Potion> FRAILTY_CURSE = POTIONS.register(JolCraftPotionIds.FRAILTY_CURSE,
            () -> new Potion(JolCraftPotionIds.FRAILTY_CURSE,
                    new MobEffectInstance(JolCraftEffects.FRAILTY_CURSE, 3000, 0)));

    public static final Holder<Potion> HEX = POTIONS.register(JolCraftPotionIds.HEX,
            () -> new Potion(JolCraftPotionIds.HEX,
                    new MobEffectInstance(JolCraftEffects.HEX, 1200, 0)));

    public static final Holder<Potion> VITALITY_CURSE = POTIONS.register(JolCraftPotionIds.VITALITY_CURSE,
            () -> new Potion(JolCraftPotionIds.VITALITY_CURSE,
                    new MobEffectInstance(JolCraftEffects.VITALITY_CURSE, 3000, 0)));



    public static final Holder<Potion> DISARMED = POTIONS.register(JolCraftPotionIds.DISARMED,
            () -> new Potion(JolCraftPotionIds.DISARMED,
                    new MobEffectInstance(JolCraftEffects.DISARMED, 200, 0)));

    public static final Holder<Potion> STUNNED = POTIONS.register(JolCraftPotionIds.STUNNED,
            () -> new Potion(JolCraftPotionIds.STUNNED,
                    new MobEffectInstance(JolCraftEffects.STUNNED, 200, 0)));

    public static final Holder<Potion> ROOTED = POTIONS.register(JolCraftPotionIds.ROOTED,
            () -> new Potion(JolCraftPotionIds.ROOTED,
                    new MobEffectInstance(JolCraftEffects.ROOTED, 200, 0)));

    public static final Holder<Potion> SUPPRESSED = POTIONS.register(JolCraftPotionIds.SUPPRESSED,
            () -> new Potion(JolCraftPotionIds.SUPPRESSED,
                    new MobEffectInstance(JolCraftEffects.SUPPRESSED, 200, 0)));

    public static final Holder<Potion> CORROSION = POTIONS.register(JolCraftPotionIds.CORROSION,
            () -> new Potion(JolCraftPotionIds.CORROSION,
                    new MobEffectInstance(JolCraftEffects.CORROSION, 300, 0)));

    public static final Holder<Potion> LONG_CORROSION = POTIONS.register(JolCraftPotionIds.LONG_CORROSION,
            () -> new Potion(JolCraftPotionIds.LONG_CORROSION,
                    new MobEffectInstance(JolCraftEffects.CORROSION, 600, 0)));

    public static final Holder<Potion> STRONG_CORROSION = POTIONS.register(JolCraftPotionIds.STRONG_CORROSION,
            () -> new Potion(JolCraftPotionIds.STRONG_CORROSION,
                    new MobEffectInstance(JolCraftEffects.CORROSION, 300, 1)));

    public static final Holder<Potion> UNLUCK = POTIONS.register(JolCraftPotionIds.UNLUCK,
            () -> new Potion(JolCraftPotionIds.UNLUCK,
                    new MobEffectInstance(MobEffects.UNLUCK, 6000, 0)));

    public static final Holder<Potion> STRONG_UNLUCK = POTIONS.register(JolCraftPotionIds.STRONG_UNLUCK,
            () -> new Potion(JolCraftPotionIds.STRONG_UNLUCK,
                    new MobEffectInstance(MobEffects.UNLUCK, 6000, 1)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} potions",
                POTIONS.getEntries().size()
        );
    }
}