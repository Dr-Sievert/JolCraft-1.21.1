package net.sievert.jolcraft.datagen.config;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ConfigCodecWriter {

    private ConfigCodecWriter() {}

    public static <T> CompletableFuture<?> write(
            CachedOutput cache,
            PackOutput output,
            String kindDirectory,
            ResourceLocation fileId,
            Codec<T> codec,
            T value
    ) {
        Optional<JsonElement> jsonOpt = codec.encodeStart(JsonOps.INSTANCE, value)
                .resultOrPartial(__ -> {});

        if (jsonOpt.isEmpty()) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("Failed to encode config json for " + fileId)
            );
        }

        PackOutput.PathProvider paths = output.createPathProvider(PackOutput.Target.DATA_PACK, kindDirectory);
        return DataProvider.saveStable(cache, jsonOpt.get(), paths.json(fileId));
    }
}