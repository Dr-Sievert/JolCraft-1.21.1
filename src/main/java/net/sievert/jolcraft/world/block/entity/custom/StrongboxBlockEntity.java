package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.custom.StrongboxBlock;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.gui.menu.LockMenu;
import net.sievert.jolcraft.world.gui.menu.StrongboxMenu;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ThreadLocalRandom;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StrongboxBlockEntity extends RandomizableContainerBlockEntity implements TickingBlockEntity, LidBlockEntity, MenuProvider {

    private static final int CONTAINER_SIZE = 18;
    private static final int LOCK_MAX_PROGRESS = 130;
    private static final int REROLL_MIN_TICKS = 40;
    private static final int REROLL_EXTRA_TICKS = 61;

    private static final double BASE_UNLOCK_CHANCE = 0.01D;
    private static final double UNLOCK_CHANCE_PER_LOCKPICKING = 0.01D;

    private static final double MIN_DECAY_RATE = 0.40D;
    private static final double DECAY_RATE_SCALE = 1.20D;
    private static final double DECAY_RATE_OFFSET = 2.0D;

    private static final double MAX_PROGRESS_BONUS = 10.0D;
    private static final double PROGRESS_BONUS_DECAY = 0.5D;

    private NonNullList<ItemStack> items =
            NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    @Nullable
    private Player currentInteractingPlayer;

    private final ChestLidController lidController =
            new ChestLidController();

    private final ContainerOpenersCounter openersCounter =
            new ContainerOpenersCounter() {

                @Override
                protected void onOpen(
                        Level level,
                        BlockPos pos,
                        BlockState state
                ) {
                    PlaySound.strongboxOpen(level, pos);
                }

                @Override
                protected void onClose(
                        Level level,
                        BlockPos pos,
                        BlockState state
                ) {
                    PlaySound.strongboxClose(level, pos);
                }

                @Override
                protected void openerCountChanged(
                        Level level,
                        BlockPos pos,
                        BlockState state,
                        int oldCount,
                        int newCount
                ) {
                    StrongboxBlockEntity.this.signalOpenCount(
                            level,
                            pos,
                            state,
                            newCount
                    );
                }

                @Override
                public void incrementOpeners(
                        Player player,
                        Level level,
                        BlockPos pos,
                        BlockState state
                ) {
                    super.incrementOpeners(
                            player,
                            level,
                            pos,
                            state
                    );

                    currentInteractingPlayer = player;
                }

                @Override
                public void decrementOpeners(
                        Player player,
                        Level level,
                        BlockPos pos,
                        BlockState state
                ) {
                    super.decrementOpeners(
                            player,
                            level,
                            pos,
                            state
                    );

                    if (currentInteractingPlayer != null
                            && currentInteractingPlayer.getUUID().equals(
                            player.getUUID()
                    )) {
                        currentInteractingPlayer = null;
                    }
                }

                @Override
                protected boolean isOwnContainer(Player player) {
                    return player.containerMenu instanceof StrongboxMenu menu
                            && menu.getBlockEntity()
                            == StrongboxBlockEntity.this;
                }
            };

    private boolean lockSessionClearedWhileUnlocked = true;
    private boolean hasLockpickInserted = false;

    private int lockProgress = 0;
    private int correctButtonId = 0;
    private int unlockSlotId = -1;
    private int buttonLayerPulse = 0;

    private int rerollCounter = 0;
    private int rerollTargetTicks =
            rollNextRerollTicks(null);

    private double decayProgress = 0.0D;

    public StrongboxBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                JolCraftBlockEntities.STRONGBOX.get(),
                pos,
                state
        );
    }

    public void clearCurrentInteractingPlayer(Player player) {
        if (this.currentInteractingPlayer != null
                && this.currentInteractingPlayer.getUUID().equals(
                player.getUUID()
        )) {
            this.currentInteractingPlayer = null;
        }
    }

    public @Nullable Player getCurrentInteractingPlayer() {
        return currentInteractingPlayer;
    }

    public void setHasLockpickInserted(boolean value) {
        if (this.hasLockpickInserted == value) {
            return;
        }

        this.hasLockpickInserted = value;

        if (!value) {
            resetLockSession();
            return;
        }

        forceImmediateReroll();
    }

    public int getLockpickProgress() {
        return this.lockProgress;
    }

    public int getCorrectButtonId() {
        return this.correctButtonId;
    }

    public int getUnlockSlotId() {
        return this.unlockSlotId;
    }

    public int getButtonLayerUpdatePulse() {
        return this.buttonLayerPulse;
    }

    public void resetLockSession() {
        this.lockProgress = 0;
        this.correctButtonId = 0;
        this.unlockSlotId = -1;
        this.buttonLayerPulse = 0;

        this.rerollCounter = 0;
        this.decayProgress = 0.0D;
        this.rerollTargetTicks =
                rollNextRerollTicks(this.level);
    }

    private boolean isLockSessionActive() {
        return this.isLocked()
                && this.currentInteractingPlayer != null
                && this.hasLockpickInserted;
    }

    private static int rollNextRerollTicks(
            @Nullable Level level
    ) {
        int add = level != null
                ? level.random.nextInt(REROLL_EXTRA_TICKS)
                : ThreadLocalRandom.current()
                .nextInt(REROLL_EXTRA_TICKS);

        return REROLL_MIN_TICKS + add;
    }

    private static int clampProgress(int value) {
        if (value <= 0) {
            return 0;
        }

        return Math.min(
                value,
                LOCK_MAX_PROGRESS
        );
    }

    private static double getUnlockChance(
            double lockpicking
    ) {
        return Math.min(
                1.0D,
                BASE_UNLOCK_CHANCE
                        + UNLOCK_CHANCE_PER_LOCKPICKING
                        * Math.max(0.0D, lockpicking)
        );
    }

    private static double getDecayRate(
            double lockpicking
    ) {
        return MIN_DECAY_RATE
                + DECAY_RATE_SCALE
                / (Math.max(0.0D, lockpicking)
                + DECAY_RATE_OFFSET);
    }

    private static double getProgressBonus(
            double lockpicking
    ) {
        return MAX_PROGRESS_BONUS
                * (1.0D - Math.pow(
                PROGRESS_BONUS_DECAY,
                Math.max(0.0D, lockpicking)
        ));
    }

    private void bumpVisualPulse() {
        this.buttonLayerPulse++;
    }

    private void setLockpickProgress(int value) {
        this.lockProgress =
                clampProgress(value);
    }

    private void forceImmediateReroll() {
        this.rerollCounter = 0;
        this.rerollTargetTicks =
                rollNextRerollTicks(this.level);

        rerollButtons();
        bumpVisualPulse();
    }

    private void rerollButtons() {
        if (this.level == null) {
            return;
        }

        Player player =
                this.currentInteractingPlayer;

        double lockpicking = player == null
                ? 0.0D
                : player.getAttributeValue(
                JolCraftAttributes.LOCKPICKING
        );

        if (this.level.random.nextDouble()
                < getUnlockChance(lockpicking)) {
            this.correctButtonId = 3;
            this.unlockSlotId =
                    this.level.random.nextInt(3);
        } else {
            this.correctButtonId =
                    this.level.random.nextInt(3);
            this.unlockSlotId = -1;
        }
    }

    public boolean handleLockButtonPress(
            ServerPlayer player,
            int buttonId,
            ItemStack lockpickSlot
    ) {
        if (this.level == null
                || this.level.isClientSide) {
            return false;
        }

        if (!this.isLocked()) {
            debugReject(
                    player,
                    "not_locked",
                    buttonId
            );
            return false;
        }

        if (this.currentInteractingPlayer == null
                || !this.currentInteractingPlayer.getUUID().equals(
                player.getUUID()
        )) {
            debugReject(
                    player,
                    "wrong_player",
                    buttonId
            );
            return false;
        }

        if (!this.hasLockpickInserted
                || lockpickSlot.isEmpty()) {
            debugReject(
                    player,
                    "no_lockpick",
                    buttonId
            );
            return false;
        }

        boolean unlockMode =
                this.correctButtonId == 3;

        if (unlockMode) {
            if (buttonId == this.unlockSlotId) {
                player.closeContainer();

                PlaySound.strongboxUnlock(
                        this.level,
                        this.getBlockPos()
                );

                this.hasLockpickInserted = false;
                resetLockSession();

                this.level.setBlock(
                        this.getBlockPos(),
                        this.getBlockState().setValue(
                                StrongboxBlock.LOCKED,
                                false
                        ),
                        Block.UPDATE_ALL
                );

                return true;
            }

            if (!player.isCreative()) {
                lockpickSlot.shrink(1);
            }

            setLockpickProgress(0);

            PlaySound.strongboxLockpickBreak(
                    this.level,
                    this.getBlockPos()
            );

            forceImmediateReroll();
            return true;
        }

        if (buttonId == this.correctButtonId) {
            double lockpicking =
                    player.getAttributeValue(
                            JolCraftAttributes.LOCKPICKING
                    );

            int baseGain =
                    10 + this.level.random.nextInt(11);

            int bonusGain = (int) Math.round(
                    this.level.random.nextDouble()
                            * getProgressBonus(lockpicking)
            );

            setLockpickProgress(
                    this.lockProgress
                            + baseGain
                            + bonusGain
            );

            PlaySound.strongboxLockpick(
                    this.level,
                    this.getBlockPos()
            );

            if (this.lockProgress >= LOCK_MAX_PROGRESS) {
                player.closeContainer();

                PlaySound.strongboxUnlock(
                        this.level,
                        this.getBlockPos()
                );

                this.hasLockpickInserted = false;
                resetLockSession();

                this.level.setBlock(
                        this.getBlockPos(),
                        this.getBlockState().setValue(
                                StrongboxBlock.LOCKED,
                                false
                        ),
                        Block.UPDATE_ALL
                );

                return true;
            }
        } else {
            if (!player.isCreative()) {
                lockpickSlot.shrink(1);
            }

            setLockpickProgress(0);

            PlaySound.strongboxLockpickBreak(
                    this.level,
                    this.getBlockPos()
            );
        }

        forceImmediateReroll();
        return true;
    }

    private void debugReject(
            ServerPlayer player,
            String reason,
            int buttonId
    ) {
        String currentName =
                currentInteractingPlayer != null
                        ? currentInteractingPlayer
                        .getName()
                        .getString()
                        : "null";

        String currentUuid =
                currentInteractingPlayer != null
                        ? currentInteractingPlayer
                        .getUUID()
                        .toString()
                        : "null";

        JolCraftLogs.debug(
                JolCraftLogTags.BLOCK_ENTITY,
                "Strongbox lock press rejected ({}) pos={} dim={} player={}({}) buttonId={} locked={} hasLockpickInserted={} currentInteracting={}({})",
                reason,
                JolCraftLogs.roundedPos(this),
                level == null
                        ? "null"
                        : level.dimension().location(),
                player.getName().getString(),
                player.getUUID(),
                buttonId,
                this.isLocked(),
                this.hasLockpickInserted,
                currentName,
                currentUuid
        );
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return lidController.getOpenness(
                partialTicks
        );
    }

    public static void lidAnimateTick(
            StrongboxBlockEntity be
    ) {
        be.lidController.tickLid();
    }

    @Override
    public boolean triggerEvent(
            int id,
            int param
    ) {
        if (id == 1) {
            this.lidController.shouldBeOpen(
                    param > 0
            );
            return true;
        }

        return super.triggerEvent(
                id,
                param
        );
    }

    @Override
    public void startOpen(Player player) {
        if (this.remove
                || player.isSpectator()) {
            return;
        }

        Level level =
                this.getLevel();

        if (level == null) {
            return;
        }

        this.openersCounter.incrementOpeners(
                player,
                level,
                this.getBlockPos(),
                this.getBlockState()
        );
    }

    @Override
    public void stopOpen(Player player) {
        if (this.remove
                || player.isSpectator()) {
            return;
        }

        Level level =
                this.getLevel();

        if (level == null) {
            return;
        }

        this.openersCounter.decrementOpeners(
                player,
                level,
                this.getBlockPos(),
                this.getBlockState()
        );
    }

    protected void signalOpenCount(
            Level level,
            BlockPos pos,
            BlockState state,
            int eventParam
    ) {
        level.blockEvent(
                pos,
                state.getBlock(),
                1,
                eventParam
        );
    }

    public void recheckOpen() {
        if (this.remove
                || this.level == null
                || this.level.isClientSide) {
            return;
        }

        this.openersCounter.recheckOpeners(
                this.level,
                this.getBlockPos(),
                this.getBlockState()
        );
    }

    @Override
    public void unpackLootTable(
            @Nullable Player player
    ) {
        if (this.isLocked()) {
            return;
        }

        super.unpackLootTable(player);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setItems(
            NonNullList<ItemStack> items
    ) {
        if (this.isLocked()) {
            this.items = NonNullList.withSize(
                    this.getContainerSize(),
                    ItemStack.EMPTY
            );
        } else {
            this.items = items;
        }
    }

    @Override
    public void clearContent() {
        if (this.isLocked()) {
            for (int i = 0;
                 i < this.getContainerSize();
                 i++) {
                this.setItem(
                        i,
                        ItemStack.EMPTY
                );
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
        return Component.translatable(
                JolCraftLanguageKeys.CONTAINER_STRONGBOX
        );
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider provider
    ) {
        super.saveAdditional(
                tag,
                provider
        );

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(
                    tag,
                    this.items,
                    provider
            );
        }
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider provider
    ) {
        super.loadAdditional(
                tag,
                provider
        );

        this.items = NonNullList.withSize(
                getContainerSize(),
                ItemStack.EMPTY
        );

        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(
                    tag,
                    this.items,
                    provider
            );
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(
            int id,
            Inventory inv
    ) {
        this.currentInteractingPlayer =
                inv.player;

        if (this.isLocked()) {
            return new LockMenu(
                    id,
                    inv,
                    this
            );
        }

        return new StrongboxMenu(
                id,
                inv,
                this
        );
    }

    @Override
    public Component getDisplayName() {
        return this.isLocked()
                ? Component.translatable(
                JolCraftLanguageKeys.CONTAINER_STRONGBOX_LOCKED
        )
                : Component.translatable(
                JolCraftLanguageKeys.CONTAINER_STRONGBOX
        );
    }

    public boolean isLocked() {
        return this.getBlockState()
                .getValue(
                        StrongboxBlock.LOCKED
                );
    }

    private void serverTickLockSession() {
        if (!isLockSessionActive()) {
            if (this.lockProgress != 0
                    || this.rerollCounter != 0
                    || this.decayProgress != 0.0D) {
                resetLockSession();
            }

            return;
        }

        Player player =
                this.currentInteractingPlayer;

        if (player == null) {
            return;
        }

        double lockpicking =
                player.getAttributeValue(
                        JolCraftAttributes.LOCKPICKING
                );

        double decayRate =
                getDecayRate(lockpicking);

        this.rerollCounter++;

        if (this.rerollCounter
                >= this.rerollTargetTicks) {
            this.rerollCounter = 0;
            this.rerollTargetTicks =
                    rollNextRerollTicks(
                            this.level
                    );

            rerollButtons();
            bumpVisualPulse();
        }

        if (this.lockProgress > 0) {
            this.decayProgress += decayRate;

            while (this.decayProgress >= 1.0D
                    && this.lockProgress > 0) {
                this.decayProgress -= 1.0D;

                setLockpickProgress(
                        this.lockProgress - 1
                );
            }
        } else {
            this.decayProgress = 0.0D;
        }
    }

    @Override
    public void tickServer() {
        if (this.level == null) {
            return;
        }

        this.recheckOpen();

        BlockState state =
                this.level.getBlockState(
                        this.getBlockPos()
                );

        if (!state.getValue(
                StrongboxBlock.LOCKED
        )) {
            if (!this.lockSessionClearedWhileUnlocked) {
                this.resetLockSession();
                this.lockSessionClearedWhileUnlocked = true;
            }

            return;
        }

        this.lockSessionClearedWhileUnlocked = false;
        this.serverTickLockSession();
    }

    @Override
    public void tickClient() {
        if (this.level == null) {
            return;
        }

        lidAnimateTick(this);
    }
}