package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.gui.menu.MortarMenu;
import net.sievert.jolcraft.world.item.custom.tool.PestleItem;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.mortar.MortarRecipe;
import net.sievert.jolcraft.world.recipe.custom.mortar.MortarRecipeInput;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MortarBlockEntity extends BaseContainerBlockEntity {

    private static final int GRINDING_COOLDOWN_TICKS = 4;

    public static final int SLOT_INPUT_1 = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_INPUT_3 = 2;
    public static final int SLOT_OUTPUT = 3;
    public static final int SLOT_TOOL = 4;

    public static final int SLOT_COUNT = 5;

    private static final int DATA_RECIPE_VALID = 0;
    private static final int DATA_GRINDING_PROGRESS = 1;
    private static final int DATA_GRINDING_WORK = 2;
    private static final int DATA_COUNT = 3;

    private static final String GRINDING_PROGRESS_TAG =
            "GrindingProgress";

    private static final String GRINDING_WORK_TAG =
            "GrindingWork";

    private static final String ACTIVE_RECIPE_TAG =
            "ActiveRecipe";

    private static final LootContextParamSet OUTPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .optional(LootContextParams.THIS_ENTITY)
                    .optional(LootContextParams.ORIGIN)
                    .build();

    private NonNullList<ItemStack> items =
            NonNullList.withSize(
                    SLOT_COUNT,
                    ItemStack.EMPTY
            );

    private final NonNullList<ItemStack> grindingState =
            NonNullList.withSize(
                    4,
                    ItemStack.EMPTY
            );

    private @Nullable MortarRecipe activeRecipe;
    private int grindingProgress;
    private int grindingWork;
    private String activeRecipeId = "";
    private long nextGrindingGameTime;

    public MortarBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                JolCraftBlockEntities.MORTAR.get(),
                pos,
                state
        );
    }

    public void handleGrinding(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (refreshGrindingState()) {
            resetGrinding();
        }

        Optional<RecipeHolder<MortarRecipe>> recipeResult =
                findValidRecipe(player);

        if (recipeResult.isEmpty()) {
            refreshCachedState();
            return;
        }

        RecipeHolder<MortarRecipe> holder =
                recipeResult.orElseThrow();

        MortarRecipe recipe = holder.value();

        updateActiveRecipe(holder);

        ItemStack toolStack = items.get(SLOT_TOOL);

        long gameTime = level.getGameTime();

        if (gameTime < nextGrindingGameTime) {
            return;
        }

        nextGrindingGameTime =
                gameTime + GRINDING_COOLDOWN_TICKS;

        MortarRecipeInput recipeInput =
                createRecipeInput();

        LootContext context =
                createExecutionContext(
                        level,
                        player
                );

        int addedProgress =
                PestleItem.rollGrindingProgress(
                        toolStack,
                        level.random
                );

        int nextProgress = (int) Math.min(
                (long) grindingWork,
                (long) grindingProgress + addedProgress
        );

        boolean completed =
                nextProgress >= grindingWork;

        Optional<List<MortarRecipe.MatchedInput>> matchedInputs =
                completed
                        ? recipe.resolveInputs(
                                recipeInput,
                                level
                        )
                        : Optional.empty();

        if (completed && matchedInputs.isEmpty()) {
            refreshCachedState();
            return;
        }

        List<ItemStack> generatedResults =
                new ArrayList<>();

        if (completed) {
            recipe.generateResult(
                    context,
                    recipeInput,
                    generatedResults::add
            );

            generatedResults.removeIf(ItemStack::isEmpty);
        }

        recipe.generateSound(
                context,
                recipeInput,
                generatedSound -> level.playSound(
                        null,
                        worldPosition,
                        generatedSound.sound().value(),
                        generatedSound.source(),
                        generatedSound.volume(),
                        generatedSound.pitch()
                )
        );

        if (!player.isCreative()) {
            damageTool(
                    player,
                    recipe.toolDamage()
            );

            captureGrindingState();
        }

        if (!completed) {
            grindingProgress = nextProgress;

            setChanged();
            return;
        }

        if (!player.isCreative()) {
            consumeInputs(
                    matchedInputs.orElseThrow()
            );
        }

        for (ItemStack generatedResult : generatedResults) {
            JolCraftItemInsertionHelper.tryInsertIntoSlotInventoryOrDrop(
                    this,
                    SLOT_OUTPUT,
                    player.getInventory(),
                    player,
                    generatedResult
            );
        }

        resetGrinding();
        setChanged();
    }

    public ContainerData createContainerData(
            ServerPlayer player
    ) {
        return new ContainerData() {

            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_RECIPE_VALID ->
                            activeRecipe != null
                                    && activeRecipe.isUnlockedFor(player)
                                    ? 1
                                    : 0;

                    case DATA_GRINDING_PROGRESS ->
                            grindingProgress;

                    case DATA_GRINDING_WORK ->
                            grindingWork;

                    default -> 0;
                };
            }

            @Override
            public void set(
                    int index,
                    int value
            ) {
                switch (index) {
                    case DATA_GRINDING_PROGRESS ->
                            grindingProgress = value;

                    case DATA_GRINDING_WORK ->
                            grindingWork = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    public void refreshCachedState() {
        refreshCachedStateInternal();
        super.setChanged();
    }

    @Override
    public void setChanged() {
        refreshCachedStateInternal();
        super.setChanged();
    }

    private void refreshCachedStateInternal() {
        if (!(getLevel() instanceof ServerLevel)) {
            return;
        }

        if (refreshGrindingState()) {
            resetGrinding();
        }

        Optional<RecipeHolder<MortarRecipe>> recipe =
                findMatchingRecipe();

        if (recipe.isEmpty()) {
            resetGrinding();
            return;
        }

        updateActiveRecipe(recipe.orElseThrow());
    }

    private Optional<RecipeHolder<MortarRecipe>>
    findValidRecipe(ServerPlayer player) {
        return findMatchingRecipe()
                .filter(holder ->
                        holder.value().isUnlockedFor(player)
                );
    }

    private Optional<RecipeHolder<MortarRecipe>>
    findMatchingRecipe() {
        if (!(getLevel() instanceof ServerLevel level)) {
            return Optional.empty();
        }

        ItemStack toolStack = items.get(SLOT_TOOL);

        if (toolStack.isEmpty()
                || !toolStack.is(JolCraftTags.Items.PESTLES)) {
            return Optional.empty();
        }

        MortarRecipeInput input =
                createRecipeInput();

        return level.getRecipeManager()
                .getRecipeFor(
                        JolCraftRecipes.MORTAR_TYPE.get(),
                        input,
                        level
                );
    }

    private MortarRecipeInput createRecipeInput() {
        return new MortarRecipeInput(
                items.getFirst(),
                items.get(SLOT_INPUT_2),
                items.get(SLOT_INPUT_3),
                items.get(SLOT_TOOL)
        );
    }

    private void updateActiveRecipe(
            RecipeHolder<MortarRecipe> holder
    ) {
        String nextRecipeId =
                holder.id().toString();

        int nextGrindingWork =
                holder.value().grindingWork();

        if (!activeRecipeId.equals(nextRecipeId)
                || grindingWork != nextGrindingWork) {
            grindingProgress = 0;
        }

        activeRecipe = holder.value();
        activeRecipeId = nextRecipeId;
        grindingWork = nextGrindingWork;
    }

    private LootContext createExecutionContext(
            ServerLevel level,
            ServerPlayer player
    ) {
        return JolCraftRecipeContexts.create(
                level,
                level.random,
                OUTPUT_CONTEXT_PARAMS,
                builder -> builder
                        .withParameter(
                                LootContextParams.THIS_ENTITY,
                                player
                        )
                        .withParameter(
                                LootContextParams.ORIGIN,
                                Vec3.atCenterOf(worldPosition)
                        )
        );
    }

    private void consumeInputs(
            List<MortarRecipe.MatchedInput> matchedInputs
    ) {
        for (MortarRecipe.MatchedInput matchedInput :
                matchedInputs) {
            consumeInput(
                    matchedInput.slot(),
                    matchedInput.count()
            );
        }
    }

    private void consumeInput(
            int slot,
            int count
    ) {
        ItemStack stack = items.get(slot);

        if (stack.isEmpty()) {
            return;
        }

        stack.shrink(count);

        if (stack.isEmpty()) {
            items.set(
                    slot,
                    ItemStack.EMPTY
            );
        }
    }

    private void damageTool(
            ServerPlayer player,
            int damage
    ) {
        if (damage <= 0) {
            return;
        }

        ItemStack toolStack =
                items.get(SLOT_TOOL);

        if (toolStack.isEmpty()
                || !toolStack.isDamageableItem()) {
            return;
        }

        toolStack.hurtAndBreak(
                damage,
                player.serverLevel(),
                player,
                brokenItem -> player.serverLevel().playSound(
                        null,
                        worldPosition,
                        brokenItem.getBreakingSound(),
                        player.getSoundSource(),
                        1.0F,
                        1.0F
                )
        );

        if (toolStack.isEmpty()) {
            items.set(
                    SLOT_TOOL,
                    ItemStack.EMPTY
            );
        }
    }

    private void resetGrinding() {
        activeRecipe = null;
        grindingProgress = 0;
        grindingWork = 0;
        activeRecipeId = "";
    }

    private boolean refreshGrindingState() {
        boolean changed = false;

        for (int index = 0;
             index < grindingState.size();
             index++) {
            ItemStack current =
                    getGrindingStateStack(index);

            ItemStack previous =
                    grindingState.get(index);

            if (!sameStackIgnoringCount(
                    current,
                    previous
            )) {
                changed = true;
            }
        }

        if (changed) {
            captureGrindingState();
        }

        return changed;
    }

    private void captureGrindingState() {
        for (int index = 0;
             index < grindingState.size();
             index++) {
            ItemStack stack =
                    getGrindingStateStack(index);

            grindingState.set(
                    index,
                    stack.isEmpty()
                            ? ItemStack.EMPTY
                            : stack.copyWithCount(1)
            );
        }
    }

    private ItemStack getGrindingStateStack(int index) {
        return switch (index) {
            case 0 -> items.get(SLOT_INPUT_1);
            case 1 -> items.get(SLOT_INPUT_2);
            case 2 -> items.get(SLOT_INPUT_3);
            case 3 -> items.get(SLOT_TOOL);
            default -> throw new IndexOutOfBoundsException(index);
        };
    }

    private static boolean sameStackIgnoringCount(
            ItemStack first,
            ItemStack second
    ) {
        if (first.isEmpty() || second.isEmpty()) {
            return first.isEmpty()
                    && second.isEmpty();
        }

        return ItemStack.isSameItemSameComponents(
                first,
                second
        );
    }

    @Override
    public boolean canPlaceItem(
            int slot,
            ItemStack stack
    ) {
        if (slot == SLOT_OUTPUT) {
            return false;
        }

        if (slot == SLOT_TOOL) {
            return stack.is(
                    JolCraftTags.Items.PESTLES
            );
        }

        return true;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(
                JolCraftLanguageKeys.CONTAINER_MORTAR
        );
    }

    @Override
    protected AbstractContainerMenu createMenu(
            int id,
            Inventory playerInventory
    ) {
        return new MortarMenu(
                id,
                playerInventory,
                this
        );
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(
            NonNullList<ItemStack> items
    ) {
        this.items = items;
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

        items = NonNullList.withSize(
                SLOT_COUNT,
                ItemStack.EMPTY
        );

        ContainerHelper.loadAllItems(
                tag,
                items,
                registries
        );

        grindingProgress =
                tag.getInt(GRINDING_PROGRESS_TAG);

        grindingWork =
                tag.getInt(GRINDING_WORK_TAG);

        activeRecipeId =
                tag.getString(ACTIVE_RECIPE_TAG);

        activeRecipe = null;
        captureGrindingState();
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(
                tag,
                registries
        );

        ContainerHelper.saveAllItems(
                tag,
                items,
                registries
        );

        tag.putInt(
                GRINDING_PROGRESS_TAG,
                grindingProgress
        );

        tag.putInt(
                GRINDING_WORK_TAG,
                grindingWork
        );

        tag.putString(
                ACTIVE_RECIPE_TAG,
                activeRecipeId
        );
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }
}
