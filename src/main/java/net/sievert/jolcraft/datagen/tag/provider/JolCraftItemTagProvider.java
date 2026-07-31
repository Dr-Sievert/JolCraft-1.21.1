package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.equipment.JolCraftArmorItemSet;
import net.sievert.jolcraft.world.item.material.trim.JolCraftAttributeTrimMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftVanillaTrimMaterials;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class JolCraftItemTagProvider
        extends ItemTagsProvider
        implements JolCraftMainTagProvider<JolCraftItemTagProvider> {

    private final @Nullable ExistingFileHelper existingFileHelper;

    public JolCraftItemTagProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @NotNull CompletableFuture<TagLookup<Block>> blockTags,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, JolCraft.MOD_ID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String tagType() {
        return JolCraftDictionary.ITEM;
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        generate(this, null, CompletableFuture.completedFuture(provider), existingFileHelper);

        JolCraftDataTracking.logExplicitCount(
                this,
                this.builders.size(),
                JolCraftStrings.spaced(tagType(), JolCraftStrings.plural(domain().getId()))
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run(
            @NotNull JolCraftItemTagProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        // Tools

        target.tag(JolCraftTags.Items.WARHAMMERS)
                .add(JolCraftItems.DEEPSLATE_WARHAMMER.get())
                .add(JolCraftItems.MITHRIL_WARHAMMER.get());

        target.tag(ItemTags.SWORDS)
                .add(JolCraftItems.DEEPSLATE_SWORD.get())
                .add(JolCraftItems.MITHRIL_SWORD.get())
                .addTags(JolCraftTags.Items.WARHAMMERS);

        target.tag(ItemTags.PICKAXES)
                .add(JolCraftItems.DEEPSLATE_PICKAXE.get())
                .add(JolCraftItems.MITHRIL_PICKAXE.get());

        target.tag(ItemTags.SHOVELS)
                .add(JolCraftItems.DEEPSLATE_SHOVEL.get())
                .add(JolCraftItems.MITHRIL_SHOVEL.get());

        target.tag(ItemTags.AXES)
                .add(JolCraftItems.DEEPSLATE_AXE.get())
                .add(JolCraftItems.MITHRIL_AXE.get());

        target.tag(ItemTags.HOES)
                .add(JolCraftItems.DEEPSLATE_HOE.get())
                .add(JolCraftItems.MITHRIL_HOE.get());

        target.tag(JolCraftTags.Items.SPANNERS)
                .add(JolCraftItems.DEEPSLATE_SPANNER.get())
                .add(JolCraftItems.MITHRIL_SPANNER.get());

        target.tag(JolCraftTags.Items.ARTISAN_HAMMERS)
                .add(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get())
                .add(JolCraftItems.MITHRIL_ARTISAN_HAMMER.get());

        target.tag(JolCraftTags.Items.CHISELS)
                .add(JolCraftItems.DEEPSLATE_CHISEL.get())
                .add(JolCraftItems.MITHRIL_CHISEL.get());

        target.tag(JolCraftTags.Items.PESTLES)
                .add(JolCraftItems.DEEPSLATE_PESTLE.get())
                .add(JolCraftItems.MITHRIL_PESTLE.get());

        target.tag(JolCraftTags.Items.DURABILITY_ENCHANTABLE)
                .addTags(JolCraftTags.Items.SPANNERS)
                .addTags(JolCraftTags.Items.ARTISAN_HAMMERS)
                .addTags(JolCraftTags.Items.CHISELS)
                .addTags(JolCraftTags.Items.PESTLES);

        target.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .addTags(JolCraftTags.Items.DURABILITY_ENCHANTABLE);

        target.tag(Tags.Items.TOOLS)
                .addTags(JolCraftTags.Items.SPANNERS)
                .addTags(JolCraftTags.Items.ARTISAN_HAMMERS)
                .addTags(JolCraftTags.Items.CHISELS);

        // Armor

        for (ArmorItem.Type type : JolCraftEquipmentHelper.PLAYER_ARMOR_TYPES) {
            var slotTag = target.tag(switch (type) {
                case HELMET -> ItemTags.HEAD_ARMOR;
                case CHESTPLATE -> ItemTags.CHEST_ARMOR;
                case LEGGINGS -> ItemTags.LEG_ARMOR;
                case BOOTS -> ItemTags.FOOT_ARMOR;
                case BODY -> throw new IllegalArgumentException("Unsupported armor type: " + type);
            });

            for (JolCraftArmorItemSet set : JolCraftItems.ARMOR_SETS) {
                slotTag.add(set.get(type).get());
            }
        }

        var trimmable = target.tag(ItemTags.TRIMMABLE_ARMOR);
        for (JolCraftArmorItemSet set : JolCraftItems.ARMOR_SETS) {
            for (ArmorItem.Type type : JolCraftEquipmentHelper.PLAYER_ARMOR_TYPES) {
                trimmable.add(set.get(type).get());
            }
        }

        target.tag(ItemTags.TRIM_MATERIALS).add(JolCraftVanillaTrimMaterials.ingredients().stream().map(Supplier::get).toArray(Item[]::new));

        target.tag(JolCraftTags.Items.ATTRIBUTE_TRIM_MATERIALS).add(JolCraftAttributeTrimMaterials.ingredients().stream().map(Supplier::get).toArray(Item[]::new));

        // Functional

        target.tag(ItemTags.DYEABLE)
                .add(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .add(JolCraftItems.DEEPSLATE_COMPASS.get());

        target.tag(ItemTags.BOOKSHELF_BOOKS)
                .addTags(JolCraftTags.Items.TOMES);

        // Plants

        target.tag(JolCraftTags.Items.HOPS_SEEDS)
                .add(JolCraftItems.ASGARNIAN_SEEDS.get())
                .add(JolCraftItems.DUSKHOLD_SEEDS.get())
                .add(JolCraftItems.KRANDONIAN_SEEDS.get())
                .add(JolCraftItems.YANILLIAN_SEEDS.get());

        target.tag(Tags.Items.SEEDS)
                .add(JolCraftItems.BARLEY_SEEDS.get())
                .addTags(JolCraftTags.Items.HOPS_SEEDS);

        target.tag(Tags.Items.CROPS)
                .add(JolCraftItems.BARLEY.get())
                .add(JolCraftItems.DEEPSLATE_BULBS.get())
                .addTag(JolCraftTags.Items.HOPS);

        target.tag(Tags.Items.MUSHROOMS)
                .add(JolCraftBlocks.FESTERLING.get().asItem())
                .add(JolCraftBlocks.DUSKCAP.get().asItem());

        // Brewing

        target.tag(JolCraftTags.Items.HOPS)
                .add(JolCraftItems.ASGARNIAN_HOPS.get())
                .add(JolCraftItems.DUSKHOLD_HOPS.get())
                .add(JolCraftItems.KRANDONIAN_HOPS.get())
                .add(JolCraftItems.YANILLIAN_HOPS.get());

        target.tag(JolCraftTags.Items.HOPS_BREW)
                .addTags(JolCraftTags.Items.HOPS)
                .add(JolCraftItems.BARLEY_MALT.get());

        // Materials

        target.tag(Tags.Items.ORES)
                .add(JolCraftBlocks.GEODE_BLOCK.get().asItem())
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().asItem());

        target.tag(Tags.Items.ORE_RATES_SINGULAR)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().asItem());

        target.tag(Tags.Items.ORE_RATES_DENSE)
                .add(JolCraftBlocks.GEODE_BLOCK.get().asItem());

        target.tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get().asItem());

        target.tag(Tags.Items.GEMS)
                .addTag(JolCraftTags.Items.GEMS_UNCUT)
                .addTag(JolCraftTags.Items.GEM_CUT);

        target.tag(Tags.Items.DUSTS)
                .addTag(JolCraftTags.Items.GEM_DUST);

        target.tag(JolCraftTags.Items.GEODES)
                .add(JolCraftItems.GEODE_SMALL.get())
                .add(JolCraftItems.GEODE_MEDIUM.get())
                .add(JolCraftItems.GEODE_LARGE.get());

        target.tag(Tags.Items.RAW_MATERIALS)
                .addTag(JolCraftTags.Items.GEMS_UNCUT)
                .add(JolCraftItems.IMPURE_MITHRIL.get())
                .add(JolCraftItems.PURE_MITHRIL.get())
                .add(JolCraftItems.DEEPSLATE_BULBS.get());

        target.tag(Tags.Items.INGOTS)
                .add(JolCraftItems.MITHRIL_INGOT.get());

        target.tag(Tags.Items.NUGGETS)
                .add(JolCraftItems.MITHRIL_NUGGET.get());

        target.tag(JolCraftTags.Items.GEMS_UNCUT)
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

        target.tag(JolCraftTags.Items.GEM_CUT)
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

        target.tag(JolCraftTags.Items.GEM_DUST)
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

        // Food

        target.tag(Tags.Items.DRINKS)
                .add(JolCraftItems.DWARVEN_BREW.get())
                .add(JolCraftItems.MUFFHORN_MILK_BUCKET.get());

        target.tag(Tags.Items.BUCKETS_MILK)
                .add(JolCraftItems.MUFFHORN_MILK_BUCKET.get());

        target.tag(Tags.Items.DRINKS_MILK)
                .add(JolCraftItems.MUFFHORN_MILK_BUCKET.get());

        target.tag(Tags.Items.DRINK_CONTAINING_BUCKET)
                .add(JolCraftItems.MUFFHORN_MILK_BUCKET.get());

        // Spawn eggs

        target.tag(JolCraftTags.Items.DWARF_SPAWN_EGGS)
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
                .add(JolCraftItems.DWARF_MINER_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_ARCANIST_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_PRIEST_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_BLACKSMITH_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_CHAMPION_SPAWN_EGG.get())
                .add(JolCraftItems.DWARF_SMELTER_SPAWN_EGG.get());

        target.tag(JolCraftTags.Items.CREATURE_SPAWN_EGGS)
                .add(JolCraftItems.MUFFHORN_SPAWN_EGG.get());

        target.tag(JolCraftTags.Items.SPAWN_EGGS)
                .addTags(JolCraftTags.Items.DWARF_SPAWN_EGGS)
                .addTags(JolCraftTags.Items.CREATURE_SPAWN_EGGS);

        // Salvage

        target.tag(JolCraftTags.Items.GENERAL_SALVAGE)
                .add(Items.TRIPWIRE_HOOK)
                .add(Items.FLINT_AND_STEEL)
                .add(Items.SHIELD)
                .add(Items.FILLED_MAP)
                .add(JolCraftItems.EXPIRED_POTION.get());

        target.tag(JolCraftTags.Items.DEEPSLATE_SALVAGE)
                .add(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get())
                .add(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get())
                .add(JolCraftItems.BROKEN_DEEPSLATE_GEAR.get())
                .add(JolCraftItems.DEEPSLATE_MUG.get())
                .add(JolCraftItems.BROKEN_TABLET.get())
                .add(JolCraftItems.INGOT_MOULD.get())
                .add(JolCraftItems.GUILD_SIGIL_MOULD.get());

        target.tag(JolCraftTags.Items.TEXTILE_SALVAGE)
                .add(Items.LEATHER_HELMET)
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.LEATHER_BOOTS)
                .add(Items.LEATHER_HORSE_ARMOR)
                .add(JolCraftItems.OLD_FABRIC.get());

        target.tag(JolCraftTags.Items.REDSTONE_SALVAGE)
                .add(Items.COMPASS)
                .add(Items.CLOCK)
                .add(Items.REPEATER)
                .add(Items.COMPARATOR);

        target.tag(JolCraftTags.Items.IRON_SALVAGE)
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
                .add(JolCraftItems.RUSTY_TONGS.get());

        target.tag(JolCraftTags.Items.GOLD_SALVAGE)
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

        target.tag(JolCraftTags.Items.MITHRIL_SALVAGE)
                .add(JolCraftItems.BROKEN_MITHRIL_PLATE.get())
                .add(JolCraftItems.BROKEN_MITHRIL_SWORD.get())
                .add(JolCraftItems.MITHRIL_SCRAP.get());

        target.tag(JolCraftTags.Items.GLOBAL_SALVAGE)
                .addTag(JolCraftTags.Items.GENERAL_SALVAGE)
                .addTag(JolCraftTags.Items.TEXTILE_SALVAGE)
                .addTag(JolCraftTags.Items.REDSTONE_SALVAGE)
                .addTag(JolCraftTags.Items.IRON_SALVAGE)
                .addTag(JolCraftTags.Items.GOLD_SALVAGE)
                .addTag(JolCraftTags.Items.MITHRIL_SALVAGE);

        // Dwarf

        target.tag(JolCraftTags.Items.PROFESSION_CONTRACTS)
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

        target.tag(JolCraftTags.Items.REPUTATION_TABLETS)
                .add(JolCraftItems.REPUTATION_TABLET_0.get())
                .add(JolCraftItems.REPUTATION_TABLET_1.get())
                .add(JolCraftItems.REPUTATION_TABLET_2.get())
                .add(JolCraftItems.REPUTATION_TABLET_3.get())
                .add(JolCraftItems.REPUTATION_TABLET_4.get());


        target.tag(JolCraftTags.Items.TOMES)
                .add(JolCraftItems.DWARVEN_TOME.get())
                .add(JolCraftItems.DWARVEN_TOME_COMMON.get())
                .add(JolCraftItems.DWARVEN_TOME_UNCOMMON.get())
                .add(JolCraftItems.DWARVEN_TOME_RARE.get())
                .add(JolCraftItems.DWARVEN_TOME_EPIC.get())
                .add(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get())
                .add(JolCraftItems.DWARVEN_LEXICON.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_TOME.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get())
                .add(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get())
                .add(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME.get())
                .add(JolCraftItems.ANCIENT_DWARVEN_LEXICON.get());

        // Coins

        target.tag(JolCraftTags.Items.COINS)
                .add(JolCraftItems.GOLD_COIN.get())
                .add(JolCraftItems.COIN_POUCH.get());

        // Tooltip

        target.tag(JolCraftTags.Items.MITHRIL_ITEMS)
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
                .add(JolCraftItems.MITHRIL_CHISEL.get())
                .add(JolCraftItems.MITHRIL_PESTLE.get());

        // Custom misc

        target.tag(JolCraftTags.Items.INK_AND_QUILLS)
                .add(JolCraftItems.QUILL_FULL.get())
                .add(JolCraftItems.QUILL_HALF.get())
                .add(JolCraftItems.QUILL_SMALL.get());
    }
}