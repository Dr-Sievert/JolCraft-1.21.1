package net.sievert.jolcraft.param.base;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

public record RegistryTarget<T>(
        Either<Holder<T>, TagKey<T>> value
) {
    public RegistryTarget {
        if (value == null) {
            throw new IllegalArgumentException("RegistryTarget value cannot be null");
        }
    }

    public DataResult<RegistryTarget<T>> validate() {
        return ParamValidations.ok(this);
    }

    public boolean isHolder() {
        return value.left().isPresent();
    }

    public boolean isTag() {
        return value.right().isPresent();
    }
}