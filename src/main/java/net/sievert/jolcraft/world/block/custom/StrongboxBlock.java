package net.sievert.jolcraft.world.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.StrongboxBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StrongboxBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LOCKED = BooleanProperty.create(JolCraftDictionary.LOCKED);

    private static final VoxelShape SHAPE_NS = Block.box(1, 0, 3, 15, 10, 13);
    private static final VoxelShape SHAPE_EW = Block.box(3, 0, 1, 13, 10, 15);

    public static final MapCodec<StrongboxBlock> CODEC = simpleCodec(StrongboxBlock::new);

    public StrongboxBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(LOCKED, false));
    }

    @Override
    protected MapCodec<? extends StrongboxBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED, LOCKED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return (facing == Direction.EAST || facing == Direction.WEST) ? SHAPE_EW : SHAPE_NS;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StrongboxBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return createTickerHelper(type, JolCraftBlockEntities.STRONGBOX.get(), TickingBlockEntity.tickOnBoth());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(be instanceof StrongboxBlockEntity strongbox)) {
            return List.of(new ItemStack(JolCraftItems.STRONGBOX_ITEM.get()));
        }

        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        boolean silkTouch = hasSilkTouch(builder.getLevel(), tool == null ? ItemStack.EMPTY : tool);

        return getStrongboxDrops(state, strongbox, silkTouch);
    }

    private static List<ItemStack> getStrongboxDrops(BlockState state, StrongboxBlockEntity strongbox, boolean silkTouch) {
        if (silkTouch) {
            return List.of(createSilkTouchDrop(strongbox, state));
        }

        if (strongbox.getLootTable() != null && state.getValue(LOCKED)) {
            return List.of(new ItemStack(JolCraftItems.STRONGBOX_ITEM.get()));
        }

        List<ItemStack> drops = new java.util.ArrayList<>();
        drops.add(new ItemStack(JolCraftItems.STRONGBOX_ITEM.get()));

        for (ItemStack stack : strongbox.getItems()) {
            if (!stack.isEmpty()) {
                drops.add(stack.copy());
            }
        }

        return drops;
    }

    private static boolean hasSilkTouch(Level level, ItemStack tool) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        return serverLevel.registryAccess()
                .lookup(Registries.ENCHANTMENT)
                .flatMap(lookup -> lookup.get(Enchantments.SILK_TOUCH))
                .map(enchantment -> EnchantmentHelper.getTagEnchantmentLevel(enchantment, tool) > 0)
                .orElse(false);
    }

    private static ItemStack createSilkTouchDrop(StrongboxBlockEntity strongbox, BlockState state) {
        ItemStack drop = new ItemStack(JolCraftItems.STRONGBOX_ITEM.get());

        drop.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(strongbox.getItems()));

        ResourceKey<LootTable> lootTable = strongbox.getLootTable();
        long seed = strongbox.getLootTableSeed();

        if (lootTable != null) {
            drop.set(JolCraftDataComponents.LOOT_TABLE, lootTable);
        }
        if (seed != 0L) {
            drop.set(JolCraftDataComponents.LOOT_SEED, seed);
        }
        if (state.getValue(LOCKED)) {
            drop.set(JolCraftDataComponents.LOCKED, true);
        }

        return drop;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof StrongboxBlockEntity strongbox)) {
            return;
        }

        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(strongbox.getContainerSize(), ItemStack.EMPTY);
            contents.copyInto(items);
            strongbox.setItems(items);
        }

        ResourceKey<LootTable> lootTable = stack.get(JolCraftDataComponents.LOOT_TABLE);
        if (lootTable != null) {
            strongbox.setLootTable(lootTable, strongbox.getLootTableSeed());
        }

        Long lootSeed = stack.get(JolCraftDataComponents.LOOT_SEED);
        if (lootSeed != null) {
            strongbox.setLootTableSeed(lootSeed);
        }

        Boolean locked = stack.get(JolCraftDataComponents.LOCKED);
        if (locked != null && state.getValue(LOCKED) != locked) {
            level.setBlock(pos, state.setValue(LOCKED, locked), Block.UPDATE_ALL);
        }

        strongbox.setChanged();
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (player.isSpectator()) {
            return ItemInteractionResult.CONSUME;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.CONSUME;
        }

        if (stack.is(JolCraftItems.DEV_KEY)) {
            boolean newLocked = !state.getValue(LOCKED);

            level.setBlock(pos, state.setValue(LOCKED, newLocked), Block.UPDATE_ALL);

            player.displayClientMessage(
                    Component.translatable(
                            newLocked
                                    ? JolCraftLanguageKeys.TOOLTIP_STRONGBOX_SET_LOCKED
                                    : JolCraftLanguageKeys.TOOLTIP_STRONGBOX_SET_UNLOCKED
                    ).withStyle(ChatFormatting.GRAY),
                    true
            );

            PlaySound.strongboxUnlock(level, pos);
            return ItemInteractionResult.SUCCESS;
        }

        if (state.getValue(LOCKED)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof StrongboxBlockEntity strongbox) {
                Player current = strongbox.getCurrentInteractingPlayer();
                if (current != null && current != player) {
                    player.displayClientMessage(
                            Component.translatable(JolCraftLanguageKeys.TOOLTIP_STRONGBOX_BUSY)
                                    .withStyle(ChatFormatting.GRAY),
                            true
                    );
                    return ItemInteractionResult.SUCCESS;
                }
            } else {
                JolCraftLogs.warn(
                        JolCraftLogTags.BLOCK,
                        "Strongbox at {} is locked but has missing/wrong BlockEntity (found={})",
                        JolCraftLogs.roundedPos(pos),
                        (be == null ? "null" : be.getClass().getName())
                );
                return ItemInteractionResult.SUCCESS;
            }
        }

        openStrongbox(state, level, pos, player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        return openStrongbox(state, level, pos, player);
    }

    private InteractionResult openStrongbox(BlockState state, Level level, BlockPos pos, Player player) {

        if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        MenuProvider provider = this.getMenuProvider(state, level, pos);
        if (provider != null) {
            player.openMenu(provider, pos);
        } else {
            BlockEntity be = level.getBlockEntity(pos);
            JolCraftLogs.warn(
                    JolCraftLogTags.BLOCK,
                    "Strongbox at {} has no MenuProvider (locked={} be={})",
                    JolCraftLogs.roundedPos(pos),
                    state.getValue(LOCKED),
                    (be == null ? "null" : be.getClass().getName())
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return (be instanceof MenuProvider menuProvider) ? menuProvider : null;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return (be instanceof StrongboxBlockEntity strongbox)
                ? AbstractContainerMenu.getRedstoneSignalFromContainer(strongbox)
                : 0;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(JolCraftItems.STRONGBOX_ITEM.get());

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof StrongboxBlockEntity strongbox) {
            stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(strongbox.getItems()));

            ResourceKey<LootTable> lootTable = strongbox.getLootTable();
            long seed = strongbox.getLootTableSeed();

            if (lootTable != null) {
                stack.set(JolCraftDataComponents.LOOT_TABLE, lootTable);
            } else {
                stack.remove(JolCraftDataComponents.LOOT_TABLE);
            }

            if (seed != 0L) {
                stack.set(JolCraftDataComponents.LOOT_SEED, seed);
            } else {
                stack.remove(JolCraftDataComponents.LOOT_SEED);
            }
        }

        if (state.getValue(LOCKED)) {
            stack.set(JolCraftDataComponents.LOCKED, true);
        } else {
            stack.remove(JolCraftDataComponents.LOCKED);
        }

        return stack;
    }
}