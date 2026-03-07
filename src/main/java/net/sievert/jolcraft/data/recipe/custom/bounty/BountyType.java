package net.sievert.jolcraft.data.recipe.custom.bounty;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;

/**
 * Represents the different types of bounties.
 *
 * Mirrors {@link DwarfProfession} ids so we don't duplicate raw strings/ids.
 */
public enum BountyType implements JolCraftEnumHelper.StringId {

    UNKNOWN(JolCraftDictionary.UNKNOWN),

    ALCHEMIST(DwarfProfession.ALCHEMIST.professionName()),
    ARCANIST(DwarfProfession.ARCANIST.professionName()),
    ARTISAN(DwarfProfession.ARTISAN.professionName()),
    BREWMASTER(DwarfProfession.BREWMASTER.professionName()),
    EXPLORER(DwarfProfession.EXPLORER.professionName()),
    GUARD(DwarfProfession.GUARD.professionName()),
    GUILDMASTER(DwarfProfession.GUILDMASTER.professionName()),
    HISTORIAN(DwarfProfession.HISTORIAN.professionName()),
    KEEPER(DwarfProfession.KEEPER.professionName()),
    MERCHANT(DwarfProfession.MERCHANT.professionName()),
    MINER(DwarfProfession.MINER.professionName()),
    PRIEST(DwarfProfession.PRIEST.professionName()),
    SCRAPPER(DwarfProfession.SCRAPPER.professionName());

    private final String id;

    BountyType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static BountyType fromString(String id) {
        return JolCraftEnumHelper.byStringId(BountyType.class, id, UNKNOWN);
    }
}