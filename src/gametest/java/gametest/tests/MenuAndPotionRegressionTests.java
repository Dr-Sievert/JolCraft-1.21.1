package gametest.tests;

import gametest.GameTestGroup;
import gametest.util.TestPlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.StrongboxBlockEntity;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfItemCost;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfClientSideMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.world.gui.menu.LockMenu;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;

import java.util.List;
import java.util.Optional;

@GameTestGroup
public class MenuAndPotionRegressionTests {

    @GameTest(template = "basic")
    public static void replacedMerchantOffersRefreshPaidResult(GameTestHelper helper) {
        AbstractDwarfEntity dwarf = helper.spawn(JolCraftEntities.DWARF.get(), new BlockPos(1, 2, 1));
        dwarf.setNoAi(true);
        dwarf.setProfession(DwarfProfession.MERCHANT);
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "merchant_snapshot_test",
                helper.absolutePos(new BlockPos(2, 2, 1)));
        try {
            DwarfMerchantOffers offers = dwarf.getOffers();
            offers.clear();
            DwarfMerchantOffer oldOffer = merchantOffer(Items.DIAMOND);
            for (int index = 0; index < 3; index++) {
                offers.add(merchantOffer(Items.COAL));
            }
            offers.add(oldOffer);

            DwarfMerchantMenu menu = new DwarfMerchantMenu(1, player.getInventory(), dwarf);
            player.containerMenu = menu;
            dwarf.setTradingPlayer(player);
            menu.setSelectionHint(3);
            menu.getSlot(0).set(new ItemStack(Items.EMERALD, 3));
            helper.assertTrue(menu.getSlot(2).getItem().is(Items.DIAMOND),
                    "Fixture must hold a paid result from the late selected offer");
            DwarfMerchantMenu clientMenu = new DwarfMerchantMenu(1, player.getInventory(),
                    new SnapshotMerchant(player, true));
            clientMenu.setOffers(offers.copy());
            // Selecting a trade updates both menus before payment/result slot synchronization.
            clientMenu.setSelectionHint(3);
            mirrorMerchantMenu(menu, clientMenu);
            helper.assertTrue(clientMenu.getSlot(2).getItem().is(Items.DIAMOND),
                    "Client fixture must hold the same paid late offer before replacement");

            // Timed restock rebuilds this same offers list, then resends its snapshot.
            offers.clear();
            DwarfMerchantOffer newOffer = merchantOffer(Items.GOLD_INGOT);
            offers.add(newOffer);
            clientMenu.setOffers(offers.copy());
            helper.assertTrue(clientMenu.getSlot(2).getItem().is(Items.DIAMOND),
                    "A client offers snapshot must wait for the authoritative result slot");
            dwarf.resendOffersToTradingPlayer();

            helper.assertTrue(menu.getSlot(2).getItem().is(Items.GOLD_INGOT),
                    "Offer replacement must replace the paid result instead of retaining the removed diamond trade");
            helper.assertTrue(menu.getSlot(0).getItem().getCount() == 3,
                    "Refreshing offers must preserve payment");
            helper.assertTrue(menu.getSelectedOfferIndex() == 0 && clientMenu.getSelectedOfferIndex() == 0
                            && clientMenu.getSlot(2).getItem().is(Items.GOLD_INGOT),
                    "The normalized selection and current result must reach the client");
            ItemStack taken = menu.getSlot(2).remove(1);
            menu.getSlot(2).onTake(player, taken);
            helper.assertTrue(taken.is(Items.GOLD_INGOT) && newOffer.getUses() == 1 && oldOffer.getUses() == 0,
                    "Taking the refreshed result must execute only the current offer");
            helper.assertTrue(menu.getSlot(0).getItem().getCount() == 2,
                    "The current offer must charge its payment exactly once");
            helper.succeed();
        } finally {
            player.closeContainer();
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void emptyMerchantSnapshotClearsPaidResult(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        DwarfMerchantMenu menu = new DwarfMerchantMenu(1, player.getInventory(), new SnapshotMerchant(player, false));
        DwarfMerchantMenu clientMenu = new DwarfMerchantMenu(1, player.getInventory(), new SnapshotMerchant(player, true));
        DwarfMerchantOffers offers = new DwarfMerchantOffers();
        DwarfMerchantOffer removedOffer = merchantOffer(Items.DIAMOND);
        offers.add(removedOffer);
        menu.setOffers(offers);
        menu.getSlot(0).set(new ItemStack(Items.EMERALD, 2));
        clientMenu.setOffers(offers.copy());
        mirrorMerchantMenu(menu, clientMenu);
        helper.assertTrue(menu.getSlot(2).getItem().is(Items.DIAMOND),
                "Fixture must contain a paid result before empty replacement");
        helper.assertTrue(menu.getFutureTraderXp() == 4,
                "Fixture must contain nonzero future XP before empty replacement");

        menu.setOffers(new DwarfMerchantOffers());
        clientMenu.setOffers(new DwarfMerchantOffers());
        helper.assertTrue(clientMenu.getSlot(2).getItem().is(Items.DIAMOND),
                "A client empty snapshot must not predict a result-slot update");
        menu.broadcastChanges();
        helper.assertTrue(menu.getSlot(2).getItem().isEmpty() && clientMenu.getSlot(2).getItem().isEmpty(),
                "An empty offers snapshot must clear the paid result on both menus");
        helper.assertTrue(menu.getFutureTraderXp() == 0 && menu.getSelectedOfferIndex() == 0
                        && clientMenu.getSelectedOfferIndex() == 0,
                "An empty snapshot must clear future XP and normalize selection");
        helper.assertTrue(menu.getSlot(0).getItem().getCount() == 2 && removedOffer.getUses() == 0,
                "Empty replacement must preserve payment and leave the removed offer unused");
        helper.succeed();
    }

    private static void mirrorMerchantMenu(DwarfMerchantMenu serverMenu, DwarfMerchantMenu clientMenu) {
        serverMenu.setSynchronizer(new ContainerSynchronizer() {
            @Override
            public void sendInitialData(AbstractContainerMenu menu, NonNullList<ItemStack> items,
                                        ItemStack carried, int[] data) {
                for (int index = 0; index < items.size(); index++) {
                    clientMenu.getSlot(index).set(items.get(index).copy());
                }
                for (int index = 0; index < data.length; index++) {
                    clientMenu.setData(index, (short) data[index]);
                }
            }

            @Override
            public void sendSlotChange(AbstractContainerMenu menu, int slot, ItemStack stack) {
                clientMenu.getSlot(slot).set(stack.copy());
            }

            @Override
            public void sendCarriedChange(AbstractContainerMenu menu, ItemStack stack) {}

            @Override
            public void sendDataChange(AbstractContainerMenu menu, int id, int value) {
                clientMenu.setData(id, (short) value);
            }
        });
    }

    private static DwarfMerchantOffer merchantOffer(Item result) {
        return new DwarfMerchantOffer(new DwarfItemCost(Items.EMERALD, 1), Optional.empty(),
                new ItemStack(result), 0, 10, 4, 0.0F, 0,
                ResourceLocation.fromNamespaceAndPath("jolcraft", "gametest/merchant_snapshot"),
                DwarfTradeRecipe.TradeGroup.EXACT_LEVEL_POOL);
    }

    private static final class SnapshotMerchant extends DwarfClientSideMerchant {
        private final boolean clientSide;

        private SnapshotMerchant(Player player, boolean clientSide) {
            super(player);
            this.clientSide = clientSide;
        }

        @Override
        public boolean isClientSide() {
            return this.clientSide;
        }
    }

    @GameTest(template = "basic")
    public static void onlyLongFireResistanceIsShortened(GameTestHelper helper) {
        assertDuration(helper, Potions.FIRE_RESISTANCE, 3600);
        assertDuration(helper, Potions.LONG_FIRE_RESISTANCE, 7200);
        for (Holder<Potion> potion : List.of(
                Potions.LONG_NIGHT_VISION, Potions.LONG_INVISIBILITY,
                Potions.LONG_LEAPING, Potions.LONG_SWIFTNESS,
                Potions.LONG_WATER_BREATHING, Potions.LONG_STRENGTH
        )) {
            assertDuration(helper, potion, 9600);
        }
        helper.succeed();
    }

    private static void assertDuration(GameTestHelper helper, Holder<Potion> potion, int duration) {
        helper.assertTrue(potion.value().getEffects().size() == 1, "Expected one effect in " + potion);
        helper.assertTrue(potion.value().getEffects().getFirst().getDuration() == duration,
                "Unexpected duration for " + potion);
    }

    @GameTest(template = "basic")
    public static void lockButtonLayersSynchronizeAtomically(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        LayerStrongbox strongbox = new LayerStrongbox();
        LockMenu serverMenu = new LockMenu(1, player.getInventory(), strongbox);
        LockMenu clientMenu = new LockMenu(1, player.getInventory(), (BlockEntity) null);

        serverMenu.setSynchronizer(new ContainerSynchronizer() {
            @Override
            public void sendInitialData(AbstractContainerMenu menu, NonNullList<ItemStack> items,
                                        ItemStack carried, int[] data) {
                for (int index = 0; index < data.length; index++) {
                    clientMenu.setData(index, data[index]);
                }
                assertValidLayer(helper, clientMenu);
            }

            @Override
            public void sendSlotChange(AbstractContainerMenu menu, int slot, ItemStack stack) {}

            @Override
            public void sendCarriedChange(AbstractContainerMenu menu, ItemStack stack) {}

            @Override
            public void sendDataChange(AbstractContainerMenu menu, int id, int value) {
                // A client may render after any individual menu data packet.
                clientMenu.setData(id, (short) value);
                assertValidLayer(helper, clientMenu);
            }
        });

        for (int slot = 0; slot < 3; slot++) {
            strongbox.setLayer(3, slot);
            serverMenu.broadcastChanges();
            helper.assertTrue(clientMenu.getCorrectButtonId() == 3
                            && clientMenu.getUnlockSlotId() == slot,
                    "Unlock layer did not reach the client");

            strongbox.setLayer(slot, -1);
            serverMenu.broadcastChanges();
            helper.assertTrue(clientMenu.getCorrectButtonId() == slot
                            && clientMenu.getUnlockSlotId() == -1,
                    "Normal layer did not reach the client");
        }
        helper.succeed();
    }

    private static void assertValidLayer(GameTestHelper helper, LockMenu menu) {
        int correct = menu.getCorrectButtonId();
        int unlock = menu.getUnlockSlotId();
        helper.assertTrue(correct == 3 ? unlock >= 0 && unlock < 3 : unlock == -1,
                "Received a partial lock layer: correct=" + correct + " unlock=" + unlock);
    }

    private static final class LayerStrongbox extends StrongboxBlockEntity {
        private int correct;
        private int unlock = -1;
        private int pulse;

        private LayerStrongbox() {
            super(BlockPos.ZERO, JolCraftBlocks.STRONGBOX.get().defaultBlockState());
        }

        private void setLayer(int correct, int unlock) {
            this.correct = correct;
            this.unlock = unlock;
            this.pulse++;
        }

        @Override
        public int getCorrectButtonId() {
            return this.correct;
        }

        @Override
        public int getUnlockSlotId() {
            return this.unlock;
        }

        @Override
        public int getButtonLayerUpdatePulse() {
            return this.pulse;
        }
    }
}
