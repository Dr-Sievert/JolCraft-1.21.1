package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractBountyTasks {

    private static final String ROOT_FOLDER = JolCraftDictionary.BOUNTY;

    protected abstract @NotNull BountyType bountyType();

    public abstract void addTasks(@NotNull AbstractRecipeProvider p);

    protected final String fullFolder() {
        return JolCraftStrings.slashed(
                ROOT_FOLDER,
                JolCraftDictionary.TASK,
                bountyType().getId()
        );
    }

    // =====================================================================
    // Amount helpers (int OR {min_count,max_count})
    // =====================================================================

    protected static BountyTaskRecipe.Amount amount(int value) {
        return BountyTaskRecipe.Amount.fixed(value);
    }

    protected static BountyTaskRecipe.Amount amount(int min, int max) {
        return new BountyTaskRecipe.Amount(min, max);
    }

    // =====================================================================
    // Ingredient helpers (item OR entity)
    // =====================================================================

    protected static BountyTaskRecipe.TaskIngredient item(Item item) {
        return new BountyTaskRecipe.TaskIngredient.ItemIngredient(item.builtInRegistryHolder());
    }

    protected static BountyTaskRecipe.TaskIngredient entity(EntityType<?> entity) {
        Holder<EntityType<?>> holder = entity.builtInRegistryHolder();
        return new BountyTaskRecipe.TaskIngredient.EntityIngredient(holder);
    }

    // =====================================================================
    // Objective convenience helpers
    // =====================================================================

    protected static BountyTaskRecipe.TaskObjective collect(Item item, BountyTaskRecipe.Amount amount) {
        return new BountyTaskRecipe.TaskObjective(
                BountyTaskRecipe.Task.COLLECT,
                item(item),
                amount
        );
    }

    protected static BountyTaskRecipe.TaskObjective slay(EntityType<?> entity, BountyTaskRecipe.Amount amount) {
        return new BountyTaskRecipe.TaskObjective(
                BountyTaskRecipe.Task.SLAY,
                entity(entity),
                amount
        );
    }

    // =====================================================================
    // Core emitter
    // =====================================================================

    protected final void task(
            AbstractRecipeProvider p,
            Item result,
            BountyTier tier,
            int weight,
            BountyTaskRecipe.TaskObjective objective
    ) {
        String idPath = switch (objective.ingredient()) {

            case BountyTaskRecipe.TaskIngredient.ItemIngredient(Holder<Item> itemHolder) ->
                    JolCraftStrings.underscored(
                            JolCraftDictionary.COLLECT,
                            itemId(itemHolder.value())
                    );

            case BountyTaskRecipe.TaskIngredient.EntityIngredient(Holder<EntityType<?>> entityHolder) ->
                    JolCraftStrings.underscored(
                            JolCraftDictionary.SLAY,
                            entityId(entityHolder.value())
                    );
        };

        BountyTaskRecipe recipe = new BountyTaskRecipe(
                result.builtInRegistryHolder(),
                bountyType(),
                tier.getId(),
                weight,
                objective
        );

        save(p, tier, idPath, recipe);
    }

    // =====================================================================
    // Save
    // =====================================================================

    private void save(
            AbstractRecipeProvider p,
            BountyTier tier,
            String idPath,
            BountyTaskRecipe recipe
    ) {
        String file = JolCraftStrings.underscored(
                tierId(tier),
                idPath
        );

        ResourceLocation id = JolCraft.location(p.inFolder(fullFolder(), file));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);

        p.out().accept(key, recipe, null);
    }

    // =====================================================================
    // Stable id helpers
    // =====================================================================

    protected static String tierId(BountyTier tier) {
        return tier.name().toLowerCase();
    }

    protected static String itemId(Item item) {
        return item.builtInRegistryHolder().key().location().getPath();
    }

    protected static String entityId(EntityType<?> entity) {
        return entity.builtInRegistryHolder().key().location().getPath();
    }

    protected static String collectId(Item item) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.COLLECT,
                itemId(item)
        );
    }

    protected static String slayId(EntityType<?> entity) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.SLAY,
                entityId(entity)
        );
    }
}