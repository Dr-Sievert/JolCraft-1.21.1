package net.sievert.jolcraft.world.item.custom.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;
import net.sievert.jolcraft.world.entity.attachment.player.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class PestleItem extends ToolItem {

    private static final int TAGGED_PESTLE_GRINDING_PROGRESS = 1;

    private final float grindingSpeed;
    private final int minimumGrindingProgress;
    private final int maximumGrindingProgress;

    public PestleItem(
            Tier tier,
            Properties properties
    ) {
        super(tier, properties);

        this.grindingSpeed = tier.getSpeed();
        this.minimumGrindingProgress = Math.max(
                1,
                (int) Math.floor(grindingSpeed * 2.0F)
        );
        this.maximumGrindingProgress = Math.max(
                minimumGrindingProgress,
                (int) Math.ceil(grindingSpeed * 3.0F)
        );
    }

    public int rollGrindingProgress(RandomSource random) {
        return minimumGrindingProgress
                + random.nextInt(
                maximumGrindingProgress
                        - minimumGrindingProgress
                        + 1
        );
    }

    public static int rollGrindingProgress(
            ItemStack stack,
            RandomSource random
    ) {
        if (stack.getItem() instanceof PestleItem pestle) {
            return pestle.rollGrindingProgress(random);
        }

        return TAGGED_PESTLE_GRINDING_PROGRESS;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        Player player = JolCraftProxy.access().getLocalPlayer();
        if (!DwarfLoreAttachmentHelper.hasUnlock(player, DwarfLoreKey.ALCHEMY_RECIPES)) {
            tooltipComponents.add(Component.translatable(JolCraftLanguageKeys.TOOLTIP_MORTAR_MULTI_LOCKED).withStyle(ChatFormatting.RED));
        }

        tooltipComponents.add(
                Component.translatable(
                        JolCraftLanguageKeys.TOOLTIP_PESTLE_GRIND_SPEED,
                        Component.literal(Float.toString(grindingSpeed)).withStyle(ChatFormatting.BLUE)
                ).withStyle(ChatFormatting.GRAY)
        );

        super.appendHoverText(
                stack,
                context,
                tooltipComponents,
                tooltipFlag
        );
    }
}
