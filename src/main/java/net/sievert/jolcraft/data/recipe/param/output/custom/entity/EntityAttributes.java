package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record EntityAttributes(
        @NotNull List<Entry> entries
) implements SelfValidating<EntityAttributes>, RegistryIntrospectionSource {

    public static final EntityAttributes EMPTY = new EntityAttributes(List.of());

    public record Entry(
            @NotNull Holder<Attribute> attribute,
            double value
    ) {}

    private static final Codec<Map<ResourceLocation, Double>> RAW_CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE);

    public static final Codec<EntityAttributes> CODEC = RAW_CODEC.flatXmap(
            EntityAttributes::fromRaw,
            attributes -> DataResult.success(attributes.toRaw())
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Entry> ENTRY_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE), Entry::attribute,
                    ByteBufCodecs.DOUBLE, Entry::value,
                    Entry::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<Entry>> ENTRY_LIST_STREAM_CODEC =
            ENTRY_STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityAttributes> STREAM_CODEC =
            StreamCodec.composite(
                    ENTRY_LIST_STREAM_CODEC, EntityAttributes::entries,
                    EntityAttributes::new
            );

    public EntityAttributes {
        entries = List.copyOf(entries);
    }

    private static @NotNull DataResult<EntityAttributes> fromRaw(
            @NotNull Map<ResourceLocation, Double> raw
    ) {
        List<Entry> out = new ArrayList<>(raw.size());

        for (Map.Entry<ResourceLocation, Double> rawEntry : raw.entrySet()) {
            ResourceLocation id = rawEntry.getKey();
            double value = rawEntry.getValue();

            if (Double.isNaN(value) || Double.isInfinite(value)) {
                final ResourceLocation badId = id;
                return DataResult.error(() -> "attributes." + badId + " must be finite");
            }

            if (!BuiltInRegistries.ATTRIBUTE.containsKey(id)) {
                final ResourceLocation missingId = id;
                return DataResult.error(() -> "unknown attribute: " + missingId);
            }

            ResourceKey<Attribute> key = ResourceKey.create(Registries.ATTRIBUTE, id);
            Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE.getOrThrow(key);
            out.add(new Entry(holder, value));
        }

        return new EntityAttributes(out).validate();
    }

    private @NotNull Map<ResourceLocation, Double> toRaw() {
        Map<ResourceLocation, Double> out = new LinkedHashMap<>();

        for (Entry entry : entries) {
            Optional<ResourceKey<Attribute>> keyOpt = entry.attribute().unwrapKey();
            if (keyOpt.isEmpty()) {
                throw new IllegalStateException("Cannot encode attribute override without registry key");
            }
            out.put(keyOpt.get().location(), entry.value());
        }

        return out;
    }

    @Override
    public @NotNull DataResult<EntityAttributes> validate() {
        for (int i = 0; i < entries.size(); i++) {
            final int index = i;
            Entry entry = entries.get(i);

            if (entry == null) {
                return DataResult.error(() -> "attributes[" + index + "] must not be null");
            }
            if (Double.isNaN(entry.value()) || Double.isInfinite(entry.value())) {
                return DataResult.error(() -> "attributes[" + index + "].value must be finite");
            }
        }

        for (int i = 0; i < entries.size(); i++) {
            Holder<Attribute> left = entries.get(i).attribute();
            for (int j = i + 1; j < entries.size(); j++) {
                Holder<Attribute> right = entries.get(j).attribute();
                if (left.equals(right)) {
                    Optional<ResourceKey<Attribute>> keyOpt = left.unwrapKey();
                    String label = keyOpt.map(key -> key.location().toString()).orElse(left.toString());
                    return DataResult.error(() -> "duplicate attribute override: " + label);
                }
            }
        }

        return SelfValidating.ok(this);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        if (entries.isEmpty()) {
            return List.of();
        }

        return entries.stream()
                .map(entry -> RegistryIntrospection.single(Registries.ATTRIBUTE, entry.attribute()))
                .toList();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}