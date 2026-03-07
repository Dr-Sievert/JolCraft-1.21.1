package net.sievert.jolcraft.data.recipe.param.quantity.draw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Plural container for {@link WeightedDrawRule}.
 *
 * Semantics (probability weights):
 * - Picks ONE eligible entry by weight (weight > 0).
 * - Eligibility is based on {@link DrawRule#conditions()} only.
 * - After selecting an entry, returns that entry's {@link DrawRule#draws(WorldContext)}.
 * - Empty / no eligible entries => 0.
 */
public record DrawRules(List<WeightedDrawRule> rules) implements SelfValidating<DrawRules> {

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<DrawRules> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    WeightedDrawRule.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.POOLS, List.of())
                            .forGetter(DrawRules::rulesSafe)
            ).apply(instance, DrawRules::new));

    public static final Codec<DrawRules> CODEC = ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, DrawRules> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        List<WeightedDrawRule> list = value.rulesSafe();
                        buf.writeVarInt(list.size());
                        for (WeightedDrawRule e : list) {
                            WeightedDrawRule.STREAM_CODEC.encode(buf, e);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size <= 0) return new DrawRules(List.of());

                        int capped = Math.min(size, 2048);

                        ArrayList<WeightedDrawRule> list = new ArrayList<>(Math.min(capped, 64));
                        for (int i = 0; i < capped; i++) {
                            list.add(WeightedDrawRule.STREAM_CODEC.decode(buf));
                        }

                        for (int i = capped; i < size; i++) {
                            WeightedDrawRule.STREAM_CODEC.decode(buf);
                        }

                        return new DrawRules(list);
                    }
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public DrawRules(List<WeightedDrawRule> rules) {
        this.rules = sanitizeList(rules);
    }

    private List<WeightedDrawRule> rulesSafe() {
        return rules == null ? List.of() : rules;
    }

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<DrawRules> validate() {
        List<WeightedDrawRule> list = rulesSafe();

        for (int i = 0; i < list.size(); i++) {
            WeightedDrawRule e = list.get(i);
            if (e == null) {
                int idx = i;
                return DataResult.error(() -> JolCraftParameterIds.POOLS + " contains null at index " + idx);
            }

            DataResult<WeightedDrawRule> ev = e.validate();
            var errOpt = ev.error();
            if (errOpt.isPresent()) {
                int idx = i;
                String msg = errOpt.get().message();
                return DataResult.error(() ->
                        JolCraftParameterIds.POOLS + " invalid entry at index " + idx + ": " + msg
                );
            }
        }

        return DataResult.success(this);
    }

    // ---------------------------------------------------------------------
    // RUNTIME
    // ---------------------------------------------------------------------

    public int draws(WorldContext ctx) {
        if (ctx == null) return 0;

        RandomSource random = ctx.random();

        List<WeightedDrawRule> list = rulesSafe();
        if (list.isEmpty()) return 0;

        int total = 0;

        for (WeightedDrawRule e : list) {
            if (e == null) continue;

            DrawRule rule = e.rule();
            if (rule == null) continue;

            if (!rule.conditions().test(ctx)) continue;

            int w = e.weight().safe();
            if (w <= 0) continue;

            int next = total + w;
            if (next < total) return 0;
            total = next;
        }

        if (total == 0) return 0;

        int roll = random.nextInt(total);

        for (WeightedDrawRule e : list) {
            if (e == null) continue;

            DrawRule rule = e.rule();
            if (rule == null) continue;

            if (!rule.conditions().test(ctx)) continue;

            int w = e.weight().safe();
            if (w <= 0) continue;

            roll -= w;
            if (roll < 0) {
                int d = rule.draws(ctx);
                return Math.max(d, 0);
            }
        }

        return 0;
    }
}