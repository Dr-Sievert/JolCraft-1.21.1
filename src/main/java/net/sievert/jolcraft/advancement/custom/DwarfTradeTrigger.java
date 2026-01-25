package net.sievert.jolcraft.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Advancement trigger for trading with a dwarf, optionally filtered by entity type.
 */
public class DwarfTradeTrigger extends SimpleCriterionTrigger<DwarfTradeTrigger.TriggerInstance> {

    public static final DwarfTradeTrigger INSTANCE =
            CriteriaTriggers.register("trade_with_dwarf", new DwarfTradeTrigger());

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    /**
     * Call this when a player successfully trades with a dwarf.
     */
    public void trigger(ServerPlayer player, AbstractDwarfEntity dwarf) {
        this.trigger(player, instance -> instance.matches(dwarf));
    }

    /**
     * Creates a criterion for a specific entity type (uses EntityType, stores as ResourceLocation).
     */
    @SuppressWarnings("deprecation")
    public static Criterion<TriggerInstance> tradedWithSpecificDwarf(EntityType<?> entityType) {
        ResourceLocation id = entityType.builtInRegistryHolder().key().location();
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.of(id))
        );
    }

    /**
     * Creates a criterion that matches trading with any dwarf.
     */
    public static Criterion<TriggerInstance> tradedWithAnyDwarf() {
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.empty())
        );
    }

    /**
     * Instance data for a trade-with-dwarf criterion.
     * Stores player predicate and optional dwarf entity type as ResourceLocation for codec stability.
     */
    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ResourceLocation> dwarfTypeId)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ResourceLocation.CODEC.optionalFieldOf("dwarf_type").forGetter(TriggerInstance::dwarfTypeId)
                ).apply(instance, TriggerInstance::new)
        );

        /**
         * Checks if the given dwarf matches the trigger's entity type filter.
         */
        @SuppressWarnings("deprecation")
        public boolean matches(AbstractDwarfEntity dwarf) {
            if (dwarfTypeId.isEmpty()) return true;
            ResourceLocation entityId = dwarf.getType().builtInRegistryHolder().key().location();
            return entityId.equals(dwarfTypeId.get());
        }

        /**
         * Resolves the stored ResourceLocation to an EntityType, if present in the registry.
         */
        public Optional<EntityType<?>> resolvedEntityType() {
            return dwarfTypeId.flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional);
        }
    }
}
