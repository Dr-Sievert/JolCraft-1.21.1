package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;

public enum DwarfProfession {

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

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return id != null
                ? Component.translatable(AbstractLanguageKeys.entity(id))
                : Component.empty();
    }

    public static Component getDisplayName(AbstractTradingEntity dwarf) {
        return dwarf.getTradeProfession().getDisplayName();
    }

    public static DwarfProfession byId(String id) {
        for (DwarfProfession prof : values()) {
            if (prof.id.equals(id)) return prof;
        }
        return NONE;
    }

    @SuppressWarnings("deprecation")
    public static DwarfProfession fromEntityType(EntityType<?> type) {
        ResourceLocation rl = type.builtInRegistryHolder().key().location();

        String path = rl.getPath();

        if (path.equals(JolCraftDwarfIds.DWARF)) return NONE;

        if (path.startsWith(JolCraftDwarfIds.DWARF + "_")) {
            return byId(path);
        }

        return NONE;
    }
}