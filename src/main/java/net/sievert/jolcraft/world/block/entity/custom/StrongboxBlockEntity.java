package net.sievert.jolcraft.world.block.entity.custom;

    import net.minecraft.MethodsReturnNonnullByDefault;
    import net.minecraft.core.BlockPos;
    import net.minecraft.core.HolderLookup;
    import net.minecraft.core.NonNullList;
    import net.minecraft.nbt.CompoundTag;
    import net.minecraft.network.chat.Component;
    import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
    import net.minecraft.server.level.ServerPlayer;
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
    import net.sievert.jolcraft.datagen.client.language.subprovider.ContainerLangSubProvider;
    import net.sievert.jolcraft.world.block.custom.StrongboxBlock;
    import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
    import net.sievert.jolcraft.world.effect.JolCraftEffects;
    import net.sievert.jolcraft.world.gui.custom.menu.LockMenu;
    import net.sievert.jolcraft.world.gui.custom.menu.StrongboxMenu;
    import net.sievert.jolcraft.world.sound.util.PlaySound;

    import javax.annotation.Nullable;
    import javax.annotation.ParametersAreNonnullByDefault;
    import java.util.concurrent.ThreadLocalRandom;

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public class StrongboxBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity, MenuProvider {

        // ---------------------------------------------------------------------
        // Constants
        // ---------------------------------------------------------------------

        private static final int CONTAINER_SIZE = 18;

        private static final int LOCK_MAX_PROGRESS = 130;
        private static final int REROLL_MIN_TICKS = 40;
        private static final int REROLL_EXTRA_TICKS = 61; // 40..100

        // ---------------------------------------------------------------------
        // Vanilla container / lid state
        // ---------------------------------------------------------------------

        private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

        @Nullable
        private Player currentInteractingPlayer;

        private boolean allowLootUnpack = false;

        private final ChestLidController lidController = new ChestLidController();

        private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {

            @Override
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                PlaySound.strongboxOpen(level, pos);
            }

            @Override
            protected void onClose(Level level, BlockPos pos, BlockState state) {
                PlaySound.strongboxClose(level, pos);
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
                StrongboxBlockEntity.this.signalOpenCount(level, pos, state, oldCount, newCount);
            }

            @Override
            public void incrementOpeners(Player player, Level level, BlockPos pos, BlockState state) {
                super.incrementOpeners(player, level, pos, state);
                currentInteractingPlayer = player;
            }

            @Override
            public void decrementOpeners(Player player, Level level, BlockPos pos, BlockState state) {
                super.decrementOpeners(player, level, pos, state);
                if (currentInteractingPlayer == player) {
                    currentInteractingPlayer = null;
                }
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                // StrongboxMenu counts as "owning" the open count.
                // LockMenu is a session UI; it should NOT affect lid open count.
                return player.containerMenu instanceof StrongboxMenu menu
                        && menu.getBlockEntity() == StrongboxBlockEntity.this;
            }
        };

        // ---------------------------------------------------------------------
        // Lockpicking session state (server-authoritative, NOT persisted)
        // ---------------------------------------------------------------------

        // session guards
        private boolean lockSessionClearedWhileUnlocked = true;
        private boolean hasLockpickInserted = false;

        // synced-to-UI values (mirrored into LockMenu ContainerData on server)
        private int lockProgress = 0;        // 0..LOCK_MAX_PROGRESS
        private int correctButtonId = 0;     // 0..2, or 3 = "unlock mode"
        private int unlockSlotId = -1;       // 0..2 if unlock mode, else -1
        private int buttonLayerPulse = 0;    // increment to refresh client layer randomization

        // gameplay tuning (derived from player effects during server tick)
        private int decayTicks = 1;          // >= 1
        private int progressBoost = 0;       // >= 0

        // timers/counters (server-only)
        private int rerollCounter = 0;
        private int rerollTargetTicks = rollNextRerollTicks(null);
        private int decayCounter = 0;

        // ---------------------------------------------------------------------
        // Construction
        // ---------------------------------------------------------------------

        public StrongboxBlockEntity(BlockPos pos, BlockState state) {
            super(JolCraftBlockEntities.STRONGBOX.get(), pos, state);
        }

        // ---------------------------------------------------------------------
        // Public hooks (menu -> BE)
        // ---------------------------------------------------------------------

        public void clearCurrentInteractingPlayer(Player player) {
            if (this.currentInteractingPlayer == player) {
                this.currentInteractingPlayer = null;
            }
        }

        public @Nullable Player getCurrentInteractingPlayer() {
            return currentInteractingPlayer;
        }

        public void setHasLockpickInserted(boolean value) {
            if (this.hasLockpickInserted == value) return;

            this.hasLockpickInserted = value;

            if (!value) {
                resetLockSession();
                setChanged();
                return;
            }

            forceImmediateReroll();
        }


        // ---------------------------------------------------------------------
        // Lock session getters (mirrored into menu ContainerData)
        // ---------------------------------------------------------------------

        public int getLockpickProgress()       { return this.lockProgress; }
        public int getCorrectButtonId()        { return this.correctButtonId; }
        public int getUnlockSlotId()           { return this.unlockSlotId; }
        public int getButtonLayerUpdatePulse() { return this.buttonLayerPulse; }
        public int getDecayTicks()             { return this.decayTicks; }
        public int getProgressBoost()          { return this.progressBoost; }

        // ---------------------------------------------------------------------
        // Lock session core
        // ---------------------------------------------------------------------

        public void resetLockSession() {
            this.lockProgress = 0;
            this.correctButtonId = 0;
            this.unlockSlotId = -1;
            this.buttonLayerPulse = 0;

            this.decayTicks = 1;
            this.progressBoost = 0;

            this.rerollCounter = 0;
            this.decayCounter = 0;
            this.rerollTargetTicks = rollNextRerollTicks(this.level);
        }

        private boolean isLockSessionActive() {
            return this.isLocked()
                    && this.currentInteractingPlayer != null
                    && this.hasLockpickInserted;
        }

        private static int rollNextRerollTicks(@Nullable Level level) {
            int add = (level != null)
                    ? level.random.nextInt(REROLL_EXTRA_TICKS)
                    : ThreadLocalRandom.current().nextInt(REROLL_EXTRA_TICKS);
            return REROLL_MIN_TICKS + add;
        }

        private static int clampProgress(int value) {
            if (value <= 0) return 0;
            return Math.min(value, LOCK_MAX_PROGRESS);
        }

        private void bumpVisualPulse() {
            this.buttonLayerPulse++;
        }

        private void setDecayTicks(int value) {
            this.decayTicks = Math.max(1, value);
        }

        private void setProgressBoost(int value) {
            this.progressBoost = Math.max(0, value);
        }

        private void setLockpickProgress(int value) {
            this.lockProgress = clampProgress(value);
        }

        private void forceImmediateReroll() {
            this.rerollCounter = 0;
            this.rerollTargetTicks = rollNextRerollTicks(this.level);
            rerollButtons();
            bumpVisualPulse();
            setChanged();
        }

        private void rerollButtons() {
            if (this.level == null) return;

            int decay = Math.max(1, this.decayTicks);

            // IMPORTANT: guard against 101/decay becoming 0 (would crash nextInt(0)).
            int denom = Math.max(1, 101 / decay);

            // decay-scaled chance to enter "unlock mode"
            if (this.level.random.nextInt(denom) == 0) {
                this.correctButtonId = 3;
                this.unlockSlotId = this.level.random.nextInt(3);
            } else {
                this.correctButtonId = this.level.random.nextInt(3);
                this.unlockSlotId = -1;
            }
        }

        private void serverTickLockSession() {
            if (!isLockSessionActive()) {
                // If anything is mid-session but not active anymore, wipe it.
                if (this.lockProgress != 0 || this.rerollCounter != 0 || this.decayCounter != 0) {
                    resetLockSession();
                    setChanged();
                }
                return;
            }

            Player player = this.currentInteractingPlayer;
            if (player == null) return;

            var effect = player.getEffect(JolCraftEffects.LOCKPICKING);
            if (effect != null) {
                setDecayTicks(2 + effect.getAmplifier());
                setProgressBoost(10 + (effect.getAmplifier() * 10));
            } else {
                setDecayTicks(1);
                setProgressBoost(0);
            }

            // Button reroll cadence (40..100 ticks)
            this.rerollCounter++;
            if (this.rerollCounter >= this.rerollTargetTicks) {
                this.rerollCounter = 0;
                this.rerollTargetTicks = rollNextRerollTicks(this.level);
                rerollButtons();
                bumpVisualPulse();
                setChanged();
            }

            // Progress decay
            if (this.lockProgress > 0) {
                this.decayCounter++;
                if (this.decayCounter >= this.decayTicks) {
                    this.decayCounter = 0;
                    setLockpickProgress(this.lockProgress - 1);
                    setChanged();
                }
            } else {
                this.decayCounter = 0;
            }
        }

        // ---------------------------------------------------------------------
        // Server-authoritative click handling (menu forwards clicks here)
        // ---------------------------------------------------------------------

        public boolean handleLockButtonPress(ServerPlayer player, int buttonId, ItemStack lockpickSlot) {
            if (this.level == null || this.level.isClientSide) return true;

            if (!this.isLocked()) return false;
            if (this.currentInteractingPlayer != player) return false;
            if (!this.hasLockpickInserted || lockpickSlot.isEmpty()) return false;

            // --- Unlock mode ---
            boolean unlockMode = (this.correctButtonId == 3);
            if (unlockMode) {
                if (buttonId == this.unlockSlotId) {
                    player.closeContainer();

                    PlaySound.strongboxUnlock(this.level, this.getBlockPos());

                    BlockState oldState = getBlockState();
                    BlockState newState = oldState.setValue(StrongboxBlock.LOCKED, false);

                    this.level.setBlock(this.worldPosition, newState, Block.UPDATE_ALL);
                    this.level.sendBlockUpdated(this.worldPosition, oldState, newState, Block.UPDATE_ALL);

                    this.hasLockpickInserted = false;
                    resetLockSession();
                    syncToClient();
                    return true;
                }

                // Wrong in unlock mode
                if (!player.isCreative()) {
                    lockpickSlot.shrink(1);
                }
                setLockpickProgress(0);

                PlaySound.strongboxLockpickBreak(this.level, this.getBlockPos());

                forceImmediateReroll();
                return true;
            }

            // --- Normal mode ---
            if (buttonId == this.correctButtonId) {
                int gain = 10 + this.level.random.nextInt(11) + this.progressBoost; // 10..20 + boost
                setLockpickProgress(this.lockProgress + gain);

                PlaySound.strongboxLockpick(this.level, this.getBlockPos());

                if (this.lockProgress >= LOCK_MAX_PROGRESS) {
                    player.closeContainer();

                    PlaySound.strongboxUnlock(this.level, this.getBlockPos());

                    this.level.setBlock(this.worldPosition,
                            this.getBlockState().setValue(StrongboxBlock.LOCKED, false), 3);

                    this.hasLockpickInserted = false;
                    resetLockSession();
                    setChanged();
                    return true;
                }
            } else {
                if (!player.isCreative()) {
                    lockpickSlot.shrink(1);
                }
                setLockpickProgress(0);

                PlaySound.strongboxLockpickBreak(this.level, this.getBlockPos());
            }

            forceImmediateReroll();
            return true;
        }

        // ---------------------------------------------------------------------
        // Lid / openers behavior
        // ---------------------------------------------------------------------

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
            if (this.remove || player.isSpectator()) return;

            var level = this.getLevel();
            if (level == null) return;

            this.openersCounter.incrementOpeners(player, level, this.getBlockPos(), this.getBlockState());
        }

        @Override
        public void stopOpen(Player player) {
            if (this.remove || player.isSpectator()) return;

            var level = this.getLevel();
            if (level == null) return;

            this.openersCounter.decrementOpeners(player, level, this.getBlockPos(), this.getBlockState());
        }


        protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int eventId, int eventParam) {
            level.blockEvent(pos, state.getBlock(), 1, eventParam);
        }

        public void recheckOpen() {
            if (this.remove || this.level == null || this.level.isClientSide) return;

            this.openersCounter.recheckOpeners(this.level, this.getBlockPos(), this.getBlockState());
        }

        // ---------------------------------------------------------------------
        // Container / loot behavior
        // ---------------------------------------------------------------------

        @Override
        public void unpackLootTable(@Nullable Player player) {
            if (this.isLocked()) return;
            if (this.allowLootUnpack) {
                super.unpackLootTable(player);
            }
        }

        @Override
        public NonNullList<ItemStack> getItems() {
            return items;
        }

        @Override
        public void setItems(NonNullList<ItemStack> items) {
            if (this.isLocked()) {
                this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            } else {
                this.items = items;
            }
        }

        @Override
        public void clearContent() {
            if (this.isLocked()) {
                for (int i = 1; i < this.getContainerSize(); i++) {
                    this.setItem(i, ItemStack.EMPTY);
                }
            } else {
                super.clearContent();
            }
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        protected Component getDefaultName() {
            return Component.translatable(ContainerLangSubProvider.CONTAINER_STRONGBOX);
        }

        // ---------------------------------------------------------------------
        // NBT persistence (inventory + loot table only)
        // ---------------------------------------------------------------------

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

        // ---------------------------------------------------------------------
        // Menu creation / display name
        // ---------------------------------------------------------------------

        @Override
        protected AbstractContainerMenu createMenu(int id, Inventory inv) {
            this.currentInteractingPlayer = inv.player;

            if (this.isLocked()) {
                return new LockMenu(id, inv, this);
            }

            this.allowLootUnpack = true;
            return new StrongboxMenu(id, inv, this);
        }

        @Override
        public Component getDisplayName() {
            return this.isLocked()
                    ? Component.translatable(ContainerLangSubProvider.CONTAINER_STRONGBOX_LOCKED)
                    : Component.translatable(ContainerLangSubProvider.CONTAINER_STRONGBOX);
        }

        // ---------------------------------------------------------------------
        // Tick entrypoint + lock state
        // ---------------------------------------------------------------------

        public boolean isLocked() {
            return this.getBlockState().getValue(StrongboxBlock.LOCKED);
        }

        public static void tick(Level level, BlockPos pos, BlockState state, StrongboxBlockEntity strongbox) {
            if (level.isClientSide) return;

            if (!state.getValue(StrongboxBlock.LOCKED)) {
                if (!strongbox.lockSessionClearedWhileUnlocked) {
                    strongbox.resetLockSession();
                    strongbox.lockSessionClearedWhileUnlocked = true;
                    strongbox.setChanged();
                }
                return;
            }

            strongbox.lockSessionClearedWhileUnlocked = false;
            strongbox.serverTickLockSession();
        }

        // ---------------------------------------------------------------------
        // Networking / misc
        // ---------------------------------------------------------------------

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        private void syncToClient() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }