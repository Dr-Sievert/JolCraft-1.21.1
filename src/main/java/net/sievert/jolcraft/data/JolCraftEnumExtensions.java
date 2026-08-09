package net.sievert.jolcraft.data;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Style;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftRarityIds;

import java.util.function.UnaryOperator;

public final class JolCraftEnumExtensions {

    private JolCraftEnumExtensions() {}

    public static final class Rarity {

        private Rarity() {}

        public static final EnumProxy<net.minecraft.world.item.Rarity> LEGENDARY = new EnumProxy<>(
                net.minecraft.world.item.Rarity.class,
                -1,
                JolCraft.location(JolCraftRarityIds.LEGENDARY).toString(),
                (UnaryOperator<Style>) style -> style
                        .withColor(ChatFormatting.GOLD)
                        .withBold(true)
        );
    }

    public static final class HeartType {

        private HeartType() {}

        public static final EnumProxy<Gui.HeartType> OVERHEAL = new EnumProxy<>(
                Gui.HeartType.class,
                JolCraft.location("hud/heart/overheal_full"),
                JolCraft.location("hud/heart/overheal_full_blinking"),
                JolCraft.location("hud/heart/overheal_half"),
                JolCraft.location("hud/heart/overheal_half_blinking"),
                JolCraft.location("hud/heart/overheal_hardcore_full"),
                JolCraft.location("hud/heart/overheal_hardcore_full_blinking"),
                JolCraft.location("hud/heart/overheal_hardcore_half"),
                JolCraft.location("hud/heart/overheal_hardcore_half_blinking")
        );
    }
}