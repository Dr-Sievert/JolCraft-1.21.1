package net.sievert.jolcraft.item.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public record BrewColor(int defaultColor) implements ItemTintSource {

    public static final MapCodec<BrewColor> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(BrewColor::defaultColor)).apply(instance, BrewColor::new)
    );

    @Override
    public int calculate(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        Integer argb = stack.get(JolCraftDataComponents.BREW_COLOR.get());
        return argb != null ? argb : this.defaultColor;
    }

    @Override
    public @NotNull MapCodec<BrewColor> type() {
        return MAP_CODEC;
    }
}