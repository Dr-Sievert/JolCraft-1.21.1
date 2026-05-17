package net.sievert.jolcraft.event.game.recipe;

import net.minecraft.resources.ResourceLocation;
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
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipeInput;
import net.sievert.jolcraft.param.runtime.WorldAnchor;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputHandler;
import net.sievert.jolcraft.world.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftHandInteractionEvents {

    private static final int HAND_INTERACTION_COOLDOWN_TICKS = 10;

    private static final OutputHandler OUTPUTS = new OutputHandler() {};

    private JolCraftHandInteractionEvents() {}

    @SubscribeEvent
    public static void onHandInteractionRecipe(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Player player = event.getEntity();
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (main.isEmpty() || off.isEmpty()) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(main.getItem())) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(off.getItem())) {
            return;
        }

        WorldContext ctx = new WorldContext(player, null);
        HandInteractionRecipeInput rawInput = new HandInteractionRecipeInput(ctx, main, off);

        ResolvedRecipe resolved = findResolvedRecipe(level, player, rawInput);
        if (resolved == null) {
            JolCraftLogs.warn(
                    JolCraftLogTags.RECIPE,
                    "Hand interaction resolved no recipe for main=" + main + " off=" + off
            );
            return;
        }

        JolCraftLogs.info(
                JolCraftLogTags.RECIPE,
                "Hand interaction resolved recipe={}",
                resolved.id()
        );

        HandInteractionRecipe recipe = resolved.recipe();
        HandMapping mapping = resolved.mapping();

        cancel(event);

        HandInteractionRecipeInput resolvedInput = new HandInteractionRecipeInput(
                ctx,
                mapping.stackA(),
                mapping.stackB()
        );

        List<Output> outputs = recipe.roll(resolvedInput, ctx);
        if (outputs.isEmpty()) {
            playFailSound(player, recipe);
            return;
        }

        OUTPUTS.handleAll(ctx, outputs, null, WorldAnchor.PLAYER, SoundSource.PLAYERS);

        playSuccessSound(player, recipe);

        ItemIngredientAction.apply(ctx, mapping.stackA(), recipe.actionA());
        ItemIngredientAction.apply(ctx, mapping.stackB(), recipe.actionB());

        player.getCooldowns().addCooldown(main.getItem(), HAND_INTERACTION_COOLDOWN_TICKS);
        player.getCooldowns().addCooldown(off.getItem(), HAND_INTERACTION_COOLDOWN_TICKS);

        player.swing(mapping.swingHand(), true);
    }

    private static @Nullable ResolvedRecipe findResolvedRecipe(
            @NotNull ServerLevel level,
            @NotNull Player player,
            @NotNull HandInteractionRecipeInput rawInput
    ) {
        List<RecipeHolder<HandInteractionRecipe>> recipes = level.getServer()
                .getRecipeManager()
                .getRecipesFor(JolCraftRecipes.HAND_INTERACTION_TYPE.get(), rawInput, level);

        JolCraftLogs.error(
                JolCraftLogTags.RECIPE,
                "Hand interaction candidate recipe count=" + recipes.size()
        );

        for (RecipeHolder<HandInteractionRecipe> holder : recipes) {
            HandInteractionRecipe recipe = holder.value();

            JolCraftLogs.error(
                    JolCraftLogTags.RECIPE,
                    "Checking hand recipe name=" + holder.id()
            );

            if (recipe.requireSneaking() && !player.isShiftKeyDown()) {
                JolCraftLogs.error(
                        JolCraftLogTags.RECIPE,
                        "Skipped hand recipe due to sneaking requirement: " + holder.id()
                );
                continue;
            }

            HandMapping mapping = resolveMapping(recipe, rawInput);
            if (mapping != null) {
                JolCraftLogs.error(
                        JolCraftLogTags.RECIPE,
                        "Resolved hand recipe: " + holder.id()
                );
                return new ResolvedRecipe(holder.id(), recipe, mapping);
            }

            JolCraftLogs.error(
                    JolCraftLogTags.RECIPE,
                    "Mapping failed for hand recipe: " + holder.id()
            );
        }

        return null;
    }

    private static @Nullable HandMapping resolveMapping(
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
            return new HandMapping(
                    main,
                    off,
                    InteractionHand.MAIN_HAND
            );
        }

        boolean swapped =
                recipe.ingredientA().matches(ctx, off) &&
                        recipe.ingredientB().matches(ctx, main) &&
                        ItemIngredientAction.isSatisfied(off, recipe.actionA()) &&
                        ItemIngredientAction.isSatisfied(main, recipe.actionB());

        if (swapped) {
            return new HandMapping(
                    off,
                    main,
                    InteractionHand.OFF_HAND
            );
        }

        return null;
    }

    private record HandMapping(
            ItemStack stackA,
            ItemStack stackB,
            InteractionHand swingHand
    ) {}

    private record ResolvedRecipe(
            ResourceLocation id,
            HandInteractionRecipe recipe,
            HandMapping mapping
    ) {}

    private static void playSuccessSound(@NotNull Player player, @NotNull HandInteractionRecipe recipe) {
        SoundOutput s = recipe.successSound();
        SoundEvent sound = s.resolveValue(player.registryAccess());
        if (sound != null) {
            JolCraftSoundHelper.player(player, sound, s.volume(), s.pitch());
        }
    }

    private static void playFailSound(@NotNull Player player, @NotNull HandInteractionRecipe recipe) {
        SoundOutput s = recipe.failSound();
        SoundEvent sound = s.resolveValue(player.registryAccess());
        if (sound != null) {
            JolCraftSoundHelper.player(player, sound, s.volume(), s.pitch());
        }
    }

    private static void cancel(@NotNull PlayerInteractEvent.RightClickItem event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}