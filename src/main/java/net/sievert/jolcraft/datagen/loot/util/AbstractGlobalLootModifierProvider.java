package net.sievert.jolcraft.datagen.loot.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Shared base for Global Loot Modifier providers with:
 * - tracked ids (avoid duplicate modifier ids)
 * - subprovider infrastructure (like language)
 * - per-subprovider logging counts
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractGlobalLootModifierProvider extends GlobalLootModifierProvider {

    private final Set<String> addedIds = new HashSet<>();

    protected AbstractGlobalLootModifierProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            String modId
    ) {
        super(output, registries, modId);
    }

    /** Simple subprovider contract. */
    public interface GlobalLootSubProvider {
        void addModifiers(AbstractGlobalLootModifierProvider provider);
    }

    /** Subclasses provide their subproviders. */
    protected abstract List<? extends GlobalLootSubProvider> subProviders();

    /** Duplicate-safe add. */
    public final void put(String id, IGlobalLootModifier modifier) {
        if (!addedIds.add(id)) {
            throw new IllegalStateException(
                    "Duplicate global loot modifier id added: '" + id + "' (provider: " + getClass().getSimpleName() + ")"
            );
        }
        add(id, modifier);
    }

    /** Runs all subproviders (and logs counts). */
    protected final void runAll(List<? extends GlobalLootSubProvider> subs) {
        int beforeTotal = addedIds.size();

        for (GlobalLootSubProvider sub : subs) {
            int before = addedIds.size();

            sub.addModifiers(this);

            int added = addedIds.size() - before;
            String name = sub.getClass().getSimpleName();

            JolCraftLogs.debug(
                    JolCraftLogTags.DATAGEN,
                    "Global loot subprovider {}: +{} modifiers",
                    name,
                    added
            );

            if (added == 0) {
                JolCraftLogs.warn(
                        JolCraftLogTags.DATAGEN,
                        "Global loot subprovider {} added 0 modifiers.",
                        name
                );
            }
        }

        int totalAdded = addedIds.size() - beforeTotal;
        JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "Total global loot modifiers generated: {} ({} subproviders)",
                totalAdded,
                subs.size()
        );
    }

    @Override
    protected final void start() {
        addedIds.clear();
        runAll(subProviders());
    }
}