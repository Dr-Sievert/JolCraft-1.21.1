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
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputation;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReputationTrigger extends SimpleCriterionTrigger<ReputationTrigger.TriggerInstance> {
    public static final ResourceLocation ID = JolCraft.location(JolCraftCriterionTriggerIds.DWARVEN_REPUTATION);

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        int playerTier = DwarvenReputation.get(player).getTierId();
        this.trigger(player, instance -> playerTier == instance.requiredTier());
    }

    public static Criterion<TriggerInstance> hasReachedTier(int tier) {
        return JolCraftCriteriaTriggers.REPUTATION_GAIN.createCriterion(
                new TriggerInstance(Optional.empty(), tier)
        );
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, int requiredTier)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf(JolCraftDictionary.PLAYER).forGetter(TriggerInstance::player),
                Codec.INT.fieldOf(JolCraftDictionary.TIER).forGetter(TriggerInstance::requiredTier)
        ).apply(instance, TriggerInstance::new));
    }
}
