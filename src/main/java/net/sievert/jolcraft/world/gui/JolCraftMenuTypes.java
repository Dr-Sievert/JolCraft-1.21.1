package net.sievert.jolcraft.world.gui;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.menu.JolCraftMenuIds;
import net.sievert.jolcraft.world.gui.menu.*;

public final class JolCraftMenuTypes {

    private JolCraftMenuTypes(){}

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, JolCraft.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<StrongboxMenu>> STRONGBOX_MENU =
            registerExtended(JolCraftMenuIds.STRONGBOX, StrongboxMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<LockMenu>> LOCK_MENU =
            registerExtended(JolCraftMenuIds.LOCKED_STRONGBOX, LockMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<LapidaryBenchMenu>> LAPIDARY_BENCH_MENU =
            registerSimple(JolCraftMenuIds.LAPIDARY_BENCH, LapidaryBenchMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<MortarMenu>> MORTAR_MENU =
            registerSimple(JolCraftMenuIds.MORTAR, MortarMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<DwarfMerchantMenu>> DWARF_MERCHANT_MENU =
            registerExtended(JolCraftMenuIds.DWARF_MERCHANT, DwarfMerchantMenu::new);


    /**
     * Register an "extended" menu type (NeoForge): factory gets (windowId, inv, buf).
     */
    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerExtended(
            String name,
            IContainerFactory<T> factory
    ) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    /**
     * Register a vanilla-style menu type: factory gets (windowId, inv).
     */
    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerSimple(
            String name,
            MenuType.MenuSupplier<T> factory
    ) {
        return MENUS.register(name, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
