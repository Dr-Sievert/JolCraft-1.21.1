package net.sievert.jolcraft.datagen.loot.glm.subprovider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.loot.glm.JolCraftGlobalLootModifierProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftFishingGlobalLootModifierProvider implements JolCraftSubDataProvider<JolCraftGlobalLootModifierProvider> {

    private final JolCraftGlobalLootModifierProvider parent;

    public JolCraftFishingGlobalLootModifierProvider(
            @NotNull JolCraftGlobalLootModifierProvider parent
    ) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<JolCraftGlobalLootModifierProvider> parent() {
        return parent;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(
                JolCraftDictionary.FISHING,
                JolCraftDictionary.GLOBAL,
                JolCraftDictionary.LOOT,
                JolCraftDictionary.MODIFIER
        );
    }

    @Override
    public void run(
            @NotNull JolCraftGlobalLootModifierProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        JolCraftGlobalLootModifierProvider.glm(
                JolCraftLootTables.Fishing.FISHING,
                BuiltInLootTables.FISHING
        ).replaceWithLootTable(
                target,
                this,
                tracking,
                0.15F
        );

        tracking.logTrackedOutputCount(
                this,
                JolCraftStrings.toTitleCase(id())
        );
    }
}