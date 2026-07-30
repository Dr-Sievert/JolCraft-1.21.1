package net.sievert.jolcraft.world.entity.custom.dwarf.profession;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;

public enum DwarfProfession implements JolCraftEnumHelper.StringId {

    NONE(JolCraftDwarfIds.DWARF),
    ALCHEMIST(JolCraftDwarfIds.DWARF_ALCHEMIST),
    ARCANIST(JolCraftDwarfIds.DWARF_ARCANIST),
    ARTISAN(JolCraftDwarfIds.DWARF_ARTISAN),
    BREWMASTER(JolCraftDwarfIds.DWARF_BREWMASTER),
    EXPLORER(JolCraftDwarfIds.DWARF_EXPLORER),
    GUARD(JolCraftDwarfIds.DWARF_GUARD),
    GUILDMASTER(JolCraftDwarfIds.DWARF_GUILDMASTER),
    HISTORIAN(JolCraftDwarfIds.DWARF_HISTORIAN),
    KEEPER(JolCraftDwarfIds.DWARF_KEEPER),
    MERCHANT(JolCraftDwarfIds.DWARF_MERCHANT),
    MINER(JolCraftDwarfIds.DWARF_MINER),
    PRIEST(JolCraftDwarfIds.DWARF_PRIEST),
    SCRAPPER(JolCraftDwarfIds.DWARF_SCRAPPER),
    BLACKSMITH(JolCraftDwarfIds.DWARF_BLACKSMITH),
    CHAMPION(JolCraftDwarfIds.DWARF_CHAMPION),
    SMELTER(JolCraftDwarfIds.DWARF_SMELTER);

    private final String id;

    DwarfProfession(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return Component.translatable(AbstractLanguageKeys.entity(id));
    }

    public static Component getDisplayName(AbstractTradingEntity dwarf) {
        return dwarf.getTradeProfession().getDisplayName();
    }

    public static DwarfProfession byId(String id) {
        return JolCraftEnumHelper.byStringId(DwarfProfession.class, id, NONE);
    }

    @SuppressWarnings("deprecation")
    public static DwarfProfession fromEntityType(EntityType<?> type) {
        return byId(type.builtInRegistryHolder().key().location().getPath());
    }

    public String professionName() {
        if (this == NONE) return JolCraftDictionary.NONE;

        String raw = getId();
        if (raw == null || raw.isBlank()) return JolCraftDictionary.NONE;

        int idx = raw.indexOf('_');
        if (idx >= 0 && idx + 1 < raw.length()) {
            return raw.substring(idx + 1);
        }

        return raw.equals(JolCraftDwarfIds.DWARF) ? JolCraftDictionary.NONE : raw.toLowerCase();
    }

    public static DwarfProfession fromProfessionName(String name) {
        if (name == null || name.isBlank()) return NONE;

        String normalized = name.trim().toLowerCase();

        if (normalized.equals(JolCraftDictionary.NONE) || normalized.equals(JolCraftDwarfIds.DWARF)) return NONE;

        return byId(JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, normalized));
    }
}