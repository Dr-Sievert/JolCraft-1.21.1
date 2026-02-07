package net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.handler.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.data.attachment.custom.compass.DiscoveredStructuresHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ExplorerInteractionHandler
        implements DwarfInteractions.ProfessionInteraction, DwarfInteractions.DwarfInteractionHooks {

    @Override
    public void preCore(DwarfInteractions.DwarfInteractionContext ctx) {
        var dwarf = ctx.dwarf();
        var player = ctx.player();

        int score = DiscoveredStructuresHelper.getDiscoveryScore(player);

        int currentLevel = dwarf.getMerchantLevel();
        int targetLevel = getLevelForScore(score);

        if (targetLevel > currentLevel) {
            dwarf.setMerchantLevel(targetLevel);
            dwarf.updateTrades();
            PlaySound.dwarfYes(dwarf);
        }
    }

    @Override
    public InteractionResult handle(DwarfInteractions.DwarfInteractionContext ctx) {
        // Explorer has no unique right-click action besides pre-core syncing.
        PlaySound.dwarfNo(ctx.dwarf());
        return InteractionResult.FAIL;
    }

    private static int getLevelForScore(int score) {
        for (int level = DwarfMerchantData.MAX_MERCHANT_LEVEL; level >= DwarfMerchantData.MIN_MERCHANT_LEVEL; level--) {
            int unlockScore = getUnlockScoreForLevel(level);
            if (score >= unlockScore) {
                return level;
            }
        }
        return DwarfMerchantData.MIN_MERCHANT_LEVEL;
    }

    private static int getUnlockScoreForLevel(int level) {
        if (level >= DwarfMerchantData.MAX_MERCHANT_LEVEL) {
            return DwarfMerchantData.getMaxXpPerLevel(DwarfMerchantData.MAX_MERCHANT_LEVEL - 1);
        }
        return DwarfMerchantData.getMinXpPerLevel(level);
    }
}