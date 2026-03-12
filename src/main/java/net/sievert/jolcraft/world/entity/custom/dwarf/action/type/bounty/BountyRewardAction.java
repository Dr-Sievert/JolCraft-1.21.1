package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.bounty;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyRewardAction extends InspectDwarfAction {

    private static final int START_TICKS = 40;

    private static final int FX_SOUND_TICKS = 25;
    private static final int FX_PARTICLES_TICKS = 10;

    private static final float THROW_SPEED = 0.4F;

    private static final double PARTICLE_SPREAD_X = 0.25D;
    private static final double PARTICLE_SPREAD_Y = 0.20D;
    private static final double PARTICLE_SPREAD_Z = 0.25D;
    private static final float PARTICLE_SPEED = 0.02F;

    private int ticksRemaining;

    @Nullable
    private SoundOutput rewardSound;

    private int plannedParticleCount;

    public BountyRewardAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
        this.ticksRemaining = 0;
        this.rewardSound = null;
        this.plannedParticleCount = 0;
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.BOUNTY_REWARD;
    }

    @Override
    public void start() {
        this.ticksRemaining = START_TICKS;
        this.rewardSound = null;
        this.plannedParticleCount = 0;

        if (dwarf.level() instanceof ServerLevel level) {
            planFxFromAnyValidRewardRecipe(level, this.itemstack);
        }

        startInspect(dwarf, player, hand, itemstack);
    }

    private void planFxFromAnyValidRewardRecipe(ServerLevel level, ItemStack redeemStack) {
        if (redeemStack.isEmpty()) {
            return;
        }
        if (!BountyRewardRecipe.isRewardBountyStack(redeemStack)) {
            return;
        }

        WorldContext ctx = makeCtx(level, player, dwarf);
        var inRes = BountyRecipeInput.of(ctx, redeemStack);
        if (inRes.error().isPresent()) {
            return;
        }

        BountyRecipeInput input = inRes.result().orElse(null);
        if (input == null) {
            return;
        }

        level.getServer().getRecipeManager()
                .recipeMap()
                .getRecipesFor(JolCraftRecipes.BOUNTY_REWARD_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .findFirst()
                .ifPresent(recipe -> {
                    this.rewardSound = recipe.sound();
                    this.plannedParticleCount = particleCountFor(input.tier());
                });
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }

        if (ticksRemaining == FX_SOUND_TICKS && rewardSound != null) {
            JolCraftSoundHelper.entity(
                    dwarf,
                    Objects.requireNonNull(rewardSound.resolveValue(player.registryAccess())),
                    rewardSound.volume(),
                    rewardSound.pitch()
            );
        }

        if (ticksRemaining == FX_PARTICLES_TICKS && plannedParticleCount > 0) {
            playRewardParticles(plannedParticleCount);
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        if (!(dwarf.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        dwarf.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
        this.previousMainHandItem = ItemStack.EMPTY;

        ItemStack redeemStack = this.itemstack;
        if (redeemStack.isEmpty()) {
            return;
        }
        if (BountyRewardRecipe.isIncompleteRewardBountyStack(redeemStack)) {
            return;
        }

        WorldContext ctx = makeCtx(serverLevel, player, dwarf);

        var inRes = BountyRecipeInput.of(ctx, redeemStack);
        if (inRes.error().isPresent()) {
            return;
        }

        BountyRecipeInput input = inRes.result().orElse(null);
        if (input == null) {
            return;
        }

        BountyRewardRecipe recipe = serverLevel.getServer().getRecipeManager()
                .recipeMap()
                .getRecipesFor(JolCraftRecipes.BOUNTY_REWARD_TYPE.get(), input, serverLevel)
                .map(RecipeHolder::value)
                .findFirst()
                .orElse(null);

        if (recipe == null) {
            return;
        }

        DwarfProfession redeemType = input.type();
        DwarfMerchantData.Level redeemTier = input.tier();

        JolCraftLogs.info(
                JolCraftLogTags.PLAYER,
                "{} completed a level {} {} bounty at {} in {}",
                player.getDisplayName().getString(),
                redeemTier.getId(),
                redeemType.professionName(),
                JolCraftLogs.roundedPos(player),
                serverLevel.dimension().location()
        );

        Vec3 start = dwarf.position().add(0.0, dwarf.getEyeHeight(), 0.0);
        Vec3 target = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);
        Vec3 velocity = target.subtract(start).normalize().scale(THROW_SPEED);

        for (Output output : recipe.roll(input, ctx)) {
            if (output instanceof Output.Items items) {
                for (ItemStack stack : items.stacksSafe()) {
                    if (stack == null || stack.isEmpty()) {
                        continue;
                    }
                    throwStack(serverLevel, start, velocity, stack);
                }
            }
        }

        int xp = xpForTier(redeemTier);

        dwarf.dwarfXp += xp;
        AbstractTradingEntity.triggerLevelUp(dwarf);

        serverLevel.addFreshEntity(new ExperienceOrb(
                serverLevel,
                dwarf.getX(),
                dwarf.getY() + 1.0,
                dwarf.getZ(),
                xp
        ));

        JolCraftSoundHelper.entity(dwarf, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8F, 1.2F);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.SNOWBALL_THROW, 0.5F, 0.7F);

        dwarf.usePlayerItem(player, hand, redeemStack);

        dwarf.restockBountiesOnly();
        player.awardStat(JolCraftStats.DWARVEN_BOUNTIES_COMPLETED.get());
    }

    private static int xpForTier(@NotNull DwarfMerchantData.Level tier) {
        return switch (tier) {
            case NOVICE -> 10;
            case APPRENTICE -> 35;
            case JOURNEYMAN -> 50;
            case EXPERT -> 65;
            case MASTER -> 80;
        };
    }

    private static int particleCountFor(@NotNull DwarfMerchantData.Level tier) {
        return switch (tier) {
            case NOVICE -> 6;
            case APPRENTICE -> 10;
            case JOURNEYMAN -> 14;
            case EXPERT -> 18;
            case MASTER -> 24;
        };
    }

    private static @NotNull WorldContext makeCtx(ServerLevel level, Player player, Entity self) {
        return new WorldContext(level, player, self);
    }

    private void playRewardParticles(int count) {
        if (count <= 0) {
            return;
        }

        JolCraftParticleHelper.spawn(
                dwarf.level(),
                ParticleTypes.FIREWORK,
                dwarf.getX(),
                dwarf.getY() + dwarf.getBbHeight() * 0.6D,
                dwarf.getZ(),
                count,
                PARTICLE_SPREAD_X,
                PARTICLE_SPREAD_Y,
                PARTICLE_SPREAD_Z,
                PARTICLE_SPEED
        );

        JolCraftSoundHelper.play(
                dwarf.level(),
                SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR,
                dwarf.getSoundSource(),
                dwarf.getX(),
                dwarf.getY() + 2.1D,
                dwarf.getZ(),
                1.0F,
                1.2F
        );
    }
}