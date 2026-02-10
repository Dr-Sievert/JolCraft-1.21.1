package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.FermentingCauldronRecipe;
import net.sievert.jolcraft.data.recipe.custom.input.FermentingCauldronRecipeInput;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.util.FermentingCauldronColorHelper;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FermentingCauldronBlockEntity extends BlockEntity {

    // ===== NBT keys =====
    private static final String NBT_BREW_START_TIME = "brewStartTime";
    private static final String NBT_BLEND_TOTAL_TICKS = "blendTotalTicks";

    private static final String NBT_BUBBLE_TICKS = "bubbleTicks";
    private static final String NBT_BUBBLE_DELAY = "bubbleDelay";

    private static final String NBT_LAST_INGREDIENT_ID = "lastIngredientId";

    private static final String NBT_INGREDIENTS = "ingredients";
    private static final String NBT_ITEM = "item";
    private static final String NBT_COUNT = "count";
    private static final String NBT_COLOR = "color";

    private static final String NBT_CURRENT_COLOR = "currentColor";
    private static final String NBT_START_COLOR = "startColor";
    private static final String NBT_TARGET_COLOR = "targetColor";

    private static final String NBT_FINALIZE = "finalize";
    private static final String NBT_EXTRACTABLE = "extractable";

    private static final String NBT_EFFECTS = "effects";
    private static final String NBT_EFFECT_ID = "id";
    private static final String NBT_EFFECT_DURATION = "duration";
    private static final String NBT_EFFECT_AMPLIFIER = "amplifier";

    // ===== gameplay state =====
    private ItemStack lastIngredient = ItemStack.EMPTY;

    private final Map<Item, IngredientData> ingredients = new HashMap<>();
    private record IngredientData(int count, int color) implements FermentingCauldronColorHelper.IngredientView {}

    private final List<FermentingCauldronRecipe.EffectData> effects = new ArrayList<>();
    private boolean finalize;
    private boolean extractable;

    // ===== server-only visuals =====
    private int bubbleTicks = 0;
    private int bubbleDelay = 0;

    // ===== synced render state (FIELDS ONLY) =====
    private int currentColor = FermentingCauldronColorHelper.UNSET_COLOR;
    private int startColor = FermentingCauldronColorHelper.UNSET_COLOR;
    private int targetColor = FermentingCauldronColorHelper.UNSET_COLOR;
    private long brewStartTime = 0L;     // 0 = not brewing
    private int blendTotalTicks = 1;     // duration for current blend

    public FermentingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.FERMENTING_CAULDRON.get(), pos, state);
    }

    // =====================================================================
    // Interaction
    // =====================================================================

    public InteractionResult handleInteraction(Player player, InteractionHand hand, ItemStack usedItem) {
        if (level == null || level.isClientSide) return InteractionResult.FAIL;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.FAIL;
        if (isBrewing()) return InteractionResult.FAIL;
        if (usedItem.isEmpty()) return InteractionResult.FAIL;

        FermentingCauldronRecipe recipe = findRecipe(usedItem);
        if (recipe == null) return InteractionResult.FAIL;

        if (extractable) {
            if (!recipe.isExtraction()) return InteractionResult.FAIL;

            ItemStack extract = recipe.extract();
            if (extract == null || extract.isEmpty()) return InteractionResult.FAIL;

            extractBrew(player, usedItem, extract);
            return InteractionResult.SUCCESS;
        }

        Item itemKey = usedItem.getItem();
        ItemStack ingredientKey = usedItem.copyWithCount(1);

        IngredientData existing = ingredients.get(itemKey);
        int count = (existing == null) ? 0 : existing.count();

        if (count >= 3) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX)
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (recipe.effect() != null && !ingredients.isEmpty() && !ingredients.containsKey(itemKey)) {
            if (!DwarfLoreUnlockHelper.hasUnlock(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)) {
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI)
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return InteractionResult.SUCCESS;
            }
        }

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

        int newCount = count + 1;
        ingredients.put(itemKey, new IngredientData(newCount, recipe.color()));

        this.finalize = recipe.finalizeBrew();

        if (recipe.effect() != null) {
            FermentingCauldronRecipe.EffectData base = recipe.effect();
            if (base == null) return InteractionResult.FAIL;
            int amp = Math.min(2, newCount - 1);
            FermentingCauldronRecipe.EffectData stacked =
                    new FermentingCauldronRecipe.EffectData(base.id(), base.duration(), amp);
            upsertEffect(stacked);
        }

        setLastIngredient(ingredientKey);
        startBrew(recipe.brewTicks(), recipe.bubbleTicks());

        return InteractionResult.SUCCESS;
    }

    private void upsertEffect(FermentingCauldronRecipe.EffectData effect) {
        for (int i = 0; i < effects.size(); i++) {
            FermentingCauldronRecipe.EffectData existing = effects.get(i);
            if (existing.id().equals(effect.id())) {
                effects.set(i, effect);
                return;
            }
        }
        effects.add(effect);
    }

    private void setLastIngredient(ItemStack stack) {
        this.lastIngredient = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
    }

    // =====================================================================
    // Brewing
    // =====================================================================

    public boolean isBrewing() {
        return brewStartTime > 0L;
    }

    private void startBrew(int recipeBlendTicks, int recipeBubbleTicks) {
        if (level == null || level.isClientSide) return;

        currentColor = FermentingCauldronColorHelper.resolveBaseWaterColor(level, worldPosition, currentColor);

        blendTotalTicks = Math.max(1, recipeBlendTicks);
        bubbleTicks = Math.max(0, recipeBubbleTicks);
        bubbleDelay = 0;

        startColor = currentColor;
        targetColor = FermentingCauldronColorHelper.computeMixedIngredientColor(ingredients.values(), currentColor);

        brewStartTime = level.getGameTime();
        sync();
    }

    private void finalizeBrew() {
        currentColor = targetColor;

        brewStartTime = 0L;
        blendTotalTicks = 1;

        bubbleTicks = 0;
        bubbleDelay = 0;

        startColor = currentColor;

        if (finalize) extractable = true;

        sync();
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (!isBrewing()) return;

        doBubbleEffects();

        if (FermentingCauldronColorHelper.isComplete(level, brewStartTime, blendTotalTicks)) {
            finalizeBrew();
        }
    }

    public void fastForwardBrew(long skippedTicks) {
        if (level == null || level.isClientSide) return;
        if (skippedTicks <= 0L) return;
        if (!isBrewing()) return;

        long newStart = FermentingCauldronColorHelper.fastForwardStartTime(level, brewStartTime, blendTotalTicks, skippedTicks);
        if (newStart <= 0L) {
            finalizeBrew();
            return;
        }

        brewStartTime = newStart;
        bubbleDelay = 0;
        sync();
    }

    // =====================================================================
    // Extraction
    // =====================================================================

    private void extractBrew(Player player, ItemStack usedItem, ItemStack result) {
        ItemStack out = createBrewStack(result.copy());

        if (!player.isCreative()) {
            usedItem.shrink(1);
            lowerFillLevel();
        }

        if (level != null) {
            PlaySound.bottleFill(player, 0.8F, 0.9F);
        }

        if (!player.getInventory().add(out)) {
            player.drop(out, false);
        }
    }

    private ItemStack createBrewStack(ItemStack out) {
        if (level == null) return out;

        out.set(JolCraftDataComponents.BREW_COLOR.get(), this.currentColor);

        if (effects.isEmpty() || !(out.getItem() instanceof PotionItem)) {
            return out;
        }

        var effectsLookup = level.registryAccess().lookupOrThrow(Registries.MOB_EFFECT);

        List<MobEffectInstance> customEffects = new ArrayList<>(effects.size());
        for (FermentingCauldronRecipe.EffectData e : effects) {
            effectsLookup.get(e.id()).ifPresent(holder ->
                    customEffects.add(new MobEffectInstance(holder, e.duration(), e.amplifier()))
            );
        }

        if (!customEffects.isEmpty()) {
            out.set(
                    DataComponents.POTION_CONTENTS,
                    new PotionContents(
                            Optional.empty(),
                            Optional.empty(),
                            List.copyOf(customEffects),
                            Optional.empty()
                    )
            );
        }

        return out;
    }

    // =====================================================================
    // Recipes
    // =====================================================================

    @Nullable
    private FermentingCauldronRecipe findRecipe(ItemStack usedItem) {
        if (level == null || level.getServer() == null) return null;

        FermentingCauldronRecipeInput input = new FermentingCauldronRecipeInput(
                usedItem.copyWithCount(1),
                lastIngredient.isEmpty() ? ItemStack.EMPTY : lastIngredient.copyWithCount(1)
        );

        return level.getServer()
                .getRecipeManager()
                .getRecipeFor(JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .orElse(null);
    }

    // =====================================================================
    // Level hooks / sync (standard BE packets only)
    // =====================================================================

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (!level.isClientSide) {
            if (brewStartTime > 0L && FermentingCauldronColorHelper.isComplete(level, brewStartTime, blendTotalTicks)) {
                finalizeBrew();
            }
        }
    }

    @Override
    public @NotNull ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_CURRENT_COLOR, currentColor);
        tag.putInt(NBT_START_COLOR, startColor);
        tag.putInt(NBT_TARGET_COLOR, targetColor);
        tag.putLong(NBT_BREW_START_TIME, brewStartTime);
        tag.putInt(NBT_BLEND_TOTAL_TICKS, blendTotalTicks);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(NBT_CURRENT_COLOR, Tag.TAG_INT)) currentColor = tag.getInt(NBT_CURRENT_COLOR);
        if (tag.contains(NBT_START_COLOR, Tag.TAG_INT)) startColor = tag.getInt(NBT_START_COLOR);
        if (tag.contains(NBT_TARGET_COLOR, Tag.TAG_INT)) targetColor = tag.getInt(NBT_TARGET_COLOR);
        if (tag.contains(NBT_BREW_START_TIME, Tag.TAG_LONG)) brewStartTime = tag.getLong(NBT_BREW_START_TIME);
        if (tag.contains(NBT_BLEND_TOTAL_TICKS, Tag.TAG_INT)) blendTotalTicks = Math.max(1, tag.getInt(NBT_BLEND_TOTAL_TICKS));
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    // =====================================================================
    // Persistence
    // =====================================================================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putLong(NBT_BREW_START_TIME, brewStartTime);
        tag.putInt(NBT_BLEND_TOTAL_TICKS, blendTotalTicks);

        tag.putInt(NBT_BUBBLE_TICKS, bubbleTicks);
        tag.putInt(NBT_BUBBLE_DELAY, bubbleDelay);

        if (!lastIngredient.isEmpty()) {
            ResourceLocation id = lastIngredient.getItem().builtInRegistryHolder().key().location();
            tag.putString(NBT_LAST_INGREDIENT_ID, id.toString());
        }

        if (!ingredients.isEmpty()) {
            ListTag list = new ListTag();
            for (var e : ingredients.entrySet()) {
                Item item = e.getKey();
                IngredientData data = e.getValue();
                if (item == Items.AIR || data == null) continue;

                int count = data.count();
                if (count <= 0) continue;

                ResourceLocation id = item.builtInRegistryHolder().key().location();

                CompoundTag one = new CompoundTag();
                one.putString(NBT_ITEM, id.toString());
                if (count != 1) one.putInt(NBT_COUNT, count);
                one.putInt(NBT_COLOR, data.color());

                list.add(one);
            }
            if (!list.isEmpty()) tag.put(NBT_INGREDIENTS, list);
        }

        tag.putInt(NBT_CURRENT_COLOR, currentColor);
        tag.putInt(NBT_START_COLOR, startColor);
        tag.putInt(NBT_TARGET_COLOR, targetColor);

        tag.putBoolean(NBT_FINALIZE, finalize);
        if (extractable) tag.putBoolean(NBT_EXTRACTABLE, true);

        if (!effects.isEmpty()) {
            ListTag list = new ListTag();
            for (FermentingCauldronRecipe.EffectData eff : effects) {
                CompoundTag one = new CompoundTag();
                one.putString(NBT_EFFECT_ID, eff.id().location().toString());
                one.putInt(NBT_EFFECT_DURATION, eff.duration());
                if (eff.amplifier() != 0) one.putInt(NBT_EFFECT_AMPLIFIER, eff.amplifier());
                list.add(one);
            }
            tag.put(NBT_EFFECTS, list);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        var itemLookup = registries.lookupOrThrow(Registries.ITEM);

        brewStartTime = tag.getLong(NBT_BREW_START_TIME);
        blendTotalTicks = Math.max(1, tag.getInt(NBT_BLEND_TOTAL_TICKS));

        bubbleTicks = Math.max(0, tag.getInt(NBT_BUBBLE_TICKS));
        bubbleDelay = Math.max(0, tag.getInt(NBT_BUBBLE_DELAY));

        // ---------------------------------------------------------------------
        // lastIngredient
        // ---------------------------------------------------------------------

        if (tag.contains(NBT_LAST_INGREDIENT_ID, Tag.TAG_STRING)) {
            String raw = tag.getString(NBT_LAST_INGREDIENT_ID);
            ResourceLocation id = ResourceLocation.tryParse(raw);

            if (id == null) {
                JolCraftLogs.warn(JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} has malformed lastIngredient getId '{}' (clearing)",
                        JolCraftLogs.roundedPos(this), raw);
                lastIngredient = ItemStack.EMPTY;
            } else {
                Item item = itemLookup
                        .get(ResourceKey.create(Registries.ITEM, id))
                        .map(Holder.Reference::value)
                        .orElse(Items.AIR);

                if (item == Items.AIR) {
                    JolCraftLogs.debug(JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} missing lastIngredient item '{}' (clearing)",
                            JolCraftLogs.roundedPos(this), id);
                    lastIngredient = ItemStack.EMPTY;
                } else {
                    lastIngredient = new ItemStack(item);
                }
            }
        } else {
            lastIngredient = ItemStack.EMPTY;
        }

        // ---------------------------------------------------------------------
        // ingredients
        // ---------------------------------------------------------------------

        ingredients.clear();
        if (tag.contains(NBT_INGREDIENTS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_INGREDIENTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag one = list.getCompound(i);

                if (!one.contains(NBT_ITEM, Tag.TAG_STRING)) continue;
                String raw = one.getString(NBT_ITEM);

                ResourceLocation id = ResourceLocation.tryParse(raw);
                if (id == null) {
                    JolCraftLogs.warn(JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} has malformed ingredient getId '{}' (skipping)",
                            JolCraftLogs.roundedPos(this), raw);
                    continue;
                }

                Item item = itemLookup
                        .get(ResourceKey.create(Registries.ITEM, id))
                        .map(Holder.Reference::value)
                        .orElse(Items.AIR);

                if (item == Items.AIR) {
                    JolCraftLogs.debug(JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} missing ingredient item '{}' (skipping)",
                            JolCraftLogs.roundedPos(this), id);
                    continue;
                }

                int count = one.contains(NBT_COUNT, Tag.TAG_INT) ? one.getInt(NBT_COUNT) : 1;
                if (count <= 0) continue;
                count = Math.min(3, count);

                int color = one.contains(NBT_COLOR, Tag.TAG_INT) ? one.getInt(NBT_COLOR) : 0xFFFFFFFF;
                ingredients.put(item, new IngredientData(count, color));
            }
        }

        // ---------------------------------------------------------------------
        // flags/colors
        // ---------------------------------------------------------------------

        finalize = tag.getBoolean(NBT_FINALIZE);
        extractable = tag.getBoolean(NBT_EXTRACTABLE);

        currentColor = tag.contains(NBT_CURRENT_COLOR, Tag.TAG_INT)
                ? tag.getInt(NBT_CURRENT_COLOR)
                : FermentingCauldronColorHelper.UNSET_COLOR;

        startColor = tag.contains(NBT_START_COLOR, Tag.TAG_INT) ? tag.getInt(NBT_START_COLOR) : currentColor;
        targetColor = tag.contains(NBT_TARGET_COLOR, Tag.TAG_INT) ? tag.getInt(NBT_TARGET_COLOR) : currentColor;

        // ---------------------------------------------------------------------
        // effects
        // ---------------------------------------------------------------------

        effects.clear();
        if (tag.contains(NBT_EFFECTS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_EFFECTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag one = list.getCompound(i);

                if (!one.contains(NBT_EFFECT_ID, Tag.TAG_STRING)) continue;
                String raw = one.getString(NBT_EFFECT_ID);

                ResourceLocation idLoc = ResourceLocation.tryParse(raw);
                if (idLoc == null) {
                    JolCraftLogs.warn(JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} has malformed effect getId '{}' (skipping)",
                            JolCraftLogs.roundedPos(worldPosition), raw);
                    continue;
                }

                ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, idLoc);

                int duration = one.getInt(NBT_EFFECT_DURATION);
                int amplifier = one.contains(NBT_EFFECT_AMPLIFIER, Tag.TAG_INT) ? one.getInt(NBT_EFFECT_AMPLIFIER) : 0;

                effects.add(new FermentingCauldronRecipe.EffectData(key, duration, amplifier));
            }
        }
    }

    // =====================================================================
    // Server visuals / misc
    // =====================================================================

    private void doBubbleEffects() {
        if (level == null || level.isClientSide) return;
        if (!isBrewing()) return;

        if (bubbleTicks <= 0) return;
        if (bubbleDelay > 0) { bubbleDelay--; return; }

        double x = worldPosition.getX() + 0.5D + (level.random.nextDouble() - 0.5D);
        double y = worldPosition.getY() + 1.01D;
        double z = worldPosition.getZ() + 0.5D + (level.random.nextDouble() - 0.5D);

        JolCraftParticleHelper.spawn(level, ParticleTypes.BUBBLE_POP, x, y, z, 0.0D, 0.05D, 0.0D);
        JolCraftSoundHelper.block(
                level,
                BlockPos.containing(x, y, z),
                SoundEvents.BUBBLE_POP,
                0.3F,
                1.4F
        );

        bubbleDelay = 3 + level.random.nextInt(bubbleTicks);
    }

    private void lowerFillLevel() {
        if (level == null || level.isClientSide) return;

        BlockState state = level.getBlockState(worldPosition);
        if (!state.hasProperty(LayeredCauldronBlock.LEVEL)) return;

        int levelValue = state.getValue(LayeredCauldronBlock.LEVEL);

        if (levelValue <= 1) {
            level.setBlockAndUpdate(worldPosition, Blocks.CAULDRON.defaultBlockState());
        } else {
            level.setBlockAndUpdate(worldPosition, state.setValue(LayeredCauldronBlock.LEVEL, levelValue - 1));
        }
    }

    // =====================================================================
    // Getters
    // =====================================================================

    public int getCurrentColor() { return currentColor; }
    public int getStartColor() { return startColor; }
    public int getTargetColor() { return targetColor; }
    public long getBrewStartTime() { return brewStartTime; }
    public int getBlendTotalTicks() { return blendTotalTicks; }
}