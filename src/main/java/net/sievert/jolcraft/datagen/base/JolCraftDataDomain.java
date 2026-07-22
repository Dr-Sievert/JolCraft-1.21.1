package net.sievert.jolcraft.datagen.base;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.NotNull;

public enum JolCraftDataDomain implements JolCraftEnumHelper.StringId {

    ADVANCEMENT(JolCraftDictionary.ADVANCEMENT),
    CONFIG(JolCraftDictionary.CONFIG),
    LANGUAGE(JolCraftDictionary.LANGUAGE),
    LOOT(JolCraftDictionary.LOOT),
    MODEL(JolCraftDictionary.MODEL),
    RECIPE(JolCraftDictionary.RECIPE),
    SOUND(JolCraftDictionary.SOUND),
    TAG(JolCraftDictionary.TAG);

    private final String id;

    JolCraftDataDomain(String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }
}