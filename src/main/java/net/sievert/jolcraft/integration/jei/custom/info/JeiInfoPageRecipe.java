package net.sievert.jolcraft.integration.jei.custom.info;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class JeiInfoPageRecipe {

    private final @Nullable ItemStack focusStack;
    private final @Nullable TagKey<Item> focusTag;
    private final @Nullable TagKey<Block> focusBlockTag;
    private final @Nullable List<ItemStack> groupStacks;

    private final Component content;
    private final @Nullable Consumer<ItemStack> stackCustomizer;
    private final @Nullable String type;

    /* ---------------------------------------------------------------------
     * Constructors
     * ------------------------------------------------------------------ */

    public JeiInfoPageRecipe(ItemStack focusStack, Component content) {
        this(focusStack, content, null);
    }

    public JeiInfoPageRecipe(ItemStack focusStack, Component content, @Nullable Consumer<ItemStack> stackCustomizer) {
        this(focusStack, content, stackCustomizer, null, null, null, null);
    }

    public JeiInfoPageRecipe(List<ItemStack> groupStacks, Component content, @Nullable String type) {
        this(null, content, null, groupStacks, type, null, null);
    }

    public JeiInfoPageRecipe(TagKey<Item> focusTag, Component content) {
        this(null, content, null, null, null, focusTag, null);
    }

    public static JeiInfoPageRecipe fromBlockTag(TagKey<Block> focusBlockTag, Component content) {
        return new JeiInfoPageRecipe(null, content, null, null, null, null, focusBlockTag);
    }

    private JeiInfoPageRecipe(
            @Nullable ItemStack focusStack,
            Component content,
            @Nullable Consumer<ItemStack> stackCustomizer,
            @Nullable List<ItemStack> groupStacks,
            @Nullable String type,
            @Nullable TagKey<Item> focusTag,
            @Nullable TagKey<Block> focusBlockTag
    ) {
        this.focusStack = focusStack;
        this.content = content;
        this.stackCustomizer = stackCustomizer;
        this.groupStacks = groupStacks;
        this.type = type;
        this.focusTag = focusTag;
        this.focusBlockTag = focusBlockTag;
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

    public boolean isGroup() {
        return groupStacks != null && !groupStacks.isEmpty();
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

    public @Nullable ItemStack getFocusStack() {
        if (focusStack == null) return null;

        ItemStack copy = focusStack.copy();
        if (stackCustomizer != null) {
            stackCustomizer.accept(copy);
        }
        return copy;
    }

    public Component getContent() {
        return content;
    }

    public @Nullable String getType() {
        return type;
    }

    public List<ItemStack> getGroupStacks() {
        return groupStacks != null ? groupStacks : Collections.emptyList();
    }

    public List<ItemStack> getGroupStacks(RegistryAccess registryAccess) {
        if (groupStacks != null) return groupStacks;
        if (focusBlockTag == null) return Collections.emptyList();
        return blocksToItemStacks(registryAccess, focusBlockTag);
    }

    /* ---------------------------------------------------------------------
     * Helpers
     * ------------------------------------------------------------------ */

    public static List<ItemStack> blocksToItemStacks(RegistryAccess registryAccess, TagKey<Block> blockTag) {
        var blocks = registryAccess.lookupOrThrow(Registries.BLOCK);
        var named = blocks.get(blockTag).orElse(null);

        List<ItemStack> stacks = new ArrayList<>();
        if (named == null) {
            return stacks;
        }

        for (var holder : named) {
            Item asItem = holder.value().asItem();
            stacks.add(new ItemStack(asItem));
        }

        return stacks;
    }
}