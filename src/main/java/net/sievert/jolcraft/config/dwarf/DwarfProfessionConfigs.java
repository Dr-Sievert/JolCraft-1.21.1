package net.sievert.jolcraft.config.dwarf;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.config.ConfigManager;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfProfessionConfigs extends ConfigManager<DwarfProfession, DwarfProfessionSettings> {

    public static final String DIRECTORY = "dwarf_professions";
    public static final ResourceLocation RELOAD_LISTENER_ID = JolCraft.location(DIRECTORY);

    private static final EnumMap<DwarfProfession, DwarfProfessionSettings> CACHE = new EnumMap<>(DwarfProfession.class);

    private static final DwarfProfessionSettings DEFAULT_SETTINGS = DwarfProfessionSettings.mainOnly();

    public DwarfProfessionConfigs() {
        super(DwarfProfessionSettings.CODEC, DIRECTORY);
    }

    @Override
    protected void clear() {
        CACHE.clear();
    }

    @Override
    protected @Nullable DwarfProfession keyFromId(ResourceLocation id) {
        String professionId = id.getPath();
        DwarfProfession profession = DwarfProfession.byId(professionId);
        if (profession == DwarfProfession.NONE) {
            JolCraftLogs.warn(JolCraftLogTags.CONFIG, "Ignoring dwarf profession config '{}' (no matching profession)", id);
            return null;
        }
        return profession;
    }

    @Override
    protected void put(DwarfProfession key, DwarfProfessionSettings value) {
        CACHE.put(key, value);
    }

    @Nullable
    public static DwarfProfessionSettings get(DwarfProfession profession) {
        return CACHE.get(profession);
    }

    public static DwarfProfessionSettings getOrDefault(DwarfProfession profession) {
        if (profession == DwarfProfession.NONE) return DEFAULT_SETTINGS;
        return CACHE.getOrDefault(profession, DEFAULT_SETTINGS);
    }
}