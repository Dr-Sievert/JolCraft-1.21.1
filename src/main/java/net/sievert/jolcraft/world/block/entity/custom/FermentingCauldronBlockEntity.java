package net.sievert.jolcraft.world.block.entity.custom;

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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.player.JolCraftStats;
import net.sievert.jolcraft.world.player.attachment.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipeInput;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.SyncingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.util.FermentingCauldronColorHelper;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
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
                    JolCraftStrings.plural(JolCraftDictionary.TICK)
            );

    private static final String NBT_BUBBLE_TICKS =
            JolCraftStrings.underscored(
                    JolCraftDictionary.BUBBLE,
                    JolCraftStrings.plural(JolCraftDictionary.TICK)
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
            JolCraftStrings.plural(JolCraftDictionary.INGREDIENT);

    private static final String NBT_ITEM = JolCraftDictionary.ITEM;
    private static final String NBT_COUNT = JolCraftDictionary.COUNT;
    private static final String NBT_COLOR = JolCraftDictionary.COLOR;

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

    private static final String NBT_FINALIZE = JolCraftDictionary.FINALIZE;
    private static final String NBT_EXTRACTABLE = JolCraftDictionary.EXTRACTABLE;

    private static final String NBT_EFFECTS =
            JolCraftStrings.plural(JolCraftDictionary.EFFECT);

    private static final String NBT_EFFECT_ID = JolCraftDictionary.ID;
    private static final String NBT_EFFECT_DURATION = JolCraftDictionary.DURATION;
    private static final String NBT_EFFECT_AMPLIFIER = JolCraftDictionary.AMPLIFIER;

    private static final int MAX_INGREDIENT_STACK = 3;

    private static final LootContextParamSet EXECUTION_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(JolCraftRecipeContextParams.INPUT_ITEM)
                    .build();

    private ItemStack lastIngredient = ItemStack.EMPTY;

    private final HashMap<Item, IngredientData> ingredients = new HashMap<>();
    private record IngredientData(int count, int color) implements FermentingCauldronColorHelper.IngredientView {}

    private final List<MobEffectInstance> effects = new ArrayList<>();
    private boolean finalize;
    private boolean extractable;

    private int bubbleTicks = 0;
    private int bubbleDelay = 0;

    private int currentColor = FermentingCauldronColorHelper.UNSET_COLOR;
    private int startColor = FermentingCauldronColorHelper.UNSET_COLOR;
    private int targetColor = FermentingCauldronColorHelper.UNSET_COLOR;
    private long brewStartTime = 0L;
    private int blendTotalTicks = 1;

    public FermentingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.FERMENTING_CAULDRON.get(), pos, state);
    }

    public ItemInteractionResult handleInteraction(Player player, InteractionHand hand, ItemStack usedItem) {
        if (level == null || level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.FAIL;
        }
        if (isBrewing()) {
            return ItemInteractionResult.FAIL;
        }
        if (usedItem.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        FermentingCauldronRecipe recipe = findRecipe(usedItem);
        if (recipe == null) {
            return ItemInteractionResult.FAIL;
        }

        if (extractable) {
            return tryExtract(player, usedItem, recipe);
        }

        return tryInsert(player, usedItem, recipe);
    }

    private ItemInteractionResult tryExtract(
            Player player,
            ItemStack usedItem,
            FermentingCauldronRecipe recipe
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemInteractionResult.FAIL;
        }

        List<ItemStack> extracts = generateExtract(
                serverLevel,
                recipe,
                usedItem
        );

        if (extracts.isEmpty()) {
            return ItemInteractionResult.FAIL;
        }

        extractBrew(
                player,
                usedItem,
                extracts
        );

        return ItemInteractionResult.SUCCESS;
    }

    private List<ItemStack> generateExtract(
            ServerLevel serverLevel,
            FermentingCauldronRecipe recipe,
            ItemStack usedItem
    ) {
        if (recipe.extract().isEmpty()) {
            return List.of();
        }

        LootContext context = createExecutionContext(
                serverLevel,
                usedItem.copyWithCount(1)
        );

        List<ItemStack> generated = new ArrayList<>();

        recipe.generateExtract(
                context,
                stack -> {
                    if (!stack.isEmpty()) {
                        generated.add(stack.copy());
                    }
                }
        );

        return generated;
    }

    private ItemInteractionResult tryInsert(Player player, ItemStack usedItem, FermentingCauldronRecipe recipe) {
        if (level == null || level.isClientSide) {
            return ItemInteractionResult.FAIL;
        }

        Item itemKey = usedItem.getItem();
        ItemStack ingredientKey = usedItem.copyWithCount(1);

        IngredientData existing = ingredients.get(itemKey);
        int count = existing == null ? 0 : existing.count();

        if (count >= MAX_INGREDIENT_STACK) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX)
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        boolean hasRecipeEffect =
                recipe.effect().isPresent();

        if (hasRecipeEffect && !ingredients.isEmpty() && !ingredients.containsKey(itemKey)) {
            if (!DwarfLoreAttachmentHelper.hasUnlock(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)) {
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI)
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return ItemInteractionResult.SUCCESS;
            }
        }

        return applyInsert(player, usedItem, ingredientKey, itemKey, count, recipe);
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
                Stats.ITEM_USED.get(usedItem.getItem())
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

        FermentingCauldronRecipeInput recipeInput =
                new FermentingCauldronRecipeInput(
                        ingredientKey.copyWithCount(1),
                        lastIngredient.isEmpty()
                                ? ItemStack.EMPTY
                                : lastIngredient.copyWithCount(1)
                );

        LootContext context =
                createExecutionContext(
                        serverLevel,
                        ingredientKey
                );

        recipe.generateEffect(
                context,
                recipeInput,
                this::upsertEffect
        );

        setLastIngredient(ingredientKey);
        startBrew(
                recipe.brewTicks(),
                recipe.bubbleTicks()
        );

        return ItemInteractionResult.SUCCESS;
    }

    private void upsertEffect(
            MobEffectInstance effect
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

        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance existing = effects.get(i);

            ResourceKey<MobEffect> existingKey =
                    existing.getEffect()
                            .unwrapKey()
                            .orElse(null);

            if (key.equals(existingKey)) {
                effects.set(
                        i,
                        new MobEffectInstance(effect)
                );
                return;
            }
        }

        effects.add(
                new MobEffectInstance(effect)
        );
    }

    private void setLastIngredient(ItemStack stack) {
        lastIngredient = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
    }

    public boolean isBrewing() {
        return brewStartTime > 0L;
    }

    private void startBrew(int recipeBlendTicks, int recipeBubbleTicks) {
        if (level == null || level.isClientSide) {
            return;
        }

        currentColor = FermentingCauldronColorHelper.resolveBaseWaterColor(level, worldPosition, currentColor);

        blendTotalTicks = Math.max(1, recipeBlendTicks);
        bubbleTicks = Math.max(0, recipeBubbleTicks);
        bubbleDelay = 0;

        startColor = currentColor;
        targetColor = FermentingCauldronColorHelper.computeMixedIngredientColor(ingredients.values(), currentColor);

        brewStartTime = level.getGameTime();
        syncClient();
    }

    private void finalizeBrew() {
        currentColor = targetColor;

        brewStartTime = 0L;
        blendTotalTicks = 1;

        bubbleTicks = 0;
        bubbleDelay = 0;

        startColor = currentColor;

        if (finalize) {
            extractable = true;
        }

        syncClient();
    }

    @Override
    public void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (!isBrewing()) {
            return;
        }

        doBubbleEffects();

        if (FermentingCauldronColorHelper.isComplete(level, brewStartTime, blendTotalTicks)) {
            finalizeBrew();
        }
    }

    public static void handleSleepFinished(ServerLevel level, long newTime) {
        long skipped = newTime - level.getDayTime();
        if (skipped <= 0L) {
            return;
        }

        Set<BlockPos> seen = new HashSet<>();

        for (ServerPlayer player : level.players()) {
            int centerChunkX = SectionPos.blockToSectionCoord(player.blockPosition().getX());
            int centerChunkZ = SectionPos.blockToSectionCoord(player.blockPosition().getZ());

            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    var chunk = level.getChunk(centerChunkX + dx, centerChunkZ + dz, ChunkStatus.FULL, false);
                    if (!(chunk instanceof LevelChunk levelChunk)) {
                        continue;
                    }

                    for (BlockEntity blockEntity : levelChunk.getBlockEntities().values()) {
                        if (!(blockEntity instanceof FermentingCauldronBlockEntity cauldron)) {
                            continue;
                        }
                        if (!cauldron.isBrewing()) {
                            continue;
                        }
                        if (!seen.add(blockEntity.getBlockPos())) {
                            continue;
                        }

                        cauldron.fastForwardBrew(skipped);
                    }
                }
            }
        }
    }

    public void fastForwardBrew(long skippedTicks) {
        if (level == null || level.isClientSide) {
            return;
        }
        if (skippedTicks <= 0L) {
            return;
        }
        if (!isBrewing()) {
            return;
        }

        long newStart = FermentingCauldronColorHelper.fastForwardStartTime(
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

    private void extractBrew(
            Player player,
            ItemStack usedItem,
            List<ItemStack> results
    ) {
        player.awardStat(
                Stats.ITEM_USED.get(usedItem.getItem())
        );

        if (!player.isCreative()) {
            usedItem.shrink(1);
            lowerFillLevel();
        }

        PlaySound.bottleFill(
                player,
                0.8F,
                0.9F
        );

        for (ItemStack result : results) {
            ItemStack out = createBrewStack(
                    result.copy()
            );

            if (out.isEmpty()) {
                continue;
            }

            if (!player.getInventory().add(out)) {
                player.drop(
                        out,
                        false
                );
            }
        }

        if (!player.level().isClientSide) {
            player.awardStat(
                    JolCraftStats.DWARVEN_BREWS_CREATED.get()
            );
        }
    }

    private ItemStack createBrewStack(
            ItemStack out
    ) {
        out.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                currentColor
        );

        if (effects.isEmpty()
                || !(out.getItem() instanceof PotionItem)) {
            return out;
        }

        List<MobEffectInstance> customEffects =
                new ArrayList<>(effects.size());

        for (MobEffectInstance effect : effects) {
            if (effect.getDuration() < 1) {
                continue;
            }

            if (effect.getAmplifier() < 0) {
                continue;
            }

            customEffects.add(
                    new MobEffectInstance(effect)
            );
        }

        out.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.empty(),
                        Optional.empty(),
                        List.copyOf(customEffects)
                )
        );

        return out;
    }

    @Nullable
    private FermentingCauldronRecipe findRecipe(
            ItemStack usedItem
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        ItemStack ingredient =
                usedItem.copyWithCount(1);

        ItemStack last =
                lastIngredient.isEmpty()
                        ? ItemStack.EMPTY
                        : lastIngredient.copyWithCount(1);

        FermentingCauldronRecipeInput input =
                new FermentingCauldronRecipeInput(
                        ingredient,
                        last
                );

        return serverLevel.getRecipeManager()
                .getRecipeFor(
                        JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get(),
                        input,
                        serverLevel
                )
                .map(RecipeHolder::value)
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
        return defaultUpdatePacket();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        writeClientData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        readClientData(tag);
    }

    private void writeClientData(CompoundTag tag) {
        tag.putInt(NBT_CURRENT_COLOR, currentColor);
        tag.putInt(NBT_START_COLOR, startColor);
        tag.putInt(NBT_TARGET_COLOR, targetColor);
        tag.putLong(NBT_BREW_START_TIME, brewStartTime);
        tag.putInt(NBT_BLEND_TOTAL_TICKS, blendTotalTicks);
    }

    private void readClientData(CompoundTag tag) {
        if (tag.contains(NBT_CURRENT_COLOR, Tag.TAG_INT)) {
            currentColor = tag.getInt(NBT_CURRENT_COLOR);
        }
        if (tag.contains(NBT_START_COLOR, Tag.TAG_INT)) {
            startColor = tag.getInt(NBT_START_COLOR);
        }
        if (tag.contains(NBT_TARGET_COLOR, Tag.TAG_INT)) {
            targetColor = tag.getInt(NBT_TARGET_COLOR);
        }
        if (tag.contains(NBT_BREW_START_TIME, Tag.TAG_LONG)) {
            brewStartTime = tag.getLong(NBT_BREW_START_TIME);
        }
        if (tag.contains(NBT_BLEND_TOTAL_TICKS, Tag.TAG_INT)) {
            blendTotalTicks = Math.max(1, tag.getInt(NBT_BLEND_TOTAL_TICKS));
        }
    }

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
                if (item == Items.AIR || data == null) {
                    continue;
                }

                int count = data.count();
                if (count <= 0) {
                    continue;
                }

                ResourceLocation id = item.builtInRegistryHolder().key().location();

                CompoundTag one = new CompoundTag();
                one.putString(NBT_ITEM, id.toString());
                if (count != 1) {
                    one.putInt(NBT_COUNT, count);
                }
                one.putInt(NBT_COLOR, data.color());

                list.add(one);
            }
            if (!list.isEmpty()) {
                tag.put(NBT_INGREDIENTS, list);
            }
        }

        tag.putInt(NBT_CURRENT_COLOR, currentColor);
        tag.putInt(NBT_START_COLOR, startColor);
        tag.putInt(NBT_TARGET_COLOR, targetColor);

        tag.putBoolean(NBT_FINALIZE, finalize);
        if (extractable) {
            tag.putBoolean(NBT_EXTRACTABLE, true);
        }

        if (!effects.isEmpty()) {
            ListTag list = new ListTag();

            for (MobEffectInstance effect : effects) {
                ResourceKey<MobEffect> key =
                        effect.getEffect()
                                .unwrapKey()
                                .orElse(null);

                if (key == null) {
                    continue;
                }

                CompoundTag one = new CompoundTag();

                one.putString(
                        NBT_EFFECT_ID,
                        key.location().toString()
                );

                one.putInt(
                        NBT_EFFECT_DURATION,
                        effect.getDuration()
                );

                if (effect.getAmplifier() != 0) {
                    one.putInt(
                            NBT_EFFECT_AMPLIFIER,
                            effect.getAmplifier()
                    );
                }

                list.add(one);
            }

            if (!list.isEmpty()) {
                tag.put(
                        NBT_EFFECTS,
                        list
                );
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        var itemLookup = registries.lookupOrThrow(Registries.ITEM);
        var effectLookup = registries.lookupOrThrow(Registries.MOB_EFFECT);

        brewStartTime = tag.getLong(NBT_BREW_START_TIME);
        blendTotalTicks = Math.max(1, tag.getInt(NBT_BLEND_TOTAL_TICKS));

        bubbleTicks = Math.max(0, tag.getInt(NBT_BUBBLE_TICKS));
        bubbleDelay = Math.max(0, tag.getInt(NBT_BUBBLE_DELAY));

        if (tag.contains(NBT_LAST_INGREDIENT_ID, Tag.TAG_STRING)) {
            String raw = tag.getString(NBT_LAST_INGREDIENT_ID);
            ResourceLocation id = ResourceLocation.tryParse(raw);

            if (id == null) {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK_ENTITY,
                        "FermentingCauldron at {} has malformed lastIngredient name '{}' (clearing)",
                        JolCraftLogs.roundedPos(this),
                        raw
                );
                lastIngredient = ItemStack.EMPTY;
            } else {
                Item item = itemLookup
                        .get(ResourceKey.create(Registries.ITEM, id))
                        .map(Holder.Reference::value)
                        .orElse(Items.AIR);

                if (item == Items.AIR) {
                    JolCraftLogs.debug(
                            JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} missing lastIngredient item '{}' (clearing)",
                            JolCraftLogs.roundedPos(this),
                            id
                    );
                    lastIngredient = ItemStack.EMPTY;
                } else {
                    lastIngredient = new ItemStack(item);
                }
            }
        } else {
            lastIngredient = ItemStack.EMPTY;
        }

        ingredients.clear();
        if (tag.contains(NBT_INGREDIENTS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_INGREDIENTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag one = list.getCompound(i);

                if (!one.contains(NBT_ITEM, Tag.TAG_STRING)) {
                    continue;
                }
                String raw = one.getString(NBT_ITEM);

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
                        .get(ResourceKey.create(Registries.ITEM, id))
                        .map(Holder.Reference::value)
                        .orElse(Items.AIR);

                if (item == Items.AIR) {
                    JolCraftLogs.debug(
                            JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} missing ingredient item '{}' (skipping)",
                            JolCraftLogs.roundedPos(this),
                            id
                    );
                    continue;
                }

                int count = one.contains(NBT_COUNT, Tag.TAG_INT) ? one.getInt(NBT_COUNT) : 1;
                if (count <= 0) {
                    continue;
                }
                count = Math.min(MAX_INGREDIENT_STACK, count);

                int color = one.contains(NBT_COLOR, Tag.TAG_INT) ? one.getInt(NBT_COLOR) : 0xFFFFFFFF;
                ingredients.put(item, new IngredientData(count, color));
            }
        }

        finalize = tag.getBoolean(NBT_FINALIZE);
        extractable = tag.getBoolean(NBT_EXTRACTABLE);

        currentColor = tag.contains(NBT_CURRENT_COLOR, Tag.TAG_INT)
                ? tag.getInt(NBT_CURRENT_COLOR)
                : FermentingCauldronColorHelper.UNSET_COLOR;

        startColor = tag.contains(NBT_START_COLOR, Tag.TAG_INT)
                ? tag.getInt(NBT_START_COLOR)
                : currentColor;

        targetColor = tag.contains(NBT_TARGET_COLOR, Tag.TAG_INT)
                ? tag.getInt(NBT_TARGET_COLOR)
                : currentColor;

        effects.clear();
        if (tag.contains(NBT_EFFECTS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_EFFECTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag one = list.getCompound(i);

                if (!one.contains(NBT_EFFECT_ID, Tag.TAG_STRING)) {
                    continue;
                }
                String raw = one.getString(NBT_EFFECT_ID);

                ResourceLocation idLoc = ResourceLocation.tryParse(raw);
                if (idLoc == null) {
                    JolCraftLogs.warn(
                            JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} has malformed effect name '{}' (skipping)",
                            JolCraftLogs.roundedPos(worldPosition),
                            raw
                    );
                    continue;
                }

                ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, idLoc);
                Holder<MobEffect> holder = effectLookup.get(key).orElse(null);
                if (holder == null) {
                    JolCraftLogs.debug(
                            JolCraftLogTags.BLOCK_ENTITY,
                            "FermentingCauldron at {} missing MobEffect '{}' (skipping)",
                            JolCraftLogs.roundedPos(this),
                            idLoc
                    );
                    continue;
                }

                int duration = one.getInt(NBT_EFFECT_DURATION);
                if (duration < 1) {
                    continue;
                }

                int amplifier = one.contains(NBT_EFFECT_AMPLIFIER, Tag.TAG_INT)
                        ? one.getInt(NBT_EFFECT_AMPLIFIER)
                        : 0;
                if (amplifier < 0) {
                    amplifier = 0;
                }

                effects.add(
                        new MobEffectInstance(
                                holder,
                                duration,
                                amplifier
                        )
                );
            }
        }
    }

    private void doBubbleEffects() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (!isBrewing()) {
            return;
        }

        if (bubbleTicks <= 0) {
            return;
        }
        if (bubbleDelay > 0) {
            bubbleDelay--;
            return;
        }

        double x = worldPosition.getX() + 0.5D + (level.random.nextDouble() - 0.5D);
        double y = worldPosition.getY() + 1.01D;
        double z = worldPosition.getZ() + 0.5D + (level.random.nextDouble() - 0.5D);

        JolCraftParticleHelper.spawn(
                level,
                ParticleTypes.BUBBLE_POP,
                x, y, z,
                1,
                0.0D, 0.05D, 0.0D,
                0.0D
        );

        JolCraftSoundHelper.block(
                level,
                BlockPos.containing(x, y, z),
                SoundEvents.BUBBLE_COLUMN_BUBBLE_POP,
                0.3F,
                1.4F
        );

        bubbleDelay = 3 + level.random.nextInt(bubbleTicks);
    }

    private void lowerFillLevel() {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockState state = level.getBlockState(worldPosition);
        if (!state.hasProperty(LayeredCauldronBlock.LEVEL)) {
            return;
        }

        int levelValue = state.getValue(LayeredCauldronBlock.LEVEL);

        if (levelValue <= 1) {
            level.setBlockAndUpdate(worldPosition, Blocks.CAULDRON.defaultBlockState());
        } else {
            level.setBlockAndUpdate(worldPosition, state.setValue(LayeredCauldronBlock.LEVEL, levelValue - 1));
        }
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
}