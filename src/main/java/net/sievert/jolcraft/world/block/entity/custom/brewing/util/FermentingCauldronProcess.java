package net.sievert.jolcraft.world.block.entity.custom.brewing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipeInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FermentingCauldronProcess {

    public static final int MAX_INGREDIENT_STACK = 10;

    private static final String NBT_BREW_START_TIME =
            JolCraftStrings.underscored(
                    JolCraftDictionary.BREW,
                    JolCraftDictionary.START,
                    JolCraftDictionary.TIME
            );

    private static final String NBT_BLEND_TOTAL_TICKS =
            JolCraftStrings.underscored(
                    JolCraftDictionary.BLEND,
                    JolCraftDictionary.TOTAL,
                    JolCraftStrings.plural(
                            JolCraftDictionary.TICK
                    )
            );

    private static final String NBT_BUBBLE_TICKS =
            JolCraftStrings.underscored(
                    JolCraftDictionary.BUBBLE,
                    JolCraftStrings.plural(
                            JolCraftDictionary.TICK
                    )
            );

    private static final String NBT_BUBBLE_DELAY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.BUBBLE,
                    JolCraftDictionary.DELAY
            );

    private static final String NBT_LAST_INGREDIENT_ID =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LAST,
                    JolCraftDictionary.INGREDIENT,
                    JolCraftDictionary.ID
            );

    private static final String NBT_INGREDIENTS =
            JolCraftStrings.plural(
                    JolCraftDictionary.INGREDIENT
            );

    private static final String NBT_ITEM =
            JolCraftDictionary.ITEM;

    private static final String NBT_COUNT =
            JolCraftDictionary.COUNT;

    private static final String NBT_COLOR =
            JolCraftDictionary.COLOR;

    private static final String NBT_CURRENT_COLOR =
            JolCraftStrings.underscored(
                    JolCraftDictionary.CURRENT,
                    JolCraftDictionary.COLOR
            );

    private static final String NBT_START_COLOR =
            JolCraftStrings.underscored(
                    JolCraftDictionary.START,
                    JolCraftDictionary.COLOR
            );

    private static final String NBT_TARGET_COLOR =
            JolCraftStrings.underscored(
                    JolCraftDictionary.TARGET,
                    JolCraftDictionary.COLOR
            );

    private static final String NBT_FINALIZE =
            JolCraftDictionary.FINALIZE;

    private static final String NBT_OUTPUT_FLUID =
            "output_fluid";

    private static final String NBT_EFFECTS =
            JolCraftStrings.plural(
                    JolCraftDictionary.EFFECT
            );

    private static final String NBT_EFFECT_ID =
            JolCraftDictionary.ID;

    private static final String NBT_EFFECT_DURATION =
            JolCraftDictionary.DURATION;

    private static final String NBT_EFFECT_AMPLIFIER =
            JolCraftDictionary.AMPLIFIER;

    private ItemStack lastIngredient =
            ItemStack.EMPTY;

    private final Map<Item, IngredientData> ingredients =
            new HashMap<>();

    private final List<MobEffectInstance> effects =
            new ArrayList<>();

    private boolean finalize;

    private FermentingCauldronRecipe.OutputFluid outputFluid =
            FermentingCauldronRecipe.DEFAULT_OUTPUT_FLUID;

    private int bubbleTicks;
    private int bubbleDelay;

    private int currentColor =
            FermentingCauldronColorHelper.UNSET_COLOR;

    private int startColor =
            FermentingCauldronColorHelper.UNSET_COLOR;

    private int targetColor =
            FermentingCauldronColorHelper.UNSET_COLOR;

    private long brewStartTime = -1L;

    private int blendTotalTicks = 1;

    public record IngredientData(
            int count,
            int color
    ) implements
            FermentingCauldronColorHelper.IngredientView {}

    // =====================================================================
    // State access
    // =====================================================================

    public boolean isBrewing() {
        return brewStartTime >= 0L;
    }

    public boolean hasIngredients() {
        return !ingredients.isEmpty();
    }

    public boolean hasEffects() {
        return !effects.isEmpty();
    }

    public boolean hasUnfinishedState() {
        return isBrewing()
                || hasIngredients()
                || hasEffects()
                || currentColor
                != FermentingCauldronColorHelper.UNSET_COLOR;
    }

    public FermentingCauldronRecipe.OutputFluid getOutputFluid() {
        return outputFluid;
    }

    public int getIngredientCount(
            Item item
    ) {
        IngredientData data =
                ingredients.get(item);

        return data == null
                ? 0
                : data.count();
    }

    public boolean containsIngredient(
            Item item
    ) {
        return ingredients.containsKey(
                item
        );
    }

    public ItemStack getLastIngredient() {
        return lastIngredient.isEmpty()
                ? ItemStack.EMPTY
                : lastIngredient.copy();
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public int getStartColor() {
        return startColor;
    }

    public int getTargetColor() {
        return targetColor;
    }

    public long getBrewStartTime() {
        return brewStartTime;
    }

    public int getBlendTotalTicks() {
        return blendTotalTicks;
    }

    public int getBubbleTicks() {
        return bubbleTicks;
    }

    // =====================================================================
    // Ingredient application
    // =====================================================================

    public void applyIngredient(
            Level level,
            BlockPos pos,
            ItemStack ingredient,
            FermentingCauldronRecipe recipe,
            LootContext context
    ) {
        Item item = ingredient.getItem();

        int oldCount = getIngredientCount(
                item
        );

        int newCount = Math.min(
                MAX_INGREDIENT_STACK,
                oldCount + 1
        );

        ingredients.put(
                item,
                new IngredientData(
                        newCount,
                        recipe.brewColor()
                )
        );

        finalize = recipe.finalizeBrew();
        outputFluid = recipe.outputFluid();

        FermentingCauldronRecipeInput recipeInput =
                new FermentingCauldronRecipeInput(
                        ingredient.copyWithCount(1),
                        lastIngredient.isEmpty()
                                ? ItemStack.EMPTY
                                : lastIngredient.copyWithCount(1)
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
                ingredient
        );

        startBrew(
                level,
                pos,
                recipe.brewTicks(),
                recipe.bubbleTicks()
        );
    }

    private void upsertEffect(
            MobEffectInstance effect,
            int ingredientCount
    ) {
        if (effect.getDuration() < 1
                || effect.getAmplifier() < 0) {
            return;
        }

        ResourceKey<MobEffect> key =
                effect.getEffect()
                        .unwrapKey()
                        .orElse(null);

        if (key == null) {
            return;
        }

        int scaledDuration =
                (int) Math.min(
                        Integer.MAX_VALUE,
                        (long) effect.getDuration()
                                * ingredientCount
                );

        MobEffectInstance scaledEffect =
                new MobEffectInstance(
                        effect.getEffect(),
                        scaledDuration,
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.isVisible(),
                        effect.showIcon()
                );

        for (
                int index = 0;
                index < effects.size();
                index++
        ) {
            MobEffectInstance existing =
                    effects.get(index);

            ResourceKey<MobEffect> existingKey =
                    existing.getEffect()
                            .unwrapKey()
                            .orElse(null);

            if (!key.equals(existingKey)) {
                continue;
            }

            effects.set(
                    index,
                    new MobEffectInstance(
                            effect.getEffect(),
                            Math.max(
                                    existing.getDuration(),
                                    scaledEffect.getDuration()
                            ),
                            Math.max(
                                    existing.getAmplifier(),
                                    scaledEffect.getAmplifier()
                            ),
                            existing.isAmbient() && scaledEffect.isAmbient(),
                            existing.isVisible() || scaledEffect.isVisible(),
                            existing.showIcon() || scaledEffect.showIcon()
                    )
            );

            return;
        }

        effects.add(
                scaledEffect
        );
    }

    private void setLastIngredient(
            ItemStack ingredient
    ) {
        lastIngredient = ingredient.isEmpty()
                ? ItemStack.EMPTY
                : ingredient.copyWithCount(1);
    }

    // =====================================================================
    // Brewing lifecycle
    // =====================================================================

    private void startBrew(
            Level level,
            BlockPos pos,
            int recipeBlendTicks,
            int recipeBubbleTicks
    ) {
        currentColor =
                FermentingCauldronColorHelper
                        .resolveBaseWaterColor(
                                level,
                                pos,
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

        targetColor =
                FermentingCauldronColorHelper
                        .computeMixedIngredientColor(
                                ingredients.values(),
                                currentColor
                        );

        brewStartTime =
                level.getGameTime();
    }

    public void restartImmediately(
            long currentGameTime
    ) {
        brewStartTime =
                currentGameTime;

        blendTotalTicks = 1;

        bubbleDelay = 0;
    }

    public boolean isComplete(
            Level level
    ) {
        return isBrewing()
                && FermentingCauldronColorHelper
                .isComplete(
                        level,
                        brewStartTime,
                        blendTotalTicks
                );
    }

    public boolean completeBlend() {
        currentColor = targetColor;

        brewStartTime = -1L;
        blendTotalTicks = 1;

        bubbleTicks = 0;
        bubbleDelay = 0;

        startColor = currentColor;

        return finalize;
    }

    public FluidStack createUnfinishedBrewFluid() {
        FluidStack fluid =
                new FluidStack(
                        switch (outputFluid) {
                            case DWARVEN_BREW ->
                                    JolCraftFluids
                                            .UNFINISHED_DWARVEN_BREW
                                            .get();
                            case YEAST ->
                                    JolCraftFluids
                                            .UNFINISHED_YEAST
                                            .get();
                        },
                        FluidType.BUCKET_VOLUME
                );

        applyFluidComponents(
                fluid
        );

        return fluid;
    }

    public FluidStack createUpdatedUnfinishedBrewFluid(
            FluidStack existing
    ) {
        if (existing.isEmpty()
                || !isMatchingUnfinishedFluid(
                existing
        )) {
            return FluidStack.EMPTY;
        }

        FluidStack updated =
                existing.copy();

        updated.setAmount(
                FluidType.BUCKET_VOLUME
        );

        applyFluidComponents(
                updated
        );

        return updated;
    }

    public FluidStack createFinishedBrewFluid() {
        FluidStack fluid =
                new FluidStack(
                        switch (outputFluid) {
                            case DWARVEN_BREW ->
                                    JolCraftFluids
                                            .DWARVEN_BREW
                                            .get();
                            case YEAST ->
                                    JolCraftFluids
                                            .YEAST
                                            .get();
                        },
                        FluidType.BUCKET_VOLUME
                );

        applyFluidComponents(
                fluid
        );

        if (outputFluid
                == FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW) {
            fluid.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    0L
            );
        }

        return fluid;
    }

    private boolean isMatchingUnfinishedFluid(
            FluidStack fluid
    ) {
        return switch (outputFluid) {
            case DWARVEN_BREW ->
                    fluid.is(
                            JolCraftFluids
                                    .UNFINISHED_DWARVEN_BREW
                                    .get()
                    );
            case YEAST ->
                    fluid.is(
                            JolCraftFluids
                                    .UNFINISHED_YEAST
                                    .get()
                    );
        };
    }

    private void applyFluidComponents(
            FluidStack fluid
    ) {
        fluid.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                currentColor
        );

        fluid.set(
                DataComponents.POTION_CONTENTS,
                createPotionContents()
        );

        fluid.remove(
                JolCraftDataComponents.BREW_AGE.get()
        );
    }

    private PotionContents createPotionContents() {
        if (effects.isEmpty()) {
            return PotionContents.EMPTY;
        }

        List<MobEffectInstance> customEffects =
                new ArrayList<>(
                        effects.size()
                );

        for (MobEffectInstance effect : effects) {
            customEffects.add(
                    new MobEffectInstance(
                            effect
                    )
            );
        }

        return new PotionContents(
                Optional.empty(),
                Optional.empty(),
                Collections.unmodifiableList(
                        customEffects
                )
        );
    }

    public void clear() {
        lastIngredient =
                ItemStack.EMPTY;

        ingredients.clear();
        effects.clear();

        finalize = false;

        outputFluid =
                FermentingCauldronRecipe.DEFAULT_OUTPUT_FLUID;

        bubbleTicks = 0;
        bubbleDelay = 0;

        currentColor =
                FermentingCauldronColorHelper.UNSET_COLOR;

        startColor =
                FermentingCauldronColorHelper.UNSET_COLOR;

        targetColor =
                FermentingCauldronColorHelper.UNSET_COLOR;

        brewStartTime = -1L;
        blendTotalTicks = 1;
    }

    // =====================================================================
    // Bubble timing
    // =====================================================================

    /**
     * Advances the bubble delay.
     *
     * @return whether the caller should spawn one bubble effect now
     */
    public boolean shouldBubble() {
        if (!isBrewing()
                || bubbleTicks <= 0) {
            return false;
        }

        if (bubbleDelay > 0) {
            bubbleDelay--;
            return false;
        }

        return true;
    }

    public void scheduleNextBubble(
            int randomDelay
    ) {
        bubbleDelay =
                3 + Math.max(
                        0,
                        randomDelay
                );
    }

    // =====================================================================
    // Sleep fast-forward
    // =====================================================================

    /**
     * Fast-forwards the active blend.
     *
     * @return true when the brew has completed and should now be finalized
     */
    public boolean fastForward(
            Level level,
            long skippedTicks
    ) {
        if (level == null
                || skippedTicks <= 0L
                || !isBrewing()) {
            return false;
        }

        long currentGameTime =
                level.getGameTime();

        int totalTicks = Math.max(
                1,
                blendTotalTicks
        );

        long elapsedTicks = Math.min(
                totalTicks,
                Math.max(
                        0L,
                        currentGameTime
                                - brewStartTime
                )
        );

        long remainingTicks =
                totalTicks
                        - elapsedTicks;

        if (skippedTicks >= remainingTicks) {
            return true;
        }

        float progress =
                (float) (elapsedTicks + skippedTicks)
                        / (float) totalTicks;

        startColor =
                FermentingCauldronColorHelper
                        .lerpArgb(
                                startColor,
                                targetColor,
                                progress
                        );

        blendTotalTicks =
                (int) (remainingTicks - skippedTicks);

        brewStartTime =
                currentGameTime;

        bubbleDelay = 0;

        return false;
    }

    // =====================================================================
    // Client data
    // =====================================================================

    public void writeClientData(
            CompoundTag tag
    ) {
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

        tag.putString(
                NBT_OUTPUT_FLUID,
                outputFluid.getId()
        );

        saveEffects(
                tag
        );
    }

    public void readClientData(
            CompoundTag tag,
            HolderLookup.Provider registries,
            BlockPos pos
    ) {
        clear();

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

        brewStartTime = tag.contains(
                NBT_BREW_START_TIME,
                Tag.TAG_LONG
        )
                ? tag.getLong(
                NBT_BREW_START_TIME
        )
                : -1L;

        blendTotalTicks = tag.contains(
                NBT_BLEND_TOTAL_TICKS,
                Tag.TAG_INT
        )
                ? Math.max(
                1,
                tag.getInt(
                        NBT_BLEND_TOTAL_TICKS
                )
        )
                : 1;

        outputFluid = loadOutputFluid(
                tag,
                pos
        );

        loadEffects(
                tag,
                registries.lookupOrThrow(
                        Registries.MOB_EFFECT
                ),
                pos
        );

        sanitizeLoadedBrewStartTime();
    }

    // =====================================================================
    // Persistent storage
    // =====================================================================

    public void save(
            CompoundTag tag
    ) {
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

        saveLastIngredient(
                tag
        );

        saveIngredients(
                tag
        );

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

        tag.putString(
                NBT_OUTPUT_FLUID,
                outputFluid.getId()
        );

        saveEffects(
                tag
        );
    }

    public void load(
            CompoundTag tag,
            HolderLookup.Provider registries,
            BlockPos pos
    ) {
        clear();

        HolderLookup.RegistryLookup<Item> itemLookup =
                registries.lookupOrThrow(
                        Registries.ITEM
                );

        HolderLookup.RegistryLookup<MobEffect> effectLookup =
                registries.lookupOrThrow(
                        Registries.MOB_EFFECT
                );

        brewStartTime = tag.contains(
                NBT_BREW_START_TIME,
                Tag.TAG_LONG
        )
                ? tag.getLong(
                NBT_BREW_START_TIME
        )
                : -1L;

        blendTotalTicks = tag.contains(
                NBT_BLEND_TOTAL_TICKS,
                Tag.TAG_INT
        )
                ? Math.max(
                1,
                tag.getInt(
                        NBT_BLEND_TOTAL_TICKS
                )
        )
                : 1;

        bubbleTicks = tag.contains(
                NBT_BUBBLE_TICKS,
                Tag.TAG_INT
        )
                ? Math.max(
                0,
                tag.getInt(
                        NBT_BUBBLE_TICKS
                )
        )
                : 0;

        bubbleDelay = tag.contains(
                NBT_BUBBLE_DELAY,
                Tag.TAG_INT
        )
                ? Math.max(
                0,
                tag.getInt(
                        NBT_BUBBLE_DELAY
                )
        )
                : 0;

        loadLastIngredient(
                tag,
                itemLookup,
                pos
        );

        loadIngredients(
                tag,
                itemLookup,
                pos
        );

        finalize =
                tag.getBoolean(
                        NBT_FINALIZE
                );

        outputFluid =
                loadOutputFluid(
                        tag,
                        pos
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
                effectLookup,
                pos
        );

        sanitizeLoadedBrewStartTime();
    }

    private void sanitizeLoadedBrewStartTime() {
        if (brewStartTime != 0L) {
            return;
        }

        boolean hasProcessState =
                !ingredients.isEmpty()
                        || !effects.isEmpty()
                        || !lastIngredient.isEmpty()
                        || currentColor
                        != FermentingCauldronColorHelper.UNSET_COLOR
                        || startColor
                        != FermentingCauldronColorHelper.UNSET_COLOR
                        || targetColor
                        != FermentingCauldronColorHelper.UNSET_COLOR;

        if (!hasProcessState) {
            brewStartTime = -1L;
        }
    }

    @SuppressWarnings("deprecation")
    private void saveLastIngredient(
            CompoundTag tag
    ) {
        if (lastIngredient.isEmpty()) {
            return;
        }

        ResourceLocation id =
                lastIngredient
                        .getItem()
                        .builtInRegistryHolder()
                        .key()
                        .location();

        tag.putString(
                NBT_LAST_INGREDIENT_ID,
                id.toString()
        );
    }

    @SuppressWarnings("deprecation")
    private void saveIngredients(
            CompoundTag tag
    ) {
        if (ingredients.isEmpty()) {
            return;
        }

        ListTag list =
                new ListTag();

        for (
                Map.Entry<Item, IngredientData> entry :
                ingredients.entrySet()
        ) {
            Item item =
                    entry.getKey();

            IngredientData data =
                    entry.getValue();

            if (item == Items.AIR
                    || data == null
                    || data.count() <= 0) {
                continue;
            }

            ResourceLocation id =
                    item.builtInRegistryHolder()
                            .key()
                            .location();

            CompoundTag ingredientTag =
                    new CompoundTag();

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

        ListTag list =
                new ListTag();

        for (MobEffectInstance effect : effects) {
            ResourceKey<MobEffect> key =
                    effect.getEffect()
                            .unwrapKey()
                            .orElse(null);

            if (key == null) {
                continue;
            }

            CompoundTag effectTag =
                    new CompoundTag();

            effectTag.putString(
                    NBT_EFFECT_ID,
                    key.location()
                            .toString()
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

    private FermentingCauldronRecipe.OutputFluid loadOutputFluid(
            CompoundTag tag,
            BlockPos pos
    ) {
        if (!tag.contains(
                NBT_OUTPUT_FLUID,
                Tag.TAG_STRING
        )) {
            return FermentingCauldronRecipe.DEFAULT_OUTPUT_FLUID;
        }

        String id =
                tag.getString(
                        NBT_OUTPUT_FLUID
                );

        for (
                FermentingCauldronRecipe.OutputFluid value
                : FermentingCauldronRecipe.OutputFluid.values()
        ) {
            if (value.getId()
                    .equals(
                            id
                    )) {
                return value;
            }
        }

        JolCraftLogs.warn(
                JolCraftLogTags.BLOCK_ENTITY,
                "FermentingCauldron at {} has unknown output fluid '{}' (using dwarven brew)",
                JolCraftLogs.roundedPos(pos),
                id
        );

        return FermentingCauldronRecipe.DEFAULT_OUTPUT_FLUID;
    }

    private void loadLastIngredient(
            CompoundTag tag,
            HolderLookup.RegistryLookup<Item> itemLookup,
            BlockPos pos
    ) {
        if (!tag.contains(
                NBT_LAST_INGREDIENT_ID,
                Tag.TAG_STRING
        )) {
            return;
        }

        String raw =
                tag.getString(
                        NBT_LAST_INGREDIENT_ID
                );

        ResourceLocation id =
                ResourceLocation.tryParse(
                        raw
                );

        if (id == null) {
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK_ENTITY,
                    "FermentingCauldron at {} has malformed lastIngredient name '{}' (clearing)",
                    JolCraftLogs.roundedPos(pos),
                    raw
            );

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
                    JolCraftLogs.roundedPos(pos),
                    id
            );

            return;
        }

        lastIngredient =
                new ItemStack(
                        item
                );
    }

    private void loadIngredients(
            CompoundTag tag,
            HolderLookup.RegistryLookup<Item> itemLookup,
            BlockPos pos
    ) {
        if (!tag.contains(
                NBT_INGREDIENTS,
                Tag.TAG_LIST
        )) {
            return;
        }

        ListTag list =
                tag.getList(
                        NBT_INGREDIENTS,
                        Tag.TAG_COMPOUND
                );

        for (
                int index = 0;
                index < list.size();
                index++
        ) {
            CompoundTag ingredientTag =
                    list.getCompound(
                            index
                    );

            if (!ingredientTag.contains(
                    NBT_ITEM,
                    Tag.TAG_STRING
            )) {
                continue;
            }

            String raw =
                    ingredientTag.getString(
                            NBT_ITEM
                    );

            ResourceLocation id =
                    ResourceLocation.tryParse(
                            raw
                    );

            if (id == null) {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} has malformed ingredient name '{}' (skipping)",
                        JolCraftLogs.roundedPos(pos),
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
                        JolCraftLogs.roundedPos(pos),
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
            HolderLookup.RegistryLookup<MobEffect> effectLookup,
            BlockPos pos
    ) {
        if (!tag.contains(
                NBT_EFFECTS,
                Tag.TAG_LIST
        )) {
            return;
        }

        ListTag list =
                tag.getList(
                        NBT_EFFECTS,
                        Tag.TAG_COMPOUND
                );

        for (
                int index = 0;
                index < list.size();
                index++
        ) {
            CompoundTag effectTag =
                    list.getCompound(
                            index
                    );

            if (!effectTag.contains(
                    NBT_EFFECT_ID,
                    Tag.TAG_STRING
            )) {
                continue;
            }

            String raw =
                    effectTag.getString(
                            NBT_EFFECT_ID
                    );

            ResourceLocation id =
                    ResourceLocation.tryParse(
                            raw
                    );

            if (id == null) {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} has malformed effect name '{}' (skipping)",
                        JolCraftLogs.roundedPos(pos),
                        raw
                );

                continue;
            }

            ResourceKey<MobEffect> key =
                    ResourceKey.create(
                            Registries.MOB_EFFECT,
                            id
                    );

            Holder<MobEffect> holder =
                    effectLookup
                            .get(key)
                            .orElse(null);

            if (holder == null) {
                JolCraftLogs.debug(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} missing MobEffect '{}' (skipping)",
                        JolCraftLogs.roundedPos(pos),
                        id
                );

                continue;
            }

            int duration =
                    effectTag.getInt(
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
}