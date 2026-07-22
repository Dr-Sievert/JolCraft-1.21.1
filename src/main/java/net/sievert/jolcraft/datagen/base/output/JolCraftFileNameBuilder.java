package net.sievert.jolcraft.datagen.base.output;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class JolCraftFileNameBuilder {

    private final List<String> tokens = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private @Nullable String extension;

    private JolCraftFileNameBuilder() {}

    public static @NotNull JolCraftFileNameBuilder create() {
        return new JolCraftFileNameBuilder();
    }

    public @NotNull JolCraftFileNameBuilder token(@Nullable String raw) {
        addToken(raw);
        return this;
    }

    public @NotNull JolCraftFileNameBuilder tokens(@Nullable Iterable<String> raws) {
        if (raws == null) {
            errors.add("tokens iterable is null");
            return this;
        }

        for (String raw : raws) {
            addToken(raw);
        }

        return this;
    }

    public @NotNull JolCraftFileNameBuilder extension(@Nullable String rawExtension) {
        String rawTrim = rawExtension == null ? "" : rawExtension.trim();
        if (rawTrim.isEmpty()) {
            errors.add("extension is null/blank");
            this.extension = null;
            return this;
        }

        String normalized = JolCraftStrings.normalizeExtension(rawTrim);
        if (normalized.isEmpty() || normalized.equals(".")) {
            errors.add("extension is invalid: " + rawExtension);
            this.extension = null;
            return this;
        }

        this.extension = normalized;
        return this;
    }

    public @NotNull JolCraftFileNameBuilder noExtension() {
        this.extension = null;
        return this;
    }

    public @NotNull DataResult<String> buildBaseName() {
        if (tokens.isEmpty()) {
            return DataResult.error(() -> "fileName: no tokens were provided");
        }

        String baseName = JolCraftStrings.underscored(tokens.toArray(String[]::new));
        return validateAndMerge(baseName, "base name");
    }

    public @NotNull DataResult<String> build() {
        return buildBaseName().flatMap(baseName -> {
            String fileName = extension == null ? baseName : baseName + extension;
            return validateAndMerge(fileName, "final file name");
        });
    }

    public static @NotNull DataResult<String> validateBaseName(@Nullable String raw) {
        return validate(raw, "base name");
    }

    public static @NotNull DataResult<String> validateFileName(@Nullable String raw) {
        return validate(raw, "final file name");
    }

    private @NotNull DataResult<String> validateAndMerge(
            @Nullable String raw,
            @NotNull String label
    ) {
        DataResult<String> validated = validate(raw, label);

        if (errors.isEmpty()) {
            return validated;
        }

        String partial = validated.result().orElse(raw == null ? "" : raw);
        return DataResult.error(
                () -> "fileName: " + String.join("; ", errors),
                partial
        );
    }

    private static @NotNull DataResult<String> validate(
            @Nullable String raw,
            @NotNull String label
    ) {
        String value = raw == null ? "" : raw.trim();

        if (value.isEmpty()) {
            return DataResult.error(() -> "fileName: " + label + " is blank");
        }

        if (value.startsWith(".") || value.endsWith(".")) {
            return DataResult.error(() -> "fileName: invalid " + label + ": " + value, value);
        }

        if (value.contains("..") || value.contains("/") || value.contains("\\")) {
            return DataResult.error(() -> "fileName: invalid " + label + ": " + value, value);
        }

        return DataResult.success(value);
    }

    private void addToken(@Nullable String raw) {
        String rawTrim = raw == null ? "" : raw.trim();
        if (rawTrim.isEmpty()) {
            errors.add("token is null/blank");
            return;
        }

        String normalized = JolCraftStrings.normalizeUnderscored(rawTrim);
        if (normalized.isBlank()) {
            errors.add("token became blank after normalization");
            return;
        }

        if (normalized.contains("/") || normalized.contains("\\") || normalized.contains("..")) {
            errors.add("token contains invalid path content: " + rawTrim);
            return;
        }

        tokens.add(normalized);
    }
}