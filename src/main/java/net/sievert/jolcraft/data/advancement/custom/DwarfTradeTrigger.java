package net.sievert.jolcraft.data.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DwarfTradeTrigger extends SimpleCriterionTrigger<DwarfTradeTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location( JolCraftCriterionTriggerIds.TRADE_WITH_DWARF);

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, DwarfProfession profession) {
        if (profession == null) {
            JolCraftLogs.debug(JolCraftLogTags.ADVANCEMENT, "trade_with_dwarf trigger called with null profession for {}",
                    player.getGameProfile().getName()
            );
            return;
        }

        this.trigger(player, instance -> instance.matches(profession));
    }

    // ------------------------------------------------------------
    // Criterion factories
    // ------------------------------------------------------------

    public static Criterion<TriggerInstance> tradedWithAnyDwarf() {
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.empty())
        );
    }

    public static Criterion<TriggerInstance> tradedWithProfession(DwarfProfession profession) {
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.of(profession.name()))
        );
    }

    // ------------------------------------------------------------
    // Instance
    // ------------------------------------------------------------

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<String> profession
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf(JolCraftDictionary.PLAYER).forGetter(TriggerInstance::player),
                        Codec.STRING.optionalFieldOf(JolCraftDictionary.PROFESSION).forGetter(TriggerInstance::profession)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(DwarfProfession actual) {
            return profession.map(s -> s.equals(actual.name())).orElse(true);
        }
    }
}