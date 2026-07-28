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
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.SyncingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.DwarvenBrewInteractionHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.FermentingBarrelAging;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingBarrelBlockEntity extends BlockEntity
        implements TickingBlockEntity, SyncingBlockEntity {

    private static final String NBT_BREW_TANK = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.TANK
    );

    private static final int AMBIENT_SOUND_INTERVAL = 200;

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
        public FluidStack getFluidInTank(
                int tank
        ) {
            return getCurrentBrew();
        }

        @Override
        public int getTankCapacity(
                int tank
        ) {
            return brewTank.getCapacity();
        }

        @Override
        public boolean isFluidValid(
                int tank,
                FluidStack stack
        ) {
            return DwarvenBrewFluidHelper.isFinishedBrew(
                    stack
            );
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
            if (!matchesStoredBrew(
                    resource
            )) {
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

    public ItemInteractionResult handleInteraction(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (level == null
                || level.isClientSide
                || hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.FAIL;
        }

        if (usedItem.is(
                JolCraftItems.DEV_KEY.get()
        )) {
            return advanceToNextBrewAge();
        }

        if (player.isCreative()
                && hasBrew()) {
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

    public boolean inspectBrewAge(
            Player player
    ) {
        if (level == null
                || level.isClientSide
                || !hasBrew()) {
            return false;
        }

        applyElapsedAge();

        DwarvenBrewAge brewAge =
                DwarvenBrewAge.fromTicks(
                        DwarvenBrewFluidHelper.getAge(
                                getCurrentBrew()
                        )
                );

        player.displayClientMessage(
                Component.translatable(
                        JolCraftLanguageKeys.BARREL_BREW_AGE,
                        Component.translatable(
                                brewAge.translationKey()
                        )
                ),
                true
        );

        return true;
    }

    // =====================================================================
    // Aging
    // =====================================================================

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

        if (level.getGameTime()
                % AMBIENT_SOUND_INTERVAL != 0L) {
            return;
        }

        DwarvenBrewAge brewAge =
                DwarvenBrewAge.fromTicks(
                        DwarvenBrewFluidHelper.getAge(
                                getCurrentBrew()
                        )
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
                0.5F
                        + level.random.nextFloat()
                        * 0.3F
        );
    }

    private void applyElapsedAge() {
        if (level == null
                || brewTank.isEmpty()) {
            return;
        }

        if (aging.applyElapsedAge(
                brewTank.getFluid(),
                level.getGameTime()
        )) {
            setChanged();
        }
    }

    public void fastForwardAge(
            long skippedTicks
    ) {
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

    private ItemInteractionResult advanceToNextBrewAge() {
        if (level == null
                || brewTank.isEmpty()) {
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

    private int fillBrew(
            FluidStack incoming,
            IFluidHandler.FluidAction action
    ) {
        if (!canInsertBrew(
                incoming
        )) {
            return 0;
        }

        int available =
                brewTank.getCapacity()
                        - brewTank.getFluidAmount();

        int accepted = Math.min(
                available,
                incoming.getAmount()
        );

        if (accepted <= 0) {
            return 0;
        }

        if (action.simulate()) {
            return accepted;
        }

        applyElapsedAge();

        if (brewTank.isEmpty()) {
            FluidStack inserted =
                    incoming.copy();

            inserted.setAmount(
                    accepted
            );

            brewTank.setFluid(
                    inserted
            );

            onBrewTankChanged();

            return accepted;
        }

        FluidStack merged =
                DwarvenBrewFluidHelper.mergeAgedBrew(
                        brewTank.getFluid(),
                        incoming,
                        accepted
                );

        if (merged.isEmpty()) {
            return 0;
        }

        brewTank.setFluid(
                merged
        );

        onBrewTankChanged();

        return accepted;
    }

    private FluidStack drainBrew(
            int maxDrain,
            IFluidHandler.FluidAction action
    ) {
        if (maxDrain <= 0
                || brewTank.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack current =
                getCurrentBrew();

        int drainedAmount = Math.min(
                maxDrain,
                current.getAmount()
        );

        if (drainedAmount <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack result =
                current.copy();

        result.setAmount(
                drainedAmount
        );

        if (action.simulate()) {
            return result;
        }

        applyElapsedAge();

        FluidStack drained =
                brewTank.drain(
                        drainedAmount,
                        IFluidHandler.FluidAction.EXECUTE
                );

        if (drained.isEmpty()) {
            return FluidStack.EMPTY;
        }

        if (brewTank.isEmpty()) {
            restoreVanillaBarrelIfEmpty();
        } else if (level != null) {
            aging.reset(
                    level.getGameTime()
            );
        }

        return drained;
    }

    private boolean canInsertBrew(
            FluidStack incoming
    ) {
        if (!DwarvenBrewFluidHelper.isFinishedBrew(
                incoming
        )) {
            return false;
        }

        return brewTank.isEmpty()
                || DwarvenBrewFluidHelper.matchesUnderlyingBrew(
                incoming,
                brewTank.getFluid()
        );
    }

    private boolean matchesStoredBrew(
            FluidStack requested
    ) {
        return !brewTank.isEmpty()
                && !requested.isEmpty()
                && DwarvenBrewFluidHelper.matchesUnderlyingBrew(
                requested,
                brewTank.getFluid()
        );
    }

    private void onBrewTankChanged() {
        setChanged();

        if (level == null
                || level.isClientSide) {
            return;
        }

        aging.ensureTimerStarted(
                !brewTank.isEmpty(),
                level.getGameTime()
        );

        syncClient();
    }

    private void sanitizeLoadedTank() {
        FluidStack brew =
                brewTank.getFluid();

        if (brew.isEmpty()) {
            aging.clear();
            return;
        }

        if (!DwarvenBrewFluidHelper.isFinishedBrew(
                brew
        )) {
            brewTank.setFluid(
                    FluidStack.EMPTY
            );

            aging.clear();
            return;
        }

        brew.setAmount(
                Math.min(
                        FluidType.BUCKET_VOLUME,
                        brew.getAmount()
                )
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                DwarvenBrewFluidHelper.getAge(
                        brew
                )
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                brew.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                )
        );
    }

    private void restoreVanillaBarrelIfEmpty() {
        if (level == null
                || level.isClientSide()
                || !brewTank.isEmpty()) {
            return;
        }

        BlockState currentState =
                getBlockState();

        Direction facing = currentState.getValue(
                FermentingBarrelBlock.FACING
        );

        BlockState barrelState =
                Blocks.BARREL
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

    @Override
    public void setLevel(
            Level level
    ) {
        super.setLevel(
                level
        );

        if (!level.isClientSide) {
            aging.ensureTimerStarted(
                    !brewTank.isEmpty(),
                    level.getGameTime()
            );
        }
    }

    @Override
    public @NotNull ClientboundBlockEntityDataPacket getUpdatePacket() {
        return defaultUpdatePacket();
    }

    @Override
    public CompoundTag getUpdateTag(
            HolderLookup.Provider registries
    ) {
        CompoundTag tag =
                new CompoundTag();

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

        aging.save(
                tag
        );
    }

    private void readData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        brewTank.setFluid(
                FluidStack.EMPTY
        );

        aging.clear();

        if (tag.contains(
                NBT_BREW_TANK,
                Tag.TAG_COMPOUND
        )) {
            brewTank.readFromNBT(
                    registries,
                    tag.getCompound(
                            NBT_BREW_TANK
                    )
            );
        }

        sanitizeLoadedTank();

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

    public int getBrewAmount() {
        return brewTank.getFluidAmount();
    }

    public FluidStack getBrewFluid() {
        return getCurrentBrew();
    }

    public IFluidHandler getBrewFluidHandler() {
        return brewFluidHandler;
    }
}