package net.sievert.jolcraft.world.block.entity.custom.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.SyncingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewInteractionHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.FermentingBarrelAging;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Stores finished brew, handles fluid transfer and advances the brew's age
 * while it remains inside the fermenting barrel.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingBarrelBlockEntity extends BlockEntity
        implements TickingBlockEntity, SyncingBlockEntity {

    private static final String NBT_BREW_TANK = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.TANK
    );

    private static final int AMBIENT_SOUND_INTERVAL = 200;

    private boolean restoreVanillaBarrelOnLoad;

    private final FermentingBarrelAging aging =
            new FermentingBarrelAging();

    private final FluidTank brewTank = new FluidTank(
            FluidType.BUCKET_VOLUME,
            DwarvenBrewFluidHelper::isFinishedBrew
    ) {
        @Override
        protected void onContentsChanged() {
            onBrewTankChanged();
        }
    };

    private final IFluidHandler brewFluidHandler = new IFluidHandler() {

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0
                    ? getCurrentBrew()
                    : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0
                    ? brewTank.getCapacity()
                    : 0;
        }

        @Override
        public boolean isFluidValid(
                int tank,
                FluidStack stack
        ) {
            return tank == 0
                    && DwarvenBrewFluidHelper.isFinishedBrew(stack);
        }

        @Override
        public int fill(
                FluidStack resource,
                FluidAction action
        ) {
            return fillBrew(
                    resource,
                    action
            );
        }

        @Override
        public FluidStack drain(
                FluidStack resource,
                FluidAction action
        ) {
            if (!matchesStoredBrew(resource)) {
                return FluidStack.EMPTY;
            }

            return drainBrew(
                    resource.getAmount(),
                    action
            );
        }

        @Override
        public FluidStack drain(
                int maxDrain,
                FluidAction action
        ) {
            return drainBrew(
                    maxDrain,
                    action
            );
        }
    };

    public FermentingBarrelBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                JolCraftBlockEntities.FERMENTING_BARREL.get(),
                pos,
                state
        );
    }

    // =====================================================================
    // Interaction
    // =====================================================================

    /**
     * Predicts whether the supplied item interaction is handled by the
     * fermenting barrel without mutating its state.
     */
    public ItemInteractionResult getInteractionResult(
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (level == null
                || hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.FAIL;
        }

        if (usedItem.is(JolCraftItems.DEV_KEY.get())) {
            if (!hasBrew()) {
                return ItemInteractionResult.FAIL;
            }

            DwarvenBrewAge brewAge = DwarvenBrewAge.fromTicks(
                    DwarvenBrewFluidHelper.getAge(
                            getCurrentBrew()
                    )
            );

            return brewAge == DwarvenBrewAge.VINTAGE
                    ? ItemInteractionResult.FAIL
                    : ItemInteractionResult.SUCCESS;
        }

        return DwarvenBrewInteractionHelper.getInteractionResult(
                usedItem,
                brewFluidHandler,
                hasBrew()
        );
    }

    /**
     * Handles developer aging and normal fluid-container interaction.
     */
    public ItemInteractionResult handleInteraction(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (level == null || level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        ItemInteractionResult interactionResult = getInteractionResult(
                hand,
                usedItem
        );

        if (interactionResult != ItemInteractionResult.SUCCESS) {
            return interactionResult;
        }

        if (usedItem.is(JolCraftItems.DEV_KEY.get())) {
            return advanceToNextBrewAge();
        }

        if (player.isCreative() && hasBrew()) {
            applyElapsedAge();
        }

        return DwarvenBrewInteractionHelper.tryInteractFluidContainer(
                level,
                worldPosition,
                player,
                hand,
                usedItem,
                brewFluidHandler,
                hasBrew()
        );
    }

    /**
     * Applies pending aging and displays the current brew age to the player.
     */
    public boolean inspectBrewAge(Player player) {
        if (level == null
                || level.isClientSide
                || !hasBrew()) {
            return false;
        }

        applyElapsedAge();

        DwarvenBrewAge brewAge = DwarvenBrewAge.fromTicks(
                DwarvenBrewFluidHelper.getAge(getCurrentBrew())
        );

        player.displayClientMessage(
                Component.translatable(
                        JolCraftLanguageKeys.BARREL_BREW_AGE,
                        Component.translatable(brewAge.translationKey())
                ),
                true
        );

        return true;
    }

    // =====================================================================
    // Aging
    // =====================================================================

    /**
     * Maintains the aging timer and occasionally plays an ambient brewing sound.
     */
    @Override
    public void tickServer() {
        if (level == null
                || level.isClientSide
                || brewTank.isEmpty()) {
            return;
        }

        aging.ensureTimerStarted(
                true,
                level.getGameTime()
        );

        if (level.getGameTime() % AMBIENT_SOUND_INTERVAL != 0L) {
            return;
        }

        DwarvenBrewAge brewAge = DwarvenBrewAge.fromTicks(
                DwarvenBrewFluidHelper.getAge(getCurrentBrew())
        );

        if (brewAge == DwarvenBrewAge.VINTAGE
                || level.random.nextInt(3) != 0) {
            return;
        }

        JolCraftSoundHelper.block(
                level,
                worldPosition,
                SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                0.40F,
                0.5F + level.random.nextFloat() * 0.3F
        );
    }

    /**
     * Commits all aging elapsed since the barrel timer was last reset.
     */
    private void applyElapsedAge() {
        if (level == null || brewTank.isEmpty()) {
            return;
        }

        if (aging.applyElapsedAge(
                brewTank.getFluid(),
                level.getGameTime()
        )) {
            setChanged();
        }
    }

    /**
     * Advances the stored brew by time skipped through sleeping.
     */
    public void fastForwardAge(long skippedTicks) {
        if (level == null
                || level.isClientSide
                || brewTank.isEmpty()
                || skippedTicks <= 0L) {
            return;
        }

        if (!aging.fastForward(
                brewTank.getFluid(),
                level.getGameTime(),
                skippedTicks
        )) {
            return;
        }

        onBrewTankChanged();
    }

    /**
     * Advances the stored brew to its next age for development testing.
     */
    private ItemInteractionResult advanceToNextBrewAge() {
        if (level == null || brewTank.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        if (!aging.advanceToNextAge(
                brewTank.getFluid(),
                level.getGameTime()
        )) {
            return ItemInteractionResult.FAIL;
        }

        onBrewTankChanged();

        return ItemInteractionResult.SUCCESS;
    }

    /**
     * Returns a copy of the stored brew with its current elapsed age applied.
     */
    public FluidStack getCurrentBrew() {
        if (brewTank.isEmpty()) {
            return FluidStack.EMPTY;
        }

        return aging.getCurrentBrew(
                brewTank.getFluid(),
                level == null
                        ? 0L
                        : level.getGameTime()
        );
    }

    // =====================================================================
    // Fluid handling
    // =====================================================================

    /**
     * Inserts compatible finished brew and merges its age with stored brew.
     */
    private int fillBrew(
            FluidStack incoming,
            IFluidHandler.FluidAction action
    ) {
        if (!canInsertBrew(incoming)) {
            return 0;
        }

        int available = brewTank.getCapacity() - brewTank.getFluidAmount();
        int accepted = Math.min(available, incoming.getAmount());

        if (accepted <= 0) {
            return 0;
        }

        if (action.simulate()) {
            return accepted;
        }

        applyElapsedAge();

        if (brewTank.isEmpty()) {
            FluidStack inserted = incoming.copy();
            inserted.setAmount(accepted);

            brewTank.setFluid(inserted);
            onBrewTankChanged();

            return accepted;
        }

        FluidStack merged = DwarvenBrewFluidHelper.mergeAgedBrew(
                brewTank.getFluid(),
                incoming,
                accepted
        );

        if (merged.isEmpty()) {
            return 0;
        }

        brewTank.setFluid(merged);
        onBrewTankChanged();

        return accepted;
    }

    /**
     * Extracts brew with its current age applied and maintains the aging timer.
     */
    private FluidStack drainBrew(
            int maxDrain,
            IFluidHandler.FluidAction action
    ) {
        if (maxDrain <= 0 || brewTank.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack current = getCurrentBrew();
        int drainedAmount = Math.min(maxDrain, current.getAmount());

        if (drainedAmount <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack result = current.copy();
        result.setAmount(drainedAmount);

        if (action.simulate()) {
            return result;
        }

        applyElapsedAge();

        FluidStack drained = brewTank.drain(
                drainedAmount,
                IFluidHandler.FluidAction.EXECUTE
        );

        if (drained.isEmpty()) {
            return FluidStack.EMPTY;
        }

        if (brewTank.isEmpty()) {
            restoreVanillaBarrelIfEmpty();
        } else if (level != null) {
            aging.reset(level.getGameTime());
        }

        return drained;
    }

    private boolean canInsertBrew(FluidStack incoming) {
        if (!DwarvenBrewFluidHelper.isFinishedBrew(incoming)) {
            return false;
        }

        return brewTank.isEmpty()
                || DwarvenBrewFluidHelper.matchesUnderlyingBrew(
                incoming,
                brewTank.getFluid()
        );
    }

    private boolean matchesStoredBrew(FluidStack requested) {
        return !brewTank.isEmpty()
                && !requested.isEmpty()
                && DwarvenBrewFluidHelper.matchesUnderlyingBrew(
                requested,
                brewTank.getFluid()
        );
    }

    /**
     * Persists fluid changes, maintains the aging timer and synchronizes clients.
     */
    private void onBrewTankChanged() {
        setChanged();

        if (level == null || level.isClientSide) {
            return;
        }

        aging.ensureTimerStarted(
                !brewTank.isEmpty(),
                level.getGameTime()
        );

        syncClient();
    }

    /**
     * Validates and normalizes brew loaded from persistent data.
     *
     * @return whether an empty invalid barrel should be restored to vanilla
     */
    private boolean sanitizeLoadedTank() {
        FluidStack brew = brewTank.getFluid();

        if (brew.isEmpty()) {
            aging.clear();

            return false;
        }

        if (!DwarvenBrewFluidHelper.isFinishedBrew(brew)) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingBarrel at {} loaded invalid fluid (clearing tank)",
                    JolCraftLogs.roundedPos(this)
            );

            brewTank.setFluid(FluidStack.EMPTY);
            aging.clear();

            return true;
        }

        brew.setAmount(
                Math.min(
                        FluidType.BUCKET_VOLUME,
                        brew.getAmount()
                )
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                DwarvenBrewFluidHelper.getAge(brew)
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                brew.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                )
        );

        return false;
    }

    /**
     * Replaces an emptied fermenting barrel with a correctly oriented vanilla barrel.
     */
    private void restoreVanillaBarrelIfEmpty() {
        if (level == null
                || level.isClientSide()
                || !brewTank.isEmpty()) {
            return;
        }

        BlockState currentState = getBlockState();
        Direction facing = currentState.getValue(FermentingBarrelBlock.FACING);

        BlockState barrelState = Blocks.BARREL
                .defaultBlockState()
                .setValue(
                        BarrelBlock.FACING,
                        facing
                );

        level.setBlock(
                worldPosition,
                barrelState,
                Block.UPDATE_ALL
        );
    }

    // =====================================================================
    // Level attachment / synchronization
    // =====================================================================

    /**
     * Starts aging after level attachment or restores an invalid empty barrel.
     */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (level.isClientSide) {
            return;
        }

        if (restoreVanillaBarrelOnLoad && brewTank.isEmpty()) {
            restoreVanillaBarrelOnLoad = false;
            restoreVanillaBarrelIfEmpty();

            return;
        }

        aging.ensureTimerStarted(
                !brewTank.isEmpty(),
                level.getGameTime()
        );
    }

    @Override
    public @NotNull ClientboundBlockEntityDataPacket getUpdatePacket() {
        return defaultUpdatePacket();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        writeData(
                tag,
                registries
        );

        return tag;
    }

    @Override
    public void handleUpdateTag(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        readData(
                tag,
                registries
        );
    }

    // =====================================================================
    // Persistence
    // =====================================================================

    /**
     * Saves the fluid tank and aging state.
     */
    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(
                tag,
                registries
        );

        writeData(
                tag,
                registries
        );
    }

    /**
     * Loads the fluid tank and aging state.
     */
    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(
                tag,
                registries
        );

        readData(
                tag,
                registries
        );
    }

    private void writeData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        if (!brewTank.isEmpty()) {
            tag.put(
                    NBT_BREW_TANK,
                    brewTank.writeToNBT(
                            registries,
                            new CompoundTag()
                    )
            );
        }

        aging.save(tag);
    }

    private void readData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        brewTank.setFluid(FluidStack.EMPTY);

        aging.clear();
        restoreVanillaBarrelOnLoad = false;

        if (tag.contains(
                NBT_BREW_TANK,
                Tag.TAG_COMPOUND
        )) {
            brewTank.readFromNBT(
                    registries,
                    tag.getCompound(NBT_BREW_TANK)
            );
        }

        restoreVanillaBarrelOnLoad = sanitizeLoadedTank();

        aging.load(
                tag,
                !brewTank.isEmpty()
        );
    }

    // =====================================================================
    // Integration access
    // =====================================================================

    public boolean hasBrew() {
        return !brewTank.isEmpty();
    }

    public IFluidHandler getBrewFluidHandler() {
        return brewFluidHandler;
    }
}