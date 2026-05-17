package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.bounty;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyTaskAction extends InspectDwarfAction {

    private static final int START_TICKS = 40;

    private static final int FX_SOUND_1_TICKS = 25;
    private static final int FX_SOUND_2_TICKS = 15;

    private int ticksRemaining;
    private ItemStack plannedResult;

    @Nullable
    private SoundOutput sound1;

    @Nullable
    private SoundOutput sound2;

    public BountyTaskAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
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
        this.ticksRemaining = START_TICKS;
        this.plannedResult = ItemStack.EMPTY;
        this.sound1 = null;
        this.sound2 = null;

        if (dwarf.level() instanceof ServerLevel level) {
            ItemStack redeemStack = this.itemstack;
            if (!redeemStack.isEmpty()) {
                planFromAnyValidTaskRecipe(level, redeemStack);
            }
        }

        startInspect(dwarf, player, hand, itemstack);
    }

    private void planFromAnyValidTaskRecipe(ServerLevel level, ItemStack redeemStack) {
        if (redeemStack.isEmpty()) return;

        WorldContext ctx = makeCtx(player, dwarf);
        var inRes = BountyRecipeInput.of(ctx, redeemStack);
        if (inRes.error().isPresent()) return;

        BountyRecipeInput input = inRes.result().orElse(null);
        if (input == null) return;

        level.getServer().getRecipeManager()
                .getRecipeFor(JolCraftRecipes.BOUNTY_TASK_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .ifPresent(r -> {
                    this.sound1 = r.sound1();
                    this.sound2 = r.sound2();

                    this.plannedResult = r.assemble(input, level.registryAccess());
                });
    }

    private static @NotNull WorldContext makeCtx(Player player, Entity self) {
        return new WorldContext(player, self);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;

        if (ticksRemaining == FX_SOUND_1_TICKS && sound1 != null) {
            JolCraftSoundHelper.entity(
                    dwarf,
                    Objects.requireNonNull(sound1.resolveValue(player.registryAccess())),
                    sound1.volume(),
                    sound1.pitch()
            );
        }

        if (ticksRemaining == FX_SOUND_2_TICKS && sound2 != null) {
            JolCraftSoundHelper.entity(
                    dwarf,
                    Objects.requireNonNull(sound2.resolveValue(player.registryAccess())),
                    sound2.volume(),
                    sound2.pitch()
            );
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        if (!(dwarf.level() instanceof ServerLevel)) return;
        if (plannedResult.isEmpty()) return;

        dwarf.usePlayerItem(player, hand, itemstack);
        throwItem(dwarf, player, plannedResult);
    }
}