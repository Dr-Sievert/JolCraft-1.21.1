package net.sievert.jolcraft.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.effect.custom.*;

public class JolCraftEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, JolCraft.MOD_ID);

    //Beneficial

    public static final Holder<MobEffect> HOMESTEAD = MOB_EFFECTS.register("homestead",
            () -> new HomesteadEffect(MobEffectCategory.BENEFICIAL, 0x6e6d6d));

    public static final Holder<MobEffect> ANCIENT_MEMORY = MOB_EFFECTS.register("ancient_memory",
            () -> new AncientMemoryEffect(MobEffectCategory.BENEFICIAL, 0x8bb386));

    public static final Holder<MobEffect> LOCKPICKING = MOB_EFFECTS.register("lockpicking",
            () -> new LockpickingEffect(MobEffectCategory.BENEFICIAL, 0x6b6b6b));

    public static final Holder<MobEffect> DWARVEN_HASTE = MOB_EFFECTS.register("dwarven_haste",
            () -> new DwarvenHasteEffect(MobEffectCategory.BENEFICIAL, 0x2bc7ac));

    public static final Holder<MobEffect> RADIANT = MOB_EFFECTS.register("radiant",
            () -> new RadiantEffect(MobEffectCategory.BENEFICIAL, 0xfaff42));


    //Harmful

    public static final Holder<MobEffect> DELIRIUM_CURSE = MOB_EFFECTS.register("delirium_curse",
            () -> new DeliriumCurseEffect(MobEffectCategory.HARMFUL, 0x7510a3));

    public static final Holder<MobEffect> CORROSION = MOB_EFFECTS.register("corrosion",
            () -> new CorrosionEffect(MobEffectCategory.HARMFUL, 0x398f22)
                    .addAttributeModifier(
                            Attributes.ARMOR,
                            JolCraft.location("armor_corrosion"),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            Attributes.ARMOR_TOUGHNESS,
                            JolCraft.location("armor_toughness_corrosion"),
                            -0.2D,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
