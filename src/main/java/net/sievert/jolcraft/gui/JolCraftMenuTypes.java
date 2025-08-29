package net.sievert.jolcraft.gui;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.gui.custom.dwarf.DwarfMerchantMenu;
import net.sievert.jolcraft.gui.custom.lapidary_bench.LapidaryBenchMenu;
import net.sievert.jolcraft.gui.custom.strongbox.LockMenu;
import net.sievert.jolcraft.gui.custom.strongbox.StrongboxMenu;

public class JolCraftMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, JolCraft.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<StrongboxMenu>> STRONGBOX_MENU =
            registerMenuType("strongbox_menu", StrongboxMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<LockMenu>> LOCK_MENU =
            registerMenuType("lock_menu", LockMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<LapidaryBenchMenu>> LAPIDARY_BENCH_MENU =
            registerMenuType("lapidary_bench_menu", LapidaryBenchMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<DwarfMerchantMenu>> DWARF_MERCHANT_MENU =
            registerMenuType("dwarf_merchant_menu", DwarfMerchantMenu::new);


    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}