package net.sievert.jolcraft.world.entity.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.damage.JolCraftDamageTypeIds;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;

public final class JolCraftDamageTypes {

    private JolCraftDamageTypes() {}

    public static final ResourceKey<DamageType> VITALITY_CURSE = create(JolCraftEffectIds.VITALITY_CURSE);

    private static ResourceKey<DamageType> create(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, JolCraft.location(id));
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(
                VITALITY_CURSE,
                new DamageType(
                        JolCraftDamageTypeIds.VITALITY_CURSE,
                        DamageScaling.NEVER,
                        0.0F,
                        DamageEffects.HURT
                )
        );
    }
}