package net.sievert.jolcraft.data.recipe.param.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record Conditions(List<Condition> conditions)
        implements SelfValidating<Conditions>, RegistryIntrospectionSource {

    public static final Conditions EMPTY = new Conditions(List.of());
    private static final int MAX_STREAM_SIZE = 2048;

    private static final Codec<Conditions> RAW_CODEC =
            ConditionTypes.CODEC.listOf().xmap(Conditions::new, Conditions::conditionsSafe);

    public static final Codec<Conditions> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Conditions> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        List<Condition> list = value.conditionsSafe();
                        buf.writeVarInt(list.size());
                        for (Condition c : list) {
                            ConditionTypes.STREAM_CODEC.encode(
                                    buf,
                                    (c == null)
                                            ? new ConditionTypes.InvalidCondition(ConditionTypes.TYPE_INVALID)
                                            : c
                            );
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size <= 0) return EMPTY;

                        int capped = Math.min(size, MAX_STREAM_SIZE);

                        ArrayList<Condition> list = new ArrayList<>(Math.min(capped, 64));
                        for (int i = 0; i < capped; i++) list.add(ConditionTypes.STREAM_CODEC.decode(buf));
                        for (int i = capped; i < size; i++) ConditionTypes.STREAM_CODEC.decode(buf);

                        return list.isEmpty() ? EMPTY : new Conditions(list);
                    }
            );

    public Conditions(List<Condition> conditions) {
        this.conditions = sanitizeList(conditions);
    }

    private List<Condition> conditionsSafe() {
        return conditions != null ? conditions : List.of();
    }

    @Override
    public @NotNull DataResult<Conditions> validate() {
        if (conditions == null) {
            return DataResult.error(() -> JolCraftParameterIds.CONDITIONS + " cannot be null");
        }
        for (int i = 0; i < conditions.size(); i++) {
            Condition c = conditions.get(i);
            if (c == null) {
                final int idx = i;
                return DataResult.error(() -> JolCraftParameterIds.CONDITIONS + " contains null at index " + idx);
            }
            DataResult<Condition> child = c.validate();
            var err = child.error();
            if (err.isPresent()) {
                final int idx = i;
                final String msg = err.get().message();
                return DataResult.error(() -> JolCraftParameterIds.CONDITIONS + " invalid child at index " + idx + ": " + msg);
            }
        }
        return DataResult.success(this);
    }

    public boolean test(@NotNull WorldContext ctx) {
        List<Condition> list = conditionsSafe();
        if (list.isEmpty()) return true;

        for (Condition condition : list) {
            if (condition == null) return false;
            if (condition instanceof ConditionTypes.InvalidCondition) return false;
            if (!condition.test(ctx)) return false;
        }
        return true;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        List<Condition> list = conditionsSafe();
        if (list.isEmpty()) return List.of();
        return RegistryIntrospectionSource.mergeByRegistry(list);
    }

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}