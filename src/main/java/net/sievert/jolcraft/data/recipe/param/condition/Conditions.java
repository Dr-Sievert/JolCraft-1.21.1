package net.sievert.jolcraft.data.recipe.param.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record Conditions(List<Condition> conditions)
        implements SelfValidating<Conditions>, RegistryIntrospectionSource {

    public static final Conditions EMPTY = new Conditions(List.of());

    private static final int MAX_STREAM_SIZE = 2048;

    private static final Codec<Conditions> RAW_CODEC =
            Condition.CODEC.listOf().xmap(Conditions::new, Conditions::conditions);

    public static final Codec<Conditions> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Conditions> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        List<Condition> list = value.conditions();
                        buf.writeVarInt(list.size());
                        for (Condition condition : list) {
                            Condition.STREAM_CODEC.encode(buf, condition);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size < 0) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.CONDITIONS + " size must be >= 0 (got " + size + ")"
                            );
                        }
                        if (size == 0) {
                            return EMPTY;
                        }
                        if (size > MAX_STREAM_SIZE) {
                            throw new IllegalArgumentException(
                                    JolCraftParameterIds.CONDITIONS + " size exceeds max " + MAX_STREAM_SIZE + " (got " + size + ")"
                            );
                        }

                        ArrayList<Condition> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Condition.STREAM_CODEC.decode(buf));
                        }
                        return new Conditions(list);
                    }
            );

    public Conditions(List<Condition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            this.conditions = List.of();
            return;
        }

        ArrayList<Condition> safe = new ArrayList<>(conditions.size());
        for (Condition condition : conditions) {
            if (condition == null) {
                throw new IllegalArgumentException(JolCraftParameterIds.CONDITIONS + " contains null");
            }
            safe.add(condition);
        }
        this.conditions = List.copyOf(safe);
    }

    @Override
    public @NotNull DataResult<Conditions> validate() {
        for (int i = 0; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            DataResult<Condition> child = condition.validate();
            var err = child.error();
            if (err.isPresent()) {
                final int idx = i;
                final String msg = err.get().message();
                return DataResult.error(() ->
                        JolCraftParameterIds.CONDITIONS + " invalid child at index " + idx + ": " + msg
                );
            }
        }

        return DataResult.success(this);
    }

    public boolean test(@NotNull WorldContext ctx) {
        if (conditions.isEmpty()) return true;

        for (Condition condition : conditions) {
            if (!condition.test(ctx)) return false;
        }
        return true;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return conditions.isEmpty()
                ? List.of()
                : RegistryIntrospectionSource.mergeByRegistry(conditions);
    }
}