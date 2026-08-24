package net.sievert.jolcraft.event.game.world.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContextParams;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipeInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class JolCraftHandInteractionEvents {

    private static final int HAND_INTERACTION_COOLDOWN_TICKS = 10;

    private static final LootContextParamSet EXECUTION_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(JolCraftRecipeContextParams.INPUT_ITEM)
                    .build();

    private JolCraftHandInteractionEvents() {}

    public static void onHandInteractionRecipe(
            PlayerInteractEvent.RightClickItem event
    ) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        if (main.isEmpty() || off.isEmpty()) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(main.getItem())
                || player.getCooldowns().isOnCooldown(off.getItem())) {
            return;
        }

        HandInteractionRecipeInput rawInput =
                new HandInteractionRecipeInput(
                        main,
                        off
                );

        ResolvedRecipe resolved =
                findResolvedRecipe(
                        level,
                        player,
                        rawInput
                );

        if (resolved == null) {
            return;
        }

        HandInteractionRecipe recipe =
                resolved.recipe();

        HandMapping mapping =
                resolved.mapping();

        HandInteractionRecipeInput resolvedInput =
                new HandInteractionRecipeInput(
                        mapping.stackA(),
                        mapping.stackB()
                );

        LootContext context =
                createExecutionContext(
                        level,
                        player,
                        mapping.stackA()
                );

        cancel(event);

        boolean generatedAny =
                generateOutputs(
                        level,
                        player,
                        recipe,
                        resolvedInput,
                        context
                );

        playSoundOutput(
                level,
                player,
                generatedAny
                        ? recipe.successSound()
                        : recipe.failSound(),
                resolvedInput,
                context
        );

        recipe.actionA().apply(
                level,
                player,
                mapping.stackA()
        );

        recipe.actionB().apply(
                level,
                player,
                mapping.stackB()
        );

        player.getCooldowns().addCooldown(
                main.getItem(),
                HAND_INTERACTION_COOLDOWN_TICKS
        );

        player.getCooldowns().addCooldown(
                off.getItem(),
                HAND_INTERACTION_COOLDOWN_TICKS
        );

        player.swing(
                mapping.swingHand(),
                true
        );

        JolCraftLogs.info(
                JolCraftLogTags.RECIPE,
                "Executed hand interaction recipe={} success={}",
                resolved.id(),
                generatedAny
        );
    }

    private static boolean generateOutputs(
            @NotNull ServerLevel level,
            @NotNull ServerPlayer player,
            @NotNull HandInteractionRecipe recipe,
            @NotNull HandInteractionRecipeInput input,
            @NotNull LootContext context
    ) {
        boolean generatedAny = false;

        for (RecipeOutput output : recipe.outputs()) {
            if (output instanceof ItemOutput itemOutput) {
                List<ItemStack> generated =
                        new ArrayList<>();

                itemOutput.generate(
                        context,
                        input,
                        generated::add
                );

                for (ItemStack stack : generated) {
                    if (stack.isEmpty()) {
                        continue;
                    }

                    JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                            player,
                            stack
                    );

                    generatedAny = true;
                }

                continue;
            }

            if (output instanceof SoundOutput soundOutput) {
                boolean[] generated = {false};

                soundOutput.generate(
                        context,
                        input,
                        sound -> {
                            playGeneratedSound(
                                    level,
                                    player,
                                    sound
                            );

                            generated[0] = true;
                        }
                );

                generatedAny |= generated[0];
                continue;
            }

            if (output instanceof EffectOutput effectOutput) {
                boolean[] generated = {false};

                effectOutput.generate(
                        context,
                        input,
                        effect -> {
                            player.addEffect(
                                    new MobEffectInstance(effect)
                            );

                            generated[0] = true;
                        }
                );

                generatedAny |= generated[0];
                continue;
            }

            if (output instanceof EntityOutput entityOutput) {
                boolean[] generated = {false};

                entityOutput.generate(
                        context,
                        input,
                        entity -> {
                            if (spawnGeneratedEntities(
                                    level,
                                    player,
                                    entity
                            )) {
                                generated[0] = true;
                            }
                        }
                );

                generatedAny |= generated[0];
            }
        }

        return generatedAny;
    }

    private static boolean spawnGeneratedEntities(
            @NotNull ServerLevel level,
            @NotNull ServerPlayer player,
            @NotNull EntityOutput.GeneratedEntity generated
    ) {
        boolean spawnedAny = false;

        for (int index = 0;
             index < generated.count();
             index++) {

            Entity entity = generated.entity().create(
                    level,
                    null,
                    player.blockPosition(),
                    MobSpawnType.EVENT,
                    false,
                    false
            );

            if (entity == null) {
                continue;
            }

            entity.moveTo(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    level.random.nextFloat() * 360.0F,
                    0.0F
            );

            if (level.addFreshEntity(entity)) {
                spawnedAny = true;
            }
        }

        return spawnedAny;
    }

    private static void playSoundOutput(
            @NotNull ServerLevel level,
            @NotNull ServerPlayer player,
            @NotNull SoundOutput output,
            @NotNull HandInteractionRecipeInput input,
            @NotNull LootContext context
    ) {
        output.generate(
                context,
                input,
                generated -> playGeneratedSound(
                        level,
                        player,
                        generated
                )
        );
    }

    private static void playGeneratedSound(
            @NotNull ServerLevel level,
            @NotNull ServerPlayer player,
            @NotNull SoundOutput.GeneratedSound generated
    ) {
        SoundEvent sound =
                generated.sound().value();

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                sound,
                generated.source(),
                generated.volume(),
                generated.pitch()
        );
    }

    private static @NotNull LootContext createExecutionContext(
            @NotNull ServerLevel level,
            @NotNull ServerPlayer player,
            @NotNull ItemStack inputStack
    ) {
        return JolCraftRecipeContexts.create(
                level,
                EXECUTION_CONTEXT_PARAMS,
                builder -> builder
                        .withParameter(
                                LootContextParams.THIS_ENTITY,
                                player
                        )
                        .withParameter(
                                JolCraftRecipeContextParams.INPUT_ITEM,
                                inputStack
                        )
        );
    }

    private static @Nullable ResolvedRecipe findResolvedRecipe(
            @NotNull ServerLevel level,
            @NotNull ServerPlayer player,
            @NotNull HandInteractionRecipeInput rawInput
    ) {
        List<RecipeHolder<HandInteractionRecipe>> recipes =
                level.getRecipeManager()
                        .getRecipesFor(
                                JolCraftRecipes.HAND_INTERACTION_TYPE.get(),
                                rawInput,
                                level
                        );

        for (RecipeHolder<HandInteractionRecipe> holder : recipes) {
            HandInteractionRecipe recipe =
                    holder.value();

            if (recipe.requireSneaking()
                    && !player.isShiftKeyDown()) {
                continue;
            }

            HandMapping mapping =
                    resolveMapping(
                            level,
                            recipe,
                            rawInput
                    );

            if (mapping != null) {
                return new ResolvedRecipe(
                        holder.id(),
                        recipe,
                        mapping
                );
            }
        }

        return null;
    }

    private static @Nullable HandMapping resolveMapping(
            @NotNull ServerLevel level,
            @NotNull HandInteractionRecipe recipe,
            @NotNull HandInteractionRecipeInput input
    ) {
        ItemStack main =
                input.ingredientA();

        ItemStack off =
                input.ingredientB();

        if (matchesMapping(
                level,
                recipe,
                main,
                off
        )) {
            return new HandMapping(
                    main,
                    off,
                    InteractionHand.MAIN_HAND
            );
        }

        if (matchesMapping(
                level,
                recipe,
                off,
                main
        )) {
            return new HandMapping(
                    off,
                    main,
                    InteractionHand.OFF_HAND
            );
        }

        return null;
    }

    private static boolean matchesMapping(
            @NotNull ServerLevel level,
            @NotNull HandInteractionRecipe recipe,
            @NotNull ItemStack stackA,
            @NotNull ItemStack stackB
    ) {
        return recipe.matchesOrdered(
                level,
                stackA,
                stackB
        )
                && recipe.actionsSatisfied(
                stackA,
                stackB
        );
    }

    private record HandMapping(
            ItemStack stackA,
            ItemStack stackB,
            InteractionHand swingHand
    ) {}

    private record ResolvedRecipe(
            ResourceLocation id,
            HandInteractionRecipe recipe,
            HandMapping mapping
    ) {}

    private static void cancel(
            @NotNull PlayerInteractEvent.RightClickItem event
    ) {
        event.setCancellationResult(
                InteractionResult.SUCCESS
        );

        event.setCanceled(true);
    }
}