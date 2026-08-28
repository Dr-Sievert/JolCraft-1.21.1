package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.datagen.base.output.JolCraftFileNameBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.mortar.MortarRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class MortarRecipeBuilder
        implements JolCraftEmissionBuilder<RecipeOutput> {

    private final List<ItemInput> inputs =
            new ArrayList<>();

    private final List<String> inputNames =
            new ArrayList<>();

    private @Nullable ItemOutput result;
    private @Nullable String resultName;

    private int grindingWork;
    private int toolDamage = 1;

    private @Nullable String fileNameOverride;

    private MortarRecipeBuilder() {}

    public static MortarRecipeBuilder create() {
        return new MortarRecipeBuilder();
    }

    public MortarRecipeBuilder input(
            @NotNull ItemLike item
    ) {
        return input(
                item,
                1
        );
    }

    public MortarRecipeBuilder input(
            @NotNull ItemLike item,
            int count
    ) {
        validateCount(
                count
        );

        ItemInput input =
                ItemInput.predicate(
                        ItemPredicate.Builder.item()
                                .of(
                                        item
                                )
                                .withCount(
                                        MinMaxBounds.Ints.exactly(
                                                count
                                        )
                                )
                );

        return addInput(
                input,
                itemToken(
                        item
                )
        );
    }

    public MortarRecipeBuilder essenceInput(
            @NotNull EssenceType type
    ) {
        return essenceInput(
                type,
                1
        );
    }

    public MortarRecipeBuilder essenceInput(
            @NotNull EssenceType type,
            int count
    ) {
        validateCount(
                count
        );

        ItemStack stack =
                JolCraftItems.ESSENCE.get()
                        .createStack(
                                type
                        );

        ItemInput input =
                ItemInput.predicate(
                        ItemPredicate.Builder.item()
                                .of(
                                        stack.getItem()
                                )
                                .withCount(
                                        MinMaxBounds.Ints.exactly(
                                                count
                                        )
                                )
                                .hasComponents(
                                        DataComponentPredicate.allOf(
                                                stack.getComponents()
                                        )
                                )
                );

        return addInput(
                input,
                essenceToken(
                        type
                )
        );
    }

    public MortarRecipeBuilder input(
            @NotNull ItemInput input,
            @NotNull String nameToken
    ) {
        return addInput(
                input,
                nameToken
        );
    }

    public MortarRecipeBuilder result(
            @NotNull ItemLike item
    ) {
        return result(
                item,
                1
        );
    }

    public MortarRecipeBuilder result(
            @NotNull ItemLike item,
            int count
    ) {
        validateCount(
                count
        );

        ItemOutput output =
                ItemOutput.item(
                        LootItem.lootTableItem(
                                        item
                                )
                                .apply(
                                        SetItemCountFunction.setCount(
                                                ConstantValue.exactly(
                                                        count
                                                )
                                        )
                                )
                );

        return setResult(
                output,
                itemToken(
                        item
                )
        );
    }

    public MortarRecipeBuilder essenceOutput(
            @NotNull EssenceType type
    ) {
        return essenceOutput(
                type,
                1
        );
    }

    public MortarRecipeBuilder essenceOutput(
            @NotNull EssenceType type,
            int count
    ) {
        validateCount(
                count
        );

        ItemOutput output =
                ItemOutput.item(
                        LootItem.lootTableItem(
                                        JolCraftItems.ESSENCE.get()
                                )
                                .apply(
                                        SetItemCountFunction.setCount(
                                                ConstantValue.exactly(
                                                        count
                                                )
                                        )
                                )
                                .apply(
                                        SetComponentsFunction.setComponent(
                                                JolCraftDataComponents
                                                        .ESSENCE_TYPE
                                                        .get(),
                                                type
                                        )
                                )
                );

        return setResult(
                output,
                essenceToken(
                        type
                )
        );
    }

    public MortarRecipeBuilder result(
            @NotNull ItemOutput result,
            @NotNull String nameToken
    ) {
        return setResult(
                result,
                nameToken
        );
    }

    public MortarRecipeBuilder grindingWork(
            int grindingWork
    ) {
        this.grindingWork =
                grindingWork;

        return this;
    }

    public MortarRecipeBuilder toolDamage(
            int toolDamage
    ) {
        this.toolDamage =
                toolDamage;

        return this;
    }

    public MortarRecipeBuilder fileNameOverride(
            @Nullable String fileName
    ) {
        this.fileNameOverride =
                fileName == null
                        || fileName.isBlank()
                        ? null
                        : fileName;

        return this;
    }

    @Override
    public @NotNull DataResult<JolCraftDataEmission<RecipeOutput>>
    buildValidated() {
        if (inputs.isEmpty()) {
            return DataResult.error(() ->
                    "at least one input is required"
            );
        }

        if (inputs.size() > 3) {
            return DataResult.error(() ->
                    "no more than three inputs are allowed"
            );
        }

        if (result == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.RESULT
                            + " is required"
            );
        }

        if (grindingWork < 1
                || grindingWork > MortarRecipe.MAX_GRINDING_WORK) {
            return DataResult.error(() ->
                    "grinding work must be between 1 and "
                            + MortarRecipe.MAX_GRINDING_WORK
            );
        }

        if (toolDamage < 0) {
            return DataResult.error(() ->
                    "tool damage cannot be negative"
            );
        }

        MortarRecipe recipe =
                new MortarRecipe(
                        List.copyOf(
                                inputs
                        ),
                        result,
                        grindingWork,
                        toolDamage
                );

        DataResult<MortarRecipe> validated =
                MortarRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message =
                    validated.error()
                            .map(
                                    DataResult.Error::message
                            )
                            .orElse(
                                    "invalid mortar recipe"
                            );

            return DataResult.error(() ->
                    message
            );
        }

        DataResult<String> nameResult =
                fileNameOverride != null
                        ? JolCraftFileNameBuilder.validateFileName(
                        fileNameOverride
                )
                        : buildAutomaticName();

        return nameResult.map(name ->
                new JolCraftDataEmission<>(
                        name,
                        (recipeOutput, path) ->
                                recipeOutput.accept(
                                        JolCraft.location(
                                                path
                                        ),
                                        recipe,
                                        null
                                )
                )
        );
    }

    private MortarRecipeBuilder addInput(
            @NotNull ItemInput input,
            @NotNull String nameToken
    ) {
        this.inputs.add(
                input
        );

        this.inputNames.add(
                nameToken
        );

        return this;
    }

    private MortarRecipeBuilder setResult(
            @NotNull ItemOutput result,
            @NotNull String nameToken
    ) {
        this.result =
                result;

        this.resultName =
                nameToken;

        return this;
    }

    private @NotNull DataResult<String> buildAutomaticName() {
        JolCraftFileNameBuilder name =
                JolCraftFileNameBuilder.create()
                        .token(
                                JolCraftDictionary.GRIND
                        );

        for (int i = 0; i < inputNames.size(); i++) {
            if (i > 0) {
                name.token(
                        JolCraftDictionary.AND
                );
            }

            name.token(
                    inputNames.get(
                            i
                    )
            );
        }

        return name
                .token(
                        "into"
                )
                .token(
                        resultName
                )
                .build();
    }

    private static void validateCount(
            int count
    ) {
        if (count < 1) {
            throw new IllegalArgumentException(
                    "mortar item count must be at least 1"
            );
        }
    }

    private static @NotNull String itemToken(
            @NotNull ItemLike item
    ) {
        ResourceLocation id =
                BuiltInRegistries.ITEM.getKey(
                        item.asItem()
                );

        return id.getPath();
    }

    private static @NotNull String essenceToken(
            @NotNull EssenceType type
    ) {
        return type.getId()
                + "_"
                + JolCraftDictionary.ESSENCE;
    }
}