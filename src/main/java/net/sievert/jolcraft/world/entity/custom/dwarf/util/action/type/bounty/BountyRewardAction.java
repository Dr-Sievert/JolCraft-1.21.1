package net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.bounty;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractTradingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.BountyGenerator;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public final class BountyRewardAction extends InspectDwarfAction {

    private int ticksRemaining;
    private final BountyType type;

    public BountyRewardAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
        this.type = BountyRecipe.readType(itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.BOUNTY_REWARD;
    }

    @Override
    public void start() {
        this.ticksRemaining = 40;
        startInspect(dwarf, player, hand, itemstack);
    }
    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;

        if (type == BountyType.UNKNOWN) return;

        if (ticksRemaining == 25) {
            SoundEvent rewardSound = dwarf.getBountyRewardSound();
            if (rewardSound != null) {
                JolCraftSoundHelper.entity(dwarf, rewardSound);
            }
        }

        if (ticksRemaining == 15) {
            PlaySound.dwarfYes(dwarf);
        }

        if (ticksRemaining == 10) {
            var particles = dwarf.getBountyRewardParticles();
            if (particles != null) {
                spawnBountyParticles(
                        particles.r(),
                        particles.g(),
                        particles.b(),
                        particles.scale()
                );
            }
        }
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        if (!(dwarf.level() instanceof ServerLevel serverLevel)) return;
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
        this.previousMainHandItem = ItemStack.EMPTY;

        ItemStack redeemStack = this.itemstack;
        if (redeemStack.isEmpty()) return;

        if (!BountyRewardRecipe.isCompletedRewardBountyStack(redeemStack)) return;

        BountyType redeemType = BountyRecipe.readType(redeemStack);
        BountyTier redeemTier = BountyRecipe.readTier(redeemStack);
        if (redeemType == BountyType.UNKNOWN || redeemTier == BountyTier.UNKNOWN) return;

        JolCraftLogs.info(
                JolCraftLogTags.PLAYER,
                "{} completed a {} {} bounty at {} in {}",
                player.getDisplayName().getString(),
                redeemTier.getDisplayName().getString().toLowerCase(),
                redeemType.getId(),
                JolCraftLogs.roundedPos(player),
                serverLevel.dimension().location()
        );

        Vec3 start = dwarf.position().add(0.0, dwarf.getEyeHeight(), 0.0);
        Vec3 target = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);
        Vec3 velocity = target.subtract(start).normalize().scale(0.4);

        ItemStack mainReward = BountyGenerator.Reward.roll(serverLevel, redeemStack, BountyRewardRecipe.RewardPool.MAIN);
        throwStack(serverLevel, start, velocity, mainReward);
        ItemStack bonusReward = BountyGenerator.Reward.roll(serverLevel, redeemStack, BountyRewardRecipe.RewardPool.BONUS);
        throwStack(serverLevel, start, velocity, bonusReward);

        int xp = switch (redeemTier.getId()) {
            case 1 -> 10;
            case 2 -> 35;
            case 3 -> 50;
            case 4 -> 65;
            case 5 -> 80;
            default -> 0;
        };

        dwarf.dwarfXp += xp;
        AbstractTradingEntity.triggerLevelUp(dwarf);

        serverLevel.addFreshEntity(new ExperienceOrb(
                serverLevel,
                dwarf.getX(),
                dwarf.getY() + 1.0,
                dwarf.getZ(),
                3 + dwarf.getRandom().nextInt(3)
        ));

        JolCraftSoundHelper.entity(dwarf, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8F, 1.2F);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.SNOWBALL_THROW, 0.5F, 0.7F);

        dwarf.usePlayerItem(player, hand, redeemStack);

        dwarf.restockBountiesOnly();
        player.awardStat(JolCraftStats.DWARVEN_BOUNTIES_COMPLETED.get());
    }

    private void spawnBountyParticles(float r, float g, float b, float alpha) {
        dwarf.spawnColoredParticles(r, g, b, alpha, 10, 1.0D);
        JolCraftSoundHelper.play(
                dwarf.level(),
                SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR,
                dwarf.getSoundSource(),
                dwarf.getX(),
                dwarf.getY() + 1.0D,
                dwarf.getZ(),
                1.0F,
                1.2F
        );
    }
}