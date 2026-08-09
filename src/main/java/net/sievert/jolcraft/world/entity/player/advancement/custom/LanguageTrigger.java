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
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageAttachmentHelper;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageType;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class LanguageTrigger extends JolCraftSimpleTrigger<LanguageTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location(JolCraftCriterionTriggerIds.LANGUAGE);

    public LanguageTrigger() {
        super(ID);
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, LanguageType language) {
        if (!LanguageAttachmentHelper.knowsLanguageBypassCreative(player, language)) {
            return;
        }

        this.trigger(player, instance -> instance.matches(language));
    }

    public static Criterion<TriggerInstance> hasLanguage(LanguageType language) {
        return JolCraftCriteriaTriggers.KNOWS_LANGUAGE.createCriterion(
                new TriggerInstance(Optional.empty(), language)
        );
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            LanguageType language
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                JolCraftTriggerCodecs.optionalPlayer().forGetter(TriggerInstance::player),
                LanguageType.CODEC.fieldOf(JolCraftDictionary.LANGUAGE).forGetter(TriggerInstance::language)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(LanguageType triggeredLanguage) {
            return this.language == triggeredLanguage;
        }
    }
}