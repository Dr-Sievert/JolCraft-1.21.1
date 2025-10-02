package net.sievert.jolcraft.item.client.compass;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public record DialColor(int defaultColor) implements ItemTintSource {
    public static final MapCodec<DialColor> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_386972_ -> p_386972_.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(DialColor::defaultColor)).apply(p_386972_,DialColor::new)
    );

    @Override
    public int calculate(@NotNull ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        return DialItemColor.getOrDefault(stack, this.defaultColor);
    }

    @Override
    public @NotNull MapCodec<DialColor> type() {
        return MAP_CODEC;
    }
}
