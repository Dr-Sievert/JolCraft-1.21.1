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

import static net.sievert.jolcraft.JolCraft.location;

public class JolCraftTags {

    public static final class Items {

        //Core

        public static final TagKey<Item> SPAWN_EGGS = createTag("spawn_eggs");

        public static final TagKey<Item> DWARF_SPAWN_EGGS = createTag("dwarf_spawn_eggs");

        public static final TagKey<Item> CREATURE_SPAWN_EGGS = createTag("creature_spawn_eggs");

        public static final TagKey<Item> MONSTER_SPAWN_EGGS = createTag("monster_spawn_eggs");

        public static final TagKey<Item> INK_AND_QUILLS = createTag("ink_and_quills");

        public static final TagKey<Item> GEODES = createTag("geodes");

        public static final TagKey<Item> DURABILITY_ENCHANTABLE = createTag("durability_enchantable");

        public static final TagKey<Item> WARHAMMERS = createTag("warhammers");

        public static final TagKey<Item> SPANNERS = createTag("spanners");

        public static final TagKey<Item> ARTISAN_HAMMERS = createTag("artisan_hammers");

        public static final TagKey<Item> CHISELS = createTag("chisels");

        public static final TagKey<Item> PESTLES = createTag("pestles");

        public static final TagKey<Item> GEMS_UNCUT = createTag("gems_uncut");

        public static final TagKey<Item> GEM_CUT = createTag("gem_cut");

        public static final TagKey<Item> GEM_DUST = createTag("gem_dust");

        public static final TagKey<Item> BONUS_TRIM_MATERIALS = createTag("bonus_trim_materials");

        public static final TagKey<Item> PROFESSION_CONTRACTS = createTag("profession_contracts");

        public static final TagKey<Item> REPUTATION_TABLETS = createTag("reputation_tablets");

        public static final TagKey<Item> HOPS = createTag("hops");

        public static final TagKey<Item> REPAIRS_DEEPSLATE = createTag("repairs_deepslate");

        public static final TagKey<Item> REPAIRS_MITHRIL = createTag("repairs_mithril");

        public static final TagKey<Item> MITHRIL_ITEMS = createTag("mithril_items");

        public static final TagKey<Item> LEGENDARY_ITEMS = createTag("legendary_items");

        //Salvage

        public static final TagKey<Item> GLOBAL_SALVAGE = createTag("global_salvage");

        public static final TagKey<Item> GENERAL_SALVAGE = createTag("general_salvage");

        public static final TagKey<Item> DEEPSLATE_SALVAGE = createTag("general_salvage");

        public static final TagKey<Item> TEXTILE_SALVAGE = createTag("textile_salvage");

        public static final TagKey<Item> REDSTONE_SALVAGE = createTag("redstone_salvage");

        public static final TagKey<Item> IRON_SALVAGE = createTag("iron_salvage");

        public static final TagKey<Item> GOLD_SALVAGE = createTag("gold_salvage");

        public static final TagKey<Item> MITHRIL_SALVAGE = createTag("mithril_salvage");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(JolCraft.location(name));
        }
    }

    public static final class Blocks {

        public static final TagKey<Block> DEEPSLATE_BULBS_PLANTABLE = createTag("deepslate_bulbs_plantable");

        public static final TagKey<Block> VERDANT = createTag("verdant");

        public static final TagKey<Block> HOPS_BOTTOM = createTag("hops_bottom");

        public static final TagKey<Block> HOPS_TOP = createTag("hops_top");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(JolCraft.location(name));
        }

    }

    public interface Structures {

        TagKey<Structure> ON_FORGE_EXPLORER_MAPS = create("on_forge_explorer_maps");

        TagKey<Structure> DWARVEN_STRUCTURES = create("dwarven_structures");

        TagKey<Structure> ANCIENT_STRUCTURES = create("ancient_structures");

        private static TagKey<Structure> create(String name) {
            return TagKey.create(Registries.STRUCTURE, location(name));
        }
    }

    public static final class Biomes {
        public static final TagKey<Biome> MOUNTAINS_AND_HILLS = create("mountains_and_hills");

        private static TagKey<Biome> create(String name) {
            return TagKey.create(Registries.BIOME, JolCraft.location(name));
        }
    }

}
