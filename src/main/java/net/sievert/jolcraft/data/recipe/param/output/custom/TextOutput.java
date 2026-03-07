package net.sievert.jolcraft.data.recipe.param.output.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Text/message output param.
 *
 * Pure data:
 * - text (literal OR translation key; caller decides interpretation)
 * - style (0..N formatting tokens)
 * - overlay (actionbar) default true
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record TextOutput(
        String text,
        List<ChatFormatting> style,
        boolean overlay
) implements OutputParam, SelfValidating<TextOutput> {

    private static final int MAX_FORMATTING_NAME = 32;
    private static final int MAX_TEXT = 1024;
    private static final int MAX_STYLE = 32;

    public TextOutput {
        style = style.isEmpty() ? List.of() : sanitizeList(style);
    }

    // ---------------------------------------------------------------------
    // OUTPUT PARAM TYPE ID
    // ---------------------------------------------------------------------

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.TEXT, JolCraftDictionary.OUTPUT));

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<ChatFormatting> FORMATTING_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        ChatFormatting f = ChatFormatting.getByName(s);
                        return f == null
                                ? DataResult.error(() -> "Unknown ChatFormatting: '" + s + "'")
                                : DataResult.success(f);
                    },
                    ChatFormatting::getName
            );

    private static final Codec<TextOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf(JolCraftParameterIds.TEXT)
                            .forGetter(TextOutput::text),

                    FORMATTING_CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.STYLE, List.of())
                            .forGetter(TextOutput::style),

                    Codec.BOOL.optionalFieldOf(JolCraftParameterIds.OVERLAY, true)
                            .forGetter(TextOutput::overlay)
            ).apply(instance, TextOutput::new));

    public static final Codec<TextOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static void encodeFormatting(RegistryFriendlyByteBuf buf, ChatFormatting f) {
        String name = f.getName();
        buf.writeUtf(name);
    }

    private static ChatFormatting decodeFormatting(RegistryFriendlyByteBuf buf) {
        String name = buf.readUtf(MAX_FORMATTING_NAME);
        ChatFormatting f = ChatFormatting.getByName(name);
        return (f != null) ? f : ChatFormatting.RESET;
    }

    private static void encodeStyle(RegistryFriendlyByteBuf buf, @Nullable List<ChatFormatting> style) {
        List<ChatFormatting> s = (style == null) ? List.of() : style;

        int n = Math.min(MAX_STYLE, s.size());
        buf.writeVarInt(n);
        for (int i = 0; i < n; i++) {
            encodeFormatting(buf, s.get(i));
        }
    }

    private static List<ChatFormatting> decodeStyle(RegistryFriendlyByteBuf buf) {
        int n = buf.readVarInt();

        int store = Math.max(0, Math.min(MAX_STYLE, n));

        List<ChatFormatting> out = new ArrayList<>(store);
        for (int i = 0; i < n; i++) {
            ChatFormatting f = decodeFormatting(buf);
            if (i < store) {
                out.add(f);
            }
        }

        return out.isEmpty() ? List.of() : sanitizeList(out);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TextOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeUtf(value.text);
                        encodeStyle(buf, value.style);
                        buf.writeBoolean(value.overlay);
                    },
                    (buf) -> {
                        String text = buf.readUtf(MAX_TEXT);
                        List<ChatFormatting> style = decodeStyle(buf);
                        boolean overlay = buf.readBoolean();
                        return new TextOutput(text, style, overlay);
                    }
            );

    // ---------------------------------------------------------------------
    // OUTPUT PARAM
    // ---------------------------------------------------------------------

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        if (style.size() > MAX_STYLE) {
            return List.of();
        }

        return List.of(new Output.Text(List.of(new Output.Message(text, style, overlay))));
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<TextOutput> validate() {
        if (text == null || text.isBlank()) {
            return SelfValidating.invalid("Missing/blank required field: '" + JolCraftParameterIds.TEXT + "'");
        }

        if (style.size() > MAX_STYLE) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.STYLE + "' may not exceed " + MAX_STYLE + " entries");
        }

        return SelfValidating.ok(this);
    }


    private static <T> List<T> sanitizeList(List<T> in) {
        if (in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

}