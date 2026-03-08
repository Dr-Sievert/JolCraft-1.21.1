package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
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

    public static final byte DISC = 7;

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<EntityOutput> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    EntitySpec.CODEC
                            .fieldOf(JolCraftParameterIds.RESULT)
                            .forGetter(EntityOutput::result)
            ).apply(inst, EntityOutput::new));

    public static final Codec<EntityOutput> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityOutput> STREAM_CODEC =
            StreamCodec.composite(
                    EntitySpec.STREAM_CODEC, EntityOutput::result,
                    EntityOutput::new
            );

    public static final ParamTypeDef<OutputParam> TYPE_DEF = new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return result.introspections();
    }

    // ---------------------------------------------------------------------
    // OUTPUT PARAM
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {

        var rolledOpt = result.roll(ctx);
        if (rolledOpt.isEmpty()) return List.of();

        EntitySpec.RolledEntity rolled = rolledOpt.get();
        BlockPos pos = rolled.spawn() != null ? rolled.spawn().pos() : null;

        Output.EntitySpec spec = new Output.EntitySpec(
                rolled.type(),
                rolled.count(),
                pos,
                rolled.nbt(),
                rolled.spawn()
        );

        return List.of(new Output.Entities(List.of(spec)));
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EntityOutput> validate() {

        if (result == null) {
            return DataResult.error(() -> "'" + JolCraftParameterIds.RESULT + "' is required");
        }

        var rv = result.validate();
        var rerr = rv.error();
        return rerr.<DataResult<EntityOutput>>map(entitySpecError ->
                DataResult.error(() -> "'" + JolCraftParameterIds.RESULT + "' invalid: " + entitySpecError.message())).orElseGet(() -> DataResult.success(this));

    }
}