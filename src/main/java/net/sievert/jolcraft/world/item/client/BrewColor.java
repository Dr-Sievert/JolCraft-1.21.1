package net.sievert.jolcraft.world.item.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.id.item.JolCraftItemPropertyIds;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public record BrewColor(int defaultColor) implements ItemTintSource {

    public static final ResourceLocation KEY =
            JolCraft.location(JolCraftItemPropertyIds.BREW_COLOR);

    public static final MapCodec<BrewColor> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ExtraCodecs.RGB_COLOR_CODEC
                                    .fieldOf(JolCraftDataKeys.DEFAULT)
                                    .forGetter(BrewColor::defaultColor)
                    ).apply(instance, BrewColor::new)
            );

    @Override
    public int calculate(@NotNull ItemStack stack,
                         @Nullable ClientLevel level,
                         @Nullable LivingEntity entity) {
        Integer argb = stack.get(JolCraftDataComponents.BREW_COLOR.get());
        return argb != null ? argb : this.defaultColor;
    }

    @Override
    public @NotNull MapCodec<BrewColor> type() {
        return MAP_CODEC;
    }
}
