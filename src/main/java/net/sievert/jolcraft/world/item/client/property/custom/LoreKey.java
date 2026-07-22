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
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.world.item.client.property.JolCraftItemProperties;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public record LoreKey() implements JolCraftItemProperties.Property {

    public static final ResourceLocation KEY = JolCraft.location(JolCraftItemPropertyIds.LORE_KEY);

    @Override
    public @NotNull ResourceLocation key() {
        return KEY;
    }

    @Override
    public void bootstrap() {
        DwarfLoreEntries.ALL.entrySet().stream()
                .filter(entry -> entry.getValue().rarity() == JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(Enum::name))
                .forEach(loreKey -> JolCraftItemProperties.registerKey(KEY, loreKey.name().toLowerCase(Locale.ROOT)));

        JolCraftItemProperties.validate(KEY);
    }

    public @Nullable String get(@NotNull ItemStack stack) {
        return stack.get(JolCraftDataComponents.DWARF_LORE_KEY.get());
    }

    @Override
    public float value(
            @NotNull ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity,
            int seed
    ) {
        String loreKey = get(stack);
        if (loreKey == null || loreKey.isBlank()) {
            return 0.0F;
        }

        return JolCraftItemProperties.value(KEY, loreKey);
    }
}