package net.sievert.jolcraft.world.worldgen.test.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.worldgen.test.JolCraftRuleTests;
import org.jetbrains.annotations.NotNull;

public class RandomNotAirRuleTest extends RuleTest {

    public static final MapCodec<RandomNotAirRuleTest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F)
                    .fieldOf(JolCraftParameterIds.CHANCE)
                    .forGetter(test -> test.probability)
    ).apply(instance, RandomNotAirRuleTest::new));

    private final float probability;

    public RandomNotAirRuleTest(float probability) {
        this.probability = probability;
    }

    @Override
    public boolean test(BlockState state, @NotNull RandomSource random) {
        return !state.isAir() && random.nextFloat() < probability;
    }

    @Override
    protected @NotNull RuleTestType<?> getType() {
        return JolCraftRuleTests.RANDOM_NOT_AIR.get();
    }
}