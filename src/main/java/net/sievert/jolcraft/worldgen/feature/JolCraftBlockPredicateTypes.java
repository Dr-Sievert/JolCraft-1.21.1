package net.sievert.jolcraft.worldgen.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.worldgen.custom.predicate.DarknessPredicate;

public class JolCraftBlockPredicateTypes {
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATE_TYPES =
            DeferredRegister.create(Registries.BLOCK_PREDICATE_TYPE, JolCraft.MOD_ID);

    public static final DeferredHolder<BlockPredicateType<?>, BlockPredicateType<DarknessPredicate>> DARKNESS =
            BLOCK_PREDICATE_TYPES.register("darkness", () -> () -> DarknessPredicate.CODEC);

    public static void register(IEventBus eventBus) {
        BLOCK_PREDICATE_TYPES.register(eventBus);
    }
}
