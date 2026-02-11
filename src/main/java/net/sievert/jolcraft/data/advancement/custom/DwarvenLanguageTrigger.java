package net.sievert.jolcraft.data.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguage;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DwarvenLanguageTrigger extends SimpleCriterionTrigger<DwarvenLanguageTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location(JolCraftCriterionTriggerIds.HAS_DWARVEN_LANGUAGE);

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        DwarvenLanguage lang = player.getData(JolCraftAttachments.DWARVEN_LANGUAGE.get());
        if (lang.hasLanguage()) {
            this.trigger(player, instance -> true);
        }
    }

    public static Criterion<TriggerInstance> hasLanguage() {
        return JolCraftCriteriaTriggers.KNOWS_DWARVEN_LANGUAGE.createCriterion(new TriggerInstance(Optional.empty()));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf(JolCraftDictionary.PLAYER).forGetter(TriggerInstance::player))
                .apply(instance, TriggerInstance::new)
        );
    }
}
