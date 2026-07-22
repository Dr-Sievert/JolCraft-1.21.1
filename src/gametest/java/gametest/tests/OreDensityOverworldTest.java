//package gametest.tests;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.gametest.framework.GameTest;
//import net.minecraft.gametest.framework.GameTestHelper;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.tags.TagKey;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.biome.Biome;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.chunk.ChunkAccess;
//import net.minecraft.world.level.chunk.LevelChunkSection;
//import net.neoforged.neoforge.gametest.GameTestHolder;
//import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
//import net.sievert.jolcraft.JolCraft;
//import net.sievert.jolcraft.data.JolCraftTags;
//import net.sievert.jolcraft.util.log.JolCraftLogTags;
//import net.sievert.jolcraft.util.log.JolCraftLogs;
//import net.sievert.jolcraft.world.block.JolCraftBlocks;
//
//import java.util.Objects;
//
//@GameTestHolder(JolCraft.MOD_ID)
//public final class OreDensityOverworldTest {
//
//    private static final int SAMPLE_RADIUS = 20;
//    private static final BlockPos SAMPLE_OFFSET = new BlockPos(0, 0, 0);
//    private static final TagKey<Biome> MOUNTAINS_AND_HILLS_TAG = JolCraftTags.Biomes.MOUNTAINS_AND_HILLS;
//
//    private record MithrilCounts(long total, long tagged) {}
//
//    public OreDensityOverworldTest() {}
//
//    @PrefixGameTestTemplate(false)
//    @GameTest(template = "gametest/empty", timeoutTicks = 1200)
//    public void testAverageMithrilOreBlocksPerChunk(GameTestHelper helper) {
//        ServerLevel level = getOverworld(helper);
//
//        BlockPos sampleOrigin = helper.absolutePos(BlockPos.ZERO).offset(SAMPLE_OFFSET);
//        ChunkPos center = new ChunkPos(sampleOrigin);
//
//        long sampledChunks = 0L;
//        long mountainsAndHillsChunks = 0L;
//        long totalMithrilBlocks = 0L;
//        long taggedMithrilBlocks = 0L;
//
//        for (int chunkX = center.x - SAMPLE_RADIUS; chunkX <= center.x + SAMPLE_RADIUS; chunkX++) {
//            for (int chunkZ = center.z - SAMPLE_RADIUS; chunkZ <= center.z + SAMPLE_RADIUS; chunkZ++) {
//                ChunkAccess chunk = level.getChunk(chunkX, chunkZ);
//                MithrilCounts counts = countMithrilInChunk(level, chunk);
//
//                sampledChunks++;
//                totalMithrilBlocks += counts.total();
//                taggedMithrilBlocks += counts.tagged();
//
//                if (isChunkInMountainsAndHills(level, chunk)) {
//                    mountainsAndHillsChunks++;
//                }
//            }
//        }
//
//        double avgMithrilPerChunk = sampledChunks == 0L ? 0.0D : totalMithrilBlocks / (double) sampledChunks;
//        double avgMithrilPerMountainsAndHillsChunk =
//                mountainsAndHillsChunks == 0L ? 0.0D : taggedMithrilBlocks / (double) mountainsAndHillsChunks;
//        float taggedShare = totalMithrilBlocks == 0L ? 0.0F : taggedMithrilBlocks / (float) totalMithrilBlocks;
//
//        JolCraftLogs.info(
//                JolCraftLogTags.GAMETEST,
//                "Overworld mithril density: sampled_chunks={}, total_mithril_blocks={}, avg_mithril_per_chunk={}",
//                sampledChunks,
//                totalMithrilBlocks,
//                String.format("%.4f", avgMithrilPerChunk)
//        );
//
//        JolCraftLogs.info(
//                JolCraftLogTags.GAMETEST,
//                " - mountains_and_hills: tagged_chunks={}, tagged_mithril_blocks={}, avg_mithril_per_mountains_and_hills_chunk={}, share={}",
//                mountainsAndHillsChunks,
//                taggedMithrilBlocks,
//                String.format("%.4f", avgMithrilPerMountainsAndHillsChunk),
//                JolCraftLogs.pct1(taggedShare)
//        );
//
//        helper.assertTrue(sampledChunks > 0, "Expected to sample at least one chunk");
//        helper.assertTrue(totalMithrilBlocks > 0, "Expected sampled overworld chunks to contain mithril ore");
//        helper.succeed();
//    }
//
//    private static ServerLevel getOverworld(GameTestHelper helper) {
//        ServerLevel level = helper.getLevel().getServer().getLevel(Level.OVERWORLD);
//        helper.assertTrue(level != null, "Expected overworld level to exist");
//        helper.assertTrue(Objects.requireNonNull(level).dimension() == Level.OVERWORLD, "Expected overworld sampling level");
//        return level;
//    }
//
//    private static boolean isChunkInMountainsAndHills(ServerLevel level, ChunkAccess chunk) {
//        ChunkPos chunkPos = chunk.getPos();
//        BlockPos pos = new BlockPos(
//                chunkPos.getMiddleBlockX(),
//                level.getSeaLevel(),
//                chunkPos.getMiddleBlockZ()
//        );
//        return level.getBiome(pos).is(MOUNTAINS_AND_HILLS_TAG);
//    }
//
//    private static MithrilCounts countMithrilInChunk(ServerLevel level, ChunkAccess chunk) {
//        long total = 0L;
//        long tagged = 0L;
//
//        ChunkPos chunkPos = chunk.getPos();
//        int minY = level.getMinBuildHeight();
//        int minX = chunkPos.getMinBlockX();
//        int minZ = chunkPos.getMinBlockZ();
//
//        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
//
//        LevelChunkSection[] sections = chunk.getSections();
//        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
//            LevelChunkSection section = sections[sectionIndex];
//            if (section == null || section.hasOnlyAir()) {
//                continue;
//            }
//
//            if (!section.maybeHas(state -> state.is(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get()))) {
//                continue;
//            }
//
//            int sectionMinY = minY + (sectionIndex * 16);
//
//            for (int localY = 0; localY < 16; localY++) {
//                int worldY = sectionMinY + localY;
//
//                for (int localZ = 0; localZ < 16; localZ++) {
//                    int worldZ = minZ + localZ;
//
//                    for (int localX = 0; localX < 16; localX++) {
//                        BlockState state = section.getBlockState(localX, localY, localZ);
//                        if (!state.is(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())) {
//                            continue;
//                        }
//
//                        int worldX = minX + localX;
//                        total++;
//
//                        pos.set(worldX, worldY, worldZ);
//                        if (level.getBiome(pos).is(MOUNTAINS_AND_HILLS_TAG)) {
//                            tagged++;
//                        }
//                    }
//                }
//            }
//        }
//
//        return new MithrilCounts(total, tagged);
//    }
//}