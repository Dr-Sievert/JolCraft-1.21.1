package net.sievert.jolcraft.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReputationTierTrigger extends SimpleCriterionTrigger<ReputationTierTrigger.TriggerInstance> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "reputation_tier");

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        int playerTier = DwarvenReputation.get(player).getTier();
        this.trigger(player, instance -> playerTier == instance.requiredTier());
    }

    public static Criterion<TriggerInstance> hasReachedTier(int tier) {
        return JolCraftCriteriaTriggers.REPUTATION_TIER.createCriterion(
                new TriggerInstance(Optional.empty(), tier)
        );
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, int requiredTier)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.INT.fieldOf("tier").forGetter(TriggerInstance::requiredTier)
        ).apply(instance, TriggerInstance::new));
    }
}
