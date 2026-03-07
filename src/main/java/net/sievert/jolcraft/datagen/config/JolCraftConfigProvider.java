package net.sievert.jolcraft.datagen.config;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.sievert.jolcraft.datagen.config.subprovider.DwarfProfessionConfigSubProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftConfigProvider implements DataProvider {

    private final PackOutput output;
    private final List<ConfigSubProvider> providers;

    public JolCraftConfigProvider(PackOutput output) {
        this.output = output;
        this.providers = List.of(
                new DwarfProfessionConfigSubProvider()
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(
                providers.stream()
                        .map(p -> p.run(cache, output))
                        .toArray(CompletableFuture[]::new)
        );
    }

    @Override
    public String getName() {
        return "JolCraft Configs";
    }
}