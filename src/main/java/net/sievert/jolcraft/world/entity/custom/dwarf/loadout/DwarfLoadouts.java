package net.sievert.jolcraft.world.entity.custom.dwarf.loadout;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfLoadouts {

    @FunctionalInterface
    public interface Provider {
        void apply(AbstractDwarfEntity dwarf);
    }

    private static final Map<DwarfProfession, Provider> PROVIDERS =
            new EnumMap<>(DwarfProfession.class);

    private static boolean bootstrapped = false;

    private DwarfLoadouts() {}

    public static void bootstrap() {
        if (bootstrapped) return;
        bootstrapped = true;

        register(DwarfProfession.NONE, DwarfLoadouts::applyNone);

        register(DwarfProfession.ALCHEMIST, DwarfLoadouts::applyAlchemist);
        register(DwarfProfession.ARCANIST, DwarfLoadouts::applyArcanist);
        register(DwarfProfession.ARTISAN, DwarfLoadouts::applyArtisan);
        register(DwarfProfession.BREWMASTER, DwarfLoadouts::applyBrewmaster);
        register(DwarfProfession.EXPLORER, DwarfLoadouts::applyExplorer);
        register(DwarfProfession.GUARD, DwarfLoadouts::applyGuard);
        register(DwarfProfession.GUILDMASTER, DwarfLoadouts::applyGuildmaster);
        register(DwarfProfession.HISTORIAN, DwarfLoadouts::applyHistorian);
        register(DwarfProfession.KEEPER, DwarfLoadouts::applyKeeper);
        register(DwarfProfession.MERCHANT, DwarfLoadouts::applyMerchant);
        register(DwarfProfession.MINER, DwarfLoadouts::applyMiner);
        register(DwarfProfession.PRIEST, DwarfLoadouts::applyPriest);
        register(DwarfProfession.SCRAPPER, DwarfLoadouts::applyScrapper);
        register(DwarfProfession.BLACKSMITH, DwarfLoadouts::applyBlacksmith);
        register(DwarfProfession.CHAMPION, DwarfLoadouts::applyChampion);
        register(DwarfProfession.SMELTER, DwarfLoadouts::applySmelter);
    }

    public static void register(DwarfProfession profession, Provider provider) {
        Objects.requireNonNull(profession, JolCraftDictionary.PROFESSION);
        Objects.requireNonNull(provider, JolCraftDictionary.PROVIDER);
        PROVIDERS.put(profession, provider);
    }

    public static void applyLoadout(AbstractDwarfEntity dwarf) {

        Provider provider = PROVIDERS.get(dwarf.getProfession());
        if (provider == null) {
            throw new IllegalStateException(
                    "No loadout provider registered for profession: " + dwarf.getProfession()
            );
        }

        provider.apply(dwarf);
    }

    private static void applyNone(AbstractDwarfEntity dwarf) {
    }

    private static void applyAlchemist(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.DEEPSLATE_PESTLE.get()));
    }

    private static void applyArcanist(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.WOECRYSTAL.get()));
    }

    private static void applyArtisan(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_CHISEL.get()));
    }

    private static void applyBrewmaster(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.GLASS_MUG.get()));
    }

    private static void applyExplorer(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()));
        dwarf.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
        dwarf.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
    }

    private static void applyGuard(AbstractDwarfEntity dwarf) {
        ItemStack weapon = dwarf.getRandom().nextBoolean()
                ? new ItemStack(JolCraftItems.DEEPSLATE_AXE.get())
                : new ItemStack(JolCraftItems.DEEPSLATE_WARHAMMER.get());

        dwarf.setItemSlot(EquipmentSlot.MAINHAND, weapon);
    }

    private static void applyGuildmaster(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.CONTRACT_SIGNED.get()));
    }

    private static void applyHistorian(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.DWARVEN_TOME.get()));
    }

    private static void applyKeeper(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.BARLEY.get()));
    }

    private static void applyMerchant(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.COIN_POUCH.get()));
    }

    private static void applyMiner(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_PICKAXE.get()));
    }

    private static void applyPriest(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.SUNGLEAM.get()));
    }

    private static void applyScrapper(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_SPANNER.get()));
    }

    private static void applyBlacksmith(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get()));
    }

    private static void applyChampion(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.MITHRIL_WARHAMMER.get()));
        dwarf.setItemSlot(EquipmentSlot.HEAD, new ItemStack(JolCraftItems.MITHRIL_HELMET.get()));
        dwarf.setItemSlot(EquipmentSlot.CHEST, new ItemStack(JolCraftItems.MITHRIL_CHESTPLATE.get()));
        dwarf.setItemSlot(EquipmentSlot.LEGS, new ItemStack(JolCraftItems.MITHRIL_LEGGINGS.get()));
        dwarf.setItemSlot(EquipmentSlot.FEET, new ItemStack(JolCraftItems.MITHRIL_BOOTS.get()));
    }

    private static void applySmelter(AbstractDwarfEntity dwarf) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.IMPURE_MITHRIL.get()));
    }
}