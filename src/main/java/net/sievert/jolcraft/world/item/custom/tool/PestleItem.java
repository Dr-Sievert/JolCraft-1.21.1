package net.sievert.jolcraft.world.item.custom.tool;

import net.minecraft.world.item.ToolMaterial;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PestleItem extends ToolItem {

    public PestleItem(ToolMaterial material, Properties properties) {
        super(material, properties);
    }

    /*

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Player player = JolCraftProxy.access().getLocalPlayer();

        if (JolCraftProxy.access().isAltDown()) {
            tooltip.add(Component.translatable("tooltip.jolcraft.chisel")
                    .withStyle(ChatFormatting.GRAY));

            if (!DwarfLoreUnlockHelper.hasUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                tooltip.add(Component.translatable("tooltip.jolcraft.chisel.cut_locked")
                        .withStyle(ChatFormatting.RED));
            }
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.jolcraft.hold_key",
                    TooltipHelper.altKey()
            ).withStyle(ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }


     */
}
