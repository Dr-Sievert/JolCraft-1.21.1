package net.sievert.jolcraft.world.item.custom.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingTooltipHelper;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TanninItem extends Item {

    public TanninItem(Properties properties) {
        super(properties);
    }

    /**
     * Prefixes the tannin's normal name if vintage max age.
     */
    @Override
    public Component getName(ItemStack stack) {
        if (DwarvenBrewFluidHelper.getContainedMaxAge(stack) == DwarvenBrewAge.VINTAGE) {
            return Component.translatable(
                    JolCraftLanguageKeys.PREFIX_NAME,
                    Component.translatable(JolCraftLanguageKeys.REFINED),
                    super.getName(stack)
            );
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        BrewingTooltipHelper.appendItemFluidTooltip(
                stack,
                tooltip::add
        );

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }
}
