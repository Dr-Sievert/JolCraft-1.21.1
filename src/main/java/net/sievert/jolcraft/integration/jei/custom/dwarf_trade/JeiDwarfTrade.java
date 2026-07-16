package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeCost;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipeInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record JeiDwarfTrade(
        DwarfTradeRecipe recipe,
        DeferredItem<Item> spawnEgg
) {

    private static final LootContextParamSet PREVIEW_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    /**
     * JEI display-only count range.
     *
     * This replaces the deleted recipe-system IntRange without adding that old
     * parameter system back into the migrated recipe architecture.
     */
    public record AmountRange(
            int min,
            int max
    ) {

        public AmountRange {
            int normalizedMin =
                    Math.max(
                            1,
                            Math.min(
                                    min,
                                    max
                            )
                    );

            int normalizedMax =
                    Math.max(
                            normalizedMin,
                            Math.max(
                                    min,
                                    max
                            )
                    );

            min =
                    normalizedMin;

            max =
                    normalizedMax;
        }

        public static @NotNull AmountRange fixed(
                int value
        ) {
            int normalized =
                    Math.max(
                            1,
                            value
                    );

            return new AmountRange(
                    normalized,
                    normalized
            );
        }

        public boolean fixed() {
            return min == max;
        }
    }

    public @NotNull DwarfProfession profession() {
        return recipe.profession();
    }

    public @Nullable DwarfMerchantData.Level level() {
        return recipe.merchantLevel();
    }

    public @NotNull ItemStack inputAExample() {
        LootContext context =
                resolveJeiLootContext();

        return normalizeForJei(
                materializeInput(
                        recipe.costA(),
                        context
                )
        );
    }

    public @Nullable ItemStack inputBExample() {
        TradeCost costB =
                recipe.costB();

        if (costB == null) {
            return null;
        }

        LootContext context =
                resolveJeiLootContext();

        ItemStack stack =
                materializeInput(
                        costB,
                        context
                );

        if (stack.isEmpty()) {
            return null;
        }

        return normalizeForJei(
                stack
        );
    }

    public boolean costAItemIs(
            @NotNull TagKey<Item> tag
    ) {
        return recipe.costA()
                .contains(tag);
    }

    public boolean costBItemIs(
            @NotNull TagKey<Item> tag
    ) {
        TradeCost costB =
                recipe.costB();

        return costB != null
                && costB.contains(tag);
    }

    public @NotNull ItemStack outputExample() {
        LootContext context =
                resolveJeiLootContext();

        if (context == null) {
            return ItemStack.EMPTY;
        }

        DwarfTradeRecipeInput input =
                buildPreviewInput(
                        context
                );

        if (input.costA().isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack output =
                recipe.resolveResult(
                        context,
                        input
                );

        return normalizeForJei(
                output
        );
    }

    public @NotNull AmountRange inputAmountA() {
        return amountRange(
                recipe.costA()
                        .count(),
                resolveJeiLootContext()
        );
    }

    public @Nullable AmountRange inputAmountB() {
        TradeCost costB =
                recipe.costB();

        if (costB == null) {
            return null;
        }

        return amountRange(
                costB.count(),
                resolveJeiLootContext()
        );
    }

    /**
     * The migrated ItemOutput can contain arbitrary loot functions and hooks,
     * so its general count range cannot safely be inferred structurally.
     *
     * Preserve the old JEI behavior by displaying the count of the generated
     * preview stack.
     */
    public @NotNull AmountRange outputAmount() {
        ItemStack preview =
                outputExample();

        return AmountRange.fixed(
                preview.isEmpty()
                        ? 1
                        : preview.getCount()
        );
    }

    private @NotNull DwarfTradeRecipeInput buildPreviewInput(
            @NotNull LootContext context
    ) {
        ItemStack costA =
                materializeInput(
                        recipe.costA(),
                        context
                );

        ItemStack costB =
                ItemStack.EMPTY;

        if (recipe.costB() != null) {
            costB =
                    materializeInput(
                            recipe.costB(),
                            context
                    );
        }

        DwarfMerchantData.Level previewLevel =
                recipe.merchantLevel() != null
                        ? recipe.merchantLevel()
                        : DwarfMerchantData.Level.NOVICE;

        return new DwarfTradeRecipeInput(
                recipe.profession(),
                previewLevel,
                costA,
                costB
        );
    }

    private static @NotNull ItemStack materializeInput(
            @Nullable TradeCost cost,
            @Nullable LootContext context
    ) {
        if (cost == null) {
            return ItemStack.EMPTY;
        }

        ItemStack[] candidates =
                cost.candidateItems();

        if (candidates.length == 0) {
            return ItemStack.EMPTY;
        }

        for (ItemStack candidate : candidates) {
            if (candidate == null
                    || candidate.isEmpty()) {
                continue;
            }

            ItemStack resolved =
                    candidate.copy();

            if (!cost.test(resolved)) {
                continue;
            }

            int count;

            if (context != null) {
                count =
                        cost.resolveCount(
                                context
                        );
            } else {
                count =
                        amountRange(
                                cost.count(),
                                null
                        ).min();
            }

            if (count < 1) {
                continue;
            }

            resolved.setCount(
                    count
            );

            return resolved;
        }

        return ItemStack.EMPTY;
    }

    private static @NotNull AmountRange amountRange(
            @NotNull NumberProvider provider,
            @Nullable LootContext context
    ) {
        if (provider instanceof ConstantValue constant) {
            return AmountRange.fixed(
                    Mth.floor(
                            constant.value()
                    )
            );
        }

        if (provider instanceof UniformGenerator uniform) {
            int min =
                    providerMinimum(
                            uniform.min(),
                            context
                    );

            int max =
                    providerMaximum(
                            uniform.max(),
                            context
                    );

            return new AmountRange(
                    min,
                    max
            );
        }

        if (context != null) {
            return AmountRange.fixed(
                    provider.getInt(
                            context
                    )
            );
        }

        return AmountRange.fixed(1);
    }

    private static int providerMinimum(
            @NotNull NumberProvider provider,
            @Nullable LootContext context
    ) {
        if (provider instanceof ConstantValue constant) {
            return Mth.floor(
                    constant.value()
            );
        }

        if (provider instanceof UniformGenerator uniform) {
            return providerMinimum(
                    uniform.min(),
                    context
            );
        }

        if (context != null) {
            return provider.getInt(
                    context
            );
        }

        return 1;
    }

    private static int providerMaximum(
            @NotNull NumberProvider provider,
            @Nullable LootContext context
    ) {
        if (provider instanceof ConstantValue constant) {
            return Mth.floor(
                    constant.value()
            );
        }

        if (provider instanceof UniformGenerator uniform) {
            return providerMaximum(
                    uniform.max(),
                    context
            );
        }

        if (context != null) {
            return provider.getInt(
                    context
            );
        }

        return 1;
    }

    private static @NotNull ItemStack normalizeForJei(
            @NotNull ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copy =
                stack.copy();

        copy.setCount(1);

        return copy;
    }

    private static @Nullable LootContext resolveJeiLootContext() {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.getSingleplayerServer() == null) {
            return null;
        }

        ServerPlayer serverPlayer =
                minecraft.getSingleplayerServer()
                        .getPlayerList()
                        .getPlayer(
                                minecraft.player.getUUID()
                        );

        if (serverPlayer == null) {
            return null;
        }

        ServerLevel serverLevel =
                serverPlayer.serverLevel();

        return JolCraftRecipeContexts.create(
                serverLevel,
                PREVIEW_CONTEXT_PARAMS,
                builder -> builder
                        .withParameter(
                                LootContextParams.THIS_ENTITY,
                                serverPlayer
                        )
                        .withParameter(
                                LootContextParams.ORIGIN,
                                serverPlayer.position()
                        )
        );
    }
}