package net.sievert.jolcraft.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.sievert.jolcraft.block.custom.crop.HopsType;
import net.sievert.jolcraft.block.entity.custom.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.data.JolCraftComponents;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.custom.attachment.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.item.JolCraftItems;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FermentingCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public static final EnumProperty<FermentingStage> STAGE = EnumProperty.create("stage", FermentingStage.class);
    public static final EnumProperty<HopsType> HOPS_TYPE = EnumProperty.create("hops_type", HopsType.class);
    public static final IntegerProperty FERMENTATION_PROGRESS = IntegerProperty.create("fermentation_progress", 0, 9);

    public FermentingCauldronBlock(Biome.Precipitation precipitationType, CauldronInteraction.InteractionMap interactions, Properties properties) {
        super(precipitationType, interactions, properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LEVEL, 3)
                        .setValue(STAGE, FermentingStage.YEAST_FERMENTING)
                        .setValue(FERMENTATION_PROGRESS, 0)
                        .setValue(HOPS_TYPE, HopsType.NONE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE, FERMENTATION_PROGRESS, HOPS_TYPE);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.PASS;

        // 1. Extract yeast (if yeast is ready)
        if (stack.is(Items.GLASS_BOTTLE) && state.getValue(STAGE) == FermentingStage.YEAST_READY) {
            int currentLevel = state.getValue(LEVEL);
            ItemStack yeastBottle = new ItemStack(JolCraftItems.YEAST.get());
            if (!player.isCreative()) stack.shrink(1);
            if (!player.getInventory().add(yeastBottle)) player.drop(yeastBottle, false);
            int newLevel = currentLevel - 1;
            BlockState newState = newLevel > 0 ? state.setValue(LEVEL, newLevel) : Blocks.CAULDRON.defaultBlockState();
            level.setBlock(pos, newState, 3);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        // 2. Extract brew (if brew is ready)
        if (stack.is(JolCraftItems.GLASS_MUG.get()) && state.getValue(STAGE) == FermentingStage.BREW_READY) {
            int currentLevel = state.getValue(LEVEL);

            // Create the brew item
            ItemStack dwarvenBrew = new ItemStack(JolCraftItems.DWARVEN_BREW.get());

            // Attempt to get hops data from the block entity
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FermentingCauldronBlockEntity cauldron) {
                String hopsData = cauldron.getHopsString();
                if (!hopsData.isEmpty()) {
                    dwarvenBrew.set(JolCraftComponents.HOPS.get(), hopsData);
                }
            }

            // Consume mug and give brew
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            if (!player.getInventory().add(dwarvenBrew)) {
                player.drop(dwarvenBrew, false);
            }

            // Update cauldron state
            int newLevel = currentLevel - 1;
            BlockState newState = newLevel > 0
                    ? state.setValue(LEVEL, newLevel)
                    : Blocks.CAULDRON.defaultBlockState();
            level.setBlock(pos, newState, 3);

            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 0.5F);
            return InteractionResult.SUCCESS;
        }


        // Add hops (in MALTED or HOPS phase)
        if (state.getValue(STAGE) == FermentingStage.HOPS || state.getValue(STAGE) == FermentingStage.MALTED) {
            if (stack.is(JolCraftTags.Items.HOPS)) {
                String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase();
                BlockState newState = state;
                FermentingCauldronBlockEntity blockEntity = (FermentingCauldronBlockEntity) level.getBlockEntity(pos);

                HopsType hopToAdd = null;
                if (path.contains("asgarnian")) {
                    hopToAdd = HopsType.ASGARNIAN;
                } else if (path.contains("yanillian")) {
                    hopToAdd = HopsType.YANILLIAN;
                } else if (path.contains("duskhold")) {
                    hopToAdd = HopsType.DUSKHOLD;
                } else if (path.contains("krandonian")) {
                    hopToAdd = HopsType.KRANDONIAN;
                }

                if (hopToAdd != null) {
                    // 1. Dupe check always first: dupe always trumps unlock
                    assert blockEntity != null;
                    if (blockEntity.getAddedHops().contains(hopToAdd)) {
                        player.displayClientMessage(
                                Component.translatable("tooltip.jolcraft.brewing.hops").withStyle(ChatFormatting.GRAY),
                                true
                        );
                        return InteractionResult.PASS;
                    }

                    // 2. Check unlock for adding multiple different hops
                    if (!blockEntity.getAddedHops().isEmpty() &&
                            !DwarfLoreUnlockHelper.hasUnlock(player, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)) {
                        player.displayClientMessage(
                                Component.translatable("tooltip.jolcraft.brewing.locked_hops").withStyle(ChatFormatting.RED),
                                true
                        );
                        return InteractionResult.PASS;
                    }

                    // 3. Try to add the hop
                    if (blockEntity.addHop(hopToAdd)) {
                        newState = newState.setValue(HOPS_TYPE, hopToAdd);
                        newState = newState.setValue(STAGE, FermentingStage.HOPS);

                        level.setBlock(pos, newState, 3);
                        level.sendBlockUpdated(pos, state, newState, 3);

                        if (!player.isCreative()) stack.shrink(1);
                        level.playSound(null, pos, SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.4F, 1.6F);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        // Add yeast to start fermentation
        if (stack.is(JolCraftItems.YEAST.get()) && (state.getValue(STAGE) == FermentingStage.HOPS)) {
            BlockState newState = state.setValue(STAGE, FermentingStage.BREW_FERMENTING);
            level.setBlock(pos, newState, 3); // Start the fermentation
            level.sendBlockUpdated(pos, state, newState, 3);  // Force update to inform the client
            if (!player.isCreative()){
                stack.shrink(1);
                ItemStack bottle = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
                if (!player.addItem(bottle)) {
                    player.drop(bottle, false);
                }
            }
            level.playSound(null, pos, SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.4F, 1.6F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public FermentingCauldronBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FermentingCauldronBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == JolCraftBlockEntities.FERMENTING_CAULDRON.get()) {
            return (lvl, pos, st, blockEntity) -> {
                if (lvl instanceof ServerLevel) {
                    if (blockEntity instanceof FermentingCauldronBlockEntity fermenting) {
                        fermenting.tick();
                    }
                }
            };
        }
        return null;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        FermentingStage stage = state.getValue(STAGE);
        if (!level.isClientSide() && (stage == FermentingStage.YEAST_FERMENTING || stage == FermentingStage.BREW_FERMENTING)) {
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Items.CAULDRON);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}