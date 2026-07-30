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
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeCost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record JeiDwarfTrade(
        @NotNull DwarfTradeRecipe recipe,
        @NotNull DeferredItem<Item> spawnEgg,
        double tradeChancePerRoll,
        @NotNull JeiItemOutcome outcome,
        @NotNull List<ItemStack> inputAExamples,
        @NotNull List<ItemStack> inputBExamples,
        @NotNull AmountRange inputAmountA,
        @Nullable AmountRange inputAmountB
) {

    private static final LootContextParamSet PREVIEW_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    public JeiDwarfTrade {
        Objects.requireNonNull(
                recipe,
                JolCraftDictionary.RECIPE
        );

        Objects.requireNonNull(
                spawnEgg,
                JolCraftDictionary.ENTITY
        );

        if (!Double.isFinite(
                tradeChancePerRoll
        ) || tradeChancePerRoll < 0.0D
                || tradeChancePerRoll > 1.0D) {
            throw new IllegalArgumentException(
                    "tradeChancePerRoll must be between 0 and 1"
            );
        }

        Objects.requireNonNull(
                outcome,
                JolCraftDictionary.RESULT
        );

        inputAExamples =
                copyStacks(
                        inputAExamples,
                        "inputAExamples",
                        true
                );

        inputBExamples =
                copyStacks(
                        inputBExamples,
                        "inputBExamples",
                        false
                );

        Objects.requireNonNull(
                inputAmountA,
                "inputAAmount"
        );

        if (recipe.costB() == null) {
            if (!inputBExamples.isEmpty()
                    || inputAmountB != null) {
                throw new IllegalArgumentException(
                        "Input B preview must be absent when the recipe has no cost B"
                );
            }
        } else {
            if (inputBExamples.isEmpty()
                    || inputAmountB == null) {
                throw new IllegalArgumentException(
                        "Input B preview is required when the recipe has cost B"
                );
            }
        }
    }

    public static @NotNull List<JeiDwarfTrade> create(
            @NotNull DwarfTradeRecipe recipe,
            @NotNull DeferredItem<Item> spawnEgg,
            double tradeChancePerRoll,
            @NotNull List<JeiItemOutcome> outcomes
    ) {
        Objects.requireNonNull(
                recipe,
                JolCraftDictionary.RECIPE
        );

        Objects.requireNonNull(
                spawnEgg,
                JolCraftDictionary.ENTITY
        );

        Objects.requireNonNull(
                outcomes,
                JolCraftStrings.plural(
                        JolCraftDictionary.RESULT
                )
        );

        LootContext context =
                resolveJeiLootContext();

        List<ItemStack> inputAExamples =
                materializeInputs(
                        recipe.costA()
                );

        if (inputAExamples.isEmpty()) {
            throw new IllegalArgumentException(
                    "Dwarf trade cost A produced no JEI input examples"
            );
        }

        AmountRange inputAmountA =
                amountRange(
                        recipe.costA()
                                .count(),
                        context
                );

        TradeCost costB =
                recipe.costB();

        List<ItemStack> inputBExamples;
        AmountRange inputAmountB;

        if (costB == null) {
            inputBExamples =
                    List.of();

            inputAmountB =
                    null;
        } else {
            inputBExamples =
                    materializeInputs(
                            costB
                    );

            if (inputBExamples.isEmpty()) {
                throw new IllegalArgumentException(
                        "Dwarf trade cost B produced no JEI input examples"
                );
            }

            inputAmountB =
                    amountRange(
                            costB.count(),
                            context
                    );
        }

        List<JeiDwarfTrade> result =
                new ArrayList<>(
                        outcomes.size()
                );

        for (JeiItemOutcome outcome : outcomes) {
            result.add(
                    new JeiDwarfTrade(
                            recipe,
                            spawnEgg,
                            tradeChancePerRoll,
                            outcome,
                            inputAExamples,
                            inputBExamples,
                            inputAmountA,
                            inputAmountB
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    public record AmountRange(
            int min,
            int max,
            boolean known
    ) {

        public AmountRange {
            if (known) {
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
            } else {
                min =
                        1;

                max =
                        1;
            }
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
                    normalized,
                    true
            );
        }

        public static @NotNull AmountRange range(
                int min,
                int max
        ) {
            return new AmountRange(
                    min,
                    max,
                    true
            );
        }

        public static @NotNull AmountRange unknown() {
            return new AmountRange(
                    1,
                    1,
                    false
            );
        }
    }

    public @NotNull DwarfProfession profession() {
        return recipe.profession();
    }

    public @Nullable DwarfMerchantData.Level level() {
        return recipe.merchantLevel();
    }

    public boolean hasInputB() {
        return !inputBExamples.isEmpty();
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

    public @NotNull AmountRange outputAmount() {
        return AmountRange.range(
                outcome.minCount(),
                outcome.maxCount()
        );
    }

    public double outputChancePerRoll() {
        return outcome.chancePerRoll();
    }

    public int outputRolls() {
        return outcome.rolls();
    }

    public boolean outputGuaranteedPerRoll() {
        return outputChancePerRoll() >= 1.0D;
    }

    public boolean tradeGuaranteedPerRoll() {
        return tradeChancePerRoll >= 1.0D;
    }

    private static @NotNull List<ItemStack> materializeInputs(
            @NotNull TradeCost cost
    ) {
        List<ItemStack> resolved =
                new ArrayList<>();

        for (ItemStack candidate : cost.candidateItems()) {
            if (candidate == null
                    || candidate.isEmpty()
                    || !cost.test(candidate)) {
                continue;
            }

            ItemStack normalized =
                    normalizeForJei(
                            candidate
                    );

            if (containsEquivalent(
                    resolved,
                    normalized
            )) {
                continue;
            }

            resolved.add(
                    normalized
            );
        }

        return List.copyOf(
                resolved
        );
    }

    private static boolean containsEquivalent(
            @NotNull List<ItemStack> stacks,
            @NotNull ItemStack candidate
    ) {
        for (ItemStack stack : stacks) {
            if (ItemStack.isSameItemSameComponents(
                    stack,
                    candidate
            )) {
                return true;
            }
        }

        return false;
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
            Integer min =
                    providerMinimum(
                            uniform.min(),
                            context
                    );

            Integer max =
                    providerMaximum(
                            uniform.max(),
                            context
                    );

            if (min == null
                    || max == null) {
                return AmountRange.unknown();
            }

            return AmountRange.range(
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

        return AmountRange.unknown();
    }

    private static @Nullable Integer providerMinimum(
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

        return null;
    }

    private static @Nullable Integer providerMaximum(
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

        return null;
    }

    private static @NotNull ItemStack normalizeForJei(
            @NotNull ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copy =
                stack.copy();

        copy.setCount(
                1
        );

        return copy;
    }

    private static @NotNull List<ItemStack> copyStacks(
            @NotNull List<ItemStack> stacks,
            @NotNull String name,
            boolean required
    ) {
        Objects.requireNonNull(
                stacks,
                name
        );

        List<ItemStack> copies =
                stacks.stream()
                        .map(ItemStack::copy)
                        .toList();

        if (required
                && copies.isEmpty()) {
            throw new IllegalArgumentException(
                    name
                            + " must contain at least one stack"
            );
        }

        return copies;
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
