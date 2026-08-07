package net.sievert.jolcraft.event.game.recipe;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.recipe.brewing.JolCraftBrewingEvents;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftVanillaRecipeEvents {

    private JolCraftVanillaRecipeEvents() {}

    @SubscribeEvent
    public static void addVanillaBrewingRecipes(RegisterBrewingRecipesEvent event) {
        JolCraftBrewingEvents.register(event.getBuilder());
    }
}