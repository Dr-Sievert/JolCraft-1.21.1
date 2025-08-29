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
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TradeWithDwarfTrigger extends SimpleCriterionTrigger<TradeWithDwarfTrigger.TriggerInstance> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "trade_with_dwarf");

    public static final TradeWithDwarfTrigger INSTANCE =
            CriteriaTriggers.register("trade_with_dwarf", new TradeWithDwarfTrigger());

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    /** Called when the player successfully trades with the dwarf */
    public void trigger(ServerPlayer player, AbstractDwarfEntity dwarf) {
        this.trigger(player, instance -> instance.matches(dwarf));
    }

    /** Convenient method to create Criterion for advancement JSON */
    public static Criterion<TriggerInstance> tradedWithSpecificDwarf(String dwarfId) {
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.of(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, dwarfId)))
        );
    }

    public static Criterion<TriggerInstance> tradedWithAnyDwarf() {
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.empty())
        );
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ResourceLocation> dwarfType)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ResourceLocation.CODEC.optionalFieldOf("dwarf_type").forGetter(TriggerInstance::dwarfType)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(AbstractDwarfEntity dwarf) {
            return dwarfType.isEmpty() || dwarf.getType().builtInRegistryHolder().key().location().equals(dwarfType.get());
        }
    }
}
