package net.sievert.jolcraft.world.recipe.base.input;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.data_component.JolCraftDataComponentIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.recipe.base.input.custom.BrewAgeIngredient;

public final class JolCraftIngredientTypes {

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.INGREDIENT_TYPES,
                    JolCraft.MOD_ID
            );

    public static final DeferredHolder<IngredientType<?>, IngredientType<BrewAgeIngredient>> BREW_AGE =
            INGREDIENT_TYPES.register(
                    JolCraftDataComponentIds.BREW_AGE,
                    () -> new IngredientType<>(
                            BrewAgeIngredient.CODEC
                    )
            );

    private JolCraftIngredientTypes() {}

    public static void register(
            IEventBus eventBus
    ) {
        INGREDIENT_TYPES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} ingredient types",
                INGREDIENT_TYPES.getEntries().size()
        );
    }
}