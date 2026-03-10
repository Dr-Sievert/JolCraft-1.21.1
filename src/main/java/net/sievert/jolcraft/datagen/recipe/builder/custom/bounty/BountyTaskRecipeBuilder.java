package net.sievert.jolcraft.datagen.recipe.builder.custom.bounty;

import com.mojang.serialization.DataResult;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTaskRecipe;
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
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity.EntityOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity.EntitySpecBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
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

    private @Nullable DwarfProfession bountyType;
    private @Nullable DwarfMerchantData.Level tier;

    private @Nullable ItemOutput bounty;
    private @Nullable Outputs objective;

    private @Nullable SoundOutput sound1;
    private @Nullable SoundOutput sound2;

    private BountyTaskRecipeBuilder() {
        this.bounty = buildDefaultBounty();
    }

    public static @NotNull BountyTaskRecipeBuilder create() {
        return new BountyTaskRecipeBuilder();
    }

    private @Nullable ItemOutput buildDefaultBounty() {
        DataResult<ItemOutput> built = ItemOutput.one(new ItemStack(JolCraftItems.BOUNTY.get()));
        if (built.error().isPresent()) {
            errors.add("default bounty invalid: " +
                    built.error().map(DataResult.Error::message).orElse("invalid"));
            return null;
        }
        return built.result().orElse(null);
    }

    // ---------------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------------

    public @NotNull BountyTaskRecipeBuilder bountyType(@Nullable DwarfProfession type) {
        if (type == null) {
            errors.add("bountyType is null");
            this.bountyType = null;
            return this;
        }

        if (type == DwarfProfession.NONE) {
            errors.add("bountyType must not be NONE");
            this.bountyType = null;
            return this;
        }

        this.bountyType = type;
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder tier(@Nullable DwarfMerchantData.Level tier) {
        if (tier == null) {
            errors.add("tier is null");
            this.tier = null;
            return this;
        }

        this.tier = tier;
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder result(@Nullable ItemOutput out) {
        if (out == null) {
            errors.add("result is null");
            this.bounty = null;
            return this;
        }

        this.bounty = out;
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder result(@Nullable ItemLike item) {
        if (item == null) {
            errors.add("result item is null");
            this.bounty = null;
            return this;
        }

        DataResult<ItemOutput> built = ItemOutput.one(new ItemStack(item.asItem(), 1));
        if (built.error().isPresent()) {
            errors.add("result item invalid: " +
                    built.error().map(DataResult.Error::message).orElse("invalid"));
            this.bounty = null;
            return this;
        }

        this.bounty = built.result().orElse(null);
        if (this.bounty == null) {
            errors.add("result item invalid");
        }

        return this;
    }

    private @Nullable SoundOutput resolveSound(
            @Nullable SoundEvent sound,
            @NotNull String field
    ) {
        if (sound == null) {
            errors.add(field + " is null");
            return null;
        }

        var built = SoundOutputBuilder.create()
                .sound(sound)
                .buildValidated();

        if (built.error().isPresent()) {
            errors.add(field + " invalid: " + built.error().map(DataResult.Error::message).orElse("invalid"));
            return null;
        }

        return built.result().orElse(null);
    }

    public @NotNull BountyTaskRecipeBuilder sound1(@Nullable SoundEvent sound) {
        this.sound1 = resolveSound(sound, "sound1");
        return this;
    }

    public @NotNull BountyTaskRecipeBuilder sound2(@Nullable SoundEvent sound) {
        this.sound2 = resolveSound(sound, "sound2");
        return this;
    }

    // ---------------------------------------------------------------------
    // Objective accumulation
    // ---------------------------------------------------------------------

    private void appendObjective(@NotNull Outputs next) {
        if (isEmptyOutputs(next)) {
            return;
        }

        this.objective = isEmptyOutputs(this.objective)
                ? next
                : this.objective.merge(next);
    }

    private static boolean isEmptyOutputs(@Nullable Outputs outputs) {
        return outputs == null || outputs.pools().pools().isEmpty();
    }

    private static @NotNull ItemTransforms noTransforms() {
        return ItemTransforms.EMPTY;
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
                                .transforms(noTransforms())
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
                                .transforms(noTransforms())
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
                .buildOrNull();

        if (spec == null) {
            errors.add("slay entity spec could not be built");
            return this;
        }

        EntityOutput out = EntityOutputBuilder.builder()
                .result(spec)
                .buildOrNull();

        if (out == null) {
            errors.add("slay entity output could not be built");
            return this;
        }

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
        if (bountyType == null) {
            errors.add("bountyType is required");
        }

        if (tier == null) {
            errors.add("tier is required");
        }

        if (bounty == null) {
            errors.add("result is required");
        }

        if (isEmptyOutputs(objective)) {
            errors.add("objective is required");
        }

        if (sound1 == null) {
            errors.add("sound1 is required");
        }

        if (sound2 == null) {
            errors.add("sound2 is required");
        }

        DataResult<String> nameBuilt = RecipeFileNameBuilder.create()
                .word(bountyTierNameSafe())
                .word(JolCraftStrings.plural(JolCraftRecipeIds.BOUNTY_TASK))
                .build();

        if (!errors.isEmpty()) {
            return nameBuilt.flatMap(name ->
                    DataResult.error(() -> "builder: " + String.join("; ", errors))
            );
        }

        DwarfProfession finalType = bountyType;
        DwarfMerchantData.Level finalTier = tier;
        ItemOutput finalBounty = bounty;
        Outputs finalObjective = objective;
        SoundOutput finalSound1 = sound1;
        SoundOutput finalSound2 = sound2;

        if (finalType == null
                || finalTier == null
                || finalBounty == null
                || finalObjective == null
                || finalObjective.pools().pools().isEmpty()
                || finalSound1 == null
                || finalSound2 == null) {
            return DataResult.error(() -> "builder: missing required fields");
        }

        BountyTaskRecipe recipe = new BountyTaskRecipe(
                finalType,
                finalTier,
                finalBounty,
                finalObjective,
                finalSound1,
                finalSound2
        );

        return nameBuilt.flatMap(name ->
                BountyTaskRecipe.validateRecipe(recipe).flatMap(validRecipe ->
                        RecipeEmission.of(
                                JolCraftRecipeIds.BOUNTY_TASK,
                                name,
                                (RecipeOutput outAccept, ResourceKey<Recipe<?>> id) ->
                                        outAccept.accept(id, validRecipe, null)
                        )
                )
        );
    }

    private @NotNull String bountyTierNameSafe() {
        if (tier == null) {
            return JolCraftDictionary.UNKNOWN;
        }

        return tier.name().toLowerCase(Locale.ROOT);
    }
}