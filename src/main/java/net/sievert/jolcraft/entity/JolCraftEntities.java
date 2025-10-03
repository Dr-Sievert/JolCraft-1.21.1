package net.sievert.jolcraft.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.custom.animal.MuffhornEntity;
import net.sievert.jolcraft.entity.custom.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.profession.*;
import net.sievert.jolcraft.entity.custom.object.RadiantEntity;

import java.util.function.Supplier;

public class JolCraftEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, JolCraft.MOD_ID);

    //Dwarves
    public static ResourceKey<EntityType<?>> DWARF_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dwarf"));
    public static ResourceKey<EntityType<?>> DWARF_GUILDMASTER_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_guildmaster"));
    public static ResourceKey<EntityType<?>> DWARF_HISTORIAN_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_historian"));
    public static ResourceKey<EntityType<?>> DWARF_MERCHANT_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_merchant"));
    public static ResourceKey<EntityType<?>> DWARF_SCRAPPER_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_scrapper"));
    public static ResourceKey<EntityType<?>> DWARF_BREWMASTER_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_brewmaster"));
    public static ResourceKey<EntityType<?>> DWARF_GUARD_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_guard"));
    public static ResourceKey<EntityType<?>> DWARF_KEEPER_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_keeper"));
    public static ResourceKey<EntityType<?>> DWARF_ARTISAN_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_artisan"));
    public static ResourceKey<EntityType<?>> DWARF_EXPLORER_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_explorer"));
    public static ResourceKey<EntityType<?>> DWARF_MINER_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_miner"));
    public static ResourceKey<EntityType<?>> DWARF_ALCHEMIST_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_alchemist"));
    public static ResourceKey<EntityType<?>> DWARF_ARCANIST_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_arcanist"));
    public static ResourceKey<EntityType<?>> DWARF_PRIEST_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID,"dwarf_priest"));

    public static final Supplier<EntityType<EntityEntity>> DWARF =
            ENTITY_TYPES.register("dwarf", () -> EntityType.Builder.of(EntityEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_KEY));

    public static final Supplier<EntityType<EntityGuildmasterEntity>> DWARF_GUILDMASTER =
            ENTITY_TYPES.register("dwarf_guildmaster", () -> EntityType.Builder.of(EntityGuildmasterEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_GUILDMASTER_KEY));

    public static final Supplier<EntityType<EntityHistorianEntity>> DWARF_HISTORIAN =
            ENTITY_TYPES.register("dwarf_historian", () -> EntityType.Builder.of(EntityHistorianEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_HISTORIAN_KEY));

    public static final Supplier<EntityType<EntityMerchantEntity>> DWARF_MERCHANT =
            ENTITY_TYPES.register("dwarf_merchant", () -> EntityType.Builder.of(EntityMerchantEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_MERCHANT_KEY));

    public static final Supplier<EntityType<EntityScrapperEntity>> DWARF_SCRAPPER =
            ENTITY_TYPES.register("dwarf_scrapper", () -> EntityType.Builder.of(EntityScrapperEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_SCRAPPER_KEY));

    public static final Supplier<EntityType<EntityBrewmasterEntity>> DWARF_BREWMASTER =
            ENTITY_TYPES.register("dwarf_brewmaster", () -> EntityType.Builder.of(EntityBrewmasterEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_BREWMASTER_KEY));

    public static final Supplier<EntityType<EntityGuardEntity>> DWARF_GUARD =
            ENTITY_TYPES.register("dwarf_guard", () -> EntityType.Builder.of(EntityGuardEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_GUARD_KEY));

    public static final Supplier<EntityType<EntityKeeperEntity>> DWARF_KEEPER =
            ENTITY_TYPES.register("dwarf_keeper", () -> EntityType.Builder.of(EntityKeeperEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_KEEPER_KEY));

    public static final Supplier<EntityType<EntityArtisanEntity>> DWARF_ARTISAN =
            ENTITY_TYPES.register("dwarf_artisan", () -> EntityType.Builder.of(EntityArtisanEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_ARTISAN_KEY));

    public static final Supplier<EntityType<EntityExplorerEntity>> DWARF_EXPLORER =
            ENTITY_TYPES.register("dwarf_explorer", () -> EntityType.Builder.of(EntityExplorerEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_EXPLORER_KEY));

    public static final Supplier<EntityType<EntityMinerEntity>> DWARF_MINER =
            ENTITY_TYPES.register("dwarf_miner", () -> EntityType.Builder.of(EntityMinerEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_MINER_KEY));

    public static final Supplier<EntityType<EntityAlchemistEntity>> DWARF_ALCHEMIST =
            ENTITY_TYPES.register("dwarf_alchemist", () -> EntityType.Builder.of(EntityAlchemistEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_ALCHEMIST_KEY));

    public static final Supplier<EntityType<EntityArcanistEntity>> DWARF_ARCANIST =
            ENTITY_TYPES.register("dwarf_arcanist", () -> EntityType.Builder.of(EntityArcanistEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_ARCANIST_KEY));

    public static final Supplier<EntityType<EntityPriestEntity>> DWARF_PRIEST =
            ENTITY_TYPES.register("dwarf_priest", () -> EntityType.Builder.of(EntityPriestEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.6f).build(DWARF_PRIEST_KEY));

    //Animals
    public static ResourceKey<EntityType<?>> MUFFHORN_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "muffhorn"));

    public static final Supplier<EntityType<MuffhornEntity>> MUFFHORN =
            ENTITY_TYPES.register("muffhorn", () -> EntityType.Builder.of(MuffhornEntity::new, MobCategory.CREATURE)
                    .sized(1.2f, 2.2f).build(MUFFHORN_KEY));

    //Objects
    public static ResourceKey<EntityType<?>> RADIANT_KEY = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "radiant"));

    public static final Supplier<EntityType<RadiantEntity>> RADIANT =
            ENTITY_TYPES.register("radiant", () -> EntityType.Builder.of(RadiantEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).build(RADIANT_KEY));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
