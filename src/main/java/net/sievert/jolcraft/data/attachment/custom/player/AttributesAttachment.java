package net.sievert.jolcraft.data.attachment.custom.player;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftAttributes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class AttributesAttachment {

    public enum RefreshKey {
        ASHFANG,
        IRONHEART,
        FROSTVEIN,
        SKYBURROW,
        MOONSHARD,
        FULL
    }

    private static final Map<ResourceLocation, EnumSet<RefreshKey>> ATTRIBUTE_TO_REFRESH = new HashMap<>();

    private static ResourceLocation attrKey(DeferredHolder<?, ?> holder) {
        return holder.getId();
    }

    static {
        ATTRIBUTE_TO_REFRESH.put(attrKey(JolCraftAttributes.ATTACK_DAMAGE_INCREASE), EnumSet.of(RefreshKey.ASHFANG));
        ATTRIBUTE_TO_REFRESH.put(attrKey(JolCraftAttributes.ARMOR_INCREASE), EnumSet.of(RefreshKey.IRONHEART));
        ATTRIBUTE_TO_REFRESH.put(attrKey(JolCraftAttributes.SLOW_RESIST), EnumSet.of(RefreshKey.FROSTVEIN));
        ATTRIBUTE_TO_REFRESH.put(attrKey(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY), EnumSet.of(RefreshKey.SKYBURROW));
        ATTRIBUTE_TO_REFRESH.put(attrKey(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT), EnumSet.of(RefreshKey.MOONSHARD));
    }

    public static EnumSet<RefreshKey> getRefreshKeysForStack(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) return EnumSet.noneOf(RefreshKey.class);

        ItemAttributeModifiers mods = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

        EnumSet<RefreshKey> out = EnumSet.noneOf(RefreshKey.class);

        mods.forEach(slot, (attrHolder, modifier) -> {
            ResourceLocation key = BuiltInRegistries.ATTRIBUTE.getKey(attrHolder.value());
            if (key == null) return;

            if (!JolCraft.MOD_ID.equals(key.getNamespace())) return;

            EnumSet<RefreshKey> mapped = ATTRIBUTE_TO_REFRESH.get(key);
            if (mapped != null && !mapped.isEmpty()) {
                out.addAll(mapped);
            }
        });

        return out;
    }

    private final EnumSet<RefreshKey> pending = EnumSet.noneOf(RefreshKey.class);

    public void markDirty(RefreshKey key) {
        pending.add(key);
    }

    public void markDirtyAll() {
        pending.add(RefreshKey.FULL);
    }

    public EnumSet<RefreshKey> consumePending() {
        if (pending.isEmpty()) return EnumSet.noneOf(RefreshKey.class);

        EnumSet<RefreshKey> out = EnumSet.copyOf(pending);
        pending.clear();
        return out;
    }
}