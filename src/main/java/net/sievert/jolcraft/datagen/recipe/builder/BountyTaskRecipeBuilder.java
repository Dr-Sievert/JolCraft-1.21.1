package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.world.recipe.output.EntityOutputs;
import net.sievert.jolcraft.world.recipe.output.ItemOutputs;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import net.sievert.jolcraft.world.recipe.output.SoundOutputs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class BountyTaskRecipeBuilder
        implements JolCraftEmissionBuilder<
        net.minecraft.data.recipes.RecipeOutput
        > {

    private static final String OBJECTIVES_KEY =
            JolCraftStrings.plural(
                    JolCraftDictionary.OBJECTIVE
            );

    private static final String SOUND_1_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.SOUND,
                    "1"
            );

    private static final String SOUND_2_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.SOUND,
                    "2"
            );

    private @Nullable String id;

    private @Nullable DwarfProfession bountyType;
    private @Nullable DwarfMerchantData.Level tier;

    private @Nullable net.minecraft.world.item.Item bounty;

    private final SimpleWeightedRandomList.Builder<
            net.sievert.jolcraft.world.recipe.output.RecipeOutput
            > objectives =
            SimpleWeightedRandomList.builder();

    private int objectiveCount;

    private @Nullable SoundOutput sound1;
    private @Nullable SoundOutput sound2;

    private BountyTaskRecipeBuilder() {}

    public static BountyTaskRecipeBuilder create() {
        return new BountyTaskRecipeBuilder();
    }

    public BountyTaskRecipeBuilder id(
            @NotNull String id
    ) {
        this.id = id;
        return this;
    }

    public BountyTaskRecipeBuilder bountyType(
            @NotNull DwarfProfession bountyType
    ) {
        this.bountyType = bountyType;
        return this;
    }

    public BountyTaskRecipeBuilder tier(
            @NotNull DwarfMerchantData.Level tier
    ) {
        this.tier = tier;
        return this;
    }

    public BountyTaskRecipeBuilder result(
            @NotNull ItemLike bounty
    ) {
        this.bounty = bounty.asItem();
        return this;
    }

    public BountyTaskRecipeBuilder sound1(
            @NotNull SoundEvent sound
    ) {
        this.sound1 =
                SoundOutputs.sound(sound);

        return this;
    }

    public BountyTaskRecipeBuilder sound1(
            @NotNull Holder<SoundEvent> sound
    ) {
        this.sound1 =
                SoundOutputs.sound(sound);

        return this;
    }

    public BountyTaskRecipeBuilder sound1(
            @NotNull SoundOutput sound
    ) {
        this.sound1 = sound;
        return this;
    }

    public BountyTaskRecipeBuilder sound2(
            @NotNull SoundEvent sound
    ) {
        this.sound2 =
                SoundOutputs.sound(sound);

        return this;
    }

    public BountyTaskRecipeBuilder sound2(
            @NotNull Holder<SoundEvent> sound
    ) {
        this.sound2 =
                SoundOutputs.sound(sound);

        return this;
    }

    public BountyTaskRecipeBuilder sound2(
            @NotNull SoundOutput sound
    ) {
        this.sound2 = sound;
        return this;
    }

    // -------------------------------------------------------------------------
    // Item objectives
    // -------------------------------------------------------------------------

    public BountyTaskRecipeBuilder collect(
            @NotNull ItemLike item,
            int count
    ) {
        return collectWeighted(
                item,
                count,
                count,
                1
        );
    }

    public BountyTaskRecipeBuilder collect(
            @NotNull ItemLike item,
            int minCount,
            int maxCount
    ) {
        return collectWeighted(
                item,
                minCount,
                maxCount,
                1
        );
    }

    public BountyTaskRecipeBuilder collectWeighted(
            @NotNull ItemLike item,
            int minCount,
            int maxCount,
            int weight
    ) {
        validateObjectiveValuesOrThrow(
                minCount,
                maxCount,
                weight,
                "item objective"
        );

        if (item.asItem() == Items.AIR) {
            throw new IllegalArgumentException(
                    "item objective must not be air"
            );
        }

        LootPool.Builder pool =
                LootPool.lootPool()
                        .setRolls(
                                ConstantValue.exactly(
                                        1.0F
                                )
                        )
                        .add(
                                LootItem.lootTableItem(
                                                item
                                        )
                                        .apply(
                                                SetItemCountFunction
                                                        .setCount(
                                                                countProvider(
                                                                        minCount,
                                                                        maxCount
                                                                )
                                                        )
                                        )
                        );

        objectives.add(
                ItemOutputs.pool(pool),
                weight
        );

        objectiveCount++;

        return this;
    }

    // -------------------------------------------------------------------------
    // Entity objectives
    // -------------------------------------------------------------------------

    public BountyTaskRecipeBuilder slay(
            @NotNull EntityType<?> entity,
            int count
    ) {
        return slayWeighted(
                entity,
                count,
                count,
                1
        );
    }

    public BountyTaskRecipeBuilder slay(
            @NotNull EntityType<?> entity,
            int minCount,
            int maxCount
    ) {
        return slayWeighted(
                entity,
                minCount,
                maxCount,
                1
        );
    }

    public BountyTaskRecipeBuilder slayWeighted(
            @NotNull EntityType<?> entity,
            int minCount,
            int maxCount,
            int weight
    ) {
        validateObjectiveValuesOrThrow(
                minCount,
                maxCount,
                weight,
                "entity objective"
        );

        objectives.add(
                EntityOutputs.entity(
                        entity,
                        countProvider(
                                minCount,
                                maxCount
                        )
                ),
                weight
        );

        objectiveCount++;

        return this;
    }

    @Override
    public @NotNull DataResult<
            JolCraftDataEmission<
                    net.minecraft.data.recipes.RecipeOutput
                    >
            > buildValidated() {
        if (bountyType == null) {
            return DataResult.error(() ->
                    BountyRecipe.TYPE_KEY
                            + " is required"
            );
        }

        if (tier == null) {
            return DataResult.error(() ->
                    BountyRecipe.TIER_KEY
                            + " is required"
            );
        }

        if (bounty == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.RESULT
                            + " is required"
            );
        }

        if (bounty == Items.AIR) {
            return DataResult.error(() ->
                    JolCraftDictionary.RESULT
                            + " must not be air"
            );
        }

        if (objectiveCount < 1) {
            return DataResult.error(() ->
                    OBJECTIVES_KEY
                            + " must contain at least one objective"
            );
        }

        if (sound1 == null) {
            return DataResult.error(() ->
                    SOUND_1_KEY
                            + " is required"
            );
        }

        if (sound2 == null) {
            return DataResult.error(() ->
                    SOUND_2_KEY
                            + " is required"
            );
        }

        BountyTaskRecipe recipe =
                new BountyTaskRecipe(
                        bountyType,
                        tier,
                        bounty,
                        objectives.build(),
                        sound1,
                        sound2
                );

        DataResult<BountyTaskRecipe> validated =
                BountyTaskRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message =
                    validated.error()
                            .map(DataResult.Error::message)
                            .orElse(
                                    "invalid bounty task recipe"
                            );

            return DataResult.error(() ->
                    message
            );
        }

        String resolvedId =
                id != null && !id.isBlank()
                        ? id
                        : tier.name()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return DataResult.success(
                new JolCraftDataEmission<>(
                        resolvedId,
                        (recipeOutput, path) ->
                                recipeOutput.accept(
                                        ResourceLocation.fromNamespaceAndPath(
                                                JolCraft.MOD_ID,
                                                path
                                        ),
                                        recipe,
                                        null
                                )
                )
        );
    }

    private static void validateObjectiveValuesOrThrow(
            int minCount,
            int maxCount,
            int weight,
            @NotNull String path
    ) {
        if (minCount < 1) {
            throw new IllegalArgumentException(
                    path
                            + ".min_count must be at least 1"
            );
        }

        if (maxCount < minCount) {
            throw new IllegalArgumentException(
                    path
                            + ".max_count must be greater than "
                            + "or equal to min_count"
            );
        }

        if (weight < 1) {
            throw new IllegalArgumentException(
                    path
                            + ".weight must be at least 1"
            );
        }
    }

    private static @NotNull NumberProvider countProvider(
            int minCount,
            int maxCount
    ) {
        if (minCount == maxCount) {
            return ConstantValue.exactly(
                    minCount
            );
        }

        return UniformGenerator.between(
                minCount,
                maxCount
        );
    }
}