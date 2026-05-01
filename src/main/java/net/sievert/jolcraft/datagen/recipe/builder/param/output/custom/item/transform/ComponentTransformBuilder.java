package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ComponentTransform;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen-only builder for {@link ComponentTransform}.
 *
 * Builds the atomic {@link ComponentTransform.Config} variant.
 *
 * Semantics:
 * - {@link #set(DataComponentType, Object)} writes fixed component values onto the output stack.
 * - {@link #remove(Holder)} means "copy from the selected source except these components".
 * - {@link #removeAll(boolean)} with {@link #keep(Holder)} means "copy only these components from the selected source".
 * - {@link #source(String)} selects which runtime input source this transform reads from.
 *
 * Validation shape:
 * - removeAll(true)  => KEEP allowed, REMOVE forbidden
 * - removeAll(false) => REMOVE allowed, KEEP forbidden
 * - copy/filter rules require a nonblank source
 *
 * If mode is not explicitly set:
 * - first {@link #keep(Holder)} infers removeAll(true)
 * - first {@link #remove(Holder)} infers removeAll(false)
 *
 * Null values are ignored fail-closed.
 */
public final class ComponentTransformBuilder implements ParamBuilder<ComponentTransform> {

    private @Nullable String source;

    private boolean removeAll = false;
    private boolean removeAllSet = false;

    private final ArrayList<Holder<DataComponentType<?>>> keep = new ArrayList<>();
    private final ArrayList<Holder<DataComponentType<?>>> remove = new ArrayList<>();

    private @Nullable DataComponentPatch.Builder patchBuilder;

    private ComponentTransformBuilder() {}

    public static ComponentTransformBuilder create() {
        return new ComponentTransformBuilder();
    }

    public ComponentTransformBuilder source(@Nullable String source) {
        if (source == null || source.isBlank()) {
            this.source = null;
            return this;
        }

        this.source = source.trim();
        return this;
    }

    public ComponentTransformBuilder removeAll(boolean value) {
        this.removeAll = value;
        this.removeAllSet = true;

        if (value) {
            remove.clear();
        } else {
            keep.clear();
        }

        return this;
    }

    public ComponentTransformBuilder keep(@Nullable Holder<DataComponentType<?>> type) {
        if (type == null) return this;

        if (removeAllSet && !removeAll) return this;

        if (!removeAllSet) {
            this.removeAll = true;
            this.removeAllSet = true;
            this.remove.clear();
        }

        this.keep.add(type);
        return this;
    }

    public ComponentTransformBuilder remove(@Nullable Holder<DataComponentType<?>> type) {
        if (type == null) return this;

        if (removeAllSet && removeAll) return this;

        if (!removeAllSet) {
            this.removeAll = false;
            this.removeAllSet = true;
            this.keep.clear();
        }

        this.remove.add(type);
        return this;
    }

    public <T> ComponentTransformBuilder set(@Nullable DataComponentType<T> type, @Nullable T value) {
        if (type == null || value == null) return this;

        if (patchBuilder == null) {
            patchBuilder = DataComponentPatch.builder();
        }

        patchBuilder.set(type, value);
        return this;
    }

    @Override
    public ComponentTransform build() {
        DataComponentPatch patch =
                patchBuilder == null ? DataComponentPatch.EMPTY : patchBuilder.build();

        if (source == null && !removeAllSet && keep.isEmpty() && remove.isEmpty() && patch.isEmpty()) {
            return ComponentTransform.Config.EMPTY;
        }

        if (removeAllSet && removeAll) {
            return ComponentTransform.config(
                    source,
                    true,
                    List.copyOf(keep),
                    List.of(),
                    patch
            );
        }

        return ComponentTransform.config(
                source,
                false,
                List.of(),
                List.copyOf(remove),
                patch
        );
    }
}