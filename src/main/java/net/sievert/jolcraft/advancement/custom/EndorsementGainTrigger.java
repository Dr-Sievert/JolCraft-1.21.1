package net.sievert.jolcraft.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EndorsementGainTrigger extends SimpleCriterionTrigger<EndorsementGainTrigger.TriggerInstance> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "endorsement_gain");

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    /**
     * Triggers the criterion for a specific player and profession.
     */
    public void trigger(ServerPlayer player, DwarfProfession profession) {
        if (profession == null || profession == DwarfProfession.NONE) return;
        this.trigger(player, instance -> instance.profession().equals(profession));
    }

    /**
     * Creates an advancement criterion for being endorsed by a profession.
     */
    public static Criterion<TriggerInstance> endorsedBy(DwarfProfession profession) {
        return JolCraftCriteriaTriggers.ENDORSEMENT_GAIN.createCriterion(
                new TriggerInstance(Optional.empty(), profession)
        );
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, DwarfProfession profession)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.STRING.xmap(DwarfProfession::byId, DwarfProfession::getId)
                        .fieldOf("profession").forGetter(i -> i.profession)
        ).apply(instance, TriggerInstance::new));
    }
}
