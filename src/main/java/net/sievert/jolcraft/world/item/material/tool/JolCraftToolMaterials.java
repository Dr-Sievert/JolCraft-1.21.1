package net.sievert.jolcraft.world.item.material.tool;

import com.google.common.base.Suppliers;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
        public Tier build() {
            return new Tier() {
                private final Supplier<Ingredient> repairIngredient =
                        Suppliers.memoize(() -> Ingredient.of(repairTag));

                @Override
                public int getUses() {
                    return durability;
                }

                @Override
                public float getSpeed() {
                    return speed;
                }

                @Override
                public float getAttackDamageBonus() {
                    return attackDamageBonus;
                }

                @Override
                public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
                    return incorrectForTag;
                }

                @Override
                public int getEnchantmentValue() {
                    return enchantmentValue;
                }

                @Override
                public @NotNull Ingredient getRepairIngredient() {
                    return repairIngredient.get();
                }
            };
        }

        public Tier asTier() {
            return new Tier() {
                @Override public int getUses() { return durability; }
                @Override public float getSpeed() { return speed; }
                @Override public float getAttackDamageBonus() { return attackDamageBonus; }
                @Override public @NotNull TagKey<Block> getIncorrectBlocksForDrops() { return incorrectForTag; }
                @Override public int getEnchantmentValue() { return enchantmentValue; }
                @Override public @NotNull Ingredient getRepairIngredient() { return Ingredient.of(repairTag); }
            };
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

    private static final Map<JolCraftMaterials.Material, Tier> BY_MATERIAL = buildAll();

    private static Map<JolCraftMaterials.Material, Tier> buildAll() {
        Map<JolCraftMaterials.Material, Tier> out =
                new EnumMap<>(JolCraftMaterials.Material.class);

        for (Entry entry : ENTRIES) {
            Tier previous = out.put(entry.material(), entry.build());
            if (previous != null) {
                throw new IllegalStateException("Duplicate tool material entry for: " + entry.material());
            }
        }

        return Map.copyOf(out);
    }

    public static Tier toolMaterial(JolCraftMaterials.Material material) {
        Tier mat = BY_MATERIAL.get(material);
        if (mat == null) {
            throw new IllegalStateException("Missing tool material entry for: " + material);
        }
        return mat;
    }

    public static Map<JolCraftMaterials.Material, Tier> all() {
        return BY_MATERIAL;
    }
}