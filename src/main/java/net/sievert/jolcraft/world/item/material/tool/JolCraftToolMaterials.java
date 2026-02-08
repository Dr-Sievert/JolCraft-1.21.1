package net.sievert.jolcraft.world.item.material.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class JolCraftToolMaterials {

    private JolCraftToolMaterials() {
    }

    private record Entry(
            JolCraftMaterials.Material material,
            TagKey<Item> repairTag,
            TagKey<Block> incorrectForTag,
            int durability,
            float speed,
            float attackDamageBonus,
            int enchantmentValue
    ) {
        public ToolMaterial build() {
            return new ToolMaterial(
                    incorrectForTag,
                    durability,
                    speed,
                    attackDamageBonus,
                    enchantmentValue,
                    repairTag
            );
        }
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(
                    JolCraftMaterials.Material.DEEPSLATE,
                    JolCraftTags.Items.REPAIRS_DEEPSLATE,
                    BlockTags.INCORRECT_FOR_IRON_TOOL,
                    1200,
                    6.0F,
                    2.5F,
                    15
            ),
            new Entry(
                    JolCraftMaterials.Material.MITHRIL,
                    JolCraftTags.Items.REPAIRS_MITHRIL,
                    BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                    5000,
                    10.0F,
                    5.0F,
                    20
            )
    );

    private static final Map<JolCraftMaterials.Material, ToolMaterial> BY_MATERIAL = buildAll();

    private static Map<JolCraftMaterials.Material, ToolMaterial> buildAll() {
        Map<JolCraftMaterials.Material, ToolMaterial> out =
                new EnumMap<>(JolCraftMaterials.Material.class);

        for (Entry entry : ENTRIES) {
            ToolMaterial previous = out.put(entry.material(), entry.build());
            if (previous != null) {
                throw new IllegalStateException("Duplicate tool material entry for: " + entry.material());
            }
        }

        return Map.copyOf(out);
    }

    public static ToolMaterial toolMaterial(JolCraftMaterials.Material material) {
        ToolMaterial mat = BY_MATERIAL.get(material);
        if (mat == null) {
            throw new IllegalStateException("Missing tool material entry for: " + material);
        }
        return mat;
    }

    public static Map<JolCraftMaterials.Material, ToolMaterial> all() {
        return BY_MATERIAL;
    }
}