package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class JolCraftMenu extends AbstractContainerMenu {

    public static final int TILE_SIZE = 16;
    public static final int STANDARD_WIDTH_TILES = 11;

    public static final int PLAYER_INVENTORY_WIDTH = 176;
    public static final int PLAYER_INVENTORY_HEIGHT = 96;

    private static final int PLAYER_INVENTORY_SLOT_X = 8;
    private static final int PLAYER_INVENTORY_MAIN_Y = 14;
    private static final int PLAYER_INVENTORY_HOTBAR_Y = 72;

    private final int widthTiles;
    private final int menuHeightTiles;

    private boolean playerInventoryAppended;
    private int playerInventorySlotStart;
    private int playerInventoryMainEnd;
    private int playerInventorySlotEnd;

    protected JolCraftMenu(
            MenuType<?> menuType,
            int containerId,
            int widthTiles,
            int heightTiles
    ) {
        super(
                menuType,
                containerId
        );

        if (widthTiles < STANDARD_WIDTH_TILES) {
            throw new IllegalArgumentException(
                    "Menu width must be at least "
                            + STANDARD_WIDTH_TILES
                            + " tiles"
            );
        }

        if (heightTiles < 3) {
            throw new IllegalArgumentException(
                    "Menu height must be at least 3 tiles"
            );
        }

        this.widthTiles = widthTiles;
        this.menuHeightTiles = heightTiles;
    }

    protected final void appendPlayerInventory(
            Inventory inventory
    ) {
        if (this.playerInventoryAppended) {
            throw new IllegalStateException(
                    "Player inventory has already been appended"
            );
        }

        this.playerInventoryAppended = true;
        this.playerInventorySlotStart = this.slots.size();

        int xOffset =
                getPlayerInventoryXOffset();

        int yOffset =
                getMenuHeight();

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        xOffset
                                + PLAYER_INVENTORY_SLOT_X
                                + column * 18,
                        yOffset
                                + PLAYER_INVENTORY_MAIN_Y
                                + row * 18
                ));
            }
        }

        this.playerInventoryMainEnd = this.slots.size();

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(
                    inventory,
                    column,
                    xOffset
                            + PLAYER_INVENTORY_SLOT_X
                            + column * 18,
                    yOffset
                            + PLAYER_INVENTORY_HOTBAR_Y
            ));
        }

        this.playerInventorySlotEnd = this.slots.size();
    }

    public final int getWidthTiles() {
        return this.widthTiles;
    }

    public final int getMenuHeightTiles() {
        return this.menuHeightTiles;
    }

    public final int getWidth() {
        return this.widthTiles * TILE_SIZE;
    }

    public final int getMenuHeight() {
        return this.menuHeightTiles * TILE_SIZE;
    }

    public final int getHeight() {
        return getMenuHeight()
                + (this.playerInventoryAppended
                ? PLAYER_INVENTORY_HEIGHT
                : 0);
    }

    public final boolean hasPlayerInventory() {
        return this.playerInventoryAppended;
    }

    public final int getPlayerInventoryXOffset() {
        return (getWidth() - PLAYER_INVENTORY_WIDTH) / 2;
    }

    public final int getPlayerInventorySlotStart() {
        return this.playerInventorySlotStart;
    }

    public final int getPlayerInventoryMainEnd() {
        return this.playerInventoryMainEnd;
    }

    public final int getPlayerInventorySlotEnd() {
        return this.playerInventorySlotEnd;
    }

    public static int tile(int tile) {
        return tile * TILE_SIZE;
    }

    public static int slot(int tile) {
        return tile(tile);
    }
}