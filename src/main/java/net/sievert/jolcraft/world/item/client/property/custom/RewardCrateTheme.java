package net.sievert.jolcraft.world.item.client.property.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemPropertyIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.item.client.property.JolCraftItemProperties;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.crate.CrateTheme;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public record RewardCrateTheme() implements JolCraftItemProperties.Property {

    public static final ResourceLocation KEY = JolCraft.location(JolCraftItemPropertyIds.REWARD_CRATE_SOURCE);

    private static final String CRATE_PREFIX = JolCraftDictionary.CRATE + "/";

    @Override
    public @NotNull ResourceLocation key() {
        return KEY;
    }

    @Override
    public void bootstrap() {
        Arrays.stream(CrateTheme.values())
                .sorted(Comparator.comparing(CrateTheme::getId))
                .forEach(theme ->
                        JolCraftItemProperties.registerKey(
                                KEY,
                                theme.getId()
                        )
                );

        JolCraftItemProperties.validate(KEY);
    }

    @Override
    public float value(
            @NotNull ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity,
            int seed
    ) {
        CrateTheme theme = getTheme(stack);

        return theme == null
                ? 0.0F
                : JolCraftItemProperties.value(
                KEY,
                theme.getId()
        );
    }

    private static @Nullable CrateTheme getTheme(
            @NotNull ItemStack stack
    ) {
        RewardCrateSource source =
                stack.get(
                        JolCraftDataComponents.REWARD_CRATE_SOURCE.get()
                );

        if (!(source instanceof RewardCrateSource.LootTableSource(ResourceKey<LootTable> lootTable))) {
            return null;
        }

        String path =
                lootTable.location().getPath();

        if (!path.startsWith(CRATE_PREFIX)) {
            return null;
        }

        int themeEnd =
                path.indexOf(
                        '/',
                        CRATE_PREFIX.length()
                );

        if (themeEnd < 0) {
            return null;
        }

        String themeId =
                path.substring(
                        CRATE_PREFIX.length(),
                        themeEnd
                );

        return CrateTheme.byId(themeId);
    }
}