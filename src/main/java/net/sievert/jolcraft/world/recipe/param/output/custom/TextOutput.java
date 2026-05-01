package net.sievert.jolcraft.world.recipe.param.output.custom;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

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

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.TEXT, JolCraftDictionary.OUTPUT));

    public static final byte DISC = 5;

    public TextOutput {
        if (text.isBlank()) {
            throw new IllegalArgumentException("Missing/blank required field: '" + JolCraftParameterIds.TEXT + "'");
        }
        style = sanitizeList(style);
    }

    private static final Codec<ChatFormatting> FORMATTING_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        ChatFormatting formatting = ChatFormatting.getByName(s);
                        return formatting == null
                                ? DataResult.error(() -> "Unknown ChatFormatting: '" + s + "'")
                                : DataResult.success(formatting);
                    },
                    ChatFormatting::getName
            );

    private static final Codec<List<ChatFormatting>> STYLE_CODEC =
            Codec.either(FORMATTING_CODEC, FORMATTING_CODEC.listOf())
                    .xmap(
                            either -> sanitizeList(either.map(List::of, list -> list)),
                            style -> style.size() == 1
                                    ? Either.left(style.getFirst())
                                    : Either.right(style)
                    );

    private static final Codec<TextOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING
                            .fieldOf(JolCraftParameterIds.TEXT)
                            .forGetter(TextOutput::text),

                    STYLE_CODEC
                            .optionalFieldOf(JolCraftParameterIds.STYLE, List.of())
                            .forGetter(TextOutput::style),

                    Codec.BOOL
                            .optionalFieldOf(JolCraftParameterIds.OVERLAY, true)
                            .forGetter(TextOutput::overlay)
            ).apply(instance, TextOutput::new));

    public static final Codec<TextOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    private static void encodeFormatting(RegistryFriendlyByteBuf buf, ChatFormatting formatting) {
        buf.writeUtf(formatting.getName());
    }

    private static ChatFormatting decodeFormatting(RegistryFriendlyByteBuf buf) {
        String name = buf.readUtf(MAX_FORMATTING_NAME);
        ChatFormatting formatting = ChatFormatting.getByName(name);
        return formatting != null ? formatting : ChatFormatting.RESET;
    }

    private static void encodeStyle(RegistryFriendlyByteBuf buf, @Nullable List<ChatFormatting> style) {
        List<ChatFormatting> safe = style == null ? List.of() : style;
        int size = Math.min(MAX_STYLE, safe.size());
        buf.writeVarInt(size);
        for (int i = 0; i < size; i++) {
            encodeFormatting(buf, safe.get(i));
        }
    }

    private static List<ChatFormatting> decodeStyle(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        int store = Math.max(0, Math.min(MAX_STYLE, size));

        List<ChatFormatting> out = new ArrayList<>(store);
        for (int i = 0; i < size; i++) {
            ChatFormatting formatting = decodeFormatting(buf);
            if (i < store) {
                out.add(formatting);
            }
        }

        return sanitizeList(out);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TextOutput> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeUtf(value.text(), MAX_TEXT);
                        encodeStyle(buf, value.style());
                        buf.writeBoolean(value.overlay());
                    },
                    buf -> new TextOutput(
                            buf.readUtf(MAX_TEXT),
                            decodeStyle(buf),
                            buf.readBoolean()
                    )
            );

    public static final ParamTypeDef<OutputParam> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return List.of(new Output.Text(List.of(new Output.Message(text, style, overlay))));
    }

    @Override
    public @NotNull DataResult<TextOutput> validate() {
        if (style.size() > MAX_STYLE) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.STYLE + "' may not exceed " + MAX_STYLE + " entries");
        }

        if (text.length() > MAX_TEXT) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.TEXT + "' may not exceed " + MAX_TEXT + " chars");
        }

        return SelfValidating.ok(this);
    }

    private static <T> List<T> sanitizeList(@Nullable List<T> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T value : in) {
            if (value != null) safe.add(value);
        }

        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}