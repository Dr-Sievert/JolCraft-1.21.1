package net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.bounty;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty.*;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import java.util.List;

public class BountyCrateDwarfAction extends InspectDwarfAction {

    public int ticksRemaining = 0;
    private final BountyType type = BountyHelper.getBountyType(itemstack);

    public BountyCrateDwarfAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {return DwarfActionType.Subtype.BOUNTY_CRATE;}

    @Override
    public void start() {
        this.ticksRemaining = 40;
        startInspect(dwarf, player, hand, itemstack);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;

        if (type != BountyType.MERCHANT && type != BountyType.MINER) return;

        if (ticksRemaining == 25) {
            JolCraftSoundHelper.entity(
                    dwarf,
                    SoundEvents.VILLAGER_WORK_FISHERMAN
            );
        }
        if (ticksRemaining == 15) {
            PlaySound.dwarfYes(dwarf);
        }
        if (ticksRemaining == 10) {
            if (type == BountyType.MERCHANT) {
                spawnBountyParticles(1.0F, 0.84F, 0.0F, 0.5F);
            }
            if (type == BountyType.MINER) {
                spawnBountyParticles(0.25F, 0.25F, 0.30F, 0.7F);
            }
        }
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

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, this.previousMainHandItem);
        this.previousMainHandItem = ItemStack.EMPTY;

        if (dwarf.level().isClientSide) {
            return;
        }

        BountyData data = itemstack.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) {
            return;
        }

        BountyTier tier = BountyTier.fromValue(data.tier());

        JolCraftLogs.info(
                JolCraftLogTags.PLAYER,
                "{} completed a {} {} bounty at {}",
                player.getDisplayName().getString(),
                tier.getDisplayName().getString().toLowerCase(),
                type.toString().toLowerCase(),
                JolCraftLogs.roundedPos(player)
        );

        Vec3 start = dwarf.position().add(0.0, dwarf.getEyeHeight(), 0.0);
        Vec3 target = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);
        Vec3 velocity = target.subtract(start).normalize().scale(0.4);

        List<ItemStack> rewards = BountyGenerator.getReward(data, dwarf.getRandom());
        for (ItemStack reward : rewards) {
            if (!reward.isEmpty()) {
                ItemEntity thrownReward = new ItemEntity(dwarf.level(), start.x, start.y, start.z, reward);
                thrownReward.setDeltaMovement(velocity);
                thrownReward.setPickUpDelay(10);
                dwarf.level().addFreshEntity(thrownReward);
            }
        }

        int xp = switch (data.tier()) {
            case 1 -> 10;
            case 2 -> 35;
            case 3 -> 50;
            case 4 -> 65;
            default -> 0;
        };

        dwarf.dwarfXp += xp;
        dwarf.level().addFreshEntity(new ExperienceOrb(
                dwarf.level(),
                dwarf.getX(),
                dwarf.getY() + 1.0,
                dwarf.getZ(),
                3 + dwarf.getRandom().nextInt(3)
        ));

        JolCraftSoundHelper.entity(dwarf, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8F, 1.2F);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.SNOWBALL_THROW, 0.5F, 0.7F);

        dwarf.restockBountiesOnly();
    }
}