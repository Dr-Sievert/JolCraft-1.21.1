package net.sievert.jolcraft.world.item.client.color;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.client.color.custom.DialColor;
import net.sievert.jolcraft.world.item.client.color.custom.EssenceColor;
import net.sievert.jolcraft.world.item.client.color.custom.FluidColor;
import net.sievert.jolcraft.world.item.client.color.custom.PotionColor;

@OnlyIn(Dist.CLIENT)
public final class JolCraftItemColors {

    private static final int NO_TINT = JolCraftColors.argb("FFFFFF");
    private static final int DEFAULT_COMPASS_DYE_COLOR = JolCraftColors.rgb("D3D3D3");

    private JolCraftItemColors() {}

    public static void register(RegisterColorHandlersEvent.Item event) {
        int colors = 0;

        colors += register(
                event,
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                (stack, tintIndex) -> tintIndex == 1
                        ? DyedItemColor.getOrDefault(stack, DEFAULT_COMPASS_DYE_COLOR)
                        : NO_TINT
        );

        colors += register(
                event,
                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(),
                (stack, tintIndex) -> tintIndex == 1
                        ? DialColor.color(stack)
                        : NO_TINT
        );

        colors += register(
                event,
                JolCraftItems.DEEPSLATE_COMPASS.get(),
                (stack, tintIndex) -> switch (tintIndex) {
                    case 1 -> DyedItemColor.getOrDefault(stack, DEFAULT_COMPASS_DYE_COLOR);
                    case 3 -> DialColor.color(stack);
                    default -> NO_TINT;
                }
        );

        colors += register(
                event,
                JolCraftItems.ESSENCE.get(),
                (stack, tintIndex) -> tintIndex == 0
                        ? EssenceColor.color(stack)
                        : NO_TINT
        );

        colors += register(
                event,
                (stack, tintIndex) -> tintIndex == 0
                        ? PotionColor.color(stack)
                        : NO_TINT,
                Items.POTION,
                Items.SPLASH_POTION,
                Items.LINGERING_POTION,
                Items.TIPPED_ARROW
        );

        colors += register(
                event,
                JolCraftItems.YEAST_CULTURE.get(),
                (stack, tintIndex) -> tintIndex == 0
                        ? FluidColor.brewingSpeedColor(stack)
                        : NO_TINT
        );

        colors += register(
                event,
                JolCraftItems.YEAST.get(),
                (stack, tintIndex) -> tintIndex == 0
                        ? FluidColor.color(stack, BrewingColors.YEAST)
                        : NO_TINT
        );

        colors += register(
                event,
                JolCraftItems.TANNIN.get(),
                (stack, tintIndex) -> tintIndex == 0
                        ? FluidColor.color(stack, BrewingColors.TANNIN)
                        : NO_TINT
        );

        colors += register(
                event,
                JolCraftItems.DWARVEN_BREW.get(),
                (stack, tintIndex) -> tintIndex == 1
                        ? FluidColor.color(stack, BrewingColors.DWARVEN_BREW)
                        : NO_TINT
        );

        colors += register(
                event,
                JolCraftItems.DWARVEN_BREW_BUCKET.get(),
                (stack, tintIndex) -> tintIndex == 1
                        ? FluidColor.color(stack, BrewingColors.DWARVEN_BREW)
                        : NO_TINT
        );

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Registered {} items with custom color layers",
                colors
        );
    }

    private static int register(
            RegisterColorHandlersEvent.Item event,
            Item item,
            ItemColor color
    ) {
        event.register(color, item);
        return 1;
    }

    private static int register(
            RegisterColorHandlersEvent.Item event,
            ItemColor color,
            Item... items
    ) {
        event.register(color, items);
        return items.length;
    }
}