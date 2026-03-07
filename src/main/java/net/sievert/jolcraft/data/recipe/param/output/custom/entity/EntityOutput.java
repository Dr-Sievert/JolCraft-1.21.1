package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

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
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Entity output param (atomic).
 *
 * Meaning:
 * - Produces at most ONE entity-spec envelope per generate(ctx) call.
 * - Repetition / multi-roll semantics are owned by Pools:
 *   - Pool.rolls (repick entry)
 *   - PoolEntry.pool.rolls (repeat chosen entry)
 *
 * Conditions:
 * - No local conditions gate.
 * - Gating is owned by Pools/Pool (pool-level) and DrawRule (entry-level).
 */
public record EntityOutput(
        EntitySpec result
) implements OutputParam, SelfValidating<EntityOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftParameterIds.ENTITY, JolCraftDictionary.OUTPUT));

    public static final EntityOutput EMPTY =
            new EntityOutput(EntitySpec.EMPTY);

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<EntityOutput> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    EntitySpec.CODEC
                            .fieldOf(JolCraftParameterIds.RESULT)
                            .forGetter(EntityOutput::result)
            ).apply(inst, EntityOutput::new));

    public static final Codec<EntityOutput> CODEC = ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityOutput> STREAM_CODEC =
            StreamCodec.composite(
                    EntitySpec.STREAM_CODEC, EntityOutput::result,
                    EntityOutput::new
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public EntityOutput {
        result = result != null ? result : EntitySpec.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        EntitySpec r = result != null ? result : EntitySpec.EMPTY;
        return r.introspections();
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {

        var rolledOpt = result.roll(ctx);
        if (rolledOpt.isEmpty()) return List.of();

        EntitySpec.RolledEntity rolled = rolledOpt.get();
        WorldAnchor anchor = (rolled.spawn() != null) ? rolled.spawn().anchor() : null;

        Output.EntitySpec spec = new Output.EntitySpec(
                rolled.type(),
                rolled.count(),
                anchor,
                rolled.nbt(),
                rolled.spawn()
        );

        return List.of(new Output.Entities(List.of(spec)));
    }

    @Override
    public @NotNull DataResult<EntityOutput> validate() {
        var rv = result.validate();
        var rerr = rv.error();
        return rerr.<DataResult<EntityOutput>>map(entitySpecError ->
                DataResult.error(() -> "result invalid: " + entitySpecError.message())
        ).orElseGet(() -> DataResult.success(this));
    }
}