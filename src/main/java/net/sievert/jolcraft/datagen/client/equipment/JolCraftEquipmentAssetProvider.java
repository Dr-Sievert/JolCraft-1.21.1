package net.sievert.jolcraft.datagen.client.equipment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.armor.JolCraftArmorMaterials;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Generates equipment asset definitions in:
 *   assets/<namespace>/equipment/<id>.json
 * These are required for ArmorMaterial.equipmentAssetKey() to resolve layers.
 */
@OnlyIn(Dist.CLIENT)
public final class JolCraftEquipmentAssetProvider implements DataProvider {

    // ---------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------

    /**
     * Layers we currently use for all JolCraft armor materials.
     * Matches the vanilla/NeoForge equipment asset JSON schema.
     */
    private static final String[] DEFAULT_LAYERS = {
            "horse_body",
            "humanoid",
            "humanoid_leggings"
    };

    // ---------------------------------------------------------------------
    // State
    // ---------------------------------------------------------------------

    private final PackOutput.PathProvider pathProvider;

    public JolCraftEquipmentAssetProvider(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        CompletableFuture<?>[] futures = JolCraftArmorMaterials.all().keySet().stream()
                .map(armorMaterial -> saveEquipmentAsset(cache, armorMaterial))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Equipment Assets";
    }

    // ---------------------------------------------------------------------
    // Save
    // ---------------------------------------------------------------------

    private CompletableFuture<?> saveEquipmentAsset(CachedOutput cache, JolCraftMaterials.Material material) {
        ResourceKey<EquipmentAsset> key = material.equipmentAssetKey();
        ResourceLocation id = key.location();
        Path path = pathProvider.json(id);

        JsonObject root = new JsonObject();
        JsonObject layers = new JsonObject();

        String textureId = id.getNamespace() + ":" + id.getPath();

        for (String layerName : DEFAULT_LAYERS) {
            JsonArray arr = new JsonArray();
            JsonObject layer = new JsonObject();
            layer.addProperty("texture", textureId);
            arr.add(layer);
            layers.add(layerName, arr);
        }

        root.add("layers", layers);

        return DataProvider.saveStable(cache, root, path);
    }
}
