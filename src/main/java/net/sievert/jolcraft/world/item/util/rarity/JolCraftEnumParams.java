package net.sievert.jolcraft.world.item.util.rarity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public final class JolCraftEnumParams {

    private JolCraftEnumParams() {}

    public static final EnumProxy<Rarity> LEGENDARY_RARITY = new EnumProxy<>(
            Rarity.class,
            -1,
            "jolcraft:legendary",
            (UnaryOperator<Style>) style -> style
                    .withColor(ChatFormatting.GOLD)
                    .withBold(true)
    );
}