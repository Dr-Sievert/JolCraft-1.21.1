package net.sievert.jolcraft.config.custom.dwarf;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.config.base.JolCraftCodecConfigManager;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public final class DwarfProfessionConfigManager extends JolCraftCodecConfigManager<DwarfProfession, DwarfProfessionConfig> {

    public static final String DIRECTORY = JolCraftStrings.slashed(
            JolCraftDictionary.CONFIG,
            JolCraftStrings.underscored(JolCraftDictionary.DWARF, JolCraftDictionary.PROFESSION)
    );

    public static final DwarfProfessionConfigManager INSTANCE = new DwarfProfessionConfigManager();

    private final Map<DwarfProfession, DwarfProfessionConfig> values =
            new EnumMap<>(DwarfProfession.class);

    private DwarfProfessionConfigManager() {
        super(DwarfProfessionConfig.CODEC, DIRECTORY);
    }

    public @NotNull DwarfProfessionConfig get(@Nullable DwarfProfession profession) {
        if (profession == null) return DwarfProfessionConfig.DEFAULTS;
        return values.getOrDefault(profession, DwarfProfessionConfig.DEFAULTS);
    }

    @Override
    protected @Nullable DwarfProfession keyFromId(@NotNull ResourceLocation id) {
        DwarfProfession profession = DwarfProfession.byId(id.getPath());
        return profession == DwarfProfession.NONE ? null : profession;
    }

    @Override
    protected void replaceAll(@NotNull Map<DwarfProfession, DwarfProfessionConfig> values) {
        this.values.clear();
        this.values.putAll(values);
    }
}