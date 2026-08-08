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
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.effect.custom.beneficial.*;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.CorrosionEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.SunfireEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.DisarmedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.RootedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.StunnedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.crowd_control.SuppressedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.curse.*;

public final class JolCraftEffects {

    private JolCraftEffects() {}

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JolCraft.MOD_ID);

    // -------------------------------------------------------------------------
    // Beneficial
    // -------------------------------------------------------------------------

    //Resistance

    public static final Holder<MobEffect> MAGIC_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.MAGIC_RESISTANCE,
            () -> new MagicResistanceEffect(MobEffectCategory.BENEFICIAL, 0x25b7b8)
                    .addAttributeModifier(
                            JolCraftAttributes.MAGIC_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.MAGIC_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.10D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> POISON_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.POISON_RESISTANCE,
            () -> new PoisonResistanceEffect(MobEffectCategory.BENEFICIAL,0x538b30)
                    .addAttributeModifier(
                            JolCraftAttributes.POISON_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.POISON_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> SLOW_RESISTANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.SLOW_RESISTANCE,
            () -> new SlowResistanceEffect(MobEffectCategory.BENEFICIAL,0x575769)
                    .addAttributeModifier(
                            JolCraftAttributes.SLOW_RESISTANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.SLOW_RESISTANCE, JolCraftDictionary.EFFECT)),
                            0.20D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> TENACITY = MOB_EFFECTS.register(
            JolCraftEffectIds.TENACITY,
            () -> new TenacityEffect(MobEffectCategory.BENEFICIAL, 0x1d2734)
                    .addAttributeModifier(
                            JolCraftAttributes.TENACITY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.TENACITY, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> HOMESTEAD = MOB_EFFECTS.register(
            JolCraftEffectIds.HOMESTEAD,
            () -> new HomesteadEffect(MobEffectCategory.BENEFICIAL, 0x6e6d6d)
    );

    public static final Holder<MobEffect> ANCIENT_MEMORY = MOB_EFFECTS.register(
            JolCraftEffectIds.ANCIENT_MEMORY,
            () -> new AncientMemoryEffect(MobEffectCategory.BENEFICIAL, 0x8bb386)
    );

    public static final Holder<MobEffect> LOCKPICKING = MOB_EFFECTS.register(
            JolCraftEffectIds.LOCKPICKING,
            () -> new LockpickingEffect(MobEffectCategory.BENEFICIAL, 0x6b6b6b)
                    .addAttributeModifier(
                            JolCraftAttributes.LOCKPICKING,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.LOCKPICKING, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> DWARVEN_HASTE = MOB_EFFECTS.register(
            JolCraftEffectIds.DWARVEN_HASTE,
            () -> new DwarvenHasteEffect(MobEffectCategory.BENEFICIAL, 0x2bc7ac)
                    .addAttributeModifier(
                            Attributes.BLOCK_BREAK_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.DWARVEN_HASTE, JolCraftDictionary.EFFECT)),
                            0.20D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
                    .addAttributeModifier(
                            Attributes.MINING_EFFICIENCY,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.DWARVEN_HASTE, JolCraftDictionary.EFFECT)),
                            3.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> LUMINANCE = MOB_EFFECTS.register(
            JolCraftEffectIds.LUMINANCE,
            () -> new LuminanceEffect(MobEffectCategory.BENEFICIAL, 0xfaff42)
                    .addAttributeModifier(
                            JolCraftAttributes.LUMINANCE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.LUMINANCE, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> MOON_SHIELD = MOB_EFFECTS.register(
            JolCraftEffectIds.MOON_SHIELD,
            () -> new MoonShieldEffect(MobEffectCategory.BENEFICIAL, 0xadb4a7)
    );

    public static final Holder<MobEffect> BULWARK = MOB_EFFECTS.register(
            JolCraftEffectIds.BULWARK,
            () -> new BulwarkEffect(MobEffectCategory.BENEFICIAL, 0x777f91)
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
            () -> new AlchemistFocusEffect(MobEffectCategory.BENEFICIAL, 0xca008b)
                    .addAttributeModifier(
                            JolCraftAttributes.FOCUS,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.ALCHEMIST_FOCUS, JolCraftDictionary.EFFECT)),
                            0.25D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> ANCHOR = MOB_EFFECTS.register(
            JolCraftEffectIds.ANCHOR,
            () -> new AnchorEffect(MobEffectCategory.BENEFICIAL, 0x4b5660)
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

    public static final Holder<MobEffect> DEXTERITY = MOB_EFFECTS.register(
            JolCraftEffectIds.DEXTERITY,
            () -> new DexterityEffect(MobEffectCategory.BENEFICIAL, 0xe2aa65)
                    .addAttributeModifier(
                            JolCraftAttributes.ITEM_USE_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.DEXTERITY, JolCraftDictionary.EFFECT)),
                            0.20D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> DWARVEN_RAGE = MOB_EFFECTS.register(
            JolCraftEffectIds.DWARVEN_RAGE,
            () -> new DwarvenRageEffect(MobEffectCategory.BENEFICIAL, 0x411515)
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
            () -> new EnduranceEffect(MobEffectCategory.BENEFICIAL, 0x9a5e57)
    );

    public static final Holder<MobEffect> MARKSMAN = MOB_EFFECTS.register(
            JolCraftEffectIds.MARKSMAN,
            () -> new MarksmanEffect(MobEffectCategory.BENEFICIAL, 0x15561b)
                    .addAttributeModifier(
                            JolCraftAttributes.PROJECTILE_DAMAGE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.MARKSMAN, JolCraftDictionary.EFFECT)),
                            1.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> STONE_SKIN = MOB_EFFECTS.register(
            JolCraftEffectIds.STONE_SKIN,
            () -> new StoneSkinEffect(MobEffectCategory.BENEFICIAL, 0x52555a)
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.STONE_SKIN, JolCraftDictionary.EFFECT)),
                            -0.1D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> HOARD = MOB_EFFECTS.register(
            JolCraftEffectIds.HOARD,
            () -> new HoardEffect(MobEffectCategory.BENEFICIAL, 0xf7be26)
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
            () -> new PiercingEffect(MobEffectCategory.BENEFICIAL, 0x828a9b)
                    .addAttributeModifier(
                            JolCraftAttributes.ARMOR_PENETRATION,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.PIERCING, JolCraftDictionary.EFFECT)),
                            0.20D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> WISDOM = MOB_EFFECTS.register(
            JolCraftEffectIds.WISDOM,
            () -> new WisdomEffect(MobEffectCategory.BENEFICIAL, 0x00ff00)
                    .addAttributeModifier(
                            JolCraftAttributes.EXPERIENCE_INCREASE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.WISDOM, JolCraftDictionary.EFFECT)),
                            0.125D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> MIGHT = MOB_EFFECTS.register(
            JolCraftEffectIds.MIGHT,
            () -> new MightEffect(MobEffectCategory.BENEFICIAL, 0xff6a00)
                    .addAttributeModifier(
                            Attributes.ATTACK_DAMAGE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.MIGHT, JolCraftDictionary.EFFECT)),
                            0.05,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> HARVEST = MOB_EFFECTS.register(
            JolCraftEffectIds.HARVEST,
            () -> new HarvestEffect(MobEffectCategory.BENEFICIAL, 0x247a24)
                    .addAttributeModifier(
                            JolCraftAttributes.CROP_LOOT_INCREASE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.HARVEST, JolCraftDictionary.EFFECT)),
                            0.125D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> LUNAR = MOB_EFFECTS.register(
            JolCraftEffectIds.LUNAR,
            () -> new LunarEffect(MobEffectCategory.BENEFICIAL, 0xadb4a7)
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
            () -> new ConflagrationEffect(MobEffectCategory.BENEFICIAL, 0xffaa3f)
                    .addAttributeModifier(
                            JolCraftAttributes.SUN_FIRE_DAMAGE,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.CONFLAGRATION, JolCraftDictionary.EFFECT)),
                            5.0D,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static final Holder<MobEffect> VITALITY = MOB_EFFECTS.register(
            JolCraftEffectIds.VITALITY,
            () -> new VitalityEffect(MobEffectCategory.BENEFICIAL, 0xa74f43)
                    .addAttributeModifier(
                            Attributes.MAX_HEALTH,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.VITALITY, JolCraftDictionary.EFFECT)),
                            0.10D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.MAX_ABSORPTION,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftEffectIds.VITALITY, JolCraftDictionary.EFFECT)),
                            VitalityEffect.ABSORPTION_PER_LEVEL,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    // -------------------------------------------------------------------------
    // Harmful
    // -------------------------------------------------------------------------

    // Curses

    private static final int CURSE_COLOR = 0x7510a3;

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

    // Crowd Control

    public static final Holder<MobEffect> DISARMED = MOB_EFFECTS.register(
            JolCraftEffectIds.DISARMED,
            () -> new DisarmedEffect(MobEffectCategory.HARMFUL, 0x820000)
    );

    public static final Holder<MobEffect> ROOTED = MOB_EFFECTS.register(
            JolCraftEffectIds.ROOTED,
            () -> new RootedEffect(MobEffectCategory.HARMFUL, 0xc4ced2)
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
            () -> new StunnedEffect(MobEffectCategory.HARMFUL, 0x716a49)
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
            () -> new SuppressedEffect(MobEffectCategory.HARMFUL, 0x820000)
    );

    // Other

    public static final Holder<MobEffect> CORROSION = MOB_EFFECTS.register(
            JolCraftEffectIds.CORROSION,
            () -> new CorrosionEffect(MobEffectCategory.HARMFUL, 0x398f22)
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
            () -> new SunfireEffect(MobEffectCategory.HARMFUL, 0xf4d919)
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