package net.sievert.jolcraft.integration.jade.provider.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class DwarfComponentProvider implements IEntityComponentProvider {

    private static final ResourceLocation UID = JolCraft.location(JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDirectoryIds.PROFESSION));

    public static final DwarfComponentProvider INSTANCE = new DwarfComponentProvider();

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            EntityAccessor accessor,
            IPluginConfig config
    ) {
        if (!(accessor.getEntity() instanceof AbstractDwarfEntity dwarf) || dwarf.getProfession() != DwarfProfession.NONE) return;

        tooltip.add(Component.translatable(
                JolCraftLanguageKeys.TOOLTIP_JADE_DWARF_PROFESSION,
                dwarf.getProfession().professionName()
        ));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}