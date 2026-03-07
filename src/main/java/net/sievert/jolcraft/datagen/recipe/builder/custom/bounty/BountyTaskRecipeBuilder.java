package net.sievert.jolcraft.datagen.recipe.builder.custom.bounty;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTier;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyType;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntityOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.entity.EntitySpec;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmission;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeFileNameBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity.EntityOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity.EntitySpecBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"UnusedReturnValue", "deprecation"})
public final class BountyTaskRecipeBuilder implements RecipeBuilder {

    private final List<String> errors = new ArrayList<>();

    private BountyType bountyType = BountyType.UNKNOWN;
    private BountyTier tier = BountyTier.UNKNOWN;

    private ItemOutput bounty = ItemOutput.one(new ItemStack(JolCraftItems.BOUNTY.get()));
    private Outputs objective = Outputs.EMPTY;

    private SoundOutput sound1 = SoundOutput.EMPTY;
    private SoundOutput sound2 = SoundOutput.EMPTY;

    private BountyTaskRecipeBuilder() {}

    public static @NotNull BountyTaskRecipeBuilder create() {
        return new BountyTaskRecipeBuilder();
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public @NotNull BountyTaskRecipeBuilder bountyType(@Nullable BountyType type) {
        if (type == null) {
            errors.add("bountyType is null");
            this.bountyType = BountyType.UNKNOWN;
            return this;
        }
        this.bountyType = type;
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder tier(@Nullable BountyTier tier) {
        if (tier == null) {
            errors.add("tier is null");
            this.tier = BountyTier.UNKNOWN;
            return this;
        }
        this.tier = tier;
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder result(@Nullable ItemOutput out) {
        if (out == null) {
            errors.add("result is null");
            this.bounty = ItemOutput.EMPTY;
            return this;
        }
        this.bounty = out;
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder result(@Nullable ItemLike item) {
        if (item == null) {
            errors.add("result item is null");
            this.bounty = ItemOutput.EMPTY;
            return this;
        }
        this.bounty = ItemOutput.one(new ItemStack(item.asItem(), 1));
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder sound1(@Nullable SoundOutput s) {
        this.sound1 = (s == null) ? SoundOutput.EMPTY : s;
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder sound2(@Nullable SoundOutput s) {
        this.sound2 = (s == null) ? SoundOutput.EMPTY : s;
        return this;
    }

    // ---------------------------------------------------------------------
    // Objective accumulation
    // ---------------------------------------------------------------------

    private void appendObjective(@NotNull Outputs next) {
        if (next == Outputs.EMPTY) return;

        if (this.objective == Outputs.EMPTY) {
            this.objective = next;
        } else {
            this.objective = this.objective.merge(next);
        }
    }

    public @NotNull BountyTaskRecipeBuilder collect(@Nullable ItemLike item, int min, int max) {
        if (item == null) {
            errors.add("collect item is null");
            return this;
        }

        appendObjective(
                Outputs.wrapSingle(
                        ItemOutputBuilder.create()
                                .result(item.asItem(), min, max)
                                .transforms(ItemTransforms.EMPTY)
                                .build()
                )
        );

        return this;
    }

    public @NotNull BountyTaskRecipeBuilder collect(@Nullable ItemLike item, int fixedCount) {
        return collect(item, fixedCount, fixedCount);
    }

    public @NotNull BountyTaskRecipeBuilder collectTag(@Nullable TagKey<Item> tag, int min, int max) {
        if (tag == null) {
            errors.add("collectTag tag is null");
            return this;
        }

        appendObjective(
                Outputs.wrapSingle(
                        ItemOutputBuilder.create()
                                .result(tag, new IntRange(min, max))
                                .transforms(ItemTransforms.EMPTY)
                                .build()
                )
        );

        return this;
    }

    public @NotNull BountyTaskRecipeBuilder collectTag(@Nullable TagKey<Item> tag, int fixedCount) {
        return collectTag(tag, fixedCount, fixedCount);
    }

    public @NotNull BountyTaskRecipeBuilder slay(@Nullable EntityType<?> type, int min, int max) {
        if (type == null) {
            errors.add("slay entity type is null");
            return this;
        }

        EntitySpec spec = EntitySpecBuilder.builder()
                .entity(type.builtInRegistryHolder())
                .count(new IntRange(min, max))
                .buildOrEmpty();

        EntityOutput out = EntityOutputBuilder.builder()
                .result(spec)
                .buildOrEmpty();

        appendObjective(Outputs.wrapSingle(out));
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder slay(@Nullable EntityType<?> type, int fixedCount) {
        return slay(type, fixedCount, fixedCount);
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<RecipeEmission> buildValidated() {

        DataResult<String> nameBuilt = RecipeFileNameBuilder.create()
                .word(bountyTierNameSafe())
                .word(bountyTypeNameSafe())
                .word(JolCraftStrings.plural(JolCraftRecipeIds.BOUNTY_TASK))
                .build();

        BountyTaskRecipe r = new BountyTaskRecipe(
                bountyType,
                tier,
                bounty,
                objective,
                sound1,
                sound2
        );

        DataResult<BountyTaskRecipe> validated = BountyTaskRecipe.validateRecipe(r);

        DataResult<BountyTaskRecipe> recipeResult =
                (!errors.isEmpty() && validated.error().isEmpty())
                        ? DataResult.error(() -> "builder: " + String.join("; ", errors), r)
                        : validated;

        return nameBuilt.flatMap(name ->
                recipeResult.flatMap(validRecipe ->
                        RecipeEmission.of(
                                JolCraftRecipeIds.BOUNTY_TASK,
                                name,
                                (RecipeOutput outAccept, ResourceKey<Recipe<?>> id) ->
                                        outAccept.accept(id, validRecipe, null)
                        )
                )
        );
    }

    private String bountyTierNameSafe() {
        return (tier == null) ? JolCraftDictionary.UNKNOWN
                : tier.name().toLowerCase(Locale.ROOT);
    }

    private String bountyTypeNameSafe() {
        try {
            BountyType t = (bountyType == null) ? BountyType.UNKNOWN : bountyType;
            String id = t.getId();
            return (id == null || id.isBlank())
                    ? JolCraftDictionary.UNKNOWN
                    : id;
        } catch (RuntimeException e) {
            errors.add("bountyType.getId() threw");
            return JolCraftDictionary.UNKNOWN;
        }
    }
}