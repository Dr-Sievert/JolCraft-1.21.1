package net.sievert.jolcraft.data.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EndorsementTrigger extends SimpleCriterionTrigger<EndorsementTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location("endorsement_gain");

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    /**
     * Triggers the criterion for a specific player and profession.
     */
    public void trigger(ServerPlayer player, DwarfProfession profession) {
        if (profession == null) {
            JolCraftLogs.debug("endorsement_gain trigger called with null profession for {}", player.getGameProfile().getName());
            return;
        }
        if (profession == DwarfProfession.NONE) return;

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
                        .fieldOf("profession").forGetter(TriggerInstance::profession)
        ).apply(instance, TriggerInstance::new));
    }
}
