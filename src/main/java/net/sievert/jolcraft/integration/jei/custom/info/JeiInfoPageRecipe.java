package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class JeiInfoPageRecipe {
    private final ItemStack focusStack;
    private final TagKey<Item> focusTag;
    private final TagKey<Block> focusBlockTag;
    private final List<ItemStack> groupStacks;
    private final Component content;
    private final Consumer<ItemStack> stackCustomizer;
    private final String type;

    public JeiInfoPageRecipe(ItemStack focusStack, Component content) {
        this(focusStack, content, null, null, null, null, null);
    }

    public JeiInfoPageRecipe(ItemStack focusStack, Component content, Consumer<ItemStack> stackCustomizer) {
        this(focusStack, content, stackCustomizer, null, null, null, null);
    }

    public JeiInfoPageRecipe(List<ItemStack> groupStacks, Component content, String type) {
        this(null, content, null, groupStacks, type, null, null);
    }

    public JeiInfoPageRecipe(TagKey<Item> focusTag, Component content) {
        this(null, content, null, null, null, focusTag, null);
    }

    private JeiInfoPageRecipe(
            ItemStack focusStack,
            Component content,
            Consumer<ItemStack> stackCustomizer,
            List<ItemStack> groupStacks,
            String type,
            TagKey<Item> focusTag,
            TagKey<Block> focusBlockTag
    ) {
        this.focusStack = focusStack;
        this.content = content;
        this.stackCustomizer = stackCustomizer;
        this.groupStacks = groupStacks;
        this.type = type;
        this.focusTag = focusTag;
        this.focusBlockTag = focusBlockTag;
    }

    public boolean isTag() { return focusTag != null; }
    public boolean isBlockTag() { return focusBlockTag != null; } // NEW
    public boolean isGroup() { return groupStacks != null && !groupStacks.isEmpty(); }

    public TagKey<Item> getFocusTag() { return focusTag; }
    public TagKey<Block> getFocusBlockTag() { return focusBlockTag; } // NEW

    public ItemStack getFocusStack() {
        if (focusStack == null) return null;
        ItemStack copy = focusStack.copy();
        if (stackCustomizer != null) stackCustomizer.accept(copy);
        return copy;
    }

    public List<ItemStack> getGroupStacks() {
        return groupStacks != null ? groupStacks : Collections.emptyList();
    }

    /**
     * Returns resolved group stacks. If this recipe is backed by a block tag,
     * it will be expanded using the provided registry access.
     */
    public List<ItemStack> getGroupStacks(RegistryAccess registryAccess) {
        if (groupStacks != null) return groupStacks;
        if (focusBlockTag == null) return Collections.emptyList();
        return blocksToItemStacks(registryAccess, focusBlockTag);
    }

    public Component getContent() { return content; }
    public String getType() { return type; }

    public static JeiInfoPageRecipe fromBlockTag(TagKey<Block> focusBlockTag, Component content) {
        return new JeiInfoPageRecipe(
                null,
                content,
                null,
                null,
                null,
                null,
                focusBlockTag
        );
    }

    public static List<ItemStack> blocksToItemStacks(RegistryAccess registryAccess, TagKey<Block> blockTag) {
        Registry<Block> blocks = registryAccess.lookupOrThrow(Registries.BLOCK);

        List<ItemStack> stacks = new ArrayList<>();
        for (var holder : blocks.getTagOrEmpty(blockTag)) {
            stacks.add(new ItemStack(holder.value().asItem()));
        }
        return stacks;
    }
}