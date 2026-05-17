package net.sievert.jolcraft.world.worldgen.structure.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class AbstractJigsawStructure extends Structure {

    // ---------------------------------------------------------------------
    // Codec field keys
    // ---------------------------------------------------------------------

    protected static final String FIELD_START_POOL = JolCraftStrings.underscored(JolCraftDictionary.START, JolCraftDictionary.POOL);

    protected static final String FIELD_START_JIGSAW_NAME = JolCraftStrings.underscored(JolCraftDictionary.START, JolCraftDictionary.JIGSAW, JolCraftDictionary.NAME);

    protected static final String FIELD_SIZE = JolCraftDictionary.SIZE;

    protected static final String FIELD_START_HEIGHT = JolCraftStrings.underscored(JolCraftDictionary.START, JolCraftDictionary.HEIGHT);

    protected static final String FIELD_PROJECT_START_TO_HEIGHTMAP = JolCraftStrings.underscored(JolCraftDictionary.PROJECT, JolCraftDictionary.START, JolCraftDictionary.TO, JolCraftDictionary.HEIGHTMAP);

    protected static final String FIELD_MAX_DISTANCE_FROM_CENTER = JolCraftStrings.underscored(JolCraftDictionary.MAX, JolCraftDictionary.DISTANCE, JolCraftDictionary.FROM, JolCraftDictionary.CENTER);

    protected static final String FIELD_DIMENSION_PADDING = JolCraftStrings.underscored(JolCraftDictionary.DIMENSION, JolCraftDictionary.PADDING);

    protected static final String FIELD_LIQUID_SETTINGS = JolCraftStrings.underscored(JolCraftDictionary.LIQUID, JolCraftStrings.plural(JolCraftDictionary.SETTING));

    // ---------------------------------------------------------------------
    // Shared state
    // ---------------------------------------------------------------------

    protected final Holder<StructureTemplatePool> startPool;
    protected final Optional<ResourceLocation> startJigsawName;
    protected final int size;
    protected final HeightProvider startHeight;
    protected final Optional<Heightmap.Types> projectStartToHeightmap;
    protected final int maxDistanceFromCenter;
    protected final DimensionPadding dimensionPadding;
    protected final LiquidSettings liquidSettings;

    protected AbstractJigsawStructure(
            StructureSettings config,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawName,
            int size,
            HeightProvider startHeight,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings
    ) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    // ---------------------------------------------------------------------
    // Shared generation
    // ---------------------------------------------------------------------

    @Override
    public final @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        if (!extraSpawningChecks(context)) {
            return Optional.empty();
        }

        BlockPos blockPos = startPos(context);

        Optional<Direction> direction = startDirection(context);

        direction.ifPresent(value ->
                JolCraftStructureContext.setRotation(switch (value) {
                    case NORTH -> Rotation.NONE;
                    case EAST -> Rotation.CLOCKWISE_90;
                    case SOUTH -> Rotation.CLOCKWISE_180;
                    case WEST -> Rotation.COUNTERCLOCKWISE_90;
                    default -> throw new IllegalStateException("Horizontal direction required");
                })
        );

        try {
            return JigsawPlacement.addPieces(
                    context,
                    this.startPool,
                    this.startJigsawName,
                    this.size,
                    blockPos,
                    false,
                    this.projectStartToHeightmap,
                    this.maxDistanceFromCenter,
                    PoolAliasLookup.EMPTY,
                    this.dimensionPadding,
                    this.liquidSettings
            );
        } finally {
            JolCraftStructureContext.clear();
        }
    }

    protected abstract boolean extraSpawningChecks(GenerationContext context);

    protected Optional<Direction> startDirection(GenerationContext context) {
        return Optional.empty();
    }

    protected BlockPos startPos(GenerationContext context) {
        int startY = this.startHeight.sample(
                context.random(),
                new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor())
        );

        ChunkPos chunkPos = context.chunkPos();

        return new BlockPos(
                chunkPos.getMinBlockX(),
                startY,
                chunkPos.getMinBlockZ()
        );
    }

    // ---------------------------------------------------------------------
    // Shared codec builder helper
    // ---------------------------------------------------------------------

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected interface Factory<T extends AbstractJigsawStructure> {
        T create(
                StructureSettings config,
                Holder<StructureTemplatePool> startPool,
                Optional<ResourceLocation> startJigsawName,
                int size,
                HeightProvider startHeight,
                Optional<Heightmap.Types> projectStartToHeightmap,
                int maxDistanceFromCenter,
                DimensionPadding dimensionPadding,
                LiquidSettings liquidSettings
        );
    }

    protected static <T extends AbstractJigsawStructure> App<RecordCodecBuilder.Mu<T>, T> codec(
            RecordCodecBuilder.Instance<T> instance,
            Factory<T> factory
    ) {
        return instance.group(
                Structure.settingsCodec(instance),
                StructureTemplatePool.CODEC.fieldOf(FIELD_START_POOL).forGetter(s -> s.startPool),
                ResourceLocation.CODEC.optionalFieldOf(FIELD_START_JIGSAW_NAME).forGetter(s -> s.startJigsawName),
                Codec.intRange(0, 30).fieldOf(FIELD_SIZE).forGetter(s -> s.size),
                HeightProvider.CODEC.fieldOf(FIELD_START_HEIGHT).forGetter(s -> s.startHeight),
                Heightmap.Types.CODEC.optionalFieldOf(FIELD_PROJECT_START_TO_HEIGHTMAP).forGetter(s -> s.projectStartToHeightmap),
                Codec.intRange(1, 128).fieldOf(FIELD_MAX_DISTANCE_FROM_CENTER).forGetter(s -> s.maxDistanceFromCenter),
                DimensionPadding.CODEC.optionalFieldOf(FIELD_DIMENSION_PADDING, JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(s -> s.dimensionPadding),
                LiquidSettings.CODEC.optionalFieldOf(FIELD_LIQUID_SETTINGS, JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(s -> s.liquidSettings)
        ).apply(instance, factory::create);
    }
}