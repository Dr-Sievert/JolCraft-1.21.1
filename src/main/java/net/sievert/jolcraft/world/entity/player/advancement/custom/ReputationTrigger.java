package net.sievert.jolcraft.world.entity.player.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.player.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.entity.player.advancement.util.JolCraftSimpleTrigger;
import net.sievert.jolcraft.world.entity.player.advancement.util.JolCraftTriggerCodecs;
import net.sievert.jolcraft.world.entity.attachment.player.custom.reputation.DwarvenReputationAttachmentHelper;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ReputationTrigger extends JolCraftSimpleTrigger<ReputationTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location(JolCraftCriterionTriggerIds.DWARVEN_REPUTATION);

    public ReputationTrigger() {
        super(ID);
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        int playerTier = DwarvenReputationAttachmentHelper.getTier(player);
        this.trigger(player, instance -> instance.matches(playerTier));
    }

    public static Criterion<TriggerInstance> hasReachedTier(int tier) {
        return JolCraftCriteriaTriggers.REPUTATION_GAIN.createCriterion(
                new TriggerInstance(Optional.empty(), tier)
        );
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            int requiredTier
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                JolCraftTriggerCodecs.optionalPlayer().forGetter(TriggerInstance::player),
                Codec.INT.fieldOf(JolCraftDictionary.TIER).forGetter(TriggerInstance::requiredTier)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(int playerTier) {
            return playerTier == this.requiredTier;
        }
    }
}