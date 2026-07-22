package net.sievert.jolcraft.datagen.config;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ConfigCodecWriter {

    private ConfigCodecWriter() {}

    public static <T> @NotNull CompletableFuture<?> write(
            @NotNull CachedOutput cache,
            @NotNull PackOutput output,
            @NotNull String path,
            @NotNull Codec<T> codec,
            @NotNull T value
    ) {
        Optional<JsonElement> jsonOpt = codec.encodeStart(JsonOps.INSTANCE, value)
                .resultOrPartial(__ -> {});

        if (jsonOpt.isEmpty()) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("Failed to encode config json for " + path)
            );
        }

        PackOutput.PathProvider paths = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                JolCraftDataDomain.CONFIG.getId()
        );

        return DataProvider.saveStable(
                cache,
                jsonOpt.get(),
                paths.json(JolCraft.location(path))
        );
    }
}