package net.sievert.jolcraft.data.language;

import net.sievert.jolcraft.data.id.data_component.JolCraftDataComponentIds;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.world.player.attachment.custom.reputation.DwarvenReputationAttachment;
import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.advancement.JolCraftCriterionTriggerIds;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.effect.JolCraftEffectIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.id.item.JolCraftCreativeTabIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.id.sound.JolCraftSoundIds;
import net.sievert.jolcraft.data.id.tag.JolCraftTagIds;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftLanguageKeys extends AbstractLanguageKeys {

    private JolCraftLanguageKeys() {}

    private static String us(String... parts) {
        return JolCraftStrings.underscored(parts);
    }

    private static String dot(String... parts) {
        return JolCraftStrings.dotted(parts);
    }

    private static String pl(String singular) {
        return JolCraftIds.plural(singular);
    }

    // ---------------------------------------------------------------------
    // BountyLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_BOUNTY_INVALID =
            tooltip(JolCraftItemIds.BOUNTY, JolCraftDictionary.INVALID);

    public static final String TOOLTIP_BOUNTY_NOT_COMPLETE =
            tooltip(JolCraftItemIds.BOUNTY, us(JolCraftDictionary.NOT, JolCraftDictionary.COMPLETE));

    public static final String TOOLTIP_BOUNTY_COMPLETE =
            tooltip(JolCraftItemIds.BOUNTY, JolCraftDictionary.COMPLETE);

    public static final String TOOLTIP_BOUNTY_COMPLETED =
            tooltip(JolCraftItemIds.BOUNTY, JolCraftDictionary.COMPLETED);

    public static final String TOOLTIP_BOUNTY_SLAY =
            tooltip(JolCraftItemIds.BOUNTY, JolCraftDictionary.SLAY);

    public static final String TOOLTIP_BOUNTY_SLAY_ALT =
            tooltip(JolCraftItemIds.BOUNTY, dot(JolCraftDictionary.SLAY, JolCraftDictionary.ALT));

    public static final String TOOLTIP_BOUNTY_TIER =
            tooltip(JolCraftItemIds.BOUNTY, JolCraftDictionary.TIER);

    public static final String TOOLTIP_BOUNTY_TYPE =
            tooltip(JolCraftItemIds.BOUNTY, JolCraftDictionary.TYPE);

    public static final String TOOLTIP_BOUNTY_WRONG_TYPE =
            tooltip(JolCraftItemIds.BOUNTY, us(JolCraftDictionary.WRONG, JolCraftDictionary.TYPE));

    public static final String TOOLTIP_BOUNTY_DWARF_PROFESSION =
            tooltip(JolCraftItemIds.BOUNTY, dot(JolCraftDwarfIds.DWARF, JolCraftDictionary.PROFESSION));

    public static final String TOOLTIP_BOUNTY_CRATE_COLLECT =
            tooltip(JolCraftItemIds.BOUNTY_CRATE, JolCraftDictionary.COLLECT);

    public static final String TOOLTIP_BOUNTY_CRATE_COLLECT_ALT =
            tooltip(JolCraftItemIds.BOUNTY_CRATE, dot(JolCraftDictionary.COLLECT, JolCraftDictionary.ALT));

    public static final String TOOLTIP_BOUNTY_CRATE_LOCKED =
            tooltip(JolCraftItemIds.BOUNTY_CRATE, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_BOUNTY_CRATE_FILLED =
            tooltip(JolCraftItemIds.BOUNTY_CRATE, JolCraftDictionary.FILLED);

    public static final String TOOLTIP_BOUNTY_CRATE_NO_ITEMS =
            tooltip(JolCraftItemIds.BOUNTY_CRATE, us(JolCraftDictionary.NO, pl(JolCraftDictionary.ITEM)));

    public static final String TOOLTIP_BOUNTY_CRATE_WRONG_TYPE =
            tooltip(JolCraftItemIds.BOUNTY_CRATE, us(JolCraftDictionary.WRONG, JolCraftDictionary.TYPE));

    // ---------------------------------------------------------------------
    // CompassLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_STRUCTURE_DISCOVERED =
            tooltip(JolCraftDictionary.STRUCTURE, JolCraftDictionary.DISCOVERED);

    public static final String TOOLTIP_STRUCTURE_ALREADY_DISCOVERED =
            tooltip(JolCraftDictionary.STRUCTURE, us(JolCraftDictionary.ALREADY, JolCraftDictionary.DISCOVERED));

    public static final String TOOLTIP_DEEPSLATE_COMPASS_TRACKING =
            category(JolCraftDictionary.TOOLTIP, JolCraftItemIds.DEEPSLATE_COMPASS);

    public static final String TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE =
            tooltip(JolCraftItemIds.DEEPSLATE_COMPASS, us(JolCraftDictionary.NO, JolCraftDictionary.STRUCTURE));

    public static final String TOOLTIP_DEEPSLATE_COMPASS_LOCATE =
            tooltip(JolCraftItemIds.DEEPSLATE_COMPASS, JolCraftDictionary.LOCATE);

    // ---------------------------------------------------------------------
    // ContainerLangSubProvider
    // ---------------------------------------------------------------------

    public static final String CONTAINER_LAPIDARY_BENCH =
            category(JolCraftDictionary.CONTAINER, JolCraftBlockIds.LAPIDARY_BENCH);

    public static final String TOOLTIP_LAPIDARY_BENCH_CUT_GEMS_LOCKED =
            tooltip(JolCraftBlockIds.LAPIDARY_BENCH, us(JolCraftDictionary.CUT, pl(JolCraftDictionary.GEM), JolCraftDictionary.LOCKED));

    public static final String TOOLTIP_GEODE =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.GEODE);

    public static final String TOOLTIP_UNCUT_GEM =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.UNCUT, JolCraftDictionary.GEM));

    public static final String TOOLTIP_ARTISAN_HAMMER =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.ARTISAN, JolCraftDictionary.HAMMER));

    public static final String TOOLTIP_CHISEL =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.CHISEL);

    public static final String TOOLTIP_CUT_LOCKED =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.CUT, JolCraftDictionary.LOCKED));

    public static final String TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX =
            tooltip(JolCraftBlockIds.FERMENTING_CAULDRON, us(JolCraftDictionary.INGREDIENT, JolCraftDictionary.MAX));

    public static final String TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI =
            tooltip(JolCraftBlockIds.FERMENTING_CAULDRON, us(JolCraftDictionary.LOCKED, JolCraftDictionary.MULTI));

    public static final String CONTAINER_STRONGBOX =
            category(JolCraftDictionary.CONTAINER, JolCraftDictionary.STRONGBOX);

    public static final String CONTAINER_STRONGBOX_LOCKED =
            category(JolCraftDictionary.CONTAINER, us(JolCraftDictionary.STRONGBOX, JolCraftDictionary.LOCKED));

    public static final String TOOLTIP_LOCKPICK =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.LOCKPICK);

    public static final String TOOLTIP_STRONGBOX_NOT_EMPTY =
            tooltip(JolCraftDictionary.STRONGBOX, us(JolCraftDictionary.NOT, JolCraftDictionary.EMPTY));

    public static final String TOOLTIP_STRONGBOX_LOOT =
            tooltip(JolCraftDictionary.STRONGBOX, JolCraftDictionary.LOOT);

    public static final String TOOLTIP_STRONGBOX_SET_LOCKED =
            tooltip(JolCraftDictionary.STRONGBOX, us(JolCraftDictionary.SELECT, JolCraftDictionary.LOCKED));

    public static final String TOOLTIP_STRONGBOX_SET_UNLOCKED =
            tooltip(JolCraftDictionary.STRONGBOX, us(JolCraftDictionary.SELECT, JolCraftDictionary.UNLOCKED));

    public static final String TOOLTIP_STRONGBOX_LOCKED =
            tooltip(JolCraftDictionary.STRONGBOX, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_STRONGBOX_BUSY =
            tooltip(JolCraftDictionary.STRONGBOX, JolCraftDictionary.BUSY);

    public static final String TOOLTIP_HEARTH_OWNER =
            tooltip(JolCraftDictionary.HEARTH, JolCraftDictionary.OWNER);

    public static final String TOOLTIP_HEARTH_COOLDOWN =
            tooltip(JolCraftDictionary.HEARTH, JolCraftDictionary.COOLDOWN);

    public static final String TOOLTIP_HEARTH_NEED_FUEL =
            tooltip(JolCraftDictionary.HEARTH, us(JolCraftDictionary.NEED, JolCraftDictionary.FUEL));

    public static final String TOOLTIP_HEARTH_NOT_SAFE =
            tooltip(JolCraftDictionary.HEARTH, us(JolCraftDictionary.NOT, JolCraftDictionary.SAFE));

    public static final String TOOLTIP_HEARTH_NO_BED_NEARBY =
            tooltip(JolCraftDictionary.HEARTH, us(JolCraftDictionary.NO, JolCraftDictionary.BED, JolCraftDictionary.NEARBY));

    // ---------------------------------------------------------------------
    // DwarfLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_NEED_DWARVEN_LANGUAGE =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.NEED, JolCraftDictionary.DWARVEN, JolCraftAttachmentIds.LANGUAGE));

    public static final String TOOLTIP_NEED_ANCIENT_DWARVEN_LANGUAGE =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.NEED, JolCraftDictionary.ANCIENT, JolCraftDictionary.DWARVEN, JolCraftAttachmentIds.LANGUAGE));

    public static final String TOOLTIP_ANCIENT_MEMORY =
            category(JolCraftDictionary.TOOLTIP, JolCraftEffectIds.ANCIENT_MEMORY);

    public static final String TOOLTIP_UNIDENTIFIED =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.UNIDENTIFIED);

    public static final String TOOLTIP_UNIDENTIFIED_DWARVEN_TOME =
            category(JolCraftDictionary.TOOLTIP, dot(JolCraftDictionary.UNIDENTIFIED, JolCraftItemIds.DWARVEN_TOME));

    public static final String TOOLTIP_DWARVEN_TOME_SHIFT =
            tooltip(JolCraftItemIds.DWARVEN_TOME, JolCraftDictionary.SHIFT);

    public static final String TOOLTIP_UNIDENTIFIED_ANCIENT_DWARVEN_TOME =
            tooltip(JolCraftDictionary.UNIDENTIFIED, JolCraftItemIds.ANCIENT_DWARVEN_TOME);

    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_TOME, us(JolCraftDictionary.PARTIAL, JolCraftDictionary.UNDERSTANDING));

    public static final String TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_TOME_LEGENDARY, JolCraftDictionary.SHIFT);

    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS =
            tooltip(JolCraftItemIds.DWARVEN_TOME, us(JolCraftDictionary.IDENTIFY, JolCraftDictionary.SUCCESS));

    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL =
            tooltip(JolCraftItemIds.DWARVEN_TOME, us(JolCraftDictionary.IDENTIFY, JolCraftDictionary.FAIL));

    public static final String TOOLTIP_DWARVEN_TOME_LOCKED =
            tooltip(JolCraftItemIds.DWARVEN_TOME, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_DWARVEN_TOME_UNLOCKED =
            tooltip(JolCraftItemIds.DWARVEN_TOME, JolCraftDictionary.UNLOCKED);

    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_TOME, JolCraftDictionary.UNLOCKED);

    public static final String TOOLTIP_LEGENDARY_PAGE =
            category(JolCraftDictionary.TOOLTIP, JolCraftItemIds.LEGENDARY_PAGE);

    public static final String TOOLTIP_PAPER_LOCKED =
            tooltip(JolCraftDictionary.PAPER, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_PARCHMENT_LOCKED =
            tooltip(JolCraftDictionary.PARCHMENT, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_STONE_LOCKED =
            tooltip(JolCraftDictionary.STONE, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_DWARVEN_LEXICON_LOCKED =
            tooltip(JolCraftItemIds.DWARVEN_LEXICON, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_DWARVEN_LEXICON_UNLOCKED =
            tooltip(JolCraftItemIds.DWARVEN_LEXICON, JolCraftDictionary.UNLOCKED);

    public static final String TOOLTIP_DWARVEN_LEXICON_USE =
            tooltip(JolCraftItemIds.DWARVEN_LEXICON, JolCraftDictionary.USE);

    public static final String TOOLTIP_DWARVEN_LEXICON_KNOWS_DWARVEN_LANGUAGE =
            tooltip(JolCraftItemIds.DWARVEN_LEXICON, us(JolCraftDictionary.KNOWS, JolCraftDictionary.DWARVEN, JolCraftAttachmentIds.LANGUAGE));

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_LEXICON, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_LEXICON, JolCraftDictionary.UNLOCKED);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_LEXICON, JolCraftDictionary.USE);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANNOT_READ =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_LEXICON, us(JolCraftDictionary.CANNOT, JolCraftDictionary.READ));

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANNOT_USE =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_LEXICON, us(JolCraftDictionary.CANNOT, JolCraftDictionary.USE));

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS_ANCIENT_DWARVEN_LANGUAGE =
            tooltip(JolCraftItemIds.ANCIENT_DWARVEN_LEXICON, us(JolCraftDictionary.KNOWS, JolCraftDictionary.ANCIENT, JolCraftDictionary.DWARVEN, JolCraftAttachmentIds.LANGUAGE));

    public static final String TOOLTIP_DWARVEN_TOME_UNLOCK_EMPTY =
            tooltip(JolCraftAttachmentIds.DWARF_LORE, JolCraftDictionary.EMPTY);

    public static final String TOOLTIP_DWARVEN_TOME_UNLOCK_BREW =
            tooltip(JolCraftAttachmentIds.DWARF_LORE, JolCraftDictionary.BREW);

    public static final String TOOLTIP_DWARVEN_TOME_UNLOCK_GEMS =
            tooltip(JolCraftAttachmentIds.DWARF_LORE, pl(JolCraftDictionary.GEM));

    public static final String TOOLTIP_WRITTEN_CONTRACT =
            tooltip(JolCraftDictionary.CONTRACT, JolCraftDictionary.WRITTEN);

    public static final String TOOLTIP_SIGNED_CONTRACT =
            tooltip(JolCraftDictionary.CONTRACT, JolCraftDictionary.SIGNED);

    public static final String TOOLTIP_PROFESSION_CONTRACT =
            tooltip(JolCraftDictionary.CONTRACT, JolCraftDictionary.PROFESSION);

    public static final String TOOLTIP_GUILD_SIGIL =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.GUILD, JolCraftDictionary.SIGIL));

    public static final String MERCHANT_TITLE = dot(JolCraftDictionary.MERCHANT, JolCraftDictionary.TITLE);
    public static final String MERCHANT_TRADES = dot(JolCraftDictionary.MERCHANT, pl(JolCraftDictionary.TRADE));
    public static final String MERCHANT_DEPRECATED = dot(JolCraftDictionary.MERCHANT, JolCraftDictionary.DEPRECATED);

    public static final String TOOLTIP_DWARF_LOCKED =
            tooltip(JolCraftDwarfIds.DWARF, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_DWARF_BUSY =
            tooltip(JolCraftDwarfIds.DWARF, JolCraftDictionary.BUSY);

    public static final String TOOLTIP_DWARF_NOT_PAID =
            tooltip(JolCraftDwarfIds.DWARF, us(JolCraftDictionary.NOT, JolCraftDictionary.PAID));

    public static final String TOOLTIP_DWARF_CANNOT_PROMOTE =
            tooltip(JolCraftDwarfIds.DWARF, us(JolCraftDictionary.CANNOT, JolCraftDictionary.PROMOTE));

    public static final String TOOLTIP_DWARF_CANNOT_SIGN =
            tooltip(JolCraftDwarfIds.DWARF, us(JolCraftDictionary.CANNOT, JolCraftDictionary.SIGN));

    public static final String TOOLTIP_DWARF_GUARD_PROMOTION =
            tooltip(JolCraftDwarfIds.DWARF_GUARD, JolCraftDictionary.PROMOTION);

    public static final String TOOLTIP_RESTOCK_CRATE =
            category(JolCraftDictionary.TOOLTIP, JolCraftItemIds.RESTOCK_CRATE);

    public static final String TOOLTIP_RESTOCK_CRATE_NO_NEED =
            category(JolCraftDictionary.TOOLTIP, dot(JolCraftItemIds.RESTOCK_CRATE, us(JolCraftDictionary.NO, JolCraftDictionary.NEED)));

    public static final String TOOLTIP_RESTOCK_CRATE_SUCCESS =
            category(JolCraftDictionary.TOOLTIP, dot(JolCraftItemIds.RESTOCK_CRATE, JolCraftDictionary.SUCCESS));

    public static final String TOOLTIP_REROLL_CRATE =
            category(JolCraftDictionary.TOOLTIP, JolCraftItemIds.REROLL_CRATE);

    public static final String TOOLTIP_REROLL_CRATE_FAIL =
            category(JolCraftDictionary.TOOLTIP, dot(JolCraftItemIds.REROLL_CRATE, JolCraftDictionary.FAIL));

    public static final String TOOLTIP_REROLL_CRATE_SUCCESS =
            category(JolCraftDictionary.TOOLTIP, dot(JolCraftItemIds.REROLL_CRATE, JolCraftDictionary.SUCCESS));

    public static final String TOOLTIP_CRATE_COOLDOWN =
            tooltip(JolCraftDictionary.CRATE, JolCraftDictionary.COOLDOWN);

    public static final String TOOLTIP_CRATE_NO_OFFERS_VILLAGER =
            tooltip(JolCraftDictionary.CRATE, us(JolCraftDictionary.NO, pl(JolCraftDictionary.OFFER), JolCraftDictionary.VILLAGER));

    public static final String TOOLTIP_CRATE_NO_OFFERS_DWARF =
            tooltip(JolCraftDictionary.CRATE, us(JolCraftDictionary.NO, pl(JolCraftDictionary.OFFER), JolCraftDictionary.DWARF));

    public static final String TOOLTIP_JADE_DWARF_PROFESSION =
            tooltip(JolCraftDictionary.JADE, us(JolCraftDwarfIds.DWARF, JolCraftDirectoryIds.PROFESSION));

    // ---------------------------------------------------------------------
    // DwarvenReputationLangSubProvider
    // ---------------------------------------------------------------------

    private static String reputationTier(int tierId) {
        return mod(
                dot(
                        us(JolCraftAttachmentIds.DWARVEN_REPUTATION, JolCraftDictionary.TIER),
                        String.valueOf(tierId)
                )
        );
    }

    public static final String DWARVEN_REPUTATION_TIER_STRANGER = reputationTier(DwarvenReputationAttachment.Tier.STRANGER.getId());
    public static final String DWARVEN_REPUTATION_TIER_KNOWN_FACE = reputationTier(DwarvenReputationAttachment.Tier.KNOWN_FACE.getId());
    public static final String DWARVEN_REPUTATION_TIER_TRUSTED = reputationTier(DwarvenReputationAttachment.Tier.TRUSTED.getId());
    public static final String DWARVEN_REPUTATION_TIER_RESPECTED = reputationTier(DwarvenReputationAttachment.Tier.RESPECTED.getId());
    public static final String DWARVEN_REPUTATION_TIER_BLOOD_KIN = reputationTier(DwarvenReputationAttachment.Tier.BLOOD_KIN.getId());

    public static final String TOOLTIP_DWARVEN_REPUTATION_LOCKED =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, JolCraftDictionary.LOCKED);

    public static final String TOOLTIP_DWARVEN_REPUTATION_MAX_TIER =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, us(JolCraftDictionary.MAX, JolCraftDictionary.TIER));

    public static final String TOOLTIP_DWARVEN_REPUTATION_NOT_ENOUGH_ENDORSEMENTS =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, us(JolCraftDictionary.NOT, JolCraftDictionary.ENOUGH, pl(JolCraftDictionary.ENDORSEMENT)));

    public static final String TOOLTIP_DWARVEN_REPUTATION_NEVER_ENDORSE =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, us(JolCraftDictionary.NEVER, JolCraftDictionary.ENDORSE));

    public static final String TOOLTIP_DWARVEN_REPUTATION_CANNOT_ENDORSE =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, us(JolCraftDictionary.CANNOT, JolCraftDictionary.ENDORSE));

    public static final String TOOLTIP_DWARVEN_REPUTATION_ALREADY_ENDORSED =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, us(JolCraftDictionary.ALREADY, JolCraftDictionary.ENDORSED));

    public static final String TOOLTIP_DWARVEN_REPUTATION_WRONG_TABLET =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, us(JolCraftDictionary.WRONG, JolCraftDictionary.TABLET));

    public static final String TOOLTIP_DWARVEN_REPUTATION_LEVEL_UP =
            tooltip(JolCraftAttachmentIds.DWARVEN_REPUTATION, us(JolCraftDictionary.LEVEL, JolCraftDictionary.UP));

    public static final String TOOLTIP_TABLET_OWNER =
            tooltip(JolCraftDictionary.TABLET, JolCraftDictionary.OWNER);

    public static final String TOOLTIP_TABLET_DWARVEN_REPUTATION =
            tooltip(JolCraftDictionary.TABLET, us(JolCraftAttachmentIds.DWARVEN_REPUTATION, JolCraftDictionary.TIER));

    public static final String TOOLTIP_TABLET_DWARVEN_ENDORSEMENTS =
            tooltip(JolCraftDictionary.TABLET, pl(JolCraftCriterionTriggerIds.DWARVEN_ENDORSEMENT));

    public static final String TOOLTIP_TABLET_PROGRESS_PREFIX =
            tooltip(JolCraftDictionary.TABLET, dot(JolCraftDictionary.PROGRESS, JolCraftDictionary.PREFIX));

    public static final String TOOLTIP_TABLET_DWARVEN_REPUTATION_PROGRESS =
            tooltip(JolCraftDictionary.TABLET, us(JolCraftAttachmentIds.DWARVEN_REPUTATION, JolCraftDictionary.PROGRESS));

    public static final String TOOLTIP_TABLET_DWARVEN_ENDORSEMENTS_INFO =
            tooltip(JolCraftDictionary.TABLET, us(pl(JolCraftCriterionTriggerIds.DWARVEN_ENDORSEMENT), JolCraftDictionary.INFO));

    public static final String TOOLTIP_TABLET_ADVANCE_INFO =
            tooltip(JolCraftDictionary.TABLET, us(JolCraftDictionary.ADVANCE, JolCraftDictionary.INFO));

    // ---------------------------------------------------------------------
    // ItemLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB =
            itemGroup(JolCraftCreativeTabIds.JOLCRAFT_GENERAL_CREATIVE_TAB);

    // ---------------------------------------------------------------------
    // JadeLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JADE_CONFIG_FERMENTING_BARREL =
            jadeConfig(JolCraftBlockIds.FERMENTING_BARREL);

    public static final String JADE_CONFIG_FERMENTING_CAULDRON =
            jadeConfig(JolCraftBlockIds.FERMENTING_CAULDRON);

    public static final String JADE_CONFIG_DWARF_PROFESSION =
            jadeConfig(us(JolCraftDwarfIds.DWARF, JolCraftDirectoryIds.PROFESSION));

    // ---------------------------------------------------------------------
    // JeiLangSubProvider
    // ---------------------------------------------------------------------

    public static final String JEI_CATEGORY_DWARF_TRADES =
            category(JolCraftDictionary.JEI, pl(JolCraftRecipeIds.DWARF_TRADE));

    public static final String JEI_CATEGORY_INFO_PAGE =
            category(JolCraftDictionary.JEI, us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE));

    public static final String JEI_CATEGORY_LAPIDARY_BENCH =
            category(JolCraftDictionary.JEI, JolCraftRecipeIds.LAPIDARY_BENCH);

    public static final String JEI_CATEGORY_HAND_INTERACTION =
            category(JolCraftDictionary.JEI, JolCraftRecipeIds.HAND_INTERACTION);

    public static final String JEI_CATEGORY_FERMENTING_CAULDRON =
            category(JolCraftDictionary.JEI, JolCraftRecipeIds.FERMENTING_CAULDRON);

    public static final String JEI_CATEGORY_BOUNTY_TASK =
            category(JolCraftDictionary.JEI, JolCraftRecipeIds.BOUNTY_TASK);

    public static final String JEI_INFO_REPUTATION_TABLET =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), us(JolCraftDictionary.REPUTATION, JolCraftDictionary.TABLET)));

    public static final String JEI_INFO_STRONGBOX =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftBlockIds.STRONGBOX));

    public static final String JEI_INFO_DEEPSLATE_COMPASS =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE),JolCraftItemIds.DEEPSLATE_COMPASS));

    public static final String JEI_INFO_COIN_POUCH =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftItemIds.COIN_POUCH));

    public static final String JEI_INFO_DWARVEN_LEXICON =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftItemIds.DWARVEN_LEXICON));

    public static final String JEI_INFO_ANCIENT_DWARVEN_LEXICON =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftItemIds.ANCIENT_DWARVEN_LEXICON));

    public static final String JEI_INFO_HEARTH =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftBlockIds.HEARTH));

    public static final String JEI_INFO_VERDANT =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftTagIds.VERDANT));

    public static final String JEI_INFO_MUSHROOM =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftDictionary.MUSHROOM));

    public static final String JEI_INFO_FESTERLING =
            category(JolCraftDictionary.JEI, dot(us(JolCraftDictionary.INFO, JolCraftDictionary.PAGE), JolCraftBlockIds.FESTERLING));

    // ---------------------------------------------------------------------
    // MiscLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_HOLD_KEY =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.HOLD, JolCraftDictionary.KEY));

    public static final String TOOLTIP_DEV_KEY =
            category(JolCraftDictionary.TOOLTIP, JolCraftItemIds.DEV_KEY);

    public static final String UNKNOWN = mod(JolCraftDictionary.UNKNOWN);

    public static final String TOOLTIP_QUILL_EMPTY =
            category(JolCraftDictionary.TOOLTIP, JolCraftItemIds.QUILL_EMPTY);

    public static final String TOOLTIP_QUILL =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.QUILL);

    public static final String TOOLTIP_QUILL_FULL =
            category(JolCraftDictionary.TOOLTIP, JolCraftItemIds.QUILL_FULL);

    public static final String TOOLTIP_VANILLA_CROP =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.VANILLA, JolCraftDictionary.CROP));

    public static final String TOOLTIP_HOPS_SEEDS =
            category(JolCraftDictionary.TOOLTIP, us(pl(JolCraftDictionary.HOP), pl(JolCraftDictionary.SEED)));

    public static final String TOOLTIP_DEEPSLATE_BULBS =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.DEEPSLATE, pl(JolCraftDictionary.BULB)));

    public static final String TOOLTIP_MALT =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.MALT);

    public static final String TOOLTIP_HOPS =
            category(JolCraftDictionary.TOOLTIP, pl(JolCraftDictionary.HOP));

    public static final String TOOLTIP_YEAST =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.YEAST);

    public static final String TOOLTIP_GLASS_MUG =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.GLASS, JolCraftDictionary.MUG));

    public static final String TOOLTIP_SPANNER =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.SPANNER);

    public static final String TOOLTIP_SALVAGEABLE =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.SALVAGEABLE);

    public static final String TOOLTIP_SALVAGE =
            category(JolCraftDictionary.TOOLTIP, JolCraftDictionary.SALVAGE);

    public static final String BREW_AGE =
            category(JolCraftDictionary.TOOLTIP, JolCraftDataComponentIds.BREW_AGE);

    public static final String BREW_AGE_NAME =
            category(JolCraftDataComponentIds.BREW_AGE, JolCraftDictionary.NAME);

    public static final String BARREL_BREW_AGE =
            category(JolCraftDictionary.BARREL, JolCraftDataComponentIds.BREW_AGE);

    public static final String BREW_AGE_FRESH =
            category(JolCraftDataComponentIds.BREW_AGE, JolCraftDictionary.FRESH);

    public static final String BREW_AGE_AGED =
            category(JolCraftDataComponentIds.BREW_AGE, JolCraftDictionary.AGED);

    public static final String BREW_AGE_MATURED =
            category(JolCraftDataComponentIds.BREW_AGE, JolCraftDictionary.MATURED);

    public static final String BREW_AGE_VINTAGE =
            category(JolCraftDataComponentIds.BREW_AGE, JolCraftDictionary.VINTAGE);

    // ---------------------------------------------------------------------
    // SubtitleLangSubProvider
    // ---------------------------------------------------------------------

    public static final String SUBTITLE_DWARF_AMBIENT =
            subtitleFromSoundId(JolCraftSoundIds.DWARF_AMBIENT);

    public static final String SUBTITLE_DWARF_HIT =
            subtitleFromSoundId(JolCraftSoundIds.DWARF_HURT);

    public static final String SUBTITLE_DWARF_DEATH =
            subtitleFromSoundId(JolCraftSoundIds.DWARF_DEATH);

    public static final String SUBTITLE_DWARF_YES =
            subtitleFromSoundId(JolCraftSoundIds.DWARF_YES);

    public static final String SUBTITLE_DWARF_NO =
            subtitleFromSoundId(JolCraftSoundIds.DWARF_NO);

    public static final String SUBTITLE_DWARF_TRADE =
            subtitleFromSoundId(JolCraftSoundIds.DWARF_TRADE);

    public static final String SUBTITLE_LEVEL_UP =
            subtitleFromSoundId(JolCraftSoundIds.LEVEL_UP);

    public static final String SUBTITLE_ARMOR_EQUIP_DEEPSLATE =
            subtitleFromSoundId(JolCraftSoundIds.ARMOR_EQUIP_DEEPSLATE);

    public static final String SUBTITLE_STRONGBOX_OPEN =
            subtitleFromSoundId(JolCraftSoundIds.STRONGBOX_OPEN);

    public static final String SUBTITLE_STRONGBOX_CLOSE =
            subtitleFromSoundId(JolCraftSoundIds.STRONGBOX_CLOSE);

    public static final String SUBTITLE_STRONGBOX_LOCKPICK =
            subtitleFromSoundId(JolCraftSoundIds.STRONGBOX_LOCKPICK);

    public static final String SUBTITLE_STRONGBOX_LOCKPICK_BREAK =
            subtitleFromSoundId(JolCraftSoundIds.STRONGBOX_LOCKPICK_BREAK);

    public static final String SUBTITLE_STRONGBOX_UNLOCK =
            subtitleFromSoundId(JolCraftSoundIds.STRONGBOX_UNLOCK);

    public static final String SUBTITLE_COIN_STACK =
            subtitleFromSoundId(JolCraftSoundIds.COIN_STACK);

    public static final String SUBTITLE_COIN_SINGLE =
            subtitleFromSoundId(JolCraftSoundIds.COIN_SINGLE);

    public static final String SUBTITLE_GEM_CUT =
            subtitleFromSoundId(JolCraftSoundIds.GEM_CUT);

    public static final String SUBTITLE_CURSE =
            subtitleFromSoundId(JolCraftSoundIds.CURSE);

    // ---------------------------------------------------------------------
    // TrimLangSubProvider
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_TRIM_MATERIALS =
            category(JolCraftDictionary.TOOLTIP, JolCraftTrimIds.TRIM_MATERIAL);

    public static final String TOOLTIP_ATTRIBUTE_TRIM_MATERIALS =
            category(JolCraftDictionary.TOOLTIP, us(JolCraftDictionary.ATTRIBUTE, JolCraftTrimIds.TRIM_MATERIAL));
}