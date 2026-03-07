package net.sievert.jolcraft.data.recipe.param.condition;

import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Atomic condition contract (runtime only).
 *
 * Dispatch (CODEC/STREAM + ids/discriminators) lives in {@link ConditionTypes}.
 */
public interface Condition extends SelfValidating<Condition>, RegistryIntrospectionSource {

    ResourceLocation typeId();

    /**
     * Runtime must be total:
     * - invalid condition -> false
     * - never throw in JolCraft logic
     */
    boolean test(@NotNull WorldContext ctx);

    default boolean invert() {
        return false;
    }

    /**
     * Conditions are not necessarily registry-backed, so default is "no introspection".
     */
    @Override
    default @NotNull List<RegistryIntrospection> introspections() {
        return List.of();
    }

    @Override
    default @NotNull DataResult<Condition> validate() {
        return SelfValidating.ok(this);
    }
}