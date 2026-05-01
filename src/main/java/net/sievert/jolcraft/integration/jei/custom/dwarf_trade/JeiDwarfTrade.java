package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipeInput;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.ItemInput;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record JeiDwarfTrade(
        DwarfTradeRecipe recipe,
        DeferredItem<Item> spawnEgg
) {

    public DwarfProfession profession() {
        return recipe.profession();
    }

    public @Nullable DwarfMerchantData.Level level() {
        return recipe.merchantLevel();
    }

    public ItemStack inputAExample() {
        WorldContext ctx = resolveJeiWorldContext();
        if (ctx == null) {
            return ItemStack.EMPTY;
        }

        return normalizeForJei(materializeInput(recipe.costA(), ctx, previewRandom()));
    }

    public @Nullable ItemStack inputBExample() {
        ItemInput costB = recipe.costB();
        if (costB == null) {
            return null;
        }

        WorldContext ctx = resolveJeiWorldContext();
        if (ctx == null) {
            return null;
        }

        ItemStack stack = materializeInput(costB, ctx, previewRandom());
        return stack.isEmpty() ? null : normalizeForJei(stack);
    }

    public boolean costAItemIs(TagKey<Item> tag) {
        WorldContext ctx = resolveJeiWorldContext();
        if (ctx == null) {
            return false;
        }
        return inputUsesTag(recipe.costA(), ctx, tag);
    }

    public boolean costBItemIs(TagKey<Item> tag) {
        ItemInput costB = recipe.costB();
        if (costB == null) {
            return false;
        }

        WorldContext ctx = resolveJeiWorldContext();
        if (ctx == null) {
            return false;
        }

        return inputUsesTag(costB, ctx, tag);
    }

    public ItemStack outputExample() {
        WorldContext ctx = resolveJeiWorldContext();
        if (ctx == null) {
            return ItemStack.EMPTY;
        }

        RandomSource random = previewRandom();
        DwarfTradeRecipeInput input = buildPreviewInput(ctx, random);

        List<Output> generated = recipe.result().generateResolved(ctx, input);
        if (generated.isEmpty()) {
            return ItemStack.EMPTY;
        }

        for (Output output : generated) {
            if (output instanceof Output.Items items) {
                List<ItemStack> stacks = items.stacksSafe();
                if (!stacks.isEmpty()) {
                    ItemStack first = stacks.getFirst();
                    if (!first.isEmpty()) {
                        return normalizeForJei(first);
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public IntRange inputAmountA() {
        return recipe.costA().count();
    }

    public @Nullable IntRange inputAmountB() {
        ItemInput costB = recipe.costB();
        if (costB == null) {
            return null;
        }

        return costB.count();
    }

    public IntRange outputAmount() {
        ItemStack preview = outputExample();
        return IntRange.fixed(Math.max(1, preview.getCount()));
    }

    private static ItemStack materializeInput(@Nullable ItemInput input, WorldContext ctx, RandomSource random) {
        if (input == null) {
            return ItemStack.EMPTY;
        }

        Holder<Item> holder = resolvePreviewItem(input, ctx);
        if (holder == null) {
            return ItemStack.EMPTY;
        }

        int rolled = input.count().roll(random);
        if (rolled < 1) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(holder.value(), rolled);
        return input.matches(ctx, stack) ? stack : ItemStack.EMPTY;
    }

    private static boolean inputUsesTag(@Nullable ItemInput input, WorldContext ctx, TagKey<Item> wantedTag) {
        if (input == null) {
            return false;
        }

        Optional<Holder<Item>> concrete = input.singleConcrete(Registries.ITEM);
        if (concrete.isPresent()) {
            return concrete.get().is(wantedTag);
        }

        for (var introspection : input.introspections()) {
            if (!Registries.ITEM.equals(introspection.registryKey())) {
                continue;
            }

            var tagOpt = introspection.singleTagOpt();
            if (tagOpt.isEmpty()) {
                continue;
            }

            @SuppressWarnings("unchecked")
            TagKey<Item> actualTag = (TagKey<Item>) tagOpt.get();

            if (actualTag.equals(wantedTag)) {
                return true;
            }
        }

        return false;
    }

    private static @Nullable Holder<Item> resolvePreviewItem(@Nullable ItemInput input, WorldContext ctx) {
        if (input == null) {
            return null;
        }

        Optional<Holder<Item>> concrete = input.singleConcrete(Registries.ITEM);
        if (concrete.isPresent()) {
            return concrete.get();
        }

        var lookup = ctx.level().registryAccess().lookupOrThrow(Registries.ITEM);

        for (var introspection : input.introspections()) {
            if (!Registries.ITEM.equals(introspection.registryKey())) {
                continue;
            }

            var tagOpt = introspection.singleTagOpt();
            if (tagOpt.isEmpty()) {
                continue;
            }

            @SuppressWarnings("unchecked")
            TagKey<Item> tag = (TagKey<Item>) tagOpt.get();

            var namedOpt = lookup.get(tag);
            if (namedOpt.isEmpty()) {
                continue;
            }

            var named = namedOpt.get();
            if (named.size() == 0) {
                continue;
            }

            return named.get(0);
        }

        return null;
    }

    private DwarfTradeRecipeInput buildPreviewInput(WorldContext ctx, RandomSource random) {
        ItemStack costA = materializeInput(recipe.costA(), ctx, random);

        ItemStack costB = ItemStack.EMPTY;
        if (recipe.costB() != null) {
            costB = materializeInput(recipe.costB(), ctx, random);
        }

        DwarfMerchantData.Level previewLevel = recipe.merchantLevel() != null
                ? recipe.merchantLevel()
                : DwarfMerchantData.Level.NOVICE;

        return new DwarfTradeRecipeInput(
                ctx,
                recipe.profession(),
                previewLevel,
                costA,
                costB
        );
    }

    private static ItemStack normalizeForJei(ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    private static RandomSource previewRandom() {
        return RandomSource.create(0xDEADBEEFL);
    }

    private static @Nullable WorldContext resolveJeiWorldContext() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ServerLevel serverLevel =
                mc.getSingleplayerServer() != null ? mc.getSingleplayerServer().overworld() : null;

        if (serverLevel == null || player == null) {
            return null;
        }

        return new WorldContext(serverLevel, player, player);
    }
}