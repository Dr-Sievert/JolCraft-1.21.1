package net.sievert.jolcraft.integration.jade.provider.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.custom.StrongboxBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum StrongboxComponentProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final ResourceLocation UID = JolCraft.location(JolCraftBlockIds.STRONGBOX);

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        boolean locked = accessor.getBlockState().getValue(StrongboxBlock.LOCKED);

        tooltip.add(
                Component.translatable(
                                locked
                                        ? JolCraftLanguageKeys.LOCKED
                                        : JolCraftLanguageKeys.UNLOCKED
                        )
                        .withStyle(
                                locked
                                        ? ChatFormatting.RED
                                        : ChatFormatting.GREEN
                        )
        );
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}