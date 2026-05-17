package net.sievert.jolcraft.world.recipe.param.input.custom.entity.selector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionGate;
import net.sievert.jolcraft.world.recipe.param.condition.ConditionalMatcher;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record EntitySelector(
        Conditions conditions,
        List<Entry> entries
) implements SelfValidating<EntitySelector>, ConditionGate, ConditionalMatcher<Entity>, RegistryIntrospectionSource {

    public record Entry(Conditions conditions, EntityIngredient ingredient)
            implements SelfValidating<Entry>, RegistryIntrospectionSource {

        public Entry {
            conditions = conditions != null ? conditions : Conditions.EMPTY;
            if (ingredient == null) {
                throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.PARAMETER + "'");
            }
        }

        private static final Codec<Entry> RAW_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(Entry::conditions),
                        EntityIngredient.CODEC.fieldOf(JolCraftParameterIds.PARAMETER).forGetter(Entry::ingredient)
                ).apply(inst, Entry::new));

        public static final Codec<Entry> CODEC = ParamCodecs.validated(RAW_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            Conditions.STREAM_CODEC.encode(buf, v.conditions());
                            EntityIngredient.STREAM_CODEC.encode(buf, v.ingredient());
                        },
                        buf -> new Entry(
                                Conditions.STREAM_CODEC.decode(buf),
                                EntityIngredient.STREAM_CODEC.decode(buf)
                        )
                );

        @Override
        public @NotNull DataResult<Entry> validate() {
            DataResult<Conditions> cv = conditions.validate();
            if (cv.error().isPresent()) {
                return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + cv.error().map(DataResult.Error::message).orElse(""));
            }

            DataResult<EntityIngredient> iv = ingredient.validate();
            if (iv.error().isPresent()) {
                return SelfValidating.invalid(JolCraftParameterIds.PARAMETER + " invalid: " + iv.error().map(DataResult.Error::message).orElse(""));
            }

            return SelfValidating.ok(this);
        }

        @Override
        public @NotNull List<RegistryIntrospection> introspections() {
            if (conditions != Conditions.EMPTY) return List.of();
            return ingredient.asList();
        }
    }

    private static final Codec<EntitySelector> RAW_CODEC =
            Codec.either(
                    EntityIngredient.CODEC,
                    RecordCodecBuilder.<EntitySelector>create(inst -> inst.group(
                            Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY).forGetter(EntitySelector::conditions),
                            Entry.CODEC.listOf().optionalFieldOf(JolCraftParameterIds.ENTRIES, List.of()).forGetter(EntitySelector::entries)
                    ).apply(inst, EntitySelector::new))
            ).xmap(
                    either -> either.map(
                            ing -> new EntitySelector(Conditions.EMPTY, List.of(new Entry(Conditions.EMPTY, ing))),
                            full -> full
                    ),
                    sel -> {
                        boolean structured = sel.conditions() != Conditions.EMPTY || hasAnyEntryConditions(sel.entries());
                        if (structured) return Either.right(sel);

                        if (sel.entries().size() == 1) {
                            return Either.left(sel.entries().getFirst().ingredient());
                        }

                        ArrayList<EntityIngredient.Target> targets = new ArrayList<>();
                        for (Entry e : sel.entries()) {
                            targets.addAll(e.ingredient().targets());
                        }
                        return Either.left(new EntityIngredient(targets));
                    }
            );

    public static final Codec<EntitySelector> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_ENTRIES_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySelector> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {
                        Conditions.STREAM_CODEC.encode(buf, v.conditions());
                        buf.writeVarInt(v.entries().size());
                        for (Entry e : v.entries()) {
                            Entry.STREAM_CODEC.encode(buf, e);
                        }
                    },
                    buf -> {
                        Conditions cond = Conditions.STREAM_CODEC.decode(buf);
                        int size = buf.readVarInt();

                        if (size < 0) throw new IllegalArgumentException("negative entry size: " + size);
                        if (size == 0) return new EntitySelector(cond, List.of());
                        if (size > MAX_ENTRIES_STREAM) {
                            throw new IllegalArgumentException("entry size exceeds max " + MAX_ENTRIES_STREAM + " (got " + size + ")");
                        }

                        ArrayList<Entry> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Entry.STREAM_CODEC.decode(buf));
                        }
                        return new EntitySelector(cond, list);
                    }
            );

    public EntitySelector {
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        entries = sanitizeList(entries);
    }

    @Override
    public @NotNull Conditions conditions() {
        return conditions;
    }

    @Override
    public @NotNull DataResult<EntitySelector> validate() {
        DataResult<Conditions> cv = conditions.validate();
        if (cv.error().isPresent()) {
            return SelfValidating.invalid(JolCraftParameterIds.CONDITIONS + " invalid: " + cv.error().map(DataResult.Error::message).orElse(""));
        }

        if (entries.isEmpty()) {
            return SelfValidating.invalid(JolCraftDictionary.SELECTOR + " must not be empty");
        }

        for (int i = 0; i < entries.size(); i++) {
            DataResult<Entry> ev = entries.get(i).validate();
            if (ev.error().isPresent()) {
                return SelfValidating.invalid(JolCraftParameterIds.ENTRIES + "[" + i + "] invalid: " + ev.error().map(DataResult.Error::message).orElse(""));
            }
        }

        return SelfValidating.ok(this);
    }

    @Override
    public boolean matches(@NotNull WorldContext ctx, @NotNull Entity entity) {
        if (!conditions.test(ctx)) return false;
        if (entries.isEmpty()) return false;

        for (Entry e : entries) {
            if (!e.conditions().test(ctx)) continue;
            if (e.ingredient().matches(entity)) return true;
        }

        return false;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        if (conditions != Conditions.EMPTY) return List.of();
        if (entries.isEmpty()) return List.of();
        return RegistryIntrospectionSource.mergeByRegistry(entries);
    }

    private static boolean hasAnyEntryConditions(List<Entry> entries) {
        for (Entry e : entries) {
            if (e != null && e.conditions() != Conditions.EMPTY) return true;
        }
        return false;
    }

    private static <T> @NotNull List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) {
            if (t != null) safe.add(t);
        }
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}