package net.sievert.jolcraft.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.loot.custom.AddItemModifier;

import java.util.concurrent.CompletableFuture;

public class JolCraftGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public JolCraftGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, JolCraft.MOD_ID);
    }

    @Override
    protected void start() {
        this.add("dwarven_lexicon_from_stronghold_library",
                new AddItemModifier(
                        new LootItemCondition[] {
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/stronghold_library")).build(),
                                LootItemRandomChanceCondition.randomChance(0.50f).build()
                        },
                        JolCraftItems.DWARVEN_LEXICON.get()
                ));

        this.add("dwarven_lexicon_from_mineshaft",
                new AddItemModifier(
                        new LootItemCondition[] {
                                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft")).build(),
                                LootItemRandomChanceCondition.randomChance(0.40f).build()
                        },
                        JolCraftItems.DWARVEN_LEXICON.get()
                ));

        this.add("dwarven_lexicon_from_trail_ruins",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_rare")).build(),
                        LootItemRandomChanceCondition.randomChance(0.50f).build()
                }, JolCraftItems.DWARVEN_LEXICON.get()));
    }

}
