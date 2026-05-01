package net.sievert.jolcraft.world.block.registry.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class JolCraftBlockRegistryHelper {

    private JolCraftBlockRegistryHelper() {}

    public static <B extends Block> DeferredBlock<B> registerBlock(
            String name,
            Function<BlockBehaviour.Properties, ? extends B> builder,
            BlockBehaviour.Properties properties,
            boolean registerItem
    ) {
        DeferredBlock<B> block = JolCraftBlocks.BLOCKS.registerBlock(name, builder, properties);
        if (registerItem) {
            registerBlockItem(name, block);
        }
        return block;
    }

    public static <B extends Block> void registerBlockItem(String name, DeferredBlock<B> block) {
        JolCraftItems.ITEMS.registerItem(name, props -> new BlockItem(block.get(), props));
    }

    public static <B extends Block> DeferredBlock<B> registerMithrilBlock(
            String name,
            Function<BlockBehaviour.Properties, ? extends B> builder,
            BlockBehaviour.Properties properties
    ) {
        DeferredBlock<B> block = JolCraftBlocks.BLOCKS.registerBlock(name, builder, properties);
        JolCraftItems.ITEMS.registerItem(name, props ->
                new BlockItem(block.get(), props.fireResistant().rarity(Rarity.RARE))
        );
        return block;
    }

    public static BlockBehaviour.Properties flowerPotProperties() {
        return BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
    }

    public static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return state -> state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    public static boolean always(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}