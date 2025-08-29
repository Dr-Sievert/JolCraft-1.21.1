package net.sievert.jolcraft.datagen.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class JolCraftItemTagProvider extends ItemTagsProvider {
    public JolCraftItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, JolCraft.MOD_ID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //Vanilla

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get())
                .add(JolCraftItems.MITHRIL_ARTISAN_HAMMER.get())
                .add(JolCraftItems.DEEPSLATE_CHISEL.get())
                .add(JolCraftItems.MITHRIL_CHISEL.get())
                .add(JolCraftItems.COPPER_SPANNER.get())
                .add(JolCraftItems.IRON_SPANNER.get());

        tag(ItemTags.SWORDS)
                .add(JolCraftItems.DEEPSLATE_SWORD.get())
                .add(JolCraftItems.DEEPSLATE_WARHAMMER.get())
                .add(JolCraftItems.MITHRIL_SWORD.get())
                .add(JolCraftItems.MITHRIL_WARHAMMER.get());

        tag(ItemTags.PICKAXES)
                .add(JolCraftItems.DEEPSLATE_PICKAXE.get())
                .add(JolCraftItems.MITHRIL_PICKAXE.get());

        tag(ItemTags.SHOVELS)
                .add(JolCraftItems.DEEPSLATE_SHOVEL.get())
                .add(JolCraftItems.MITHRIL_SHOVEL.get());

        tag(ItemTags.AXES)
                .add(JolCraftItems.DEEPSLATE_AXE.get())
                .add(JolCraftItems.MITHRIL_AXE.get());

        tag(ItemTags.HOES)
                .add(JolCraftItems.DEEPSLATE_HOE.get())
                .add(JolCraftItems.MITHRIL_HOE.get());

        tag(ItemTags.HEAD_ARMOR)
                .add(JolCraftItems.DEEPSLATE_HELMET.get())
                .add(JolCraftItems.MITHRIL_HELMET.get());

        tag(ItemTags.CHEST_ARMOR)
                .add(JolCraftItems.DEEPSLATE_CHESTPLATE.get())
                .add(JolCraftItems.MITHRIL_CHESTPLATE.get());

        tag(ItemTags.LEG_ARMOR)
                .add(JolCraftItems.DEEPSLATE_LEGGINGS.get())
                .add(JolCraftItems.MITHRIL_LEGGINGS.get());

        tag(ItemTags.FOOT_ARMOR)
                .add(JolCraftItems.DEEPSLATE_BOOTS.get())
                .add(JolCraftItems.MITHRIL_BOOTS.get());

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(JolCraftItems.DEEPSLATE_HELMET.get())
                .add(JolCraftItems.DEEPSLATE_CHESTPLATE.get())
                .add(JolCraftItems.DEEPSLATE_LEGGINGS.get())
                .add(JolCraftItems.DEEPSLATE_BOOTS.get())
                .add(JolCraftItems.MITHRIL_HELMET.get())
                .add(JolCraftItems.MITHRIL_CHESTPLATE.get())
                .add(JolCraftItems.MITHRIL_LEGGINGS.get())
                .add(JolCraftItems.MITHRIL_BOOTS.get());

        tag(ItemTags.TRIM_MATERIALS)
                .add(JolCraftItems.DEEPSLATE_PLATE.get())
                .add(JolCraftItems.MITHRIL_INGOT.get());

        tag(ItemTags.DYEABLE)
                .add(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .add(JolCraftItems.DEEPSLATE_COMPASS.get());

        //Common Neoforge

        tag(Tags.Items.SEEDS)
                .add(JolCraftItems.BARLEY_SEEDS.get())
                .add(JolCraftItems.ASGARNIAN_SEEDS.get())
                .add(JolCraftItems.DUSKHOLD_SEEDS.get())
                .add(JolCraftItems.KRANDONIAN_SEEDS.get())
                .add(JolCraftItems.YANILLIAN_SEEDS.get());

        tag(Tags.Items.SEEDS)
                .add(JolCraftItems.BARLEY_SEEDS.get())
                .add(JolCraftItems.ASGARNIAN_SEEDS.get())
                .add(JolCraftItems.DUSKHOLD_SEEDS.get())
                .add(JolCraftItems.KRANDONIAN_SEEDS.get())
                .add(JolCraftItems.YANILLIAN_SEEDS.get());

        tag(Tags.Items.CROPS)
                .add(JolCraftItems.BARLEY.get())
                .add(JolCraftItems.DEEPSLATE_BULBS.get())
                .add(JolCraftItems.ASGARNIAN_HOPS.get())
                .add(JolCraftItems.DUSKHOLD_HOPS.get())
                .add(JolCraftItems.KRANDONIAN_HOPS.get())
                .add(JolCraftItems.YANILLIAN_HOPS.get());

        tag(Tags.Items.GEMS)
                .add(JolCraftItems.AEGISCORE.get())
                .add(JolCraftItems.ASHFANG.get())
                .add(JolCraftItems.DEEPMARROW.get())
                .add(JolCraftItems.EARTHBLOOD.get())
                .add(JolCraftItems.EMBERGLASS.get())
                .add(JolCraftItems.FROSTVEIN.get())
                .add(JolCraftItems.GRIMSTONE.get())
                .add(JolCraftItems.IRONHEART.get())
                .add(JolCraftItems.LUMIERE.get())
                .add(JolCraftItems.MOONSHARD.get())
                .add(JolCraftItems.RUSTAGATE.get())
                .add(JolCraftItems.SKYBURROW.get())
                .add(JolCraftItems.SUNGLEAM.get())
                .add(JolCraftItems.VERDANITE.get())
                .add(JolCraftItems.WOECRYSTAL.get())
                .add(JolCraftItems.AEGISCORE_CUT.get())
                .add(JolCraftItems.ASHFANG_CUT.get())
                .add(JolCraftItems.DEEPMARROW_CUT.get())
                .add(JolCraftItems.EARTHBLOOD_CUT.get())
                .add(JolCraftItems.EMBERGLASS_CUT.get())
                .add(JolCraftItems.FROSTVEIN_CUT.get())
                .add(JolCraftItems.GRIMSTONE_CUT.get())
                .add(JolCraftItems.IRONHEART_CUT.get())
                .add(JolCraftItems.LUMIERE_CUT.get())
                .add(JolCraftItems.MOONSHARD_CUT.get())
                .add(JolCraftItems.RUSTAGATE_CUT.get())
                .add(JolCraftItems.SKYBURROW_CUT.get())
                .add(JolCraftItems.SUNGLEAM_CUT.get())
                .add(JolCraftItems.VERDANITE_CUT.get())
                .add(JolCraftItems.WOECRYSTAL_CUT.get());


        tag(Tags.Items.DUSTS)
                .add(JolCraftItems.INVERIX.get())
                .add(JolCraftItems.AEGISCORE_DUST.get())
                .add(JolCraftItems.ASHFANG_DUST.get())
                .add(JolCraftItems.DEEPMARROW_DUST.get())
                .add(JolCraftItems.EARTHBLOOD_DUST.get())
                .add(JolCraftItems.EMBERGLASS_DUST.get())
                .add(JolCraftItems.FROSTVEIN_DUST.get())
                .add(JolCraftItems.GRIMSTONE_DUST.get())
                .add(JolCraftItems.IRONHEART_DUST.get())
                .add(JolCraftItems.LUMIERE_DUST.get())
                .add(JolCraftItems.MOONSHARD_DUST.get())
                .add(JolCraftItems.RUSTAGATE_DUST.get())
                .add(JolCraftItems.SKYBURROW_DUST.get())
                .add(JolCraftItems.SUNGLEAM_DUST.get())
                .add(JolCraftItems.VERDANITE_DUST.get())
                .add(JolCraftItems.WOECRYSTAL_DUST.get());

        tag(Tags.Items.MUSHROOMS)
                .add(JolCraftBlocks.FESTERLING.get().asItem())
                .add(JolCraftBlocks.DUSKCAP.get().asItem());

        tag(Tags.Items.ORES)
                .add(JolCraftBlocks.GEODE_BLOCK.get().asItem())
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().asItem());

        tag(Tags.Items.ORE_RATES_SINGULAR)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().asItem());

        tag(Tags.Items.ORE_RATES_DENSE)
                .add(JolCraftBlocks.GEODE_BLOCK.get().asItem());

        tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().asItem());

        tag(Tags.Items.RAW_MATERIALS)
                .add(JolCraftItems.AEGISCORE.get())
                .add(JolCraftItems.ASHFANG.get())
                .add(JolCraftItems.DEEPMARROW.get())
                .add(JolCraftItems.EARTHBLOOD.get())
                .add(JolCraftItems.EMBERGLASS.get())
                .add(JolCraftItems.FROSTVEIN.get())
                .add(JolCraftItems.GRIMSTONE.get())
                .add(JolCraftItems.IRONHEART.get())
                .add(JolCraftItems.LUMIERE.get())
                .add(JolCraftItems.MOONSHARD.get())
                .add(JolCraftItems.RUSTAGATE.get())
                .add(JolCraftItems.SKYBURROW.get())
                .add(JolCraftItems.SUNGLEAM.get())
                .add(JolCraftItems.VERDANITE.get())
                .add(JolCraftItems.WOECRYSTAL.get())
                .add(JolCraftItems.IMPURE_MITHRIL.get())
                .add(JolCraftItems.PURE_MITHRIL.get())
                .add(JolCraftItems.DEEPSLATE_BULBS.get());

        tag(Tags.Items.INGOTS)
                .add(JolCraftItems.MITHRIL_INGOT.get());

        tag(Tags.Items.NUGGETS)
                .add(JolCraftItems.MITHRIL_NUGGET.get());

        tag(Tags.Items.DRINKS)
                .add(JolCraftItems.DWARVEN_BREW.get());

        tag(Tags.Items.DRINKS_MILK)
                .add(JolCraftItems.MUFFHORN_MILK_BUCKET.get());

        tag(Tags.Items.DRINK_CONTAINING_BUCKET)
                .add(JolCraftItems.MUFFHORN_MILK_BUCKET.get());

        tag(Tags.Items.TOOLS)
                .addTags(JolCraftTags.Items.SPANNERS)
                .addTags(JolCraftTags.Items.ARTISAN_HAMMERS)
                .addTags(JolCraftTags.Items.CHISELS);

        //Custom

        tag(JolCraftTags.Items.REPUTATION_TABLETS)
                .add(JolCraftItems.REPUTATION_TABLET_0.get())
                .add(JolCraftItems.REPUTATION_TABLET_1.get())
                .add(JolCraftItems.REPUTATION_TABLET_2.get())
                .add(JolCraftItems.REPUTATION_TABLET_3.get())
                .add(JolCraftItems.REPUTATION_TABLET_4.get());

        tag(JolCraftTags.Items.INK_AND_QUILLS)
                .add(JolCraftItems.QUILL_FULL.get())
                .add(JolCraftItems.QUILL_HALF.get())
                .add(JolCraftItems.QUILL_SMALL.get());


        tag(JolCraftTags.Items.DWARF_SPAWN_EGGS)
                .add(JolCraftItems.DWARF_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_MERCHANT_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_GUARD_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_KEEPER_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_ARTISAN_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_EXPLORER_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_MINER_SPAWN_EGG.get());

        tag(JolCraftTags.Items.CREATURE_SPAWN_EGGS)
                .add(JolCraftItems.MUFFHORN_SPAWN_EGG.get());

        tag(JolCraftTags.Items.SPAWN_EGGS)
                .addTags(JolCraftTags.Items.DWARF_SPAWN_EGGS)
                .addTags(JolCraftTags.Items.CREATURE_SPAWN_EGGS);

        tag(JolCraftTags.Items.GEODES)
                .add(JolCraftItems.GEODE_SMALL.get())
                .add(JolCraftItems.GEODE_MEDIUM.get())
                .add(JolCraftItems.GEODE_LARGE.get());

        tag(JolCraftTags.Items.GEMS_UNCUT)
                .add(JolCraftItems.AEGISCORE.get())
                .add(JolCraftItems.ASHFANG.get())
                .add(JolCraftItems.DEEPMARROW.get())
                .add(JolCraftItems.EARTHBLOOD.get())
                .add(JolCraftItems.EMBERGLASS.get())
                .add(JolCraftItems.FROSTVEIN.get())
                .add(JolCraftItems.GRIMSTONE.get())
                .add(JolCraftItems.IRONHEART.get())
                .add(JolCraftItems.LUMIERE.get())
                .add(JolCraftItems.MOONSHARD.get())
                .add(JolCraftItems.RUSTAGATE.get())
                .add(JolCraftItems.SKYBURROW.get())
                .add(JolCraftItems.SUNGLEAM.get())
                .add(JolCraftItems.VERDANITE.get())
                .add(JolCraftItems.WOECRYSTAL.get());

        tag(JolCraftTags.Items.GEM_CUT)
                .add(JolCraftItems.AEGISCORE_CUT.get())
                .add(JolCraftItems.ASHFANG_CUT.get())
                .add(JolCraftItems.DEEPMARROW_CUT.get())
                .add(JolCraftItems.EARTHBLOOD_CUT.get())
                .add(JolCraftItems.EMBERGLASS_CUT.get())
                .add(JolCraftItems.FROSTVEIN_CUT.get())
                .add(JolCraftItems.GRIMSTONE_CUT.get())
                .add(JolCraftItems.IRONHEART_CUT.get())
                .add(JolCraftItems.LUMIERE_CUT.get())
                .add(JolCraftItems.MOONSHARD_CUT.get())
                .add(JolCraftItems.RUSTAGATE_CUT.get())
                .add(JolCraftItems.SKYBURROW_CUT.get())
                .add(JolCraftItems.SUNGLEAM_CUT.get())
                .add(JolCraftItems.VERDANITE_CUT.get())
                .add(JolCraftItems.WOECRYSTAL_CUT.get());

        tag(JolCraftTags.Items.GEM_DUST)
                .add(JolCraftItems.AEGISCORE_DUST.get())
                .add(JolCraftItems.ASHFANG_DUST.get())
                .add(JolCraftItems.DEEPMARROW_DUST.get())
                .add(JolCraftItems.EARTHBLOOD_DUST.get())
                .add(JolCraftItems.EMBERGLASS_DUST.get())
                .add(JolCraftItems.FROSTVEIN_DUST.get())
                .add(JolCraftItems.GRIMSTONE_DUST.get())
                .add(JolCraftItems.IRONHEART_DUST.get())
                .add(JolCraftItems.LUMIERE_DUST.get())
                .add(JolCraftItems.MOONSHARD_DUST.get())
                .add(JolCraftItems.RUSTAGATE_DUST.get())
                .add(JolCraftItems.SKYBURROW_DUST.get())
                .add(JolCraftItems.SUNGLEAM_DUST.get())
                .add(JolCraftItems.VERDANITE_DUST.get())
                .add(JolCraftItems.WOECRYSTAL_DUST.get());

        tag(JolCraftTags.Items.GENERAL_SALVAGE)
                .add(Items.TRIPWIRE_HOOK)
                .add(Items.FLINT_AND_STEEL)
                .add(Items.SHIELD)
                .add(Items.FILLED_MAP)
                .add(JolCraftItems.EXPIRED_POTION.get());

        tag(JolCraftTags.Items.DEEPSLATE_SALVAGE)
                .add(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get())
                .add(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get())
                .add(JolCraftItems.BROKEN_DEEPSLATE_GEAR.get())
                .add(JolCraftItems.DEEPSLATE_MUG.get())
                .add(JolCraftItems.BROKEN_TABLET.get());

        tag(JolCraftTags.Items.TEXTILE_SALVAGE)
                .add(Items.LEATHER_HELMET)
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.LEATHER_BOOTS)
                .add(Items.LEATHER_HORSE_ARMOR)
                .add(JolCraftItems.OLD_FABRIC.get());

        tag(JolCraftTags.Items.REDSTONE_SALVAGE)
                .add(Items.COMPASS)
                .add(Items.CLOCK)
                .add(Items.REPEATER)
                .add(Items.COMPARATOR);

        tag(JolCraftTags.Items.IRON_SALVAGE)
                .add(Items.IRON_SWORD)
                .add(Items.IRON_PICKAXE)
                .add(Items.IRON_SHOVEL)
                .add(Items.IRON_AXE)
                .add(Items.IRON_HOE)
                .add(Items.IRON_HELMET)
                .add(Items.IRON_CHESTPLATE)
                .add(Items.IRON_LEGGINGS)
                .add(Items.IRON_BOOTS)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.CHAINMAIL_CHESTPLATE)
                .add(Items.CHAINMAIL_LEGGINGS)
                .add(Items.CHAINMAIL_BOOTS)
                .add(Items.IRON_HORSE_ARMOR)
                .add(Items.SHEARS)
                .add(JolCraftItems.BROKEN_PICKAXE.get())
                .add(JolCraftItems.BROKEN_AMULET.get())
                .add(JolCraftItems.RUSTY_TONGS.get())
                .add(JolCraftItems.INGOT_MOULD.get());

        tag(JolCraftTags.Items.GOLD_SALVAGE)
                .add(Items.GOLDEN_SWORD)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_SHOVEL)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_HOE)
                .add(Items.GOLDEN_HELMET)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.GOLDEN_HORSE_ARMOR)
                .add(JolCraftItems.BROKEN_BELT.get())
                .add(JolCraftItems.BROKEN_COINS.get());

        tag(JolCraftTags.Items.MITHRIL_SALVAGE)
                .add(JolCraftItems.BROKEN_MITHRIL_PLATE.get())
                .add(JolCraftItems.BROKEN_MITHRIL_SWORD.get())
                .add(JolCraftItems.MITHRIL_SALVAGE.get());

        tag(JolCraftTags.Items.GLOBAL_SALVAGE)
                .addTag(JolCraftTags.Items.GENERAL_SALVAGE)
                .addTag(JolCraftTags.Items.TEXTILE_SALVAGE)
                .addTag(JolCraftTags.Items.REDSTONE_SALVAGE)
                .addTag(JolCraftTags.Items.IRON_SALVAGE)
                .addTag(JolCraftTags.Items.GOLD_SALVAGE)
                .addTag(JolCraftTags.Items.MITHRIL_SALVAGE);

        tag(JolCraftTags.Items.SIGNED_CONTRACTS)
                .add(JolCraftItems.CONTRACT_GUILDMASTER.get())
                .add(JolCraftItems.CONTRACT_MERCHANT.get())
                .add(JolCraftItems.CONTRACT_HISTORIAN.get())
                .add(JolCraftItems.CONTRACT_SCRAPPER.get())
                .add(JolCraftItems.CONTRACT_GUARD.get())
                .add(JolCraftItems.CONTRACT_EXPLORER.get())
                .add(JolCraftItems.CONTRACT_KEEPER.get())
                .add(JolCraftItems.CONTRACT_MINER.get())
                .add(JolCraftItems.CONTRACT_BREWMASTER.get())
                .add(JolCraftItems.CONTRACT_ARTISAN.get())
                .add(JolCraftItems.CONTRACT_ALCHEMIST.get())
                .add(JolCraftItems.CONTRACT_ARCANIST.get())
                .add(JolCraftItems.CONTRACT_PRIEST.get())
                .add(JolCraftItems.CONTRACT_CHAMPION.get())
                .add(JolCraftItems.CONTRACT_BLACKSMITH.get())
                .add(JolCraftItems.CONTRACT_SMELTER.get());

        tag(JolCraftTags.Items.REPUTATION_TABLETS)
                .add(JolCraftItems.REPUTATION_TABLET_0.get())
                .add(JolCraftItems.REPUTATION_TABLET_1.get())
                .add(JolCraftItems.REPUTATION_TABLET_2.get())
                .add(JolCraftItems.REPUTATION_TABLET_3.get())
                .add(JolCraftItems.REPUTATION_TABLET_4.get());

        tag(JolCraftTags.Items.HOPS)
                .add(JolCraftItems.ASGARNIAN_HOPS.get())
                .add(JolCraftItems.DUSKHOLD_HOPS.get())
                .add(JolCraftItems.KRANDONIAN_HOPS.get())
                .add(JolCraftItems.YANILLIAN_HOPS.get());

        tag(JolCraftTags.Items.BONUS_TRIM_MATERIALS)
                .add(JolCraftItems.AEGISCORE_CUT.get())
                .add(JolCraftItems.ASHFANG_CUT.get())
                .add(JolCraftItems.DEEPMARROW_CUT.get())
                .add(JolCraftItems.EARTHBLOOD_CUT.get())
                .add(JolCraftItems.EMBERGLASS_CUT.get())
                .add(JolCraftItems.FROSTVEIN_CUT.get())
                .add(JolCraftItems.GRIMSTONE_CUT.get())
                .add(JolCraftItems.IRONHEART_CUT.get())
                .add(JolCraftItems.LUMIERE_CUT.get())
                .add(JolCraftItems.MOONSHARD_CUT.get())
                .add(JolCraftItems.RUSTAGATE_CUT.get())
                .add(JolCraftItems.SKYBURROW_CUT.get())
                .add(JolCraftItems.SUNGLEAM_CUT.get())
                .add(JolCraftItems.VERDANITE_CUT.get())
                .add(JolCraftItems.WOECRYSTAL_CUT.get());

        tag(JolCraftTags.Items.MITHRIL_ITEMS)
                .add(JolCraftItems.MITHRIL_SWORD.get())
                .add(JolCraftItems.MITHRIL_WARHAMMER.get())
                .add(JolCraftItems.MITHRIL_PICKAXE.get())
                .add(JolCraftItems.MITHRIL_SHOVEL.get())
                .add(JolCraftItems.MITHRIL_AXE.get())
                .add(JolCraftItems.MITHRIL_HOE.get())
                .add(JolCraftItems.MITHRIL_HELMET.get())
                .add(JolCraftItems.MITHRIL_CHESTPLATE.get())
                .add(JolCraftItems.MITHRIL_LEGGINGS.get())
                .add(JolCraftItems.MITHRIL_BOOTS.get())
                .add(JolCraftItems.MITHRIL_ARTISAN_HAMMER.get())
                .add(JolCraftItems.MITHRIL_CHISEL.get());

        tag(JolCraftTags.Items.LEGENDARY_ITEMS)
                .add(JolCraftItems.REPUTATION_TABLET_4.get())
                .add(JolCraftItems.LEGENDARY_PAGE.get())
                .add(JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());

        tag(JolCraftTags.Items.REPAIRS_DEEPSLATE)
                .add(JolCraftItems.DEEPSLATE_PLATE.get());

        tag(JolCraftTags.Items.REPAIRS_MITHRIL)
                .add(JolCraftItems.MITHRIL_INGOT.get());

        tag(JolCraftTags.Items.SPANNERS)
                .add(JolCraftItems.COPPER_SPANNER.get())
                .add(JolCraftItems.IRON_SPANNER.get());

        tag(JolCraftTags.Items.ARTISAN_HAMMERS)
                .add(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get())
                .add(JolCraftItems.MITHRIL_ARTISAN_HAMMER.get());

        tag(JolCraftTags.Items.CHISELS)
                .add(JolCraftItems.DEEPSLATE_CHISEL.get())
                .add(JolCraftItems.MITHRIL_CHISEL.get());

    }


}