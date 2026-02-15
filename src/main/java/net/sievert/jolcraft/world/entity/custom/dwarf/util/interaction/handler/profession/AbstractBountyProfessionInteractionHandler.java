package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractBountyProfessionInteractionHandler implements DwarfInteractions.ProfessionInteraction {

    private final BountyType type;

    protected AbstractBountyProfessionInteractionHandler(BountyType type) {
        this.type = type;
    }

    @Override
    public final InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        var player = ctx.player();
        var hand = ctx.hand();
        ItemStack stack = ctx.stack();

        if (!(dwarf.level() instanceof ServerLevel level)) {
            return InteractionResult.PASS;
        }

        if (stack.isEmpty()) {
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        BountyType stackType = BountyRecipe.readType(stack);
        BountyTier stackTier = BountyRecipe.readTier(stack);

        if (stackType == BountyType.UNKNOWN || stackTier == BountyTier.UNKNOWN) {
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        if (stackType != type) {
            PlaySound.dwarfNo(dwarf);
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE)
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        boolean complete = BountyRewardRecipe.isCompletedBountyStack(stack);

        if (!complete && isActiveBountyStack(stack)) {
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        BountyRecipeInput input = new BountyRecipeInput(stack, stackType, stackTier);

        RecipeManager manager = level.getServer().getRecipeManager();
        boolean hasMatchingRecipe = complete
                ? hasAnyRewardRecipe(manager, level, input)
                : hasAnyTaskRecipe(manager, level, input);

        if (!hasMatchingRecipe) {
            PlaySound.dwarfNo(dwarf);
            return InteractionResult.SUCCESS;
        }

        DwarfActionType.Subtype subtype = complete
                ? DwarfActionType.Subtype.BOUNTY_REWARD
                : DwarfActionType.Subtype.BOUNTY;

        dwarf.getActionHelper().setAction(dwarf, subtype, player, hand, stack);
        return InteractionResult.SUCCESS;
    }

    private static boolean hasAnyTaskRecipe(RecipeManager manager, ServerLevel level, BountyRecipeInput input) {
        return manager.recipeMap()
                .getRecipesFor(JolCraftRecipes.BOUNTY_TASK_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .anyMatch(r -> r.weight() > 0);
    }

    private static boolean hasAnyRewardRecipe(RecipeManager manager, ServerLevel level, BountyRecipeInput input) {
        return manager.recipeMap()
                .getRecipesFor(JolCraftRecipes.BOUNTY_REWARD_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .anyMatch(r -> r.weight() > 0);
    }

    private static boolean isActiveBountyStack(ItemStack stack) {
        if (stack.has(JolCraftDataComponents.BOUNTY_DATA.get())) return true;

        int fill = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
        if (fill != 0) return true;

        return Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()));
    }
}