package net.sievert.jolcraft.world.player.advancement.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

import java.util.Optional;
import java.util.function.Function;

public final class JolCraftTriggerCodecs {

    private JolCraftTriggerCodecs() {}

    public static MapCodec<Optional<ContextAwarePredicate>> optionalPlayer() {
        return EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf(JolCraftDictionary.PLAYER);
    }

    public static <T> Codec<T> strictStringMapped(
            Function<String, T> decoder,
            Function<T, String> encoder,
            String typeName
    ) {
        return Codec.STRING.comapFlatMap(
                id -> {
                    T value = decoder.apply(id);
                    if (value == null) {
                        return DataResult.error(() -> "Unknown " + typeName + ": " + id);
                    }
                    return DataResult.success(value);
                },
                encoder
        );
    }
}