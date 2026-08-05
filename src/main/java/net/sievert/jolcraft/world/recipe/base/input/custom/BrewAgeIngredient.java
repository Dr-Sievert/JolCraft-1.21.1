package net.sievert.jolcraft.world.recipe.base.input.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
import net.sievert.jolcraft.world.recipe.base.input.JolCraftIngredientTypes;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record BrewAgeIngredient(
        DwarvenBrewAge age
) implements ICustomIngredient {

    public static final MapCodec<BrewAgeIngredient> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            DwarvenBrewAge.CODEC.fieldOf(JolCraftDictionary.AGE)
                                    .forGetter(BrewAgeIngredient::age)
                    ).apply(
                            instance,
                            BrewAgeIngredient::new
                    )
            );

    @Override
    public boolean test(
            ItemStack stack
    ) {
        if (!stack.is(JolCraftItems.DWARVEN_BREW.get())) {
            return false;
        }

        return DwarvenBrewFluidHelper.findContainedBrew(stack)
                .map(DwarvenBrewFluidHelper::getAge)
                .map(DwarvenBrewAge::fromTicks)
                .filter(age::equals)
                .isPresent();
    }

    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return Stream.of(
                JolCraftBrewingItems.createDwarvenBrewStack(age)
        );
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NotNull IngredientType<?> getType() {
        return JolCraftIngredientTypes.BREW_AGE.get();
    }
}