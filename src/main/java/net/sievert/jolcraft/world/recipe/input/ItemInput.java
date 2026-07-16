package net.sievert.jolcraft.world.recipe.input;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.sievert.jolcraft.world.recipe.condition.custom.InputItemCondition;

public record ItemInput(
        LootItemCondition condition
) {

    public static final Codec<ItemInput> CODEC =
            LootItemCondition.DIRECT_CODEC.xmap(
                    ItemInput::new,
                    ItemInput::condition
            );

    public static ItemInput item(ItemLike... items) {
        return predicate(
                ItemPredicate.Builder.item()
                        .of(items)
        );
    }

    public static ItemInput tag(TagKey<Item> tag) {
        return predicate(
                ItemPredicate.Builder.item()
                        .of(tag)
        );
    }

    public static ItemInput predicate(
            ItemPredicate.Builder predicate
    ) {
        return new ItemInput(
                InputItemCondition.inputMatches(predicate).build()
        );
    }

    public static ItemInput predicate(
            ItemPredicate predicate
    ) {
        return new ItemInput(
                InputItemCondition.inputMatches(predicate).build()
        );
    }
}