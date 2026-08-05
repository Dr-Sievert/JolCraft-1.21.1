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
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.data.id.item.JolCraftCreativeTabIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateType;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
import org.jetbrains.annotations.NotNull;

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
                    .displayItems((pParameters, output) -> {

                        if (dev) {
                            output.accept(JolCraftItems.DEV_KEY);
                        }

                        output.accept(JolCraftItems.DWARVEN_LEXICON);
                        output.accept(JolCraftItems.ANCIENT_DWARVEN_LEXICON);
                        output.accept(JolCraftItems.GOLD_COIN);
                        output.accept(JolCraftItems.COIN_POUCH);

                        output.accept(JolCraftItems.REPUTATION_TABLET_0);
                        output.accept(JolCraftItems.REPUTATION_TABLET_1);
                        output.accept(JolCraftItems.REPUTATION_TABLET_2);
                        output.accept(JolCraftItems.REPUTATION_TABLET_3);
                        output.accept(JolCraftItems.REPUTATION_TABLET_4);

                        output.accept(JolCraftItems.QUILL_EMPTY);
                        output.accept(JolCraftItems.QUILL_FULL);
                        output.accept(JolCraftItems.PARCHMENT);
                        output.accept(JolCraftItems.CONTRACT_BLANK);
                        output.accept(JolCraftItems.CONTRACT_WRITTEN);
                        output.accept(JolCraftItems.CONTRACT_SIGNED);
                        output.accept(JolCraftItems.GUILD_SIGIL_MOULD);
                        output.accept(JolCraftItems.GUILD_SIGIL);
                        output.accept(JolCraftItems.CONTRACT_GUILDMASTER);
                        output.accept(JolCraftItems.CONTRACT_MERCHANT);
                        output.accept(JolCraftItems.CONTRACT_HISTORIAN);
                        output.accept(JolCraftItems.CONTRACT_SCRAPPER);
                        output.accept(JolCraftItems.CONTRACT_GUARD);
                        output.accept(JolCraftItems.CONTRACT_EXPLORER);
                        output.accept(JolCraftItems.CONTRACT_KEEPER);
                        output.accept(JolCraftItems.CONTRACT_MINER);
                        output.accept(JolCraftItems.CONTRACT_BREWMASTER);
                        output.accept(JolCraftItems.CONTRACT_ARTISAN);
                        output.accept(JolCraftItems.CONTRACT_ALCHEMIST);
                        output.accept(JolCraftItems.CONTRACT_ARCANIST);
                        output.accept(JolCraftItems.CONTRACT_PRIEST);
                        output.accept(JolCraftItems.CONTRACT_CHAMPION);
                        output.accept(JolCraftItems.CONTRACT_BLACKSMITH);
                        output.accept(JolCraftItems.CONTRACT_SMELTER);

                        output.accept(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME);
                        output.accept(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME);
                        output.accept(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME);
                        output.accept(JolCraftItems.LEGENDARY_PAGE);

                        addLegendaryTome(output, DwarfLoreKey.ANCIENT_GEMCRAFT);
                        addLegendaryTome(output, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS);
                        addLegendaryTome(output, DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE);
                        addLegendaryTome(output, DwarfLoreKey.COIN_PRESS_MANUAL);
                        addLegendaryTome(output, DwarfLoreKey.ALCHEMY_RECIPES);

                        output.accept(JolCraftBlocks.HEARTH);
                        output.accept(JolCraftItems.STRONGBOX_ITEM);
                        output.accept(JolCraftItems.LOCKPICK);

                        output.accept(JolCraftItems.BOUNTY);
                        output.accept(JolCraftItems.BOUNTY_CRATE);
                        output.accept(JolCraftItems.RESTOCK_CRATE);
                        output.accept(JolCraftItems.REROLL_CRATE);

                        for (RewardCrateType crate : RewardCrateType.values()) {
                            addCrate(output, crate);
                        }

                        output.accept(JolCraftItems.MUFFHORN_FUR);
                        output.accept(JolCraftBlocks.MUFFHORN_FUR_BLOCK);
                        output.accept(JolCraftItems.MUFFHORN_MILK_BUCKET);

                        output.accept(JolCraftItems.DEEPSLATE_PLATE);
                        output.accept(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK);
                        output.accept(JolCraftItems.DEEPSLATE_ROD);
                        output.accept(JolCraftItems.DEEPSLATE_SWORD);
                        output.accept(JolCraftItems.DEEPSLATE_WARHAMMER);
                        output.accept(JolCraftItems.DEEPSLATE_PICKAXE);
                        output.accept(JolCraftItems.DEEPSLATE_SHOVEL);
                        output.accept(JolCraftItems.DEEPSLATE_AXE);
                        output.accept(JolCraftItems.DEEPSLATE_HOE);
                        output.accept(JolCraftItems.DEEPSLATE_HELMET);
                        output.accept(JolCraftItems.DEEPSLATE_CHESTPLATE);
                        output.accept(JolCraftItems.DEEPSLATE_LEGGINGS);
                        output.accept(JolCraftItems.DEEPSLATE_BOOTS);

                        output.accept(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE);
                        output.accept(JolCraftBlocks.PURE_MITHRIL_BLOCK);
                        output.accept(JolCraftBlocks.MITHRIL_BLOCK);
                        output.accept(JolCraftItems.IMPURE_MITHRIL);
                        output.accept(JolCraftItems.PURE_MITHRIL);
                        output.accept(JolCraftItems.MITHRIL_INGOT);
                        output.accept(JolCraftItems.MITHRIL_NUGGET);
                        output.accept(JolCraftItems.MITHRIL_CHAINWEAVE);
                        output.accept(JolCraftItems.MITHRIL_SWORD);
                        output.accept(JolCraftItems.MITHRIL_WARHAMMER);
                        output.accept(JolCraftItems.MITHRIL_PICKAXE);
                        output.accept(JolCraftItems.MITHRIL_SHOVEL);
                        output.accept(JolCraftItems.MITHRIL_AXE);
                        output.accept(JolCraftItems.MITHRIL_HOE);
                        output.accept(JolCraftItems.MITHRIL_HELMET);
                        output.accept(JolCraftItems.MITHRIL_CHESTPLATE);
                        output.accept(JolCraftItems.MITHRIL_LEGGINGS);
                        output.accept(JolCraftItems.MITHRIL_BOOTS);

                        output.accept(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE);

                        output.accept(JolCraftBlocks.GEODE_BLOCK);
                        output.accept(JolCraftItems.GEODE_SMALL);
                        output.accept(JolCraftItems.GEODE_MEDIUM);
                        output.accept(JolCraftItems.GEODE_LARGE);

                        output.accept(JolCraftItems.AEGISCORE);
                        output.accept(JolCraftItems.AEGISCORE_CUT);
                        output.accept(JolCraftItems.AEGISCORE_DUST);
                        output.accept(JolCraftItems.ASHFANG);
                        output.accept(JolCraftItems.ASHFANG_CUT);
                        output.accept(JolCraftItems.ASHFANG_DUST);
                        output.accept(JolCraftItems.DEEPMARROW);
                        output.accept(JolCraftItems.DEEPMARROW_CUT);
                        output.accept(JolCraftItems.DEEPMARROW_DUST);
                        output.accept(JolCraftItems.EARTHBLOOD);
                        output.accept(JolCraftItems.EARTHBLOOD_CUT);
                        output.accept(JolCraftItems.EARTHBLOOD_DUST);
                        output.accept(JolCraftItems.EMBERGLASS);
                        output.accept(JolCraftItems.EMBERGLASS_CUT);
                        output.accept(JolCraftItems.EMBERGLASS_DUST);
                        output.accept(JolCraftItems.FROSTVEIN);
                        output.accept(JolCraftItems.FROSTVEIN_CUT);
                        output.accept(JolCraftItems.FROSTVEIN_DUST);
                        output.accept(JolCraftItems.GRIMSTONE);
                        output.accept(JolCraftItems.GRIMSTONE_CUT);
                        output.accept(JolCraftItems.GRIMSTONE_DUST);
                        output.accept(JolCraftItems.IRONHEART);
                        output.accept(JolCraftItems.IRONHEART_CUT);
                        output.accept(JolCraftItems.IRONHEART_DUST);
                        output.accept(JolCraftItems.LUMIERE);
                        output.accept(JolCraftItems.LUMIERE_CUT);
                        output.accept(JolCraftItems.LUMIERE_DUST);
                        output.accept(JolCraftItems.MOONSHARD);
                        output.accept(JolCraftItems.MOONSHARD_CUT);
                        output.accept(JolCraftItems.MOONSHARD_DUST);
                        output.accept(JolCraftItems.RUSTAGATE);
                        output.accept(JolCraftItems.RUSTAGATE_CUT);
                        output.accept(JolCraftItems.RUSTAGATE_DUST);
                        output.accept(JolCraftItems.SKYBURROW);
                        output.accept(JolCraftItems.SKYBURROW_CUT);
                        output.accept(JolCraftItems.SKYBURROW_DUST);
                        output.accept(JolCraftItems.SUNGLEAM);
                        output.accept(JolCraftItems.SUNGLEAM_CUT);
                        output.accept(JolCraftItems.SUNGLEAM_DUST);
                        output.accept(JolCraftItems.VERDANITE);
                        output.accept(JolCraftItems.VERDANITE_CUT);
                        output.accept(JolCraftItems.VERDANITE_DUST);
                        output.accept(JolCraftItems.WOECRYSTAL);
                        output.accept(JolCraftItems.WOECRYSTAL_CUT);
                        output.accept(JolCraftItems.WOECRYSTAL_DUST);

                        output.accept(JolCraftBlocks.LAPIDARY_BENCH);
                        output.accept(JolCraftItems.WOODEN_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.STONE_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.IRON_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.GOLDEN_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.DIAMOND_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.NETHERITE_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.MITHRIL_ARTISAN_HAMMER);
                        output.accept(JolCraftItems.WOODEN_CHISEL);
                        output.accept(JolCraftItems.STONE_CHISEL);
                        output.accept(JolCraftItems.IRON_CHISEL);
                        output.accept(JolCraftItems.GOLDEN_CHISEL);
                        output.accept(JolCraftItems.DIAMOND_CHISEL);
                        output.accept(JolCraftItems.NETHERITE_CHISEL);
                        output.accept(JolCraftItems.DEEPSLATE_CHISEL);
                        output.accept(JolCraftItems.MITHRIL_CHISEL);

                        output.accept(JolCraftItems.BARLEY_SEEDS);
                        output.accept(JolCraftItems.BARLEY);
                        output.accept(JolCraftBlocks.BARLEY_BLOCK);
                        output.accept(JolCraftItems.ASGARNIAN_SEEDS);
                        output.accept(JolCraftItems.DUSKHOLD_SEEDS);
                        output.accept(JolCraftItems.KRANDONIAN_SEEDS);
                        output.accept(JolCraftItems.YANILLIAN_SEEDS);
                        output.accept(JolCraftItems.ASGARNIAN_HOPS);
                        output.accept(JolCraftItems.DUSKHOLD_HOPS);
                        output.accept(JolCraftItems.KRANDONIAN_HOPS);
                        output.accept(JolCraftItems.YANILLIAN_HOPS);
                        output.accept(JolCraftBlocks.DUSKCAP);
                        output.accept(JolCraftBlocks.DUSKCAP_BLOCK);
                        output.accept(JolCraftBlocks.DUSKCAP_STEM);
                        output.accept(JolCraftBlocks.FESTERLING);
                        output.accept(JolCraftBlocks.FESTERLING_BLOCK);
                        output.accept(JolCraftBlocks.FESTERLING_STEM);
                        output.accept(JolCraftItems.DEEPSLATE_BULBS);
                        output.accept(JolCraftBlocks.VERDANT_SOIL);
                        output.accept(JolCraftBlocks.VERDANT_FARMLAND);

                        output.accept(JolCraftItems.BARLEY_MALT);

                        for (float brewingSpeed : DwarvenBrewFluidHelper.BREWING_SPEED_TIERS) {
                            addYeastVariants(
                                    output,
                                    brewingSpeed
                            );
                        }

                        output.accept(
                                JolCraftBrewingItems.createTanninStack(
                                        JolCraftItems.TANNIN.get(),
                                        JolCraftFluids.TANNIN.get(),
                                        DwarvenBrewAge.MATURED
                                )
                        );

                        output.accept(
                                JolCraftBrewingItems.createTanninStack(
                                        JolCraftItems.TANNIN.get(),
                                        JolCraftFluids.REFINED_TANNIN.get(),
                                        DwarvenBrewAge.VINTAGE
                                )
                        );

                        output.accept(JolCraftItems.GLASS_MUG);

                        output.accept(
                                JolCraftBrewingItems.createDwarvenBrewStack(
                                       DwarvenBrewAge.FRESH
                                )
                        );

                        output.accept(JolCraftItems.INVERIX);

                        output.accept(JolCraftItems.DEEPSLATE_MORTAR_ITEM);
                        output.accept(JolCraftItems.WOODEN_PESTLE);
                        output.accept(JolCraftItems.STONE_PESTLE);
                        output.accept(JolCraftItems.IRON_PESTLE);
                        output.accept(JolCraftItems.GOLDEN_PESTLE);
                        output.accept(JolCraftItems.DIAMOND_PESTLE);
                        output.accept(JolCraftItems.NETHERITE_PESTLE);
                        output.accept(JolCraftItems.DEEPSLATE_PESTLE);
                        output.accept(JolCraftItems.MITHRIL_PESTLE);

                        output.accept(JolCraftItems.EMPTY_DEEPSLATE_COMPASS);

                        for (DeepslateCompassStructureGroup group : DeepslateCompassStructureGroup.values()) {
                            addCompassDialVariant(output, group);
                        }

                        output.accept(JolCraftItems.WOODEN_SPANNER);
                        output.accept(JolCraftItems.STONE_SPANNER);
                        output.accept(JolCraftItems.IRON_SPANNER);
                        output.accept(JolCraftItems.GOLDEN_SPANNER);
                        output.accept(JolCraftItems.DIAMOND_SPANNER);
                        output.accept(JolCraftItems.NETHERITE_SPANNER);
                        output.accept(JolCraftItems.DEEPSLATE_SPANNER);
                        output.accept(JolCraftItems.MITHRIL_SPANNER);
                        output.accept(JolCraftItems.SCRAP);
                        output.accept(JolCraftItems.SCRAP_HEAP);
                        output.accept(JolCraftItems.EXPIRED_POTION);
                        output.accept(JolCraftItems.OLD_FABRIC);
                        output.accept(JolCraftItems.BROKEN_PICKAXE);
                        output.accept(JolCraftItems.BROKEN_AMULET);
                        output.accept(JolCraftItems.RUSTY_TONGS);
                        output.accept(JolCraftItems.INGOT_MOULD);
                        output.accept(JolCraftItems.DEEPSLATE_MUG);
                        output.accept(JolCraftItems.BROKEN_TABLET);
                        output.accept(JolCraftItems.BROKEN_DEEPSLATE_PLATES);
                        output.accept(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD);
                        output.accept(JolCraftItems.BROKEN_DEEPSLATE_GEAR);
                        output.accept(JolCraftItems.BROKEN_BELT);
                        output.accept(JolCraftItems.BROKEN_COINS);
                        output.accept(JolCraftItems.MITHRIL_SCRAP);
                        output.accept(JolCraftItems.BROKEN_MITHRIL_PLATE);
                        output.accept(JolCraftItems.BROKEN_MITHRIL_SWORD);

                        if (!dev) {
                            output.accept(JolCraftItems.DEV_KEY);
                        }

                    }).build());

    private static void addLegendaryTome(
            CreativeModeTab.Output output,
            DwarfLoreKey loreKey
    ) {
        ItemStack stack = new ItemStack(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());

        LoreHelper.setLoreKey(stack, loreKey);

        output.accept(stack);
    }

    private static void addYeastVariants(
            CreativeModeTab.Output output,
            float brewingSpeed
    ) {
        output.accept(
                JolCraftBrewingItems.createYeastCultureStack(
                        JolCraftItems.YEAST_CULTURE.get(),
                        brewingSpeed
                )
        );

        output.accept(
                JolCraftBrewingItems.createYeastStack(
                        JolCraftItems.YEAST.get(),
                        brewingSpeed
                )
        );
    }

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

    private static void addCrate(
            @NotNull CreativeModeTab.Output output,
            @NotNull RewardCrateType crate
    ) {
        output.accept(crate.createStack());
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
