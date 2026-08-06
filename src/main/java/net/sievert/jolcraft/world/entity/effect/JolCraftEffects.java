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
import net.sievert.jolcraft.world.entity.effect.custom.*;
import net.sievert.jolcraft.world.entity.effect.custom.crowd_control.DisarmedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.crowd_control.RootedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.crowd_control.StunnedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.crowd_control.SuppressedEffect;
import net.sievert.jolcraft.world.entity.effect.custom.curse.*;

public final class JolCraftEffects {

    private JolCraftEffects() {}

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JolCraft.MOD_ID);

    // Beneficial

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
    );

    public static final Holder<MobEffect> DWARVEN_HASTE = MOB_EFFECTS.register(
            JolCraftEffectIds.DWARVEN_HASTE,
            () -> new DwarvenHasteEffect(MobEffectCategory.BENEFICIAL, 0x2bc7ac)
    );

    public static final Holder<MobEffect> RADIANT = MOB_EFFECTS.register(
            JolCraftEffectIds.RADIANT,
            () -> new RadiantEffect(MobEffectCategory.BENEFICIAL, 0xfaff42)
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
                            JolCraft.location(JolCraftEffectIds.BULWARK),
                            0.05D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    // Harmful

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

    public static final Holder<MobEffect> HEX = MOB_EFFECTS.register(
            JolCraftEffectIds.HEX,
            () -> new HexEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
    );

    public static final Holder<MobEffect> VITALITY_CURSE = MOB_EFFECTS.register(
            JolCraftEffectIds.VITALITY_CURSE,
            () -> new VitalityCurseEffect(MobEffectCategory.HARMFUL, CURSE_COLOR)
                    .addAttributeModifier(
                            Attributes.MAX_HEALTH,
                            JolCraft.location(JolCraftEffectIds.VITALITY_CURSE),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> DISARMED = MOB_EFFECTS.register(
            JolCraftEffectIds.DISARMED,
            () -> new DisarmedEffect(MobEffectCategory.HARMFUL, 0x820000)
    );

    public static final Holder<MobEffect> ROOTED = MOB_EFFECTS.register(
            JolCraftEffectIds.ROOTED,
            () -> new RootedEffect(MobEffectCategory.HARMFUL, 0xc4ced2)
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftStrings.underscored(JolCraftDictionary.MOVEMENT, JolCraftDictionary.SPEED), JolCraftEffectIds.ROOTED)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.JUMP_STRENGTH,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftStrings.underscored(JolCraftDictionary.JUMP, JolCraftDictionary.STRENGTH), JolCraftEffectIds.ROOTED)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> STUNNED = MOB_EFFECTS.register(
            JolCraftEffectIds.STUNNED,
            () -> new StunnedEffect(MobEffectCategory.HARMFUL, 0x716a49)
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftStrings.underscored(JolCraftDictionary.MOVEMENT, JolCraftDictionary.SPEED), JolCraftEffectIds.STUNNED)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.JUMP_STRENGTH,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftStrings.underscored(JolCraftDictionary.JUMP, JolCraftDictionary.STRENGTH), JolCraftEffectIds.STUNNED)),
                            -1.0D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static final Holder<MobEffect> SUPPRESSED = MOB_EFFECTS.register(
            JolCraftEffectIds.SUPPRESSED,
            () -> new SuppressedEffect(MobEffectCategory.HARMFUL, 0x820000)
    );

    public static final Holder<MobEffect> CORROSION = MOB_EFFECTS.register(
            JolCraftEffectIds.CORROSION,
            () -> new CorrosionEffect(MobEffectCategory.HARMFUL, 0x398f22)
                    .addAttributeModifier(
                            Attributes.ARMOR,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ARMOR, JolCraftEffectIds.CORROSION)),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.ARMOR_TOUGHNESS,
                            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ARMOR, JolCraftDictionary.TOUGHNESS, JolCraftEffectIds.CORROSION)),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
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