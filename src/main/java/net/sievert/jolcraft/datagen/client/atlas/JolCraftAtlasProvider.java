package net.sievert.jolcraft.datagen.client.atlas;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
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
            "netherite_darker"
    );

    private static final List<String> TRIM_ITEM_TEXTURES = List.of(
            "trims/items/leggings_trim",
            "trims/items/chestplate_trim",
            "trims/items/helmet_trim",
            "trims/items/boots_trim"
    );

    private final PackOutput packOutput;

    public JolCraftAtlasProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path root = packOutput.getOutputFolder();

        Path armorTrims = root.resolve("assets/minecraft/atlases/armor_trims.json");
        Path blocks = root.resolve("assets/minecraft/atlases/blocks.json");

        CompletableFuture<?> future = CompletableFuture.allOf(
                write(cache, armorTrims, buildArmorTrimsJson()),
                write(cache, blocks, buildBlocksJson())
        );

        return future.thenRun(() -> JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "Atlas provider: {}, {}",
                armorTrims.getFileName(),
                blocks.getFileName()
        ));
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Atlases";
    }

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

    private String buildArmorTrimsJson() {
        StringBuilder sb = new StringBuilder(4096);

        sb.append("{\n");
        sb.append("  \"sources\": [\n");
        sb.append("    {\n");
        sb.append("      \"type\": \"paletted_permutations\",\n");
        sb.append("      \"textures\": [\n");

        for (String pattern : VANILLA_TRIM_PATTERN_ORDER) {
            sb.append("        \"trims/models/armor/").append(pattern).append("\",\n");
            sb.append("        \"trims/models/armor/").append(pattern).append("_leggings\",\n");
        }

        sb.append("        \"jolcraft:trims/models/armor/forge\",\n");
        sb.append("        \"jolcraft:trims/models/armor/forge_leggings\"\n");

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
        sb.append("  \"sources\": [\n");

        appendDirectory(sb, "block", "block/");
        sb.append(",\n");
        appendDirectory(sb, "item", "item/");
        sb.append(",\n");
        appendDirectory(sb, "entity/conduit", "entity/conduit/");
        sb.append(",\n");

        appendSingle(sb, "entity/bell/bell_body");
        sb.append(",\n");
        appendSingle(sb, "entity/decorated_pot/decorated_pot_side");
        sb.append(",\n");
        appendSingle(sb, "entity/enchanting_table_book");
        sb.append(",\n");

        sb.append("    {\n");
        sb.append("      \"type\": \"paletted_permutations\",\n");
        sb.append("      \"textures\": [\n");

        for (int i = 0; i < TRIM_ITEM_TEXTURES.size(); i++) {
            sb.append("        \"").append(TRIM_ITEM_TEXTURES.get(i)).append("\"");
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

    private void appendPermutations(StringBuilder sb) {
        boolean first = true;

        for (String id : VANILLA_TRIM_MATERIAL_ORDER) {
            first = appendPermutation(sb, first, id, "trims/color_palettes/" + id);
        }

        first = appendJolCraftMaterial(sb, first, JolCraftMaterials.Material.DEEPSLATE);
        first = appendJolCraftMaterial(sb, first, JolCraftMaterials.Material.MITHRIL);

        for (JolCraftTrimMaterials.Attribute attribute : JolCraftTrimMaterials.Attribute.values()) {
            String id = attribute.getId();
            first = appendPermutation(sb, first, id, "jolcraft:trims/color_palettes/" + id);
        }

        sb.append('\n');
    }

    private static boolean appendJolCraftMaterial(
            StringBuilder sb,
            boolean first,
            JolCraftMaterials.Material material
    ) {
        first = appendPermutation(
                sb,
                first,
                material.getId(),
                "jolcraft:trims/color_palettes/" + material.getId()
        );

        return appendPermutation(
                sb,
                first,
                material.darkerTrimName(),
                "jolcraft:trims/color_palettes/" + material.darkerTrimName()
        );
    }

    private static boolean appendPermutation(
            StringBuilder sb,
            boolean first,
            String key,
            String value
    ) {
        if (!first) {
            sb.append(",\n");
        }

        sb.append("        \"")
                .append(key)
                .append("\": \"")
                .append(value)
                .append("\"");

        return false;
    }
}