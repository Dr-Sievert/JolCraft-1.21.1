package net.sievert.jolcraft.world.entity.custom.dwarf.util.attribute;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Central dwarf attribute source:
 * - Base registration lives here (single DwarfEntityType).
 * - Profession-specific base-values are applied at runtime.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfAttributes {

    private DwarfAttributes() {}

    public static AttributeSupplier.Builder createBase() {
        Values v = Values.base();
        return AbstractDwarfEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, v.maxHealth())
                .add(Attributes.MOVEMENT_SPEED, v.movementSpeed())
                .add(Attributes.FOLLOW_RANGE, v.followRange())
                .add(Attributes.TEMPT_RANGE, v.temptRange())
                .add(Attributes.ATTACK_DAMAGE, v.attackDamage());
    }

    public static void applyTo(LivingEntity entity, DwarfProfession profession) {
        Values v = Values.base().applyProfessionDelta(profession);

        setBase(entity, Attributes.MAX_HEALTH, v.maxHealth(), true);
        setBase(entity, Attributes.MOVEMENT_SPEED, v.movementSpeed(), false);
        setBase(entity, Attributes.FOLLOW_RANGE, v.followRange(), false);
        setBase(entity, Attributes.TEMPT_RANGE, v.temptRange(), false);
        setBase(entity, Attributes.ATTACK_DAMAGE, v.attackDamage(), false);
    }

    private static void setBase(LivingEntity entity, Holder<Attribute> attr, double value, boolean clampHealth) {
        AttributeInstance inst = entity.getAttribute(attr);
        if (inst == null) return;

        if (Double.compare(inst.getBaseValue(), value) == 0) return;
        inst.setBaseValue(value);

        if (clampHealth && attr == Attributes.MAX_HEALTH) {
            float max = entity.getMaxHealth();
            if (entity.getHealth() > max) entity.setHealth(max);
            if (entity.getHealth() < 1.0F) entity.setHealth(1.0F);
        }
    }

    private record Values(
            double maxHealth,
            double movementSpeed,
            double followRange,
            double temptRange,
            double attackDamage
    ) {

        static Values base() {
            return new Values(
                    30D,
                    0.20D,
                    24D,
                    16D,
                    3.0D
            );
        }

        Values applyProfessionDelta(DwarfProfession profession) {
            return switch (profession) {

                case GUARD -> new Values(
                        this.maxHealth(),
                        0.25D,
                        this.followRange(),
                        this.temptRange(),
                        this.attackDamage()
                );

                default -> this;
            };
        }
    }
}
