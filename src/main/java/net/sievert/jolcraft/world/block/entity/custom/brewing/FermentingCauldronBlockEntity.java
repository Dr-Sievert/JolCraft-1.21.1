package net.sievert.jolcraft.world.block.entity.custom.brewing;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.SyncingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.util.FermentingCauldronColorHelper;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.player.JolCraftStats;
import net.sievert.jolcraft.world.player.attachment.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipeInput;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingCauldronBlockEntity extends BlockEntity
        implements TickingBlockEntity, SyncingBlockEntity {

    private static final String NBT_BREW_START_TIME = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.START,
            JolCraftDictionary.TIME
    );

    private static final String NBT_BLEND_TOTAL_TICKS = JolCraftStrings.underscored(
            JolCraftDictionary.BLEND,
            JolCraftDictionary.TOTAL,
            JolCraftStrings.plural(JolCraftDictionary.TICK)
    );

    private static final String NBT_BUBBLE_TICKS = JolCraftStrings.underscored(
            JolCraftDictionary.BUBBLE,
            JolCraftStrings.plural(JolCraftDictionary.TICK)
    );

    private static final String NBT_BUBBLE_DELAY = JolCraftStrings.underscored(
            JolCraftDictionary.BUBBLE,
            JolCraftDictionary.DELAY
    );

    private static final String NBT_LAST_INGREDIENT_ID = JolCraftStrings.underscored(
            JolCraftDictionary.LAST,
            JolCraftDictionary.INGREDIENT,
            JolCraftDictionary.ID
    );

    private static final String NBT_INGREDIENTS = JolCraftStrings.plural(
            JolCraftDictionary.INGREDIENT
    );

    private static final String NBT_ITEM = JolCraftDictionary.ITEM;

    private static final String NBT_COUNT = JolCraftDictionary.COUNT;

    private static final String NBT_COLOR = JolCraftDictionary.COLOR;

    private static final String NBT_CURRENT_COLOR = JolCraftStrings.underscored(
            JolCraftDictionary.CURRENT,
            JolCraftDictionary.COLOR
    );

    private static final String NBT_START_COLOR = JolCraftStrings.underscored(
            JolCraftDictionary.START,
            JolCraftDictionary.COLOR
    );

    private static final String NBT_TARGET_COLOR = JolCraftStrings.underscored(
            JolCraftDictionary.TARGET,
            JolCraftDictionary.COLOR
    );

    private static final String NBT_FINALIZE = JolCraftDictionary.FINALIZE;

    private static final String NBT_EFFECTS = JolCraftStrings.plural(
            JolCraftDictionary.EFFECT
    );

    private static final String NBT_EFFECT_ID = JolCraftDictionary.ID;

    private static final String NBT_EFFECT_DURATION = JolCraftDictionary.DURATION;

    private static final String NBT_EFFECT_AMPLIFIER = JolCraftDictionary.AMPLIFIER;

    private static final String NBT_BREW_TANK = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.TANK
    );

    private static final int MAX_INGREDIENT_STACK = 10;
    private static final int MAX_FILL_LEVEL = 3;

    private static final int MUG_VOLUME = FluidType.BUCKET_VOLUME / MAX_FILL_LEVEL;

    private static final int FIRST_MUG_VOLUME = FluidType.BUCKET_VOLUME - MUG_VOLUME * 2;

    private static final LootContextParamSet EXECUTION_CONTEXT_PARAMS = new LootContextParamSet.Builder()
            .required(
                    JolCraftRecipeContextParams.INPUT_ITEM
            )
            .build();

    private ItemStack lastIngredient = ItemStack.EMPTY;

    private final HashMap<Item, IngredientData> ingredients = new HashMap<>();

    private record IngredientData(
            int count,
            int color
    ) implements FermentingCauldronColorHelper.IngredientView {}

    private final List<MobEffectInstance> effects = new ArrayList<>();

    private boolean finalize;

    private int bubbleTicks;
    private int bubbleDelay;

    private int currentColor = FermentingCauldronColorHelper.UNSET_COLOR;

    private int startColor = FermentingCauldronColorHelper.UNSET_COLOR;

    private int targetColor = FermentingCauldronColorHelper.UNSET_COLOR;

    private long brewStartTime;
    private int blendTotalTicks = 1;

    private final FluidTank brewTank = new FluidTank(
            FluidType.BUCKET_VOLUME,
            stack -> stack.is(
                    JolCraftFluids.DWARVEN_BREW.get()
            ) || stack.is(
                    JolCraftFluids.UNFINISHED_DWARVEN_BREW.get()
            )
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
            return createExtractedFluid(
                    brewTank.getFluid()
            );
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
            if (!canInsertBrew(resource)) {
                return 0;
            }

            return brewTank.fill(
                    withFreshBrewAge(resource),
                    action
            );
        }

        @Override
        public FluidStack drain(
                FluidStack resource,
                FluidAction action
        ) {
            if (!hasFinishedBrew() || !matchesStoredBrew(resource)) {
                return FluidStack.EMPTY;
            }

            return createExtractedFluid(
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
            if (!hasFinishedBrew()) {
                return FluidStack.EMPTY;
            }

            return createExtractedFluid(
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

    public ItemInteractionResult handleInteraction(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if (level == null || level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.FAIL;
        }

        if (isBrewing()) {
            return handleBrewingInteraction(
                    usedItem
            );
        }

        if (usedItem.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        if (usedItem.is(JolCraftItems.GLASS_MUG.get())) {
            return hasFinishedBrew()
                    ? tryExtractMug(
                    player,
                    hand,
                    usedItem
            )
                    : ItemInteractionResult.FAIL;
        }

        if (hasFinishedBrew() || containsDwarvenBrew(usedItem)) {
            return tryInteractFluidContainer(
                    player,
                    hand,
                    usedItem
            );
        }

        FermentingCauldronRecipe recipe = findRecipe(usedItem);

        if (recipe == null) {
            return ItemInteractionResult.FAIL;
        }

        return tryInsert(
                player,
                usedItem,
                recipe
        );
    }

    private ItemInteractionResult handleBrewingInteraction(
            ItemStack usedItem
    ) {
        if (level == null) {
            return ItemInteractionResult.FAIL;
        }

        if (!usedItem.is(JolCraftItems.DEV_KEY)) {
            return ItemInteractionResult.FAIL;
        }

        brewStartTime = level.getGameTime();
        blendTotalTicks = 1;
        bubbleDelay = 0;

        syncClient();

        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult tryInteractFluidContainer(
            Player player,
            InteractionHand hand,
            ItemStack usedItem
    ) {
        if(level == null) return ItemInteractionResult.FAIL;

        if (player.isCreative() && hasFinishedBrew()) {
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

        ItemStack output = createBrewMug(drained);

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

        player.awardStat(
                JolCraftStats.DWARVEN_BREWS_CREATED.get()
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

    private static boolean containsDwarvenBrew(
            ItemStack stack
    ) {
        return FluidUtil.getFluidContained(stack)
                .filter(fluid -> fluid.is(
                        JolCraftFluids.DWARVEN_BREW.get()
                ))
                .isPresent();
    }

    // =====================================================================
    // Recipe insertion
    // =====================================================================

    private ItemInteractionResult tryInsert(
            Player player,
            ItemStack usedItem,
            FermentingCauldronRecipe recipe
    ) {
        if (level == null || level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        Item itemKey = usedItem.getItem();

        ItemStack ingredientKey = usedItem.copyWithCount(1);

        IngredientData existing = ingredients.get(itemKey);

        int count = existing == null
                ? 0
                : existing.count();

        if (count >= MAX_INGREDIENT_STACK) {
            player.displayClientMessage(
                    Component.translatable(
                                    JolCraftLanguageKeys
                                            .TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX
                            )
                            .withStyle(
                                    ChatFormatting.GRAY
                            ),
                    true
            );

            return ItemInteractionResult.SUCCESS;
        }

        boolean hasRecipeEffect = recipe.effect().isPresent();

        if (hasRecipeEffect && !ingredients.isEmpty() && !ingredients.containsKey(itemKey) && !DwarfLoreAttachmentHelper.hasUnlock(
                player,
                DwarfLoreKey.FORGOTTEN_BREW_FORMULAS
        )) {
            player.displayClientMessage(
                    Component.translatable(
                                    JolCraftLanguageKeys
                                            .TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI
                            )
                            .withStyle(
                                    ChatFormatting.RED
                            ),
                    true
            );

            return ItemInteractionResult.SUCCESS;
        }

        return applyInsert(
                player,
                usedItem,
                ingredientKey,
                itemKey,
                count,
                recipe
        );
    }

    private ItemInteractionResult applyInsert(
            Player player,
            ItemStack usedItem,
            ItemStack ingredientKey,
            Item itemKey,
            int oldCount,
            FermentingCauldronRecipe recipe
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemInteractionResult.FAIL;
        }

        player.awardStat(
                Stats.ITEM_USED.get(
                        usedItem.getItem()
                )
        );

        if (!player.isCreative()) {
            usedItem.shrink(1);
        }

        JolCraftSoundHelper.block(
                level,
                worldPosition,
                SoundEvents.PLAYER_SPLASH,
                0.4F,
                1.5F
        );

        int newCount = Math.min(
                MAX_INGREDIENT_STACK,
                oldCount + 1
        );

        ingredients.put(
                itemKey,
                new IngredientData(
                        newCount,
                        recipe.brewColor()
                )
        );

        finalize = recipe.finalizeBrew();

        FermentingCauldronRecipeInput recipeInput = new FermentingCauldronRecipeInput(
                ingredientKey.copyWithCount(1),
                lastIngredient.isEmpty()
                        ? ItemStack.EMPTY
                        : lastIngredient.copyWithCount(1)
        );

        LootContext context = createExecutionContext(
                serverLevel,
                ingredientKey
        );

        recipe.generateEffect(
                context,
                recipeInput,
                effect -> upsertEffect(
                        effect,
                        newCount
                )
        );

        setLastIngredient(
                ingredientKey
        );

        startBrew(
                recipe.brewTicks(),
                recipe.bubbleTicks()
        );

        return ItemInteractionResult.SUCCESS;
    }

    private void upsertEffect(
            MobEffectInstance effect,
            int ingredientCount
    ) {
        if (effect.getDuration() < 1 || effect.getAmplifier() < 0) {
            return;
        }

        ResourceKey<MobEffect> key = effect.getEffect()
                .unwrapKey()
                .orElse(null);

        if (key == null) {
            return;
        }

        MobEffectInstance scaledEffect = new MobEffectInstance(
                effect.getEffect(),
                effect.getDuration() * ingredientCount,
                effect.getAmplifier(),
                effect.isAmbient(),
                effect.isVisible(),
                effect.showIcon()
        );

        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance existing = effects.get(i);

            ResourceKey<MobEffect> existingKey = existing.getEffect()
                    .unwrapKey()
                    .orElse(null);

            if (key.equals(existingKey)) {
                effects.set(
                        i,
                        scaledEffect
                );

                return;
            }
        }

        effects.add(
                scaledEffect
        );
    }

    private void setLastIngredient(
            ItemStack stack
    ) {
        lastIngredient = stack.isEmpty()
                ? ItemStack.EMPTY
                : stack.copyWithCount(1);
    }

    // =====================================================================
    // Brewing lifecycle
    // =====================================================================

    public boolean isBrewing() {
        return brewStartTime > 0L;
    }

    private boolean hasUnfinishedBrewFluid() {
        return brewTank
                .getFluid()
                .is(
                        JolCraftFluids.UNFINISHED_DWARVEN_BREW.get()
                );
    }

    public boolean hasFinishedBrew() {
        return brewTank
                .getFluid()
                .is(
                        JolCraftFluids.DWARVEN_BREW.get()
                );
    }

    private void startBrew(
            int recipeBlendTicks,
            int recipeBubbleTicks
    ) {
        if (level == null || level.isClientSide) {
            return;
        }

        currentColor = FermentingCauldronColorHelper
                .resolveBaseWaterColor(
                        level,
                        worldPosition,
                        currentColor
                );

        blendTotalTicks = Math.max(
                1,
                recipeBlendTicks
        );

        bubbleTicks = Math.max(
                0,
                recipeBubbleTicks
        );

        bubbleDelay = 0;

        startColor = currentColor;

        targetColor = FermentingCauldronColorHelper
                .computeMixedIngredientColor(
                        ingredients.values(),
                        currentColor
                );

        brewStartTime = level.getGameTime();

        brewTank.setFluid(
                createUnfinishedBrewFluid()
        );

        onBrewTankChanged();
    }

    private void finalizeBrew() {
        currentColor = targetColor;

        brewStartTime = 0L;
        blendTotalTicks = 1;

        bubbleTicks = 0;
        bubbleDelay = 0;

        startColor = currentColor;

        if (!finalize) {
            updateUnfinishedBrewFluid();
            return;
        }

        FluidStack finishedBrew = createFinishedBrewFluid();

        clearBrewingProcess();

        brewTank.setFluid(
                finishedBrew
        );

        onBrewTankChanged();
    }

    private FluidStack createUnfinishedBrewFluid() {
        FluidStack brew = new FluidStack(
                JolCraftFluids.UNFINISHED_DWARVEN_BREW.get(),
                FluidType.BUCKET_VOLUME
        );

        brew.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                currentColor
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                createPotionContents()
        );

        return brew;
    }

    private void updateUnfinishedBrewFluid() {
        if (!hasUnfinishedBrewFluid()) {
            return;
        }

        FluidStack brew = brewTank.getFluid();

        brew.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                currentColor
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                createPotionContents()
        );

        brew.setAmount(
                FluidType.BUCKET_VOLUME
        );

        onBrewTankChanged();
    }

    private FluidStack createFinishedBrewFluid() {
        FluidStack brew = new FluidStack(
                JolCraftFluids.DWARVEN_BREW.get(),
                FluidType.BUCKET_VOLUME
        );

        brew.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                currentColor
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                createPotionContents()
        );

        return brew;
    }

    private PotionContents createPotionContents() {
        if (effects.isEmpty()) {
            return PotionContents.EMPTY;
        }

        List<MobEffectInstance> customEffects = new ArrayList<>(
                effects.size()
        );

        for (MobEffectInstance effect : effects) {
            if (effect.getDuration() < 1 || effect.getAmplifier() < 0) {
                continue;
            }

            customEffects.add(
                    new MobEffectInstance(effect)
            );
        }

        if (customEffects.isEmpty()) {
            return PotionContents.EMPTY;
        }

        return new PotionContents(
                Optional.empty(),
                Optional.empty(),
                List.copyOf(customEffects)
        );
    }

    private void clearBrewingProcess() {
        lastIngredient = ItemStack.EMPTY;

        ingredients.clear();
        effects.clear();

        finalize = false;

        bubbleTicks = 0;
        bubbleDelay = 0;

        currentColor = FermentingCauldronColorHelper.UNSET_COLOR;

        startColor = FermentingCauldronColorHelper.UNSET_COLOR;

        targetColor = FermentingCauldronColorHelper.UNSET_COLOR;

        brewStartTime = 0L;
        blendTotalTicks = 1;
    }

    // =====================================================================
    // Fluid handling
    // =====================================================================

    private void onBrewTankChanged() {
        setChanged();

        if (level == null || level.isClientSide) {
            return;
        }

        if (!updateBlockLevelFromTank()) {
            return;
        }

        syncClient();
    }

    private boolean updateBlockLevelFromTank() {
        if (level == null || level.isClientSide) {
            return true;
        }

        int amount = brewTank.getFluidAmount();

        if (amount <= 0) {
            level.setBlockAndUpdate(
                    worldPosition,
                    Blocks.CAULDRON.defaultBlockState()
            );

            return false;
        }

        BlockState state = level.getBlockState(worldPosition);

        if (!state.hasProperty(
                LayeredCauldronBlock.LEVEL
        )) {
            return true;
        }

        int fillLevel = Mth.clamp(
                (
                        amount * MAX_FILL_LEVEL
                                + FluidType.BUCKET_VOLUME
                                - 1
                ) / FluidType.BUCKET_VOLUME,
                1,
                MAX_FILL_LEVEL
        );

        if (state.getValue(
                LayeredCauldronBlock.LEVEL
        ) == fillLevel) {
            return true;
        }

        level.setBlockAndUpdate(
                worldPosition,
                state.setValue(
                        LayeredCauldronBlock.LEVEL,
                        fillLevel
                )
        );

        return true;
    }

    private boolean canInsertBrew(
            FluidStack incoming
    ) {
        if (incoming.isEmpty() || !incoming.is(JolCraftFluids.DWARVEN_BREW.get()) || getBrewAge(incoming) > 0L || hasUnfinishedBrew()) {
            return false;
        }

        return brewTank.isEmpty() || matchesIgnoringAge(
                incoming,
                brewTank.getFluid()
        );
    }

    private boolean hasUnfinishedBrew() {
        return isBrewing() || !ingredients.isEmpty() || !effects.isEmpty() || currentColor != FermentingCauldronColorHelper.UNSET_COLOR;
    }

    private static long getBrewAge(
            FluidStack brew
    ) {
        return brew.getOrDefault(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );
    }

    private static FluidStack withFreshBrewAge(
            FluidStack brew
    ) {
        if (brew.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack fresh = brew.copy();

        fresh.set(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        return fresh;
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

    private static FluidStack createExtractedFluid(
            FluidStack stored
    ) {
        return withFreshBrewAge(stored);
    }

    private boolean matchesStoredBrew(
            FluidStack requested
    ) {
        return !brewTank.isEmpty() && matchesIgnoringAge(
                requested,
                brewTank.getFluid()
        );
    }

    private void sanitizeLoadedTank() {
        FluidStack brew = brewTank.getFluid();

        if (brew.isEmpty()) {
            return;
        }

        boolean finished = brew.is(
                JolCraftFluids.DWARVEN_BREW.get()
        );

        boolean unfinished = brew.is(
                JolCraftFluids.UNFINISHED_DWARVEN_BREW.get()
        );

        if (!finished && !unfinished) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} loaded invalid fluid '{}' (clearing)",
                    JolCraftLogs.roundedPos(this),
                    brew.getFluid()
                            .builtInRegistryHolder()
                            .key()
                            .location()
            );

            brewTank.setFluid(
                    FluidStack.EMPTY
            );

            return;
        }

        if (getBrewAge(brew) > 0L) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} loaded aged brew (clearing)",
                    JolCraftLogs.roundedPos(this)
            );

            brewTank.setFluid(
                    FluidStack.EMPTY
            );

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
                0L
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                brew.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                )
        );
    }

    // =====================================================================
    // Server ticking / sleep skipping
    // =====================================================================

    @Override
    public void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }

        if (!isBrewing()) {
            return;
        }

        doBubbleEffects();

        if (FermentingCauldronColorHelper.isComplete(
                level,
                brewStartTime,
                blendTotalTicks
        )) {
            finalizeBrew();
        }
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
                                FermentingCauldronBlockEntity cauldron)) {
                            continue;
                        }

                        if (!cauldron.isBrewing()) {
                            continue;
                        }

                        if (!seen.add(
                                blockEntity.getBlockPos()
                        )) {
                            continue;
                        }

                        cauldron.fastForwardBrew(
                                skipped
                        );
                    }
                }
            }
        }
    }

    public void fastForwardBrew(
            long skippedTicks
    ) {
        if (level == null || level.isClientSide) {
            return;
        }

        if (skippedTicks <= 0L || !isBrewing()) {
            return;
        }

        long newStart = FermentingCauldronColorHelper
                .fastForwardStartTime(
                        level,
                        brewStartTime,
                        blendTotalTicks,
                        skippedTicks
                );

        if (newStart <= 0L) {
            finalizeBrew();
            return;
        }

        brewStartTime = newStart;

        bubbleDelay = 0;

        syncClient();
    }

    private void doBubbleEffects() {
        if (level == null || level.isClientSide || !isBrewing() || bubbleTicks <= 0) {
            return;
        }

        if (bubbleDelay > 0) {
            bubbleDelay--;
            return;
        }

        double x = worldPosition.getX()
                + 0.5D
                + (
                level.random.nextDouble()
                        - 0.5D
        );

        double y = worldPosition.getY()
                + 1.01D;

        double z = worldPosition.getZ()
                + 0.5D
                + (
                level.random.nextDouble()
                        - 0.5D
        );

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

        bubbleDelay = 3 + level.random.nextInt(
                bubbleTicks
        );
    }

    // =====================================================================
    // Recipe lookup
    // =====================================================================

    @Nullable
    private FermentingCauldronRecipe findRecipe(
            ItemStack usedItem
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        ItemStack ingredient = usedItem.copyWithCount(1);

        ItemStack last = lastIngredient.isEmpty()
                ? ItemStack.EMPTY
                : lastIngredient.copyWithCount(1);

        FermentingCauldronRecipeInput input = new FermentingCauldronRecipeInput(
                ingredient,
                last
        );

        return serverLevel
                .getRecipeManager()
                .getRecipeFor(
                        JolCraftRecipes
                                .FERMENTING_CAULDRON_TYPE
                                .get(),
                        input,
                        serverLevel
                )
                .map(
                        RecipeHolder::value
                )
                .orElse(null);
    }

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
    // Level attachment / client sync
    // =====================================================================

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (level.isClientSide) {
            return;
        }

        if (!brewTank.isEmpty()) {
            updateBlockLevelFromTank();
        }

        if (isBrewing() && FermentingCauldronColorHelper.isComplete(
                level,
                brewStartTime,
                blendTotalTicks
        )) {
            finalizeBrew();
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

    private void writeClientData(
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

        if (hasFinishedBrew()) {
            return;
        }

        tag.putInt(
                NBT_CURRENT_COLOR,
                currentColor
        );

        tag.putInt(
                NBT_START_COLOR,
                startColor
        );

        tag.putInt(
                NBT_TARGET_COLOR,
                targetColor
        );

        tag.putLong(
                NBT_BREW_START_TIME,
                brewStartTime
        );

        tag.putInt(
                NBT_BLEND_TOTAL_TICKS,
                blendTotalTicks
        );
    }

    private void readClientData(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        brewTank.setFluid(
                FluidStack.EMPTY
        );

        clearBrewingProcess();

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

            sanitizeLoadedTank();

            if (hasFinishedBrew()) {
                return;
            }
        }

        currentColor = tag.contains(
                NBT_CURRENT_COLOR,
                Tag.TAG_INT
        )
                ? tag.getInt(
                NBT_CURRENT_COLOR
        )
                : FermentingCauldronColorHelper.UNSET_COLOR;

        startColor = tag.contains(
                NBT_START_COLOR,
                Tag.TAG_INT
        )
                ? tag.getInt(
                NBT_START_COLOR
        )
                : currentColor;

        targetColor = tag.contains(
                NBT_TARGET_COLOR,
                Tag.TAG_INT
        )
                ? tag.getInt(
                NBT_TARGET_COLOR
        )
                : currentColor;

        brewStartTime = tag.getLong(
                NBT_BREW_START_TIME
        );

        blendTotalTicks = Math.max(
                1,
                tag.getInt(
                        NBT_BLEND_TOTAL_TICKS
                )
        );
    }

    // =====================================================================
    // Persistent storage
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

        if (!brewTank.isEmpty()) {
            tag.put(
                    NBT_BREW_TANK,
                    brewTank.writeToNBT(
                            registries,
                            new CompoundTag()
                    )
            );
        }

        if (hasFinishedBrew()) {
            return;
        }

        tag.putLong(
                NBT_BREW_START_TIME,
                brewStartTime
        );

        tag.putInt(
                NBT_BLEND_TOTAL_TICKS,
                blendTotalTicks
        );

        tag.putInt(
                NBT_BUBBLE_TICKS,
                bubbleTicks
        );

        tag.putInt(
                NBT_BUBBLE_DELAY,
                bubbleDelay
        );

        if (!lastIngredient.isEmpty()) {
            ResourceLocation id = lastIngredient
                    .getItem()
                    .builtInRegistryHolder()
                    .key()
                    .location();

            tag.putString(
                    NBT_LAST_INGREDIENT_ID,
                    id.toString()
            );
        }

        saveIngredients(tag);

        tag.putInt(
                NBT_CURRENT_COLOR,
                currentColor
        );

        tag.putInt(
                NBT_START_COLOR,
                startColor
        );

        tag.putInt(
                NBT_TARGET_COLOR,
                targetColor
        );

        tag.putBoolean(
                NBT_FINALIZE,
                finalize
        );

        saveEffects(tag);
    }

    private void saveIngredients(
            CompoundTag tag
    ) {
        if (ingredients.isEmpty()) {
            return;
        }

        ListTag list = new ListTag();

        for (var entry : ingredients.entrySet()) {
            Item item = entry.getKey();

            IngredientData data = entry.getValue();

            if (item == Items.AIR || data == null || data.count() <= 0) {
                continue;
            }

            ResourceLocation id = item.builtInRegistryHolder()
                    .key()
                    .location();

            CompoundTag ingredientTag = new CompoundTag();

            ingredientTag.putString(
                    NBT_ITEM,
                    id.toString()
            );

            if (data.count() != 1) {
                ingredientTag.putInt(
                        NBT_COUNT,
                        data.count()
                );
            }

            ingredientTag.putInt(
                    NBT_COLOR,
                    data.color()
            );

            list.add(
                    ingredientTag
            );
        }

        if (!list.isEmpty()) {
            tag.put(
                    NBT_INGREDIENTS,
                    list
            );
        }
    }

    private void saveEffects(
            CompoundTag tag
    ) {
        if (effects.isEmpty()) {
            return;
        }

        ListTag list = new ListTag();

        for (MobEffectInstance effect : effects) {
            ResourceKey<MobEffect> key = effect.getEffect()
                    .unwrapKey()
                    .orElse(null);

            if (key == null) {
                continue;
            }

            CompoundTag effectTag = new CompoundTag();

            effectTag.putString(
                    NBT_EFFECT_ID,
                    key.location().toString()
            );

            effectTag.putInt(
                    NBT_EFFECT_DURATION,
                    effect.getDuration()
            );

            if (effect.getAmplifier() != 0) {
                effectTag.putInt(
                        NBT_EFFECT_AMPLIFIER,
                        effect.getAmplifier()
                );
            }

            list.add(
                    effectTag
            );
        }

        if (!list.isEmpty()) {
            tag.put(
                    NBT_EFFECTS,
                    list
            );
        }
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

        brewTank.setFluid(
                FluidStack.EMPTY
        );

        clearBrewingProcess();

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

            sanitizeLoadedTank();

            if (hasFinishedBrew()) {
                return;
            }
        }

        var itemLookup = registries.lookupOrThrow(
                Registries.ITEM
        );

        var effectLookup = registries.lookupOrThrow(
                Registries.MOB_EFFECT
        );

        brewStartTime = tag.getLong(
                NBT_BREW_START_TIME
        );

        blendTotalTicks = Math.max(
                1,
                tag.getInt(
                        NBT_BLEND_TOTAL_TICKS
                )
        );

        bubbleTicks = Math.max(
                0,
                tag.getInt(
                        NBT_BUBBLE_TICKS
                )
        );

        bubbleDelay = Math.max(
                0,
                tag.getInt(
                        NBT_BUBBLE_DELAY
                )
        );

        loadLastIngredient(
                tag,
                itemLookup
        );

        loadIngredients(
                tag,
                itemLookup
        );

        finalize = tag.getBoolean(
                NBT_FINALIZE
        );

        currentColor = tag.contains(
                NBT_CURRENT_COLOR,
                Tag.TAG_INT
        )
                ? tag.getInt(
                NBT_CURRENT_COLOR
        )
                : FermentingCauldronColorHelper.UNSET_COLOR;

        startColor = tag.contains(
                NBT_START_COLOR,
                Tag.TAG_INT
        )
                ? tag.getInt(
                NBT_START_COLOR
        )
                : currentColor;

        targetColor = tag.contains(
                NBT_TARGET_COLOR,
                Tag.TAG_INT
        )
                ? tag.getInt(
                NBT_TARGET_COLOR
        )
                : currentColor;

        loadEffects(
                tag,
                effectLookup
        );
    }

    private void loadLastIngredient(
            CompoundTag tag,
            HolderLookup.RegistryLookup<Item> itemLookup
    ) {
        if (!tag.contains(
                NBT_LAST_INGREDIENT_ID,
                Tag.TAG_STRING
        )) {
            lastIngredient = ItemStack.EMPTY;

            return;
        }

        String raw = tag.getString(
                NBT_LAST_INGREDIENT_ID
        );

        ResourceLocation id = ResourceLocation.tryParse(raw);

        if (id == null) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} has malformed lastIngredient name '{}' (clearing)",
                    JolCraftLogs.roundedPos(this),
                    raw
            );

            lastIngredient = ItemStack.EMPTY;

            return;
        }

        Item item = itemLookup
                .get(
                        ResourceKey.create(
                                Registries.ITEM,
                                id
                        )
                )
                .map(
                        Holder.Reference::value
                )
                .orElse(
                        Items.AIR
                );

        if (item == Items.AIR) {
            JolCraftLogs.debug(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} missing lastIngredient item '{}' (clearing)",
                    JolCraftLogs.roundedPos(this),
                    id
            );

            lastIngredient = ItemStack.EMPTY;

            return;
        }

        lastIngredient = new ItemStack(item);
    }

    private void loadIngredients(
            CompoundTag tag,
            HolderLookup.RegistryLookup<Item> itemLookup
    ) {
        ingredients.clear();

        if (!tag.contains(
                NBT_INGREDIENTS,
                Tag.TAG_LIST
        )) {
            return;
        }

        ListTag list = tag.getList(
                NBT_INGREDIENTS,
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < list.size(); i++) {
            CompoundTag ingredientTag = list.getCompound(i);

            if (!ingredientTag.contains(
                    NBT_ITEM,
                    Tag.TAG_STRING
            )) {
                continue;
            }

            String raw = ingredientTag.getString(
                    NBT_ITEM
            );

            ResourceLocation id = ResourceLocation.tryParse(raw);

            if (id == null) {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} has malformed ingredient name '{}' (skipping)",
                        JolCraftLogs.roundedPos(this),
                        raw
                );

                continue;
            }

            Item item = itemLookup
                    .get(
                            ResourceKey.create(
                                    Registries.ITEM,
                                    id
                            )
                    )
                    .map(
                            Holder.Reference::value
                    )
                    .orElse(
                            Items.AIR
                    );

            if (item == Items.AIR) {
                JolCraftLogs.debug(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} missing ingredient item '{}' (skipping)",
                        JolCraftLogs.roundedPos(this),
                        id
                );

                continue;
            }

            int count = ingredientTag.contains(
                    NBT_COUNT,
                    Tag.TAG_INT
            )
                    ? ingredientTag.getInt(
                    NBT_COUNT
            )
                    : 1;

            if (count <= 0) {
                continue;
            }

            count = Math.min(
                    MAX_INGREDIENT_STACK,
                    count
            );

            int color = ingredientTag.contains(
                    NBT_COLOR,
                    Tag.TAG_INT
            )
                    ? ingredientTag.getInt(
                    NBT_COLOR
            )
                    : 0xFFFFFFFF;

            ingredients.put(
                    item,
                    new IngredientData(
                            count,
                            color
                    )
            );
        }
    }

    private void loadEffects(
            CompoundTag tag,
            HolderLookup.RegistryLookup<MobEffect> effectLookup
    ) {
        effects.clear();

        if (!tag.contains(
                NBT_EFFECTS,
                Tag.TAG_LIST
        )) {
            return;
        }

        ListTag list = tag.getList(
                NBT_EFFECTS,
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < list.size(); i++) {
            CompoundTag effectTag = list.getCompound(i);

            if (!effectTag.contains(
                    NBT_EFFECT_ID,
                    Tag.TAG_STRING
            )) {
                continue;
            }

            String raw = effectTag.getString(
                    NBT_EFFECT_ID
            );

            ResourceLocation id = ResourceLocation.tryParse(raw);

            if (id == null) {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} has malformed effect name '{}' (skipping)",
                        JolCraftLogs.roundedPos(worldPosition),
                        raw
                );

                continue;
            }

            ResourceKey<MobEffect> key = ResourceKey.create(
                    Registries.MOB_EFFECT,
                    id
            );

            Holder<MobEffect> holder = effectLookup
                    .get(key)
                    .orElse(null);

            if (holder == null) {
                JolCraftLogs.debug(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} missing MobEffect '{}' (skipping)",
                        JolCraftLogs.roundedPos(this),
                        id
                );

                continue;
            }

            int duration = effectTag.getInt(
                    NBT_EFFECT_DURATION
            );

            if (duration < 1) {
                continue;
            }

            int amplifier = effectTag.contains(
                    NBT_EFFECT_AMPLIFIER,
                    Tag.TAG_INT
            )
                    ? effectTag.getInt(
                    NBT_EFFECT_AMPLIFIER
            )
                    : 0;

            effects.add(
                    new MobEffectInstance(
                            holder,
                            duration,
                            Math.max(
                                    0,
                                    amplifier
                            )
                    )
            );
        }
    }

    // =====================================================================
    // Renderer / integration access
    // =====================================================================

    public int getCurrentColor() {
        return hasFinishedBrew()
                ? getFinishedBrewColor()
                : currentColor;
    }

    public int getStartColor() {
        return hasFinishedBrew()
                ? getFinishedBrewColor()
                : startColor;
    }

    public int getTargetColor() {
        return hasFinishedBrew()
                ? getFinishedBrewColor()
                : targetColor;
    }

    private int getFinishedBrewColor() {
        return brewTank
                .getFluid()
                .getOrDefault(
                        JolCraftDataComponents.BREW_COLOR.get(),
                        0xFFFFFFFF
                );
    }

    public long getBrewStartTime() {
        return brewStartTime;
    }

    public int getBlendTotalTicks() {
        return blendTotalTicks;
    }

    public int getBrewAmount() {
        return brewTank.getFluidAmount();
    }

    public FluidStack getBrewFluid() {
        return brewTank
                .getFluid()
                .copy();
    }

    public IFluidHandler getBrewFluidHandler() {
        return brewFluidHandler;
    }
}