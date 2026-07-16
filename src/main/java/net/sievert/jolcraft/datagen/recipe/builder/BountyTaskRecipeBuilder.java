package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BountyTaskRecipeBuilder
        implements JolCraftEmissionBuilder<RecipeOutput> {

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

    private @Nullable Item bounty;

    /*
     * A bounty task resolves exactly one objective.
     *
     * Multiple collect calls become weighted alternatives in one vanilla
     * LootPool-backed ItemOutput.
     *
     * EntityOutput currently represents one entity type, so only one slay
     * objective can be represented until a generic weighted output wrapper
     * exists.
     */
    private final List<ItemObjective> itemObjectives =
            new ArrayList<>();

    private final List<EntityObjective> entityObjectives =
            new ArrayList<>();

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
        itemObjectives.add(
                new ItemObjective(
                        item.asItem(),
                        minCount,
                        maxCount,
                        weight
                )
        );

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
        entityObjectives.add(
                new EntityObjective(
                        entity,
                        minCount,
                        maxCount,
                        weight
                )
        );

        return this;
    }

    @Override
    public @NotNull DataResult<
            JolCraftDataEmission<RecipeOutput>
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

        if (itemObjectives.isEmpty()
                && entityObjectives.isEmpty()) {
            return DataResult.error(() ->
                    JolCraftDictionary.OBJECTIVE
                            + " must contain at least one objective"
            );
        }

        if (!itemObjectives.isEmpty()
                && !entityObjectives.isEmpty()) {
            return DataResult.error(() ->
                    JolCraftDictionary.OBJECTIVE
                            + " cannot mix item and entity objectives"
            );
        }

        /*
         * ItemOutput can use a vanilla LootPool for weighted choices.
         * EntityOutput currently holds only one direct EntityType.
         */
        if (entityObjectives.size() > 1) {
            return DataResult.error(() ->
                    JolCraftDictionary.OBJECTIVE
                            + " cannot currently contain multiple "
                            + "entity objectives"
            );
        }

        DataResult<Void> itemValidation =
                validateItemObjectives();

        if (itemValidation.error().isPresent()) {
            String message = itemValidation.error()
                    .map(DataResult.Error::message)
                    .orElse("invalid item objective");

            return DataResult.error(() -> message);
        }

        DataResult<Void> entityValidation =
                validateEntityObjectives();

        if (entityValidation.error().isPresent()) {
            String message = entityValidation.error()
                    .map(DataResult.Error::message)
                    .orElse("invalid entity objective");

            return DataResult.error(() -> message);
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

        DataResult<
                net.sievert.jolcraft.world.recipe.output.RecipeOutput
                > objectiveResult =
                buildObjective();

        if (objectiveResult.error().isPresent()) {
            String message = objectiveResult.error()
                    .map(DataResult.Error::message)
                    .orElse("invalid bounty objective");

            return DataResult.error(() -> message);
        }

        net.sievert.jolcraft.world.recipe.output.RecipeOutput objective =
                objectiveResult.result().orElse(null);

        if (objective == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.OBJECTIVE
                            + " could not be built"
            );
        }

        BountyTaskRecipe recipe =
                new BountyTaskRecipe(
                        bountyType,
                        tier,
                        bounty,
                        objective,
                        sound1,
                        sound2
                );

        DataResult<BountyTaskRecipe> validated =
                BountyTaskRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message = validated.error()
                    .map(DataResult.Error::message)
                    .orElse(
                            "invalid bounty task recipe"
                    );

            return DataResult.error(() -> message);
        }

        /*
         * Current bounty task providers are already separated by profession
         * folders, so the tier name is enough as the default recipe id.
         */
        String resolvedId =
                id != null && !id.isBlank()
                        ? id
                        : tier.name()
                        .toLowerCase(Locale.ROOT);

        return DataResult.success(
                new JolCraftDataEmission<>(
                        resolvedId,
                        (recipeOutput, path) ->
                                recipeOutput.accept(
                                        ResourceLocation
                                                .fromNamespaceAndPath(
                                                        JolCraft.MOD_ID,
                                                        path
                                                ),
                                        recipe,
                                        null
                                )
                )
        );
    }

    private @NotNull DataResult<
            net.sievert.jolcraft.world.recipe.output.RecipeOutput
            > buildObjective() {
        if (!itemObjectives.isEmpty()) {
            LootPool.Builder pool =
                    LootPool.lootPool()
                            .setRolls(
                                    ConstantValue.exactly(
                                            1.0F
                                    )
                            );

            for (ItemObjective objective : itemObjectives) {
                pool.add(
                        LootItem.lootTableItem(
                                        objective.item()
                                )
                                .apply(
                                        SetItemCountFunction
                                                .setCount(
                                                        countProvider(
                                                                objective.minCount(),
                                                                objective.maxCount()
                                                        )
                                                )
                                )
                                .setWeight(
                                        objective.weight()
                                )
                );
            }

            return DataResult.success(
                    ItemOutputs.pool(pool)
            );
        }

        if (!entityObjectives.isEmpty()) {
            EntityObjective objective =
                    entityObjectives.getFirst();

            /*
             * Weight has no effect with one entity alternative. It remains on
             * the builder entry so the public API can stay unchanged until
             * weighted polymorphic outputs are added.
             */
            return DataResult.success(
                    EntityOutputs.entity(
                            objective.entity(),
                            countProvider(
                                    objective.minCount(),
                                    objective.maxCount()
                            )
                    )
            );
        }

        return DataResult.error(() ->
                JolCraftDictionary.OBJECTIVE
                        + " is required"
        );
    }

    private @NotNull DataResult<Void>
    validateItemObjectives() {
        for (int index = 0;
             index < itemObjectives.size();
             index++) {
            ItemObjective objective =
                    itemObjectives.get(index);

            if (objective.item() == Items.AIR) {
                int resolvedIndex = index;

                return DataResult.error(() ->
                        "item objective["
                                + resolvedIndex
                                + "] must not be air"
                );
            }

            DataResult<Void> values =
                    validateObjectiveValues(
                            objective.minCount(),
                            objective.maxCount(),
                            objective.weight(),
                            "item objective[" + index + "]"
                    );

            if (values.error().isPresent()) {
                return values;
            }
        }

        return DataResult.success(null);
    }

    private @NotNull DataResult<Void>
    validateEntityObjectives() {
        for (int index = 0;
             index < entityObjectives.size();
             index++) {
            EntityObjective objective =
                    entityObjectives.get(index);

            DataResult<Void> values =
                    validateObjectiveValues(
                            objective.minCount(),
                            objective.maxCount(),
                            objective.weight(),
                            "entity objective[" + index + "]"
                    );

            if (values.error().isPresent()) {
                return values;
            }
        }

        return DataResult.success(null);
    }

    private static @NotNull DataResult<Void>
    validateObjectiveValues(
            int minCount,
            int maxCount,
            int weight,
            @NotNull String path
    ) {
        if (minCount < 1) {
            return DataResult.error(() ->
                    path + ".min_count must be at least 1"
            );
        }

        if (maxCount < minCount) {
            return DataResult.error(() ->
                    path
                            + ".max_count must be greater than "
                            + "or equal to min_count"
            );
        }

        if (weight < 1) {
            return DataResult.error(() ->
                    path + ".weight must be at least 1"
            );
        }

        return DataResult.success(null);
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

    private record ItemObjective(
            @NotNull Item item,
            int minCount,
            int maxCount,
            int weight
    ) {}

    private record EntityObjective(
            @NotNull EntityType<?> entity,
            int minCount,
            int maxCount,
            int weight
    ) {}
}