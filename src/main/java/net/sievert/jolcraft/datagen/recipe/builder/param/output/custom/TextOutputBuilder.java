package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.ChatFormatting;
import net.sievert.jolcraft.data.recipe.param.output.custom.TextOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class TextOutputBuilder {

    private @Nullable String text;
    private final ArrayList<ChatFormatting> style = new ArrayList<>();
    private boolean overlay = true;

    private TextOutputBuilder() {}

    public static @NotNull TextOutputBuilder builder() {
        return new TextOutputBuilder();
    }

    // ---------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------

    public @NotNull TextOutputBuilder text(@Nullable String text) {
        this.text = text;
        return this;
    }

    public @NotNull TextOutputBuilder overlay(boolean overlay) {
        this.overlay = overlay;
        return this;
    }

    // ---------------------------------------------------------------------
    // Style helpers
    // ---------------------------------------------------------------------

    public @NotNull TextOutputBuilder style(@Nullable List<ChatFormatting> style) {
        this.style.clear();
        if (style != null) {
            for (ChatFormatting f : style) {
                if (f != null) this.style.add(f);
            }
        }
        return this;
    }

    public @NotNull TextOutputBuilder addStyle(@Nullable ChatFormatting formatting) {
        if (formatting != null) this.style.add(formatting);
        return this;
    }

    public @NotNull TextOutputBuilder clearStyle() {
        this.style.clear();
        return this;
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public @NotNull DataResult<TextOutput> build() {
        if (text == null || text.isBlank()) {
            return DataResult.error(() -> "Missing/blank required field: 'text'");
        }

        TextOutput out = new TextOutput(text, List.copyOf(style), overlay);
        return out.validate();
    }
}