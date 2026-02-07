package net.sievert.jolcraft.config;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ConfigManager<K, V> extends SimpleJsonResourceReloadListener<V> {

    protected ConfigManager(Codec<V> codec, String directory) {
        super(codec, new FileToIdConverter(directory, ".json"));
    }

    protected abstract void clear();


    @Nullable
    protected abstract K keyFromId(ResourceLocation id);

    protected abstract void put(K key, V value);

    @Override
    protected final void apply(Map<ResourceLocation, V> prepared, ResourceManager manager, ProfilerFiller profiler) {
        clear();

        for (Map.Entry<ResourceLocation, V> entry : prepared.entrySet()) {
            ResourceLocation id = entry.getKey();
            V value = entry.getValue();

            K key = keyFromId(id);
            if (key == null) continue;

            put(key, value);
        }
    }
}