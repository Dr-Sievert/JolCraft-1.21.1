package net.sievert.jolcraft.event.game.recipe;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.data.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.data.recipe.custom.hand.HandInteractionRecipeInput;
import net.sievert.jolcraft.data.recipe.param.level.WorldAnchor;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputHandler;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftHandInteractionEvents {

    private JolCraftHandInteractionEvents() {}

    private static final OutputHandler OUTPUTS = new OutputHandler() {};

    @SubscribeEvent
    public static void onHandInteractionRecipe(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (main.isEmpty() || off.isEmpty()) return;

        WorldContext ctx = new WorldContext(level, player, player);
        HandInteractionRecipeInput rawInput = new HandInteractionRecipeInput(ctx, main, off);

        Optional<ResolvedRecipe> resolvedOpt = findResolvedRecipe(level, player, rawInput);
        if (resolvedOpt.isEmpty()) return;

        ResolvedRecipe resolved = resolvedOpt.get();
        HandInteractionRecipe recipe = resolved.recipe();
        HandMapping mapping = resolved.mapping();

        HandInteractionRecipeInput resolvedInput = new HandInteractionRecipeInput(
                ctx,
                mapping.stackA(),
                mapping.stackB()
        );

        cancel(event);

        List<Output> outputs = recipe.roll(resolvedInput, ctx);
        if (outputs.isEmpty()) {
            playFailSound(player, recipe);
            return;
        }

        OUTPUTS.handleAll(ctx, outputs, null, WorldAnchor.PLAYER, SoundSource.PLAYERS);

        playSuccessSound(player, recipe);

        ItemIngredientAction.apply(ctx, mapping.stackA(), recipe.actionA());
        ItemIngredientAction.apply(ctx, mapping.stackB(), recipe.actionB());

        player.swing(mapping.swingHand(), true);
    }

    private static @NotNull Optional<ResolvedRecipe> findResolvedRecipe(
            @NotNull ServerLevel level,
            @NotNull Player player,
            @NotNull HandInteractionRecipeInput rawInput
    ) {
        var recipes = level.getServer()
                .getRecipeManager()
                .recipeMap()
                .getRecipesFor(JolCraftRecipes.HAND_INTERACTION_TYPE.get(), rawInput, level);

        return recipes
                .map(RecipeHolder::value)
                .map(recipe -> {
                    if (recipe.requireSneaking() && !player.isShiftKeyDown()) {
                        return null;
                    }

                    Optional<HandMapping> mapping = resolveMapping(recipe, rawInput);
                    return mapping.map(handMapping -> new ResolvedRecipe(recipe, handMapping)).orElse(null);

                })
                .filter(java.util.Objects::nonNull)
                .findFirst();
    }

    private static @NotNull Optional<HandMapping> resolveMapping(
            @NotNull HandInteractionRecipe recipe,
            @NotNull HandInteractionRecipeInput in
    ) {
        WorldContext ctx = in.ctx();

        ItemStack main = in.ingredientA();
        ItemStack off = in.ingredientB();

        boolean direct =
                recipe.ingredientA().matches(ctx, main) &&
                        recipe.ingredientB().matches(ctx, off) &&
                        ItemIngredientAction.isSatisfied(main, recipe.actionA()) &&
                        ItemIngredientAction.isSatisfied(off, recipe.actionB());

        if (direct) {
            return Optional.of(new HandMapping(
                    main,
                    off,
                    InteractionHand.MAIN_HAND,
                    InteractionHand.OFF_HAND,
                    InteractionHand.MAIN_HAND
            ));
        }

        boolean swapped =
                recipe.ingredientA().matches(ctx, off) &&
                        recipe.ingredientB().matches(ctx, main) &&
                        ItemIngredientAction.isSatisfied(off, recipe.actionA()) &&
                        ItemIngredientAction.isSatisfied(main, recipe.actionB());

        if (swapped) {
            return Optional.of(new HandMapping(
                    off,
                    main,
                    InteractionHand.OFF_HAND,
                    InteractionHand.MAIN_HAND,
                    InteractionHand.OFF_HAND
            ));
        }

        return Optional.empty();
    }

    private record HandMapping(
            ItemStack stackA,
            ItemStack stackB,
            InteractionHand handA,
            InteractionHand handB,
            InteractionHand swingHand
    ) {}

    private record ResolvedRecipe(
            HandInteractionRecipe recipe,
            HandMapping mapping
    ) {}

    private static void playSuccessSound(Player player, HandInteractionRecipe recipe) {
        SoundOutput s = recipe.successSound();
        SoundEvent soundEvent = s.sound().value();
        JolCraftSoundHelper.player(player, soundEvent, s.volume(), s.pitch());
    }

    private static void playFailSound(Player player, HandInteractionRecipe recipe) {
        SoundOutput s = recipe.failSound();
        SoundEvent soundEvent = s.sound().value();
        JolCraftSoundHelper.player(player, soundEvent, s.volume(), s.pitch());
    }

    private static void cancel(PlayerInteractEvent.RightClickItem event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}