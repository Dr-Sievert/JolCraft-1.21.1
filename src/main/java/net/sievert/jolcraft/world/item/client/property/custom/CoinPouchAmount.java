package net.sievert.jolcraft.world.item.client.property.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.id.item.JolCraftItemPropertyIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.item.client.property.JolCraftItemProperties;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public record CoinPouchAmount() implements JolCraftItemProperties.Property {

    public static final ResourceLocation KEY = JolCraft.location(JolCraftItemPropertyIds.COIN_POUCH_AMOUNT);

    private static final int MAX_COINS = 999;
    private static final float TIER_1 = 1.0F / 3.0F;
    private static final float TIER_2 = 2.0F / 3.0F;

    private static final String EMPTY = JolCraftDictionary.EMPTY;
    private static final String MEDIUM = JolCraftDictionary.MEDIUM;
    private static final String FULL = JolCraftDictionary.FULL;

    @Override
    public @NotNull ResourceLocation key() {
        return KEY;
    }

    @Override
    public void bootstrap() {
        JolCraftItemProperties.registerKey(KEY, EMPTY);
        JolCraftItemProperties.registerKey(KEY, MEDIUM);
        JolCraftItemProperties.registerKey(KEY, FULL);
        JolCraftItemProperties.validate(KEY);
    }

    private static String getKey(@NotNull ItemStack stack) {
        int coins = stack.getOrDefault(JolCraftDataComponents.COIN_POUCH_AMOUNT.get(), 0);
        float ratio = Math.min(coins / (float) MAX_COINS, 1.0F);

        if (ratio < TIER_1) {
            return EMPTY;
        }

        if (ratio < TIER_2) {
            return MEDIUM;
        }

        return FULL;
    }

    @Override
    public float value(
            @NotNull ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity,
            int seed
    ) {
        return JolCraftItemProperties.value(KEY, getKey(stack));
    }
}