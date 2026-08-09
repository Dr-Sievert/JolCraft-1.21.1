package net.sievert.jolcraft.world.item.potion;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftPotionIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({"SameParameterValue", "unused"})
public final class JolCraftPotions {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, JolCraft.MOD_ID);

    private static final Map<String, Holder<Potion>> POTIONS_BY_ID =
            new LinkedHashMap<>();

    private static final Map<Holder<Potion>, PotionFamily> FAMILIES =
            new LinkedHashMap<>();

    static {
        longFamily(JolCraftPotionIds.ANCIENT_MEMORY, JolCraftEffects.ANCIENT_MEMORY, 300, 600);
        family(JolCraftPotionIds.LOCKPICKING, JolCraftEffects.LOCKPICKING, 600, 1200, 300);
        family(JolCraftPotionIds.DWARVEN_HASTE, JolCraftEffects.DWARVEN_HASTE, 6000, 12000, 3000);
        single(JolCraftPotionIds.STRONG_LUCK, MobEffects.LUCK, 3000, 1);
        family(JolCraftPotionIds.BULWARK, JolCraftEffects.BULWARK, 1200, 2400, 600);
        family(JolCraftPotionIds.ALCHEMIST_FOCUS, JolCraftEffects.ALCHEMIST_FOCUS, 600, 1200, 300);
        longFamily(JolCraftPotionIds.ANCHOR, JolCraftEffects.ANCHOR, 6000, 12000);
        family(JolCraftPotionIds.DEXTERITY, JolCraftEffects.DEXTERITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.DWARVEN_RAGE, JolCraftEffects.DWARVEN_RAGE, 1200, 2400, 600);
        family(JolCraftPotionIds.ENDURANCE, JolCraftEffects.ENDURANCE, 6000, 12000, 3000);
        family(JolCraftPotionIds.EXPLOSION_RESISTANCE, JolCraftEffects.EXPLOSION_RESISTANCE, 3600, 7200, 1800);
        family(JolCraftPotionIds.EXPLOSION_VULNERABILITY, JolCraftEffects.EXPLOSION_VULNERABILITY, 3600, 7200, 1800);
        single(JolCraftPotionIds.STRONG_FIRE_RESISTANCE, MobEffects.FIRE_RESISTANCE, 1800, 1);
        family(JolCraftPotionIds.FIRE_VULNERABILITY, JolCraftEffects.FIRE_VULNERABILITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.FROST_RESISTANCE, JolCraftEffects.FROST_RESISTANCE, 3600, 7200, 1800);
        family(JolCraftPotionIds.FROST_VULNERABILITY, JolCraftEffects.FROST_VULNERABILITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.MAGIC_RESISTANCE, JolCraftEffects.MAGIC_RESISTANCE, 3600, 7200, 1800);
        family(JolCraftPotionIds.MAGIC_VULNERABILITY, JolCraftEffects.MAGIC_VULNERABILITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.POISON_RESISTANCE, JolCraftEffects.POISON_RESISTANCE, 3600, 7200, 1800);
        family(JolCraftPotionIds.POISON_VULNERABILITY, JolCraftEffects.POISON_VULNERABILITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.SLOW_RESISTANCE, JolCraftEffects.SLOW_RESISTANCE, 3600, 7200, 1800);
        family(JolCraftPotionIds.SLOW_VULNERABILITY, JolCraftEffects.SLOW_VULNERABILITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.WITHER_RESISTANCE, JolCraftEffects.WITHER_RESISTANCE, 3600, 7200, 1800);
        family(JolCraftPotionIds.WITHER_VULNERABILITY, JolCraftEffects.WITHER_VULNERABILITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.MARKSMAN, JolCraftEffects.MARKSMAN, 3600, 9600, 1800);
        family(JolCraftPotionIds.STONE_SKIN, JolCraftEffects.STONE_SKIN, 1200, 2400, 600);
        family(JolCraftPotionIds.HOARD, JolCraftEffects.HOARD, 6000, 12000, 3000);
        family(JolCraftPotionIds.PIERCING, JolCraftEffects.PIERCING, 3600, 7200, 1800);
        family(JolCraftPotionIds.TENACITY, JolCraftEffects.TENACITY, 3600, 7200, 1800);
        family(JolCraftPotionIds.WISDOM, JolCraftEffects.WISDOM, 3600, 7200, 1800);
        family(JolCraftPotionIds.MIGHT, JolCraftEffects.MIGHT, 3600, 7200, 1800);
        family(JolCraftPotionIds.HARVEST, JolCraftEffects.HARVEST, 3600, 7200, 1800);
        family(JolCraftPotionIds.LUNAR, JolCraftEffects.LUNAR, 3600, 7200, 1800);
        family(JolCraftPotionIds.CONFLAGRATION, JolCraftEffects.CONFLAGRATION, 3600, 7200, 1800);
        longFamily(JolCraftPotionIds.SUNFIRE, JolCraftEffects.SUNFIRE, 600, 1200);
        family(JolCraftPotionIds.LUMINANCE, JolCraftEffects.LUMINANCE, 3600, 7200, 1800);
        family(JolCraftPotionIds.OVERHEAL, JolCraftEffects.OVERHEAL, 3600, 7200, 1800);
        single(JolCraftPotionIds.ATAXIA_CURSE, JolCraftEffects.ATAXIA_CURSE, 3000);
        single(JolCraftPotionIds.CURSED_WOUND, JolCraftEffects.CURSED_WOUND, 600);
        single(JolCraftPotionIds.DELIRIUM_CURSE, JolCraftEffects.DELIRIUM_CURSE, 3000);
        single(JolCraftPotionIds.FAMINE_CURSE, JolCraftEffects.FAMINE_CURSE, 3000);
        single(JolCraftPotionIds.FRAILTY_CURSE, JolCraftEffects.FRAILTY_CURSE, 3000);
        single(JolCraftPotionIds.HEX, JolCraftEffects.HEX, 1200);
        single(JolCraftPotionIds.VITALITY_CURSE, JolCraftEffects.VITALITY_CURSE, 3000);
        single(JolCraftPotionIds.DISARMED, JolCraftEffects.DISARMED, 200);
        single(JolCraftPotionIds.STUNNED, JolCraftEffects.STUNNED, 200);
        single(JolCraftPotionIds.ROOTED, JolCraftEffects.ROOTED, 200);
        single(JolCraftPotionIds.SUPPRESSED, JolCraftEffects.SUPPRESSED, 200);
        family(JolCraftPotionIds.CORROSION, JolCraftEffects.CORROSION, 1200, 2400, 600);
        strongFamily(JolCraftPotionIds.UNLUCK, MobEffects.UNLUCK, 6000, 3000);
    }

    private JolCraftPotions() {}

    // Beneficial

    public static final Holder<Potion> ANCIENT_MEMORY = holder(JolCraftPotionIds.ANCIENT_MEMORY);
    public static final Holder<Potion> LONG_ANCIENT_MEMORY = holder(JolCraftPotionIds.LONG_ANCIENT_MEMORY);
    public static final Holder<Potion> LOCKPICKING = holder(JolCraftPotionIds.LOCKPICKING);
    public static final Holder<Potion> LONG_LOCKPICKING = holder(JolCraftPotionIds.LONG_LOCKPICKING);
    public static final Holder<Potion> STRONG_LOCKPICKING = holder(JolCraftPotionIds.STRONG_LOCKPICKING);
    public static final Holder<Potion> DWARVEN_HASTE = holder(JolCraftPotionIds.DWARVEN_HASTE);
    public static final Holder<Potion> LONG_DWARVEN_HASTE = holder(JolCraftPotionIds.LONG_DWARVEN_HASTE);
    public static final Holder<Potion> STRONG_DWARVEN_HASTE = holder(JolCraftPotionIds.STRONG_DWARVEN_HASTE);
    public static final Holder<Potion> STRONG_LUCK = holder(JolCraftPotionIds.STRONG_LUCK);
    public static final Holder<Potion> BULWARK = holder(JolCraftPotionIds.BULWARK);
    public static final Holder<Potion> LONG_BULWARK = holder(JolCraftPotionIds.LONG_BULWARK);
    public static final Holder<Potion> STRONG_BULWARK = holder(JolCraftPotionIds.STRONG_BULWARK);
    public static final Holder<Potion> ALCHEMIST_FOCUS = holder(JolCraftPotionIds.ALCHEMIST_FOCUS);
    public static final Holder<Potion> LONG_ALCHEMIST_FOCUS = holder(JolCraftPotionIds.LONG_ALCHEMIST_FOCUS);
    public static final Holder<Potion> STRONG_ALCHEMIST_FOCUS = holder(JolCraftPotionIds.STRONG_ALCHEMIST_FOCUS);
    public static final Holder<Potion> ANCHOR = holder(JolCraftPotionIds.ANCHOR);
    public static final Holder<Potion> LONG_ANCHOR = holder(JolCraftPotionIds.LONG_ANCHOR);
    public static final Holder<Potion> DEXTERITY = holder(JolCraftPotionIds.DEXTERITY);
    public static final Holder<Potion> LONG_DEXTERITY = holder(JolCraftPotionIds.LONG_DEXTERITY);
    public static final Holder<Potion> STRONG_DEXTERITY = holder(JolCraftPotionIds.STRONG_DEXTERITY);
    public static final Holder<Potion> DWARVEN_RAGE = holder(JolCraftPotionIds.DWARVEN_RAGE);
    public static final Holder<Potion> LONG_DWARVEN_RAGE = holder(JolCraftPotionIds.LONG_DWARVEN_RAGE);
    public static final Holder<Potion> STRONG_DWARVEN_RAGE = holder(JolCraftPotionIds.STRONG_DWARVEN_RAGE);
    public static final Holder<Potion> ENDURANCE = holder(JolCraftPotionIds.ENDURANCE);
    public static final Holder<Potion> LONG_ENDURANCE = holder(JolCraftPotionIds.LONG_ENDURANCE);
    public static final Holder<Potion> STRONG_ENDURANCE = holder(JolCraftPotionIds.STRONG_ENDURANCE);

    public static final Holder<Potion> EXPLOSION_RESISTANCE = holder(JolCraftPotionIds.EXPLOSION_RESISTANCE);
    public static final Holder<Potion> LONG_EXPLOSION_RESISTANCE = holder(JolCraftPotionIds.LONG_EXPLOSION_RESISTANCE);
    public static final Holder<Potion> STRONG_EXPLOSION_RESISTANCE = holder(JolCraftPotionIds.STRONG_EXPLOSION_RESISTANCE);

    public static final Holder<Potion> EXPLOSION_VULNERABILITY = holder(JolCraftPotionIds.EXPLOSION_VULNERABILITY);
    public static final Holder<Potion> LONG_EXPLOSION_VULNERABILITY = holder(JolCraftPotionIds.LONG_EXPLOSION_VULNERABILITY);
    public static final Holder<Potion> STRONG_EXPLOSION_VULNERABILITY = holder(JolCraftPotionIds.STRONG_EXPLOSION_VULNERABILITY);

    public static final Holder<Potion> STRONG_FIRE_RESISTANCE = holder(JolCraftPotionIds.STRONG_FIRE_RESISTANCE);

    public static final Holder<Potion> FIRE_VULNERABILITY = holder(JolCraftPotionIds.FIRE_VULNERABILITY);
    public static final Holder<Potion> LONG_FIRE_VULNERABILITY = holder(JolCraftPotionIds.LONG_FIRE_VULNERABILITY);
    public static final Holder<Potion> STRONG_FIRE_VULNERABILITY = holder(JolCraftPotionIds.STRONG_FIRE_VULNERABILITY);

    public static final Holder<Potion> FROST_RESISTANCE = holder(JolCraftPotionIds.FROST_RESISTANCE);
    public static final Holder<Potion> LONG_FROST_RESISTANCE = holder(JolCraftPotionIds.LONG_FROST_RESISTANCE);
    public static final Holder<Potion> STRONG_FROST_RESISTANCE = holder(JolCraftPotionIds.STRONG_FROST_RESISTANCE);

    public static final Holder<Potion> FROST_VULNERABILITY = holder(JolCraftPotionIds.FROST_VULNERABILITY);
    public static final Holder<Potion> LONG_FROST_VULNERABILITY = holder(JolCraftPotionIds.LONG_FROST_VULNERABILITY);
    public static final Holder<Potion> STRONG_FROST_VULNERABILITY = holder(JolCraftPotionIds.STRONG_FROST_VULNERABILITY);

    public static final Holder<Potion> MAGIC_RESISTANCE = holder(JolCraftPotionIds.MAGIC_RESISTANCE);
    public static final Holder<Potion> LONG_MAGIC_RESISTANCE = holder(JolCraftPotionIds.LONG_MAGIC_RESISTANCE);
    public static final Holder<Potion> STRONG_MAGIC_RESISTANCE = holder(JolCraftPotionIds.STRONG_MAGIC_RESISTANCE);

    public static final Holder<Potion> MAGIC_VULNERABILITY = holder(JolCraftPotionIds.MAGIC_VULNERABILITY);
    public static final Holder<Potion> LONG_MAGIC_VULNERABILITY = holder(JolCraftPotionIds.LONG_MAGIC_VULNERABILITY);
    public static final Holder<Potion> STRONG_MAGIC_VULNERABILITY = holder(JolCraftPotionIds.STRONG_MAGIC_VULNERABILITY);

    public static final Holder<Potion> POISON_RESISTANCE = holder(JolCraftPotionIds.POISON_RESISTANCE);
    public static final Holder<Potion> LONG_POISON_RESISTANCE = holder(JolCraftPotionIds.LONG_POISON_RESISTANCE);
    public static final Holder<Potion> STRONG_POISON_RESISTANCE = holder(JolCraftPotionIds.STRONG_POISON_RESISTANCE);

    public static final Holder<Potion> POISON_VULNERABILITY = holder(JolCraftPotionIds.POISON_VULNERABILITY);
    public static final Holder<Potion> LONG_POISON_VULNERABILITY = holder(JolCraftPotionIds.LONG_POISON_VULNERABILITY);
    public static final Holder<Potion> STRONG_POISON_VULNERABILITY = holder(JolCraftPotionIds.STRONG_POISON_VULNERABILITY);

    public static final Holder<Potion> SLOW_RESISTANCE = holder(JolCraftPotionIds.SLOW_RESISTANCE);
    public static final Holder<Potion> LONG_SLOW_RESISTANCE = holder(JolCraftPotionIds.LONG_SLOW_RESISTANCE);
    public static final Holder<Potion> STRONG_SLOW_RESISTANCE = holder(JolCraftPotionIds.STRONG_SLOW_RESISTANCE);

    public static final Holder<Potion> SLOW_VULNERABILITY = holder(JolCraftPotionIds.SLOW_VULNERABILITY);
    public static final Holder<Potion> LONG_SLOW_VULNERABILITY = holder(JolCraftPotionIds.LONG_SLOW_VULNERABILITY);
    public static final Holder<Potion> STRONG_SLOW_VULNERABILITY = holder(JolCraftPotionIds.STRONG_SLOW_VULNERABILITY);

    public static final Holder<Potion> WITHER_RESISTANCE = holder(JolCraftPotionIds.WITHER_RESISTANCE);
    public static final Holder<Potion> LONG_WITHER_RESISTANCE = holder(JolCraftPotionIds.LONG_WITHER_RESISTANCE);
    public static final Holder<Potion> STRONG_WITHER_RESISTANCE = holder(JolCraftPotionIds.STRONG_WITHER_RESISTANCE);

    public static final Holder<Potion> WITHER_VULNERABILITY = holder(JolCraftPotionIds.WITHER_VULNERABILITY);
    public static final Holder<Potion> LONG_WITHER_VULNERABILITY = holder(JolCraftPotionIds.LONG_WITHER_VULNERABILITY);
    public static final Holder<Potion> STRONG_WITHER_VULNERABILITY = holder(JolCraftPotionIds.STRONG_WITHER_VULNERABILITY);

    public static final Holder<Potion> MARKSMAN = holder(JolCraftPotionIds.MARKSMAN);
    public static final Holder<Potion> LONG_MARKSMAN = holder(JolCraftPotionIds.LONG_MARKSMAN);
    public static final Holder<Potion> STRONG_MARKSMAN = holder(JolCraftPotionIds.STRONG_MARKSMAN);
    public static final Holder<Potion> STONE_SKIN = holder(JolCraftPotionIds.STONE_SKIN);
    public static final Holder<Potion> LONG_STONE_SKIN = holder(JolCraftPotionIds.LONG_STONE_SKIN);
    public static final Holder<Potion> STRONG_STONE_SKIN = holder(JolCraftPotionIds.STRONG_STONE_SKIN);
    public static final Holder<Potion> HOARD = holder(JolCraftPotionIds.HOARD);
    public static final Holder<Potion> LONG_HOARD = holder(JolCraftPotionIds.LONG_HOARD);
    public static final Holder<Potion> STRONG_HOARD = holder(JolCraftPotionIds.STRONG_HOARD);
    public static final Holder<Potion> PIERCING = holder(JolCraftPotionIds.PIERCING);
    public static final Holder<Potion> LONG_PIERCING = holder(JolCraftPotionIds.LONG_PIERCING);
    public static final Holder<Potion> STRONG_PIERCING = holder(JolCraftPotionIds.STRONG_PIERCING);
    public static final Holder<Potion> TENACITY = holder(JolCraftPotionIds.TENACITY);
    public static final Holder<Potion> LONG_TENACITY = holder(JolCraftPotionIds.LONG_TENACITY);
    public static final Holder<Potion> STRONG_TENACITY = holder(JolCraftPotionIds.STRONG_TENACITY);
    public static final Holder<Potion> WISDOM = holder(JolCraftPotionIds.WISDOM);
    public static final Holder<Potion> LONG_WISDOM = holder(JolCraftPotionIds.LONG_WISDOM);
    public static final Holder<Potion> STRONG_WISDOM = holder(JolCraftPotionIds.STRONG_WISDOM);
    public static final Holder<Potion> MIGHT = holder(JolCraftPotionIds.MIGHT);
    public static final Holder<Potion> LONG_MIGHT = holder(JolCraftPotionIds.LONG_MIGHT);
    public static final Holder<Potion> STRONG_MIGHT = holder(JolCraftPotionIds.STRONG_MIGHT);
    public static final Holder<Potion> HARVEST = holder(JolCraftPotionIds.HARVEST);
    public static final Holder<Potion> LONG_HARVEST = holder(JolCraftPotionIds.LONG_HARVEST);
    public static final Holder<Potion> STRONG_HARVEST = holder(JolCraftPotionIds.STRONG_HARVEST);
    public static final Holder<Potion> LUNAR = holder(JolCraftPotionIds.LUNAR);
    public static final Holder<Potion> LONG_LUNAR = holder(JolCraftPotionIds.LONG_LUNAR);
    public static final Holder<Potion> STRONG_LUNAR = holder(JolCraftPotionIds.STRONG_LUNAR);
    public static final Holder<Potion> CONFLAGRATION = holder(JolCraftPotionIds.CONFLAGRATION);
    public static final Holder<Potion> LONG_CONFLAGRATION = holder(JolCraftPotionIds.LONG_CONFLAGRATION);
    public static final Holder<Potion> STRONG_CONFLAGRATION = holder(JolCraftPotionIds.STRONG_CONFLAGRATION);
    public static final Holder<Potion> SUNFIRE = holder(JolCraftPotionIds.SUNFIRE);
    public static final Holder<Potion> LONG_SUNFIRE = holder(JolCraftPotionIds.LONG_SUNFIRE);
    public static final Holder<Potion> LUMINANCE = holder(JolCraftPotionIds.LUMINANCE);
    public static final Holder<Potion> LONG_LUMINANCE = holder(JolCraftPotionIds.LONG_LUMINANCE);
    public static final Holder<Potion> STRONG_LUMINANCE = holder(JolCraftPotionIds.STRONG_LUMINANCE);
    public static final Holder<Potion> OVERHEAL = holder(JolCraftPotionIds.OVERHEAL);
    public static final Holder<Potion> LONG_OVERHEAL = holder(JolCraftPotionIds.LONG_OVERHEAL);
    public static final Holder<Potion> STRONG_OVERHEAL = holder(JolCraftPotionIds.STRONG_OVERHEAL);

    // Harmful

    public static final Holder<Potion> ATAXIA_CURSE = holder(JolCraftPotionIds.ATAXIA_CURSE);
    public static final Holder<Potion> CURSED_WOUND = holder(JolCraftPotionIds.CURSED_WOUND);
    public static final Holder<Potion> DELIRIUM_CURSE = holder(JolCraftPotionIds.DELIRIUM_CURSE);
    public static final Holder<Potion> FAMINE_CURSE = holder(JolCraftPotionIds.FAMINE_CURSE);
    public static final Holder<Potion> FRAILTY_CURSE = holder(JolCraftPotionIds.FRAILTY_CURSE);
    public static final Holder<Potion> HEX = holder(JolCraftPotionIds.HEX);
    public static final Holder<Potion> VITALITY_CURSE = holder(JolCraftPotionIds.VITALITY_CURSE);
    public static final Holder<Potion> DISARMED = holder(JolCraftPotionIds.DISARMED);
    public static final Holder<Potion> STUNNED = holder(JolCraftPotionIds.STUNNED);
    public static final Holder<Potion> ROOTED = holder(JolCraftPotionIds.ROOTED);
    public static final Holder<Potion> SUPPRESSED = holder(JolCraftPotionIds.SUPPRESSED);
    public static final Holder<Potion> CORROSION = holder(JolCraftPotionIds.CORROSION);
    public static final Holder<Potion> LONG_CORROSION = holder(JolCraftPotionIds.LONG_CORROSION);
    public static final Holder<Potion> STRONG_CORROSION = holder(JolCraftPotionIds.STRONG_CORROSION);
    public static final Holder<Potion> UNLUCK = holder(JolCraftPotionIds.UNLUCK);
    public static final Holder<Potion> STRONG_UNLUCK = holder(JolCraftPotionIds.STRONG_UNLUCK);

    public static PotionFamily familyOf(Holder<Potion> potion) {
        PotionFamily family = FAMILIES.get(potion);

        if (family == null) {
            throw new IllegalArgumentException(
                    "Potion is not registered to a JolCraft family: " + potion
            );
        }

        return family;
    }

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} potions",
                POTIONS.getEntries().size()
        );
    }

    private static void family(
            String id,
            Holder<MobEffect> effect,
            int duration,
            int longDuration,
            int strongDuration
    ) {
        registerFamily(new PotionFamily(
                registerPotion(id, effect, duration, 0),
                registerPotion(longId(id), effect, longDuration, 0),
                registerPotion(strongId(id), effect, strongDuration, 1)
        ));
    }

    private static void longFamily(
            String id,
            Holder<MobEffect> effect,
            int duration,
            int longDuration
    ) {
        registerFamily(new PotionFamily(
                registerPotion(id, effect, duration, 0),
                registerPotion(longId(id), effect, longDuration, 0),
                null
        ));
    }

    private static void strongFamily(
            String id,
            Holder<MobEffect> effect,
            int duration,
            int strongDuration
    ) {
        registerFamily(new PotionFamily(
                registerPotion(id, effect, duration, 0),
                null,
                registerPotion(strongId(id), effect, strongDuration, 1)
        ));
    }

    private static void single(
            String id,
            Holder<MobEffect> effect,
            int duration
    ) {
        single(id, effect, duration, 0);
    }

    private static void single(
            String id,
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        registerFamily(new PotionFamily(
                registerPotion(id, effect, duration, amplifier),
                null,
                null
        ));
    }

    private static Holder<Potion> registerPotion(
            String id,
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        Holder<Potion> potion = POTIONS.register(
                id,
                () -> new Potion(
                        id,
                        new MobEffectInstance(
                                effect,
                                duration,
                                amplifier
                        )
                )
        );

        POTIONS_BY_ID.put(id, potion);
        return potion;
    }

    private static void registerFamily(PotionFamily family) {
        FAMILIES.put(family.base(), family);

        if (family.longPotion() != null) {
            FAMILIES.put(family.longPotion(), family);
        }

        if (family.strongPotion() != null) {
            FAMILIES.put(family.strongPotion(), family);
        }
    }

    private static Holder<Potion> holder(String id) {
        Holder<Potion> potion = POTIONS_BY_ID.get(id);

        if (potion == null) {
            throw new IllegalStateException(
                    "Potion was not registered: " + id
            );
        }

        return potion;
    }

    private static String longId(String id) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.LONG,
                id
        );
    }

    private static String strongId(String id) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.STRONG,
                id
        );
    }

    public record PotionFamily(
            Holder<Potion> base,
            @Nullable Holder<Potion> longPotion,
            @Nullable Holder<Potion> strongPotion
    ) {}
}
