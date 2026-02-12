package net.sievert.jolcraft.world.entity.custom.dwarf.util.loadout;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfLoadouts {

    @FunctionalInterface
    public interface Provider {
        void apply(AbstractDwarfEntity dwarf,
                   ServerLevelAccessor level,
                   DifficultyInstance difficulty,
                   EntitySpawnReason spawnReason,
                   @Nullable SpawnGroupData spawnGroupData);
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
    }

    public static void register(DwarfProfession profession, Provider provider) {
        Objects.requireNonNull(profession, JolCraftDictionary.PROFESSION);
        Objects.requireNonNull(provider, JolCraftDictionary.PROVIDER);
        PROVIDERS.put(profession, provider);
    }

    public static void applySpawnLoadout(AbstractDwarfEntity dwarf,
                                         ServerLevelAccessor level,
                                         DifficultyInstance difficulty,
                                         EntitySpawnReason spawnReason,
                                         @Nullable SpawnGroupData spawnGroupData) {

        Provider provider = PROVIDERS.get(dwarf.getProfession());
        if (provider == null) {
            throw new IllegalStateException(
                    "No loadout provider registered for profession: " + dwarf.getProfession()
            );
        }

        provider.apply(dwarf, level, difficulty, spawnReason, spawnGroupData);
    }

    // -------------------------------------------------------------------------
    // Profession implementations (hands + armor only)
    // -------------------------------------------------------------------------

    private static void applyNone(AbstractDwarfEntity dwarf,
                                  ServerLevelAccessor level,
                                  DifficultyInstance difficulty,
                                  EntitySpawnReason spawnReason,
                                  @Nullable SpawnGroupData spawnGroupData) {
    }

    private static void applyAlchemist(AbstractDwarfEntity dwarf,
                                       ServerLevelAccessor level,
                                       DifficultyInstance difficulty,
                                       EntitySpawnReason spawnReason,
                                       @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.GLASS_BOTTLE));
    }

    private static void applyArcanist(AbstractDwarfEntity dwarf,
                                      ServerLevelAccessor level,
                                      DifficultyInstance difficulty,
                                      EntitySpawnReason spawnReason,
                                      @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.WOECRYSTAL.get()));
    }

    private static void applyArtisan(AbstractDwarfEntity dwarf,
                                     ServerLevelAccessor level,
                                     DifficultyInstance difficulty,
                                     EntitySpawnReason spawnReason,
                                     @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.DEEPSLATE_CHISEL.get()));
    }

    private static void applyBrewmaster(AbstractDwarfEntity dwarf,
                                        ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        EntitySpawnReason spawnReason,
                                        @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.GLASS_MUG.get()));
    }

    private static void applyExplorer(AbstractDwarfEntity dwarf,
                                      ServerLevelAccessor level,
                                      DifficultyInstance difficulty,
                                      EntitySpawnReason spawnReason,
                                      @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()));
        dwarf.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
        dwarf.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
    }

    private static void applyGuard(AbstractDwarfEntity dwarf,
                                   ServerLevelAccessor level,
                                   DifficultyInstance difficulty,
                                   EntitySpawnReason spawnReason,
                                   @Nullable SpawnGroupData spawnGroupData) {
        ItemStack weapon = dwarf.getRandom().nextBoolean()
                ? new ItemStack(JolCraftItems.DEEPSLATE_AXE.get())
                : new ItemStack(JolCraftItems.DEEPSLATE_WARHAMMER.get());

        dwarf.setItemSlot(EquipmentSlot.MAINHAND, weapon);
    }

    private static void applyGuildmaster(AbstractDwarfEntity dwarf,
                                         ServerLevelAccessor level,
                                         DifficultyInstance difficulty,
                                         EntitySpawnReason spawnReason,
                                         @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.CONTRACT_SIGNED.get()));
    }

    private static void applyHistorian(AbstractDwarfEntity dwarf,
                                       ServerLevelAccessor level,
                                       DifficultyInstance difficulty,
                                       EntitySpawnReason spawnReason,
                                       @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.DWARVEN_TOME.get()));
    }

    private static void applyKeeper(AbstractDwarfEntity dwarf,
                                    ServerLevelAccessor level,
                                    DifficultyInstance difficulty,
                                    EntitySpawnReason spawnReason,
                                    @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.BARLEY.get()));
    }

    private static void applyMerchant(AbstractDwarfEntity dwarf,
                                      ServerLevelAccessor level,
                                      DifficultyInstance difficulty,
                                      EntitySpawnReason spawnReason,
                                      @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.COIN_POUCH.get()));
    }

    private static void applyMiner(AbstractDwarfEntity dwarf,
                                   ServerLevelAccessor level,
                                   DifficultyInstance difficulty,
                                   EntitySpawnReason spawnReason,
                                   @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(JolCraftItems.DEEPSLATE_PICKAXE.get()));
    }

    private static void applyPriest(AbstractDwarfEntity dwarf,
                                    ServerLevelAccessor level,
                                    DifficultyInstance difficulty,
                                    EntitySpawnReason spawnReason,
                                    @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.SUNGLEAM.get()));
    }

    private static void applyScrapper(AbstractDwarfEntity dwarf,
                                      ServerLevelAccessor level,
                                      DifficultyInstance difficulty,
                                      EntitySpawnReason spawnReason,
                                      @Nullable SpawnGroupData spawnGroupData) {
        dwarf.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(JolCraftItems.COPPER_SPANNER.get()));
    }
}