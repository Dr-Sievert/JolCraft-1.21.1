package net.sievert.jolcraft.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.tag.JolCraftTagIds;
import net.sievert.jolcraft.util.JolCraftStrings;

import static net.sievert.jolcraft.JolCraft.location;

public final class JolCraftTags {

    private JolCraftTags() {}

    public static final class Biomes {

        public static final TagKey<Biome> MOUNTAINS_AND_HILLS = create(JolCraftTagIds.MOUNTAINS_HILLS);
        public static final TagKey<Biome> DWARVEN = create(JolCraftTagIds.DWARVEN);

        private static TagKey<Biome> create(String id) {
            return TagKey.create(Registries.BIOME, JolCraft.location(id));
        }

        private static TagKey<Biome> hasStructure(String structurePath) {
            if (structurePath == null || structurePath.isEmpty()) {
                throw new IllegalArgumentException("Structure name path must not be null/empty for has_structure biome tag.");
            }

            int colon = structurePath.indexOf(':');
            if (colon >= 0) structurePath = structurePath.substring(colon + 1);

            String tagPath = JolCraftStrings.slashed(JolCraftTagIds.HAS_STRUCTURE, structurePath);
            return TagKey.create(Registries.BIOME, JolCraft.location(tagPath));
        }
    }

    public static final class Blocks {

        public static final TagKey<Block> DEEPSLATE_BULBS_PLANTABLE = createTag(JolCraftTagIds.DEEPSLATE_BULBS_PLANTABLE);
        public static final TagKey<Block> VERDANT = createTag(JolCraftTagIds.VERDANT);
        public static final TagKey<Block> HOPS_BOTTOM = createTag(JolCraftTagIds.HOPS_BOTTOM);
        public static final TagKey<Block> HOPS_TOP = createTag(JolCraftTagIds.HOPS_TOP);

        private static TagKey<Block> createTag(String id) {
            return BlockTags.create(JolCraft.location(id));
        }
    }

    public static final class Items {

        // Core
        public static final TagKey<Item> COINS = createTag(JolCraftTagIds.COINS);
        public static final TagKey<Item> SPAWN_EGGS = createTag(JolCraftTagIds.SPAWN_EGGS);
        public static final TagKey<Item> DWARF_SPAWN_EGGS = createTag(JolCraftTagIds.DWARF_SPAWN_EGGS);
        public static final TagKey<Item> CREATURE_SPAWN_EGGS = createTag(JolCraftTagIds.CREATURE_SPAWN_EGGS);
        public static final TagKey<Item> MONSTER_SPAWN_EGGS = createTag(JolCraftTagIds.MONSTER_SPAWN_EGGS);
        public static final TagKey<Item> INK_AND_QUILLS = createTag(JolCraftTagIds.INK_AND_QUILLS);
        public static final TagKey<Item> GEODES = createTag(JolCraftTagIds.GEODES);
        public static final TagKey<Item> DURABILITY_ENCHANTABLE = createTag(JolCraftTagIds.DURABILITY_ENCHANTABLE);
        public static final TagKey<Item> WARHAMMERS = createTag(JolCraftTagIds.WARHAMMERS);
        public static final TagKey<Item> SPANNERS = createTag(JolCraftTagIds.SPANNERS);
        public static final TagKey<Item> ARTISAN_HAMMERS = createTag(JolCraftTagIds.ARTISAN_HAMMERS);
        public static final TagKey<Item> CHISELS = createTag(JolCraftTagIds.CHISELS);
        public static final TagKey<Item> PESTLES = createTag(JolCraftTagIds.PESTLES);
        public static final TagKey<Item> GEMS_UNCUT = createTag(JolCraftTagIds.UNCUT_GEMS);
        public static final TagKey<Item> GEM_CUT = createTag(JolCraftTagIds.CUT_GEMS);
        public static final TagKey<Item> GEM_DUST = createTag(JolCraftTagIds.GEM_DUSTS);
        public static final TagKey<Item> ATTRIBUTE_TRIM_MATERIALS = createTag(JolCraftTagIds.ATTRIBUTE_TRIM_MATERIALS);
        public static final TagKey<Item> PROFESSION_CONTRACTS = createTag(JolCraftTagIds.PROFESSION_CONTRACTS);
        public static final TagKey<Item> REPUTATION_TABLETS = createTag(JolCraftTagIds.REPUTATION_TABLETS);
        public static final TagKey<Item> HOPS = createTag(JolCraftTagIds.HOPS);
        public static final TagKey<Item> HOPS_BREW = createTag(JolCraftTagIds.HOPS_BREW);
        public static final TagKey<Item> REPAIRS_DEEPSLATE = createTag(JolCraftTagIds.REPAIRS_DEEPSLATE);
        public static final TagKey<Item> REPAIRS_MITHRIL = createTag(JolCraftTagIds.REPAIRS_MITHRIL);
        public static final TagKey<Item> MITHRIL_ITEMS = createTag(JolCraftTagIds.MITHRIL_ITEMS);

        // Salvage
        public static final TagKey<Item> GLOBAL_SALVAGE = createTag(JolCraftTagIds.SALVAGE);
        public static final TagKey<Item> GENERAL_SALVAGE = createTag(JolCraftTagIds.GENERAL_SALVAGE);
        public static final TagKey<Item> DEEPSLATE_SALVAGE = createTag(JolCraftTagIds.DEEPSLATE_SALVAGE);
        public static final TagKey<Item> TEXTILE_SALVAGE = createTag(JolCraftTagIds.TEXTILE_SALVAGE);
        public static final TagKey<Item> REDSTONE_SALVAGE = createTag(JolCraftTagIds.REDSTONE_SALVAGE);
        public static final TagKey<Item> IRON_SALVAGE = createTag(JolCraftTagIds.IRON_SALVAGE);
        public static final TagKey<Item> GOLD_SALVAGE = createTag(JolCraftTagIds.GOLD_SALVAGE);
        public static final TagKey<Item> MITHRIL_SALVAGE = createTag(JolCraftTagIds.MITHRIL_SALVAGE);

        private static TagKey<Item> createTag(String id) {
            return ItemTags.create(JolCraft.location(id));
        }
    }

    public interface Structures {

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
}