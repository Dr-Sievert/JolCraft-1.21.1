package net.sievert.jolcraft.config.custom.dwarf;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.config.ConfigManager;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public final class DwarfProfessionConfigManager extends ConfigManager<DwarfProfession, DwarfProfessionConfig> {

    public static final String DIRECTORY = JolCraftStrings.slashed(
            JolCraftDictionary.CONFIG,
            JolCraftStrings.underscored(JolCraftDictionary.DWARF, JolCraftDictionary.PROFESSION)
    );

    public static final ResourceLocation LISTENER_ID = JolCraft.location(DIRECTORY);

    public static final DwarfProfessionConfigManager INSTANCE = new DwarfProfessionConfigManager();

    private final Map<DwarfProfession, DwarfProfessionConfig> values =
            new EnumMap<>(DwarfProfession.class);

    private DwarfProfessionConfigManager() {
        super(DwarfProfessionConfig.CODEC, DIRECTORY);
    }

    public @NotNull DwarfProfessionConfig get(@Nullable DwarfProfession profession) {
        if (profession == null) {
            return DwarfProfessionConfig.DEFAULTS;
        }

        DwarfProfessionConfig cfg = values.get(profession);
        return cfg != null ? cfg : DwarfProfessionConfig.DEFAULTS;
    }

    @Override
    protected void clear() {
        values.clear();
    }

    @Nullable
    @Override
    protected DwarfProfession keyFromId(@NotNull ResourceLocation id) {
        String path = id.getPath();

        DwarfProfession profession = DwarfProfession.byId(path);
        if (profession == DwarfProfession.NONE && !path.equals(DwarfProfession.NONE.getId())) {
            return null;
        }

        return profession;
    }

    @Override
    protected void put(@NotNull DwarfProfession key, @NotNull DwarfProfessionConfig value) {
        values.put(key, value);
    }
}