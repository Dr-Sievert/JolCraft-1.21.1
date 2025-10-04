package net.sievert.jolcraft.datagen.advancement;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.custom.*;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;
import net.minecraft.advancements.AdvancementHolder;

import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class JolCraftAdvancementProvider implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer) {

        String idPathPrefix = "main/";

        // ROOT
        AdvancementHolder root = addAdvancement(
                consumer, AdvancementKey.ROOT, idPathPrefix,
                Items.CHISELED_DEEPSLATE,
                ResourceLocation.withDefaultNamespace("textures/block/deepslate_bricks.png"),
                AdvancementType.TASK,
                false, false, false,
                CriteriaTriggers.TICK.createCriterion(
                        new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create()))
                )
        );

        // LEXICON
        AdvancementHolder read_lexicon = addChildAdvancement(
                consumer, AdvancementKey.READ_LEXICON, idPathPrefix,
                root,
                JolCraftItems.DWARVEN_LEXICON.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                DwarvenLanguageTrigger.hasLanguage()
        );

        // STRANGER
        AdvancementHolder rep_0_dummy = addDummyAdvancement(
                consumer, AdvancementKey.REP_0_DUMMY, idPathPrefix, read_lexicon
        );

        // TRADE WITH DWARF
        AdvancementHolder trade_dwarf = addChildAdvancement(
                consumer, AdvancementKey.TRADE_DWARF, idPathPrefix,
                rep_0_dummy,
                JolCraftItems.GOLD_COIN.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithAnyDwarf()
        );

        // HISTORIAN
        AdvancementHolder trade_historian = addChildAdvancement(
                consumer, AdvancementKey.TRADE_HISTORIAN, idPathPrefix,
                trade_dwarf,
                JolCraftItems.DWARVEN_TOME.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithSpecificDwarf(JolCraftEntities.DWARF_HISTORIAN.get())
        );

        AdvancementHolder endorse_historian = addChildAdvancement(
                consumer, AdvancementKey.ENDORSE_HISTORIAN, idPathPrefix,
                trade_historian,
                JolCraftItems.REPUTATION_TABLET_0.get(),
                AdvancementType.GOAL,
                true, true, false,
                EndorsementTrigger.endorsedBy(DwarfProfession.HISTORIAN)
        );

        // MERCHANT
        AdvancementHolder trade_merchant = addChildAdvancement(
                consumer, AdvancementKey.TRADE_MERCHANT, idPathPrefix,
                trade_dwarf,
                JolCraftItems.RESTOCK_CRATE.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithSpecificDwarf(JolCraftEntities.DWARF_MERCHANT.get())
        );

        AdvancementHolder endorse_merchant = addChildAdvancement(
                consumer, AdvancementKey.ENDORSE_MERCHANT, idPathPrefix,
                trade_merchant,
                JolCraftItems.REPUTATION_TABLET_0.get(),
                AdvancementType.GOAL,
                true, true, false,
                EndorsementTrigger.endorsedBy(DwarfProfession.MERCHANT)
        );

        // SCRAPPER
        AdvancementHolder trade_scrapper = addChildAdvancement(
                consumer, AdvancementKey.TRADE_SCRAPPER, idPathPrefix,
                trade_dwarf,
                JolCraftItems.SCRAP.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithSpecificDwarf(JolCraftEntities.DWARF_SCRAPPER.get())
        );

        AdvancementHolder endorse_scrapper = addChildAdvancement(
                consumer, AdvancementKey.ENDORSE_SCRAPPER, idPathPrefix,
                trade_scrapper,
                JolCraftItems.REPUTATION_TABLET_0.get(),
                AdvancementType.GOAL,
                true, true, false,
                EndorsementTrigger.endorsedBy(DwarfProfession.SCRAPPER)
        );

        // KNOWN FACE
        AdvancementHolder rep_1 = addChildAdvancement(
                consumer, AdvancementKey.REP_1, idPathPrefix,
                endorse_historian,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                ReputationTrigger.hasReachedTier(1)
        );

        AdvancementHolder rep_1_dummy = addDummyAdvancement(
                consumer, AdvancementKey.REP_1_DUMMY, idPathPrefix, rep_1
        );

        // BREWMASTER
        AdvancementHolder trade_brewmaster = addChildAdvancement(
                consumer, AdvancementKey.TRADE_BREWMASTER, idPathPrefix,
                rep_1_dummy,
                JolCraftItems.DWARVEN_BREW.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithSpecificDwarf(JolCraftEntities.DWARF_BREWMASTER.get())
        );

        AdvancementHolder endorse_brewmaster = addChildAdvancement(
                consumer, AdvancementKey.ENDORSE_BREWMASTER, idPathPrefix,
                trade_brewmaster,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.GOAL,
                true, true, false,
                EndorsementTrigger.endorsedBy(DwarfProfession.BREWMASTER)
        );

        // GUARD
        AdvancementHolder trade_guard = addChildAdvancement(
                consumer, AdvancementKey.TRADE_GUARD, idPathPrefix,
                rep_1_dummy,
                JolCraftItems.DEEPSLATE_AXE.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithSpecificDwarf(JolCraftEntities.DWARF_GUARD.get())
        );

        AdvancementHolder endorse_guard = addChildAdvancement(
                consumer, AdvancementKey.ENDORSE_GUARD, idPathPrefix,
                trade_guard,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.GOAL,
                true, true, false,
                EndorsementTrigger.endorsedBy(DwarfProfession.GUARD)
        );

        // KEEPER
        AdvancementHolder trade_keeper = addChildAdvancement(
                consumer, AdvancementKey.TRADE_KEEPER, idPathPrefix,
                rep_1_dummy,
                JolCraftItems.BARLEY.get(),
                AdvancementType.TASK,
                true, true, false,
                DwarfTradeTrigger.tradedWithSpecificDwarf(JolCraftEntities.DWARF_KEEPER.get())
        );

        AdvancementHolder endorse_keeper = addChildAdvancement(
                consumer, AdvancementKey.ENDORSE_KEEPER, idPathPrefix,
                trade_keeper,
                JolCraftItems.REPUTATION_TABLET_1.get(),
                AdvancementType.GOAL,
                true, true, false,
                EndorsementTrigger.endorsedBy(DwarfProfession.KEEPER)
        );

        // TRUSTED (REP TIER 2)
        AdvancementHolder rep_2 = addChildAdvancement(
                consumer, AdvancementKey.REP_2, idPathPrefix,
                endorse_guard,
                JolCraftItems.REPUTATION_TABLET_2.get(),
                AdvancementType.CHALLENGE,
                true, true, false,
                ReputationTrigger.hasReachedTier(2)
        );

        AdvancementHolder rep_2_dummy = addDummyAdvancement(
                consumer, AdvancementKey.REP_2_DUMMY, idPathPrefix, rep_2
        );
    }

    private static AdvancementHolder buildAdvancement(
            Consumer<AdvancementHolder> consumer,
            AdvancementKey key,
            @Nullable String idPathPrefix,
            @Nullable AdvancementHolder parent,
            ItemLike icon,
            @Nullable ResourceLocation background,
            AdvancementType type,
            boolean showToast, boolean announce, boolean hidden,
            Criterion<?>... criteria
    ) {
        String id = key.id(); // always lowercase from enum
        String fullId = (idPathPrefix == null ? "" : idPathPrefix) + id;
        ResourceLocation resourceId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, fullId);

        String keyPrefix = "advancement.jolcraft." + id;
        Advancement.Builder builder = Advancement.Builder.advancement();
        if (parent != null) builder.parent(parent);
        builder.display(
                icon,
                Component.translatable(keyPrefix + ".title"),
                Component.translatable(keyPrefix + ".description"),
                background, type, showToast, announce, hidden
        );

        for (int i = 0; i < criteria.length; i++) {
            builder.addCriterion("criterion_" + id + "_" + i, criteria[i]);
        }
        return builder.save(consumer, resourceId);
    }

    private static AdvancementHolder addAdvancement(
            Consumer<AdvancementHolder> consumer,
            AdvancementKey key,
            @Nullable String idPathPrefix,
            ItemLike icon,
            @Nullable ResourceLocation background,
            AdvancementType type,
            boolean showToast, boolean announce, boolean hidden,
            Criterion<?>... criteria
    ) {
        return buildAdvancement(
                consumer,
                key, idPathPrefix,
                null,
                icon,
                background,
                type,
                showToast,
                announce,
                hidden,
                criteria);
    }


    private static AdvancementHolder addChildAdvancement(
            Consumer<AdvancementHolder> consumer,
            AdvancementKey key,
            @Nullable String idPathPrefix,
            AdvancementHolder parent,
            ItemLike icon,
            AdvancementType type,
            boolean showToast, boolean announce, boolean hidden,
            Criterion<?>... criteria
    ) {
        return buildAdvancement(
                consumer,
                key,
                idPathPrefix,
                parent, icon,
                null,
                type,
                showToast,
                announce,
                hidden,
                criteria);
    }

    private static AdvancementHolder addDummyAdvancement(
            Consumer<AdvancementHolder> consumer,
            AdvancementKey key,
            @Nullable String idPathPrefix,
            AdvancementHolder parent
    ) {
        return buildAdvancement(
                consumer,
                key,
                idPathPrefix,
                parent,
                Items.CHISELED_DEEPSLATE,
                null,
                AdvancementType.TASK,
                false, false, true,
                AdvancementTrigger.has(parent.id())
        );
    }
}
