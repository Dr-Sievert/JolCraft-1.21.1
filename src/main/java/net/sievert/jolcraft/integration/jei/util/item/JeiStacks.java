package net.sievert.jolcraft.integration.jei.util.item;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class JeiStacks {

    private JeiStacks() {
    }

    public static @NotNull List<ItemStack> copyRequired(
            @NotNull List<ItemStack> stacks,
            @NotNull String name
    ) {
        Objects.requireNonNull(
                stacks,
                name
        );

        List<ItemStack> copies =
                stacks.stream()
                        .map(ItemStack::copy)
                        .toList();

        if (copies.isEmpty()) {
            throw new IllegalArgumentException(
                    name
                            + " must contain at least one stack"
            );
        }

        return copies;
    }

    public static @NotNull ItemStack copyRequired(
            @NotNull ItemStack stack,
            @NotNull String name
    ) {
        Objects.requireNonNull(
                stack,
                name
        );

        if (stack.isEmpty()) {
            throw new IllegalArgumentException(
                    name
                            + " must not be empty"
            );
        }

        return stack.copy();
    }
}
