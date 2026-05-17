package net.sievert.jolcraft.world.recipe.param.output.custom.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldAnchor;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("deprecation")
public final class ItemProducer implements SelfValidating<ItemProducer>, RegistryIntrospectionSource {

    static final String ITEM = JolCraftDictionary.ITEM;
    static final String TAG = JolCraftDictionary.TAG;
    static final String MAP = JolCraftDictionary.MAP;

    private static final String STRUCTURE_TAG =
            JolCraftStrings.underscored(JolCraftDictionary.STRUCTURE, TAG);

    private static final String MAP_DECORATION =
            JolCraftStrings.underscored(MAP, JolCraftDictionary.DECORATION);

    private static final String DISPLAY_NAME_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.DISPLAY,
                    JolCraftDictionary.NAME,
                    JolCraftDictionary.KEY
            );

    static final Codec<Holder<Item>> ITEM_HOLDER_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Holder<Item>, T>> decode(
                com.mojang.serialization.DynamicOps<T> ops,
                T input
        ) {
            return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
                ResourceLocation id = pair.getFirst();
                T rest = pair.getSecond();

                if (!(ops instanceof RegistryOps<T> registryOps)) {
                    return DataResult.error(() ->
                            "item producer requires RegistryOps for '" + Registries.ITEM.location() + "'"
                    );
                }

                var lookupOpt = registryOps.lookupProvider.lookup(Registries.ITEM);
                if (lookupOpt.isEmpty()) {
                    return DataResult.error(() ->
                            "missing registry info for '" + Registries.ITEM.location() + "'"
                    );
                }

                ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
                var holderOpt = lookupOpt.get().getter().get(key);

                return holderOpt.<DataResult<Pair<Holder<Item>, T>>>map(itemReference ->
                        DataResult.success(Pair.of(itemReference, rest))).orElseGet(() -> DataResult.error(() -> "unknown item '" + id + "'"));

            });
        }

        @Override
        public <T> DataResult<T> encode(
                Holder<Item> input,
                com.mojang.serialization.DynamicOps<T> ops,
                T prefix
        ) {
            if (input == null) {
                return DataResult.error(() -> "item holder cannot be null");
            }

            return input.unwrapKey()
                    .map(ResourceKey::location)
                    .map(id -> ResourceLocation.CODEC.encode(id, ops, prefix))
                    .orElseGet(() -> DataResult.error(() -> "unkeyed item holder"));
        }
    };

    static final Codec<TagKey<Item>> ITEM_TAG_CODEC =
            TagKey.codec(Registries.ITEM);

    static final Codec<Holder<MapDecorationType>> MAP_DECORATION_HOLDER_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Holder<MapDecorationType>, T>> decode(
                com.mojang.serialization.DynamicOps<T> ops,
                T input
        ) {
            return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
                ResourceLocation id = pair.getFirst();
                T rest = pair.getSecond();

                if (!(ops instanceof RegistryOps<T> registryOps)) {
                    return DataResult.error(() ->
                            "map decoration decode requires RegistryOps for '" +
                                    Registries.MAP_DECORATION_TYPE.location() + "'"
                    );
                }

                var lookupOpt = registryOps.lookupProvider.lookup(Registries.MAP_DECORATION_TYPE);
                if (lookupOpt.isEmpty()) {
                    return DataResult.error(() ->
                            "missing registry info for '" +
                                    Registries.MAP_DECORATION_TYPE.location() + "'"
                    );
                }

                ResourceKey<MapDecorationType> key =
                        ResourceKey.create(Registries.MAP_DECORATION_TYPE, id);

                var holderOpt = lookupOpt.get().getter().get(key);
                return holderOpt.<DataResult<Pair<Holder<MapDecorationType>, T>>>map(mapDecorationTypeReference ->
                        DataResult.success(Pair.of(mapDecorationTypeReference, rest))).orElseGet(() -> DataResult.error(() -> "unknown map decoration type '" + id + "'"));

            });
        }

        @Override
        public <T> DataResult<T> encode(
                Holder<MapDecorationType> input,
                com.mojang.serialization.DynamicOps<T> ops,
                T prefix
        ) {
            if (input == null) {
                return DataResult.error(() -> "map decoration holder cannot be null");
            }

            return input.unwrapKey()
                    .map(ResourceKey::location)
                    .map(id -> ResourceLocation.CODEC.encode(id, ops, prefix))
                    .orElseGet(() -> DataResult.error(() -> "unkeyed map decoration holder"));
        }
    };

    public record MapData(
            @NotNull TagKey<Structure> structureTag,
            @NotNull Holder<MapDecorationType> decoration,
            @NotNull String displayNameKey
    ) {
        public static final Codec<MapData> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        TagKey.codec(Registries.STRUCTURE)
                                .fieldOf(STRUCTURE_TAG)
                                .forGetter(MapData::structureTag),

                        MAP_DECORATION_HOLDER_CODEC
                                .fieldOf(MAP_DECORATION)
                                .forGetter(MapData::decoration),

                        Codec.STRING
                                .fieldOf(DISPLAY_NAME_KEY)
                                .forGetter(MapData::displayNameKey)
                ).apply(instance, MapData::new));

        public MapData {
            Objects.requireNonNull(structureTag, STRUCTURE_TAG);
            Objects.requireNonNull(decoration, MAP_DECORATION);
            Objects.requireNonNull(displayNameKey, DISPLAY_NAME_KEY);

            if (displayNameKey.isBlank()) {
                throw new IllegalArgumentException(DISPLAY_NAME_KEY + " cannot be blank");
            }
        }
    }

    private sealed interface Target permits ItemTarget, TagTarget, MapTarget {}

    private record ItemTarget(@NotNull Holder<Item> item) implements Target {
        private ItemTarget {
            Objects.requireNonNull(item, JolCraftParameterIds.ITEM);
        }
    }

    private record TagTarget(@NotNull TagKey<Item> tag) implements Target {
        private TagTarget {
            Objects.requireNonNull(tag, JolCraftParameterIds.TAG);
        }
    }

    private record MapTarget(@NotNull MapData data) implements Target {
        private MapTarget {
            Objects.requireNonNull(data, MAP);
        }
    }

    private final @NotNull Target target;

    private ItemProducer(@NotNull Target target) {
        this.target = Objects.requireNonNull(target, JolCraftParameterIds.TARGET);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return switch (target) {
            case ItemTarget(Holder<Item> item) ->
                    List.of(RegistryIntrospection.single(Registries.ITEM, item));

            case TagTarget(TagKey<Item> tag) ->
                    List.of(RegistryIntrospection.singleTag(Registries.ITEM, tag));

            case MapTarget(MapData data) ->
                    List.of(
                            RegistryIntrospection.singleTag(Registries.STRUCTURE, data.structureTag()),
                            RegistryIntrospection.single(Registries.MAP_DECORATION_TYPE, data.decoration())
                    );
        };
    }

    private record RawCodecData(
            Optional<Holder<Item>> item,
            Optional<TagKey<Item>> tag,
            Optional<MapData> map
    ) {
        private RawCodecData {
            item = item != null ? item : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
            map = map != null ? map : Optional.empty();
        }
    }

    private static final Codec<RawCodecData> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ITEM_HOLDER_CODEC.optionalFieldOf(ITEM).forGetter(RawCodecData::item),
                    ITEM_TAG_CODEC.optionalFieldOf(TAG).forGetter(RawCodecData::tag),
                    MapData.CODEC.optionalFieldOf(MAP).forGetter(RawCodecData::map)
            ).apply(instance, RawCodecData::new));

    public static final Codec<ItemProducer> CODEC =
            ParamCodecContract.create(
                    RAW_CODEC,
                    ItemProducer::fromRaw,
                    ItemProducer::toRaw
            );

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static @NotNull DataResult<ItemProducer> fromSelection(
            @NotNull Optional<Holder<Item>> item,
            @NotNull Optional<TagKey<Item>> tag,
            @NotNull Optional<MapData> map
    ) {
        return fromRaw(new RawCodecData(item, tag, map));
    }

    private static int selectionCount(@NotNull RawCodecData raw) {
        int count = 0;
        if (raw.item().isPresent()) count++;
        if (raw.tag().isPresent()) count++;
        if (raw.map().isPresent()) count++;
        return count;
    }

    private static @NotNull DataResult<ItemProducer> fromRaw(@NotNull RawCodecData raw) {
        int familyCount = selectionCount(raw);

        if (familyCount == 0) {
            return DataResult.error(() ->
                    "item producer requires exactly one family: '" + ITEM + "', '" + TAG + "', or '" + MAP + "'"
            );
        }

        if (familyCount > 1) {
            return DataResult.error(() ->
                    "item producer has ambiguous families; provide exactly one of '" + ITEM + "', '" + TAG + "', or '" + MAP + "'"
            );
        }

        if (raw.item().isPresent()) {
            return DataResult.success(new ItemProducer(new ItemTarget(raw.item().orElseThrow())));
        }

        if (raw.tag().isPresent()) {
            return DataResult.success(new ItemProducer(new TagTarget(raw.tag().orElseThrow())));
        }

        return DataResult.success(new ItemProducer(new MapTarget(raw.map().orElseThrow())));
    }

    private static @NotNull RawCodecData toRaw(@NotNull ItemProducer producer) {
        return switch (producer.target) {
            case ItemTarget(Holder<Item> item) ->
                    new RawCodecData(Optional.of(item), Optional.empty(), Optional.empty());

            case TagTarget(TagKey<Item> tag) ->
                    new RawCodecData(Optional.empty(), Optional.of(tag), Optional.empty());

            case MapTarget(MapData data) ->
                    new RawCodecData(Optional.empty(), Optional.empty(), Optional.of(data));
        };
    }

    private static final byte KIND_ITEM = 0;
    private static final byte KIND_TAG = 1;
    private static final byte KIND_MAP = 2;

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> ITEM_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ITEM);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MapDecorationType>> MAP_DECO_STREAM =
            ByteBufCodecs.holderRegistry(Registries.MAP_DECORATION_TYPE);

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Item>> ITEM_TAG_STREAM =
            ByteBufCodecs.fromCodecWithRegistries(TagKey.codec(Registries.ITEM));

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Structure>> STRUCTURE_TAG_STREAM =
            ByteBufCodecs.fromCodecWithRegistries(TagKey.codec(Registries.STRUCTURE));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemProducer> STREAM_CODEC =
            StreamCodec.of(
                    (buf, producer) -> {
                        switch (producer.target) {
                            case ItemTarget(Holder<Item> item) -> {
                                buf.writeByte(KIND_ITEM);
                                ITEM_HOLDER_STREAM.encode(buf, item);
                            }
                            case TagTarget(TagKey<Item> tag) -> {
                                buf.writeByte(KIND_TAG);
                                ITEM_TAG_STREAM.encode(buf, tag);
                            }
                            case MapTarget(MapData data) -> {
                                buf.writeByte(KIND_MAP);
                                STRUCTURE_TAG_STREAM.encode(buf, data.structureTag());
                                MAP_DECO_STREAM.encode(buf, data.decoration());
                                buf.writeUtf(data.displayNameKey());
                            }
                        }
                    },
                    buf -> {
                        byte kind = buf.readByte();
                        return switch (kind) {
                            case KIND_ITEM -> new ItemProducer(new ItemTarget(ITEM_HOLDER_STREAM.decode(buf)));
                            case KIND_TAG -> new ItemProducer(new TagTarget(ITEM_TAG_STREAM.decode(buf)));
                            case KIND_MAP -> new ItemProducer(new MapTarget(new MapData(
                                    STRUCTURE_TAG_STREAM.decode(buf),
                                    MAP_DECO_STREAM.decode(buf),
                                    buf.readUtf()
                            )));
                            default -> throw new IllegalArgumentException("unknown item producer kind: " + kind);
                        };
                    }
            );

    @Override
    public @NotNull DataResult<ItemProducer> validate() {
        return SelfValidating.ok(this);
    }

    public @NotNull ItemStack create(@NotNull WorldContext ctx) {
        return switch (target) {
            case ItemTarget(Holder<Item> item) ->
                    new ItemStack(item.value());

            case TagTarget(TagKey<Item> tag) ->
                    createFromTag(ctx, tag);

            case MapTarget(MapData data) ->
                    createTreasureMap(ctx, data.structureTag(), data.decoration(), data.displayNameKey());
        };
    }

    private static @NotNull ItemStack createFromTag(@NotNull WorldContext ctx, @NotNull TagKey<Item> tag) {
        RegistryAccess access = ctx.level().registryAccess();
        RandomSource random = ctx.random();

        var registryLookup = access.lookup(Registries.ITEM);
        if (registryLookup.isEmpty()) return ItemStack.EMPTY;

        var namedSet = registryLookup.get().get(tag);
        if (namedSet.isEmpty()) return ItemStack.EMPTY;

        var holders = namedSet.get();
        int size = holders.size();
        if (size <= 0) return ItemStack.EMPTY;

        Holder<Item> chosen = holders.get(random.nextInt(size));
        return new ItemStack(chosen.value());
    }

    private static @NotNull ItemStack createTreasureMap(
            @NotNull WorldContext ctx,
            @NotNull TagKey<Structure> structureTag,
            @NotNull Holder<MapDecorationType> decoration,
            @NotNull String displayNameKey
    ) {
        ServerLevel level = ctx.level();

        BlockPos origin = WorldAnchor.resolve(ctx);
        if (origin == null) {
            return ItemStack.EMPTY;
        }

        BlockPos found = level.findNearestMapStructure(structureTag, origin, 100, true);
        if (found == null) {
            return ItemStack.EMPTY;
        }

        ItemStack map = MapItem.create(level, found.getX(), found.getZ(), (byte) 2, true, true);
        map.set(DataComponents.CUSTOM_NAME, Component.translatable(displayNameKey));
        MapItemSavedData.addTargetDecoration(map, found, JolCraftDictionary.MAP, decoration);
        return map;
    }

    public static @NotNull ItemProducer item(@NotNull ItemLike item) {
        return new ItemProducer(new ItemTarget(item.asItem().builtInRegistryHolder()));
    }

    public static @NotNull ItemProducer holder(@NotNull Holder<Item> item) {
        return new ItemProducer(new ItemTarget(item));
    }

    public static @NotNull ItemProducer tag(@NotNull TagKey<Item> tag) {
        return new ItemProducer(new TagTarget(tag));
    }

    public static @NotNull ItemProducer map(
            @NotNull TagKey<Structure> structureTag,
            @NotNull Holder<MapDecorationType> decoration,
            @NotNull String displayNameKey
    ) {
        return new ItemProducer(new MapTarget(new MapData(structureTag, decoration, displayNameKey)));
    }

    public boolean isItemSelection() {
        return target instanceof ItemTarget;
    }

    public boolean isTagSelection() {
        return target instanceof TagTarget;
    }

    public boolean isMapSelection() {
        return target instanceof MapTarget;
    }

    public @NotNull Optional<Holder<Item>> itemHolderOpt() {
        return target instanceof ItemTarget(Holder<Item> item) ? Optional.of(item) : Optional.empty();
    }

    public @NotNull Optional<TagKey<Item>> tagOpt() {
        return target instanceof TagTarget(TagKey<Item> tag) ? Optional.of(tag) : Optional.empty();
    }

    public @NotNull Optional<MapData> mapDataOpt() {
        return target instanceof MapTarget(MapData data) ? Optional.of(data) : Optional.empty();
    }

    public @NotNull Optional<String> mapFileNameTokenOpt() {
        if (!(target instanceof MapTarget(MapData data))) {
            return Optional.empty();
        }

        String key = data.displayNameKey().trim();
        if (key.isEmpty()) {
            return Optional.empty();
        }

        int lastDot = key.lastIndexOf('.');
        String leaf = (lastDot >= 0 && lastDot + 1 < key.length())
                ? key.substring(lastDot + 1)
                : key;

        leaf = leaf.trim();
        if (leaf.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(JolCraftStrings.underscored(leaf, JolCraftDictionary.MAP));
    }
}