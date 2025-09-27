package net.sievert.jolcraft.data.custom.component;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public final class CoinPouchAmountProperty implements SelectItemModelProperty<Integer> {
    public static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath("jolcraft", "coin_pouch_amount");
    public static final CoinPouchAmountProperty INSTANCE = new CoinPouchAmountProperty();

    public static final MapCodec<CoinPouchAmountProperty> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final Type<CoinPouchAmountProperty, Integer> TYPE =
            SelectItemModelProperty.Type.create(MAP_CODEC, Codec.INT);

    private CoinPouchAmountProperty() {}

    @Override
    public @NotNull Integer get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, @NotNull ItemDisplayContext context) {
        int coins = stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);

        if (coins == 0) {
            return 0;
        } else if (coins < 333) {
            return 0;
        } else if (coins < 666) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public @NotNull Type<? extends SelectItemModelProperty<Integer>, Integer> type() {
        return TYPE;
    }
}