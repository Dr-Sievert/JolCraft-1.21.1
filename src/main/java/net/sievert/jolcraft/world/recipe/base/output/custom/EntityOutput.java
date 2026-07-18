package net.sievert.jolcraft.world.recipe.base.output.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.recipe.base.output.JolCraftRecipeOutputTypes;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutputType;
import net.sievert.jolcraft.world.recipe.base.output.hook.JolCraftRecipeHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public record EntityOutput(
        EntityType<?> entity,
        NumberProvider count,
        List<ResourceLocation> hooks
) implements RecipeOutput {

    private static final String ENTITY_KEY = JolCraftDictionary.ENTITY;
    private static final String COUNT_KEY = JolCraftDictionary.COUNT;
    private static final String HOOKS_KEY =
            JolCraftStrings.plural(JolCraftDictionary.HOOK);

    public static final MapCodec<EntityOutput> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            BuiltInRegistries.ENTITY_TYPE
                                    .byNameCodec()
                                    .fieldOf(ENTITY_KEY)
                                    .forGetter(EntityOutput::entity),

                            NumberProviders.CODEC
                                    .optionalFieldOf(
                                            COUNT_KEY,
                                            ConstantValue.exactly(1.0F)
                                    )
                                    .forGetter(EntityOutput::count),

                            ResourceLocation.CODEC
                                    .listOf()
                                    .optionalFieldOf(
                                            HOOKS_KEY,
                                            List.of()
                                    )
                                    .forGetter(EntityOutput::hooks)
                    ).apply(
                            instance,
                            EntityOutput::new
                    )
            );

    public EntityOutput {
        Objects.requireNonNull(
                entity,
                ENTITY_KEY
        );

        Objects.requireNonNull(
                count,
                COUNT_KEY
        );

        hooks = hooks == null
                ? List.of()
                : List.copyOf(hooks);
    }

    @Override
    public RecipeOutputType getType() {
        return JolCraftRecipeOutputTypes.ENTITY.get();
    }

    @Override
    public void validate(
            @NotNull ValidationContext context
    ) {
        RecipeOutput.super.validate(context);

        count.validate(
                context.forChild("." + COUNT_KEY)
        );
    }

    public void generate(
            @NotNull LootContext context,
            @NotNull RecipeInput recipeInput,
            @NotNull Consumer<GeneratedEntity> output
    ) {
        int generatedCount =
                count.getInt(context);

        if (generatedCount <= 0) {
            return;
        }

        GeneratedEntity generated =
                new GeneratedEntity(
                        entity,
                        generatedCount
                );

        if (applyHooks(
                context,
                generated,
                recipeInput
        )) {
            output.accept(generated);
        }
    }

    public EntityOutput applyHooks(
            @NotNull ResourceLocation... hooks
    ) {
        Objects.requireNonNull(hooks, HOOKS_KEY);

        List<ResourceLocation> updated = new ArrayList<>(this.hooks);

        for (ResourceLocation hook : hooks) {
            updated.add(
                    Objects.requireNonNull(
                            hook,
                            JolCraftDictionary.HOOK
                    )
            );
        }

        return new EntityOutput(
                entity,
                count,
                updated
        );
    }

    public static EntityOutput entity(
            @NotNull EntityType<?> entity
    ) {
        return entity(
                entity,
                ConstantValue.exactly(1.0F)
        );
    }

    public static EntityOutput entity(
            @NotNull EntityType<?> entity,
            @NotNull NumberProvider count
    ) {
        return new EntityOutput(
                entity,
                count,
                List.of()
        );
    }

    public static EntityOutput of(
            @NotNull EntityType<?> entity
    ) {
        return entity(entity);
    }

    public static EntityOutput of(
            @NotNull EntityType<?> entity,
            @NotNull NumberProvider count
    ) {
        return entity(
                entity,
                count
        );
    }

    private boolean applyHooks(
            LootContext context,
            GeneratedEntity generated,
            RecipeInput recipeInput
    ) {
        for (ResourceLocation hook : hooks) {
            if (!JolCraftRecipeHooks.apply(
                    hook,
                    context,
                    generated,
                    recipeInput
            )) {
                return false;
            }
        }

        return true;
    }

    public record GeneratedEntity(
            EntityType<?> entity,
            int count
    ) {}
}