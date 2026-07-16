package net.sievert.jolcraft.world.recipe.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.sievert.jolcraft.world.recipe.output.hook.JolCraftRecipeHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public record ItemOutput(
        LootPool pool,
        List<ResourceLocation> hooks
) implements RecipeOutput {

    private static final String POOL_KEY = "pool";
    private static final String HOOKS_KEY = "hooks";

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

    public ItemOutput applyHook(
            @NotNull ResourceLocation hook
    ) {
        Objects.requireNonNull(
                hook,
                "hook"
        );

        List<ResourceLocation> updated =
                new ArrayList<>(hooks);

        updated.add(hook);

        return new ItemOutput(
                pool,
                updated
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
                            "hook"
                    )
            );
        }

        return new ItemOutput(
                pool,
                updated
        );
    }

    public static ItemOutput of(
            @NotNull LootPool.Builder pool
    ) {
        return new ItemOutput(
                pool.build(),
                List.of()
        );
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