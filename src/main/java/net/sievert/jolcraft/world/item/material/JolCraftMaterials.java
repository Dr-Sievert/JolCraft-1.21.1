package net.sievert.jolcraft.world.item.material;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftMaterialIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class JolCraftMaterials {

    private JolCraftMaterials() {}

    public enum Material implements JolCraftEnumHelper.StringId {
        DEEPSLATE(JolCraftMaterialIds.DEEPSLATE),
        MITHRIL(JolCraftMaterialIds.MITHRIL);

        private final String id;

        Material(@NotNull String id) {
            this.id = id;
        }

        @Override
        public @NotNull String getId() {
            return this.id;
        }

        public @NotNull ResourceKey<TrimMaterial> trimKey() {
            return ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location(this.id));
        }

        public @NotNull String trimAssetName() {
            return this.id;
        }

        public @NotNull Map<Holder<ArmorMaterial>, String> overrideArmorMaterials() {
            return Map.of();
        }

        public @NotNull String darkerTrimName() {
            return JolCraftStrings.underscored(this.id, JolCraftDictionary.DARKER);
        }
    }
}