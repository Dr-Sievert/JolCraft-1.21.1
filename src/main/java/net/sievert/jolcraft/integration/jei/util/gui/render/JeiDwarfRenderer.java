package net.sievert.jolcraft.integration.jei.util.gui.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.integration.jei.util.gui.JeiDrawHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sievert.jolcraft.integration.jei.util.gui.JeiGuiConstants.ENTITY_TARGET_SIZE;

public final class JeiDwarfRenderer {

    private JeiDwarfRenderer() {
    }

    public static @Nullable LivingEntity create(
            @NotNull DwarfProfession profession
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return null;
        }

        DwarfEntity dwarf =
                new DwarfEntity(
                        DwarfProfessionHelper.getEntityType(
                                profession
                        ),
                        minecraft.level
                );

        dwarf.getEntityData()
                .set(
                        AbstractDwarfEntity.PROFESSION,
                        profession.getId()
                );

        return dwarf;
    }

    public static void drawBountyDwarf(
            @NotNull GuiGraphics graphics,
            @NotNull DwarfProfession profession,
            float centerX,
            float bottomY
    ) {
        LivingEntity dwarf =
                create(
                        profession
                );

        if (dwarf == null) {
            return;
        }

        JeiEntityRenderer.renderToBounds(
                graphics,
                dwarf,
                centerX,
                bottomY,
                ENTITY_TARGET_SIZE,
                200.0F,
                -5.0F
        );
    }

    public static void drawTradeDwarf(
            @NotNull GuiGraphics graphics,
            @NotNull DwarfProfession profession,
            float centerX,
            float bottomY
    ) {
        LivingEntity dwarf =
                create(
                        profession
                );

        if (dwarf == null) {
            return;
        }

        JeiEntityRenderer.render(
                graphics,
                dwarf,
                centerX,
                bottomY,
                ENTITY_TARGET_SIZE
                        / dwarf.getScale(),
                200.0F,
                170.0F,
                -5.0F,
                false
        );
    }

    public static void drawHeader(
            @NotNull GuiGraphics graphics,
            @NotNull Font font,
            @NotNull Component level,
            @NotNull DwarfProfession profession,
            float centerX,
            int levelY,
            int professionY
    ) {
        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                level,
                centerX,
                levelY
        );

        JeiDrawHelper.drawCenteredText(
                graphics,
                font,
                profession.getDisplayName(),
                centerX,
                professionY
        );
    }

    public static @NotNull ItemStack spawnEgg(
            @NotNull DwarfProfession profession
    ) {
        return new ItemStack(
                DwarfProfessionHelper
                        .getSpawnEgg(
                                profession
                        )
                        .get()
        );
    }
}
