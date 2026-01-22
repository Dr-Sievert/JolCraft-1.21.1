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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.data.custom.attachment.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.recipe.JolCraftRecipes;
import net.sievert.jolcraft.recipe.custom.FermentingCauldronRecipe;
import net.sievert.jolcraft.recipe.custom.input.FermentingCauldronRecipeInput;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FermentingCauldronBlockEntity extends BlockEntity {

    private ItemStack lastIngredient = ItemStack.EMPTY;
    private final Map<Item, Integer> addedIngredients = new HashMap<>();

    private int brewTicks = 0;

    private int bubbleTicks = 0;
    private int bubbleDelay = 0;

    private int currentColor = 0xFFFFFFFF;
    private int startColor = 0xFFFFFFFF;
    private int targetColor = 0xFFFFFFFF;
    private int blendTotalTicks = 1;

    private long clientBlendStartGameTime = -1L;

    private final Map<Integer, FermentingCauldronRecipe.EffectData> effects = new LinkedHashMap<>();

    private boolean finalize;
    private boolean extractable;

    public FermentingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.FERMENTING_CAULDRON.get(), pos, state);
    }

    private void setLastIngredient(ItemStack stack) {
        this.lastIngredient = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
    }

    private boolean isBrewing() {
        return brewTicks > 0;
    }

    private void startBrew(int recipeBrewTicks, int recipeBubbleTicks, int recipeColor) {
        ensureBaseWaterColor();

        brewTicks = Math.max(1, recipeBrewTicks);
        bubbleTicks = Math.max(1, recipeBubbleTicks);
        bubbleDelay = 0;

        startColor = currentColor;
        targetColor = recipeColor;

        blendTotalTicks = brewTicks;

        clientBlendStartGameTime = -1L;

        syncToClient();
    }

    private void resetBrewState() {
        brewTicks = 0;
        bubbleTicks = 0;
        bubbleDelay = 0;

        startColor = currentColor;
        targetColor = currentColor;
        blendTotalTicks = 1;

        clientBlendStartGameTime = -1L;
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
        if (level == null || !(out.getItem() instanceof PotionItem) || effects.isEmpty()) {
            return out;
        }

        var effectsLookup = level.registryAccess().lookupOrThrow(Registries.MOB_EFFECT);

        List<MobEffectInstance> customEffects = new ArrayList<>(effects.size());
        for (FermentingCauldronRecipe.EffectData e : effects.values()) {
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
        int count = addedIngredients.getOrDefault(itemKey, 0);

        if (count >= 3) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.fermenting_cauldron.ingredient_max")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (recipe.effect() != null && !addedIngredients.isEmpty() && !addedIngredients.containsKey(itemKey)) {
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
        addedIngredients.put(itemKey, newCount);

        this.finalize = recipe.finalizeBrew();

        if (recipe.effect() != null) {
            FermentingCauldronRecipe.EffectData base = recipe.effect();
            if (base == null) return InteractionResult.SUCCESS;

            int amp = Math.min(2, newCount - 1);
            FermentingCauldronRecipe.EffectData stacked =
                    new FermentingCauldronRecipe.EffectData(base.id(), base.duration(), amp);

            boolean updatedExisting = false;
            for (var entry : effects.entrySet()) {
                FermentingCauldronRecipe.EffectData existing = entry.getValue();
                if (existing.id().equals(base.id())) {
                    entry.setValue(stacked);
                    updatedExisting = true;
                    break;
                }
            }

            if (!updatedExisting) {
                effects.put(effects.size(), stacked);
            }
        }

        setLastIngredient(ingredientKey);

        startBrew(recipe.brewTicks(), recipe.bubbleTicks(), recipe.color());

        return InteractionResult.SUCCESS;
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

        if (level.isClientSide) {
            if (isBrewing()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0);
            }
            return;
        }

        if (isBrewing()) {
            brewTicks--;

            if (bubbleTicks > 0) {
                doBubbleEffects();
            }

            if (brewTicks <= 0) {
                finalizeBrew();
            }
        }
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
        return saveWithoutMetadata(registries);
    }

    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("brewTicks", brewTicks);
        tag.putInt("bubbleTicks", bubbleTicks);

        if (lastIngredient.isEmpty()) {
            tag.remove("lastIngredient");
        } else {
            CompoundTag last = new CompoundTag();
            lastIngredient.save(registries, last);
            tag.put("lastIngredient", last);
        }

        ListTag list = new ListTag();
        for (var e : addedIngredients.entrySet()) {
            Item item = e.getKey();
            int count = e.getValue();

            if (item == Items.AIR || count <= 0) continue;

            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

            CompoundTag one = new CompoundTag();
            one.putString("item", id.toString());
            if (count != 1) one.putInt("count", count);

            list.add(one);
        }
        if (list.isEmpty()) tag.remove("addedIngredients");
        else tag.put("addedIngredients", list);

        tag.putInt("color", targetColor);

        tag.putBoolean("finalize", finalize);
        if (extractable) tag.putBoolean("extractable", true);
        else tag.remove("extractable");

        if (brewTicks > 0) {
            tag.putInt("startColor", startColor);
            tag.putInt("blendTotalTicks", blendTotalTicks);
        } else {
            tag.remove("startColor");
            tag.remove("blendTotalTicks");
        }

        if (effects.isEmpty()) {
            tag.remove("effects");
        } else {
            CompoundTag effTag = new CompoundTag();
            for (var e : effects.entrySet()) {
                FermentingCauldronRecipe.EffectData eff = e.getValue();

                CompoundTag one = new CompoundTag();
                one.putString("id", eff.id().location().toString());
                one.putInt("duration", eff.duration());
                if (eff.amplifier() != 0) one.putInt("amplifier", eff.amplifier());

                effTag.put(Integer.toString(e.getKey()), one);
            }
            tag.put("effects", effTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        brewTicks = Math.max(0, tag.getInt("brewTicks"));
        bubbleTicks = Math.max(0, tag.getInt("bubbleTicks"));

        if (tag.contains("lastIngredient", Tag.TAG_COMPOUND)) {
            lastIngredient = ItemStack.parseOptional(registries, tag.getCompound("lastIngredient"));
            if (!lastIngredient.isEmpty()) lastIngredient = lastIngredient.copyWithCount(1);
        } else {
            lastIngredient = ItemStack.EMPTY;
        }

        addedIngredients.clear();
        if (tag.contains("addedIngredients", Tag.TAG_LIST)) {
            ListTag list = tag.getList("addedIngredients", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag one = list.getCompound(i);

                if (!one.contains("item", Tag.TAG_STRING)) continue;
                ResourceLocation id = ResourceLocation.tryParse(one.getString("item"));
                if (id == null) continue;

                Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
                if (item == Items.AIR) continue;

                int count = one.contains("count", Tag.TAG_INT) ? one.getInt("count") : 1;
                if (count <= 0) continue;

                addedIngredients.put(item, Math.min(3, count));
            }
        }

        finalize = tag.getBoolean("finalize");
        extractable = tag.getBoolean("extractable");

        targetColor = tag.contains("color", Tag.TAG_INT) ? tag.getInt("color") : 0xFFFFFFFF;
        currentColor = targetColor;

        if (brewTicks > 0) {
            startColor = tag.contains("startColor", Tag.TAG_INT) ? tag.getInt("startColor") : targetColor;

            int total = tag.contains("blendTotalTicks", Tag.TAG_INT) ? tag.getInt("blendTotalTicks") : brewTicks;
            blendTotalTicks = Math.max(1, total);

            clientBlendStartGameTime = -1L;
        } else {
            startColor = targetColor;
            blendTotalTicks = 1;
            clientBlendStartGameTime = -1L;
        }

        effects.clear();
        if (tag.contains("effects", Tag.TAG_COMPOUND)) {
            CompoundTag effTag = tag.getCompound("effects");
            for (String k : effTag.getAllKeys()) {
                int idx;
                try {
                    idx = Integer.parseInt(k);
                } catch (NumberFormatException ignored) {
                    continue;
                }

                CompoundTag one = effTag.getCompound(k);
                ResourceLocation idLoc = ResourceLocation.tryParse(one.getString("id"));
                if (idLoc == null) continue;

                ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, idLoc);

                int duration = one.getInt("duration");
                int amplifier = one.contains("amplifier", Tag.TAG_INT) ? one.getInt("amplifier") : 0;

                effects.put(idx, new FermentingCauldronRecipe.EffectData(key, duration, amplifier));
            }
        }
    }

    private void doBubbleEffects() {
        if (!(level instanceof ServerLevel serverLevel) || bubbleTicks <= 0) return;

        if (bubbleDelay > 0) {
            bubbleDelay--;
            return;
        }

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
        if (level != null && level.isClientSide && currentColor == 0xFFFFFFFF) {
            ensureBaseWaterColor();
        }

        if (level == null) return currentColor;
        if (!isBrewing()) return currentColor;

        if (clientBlendStartGameTime < 0L) {
            clientBlendStartGameTime = level.getGameTime();
        }

        long elapsed = level.getGameTime() - clientBlendStartGameTime;
        float t = clamp01(elapsed / (float) Math.max(1, blendTotalTicks));
        return lerpArgb(startColor, targetColor, t);
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