package net.sievert.jolcraft.datagen.advancement;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.custom.*;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;
import net.minecraft.advancements.AdvancementHolder;

import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import org.jetbrains.annotations.NotNull;

public class JolCraftAdvancementProvider implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer) {
        // Root advancement (dummy) — sets the tab name
        ResourceLocation rootId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/root");
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        Items.CHISELED_DEEPSLATE,
                        Component.translatable("advancement.jolcraft.root.title"),
                        Component.translatable("advancement.jolcraft.root.description"),
                        ResourceLocation.withDefaultNamespace("textures/block/deepslate_bricks.png"),
                        AdvancementType.TASK,
                        false, false, false
                )
                .addCriterion("tick", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create()))))
                .save(consumer, rootId);

        //Language
        ResourceLocation readLexiconId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/read_lexicon");
        AdvancementHolder readLexiconAdv = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        JolCraftItems.DWARVEN_LEXICON.get(),
                        Component.translatable("advancement.jolcraft.read_lexicon.title"),
                        Component.translatable("advancement.jolcraft.read_lexicon.description"),
                        null, // null means no override background; inherits from root
                        AdvancementType.CHALLENGE,
                        true, true, false
                )
                .addCriterion("knows_dwarvish", HasDwarvenLanguageTrigger.hasLanguage())
                .save(consumer, readLexiconId);

        //Stranger dummy
        ResourceLocation tradedummyId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade_dummy");
        AdvancementHolder tradedummyAdv = Advancement.Builder.advancement()
                .parent(readLexiconAdv)
                .display(
                        Items.CHISELED_DEEPSLATE,
                        Component.translatable("advancement.jolcraft.trade_dummy.title"),
                        Component.translatable("advancement.jolcraft.trade_dummy.description"),
                        null,
                        AdvancementType.TASK,
                        false, false, true
                )
                .addCriterion("has_read_lexicon", HasAdvancementTrigger.has(readLexiconId))
                .save(consumer, tradedummyId);

        //Trade with dwarf
        ResourceLocation tradeId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade");
        AdvancementHolder tradeAdv = Advancement.Builder.advancement()
                .parent(tradedummyAdv)
                .display(
                        JolCraftItems.GOLD_COIN.get(),
                        Component.translatable("advancement.jolcraft.trade_with_dwarf.title"),
                        Component.translatable("advancement.jolcraft.trade_with_dwarf.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("generic_dwarf_trade", TradeWithDwarfTrigger.tradedWithAnyDwarf())
                .save(consumer, tradeId);


        //Historian path
        ResourceLocation tradehistorianId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade_historian");
        AdvancementHolder tradehistorianAdv = Advancement.Builder.advancement()
                .parent(tradeAdv)
                .display(
                        JolCraftItems.DWARVEN_TOME.get(),
                        Component.translatable("advancement.jolcraft.historian.trade.title"),
                        Component.translatable("advancement.jolcraft.historian.trade.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("trade_historian", TradeWithDwarfTrigger.tradedWithSpecificDwarf("dwarf_historian"))
                .save(consumer, tradehistorianId);

        ResourceLocation endorsehistorianId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/endorse_historian");
        AdvancementHolder endorsehistorianAdv = Advancement.Builder.advancement()
                .parent(tradehistorianAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_0.get(),
                        Component.translatable("advancement.jolcraft.historian.endorse.title"),
                        Component.translatable("advancement.jolcraft.historian.endorse.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("endorse_historian", EndorsementGainTrigger.endorsedBy(DwarfProfession.HISTORIAN))
                .save(consumer, endorsehistorianId);

        //Merchant path
        ResourceLocation trademerchantId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade_merchant");
        AdvancementHolder trademerchantAdv = Advancement.Builder.advancement()
                .parent(tradeAdv)
                .display(
                        JolCraftItems.BOUNTY.get(),
                        Component.translatable("advancement.jolcraft.merchant.trade.title"),
                        Component.translatable("advancement.jolcraft.merchant.trade.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("trade_merchant", TradeWithDwarfTrigger.tradedWithSpecificDwarf("dwarf_merchant"))
                .save(consumer, trademerchantId);

        ResourceLocation endorsemerchantId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/endorse_merchant");
        AdvancementHolder endorsemerchantAdv = Advancement.Builder.advancement()
                .parent(trademerchantAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_0.get(),
                        Component.translatable("advancement.jolcraft.merchant.endorse.title"),
                        Component.translatable("advancement.jolcraft.merchant.endorse.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("endorse_historian", EndorsementGainTrigger.endorsedBy(DwarfProfession.MERCHANT))
                .save(consumer, endorsemerchantId);


        //Scrapper path
        ResourceLocation tradescrapperId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade_scrapper");
        AdvancementHolder tradescrapperAdv = Advancement.Builder.advancement()
                .parent(tradeAdv)
                .display(
                        JolCraftItems.SCRAP.get(),
                        Component.translatable("advancement.jolcraft.scrapper.trade.title"),
                        Component.translatable("advancement.jolcraft.scrapper.trade.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("trade_scrapper", TradeWithDwarfTrigger.tradedWithSpecificDwarf("dwarf_scrapper"))
                .save(consumer, tradescrapperId);

        ResourceLocation endorsescrapperId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/endorse_scrapper");
        AdvancementHolder endorsescrapperAdv = Advancement.Builder.advancement()
                .parent(tradescrapperAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_0.get(),
                        Component.translatable("advancement.jolcraft.scrapper.endorse.title"),
                        Component.translatable("advancement.jolcraft.scrapper.endorse.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("endorse_historian", EndorsementGainTrigger.endorsedBy(DwarfProfession.SCRAPPER))
                .save(consumer, endorsescrapperId);


        //Advance tier to known face

        ResourceLocation rep1Id = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/rep_known_face");
        AdvancementHolder rep1Adv = Advancement.Builder.advancement()
                .parent(endorsemerchantAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_1.get(),
                        Component.translatable("advancement.jolcraft.reputation.known_face.title"),
                        Component.translatable("advancement.jolcraft.reputation.known_face.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, false
                )
                .addCriterion("rep_known_face", ReputationTierTrigger.hasReachedTier(1))
                .save(consumer, rep1Id);


        //Known face tier dummy
        ResourceLocation rep1dummyId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/rep1_dummy");
        AdvancementHolder rep1dummyAdv = Advancement.Builder.advancement()
                .parent(rep1Adv)
                .display(
                        Items.CHISELED_DEEPSLATE,
                        Component.translatable("advancement.jolcraft.rep1_dummy.title"),
                        Component.translatable("advancement.jolcraft.rep1_dummy.description"),
                        null,
                        AdvancementType.TASK,
                        false, false, true
                )
                .addCriterion("rep1_dummy", HasAdvancementTrigger.has(rep1Id))
                .save(consumer, rep1dummyId);


        //Brewmaster path
        ResourceLocation tradebrewmasterId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade_brewmaster");
        AdvancementHolder tradebrewmasterAdv = Advancement.Builder.advancement()
                .parent(rep1dummyAdv)
                .display(
                        JolCraftItems.DWARVEN_BREW.get(),
                        Component.translatable("advancement.jolcraft.brewmaster.trade.title"),
                        Component.translatable("advancement.jolcraft.brewmaster.trade.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("trade_brewmaster", TradeWithDwarfTrigger.tradedWithSpecificDwarf("dwarf_brewmaster"))
                .save(consumer, tradebrewmasterId);

        ResourceLocation endorsebrewmasterId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/endorse_brewmaster");
        AdvancementHolder endorsebrewmasterAdv = Advancement.Builder.advancement()
                .parent(tradebrewmasterAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_1.get(),
                        Component.translatable("advancement.jolcraft.brewmaster.endorse.title"),
                        Component.translatable("advancement.jolcraft.brewmaster.endorse.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("endorse_historian", EndorsementGainTrigger.endorsedBy(DwarfProfession.BREWMASTER))
                .save(consumer, endorsebrewmasterId);


        //Guard path
        ResourceLocation tradeguardId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade_guard");
        AdvancementHolder tradeguardAdv = Advancement.Builder.advancement()
                .parent(rep1dummyAdv)
                .display(
                        JolCraftItems.DEEPSLATE_AXE.get(),
                        Component.translatable("advancement.jolcraft.guard.trade.title"),
                        Component.translatable("advancement.jolcraft.guard.trade.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("trade_guard", TradeWithDwarfTrigger.tradedWithSpecificDwarf("dwarf_guard"))
                .save(consumer, tradeguardId);

        ResourceLocation endorseguardId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/endorse_guard");
        AdvancementHolder endorseguardAdv = Advancement.Builder.advancement()
                .parent(tradeguardAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_1.get(),
                        Component.translatable("advancement.jolcraft.guard.endorse.title"),
                        Component.translatable("advancement.jolcraft.guard.endorse.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("endorse_historian", EndorsementGainTrigger.endorsedBy(DwarfProfession.GUARD))
                .save(consumer, endorseguardId);


        //Keeper path
        ResourceLocation tradekeeperId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/trade_keeper");
        AdvancementHolder tradekeeperAdv = Advancement.Builder.advancement()
                .parent(rep1dummyAdv)
                .display(
                        JolCraftItems.BARLEY.get(),
                        Component.translatable("advancement.jolcraft.keeper.trade.title"),
                        Component.translatable("advancement.jolcraft.keeper.trade.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("trade_keeper", TradeWithDwarfTrigger.tradedWithSpecificDwarf("dwarf_keeper"))
                .save(consumer, tradekeeperId);

        ResourceLocation endorsekeeperId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/endorse_keeper");
        AdvancementHolder endorsekeeperAdv = Advancement.Builder.advancement()
                .parent(tradekeeperAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_1.get(),
                        Component.translatable("advancement.jolcraft.keeper.endorse.title"),
                        Component.translatable("advancement.jolcraft.keeper.endorse.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("endorse_historian", EndorsementGainTrigger.endorsedBy(DwarfProfession.KEEPER))
                .save(consumer, endorsekeeperId);


        //Advance tier to trusted
        ResourceLocation rep2Id = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/rep_trusted");
        AdvancementHolder rep2Adv = Advancement.Builder.advancement()
                .parent(endorseguardAdv)
                .display(
                        JolCraftItems.REPUTATION_TABLET_2.get(),
                        Component.translatable("advancement.jolcraft.reputation.trusted.title"),
                        Component.translatable("advancement.jolcraft.reputation.trusted.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, false
                )
                .addCriterion("rep_trusted", ReputationTierTrigger.hasReachedTier(2))
                .save(consumer, rep2Id);


        //Trusted tier dummy
        ResourceLocation rep2dummyId = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "story/rep2_dummy");
        AdvancementHolder rep2dummyAdv = Advancement.Builder.advancement()
                .parent(rep2Adv)
                .display(
                        Items.CHISELED_DEEPSLATE,
                        Component.translatable("advancement.jolcraft.rep2_dummy.title"),
                        Component.translatable("advancement.jolcraft.rep2_dummy.description"),
                        null,
                        AdvancementType.TASK,
                        false, false, true
                )
                .addCriterion("rep2_dummy", HasAdvancementTrigger.has(rep2Id))
                .save(consumer, rep2dummyId);




















    }


}
