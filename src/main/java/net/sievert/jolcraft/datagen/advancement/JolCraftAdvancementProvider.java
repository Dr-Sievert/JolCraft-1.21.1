package net.sievert.jolcraft.datagen.advancement;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.advancement.custom.*;
import net.sievert.jolcraft.data.id.advancement.JolCraftAdvancementIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public final class JolCraftAdvancementProvider implements AdvancementSubProvider {

    @Override
    public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer) {

        // ROOT
        AdvancementHolder root = addAdvancement(
                consumer, JolCraftAdvancementIds.ROOT,
                Items.CHISELED_DEEPSLATE,
                JolCraftTextures.vanilla(
                        JolCraftTextures.block(
                                JolCraftStrings.underscored(
                                        JolCraftDictionary.DEEPSLATE,
                                        JolCraftStrings.plural(JolCraftDictionary.BRICK)
                                )
                        )
                ),
                AdvancementType.TASK,
                false, false, false,
                CriteriaTriggers.TICK.createCriterion(
                        new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create()))
                )
        );

        // LEXICON
        AdvancementHolder read_lexicon = addChildAdvancement(
                consumer, JolCraftAdvancementIds.READ_LEXICON,
                root,
                JolCraftItems.DWARVEN_LEXICON.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                DwarvenLanguageTrigger.hasLanguage()
        );

        // STRANGER
        AdvancementHolder rep_0_dummy = addDummyAdvancement(
                consumer, JolCraftAdvancementIds.REP_0_DUMMY, read_lexicon
        );

        // TRADE WITH DWARF
        AdvancementHolder trade_dwarf = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_DWARF,
                rep_0_dummy,
                JolCraftItems.GOLD_COIN.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithAnyDwarf()
        );

        // HISTORIAN
        AdvancementHolder trade_historian = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_HISTORIAN,
                trade_dwarf,
                JolCraftItems.DWARVEN_TOME.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.HISTORIAN)
        );

        AdvancementHolder endorse_historian = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_HISTORIAN,
                trade_historian,
                JolCraftItems.REPUTATION_TABLET_0.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.HISTORIAN)
        );

        // MERCHANT
        AdvancementHolder trade_merchant = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_MERCHANT,
                trade_dwarf,
                JolCraftItems.RESTOCK_CRATE.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.MERCHANT)
        );

        AdvancementHolder endorse_merchant = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_MERCHANT,
                trade_merchant,
                JolCraftItems.REPUTATION_TABLET_0.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.MERCHANT)
        );

        // SCRAPPER
        AdvancementHolder trade_scrapper = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_SCRAPPER,
                trade_dwarf,
                JolCraftItems.SCRAP.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.SCRAPPER)
        );

        AdvancementHolder endorse_scrapper = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_SCRAPPER,
                trade_scrapper,
                JolCraftItems.REPUTATION_TABLET_0.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.SCRAPPER)
        );

        // KNOWN FACE (REP TIER 1)
        AdvancementHolder rep_1 = addChildAdvancement(
                consumer, JolCraftAdvancementIds.REP_1,
                endorse_historian,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                ReputationTrigger.hasReachedTier(1)
        );

        AdvancementHolder rep_1_dummy = addDummyAdvancement(
                consumer, JolCraftAdvancementIds.REP_1_DUMMY, rep_1
        );

        // BREWMASTER
        AdvancementHolder trade_brewmaster = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_BREWMASTER,
                rep_1_dummy,
                JolCraftItems.DWARVEN_BREW.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.BREWMASTER)
        );

        AdvancementHolder endorse_brewmaster = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_BREWMASTER,
                trade_brewmaster,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.BREWMASTER)
        );

        // GUARD
        AdvancementHolder trade_guard = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_GUARD,
                rep_1_dummy,
                JolCraftItems.DEEPSLATE_AXE.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.GUARD)
        );

        AdvancementHolder endorse_guard = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_GUARD,
                trade_guard,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.GUARD)
        );

        // KEEPER
        AdvancementHolder trade_keeper = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_KEEPER,
                rep_1_dummy,
                JolCraftItems.BARLEY.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.KEEPER)
        );

        AdvancementHolder endorse_keeper = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_KEEPER,
                trade_keeper,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.KEEPER)
        );

        // TRUSTED (REP TIER 2)
        AdvancementHolder rep_2 = addChildAdvancement(
                consumer, JolCraftAdvancementIds.REP_2,
                endorse_guard,
                JolCraftItems.REPUTATION_TABLET_2.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                ReputationTrigger.hasReachedTier(2)
        );

        AdvancementHolder rep_2_dummy = addDummyAdvancement(
                consumer, JolCraftAdvancementIds.REP_2_DUMMY, rep_2
        );

        // ARTISAN
        AdvancementHolder trade_artisan = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_ARTISAN,
                rep_2_dummy,
                JolCraftItems.DEEPSLATE_CHISEL.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.ARTISAN)
        );

        AdvancementHolder endorse_artisan = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_ARTISAN,
                trade_artisan,
                JolCraftItems.REPUTATION_TABLET_2.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.ARTISAN)
        );

        // EXPLORER
        AdvancementHolder trade_explorer = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_EXPLORER,
                rep_2_dummy,
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.EXPLORER)
        );

        AdvancementHolder endorse_explorer = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_EXPLORER,
                trade_explorer,
                JolCraftItems.REPUTATION_TABLET_2.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.EXPLORER)
        );

        // MINER
        AdvancementHolder trade_miner = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_MINER,
                rep_2_dummy,
                JolCraftItems.DEEPSLATE_PICKAXE.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.MINER)
        );

        AdvancementHolder endorse_miner = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_MINER,
                trade_miner,
                JolCraftItems.REPUTATION_TABLET_2.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.MINER)
        );

        // RESPECTED (REP TIER 3)
        AdvancementHolder rep_3 = addChildAdvancement(
                consumer, JolCraftAdvancementIds.REP_3,
                endorse_artisan,
                JolCraftItems.REPUTATION_TABLET_3.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                ReputationTrigger.hasReachedTier(3)
        );

        AdvancementHolder rep_3_dummy = addDummyAdvancement(
                consumer, JolCraftAdvancementIds.REP_3_DUMMY, rep_3
        );

        // ALCHEMIST
        AdvancementHolder trade_alchemist = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_ALCHEMIST,
                rep_3_dummy,
                JolCraftItems.DEEPSLATE_MORTAR_ITEM.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.ALCHEMIST)
        );

        AdvancementHolder endorse_alchemist = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_ALCHEMIST,
                trade_alchemist,
                JolCraftItems.REPUTATION_TABLET_3.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.ALCHEMIST)
        );

        // ARCANIST
        AdvancementHolder trade_arcanist = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_ARCANIST,
                rep_3_dummy,
                JolCraftItems.WOECRYSTAL.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.ARCANIST)
        );

        AdvancementHolder endorse_arcanist = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_ARCANIST,
                trade_arcanist,
                JolCraftItems.REPUTATION_TABLET_3.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.ARCANIST)
        );

        // PRIEST
        AdvancementHolder trade_priest = addChildAdvancement(
                consumer, JolCraftAdvancementIds.TRADE_PRIEST,
                rep_3_dummy,
                JolCraftItems.LUMIERE.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithProfession(DwarfProfession.PRIEST)
        );

        AdvancementHolder endorse_priest = addChildAdvancement(
                consumer, JolCraftAdvancementIds.ENDORSE_PRIEST,
                trade_priest,
                JolCraftItems.REPUTATION_TABLET_3.get(),
                AdvancementType.GOAL,
                true, true, false,
                DwarfEndorsementTrigger.endorsedBy(DwarfProfession.PRIEST)
        );

        // BLOOD-KIN (REP TIER 4)
        AdvancementHolder rep_4 = addChildAdvancement(
                consumer, JolCraftAdvancementIds.REP_4,
                endorse_arcanist,
                JolCraftItems.REPUTATION_TABLET_4.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                ReputationTrigger.hasReachedTier(4)
        );

        addDummyAdvancement(
                consumer, JolCraftAdvancementIds.REP_4_DUMMY, rep_4
        );
    }

    // ---------------------------------------------------------------------
    // Builders (idPath is already stable and canonical)
    // ---------------------------------------------------------------------

    private static AdvancementHolder buildAdvancement(
            Consumer<AdvancementHolder> consumer,
            String idPath,
            @Nullable AdvancementHolder parent,
            ItemLike icon,
            @Nullable ResourceLocation background,
            AdvancementType type,
            boolean showToast, boolean announce, boolean hidden,
            Criterion<?>... criteria
    ) {
        ResourceLocation resourceId = JolCraft.location(idPath);

        String keyPrefix = JolCraftStrings.dotted(
                JolCraftDictionary.ADVANCEMENT,
                JolCraft.MOD_ID,
                idPath
        );

        Advancement.Builder builder = Advancement.Builder.advancement();
        if (parent != null) builder.parent(parent);

        String titleKey = JolCraftStrings.dotted(keyPrefix, JolCraftDictionary.TITLE);
        String descKey  = JolCraftStrings.dotted(keyPrefix, JolCraftDictionary.DESCRIPTION);

        builder.display(
                icon,
                Component.translatable(titleKey),
                Component.translatable(descKey),
                background,
                type,
                showToast,
                announce,
                hidden
        );

        for (int i = 0; i < criteria.length; i++) {
            String criterionKey = JolCraftStrings.underscored(
                    JolCraftDictionary.CRITERION,
                    idPath,
                    String.valueOf(i)
            );
            builder.addCriterion(criterionKey, criteria[i]);
        }

        return builder.save(consumer, resourceId);
    }

    private static AdvancementHolder addAdvancement(
            Consumer<AdvancementHolder> consumer,
            String idPath,
            ItemLike icon,
            @Nullable ResourceLocation background,
            AdvancementType type,
            boolean showToast, boolean announce, boolean hidden,
            Criterion<?>... criteria
    ) {
        return buildAdvancement(
                consumer,
                idPath,
                null,
                icon,
                background,
                type,
                showToast,
                announce,
                hidden,
                criteria
        );
    }

    private static AdvancementHolder addChildAdvancement(
            Consumer<AdvancementHolder> consumer,
            String idPath,
            AdvancementHolder parent,
            ItemLike icon,
            AdvancementType type,
            boolean showToast, boolean announce, boolean hidden,
            Criterion<?>... criteria
    ) {
        return buildAdvancement(
                consumer,
                idPath,
                parent,
                icon,
                null,
                type,
                showToast,
                announce,
                hidden,
                criteria
        );
    }

    private static AdvancementHolder addDummyAdvancement(
            Consumer<AdvancementHolder> consumer,
            String idPath,
            AdvancementHolder parent
    ) {
        return buildAdvancement(
                consumer,
                idPath,
                parent,
                Items.CHISELED_DEEPSLATE,
                null,
                AdvancementType.TASK,
                false, false, true,
                AdvancementTrigger.has(parent.id())
        );
    }
}