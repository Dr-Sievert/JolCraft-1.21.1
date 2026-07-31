package net.sievert.jolcraft.world.item.creative;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.data.id.item.JolCraftCreativeTabIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class JolCraftCreativeModeTabs {

    private JolCraftCreativeModeTabs(){}

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JolCraft.MOD_ID);

    private static final boolean dev = !FMLEnvironment.production;

    public static final Supplier<CreativeModeTab> JOLCRAFT_ITEMS =
            CREATIVE_MODE_TABS.register(JolCraftCreativeTabIds.JOLCRAFT_GENERAL_CREATIVE_TAB, () -> CreativeModeTab.builder()
                    .title(Component.translatable(JolCraftLanguageKeys.JOLCRAFT_GENERAL_CREATIVE_TAB))
                    .icon(() -> new ItemStack(JolCraftItems.GOLD_COIN.get()))
                    .displayItems((pParameters, pOutput) -> {

                        //Testing
                        if (dev) {
                            pOutput.accept(JolCraftItems.DEV_KEY);
                        }

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

                        for (DeepslateCompassStructureGroup group : DeepslateCompassStructureGroup.values()) {
                            addCompassDialVariant(pOutput, group);
                        }

                        pOutput.accept(JolCraftBlocks.HEARTH);
                        pOutput.accept(JolCraftItems.STRONGBOX_ITEM);
                        pOutput.accept(JolCraftItems.LOCKPICK);

                        pOutput.accept(JolCraftBlocks.VERDANT_SOIL);
                        pOutput.accept(JolCraftBlocks.VERDANT_FARMLAND);
                        pOutput.accept(JolCraftBlocks.DUSKCAP);
                        pOutput.accept(JolCraftBlocks.DUSKCAP_BLOCK);
                        pOutput.accept(JolCraftBlocks.DUSKCAP_STEM);
                        pOutput.accept(JolCraftBlocks.FESTERLING);
                        pOutput.accept(JolCraftBlocks.FESTERLING_BLOCK);
                        pOutput.accept(JolCraftBlocks.FESTERLING_STEM);
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
                        pOutput.accept(JolCraftItems.WOODEN_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.STONE_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.IRON_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.GOLDEN_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.DIAMOND_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.NETHERITE_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.MITHRIL_ARTISAN_HAMMER);
                        pOutput.accept(JolCraftItems.WOODEN_CHISEL);
                        pOutput.accept(JolCraftItems.STONE_CHISEL);
                        pOutput.accept(JolCraftItems.IRON_CHISEL);
                        pOutput.accept(JolCraftItems.GOLDEN_CHISEL);
                        pOutput.accept(JolCraftItems.DIAMOND_CHISEL);
                        pOutput.accept(JolCraftItems.NETHERITE_CHISEL);
                        pOutput.accept(JolCraftItems.DEEPSLATE_CHISEL);
                        pOutput.accept(JolCraftItems.MITHRIL_CHISEL);

                        pOutput.accept(JolCraftBlocks.GEODE_BLOCK);
                        pOutput.accept(JolCraftItems.GEODE_SMALL);
                        pOutput.accept(JolCraftItems.GEODE_MEDIUM);
                        pOutput.accept(JolCraftItems.GEODE_LARGE);

                        pOutput.accept(JolCraftItems.DEEPSLATE_MORTAR_ITEM);
                        pOutput.accept(JolCraftItems.WOODEN_PESTLE);
                        pOutput.accept(JolCraftItems.STONE_PESTLE);
                        pOutput.accept(JolCraftItems.IRON_PESTLE);
                        pOutput.accept(JolCraftItems.GOLDEN_PESTLE);
                        pOutput.accept(JolCraftItems.DIAMOND_PESTLE);
                        pOutput.accept(JolCraftItems.NETHERITE_PESTLE);
                        pOutput.accept(JolCraftItems.DEEPSLATE_PESTLE);
                        pOutput.accept(JolCraftItems.MITHRIL_PESTLE);

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
                        pOutput.accept(JolCraftItems.GUILD_SIGIL_MOULD);
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
                        pOutput.accept(JolCraftItems.REWARD_CRATE);
                        pOutput.accept(JolCraftItems.RESTOCK_CRATE);
                        pOutput.accept(JolCraftItems.REROLL_CRATE);

                        pOutput.accept(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME);
                        pOutput.accept(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME);
                        pOutput.accept(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME);
                        pOutput.accept(JolCraftItems.LEGENDARY_PAGE);

                        ItemStack gemTome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
                        LoreHelper.setLoreKey(gemTome, DwarfLoreKey.ANCIENT_GEMCRAFT);
                        pOutput.accept(gemTome);

                        ItemStack brewTome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
                        LoreHelper.setLoreKey(brewTome, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS);
                        pOutput.accept(brewTome);

                        ItemStack forgeTome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
                        LoreHelper.setLoreKey(forgeTome, DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE);
                        pOutput.accept(forgeTome);

                        ItemStack coinTome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
                        LoreHelper.setLoreKey(coinTome, DwarfLoreKey.COIN_PRESS_MANUAL);
                        pOutput.accept(coinTome);

                        ItemStack alchemyTome = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());
                        LoreHelper.setLoreKey(alchemyTome, DwarfLoreKey.ALCHEMY_RECIPES);
                        pOutput.accept(alchemyTome);

                        pOutput.accept(JolCraftItems.WOODEN_SPANNER);
                        pOutput.accept(JolCraftItems.STONE_SPANNER);
                        pOutput.accept(JolCraftItems.IRON_SPANNER);
                        pOutput.accept(JolCraftItems.GOLDEN_SPANNER);
                        pOutput.accept(JolCraftItems.DIAMOND_SPANNER);
                        pOutput.accept(JolCraftItems.NETHERITE_SPANNER);
                        pOutput.accept(JolCraftItems.DEEPSLATE_SPANNER);
                        pOutput.accept(JolCraftItems.MITHRIL_SPANNER);
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
                        pOutput.accept(JolCraftItems.MITHRIL_SCRAP);
                        pOutput.accept(JolCraftItems.BROKEN_MITHRIL_PLATE);
                        pOutput.accept(JolCraftItems.BROKEN_MITHRIL_SWORD);

                        if (!dev) {
                            pOutput.accept(JolCraftItems.DEV_KEY);
                        }

                    }).build());

    private static void addCompassDialVariant(
            CreativeModeTab.Output output,
            DeepslateCompassStructureGroup group
    ) {
        ItemStack stack = new ItemStack(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get());

        stack.set(JolCraftDataComponents.STRUCTURE_GROUP, group.getId());

        stack.set(
                JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get(),
                new DeepslateCompassDialColor(group.color())
        );

        output.accept(stack);
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}