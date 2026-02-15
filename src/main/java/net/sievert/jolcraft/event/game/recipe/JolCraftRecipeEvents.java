package net.sievert.jolcraft.event.game.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.data.recipe.custom.hand.HandInteractionRecipeInput;
import net.sievert.jolcraft.data.recipe.util.RecipeStackModifiers;
import net.sievert.jolcraft.data.recipe.util.RecipeStackTransformations;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftRecipeEvents {

    @SubscribeEvent
    public static void onHandInteractionRecipe(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (main.isEmpty() || off.isEmpty()) return;

        HandInteractionRecipeInput input = new HandInteractionRecipeInput(main, off);

        HandInteractionRecipe recipe = level.getServer().getRecipeManager()
                .getRecipeFor(JolCraftRecipes.HAND_INTERACTION_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .orElse(null);

        if (recipe == null) return;

        if (recipe.requireSneaking() && !player.isShiftKeyDown()) {
            return;
        }

        cancel(event);

        RandomSource random = level.getRandom();
        boolean success = random.nextFloat() < recipe.chance();

        if (success) {
            recipe.successSound().ifPresentOrElse(
                    s -> JolCraftSoundHelper.player(player, s.sound().value(), s.volume(), s.pitch()),
                    () -> JolCraftSoundHelper.player(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.6F, 1.2F)
            );
        } else {
            recipe.failSound().ifPresentOrElse(
                    s -> JolCraftSoundHelper.player(player, s.sound().value(), s.volume(), s.pitch()),
                    () -> JolCraftSoundHelper.player(player, SoundEvents.VILLAGER_NO, 0.7F, 1.0F)
            );
            return;
        }

        int picks = recipe.rollCount(random);
        List<HandInteractionRecipe.ResultEntry> chosen = recipe.pickWeightedResults(random, picks);

        if (chosen.isEmpty()) return;

        HolderLookup.Provider registries = level.registryAccess();

        List<ItemStack> outputs = new ArrayList<>();
        for (HandInteractionRecipe.ResultEntry entry : chosen) {
            ItemStack out = entry.rollBase(registries, random);
            if (out.isEmpty()) continue;

            HandInteractionEngine.applyResultTransforms(level, player, out, entry, random);

            if (!out.isEmpty()) outputs.add(out);
        }

        if (outputs.isEmpty()) return;

        applyIngredientActions(player, level, recipe, main, off);

        for (ItemStack out : outputs) {
            if (!player.addItem(out)) {
                player.drop(out, false);
            }
        }

        player.swing(event.getHand(), true);
    }

    /**
     * Apply ingredient actions to the actual stacks in hand.
     * This respects swapped matching (A/B can be in either hand).
     */
    private static void applyIngredientActions(
            Player player,
            ServerLevel level,
            HandInteractionRecipe recipe,
            ItemStack main,
            ItemStack off
    ) {
        boolean mainIsA = recipe.ingredientA().matches(main) && recipe.ingredientB().matches(off);
        boolean offIsA  = recipe.ingredientA().matches(off) && recipe.ingredientB().matches(main);

        if (!mainIsA && !offIsA) return; // should not happen since recipe matched

        HandInteractionRecipe.IngredientEntry aEntry = recipe.ingredientA();
        HandInteractionRecipe.IngredientEntry bEntry = recipe.ingredientB();

        ItemStack stackA = mainIsA ? main : off;
        ItemStack stackB = mainIsA ? off : main;

        applyAction(player, stackA, aEntry.action(), mainIsA ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        applyAction(player, stackB, bEntry.action(), mainIsA ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
    }

    private static void applyAction(Player player, ItemStack stack, HandInteractionRecipe.IngredientAction action, EquipmentSlot slot) {
        if (player.isCreative()) return;

        switch (action.type()) {
            case CONSUME -> stack.shrink(1);
            case CATALYST -> {
                // nothing
            }
            case DAMAGE -> {
                int amt = action.amount().orElse(1);
                stack.hurtAndBreak(amt, player, slot);
            }
        }
    }

    private static void cancel(PlayerInteractEvent.RightClickItem event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    /**
     * Minimal hook point so the event class stays dumb/clean.
     * Uses the canonical recipe transform order via RecipeStackTransformations.
     */
    private static final class HandInteractionEngine {
        private HandInteractionEngine() {}

        static void applyResultTransforms(
                ServerLevel level,
                Player player,
                ItemStack base,
                HandInteractionRecipe.ResultEntry entry,
                RandomSource random
        ) {
            RecipeStackTransformations.applyWithResolver(
                    base,
                    level,
                    player,
                    random,
                    entry.enchantmentProvider(),
                    entry.stackModifierId(),
                    entry.resultPatch(),
                    RecipeStackModifiers::resolve
            );
        }
    }
}
