package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.behavior;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.behavior.profession.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfProfessionBehaviors {

    private static final EnumMap<DwarfProfession, DwarfProfessionBehavior> BEHAVIORS =
            new EnumMap<>(DwarfProfession.class);

    static {
        BEHAVIORS.put(DwarfProfession.EXPLORER, ExplorerBehavior.INSTANCE);
        BEHAVIORS.put(DwarfProfession.GUILDMASTER, GuildmasterBehavior.INSTANCE);
    }

    @Nullable
    public static DwarfProfessionBehavior get(DwarfProfession profession) {
        return BEHAVIORS.get(profession);
    }

    private DwarfProfessionBehaviors() {}
}
