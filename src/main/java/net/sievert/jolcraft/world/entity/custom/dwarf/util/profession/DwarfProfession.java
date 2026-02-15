package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.data.util.JolCraftEnumHelper;
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
    SCRAPPER(JolCraftDwarfIds.DWARF_SCRAPPER);

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
}