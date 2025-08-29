package net.sievert.jolcraft.block.custom.crop;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum HopsType implements StringRepresentable {
    NONE("none"),
    ASGARNIAN("asgarnian_hop"),
    YANILLIAN("yanillian_hop"),
    DUSKHOLD("duskhold_hop"),
    KRANDONIAN("krandonian_hop");

    private final String name;

    HopsType(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}