package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class JeiInfoPageRecipe {
    private final ItemStack focusStack;
    private final TagKey<Item> focusTag;
    private final List<ItemStack> groupStacks;
    private final Component content;
    private final Consumer<ItemStack> stackCustomizer;
    private final String type;

    public JeiInfoPageRecipe(ItemStack focusStack, Component content) {
        this(focusStack, content, null, null, null, null);
    }

    public JeiInfoPageRecipe(ItemStack focusStack, Component content, Consumer<ItemStack> stackCustomizer) {
        this(focusStack, content, stackCustomizer, null, null, null);
    }

    public JeiInfoPageRecipe(List<ItemStack> groupStacks, Component content, String type) {
        this(null, content, null, groupStacks, type, null);
    }

    public JeiInfoPageRecipe(TagKey<Item> focusTag, Component content) {
        this(null, content, null, null, null, focusTag);
    }

    private JeiInfoPageRecipe(ItemStack focusStack, Component content, Consumer<ItemStack> stackCustomizer,
                              List<ItemStack> groupStacks, String type, TagKey<Item> focusTag) {
        this.focusStack = focusStack;
        this.content = content;
        this.stackCustomizer = stackCustomizer;
        this.groupStacks = groupStacks;
        this.type = type;
        this.focusTag = focusTag;
    }

    public boolean isTag() { return focusTag != null; }
    public boolean isGroup() { return groupStacks != null && !groupStacks.isEmpty(); }

    public TagKey<Item> getFocusTag() { return focusTag; }

    public ItemStack getFocusStack() {
        if (focusStack == null) return null;
        ItemStack copy = focusStack.copy();
        if (stackCustomizer != null) stackCustomizer.accept(copy);
        return copy;
    }

    public List<ItemStack> getGroupStacks() { return groupStacks != null ? groupStacks : Collections.emptyList(); }

    public Component getContent() { return content; }

    public String getType() { return type; }

    public static JeiInfoPageRecipe fromBlockTag(TagKey<Block> focusBlockTag, Component content) {
        return new JeiInfoPageRecipe(null, content, null, blocksToItemStacks(focusBlockTag), null, null);
    }

    public static List<ItemStack> blocksToItemStacks(TagKey<Block> blockTag) {
        List<ItemStack> stacks = new java.util.ArrayList<>();
        for (var holder : BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag)) {
            stacks.add(new ItemStack(holder.value().asItem()));
        }
        return stacks;
    }

}
