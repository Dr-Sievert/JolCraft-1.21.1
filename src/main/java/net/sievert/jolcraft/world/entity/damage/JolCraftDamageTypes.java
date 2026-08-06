package net.sievert.jolcraft.world.entity.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.damage.JolCraftDamageTypeIds;

public final class JolCraftDamageTypes {

    private JolCraftDamageTypes() {}

    public static final ResourceKey<DamageType> CURSED_WOUND =
            create(JolCraftDamageTypeIds.CURSED_WOUND);

    public static final ResourceKey<DamageType> VITALITY_CURSE =
            create(JolCraftDamageTypeIds.VITALITY_CURSE);

    private static ResourceKey<DamageType> create(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, JolCraft.location(id));
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        register(context, CURSED_WOUND, JolCraftDamageTypeIds.CURSED_WOUND);
        register(context, VITALITY_CURSE, JolCraftDamageTypeIds.VITALITY_CURSE);
    }

    private static void register(
            BootstrapContext<DamageType> context,
            ResourceKey<DamageType> key,
            String messageId
    ) {
        context.register(
                key,
                new DamageType(
                        messageId,
                        DamageScaling.NEVER,
                        0.0F,
                        DamageEffects.HURT
                )
        );
    }
}