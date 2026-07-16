package net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyInteractionHandler implements DwarfInteractions.CoreInteraction {

    @Override
    public @NotNull InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        if (!(dwarf.level() instanceof ServerLevel level)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = ctx.stack();
        if (stack.isEmpty()) {
            return InteractionResult.PASS;
        }

        var infoRes = BountyRecipe.readInfo(stack);
        if (infoRes.error().isPresent()) {
            return InteractionResult.PASS;
        }

        var info = infoRes.result().orElse(null);
        if (info == null) {
            return InteractionResult.PASS;
        }

        boolean isTask = BountyTaskRecipe.isTaskBountyStack(stack);
        boolean isReward = BountyRewardRecipe.isRewardBountyStack(stack);
        if (!isTask && !isReward) {
            return InteractionResult.PASS;
        }

        DwarfProfession expected = dwarf.getProfession();
        if (expected == DwarfProfession.NONE) {
            return InteractionResult.PASS;
        }

        if (info.type() != expected) {
            return deny(ctx, JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE);
        }

        if (isReward && BountyRewardRecipe.isIncompleteRewardBountyStack(stack)) {
            return deny(ctx, JolCraftLanguageKeys.TOOLTIP_BOUNTY_NOT_COMPLETE);
        }

        var inputRes = BountyRecipeInput.of(stack);

        if (inputRes.error().isPresent()) {
            return InteractionResult.PASS;
        }

        BountyRecipeInput input = inputRes.result().orElse(null);
        if (input == null) {
            return InteractionResult.PASS;
        }

        RecipeManager manager = level.getServer().getRecipeManager();
        boolean ok = isReward
                ? hasAnyRewardRecipe(manager, level, input)
                : hasAnyTaskRecipe(manager, level, input);

        if (!ok) {
            return deny(ctx, null);
        }

        DwarfActionType.Subtype subtype = isReward
                ? DwarfActionType.Subtype.BOUNTY_REWARD
                : DwarfActionType.Subtype.BOUNTY;

        dwarf.getActionHelper().setAction(dwarf, subtype, ctx.player(), ctx.hand(), stack);
        return InteractionResult.SUCCESS;
    }

    private static @NotNull InteractionResult deny(
            DwarfInteractions.DwarfInteractionContext ctx,
            @Nullable String tooltipKey
    ) {
        PlaySound.dwarfNo(ctx.dwarf());

        if (tooltipKey != null) {
            ctx.player().displayClientMessage(
                    Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY),
                    true
            );
        }

        return InteractionResult.SUCCESS;
    }

    private static boolean hasAnyRewardRecipe(
            RecipeManager manager,
            ServerLevel level,
            BountyRecipeInput input
    ) {
        return manager.getRecipeFor(JolCraftRecipes.BOUNTY_REWARD_TYPE.get(), input, level).isPresent();
    }

    private static boolean hasAnyTaskRecipe(
            RecipeManager manager,
            ServerLevel level,
            BountyRecipeInput input
    ) {
        return manager.getRecipeFor(JolCraftRecipes.BOUNTY_TASK_TYPE.get(), input, level).isPresent();
    }
}