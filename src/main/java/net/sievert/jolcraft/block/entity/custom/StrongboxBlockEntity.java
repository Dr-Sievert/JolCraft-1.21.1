package net.sievert.jolcraft.block.entity.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.block.custom.StrongboxBlock;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.gui.custom.strongbox.LockMenu;
import net.sievert.jolcraft.gui.custom.strongbox.StrongboxMenu;
import net.sievert.jolcraft.sound.JolCraftSounds;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StrongboxBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity, MenuProvider {

    @Nullable
    public Player currentInteractingPlayer = null;  // Track the currently interacting player
    private NonNullList<ItemStack> items = NonNullList.withSize(18, ItemStack.EMPTY); // 2x9
    private boolean allowLootUnpack = false;


    private final ContainerOpenersCounter openersCounter =
            new ContainerOpenersCounter() {

                @Override
                protected void onOpen(Level level, BlockPos pos, BlockState state) {
                    // Play the sound when the strongbox is opened
                    StrongboxBlockEntity.playSound(level, pos, JolCraftSounds.STRONGBOX_OPEN.get());

                }

                @Override
                protected void onClose(Level level, BlockPos pos, BlockState state) {
                    StrongboxBlockEntity.playSound(level, pos, JolCraftSounds.STRONGBOX_CLOSE.get());
                }

                @Override
                protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
                    StrongboxBlockEntity.this.signalOpenCount(level, pos, state, oldCount, newCount);
                }

                // Override incrementOpeners to track players interacting with the strongbox
                @Override
                public void incrementOpeners(Player player, Level level, BlockPos pos, BlockState state) {
                    super.incrementOpeners(player, level, pos, state);
                    currentInteractingPlayer = player;  // Update the current interacting player

                }

                // Override decrementOpeners to remove players from the list
                @Override
                public void decrementOpeners(Player player, Level level, BlockPos pos, BlockState state) {
                    super.decrementOpeners(player, level, pos, state);
                    if (currentInteractingPlayer == player) {
                        currentInteractingPlayer = null;  // Reset if the player who opened it is the one closing it
                    }
                }

                @Override
                protected boolean isOwnContainer(Player player) {

                    // Handle StrongboxMenu
                    if (player.containerMenu instanceof StrongboxMenu menu) {
                        return menu.getBlockEntity() == StrongboxBlockEntity.this;
                    }

                    // Handle LockMenu
                    if (player.containerMenu instanceof LockMenu) {
                        return false;  // Prevent storage action when LockMenu is active
                    }

                    return false;
                }

            };

    private final ChestLidController lidController = new ChestLidController();

    public StrongboxBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.STRONGBOX.get(), pos, state);
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
        // Always set the loot table, regardless of locked state
        this.lootTable = lootTable;
    }

    @Override
    public void unpackLootTable(@Nullable Player player) {
        // Skip unpacking loot table if the strongbox is locked
        if (this.isLocked()) {
            return;  // Do nothing if locked
        }
        if(allowLootUnpack){
            super.unpackLootTable(player);  // Unpack loot if not locked
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        // If locked, clear the inventory and prevent setting items
        if (this.isLocked()) {
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);  // Clear inventory
        } else {
            this.items = items;  // Set items if unlocked
        }
    }

    @Override
    public void clearContent() {
        // Ensure that only non-lockpick slots are cleared when the block is locked
        if (this.isLocked()) {
            // Clear only non-lockpick slots
            for (int i = 1; i < this.getContainerSize(); i++) {
                this.setItem(i, ItemStack.EMPTY);  // Clear non-lockpick slots
            }
        } else {
            // If it's not locked, clear the entire container
            super.clearContent(); // Use the superclass logic to clear everything if not locked
        }
    }

    @Override
    public int getContainerSize() { return items.size(); }


    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.jolcraft.strongbox");
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return lidController.getOpenness(partialTicks);
    }
    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, StrongboxBlockEntity be) {
        be.lidController.tickLid();
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == 1) {
            this.lidController.shouldBeOpen(param > 0);
            return true;
        }
        return super.triggerEvent(id, param);
    }
    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            assert this.getLevel() != null;
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            assert this.getLevel() != null;
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int eventId, int eventParam) {
        Block block = state.getBlock();
        level.blockEvent(pos, block, 1, eventParam);
    }

    public void recheckOpen() {
        if (!this.remove && this.level != null && !this.level.isClientSide) {
            assert this.getLevel() != null;
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, provider);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, provider);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        if (this.isLocked()) {
            // Only allow if nobody is using, or this *is* the same player
            if (this.currentInteractingPlayer != null && this.currentInteractingPlayer != inv.player) {
                // Send message (only for the second player, not the first)
                inv.player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.strongbox.busy").withStyle(ChatFormatting.GRAY), true
                );
                return null;
            }
            // Allow access for the first or same player
            currentInteractingPlayer = inv.player;
            return new LockMenu(id, inv, this);
        } else {
            // For unlocked, you might or might not want this restriction; usually not needed:
            this.allowLootUnpack = true;
            currentInteractingPlayer = inv.player;
            return new StrongboxMenu(id, inv, this);
        }
    }

    @Override
    public Component getDisplayName() {
        // Use the helper method to check if the Strongbox is locked and return the appropriate display name
        if (this.isLocked()) {
            return Component.translatable("container.jolcraft.strongbox_locked");  // Display locked name
        } else {
            return Component.translatable("container.jolcraft.strongbox");  // Display normal name
        }
    }

    private boolean silkTouched = false;

    public void setSilkTouched(boolean value) {
        this.silkTouched = value;
    }

    public boolean wasSilkTouched() {
        return this.silkTouched;
    }

    public boolean isLocked() {
        // Get the current block state
        BlockState state = this.getBlockState();

        return state.getValue(StrongboxBlock.LOCKED);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StrongboxBlockEntity strongbox) {
    }

    public @Nullable Player getCurrentInteractingPlayer() {
        return currentInteractingPlayer;
    }


}