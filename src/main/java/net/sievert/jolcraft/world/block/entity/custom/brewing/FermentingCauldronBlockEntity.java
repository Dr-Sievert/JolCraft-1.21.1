package net.sievert.jolcraft.world.block.entity.custom.brewing;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.SyncingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewInteractionHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.FermentingCauldronProcess;
import net.sievert.jolcraft.world.data.custom.PendingStatData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.entity.player.JolCraftStats;
import net.sievert.jolcraft.world.entity.attachment.player.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipeInput;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * Handles ingredient insertion, brewing progression, fluid storage and
 * synchronization for a fermenting cauldron.
 */
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingCauldronBlockEntity extends BlockEntity
        implements TickingBlockEntity, SyncingBlockEntity {

    private static final String NBT_BREW_TANK = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.TANK
    );

    private static final String NBT_BREW_PLAYER = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.PLAYER
    );

    private static final LootContextParamSet EXECUTION_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(JolCraftRecipeContextParams.INPUT_ITEM)
                    .build();

    @Nullable
    private UUID brewPlayer;

    private boolean restoreVanillaCauldronOnLoad;
    private boolean loadedStateNeedsSave;

    private final FermentingCauldronProcess process =
            new FermentingCauldronProcess();

    private final FluidTank brewTank = new FluidTank(
            FluidType.BUCKET_VOLUME,
            stack -> DwarvenBrewFluidHelper.isFinishedBrewingFluid(stack)
                    || DwarvenBrewFluidHelper.isUnfinishedBrewingFluid(stack)
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
            if (tank != 0) {
                return FluidStack.EMPTY;
            }

            return DwarvenBrewFluidHelper.withFreshAge(
                    brewTank.getFluid()
            );
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
            if (!hasFinishedFluid()
                    || !matchesStoredBrew(resource)) {
                return FluidStack.EMPTY;
            }

            return DwarvenBrewFluidHelper.withFreshAge(
                    brewTank.drain(
                            resource.getAmount(),
                            action
                    )
            );
        }

        @Override
        public FluidStack drain(
                int maxDrain,
                FluidAction action
        ) {
            if (!hasFinishedFluid() || maxDrain <= 0) {
                return FluidStack.EMPTY;
            }

            return DwarvenBrewFluidHelper.withFreshAge(
                    brewTank.drain(
                            maxDrain,
                            action
                    )
            );
        }
    };

    public FermentingCauldronBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                JolCraftBlockEntities.FERMENTING_CAULDRON.get(),
                pos,
                state
        );
    }

    // =====================================================================
    // Interaction
    // =====================================================================

    /**
     * Predicts whether the supplied item interaction is handled by the
     * fermenting cauldron without mutating its state.
     */
    public ItemInteractionResult getInteractionResult(
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (level == null
                || hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.FAIL;
        }

        if (process.isBrewing()) {
            return usedItem.is(JolCraftItems.DEV_KEY.get())
                    ? ItemInteractionResult.SUCCESS
                    : ItemInteractionResult.FAIL;
        }

        if (usedItem.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        if (hasFinishedFluid()
                || DwarvenBrewFluidHelper.containsDwarvenBrew(usedItem)
                || usedItem.is(JolCraftItems.GLASS_MUG.get())
                || usedItem.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
            return DwarvenBrewInteractionHelper.getInteractionResult(
                    usedItem,
                    brewFluidHandler,
                    hasFinishedFluid()
            );
        }

        return findRecipe(usedItem) == null
                ? ItemInteractionResult.FAIL
                : ItemInteractionResult.SUCCESS;
    }

    /**
     * Handles fluid-container interaction or inserts a matching recipe ingredient.
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

        if (process.isBrewing()) {
            return handleBrewingInteraction(usedItem);
        }

        if (hasFinishedFluid()
                || DwarvenBrewFluidHelper.containsDwarvenBrew(usedItem)
                || usedItem.is(JolCraftItems.GLASS_MUG.get())
                || usedItem.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
            return DwarvenBrewInteractionHelper.tryInteractFluidContainer(
                    level,
                    worldPosition,
                    player,
                    hand,
                    usedItem,
                    brewFluidHandler,
                    hasFinishedFluid()
            );
        }

        FermentingCauldronRecipe recipe = findRecipe(usedItem);

        if (recipe == null) {
            return ItemInteractionResult.FAIL;
        }

        return tryInsert(
                player,
                hand,
                usedItem,
                recipe
        );
    }

    /**
     * Allows the developer key to immediately complete the active blend.
     */
    private ItemInteractionResult handleBrewingInteraction(ItemStack usedItem) {
        if (level == null || !usedItem.is(JolCraftItems.DEV_KEY.get())) {
            return ItemInteractionResult.FAIL;
        }

        process.restartImmediately(level.getGameTime());
        syncClient();

        return ItemInteractionResult.SUCCESS;
    }

    /**
     * Validates ingredient limits and progression requirements before insertion.
     */
    private ItemInteractionResult tryInsert(
            Player player,
            InteractionHand hand,
            ItemStack usedItem,
            FermentingCauldronRecipe recipe
    ) {
        if (!(level instanceof ServerLevel)) {
            return ItemInteractionResult.FAIL;
        }

        Item item = usedItem.getItem();
        int count = process.getIngredientCount(item);

        if (count >= FermentingCauldronProcess.MAX_INGREDIENT_STACK) {
            player.displayClientMessage(
                    Component.translatable(
                                    JolCraftLanguageKeys
                                            .TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX
                            )
                            .withStyle(ChatFormatting.GRAY),
                    true
            );

            return ItemInteractionResult.SUCCESS;
        }

        if (recipe.effect().isPresent()
                && process.hasEffects()
                && !process.containsIngredient(item)
                && !DwarfLoreAttachmentHelper.hasUnlock(
                player,
                DwarfLoreKey.FORGOTTEN_BREW_FORMULAS
        )) {
            player.displayClientMessage(
                    Component.translatable(
                                    JolCraftLanguageKeys
                                            .TOOLTIP_FERMENTING_CAULDRON_MULTI_LOCKED
                            )
                            .withStyle(ChatFormatting.RED),
                    true
            );

            return ItemInteractionResult.SUCCESS;
        }

        return applyInsert(
                player,
                hand,
                usedItem,
                recipe
        );
    }

    /**
     * Consumes one ingredient and starts its resulting brewing process.
     */
    private ItemInteractionResult applyInsert(
            Player player,
            InteractionHand hand,
            ItemStack usedItem,
            FermentingCauldronRecipe recipe
    ) {
        if (!(level instanceof ServerLevel serverLevel)
                || !(player instanceof ServerPlayer serverPlayer)) {
            return ItemInteractionResult.FAIL;
        }

        ItemStack ingredient = usedItem.copyWithCount(1);

        LootContext context = createExecutionContext(
                serverLevel,
                ingredient
        );

        player.awardStat(
                Stats.ITEM_USED.get(usedItem.getItem())
        );

        JolCraftItemHelper.consume(
                serverPlayer,
                hand
        );

        JolCraftSoundHelper.block(
                level,
                worldPosition,
                SoundEvents.PLAYER_SPLASH,
                0.4F,
                1.5F
        );

        int brewAmount = brewTank.isEmpty()
                ? FluidType.BUCKET_VOLUME
                : brewTank.getFluidAmount();

        process.applyIngredient(
                level,
                worldPosition,
                ingredient,
                recipe,
                context
        );

        if (recipe.finalizeBrew()) {
            brewPlayer = player.getUUID();
        }

        brewTank.setFluid(
                process.createUnfinishedBrewFluid(
                        brewAmount,
                        brewTank.getFluid(),
                        recipe
                )
        );

        onBrewTankChanged();

        return ItemInteractionResult.SUCCESS;
    }

    // =====================================================================
    // Brewing
    // =====================================================================

    /**
     * Advances the active brewing process and completes it when ready.
     */
    @Override
    public void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }

        commitBrewCreatedStat();

        if (!process.isBrewing()) {
            return;
        }

        doBubbleEffects();

        if (process.isComplete(level)) {
            completeBrew();
        }
    }

    /**
     * Commits the completed blend or replaces the unfinished fluid with its
     * finished output.
     */
    private void completeBrew() {
        boolean finished = process.completeBlend();

        if (!finished) {
            FluidStack updated = process.createUpdatedUnfinishedBrewFluid(
                    brewTank.getFluid()
            );

            if (!updated.isEmpty()) {
                brewTank.setFluid(updated);
                onBrewTankChanged();
            }

            return;
        }

        FluidStack finishedBrew = process.createFinishedBrewFluid(
                brewTank.getFluidAmount(),
                brewTank.getFluid()
        );

        process.clear();
        brewTank.setFluid(finishedBrew);

        if (DwarvenBrewFluidHelper.isFinishedBrew(finishedBrew)) {
            commitBrewCreatedStat();
        } else {
            brewPlayer = null;
        }

        onBrewTankChanged();
    }

    /**
     * Commits the attributed brew-created stat immediately or queues it for the player's next login when they are offline.
     */
    private void commitBrewCreatedStat() {
        if (!(level instanceof ServerLevel serverLevel)
                || brewPlayer == null
                || !hasFinishedBrew()) {
            return;
        }

        PendingStatData.awardOrQueue(
                serverLevel,
                brewPlayer,
                JolCraftStats.DWARVEN_BREWS_CREATED.get()
        );

        brewPlayer = null;
        setChanged();
    }

    /**
     * Advances the active brew by time skipped through sleeping.
     */
    public void fastForwardBrew(long skippedTicks) {
        if (level == null
                || level.isClientSide
                || skippedTicks <= 0L
                || !process.isBrewing()) {
            return;
        }

        if (process.fastForward(
                level,
                skippedTicks
        )) {
            completeBrew();

            return;
        }

        syncClient();
    }

    /**
     * Spawns the next scheduled brewing bubble and sound.
     */
    private void doBubbleEffects() {
        if (level == null
                || level.isClientSide
                || !process.shouldBubble()) {
            return;
        }

        double x = worldPosition.getX()
                + 0.5D
                + level.random.nextDouble()
                - 0.5D;

        double y = worldPosition.getY() + 1.01D;

        double z = worldPosition.getZ()
                + 0.5D
                + level.random.nextDouble()
                - 0.5D;

        JolCraftParticleHelper.spawn(
                level,
                ParticleTypes.BUBBLE_POP,
                x,
                y,
                z,
                1,
                0.0D,
                0.05D,
                0.0D,
                0.0D
        );

        JolCraftSoundHelper.block(
                level,
                BlockPos.containing(
                        x,
                        y,
                        z
                ),
                SoundEvents.BUBBLE_COLUMN_BUBBLE_POP,
                0.3F,
                1.4F
        );

        process.scheduleNextBubble(
                level.random.nextInt(process.getBubbleTicks())
        );
    }

    // =====================================================================
    // Fluid handling
    // =====================================================================

    /**
     * Inserts fresh finished brew into the cauldron.
     */
    private int fillBrew(
            FluidStack incoming,
            IFluidHandler.FluidAction action
    ) {
        if (!canInsertBrew(incoming)) {
            return 0;
        }

        int accepted = Math.min(
                brewTank.getSpace(),
                incoming.getAmount()
        );

        if (accepted <= 0) {
            return 0;
        }

        if (action.simulate()) {
            return accepted;
        }

        if (brewTank.isEmpty()) {
            FluidStack inserted =
                    DwarvenBrewFluidHelper.withFreshAge(incoming);

            inserted.setAmount(accepted);
            brewTank.setFluid(inserted);
        } else {
            brewTank.getFluid().grow(accepted);
        }

        onBrewTankChanged();

        return accepted;
    }

    private boolean canInsertBrew(FluidStack incoming) {
        if (!DwarvenBrewFluidHelper.isFinishedBrew(incoming)
                || DwarvenBrewFluidHelper.getAge(incoming) > 0L
                || process.hasUnfinishedState()) {
            return false;
        }

        return brewTank.isEmpty()
                || DwarvenBrewFluidHelper.matchesIgnoringAgeComponent(
                incoming,
                brewTank.getFluid()
        );
    }

    private boolean matchesStoredBrew(FluidStack requested) {
        return !brewTank.isEmpty()
                && DwarvenBrewFluidHelper.matchesIgnoringAgeComponent(
                requested,
                brewTank.getFluid()
        );
    }

    /**
     * Persists fluid changes, clears stale process state and synchronizes clients.
     */
    private void onBrewTankChanged() {
        setChanged();

        if (level == null || level.isClientSide) {
            return;
        }

        if (brewTank.isEmpty()) {
            process.clear();
            brewPlayer = null;
            restoreVanillaCauldron();

            return;
        }

        syncClient();
    }

    /**
     * Replaces the fermenting cauldron with an empty vanilla cauldron.
     */
    private void restoreVanillaCauldron() {
        if (level == null || level.isClientSide()) {
            return;
        }

        level.setBlockAndUpdate(
                worldPosition,
                Blocks.CAULDRON.defaultBlockState()
        );
    }

    // =====================================================================
    // Loaded-state validation
    // =====================================================================

    /**
     * Validates the stored fluid and normalizes its amount and components.
     *
     * @return whether the stored fluid was changed
     */
    private boolean sanitizeLoadedTank() {
        FluidStack brew = brewTank.getFluid();

        if (brew.isEmpty()) {
            return false;
        }

        FluidStack original = brew.copy();

        if (!DwarvenBrewFluidHelper.isFinishedBrewingFluid(brew)
                && !DwarvenBrewFluidHelper.isUnfinishedBrewingFluid(brew)) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} loaded invalid fluid '{}' (clearing)",
                    JolCraftLogs.roundedPos(this),
                    brew.getFluid()
                            .builtInRegistryHolder()
                            .key()
                            .location()
            );

            brewTank.setFluid(FluidStack.EMPTY);

            return true;
        }

        if (DwarvenBrewFluidHelper.getAge(brew) > 0L) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} loaded aged brew (clearing)",
                    JolCraftLogs.roundedPos(this)
            );

            brewTank.setFluid(FluidStack.EMPTY);

            return true;
        }

        brew.setAmount(
                Math.min(
                        FluidType.BUCKET_VOLUME,
                        brew.getAmount()
                )
        );

        DwarvenBrewFluidHelper.normalizeBrewingFluid(brew);

        return original.getAmount() != brew.getAmount()
                || !FluidStack.isSameFluidSameComponents(
                original,
                brew
        );
    }

    /**
     * Reconciles the loaded process state with the fluid stored in the tank.
     */
    private boolean sanitizeLoadedState() {
        boolean changed = sanitizeLoadedTank();

        restoreVanillaCauldronOnLoad = changed
                && brewTank.isEmpty();

        FluidStack fluid = brewTank.getFluid();

        if (process.hasUnfinishedState()) {
            if (process.matchesUnfinishedFluid(fluid)) {
                return changed;
            }

            if (DwarvenBrewFluidHelper.isFinishedBrewingFluid(fluid)) {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} loaded finished fluid with stale process state (clearing process)",
                        JolCraftLogs.roundedPos(this)
                );

                process.clear();
                brewPlayer = null;

                return true;
            }

            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} loaded unfinished process state without its expected fluid (clearing batch)",
                    JolCraftLogs.roundedPos(this)
            );

            process.clear();
            brewPlayer = null;

            if (DwarvenBrewFluidHelper.isUnfinishedBrewingFluid(fluid)) {
                brewTank.setFluid(FluidStack.EMPTY);
            }

            restoreVanillaCauldronOnLoad = brewTank.isEmpty();

            return true;
        }

        if (DwarvenBrewFluidHelper.isUnfinishedBrewingFluid(fluid)) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} loaded unfinished fluid without process state (clearing tank)",
                    JolCraftLogs.roundedPos(this)
            );

            brewTank.setFluid(FluidStack.EMPTY);
            restoreVanillaCauldronOnLoad = true;

            return true;
        }

        return changed;
    }

    // =====================================================================
    // Recipe handling
    // =====================================================================

    /**
     * Finds the recipe matching the held item and previous ingredient.
     */
    @Nullable
    private FermentingCauldronRecipe findRecipe(ItemStack usedItem) {
        if (level == null) {
            return null;
        }

        ItemStack ingredient = usedItem.copyWithCount(1);

        FermentingCauldronRecipeInput input =
                new FermentingCauldronRecipeInput(
                        ingredient,
                        process.getLastIngredient()
                );

        return level
                .getRecipeManager()
                .getRecipeFor(
                        JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get(),
                        input,
                        level
                )
                .map(RecipeHolder::value)
                .filter(recipe -> canApplyRecipe(
                        usedItem,
                        recipe
                ))
                .orElse(null);
    }

    private boolean canApplyRecipe(
            ItemStack usedItem,
            FermentingCauldronRecipe recipe
    ) {
        if (recipe.finalizeBrew()
                && recipe.outputFluid()
                == FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW
                && !process.hasEffects()) {
            return false;
        }

        if (!usedItem.is(JolCraftItems.TANNIN.get())
                || recipe.outputFluid()
                != FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW) {
            return true;
        }

        return DwarvenBrewFluidHelper.findContainedMaxAge(
                        usedItem
                )
                .map(maxAge -> maxAge.ordinal()
                        > DwarvenBrewFluidHelper.getMaxAge(
                        brewTank.getFluid()
                ).ordinal())
                .orElse(false);
    }

    /**
     * Creates the loot context used to execute an ingredient recipe.
     */
    private static LootContext createExecutionContext(
            ServerLevel level,
            ItemStack input
    ) {
        return JolCraftRecipeContexts.create(
                level,
                EXECUTION_CONTEXT_PARAMS,
                builder -> builder.withParameter(
                        JolCraftRecipeContextParams.INPUT_ITEM,
                        input
                )
        );
    }

    // =====================================================================
    // Level attachment / synchronization
    // =====================================================================

    /**
     * Restores invalid empty cauldrons and completes brews that finished while unloaded.
     */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (level.isClientSide) {
            return;
        }

        if (restoreVanillaCauldronOnLoad
                && brewTank.isEmpty()
                && !process.hasUnfinishedState()) {
            restoreVanillaCauldronOnLoad = false;
            restoreVanillaCauldron();

            return;
        }

        if (loadedStateNeedsSave) {
            loadedStateNeedsSave = false;
            setChanged();
        }

        commitBrewCreatedStat();

        if (process.isBrewing() && process.isComplete(level)) {
            completeBrew();
        }
    }

    @Override
    public @NotNull ClientboundBlockEntityDataPacket getUpdatePacket() {
        return defaultUpdatePacket();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        writeClientData(
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
        readClientData(
                tag,
                registries
        );
    }

    /**
     * Writes the fluid and process data required for client rendering and interaction prediction.
     */
    private void writeClientData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        FluidStack clientFluid = process.hasUnfinishedState()
                ? process.createUnfinishedBrewFluid(
                        getBrewAmount(),
                        brewTank.getFluid()
                )
                : brewTank.getFluid();

        if (!clientFluid.isEmpty()) {
            FluidTank clientTank = new FluidTank(
                    FluidType.BUCKET_VOLUME
            );

            clientTank.setFluid(clientFluid.copy());

            tag.put(
                    NBT_BREW_TANK,
                    clientTank.writeToNBT(
                            registries,
                            new CompoundTag()
                    )
            );
        }

        process.writeClientData(
                tag,
                registries
        );
    }

    /**
     * Replaces the local client state with data received from the server.
     */
    private void readClientData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        brewTank.setFluid(FluidStack.EMPTY);

        if (tag.contains(
                NBT_BREW_TANK,
                Tag.TAG_COMPOUND
        )) {
            brewTank.readFromNBT(
                    registries,
                    tag.getCompound(NBT_BREW_TANK)
            );
        }

        process.readClientData(
                tag,
                registries,
                worldPosition
        );
    }

    // =====================================================================
    // Persistence
    // =====================================================================

    /**
     * Saves the fluid tank, attributed player and brewing process.
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

        if (!brewTank.isEmpty()) {
            tag.put(
                    NBT_BREW_TANK,
                    brewTank.writeToNBT(
                            registries,
                            new CompoundTag()
                    )
            );
        }

        if (brewPlayer != null) {
            tag.putUUID(
                    NBT_BREW_PLAYER,
                    brewPlayer
            );
        }

        process.save(
                tag,
                registries
        );
    }

    /**
     * Loads the fluid tank, attributed player and brewing process.
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

        brewTank.setFluid(FluidStack.EMPTY);
        process.clear();

        brewPlayer = null;
        restoreVanillaCauldronOnLoad = false;
        loadedStateNeedsSave = false;

        if (tag.contains(
                NBT_BREW_TANK,
                Tag.TAG_COMPOUND
        )) {
            brewTank.readFromNBT(
                    registries,
                    tag.getCompound(NBT_BREW_TANK)
            );
        }

        if (tag.hasUUID(NBT_BREW_PLAYER)) {
            brewPlayer = tag.getUUID(NBT_BREW_PLAYER);
        }

        process.load(
                tag,
                registries,
                worldPosition
        );

        loadedStateNeedsSave = sanitizeLoadedState();
    }

    // =====================================================================
    // Integration access
    // =====================================================================

    public boolean isBrewing() {
        return process.isBrewing();
    }

    public boolean hasFinishedBrew() {
        return DwarvenBrewFluidHelper.isFinishedBrew(
                brewTank.getFluid()
        );
    }

    public boolean hasFinishedFluid() {
        return DwarvenBrewFluidHelper.isFinishedBrewingFluid(
                brewTank.getFluid()
        );
    }

    public int getCurrentColor() {
        return hasFinishedFluid()
                ? getFinishedFluidColor()
                : process.getCurrentColor();
    }

    public int getStartColor() {
        return hasFinishedFluid()
                ? getFinishedFluidColor()
                : process.getStartColor();
    }

    public int getTargetColor() {
        return hasFinishedFluid()
                ? getFinishedFluidColor()
                : process.getTargetColor();
    }

    private int getFinishedFluidColor() {
        return brewTank
                .getFluid()
                .getOrDefault(
                        JolCraftDataComponents.BREW_COLOR.get(),
                        JolCraftColors.argb("FFFFFF")
                );
    }

    public long getBrewStartTime() {
        return process.getBrewStartTime();
    }

    public int getBlendTotalTicks() {
        return process.getBlendTotalTicks();
    }

    public int getBrewAmount() {
        int storedAmount = brewTank.getFluidAmount();

        if (storedAmount > 0) {
            return storedAmount;
        }

        return process.hasUnfinishedState()
                ? FluidType.BUCKET_VOLUME
                : 0;
    }

    /**
     * Returns the brewing fluid representation exposed to Jade.
     */
    public FluidStack getJadeBrewFluid() {
        FluidStack stored = brewTank.getFluid();

        if (DwarvenBrewFluidHelper.isFinishedBrew(stored)) {
            return DwarvenBrewFluidHelper.withFreshAge(stored);
        }

        if (DwarvenBrewFluidHelper.isFinishedYeast(stored)
                || DwarvenBrewFluidHelper.isFinishedTannin(stored)) {
            return stored.copy();
        }

        if (!process.hasUnfinishedState()
                || (process.getOutputFluid()
                != FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW
                && process.getOutputFluid()
                != FermentingCauldronRecipe.OutputFluid.YEAST
                && process.getOutputFluid()
                != FermentingCauldronRecipe.OutputFluid.TANNIN)) {
            return FluidStack.EMPTY;
        }

        return process.createUnfinishedBrewFluid(
                getBrewAmount(),
                stored
        );
    }

    public IFluidHandler getBrewFluidHandler() {
        return brewFluidHandler;
    }
}
