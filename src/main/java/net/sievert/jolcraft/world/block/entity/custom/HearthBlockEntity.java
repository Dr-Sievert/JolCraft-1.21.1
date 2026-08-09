package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.sievert.jolcraft.world.entity.attachment.player.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.custom.HearthBlock;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.base.TickingBlockEntity;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HearthBlockEntity extends BlockEntity implements TickingBlockEntity {

    private static final String NBT_OWNER = JolCraftDictionary.OWNER;
    private static final String NBT_LIT_CREATIVE = JolCraftStrings.underscored(JolCraftDictionary.LIT, JolCraftDictionary.CREATIVE);

    @Nullable private UUID owner;
    private boolean litCreative = false;

    private static final int RADIUS = 16;
    public static final int RADIUS_SQ = RADIUS * RADIUS;

    private static final int EFFECT_DURATION = MobEffectInstance.INFINITE_DURATION;
    private static final int EFFECT_AMPLIFIER = 0;

    public HearthBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.HEARTH.get(), pos, state);
    }

    public ItemInteractionResult handleUse(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            @NotNull ServerPlayer player,
            @NotNull InteractionHand hand
    ) {
        if (state.getValue(HearthBlock.LIT)) {
            if (player.isCreative()){
                deactivate();
            }
            if (player.getItemInHand(hand).is(Tags.Items.BUCKETS_WATER)){
                JolCraftItemHelper.consume(player, hand);
                deactivate();
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (this.owner != null && !isOwner(player)){
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_OWNER)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (player.isCreative()) {
            activate(player);
            return ItemInteractionResult.SUCCESS;
        }

        if (isUnsafeArea(player)) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_NOT_SAFE)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (hasNoNearbyBed(player)) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_NO_BED_NEARBY)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (!isFuel(stack)){
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_NEED_FUEL)
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (HearthAttachmentHelper.hasLitToday(player)) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_HEARTH_COOLDOWN)
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            return ItemInteractionResult.SUCCESS;
        }

        player.awardStat(Stats.ITEM_USED.get(player.getItemInHand(hand).getItem()));
        JolCraftItemHelper.consume(player, hand);
        activate(player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void tickServer() {
        if (!(this.level instanceof ServerLevel serverLevel) || this.owner == null) return;

        BlockState state = this.getBlockState();
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(this.owner);

        if (player == null) {
            if (state.getValue(HearthBlock.LIT) && !this.litCreative) {
                setLitOff();
            }
            return;
        }

        if (this.litCreative) {
            if (!state.getValue(HearthBlock.LIT)) setLit();
            applyHomesteadEffect(player);
            return;
        }

        if (hasNoNearbyBed(player)
                || !HearthAttachmentHelper.isActiveHearth(player, this.worldPosition)) {
            deactivate();
            return;
        }

        if (!state.getValue(HearthBlock.LIT)) {
            setLit();
        }

        applyHomesteadEffect(player);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);

        if (this.owner != null) {
            tag.putUUID(NBT_OWNER, this.owner);
        }

        if (this.litCreative) {
            tag.putBoolean(NBT_LIT_CREATIVE, true);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);

        this.owner = tag.hasUUID(NBT_OWNER) ? tag.getUUID(NBT_OWNER) : null;
        this.litCreative = tag.getBoolean(NBT_LIT_CREATIVE);
    }

    private boolean isFuel(ItemStack stack){
        return stack.getBurnTime(null ) > 10000;
    }

    public @Nullable UUID getOwner() {
        return this.owner;
    }

    private boolean isOwner(ServerPlayer player) {
        return this.owner != null && this.owner.equals(player.getUUID());
    }

    private void setOwner(ServerPlayer player){
        this.owner = player.getUUID();
        HearthAttachmentHelper.setActiveHearthPos(player, this.worldPosition);
    }

    private void clearOwner(){
        this.owner = null;
    }

    private void setLit() {
        setLit(true);
    }

    private void setLitOff() {
        setLit(false);
    }

    private void setLit(boolean lit) {
        if (this.level == null) return;

        BlockPos pos = this.worldPosition;
        BlockState lower = this.level.getBlockState(pos);

        if (!(lower.getBlock() instanceof HearthBlock)) return;
        if (lower.getValue(HearthBlock.HALF) != DoubleBlockHalf.LOWER) return;

        setLitState(pos, lower, lit);

        BlockPos upperPos = pos.above();
        BlockState upper = this.level.getBlockState(upperPos);
        if (upper.is(lower.getBlock()) && upper.getValue(HearthBlock.HALF) == DoubleBlockHalf.UPPER) {
            setLitState(upperPos, upper, lit);
        }

        if(lit){
            JolCraftSoundHelper.block(this.level, this.worldPosition, SoundEvents.BLAZE_SHOOT, 1.0F, 0.8F);
        } else{
            JolCraftSoundHelper.block(this.level, this.worldPosition, SoundEvents.GENERIC_EXTINGUISH_FIRE, 1.0F, 0.8F);
        }
    }

    private void setLitState(BlockPos pos, BlockState state, boolean lit) {
        if (this.level == null) return;
        if (state.getValue(HearthBlock.LIT) != lit) {
            this.level.setBlock(pos, state.setValue(HearthBlock.LIT, lit), Block.UPDATE_ALL);
        }
    }

    private void activate(ServerPlayer player) {
        setLit();
        setOwner(player);
        if (player.isCreative()){
            this.litCreative = true;
            return;
        }
        HearthAttachmentHelper.setLastLitToday(player);
        this.litCreative = false;
    }

    private void deactivate() {
        if (this.level instanceof ServerLevel serverLevel && this.owner != null) {
            ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(this.owner);
            if (HearthAttachmentHelper.isActiveHearth(player, this.worldPosition)) {
                HearthAttachmentHelper.clearActiveHearthPos(player);
            }
        }

        setLitOff();
        clearOwner();
        this.litCreative = false;
    }

    private boolean isUnsafeArea(ServerPlayer player) {
        if (this.level == null) return true;

        AABB area = new AABB(this.worldPosition).inflate(8, 5, 8);

        return !this.level.getEntitiesOfClass(
                Monster.class,
                area,
                mob -> mob.isPreventingPlayerRest(player)
        ).isEmpty();
    }

    private boolean hasNoNearbyBed(ServerPlayer player) {
        if (this.level == null) return true;

        BlockPos bedPos = player.getRespawnPosition();
        return bedPos == null
                || !player.getRespawnDimension().equals(this.level.dimension())
                || !(this.level.getBlockState(bedPos).getBlock() instanceof BedBlock)
                || !(bedPos.distSqr(this.worldPosition) <= RADIUS_SQ);
    }

    private void applyHomesteadEffect(ServerPlayer player) {
        if (player.hasEffect(JolCraftEffects.HOMESTEAD)) return;
        if (player.blockPosition().distSqr(this.worldPosition) > RADIUS_SQ) return;

        player.addEffect(new MobEffectInstance(
                JolCraftEffects.HOMESTEAD,
                EFFECT_DURATION,
                EFFECT_AMPLIFIER,
                false,
                false,
                true
        ));
    }
}
