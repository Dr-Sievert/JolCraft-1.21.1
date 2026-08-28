package net.sievert.jolcraft.world.gui.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.network.packet.c2s.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class DwarfMerchantScreen extends AbstractContainerScreen<DwarfMerchantMenu> {

    private static ResourceLocation modTradeSprite(String sprite) {
        return JolCraftTextures.modSprite(JolCraftDictionary.TRADE, sprite);
    }

    private static ResourceLocation vanillaTradeSprite(String sprite) {
        return JolCraftTextures.vanillaSprite(JolCraftDictionary.CONTAINER, JolCraftDictionary.VILLAGER, sprite);
    }

    private static final ResourceLocation OUT_OF_STOCK_SPRITE =
            modTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.OUT,
                    JolCraftDictionary.OF,
                    JolCraftDictionary.STOCK
            ));

    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE =
            vanillaTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.EXPERIENCE,
                    JolCraftDictionary.BAR,
                    JolCraftDictionary.BACKGROUND
            ));

    private static final ResourceLocation EXPERIENCE_BAR_CURRENT_SPRITE =
            vanillaTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.EXPERIENCE,
                    JolCraftDictionary.BAR,
                    JolCraftDictionary.CURRENT
            ));

    private static final ResourceLocation EXPERIENCE_BAR_RESULT_SPRITE =
            vanillaTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.EXPERIENCE,
                    JolCraftDictionary.BAR,
                    JolCraftDictionary.RESULT
            ));

    private static final ResourceLocation SCROLLER_SPRITE =
            modTradeSprite(JolCraftDictionary.SCROLLER);

    private static final ResourceLocation SCROLLER_DISABLED_SPRITE =
            modTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.SCROLLER,
                    JolCraftDictionary.DISABLED
            ));

    private static final ResourceLocation TRADE_ARROW_OUT_OF_STOCK_SPRITE =
            modTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.TRADE,
                    JolCraftDictionary.ARROW,
                    JolCraftDictionary.OUT,
                    JolCraftDictionary.OF,
                    JolCraftDictionary.STOCK
            ));

    private static final ResourceLocation TRADE_ARROW_SPRITE =
            modTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.TRADE,
                    JolCraftDictionary.ARROW
            ));

    private static final ResourceLocation DISCOUNT_STRIKETHROUGH_SPRITE =
            vanillaTradeSprite(JolCraftStrings.underscored(
                    JolCraftDictionary.DISCOUNT,
                    JolCraftDictionary.STRIKETHROUGH
            ));

    private static final ResourceLocation TEXTURE_LOCATION =
            JolCraftTextures.mod(JolCraftTextures.container(JolCraftRecipeIds.DWARF_TRADE));

    private static final Component TRADES_LABEL = Component.translatable(JolCraftLanguageKeys.MERCHANT_TRADES);
    private static final Component DEPRECATED_TOOLTIP = Component.translatable(JolCraftLanguageKeys.MERCHANT_DEPRECATED);

    private int shopItem;
    private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
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

        if (this.minecraft == null) {
            return;
        }

        var connection = this.minecraft.getConnection();
        if (connection == null) {
            return;
        }

        connection.send(new ServerboundDwarfSelectTradePacket(this.shopItem));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for (int l = 0; l < 7; l++) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(new TradeOfferButton(i + 5, k, l, button -> {
                if (button instanceof TradeOfferButton tradeButton) {
                    this.shopItem = tradeButton.getIndex() + this.scrollOff;
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
            Component rank = Component.translatable(DwarfMerchantData.Level.langKeyFromId(level));
            displayTitle = Component.translatable(JolCraftLanguageKeys.MERCHANT_TITLE, this.title, rank);
        } else {
            displayTitle = this.title;
        }

        int titleX = 49 + this.imageWidth / 2 - this.font.width(displayTitle) / 2;
        graphics.drawString(this.font, displayTitle, titleX, 6, JolCraftColors.rgb("DDDDDD"), false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, JolCraftColors.rgb("DDDDDD"), false);

        int tradesLabelWidth = this.font.width(TRADES_LABEL);
        graphics.drawString(this.font, TRADES_LABEL, 5 - tradesLabelWidth / 2 + 48, 6, JolCraftColors.rgb("DDDDDD"), false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);

        DwarfMerchantOffers offers = this.menu.getOffers();
        if (!offers.isEmpty()) {
            int k = this.shopItem;
            if (k < 0 || k >= offers.size()) {
                return;
            }

            DwarfMerchantOffer offer = offers.get(k);
            if (offer.isOutOfStock()) {
                graphics.blitSprite(OUT_OF_STOCK_SPRITE, this.leftPos + 83 + 99, this.topPos + 35, 0, 28, 21);
            }
        }
    }

    private void renderProgressBar(GuiGraphics graphics, int posX, int posY, DwarfMerchantOffer merchantOffer) {
        int i = this.menu.getTraderLevel();
        int j = this.menu.getTraderXp();
        if (i < 5) {
            graphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, posX + 136, posY + 16, 0, 102, 5);
            int k = VillagerData.getMinXpPerLevel(i);
            if (j >= k && VillagerData.canLevelUp(i)) {
                float f = 102.0F / (float) (VillagerData.getMaxXpPerLevel(i) - k);
                int i1 = Math.min(Mth.floor(f * (float) (j - k)), 102);
                graphics.blitSprite(EXPERIENCE_BAR_CURRENT_SPRITE, 102, 5, 0, 0, posX + 136, posY + 16, 0, i1, 5);

                int j1 = this.menu.getFutureTraderXp();
                if (j1 > 0) {
                    int k1 = Math.min(Mth.floor((float) j1 * f), 102 - i1);
                    graphics.blitSprite(EXPERIENCE_BAR_RESULT_SPRITE, 102, 5, i1, 0, posX + 136 + i1, posY + 16, 0, k1, 5);
                }
            }
        }
    }

    private void renderScroller(GuiGraphics graphics, int posX, int posY, DwarfMerchantOffers offers) {
        int i = offers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }

            graphics.blitSprite(SCROLLER_SPRITE, posX + 94, posY + 18 + i1, 0, 6, 27);
        } else {
            graphics.blitSprite(SCROLLER_DISABLED_SPRITE, posX + 94, posY + 18, 0, 6, 27);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        DwarfMerchantOffers offers = this.menu.getOffers();

        if (!offers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;

            this.renderScroller(graphics, i, j, offers);

            int i1 = 0;
            for (DwarfMerchantOffer offer : offers) {
                if (!this.canScroll(offers.size()) || i1 >= this.scrollOff && i1 < 7 + this.scrollOff) {
                    ItemStack baseCost = offer.getBaseCostA();
                    ItemStack realCost = offer.getCostA();
                    ItemStack costB = offer.getCostB();
                    ItemStack result = offer.getResult();

                    graphics.pose().pushPose();
                    graphics.pose().translate(0.0F, 0.0F, 100.0F);

                    int j1 = k + 2;
                    this.renderAndDecorateCostA(graphics, realCost, baseCost, l, j1);

                    if (!costB.isEmpty()) {
                        graphics.renderFakeItem(costB, i + 5 + 35, j1);
                        graphics.renderItemDecorations(this.font, costB, i + 5 + 35, j1);
                    }

                    this.renderButtonArrows(graphics, offer, i, j1);
                    graphics.renderFakeItem(result, i + 5 + 68, j1);
                    graphics.renderItemDecorations(this.font, result, i + 5 + 68, j1);
                    graphics.pose().popPose();

                    k += 20;
                }
                i1++;
            }

            int k1 = this.shopItem;
            DwarfMerchantOffer selectedOffer = offers.get(k1);
            if (this.menu.showProgressBar()) {
                this.renderProgressBar(graphics, i, j, selectedOffer);
            }

            if (selectedOffer.isOutOfStock() && this.isHovering(186, 35, 22, 21, mouseX, mouseY) && this.menu.canRestock()) {
                graphics.renderTooltip(this.font, DEPRECATED_TOOLTIP, mouseX, mouseY);
            }

            for (TradeOfferButton tradeOfferButton : this.tradeOfferButtons) {
                if (tradeOfferButton.isHoveredOrFocused()) {
                    tradeOfferButton.renderToolTip(graphics, mouseX, mouseY);
                }

                tradeOfferButton.visible = tradeOfferButton.index < this.menu.getOffers().size();
            }

            RenderSystem.enableDepthTest();
        }

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private void renderButtonArrows(GuiGraphics graphics, DwarfMerchantOffer offer, int posX, int posY) {
        RenderSystem.enableBlend();
        if (offer.isOutOfStock()) {
            graphics.blitSprite(TRADE_ARROW_OUT_OF_STOCK_SPRITE, posX + 5 + 35 + 20, posY + 3, 0, 10, 9);
        } else {
            graphics.blitSprite(TRADE_ARROW_SPRITE, posX + 5 + 35 + 20, posY + 3, 0, 10, 9);
        }
    }

    private void renderAndDecorateCostA(GuiGraphics graphics, ItemStack realCost, ItemStack baseCost, int x, int y) {
        graphics.renderFakeItem(realCost, x, y);
        if (baseCost.getCount() == realCost.getCount()) {
            graphics.renderItemDecorations(this.font, realCost, x, y);
        } else {
            graphics.renderItemDecorations(this.font, baseCost, x, y, baseCost.getCount() == 1 ? "1" : null);

            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 200.0F);

            String count = realCost.getCount() == 1 ? "1" : String.valueOf(realCost.getCount());
            this.font.drawInBatch(
                    count,
                    (float) (x + 14) + 19 - 2 - this.font.width(count),
                    (float) y + 6 + 3,
                    JolCraftColors.rgb("FFFFFF"),
                    true,
                    graphics.pose().last().pose(),
                    graphics.bufferSource(),
                    net.minecraft.client.gui.Font.DisplayMode.NORMAL,
                    0,
                    15728880,
                    false
            );

            graphics.pose().popPose();
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 300.0F);
            graphics.blitSprite(DISCOUNT_STRIKETHROUGH_SPRITE, x + 7, y + 12, 0, 9, 2);
            graphics.pose().popPose();
        }
    }

    private boolean canScroll(int numOffers) {
        return numOffers > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int i = this.menu.getOffers().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - scrollY), 0, j);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int i = this.menu.getOffers().size();
        if (this.isDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float) mouseY - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
            f = f * (float) l + 0.5F;
            this.scrollOff = Mth.clamp((int) f, 0, l);
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
                && mouseX > (double) (i + 94)
                && mouseX < (double) (i + 94 + 6)
                && mouseY > (double) (j + 18)
                && mouseY <= (double) (j + 18 + 139 + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        protected static final WidgetSprites SPRITES = new WidgetSprites(
                JolCraft.location("widget/button"),
                JolCraft.location("widget/button_disabled"),
                JolCraft.location("widget/button_highlighted")
        );

        public TradeOfferButton(int x, int y, int index, OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            graphics.blitSprite(
                    SPRITES.get(this.active, this.isHoveredOrFocused()),
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight()
            );
            int color = JolCraftColors.withAlpha(this.getFGColor(), Mth.ceil(this.alpha * 255.0F));
            this.renderString(graphics, minecraft.font, color);
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(GuiGraphics graphics, int mouseX, int mouseY) {
            if (this.isHovered && DwarfMerchantScreen.this.menu.getOffers().size() > this.index + DwarfMerchantScreen.this.scrollOff) {
                if (mouseX < this.getX() + 20) {
                    ItemStack itemStack = DwarfMerchantScreen.this.menu.getOffers().get(this.index + DwarfMerchantScreen.this.scrollOff).getCostA();
                    graphics.renderTooltip(DwarfMerchantScreen.this.font, itemStack, mouseX, mouseY);
                } else if (mouseX < this.getX() + 50 && mouseX > this.getX() + 30) {
                    ItemStack itemStack = DwarfMerchantScreen.this.menu.getOffers().get(this.index + DwarfMerchantScreen.this.scrollOff).getCostB();
                    if (!itemStack.isEmpty()) {
                        graphics.renderTooltip(DwarfMerchantScreen.this.font, itemStack, mouseX, mouseY);
                    }
                } else if (mouseX > this.getX() + 65) {
                    ItemStack itemStack = DwarfMerchantScreen.this.menu.getOffers().get(this.index + DwarfMerchantScreen.this.scrollOff).getResult();
                    graphics.renderTooltip(DwarfMerchantScreen.this.font, itemStack, mouseX, mouseY);
                }
            }
        }
    }
}
