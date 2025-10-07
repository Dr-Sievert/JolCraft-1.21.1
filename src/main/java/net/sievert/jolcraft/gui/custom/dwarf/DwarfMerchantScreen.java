package net.sievert.jolcraft.gui.custom.dwarf;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.network.packet.C2S.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffers;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class DwarfMerchantScreen extends AbstractContainerScreen<DwarfMerchantMenu> {
    private static final ResourceLocation OUT_OF_STOCK_SPRITE = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "trade/out_of_stock");
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace(
            "container/villager/experience_bar_background"
    );
    private static final ResourceLocation EXPERIENCE_BAR_CURRENT_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/experience_bar_current");
    private static final ResourceLocation EXPERIENCE_BAR_RESULT_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/experience_bar_result");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "trade/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "trade/scroller_disabled");
    private static final ResourceLocation TRADE_ARROW_OUT_OF_STOCK_SPRITE = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "trade/trade_arrow_out_of_stock");
    private static final ResourceLocation TRADE_ARROW_SPRITE = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "trade/trade_arrow");
    private static final ResourceLocation DISCOUNT_STRIKETHRUOGH_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/discount_strikethrough");
    /**
     * The GUI texture for the villager merchant GUI.
     */
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/gui/container/dwarf_trade.png");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int MERCHANT_MENU_PART_X = 99;
    private static final int PROGRESS_BAR_X = 136;
    private static final int PROGRESS_BAR_Y = 16;
    private static final int SELL_ITEM_1_X = 5;
    private static final int SELL_ITEM_2_X = 35;
    private static final int BUY_ITEM_X = 68;
    private static final int LABEL_Y = 6;
    private static final int NUMBER_OF_OFFER_BUTTONS = 7;
    private static final int TRADE_BUTTON_X = 5;
    private static final int TRADE_BUTTON_HEIGHT = 20;
    private static final int TRADE_BUTTON_WIDTH = 88;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCROLL_BAR_TOP_POS_Y = 18;
    private static final int SCROLL_BAR_START_X = 94;
    private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
    private static final Component DEPRECATED_TOOLTIP = Component.translatable("merchant.deprecated");
    /**
     * The integer value corresponding to the currently selected merchant recipe.
     */
    private int shopItem;
    private final DwarfMerchantScreen.TradeOfferButton[] tradeOfferButtons = new DwarfMerchantScreen.TradeOfferButton[7];
    int scrollOff;
    private boolean isDragging;

    public DwarfMerchantScreen(DwarfMerchantMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    private void postButtonClick() {
        this.menu.setSelectionHint(this.shopItem);
        this.menu.tryMoveItems(this.shopItem);
        assert this.minecraft != null;
        this.minecraft.getConnection().send(new ServerboundDwarfSelectTradePacket(this.shopItem));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for (int l = 0; l < 7; l++) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(new DwarfMerchantScreen.TradeOfferButton(i + 5, k, l, p_99174_ -> {
                if (p_99174_ instanceof DwarfMerchantScreen.TradeOfferButton) {
                    this.shopItem = ((DwarfMerchantScreen.TradeOfferButton)p_99174_).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
            }));
            k += 20;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int level = this.menu.getTraderLevel();
        boolean showLvl = this.menu.showLevel();

        Component displayTitle;
        if (showLvl && level > 0 && level <= 5) {
            Component rank = Component.translatable("merchant.level." + level);
            displayTitle = Component.translatable("merchant.title", this.title, rank);
        } else {
            displayTitle = this.title;
        }
        int titleX = 49 + this.imageWidth / 2 - this.font.width(displayTitle) / 2;
        graphics.drawString(this.font, displayTitle, titleX, 6, 0xDDDDDD, false);

        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xDDDDDD, false);

        int tradesLabelWidth = this.font.width(TRADES_LABEL);
        graphics.drawString(this.font, TRADES_LABEL, 5 - tradesLabelWidth / 2 + 48, 6, 0xDDDDDD, false);

    }


    @Override
    protected void renderBg(GuiGraphics p_283072_, float p_281275_, int p_282312_, int p_282984_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_283072_.blit(RenderType::guiTextured, TEXTURE_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
        DwarfMerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int k = this.shopItem;
            if (k < 0 || k >= merchantoffers.size()) {
                return;
            }

            DwarfMerchantOffer merchantoffer = merchantoffers.get(k);
            if (merchantoffer.isOutOfStock()) {
                p_283072_.blitSprite(RenderType::guiTextured, OUT_OF_STOCK_SPRITE, this.leftPos + 83 + 99, this.topPos + 35, 28, 21);
            }
        }
    }

    private void renderProgressBar(GuiGraphics guiGraphics, int posX, int posY, DwarfMerchantOffer merchantOffer) {
        int i = this.menu.getTraderLevel();
        int j = this.menu.getTraderXp();
        if (i < 5) {
            guiGraphics.blitSprite(RenderType::guiTextured, EXPERIENCE_BAR_BACKGROUND_SPRITE, posX + 136, posY + 16, 102, 5);
            int k = VillagerData.getMinXpPerLevel(i);
            if (j >= k && VillagerData.canLevelUp(i)) {
                int l = 102;
                float f = 102.0F / (float)(VillagerData.getMaxXpPerLevel(i) - k);
                int i1 = Math.min(Mth.floor(f * (float)(j - k)), 102);
                guiGraphics.blitSprite(RenderType::guiTextured, EXPERIENCE_BAR_CURRENT_SPRITE, 102, 5, 0, 0, posX + 136, posY + 16, i1, 5);
                int j1 = this.menu.getFutureTraderXp();
                if (j1 > 0) {
                    int k1 = Math.min(Mth.floor((float)j1 * f), 102 - i1);
                    guiGraphics.blitSprite(RenderType::guiTextured, EXPERIENCE_BAR_RESULT_SPRITE, 102, 5, i1, 0, posX + 136 + i1, posY + 16, k1, 5);
                }
            }
        }
    }

    private void renderScroller(GuiGraphics guiGraphics, int posX, int posY, DwarfMerchantOffers merchantOffers) {
        int i = merchantOffers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }

            guiGraphics.blitSprite(RenderType::guiTextured, SCROLLER_SPRITE, posX + 94, posY + 18 + i1, 6, 27);
        } else {
            guiGraphics.blitSprite(RenderType::guiTextured, SCROLLER_DISABLED_SPRITE, posX + 94, posY + 18, 6, 27);
        }
    }

    @Override
    public void render(GuiGraphics p_283487_, int p_281994_, int p_282099_, float p_281815_) {
        super.render(p_283487_, p_281994_, p_282099_, p_281815_);
        DwarfMerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScroller(p_283487_, i, j, merchantoffers);
            int i1 = 0;

            for (DwarfMerchantOffer merchantoffer : merchantoffers) {
                if (!this.canScroll(merchantoffers.size()) || i1 >= this.scrollOff && i1 < 7 + this.scrollOff) {
                    ItemStack itemstack = merchantoffer.getBaseCostA();
                    ItemStack itemstack1 = merchantoffer.getCostA();
                    ItemStack itemstack2 = merchantoffer.getCostB();
                    ItemStack itemstack3 = merchantoffer.getResult();
                    p_283487_.pose().pushPose();
                    p_283487_.pose().translate(0.0F, 0.0F, 100.0F);
                    int j1 = k + 2;
                    this.renderAndDecorateCostA(p_283487_, itemstack1, itemstack, l, j1);
                    if (!itemstack2.isEmpty()) {
                        p_283487_.renderFakeItem(itemstack2, i + 5 + 35, j1);
                        p_283487_.renderItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.renderButtonArrows(p_283487_, merchantoffer, i, j1);
                    p_283487_.renderFakeItem(itemstack3, i + 5 + 68, j1);
                    p_283487_.renderItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
                    p_283487_.pose().popPose();
                    k += 20;
                    i1++;
                } else {
                    i1++;
                }
            }

            int k1 = this.shopItem;
            DwarfMerchantOffer merchantoffer1 = merchantoffers.get(k1);
            if (this.menu.showProgressBar()) {
                this.renderProgressBar(p_283487_, i, j, merchantoffer1);
            }

            if (merchantoffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, p_281994_, p_282099_) && this.menu.canRestock()) {
                p_283487_.renderTooltip(this.font, DEPRECATED_TOOLTIP, p_281994_, p_282099_);
            }

            for (DwarfMerchantScreen.TradeOfferButton merchantscreen$tradeofferbutton : this.tradeOfferButtons) {
                if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
                    merchantscreen$tradeofferbutton.renderToolTip(p_283487_, p_281994_, p_282099_);
                }

                merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < this.menu.getOffers().size();
            }
        }

        this.renderTooltip(p_283487_, p_281994_, p_282099_);
    }

    private void renderButtonArrows(GuiGraphics guiGraphics, DwarfMerchantOffer merchantOffers, int posX, int posY) {
        if (merchantOffers.isOutOfStock()) {
            guiGraphics.blitSprite(RenderType::guiTextured, TRADE_ARROW_OUT_OF_STOCK_SPRITE, posX + 5 + 35 + 20, posY + 3, 10, 9);
        } else {
            guiGraphics.blitSprite(RenderType::guiTextured, TRADE_ARROW_SPRITE, posX + 5 + 35 + 20, posY + 3, 10, 9);
        }
    }

    private void renderAndDecorateCostA(GuiGraphics guiGraphics, ItemStack realCost, ItemStack baseCost, int x, int y) {
        guiGraphics.renderFakeItem(realCost, x, y);
        if (baseCost.getCount() == realCost.getCount()) {
            guiGraphics.renderItemDecorations(this.font, realCost, x, y);
        } else {
            guiGraphics.renderItemDecorations(this.font, baseCost, x, y, baseCost.getCount() == 1 ? "1" : null);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            String count = realCost.getCount() == 1 ? "1" : String.valueOf(realCost.getCount());
            guiGraphics.drawString(font, count, (float) (x + 14) + 19 - 2 - font.width(count), (float)y + 6 + 3, 0xFFFFFF, true);
            guiGraphics.pose().popPose();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            guiGraphics.blitSprite(RenderType::guiTextured, DISCOUNT_STRIKETHRUOGH_SPRITE, x + 7, y + 12, 9, 2);
            guiGraphics.pose().popPose();
        }
    }

    private boolean canScroll(int numOffers) {
        return numOffers > 7;
    }

    @Override
    public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_, double p_295610_) {
        if (super.mouseScrolled(p_99127_, p_99128_, p_99129_, p_295610_)) {
            return true;
        } else {
            int i = this.menu.getOffers().size();
            if (this.canScroll(i)) {
                int j = i - 7;
                this.scrollOff = Mth.clamp((int)((double)this.scrollOff - p_295610_), 0, j);
            }

            return true;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int i = this.menu.getOffers().size();
        if (this.isDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(this.menu.getOffers().size())
                && mouseX > (double)(i + 94)
                && mouseX < (double)(i + 94 + 6)
                && mouseY > (double)(j + 18)
                && mouseY <= (double)(j + 18 + 139 + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        public TradeOfferButton(int x, int y, int index, Button.OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        protected static final WidgetSprites SPRITES = new WidgetSprites(
                ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "widget/button"),
                ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "widget/button_disabled"),
                ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "widget/button_highlighted")
        );

        @Override
        protected void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
            Minecraft minecraft = Minecraft.getInstance();
            p_281670_.blitSprite(
                    RenderType::guiTextured,
                    SPRITES.get(this.active, this.isHoveredOrFocused()),
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight(),
                    ARGB.white(this.alpha)
            );
            int i = getFGColor();
            this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (this.isHovered && DwarfMerchantScreen.this.menu.getOffers().size() > this.index + DwarfMerchantScreen.this.scrollOff) {
                if (mouseX < this.getX() + 20) {
                    ItemStack itemstack = DwarfMerchantScreen.this.menu.getOffers().get(this.index + DwarfMerchantScreen.this.scrollOff).getCostA();
                    guiGraphics.renderTooltip(DwarfMerchantScreen.this.font, itemstack, mouseX, mouseY);
                } else if (mouseX < this.getX() + 50 && mouseX > this.getX() + 30) {
                    ItemStack itemstack2 = DwarfMerchantScreen.this.menu.getOffers().get(this.index + DwarfMerchantScreen.this.scrollOff).getCostB();
                    if (!itemstack2.isEmpty()) {
                        guiGraphics.renderTooltip(DwarfMerchantScreen.this.font, itemstack2, mouseX, mouseY);
                    }
                } else if (mouseX > this.getX() + 65) {
                    ItemStack itemstack1 = DwarfMerchantScreen.this.menu.getOffers().get(this.index + DwarfMerchantScreen.this.scrollOff).getResult();
                    guiGraphics.renderTooltip(DwarfMerchantScreen.this.font, itemstack1, mouseX, mouseY);
                }
            }
        }
    }
}
