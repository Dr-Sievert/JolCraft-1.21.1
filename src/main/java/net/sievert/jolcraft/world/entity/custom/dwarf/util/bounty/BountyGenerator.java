package net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public class BountyGenerator {

    /** Holds all data for a given item in a bounty pool. */
    public record BountyEntry(Item item, ToIntFunction<RandomSource> count) {}

    /** Pool for a single tier: a list of entries. */
    public record BountyPool(List<BountyEntry> entries) {}

    // === Define pools per bounty type ===

    private static final Map<BountyType, List<BountyPool>> POOLS = new EnumMap<>(BountyType.class);

    static {
        // MERCHANT
        POOLS.put(BountyType.MERCHANT, List.of(
                new BountyPool(List.of(
                        new BountyEntry(Items.COAL,          r -> 5 + r.nextInt(8)),
                        new BountyEntry(Items.FLINT,         r -> 5 + r.nextInt(8)),
                        new BountyEntry(Items.COPPER_INGOT,  r -> 5 + r.nextInt(8)),
                        new BountyEntry(Items.COBBLED_DEEPSLATE, r -> 5 + r.nextInt(8)),
                        new BountyEntry(Items.TORCH,         r -> 5 + r.nextInt(8)),
                        new BountyEntry(Items.CLAY_BALL,     r -> 5 + r.nextInt(8)),
                        new BountyEntry(Items.IRON_NUGGET,   r -> 5 + r.nextInt(8))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.IRON_INGOT,    r -> 4 + r.nextInt(5)),
                        new BountyEntry(Items.LAPIS_LAZULI,  r -> 4 + r.nextInt(5)),
                        new BountyEntry(Items.REDSTONE,      r -> 4 + r.nextInt(5)),
                        new BountyEntry(Items.GLOW_INK_SAC,  r -> 3 + r.nextInt(4)),
                        new BountyEntry(Items.SPIDER_EYE,    r -> 3 + r.nextInt(4)),
                        new BountyEntry(Items.GUNPOWDER,     r -> 3 + r.nextInt(4)),
                        new BountyEntry(Items.BONE,          r -> 5 + r.nextInt(5))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.GOLD_INGOT,    r -> 3 + r.nextInt(4)),
                        new BountyEntry(Items.EMERALD,       r -> 2 + r.nextInt(4)),
                        new BountyEntry(Items.AMETHYST_SHARD, r -> 3 + r.nextInt(4)),
                        new BountyEntry(Items.BLAZE_POWDER,  r -> 3 + r.nextInt(4)),
                        new BountyEntry(Items.INK_SAC,       r -> 3 + r.nextInt(4))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.ANVIL,         r -> 1),
                        new BountyEntry(Items.GOLDEN_APPLE,  r -> 1 + r.nextInt(2)),
                        new BountyEntry(Items.BOOK,          r -> 1 + r.nextInt(2)),
                        new BountyEntry(Items.CAULDRON,      r -> 1),
                        new BountyEntry(Items.ITEM_FRAME,    r -> 1 + r.nextInt(3)),
                        new BountyEntry(Items.ENDER_PEARL,   r -> 1)
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.NETHERITE_SCRAP, r -> 1 + r.nextInt(2)),
                        new BountyEntry(Items.HEART_OF_THE_SEA, r -> 1),
                        new BountyEntry(Items.DRAGON_BREATH, r -> 1 + r.nextInt(2))
                ))
        ));

        // MINER
        POOLS.put(BountyType.MINER, List.of(
                new BountyPool(List.of(
                        new BountyEntry(Items.STONE,     r -> 8 + r.nextInt(8)),
                        new BountyEntry(Items.GRANITE,   r -> 8 + r.nextInt(8)),
                        new BountyEntry(Items.DIORITE,   r -> 8 + r.nextInt(8)),
                        new BountyEntry(Items.ANDESITE,  r -> 8 + r.nextInt(8)),
                        new BountyEntry(Items.TUFF,      r -> 8 + r.nextInt(8))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.IRON_ORE,            r -> 4 + r.nextInt(5)),
                        new BountyEntry(Items.COPPER_ORE,          r -> 4 + r.nextInt(5)),
                        new BountyEntry(Items.DEEPSLATE_IRON_ORE,  r -> 4 + r.nextInt(5))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.GOLD_ORE,     r -> 3 + r.nextInt(4)),
                        new BountyEntry(Items.EMERALD_ORE,  r -> 2 + r.nextInt(3))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.DIAMOND_ORE,           r -> 1 + r.nextInt(2)),
                        new BountyEntry(Items.DEEPSLATE_DIAMOND_ORE, r -> 1 + r.nextInt(2))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.ANCIENT_DEBRIS, r -> 1)
                ))
        ));
    }

    public static List<ItemStack> getReward(BountyData data, RandomSource random) {
        BountyType type = BountyType.fromString(data.type());
        BountyTier tier = BountyTier.fromValue(data.tier());

        List<ItemStack> rewards = new ArrayList<>();
        switch (type) {
            case null -> {}
            case UNKNOWN -> {}
            case MERCHANT -> {
                int coins = switch (tier) {
                    case NOVICE -> 4 + random.nextInt(3);
                    case APPRENTICE -> 7 + random.nextInt(4);
                    case JOURNEYMAN -> 12 + random.nextInt(5);
                    case EXPERT -> 20 + random.nextInt(8);
                    case MASTER -> 30 + random.nextInt(10);
                    default -> 0;
                };
                if (coins > 0) rewards.add(new ItemStack(JolCraftItems.GOLD_COIN.get(), coins));

                float crateChance = switch (tier) {
                    case APPRENTICE -> 0.125f;
                    case JOURNEYMAN -> 0.25f;
                    case EXPERT -> 0.5f;
                    case MASTER -> 0.7f;
                    default -> 0f;
                };
                if (crateChance > 0 && random.nextFloat() < crateChance) {
                    boolean restock = random.nextBoolean();
                    rewards.add(new ItemStack(restock ? JolCraftItems.RESTOCK_CRATE.get() : JolCraftItems.REROLL_CRATE.get()));
                }
            }
            case MINER -> {
                int num = switch (tier) {
                    case NOVICE -> 1;
                    case APPRENTICE -> 1 + random.nextInt(2);
                    case JOURNEYMAN -> 1 + random.nextInt(3);
                    case EXPERT -> 1 + random.nextInt(4);
                    case MASTER -> 1 + random.nextInt(5);
                    default -> 0;
                };
                for (int i = 0; i < num; i++) {
                    rewards.add(new ItemStack(getWeightedGeode(random, tier)));
                }
            }
        }
        return rewards;
    }

    private static Item getWeightedGeode(RandomSource random, BountyTier tier) {
        int[] weights = switch (tier) {
            case NOVICE, APPRENTICE -> new int[]{4, 2, 1};
            case JOURNEYMAN -> new int[]{2, 2, 2};
            case MASTER -> new int[]{1, 2, 4};
            default -> new int[]{2, 2, 1};
        };
        int total = Arrays.stream(weights).sum();
        int roll = random.nextInt(total);
        if (roll < weights[0]) return JolCraftItems.GEODE_SMALL.get();
        else if (roll < weights[0] + weights[1]) return JolCraftItems.GEODE_MEDIUM.get();
        else return JolCraftItems.GEODE_LARGE.get();
    }

    /** Main and only method for generating bounty data. */
    public static BountyData generate(ItemStack stack, RandomSource random) {
        BountyType type = BountyHelper.getBountyType(stack);
        BountyTier tier = BountyHelper.getBountyTier(stack);

        List<BountyPool> pools = POOLS.getOrDefault(type, POOLS.get(BountyType.MERCHANT));
        int tierIndex = Math.max(0, Math.min(tier.getValue() - 1, pools.size() - 1));
        BountyPool pool = pools.get(tierIndex);

        List<BountyEntry> entries = pool.entries();
        BountyEntry entry = entries.get(random.nextInt(entries.size()));
        int count = entry.count().applyAsInt(random);

        return new BountyData(
                BuiltInRegistries.ITEM.getKey(entry.item()),
                count,
                tier.getValue(),
                type.getId()
        );
    }
}