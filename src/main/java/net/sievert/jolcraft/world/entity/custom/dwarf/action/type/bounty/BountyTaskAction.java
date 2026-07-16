package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.bounty;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyTaskAction extends InspectDwarfAction {

    private static final int START_TICKS = 40;

    private static final int FX_SOUND_1_TICKS = 25;
    private static final int FX_SOUND_2_TICKS = 15;

    private static final LootContextParamSet CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    private int ticksRemaining;

    private ItemStack plannedResult;

    @Nullable
    private SoundOutput.GeneratedSound sound1;

    @Nullable
    private SoundOutput.GeneratedSound sound2;

    public BountyTaskAction(
            AbstractDwarfEntity dwarf,
            Player player,
            InteractionHand hand,
            ItemStack itemstack
    ) {
        super(dwarf, player, hand, itemstack);

        this.ticksRemaining = 0;
        this.plannedResult = ItemStack.EMPTY;
    }

    @Override
    public @NotNull DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.BOUNTY;
    }

    @Override
    public void start() {
        ticksRemaining = START_TICKS;
        plannedResult = ItemStack.EMPTY;
        sound1 = null;
        sound2 = null;

        if (dwarf.level() instanceof ServerLevel level) {
            ItemStack redeemStack = itemstack;

            if (!redeemStack.isEmpty()) {
                planFromAnyValidTaskRecipe(
                        level,
                        redeemStack
                );
            }
        }

        startInspect(
                dwarf,
                player,
                hand,
                itemstack
        );
    }

    private void planFromAnyValidTaskRecipe(
            ServerLevel level,
            ItemStack redeemStack
    ) {
        if (redeemStack.isEmpty()) {
            return;
        }

        BountyRecipeInput input =
                BountyRecipeInput.of(redeemStack)
                        .result()
                        .orElse(null);

        if (input == null) {
            return;
        }

        LootContext context = createContext(level);

        level.getServer()
                .getRecipeManager()
                .getRecipeFor(
                        JolCraftRecipes.BOUNTY_TASK_TYPE.get(),
                        input,
                        level
                )
                .map(RecipeHolder::value)
                .ifPresent(recipe -> {
                    plannedResult = recipe.createBounty(
                            context,
                            input
                    );

                    recipe.generateSound1(
                            context,
                            input,
                            generated -> {
                                if (sound1 == null) {
                                    sound1 = generated;
                                }
                            }
                    );

                    recipe.generateSound2(
                            context,
                            input,
                            generated -> {
                                if (sound2 == null) {
                                    sound2 = generated;
                                }
                            }
                    );
                });
    }

    private @NotNull LootContext createContext(
            ServerLevel level
    ) {
        return JolCraftRecipeContexts.create(
                level,
                dwarf.getRandom(),
                CONTEXT_PARAMS,
                builder -> builder
                        .withParameter(
                                LootContextParams.THIS_ENTITY,
                                dwarf
                        )
                        .withParameter(
                                LootContextParams.ORIGIN,
                                dwarf.position()
                        )
        );
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }

        if (ticksRemaining == FX_SOUND_1_TICKS) {
            playSound(sound1);
        }

        if (ticksRemaining == FX_SOUND_2_TICKS) {
            playSound(sound2);
        }
    }

    private void playSound(
            @Nullable SoundOutput.GeneratedSound sound
    ) {
        if (sound == null) {
            return;
        }

        JolCraftSoundHelper.entity(
                dwarf,
                sound.sound().value(),
                sound.volume(),
                sound.pitch()
        );
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        if (!(dwarf.level() instanceof ServerLevel)) {
            return;
        }

        if (plannedResult.isEmpty()) {
            return;
        }

        dwarf.usePlayerItem(
                player,
                hand,
                itemstack
        );

        throwItem(
                dwarf,
                player,
                plannedResult
        );
    }
}