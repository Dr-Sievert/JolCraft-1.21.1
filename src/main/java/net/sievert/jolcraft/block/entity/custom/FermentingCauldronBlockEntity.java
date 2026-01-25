package net.sievert.jolcraft.block.entity.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.custom.attachment.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.recipe.JolCraftRecipes;
import net.sievert.jolcraft.recipe.custom.FermentingCauldronRecipe;
import net.sievert.jolcraft.recipe.custom.input.FermentingCauldronRecipeInput;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FermentingCauldronBlockEntity extends BlockEntity {

    // ===== NBT keys =====
    private static final String NBT_BREW_TICKS = "brewTicks";
    private static final String NBT_BUBBLE_TICKS = "bubbleTicks";
    private static final String NBT_BUBBLE_DELAY = "bubbleDelay";

    private static final String NBT_LAST_INGREDIENT_ID = "lastIngredientId";

    private static final String NBT_INGREDIENTS = "ingredients";
    private static final String NBT_ITEM = "item";
    private static final String NBT_COUNT = "count";
    private static final String NBT_COLOR = "color";

    private static final String NBT_TARGET_COLOR = "targetColor";
    private static final String NBT_CURRENT_COLOR = "currentColor";
    private static final String NBT_START_COLOR = "startColor";
    private static final String NBT_BLEND_TOTAL_TICKS = "blendTotalTicks";

    private static final String NBT_FINALIZE = "finalize";
    private static final String NBT_EXTRACTABLE = "extractable";

    private static final String NBT_EFFECTS = "effects";
    private static final String NBT_EFFECT_ID = "id";
    private static final String NBT_EFFECT_DURATION = "duration";
    private static final String NBT_EFFECT_AMPLIFIER = "amplifier";

    // ===== state =====
    private ItemStack lastIngredient = ItemStack.EMPTY;

    private final Map<Item, IngredientData> ingredients = new HashMap<>();
    private record IngredientData(int count, int color) {}

    private int brewTicks = 0;

    private int bubbleTicks = 0;
    private int bubbleDelay = 0;

    private int currentColor = 0xFFFFFFFF;
    private int startColor = 0xFFFFFFFF;
    private int targetColor = 0xFFFFFFFF;
    private int blendTotalTicks = 1;

    private final List<FermentingCauldronRecipe.EffectData> effects = new ArrayList<>();

    private boolean finalize;
    private boolean extractable;

    public FermentingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.FERMENTING_CAULDRON.get(), pos, state);
    }

    public InteractionResult handleInteraction(Player player, InteractionHand hand, ItemStack usedItem) {
        if (level == null || level.isClientSide || hand != InteractionHand.MAIN_HAND || isBrewing() || usedItem.isEmpty()) {
            return InteractionResult.SUCCESS;
        }

        FermentingCauldronRecipe recipe = findRecipe(usedItem);
        if (recipe == null) {
            return InteractionResult.SUCCESS;
        }

        if (extractable) {
            if (!recipe.isExtraction()) return InteractionResult.SUCCESS;

            ItemStack extract = recipe.extract();
            if (extract == null || extract.isEmpty()) return InteractionResult.SUCCESS;

            extractBrew(player, usedItem, extract);
            return InteractionResult.SUCCESS;
        }

        Item itemKey = usedItem.getItem();
        ItemStack ingredientKey = usedItem.copyWithCount(1);

        IngredientData existing = ingredients.get(itemKey);
        int count = (existing == null) ? 0 : existing.count();

        if (count >= 3) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.fermenting_cauldron.ingredient_max")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (recipe.effect() != null && !ingredients.isEmpty() && !ingredients.containsKey(itemKey)) {
            if (!DwarfLoreUnlockHelper.hasUnlock(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.fermenting_cauldron.locked_multi")
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return InteractionResult.SUCCESS;
            }
        }

        if (!player.isCreative()) {
            usedItem.shrink(1);
        }

        int newCount = count + 1;
        ingredients.put(itemKey, new IngredientData(newCount, recipe.color()));

        this.finalize = recipe.finalizeBrew();

        if (recipe.effect() != null) {
            FermentingCauldronRecipe.EffectData base = recipe.effect();
            if (base == null) return InteractionResult.SUCCESS;

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

    public boolean isBrewing() {
        return brewTicks > 0;
    }

    private void startBrew(int recipeBrewTicks, int recipeBubbleTicks) {
        ensureBaseWaterColor();

        brewTicks = Math.max(1, recipeBrewTicks);
        bubbleTicks = Math.max(0, recipeBubbleTicks);
        bubbleDelay = 0;

        startColor = currentColor;
        targetColor = computeMixedIngredientColor();
        blendTotalTicks = brewTicks;

        syncToClient();
    }

    private void resetBrewState() {
        brewTicks = 0;
        bubbleTicks = 0;
        bubbleDelay = 0;

        startColor = currentColor;
        targetColor = currentColor;
        blendTotalTicks = 1;
    }

    private void finalizeBrew() {
        currentColor = targetColor;
        resetBrewState();

        if (finalize) {
            extractable = true;
        }

        syncToClient();
    }

    private void extractBrew(Player player, ItemStack usedItem, ItemStack result) {
        ItemStack out = createBrewStack(result.copy());

        if (!player.isCreative()) {
            usedItem.shrink(1);
            lowerFillLevel();
        }

        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 0.5F);
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

    public void tick() {
        if (level == null) return;
        if (!isBrewing()) return;

        brewTicks--;
        doBubbleEffects();

        if (brewTicks <= 0) {
            finalizeBrew();
        }

        if (level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 8);
        }
    }

    public void fastForwardBrew(long skippedTicks) {
        if (level == null || skippedTicks <= 0) return;

        int total = Math.max(1, blendTotalTicks);
        int remaining = Math.max(0, brewTicks);

        int skip = (int) Math.min(skippedTicks, remaining);
        int elapsed = total - remaining;
        int newElapsed = Math.min(total, elapsed + skip);

        float t = clamp01(newElapsed / (float) total);
        int progressed = lerpArgb(startColor, targetColor, t);

        currentColor = progressed;
        startColor = progressed;

        brewTicks = Math.max(1, remaining - skip);

        bubbleDelay = 0;
        syncToClient();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        if (level.isClientSide) {
            ensureBaseWaterColor();
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_BREW_TICKS, brewTicks);
        tag.putInt(NBT_BLEND_TOTAL_TICKS, blendTotalTicks);
        tag.putInt(NBT_START_COLOR, startColor);
        tag.putInt(NBT_TARGET_COLOR, targetColor);
        tag.putInt(NBT_CURRENT_COLOR, currentColor);
        tag.putInt(NBT_BUBBLE_TICKS, bubbleTicks);
        tag.putBoolean(NBT_FINALIZE, finalize);
        if (extractable) tag.putBoolean(NBT_EXTRACTABLE, true);
        else tag.remove(NBT_EXTRACTABLE);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        brewTicks = Math.max(0, tag.getInt(NBT_BREW_TICKS));
        bubbleTicks = Math.max(0, tag.getInt(NBT_BUBBLE_TICKS));
        blendTotalTicks = Math.max(1, tag.getInt(NBT_BLEND_TOTAL_TICKS));

        if (tag.contains(NBT_START_COLOR, Tag.TAG_INT)) startColor = tag.getInt(NBT_START_COLOR);
        if (tag.contains(NBT_TARGET_COLOR, Tag.TAG_INT)) targetColor = tag.getInt(NBT_TARGET_COLOR);
        if (tag.contains(NBT_CURRENT_COLOR, Tag.TAG_INT)) currentColor = tag.getInt(NBT_CURRENT_COLOR);

        finalize = tag.getBoolean(NBT_FINALIZE);
        extractable = tag.getBoolean(NBT_EXTRACTABLE);
    }

    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt(NBT_BREW_TICKS, brewTicks);
        tag.putInt(NBT_BUBBLE_TICKS, bubbleTicks);
        tag.putInt(NBT_BUBBLE_DELAY, bubbleDelay);

        if (!lastIngredient.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(lastIngredient.getItem());
            tag.putString(NBT_LAST_INGREDIENT_ID, id.toString());
        } else {
            tag.remove(NBT_LAST_INGREDIENT_ID);
        }

        if (ingredients.isEmpty()) {
            tag.remove(NBT_INGREDIENTS);
        } else {
            ListTag list = new ListTag();
            for (var e : ingredients.entrySet()) {
                Item item = e.getKey();
                IngredientData data = e.getValue();
                if (item == Items.AIR || data == null) continue;

                int count = data.count();
                if (count <= 0) continue;

                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

                CompoundTag one = new CompoundTag();
                one.putString(NBT_ITEM, id.toString());
                if (count != 1) one.putInt(NBT_COUNT, count);
                one.putInt(NBT_COLOR, data.color());

                list.add(one);
            }

            if (list.isEmpty()) tag.remove(NBT_INGREDIENTS);
            else tag.put(NBT_INGREDIENTS, list);
        }

        tag.putInt(NBT_TARGET_COLOR, targetColor);
        tag.putInt(NBT_CURRENT_COLOR, currentColor);

        tag.putBoolean(NBT_FINALIZE, finalize);
        if (extractable) tag.putBoolean(NBT_EXTRACTABLE, true);
        else tag.remove(NBT_EXTRACTABLE);

        if (brewTicks > 0) {
            tag.putInt(NBT_START_COLOR, startColor);
            tag.putInt(NBT_BLEND_TOTAL_TICKS, blendTotalTicks);
        } else {
            tag.remove(NBT_START_COLOR);
            tag.remove(NBT_BLEND_TOTAL_TICKS);
        }

        if (effects.isEmpty()) {
            tag.remove(NBT_EFFECTS);
        } else {
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

        brewTicks = Math.max(0, tag.getInt(NBT_BREW_TICKS));
        bubbleTicks = Math.max(0, tag.getInt(NBT_BUBBLE_TICKS));
        bubbleDelay = Math.max(0, tag.getInt(NBT_BUBBLE_DELAY));

        if (tag.contains(NBT_LAST_INGREDIENT_ID, Tag.TAG_STRING)) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString(NBT_LAST_INGREDIENT_ID));
            if (id != null) {
                Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
                lastIngredient = (item != Items.AIR) ? new ItemStack(item) : ItemStack.EMPTY;
            } else {
                lastIngredient = ItemStack.EMPTY;
            }
        } else {
            lastIngredient = ItemStack.EMPTY;
        }

        ingredients.clear();
        if (tag.contains(NBT_INGREDIENTS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_INGREDIENTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag one = list.getCompound(i);

                if (!one.contains(NBT_ITEM, Tag.TAG_STRING)) continue;
                ResourceLocation id = ResourceLocation.tryParse(one.getString(NBT_ITEM));
                if (id == null) continue;

                Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
                if (item == Items.AIR) continue;

                int count = one.contains(NBT_COUNT, Tag.TAG_INT) ? one.getInt(NBT_COUNT) : 1;
                if (count <= 0) continue;

                count = Math.min(3, count);

                int color = one.contains(NBT_COLOR, Tag.TAG_INT) ? one.getInt(NBT_COLOR) : 0xFFFFFFFF;
                ingredients.put(item, new IngredientData(count, color));
            }
        }

        finalize = tag.getBoolean(NBT_FINALIZE);
        extractable = tag.getBoolean(NBT_EXTRACTABLE);

        targetColor = tag.contains(NBT_TARGET_COLOR, Tag.TAG_INT) ? tag.getInt(NBT_TARGET_COLOR) : 0xFFFFFFFF;
        currentColor = tag.contains(NBT_CURRENT_COLOR, Tag.TAG_INT) ? tag.getInt(NBT_CURRENT_COLOR) : targetColor;

        if (brewTicks > 0) {
            startColor = tag.contains(NBT_START_COLOR, Tag.TAG_INT) ? tag.getInt(NBT_START_COLOR) : currentColor;
            int total = tag.contains(NBT_BLEND_TOTAL_TICKS, Tag.TAG_INT) ? tag.getInt(NBT_BLEND_TOTAL_TICKS) : brewTicks;
            blendTotalTicks = Math.max(1, total);
        } else {
            startColor = targetColor;
            blendTotalTicks = 1;
            currentColor = targetColor;
        }

        effects.clear();
        if (tag.contains(NBT_EFFECTS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_EFFECTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag one = list.getCompound(i);

                if (!one.contains(NBT_EFFECT_ID, Tag.TAG_STRING)) continue;
                ResourceLocation idLoc = ResourceLocation.tryParse(one.getString(NBT_EFFECT_ID));
                if (idLoc == null) continue;

                ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, idLoc);

                int duration = one.getInt(NBT_EFFECT_DURATION);
                int amplifier = one.contains(NBT_EFFECT_AMPLIFIER, Tag.TAG_INT) ? one.getInt(NBT_EFFECT_AMPLIFIER) : 0;

                effects.add(new FermentingCauldronRecipe.EffectData(key, duration, amplifier));
            }
        }
    }

    private void doBubbleEffects() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!isBrewing()) return;
        if (bubbleTicks <= 0) return;
        if (bubbleDelay > 0) { bubbleDelay--; return; }

        double x = worldPosition.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5);
        double y = worldPosition.getY() + 1.01;
        double z = worldPosition.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5);

        serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, x, y, z, 1, 0.0, 0.05, 0.0, 0.05);
        serverLevel.playSound(null, x, y, z, SoundEvents.BUBBLE_POP, SoundSource.BLOCKS, 0.3f, 1.4f);

        bubbleDelay = 3 + serverLevel.random.nextInt(bubbleTicks);
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

    public int getRenderColor() {
        if (level == null) return currentColor;

        if (level.isClientSide && currentColor == 0xFFFFFFFF) {
            ensureBaseWaterColor();
        }

        if (!isBrewing()) return currentColor;

        int total = Math.max(1, blendTotalTicks);
        int remaining = Math.max(0, brewTicks);
        int elapsed = total - remaining;

        float t = clamp01(elapsed / (float) total);
        return lerpArgb(startColor, targetColor, t);
    }

    private int computeMixedIngredientColor() {
        double sumW = 0.0;
        double sumR = 0.0;
        double sumG = 0.0;
        double sumB = 0.0;

        for (var e : ingredients.entrySet()) {
            IngredientData data = e.getValue();
            if (data == null) continue;

            int count = data.count();
            if (count <= 0) continue;

            int c = data.color();
            int r = (c >>> 16) & 0xFF;
            int g = (c >>> 8) & 0xFF;
            int b = c & 0xFF;

            int steps = Math.min(3, count);
            for (int i = 0; i < steps; i++) {
                double w = 1.0 / (1 << i);
                sumW += w;
                sumR += r * w;
                sumG += g * w;
                sumB += b * w;
            }
        }

        if (sumW <= 0.0) {
            return currentColor;
        }

        int outR = clamp255((int) Math.round(sumR / sumW));
        int outG = clamp255((int) Math.round(sumG / sumW));
        int outB = clamp255((int) Math.round(sumB / sumW));

        return 0xFF000000 | (outR << 16) | (outG << 8) | outB;
    }

    private static int clamp255(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }

    private static float clamp01(float v) {
        return v < 0f ? 0f : Math.min(v, 1f);
    }

    private static int lerpArgb(int a, int b, float t) {
        int aA = (a >>> 24) & 0xFF, aR = (a >>> 16) & 0xFF, aG = (a >>> 8) & 0xFF, aB = a & 0xFF;
        int bA = (b >>> 24) & 0xFF, bR = (b >>> 16) & 0xFF, bG = (b >>> 8) & 0xFF, bB = b & 0xFF;

        int oA = (int) (aA + (bA - aA) * t);
        int oR = (int) (aR + (bR - aR) * t);
        int oG = (int) (aG + (bG - aG) * t);
        int oB = (int) (aB + (bB - aB) * t);

        return (oA << 24) | (oR << 16) | (oG << 8) | oB;
    }

    private void ensureBaseWaterColor() {
        if (level == null) return;
        if (currentColor != 0xFFFFFFFF) return;

        int rgb = level.getBiome(worldPosition).value().getWaterColor();
        int argb = 0xFF000000 | (rgb & 0xFFFFFF);

        currentColor = argb;
        startColor = argb;

        if (targetColor == 0xFFFFFFFF) {
            targetColor = argb;
        }
    }
}