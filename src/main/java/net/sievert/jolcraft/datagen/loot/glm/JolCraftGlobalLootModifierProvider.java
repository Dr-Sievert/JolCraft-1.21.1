package net.sievert.jolcraft.datagen.loot.glm;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.loot.custom.AddItemModifier;
import net.sievert.jolcraft.world.loot.custom.ReplaceWithItemModifier;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.loot.glm.subprovider.JolCraftArchaeologyGlobalLootModifierProvider;
import net.sievert.jolcraft.datagen.loot.glm.subprovider.JolCraftChestGlobalLootModifierProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class JolCraftGlobalLootModifierProvider
        extends GlobalLootModifierProvider
        implements JolCraftMainDataProvider<JolCraftGlobalLootModifierProvider> {

    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final List<JolCraftSubDataProvider<JolCraftGlobalLootModifierProvider>> subProviders;

    public JolCraftGlobalLootModifierProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, JolCraft.MOD_ID);
        this.lookupProvider = lookupProvider;
        this.subProviders = List.of(
                new JolCraftArchaeologyGlobalLootModifierProvider(this),
                new JolCraftChestGlobalLootModifierProvider(this)
        );
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.LOOT;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(
                JolCraftDictionary.GLOBAL,
                domain().getId(),
                JolCraftDictionary.MODIFIER
        );
    }

    @Override
    public @NotNull String name() {
        return JolCraft.MOD_NAME + " " +
                JolCraftStrings.toTitleCase(
                        JolCraftStrings.underscored(
                                id(),
                                JolCraftParameterIds.PROVIDER
                        )
                );
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    public @NotNull List<JolCraftSubDataProvider<JolCraftGlobalLootModifierProvider>> subProviders() {
        return subProviders;
    }

    @Override
    protected void start() {
        generate(this, null, lookupProvider, null);
    }

    public void add(
            @NotNull JolCraftDataProvider<?> provider,
            @NotNull JolCraftDataTracking tracking,
            @NotNull String modifierId,
            @NotNull IGlobalLootModifier modifier
    ) {
        super.add(modifierId, modifier);
        tracking.record(provider, modifierId);
    }

    public static @NotNull GlmTarget glm(
            @NotNull Holder<Item> item,
            @NotNull ResourceKey<LootTable> table
    ) {
        return new GlmTarget(
                JolCraftStrings.underscored(
                        item.unwrapKey().orElseThrow().location().getPath(),
                        JolCraftDictionary.IN,
                        table.location().getPath()
                ),
                LootTableIdCondition.builder(table.location()).build(),
                item
        );
    }

    public record GlmTarget(
            @NotNull String id,
            @NotNull LootItemCondition condition,
            @NotNull Holder<Item> item
    ) {

        public void add(
                @NotNull JolCraftGlobalLootModifierProvider target,
                @NotNull JolCraftDataProvider<?> provider,
                @NotNull JolCraftDataTracking tracking,
                @NotNull IGlobalLootModifier modifier
        ) {
            target.add(provider, tracking, id, modifier);
        }

        public void addItem(
                @NotNull JolCraftGlobalLootModifierProvider target,
                @NotNull JolCraftDataProvider<?> provider,
                @NotNull JolCraftDataTracking tracking,
                float chance
        ) {
            add(
                    target,
                    provider,
                    tracking,
                    new AddItemModifier(
                            new LootItemCondition[]{condition},
                            item,
                            chance
                    )
            );
        }

        public void replaceWithItem(
                @NotNull JolCraftGlobalLootModifierProvider target,
                @NotNull JolCraftDataProvider<?> provider,
                @NotNull JolCraftDataTracking tracking,
                float chance
        ) {
            add(
                    target,
                    provider,
                    tracking,
                    new ReplaceWithItemModifier(
                            new LootItemCondition[]{condition},
                            item,
                            chance
                    )
            );
        }
    }
}