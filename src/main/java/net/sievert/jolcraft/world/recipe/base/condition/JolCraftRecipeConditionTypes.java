package net.sievert.jolcraft.world.recipe.base.condition;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftContextParamIds;
import net.sievert.jolcraft.world.recipe.base.condition.custom.InputItemCondition;

public final class JolCraftRecipeConditionTypes {

    private static final DeferredRegister<LootItemConditionType> CONDITION_TYPES =
            DeferredRegister.create(
                    Registries.LOOT_CONDITION_TYPE,
                    JolCraft.MOD_ID
            );

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> INPUT_ITEM =
            CONDITION_TYPES.register(
                    JolCraftContextParamIds.INPUT_ITEM,
                    () -> new LootItemConditionType(InputItemCondition.CODEC)
            );

    private JolCraftRecipeConditionTypes() {}

    public static void register(IEventBus modEventBus) {
        CONDITION_TYPES.register(modEventBus);
    }
}