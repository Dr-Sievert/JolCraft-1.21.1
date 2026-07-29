package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.gui.menu.LapidaryBenchMenu;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.player.JolCraftStats;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryRecipeInput;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchBlockEntity extends BaseContainerBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_OUTPUT = 2;

    private static final int SLOT_COUNT = 3;

    /*
     * Runtime parameters available while generating the lapidary result,
     * XP, tool damage, and sound.
     *
     * INPUT_ITEM identifies the material being processed.
     */
    private static final LootContextParamSet OUTPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(JolCraftRecipeContextParams.INPUT_ITEM)
                    .optional(LootContextParams.THIS_ENTITY)
                    .optional(LootContextParams.ORIGIN)
                    .build();

    private NonNullList<ItemStack> items =
            NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    private boolean recipeValid;
    private int actionId = -1;

    public LapidaryBenchBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                JolCraftBlockEntities.LAPIDARY_BENCH.get(),
                pos,
                state
        );
    }

    // ---------------------------------------------------------------------
    // ACTION
    // ---------------------------------------------------------------------

    public void handleAction(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        Optional<LapidaryBenchRecipe> recipeResult =
                findValidRecipe(player);

        if (recipeResult.isEmpty()) {
            return;
        }

        LapidaryBenchRecipe recipe = recipeResult.orElseThrow();

        ItemStack inputStack = items.get(SLOT_INPUT);
        ItemStack toolStack = items.get(SLOT_TOOL);

        LapidaryRecipeInput recipeInput =
                new LapidaryRecipeInput(
                        inputStack,
                        toolStack
                );

        /*
         * Create one shared context for this execution.
         *
         * This ensures the recipe output, XP, tool damage, and sound are
         * rolled only once and all use the same runtime random source.
         */
        LootContext context = createExecutionContext(
                level,
                player,
                inputStack
        );

        List<ItemStack> generatedResults = new ArrayList<>();
        recipe.generateResult(
                context,
                recipeInput,
                generatedResults::add
        );

        generatedResults.removeIf(ItemStack::isEmpty);

        if (!player.isCreative()) {
            consumeInput();

            int toolDamage = recipe.rollToolDamage(context);
            damageTool(player, toolDamage);
        }

        if (generatedResults.isEmpty()) {
            setChanged();
            refreshCachedState(player);
            return;
        }

        boolean wasGeode =
                inputStack.is(JolCraftTags.Items.GEODES);

        boolean wasUncutGem =
                inputStack.is(JolCraftTags.Items.GEMS_UNCUT);

        boolean wasHammer =
                toolStack.is(JolCraftTags.Items.ARTISAN_HAMMERS);

        boolean wasChisel =
                toolStack.is(JolCraftTags.Items.CHISELS);

        for (ItemStack generatedResult : generatedResults) {
            JolCraftItemInsertionHelper.tryInsertIntoSlotInventoryOrDrop(
                    this,
                    SLOT_OUTPUT,
                    player.getInventory(),
                    player,
                    generatedResult
            );
        }

        int xp = recipe.rollXp(context);

        if (xp > 0) {
            player.giveExperiencePoints(xp);
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

        awardStats(
                player,
                wasGeode,
                wasUncutGem,
                wasHammer,
                wasChisel
        );

        spawnParticles(level, player);

        setChanged();
        refreshCachedState(player);
    }

    // ---------------------------------------------------------------------
    // MENU SYNC
    // ---------------------------------------------------------------------

    private final ContainerData containerData = new ContainerData() {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> recipeValid ? 1 : 0;
                case 1 -> actionId;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> recipeValid = value != 0;
                case 1 -> actionId = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ContainerData getContainerData() {
        return containerData;
    }

    // ---------------------------------------------------------------------
    // VALIDATION / UI GATING
    // ---------------------------------------------------------------------

    public void refreshCachedState(ServerPlayer player) {
        int nextActionId = computeActionId();

        /*
         * This checks only matching and player unlock requirements.
         * It does not roll recipe outputs.
         */
        boolean nextRecipeValid =
                findValidRecipe(player).isPresent();

        if (actionId == nextActionId
                && recipeValid == nextRecipeValid) {
            return;
        }

        actionId = nextActionId;
        recipeValid = nextRecipeValid;

        setChanged();
    }

    private int computeActionId() {
        ItemStack tool = items.get(SLOT_TOOL);

        if (tool.is(JolCraftTags.Items.ARTISAN_HAMMERS)) {
            return 0;
        }

        if (tool.is(JolCraftTags.Items.CHISELS)) {
            return 1;
        }

        return -1;
    }

    // ---------------------------------------------------------------------
    // RECIPE RESOLUTION
    // ---------------------------------------------------------------------

    private Optional<LapidaryBenchRecipe> findValidRecipe(
            ServerPlayer player
    ) {
        if (!(getLevel() instanceof ServerLevel level)) {
            return Optional.empty();
        }

        ItemStack inputStack = items.get(SLOT_INPUT);
        ItemStack toolStack = items.get(SLOT_TOOL);

        if (inputStack.isEmpty() || toolStack.isEmpty()) {
            return Optional.empty();
        }

        LapidaryRecipeInput input = new LapidaryRecipeInput(
                inputStack,
                toolStack
        );

        Optional<LapidaryBenchRecipe> recipe =
                level.getRecipeManager()
                        .getRecipeFor(
                                JolCraftRecipes.LAPIDARY_BENCH_TYPE.get(),
                                input,
                                level
                        )
                        .map(holder -> holder.value());

        if (recipe.isEmpty()) {
            return Optional.empty();
        }

        LapidaryBenchRecipe resolved = recipe.orElseThrow();

        /*
         * RecipeManager#getRecipeFor has already called matches(...).
         * Only the player-specific lore requirement remains.
         */
        if (!resolved.isUnlockedFor(player, toolStack)) {
            return Optional.empty();
        }

        return Optional.of(resolved);
    }

    private LootContext createExecutionContext(
            ServerLevel level,
            ServerPlayer player,
            ItemStack inputStack
    ) {
        return JolCraftRecipeContexts.create(
                level,
                level.random,
                OUTPUT_CONTEXT_PARAMS,
                builder -> builder
                        .withParameter(
                                JolCraftRecipeContextParams.INPUT_ITEM,
                                inputStack
                        )
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

    // ---------------------------------------------------------------------
    // INGREDIENT MUTATION
    // ---------------------------------------------------------------------

    private void consumeInput() {
        ItemStack inputStack = items.get(SLOT_INPUT);

        if (inputStack.isEmpty()) {
            return;
        }

        inputStack.shrink(1);

        if (inputStack.isEmpty()) {
            items.set(SLOT_INPUT, ItemStack.EMPTY);
        }
    }

    private void damageTool(
            ServerPlayer player,
            int damage
    ) {
        if (damage <= 0) {
            return;
        }

        ItemStack toolStack = items.get(SLOT_TOOL);

        if (toolStack.isEmpty() || !toolStack.isDamageableItem()) {
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
            items.set(SLOT_TOOL, ItemStack.EMPTY);
        }
    }

    // ---------------------------------------------------------------------
    // FEEDBACK
    // ---------------------------------------------------------------------

    private static void awardStats(
            ServerPlayer player,
            boolean wasGeode,
            boolean wasUncutGem,
            boolean wasHammer,
            boolean wasChisel
    ) {
        if (wasGeode) {
            player.awardStat(
                    JolCraftStats.GEODES_CRACKED.get()
            );
        }

        if (!wasUncutGem) {
            return;
        }

        if (wasHammer) {
            player.awardStat(
                    JolCraftStats.GEMS_CRUSHED.get()
            );
        }

        if (wasChisel) {
            player.awardStat(
                    JolCraftStats.GEMS_CUT.get()
            );
        }
    }

    private static void spawnParticles(
            ServerLevel level,
            ServerPlayer player
    ) {
        JolCraftParticleHelper.spawn(
                level,
                ParticleTypes.CRIT,
                player.getX(),
                player.getY() + 1.1D,
                player.getZ(),
                1,
                (level.random.nextDouble() - 0.5D) * 0.24D,
                level.random.nextDouble() * 0.10D,
                (level.random.nextDouble() - 0.5D) * 0.24D,
                0.0D
        );
    }

    // ---------------------------------------------------------------------
    // VANILLA CONTAINER
    // ---------------------------------------------------------------------

    @Override
    protected Component getDefaultName() {
        return Component.translatable(
                JolCraftLanguageKeys.CONTAINER_LAPIDARY_BENCH
        );
    }

    @Override
    protected AbstractContainerMenu createMenu(
            int id,
            Inventory playerInventory
    ) {
        return new LapidaryBenchMenu(
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
        super.loadAdditional(tag, registries);

        items = NonNullList.withSize(
                SLOT_COUNT,
                ItemStack.EMPTY
        );

        ContainerHelper.loadAllItems(
                tag,
                items,
                registries
        );
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);

        ContainerHelper.saveAllItems(
                tag,
                items,
                registries
        );
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }
}