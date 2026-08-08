package net.sievert.jolcraft.world.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.entity.creature.JolCraftCreatureIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.creature.MuffhornEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;

import java.util.function.Supplier;

public final class JolCraftEntities {

    private JolCraftEntities(){}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, JolCraft.MOD_ID);

    //Dwarves
    public static final Supplier<EntityType<DwarfEntity>> DWARF = registerDwarf(DwarfProfession.NONE);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_ALCHEMIST = registerDwarf(DwarfProfession.ALCHEMIST);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_ARCANIST = registerDwarf(DwarfProfession.ARCANIST);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_ARTISAN = registerDwarf(DwarfProfession.ARTISAN);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_BREWMASTER = registerDwarf(DwarfProfession.BREWMASTER);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_EXPLORER = registerDwarf(DwarfProfession.EXPLORER);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_GUARD = registerDwarf(DwarfProfession.GUARD);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_GUILDMASTER = registerDwarf(DwarfProfession.GUILDMASTER);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_HISTORIAN = registerDwarf(DwarfProfession.HISTORIAN);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_KEEPER = registerDwarf(DwarfProfession.KEEPER);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_MERCHANT = registerDwarf(DwarfProfession.MERCHANT);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_MINER = registerDwarf(DwarfProfession.MINER);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_PRIEST = registerDwarf(DwarfProfession.PRIEST);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_SCRAPPER = registerDwarf(DwarfProfession.SCRAPPER);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_BLACKSMITH = registerDwarf(DwarfProfession.BLACKSMITH);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_CHAMPION = registerDwarf(DwarfProfession.CHAMPION);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_SMELTER = registerDwarf(DwarfProfession.SMELTER);

    private static Supplier<EntityType<DwarfEntity>> registerDwarf(DwarfProfession profession) {
        return ENTITY_TYPES.register(
                profession.getId(),
                () -> EntityType.Builder.of(DwarfEntity::new, MobCategory.CREATURE)
                        .sized(0.5f, 1.6f)
                        .build(profession.getId())
        );
    }

    //Animals
    public static final Supplier<EntityType<MuffhornEntity>> MUFFHORN =
            ENTITY_TYPES.register(JolCraftCreatureIds.MUFFHORN, () -> EntityType.Builder.of(MuffhornEntity::new, MobCategory.CREATURE)
                    .sized(1.2f, 2.2f)
                    .build(JolCraftCreatureIds.MUFFHORN));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} entity types",
                ENTITY_TYPES.getEntries().size()
        );
    }
}