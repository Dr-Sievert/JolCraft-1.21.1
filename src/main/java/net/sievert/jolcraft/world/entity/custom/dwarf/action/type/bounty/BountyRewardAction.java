package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.bounty;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.RewardCrateSource;
import net.sievert.jolcraft.world.player.JolCraftStats;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.context.JolCraftRecipeContexts;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BountyRewardAction extends InspectDwarfAction {

    private static final int START_TICKS = 40;

    private static final int FX_SOUND_TICKS = 25;
    private static final int FX_PARTICLES_TICKS = 10;

    private static final float THROW_SPEED = 0.4F;

    private static final LootContextParamSet CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    private int ticksRemaining;

    @Nullable
    private SoundOutput.GeneratedSound rewardSound;

    private int plannedParticleCount;

    public BountyRewardAction(
            AbstractDwarfEntity dwarf,
            Player player,
            InteractionHand hand,
            ItemStack itemstack
    ) {
        super(
                dwarf,
                player,
                hand,
                itemstack
        );

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
        ticksRemaining = START_TICKS;
        rewardSound = null;
        plannedParticleCount = 0;

        if (dwarf.level() instanceof ServerLevel level) {
            planFxFromAnyValidRewardRecipe(
                    level,
                    itemstack
            );
        }

        startInspect(
                dwarf,
                player,
                hand,
                itemstack
        );
    }

    private void planFxFromAnyValidRewardRecipe(
            ServerLevel level,
            ItemStack redeemStack
    ) {
        if (redeemStack.isEmpty()) {
            return;
        }

        if (!BountyRewardRecipe.isRewardBountyStack(
                redeemStack
        )) {
            return;
        }

        BountyRecipeInput input =
                BountyRecipeInput.of(
                        redeemStack
                ).result().orElse(null);

        if (input == null) {
            return;
        }

        LootContext context =
                createContext(
                        level
                );

        level.getRecipeManager()
                .getRecipeFor(
                        JolCraftRecipes.BOUNTY_REWARD_TYPE.get(),
                        input,
                        level
                )
                .map(RecipeHolder::value)
                .ifPresent(recipe -> {
                    recipe.generateSound(
                            context,
                            input,
                            generated -> {
                                if (rewardSound == null) {
                                    rewardSound = generated;
                                }
                            }
                    );

                    plannedParticleCount =
                            particleCountFor(
                                    input.tier()
                            );
                });
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }

        if (ticksRemaining == FX_SOUND_TICKS) {
            playRewardSound();
        }

        if (ticksRemaining == FX_PARTICLES_TICKS
                && plannedParticleCount > 0) {
            playRewardParticles(
                    plannedParticleCount
            );
        }
    }

    private void playRewardSound() {
        if (rewardSound == null) {
            return;
        }

        JolCraftSoundHelper.entity(
                dwarf,
                rewardSound.sound().value(),
                rewardSound.volume(),
                rewardSound.pitch()
        );
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

        dwarf.setItemSlot(
                EquipmentSlot.MAINHAND,
                previousMainHandItem
        );

        previousMainHandItem = ItemStack.EMPTY;

        ItemStack redeemStack =
                itemstack.copy();

        if (redeemStack.isEmpty()) {
            return;
        }

        if (BountyRewardRecipe.isIncompleteRewardBountyStack(
                redeemStack
        )) {
            return;
        }

        BountyRecipeInput input =
                BountyRecipeInput.of(
                        redeemStack
                ).result().orElse(null);

        if (input == null) {
            return;
        }

        RecipeHolder<BountyRewardRecipe> recipeHolder =
                serverLevel.getRecipeManager()
                        .getRecipeFor(
                                JolCraftRecipes.BOUNTY_REWARD_TYPE.get(),
                                input,
                                serverLevel
                        )
                        .orElse(null);

        if (recipeHolder == null) {
            return;
        }

        DwarfProfession redeemType =
                input.type();

        DwarfMerchantData.Level redeemTier =
                input.tier();

        JolCraftLogs.info(
                JolCraftLogTags.PLAYER,
                "{} completed a level {} {} bounty at {} in {}",
                player.getDisplayName().getString(),
                redeemTier.getId(),
                redeemType.professionName(),
                JolCraftLogs.roundedPos(player),
                serverLevel.dimension().location()
        );

        ItemStack rewardCrate = JolCraftItems.REWARD_CRATE.toStack();

        rewardCrate.set(
                DataComponents.RARITY,
                rarityForTier(
                        input.tier()
                )
        );

        rewardCrate.set(
                JolCraftDataComponents.REWARD_CRATE_SOURCE.get(),
                RewardCrateSource.recipe(
                        recipeHolder.id()
                )
        );

        Vec3 start =
                dwarf.position()
                        .add(
                                0.0D,
                                dwarf.getEyeHeight(),
                                0.0D
                        );

        Vec3 target =
                player.position()
                        .add(
                                0.0D,
                                player.getBbHeight() * 0.5D,
                                0.0D
                        );

        Vec3 direction =
                target.subtract(
                        start
                );

        Vec3 velocity =
                direction.lengthSqr() > 0.0D
                        ? direction.normalize()
                        .scale(THROW_SPEED)
                        : Vec3.ZERO;

        throwStack(
                serverLevel,
                start,
                velocity,
                rewardCrate
        );

        int xp =
                xpForTier(
                        redeemTier
                );

        dwarf.dwarfXp += xp;

        AbstractTradingEntity.triggerLevelUp(
                dwarf
        );

        serverLevel.addFreshEntity(
                new ExperienceOrb(
                        serverLevel,
                        dwarf.getX(),
                        dwarf.getY() + 1.0D,
                        dwarf.getZ(),
                        xp
                )
        );

        JolCraftSoundHelper.entity(
                dwarf,
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                0.8F,
                1.2F
        );

        JolCraftSoundHelper.entity(
                dwarf,
                SoundEvents.SNOWBALL_THROW,
                0.5F,
                0.7F
        );

        dwarf.restockBountiesOnly();

        player.awardStat(
                JolCraftStats.DWARVEN_BOUNTIES_COMPLETED.get()
        );
    }

    private @NotNull LootContext createContext(
            @NotNull ServerLevel level
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

    private static int xpForTier(
            @NotNull DwarfMerchantData.Level tier
    ) {
        return switch (tier) {
            case NOVICE -> 10;
            case APPRENTICE -> 35;
            case JOURNEYMAN -> 50;
            case EXPERT -> 65;
            case MASTER -> 80;
        };
    }

    private static @NotNull Rarity rarityForTier(
            @NotNull DwarfMerchantData.Level tier
    ) {
        return switch (tier) {
            case NOVICE -> Rarity.COMMON;
            case APPRENTICE -> Rarity.UNCOMMON;
            case JOURNEYMAN -> Rarity.RARE;
            case EXPERT -> Rarity.EPIC;
            case MASTER -> JolCraftEnumExtensions.Rarity.LEGENDARY.getValue();
        };
    }

    private static int particleCountFor(
            @NotNull DwarfMerchantData.Level tier
    ) {
        return switch (tier) {
            case NOVICE -> 6;
            case APPRENTICE -> 10;
            case JOURNEYMAN -> 14;
            case EXPERT -> 18;
            case MASTER -> 24;
        };
    }

    private void playRewardParticles(
            int count
    ) {
        if (count <= 0) {
            return;
        }

        dwarf.spawnColoredParticles(
                1.0F,
                0.84F,
                0.0F,
                1.0F,
                count,
                0.5D
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