package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.language.JolCraftLanguageCategory;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

public enum DwarfProfession {

    NONE(JolCraftLanguageKeys.DWARF),
    ALCHEMIST(JolCraftLanguageKeys.DWARF_ALCHEMIST),
    ARCANIST(JolCraftLanguageKeys.DWARF_ARCANIST),
    ARTISAN(JolCraftLanguageKeys.DWARF_ARTISAN),
    BREWMASTER(JolCraftLanguageKeys.DWARF_BREWMASTER),
    EXPLORER(JolCraftLanguageKeys.DWARF_EXPLORER),
    GUARD(JolCraftLanguageKeys.DWARF_GUARD),
    GUILDMASTER(JolCraftLanguageKeys.DWARF_GUILDMASTER),
    HISTORIAN(JolCraftLanguageKeys.DWARF_HISTORIAN),
    KEEPER(JolCraftLanguageKeys.DWARF_KEEPER),
    MERCHANT(JolCraftLanguageKeys.DWARF_MERCHANT),
    MINER(JolCraftLanguageKeys.DWARF_MINER),
    PRIEST(JolCraftLanguageKeys.DWARF_PRIEST),
    SCRAPPER(JolCraftLanguageKeys.DWARF_SCRAPPER);

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

    public static DwarfProfession byId(String id) {
        for (DwarfProfession prof : values()) {
            if (prof.id.equals(id)) return prof;
        }
        return NONE;
    }

    @SuppressWarnings("deprecation")
    public static DwarfProfession fromEntityType(EntityType<?> type) {
        ResourceLocation id = type.builtInRegistryHolder().key().location();
        String path = id.getPath();

        if (path.equals("dwarf")) return NONE;

        if (path.startsWith("dwarf_")) {
            return byId(path);
        }

        return NONE;
    }
}