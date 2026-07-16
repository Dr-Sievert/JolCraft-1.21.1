package net.sievert.jolcraft.world.recipe.condition.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.condition.JolCraftRecipeConditionTypes;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record InputItemCondition(ItemPredicate predicate)
        implements LootItemCondition {

    public static final MapCodec<InputItemCondition> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ItemPredicate.CODEC
                            .fieldOf(JolCraftDictionary.PREDICATE)
                            .forGetter(InputItemCondition::predicate)
            ).apply(instance, InputItemCondition::new));

    @Override
    public @NotNull LootItemConditionType getType() {
        return JolCraftRecipeConditionTypes.INPUT_ITEM.get();
    }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(JolCraftRecipeContextParams.INPUT_ITEM);
    }

    @Override
    public boolean test(LootContext context) {
        ItemStack input = context.getParamOrNull(
                JolCraftRecipeContextParams.INPUT_ITEM
        );

        return input != null && predicate.test(input);
    }

    public static LootItemCondition.Builder inputMatches(
            ItemPredicate.Builder predicate
    ) {
        return () -> new InputItemCondition(predicate.build());
    }

    public static LootItemCondition.Builder inputMatches(
            ItemPredicate predicate
    ) {
        return () -> new InputItemCondition(predicate);
    }
}