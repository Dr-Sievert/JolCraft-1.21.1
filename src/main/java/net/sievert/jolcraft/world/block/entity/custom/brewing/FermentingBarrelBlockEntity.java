package net.sievert.jolcraft.world.block.entity.custom.brewing;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.event.game.world.JolCraftTimeHelper;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.SyncingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingBarrelBlockEntity extends BlockEntity
        implements TickingBlockEntity, SyncingBlockEntity {

    private static final String NBT_BREW_TANK = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.TANK
    );

    private static final String NBT_LAST_AGE_TIME = JolCraftStrings.underscored(
            JolCraftDictionary.LAST,
            JolCraftDictionary.AGE,
            JolCraftDictionary.TIME
    );

    private static final int AMBIENT_SOUND_INTERVAL = 200;

    private static final int MAX_MUGS = 3;

    private static final int MUG_VOLUME =
            FluidType.BUCKET_VOLUME / MAX_MUGS;

    private static final int FIRST_MUG_VOLUME =
            FluidType.BUCKET_VOLUME - MUG_VOLUME * 2;

    private long lastAgeTime;

    private final FluidTank brewTank = new FluidTank(
            FluidType.BUCKET_VOLUME,
            FermentingBarrelBlockEntity::isValidBrew
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
            return getCurrentBrew();
        }

        @Override
        public int getTankCapacity(int tank) {
            return brewTank.getCapacity();
        }

        @Override
        public boolean isFluidValid(
                int tank,
                FluidStack stack
        ) {
            return canInsertBrew(stack);
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

    public ItemInteractionResult handleInteraction(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (level == null || level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        if (hand != InteractionHand.MAIN_HAND
                || (!usedItem.isEmpty()
                && !usedItem.is(JolCraftItems.DEV_KEY.get())
                && !usedItem.is(Items.BUCKET)
                && !usedItem.is(JolCraftItems.DWARVEN_BREW_BUCKET.get())
                && !usedItem.is(JolCraftItems.GLASS_MUG.get())
                && !usedItem.is(JolCraftItems.DWARVEN_BREW.get()))) {
            return ItemInteractionResult.FAIL;
        }

        DwarvenBrewAge brewAge = DwarvenBrewAge.fromTicks(
                getBrewAge(getCurrentBrew())
        );

        if (usedItem.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable(
                            JolCraftLanguageKeys.BARREL_BREW_AGE,
                            Component.translatable(brewAge.translationKey()).getString().toLowerCase()
                    ),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (usedItem.is(JolCraftItems.DEV_KEY.get())) {
            return advanceToNextBrewAge();
        }

        if (usedItem.is(JolCraftItems.GLASS_MUG.get())) {
            return hasBrew()
                    ? tryExtractMug(
                    player,
                    hand,
                    usedItem
            )
                    : ItemInteractionResult.FAIL;
        }

        if (usedItem.is(JolCraftItems.DWARVEN_BREW.get())) {
            return tryInsertMug(
                    player,
                    hand,
                    usedItem
            );
        }

        return tryInteractFluidContainer(
                player,
                hand,
                usedItem
        );
    }

    private ItemInteractionResult tryInteractFluidContainer(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (level == null) {
            return ItemInteractionResult.FAIL;
        }

        if (player.isCreative() && hasBrew()) {
            applyElapsedAge();

            FluidActionResult result = FluidUtil.tryFillContainer(
                    usedItem,
                    brewFluidHandler,
                    Integer.MAX_VALUE,
                    player,
                    false
            );

            if (result.isSuccess() && player instanceof ServerPlayer serverPlayer) {
                JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                        serverPlayer,
                        result.getResult()
                );

                player.awardStat(
                        Stats.ITEM_USED.get(
                                usedItem.getItem()
                        )
                );

                JolCraftSoundHelper.block(
                        level,
                        worldPosition,
                        SoundEvents.BUCKET_FILL,
                        1.0F,
                        1.0F
                );

                return ItemInteractionResult.SUCCESS;
            }
        }

        if (!FluidUtil.interactWithFluidHandler(
                player,
                hand,
                brewFluidHandler
        )) {
            return ItemInteractionResult.FAIL;
        }

        player.awardStat(
                Stats.ITEM_USED.get(
                        usedItem.getItem()
                )
        );

        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult tryExtractMug(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return ItemInteractionResult.FAIL;
        }

        int drainAmount = getMugDrainAmount();

        if (drainAmount <= 0) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack simulated = brewFluidHandler.drain(
                drainAmount,
                IFluidHandler.FluidAction.SIMULATE
        );

        if (simulated.isEmpty() || simulated.getAmount() != drainAmount) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack drained = brewFluidHandler.drain(
                drainAmount,
                player.isCreative()
                        ? IFluidHandler.FluidAction.SIMULATE
                        : IFluidHandler.FluidAction.EXECUTE
        );

        if (drained.isEmpty() || drained.getAmount() != drainAmount) {
            return ItemInteractionResult.FAIL;
        }

        ItemStack output = createBrewMug(
                drained
        );

        player.awardStat(
                Stats.ITEM_USED.get(
                        usedItem.getItem()
                )
        );

        JolCraftItemHelper.consume(
                serverPlayer,
                hand
        );

        JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                serverPlayer,
                output
        );

        PlaySound.bottleFill(
                player,
                0.8F,
                0.9F
        );

        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult tryInsertMug(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (!(player instanceof ServerPlayer serverPlayer) || level == null) {
            return ItemInteractionResult.FAIL;
        }

        int fillAmount = getMugFillAmount();

        if (fillAmount <= 0) {
            return ItemInteractionResult.FAIL;
        }

        FluidStack incoming = createBrewFluidFromMug(
                usedItem,
                fillAmount
        );

        int simulated = brewFluidHandler.fill(
                incoming,
                IFluidHandler.FluidAction.SIMULATE
        );

        if (simulated != fillAmount) {
            return ItemInteractionResult.FAIL;
        }

        int filled = brewFluidHandler.fill(
                incoming,
                IFluidHandler.FluidAction.EXECUTE
        );

        if (filled != fillAmount) {
            return ItemInteractionResult.FAIL;
        }

        player.awardStat(
                Stats.ITEM_USED.get(
                        usedItem.getItem()
                )
        );

        if (!player.isCreative()) {
            JolCraftItemHelper.consume(
                    serverPlayer,
                    hand
            );
        }

        JolCraftSoundHelper.block(
                level,
                worldPosition,
                SoundEvents.BOTTLE_EMPTY,
                0.8F,
                0.9F
        );

        return ItemInteractionResult.SUCCESS;
    }

    private int getMugDrainAmount() {
        int amount = brewTank.getFluidAmount();

        if (amount == FluidType.BUCKET_VOLUME) {
            return FIRST_MUG_VOLUME;
        }

        if (amount >= MUG_VOLUME && amount <= FIRST_MUG_VOLUME) {
            return amount;
        }

        return amount >= MUG_VOLUME
                ? MUG_VOLUME
                : 0;
    }

    private int getMugFillAmount() {
        int stored = brewTank.getFluidAmount();
        int remaining = brewTank.getCapacity() - stored;

        if (remaining < MUG_VOLUME) {
            return 0;
        }

        if (stored == 0 || remaining == FIRST_MUG_VOLUME) {
            return FIRST_MUG_VOLUME;
        }

        return MUG_VOLUME;
    }

    private static ItemStack createBrewMug(
            FluidStack brew
    ) {
        ItemStack mug = new ItemStack(
                JolCraftItems.DWARVEN_BREW.get()
        );

        mug.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                brew.getOrDefault(
                        JolCraftDataComponents.BREW_COLOR.get(),
                        0xFFFFFFFF
                )
        );

        mug.set(
                JolCraftDataComponents.BREW_AGE.get(),
                brew.getOrDefault(
                        JolCraftDataComponents.BREW_AGE.get(),
                        0L
                )
        );

        mug.set(
                DataComponents.POTION_CONTENTS,
                brew.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                )
        );

        return mug;
    }

    private static FluidStack createBrewFluidFromMug(
            ItemStack mug,
            int amount
    ) {
        FluidStack brew = new FluidStack(
                JolCraftFluids.DWARVEN_BREW.get(),
                amount
        );

        brew.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                mug.getOrDefault(
                        JolCraftDataComponents.BREW_COLOR.get(),
                        0xFFFFFFFF
                )
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                Math.max(
                        0L,
                        mug.getOrDefault(
                                JolCraftDataComponents.BREW_AGE.get(),
                                0L
                        )
                )
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                mug.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                )
        );

        return brew;
    }

    // =====================================================================
    // Aging
    // =====================================================================

    @Override
    public void tickServer() {
        if (level == null || level.isClientSide || brewTank.isEmpty()) {
            return;
        }

        if (lastAgeTime <= 0L) {
            resetAgeTimer();
        }

        DwarvenBrewAge brewAge = DwarvenBrewAge.fromTicks(
                getBrewAge(brewTank.getFluid())
        );

        if (brewAge != DwarvenBrewAge.VINTAGE && level.getGameTime() % AMBIENT_SOUND_INTERVAL == 0L && level.random.nextInt(3) == 0) {

            JolCraftSoundHelper.block(
                    level,
                    worldPosition,
                    SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    0.40F,
                    0.5F + level.random.nextFloat() * 0.3F
            );
        }
    }

    private static void applyAgeAmplifierIncrease(
            FluidStack brew,
            long previousAgeTicks,
            long currentAgeTicks
    ) {
        DwarvenBrewAge previousAge = DwarvenBrewAge.fromTicks(
                previousAgeTicks
        );

        DwarvenBrewAge currentAge = DwarvenBrewAge.fromTicks(
                currentAgeTicks
        );

        int amplifierIncrease =
                currentAge.amplifierBonus()
                        - previousAge.amplifierBonus();

        if (amplifierIncrease <= 0) {
            return;
        }

        amplifyBrewEffects(
                brew,
                amplifierIncrease
        );
    }

    private void applyElapsedAge() {
        if (level == null || brewTank.isEmpty()) {
            return;
        }

        long currentTime = level.getGameTime();

        if (lastAgeTime <= 0L) {
            lastAgeTime = currentTime;
            return;
        }

        long elapsed = currentTime - lastAgeTime;

        if (elapsed <= 0L) {
            return;
        }

        FluidStack brew = brewTank.getFluid();

        long previousAgeTicks = getBrewAge(
                brew
        );

        long currentAgeTicks = previousAgeTicks + elapsed;

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                currentAgeTicks
        );

        applyAgeAmplifierIncrease(
                brew,
                previousAgeTicks,
                currentAgeTicks
        );

        lastAgeTime = currentTime;

        setChanged();
    }

    public void fastForwardAge(
            long skippedTicks
    ) {
        if (level == null || level.isClientSide || brewTank.isEmpty()) {
            return;
        }

        if (skippedTicks <= 0L) {
            return;
        }

        applyElapsedAge();

        FluidStack brew = brewTank.getFluid();

        long previousAgeTicks = getBrewAge(
                brew
        );

        long currentAgeTicks = previousAgeTicks + skippedTicks;

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                currentAgeTicks
        );

        applyAgeAmplifierIncrease(
                brew,
                previousAgeTicks,
                currentAgeTicks
        );

        resetAgeTimer();
        onBrewTankChanged();
    }

    public static void handleSleepFinished(
            ServerLevel level,
            long newTime
    ) {
        long skipped = newTime - level.getDayTime();

        if (skipped <= 0L) {
            return;
        }

        Set<BlockPos> seen = new HashSet<>();

        for (ServerPlayer player : level.players()) {
            int centerChunkX = SectionPos.blockToSectionCoord(
                    player.blockPosition().getX()
            );

            int centerChunkZ = SectionPos.blockToSectionCoord(
                    player.blockPosition().getZ()
            );

            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    var chunk = level.getChunk(
                            centerChunkX + dx,
                            centerChunkZ + dz,
                            ChunkStatus.FULL,
                            false
                    );

                    if (!(chunk instanceof LevelChunk levelChunk)) {
                        continue;
                    }

                    for (BlockEntity blockEntity :
                            levelChunk.getBlockEntities().values()) {
                        if (!(blockEntity instanceof
                                FermentingBarrelBlockEntity barrel)) {
                            continue;
                        }

                        if (!barrel.hasBrew()) {
                            continue;
                        }

                        if (!seen.add(
                                blockEntity.getBlockPos()
                        )) {
                            continue;
                        }

                        barrel.fastForwardAge(
                                skipped
                        );
                    }
                }
            }
        }
    }

    private static void amplifyBrewEffects(
            FluidStack brew,
            int amplifierIncrease
    ) {
        if (amplifierIncrease <= 0) {
            return;
        }

        PotionContents contents = brew.getOrDefault(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY
        );

        List<MobEffectInstance> amplifiedEffects = new ArrayList<>(
                contents.customEffects().size()
        );

        for (MobEffectInstance effect : contents.customEffects()) {
            amplifiedEffects.add(
                    amplifyEffect(
                            effect,
                            amplifierIncrease
                    )
            );
        }

        brew.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        contents.potion(),
                        contents.customColor(),
                        amplifiedEffects
                )
        );
    }

    private static MobEffectInstance amplifyEffect(
            MobEffectInstance effect,
            int amplifierIncrease
    ) {
        return new MobEffectInstance(
                effect.getEffect(),
                effect.getDuration(),
                effect.getAmplifier() + amplifierIncrease,
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon()
        );
    }

    private ItemInteractionResult advanceToNextBrewAge() {
        if (level == null || brewTank.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        applyElapsedAge();

        FluidStack brew = brewTank.getFluid();

        long currentAge = getBrewAge(
                brew
        );

        long nextAge = getNextAgeThreshold(
                currentAge
        );

        if (nextAge <= currentAge) {
            return ItemInteractionResult.FAIL;
        }

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                nextAge
        );

        applyAgeAmplifierIncrease(
                brew,
                currentAge,
                nextAge
        );

        resetAgeTimer();
        onBrewTankChanged();

        return ItemInteractionResult.SUCCESS;
    }

    private static long getNextAgeThreshold(
            long currentAge
    ) {
        long day = JolCraftTimeHelper.TICKS_PER_DAY;

        if (currentAge <= day) {
            return day + 1L;
        }

        if (currentAge <= day * 3L) {
            return day * 3L + 1L;
        }

        if (currentAge <= day * 5L) {
            return day * 5L + 1L;
        }

        return currentAge;
    }

    private void resetAgeTimer() {
        lastAgeTime = level == null
                ? 0L
                : level.getGameTime();
    }

    public FluidStack getCurrentBrew() {
        if (brewTank.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack brew = brewTank.getFluid().copy();

        if (level == null || lastAgeTime <= 0L) {
            return brew;
        }

        long elapsed = Math.max(
                0L,
                level.getGameTime() - lastAgeTime
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                getBrewAge(brew) + elapsed
        );

        return brew;
    }

    private static long getBrewAge(
            FluidStack brew
    ) {
        return Math.max(
                0L,
                brew.getOrDefault(
                        JolCraftDataComponents.BREW_AGE.get(),
                        0L
                )
        );
    }

    // =====================================================================
    // Fluid handling
    // =====================================================================

    private int fillBrew(
            FluidStack incoming,
            IFluidHandler.FluidAction action
    ) {
        if (!canInsertBrew(incoming)) {
            return 0;
        }

        int available = brewTank.getCapacity()
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
            FluidStack inserted = incoming.copy();

            inserted.setAmount(
                    accepted
            );

            inserted.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    getBrewAge(inserted)
            );

            brewTank.setFluid(
                    inserted
            );

            onBrewTankChanged();
        } else {
            FluidStack stored = brewTank.getFluid();

            int oldAmount = stored.getAmount();

            long mergedAge = weightedAverageAge(
                    getBrewAge(stored),
                    oldAmount,
                    getBrewAge(incoming),
                    accepted
            );

            stored.setAmount(
                    oldAmount + accepted
            );

            stored.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    mergedAge
            );

            onBrewTankChanged();
        }

        resetAgeTimer();

        return accepted;
    }

    private FluidStack drainBrew(
            int maxDrain,
            IFluidHandler.FluidAction action
    ) {
        if (maxDrain <= 0 || brewTank.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack current = getCurrentBrew();

        int drainedAmount = Math.min(
                maxDrain,
                current.getAmount()
        );

        if (drainedAmount <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack result = current.copy();

        result.setAmount(
                drainedAmount
        );

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

        drained.set(
                JolCraftDataComponents.BREW_AGE.get(),
                getBrewAge(result)
        );

        if (brewTank.isEmpty()) {
            restoreVanillaBarrelIfEmpty();
        } else {
            resetAgeTimer();
        }

        return drained;
    }

    private static long weightedAverageAge(
            long firstAge,
            int firstAmount,
            long secondAge,
            int secondAmount
    ) {
        long totalAmount = (long) firstAmount + secondAmount;

        if (totalAmount <= 0L) {
            return 0L;
        }

        double weightedAge =
                (double) firstAge * firstAmount
                        + (double) secondAge * secondAmount;

        return Math.max(
                0L,
                Math.round(
                        weightedAge / totalAmount
                )
        );
    }

    private static boolean isValidBrew(
            FluidStack brew
    ) {
        return !brew.isEmpty()
                && brew.is(
                JolCraftFluids.DWARVEN_BREW.get()
        );
    }

    private boolean canInsertBrew(
            FluidStack incoming
    ) {
        if (!isValidBrew(incoming)) {
            return false;
        }

        return brewTank.isEmpty()
                || matchesIgnoringAge(
                incoming,
                brewTank.getFluid()
        );
    }

    private boolean matchesStoredBrew(
            FluidStack requested
    ) {
        return !brewTank.isEmpty()
                && !requested.isEmpty()
                && matchesIgnoringAge(
                requested,
                brewTank.getFluid()
        );
    }

    private static boolean matchesIgnoringAge(
            FluidStack first,
            FluidStack second
    ) {
        if (first.isEmpty() || second.isEmpty()) {
            return false;
        }

        return FluidStack.isSameFluidSameComponents(
                withoutBrewAge(first),
                withoutBrewAge(second)
        );
    }

    private static FluidStack withoutBrewAge(
            FluidStack brew
    ) {
        if (brew.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack normalized = brew.copy();

        normalized.remove(
                JolCraftDataComponents.BREW_AGE.get()
        );

        return normalized;
    }

    private void onBrewTankChanged() {
        setChanged();

        if (level == null || level.isClientSide) {
            return;
        }

        if (brewTank.isEmpty()) {
            lastAgeTime = 0L;
        } else if (lastAgeTime <= 0L) {
            resetAgeTimer();
        }

        syncClient();
    }

    private void sanitizeLoadedTank() {
        FluidStack brew = brewTank.getFluid();

        if (brew.isEmpty()) {
            lastAgeTime = 0L;
            return;
        }

        if (!isValidBrew(brew)) {
            brewTank.setFluid(
                    FluidStack.EMPTY
            );

            lastAgeTime = 0L;
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
                getBrewAge(brew)
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
        if (level == null || level.isClientSide() || !brewTank.isEmpty()) {
            return;
        }

        BlockState currentState = getBlockState();

        Direction facing = currentState.getValue(
                FermentingBarrelBlock.FACING
        );

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

    @Override
    public void setLevel(
            Level level
    ) {
        super.setLevel(
                level
        );

        if (!level.isClientSide
                && !brewTank.isEmpty()
                && lastAgeTime <= 0L) {
            resetAgeTimer();
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

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(
                tag,
                registries
        );

        applyElapsedAge();

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

        if (lastAgeTime > 0L) {
            tag.putLong(
                    NBT_LAST_AGE_TIME,
                    lastAgeTime
            );
        }
    }

    private void readData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        brewTank.setFluid(
                FluidStack.EMPTY
        );

        lastAgeTime = 0L;

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

        if (!brewTank.isEmpty()) {
            lastAgeTime = tag.getLong(
                    NBT_LAST_AGE_TIME
            );
        }
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