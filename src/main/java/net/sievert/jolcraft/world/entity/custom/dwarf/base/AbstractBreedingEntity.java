package net.sievert.jolcraft.world.entity.custom.dwarf.base;

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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfBeardColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfEyeColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfVariant;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.world.entity.util.EntityData;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractBreedingEntity extends AgeableMob implements EntityData {

    private static final String NBT_VARIANT = JolCraftDataKeys.VARIANT;
    private static final String NBT_BEARD = JolCraftDataKeys.BEARD;
    private static final String NBT_EYE = JolCraftDataKeys.EYE;
    private static final String NBT_IN_LOVE = JolCraftDataKeys.IN_LOVE;
    private static final String NBT_LOVE_CAUSE = JolCraftDataKeys.LOVE_CAUSE;
    private static final String NBT_AGE = JolCraftDataKeys.AGE;
    private static final String NBT_FORCED_AGE = JolCraftDataKeys.FORCED_AGE;

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
        compound.putInt(NBT_VARIANT, this.getData(VARIANT));
        compound.putInt(NBT_BEARD, this.getData(BEARD_COLOR));
        compound.putInt(NBT_EYE, this.getData(EYE_COLOR));
        compound.putInt(NBT_IN_LOVE, this.inLove);
        if (this.loveCause != null) {
            compound.putUUID(NBT_LOVE_CAUSE, this.loveCause);
        }
        compound.putInt(NBT_AGE, this.getAge());
        compound.putInt(NBT_FORCED_AGE, this.forcedAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setData(VARIANT, compound.getInt(NBT_VARIANT));
        setData(BEARD_COLOR, compound.getInt(NBT_BEARD));
        setData(EYE_COLOR, compound.getInt(NBT_EYE));
        this.inLove = compound.getInt(NBT_IN_LOVE);
        this.loveCause = compound.hasUUID(NBT_LOVE_CAUSE) ? compound.getUUID(NBT_LOVE_CAUSE) : null;
        this.setAge(compound.getInt(NBT_AGE));
        this.forcedAge = compound.getInt(NBT_FORCED_AGE);
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
        JolCraftSoundHelper.entity(this, SoundEvents.PLAYER_BURP);
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
        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, partner, ageablemob);
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
        if (baby == null) return null;

        DwarfVariant variant = Util.getRandom(DwarfVariant.values(), this.random);
        DwarfBeardColor beard = Util.getRandom(DwarfBeardColor.values(), this.random);
        DwarfEyeColor eye = Util.getRandom(DwarfEyeColor.values(), this.random);

        baby.setData(VARIANT, variant.getId());
        baby.setData(BEARD_COLOR, beard.getId());
        baby.setData(EYE_COLOR, eye.getId());
        return baby;
    }
}