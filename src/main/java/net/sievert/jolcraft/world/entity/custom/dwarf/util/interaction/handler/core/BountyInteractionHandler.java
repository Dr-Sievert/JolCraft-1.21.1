package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.ToIntFunction;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public @NotNull InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        if (!(dwarf.level() instanceof ServerLevel level)) return InteractionResult.PASS;

        ItemStack stack = ctx.stack();
        if (stack.isEmpty()) return InteractionResult.PASS;

        if (!BountyRecipe.isValidBountyStack(stack)) return InteractionResult.PASS;

        boolean isTask = BountyTaskRecipe.isTaskBountyStack(stack);
        boolean isReward = BountyRewardRecipe.isRewardBountyStack(stack);

        if (!isTask && !isReward) return InteractionResult.PASS;

        if (!dwarf.canBountyInteract()) {
            return InteractionResult.PASS;
        }

        BountyType expected = BountyType.fromString(dwarf.getProfession().getId());
        if (expected == BountyType.UNKNOWN) {
            return InteractionResult.PASS;
        }

        BountyType type = BountyRecipe.readType(stack);
        if (type != expected) {
            return deny(ctx, JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE);
        }

        if (isReward && !BountyRewardRecipe.isCompletedRewardBountyStack(stack)) {
            return deny(ctx, JolCraftLanguageKeys.TOOLTIP_BOUNTY_NOT_COMPLETE);
        }

        BountyTier tier = BountyRecipe.readTier(stack);
        BountyRecipeInput input = new BountyRecipeInput(stack, type, tier);

        RecipeManager manager = level.getServer().getRecipeManager();
        boolean ok = isReward
                ? hasAnyRecipe(manager, level, JolCraftRecipes.BOUNTY_REWARD_TYPE.get(), input, BountyRewardRecipe::weight)
                : hasAnyRecipe(manager, level, JolCraftRecipes.BOUNTY_TASK_TYPE.get(), input, BountyTaskRecipe::weight);

        if (!ok) {
            return deny(ctx, null);
        }

        DwarfActionType.Subtype subtype = isReward
                ? DwarfActionType.Subtype.BOUNTY_REWARD
                : DwarfActionType.Subtype.BOUNTY;

        dwarf.getActionHelper().setAction(dwarf, subtype, ctx.player(), ctx.hand(), stack);
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult deny(DwarfInteractions.DwarfInteractionContext ctx, @Nullable String tooltipKey) {
        PlaySound.dwarfNo(ctx.dwarf());
        if (tooltipKey != null) {
            ctx.player().displayClientMessage(
                    Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY),
                    true
            );
        }
        return InteractionResult.SUCCESS;
    }

    private static <I extends RecipeInput, T extends Recipe<I>> boolean hasAnyRecipe(
            RecipeManager manager,
            ServerLevel level,
            RecipeType<T> type,
            I input,
            ToIntFunction<T> weightFn
    ) {
        return manager.recipeMap()
                .getRecipesFor(type, input, level)
                .map(RecipeHolder::value)
                .anyMatch(r -> weightFn.applyAsInt(r) > 0);
    }
}