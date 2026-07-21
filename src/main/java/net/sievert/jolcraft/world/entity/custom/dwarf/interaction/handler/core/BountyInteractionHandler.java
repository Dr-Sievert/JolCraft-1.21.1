package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractionOutcome.HeldItemUse;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyInteractionHandler
        implements DwarfInteractions.CoreInteraction {

    @Override
    public @NotNull DwarfInteractionOutcome handle(
            DwarfInteractions.DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return DwarfInteractionOutcome.handled();
        }

        var dwarf = ctx.dwarf();

        if (!(dwarf.level() instanceof ServerLevel level)) {
            return DwarfInteractionOutcome.pass();
        }

        ItemStack stack = ctx.stack();

        if (stack.isEmpty()) {
            return DwarfInteractionOutcome.pass();
        }

        var infoResult =
                BountyRecipe.readInfo(stack);

        if (infoResult.error().isPresent()) {
            return DwarfInteractionOutcome.pass();
        }

        var info =
                infoResult.result().orElse(null);

        if (info == null) {
            return DwarfInteractionOutcome.pass();
        }

        boolean isTask =
                BountyTaskRecipe.isTaskBountyStack(stack);

        boolean isReward =
                BountyRewardRecipe.isRewardBountyStack(stack);

        if (!isTask && !isReward) {
            return DwarfInteractionOutcome.pass();
        }

        DwarfProfession expectedProfession =
                dwarf.getProfession();

        if (expectedProfession == DwarfProfession.NONE) {
            return DwarfInteractionOutcome.pass();
        }

        if (info.type() != expectedProfession) {
            return deny(
                    ctx,
                    JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE
            );
        }

        if (isReward
                && BountyRewardRecipe
                .isIncompleteRewardBountyStack(stack)) {

            return deny(
                    ctx,
                    JolCraftLanguageKeys.TOOLTIP_BOUNTY_NOT_COMPLETE
            );
        }

        var inputResult =
                BountyRecipeInput.of(stack);

        if (inputResult.error().isPresent()) {
            return DwarfInteractionOutcome.pass();
        }

        BountyRecipeInput input =
                inputResult.result().orElse(null);

        if (input == null) {
            return DwarfInteractionOutcome.pass();
        }

        RecipeManager recipeManager =
                level.getServer().getRecipeManager();

        boolean hasRecipe =
                isReward
                        ? hasRewardRecipe(
                        recipeManager,
                        level,
                        input
                )
                        : hasTaskRecipe(
                        recipeManager,
                        level,
                        input
                );

        if (!hasRecipe) {
            return deny(
                    ctx,
                    null
            );
        }

        DwarfActionType.Subtype subtype =
                isReward
                        ? DwarfActionType.Subtype.BOUNTY_REWARD
                        : DwarfActionType.Subtype.BOUNTY;

        return DwarfInteractionOutcome.startAction(
                subtype,
                HeldItemUse.CONSUME_ONE
        );
    }

    private static @NotNull DwarfInteractionOutcome deny(
            DwarfInteractions.DwarfInteractionContext ctx,
            @Nullable String tooltipKey
    ) {
        PlaySound.dwarfNo(ctx.dwarf());

        if (tooltipKey != null) {
            ctx.player().displayClientMessage(
                    Component.translatable(
                            tooltipKey
                    ).withStyle(ChatFormatting.GRAY),
                    true
            );
        }

        return DwarfInteractionOutcome.handled();
    }

    private static boolean hasRewardRecipe(
            RecipeManager recipeManager,
            ServerLevel level,
            BountyRecipeInput input
    ) {
        return recipeManager.getRecipeFor(
                JolCraftRecipes.BOUNTY_REWARD_TYPE.get(),
                input,
                level
        ).isPresent();
    }

    private static boolean hasTaskRecipe(
            RecipeManager recipeManager,
            ServerLevel level,
            BountyRecipeInput input
    ) {
        return recipeManager.getRecipeFor(
                JolCraftRecipes.BOUNTY_TASK_TYPE.get(),
                input,
                level
        ).isPresent();
    }
}