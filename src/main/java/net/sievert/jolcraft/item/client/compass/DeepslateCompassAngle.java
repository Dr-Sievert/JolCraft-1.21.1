package net.sievert.jolcraft.item.client.compass;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class DeepslateCompassAngle implements RangeSelectItemModelProperty {
    public static final MapCodec<DeepslateCompassAngle> MAP_CODEC = DeepslateCompassAngleState.MAP_CODEC.xmap(
            DeepslateCompassAngle::new, p_388035_ -> p_388035_.state
    );
    private final DeepslateCompassAngleState state;

    private DeepslateCompassAngle(DeepslateCompassAngleState state) {
        this.state = state;
    }

    @Override
    public float get(@NotNull ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
        return this.state.get(stack, clientLevel, livingEntity, seed);
    }

    @Override
    public @NotNull MapCodec<DeepslateCompassAngle> type() {
        return MAP_CODEC;
    }
}

