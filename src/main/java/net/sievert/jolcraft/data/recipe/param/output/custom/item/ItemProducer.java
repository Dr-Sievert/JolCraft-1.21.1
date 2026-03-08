package net.sievert.jolcraft.data.recipe.param.output.custom.item;

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
import net.minecraft.resources.RegistryFixedCodec;
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
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public final class ItemProducer implements SelfValidating<ItemProducer>, RegistryIntrospectionSource {

    private static final ResourceLocation INVALID_ID = JolCraft.location(JolCraftDictionary.INVALID);

    public static final ItemProducer EMPTY = new ItemProducer(new InvalidTarget(INVALID_ID));

    // ---------------------------------------------------------------------
    // LOCAL STRINGS
    // ---------------------------------------------------------------------

    private static final String TYPE = JolCraftParameterIds.TYPE;
    private static final String ITEM = JolCraftDictionary.ITEM;
    private static final String TAG = JolCraftDictionary.TAG;
    private static final String MAP = JolCraftDictionary.MAP;
    private static final String STRUCTURE_TAG = JolCraftStrings.underscored(JolCraftDictionary.STRUCTURE, TAG);
    private static final String MAP_DECORATION = JolCraftStrings.underscored(MAP, JolCraftDictionary.DECORATION);
    private static final String DISPLAY_NAME_KEY = JolCraftStrings.underscored(JolCraftDictionary.DISPLAY, JolCraftDictionary.NAME, JolCraftDictionary.KEY);
    private static final String INVALID = JolCraftDictionary.INVALID;

    // ---------------------------------------------------------------------
    // TARGETS
    // ---------------------------------------------------------------------

    private sealed interface Target permits ItemTarget, TagTarget, MapTarget, InvalidTarget {}

    private record ItemTarget(Holder<Item> item) implements Target {}
    private record TagTarget(TagKey<Item> tag) implements Target {}
    private record MapTarget(TagKey<Structure> structureTag, Holder<MapDecorationType> decoration, String displayNameKey) implements Target {}
    private record InvalidTarget(ResourceLocation reasonId) implements Target {}

    private final Target target;

    private ItemProducer(Target target) {
        this.target = target != null ? target : new InvalidTarget(INVALID_ID);
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return switch (target) {
            case ItemTarget(Holder<Item> item) ->
                    (item != null)
                            ? List.of(RegistryIntrospection.single(Registries.ITEM, item))
                            : List.of(RegistryIntrospection.mixed(Registries.ITEM, 0, false));

            case TagTarget(TagKey<Item> tag) ->
                    (tag != null)
                            ? List.of(RegistryIntrospection.singleTag(Registries.ITEM, tag))
                            : List.of(RegistryIntrospection.mixed(Registries.ITEM, 0, false));

            case MapTarget(TagKey<Structure> structureTag, Holder<MapDecorationType> deco, String ignoredKey) -> {
                RegistryIntrospection a =
                        (structureTag != null)
                                ? RegistryIntrospection.singleTag(Registries.STRUCTURE, structureTag)
                                : RegistryIntrospection.mixed(Registries.STRUCTURE, 0, false);

                RegistryIntrospection b =
                        (deco != null)
                                ? RegistryIntrospection.single(Registries.MAP_DECORATION_TYPE, deco)
                                : RegistryIntrospection.mixed(Registries.MAP_DECORATION_TYPE, 0, false);

                yield List.of(a, b);
            }

            case InvalidTarget ignored -> List.of();
        };
    }

    // ---------------------------------------------------------------------
    // CODEC / STREAM_CODEC
    // ---------------------------------------------------------------------

    private static final Codec<Holder<Item>> ITEM_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.ITEM);

    private static final Codec<TagKey<Item>> ITEM_TAG_CODEC =
            TagKey.codec(Registries.ITEM);

    private static final Codec<TagKey<Structure>> STRUCTURE_TAG_CODEC =
            TagKey.codec(Registries.STRUCTURE);

    private static final Codec<Holder<MapDecorationType>> MAP_DECO_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.MAP_DECORATION_TYPE);

    private record CodecData(
            String type,
            Holder<Item> item,
            TagKey<Item> tag,
            TagKey<Structure> structureTag,
            Holder<MapDecorationType> decoration,
            String displayNameKey,
            ResourceLocation invalid
    ) {}

    private static final Codec<CodecData> RAW_CODEC =
            RecordCodecBuilder.create(i -> i.group(
                    Codec.STRING
                            .fieldOf(TYPE)
                            .forGetter(CodecData::type),

                    ITEM_HOLDER_CODEC
                            .optionalFieldOf(ITEM)
                            .forGetter(d -> Optional.ofNullable(d.item())),

                    ITEM_TAG_CODEC
                            .optionalFieldOf(TAG)
                            .forGetter(d -> Optional.ofNullable(d.tag())),

                    STRUCTURE_TAG_CODEC
                            .optionalFieldOf(STRUCTURE_TAG)
                            .forGetter(d -> Optional.ofNullable(d.structureTag())),

                    MAP_DECO_HOLDER_CODEC
                            .optionalFieldOf(MAP_DECORATION)
                            .forGetter(d -> Optional.ofNullable(d.decoration())),

                    Codec.STRING
                            .optionalFieldOf(DISPLAY_NAME_KEY, "")
                            .forGetter(CodecData::displayNameKey),

                    ResourceLocation.CODEC
                            .optionalFieldOf(INVALID)
                            .forGetter(d -> Optional.ofNullable(d.invalid()))
            ).apply(i, (type, item, tag, structureTag, decoration, displayNameKey, invalid) ->
                    new CodecData(
                            type,
                            item.orElse(null),
                            tag.orElse(null),
                            structureTag.orElse(null),
                            decoration.orElse(null),
                            displayNameKey,
                            invalid.orElse(null)
                    )
            ));

    public static final Codec<ItemProducer> CODEC =
            RAW_CODEC.comapFlatMap(ItemProducer::fromCodecData, ItemProducer::toCodecData);

    private static DataResult<ItemProducer> fromCodecData(CodecData d) {
        if (d == null || d.type == null) return DataResult.success(EMPTY);

        return switch (d.type) {
            case ITEM -> (d.item != null)
                    ? DataResult.success(item(d.item.value()))
                    : DataResult.error(() -> "item producer missing '" + ITEM + "'");
            case TAG -> (d.tag != null)
                    ? DataResult.success(tag(d.tag))
                    : DataResult.error(() -> "tag producer missing '" + TAG + "'");
            case MAP -> {
                if (d.structureTag == null)
                    yield DataResult.error(() -> "map producer missing '" + STRUCTURE_TAG + "'");
                if (d.decoration == null)
                    yield DataResult.error(() -> "map producer missing '" + MAP_DECORATION + "'");
                if (d.displayNameKey == null || d.displayNameKey.isBlank())
                    yield DataResult.error(() -> "map producer missing '" + DISPLAY_NAME_KEY + "'");
                yield DataResult.success(map(d.structureTag, d.decoration, d.displayNameKey));
            }
            case INVALID -> DataResult.success(invalid(d.invalid));
            default -> DataResult.success(EMPTY);
        };
    }

    private static CodecData toCodecData(ItemProducer p) {
        if (p == null) {
            return new CodecData(INVALID, null, null, null, null, "", INVALID_ID);
        }

        return switch (p.target) {
            case ItemTarget(Holder<Item> item) ->
                    new CodecData(ITEM, item, null, null, null, "", null);

            case TagTarget(TagKey<Item> tag) ->
                    new CodecData(TAG, null, tag, null, null, "", null);

            case MapTarget(TagKey<Structure> structureTag, Holder<MapDecorationType> deco, String key) ->
                    new CodecData(MAP, null, null, structureTag, deco, key, null);

            case InvalidTarget(ResourceLocation id) ->
                    new CodecData(INVALID, null, null, null, null, "", id);
        };
    }

    private static final byte KIND_ITEM = 0;
    private static final byte KIND_TAG = 1;
    private static final byte KIND_MAP = 2;
    private static final byte KIND_INVALID = 3;

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> ITEM_HOLDER_STREAM = ByteBufCodecs.holderRegistry(Registries.ITEM);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MapDecorationType>> MAP_DECO_STREAM =
            ByteBufCodecs.holderRegistry(Registries.MAP_DECORATION_TYPE);

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Item>> ITEM_TAG_STREAM =
            ByteBufCodecs.fromCodecWithRegistries(TagKey.codec(Registries.ITEM));

    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Structure>> STRUCTURE_TAG_STREAM =
            ByteBufCodecs.fromCodecWithRegistries(TagKey.codec(Registries.STRUCTURE));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemProducer> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        switch (p.target) {
                            case ItemTarget(Holder<Item> item) -> {
                                buf.writeByte(KIND_ITEM);
                                ITEM_HOLDER_STREAM.encode(buf, item);
                            }
                            case TagTarget(TagKey<Item> tag) -> {
                                buf.writeByte(KIND_TAG);
                                ITEM_TAG_STREAM.encode(buf, tag);
                            }
                            case MapTarget(TagKey<Structure> structureTag, Holder<MapDecorationType> deco, String key) -> {
                                buf.writeByte(KIND_MAP);
                                STRUCTURE_TAG_STREAM.encode(buf, structureTag);
                                MAP_DECO_STREAM.encode(buf, deco);
                                buf.writeUtf(key != null ? key : "");
                            }
                            case InvalidTarget(ResourceLocation id) -> {
                                buf.writeByte(KIND_INVALID);
                                buf.writeResourceLocation(id != null ? id : INVALID_ID);
                            }
                        }
                    },
                    buf -> {
                        byte kind = buf.readByte();
                        return switch (kind) {
                            case KIND_ITEM -> item(ITEM_HOLDER_STREAM.decode(buf).value());
                            case KIND_TAG -> tag(ITEM_TAG_STREAM.decode(buf));
                            case KIND_MAP -> {
                                TagKey<Structure> st = STRUCTURE_TAG_STREAM.decode(buf);
                                Holder<MapDecorationType> deco = MAP_DECO_STREAM.decode(buf);
                                String key = buf.readUtf();
                                yield map(st, deco, key);
                            }
                            case KIND_INVALID -> invalid(buf.readResourceLocation());
                            default -> EMPTY;
                        };
                    }
            );

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<ItemProducer> validate() {
        return switch (target) {
            case InvalidTarget(ResourceLocation id) ->
                    DataResult.error(() -> "invalid producer (" + id + ")");
            case ItemTarget(Holder<Item> item) ->
                    item == null
                            ? DataResult.error(() -> "item cannot be null")
                            : DataResult.success(this);
            case TagTarget(TagKey<Item> tag) ->
                    tag == null
                            ? DataResult.error(() -> "tag cannot be null")
                            : DataResult.success(this);
            case MapTarget(TagKey<Structure> tag,
                           Holder<MapDecorationType> deco,
                           String name) ->
                    (tag == null || deco == null || name == null || name.isBlank())
                            ? DataResult.error(() -> "invalid map producer")
                            : DataResult.success(this);
        };
    }

    // ---------------------------------------------------------------------
    // RUNTIME CREATION
    // ---------------------------------------------------------------------

    public @NotNull ItemStack create(@NotNull WorldContext ctx) {
        return switch (target) {

            case ItemTarget(Holder<Item> item) ->
                    item == null ? ItemStack.EMPTY : new ItemStack(item.value());

            case TagTarget(TagKey<Item> tag) ->
                    createFromTag(ctx, tag);

            case MapTarget(TagKey<Structure> structureTag,
                           Holder<MapDecorationType> decoration,
                           String nameKey) ->
                    createTreasureMap(ctx, structureTag, decoration, nameKey);

            case InvalidTarget ignored ->
                    ItemStack.EMPTY;
        };
    }

    private static ItemStack createFromTag(@NotNull WorldContext ctx, @NotNull TagKey<Item> tag) {
        RegistryAccess access = ctx.level().registryAccess();
        RandomSource random = ctx.random();

        var regOpt = access.lookup(Registries.ITEM);
        if (regOpt.isEmpty()) return ItemStack.EMPTY;

        var namedOpt = regOpt.get().get(tag);
        if (namedOpt.isEmpty()) return ItemStack.EMPTY;

        var named = namedOpt.get();
        int size = named.size();
        if (size <= 0) return ItemStack.EMPTY;

        Holder<Item> chosen = named.get(random.nextInt(size));
        return new ItemStack(chosen.value());
    }

    private static ItemStack createTreasureMap(
            @NotNull WorldContext ctx,
            @NotNull TagKey<Structure> structureTag,
            @NotNull Holder<MapDecorationType> decoration,
            @NotNull String displayNameKey
    ) {
        ServerLevel server = ctx.level();

        BlockPos origin = WorldAnchor.resolve(ctx);
        if (origin == null) {
            return ItemStack.EMPTY;
        }

        BlockPos found = server.findNearestMapStructure(structureTag, origin, 100, true);
        if (found == null) {
            return ItemStack.EMPTY;
        }

        ItemStack map =
                MapItem.create(server, found.getX(), found.getZ(), (byte) 2, true, true);

        map.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable(displayNameKey)
        );

        MapItemSavedData.addTargetDecoration(
                map,
                found,
                JolCraftDictionary.MAP,
                decoration
        );

        return map;
    }

    // ---------------------------------------------------------------------
    // FACTORIES
    // ---------------------------------------------------------------------

    public static ItemProducer item(ItemLike item) {
        if (item == null) return EMPTY;

        return new ItemProducer(
                new ItemTarget(item.asItem().builtInRegistryHolder())
        );
    }

    public static ItemProducer tag(TagKey<Item> tag) {
        return tag == null ? EMPTY : new ItemProducer(new TagTarget(tag));
    }

    public static ItemProducer map(TagKey<Structure> tag, Holder<MapDecorationType> deco, String name) {
        if (tag == null || deco == null || name == null || name.isBlank()) return EMPTY;
        return new ItemProducer(new MapTarget(tag, deco, name));
    }

    public static ItemProducer invalid(ResourceLocation id) {
        return new ItemProducer(new InvalidTarget(id != null ? id : INVALID_ID));
    }

    // ---------------------------------------------------------------------
    // TYPE HELPERS
    // ---------------------------------------------------------------------

    public boolean isItemSelection() {
        return target instanceof ItemTarget || isMapSelection();
    }

    public boolean isTagSelection() {
        return target instanceof TagTarget;
    }

    public boolean isMapSelection() {
        return target instanceof MapTarget;
    }

    public Optional<Holder<Item>> itemHolderOpt() {
        if (target instanceof ItemTarget(Holder<Item> item)) {
            return Optional.ofNullable(item);
        }
        return Optional.empty();
    }

    // ---------------------------------------------------------------------
    // NAMING HELPERS
    // ---------------------------------------------------------------------

    /**
     * If this producer is a treasure-map producer, returns a stable filename token:
     * - take displayNameKey
     * - keep substring after last '.'
     * - append "_map"
     *
     * Example: "filled_map.forge" -> "forge_map"
     *
     * Fail-closed: Optional.empty() if missing/blank.
     */
    public Optional<String> mapFileNameTokenOpt() {
        if (target instanceof MapTarget(TagKey<Structure> ignoredTag,
                                        Holder<MapDecorationType> ignoredDeco,
                                        String key)) {

            if (key == null) return Optional.empty();
            String k = key.trim();
            if (k.isEmpty()) return Optional.empty();

            int lastDot = k.lastIndexOf('.');
            String leaf = (lastDot >= 0 && lastDot + 1 < k.length())
                    ? k.substring(lastDot + 1)
                    : k;

            leaf = leaf.trim();
            if (leaf.isEmpty()) return Optional.empty();

            return Optional.of(leaf + "_map");
        }
        return Optional.empty();
    }
}