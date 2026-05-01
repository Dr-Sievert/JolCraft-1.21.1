package net.sievert.jolcraft.config.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class JolCraftCodecConfigManager<K, V> extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    private final Codec<V> codec;

    protected JolCraftCodecConfigManager(Codec<V> codec, String directory) {
        super(GSON, directory);
        this.codec = codec;
    }

    @Nullable
    protected abstract K keyFromId(ResourceLocation id);

    protected abstract void replaceAll(Map<K, V> values);

    @Override
    protected final void apply(
            Map<ResourceLocation, JsonElement> prepared,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        Map<K, V> loaded = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : prepared.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement json = entry.getValue();

            V value = codec.parse(JsonOps.INSTANCE, json)
                    .getOrThrow(error -> new IllegalStateException(
                            "Failed to parse config '" + id + "' in '" + getName() + "': " + error
                    ));

            K key = keyFromId(id);
            if (key == null) {
                throw new IllegalStateException(
                        "Unknown config id '" + id + "' in '" + getName() + "'"
                );
            }

            loaded.put(key, value);
        }

        replaceAll(Map.copyOf(loaded));
    }
}