package net.sievert.jolcraft.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.loot.util.AbstractGlobalLootModifierProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class JolCraftGlobalLootModifierProvider extends AbstractGlobalLootModifierProvider {

    public JolCraftGlobalLootModifierProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, JolCraft.MOD_ID);
    }

    @Override
    protected @NotNull List<? extends GlobalLootSubProvider> subProviders() {
        return List.of(new LexiconGlobalLootSubProvider());
    }

    private static final class LexiconGlobalLootSubProvider implements GlobalLootSubProvider {

        @Override
        public void addModifiers(@NotNull AbstractGlobalLootModifierProvider p) {

            /*

            p.put(JolCraftLootTableIds.DWARVEN_LEXICON_IN_STRONGHOLD_LIBRARY,
                    new AddTableLootModifier(
                            new LootItemCondition[]{
                                    new LootTableIdCondition.Builder(BuiltInLootTables.STRONGHOLD_LIBRARY.location()).build()
                            },
                            JolCraftLootTables.DWARVEN_LEXICON_IN_STRONGHOLD_LIBRARY
                    )
            );

            */

        }
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Global Loot Modifiers";
    }
}