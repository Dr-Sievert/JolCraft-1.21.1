package net.sievert.jolcraft.world.item.component.custom.crate;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public enum CrateTheme implements JolCraftEnumHelper.StringId {

    ALCHEMY(JolCraftDictionary.ALCHEMY),
    ARCHAEOLOGY(JolCraftDictionary.ARCHAEOLOGY),
    ARTISAN(JolCraftDictionary.ARTISAN),
    GARDEN(JolCraftDictionary.GARDEN),
    MINING(JolCraftDictionary.MINING),
    OCEAN(JolCraftDictionary.OCEAN),
    REINFORCED(JolCraftDictionary.REINFORCED),
    SLAYER(JolCraftDictionary.SLAYER),
    TREASURE(JolCraftDictionary.TREASURE);

    private final String id;

    CrateTheme(@NotNull String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }


    public static @Nullable CrateTheme byId(@Nullable String id) {
        return JolCraftEnumHelper.byStringIdNullable(
                CrateTheme.class,
                id,
                null
        );
    }
}