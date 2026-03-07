package net.sievert.jolcraft.data.recipe.param.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.ParamDispatch;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.condition.custom.BiomeCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.ChanceCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.DimensionCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.PlayerLevelCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.TimeCondition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.WeatherCondition;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Condition type registry + dispatch plumbing (single source of truth).
 *
 * - JSON dispatch via ParamDispatch using {@link JolCraftParameterIds#TYPE}
 * - Stream dispatch is total and payload-length framed: [disc][len][payload]
 */
public final class ConditionTypes {

    private ConditionTypes() {}

    // ---------------------------------------------------------------------
    // KNOWN TYPE IDS
    // ---------------------------------------------------------------------

    public static final ResourceLocation TYPE_CHANCE = JolCraft.location(JolCraftDictionary.CHANCE);
    public static final ResourceLocation TYPE_WEATHER = JolCraft.location(JolCraftDictionary.WEATHER);
    public static final ResourceLocation TYPE_TIME = JolCraft.location(JolCraftDictionary.TIME);
    public static final ResourceLocation TYPE_DIMENSION = JolCraft.location(JolCraftDictionary.DIMENSION);
    public static final ResourceLocation TYPE_BIOME = JolCraft.location(JolCraftDictionary.BIOME);
    public static final ResourceLocation TYPE_PLAYER_LEVEL = JolCraft.location(
            JolCraftStrings.underscored(JolCraftDictionary.PLAYER, JolCraftDictionary.LEVEL)
    );

    /**
     * Internal sentinel type ids used to represent invalid states deterministically.
     */
    public static final ResourceLocation TYPE_INVALID = JolCraft.location(
            JolCraftStrings.underscored(JolCraftDictionary.INVALID, JolCraftDictionary.CONDITION)
    );
    public static final ResourceLocation TYPE_MISSING = JolCraft.location(
            JolCraftStrings.underscored(JolCraftDictionary.MISSING, JolCraftParameterIds.TYPE)
    );

    /**
     * Payload key for InvalidCondition so we don't collide with the dispatch key ("type").
     */
    public static final String KEY_UNKNOWN_TYPE =
            JolCraftStrings.underscored(JolCraftDictionary.UNKNOWN, JolCraftParameterIds.TYPE);

    // ---------------------------------------------------------------------
    // STABLE STREAM DISCRIMINATORS
    // ---------------------------------------------------------------------

    public static final byte DISC_INVALID = 0;
    public static final byte DISC_CHANCE = 1;
    public static final byte DISC_WEATHER = 2;
    public static final byte DISC_TIME = 3;
    public static final byte DISC_DIMENSION = 4;
    public static final byte DISC_BIOME = 5;
    public static final byte DISC_PLAYER_LEVEL = 6;

    // ---------------------------------------------------------------------
    // INVALID SENTINEL
    // ---------------------------------------------------------------------

    public record InvalidCondition(ResourceLocation receivedTypeId, String message) implements Condition {

        public InvalidCondition(ResourceLocation receivedTypeId) {
            this(receivedTypeId, "");
        }

        private static final int MAX_MESSAGE = 512;

        public static final Codec<InvalidCondition> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        ResourceLocation.CODEC
                                .optionalFieldOf(KEY_UNKNOWN_TYPE, TYPE_INVALID)
                                .forGetter(InvalidCondition::receivedTypeId),
                        Codec.STRING
                                .optionalFieldOf(JolCraftParameterIds.MESSAGE, "")
                                .forGetter(InvalidCondition::message)
                ).apply(instance, InvalidCondition::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InvalidCondition> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            ResourceLocation id = v.receivedTypeId();
                            buf.writeResourceLocation(id == null ? TYPE_INVALID : id);

                            String msg = v.message == null ? "" : v.message;
                            buf.writeUtf(msg, MAX_MESSAGE);
                        },
                        buf -> new InvalidCondition(buf.readResourceLocation(), buf.readUtf(MAX_MESSAGE))
                );

        @Override
        public ResourceLocation typeId() {
            return TYPE_INVALID;
        }

        @Override
        public boolean test(@NotNull WorldContext ctx) {
            return false;
        }

        @Override
        public @NotNull DataResult<Condition> validate() {
            ResourceLocation got = (receivedTypeId == null) ? TYPE_INVALID : receivedTypeId;
            String msg = (message == null || message.isEmpty()) ? "" : (": " + message);
            return SelfValidating.invalid("invalid condition (received typeId=" + got + ")" + msg);
        }
    }

    // ---------------------------------------------------------------------
    // DISPATCH TABLES
    // ---------------------------------------------------------------------

    public static final ParamDispatch.Entry<Condition> ENTRY_INVALID =
            new ParamDispatch.Entry<>(TYPE_INVALID, DISC_INVALID, InvalidCondition.CODEC, InvalidCondition.STREAM_CODEC);

    public static final ParamDispatch.Entry<Condition> ENTRY_CHANCE =
            new ParamDispatch.Entry<>(TYPE_CHANCE, DISC_CHANCE, ChanceCondition.CODEC, ChanceCondition.STREAM_CODEC);

    public static final ParamDispatch.Entry<Condition> ENTRY_WEATHER =
            new ParamDispatch.Entry<>(TYPE_WEATHER, DISC_WEATHER, WeatherCondition.CODEC, WeatherCondition.STREAM_CODEC);

    public static final ParamDispatch.Entry<Condition> ENTRY_TIME =
            new ParamDispatch.Entry<>(TYPE_TIME, DISC_TIME, TimeCondition.CODEC, TimeCondition.STREAM_CODEC);

    public static final ParamDispatch.Entry<Condition> ENTRY_DIMENSION =
            new ParamDispatch.Entry<>(TYPE_DIMENSION, DISC_DIMENSION, DimensionCondition.CODEC, DimensionCondition.STREAM_CODEC);

    public static final ParamDispatch.Entry<Condition> ENTRY_BIOME =
            new ParamDispatch.Entry<>(TYPE_BIOME, DISC_BIOME, BiomeCondition.CODEC, BiomeCondition.STREAM_CODEC);

    public static final ParamDispatch.Entry<Condition> ENTRY_PLAYER_LEVEL =
            new ParamDispatch.Entry<>(TYPE_PLAYER_LEVEL, DISC_PLAYER_LEVEL, PlayerLevelCondition.CODEC, PlayerLevelCondition.STREAM_CODEC);

    public static final Map<ResourceLocation, ParamDispatch.Entry<Condition>> BY_TYPE_ID = createTypeIdMap();
    public static final Map<Byte, ParamDispatch.Entry<Condition>> BY_DISCRIMINATOR = createDiscriminatorMap();

    private static Map<ResourceLocation, ParamDispatch.Entry<Condition>> createTypeIdMap() {
        Map<ResourceLocation, ParamDispatch.Entry<Condition>> map = new LinkedHashMap<>();
        registerTypeId(map, ENTRY_INVALID);
        registerTypeId(map, ENTRY_CHANCE);
        registerTypeId(map, ENTRY_WEATHER);
        registerTypeId(map, ENTRY_TIME);
        registerTypeId(map, ENTRY_DIMENSION);
        registerTypeId(map, ENTRY_BIOME);
        registerTypeId(map, ENTRY_PLAYER_LEVEL);
        return map;
    }

    private static Map<Byte, ParamDispatch.Entry<Condition>> createDiscriminatorMap() {
        Map<Byte, ParamDispatch.Entry<Condition>> map = new LinkedHashMap<>();
        registerDiscriminator(map, ENTRY_INVALID);
        registerDiscriminator(map, ENTRY_CHANCE);
        registerDiscriminator(map, ENTRY_WEATHER);
        registerDiscriminator(map, ENTRY_TIME);
        registerDiscriminator(map, ENTRY_DIMENSION);
        registerDiscriminator(map, ENTRY_BIOME);
        registerDiscriminator(map, ENTRY_PLAYER_LEVEL);
        return map;
    }

    private static void registerTypeId(Map<ResourceLocation, ParamDispatch.Entry<Condition>> map, ParamDispatch.Entry<Condition> e) {
        map.putIfAbsent(e.typeId(), e);
    }

    private static void registerDiscriminator(Map<Byte, ParamDispatch.Entry<Condition>> map, ParamDispatch.Entry<Condition> e) {
        map.putIfAbsent(e.discriminator(), e);
    }

    private static Condition invalidFactory(ResourceLocation receivedTypeId) {
        return new InvalidCondition(receivedTypeId);
    }

    // ---------------------------------------------------------------------
    // JSON + STREAM DISPATCH
    // ---------------------------------------------------------------------

    public static final Codec<Condition> RAW_CODEC = ParamDispatch.codec(
            JolCraftParameterIds.TYPE,
            () -> new InvalidCondition(TYPE_INVALID),
            Condition::typeId,
            BY_TYPE_ID,
            ENTRY_INVALID,
            ConditionTypes::invalidFactory,
            TYPE_MISSING,
            TYPE_INVALID
    );

    public static final Codec<Condition> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Condition> STREAM_CODEC = ParamDispatch.streamCodec(
            () -> new InvalidCondition(TYPE_INVALID),
            Condition::typeId,
            BY_TYPE_ID,
            BY_DISCRIMINATOR,
            ENTRY_INVALID,
            ConditionTypes::invalidFactory,
            TYPE_INVALID
    );
}