package net.sievert.jolcraft.block.entity.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sievert.jolcraft.block.custom.FermentingCauldronBlock;
import net.sievert.jolcraft.block.custom.FermentingStage;
import net.sievert.jolcraft.block.custom.crop.HopsType;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FermentingCauldronBlockEntity extends BlockEntity {

    private int fermentationProgress = 0;
    private final int maxFermentationProgress = 100;
    private int bubbleCooldown = 0;
    private int yeastTickDelay = 12; //How many times we multiply 5 seconds (100 ticks)
    private int yeastTickCounter = 0;
    private int brewTickDelay = 60;
    private int brewTickCounter = 0;


    public FermentingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.FERMENTING_CAULDRON.get(), pos, state);
    }

    private final Set<HopsType> addedHops = new HashSet<>();

    public String getHopsString() {
        return addedHops.stream()
                .map(HopsType::name)
                .sorted() // optional, makes the string deterministic
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("FermentationProgress", fermentationProgress);
        tag.putInt("BubbleCooldown", bubbleCooldown);
        tag.putIntArray("AddedHops", addedHops.stream().mapToInt(HopsType::ordinal).toArray());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fermentationProgress = tag.getInt("FermentationProgress");
        bubbleCooldown = tag.getInt("BubbleCooldown");
        int[] hopsArray = tag.getIntArray("AddedHops");
        addedHops.clear();
        for (int hop : hopsArray) {
            addedHops.add(HopsType.values()[hop]);
        }
    }

    public boolean addHop(HopsType hop) {
        if (addedHops.contains(hop)) {
            return false;
        }
        addedHops.add(hop);
        return true;
    }

    public Set<HopsType> getAddedHops() {
        return addedHops;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        FermentingStage stage = state.getValue(FermentingCauldronBlock.STAGE);

        if (isFermentingStage(stage)) {

            if (state.getValue(FermentingCauldronBlock.STAGE) == FermentingStage.BREW_FERMENTING) {
                if (++brewTickCounter >= brewTickDelay) {
                    fermentationProgress++;
                    brewTickCounter = 0;
                }

                updateBlockStateProgress();

                if (bubbleCooldown <= 0) {
                    if (level instanceof ServerLevel serverLevel) {
                        double x = worldPosition.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5);
                        double y = worldPosition.getY() + 1.01;
                        double z = worldPosition.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5);

                        serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, x, y, z, 1, 0.0, 0.05, 0.0, 0.05);
                        serverLevel.playSound(null, x, y, z, SoundEvents.BUBBLE_POP, SoundSource.BLOCKS, 0.3f, 1.4f);

                        bubbleCooldown = 3 + serverLevel.random.nextInt(60);
                    }
                } else {
                    bubbleCooldown--;
                }
            }

            if (state.getValue(FermentingCauldronBlock.STAGE) == FermentingStage.YEAST_FERMENTING) {
                if (++yeastTickCounter >= yeastTickDelay) {
                    fermentationProgress++;
                    yeastTickCounter = 0;
                }

                updateBlockStateProgress();

                if (bubbleCooldown <= 0) {
                    if (level instanceof ServerLevel serverLevel) {
                        double x = worldPosition.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5);
                        double y = worldPosition.getY() + 1.01;
                        double z = worldPosition.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5);

                        serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, x, y, z, 1, 0.0, 0.05, 0.0, 0.05);
                        serverLevel.playSound(null, x, y, z, SoundEvents.BUBBLE_POP, SoundSource.BLOCKS, 0.3f, 1.4f);

                        bubbleCooldown = 3 + serverLevel.random.nextInt(3);
                    }
                } else {
                    bubbleCooldown--;
                }
            }

            if (fermentationProgress >= maxFermentationProgress) {
                finishFermentation(stage);
                fermentationProgress = 0;
            }

        }

        setChanged();
        syncToClient();
    }

    private boolean isFermentingStage(FermentingStage stage) {
        return stage == FermentingStage.YEAST_FERMENTING || stage == FermentingStage.BREW_FERMENTING;
    }

    private void finishFermentation(FermentingStage currentStage) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockState currentState = getBlockState();
        BlockState newState;

        switch (currentStage) {
            case YEAST_FERMENTING -> newState = currentState.setValue(FermentingCauldronBlock.STAGE, FermentingStage.YEAST_READY)
                    .setValue(FermentingCauldronBlock.LEVEL, 3)
                    .setValue(FermentingCauldronBlock.FERMENTATION_PROGRESS, 0);

            case BREW_FERMENTING -> newState = currentState.setValue(FermentingCauldronBlock.STAGE, FermentingStage.BREW_READY)
                    .setValue(FermentingCauldronBlock.LEVEL, 3)
                    .setValue(FermentingCauldronBlock.FERMENTATION_PROGRESS, 0);

            default -> newState = currentState.setValue(FermentingCauldronBlock.FERMENTATION_PROGRESS, 0);
        }

        serverLevel.setBlock(worldPosition, newState, 3);
    }

    private void updateBlockStateProgress() {
        if (level == null || level.isClientSide) return;

        BlockState currentState = getBlockState();
        int blockStateProgress = Math.min(fermentationProgress * 10 / maxFermentationProgress, 9);
        if (currentState.getValue(FermentingCauldronBlock.FERMENTATION_PROGRESS) != blockStateProgress) {
            level.setBlock(worldPosition, currentState.setValue(FermentingCauldronBlock.FERMENTATION_PROGRESS, blockStateProgress), 3);
        }
    }

}