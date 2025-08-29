package net.sievert.jolcraft.block.custom;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum FermentingStage implements StringRepresentable {
    YEAST_FERMENTING,
    YEAST_READY,
    MALTED,
    HOPS,
    BREW_FERMENTING,
    BREW_READY;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase();
    }
}
