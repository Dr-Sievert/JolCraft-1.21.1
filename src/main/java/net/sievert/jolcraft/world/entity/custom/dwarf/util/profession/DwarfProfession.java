package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageCategory;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;

public enum DwarfProfession {

    NONE(JolCraftIds.DWARF),
    ALCHEMIST(JolCraftIds.DWARF_ALCHEMIST),
    ARCANIST(JolCraftIds.DWARF_ARCANIST),
    ARTISAN(JolCraftIds.DWARF_ARTISAN),
    BREWMASTER(JolCraftIds.DWARF_BREWMASTER),
    EXPLORER(JolCraftIds.DWARF_EXPLORER),
    GUARD(JolCraftIds.DWARF_GUARD),
    GUILDMASTER(JolCraftIds.DWARF_GUILDMASTER),
    HISTORIAN(JolCraftIds.DWARF_HISTORIAN),
    KEEPER(JolCraftIds.DWARF_KEEPER),
    MERCHANT(JolCraftIds.DWARF_MERCHANT),
    MINER(JolCraftIds.DWARF_MINER),
    PRIEST(JolCraftIds.DWARF_PRIEST),
    SCRAPPER(JolCraftIds.DWARF_SCRAPPER);

    private final String id;

    DwarfProfession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return id != null
                ? Component.translatable(JolCraftLanguageKeys.category(JolCraftLanguageCategory.ENTITY, id))
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

        if (path.equals(JolCraftIds.DWARF)) return NONE;

        if (path.startsWith(JolCraftIds.DWARF + "_")) {
            return byId(path);
        }

        return NONE;
    }
}