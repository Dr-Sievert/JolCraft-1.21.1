package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.behavior.profession;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.custom.compass.DiscoveredStructuresHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.behavior.DwarfProfessionBehavior;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public final class ExplorerBehavior implements DwarfProfessionBehavior {

    public static final ExplorerBehavior INSTANCE = new ExplorerBehavior();

    private ExplorerBehavior() {}

    @Override
    public void onBeforeTradeScreen(AbstractDwarfEntity dwarf, Player player, InteractionHand hand) {
        int score = DiscoveredStructuresHelper.getDiscoveryScore(player);

        int currentLevel = dwarf.getMerchantLevel();
        int targetLevel = getLevelForScore(score);

        if (targetLevel > currentLevel) {
            dwarf.setMerchantLevel(targetLevel);
            dwarf.updateTrades();
            PlaySound.dwarfYes(dwarf);
        }
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
