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

/**
 * Model predicate property for switching legendary tome models by LORE_LINE_ID.
 * This is a stateless, singleton property used only for SelectItemModel.
 */
@OnlyIn(Dist.CLIENT)
public final class LoreLineIdProperty implements SelectItemModelProperty<String> {
    public static final ResourceLocation LORE_LINE_ID_PROPERTY = ResourceLocation.fromNamespaceAndPath("jolcraft", "lore_line_id");

    public static final LoreLineIdProperty INSTANCE = new LoreLineIdProperty();

    public static final MapCodec<LoreLineIdProperty> MAP_CODEC = MapCodec.unit(INSTANCE);

    public static final Type<LoreLineIdProperty, String> TYPE =
            SelectItemModelProperty.Type.create(MAP_CODEC, Codec.STRING);

    private LoreLineIdProperty() {}

    @Nullable
    @Override
    public String get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, @NotNull ItemDisplayContext context) {
        return stack.get(JolCraftDataComponents.LORE_LINE_ID.get());
    }

    @Override
    public @NotNull Type<? extends SelectItemModelProperty<String>, String> type() {
        return TYPE;
    }
}
