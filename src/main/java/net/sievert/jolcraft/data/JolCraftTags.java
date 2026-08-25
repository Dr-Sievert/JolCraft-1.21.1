package net.sievert.jolcraft.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.tag.JolCraftTagIds;
import net.sievert.jolcraft.util.JolCraftStrings;

import static net.sievert.jolcraft.JolCraft.location;

public final class JolCraftTags {

    private JolCraftTags() {}

    public static final class Items {

        // Core
        public static final TagKey<Item> COINS = createTag(JolCraftTagIds.COINS);
        public static final TagKey<Item> TOMES = createTag(JolCraftTagIds.TOMES);
        public static final TagKey<Item> SPAWN_EGGS = createTag(JolCraftTagIds.SPAWN_EGGS);
        public static final TagKey<Item> DWARF_SPAWN_EGGS = createTag(JolCraftTagIds.DWARF_SPAWN_EGGS);
        public static final TagKey<Item> CREATURE_SPAWN_EGGS = createTag(JolCraftTagIds.CREATURE_SPAWN_EGGS);
        public static final TagKey<Item> MONSTER_SPAWN_EGGS = createTag(JolCraftTagIds.MONSTER_SPAWN_EGGS);
        public static final TagKey<Item> INK_AND_QUILLS = createTag(JolCraftTagIds.INK_AND_QUILLS);
        public static final TagKey<Item> GEODES = createTag(JolCraftTagIds.GEODES);
        public static final TagKey<Item> WARHAMMERS = createTag(JolCraftTagIds.WARHAMMERS);
        public static final TagKey<Item> SPANNERS = createTag(JolCraftTagIds.SPANNERS);
        public static final TagKey<Item> ARTISAN_HAMMERS = createTag(JolCraftTagIds.ARTISAN_HAMMERS);
        public static final TagKey<Item> CHISELS = createTag(JolCraftTagIds.CHISELS);
        public static final TagKey<Item> PESTLES = createTag(JolCraftTagIds.PESTLES);
        public static final TagKey<Item> GEMS_UNCUT = createTag(JolCraftTagIds.UNCUT_GEMS);
        public static final TagKey<Item> GEM_CUT = createTag(JolCraftTagIds.CUT_GEMS);
        public static final TagKey<Item> GEM_DUST = createTag(JolCraftTagIds.GEM_DUSTS);
        public static final TagKey<Item> ATTRIBUTE_TRIM_MATERIALS = createTag(JolCraftTagIds.ATTRIBUTE_TRIM_MATERIALS);
        public static final TagKey<Item> PARTIAL_CONTRACTS = createTag(JolCraftTagIds.PARTIAL_CONTRACTS);
        public static final TagKey<Item> PROFESSION_CONTRACTS = createTag(JolCraftTagIds.PROFESSION_CONTRACTS);
        public static final TagKey<Item> REPUTATION_TABLETS = createTag(JolCraftTagIds.REPUTATION_TABLETS);
        public static final TagKey<Item> HOPS_SEEDS = createTag(JolCraftTagIds.HOPS_SEEDS);
        public static final TagKey<Item> HOPS = createTag(JolCraftTagIds.HOPS);
        public static final TagKey<Item> UNFINISHED_BREW = createTag(JolCraftTagIds.UNFINISHED_BREW);
        public static final TagKey<Item> YEAST_BREW = createTag(JolCraftTagIds.YEAST_BREW);
        public static final TagKey<Item> TANNIN_BREW = createTag(JolCraftTagIds.TANNIN_BREW);
        public static final TagKey<Item> REPAIRS_DEEPSLATE = createTag(JolCraftTagIds.REPAIRS_DEEPSLATE);
        public static final TagKey<Item> REPAIRS_MITHRIL = createTag(JolCraftTagIds.REPAIRS_MITHRIL);
        public static final TagKey<Item> MITHRIL_ITEMS = createTag(JolCraftTagIds.MITHRIL_ITEMS);

        // Salvage
        public static final TagKey<Item> GLOBAL_SALVAGE = createTag(JolCraftTagIds.GLOBAL_SALVAGE);
        public static final TagKey<Item> SALVAGE = createTag(JolCraftTagIds.SALVAGE);
        public static final TagKey<Item> SPECIAL_SALVAGE = createTag(JolCraftTagIds.SPECIAL_SALVAGE);

        public static final TagKey<Item> GENERAL_SALVAGE = createTag(JolCraftTagIds.GENERAL_SALVAGE);
        public static final TagKey<Item> DEEPSLATE_SALVAGE = createTag(JolCraftTagIds.DEEPSLATE_SALVAGE);
        public static final TagKey<Item> TEXTILE_SALVAGE = createTag(JolCraftTagIds.TEXTILE_SALVAGE);
        public static final TagKey<Item> REDSTONE_SALVAGE = createTag(JolCraftTagIds.REDSTONE_SALVAGE);
        public static final TagKey<Item> IRON_SALVAGE = createTag(JolCraftTagIds.IRON_SALVAGE);
        public static final TagKey<Item> GOLD_SALVAGE = createTag(JolCraftTagIds.GOLD_SALVAGE);
        public static final TagKey<Item> MITHRIL_SALVAGE = createTag(JolCraftTagIds.MITHRIL_SALVAGE);

        public static final TagKey<Item> SPECIAL_GENERAL_SALVAGE = createTag(JolCraftTagIds.SPECIAL_GENERAL_SALVAGE);
        public static final TagKey<Item> SPECIAL_DEEPSLATE_SALVAGE = createTag(JolCraftTagIds.SPECIAL_DEEPSLATE_SALVAGE);
        public static final TagKey<Item> SPECIAL_TEXTILE_SALVAGE = createTag(JolCraftTagIds.SPECIAL_TEXTILE_SALVAGE);
        public static final TagKey<Item> SPECIAL_IRON_SALVAGE = createTag(JolCraftTagIds.SPECIAL_IRON_SALVAGE);
        public static final TagKey<Item> SPECIAL_GOLD_SALVAGE = createTag(JolCraftTagIds.SPECIAL_GOLD_SALVAGE);
        public static final TagKey<Item> SPECIAL_MITHRIL_SALVAGE = createTag(JolCraftTagIds.SPECIAL_MITHRIL_SALVAGE);

        private static TagKey<Item> createTag(String id) {
            return ItemTags.create(JolCraft.location(id));
        }
    }

    public static final class Blocks {

        public static final TagKey<Block> DEEPSLATE_BULBS_PLANTABLE = createTag(JolCraftTagIds.DEEPSLATE_BULBS_PLANTABLE);
        public static final TagKey<Block> CYANELLA_PLANTABLE = createTag(JolCraftTagIds.CYANELLA_PLANTABLE);
        public static final TagKey<Block> VERDANT = createTag(JolCraftTagIds.VERDANT);
        public static final TagKey<Block> HOPS_BOTTOM = createTag(JolCraftTagIds.HOPS_BOTTOM);
        public static final TagKey<Block> HOPS_TOP = createTag(JolCraftTagIds.HOPS_TOP);
        public static final TagKey<Block> SHEARS_LOOT = createTag(JolCraftTagIds.SHEARS_LOOT);

        private static TagKey<Block> createTag(String id) {
            return BlockTags.create(JolCraft.location(id));
        }
    }

    public static final class Biomes {

        public static final TagKey<Biome> MOUNTAINS_AND_HILLS = create(JolCraftTagIds.MOUNTAINS_HILLS);
        public static final TagKey<Biome> DWARVEN = create(JolCraftTagIds.DWARVEN);
        public static final TagKey<Biome> MITHRIL_SPECIAL = create(JolCraftTagIds.MITHRIL_SPECIAL);

        private static TagKey<Biome> create(String id) {
            return TagKey.create(Registries.BIOME, JolCraft.location(id));
        }
    }

    public static final class EntityTypes {

        public static final TagKey<EntityType<?>> EXPLOSION_IMMUNE = create(JolCraftTagIds.EXPLOSION_IMMUNE);
        public static final TagKey<EntityType<?>> FIRE_IMMUNE = create(JolCraftTagIds.FIRE_IMMUNE);
        public static final TagKey<EntityType<?>> FROST_IMMUNE = create(JolCraftTagIds.FROST_IMMUNE);
        public static final TagKey<EntityType<?>> MAGIC_IMMUNE = create(JolCraftTagIds.MAGIC_IMMUNE);
        public static final TagKey<EntityType<?>> POISON_IMMUNE = create(JolCraftTagIds.POISON_IMMUNE);
        public static final TagKey<EntityType<?>> WITHER_IMMUNE = create(JolCraftTagIds.WITHER_IMMUNE);

        public static final TagKey<EntityType<?>> EXPLOSION_RESISTANT = create(JolCraftTagIds.EXPLOSION_RESISTANT);
        public static final TagKey<EntityType<?>> FIRE_RESISTANT = create(JolCraftTagIds.FIRE_RESISTANT);
        public static final TagKey<EntityType<?>> FROST_RESISTANT = create(JolCraftTagIds.FROST_RESISTANT);
        public static final TagKey<EntityType<?>> MAGIC_RESISTANT = create(JolCraftTagIds.MAGIC_RESISTANT);
        public static final TagKey<EntityType<?>> POISON_RESISTANT = create(JolCraftTagIds.POISON_RESISTANT);
        public static final TagKey<EntityType<?>> WITHER_RESISTANT = create(JolCraftTagIds.WITHER_RESISTANT);

        public static final TagKey<EntityType<?>> EXPLOSION_VULNERABLE = create(JolCraftTagIds.EXPLOSION_VULNERABLE);
        public static final TagKey<EntityType<?>> FIRE_VULNERABLE = create(JolCraftTagIds.FIRE_VULNERABLE);
        public static final TagKey<EntityType<?>> FROST_VULNERABLE = create(JolCraftTagIds.FROST_VULNERABLE);
        public static final TagKey<EntityType<?>> MAGIC_VULNERABLE = create(JolCraftTagIds.MAGIC_VULNERABLE);
        public static final TagKey<EntityType<?>> POISON_VULNERABLE = create(JolCraftTagIds.POISON_VULNERABLE);
        public static final TagKey<EntityType<?>> WITHER_VULNERABLE = create(JolCraftTagIds.WITHER_VULNERABLE);

        private static TagKey<EntityType<?>> create(String id) {
            return TagKey.create(Registries.ENTITY_TYPE, JolCraft.location(id));
        }
    }

    public interface Structures {

        TagKey<Structure> FEATURE_PROTECTED = create(JolCraftTagIds.FEATURE_PROTECTED);

        TagKey<Structure> ON_DWARVEN_FORTRESS_EXPLORER_MAPS = create(JolCraftTagIds.ON_DWARVEN_FORTRESS_EXPLORER_MAPS);

        TagKey<Structure> VILLAGES = create(JolCraftTagIds.VILLAGES);
        TagKey<Structure> PILLAGERS = create(JolCraftTagIds.PILLAGERS);
        TagKey<Structure> SURFACE = create(JolCraftTagIds.SURFACE);
        TagKey<Structure> DWARVEN = create(JolCraftTagIds.DWARVEN);
        TagKey<Structure> RUINS = create(JolCraftTagIds.RUINS);
        TagKey<Structure> OCEAN = create(JolCraftTagIds.OCEAN);
        TagKey<Structure> UNDERGROUND = create(JolCraftTagIds.UNDERGROUND);
        TagKey<Structure> NETHER_PORTALS = create(JolCraftTagIds.NETHER_PORTALS);

        private static TagKey<Structure> create(String id) {
            return TagKey.create(Registries.STRUCTURE, location(id));
        }
    }

    public static final class DamageTypes {

        public static final TagKey<DamageType> CURSE = create(JolCraftTagIds.CURSE);

        private static TagKey<DamageType> create(String id) {
            return TagKey.create(
                    Registries.DAMAGE_TYPE,
                    JolCraft.location(id)
            );
        }

        private DamageTypes() {}
    }

    public static final class Instruments {

        public static final TagKey<Instrument> WAR_HORNS =
                TagKey.create(
                        Registries.INSTRUMENT,
                        JolCraft.location(JolCraftStrings.plural(JolCraftItemIds.WAR_HORN))
                );

        private Instruments() {}
    }
}