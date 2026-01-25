package net.sievert.jolcraft.event.game.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.item.custom.tool.SpannerItem;
import net.sievert.jolcraft.world.entity.util.dwarf.SalvageLootHelper;

import java.util.List;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftItemEvents {

    @SubscribeEvent
    public static void onSpannerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack main = event.getItemStack();
        ItemStack offhand = player.getOffhandItem();

        boolean mainIsSpanner = main.getItem() instanceof SpannerItem;
        boolean offIsSpanner = offhand.getItem() instanceof SpannerItem;
        boolean mainIsScrap = main.is(JolCraftTags.Items.GLOBAL_SALVAGE);
        boolean offIsScrap = offhand.is(JolCraftTags.Items.GLOBAL_SALVAGE);

        if (!((mainIsSpanner && offIsScrap) || (offIsSpanner && mainIsScrap))) return;

        if (!level.isClientSide) {
            ItemStack scrap = mainIsScrap ? main : offhand;
            ItemStack spanner = mainIsSpanner ? main : offhand;
            EquipmentSlot spannerSlot = mainIsSpanner ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            InteractionHand swingHand = mainIsSpanner ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

            List<ItemStack> loot = SalvageLootHelper.generateSalvageLoot(scrap);
            loot.forEach(stack -> level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                    level,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    stack
            )));

            if (!player.isCreative()) {
                scrap.shrink(1);
                spanner.hurtAndBreak(1, player, spannerSlot);
            }
            player.swing(swingHand, true);
            level.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.5F);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
