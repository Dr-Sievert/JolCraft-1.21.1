package net.sievert.jolcraft.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class JolCraftCommonConfig {

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        SPEC = builder.build();
    }

    private JolCraftCommonConfig() {}
}