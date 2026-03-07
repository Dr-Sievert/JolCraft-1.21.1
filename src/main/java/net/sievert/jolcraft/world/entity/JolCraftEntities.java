package net.sievert.jolcraft.world.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.entity.creature.JolCraftCreatureIds;
import net.sievert.jolcraft.data.id.entity.object.JolCraftEntityObjectIds;
import net.sievert.jolcraft.world.entity.custom.creature.MuffhornEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.object.RadiantEntity;

import java.util.function.Supplier;

public final class JolCraftEntities {

    private JolCraftEntities(){}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, JolCraft.MOD_ID);

    //Dwarves
    public static final ResourceKey<EntityType<?>> DWARF_KEY = dwarfKey(DwarfProfession.NONE);
    public static final ResourceKey<EntityType<?>> DWARF_ALCHEMIST_KEY = dwarfKey(DwarfProfession.ALCHEMIST);
    public static final ResourceKey<EntityType<?>> DWARF_ARCANIST_KEY = dwarfKey(DwarfProfession.ARCANIST);
    public static final ResourceKey<EntityType<?>> DWARF_ARTISAN_KEY = dwarfKey(DwarfProfession.ARTISAN);
    public static final ResourceKey<EntityType<?>> DWARF_BREWMASTER_KEY = dwarfKey(DwarfProfession.BREWMASTER);
    public static final ResourceKey<EntityType<?>> DWARF_EXPLORER_KEY = dwarfKey(DwarfProfession.EXPLORER);
    public static final ResourceKey<EntityType<?>> DWARF_GUARD_KEY = dwarfKey(DwarfProfession.GUARD);
    public static final ResourceKey<EntityType<?>> DWARF_GUILDMASTER_KEY = dwarfKey(DwarfProfession.GUILDMASTER);
    public static final ResourceKey<EntityType<?>> DWARF_HISTORIAN_KEY = dwarfKey(DwarfProfession.HISTORIAN);
    public static final ResourceKey<EntityType<?>> DWARF_KEEPER_KEY = dwarfKey(DwarfProfession.KEEPER);
    public static final ResourceKey<EntityType<?>> DWARF_MERCHANT_KEY = dwarfKey(DwarfProfession.MERCHANT);
    public static final ResourceKey<EntityType<?>> DWARF_MINER_KEY = dwarfKey(DwarfProfession.MINER);
    public static final ResourceKey<EntityType<?>> DWARF_PRIEST_KEY = dwarfKey(DwarfProfession.PRIEST);
    public static final ResourceKey<EntityType<?>> DWARF_SCRAPPER_KEY = dwarfKey(DwarfProfession.SCRAPPER);

    private static ResourceKey<EntityType<?>> dwarfKey(DwarfProfession prof) {
        return ResourceKey.create(Registries.ENTITY_TYPE, JolCraft.location(prof.getId()));
    }

    public static final Supplier<EntityType<DwarfEntity>> DWARF = registerDwarf(DwarfProfession.NONE, DWARF_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_ALCHEMIST = registerDwarf(DwarfProfession.ALCHEMIST, DWARF_ALCHEMIST_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_ARCANIST = registerDwarf(DwarfProfession.ARCANIST, DWARF_ARCANIST_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_ARTISAN = registerDwarf(DwarfProfession.ARTISAN, DWARF_ARTISAN_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_BREWMASTER = registerDwarf(DwarfProfession.BREWMASTER, DWARF_BREWMASTER_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_EXPLORER = registerDwarf(DwarfProfession.EXPLORER, DWARF_EXPLORER_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_GUARD = registerDwarf(DwarfProfession.GUARD, DWARF_GUARD_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_GUILDMASTER = registerDwarf(DwarfProfession.GUILDMASTER, DWARF_GUILDMASTER_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_HISTORIAN = registerDwarf(DwarfProfession.HISTORIAN, DWARF_HISTORIAN_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_KEEPER = registerDwarf(DwarfProfession.KEEPER, DWARF_KEEPER_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_MERCHANT = registerDwarf(DwarfProfession.MERCHANT, DWARF_MERCHANT_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_MINER = registerDwarf(DwarfProfession.MINER, DWARF_MINER_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_PRIEST = registerDwarf(DwarfProfession.PRIEST, DWARF_PRIEST_KEY);
    public static final Supplier<EntityType<DwarfEntity>> DWARF_SCRAPPER = registerDwarf(DwarfProfession.SCRAPPER, DWARF_SCRAPPER_KEY);

    private static Supplier<EntityType<DwarfEntity>> registerDwarf(
            DwarfProfession profession,
            ResourceKey<EntityType<?>> key
    ) {
        return ENTITY_TYPES.register(
                profession.getId(),
                () -> EntityType.Builder.of(DwarfEntity::new, MobCategory.CREATURE)
                        .sized(0.5f, 1.6f)
                        .build(key)
        );
    }

    //Animals
    public static final ResourceKey<EntityType<?>> MUFFHORN_KEY = ResourceKey.create(Registries.ENTITY_TYPE, JolCraft.location(JolCraftCreatureIds.MUFFHORN));

    public static final Supplier<EntityType<MuffhornEntity>> MUFFHORN =
            ENTITY_TYPES.register(JolCraftCreatureIds.MUFFHORN, () -> EntityType.Builder.of(MuffhornEntity::new, MobCategory.CREATURE)
                    .sized(1.2f, 2.2f).build(MUFFHORN_KEY));

    //Objects
    public static final ResourceKey<EntityType<?>> RADIANT_KEY = ResourceKey.create(Registries.ENTITY_TYPE, JolCraft.location(JolCraftEntityObjectIds.RADIANT));

    public static final Supplier<EntityType<RadiantEntity>> RADIANT =
            ENTITY_TYPES.register(JolCraftEntityObjectIds.RADIANT, () -> EntityType.Builder.of(RadiantEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).build(RADIANT_KEY));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}