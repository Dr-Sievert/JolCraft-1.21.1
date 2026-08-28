package net.sievert.jolcraft.world.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.effect.custom.beneficial.combat.defence.*;
import net.sievert.jolcraft.world.entity.effect.custom.beneficial.combat.defence.resistance.*;
import net.sievert.jolcraft.world.entity.effect.custom.beneficial.combat.offense.*;
import net.sievert.jolcraft.world.entity.effect.custom.beneficial.utility.*;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.CorrosionEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.SunfireEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.DisarmedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.RootedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.StunnedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.SuppressedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.curse.*;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.vulnerability.*;
import net.sievert.jolcraft.world.entity.effect.custom.neutral.AnchorEffect;

public final class JolCraftEffects {

    private JolCraftEffects() {}

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JolCraft.MOD_ID);

    // -------------------------------------------------------------------------
    // Beneficial
    // -------------------------------------------------------------------------

    //Resistance

    public static final Holder<MobEffect> MAGIC_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.MAGIC_RESISTANCE,
            () -> new MagicResistanceEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("25B7B8"))
                    .addAttributeModifier(
                            JolCraftAttributes.MAGIC_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.MAGIC_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.10D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> EXPLOSION_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.EXPLOSION_RESISTANCE,
            () -> new ExplosionResistanceEffect(MobEffectCategory.BENEFICIAL,JolCraftColors.rgb("796E70"))
                    .addAttributeModifier(
                            JolCraftAttributes.EXPLOSION_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.EXPLOSION_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> POISON_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.POISON_RESISTANCE,
            () -> new PoisonResistanceEffect(MobEffectCategory.BENEFICIAL,JolCraftColors.rgb("538B30"))
                    .addAttributeModifier(
                            JolCraftAttributes.POISON_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.POISON_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> FROST_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.FROST_RESISTANCE,
            () -> new FrostResistanceEffect(MobEffectCategory.BENEFICIAL,JolCraftColors.rgb("01BEF2"))
                    .addAttributeModifier(
                            JolCraftAttributes.FROST_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.FROST_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> WITHER_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.WITHER_RESISTANCE,
            () -> new WitherResistanceEffect(MobEffectCategory.BENEFICIAL,JolCraftColors.rgb("1A1310"))
                    .addAttributeModifier(
                            JolCraftAttributes.WITHER_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.WITHER_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> SLOW_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.SLOW_RESISTANCE,
            () -> new SlowResistanceEffect(MobEffectCategory.BENEFICIAL,JolCraftColors.rgb("575769"))
                    .addAttributeModifier(
                            JolCraftAttributes.SLOW_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.SLOW_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.20D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> TENACITY = MOB_EFFECTS.register(
            JolCraftEffectIds.TENACITY,
            () -> new TenacityEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("1D2734"))
                    .addAttributeModifier(
                            JolCraftAttributes.CROWD_CONTROL_REDUCTION,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.TENACITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> HOMESTEAD = MOB_EFFECTS.register(
            JolCraftEffectIds.HOMESTEAD,
            () -> new HomesteadEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("6E6D6D"))
    );

    public static final Holder<MobEffect> ANCIENT_MEMORY = MOB_EFFECTS.register(
            JolCraftEffectIds.ANCIENT_MEMORY,
            () -> new AncientMemoryEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("8BB386"))
    );

    public static final Holder<MobEffect> LOCKPICKING = MOB_EFFECTS.register(
            JolCraftEffectIds.LOCKPICKING,
            () -> new LockpickingEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("6B6B6B"))
                    .addAttributeModifier(
                            JolCraftAttributes.LOCKPICKING,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.LOCKPICKING, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> DWARVEN_HASTE = MOB_EFFECTS.register(
            JolCraftEffectIds.DWARVEN_HASTE,
            () -> new DwarvenHasteEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("2BC7AC"))
                    .addAttributeModifier(
                            Attributes.BLOCK_BREAK_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.DWARVEN_HASTE, JolCraftDictionary.EFFECT)),
                            0.10D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
                    .addAttributeModifier(
                            Attributes.MINING_EFFICIENCY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.DWARVEN_HASTE, JolCraftDictionary.EFFECT)),
                            2.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> LUMINANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.LUMINANCE,
            () -> new LuminanceEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("FAFF42"))
                    .addAttributeModifier(
                            JolCraftAttributes.LUMINANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.LUMINANCE, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> MOON_SHIELD = MOB_EFFECTS.register(
            JolCraftEffectIds.MOON_SHIELD,
            () -> new MoonShieldEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("ADB4A7"))
    );

    public static final Holder<MobEffect> BULWARK = MOB_EFFECTS.register(
            JolCraftEffectIds.BULWARK,
            () -> new BulwarkEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("777F91"))
                    .addAttributeModifier(
                            Attributes.ARMOR,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.BULWARK, JolCraftDictionary.EFFECT)),
                            0.05D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.ARMOR_TOUGHNESS,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.BULWARK, JolCraftDictionary.EFFECT)),
                            0.05D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> ALCHEMIST_FOCUS = MOB_EFFECTS.register(
            JolCraftEffectIds.ALCHEMIST_FOCUS,
            () -> new AlchemistFocusEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("CA008B"))
                    .addAttributeModifier(
                            JolCraftAttributes.FOCUS,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ALCHEMIST_FOCUS, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> DEXTERITY = MOB_EFFECTS.register(
            JolCraftEffectIds.DEXTERITY,
            () -> new DexterityEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("E2AA65"))
                    .addAttributeModifier(
                            JolCraftAttributes.ITEM_USE_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.DEXTERITY, JolCraftDictionary.EFFECT)),
                            0.20D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> DWARVEN_RAGE = MOB_EFFECTS.register(
            JolCraftEffectIds.DWARVEN_RAGE,
            () -> new DwarvenRageEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("411515"))
                    .addAttributeModifier(
                    Attributes.ATTACK_SPEED,
                    DwarvenRageEffect.DWARVEN_RAGE_EFFECT_MODIFIER_ID,
                    0.0D,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
                    .addAttributeModifier(
                            Attributes.BLOCK_BREAK_SPEED,
                            DwarvenRageEffect.DWARVEN_RAGE_EFFECT_MODIFIER_ID,
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
                    .addAttributeModifier(
                            Attributes.MINING_EFFICIENCY,
                            DwarvenRageEffect.DWARVEN_RAGE_EFFECT_MODIFIER_ID,
                            -3.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> ENDURANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.ENDURANCE,
            () -> new EnduranceEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("9A5E57"))
    );

    public static final Holder<MobEffect> MARKSMAN = MOB_EFFECTS.register(
            JolCraftEffectIds.MARKSMAN,
            () -> new MarksmanEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("15561B"))
                    .addAttributeModifier(
                            JolCraftAttributes.PROJECTILE_DAMAGE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.MARKSMAN, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> STONE_SKIN = MOB_EFFECTS.register(
            JolCraftEffectIds.STONE_SKIN,
            () -> new StoneSkinEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("52555A"))
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.STONE_SKIN, JolCraftDictionary.EFFECT)),
                            -0.1D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> HOARD = MOB_EFFECTS.register(
            JolCraftEffectIds.HOARD,
            () -> new HoardEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("F7BE26"))
                    .addAttributeModifier(
                            JolCraftAttributes.CONTAINER_LOOT_INCREASE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.HOARD, JolCraftDictionary.EFFECT)),
                            0.1D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
                    .addAttributeModifier(
                            Attributes.LUCK,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.HOARD, JolCraftDictionary.EFFECT)),
                            2.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
                    .addAttributeModifier(
                            Attributes.FOLLOW_RANGE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.HOARD, JolCraftDictionary.EFFECT)),
                            0.50D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> PIERCING = MOB_EFFECTS.register(
            JolCraftEffectIds.PIERCING,
            () -> new PiercingEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("828A9B"))
                    .addAttributeModifier(
                            JolCraftAttributes.ARMOR_PENETRATION,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.PIERCING, JolCraftDictionary.EFFECT)),
                            0.20D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> WISDOM = MOB_EFFECTS.register(
            JolCraftEffectIds.WISDOM,
            () -> new WisdomEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("00FF00"))
                    .addAttributeModifier(
                            JolCraftAttributes.EXPERIENCE_INCREASE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.WISDOM, JolCraftDictionary.EFFECT)),
                            0.125D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> MIGHT = MOB_EFFECTS.register(
            JolCraftEffectIds.MIGHT,
            () -> new MightEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("FF6A00"))
                    .addAttributeModifier(
                            Attributes.ATTACK_DAMAGE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.MIGHT, JolCraftDictionary.EFFECT)),
                            0.05,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> HARVEST = MOB_EFFECTS.register(
            JolCraftEffectIds.HARVEST,
            () -> new HarvestEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("247A24"))
                    .addAttributeModifier(
                            JolCraftAttributes.CROP_LOOT_INCREASE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.HARVEST, JolCraftDictionary.EFFECT)),
                            0.125D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> LUNAR = MOB_EFFECTS.register(
            JolCraftEffectIds.LUNAR,
            () -> new LunarEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("ADB4A7"))
                    .addAttributeModifier(
                            JolCraftAttributes.MOON_SHIELD,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.LUNAR, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
                    .addAttributeModifier(
                            Attributes.GRAVITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.LUNAR, JolCraftDictionary.EFFECT)),
                            -0.20D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.SAFE_FALL_DISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.LUNAR, JolCraftDictionary.EFFECT)),
                            0.5D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> CONFLAGRATION = MOB_EFFECTS.register(
            JolCraftEffectIds.CONFLAGRATION,
            () -> new ConflagrationEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("FFAA3F"))
                    .addAttributeModifier(
                            JolCraftAttributes.SUN_FIRE_DAMAGE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.CONFLAGRATION, JolCraftDictionary.EFFECT)),
                            5.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> OVERHEAL = MOB_EFFECTS.register(
            JolCraftEffectIds.OVERHEAL,
            () -> new OverhealEffect(MobEffectCategory.BENEFICIAL, JolCraftColors.rgb("A74F43"))
                    .addAttributeModifier(
                            Attributes.MAX_HEALTH,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.OVERHEAL, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
                    .addAttributeModifier(
                            JolCraftAttributes.MAX_OVERHEAL,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.OVERHEAL, JolCraftDictionary.EFFECT)),
                            0.05D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    // -------------------------------------------------------------------------
    // Neutral
    // -------------------------------------------------------------------------

    public static final Holder<MobEffect> ANCHOR = MOB_EFFECTS.register(
            JolCraftEffectIds.ANCHOR,
            () -> new AnchorEffect(MobEffectCategory.NEUTRAL, JolCraftColors.rgb("4B5660"))
                    .addAttributeModifier(
                            Attributes.KNOCKBACK_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ANCHOR, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
                    .addAttributeModifier(
                            Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ANCHOR, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
                    .addAttributeModifier(
                            Attributes.MOVEMENT_EFFICIENCY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ANCHOR, JolCraftDictionary.EFFECT)),
                            -0.25D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.WATER_MOVEMENT_EFFICIENCY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ANCHOR, JolCraftDictionary.EFFECT)),
                            -0.25D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.GRAVITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ANCHOR, JolCraftDictionary.EFFECT)),
                            0.30D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.SAFE_FALL_DISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ANCHOR, JolCraftDictionary.EFFECT)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
                    .addAttributeModifier(
                            Attributes.FALL_DAMAGE_MULTIPLIER,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ANCHOR, JolCraftDictionary.EFFECT)),
                            0.5D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    // -------------------------------------------------------------------------
    // Harmful
    // -------------------------------------------------------------------------

    // Curses

    private static final int CURSE_COLOR = JolCraftColors.rgb("7510A3");

    public static final Holder<MobEffect> ATAXIA_CURSE = MOB_EFFECTS.register(
            JolCraftEffectIds.ATAXIA_CURSE,
            () -> new AtaxiaCurseEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
    );

    public static final Holder<MobEffect> CURSED_WOUND = MOB_EFFECTS.register(
            JolCraftEffectIds.CURSED_WOUND,
            () -> new CursedWoundEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
    );

    public static final Holder<MobEffect> DELIRIUM_CURSE = MOB_EFFECTS.register(
            JolCraftEffectIds.DELIRIUM_CURSE,
            () -> new DeliriumCurseEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
    );

    public static final Holder<MobEffect> FAMINE_CURSE = MOB_EFFECTS.register(
            JolCraftEffectIds.FAMINE_CURSE,
            () -> new FamineCurseEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
    );

    public static final Holder<MobEffect> FRAILTY_CURSE = MOB_EFFECTS.register(
            JolCraftEffectIds.FRAILTY_CURSE,
            () -> new FrailtyCurseEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
    );

    public static final Holder<MobEffect> VITALITY_CURSE = MOB_EFFECTS.register(
            JolCraftEffectIds.VITALITY_CURSE,
            () -> new VitalityCurseEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
                    .addAttributeModifier(
                            Attributes.MAX_HEALTH,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.VITALITY_CURSE, JolCraftDictionary.EFFECT)),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> HEX = MOB_EFFECTS.register(
            JolCraftEffectIds.HEX,
            () -> new HexEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
                    .addAttributeModifier(
                            JolCraftAttributes.CURSE_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.HEX, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    //Vulnerabilities

    public static final Holder<MobEffect> EXPLOSION_VULNERABILITY = MOB_EFFECTS.register(
            JolCraftEffectIds.EXPLOSION_VULNERABILITY,
            () -> new ExplosionVulnerabilityEffect(MobEffectCategory.HARMFUL,JolCraftColors.rgb("635A5A"))
                    .addAttributeModifier(
                            JolCraftAttributes.EXPLOSION_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.EXPLOSION_VULNERABILITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> FIRE_VULNERABILITY = MOB_EFFECTS.register(
            JolCraftEffectIds.FIRE_VULNERABILITY,
            () -> new FireVulnerabilityEffect(MobEffectCategory.HARMFUL,JolCraftColors.rgb("281B1B"))
                    .addAttributeModifier(
                            JolCraftAttributes.FIRE_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.FIRE_VULNERABILITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> FROST_VULNERABILITY = MOB_EFFECTS.register(
            JolCraftEffectIds.FROST_VULNERABILITY,
            () -> new FrostVulnerabilityEffect(MobEffectCategory.HARMFUL,JolCraftColors.rgb("5EA2B7"))
                    .addAttributeModifier(
                            JolCraftAttributes.FROST_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.FROST_VULNERABILITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> MAGIC_VULNERABILITY = MOB_EFFECTS.register(
            JolCraftEffectIds.MAGIC_VULNERABILITY,
            () -> new MagicVulnerabilityEffect(MobEffectCategory.HARMFUL,JolCraftColors.rgb("2A6D76"))
                    .addAttributeModifier(
                            JolCraftAttributes.MAGIC_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.MAGIC_VULNERABILITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> POISON_VULNERABILITY = MOB_EFFECTS.register(
            JolCraftEffectIds.POISON_VULNERABILITY,
            () -> new PoisonVulnerabilityEffect(MobEffectCategory.HARMFUL,JolCraftColors.rgb("629E31"))
                    .addAttributeModifier(
                            JolCraftAttributes.POISON_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.POISON_VULNERABILITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> SLOW_VULNERABILITY = MOB_EFFECTS.register(
            JolCraftEffectIds.SLOW_VULNERABILITY,
            () -> new SlowVulnerabilityEffect(MobEffectCategory.HARMFUL,JolCraftColors.rgb("974E35"))
                    .addAttributeModifier(
                            JolCraftAttributes.SLOW_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.SLOW_VULNERABILITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> WITHER_VULNERABILITY = MOB_EFFECTS.register(
            JolCraftEffectIds.WITHER_VULNERABILITY,
            () -> new WitherVulnerabilityEffect(MobEffectCategory.HARMFUL,JolCraftColors.rgb("674451"))
                    .addAttributeModifier(
                            JolCraftAttributes.WITHER_VULNERABILITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.WITHER_VULNERABILITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    // Crowd Control

    public static final Holder<MobEffect> DISARMED = MOB_EFFECTS.register(
            JolCraftEffectIds.DISARMED,
            () -> new DisarmedEffect(MobEffectCategory.HARMFUL, JolCraftColors.rgb("820000"))
    );

    public static final Holder<MobEffect> ROOTED = MOB_EFFECTS.register(
            JolCraftEffectIds.ROOTED,
            () -> new RootedEffect(MobEffectCategory.HARMFUL, JolCraftColors.rgb("C4CED2"))
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ROOTED, JolCraftDictionary.EFFECT)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.JUMP_STRENGTH,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ROOTED, JolCraftDictionary.EFFECT)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> STUNNED = MOB_EFFECTS.register(
            JolCraftEffectIds.STUNNED,
            () -> new StunnedEffect(MobEffectCategory.HARMFUL, JolCraftColors.rgb("716A49"))
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.STUNNED, JolCraftDictionary.EFFECT)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.JUMP_STRENGTH,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.STUNNED, JolCraftDictionary.EFFECT)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> SUPPRESSED = MOB_EFFECTS.register(
            JolCraftEffectIds.SUPPRESSED,
            () -> new SuppressedEffect(MobEffectCategory.HARMFUL, JolCraftColors.rgb("820000"))
    );

    // Other

    public static final Holder<MobEffect> CORROSION = MOB_EFFECTS.register(
            JolCraftEffectIds.CORROSION,
            () -> new CorrosionEffect(MobEffectCategory.HARMFUL, JolCraftColors.rgb("398F22"))
                    .addAttributeModifier(
                            Attributes.ARMOR,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.CORROSION, JolCraftDictionary.EFFECT)),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.ARMOR_TOUGHNESS,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.CORROSION, JolCraftDictionary.EFFECT)),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> SUNFIRE = MOB_EFFECTS.register(
            JolCraftEffectIds.SUNFIRE,
            () -> new SunfireEffect(MobEffectCategory.HARMFUL, JolCraftColors.rgb("F4D919"))
    );



    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} mob effects",
                MOB_EFFECTS.getEntries().size()
        );
    }
}
