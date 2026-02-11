package net.sievert.jolcraft.world.item.client.compass;

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
import net.sievert.jolcraft.data.id.item.JolCraftItemPropertyIds;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public record DialColor(int defaultColor) implements ItemTintSource {

    public static final ResourceLocation KEY =
            JolCraft.location(JolCraftItemPropertyIds.DEEPSLATE_COMPASS_DIAL_COLOR);

    public static final MapCodec<DialColor> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ExtraCodecs.RGB_COLOR_CODEC
                                    .fieldOf(JolCraftDataKeys.DEFAULT)
                                    .forGetter(DialColor::defaultColor)
                    ).apply(instance, DialColor::new)
            );

    @Override
    public int calculate(@NotNull ItemStack stack,
                         @Nullable ClientLevel clientLevel,
                         @Nullable LivingEntity livingEntity) {
        return DialItemColor.getOrDefault(stack, this.defaultColor);
    }

    @Override
    public @NotNull MapCodec<DialColor> type() {
        return MAP_CODEC;
    }
}
