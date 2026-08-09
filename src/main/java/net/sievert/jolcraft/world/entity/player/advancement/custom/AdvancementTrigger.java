package net.sievert.jolcraft.world.entity.player.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.player.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.entity.player.advancement.util.JolCraftSimpleTrigger;
import net.sievert.jolcraft.world.entity.player.advancement.util.JolCraftTriggerCodecs;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class AdvancementTrigger extends JolCraftSimpleTrigger<AdvancementTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location(JolCraftCriterionTriggerIds.ADVANCEMENT);

    public AdvancementTrigger() {
        super(ID);
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation advancementId) {
        AdvancementHolder holder = player.server.getAdvancements().get(advancementId);
        if (holder == null) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ADVANCEMENT,
                    "Advancement trigger: unknown advancement {}",
                    advancementId
            );
            return;
        }

        if (!player.getAdvancements().getOrStartProgress(holder).isDone()) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ADVANCEMENT,
                    "Advancement trigger: {} does not have {}",
                    player.getGameProfile().getName(),
                    advancementId
            );
            return;
        }

        this.trigger(player, instance -> instance.matches(advancementId));
    }

    public static Criterion<TriggerInstance> has(ResourceLocation advancementId) {
        return JolCraftCriteriaTriggers.HAS_ADVANCEMENT.createCriterion(
                new TriggerInstance(Optional.empty(), advancementId)
        );
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            ResourceLocation advancement
    ) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                JolCraftTriggerCodecs.optionalPlayer().forGetter(TriggerInstance::player),
                ResourceLocation.CODEC.fieldOf(JolCraftDictionary.ADVANCEMENT).forGetter(TriggerInstance::advancement)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(ResourceLocation advancementId) {
            return this.advancement.equals(advancementId);
        }
    }
}