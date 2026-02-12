package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession;

import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DwarfProfessionEntityTypes {

    private DwarfProfessionEntityTypes() {}

    private static final Map<DwarfProfession, Supplier<EntityType<DwarfEntity>>> TYPES = new EnumMap<>(DwarfProfession.class);

    static {
        TYPES.put(DwarfProfession.NONE, JolCraftEntities.DWARF);
        TYPES.put(DwarfProfession.ALCHEMIST, JolCraftEntities.DWARF_ALCHEMIST);
        TYPES.put(DwarfProfession.ARCANIST, JolCraftEntities.DWARF_ARCANIST);
        TYPES.put(DwarfProfession.ARTISAN, JolCraftEntities.DWARF_ARTISAN);
        TYPES.put(DwarfProfession.BREWMASTER, JolCraftEntities.DWARF_BREWMASTER);
        TYPES.put(DwarfProfession.EXPLORER, JolCraftEntities.DWARF_EXPLORER);
        TYPES.put(DwarfProfession.GUARD, JolCraftEntities.DWARF_GUARD);
        TYPES.put(DwarfProfession.GUILDMASTER, JolCraftEntities.DWARF_GUILDMASTER);
        TYPES.put(DwarfProfession.HISTORIAN, JolCraftEntities.DWARF_HISTORIAN);
        TYPES.put(DwarfProfession.KEEPER, JolCraftEntities.DWARF_KEEPER);
        TYPES.put(DwarfProfession.MERCHANT, JolCraftEntities.DWARF_MERCHANT);
        TYPES.put(DwarfProfession.MINER, JolCraftEntities.DWARF_MINER);
        TYPES.put(DwarfProfession.PRIEST, JolCraftEntities.DWARF_PRIEST);
        TYPES.put(DwarfProfession.SCRAPPER, JolCraftEntities.DWARF_SCRAPPER);
    }

    public static EntityType<DwarfEntity> get(DwarfProfession profession) {
        Supplier<EntityType<DwarfEntity>> sup = TYPES.get(profession);
        if (sup == null) sup = TYPES.get(DwarfProfession.NONE);
        return sup.get();
    }
}
