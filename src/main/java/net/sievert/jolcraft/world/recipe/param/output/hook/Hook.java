package net.sievert.jolcraft.world.recipe.param.output.hook;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.List;

public record Hook(ResourceLocation id, Conditions conditions) implements SelfValidating<Hook> {

    private static final Set<String> RESERVED_KEYS = Set.of(
            JolCraftParameterIds.ID,
            JolCraftParameterIds.CONDITIONS
    );

    public static final Codec<Hook> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<com.mojang.datafixers.util.Pair<Hook, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<ResourceLocation> direct = ResourceLocation.CODEC.parse(ops, input);
            if (direct.result().isPresent()) {
                return DataResult.success(com.mojang.datafixers.util.Pair.of(
                        new Hook(direct.result().orElseThrow(), Conditions.EMPTY),
                        input
                ));
            }

            DataResult<Conditions.Extracted<T>> extracted =
                    Conditions.extractInlineConditions(ops, input, RESERVED_KEYS);

            if (extracted.error().isPresent()) {
                return DataResult.error(() ->
                        extracted.error().map(DataResult.Error::message).orElse("invalid hook conditions")
                );
            }

            Conditions.Extracted<T> ex = extracted.result().orElseThrow();
            T stripped = ex.strippedInput();

            T idValue = ops.getMap(stripped).result()
                    .map(map -> map.get(ops.createString(JolCraftParameterIds.ID)))
                    .orElse(null);

            if (idValue == null) {
                return DataResult.error(() ->
                        "missing required field '" + JolCraftParameterIds.ID + "'"
                );
            }

            DataResult<ResourceLocation> id = ResourceLocation.CODEC.parse(ops, idValue);
            if (id.error().isPresent()) {
                return DataResult.error(() ->
                        id.error().map(DataResult.Error::message).orElse("invalid hook name")
                );
            }

            DataResult<Conditions> explicit = ops.getMap(stripped).result()
                    .map(map -> {
                        T conditionsValue = map.get(ops.createString(JolCraftParameterIds.CONDITIONS));
                        if (conditionsValue == null) {
                            return DataResult.success(Conditions.EMPTY);
                        }
                        return Conditions.CODEC.parse(ops, conditionsValue);
                    })
                    .orElse(DataResult.success(Conditions.EMPTY));

            if (explicit.error().isPresent()) {
                return DataResult.error(() ->
                        explicit.error().map(DataResult.Error::message).orElse("invalid hook conditions")
                );
            }

            DataResult<Conditions> merged =
                    Conditions.mergeExplicitAndInline(explicit.result().orElse(Conditions.EMPTY), ex.conditions());

            if (merged.error().isPresent()) {
                return DataResult.error(() ->
                        merged.error().map(DataResult.Error::message).orElse("invalid hook conditions")
                );
            }

            return DataResult.success(com.mojang.datafixers.util.Pair.of(
                    new Hook(id.result().orElseThrow(), merged.result().orElse(Conditions.EMPTY)),
                    input
            ));
        }

        @Override
        public <T> DataResult<T> encode(Hook input, DynamicOps<T> ops, T prefix) {
            if (input.conditions().isEmpty()) {
                return ResourceLocation.CODEC.encode(input.id(), ops, prefix);
            }

            T result = ops.createMap(java.util.stream.Stream.of(
                    com.mojang.datafixers.util.Pair.of(
                            ops.createString(JolCraftParameterIds.ID),
                            ResourceLocation.CODEC.encodeStart(ops, input.id()).result().orElseThrow()
                    )
            ));

            DataResult<T> flattened = Conditions.encodeInlineConditions(
                    ops,
                    input.conditions(),
                    result,
                    RESERVED_KEYS
            );

            if (flattened.error().isEmpty()) {
                return flattened;
            }

            result = ops.mergeToMap(
                    result,
                    ops.createString(JolCraftParameterIds.CONDITIONS),
                    Conditions.CODEC.encodeStart(ops, input.conditions()).result().orElseThrow()
            ).result().orElse(result);

            return DataResult.success(result);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, Hook> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, Hook::id,
                    Conditions.STREAM_CODEC, Hook::conditions,
                    Hook::new
            );

    public Hook {
        if (id == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ID + "'");
        }
        conditions = conditions != null ? conditions : Conditions.EMPTY;
    }

    @Override
    public @NotNull DataResult<Hook> validate() {
        if (!Hooks.isRegistered(id)) {
            return DataResult.error(() -> Hooks.unknownHookError(id));
        }

        DataResult<Conditions> cv = conditions.validate();
        if (cv.error().isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.CONDITIONS + " invalid: " +
                            cv.error().map(DataResult.Error::message).orElse("invalid")
            );
        }

        return SelfValidating.ok(this);
    }

    public void apply(
            @NotNull WorldContext ctx,
            @NotNull ItemTransformSourceResolver resolver,
            @NotNull List<Output> outputs
    ) {
        if (!conditions.test(ctx)) {
            return;
        }
        Hooks.apply(id, ctx, resolver, outputs);
    }
}