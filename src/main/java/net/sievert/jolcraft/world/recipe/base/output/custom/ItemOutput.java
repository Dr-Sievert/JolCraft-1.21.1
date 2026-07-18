package net.sievert.jolcraft.world.recipe.base.output.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
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

public record ItemOutput(
        LootPool pool,
        List<ResourceLocation> hooks
) implements RecipeOutput {

    private static final String POOL_KEY = JolCraftDictionary.POOL;
    private static final String HOOK_KEY = JolCraftDictionary.HOOK;
    private static final String HOOKS_KEY = JolCraftStrings.plural(HOOK_KEY);

    public static final MapCodec<ItemOutput> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            LootPool.CODEC
                                    .fieldOf(POOL_KEY)
                                    .forGetter(ItemOutput::pool),

                            ResourceLocation.CODEC
                                    .listOf()
                                    .optionalFieldOf(
                                            HOOKS_KEY,
                                            List.of()
                                    )
                                    .forGetter(ItemOutput::hooks)
                    ).apply(
                            instance,
                            ItemOutput::new
                    )
            );

    public ItemOutput {
        Objects.requireNonNull(
                pool,
                POOL_KEY
        );

        hooks = hooks == null
                ? List.of()
                : List.copyOf(hooks);
    }

    @Override
    public RecipeOutputType getType() {
        return JolCraftRecipeOutputTypes.ITEM.get();
    }

    @Override
    public void validate(
            @NotNull ValidationContext context
    ) {
        RecipeOutput.super.validate(context);

        pool.validate(
                context.forChild("." + POOL_KEY)
        );
    }

    public void generate(
            @NotNull LootContext context,
            @NotNull RecipeInput recipeInput,
            @NotNull Consumer<ItemStack> output
    ) {
        pool.addRandomItems(
                generated -> {
                    if (generated.isEmpty()) {
                        return;
                    }

                    if (!applyHooks(
                            context,
                            generated,
                            recipeInput
                    )) {
                        return;
                    }

                    if (!generated.isEmpty()) {
                        output.accept(generated);
                    }
                },
                context
        );
    }

    public ItemOutput applyHooks(
            @NotNull ResourceLocation... hooks
    ) {
        Objects.requireNonNull(
                hooks,
                HOOKS_KEY
        );

        List<ResourceLocation> updated =
                new ArrayList<>(this.hooks);

        for (ResourceLocation hook : hooks) {
            updated.add(
                    Objects.requireNonNull(
                            hook,
                            HOOK_KEY
                    )
            );
        }

        return new ItemOutput(
                pool,
                updated
        );
    }

    public static ItemOutput item(
            @NotNull LootPoolEntryContainer.Builder<?> entry
    ) {
        Objects.requireNonNull(
                entry,
                JolCraftDictionary.ENTRY
        );

        return pool(
                LootPool.lootPool()
                        .add(entry)
        );
    }

    public static ItemOutput pool(
            @NotNull LootPool.Builder pool
    ) {
        Objects.requireNonNull(
                pool,
                POOL_KEY
        );

        return new ItemOutput(
                pool.build(),
                List.of()
        );
    }

    public static ItemOutput pool(
            @NotNull NumberProvider rolls,
            @NotNull LootPoolEntryContainer.Builder<?>... entries
    ) {
        Objects.requireNonNull(
                rolls,
                JolCraftStrings.plural(JolCraftDictionary.ROLL)
        );

        Objects.requireNonNull(
                entries,
                JolCraftDictionary.ENTRIES
        );

        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(rolls);

        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            pool.add(
                    Objects.requireNonNull(
                            entry,
                            JolCraftDictionary.ENTRY
                    )
            );
        }

        return pool(pool);
    }

    public static ItemOutput of(
            @NotNull LootPool.Builder pool
    ) {
        return pool(pool);
    }

    private boolean applyHooks(
            LootContext context,
            ItemStack generated,
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
}