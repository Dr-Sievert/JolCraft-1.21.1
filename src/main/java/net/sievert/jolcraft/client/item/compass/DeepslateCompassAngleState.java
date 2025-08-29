package net.sievert.jolcraft.client.item.compass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class DeepslateCompassAngleState extends NeedleDirectionHelper {
    public static final MapCodec<DeepslateCompassAngleState> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_387422_ -> p_387422_.group(
                            Codec.BOOL.optionalFieldOf("wobble", Boolean.TRUE).forGetter(NeedleDirectionHelper::wobble),
                            DeepslateCompassAngleState.CompassTarget.CODEC.fieldOf("target").forGetter(DeepslateCompassAngleState::target)
                    )
                    .apply(p_387422_, DeepslateCompassAngleState::new)
    );
    private final NeedleDirectionHelper.Wobbler wobbler;
    private final NeedleDirectionHelper.Wobbler noTargetWobbler;
    private final DeepslateCompassAngleState.CompassTarget compassTarget;
    private final RandomSource random = RandomSource.create();

    public DeepslateCompassAngleState(boolean wobble, DeepslateCompassAngleState.CompassTarget compassTarget) {
        super(wobble);
        this.wobbler = this.newWobbler(0.8F);
        this.noTargetWobbler = this.newWobbler(0.8F);
        this.compassTarget = compassTarget;
    }

    @Override
    protected float calculate(ItemStack p_388108_, ClientLevel p_387750_, int p_388073_, Entity p_388489_) {
        GlobalPos globalpos = this.compassTarget.get(p_387750_, p_388108_, p_388489_);
        long i = p_387750_.getGameTime();
        return !isValidCompassTargetPos(p_388489_, globalpos)
                ? this.getRandomlySpinningRotation(p_388073_, i)
                : this.getRotationTowardsCompassTarget(p_388489_, i, globalpos.pos());
    }

    private float getRandomlySpinningRotation(int seed, long gameTime) {
        if (this.noTargetWobbler.shouldUpdate(gameTime)) {
            this.noTargetWobbler.update(gameTime, this.random.nextFloat());
        }

        float f = this.noTargetWobbler.rotation() + (float)hash(seed) / 2.1474836E9F;
        return Mth.positiveModulo(f, 1.0F);
    }

    private float getRotationTowardsCompassTarget(Entity entity, long gameTime, BlockPos targetPos) {
        float f = (float)getAngleFromEntityToPos(entity, targetPos);
        float f1 = getWrappedVisualRotationY(entity);
        if (entity instanceof Player player && player.isLocalPlayer() && player.level().tickRateManager().runsNormally()) {
            if (this.wobbler.shouldUpdate(gameTime)) {
                this.wobbler.update(gameTime, 0.5F - (f1 - 0.25F));
            }

            float f3 = f + this.wobbler.rotation();
            return Mth.positiveModulo(f3, 1.0F);
        }

        float f2 = 0.5F - (f1 - 0.25F - f);
        return Mth.positiveModulo(f2, 1.0F);
    }

    private static boolean isValidCompassTargetPos(Entity entity, @Nullable GlobalPos pos) {
        return pos != null
                && pos.dimension() == entity.level().dimension()
                && !(pos.pos().distToCenterSqr(entity.position()) < 1.0E-5F);
    }

    private static double getAngleFromEntityToPos(Entity entity, BlockPos pos) {
        Vec3 vec3 = Vec3.atCenterOf(pos);
        return Math.atan2(vec3.z() - entity.getZ(), vec3.x() - entity.getX()) / (float) (Math.PI * 2);
    }

    private static float getWrappedVisualRotationY(Entity entity) {
        return Mth.positiveModulo(entity.getVisualRotationYInDegrees() / 360.0F, 1.0F);
    }

    private static int hash(int seed) {
        return seed * 1327217883;
    }

    protected DeepslateCompassAngleState.CompassTarget target() {
        return this.compassTarget;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum CompassTarget implements StringRepresentable {
        NONE("none") {
            @Nullable
            @Override
            public GlobalPos get(ClientLevel p_388090_, ItemStack p_388593_, Entity p_388853_) {
                return null;
            }
        },
        STRUCTURE("structure") {
            @Override
            public @Nullable GlobalPos get(ClientLevel level, ItemStack stack, Entity entity) {
                return stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET.get());
            }
        };
        public static final Codec<DeepslateCompassAngleState.CompassTarget> CODEC = StringRepresentable.fromEnum(DeepslateCompassAngleState.CompassTarget::values);
        private final String name;

        CompassTarget(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        @Nullable
        abstract GlobalPos get(ClientLevel level, ItemStack stack, Entity entity);
    }
}
