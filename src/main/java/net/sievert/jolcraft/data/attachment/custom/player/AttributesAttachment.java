package net.sievert.jolcraft.data.attachment.custom.player;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
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

    private static final Map<ResourceLocation, EnumSet<RefreshKey>> ATTRIBUTE_TO_REFRESH = createAttributeToRefreshMap();

    private static Map<ResourceLocation, EnumSet<RefreshKey>> createAttributeToRefreshMap() {
        Map<ResourceLocation, EnumSet<RefreshKey>> map = new HashMap<>();
        map.put(attrKey(JolCraftAttributes.ATTACK_DAMAGE_INCREASE), EnumSet.of(RefreshKey.ASHFANG));
        map.put(attrKey(JolCraftAttributes.ARMOR_INCREASE), EnumSet.of(RefreshKey.IRONHEART));
        map.put(attrKey(JolCraftAttributes.SLOW_RESISTANCE), EnumSet.of(RefreshKey.FROSTVEIN));
        map.put(attrKey(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY), EnumSet.of(RefreshKey.SKYBURROW));
        map.put(attrKey(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT), EnumSet.of(RefreshKey.MOONSHARD));
        return Map.copyOf(map);
    }

    private static ResourceLocation attrKey(DeferredHolder<?, ?> holder) {
        return holder.getId();
    }

    public static EnumSet<RefreshKey> getRefreshKeysForStack(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) return EnumSet.noneOf(RefreshKey.class);

        ItemAttributeModifiers mods = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        if (mods == ItemAttributeModifiers.EMPTY) return EnumSet.noneOf(RefreshKey.class);

        EnumSet<RefreshKey> out = EnumSet.noneOf(RefreshKey.class);

        mods.forEach(slot, (attrHolder, modifier) -> {
            ResourceLocation key = attrHolder.unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(null);
            if (key == null) return;

            if (!JolCraft.MOD_ID.equals(key.getNamespace())) return;

            EnumSet<RefreshKey> mapped = ATTRIBUTE_TO_REFRESH.get(key);
            if (mapped != null) out.addAll(mapped);
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