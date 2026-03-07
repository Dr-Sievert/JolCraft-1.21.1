package net.sievert.jolcraft.datagen.config;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public interface ConfigSubProvider {
    CompletableFuture<?> run(CachedOutput cache, PackOutput output);
    String name();
}