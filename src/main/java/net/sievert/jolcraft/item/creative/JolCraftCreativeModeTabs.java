package net.sievert.jolcraft.item.creative;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.item.client.compass.DialItemColor;
import net.sievert.jolcraft.item.util.StructureGroupColorHelper;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.data.util.attachment.TomeUnlockHelper;

import java.util.function.Supplier;

public class JolCraftCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JolCraft.MOD_ID);

    public static final Supplier<CreativeModeTab> JOLCRAFT_ITEMS =
            CREATIVE_MODE_TABS.register("jolcraft_items_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.jolcraft.jolcraft_items_tab"))
                    .icon(() -> new ItemStack(JolCraftItems.GOLD_COIN.get()))
                    .displayItems((pParameters, pOutput) -> {

                        //Testing
                        pOutput.accept(JolCraftItems.DEV_KEY);

                        //pOutput.accept(PotionContents.createItemStack(Items.POTION, JolCraftPotions.CURSE));

                        //Real
                        pOutput.accept(JolCraftItems.GOLD_COIN);
                        pOutput.accept(JolCraftItems.COIN_POUCH);
                        pOutput.accept(JolCraftItems.DWARVEN_LEXICON);
                        pOutput.accept(JolCraftItems.ANCIENT_DWARVEN_LEXICON);
                        pOutput.accept(JolCraftItems.REPUTATION_TABLET_0);
                        pOutput.accept(JolCraftItems.REPUTATION_TABLET_1);
                        pOutput.accept(JolCraftItems.REPUTATION_TABLET_2);
                        pOutput.accept(JolCraftItems.REPUTATION_TABLET_3);
                        pOutput.accept(JolCraftItems.REPUTATION_TABLET_4);

                        pOutput.accept(JolCraftItems.EMPTY_DEEPSLATE_COMPASS);
                        addCompassDialVariant(pOutput, "dwarven_structures");
                        addCompassDialVariant(pOutput, "ancient_structures");

                        pOutput.accept(JolCraftBlocks.HEARTH);
                        pOutput.accept(JolCraftItems.STRONGBOX_ITEM);
                        pOutput.accept(JolCraftItems.LOCKPICK);

                        pOutput.accept(JolCraftBlocks.VERDANT_SOIL);
                        pOutput.accept(JolCraftBlocks.VERDANT_FARMLAND);
                        pOutput.accept(JolCraftBlocks.DUSKCAP);
                        pOutput.accept(JolCraftBlocks.FESTERLING);
                        pOutput.accept(JolCraftItems.BARLEY_SEEDS);
                        pOutput.accept(JolCraftItems.BARLEY_SEEDS);
                        pOutput.accept(JolCraftItems.BARLEY);
                        pOutput.accept(JolCraftBlocks.BARLEY_BLOCK);
                        pOutput.accept(JolCraftItems.BARLEY_MALT);
                        pOutput.accept(JolCraftItems.ASGARNIAN_SEEDS);
                        pOutput.accept(JolCraftItems.DUSKHOLD_SEEDS);
                        pOutput.accept(JolCraftItems.KRANDONIAN_SEEDS);
                        pOutput.accept(JolCraftItems.YANILLIAN_SEEDS);
                        pOutput.accept(JolCraftItems.ASGARNIAN_HOPS);
                        pOutput.accept(JolCraftItems.DUSKHOLD_HOPS);
                        pOutput.accept(JolCraftItems.KRANDONIAN_HOPS);
                        pOutput.accept(JolCraftItems.YANILLIAN_HOPS);
                        pOutput.accept(JolCraftItems.YEAST);
                        pOutput.accept(JolCraftItems.GLASS_MUG);
                        pOutput.accept(JolCraftItems.DWARVEN_BREW);

                        pOutput.accept(JolCraftItems.MUFFHORN_MILK_BUCKET);
                        pOutput.accept(JolCraftItems.MUFFHORN_FUR);
                        pOutput.accept(JolCraftBlocks.MUFFHORN_FUR_BLOCK);

                        pOutput.accept(JolCraftItems.DEEPSLATE_BULBS);
                        pOutput.accept(JolCraftItems.DEEPSLATE_PLATE);
                        pOutput.accept(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK);
                        pOutput.accept(JolCraftItems.DEEPSLATE_ROD);
                        pOutput.accept(JolCraftItems.DEEPSLATE_SWORD);
                        pOutput.accept(JolCraftItems.DEEPSLATE_WARHAMMER);
                        pOutput.accept(JolCraftItems.DEEPSLATE_PICKAXE);
                        pOutput.accept(JolCraftItems.DEEPSLATE_SHOVEL);
                        pOutput.accept(JolCraftItems.DEEPSLATE_AXE);
                        pOutput.accept(JolCraftItems.DEEPSLATE_HOE);
                        pOutput.accept(JolCraftItems.DEEPSLATE_HELMET);
                        pOutput.accept(JolCraftItems.DEEPSLATE_CHESTPLATE);
                        pOutput.accept(JolCraftItems.DEEPSLATE_LEGGINGS);
                        pOutput.accept(JolCraftItems.DEEPSLATE_BOOTS);

                        pOutput.accept(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE);
                        pOutput.accept(JolCraftBlocks.PURE_MITHRIL_BLOCK);
                        pOutput.accept(JolCraftBlocks.MITHRIL_BLOCK);
                        pOutput.accept(JolCraftItems.IMPURE_MITHRIL);
                        pOutput.accept(JolCraftItems.PURE_MITHRIL);
                        pOutput.accept(JolCraftItems.MITHRIL_INGOT);
                        pOutput.accept(JolCraftItems.MITHRIL_NUGGET);
                        pOutput.accept(JolCraftItems.MITHRIL_CHAINWEAVE);
                        pOutput.accept(JolCraftItems.MITHRIL_SWORD);
                        pOutput.accept(JolCraftItems.MITHRIL_WARHAMMER);
                        pOutput.accept(JolCraftItems.MITHRIL_PICKAXE);
                        pOutput.accept(JolCraftItems.MITHRIL_SHOVEL);
                        pOutput.accept(JolCraftItems.MITHRIL_AXE);
                        pOutput.accept(JolCraftItems.MITHRIL_HOE);
                        pOutput.accept(JolCraftItems.MITHRIL_HELMET);
                        pOutput.accept(JolCraftItems.MITHRIL_CHESTPLATE);
                        pOutput.accept(JolCraftItems.MITHRIL_LEGGINGS);
                        pOutput.accept(JolCraftItems.MITHRIL_BOOTS);

                        pOutput.accept(JolCraftBlocks.LAPIDARY_BENCH);
                        pOutput.accept(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.MITHRIL_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.DEEPSLATE_CHISEL);
                        pOutput.accept(JolCraftItems.MITHRIL_CHISEL);

                        pOutput.accept(JolCraftBlocks.GEODE_BLOCK);
                        pOutput.accept(JolCraftItems.GEODE_SMALL);
                        pOutput.accept(JolCraftItems.GEODE_MEDIUM);
                        pOutput.accept(JolCraftItems.GEODE_LARGE);

                        pOutput.accept(JolCraftItems.INVERIX);

                        pOutput.accept(JolCraftItems.AEGISCORE);
                        pOutput.accept(JolCraftItems.AEGISCORE_CUT);
                        pOutput.accept(JolCraftItems.AEGISCORE_DUST);
                        pOutput.accept(JolCraftItems.ASHFANG);
                        pOutput.accept(JolCraftItems.ASHFANG_CUT);
                        pOutput.accept(JolCraftItems.ASHFANG_DUST);
                        pOutput.accept(JolCraftItems.DEEPMARROW);
                        pOutput.accept(JolCraftItems.DEEPMARROW_CUT);
                        pOutput.accept(JolCraftItems.DEEPMARROW_DUST);
                        pOutput.accept(JolCraftItems.EARTHBLOOD);
                        pOutput.accept(JolCraftItems.EARTHBLOOD_CUT);
                        pOutput.accept(JolCraftItems.EARTHBLOOD_DUST);
                        pOutput.accept(JolCraftItems.EMBERGLASS);
                        pOutput.accept(JolCraftItems.EMBERGLASS_CUT);
                        pOutput.accept(JolCraftItems.EMBERGLASS_DUST);
                        pOutput.accept(JolCraftItems.FROSTVEIN);
                        pOutput.accept(JolCraftItems.FROSTVEIN_CUT);
                        pOutput.accept(JolCraftItems.FROSTVEIN_DUST);
                        pOutput.accept(JolCraftItems.GRIMSTONE);
                        pOutput.accept(JolCraftItems.GRIMSTONE_CUT);
                        pOutput.accept(JolCraftItems.GRIMSTONE_DUST);
                        pOutput.accept(JolCraftItems.IRONHEART);
                        pOutput.accept(JolCraftItems.IRONHEART_CUT);
                        pOutput.accept(JolCraftItems.IRONHEART_DUST);
                        pOutput.accept(JolCraftItems.LUMIERE);
                        pOutput.accept(JolCraftItems.LUMIERE_CUT);
                        pOutput.accept(JolCraftItems.LUMIERE_DUST);
                        pOutput.accept(JolCraftItems.MOONSHARD);
                        pOutput.accept(JolCraftItems.MOONSHARD_CUT);
                        pOutput.accept(JolCraftItems.MOONSHARD_DUST);
                        pOutput.accept(JolCraftItems.RUSTAGATE);
                        pOutput.accept(JolCraftItems.RUSTAGATE_CUT);
                        pOutput.accept(JolCraftItems.RUSTAGATE_DUST);
                        pOutput.accept(JolCraftItems.SKYBURROW);
                        pOutput.accept(JolCraftItems.SKYBURROW_CUT);
                        pOutput.accept(JolCraftItems.SKYBURROW_DUST);
                        pOutput.accept(JolCraftItems.SUNGLEAM);
                        pOutput.accept(JolCraftItems.SUNGLEAM_CUT);
                        pOutput.accept(JolCraftItems.SUNGLEAM_DUST);
                        pOutput.accept(JolCraftItems.VERDANITE);
                        pOutput.accept(JolCraftItems.VERDANITE_CUT);
                        pOutput.accept(JolCraftItems.VERDANITE_DUST);
                        pOutput.accept(JolCraftItems.WOECRYSTAL);
                        pOutput.accept(JolCraftItems.WOECRYSTAL_CUT);
                        pOutput.accept(JolCraftItems.WOECRYSTAL_DUST);

                        pOutput.accept(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE);

                        pOutput.accept(JolCraftItems.QUILL_EMPTY);
                        pOutput.accept(JolCraftItems.QUILL_FULL);
                        pOutput.accept(JolCraftItems.PARCHMENT);
                        pOutput.accept(JolCraftItems.CONTRACT_BLANK);
                        pOutput.accept(JolCraftItems.CONTRACT_WRITTEN);
                        pOutput.accept(JolCraftItems.CONTRACT_SIGNED);
                        pOutput.accept(JolCraftItems.GUILD_SIGIL);
                        pOutput.accept(JolCraftItems.CONTRACT_GUILDMASTER);
                        pOutput.accept(JolCraftItems.CONTRACT_MERCHANT);
                        pOutput.accept(JolCraftItems.CONTRACT_HISTORIAN);
                        pOutput.accept(JolCraftItems.CONTRACT_SCRAPPER);
                        pOutput.accept(JolCraftItems.CONTRACT_GUARD);
                        pOutput.accept(JolCraftItems.CONTRACT_EXPLORER);
                        pOutput.accept(JolCraftItems.CONTRACT_KEEPER);
                        pOutput.accept(JolCraftItems.CONTRACT_MINER);
                        pOutput.accept(JolCraftItems.CONTRACT_BREWMASTER);
                        pOutput.accept(JolCraftItems.CONTRACT_ARTISAN);
                        pOutput.accept(JolCraftItems.CONTRACT_ALCHEMIST);
                        pOutput.accept(JolCraftItems.CONTRACT_ARCANIST);
                        pOutput.accept(JolCraftItems.CONTRACT_PRIEST);
                        pOutput.accept(JolCraftItems.CONTRACT_CHAMPION);
                        pOutput.accept(JolCraftItems.CONTRACT_BLACKSMITH);
                        pOutput.accept(JolCraftItems.CONTRACT_SMELTER);

                        pOutput.accept(JolCraftItems.BOUNTY);
                        pOutput.accept(JolCraftItems.BOUNTY_CRATE);
                        pOutput.accept(JolCraftItems.RESTOCK_CRATE);
                        pOutput.accept(JolCraftItems.REROLL_CRATE);

                        pOutput.accept(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME);
                        pOutput.accept(JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME);
                        pOutput.accept(JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME);
                        pOutput.accept(JolCraftItems.LEGENDARY_PAGE);

                        ItemStack gemTome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
                        gemTome.set(JolCraftDataComponents.LORE_LINE_ID.get(), TomeUnlockHelper.CUTTING_GEMS);
                        pOutput.accept(gemTome);

                        ItemStack brewTome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
                        brewTome.set(JolCraftDataComponents.LORE_LINE_ID.get(), TomeUnlockHelper.BREW_MULTIPLE_HOPS);
                        pOutput.accept(brewTome);

                        pOutput.accept(JolCraftItems.COPPER_SPANNER);
                        pOutput.accept(JolCraftItems.IRON_SPANNER);
                        pOutput.accept(JolCraftItems.SCRAP);
                        pOutput.accept(JolCraftItems.SCRAP_HEAP);
                        pOutput.accept(JolCraftItems.EXPIRED_POTION);
                        pOutput.accept(JolCraftItems.OLD_FABRIC);
                        pOutput.accept(JolCraftItems.BROKEN_PICKAXE);
                        pOutput.accept(JolCraftItems.BROKEN_AMULET);
                        pOutput.accept(JolCraftItems.RUSTY_TONGS);
                        pOutput.accept(JolCraftItems.INGOT_MOULD);
                        pOutput.accept(JolCraftItems.DEEPSLATE_MUG);
                        pOutput.accept(JolCraftItems.BROKEN_TABLET);
                        pOutput.accept(JolCraftItems.BROKEN_DEEPSLATE_PLATES);
                        pOutput.accept(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD);
                        pOutput.accept(JolCraftItems.BROKEN_DEEPSLATE_GEAR);
                        pOutput.accept(JolCraftItems.BROKEN_BELT);
                        pOutput.accept(JolCraftItems.BROKEN_COINS);
                        pOutput.accept(JolCraftItems.MITHRIL_SALVAGE);
                        pOutput.accept(JolCraftItems.BROKEN_MITHRIL_PLATE);
                        pOutput.accept(JolCraftItems.BROKEN_MITHRIL_SWORD);

                    }).build());

    public static final Supplier<CreativeModeTab> JOLCRAFT_EGGS =
            CREATIVE_MODE_TABS.register("jolcraft_egg_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.jolcraft.jolcraft_egg_tab"))
                    .icon(() -> new ItemStack(JolCraftItems.DWARF_SPAWN_EGG.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "jolcraft_items_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(JolCraftItems.DWARF_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_MERCHANT_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_GUARD_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_KEEPER_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_ARTISAN_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_EXPLORER_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_MINER_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_ARCANIST_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.DWARF_PRIEST_SPAWN_EGG);
                        pOutput.accept(JolCraftItems.MUFFHORN_SPAWN_EGG);

                    }).build());

    private static void addCompassDialVariant(CreativeModeTab.Output output, String group) {
        ItemStack stack = new ItemStack(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get());
        stack.set(JolCraftDataComponents.STRUCTURE_GROUP, group);
        stack.set(JolCraftDataComponents.DIAL_COLOR, new DialItemColor(StructureGroupColorHelper.getColor(group)));
        output.accept(stack);
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}


