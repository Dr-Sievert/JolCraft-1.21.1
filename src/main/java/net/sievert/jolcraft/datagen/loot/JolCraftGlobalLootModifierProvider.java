package net.sievert.jolcraft.datagen.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.loot.custom.AddItemModifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
public final class JolCraftGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public JolCraftGlobalLootModifierProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, JolCraft.MOD_ID);
    }

    @Override
    protected void start() {
        Holder<Item> lexicon = JolCraftItems.DWARVEN_LEXICON.get().builtInRegistryHolder();

        this.add("dwarven_lexicon_from_stronghold_library",
                new AddItemModifier(
                        new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                                LootItemRandomChanceCondition.randomChance(0.50f).build()
                        },
                        lexicon
                ));

        this.add("dwarven_lexicon_from_mineshaft",
                new AddItemModifier(
                        new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.20f).build()
                        },
                        lexicon
                ));

        this.add("dwarven_lexicon_from_trail_ruins",
                new AddItemModifier(
                        new LootItemCondition[]{
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_rare")).build(),
                                LootItemRandomChanceCondition.randomChance(0.50f).build()
                        },
                        lexicon
                ));
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Global Loot";
    }
}