package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.profession;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.item.JolCraftItems;
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
        var stack = ctx.stack();

        if (stack.is(JolCraftItems.BOUNTY.get())) {
            BountyType requiredType = BountyHelper.getBountyType(stack);

            if (requiredType != type) {
                PlaySound.dwarfNo(dwarf);
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_WRONG_TYPE)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                return InteractionResult.SUCCESS;
            }

            dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.BOUNTY, player, hand, stack);
            return InteractionResult.SUCCESS;
        }

        if (stack.is(JolCraftItems.BOUNTY_CRATE.get())) {
            BountyType requiredType = BountyHelper.getBountyType(stack);
            Boolean complete = stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get());

            if (requiredType != type) {
                PlaySound.dwarfNo(dwarf);
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_WRONG_TYPE)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                return InteractionResult.SUCCESS;
            }

            if (complete == null || !complete) {
                PlaySound.dwarfNo(dwarf);
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_CRATE_NOT_COMPLETE)
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                return InteractionResult.SUCCESS;
            }

            dwarf.getActionHelper().setAction(dwarf, DwarfActionType.Subtype.BOUNTY_CRATE, player, hand, stack);
            return InteractionResult.SUCCESS;
        }

        PlaySound.dwarfNo(dwarf);
        return InteractionResult.FAIL;
    }
}