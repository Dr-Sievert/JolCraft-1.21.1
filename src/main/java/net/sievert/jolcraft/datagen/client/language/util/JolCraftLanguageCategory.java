package net.sievert.jolcraft.datagen.client.language.util;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum JolCraftLanguageCategory {

    BLOCK("block"),
    ITEM("item"),
    ENTITY("entity"),
    TOOLTIP("tooltip"),
    CONTAINER("container"),
    JEI("jei"),
    STAT("stat"),
    LORE("lore");

    private final String key;

    JolCraftLanguageCategory(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
