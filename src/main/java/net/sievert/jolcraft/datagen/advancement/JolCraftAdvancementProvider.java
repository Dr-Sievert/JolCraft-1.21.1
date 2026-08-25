package net.sievert.jolcraft.datagen.advancement;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.player.advancement.custom.DwarfEndorsementTrigger;
import net.sievert.jolcraft.world.entity.player.advancement.custom.DwarfTradeTrigger;
import net.sievert.jolcraft.world.entity.player.advancement.custom.LanguageTrigger;
import net.sievert.jolcraft.world.entity.player.advancement.custom.ReputationTrigger;
import net.sievert.jolcraft.world.entity.attachment.player.custom.language.LanguageType;
import net.sievert.jolcraft.data.id.advancement.JolCraftAdvancementIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataExecutor;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class JolCraftAdvancementProvider
        implements AdvancementSubProvider, JolCraftMainDataProvider<Consumer<AdvancementHolder>> {

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.ADVANCEMENT;
    }

    @Override
    public @NotNull String id() {
        return domain().getId();
    }

    @Override
    public void generate(
            HolderLookup.@NotNull Provider registries,
            @NotNull Consumer<AdvancementHolder> consumer
    ) {
        generate(consumer, null, null, null);
    }

    @Override
    public void run(
            @NotNull Consumer<AdvancementHolder> target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        List<JolCraftDataEmission<Consumer<AdvancementHolder>>> emissions = new ArrayList<>();

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ROOT_1)
                        .root()
                        .icon(Items.CHISELED_DEEPSLATE)
                        .background(
                                JolCraftTextures.vanilla(
                                        JolCraftTextures.block(
                                                JolCraftStrings.underscored(
                                                        JolCraftDictionary.DEEPSLATE,
                                                        JolCraftStrings.plural(JolCraftDictionary.BRICK)
                                                )
                                        )
                                )
                        )
                        .type(AdvancementType.TASK)
                        .display(false, false, false)
                        .criterion(
                                CriteriaTriggers.TICK.createCriterion(
                                        new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create()))
                                )
                        )
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.READ_LEXICON)
                        .parent(JolCraft.location(JolCraftAdvancementIds.ROOT_1))
                        .icon(JolCraftItems.DWARVEN_LEXICON.get())
                        .type(AdvancementType.CHALLENGE)
                        .display(true, true, false)
                        .criterion(LanguageTrigger.hasLanguage(LanguageType.DWARVEN))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.READ_ANCIENT_LEXICON)
                        .parent(JolCraft.location(JolCraftAdvancementIds.READ_LEXICON))
                        .icon(JolCraftItems.ANCIENT_DWARVEN_LEXICON.get())
                        .type(AdvancementType.CHALLENGE)
                        .display(true, true, true)
                        .criterion(LanguageTrigger.hasLanguage(LanguageType.ANCIENT_DWARVEN))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_0_DUMMY)
                        .dummyChild(JolCraft.location(JolCraftAdvancementIds.READ_LEXICON))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_DWARF)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_0_DUMMY))
                        .icon(JolCraftItems.GOLD_COIN.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithAnyDwarf())
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_HISTORIAN)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_DWARF))
                        .icon(JolCraftItems.DWARVEN_TOME.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.HISTORIAN))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_HISTORIAN)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_HISTORIAN))
                        .icon(JolCraftItems.REPUTATION_TABLET_0.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.HISTORIAN))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_MERCHANT)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_DWARF))
                        .icon(JolCraftItems.REWARD_CRATE.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.MERCHANT))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_MERCHANT)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_MERCHANT))
                        .icon(JolCraftItems.REPUTATION_TABLET_0.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.MERCHANT))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_SCRAPPER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_DWARF))
                        .icon(JolCraftItems.SCRAP.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.SCRAPPER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_SCRAPPER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_SCRAPPER))
                        .icon(JolCraftItems.REPUTATION_TABLET_0.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.SCRAPPER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_1)
                        .parent(JolCraft.location(JolCraftAdvancementIds.ENDORSE_HISTORIAN))
                        .icon(JolCraftItems.REPUTATION_TABLET_1.get())
                        .type(AdvancementType.CHALLENGE)
                        .display(true, true, false)
                        .criterion(ReputationTrigger.hasReachedTier(1))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_1_DUMMY)
                        .dummyChild(JolCraft.location(JolCraftAdvancementIds.REP_1))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_BREWMASTER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_1_DUMMY))
                        .icon(JolCraftItems.DWARVEN_BREW.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.BREWMASTER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_BREWMASTER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_BREWMASTER))
                        .icon(JolCraftItems.REPUTATION_TABLET_1.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.BREWMASTER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_GUARD)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_1_DUMMY))
                        .icon(JolCraftItems.DEEPSLATE_AXE.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.GUARD))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_GUARD)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_GUARD))
                        .icon(JolCraftItems.REPUTATION_TABLET_1.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.GUARD))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_KEEPER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_1_DUMMY))
                        .icon(JolCraftItems.BARLEY.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.KEEPER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_KEEPER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_KEEPER))
                        .icon(JolCraftItems.REPUTATION_TABLET_1.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.KEEPER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_2)
                        .parent(JolCraft.location(JolCraftAdvancementIds.ENDORSE_GUARD))
                        .icon(JolCraftItems.REPUTATION_TABLET_2.get())
                        .type(AdvancementType.CHALLENGE)
                        .display(true, true, false)
                        .criterion(ReputationTrigger.hasReachedTier(2))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_2_DUMMY)
                        .dummyChild(JolCraft.location(JolCraftAdvancementIds.REP_2))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_ARTISAN)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_2_DUMMY))
                        .icon(JolCraftItems.DEEPSLATE_CHISEL.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.ARTISAN))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_ARTISAN)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_ARTISAN))
                        .icon(JolCraftItems.REPUTATION_TABLET_2.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.ARTISAN))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_EXPLORER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_2_DUMMY))
                        .icon(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.EXPLORER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_EXPLORER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_EXPLORER))
                        .icon(JolCraftItems.REPUTATION_TABLET_2.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.EXPLORER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_MINER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_2_DUMMY))
                        .icon(JolCraftItems.DEEPSLATE_PICKAXE.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.MINER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_MINER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_MINER))
                        .icon(JolCraftItems.REPUTATION_TABLET_2.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.MINER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_3)
                        .parent(JolCraft.location(JolCraftAdvancementIds.ENDORSE_ARTISAN))
                        .icon(JolCraftItems.REPUTATION_TABLET_3.get())
                        .type(AdvancementType.CHALLENGE)
                        .display(true, true, false)
                        .criterion(ReputationTrigger.hasReachedTier(3))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_3_DUMMY)
                        .dummyChild(JolCraft.location(JolCraftAdvancementIds.REP_3))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_ALCHEMIST)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_3_DUMMY))
                        .icon(JolCraftItems.MORTAR_ITEM.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.ALCHEMIST))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_ALCHEMIST)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_ALCHEMIST))
                        .icon(JolCraftItems.REPUTATION_TABLET_3.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.ALCHEMIST))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_ARCANIST)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_3_DUMMY))
                        .icon(JolCraftItems.WOECRYSTAL.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.ARCANIST))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_ARCANIST)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_ARCANIST))
                        .icon(JolCraftItems.REPUTATION_TABLET_3.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.ARCANIST))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_PRIEST)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_3_DUMMY))
                        .icon(JolCraftItems.LUMIERE.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.PRIEST))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_PRIEST)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_PRIEST))
                        .icon(JolCraftItems.REPUTATION_TABLET_3.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.PRIEST))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_4)
                        .parent(JolCraft.location(JolCraftAdvancementIds.ENDORSE_ARCANIST))
                        .icon(JolCraftItems.REPUTATION_TABLET_4.get())
                        .type(AdvancementType.CHALLENGE)
                        .display(true, true, false)
                        .criterion(ReputationTrigger.hasReachedTier(4))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.REP_4_DUMMY)
                        .dummyChild(JolCraft.location(JolCraftAdvancementIds.REP_4))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_BLACKSMITH)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_4_DUMMY))
                        .icon(JolCraftItems.MITHRIL_ARTISAN_HAMMER.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.BLACKSMITH))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_BLACKSMITH)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_BLACKSMITH))
                        .icon(JolCraftItems.REPUTATION_TABLET_4.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.BLACKSMITH))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_CHAMPION)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_4_DUMMY))
                        .icon(JolCraftItems.MITHRIL_WARHAMMER.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.CHAMPION))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_CHAMPION)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_CHAMPION))
                        .icon(JolCraftItems.REPUTATION_TABLET_4.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.CHAMPION))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.TRADE_SMELTER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.REP_4_DUMMY))
                        .icon(JolCraftItems.IMPURE_MITHRIL.get())
                        .type(AdvancementType.TASK)
                        .display(true, true, false)
                        .criterion(DwarfTradeTrigger.tradedWithProfession(DwarfProfession.SMELTER))
        ));

        emissions.add(build(
                JolCraftAdvancementBuilder.create()
                        .idPath(JolCraftAdvancementIds.ENDORSE_SMELTER)
                        .parent(JolCraft.location(JolCraftAdvancementIds.TRADE_SMELTER))
                        .icon(JolCraftItems.REPUTATION_TABLET_4.get())
                        .type(AdvancementType.GOAL)
                        .display(true, true, false)
                        .criterion(DwarfEndorsementTrigger.endorsedBy(DwarfProfession.SMELTER))
        ));

        JolCraftDataExecutor.execute(target, this, emissions, tracking, true);

        tracking.logTrackedOutputCount(this, JolCraftStrings.plural(JolCraftDictionary.ADVANCEMENT));
    }

    private static @NotNull JolCraftDataEmission<Consumer<AdvancementHolder>> build(
            @NotNull JolCraftAdvancementBuilder builder
    ) {
        DataResult<JolCraftDataEmission<Consumer<AdvancementHolder>>> result = builder.buildValidated();
        return result.getOrThrow(IllegalStateException::new);
    }
}