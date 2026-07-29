package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeCost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record JeiDwarfTrade(
        @NotNull DwarfTradeRecipe recipe,
        @NotNull DeferredItem<Item> spawnEgg,
        @NotNull JeiItemOutcome outcome
) {

    private static final LootContextParamSet PREVIEW_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

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

            min = normalizedMin;
            max = normalizedMax;
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
        return normalizeForJei(
                outcome.stack()
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

    public @NotNull AmountRange outputAmount() {
        return new AmountRange(
                outcome.minCount(),
                outcome.maxCount()
        );
    }

    public double outputChance() {
        double chancePerRoll =
                outcome.chancePerRoll();

        return 1.0D - Math.pow(
                1.0D - chancePerRoll,
                outcome.rolls()
        );
    }

    public boolean outputGuaranteed() {
        return outputChance() >= 1.0D;
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
        if (provider instanceof ConstantValue(float value)) {
            return AmountRange.fixed(
                    Mth.floor(
                            value
                    )
            );
        }

        //noinspection DeconstructionCanBeUsed
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
        if (provider instanceof ConstantValue(float value)) {
            return Mth.floor(
                    value
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
        if (provider instanceof ConstantValue(float value)) {
            return Mth.floor(
                    value
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
                RandomSource.create(
                        0x4A4F4C4352414654L
                ),
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