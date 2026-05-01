package net.sievert.jolcraft.datagen.base.output;

import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * One unit of datagen output.
 *
 * Holds:
 * - validated file name
 * - deferred save action
 *
 * Does NOT hold:
 * - folder/path
 * - domain-specific id binding
 *
 * File-name validation belongs to the file-name builder layer.
 */
public final class JolCraftDataEmission<TTarget> {

    private final String fileName;
    private final BiConsumer<TTarget, String> saveAction;

    public JolCraftDataEmission(
            @NotNull String fileName,
            @NotNull BiConsumer<TTarget, String> saveAction
    ) {
        this.fileName = Objects.requireNonNull(fileName, JolCraftDictionary.NAME);
        this.saveAction = Objects.requireNonNull(saveAction, JolCraftDictionary.SAVE);
    }

    @NotNull
    public String fileName() {
        return fileName;
    }

    public void save(@NotNull TTarget target, @NotNull String path) {
        saveAction.accept(
                Objects.requireNonNull(target, JolCraftParameterIds.TARGET),
                Objects.requireNonNull(path, JolCraftParameterIds.PATH)
        );
    }
}