package net.sievert.jolcraft.world.worldgen.test;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftRuleTestIds;
import net.sievert.jolcraft.world.worldgen.test.custom.RandomNotAirRuleTest;

public class JolCraftRuleTests {

    public static final DeferredRegister<RuleTestType<?>> RULE_TEST_TYPES = DeferredRegister.create(Registries.RULE_TEST, JolCraft.MOD_ID);

    public static final DeferredHolder<RuleTestType<?>, RuleTestType<RandomNotAirRuleTest>> RANDOM_NOT_AIR =
            RULE_TEST_TYPES.register(JolCraftRuleTestIds.RANDOM_NOT_AIR, () -> () -> RandomNotAirRuleTest.CODEC);

    public static void register(IEventBus eventBus) {
        RULE_TEST_TYPES.register(eventBus);
    }
}
