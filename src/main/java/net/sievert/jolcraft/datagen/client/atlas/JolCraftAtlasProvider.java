package net.sievert.jolcraft.datagen.client.atlas;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public final class JolCraftAtlasProvider implements DataProvider {

    // -------------------------------------------------------------------------
    // Ordering (match your manual JSON exactly)
    // -------------------------------------------------------------------------

    private static final List<String> VANILLA_TRIM_PATTERN_ORDER = List.of(
            "coast",
            "sentry",
            "dune",
            "wild",
            "ward",
            "eye",
            "vex",
            "tide",
            "snout",
            "rib",
            "spire",
            "wayfinder",
            "shaper",
            "silence",
            "raiser",
            "host",
            "flow",
            "bolt"
    );

    private static final List<String> VANILLA_TRIM_MATERIAL_ORDER = List.of(
            "quartz",
            "iron",
            "gold",
            "diamond",
            "netherite",
            "redstone",
            "copper",
            "emerald",
            "lapis",
            "amethyst",
            "iron_darker",
            "gold_darker",
            "diamond_darker",
            "netherite_darker",
            "resin"
    );

    private static final List<String> TRIM_ITEM_TEXTURES = List.of(
            "trims/items/leggings_trim",
            "trims/items/chestplate_trim",
            "trims/items/helmet_trim",
            "trims/items/boots_trim"
    );

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private final PackOutput packOutput;

    public JolCraftAtlasProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path root = packOutput.getOutputFolder();

        Path armorTrims = root.resolve("assets/minecraft/atlases/armor_trims.json");
        Path blocks     = root.resolve("assets/minecraft/atlases/blocks.json");

        return CompletableFuture.allOf(
                write(cache, armorTrims, buildArmorTrimsJson()),
                write(cache, blocks, buildBlocksJson())
        );
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Atlases";
    }

    // -------------------------------------------------------------------------
    // Stable write helper (like DataProvider.saveStable but for raw strings)
    // -------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    private static CompletableFuture<?> write(CachedOutput cache, Path path, String contents) {
        return CompletableFuture.runAsync(() -> {
            byte[] bytes = contents.getBytes(StandardCharsets.UTF_8);
            HashCode hash = Hashing.sha1().hashBytes(bytes);
            try {
                cache.writeIfNeeded(path, bytes, hash);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    // -------------------------------------------------------------------------
    // JSON builders (exact key order)
    // -------------------------------------------------------------------------

    private String buildArmorTrimsJson() {
        StringBuilder sb = new StringBuilder(4096);

        sb.append("{\n");
        sb.append("  \"replace\": false,\n");
        sb.append("  \"sources\": [\n");
        sb.append("    {\n");
        sb.append("      \"type\": \"paletted_permutations\",\n");
        sb.append("      \"textures\": [\n");

        // textures in exact order
        for (String p : VANILLA_TRIM_PATTERN_ORDER) {
            sb.append("        \"trims/entity/humanoid/").append(p).append("\",\n");
            sb.append("        \"trims/entity/humanoid_leggings/").append(p).append("\",\n");
        }
        sb.append("        \"jolcraft:trims/entity/humanoid/forge\",\n");
        sb.append("        \"jolcraft:trims/entity/humanoid_leggings/forge\"\n");

        sb.append("      ],\n");
        sb.append("      \"palette_key\": \"trims/color_palettes/trim_palette\",\n");
        sb.append("      \"permutations\": {\n");
        appendPermutations(sb);
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    private String buildBlocksJson() {
        StringBuilder sb = new StringBuilder(4096);

        sb.append("{\n");
        sb.append("  \"replace\": false,\n");
        sb.append("  \"sources\": [\n");

        // directory entries: type, source, prefix (exact order)
        appendDirectory(sb, "block", "block/");
        sb.append(",\n");
        appendDirectory(sb, "item", "item/");
        sb.append(",\n");
        appendDirectory(sb, "entity/conduit", "entity/conduit/");
        sb.append(",\n");

        // singles
        appendSingle(sb, "entity/bell/bell_body");
        sb.append(",\n");
        appendSingle(sb, "entity/decorated_pot/decorated_pot_side");
        sb.append(",\n");
        appendSingle(sb, "entity/enchanting_table_book");
        sb.append(",\n");

        // paletted_permutations: type, textures, palette_key, permutations
        sb.append("    {\n");
        sb.append("      \"type\": \"paletted_permutations\",\n");
        sb.append("      \"textures\": [\n");

        for (int i = 0; i < TRIM_ITEM_TEXTURES.size(); i++) {
            String t = TRIM_ITEM_TEXTURES.get(i);
            sb.append("        \"").append(t).append("\"");
            sb.append(i == TRIM_ITEM_TEXTURES.size() - 1 ? "\n" : ",\n");
        }

        sb.append("      ],\n");
        sb.append("      \"palette_key\": \"trims/color_palettes/trim_palette\",\n");
        sb.append("      \"permutations\": {\n");
        appendPermutations(sb);
        sb.append("      }\n");
        sb.append("    }\n");

        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    private static void appendDirectory(StringBuilder sb, String source, String prefix) {
        sb.append("    {\n");
        sb.append("      \"type\": \"directory\",\n");
        sb.append("      \"source\": \"").append(source).append("\",\n");
        sb.append("      \"prefix\": \"").append(prefix).append("\"\n");
        sb.append("    }");
    }

    private static void appendSingle(StringBuilder sb, String resource) {
        sb.append("    {\n");
        sb.append("      \"type\": \"single\",\n");
        sb.append("      \"resource\": \"").append(resource).append("\"\n");
        sb.append("    }");
    }

    // permutations in exact order: vanilla -> deepslate/mithril -> attributes
    private void appendPermutations(StringBuilder sb) {
        // vanilla first (exact order)
        for (String id : VANILLA_TRIM_MATERIAL_ORDER) {
            sb.append("        ").append("\"").append(id).append("\": \"trims/color_palettes/").append(id).append("\",\n");
        }

        // jolcraft base mats (exact order)
        appendJolCraftMaterial(sb, JolCraftMaterials.Material.DEEPSLATE);
        appendJolCraftMaterial(sb, JolCraftMaterials.Material.MITHRIL);

        // attributes in enum order, last one without trailing comma
        JolCraftTrimMaterials.Attribute[] attrs = JolCraftTrimMaterials.Attribute.values();
        for (int i = 0; i < attrs.length; i++) {
            String id = attrs[i].id();
            sb.append("        ")
                    .append("\"").append(id).append("\": ")
                    .append("\"jolcraft:trims/color_palettes/").append(id).append("\"");
            sb.append(i == attrs.length - 1 ? "\n" : ",\n");
        }
    }

    private static void appendJolCraftMaterial(StringBuilder sb, JolCraftMaterials.Material mat) {
        sb.append("        ")
                .append("\"").append(mat.id()).append("\": ")
                .append("\"jolcraft:trims/color_palettes/").append(mat.id()).append("\",\n");

        sb.append("        ")
                .append("\"").append(mat.darkerTrimName()).append("\": ")
                .append("\"jolcraft:trims/color_palettes/").append(mat.darkerTrimName()).append("\",\n");
    }
}