package net.sievert.jolcraft.integration.jei.util.recipe;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.world.recipe.base.condition.custom.InputItemCondition;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ItemInputJeiTranslator {

    private ItemInputJeiTranslator() {
    }

    public static @NotNull List<ItemStack> translate(
            @NotNull ItemInput input
    ) {
        if (!(input.condition() instanceof InputItemCondition(ItemPredicate predicate))) {
            throw new IllegalArgumentException(
                    "Unsupported item input condition for JEI translation: "
                            + input.condition()
                            .getClass()
                            .getName()
            );
        }

        List<ItemStack> matches =
                new ArrayList<>();

        Optional<HolderSet<Item>> restrictedItems =
                predicate.items();

        if (restrictedItems.isPresent()) {
            for (Holder<Item> holder : restrictedItems.get()) {
                addMatchingStack(
                        matches,
                        predicate,
                        holder.value()
                );
            }
        } else {
            for (Item item : BuiltInRegistries.ITEM) {
                addMatchingStack(
                        matches,
                        predicate,
                        item
                );
            }
        }

        if (matches.isEmpty()) {
            throw new IllegalArgumentException(
                    "Item input produced no displayable JEI ingredients from default item stacks; "
                            + "component-specific predicates require an explicit preview stack"
            );
        }

        return List.copyOf(
                matches
        );
    }

    private static void addMatchingStack(
            @NotNull List<ItemStack> matches,
            @NotNull ItemPredicate predicate,
            @NotNull Item item
    ) {
        if (item == Items.AIR) {
            return;
        }

        ItemStack stack =
                item.getDefaultInstance();

        int maxCount =
                Math.max(
                        1,
                        stack.getMaxStackSize()
                );

        for (
                int count = 1;
                count <= maxCount;
                count++
        ) {
            stack.setCount(
                    count
            );

            if (!predicate.test(
                    stack
            )) {
                continue;
            }

            matches.add(
                    stack.copy()
            );

            return;
        }
    }
}