package net.sievert.jolcraft.datagen.loot.glm.subprovider;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.loot.glm.JolCraftGlobalLootModifierProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.loot.custom.AddItemModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftBlockGlobalLootModifierProvider
        implements JolCraftSubDataProvider<JolCraftGlobalLootModifierProvider> {

    private static final float BARLEY_SEED_CHANCE = 0.05F;

    private final JolCraftGlobalLootModifierProvider parent;

    public JolCraftBlockGlobalLootModifierProvider(
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
                JolCraftDictionary.BLOCK,
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
        addBarleySeeds(
                target,
                tracking,
                Blocks.SHORT_GRASS.getLootTable()
        );

        addBarleySeeds(
                target,
                tracking,
                Blocks.TALL_GRASS.getLootTable()
        );

        tracking.logTrackedOutputCount(
                this,
                JolCraftStrings.toTitleCase(id())
        );
    }

    private void addBarleySeeds(
            @NotNull JolCraftGlobalLootModifierProvider target,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ResourceKey<
                                LootTable
                                > lootTable
    ) {
        String modifierId = JolCraftStrings.underscored(
                JolCraftItems.BARLEY_SEEDS
                        .unwrapKey()
                        .orElseThrow()
                        .location()
                        .getPath(),
                JolCraftDictionary.IN,
                lootTable.location().getPath()
        );

        LootItemCondition notShears = InvertedLootItemCondition.invert(
                MatchTool.toolMatches(
                        ItemPredicate.Builder.item()
                                .of(Items.SHEARS)
                )
        ).build();

        target.add(
                this,
                tracking,
                modifierId,
                new AddItemModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(
                                        lootTable.location()
                                ).build(),
                                notShears
                        },
                        JolCraftItems.BARLEY_SEEDS,
                        BARLEY_SEED_CHANCE,
                        false
                )
        );
    }
}