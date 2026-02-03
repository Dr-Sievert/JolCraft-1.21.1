package net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty.BountyType;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.*;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.bounty.BountyCrateInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.bounty.BountyInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.check.ActionCheckInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.check.BlacklistCheckInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.check.LanguageCheckInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.check.ReputationCheckInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.profession.GuardEquipInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.profession.PromoteInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.profession.SignInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.reputation.EndorseInteraction;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.interaction.type.reputation.ReputationGainInteraction;

public final class DwarfInteractionHelper {

    private static final LanguageCheckInteraction LANGUAGE_CHECK = new LanguageCheckInteraction();

    public static InteractionResult languageCheck(AbstractDwarfEntity dwarf, Player player) {
        return LANGUAGE_CHECK.handle(dwarf, player);
    }

    public static InteractionResult reputationCheck(AbstractDwarfEntity dwarf, Player player, int requiredTier) {
        return new ReputationCheckInteraction(requiredTier).handle(dwarf, player);
    }

    private static final BlacklistCheckInteraction BLACKLIST_CHECK = new BlacklistCheckInteraction();

    public static InteractionResult blacklistCheck(AbstractDwarfEntity dwarf, Player player, ItemStack stack) {
        return BLACKLIST_CHECK.handle(dwarf, player, stack);
    }

    private static final ActionCheckInteraction ACTION_CHECK = new ActionCheckInteraction();

    public static InteractionResult actionCheck(AbstractDwarfEntity dwarf, Player player) {
        return ACTION_CHECK.handle(dwarf, player);
    }

    private static final BreedInteraction BREED = new BreedInteraction();

    public static InteractionResult breed(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        return BREED.handle(dwarf, player, hand, itemstack);
    }

    private static final SignInteraction SIGN = new SignInteraction();

    public static InteractionResult sign(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        return SIGN.handle(dwarf, player, hand, itemstack);
    }

    private static final PromoteInteraction PROMOTE = new PromoteInteraction();

    public static InteractionResult promote(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        return PROMOTE.handle(dwarf, player, hand, itemstack);
    }

    private static final EndorseInteraction ENDORSE = new EndorseInteraction();

    public static InteractionResult endorse(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        return ENDORSE.handle(dwarf, player, hand, itemstack);
    }

    private static final GuardEquipInteraction GUARD_EQUIP = new GuardEquipInteraction();

    public static InteractionResult guardEquip(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        return GUARD_EQUIP.handle(dwarf, player, hand, itemstack);
    }

    private static final ReputationGainInteraction REPUTATION_GAIN = new ReputationGainInteraction();

    public static InteractionResult reputationGain(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        return REPUTATION_GAIN.handle(dwarf, player, hand, itemstack);
    }

    public static InteractionResult bounty(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack, BountyType type) {
        return new BountyInteraction(type).handle(dwarf, player, hand, itemstack);
    }

    public static InteractionResult bountyCrate(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack, BountyType type) {
        return new BountyCrateInteraction(type).handle(dwarf, player, hand, itemstack);
    }

}