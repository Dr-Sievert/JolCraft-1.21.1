package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.custom.paper.*;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftContractItems {

    private JolCraftContractItems() {}

    public static DeferredItem<Item> registerParchment() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.PARCHMENT);
    }

    public static DeferredItem<Item> registerBlank() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.CONTRACT_BLANK);
    }

    public static DeferredItem<Item> registerWritten() {
        return JolCraftItemRegistryHelper.registerItem(JolCraftItemIds.CONTRACT_WRITTEN, WrittenContractItem::new);
    }

    public static DeferredItem<Item> registerSigned() {
        return JolCraftItemRegistryHelper.registerItem(JolCraftItemIds.CONTRACT_SIGNED, SignedContractItem::new);
    }

    public static DeferredItem<Item> registerGuildSigil() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.GUILD_SIGIL);
    }

    public static DeferredItem<Item> registerGuildSigilMould() {
        return JolCraftItemRegistryHelper.registerItem(JolCraftItemIds.GUILD_SIGIL_MOULD, MouldItem::new, new Item.Properties()
                .rarity(Rarity.UNCOMMON)
                .stacksTo(1));
    }

    public static DeferredItem<Item> registerGuildmaster() { return registerProfession(JolCraftItemIds.CONTRACT_GUILDMASTER); }

    public static DeferredItem<Item> registerMerchant() { return registerProfession(JolCraftItemIds.CONTRACT_MERCHANT); }
    public static DeferredItem<Item> registerHistorian() { return registerProfession(JolCraftItemIds.CONTRACT_HISTORIAN); }
    public static DeferredItem<Item> registerScrapper() { return registerProfession(JolCraftItemIds.CONTRACT_SCRAPPER); }

    public static DeferredItem<Item> registerGuard() { return registerProfession(JolCraftItemIds.CONTRACT_GUARD); }
    public static DeferredItem<Item> registerBrewmaster() { return registerProfession(JolCraftItemIds.CONTRACT_BREWMASTER); }
    public static DeferredItem<Item> registerKeeper() { return registerProfession(JolCraftItemIds.CONTRACT_KEEPER); }

    public static DeferredItem<Item> registerMiner() { return registerProfession(JolCraftItemIds.CONTRACT_MINER); }
    public static DeferredItem<Item> registerExplorer() { return registerProfession(JolCraftItemIds.CONTRACT_EXPLORER); }
    public static DeferredItem<Item> registerAlchemist() { return registerProfession(JolCraftItemIds.CONTRACT_ALCHEMIST); }

    public static DeferredItem<Item> registerArcanist() { return registerProfession(JolCraftItemIds.CONTRACT_ARCANIST); }
    public static DeferredItem<Item> registerPriest() { return registerProfession(JolCraftItemIds.CONTRACT_PRIEST); }
    public static DeferredItem<Item> registerArtisan() { return registerProfession(JolCraftItemIds.CONTRACT_ARTISAN); }

    public static DeferredItem<Item> registerChampion() { return registerProfession(JolCraftItemIds.CONTRACT_CHAMPION); }
    public static DeferredItem<Item> registerBlacksmith() { return registerProfession(JolCraftItemIds.CONTRACT_BLACKSMITH); }
    public static DeferredItem<Item> registerSmelter() { return registerProfession(JolCraftItemIds.CONTRACT_SMELTER); }

    public static DeferredItem<Item> registerQuillEmpty() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.QUILL_EMPTY,
                props -> new QuillItem(props.stacksTo(16), JolCraftLanguageKeys.TOOLTIP_QUILL)
        );
    }

    public static DeferredItem<Item> registerQuillSmall(DeferredItem<Item> emptyQuill) {
        return registerQuill(JolCraftItemIds.QUILL_SMALL, emptyQuill);
    }

    public static DeferredItem<Item> registerQuillHalf(DeferredItem<Item> smallQuill) {
        return registerQuill(JolCraftItemIds.QUILL_HALF, smallQuill);
    }

    public static DeferredItem<Item> registerQuillFull(
            DeferredItem<Item> halfQuill
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.QUILL_FULL,
                properties -> new Item(
                        properties
                                .stacksTo(1)
                                .craftRemainder(halfQuill.get())
                )
        );
    }

    private static DeferredItem<Item> registerProfession(String id) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                ProfessionContractItem::new,
                new Item.Properties().rarity(Rarity.UNCOMMON)
        );
    }

    private static DeferredItem<Item> registerQuill(
            String id,
            DeferredItem<Item> craftRemainder
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new QuillItem(
                        props.craftRemainder(craftRemainder.get()).stacksTo(1),
                        JolCraftLanguageKeys.TOOLTIP_QUILL
                )
        );
    }
}