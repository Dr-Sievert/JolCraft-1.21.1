package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.LapidaryBenchBlockEntity;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.slot.JolCraftResultSlot;
import net.sievert.jolcraft.world.gui.slot.JolCraftSlot;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchMenu extends JolCraftMenu {

    private static final int DATA_RECIPE_VALID = 0;
    private static final int DATA_ACTION_ID = 1;
    private static final int DATA_COUNT = 2;

    private static final int MENU_HEIGHT_TILES = 4;

    private final Player player;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public LapidaryBenchMenu(
            int windowId,
            Inventory playerInventory,
            LapidaryBenchBlockEntity blockEntity
    ) {
        this(
                windowId,
                playerInventory,
                blockEntity,
                blockEntity.getContainerData(),
                ContainerLevelAccess.create(
                        Objects.requireNonNull(
                                blockEntity.getLevel()
                        ),
                        blockEntity.getBlockPos()
                )
        );

        if (playerInventory.player
                instanceof ServerPlayer serverPlayer) {
            blockEntity.refreshCachedState(
                    serverPlayer
            );
        }
    }

    public LapidaryBenchMenu(
            int windowId,
            Inventory playerInventory
    ) {
        this(
                windowId,
                playerInventory,
                new SimpleContainer(3),
                new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.NULL
        );
    }

    private LapidaryBenchMenu(
            int windowId,
            Inventory playerInventory,
            Container container,
            ContainerData data,
            ContainerLevelAccess access
    ) {
        super(
                JolCraftMenuTypes.LAPIDARY_BENCH_MENU.get(),
                windowId,
                STANDARD_WIDTH_TILES,
                MENU_HEIGHT_TILES
        );

        this.access = access;
        this.player = playerInventory.player;
        this.data = data;

        checkContainerSize(
                container,
                3
        );

        checkContainerDataCount(
                data,
                DATA_COUNT
        );

        this.addSlot(new JolCraftSlot(
                container,
                LapidaryBenchBlockEntity.SLOT_INPUT,
                slot(3),
                slot(3)
        ).onSlotChanged(
                this::refreshBenchState
        ));

        this.addSlot(new JolCraftSlot(
                container,
                LapidaryBenchBlockEntity.SLOT_TOOL,
                slot(5),
                slot(3)
        ).onSlotChanged(
                this::refreshBenchState
        ).mayPlaceRule(stack ->
                stack.is(
                        JolCraftTags.Items.ARTISAN_HAMMERS
                )
                        || stack.is(
                        JolCraftTags.Items.CHISELS
                )
        ));

        this.addSlot(new JolCraftResultSlot(
                this.player,
                container,
                LapidaryBenchBlockEntity.SLOT_OUTPUT,
                slot(9),
                slot(3)
        ));

        appendPlayerInventory(
                playerInventory
        );

        this.addDataSlots(
                this.data
        );
    }

    private void refreshBenchState() {
        if (!(this.player
                instanceof ServerPlayer serverPlayer)) {
            return;
        }

        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos)
                    instanceof LapidaryBenchBlockEntity blockEntity) {
                blockEntity.refreshCachedState(
                        serverPlayer
                );
            }
        });
    }

    public boolean isRecipeValid() {
        return this.data.get(
                DATA_RECIPE_VALID
        ) == 1;
    }

    public boolean isButtonActive() {
        return isRecipeValid();
    }

    @Override
    public boolean clickMenuButton(
            Player player,
            int buttonId
    ) {
        if (!isButtonActive()
                || buttonId
                != this.data.get(DATA_ACTION_ID)) {
            return false;
        }

        this.access.execute((level, pos) -> {
            if (player
                    instanceof ServerPlayer serverPlayer
                    && level.getBlockEntity(pos)
                    instanceof LapidaryBenchBlockEntity blockEntity) {
                blockEntity.handleAction(
                        serverPlayer
                );
            }
        });

        return true;
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {
        Slot slot =
                this.slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot =
                slot.getItem();

        ItemStack originalStack =
                stackInSlot.copy();

        if (index
                == LapidaryBenchBlockEntity.SLOT_OUTPUT) {
            if (!this.moveItemStackTo(
                    stackInSlot,
                    getPlayerInventorySlotStart(),
                    getPlayerInventorySlotEnd(),
                    true
            )) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(
                    stackInSlot,
                    originalStack
            );
        } else if (index >= getPlayerInventorySlotStart()) {
            if (!this.moveItemStackTo(
                    stackInSlot,
                    LapidaryBenchBlockEntity.SLOT_TOOL,
                    LapidaryBenchBlockEntity.SLOT_TOOL + 1,
                    false
            )) {
                if (!this.moveItemStackTo(
                        stackInSlot,
                        LapidaryBenchBlockEntity.SLOT_INPUT,
                        LapidaryBenchBlockEntity.SLOT_INPUT + 1,
                        false
                )) {
                    if (index < getPlayerInventoryMainEnd()) {
                        if (!this.moveItemStackTo(
                                stackInSlot,
                                getPlayerInventoryMainEnd(),
                                getPlayerInventorySlotEnd(),
                                false
                        )) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(
                            stackInSlot,
                            getPlayerInventorySlotStart(),
                            getPlayerInventoryMainEnd(),
                            false
                    )) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        } else if (!this.moveItemStackTo(
                stackInSlot,
                getPlayerInventorySlotStart(),
                getPlayerInventorySlotEnd(),
                false
        )) {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(
                    ItemStack.EMPTY
            );
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount()
                == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(
                player,
                stackInSlot
        );

        return originalStack;
    }

    public ItemStack getToolStack() {
        return this.slots.get(
                LapidaryBenchBlockEntity.SLOT_TOOL
        ).getItem();
    }

    public boolean hasTool() {
        return !getToolStack().isEmpty();
    }

    public int getActionIdForTool() {
        return this.data.get(
                DATA_ACTION_ID
        );
    }

    @Override
    public boolean stillValid(
            Player player
    ) {
        return stillValid(
                this.access,
                player,
                JolCraftBlocks.LAPIDARY_BENCH.get()
        );
    }
}