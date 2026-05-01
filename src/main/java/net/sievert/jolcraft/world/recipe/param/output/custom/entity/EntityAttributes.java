package net.sievert.jolcraft.world.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import org.jetbrains.annotations.NotNull;

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
    ) {
        public static final Codec<Entry> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        RegistryFixedCodec.create(Registries.ATTRIBUTE)
                                .fieldOf("attribute")
                                .forGetter(Entry::attribute),

                        Codec.DOUBLE
                                .fieldOf("value")
                                .forGetter(Entry::value)
                ).apply(instance, Entry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE), Entry::attribute,
                        ByteBufCodecs.DOUBLE, Entry::value,
                        Entry::new
                );
    }

    private static final Codec<List<Entry>> ENTRY_CODEC = Entry.CODEC.listOf();

    public static final Codec<EntityAttributes> CODEC =
            ENTRY_CODEC.flatXmap(
                    entries -> new EntityAttributes(entries).validate(),
                    attributes -> DataResult.success(attributes.entries())
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<Entry>> ENTRY_LIST_STREAM_CODEC =
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityAttributes> STREAM_CODEC =
            StreamCodec.composite(
                    ENTRY_LIST_STREAM_CODEC, EntityAttributes::entries,
                    EntityAttributes::new
            );

    public EntityAttributes {
        entries = List.copyOf(entries);
    }

    public @NotNull Map<ResourceLocation, Double> toRaw() {
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