package net.sievert.jolcraft.event.game.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.block.JolCraftBlocks;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"removal", "SameParameterValue"})
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftBonemealEvents {

    private static final int PLACEMENT_ATTEMPTS = 16;

    private static final Map<Block, List<BonemealRule>> RULES = Map.of(
            Blocks.GRASS_BLOCK, List.of(
                    rule(
                            JolCraftBlocks.SKYBELL,
                            biomeTag(JolCraftTags.Biomes.DWARVEN),
                            0.25F,
                            (level, pos) -> pos.getY() >= 100
                    )
            ),
            Blocks.WARPED_NYLIUM, List.of(
                    rule(
                            JolCraftBlocks.CYANELLA,
                            biome(Biomes.WARPED_FOREST),
                            0.25F
                    )
            )
    );

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        List<BonemealRule> rules = RULES.get(event.getState().getBlock());
        if (rules == null) {
            return;
        }

        BlockPos origin = event.getPos();
        Holder<Biome> biome = level.getBiome(origin);

        for (BonemealRule rule : rules) {
            if (!rule.biome().test(biome)
                    || !rule.guard().test(level, origin)
                    || level.random.nextFloat() >= rule.chance()) {
                continue;
            }

            placeNearby(level, origin, rule.block().get());
        }
    }

    private static void placeNearby(ServerLevel level, BlockPos origin, Block block) {
        BlockState state = block.defaultBlockState();

        for (int i = 0; i < PLACEMENT_ATTEMPTS; i++) {
            BlockPos pos = origin.above().offset(
                    level.random.nextInt(7) - 3,
                    level.random.nextInt(3) - 1,
                    level.random.nextInt(7) - 3
            );

            if (!level.getBlockState(pos).isAir()) {
                continue;
            }

            if (!state.canSurvive(level, pos)) {
                continue;
            }

            level.setBlock(pos, state, Block.UPDATE_ALL);
            return;
        }
    }

    private static BonemealRule rule(
            Supplier<? extends Block> block,
            Predicate<Holder<Biome>> biome,
            float chance
    ) {
        return rule(block, biome, chance, (level, pos) -> true);
    }

    private static BonemealRule rule(
            Supplier<? extends Block> block,
            Predicate<Holder<Biome>> biome,
            float chance,
            BiPredicate<ServerLevel, BlockPos> guard
    ) {
        return new BonemealRule(block, biome, chance, guard);
    }

    private static Predicate<Holder<Biome>> biomeTag(TagKey<Biome> tag) {
        return biome -> biome.is(tag);
    }

    private static Predicate<Holder<Biome>> biome(ResourceKey<Biome> key) {
        return biome -> biome.is(key);
    }

    private record BonemealRule(
            Supplier<? extends Block> block,
            Predicate<Holder<Biome>> biome,
            float chance,
            BiPredicate<ServerLevel, BlockPos> guard
    ) {}
}