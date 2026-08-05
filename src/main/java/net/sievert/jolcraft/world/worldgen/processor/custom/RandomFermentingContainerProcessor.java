package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.brewing.FermentingBarrelBlock;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Randomly replaces vanilla barrels and cauldrons with their fermenting equivalents and initializes them with one weighted finished-brew entry.
 *
 * Barrel facing is preserved. Existing block-entity NBT is deliberately discarded, which clears inventories and loot-table data without breaking
 * the original container or spawning its drops.
 *
 * The configured brew age applies to fermenting barrels. Fermenting cauldrons are initialized with fresh brew because their normal storage rules reject aged brew.
 */
@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class RandomFermentingContainerProcessor extends StructureProcessor {

    private static final String BREW_AGE_KEY = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.AGE
    );

    private static final String NBT_BREW_TANK = JolCraftStrings.underscored(
            JolCraftDictionary.BREW,
            JolCraftDictionary.TANK
    );

    private static final Codec<List<BrewEntry>> ENTRIES_CODEC =
            BrewEntry.CODEC.listOf().validate(
                    RandomFermentingContainerProcessor::validateEntries
            );

    public static final MapCodec<RandomFermentingContainerProcessor> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.floatRange(0.0F, 1.0F)
                            .fieldOf(JolCraftDictionary.CHANCE)
                            .forGetter(processor -> processor.chance),
                    ENTRIES_CODEC
                            .fieldOf(JolCraftDictionary.ENTRIES)
                            .forGetter(processor -> processor.entries),
                    DwarvenBrewAge.CODEC
                            .optionalFieldOf(
                                    BREW_AGE_KEY,
                                    DwarvenBrewAge.FRESH
                            )
                            .forGetter(processor -> processor.brewAge)
            ).apply(instance, RandomFermentingContainerProcessor::new));

    private final float chance;
    private final List<BrewEntry> entries;
    private final DwarvenBrewAge brewAge;
    private final int totalWeight;

    public RandomFermentingContainerProcessor(
            float chance,
            List<BrewEntry> entries,
            DwarvenBrewAge brewAge
    ) {
        if (chance < 0.0F || chance > 1.0F) {
            throw new IllegalArgumentException(
                    "chance must be between 0.0 and 1.0"
            );
        }

        this.chance = chance;
        this.entries = List.copyOf(
                Objects.requireNonNull(
                        entries,
                        JolCraftDictionary.ENTRIES
                )
        );
        this.brewAge = Objects.requireNonNull(
                brewAge,
                BREW_AGE_KEY
        );
        this.totalWeight = calculateTotalWeight(
                this.entries
        );
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos blockPos,
            BlockPos relativePos,
            StructureTemplate.StructureBlockInfo original,
            StructureTemplate.StructureBlockInfo current,
            StructurePlaceSettings settings
    ) {
        ContainerType containerType = ContainerType.fromState(
                current.state()
        );

        if (containerType == null) {
            return current;
        }

        var random = settings.getRandom(
                current.pos()
        );

        if (random.nextFloat() >= chance) {
            return current;
        }

        BrewEntry selected = selectEntry(
                random.nextInt(totalWeight)
        );

        FluidStack brew = createBrew(
                selected,
                containerType == ContainerType.BARREL
                        ? brewAge
                        : DwarvenBrewAge.FRESH
        );

        return new StructureTemplate.StructureBlockInfo(
                current.pos(),
                containerType.replacementState(current.state()),
                createBrewNbt(
                        level,
                        brew
                )
        );
    }

    private BrewEntry selectEntry(int roll) {
        int remaining = roll;

        for (BrewEntry entry : entries) {
            remaining -= entry.weight();

            if (remaining < 0) {
                return entry;
            }
        }

        throw new IllegalStateException(
                "Failed to select weighted brew entry"
        );
    }

    private static FluidStack createBrew(
            BrewEntry entry,
            DwarvenBrewAge age
    ) {
        List<MobEffectInstance> effects = new ArrayList<>(
                entry.effects().size()
        );

        for (MobEffectInstance effect : entry.effects()) {
            effects.add(
                    new MobEffectInstance(effect)
            );
        }

        return DwarvenBrewFluidHelper.createDwarvenBrew(
                FluidType.BUCKET_VOLUME,
                BrewingColors.DWARVEN_BREW,
                age.thresholdTicks(),
                age.ordinal() > DwarvenBrewAge.AGED.ordinal()
                        ? age
                        : DwarvenBrewAge.AGED,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED,
                new PotionContents(
                        Optional.empty(),
                        Optional.empty(),
                        List.copyOf(effects)
                )
        );
    }

    private static CompoundTag createBrewNbt(
            LevelReader level,
            FluidStack brew
    ) {
        FluidTank tank = new FluidTank(
                FluidType.BUCKET_VOLUME
        );

        tank.setFluid(brew);

        CompoundTag nbt = new CompoundTag();

        nbt.put(
                NBT_BREW_TANK,
                tank.writeToNBT(
                        level.registryAccess(),
                        new CompoundTag()
                )
        );

        return nbt;
    }

    private static DataResult<List<BrewEntry>> validateEntries(
            List<BrewEntry> entries
    ) {
        if (entries.isEmpty()) {
            return DataResult.error(
                    () -> "Fermenting container processor requires at least one brew entry"
            );
        }

        long totalWeight = 0L;

        for (BrewEntry entry : entries) {
            totalWeight += entry.weight();

            if (totalWeight > Integer.MAX_VALUE) {
                return DataResult.error(
                        () -> "Combined brew entry weight exceeds " + Integer.MAX_VALUE
                );
            }
        }

        return DataResult.success(
                List.copyOf(entries)
        );
    }

    private static int calculateTotalWeight(
            List<BrewEntry> entries
    ) {
        if (entries.isEmpty()) {
            throw new IllegalArgumentException(
                    "Fermenting container processor requires at least one brew entry"
            );
        }

        long total = 0L;

        for (BrewEntry entry : entries) {
            total += entry.weight();

            if (total > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Combined brew entry weight exceeds " + Integer.MAX_VALUE
                );
            }
        }

        return (int) total;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return JolCraftProcessors.RANDOM_FERMENTING_CONTAINER.type().get();
    }

    public record BrewEntry(
            List<MobEffectInstance> effects,
            int weight
    ) {

        private static final Codec<List<MobEffectInstance>> EFFECTS_CODEC =
                MobEffectInstance.CODEC.listOf().validate(
                        effects -> effects.isEmpty()
                                ? DataResult.error(
                                () -> "Brew entry requires at least one effect"
                        )
                                : DataResult.success(
                                List.copyOf(effects)
                        )
                );

        public static final Codec<BrewEntry> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        EFFECTS_CODEC
                                .fieldOf(
                                        JolCraftStrings.plural(
                                                JolCraftDictionary.EFFECT
                                        )
                                )
                                .forGetter(BrewEntry::effects),
                        Codec.intRange(1, Integer.MAX_VALUE)
                                .optionalFieldOf(
                                        JolCraftDictionary.WEIGHT,
                                        1
                                )
                                .forGetter(BrewEntry::weight)
                ).apply(instance, BrewEntry::new));

        public BrewEntry {
            effects = List.copyOf(
                    Objects.requireNonNull(
                            effects,
                            JolCraftStrings.plural(
                                    JolCraftDictionary.EFFECT
                            )
                    )
            );

            if (effects.isEmpty()) {
                throw new IllegalArgumentException(
                        "Brew entry requires at least one effect"
                );
            }

            if (weight <= 0) {
                throw new IllegalArgumentException(
                        "Brew entry weight must be positive"
                );
            }
        }
    }

    private enum ContainerType {
        BARREL {
            @Override
            BlockState replacementState(BlockState source) {
                return JolCraftBlocks.FERMENTING_BARREL
                        .get()
                        .defaultBlockState()
                        .setValue(
                                FermentingBarrelBlock.FACING,
                                source.getValue(
                                        BarrelBlock.FACING
                                )
                        );
            }
        },
        CAULDRON {
            @Override
            BlockState replacementState(BlockState source) {
                return JolCraftBlocks.FERMENTING_CAULDRON
                        .get()
                        .defaultBlockState();
            }
        };

        abstract BlockState replacementState(BlockState source);

        private static @Nullable ContainerType fromState(
                BlockState state
        ) {
            if (state.is(Blocks.BARREL)) {
                return BARREL;
            }

            if (state.is(Blocks.CAULDRON)
                    || state.is(Blocks.WATER_CAULDRON)
                    || state.is(Blocks.LAVA_CAULDRON)
                    || state.is(Blocks.POWDER_SNOW_CAULDRON)) {
                return CAULDRON;
            }

            return null;
        }
    }
}
