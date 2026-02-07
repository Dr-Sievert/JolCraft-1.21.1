package net.sievert.jolcraft.datagen.config;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.sievert.jolcraft.datagen.config.dwarf.DwarfProfessionConfigProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftConfigProvider implements DataProvider {

    private final List<DataProvider> providers;

    public JolCraftConfigProvider(PackOutput output) {
        this.providers = List.of(
                new DwarfProfessionConfigProvider(output)
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(
                providers.stream()
                        .map(p -> p.run(cache))
                        .toArray(CompletableFuture[]::new)
        );
    }

    @Override
    public String getName() {
        return "JolCraft Configs";
    }
}
