package net.sievert.jolcraft.world.gui.menu;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.custom.MortarBlockEntity;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.slot.JolCraftResultSlot;
import net.sievert.jolcraft.world.gui.slot.JolCraftSlot;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MortarMenu extends AbstractContainerMenu {

    private static final int MORTAR_SLOT_COUNT = 5;

    private static final int DATA_RECIPE_VALID = 0;
    private static final int DATA_GRINDING_PROGRESS = 1;
    private static final int DATA_GRINDING_WORK = 2;
    private static final int DATA_COUNT = 3;

    private static final int GRIND_BUTTON_ID = 0;

    private static final int PLAYER_SLOT_START = 5;
    private static final int PLAYER_SLOT_END = 41;
    private static final int PLAYER_MAIN_END = 32;

    private final Player player;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public MortarMenu(
            int windowId,
            Inventory playerInventory,
            MortarBlockEntity blockEntity
    ) {
        this(
                windowId,
                playerInventory,
                blockEntity,
                createContainerData(
                        blockEntity,
                        playerInventory.player
                ),
                ContainerLevelAccess.create(
                        Objects.requireNonNull(
                                blockEntity.getLevel()
                        ),
                        blockEntity.getBlockPos()
                )
        );

        blockEntity.refreshCachedState();
    }

    public MortarMenu(
            int windowId,
            Inventory playerInventory
    ) {
        this(
                windowId,
                playerInventory,
                new SimpleContainer(MORTAR_SLOT_COUNT),
                new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.NULL
        );
    }

    private static ContainerData createContainerData(
            MortarBlockEntity blockEntity,
            Player player
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            throw new IllegalStateException(
                    "Mortar block entity menu requires a server player"
            );
        }

        return blockEntity.createContainerData(serverPlayer);
    }

    private MortarMenu(
            int windowId,
            Inventory playerInventory,
            Container container,
            ContainerData data,
            ContainerLevelAccess access
    ) {
        super(
                JolCraftMenuTypes.MORTAR_MENU.get(),
                windowId
        );

        this.player = playerInventory.player;
        this.data = data;
        this.access = access;

        checkContainerSize(
                container,
                MORTAR_SLOT_COUNT
        );

        checkContainerDataCount(
                data,
                DATA_COUNT
        );

        this.addSlot(new JolCraftSlot(
                container,
                MortarBlockEntity.SLOT_INPUT_1,
                16,
                32
        ));

        this.addSlot(new JolCraftSlot(
                container,
                MortarBlockEntity.SLOT_INPUT_2,
                48,
                32
        ));

        this.addSlot(new JolCraftSlot(
                container,
                MortarBlockEntity.SLOT_INPUT_3,
                80,
                32
        ));

        this.addSlot(new JolCraftResultSlot(
                this.player,
                container,
                MortarBlockEntity.SLOT_OUTPUT,
                112,
                16
        ));

        this.addSlot(new JolCraftSlot(
                container,
                MortarBlockEntity.SLOT_TOOL,
                144,
                16
        ).mayPlaceRule(stack ->
                stack.is(JolCraftTags.Items.PESTLES)
        ));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        68 + row * 18
                ));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    playerInventory,
                    col,
                    8 + col * 18,
                    126
            ));
        }

        this.addDataSlots(this.data);
    }

    public boolean isRecipeValid() {
        return this.data.get(
                DATA_RECIPE_VALID
        ) == 1;
    }

    public boolean isButtonActive() {
        return isRecipeValid()
                && hasTool();
    }

    public int getGrindingProgress() {
        return this.data.get(
                DATA_GRINDING_PROGRESS
        );
    }

    public int getGrindingWork() {
        return this.data.get(
                DATA_GRINDING_WORK
        );
    }

    public int getScaledGrindingProgress(
            int width
    ) {
        int progress = getGrindingProgress();
        int work = getGrindingWork();

        if (width <= 0
                || progress <= 0
                || work <= 0) {
            return 0;
        }

        return (int) Math.min(
                (long) width,
                (long) progress * width / work
        );
    }

    @Override
    public boolean clickMenuButton(
            Player player,
            int buttonId
    ) {
        if (!isButtonActive()
                || buttonId != GRIND_BUTTON_ID) {
            return false;
        }

        this.access.execute((level, pos) -> {
            if (player instanceof ServerPlayer serverPlayer
                    && level.getBlockEntity(pos)
                    instanceof MortarBlockEntity blockEntity) {
                blockEntity.handleGrinding(
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
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot =
                slot.getItem();

        ItemStack originalStack =
                stackInSlot.copy();

        if (index == MortarBlockEntity.SLOT_OUTPUT) {
            if (!this.moveItemStackTo(
                    stackInSlot,
                    PLAYER_SLOT_START,
                    PLAYER_SLOT_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(
                    stackInSlot,
                    originalStack
            );
        } else if (index >= PLAYER_SLOT_START) {
            if (stackInSlot.is(
                    JolCraftTags.Items.PESTLES
            )) {
                if (!this.moveItemStackTo(
                        stackInSlot,
                        MortarBlockEntity.SLOT_TOOL,
                        MortarBlockEntity.SLOT_TOOL + 1,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(
                    stackInSlot,
                    MortarBlockEntity.SLOT_INPUT_1,
                    MortarBlockEntity.SLOT_OUTPUT,
                    false
            )) {
                if (index < PLAYER_MAIN_END) {
                    if (!this.moveItemStackTo(
                            stackInSlot,
                            PLAYER_MAIN_END,
                            PLAYER_SLOT_END,
                            false
                    )) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(
                        stackInSlot,
                        PLAYER_SLOT_START,
                        PLAYER_MAIN_END,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!this.moveItemStackTo(
                stackInSlot,
                PLAYER_SLOT_START,
                PLAYER_SLOT_END,
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
                MortarBlockEntity.SLOT_TOOL
        ).getItem();
    }

    public boolean hasTool() {
        return !getToolStack().isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
                this.access,
                player,
                JolCraftBlocks.MORTAR.get()
        );
    }
}
