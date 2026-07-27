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

    private int bubbleTicks;
    private int bubbleDelay;

    private int currentColor =
            FermentingCauldronColorHelper.UNSET_COLOR;

    private int startColor =
            FermentingCauldronColorHelper.UNSET_COLOR;

    private int targetColor =
            FermentingCauldronColorHelper.UNSET_COLOR;

    private long brewStartTime;

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
        return brewStartTime > 0L;
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

    public boolean shouldFinalize() {
        return finalize;
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

    public int applyIngredient(
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

        return newCount;
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

        MobEffectInstance scaledEffect =
                new MobEffectInstance(
                        effect.getEffect(),
                        effect.getDuration()
                                * ingredientCount,
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
                    scaledEffect
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

        brewStartTime = 0L;
        blendTotalTicks = 1;

        bubbleTicks = 0;
        bubbleDelay = 0;

        startColor = currentColor;

        return finalize;
    }

    public FluidStack createUnfinishedBrewFluid() {
        FluidStack brew =
                new FluidStack(
                        JolCraftFluids
                                .UNFINISHED_DWARVEN_BREW
                                .get(),
                        FluidType.BUCKET_VOLUME
                );

        applyBrewComponents(
                brew
        );

        return brew;
    }

    public FluidStack createUpdatedUnfinishedBrewFluid(
            FluidStack existing
    ) {
        if (existing.isEmpty()
                || !existing.is(
                JolCraftFluids
                        .UNFINISHED_DWARVEN_BREW
                        .get()
        )) {
            return FluidStack.EMPTY;
        }

        FluidStack updated =
                existing.copy();

        updated.setAmount(
                FluidType.BUCKET_VOLUME
        );

        applyBrewComponents(
                updated
        );

        return updated;
    }

    public FluidStack createFinishedBrewFluid() {
        FluidStack brew =
                new FluidStack(
                        JolCraftFluids
                                .DWARVEN_BREW
                                .get(),
                        FluidType.BUCKET_VOLUME
                );

        applyBrewComponents(
                brew
        );

        return brew;
    }

    private void applyBrewComponents(
            FluidStack brew
    ) {
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
            if (effect.getDuration() < 1
                    || effect.getAmplifier() < 0) {
                continue;
            }

            customEffects.add(
                    new MobEffectInstance(
                            effect
                    )
            );
        }

        if (customEffects.isEmpty()) {
            return PotionContents.EMPTY;
        }

        return new PotionContents(
                Optional.empty(),
                Optional.empty(),
                List.copyOf(
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

        bubbleTicks = 0;
        bubbleDelay = 0;

        currentColor =
                FermentingCauldronColorHelper.UNSET_COLOR;

        startColor =
                FermentingCauldronColorHelper.UNSET_COLOR;

        targetColor =
                FermentingCauldronColorHelper.UNSET_COLOR;

        brewStartTime = 0L;
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
        if (skippedTicks <= 0L
                || !isBrewing()) {
            return false;
        }

        long newStart =
                FermentingCauldronColorHelper
                        .fastForwardStartTime(
                                level,
                                brewStartTime,
                                blendTotalTicks,
                                skippedTicks
                        );

        if (newStart <= 0L) {
            return true;
        }

        brewStartTime =
                newStart;

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
    }

    public void readClientData(
            CompoundTag tag
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

        brewStartTime =
                tag.getLong(
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

        brewStartTime =
                tag.getLong(
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