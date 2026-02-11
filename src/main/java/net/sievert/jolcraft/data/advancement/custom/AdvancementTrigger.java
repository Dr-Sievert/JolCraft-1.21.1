package net.sievert.jolcraft.data.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AdvancementTrigger extends SimpleCriterionTrigger<AdvancementTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location(JolCraftCriterionTriggerIds.HAS_ADVANCEMENT);

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation advancementId) {
        AdvancementHolder holder = player.server.getAdvancements().get(advancementId);
        if (holder == null) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ADVANCEMENT,
                    "has_advancement trigger: unknown advancement {}",
                    advancementId
            );
            return;
        }

        if (!player.getAdvancements().getOrStartProgress(holder).isDone()) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ADVANCEMENT,
                    "has_advancement trigger: {} does not have {}",
                    player.getGameProfile().getName(),
                    advancementId
            );
            return;
        }

        this.trigger(player, instance -> instance.advancement().equals(advancementId));
    }

    public static Criterion<TriggerInstance> has(ResourceLocation advancementId) {
        return JolCraftCriteriaTriggers.HAS_ADVANCEMENT.createCriterion(new TriggerInstance(Optional.empty(), advancementId));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceLocation advancement)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf(JolCraftDataKeys.PLAYER).forGetter(TriggerInstance::player),
                ResourceLocation.CODEC.fieldOf(JolCraftDataKeys.ADVANCEMENT).forGetter(TriggerInstance::advancement)
        ).apply(instance, TriggerInstance::new));
    }
}
