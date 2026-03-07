package net.sievert.jolcraft.world.entity.custom.dwarf.attribute;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class DwarfAttributes {

    private DwarfAttributes() {}

    // ---------------------------------------------------------
    // Base attribute registration
    // ---------------------------------------------------------

    public static AttributeSupplier.Builder createBase() {
        Map<ResourceLocation, Double> base =
                DwarfProfessionConfig.DEFAULTS.attributes().overrides();

        return AbstractDwarfEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, require(base, Attributes.MAX_HEALTH))
                .add(Attributes.MOVEMENT_SPEED, require(base, Attributes.MOVEMENT_SPEED))
                .add(Attributes.FOLLOW_RANGE, require(base, Attributes.FOLLOW_RANGE))
                .add(Attributes.TEMPT_RANGE, require(base, Attributes.TEMPT_RANGE))
                .add(Attributes.ATTACK_DAMAGE, require(base, Attributes.ATTACK_DAMAGE));
    }

    private static double require(Map<ResourceLocation, Double> base, Holder<Attribute> attr) {
        ResourceLocation id = attr.unwrapKey().orElseThrow().location();
        Double v = base.get(id);
        if (v == null) {
            throw new IllegalStateException("Missing base attribute default for: " + id);
        }
        return v;
    }

    // ---------------------------------------------------------
    // Runtime application
    // ---------------------------------------------------------

    public static void applyTo(LivingEntity entity, DwarfProfession profession) {
        Map<ResourceLocation, Double> merged = new HashMap<>(DwarfProfessionConfig.DEFAULTS.attributes().overrides());

        DwarfProfessionConfig cfg = DwarfProfessionConfigManager.INSTANCE.get(profession);
        merged.putAll(cfg.attributes().overrides());

        applyMap(entity, merged);
    }

    private static void applyMap(LivingEntity entity, Map<ResourceLocation, Double> map) {
        if (map.isEmpty()) return;

        HolderLookup.RegistryLookup<Attribute> lookup =
                entity.level().registryAccess().lookupOrThrow(Registries.ATTRIBUTE);

        for (var entry : map.entrySet()) {
            ResourceLocation id = entry.getKey();
            double value = entry.getValue();

            Holder<Attribute> holder = holderFromId(lookup, id);
            if (holder == null) continue;

            boolean clampHealth = holder.is(Attributes.MAX_HEALTH);
            setBase(entity, holder, value, clampHealth);
        }
    }

    @Nullable
    private static Holder<Attribute> holderFromId(
            HolderLookup.RegistryLookup<Attribute> lookup,
            ResourceLocation id
    ) {
        ResourceKey<Attribute> key = ResourceKey.create(Registries.ATTRIBUTE, id);

        return lookup.get(key).orElse(null);
    }

    private static void setBase(
            LivingEntity entity,
            Holder<Attribute> attr,
            double value,
            boolean clampHealth
    ) {
        AttributeInstance inst = entity.getAttribute(attr);
        if (inst == null) return;

        if (Double.compare(inst.getBaseValue(), value) == 0) return;
        inst.setBaseValue(value);

        if (clampHealth && attr.is(Attributes.MAX_HEALTH)) {
            float max = entity.getMaxHealth();
            if (entity.getHealth() > max) entity.setHealth(max);
            if (entity.getHealth() < 1.0F) entity.setHealth(1.0F);
        }
    }
}