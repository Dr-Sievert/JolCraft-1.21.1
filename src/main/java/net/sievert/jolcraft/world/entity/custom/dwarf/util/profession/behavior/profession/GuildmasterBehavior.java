package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.behavior.profession;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.behavior.DwarfProfessionBehavior;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;

public final class GuildmasterBehavior implements DwarfProfessionBehavior {

    public static final GuildmasterBehavior INSTANCE = new GuildmasterBehavior();

    private GuildmasterBehavior() {}

    @Override
    public void onBeforeTradeScreen(AbstractDwarfEntity dwarf, Player player, InteractionHand hand) {

        int reputationTier = DwarvenReputationHelper.getTier(player);

        int desiredLevel = Math.min(
                reputationTier + 1,
                DwarfMerchantData.MAX_MERCHANT_LEVEL
        );

        int currentLevel = dwarf.getMerchantLevel();

        if (currentLevel < desiredLevel) {

            if (dwarf.getOffers().isEmpty()) {
                dwarf.updateTrades();
            }

            for (int level = currentLevel; level < desiredLevel; level++) {
                dwarf.increaseMerchantCareer();
            }
        }
    }
}
