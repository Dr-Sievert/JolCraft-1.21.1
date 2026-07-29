package net.sievert.jolcraft.world.item.custom.compass;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class DeepslateCompassDialItem extends Item {

    public DeepslateCompassDialItem(Properties properties) {
        super(properties);
    }

    private final String TAG_TRANSLATION_PREFIX = JolCraftStrings.slashed(
            JolCraftStrings.dotted(JolCraftDictionary.TAG, JolCraftDictionary.WORLDGEN),
            JolCraftStrings.dotted(JolCraftDictionary.STRUCTURE, JolCraft.MOD_ID)
    );

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String structureId = stack.get(JolCraftDataComponents.STRUCTURE_GROUP);
        if (structureId != null && !structureId.isEmpty()) {
            tooltip.add(
                    Component.translatable(JolCraftStrings.dotted(TAG_TRANSLATION_PREFIX, structureId)).withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable(JolCraftLanguageKeys.UNKNOWN).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
