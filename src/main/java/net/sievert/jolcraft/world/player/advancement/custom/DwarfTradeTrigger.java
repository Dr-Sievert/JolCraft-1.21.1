package net.sievert.jolcraft.world.player.advancement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.player.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.player.advancement.util.JolCraftSimpleTrigger;
import net.sievert.jolcraft.world.player.advancement.util.JolCraftTriggerCodecs;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfTradeTrigger extends JolCraftSimpleTrigger<DwarfTradeTrigger.TriggerInstance> {

    public static final ResourceLocation ID = JolCraft.location(JolCraftCriterionTriggerIds.TRADE_WITH_DWARF);

    public DwarfTradeTrigger() {
        super(ID);
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, DwarfProfession profession) {
        if (profession == null) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ADVANCEMENT,
                    "Dwarf trade trigger called with null profession for {}",
                    player.getGameProfile().getName()
            );
            return;
        }

        this.trigger(player, instance -> instance.matches(profession));
    }

    public static Criterion<TriggerInstance> tradedWithAnyDwarf() {
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.empty())
        );
    }

    public static Criterion<TriggerInstance> tradedWithProfession(DwarfProfession profession) {
        return JolCraftCriteriaTriggers.TRADE_WITH_DWARF.createCriterion(
                new TriggerInstance(Optional.empty(), Optional.of(profession))
        );
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<DwarfProfession> profession
    ) implements SimpleCriterionTrigger.SimpleInstance {

        private static final Codec<DwarfProfession> PROFESSION_CODEC =
                Codec.STRING.comapFlatMap(
                        id -> {
                            DwarfProfession p = DwarfProfession.byId(id);
                            return p != null
                                    ? DataResult.success(p)
                                    : DataResult.error(() -> "Unknown profession: " + id);
                        },
                        DwarfProfession::getId
                );

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                JolCraftTriggerCodecs.optionalPlayer().forGetter(TriggerInstance::player),
                PROFESSION_CODEC.optionalFieldOf(JolCraftDictionary.PROFESSION).forGetter(TriggerInstance::profession)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(DwarfProfession actual) {
            return this.profession.map(value -> value == actual).orElse(true);
        }
    }
}