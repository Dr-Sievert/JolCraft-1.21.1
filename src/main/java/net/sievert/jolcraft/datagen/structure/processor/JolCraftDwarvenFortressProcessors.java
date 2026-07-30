package net.sievert.jolcraft.datagen.structure.processor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftDwarvenFortressPoolIds;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftTemplatePoolIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.structure.util.AbstractProcessorProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import net.sievert.jolcraft.world.worldgen.processor.custom.*;
import net.sievert.jolcraft.world.worldgen.test.custom.RandomNotAirRuleTest;

import java.util.List;

@SuppressWarnings("deprecation")
public final class JolCraftDwarvenFortressProcessors extends AbstractProcessorProvider {

    private static final String DIRECTORY_ID = JolCraftStructureIds.DWARVEN_FORTRESS;

    public static final ResourceKey<StructureProcessorList> DWARVEN_FORTRESS =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, DIRECTORY_ID));

    public static final ResourceKey<StructureProcessorList> BROKEN =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftTemplatePoolIds.BROKEN));

    public static final ResourceKey<StructureProcessorList> CAVE_IN =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftStrings.underscored(JolCraftDictionary.CAVE, JolCraftDictionary.IN)));

    public static final ResourceKey<StructureProcessorList> COLLAPSED =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftTemplatePoolIds.COLLAPSED));

    public static final ResourceKey<StructureProcessorList> FORGE_LOOT =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDwarvenFortressPoolIds.FORGE_LOOT));

    public static final ResourceKey<StructureProcessorList> VAULT_LOOT =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDwarvenFortressPoolIds.VAULT_LOOT));

    public static final ResourceKey<StructureProcessorList> GARDEN_LOOT =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDwarvenFortressPoolIds.GARDEN_LOOT));

    public static final ResourceKey<StructureProcessorList> ARCHIVES_LOOT =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDwarvenFortressPoolIds.ARCHIVES_LOOT));

    public static final ResourceKey<StructureProcessorList> CATACOMBS_LOOT =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDwarvenFortressPoolIds.CATACOMBS_LOOT));

    public static final ResourceKey<StructureProcessorList> BARREL =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDictionary.BARREL));

    public static final ResourceKey<StructureProcessorList> ARCHAEOLOGY =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDictionary.ARCHAEOLOGY));

    public static final ResourceKey<StructureProcessorList> TOWN =
            processorKey(JolCraftStrings.slashed(DIRECTORY_ID, JolCraftDwarvenFortressPoolIds.TOWN));

    private JolCraftDwarvenFortressProcessors(BootstrapContext<StructureProcessorList> context) {
        super(context, DIRECTORY_ID);
    }

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        new JolCraftDwarvenFortressProcessors(context).registerProcessors();
    }

    private void registerProcessors() {

        register(
                DWARVEN_FORTRESS,
                crackedBricks(),
                new RandomCobwebProcessor(0.01F)
        );

        register(
                TOWN,
                crackedBricks()
        );

        register(
                CAVE_IN,
                crackedBricks(),
                new RandomCaveInProcessor(0.10F),
                new StructureVoidProcessor(),
                new RandomCobwebProcessor(0.01F)
        );

        register(
                COLLAPSED,
                crackedBricks(),
                new RuleProcessor(List.of(
                        new ProcessorRule(
                                new RandomNotAirRuleTest(0.10F),
                                AlwaysTrueTest.INSTANCE,
                                Blocks.STRUCTURE_VOID.defaultBlockState()
                        )
                )),
                new StructureVoidProcessor(),
                new RandomCobwebProcessor(0.10F)
        );

        register(
                BROKEN,
                new RuleProcessor(List.of(
                        new ProcessorRule(
                                new RandomNotAirRuleTest(0.20F),
                                AlwaysTrueTest.INSTANCE,
                                Blocks.AIR.defaultBlockState()
                        )
                ))
        );

        register(
                FORGE_LOOT,
                new AddLootTableProcessor(
                        JolCraftBlocks.STRONGBOX.getId(),
                        JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_FORGE
                ),
                new RandomCobwebProcessor(0.01F)
        );

        register(
                VAULT_LOOT,
                new AddLootTableProcessor(
                        JolCraftBlocks.STRONGBOX.getId(),
                        JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_VAULT
                ),
                new RandomCobwebProcessor(0.01F)
        );

        register(
                GARDEN_LOOT,
                new AddLootTableProcessor(
                        JolCraftBlocks.STRONGBOX.getId(),
                        JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_GARDEN
                ),
                new RandomCobwebProcessor(0.01F)
        );

        register(
                ARCHIVES_LOOT,
                new AddLootTableProcessor(
                        JolCraftBlocks.STRONGBOX.getId(),
                        JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_ARCHIVES
                ),
                new RandomCobwebProcessor(0.01F)
        );

        register(
                CATACOMBS_LOOT,
                new AddLootTableProcessor(
                        JolCraftBlocks.STRONGBOX.getId(),
                        JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_CATACOMBS
                ),
                new RandomCobwebProcessor(0.01F)
        );

        register(
                BARREL,
                new AddLootTableProcessor(
                        BuiltInRegistries.BLOCK.getKey(Blocks.BARREL),
                        JolCraftLootTables.Chests.SUPPLIES
                ),
                new RandomFermentingContainerProcessor(
                        0.05F,
                        List.of(
                                new RandomFermentingContainerProcessor.BrewEntry(
                                        List.of(
                                                new MobEffectInstance(
                                                        MobEffects.DAMAGE_BOOST,
                                                        6000,
                                                        3
                                                )
                                        ),
                                        1
                                ),
                                new RandomFermentingContainerProcessor.BrewEntry(
                                        List.of(
                                                new MobEffectInstance(
                                                        MobEffects.DAMAGE_RESISTANCE,
                                                        6000,
                                                        3
                                                )
                                        ),
                                        1
                                ),
                                new RandomFermentingContainerProcessor.BrewEntry(
                                        List.of(
                                                new MobEffectInstance(
                                                        MobEffects.HEALTH_BOOST,
                                                        6000,
                                                        3
                                                )
                                        ),
                                        1
                                ),
                                new RandomFermentingContainerProcessor.BrewEntry(
                                        List.of(
                                                new MobEffectInstance(
                                                        MobEffects.ABSORPTION,
                                                        6000,
                                                        3
                                                )
                                        ),
                                        1
                                )
                        ),
                        DwarvenBrewAge.VINTAGE
                )
        );

        register(
                ARCHAEOLOGY,
                crackedBricks(),
                new RandomCobwebProcessor(0.01F),
                new RandomReplaceWithLootProcessor(
                        Blocks.GRAVEL.builtInRegistryHolder().key().location(),
                        Blocks.SUSPICIOUS_GRAVEL.builtInRegistryHolder().key().location(),
                        0.01F,
                        JolCraftLootTables.Archaeology.DWARVEN_FORTRESS_RARE.location()
                ),
                new RandomReplaceWithLootProcessor(
                        Blocks.GRAVEL.builtInRegistryHolder().key().location(),
                        Blocks.SUSPICIOUS_GRAVEL.builtInRegistryHolder().key().location(),
                        0.08F,
                        JolCraftLootTables.Archaeology.DWARVEN_FORTRESS_COMMON.location()
                )
        );
    }

    private static RuleProcessor crackedBricks() {
        return new RuleProcessor(List.of(
                new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.DEEPSLATE_BRICKS, (float) 0.1),
                        AlwaysTrueTest.INSTANCE,
                        Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState()
                )
        ));
    }
}