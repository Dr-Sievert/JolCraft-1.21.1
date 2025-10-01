package net.sievert.jolcraft.entity.custom.dwarf;

import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfBeardColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfEyeColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfVariant;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.data.DwarfData;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchant;

import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractBreedingEntity extends AgeableMob implements DwarfData {

    protected int inLove;

    @Nullable
    protected UUID loveCause;

    protected AbstractBreedingEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    public static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(AbstractBreedingEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BEARD_COLOR =
            SynchedEntityData.defineId(AbstractBreedingEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> EYE_COLOR =
            SynchedEntityData.defineId(AbstractBreedingEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(BEARD_COLOR, 0);
        builder.define(EYE_COLOR, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getData(VARIANT));
        compound.putInt("Beard", this.getData(BEARD_COLOR));
        compound.putInt("Eye", this.getData(EYE_COLOR));
        compound.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            compound.putUUID("LoveCause", this.loveCause);
        }
        compound.putInt("Age", this.getAge());
        compound.putInt("ForcedAge", this.forcedAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setData(VARIANT, compound.getInt("Variant"));
        setData(BEARD_COLOR, compound.getInt("Beard"));
        setData(EYE_COLOR, compound.getInt("Eye"));
        this.inLove = compound.getInt("InLove");
        this.loveCause = compound.hasUUID("LoveCause") ? compound.getUUID("LoveCause") : null;
        this.setAge(compound.getInt("Age"));
        this.forcedAge = compound.getInt("ForcedAge");
    }

    @Override
    public <T> void setData(EntityDataAccessor<T> accessor, T value) {
        this.entityData.set(accessor, value);
    }

    @Override
    public <T> T getData(EntityDataAccessor<T> accessor) {
        return this.entityData.get(accessor);
    }

    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.BREAD);
    }

    public void playEatingSound() {
        this.playSound(SoundEvents.PLAYER_BURP, 1.0F, this.getVoicePitch());
    }

    public boolean canFallInLove() {
        return this.inLove <= 0;
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public void setInLoveTime(int inLove) {
        this.inLove = inLove;
    }

    public int getInLoveTime() {
        return this.inLove;
    }

    public void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        if (player.level().isClientSide) return;
        if (player.isCreative()) return;
        if (stack.isEmpty() || stack.getCount() == 0) return;
        int initialCount = stack.getCount();
        UseRemainder useRemainder = stack.get(DataComponents.USE_REMAINDER);
        stack.consume(1, player);
        if (useRemainder != null) {
            ItemStack remainderStack = useRemainder.convertIntoRemainder(
                    stack,
                    initialCount,
                    false,
                    player::handleExtraItemsCreatedOnUse
            );
            player.setItemInHand(hand, remainderStack);
        }
    }

    public void setInLove(@Nullable Player player) {
        this.inLove = 600;
        if (player != null) {
            this.loveCause = player.getUUID();
        }

        this.level().broadcastEntityEvent(this, (byte)18);
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        } else {
            Player player = this.level().getPlayerByUUID(this.loveCause);
            return player instanceof ServerPlayer ? (ServerPlayer)player : null;
        }
    }

    public boolean canMate(AbstractBreedingEntity partner) {
        if (partner == this) {
            return false;
        } else {
            return partner instanceof AbstractBreedingEntity && this.isInLove() && partner.isInLove();
        }
    }

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource source, float amount) {
        this.resetLove();
        super.actuallyHurt(level, source, amount);
    }

    public void spawnChildFromBreeding(ServerLevel level, AbstractBreedingEntity partner) {
        AgeableMob ageablemob = this.getBreedOffspring(level, partner);
        final net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, partner, ageablemob);
        final boolean cancelled = NeoForge.EVENT_BUS.post(event).isCanceled();
        ageablemob = event.getChild();
        if (cancelled) {
            this.setAge(6000);
            partner.setAge(6000);
            this.resetLove();
            partner.resetLove();
            return;
        }
        if (ageablemob != null) {
            ageablemob.setBaby(true);
            ageablemob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.finalizeSpawnChildFromBreeding(level, partner, ageablemob);
            level.addFreshEntityWithPassengers(ageablemob);
        }
    }

    public void finalizeSpawnChildFromBreeding(ServerLevel level, AbstractBreedingEntity dwarf, @Nullable AgeableMob baby) {
        this.setAge(6000);
        dwarf.setAge(6000);
        this.resetLove();
        dwarf.resetLove();
        level.broadcastEntityEvent(this, (byte)18);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            level.addFreshEntity(new ExperienceOrb(level, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        DwarfEntity baby = JolCraftEntities.DWARF.get().create(level, EntitySpawnReason.BREEDING);
        DwarfVariant variant = Util.getRandom(DwarfVariant.values(), this.random);
        DwarfBeardColor beard = Util.getRandom(DwarfBeardColor.values(), this.random);
        DwarfEyeColor eye = Util.getRandom(DwarfEyeColor.values(), this.random);
        assert baby != null;
        baby.setData(VARIANT, variant.getId());
        baby.setData(BEARD_COLOR, beard.getId());
        baby.setData(EYE_COLOR, eye.getId());
        return baby;
    }
}
