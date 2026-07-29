package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class JeiInfoPageRecipe {

    private final @Nullable ItemStack focusStack;
    private final @Nullable FluidStack focusFluidStack;

    private final @Nullable TagKey<Item> focusTag;
    private final @Nullable TagKey<Block> focusBlockTag;
    private final @Nullable TagKey<Fluid> focusFluidTag;

    private final @Nullable List<ItemStack> groupStacks;

    private final Component content;
    private final @Nullable Consumer<ItemStack> stackCustomizer;
    private final @Nullable String type;

    /* ---------------------------------------------------------------------
     * Constructors
     * ------------------------------------------------------------------ */

    public JeiInfoPageRecipe(
            ItemStack focusStack,
            Component content
    ) {
        this(
                focusStack,
                null,
                content,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public JeiInfoPageRecipe(
            ItemStack focusStack,
            Component content,
            @Nullable Consumer<ItemStack> stackCustomizer
    ) {
        this(
                focusStack,
                null,
                content,
                stackCustomizer,
                null,
                null,
                null,
                null,
                null
        );
    }

    public JeiInfoPageRecipe(
            Block focusBlock,
            Component content
    ) {
        this(
                new ItemStack(focusBlock.asItem()),
                content
        );
    }

    public JeiInfoPageRecipe(
            Fluid focusFluid,
            Component content
    ) {
        this(
                new FluidStack(
                        focusFluid,
                        FluidType.BUCKET_VOLUME
                ),
                content
        );
    }

    public JeiInfoPageRecipe(
            FluidStack focusFluidStack,
            Component content
    ) {
        this(
                null,
                focusFluidStack,
                content,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public JeiInfoPageRecipe(
            Block focusBlock,
            Fluid focusFluid,
            Component content
    ) {
        this(
                new ItemStack(focusBlock.asItem()),
                new FluidStack(
                        focusFluid,
                        FluidType.BUCKET_VOLUME
                ),
                content,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public JeiInfoPageRecipe(
            Block focusBlock,
            FluidStack focusFluidStack,
            Component content
    ) {
        this(
                new ItemStack(focusBlock.asItem()),
                focusFluidStack,
                content,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public JeiInfoPageRecipe(
            ItemStack focusStack,
            FluidStack focusFluidStack,
            Component content
    ) {
        this(
                focusStack,
                focusFluidStack,
                content,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public JeiInfoPageRecipe(
            List<ItemStack> groupStacks,
            Component content,
            @Nullable String type
    ) {
        this(
                null,
                null,
                content,
                null,
                groupStacks,
                type,
                null,
                null,
                null
        );
    }

    public JeiInfoPageRecipe(
            TagKey<Item> focusTag,
            Component content
    ) {
        this(
                null,
                null,
                content,
                null,
                null,
                null,
                focusTag,
                null,
                null
        );
    }

    public static JeiInfoPageRecipe fromBlockTag(
            TagKey<Block> focusBlockTag,
            Component content
    ) {
        return new JeiInfoPageRecipe(
                null,
                null,
                content,
                null,
                null,
                null,
                null,
                focusBlockTag,
                null
        );
    }

    public static JeiInfoPageRecipe fromFluidTag(
            TagKey<Fluid> focusFluidTag,
            Component content
    ) {
        return new JeiInfoPageRecipe(
                null,
                null,
                content,
                null,
                null,
                null,
                null,
                null,
                focusFluidTag
        );
    }

    private JeiInfoPageRecipe(
            @Nullable ItemStack focusStack,
            @Nullable FluidStack focusFluidStack,
            Component content,
            @Nullable Consumer<ItemStack> stackCustomizer,
            @Nullable List<ItemStack> groupStacks,
            @Nullable String type,
            @Nullable TagKey<Item> focusTag,
            @Nullable TagKey<Block> focusBlockTag,
            @Nullable TagKey<Fluid> focusFluidTag
    ) {
        this.focusStack = focusStack;
        this.focusFluidStack = focusFluidStack;
        this.content = content;
        this.stackCustomizer = stackCustomizer;
        this.groupStacks = groupStacks;
        this.type = type;
        this.focusTag = focusTag;
        this.focusBlockTag = focusBlockTag;
        this.focusFluidTag = focusFluidTag;
    }

    /* ---------------------------------------------------------------------
     * Kind
     * ------------------------------------------------------------------ */

    public boolean isTag() {
        return focusTag != null;
    }

    public boolean isBlockTag() {
        return focusBlockTag != null;
    }

    public boolean isFluidTag() {
        return focusFluidTag != null;
    }

    public boolean isFluid() {
        return focusFluidStack != null
                && !focusFluidStack.isEmpty();
    }

    public boolean isGroup() {
        return groupStacks != null
                && !groupStacks.isEmpty();
    }

    /* ---------------------------------------------------------------------
     * Access
     * ------------------------------------------------------------------ */

    public @Nullable TagKey<Item> getFocusTag() {
        return focusTag;
    }

    public @Nullable TagKey<Block> getFocusBlockTag() {
        return focusBlockTag;
    }

    public @Nullable TagKey<Fluid> getFocusFluidTag() {
        return focusFluidTag;
    }

    public @Nullable ItemStack getFocusStack() {
        if (focusStack == null) {
            return null;
        }

        ItemStack copy = focusStack.copy();

        if (stackCustomizer != null) {
            stackCustomizer.accept(copy);
        }

        return copy;
    }

    public @Nullable FluidStack getFocusFluidStack() {
        if (focusFluidStack == null) {
            return null;
        }

        return focusFluidStack.copy();
    }

    public Component getContent() {
        return content;
    }

    public @Nullable String getType() {
        return type;
    }

    public List<ItemStack> getGroupStacks() {
        return groupStacks != null
                ? groupStacks
                : Collections.emptyList();
    }

    public List<ItemStack> getGroupStacks(
            RegistryAccess registryAccess
    ) {
        if (groupStacks != null) {
            return groupStacks;
        }

        if (focusBlockTag == null) {
            return Collections.emptyList();
        }

        return blocksToItemStacks(
                registryAccess,
                focusBlockTag
        );
    }

    public List<FluidStack> getFluidStacks(
            RegistryAccess registryAccess
    ) {
        if (focusFluidStack != null) {
            return List.of(
                    focusFluidStack.copy()
            );
        }

        if (focusFluidTag == null) {
            return Collections.emptyList();
        }

        return fluidsToFluidStacks(
                registryAccess,
                focusFluidTag
        );
    }

    /* ---------------------------------------------------------------------
     * Helpers
     * ------------------------------------------------------------------ */

    public static List<ItemStack> blocksToItemStacks(
            RegistryAccess registryAccess,
            TagKey<Block> blockTag
    ) {
        var blocks =
                registryAccess.lookupOrThrow(
                        Registries.BLOCK
                );

        var named =
                blocks.get(blockTag)
                        .orElse(null);

        List<ItemStack> stacks =
                new ArrayList<>();

        if (named == null) {
            return stacks;
        }

        for (var holder : named) {
            Item asItem =
                    holder.value()
                            .asItem();

            ItemStack stack =
                    new ItemStack(asItem);

            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        return stacks;
    }

    public static List<FluidStack> fluidsToFluidStacks(
            RegistryAccess registryAccess,
            TagKey<Fluid> fluidTag
    ) {
        var fluids =
                registryAccess.lookupOrThrow(
                        Registries.FLUID
                );

        var named =
                fluids.get(fluidTag)
                        .orElse(null);

        List<FluidStack> stacks =
                new ArrayList<>();

        if (named == null) {
            return stacks;
        }

        for (var holder : named) {
            Fluid fluid =
                    holder.value();

            FluidStack stack =
                    new FluidStack(
                            fluid,
                            FluidType.BUCKET_VOLUME
                    );

            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        return stacks;
    }
}