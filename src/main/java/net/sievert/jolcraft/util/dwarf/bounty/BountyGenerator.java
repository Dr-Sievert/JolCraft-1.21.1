package net.sievert.jolcraft.util.dwarf.bounty;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.item.JolCraftItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class BountyGenerator {

    /** Holds all data for a given item in a bounty pool. */
    public record BountyEntry(Item item, IntSupplier count) {}

    /** Pool for a single tier: a list of entries. */
    public record BountyPool(List<BountyEntry> entries) {}

    public enum BountyType {
        MERCHANT(List.of(
                // T1
                new BountyPool(List.of(
                        new BountyEntry(Items.COAL, () -> 5 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.FLINT, () -> 5 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.COPPER_INGOT, () -> 5 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.COBBLED_DEEPSLATE, () -> 5 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.TORCH, () -> 5 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.CLAY_BALL, () -> 5 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.IRON_NUGGET, () -> 5 + RandomSource.create().nextInt(8))
                )),
                // T2
                new BountyPool(List.of(
                        new BountyEntry(Items.IRON_INGOT, () -> 4 + RandomSource.create().nextInt(5)),
                        new BountyEntry(Items.LAPIS_LAZULI, () -> 4 + RandomSource.create().nextInt(5)),
                        new BountyEntry(Items.REDSTONE, () -> 4 + RandomSource.create().nextInt(5)),
                        new BountyEntry(Items.GLOW_INK_SAC, () -> 3 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.SPIDER_EYE, () -> 3 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.GUNPOWDER, () -> 3 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.BONE, () -> 5 + RandomSource.create().nextInt(5))
                )),
                // T3
                new BountyPool(List.of(
                        new BountyEntry(Items.GOLD_INGOT, () -> 3 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.EMERALD, () -> 2 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.AMETHYST_SHARD, () -> 3 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.BLAZE_POWDER, () -> 3 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.INK_SAC, () -> 3 + RandomSource.create().nextInt(4))
                )),
                // T4
                new BountyPool(List.of(
                        new BountyEntry(Items.ANVIL, () -> 1),
                        new BountyEntry(Items.GOLDEN_APPLE, () -> 1 + RandomSource.create().nextInt(2)),
                        new BountyEntry(Items.BOOK, () -> 1 + RandomSource.create().nextInt(2)),
                        new BountyEntry(Items.CAULDRON, () -> 1),
                        new BountyEntry(Items.ITEM_FRAME, () -> 1 + RandomSource.create().nextInt(3)),
                        new BountyEntry(Items.ENDER_PEARL, () -> 1)
                )),
                // T5 (Master) — just add JolCraftItems here directly if desired
                new BountyPool(List.of(
                        new BountyEntry(Items.NETHERITE_SCRAP, () -> 1 + RandomSource.create().nextInt(2)),
                        new BountyEntry(Items.HEART_OF_THE_SEA, () -> 1),
                        new BountyEntry(Items.DRAGON_BREATH, () -> 1 + RandomSource.create().nextInt(2))
                        // To add JolCraft gems: new BountyEntry(JolCraftItems.YOUR_GEM.get(), () -> 1),
                ))
        )),
        MINER(List.of(
                new BountyPool(List.of(
                        new BountyEntry(Items.STONE, () -> 8 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.GRANITE, () -> 8 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.DIORITE, () -> 8 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.ANDESITE, () -> 8 + RandomSource.create().nextInt(8)),
                        new BountyEntry(Items.TUFF, () -> 8 + RandomSource.create().nextInt(8))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.IRON_ORE, () -> 4 + RandomSource.create().nextInt(5)),
                        new BountyEntry(Items.COPPER_ORE, () -> 4 + RandomSource.create().nextInt(5)),
                        new BountyEntry(Items.DEEPSLATE_IRON_ORE, () -> 4 + RandomSource.create().nextInt(5))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.GOLD_ORE, () -> 3 + RandomSource.create().nextInt(4)),
                        new BountyEntry(Items.EMERALD_ORE, () -> 2 + RandomSource.create().nextInt(3))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.DIAMOND_ORE, () -> 1 + randomInt(2)),
                        new BountyEntry(Items.DEEPSLATE_DIAMOND_ORE, () -> 1 + randomInt(2))
                )),
                new BountyPool(List.of(
                        new BountyEntry(Items.NETHERITE_SCRAP, () -> 1)
                ))
        ));

        private final List<BountyPool> pools;
        BountyType(List<BountyPool> pools) { this.pools = pools; }
        public BountyPool getPool(int tier) {
            if (tier < 1 || tier > pools.size()) throw new IllegalArgumentException("Invalid tier: " + tier);
            return pools.get(tier - 1);
        }
    }

    public static List<ItemStack> getReward(BountyData data, RandomSource random) {
        BountyType type = BountyType.valueOf(data.type().toUpperCase());
        int tier = data.tier();
        List<ItemStack> rewards = new ArrayList<>();

        switch (type) {
            case MERCHANT -> {
                int coins = switch (tier) {
                    case 1 -> 4 + random.nextInt(3);
                    case 2 -> 7 + random.nextInt(4);
                    case 3 -> 12 + random.nextInt(5);
                    case 4 -> 20 + random.nextInt(8);
                    case 5 -> 30 + random.nextInt(10);
                    default -> 0;
                };
                if (coins > 0) rewards.add(new ItemStack(JolCraftItems.GOLD_COIN.get(), coins));
                float crateChance = switch (tier) {
                    case 2 -> 0.125f;
                    case 3 -> 0.25f;
                    case 4 -> 0.5f;
                    case 5 -> 0.7f;
                    default -> 0f;
                };
                if (crateChance > 0 && random.nextFloat() < crateChance) {
                    boolean restock = random.nextBoolean();
                    rewards.add(new ItemStack(
                            restock ? JolCraftItems.RESTOCK_CRATE.get() : JolCraftItems.REROLL_CRATE.get()
                    ));
                }
            }

            case MINER -> {
                int num = switch (tier) {
                    case 1 -> 1;
                    case 2 -> 1 + random.nextInt(2);
                    case 3 -> 1 + random.nextInt(3);
                    case 4 -> 1 + random.nextInt(4);
                    case 5 -> 1 + random.nextInt(5);
                    default -> 0;
                };
                for (int i = 0; i < num; i++) {
                    rewards.add(new ItemStack(getWeightedGeode(random, tier)));
                }
            }


        }

        return rewards;
    }

    private static Item getWeightedGeode(RandomSource random, int tier) {
        // Each array: [small, medium, large]
        int[] weights = switch (tier) {
            case 1, 2 -> new int[]{4, 2, 1}; // Novice/Apprentice
            case 3    -> new int[]{2, 2, 2}; // Journeyman
            case 5    -> new int[]{1, 2, 4}; // Master
            default   -> new int[]{2, 2, 1}; // Expert/other (customize as needed)
        };
        int total = weights[0] + weights[1] + weights[2];
        int roll = random.nextInt(total);

        if (roll < weights[0]) return JolCraftItems.GEODE_SMALL.get();
        else if (roll < weights[0] + weights[1]) return JolCraftItems.GEODE_MEDIUM.get();
        else return JolCraftItems.GEODE_LARGE.get();
    }

    /** Main and only method. */
    public static BountyData generate(ItemStack stack, RandomSource random) {
        String typeStr = stack.get(JolCraftDataComponents.BOUNTY_TYPE.get());
        int tier = stack.getOrDefault(JolCraftDataComponents.BOUNTY_TIER.get(), 1);

        BountyType type;
        try {
            assert typeStr != null;
            type = BountyType.valueOf(typeStr.toUpperCase());
        } catch (Exception e) {
            type = BountyType.MERCHANT;
        }

        List<BountyPool> pools = type.pools;
        if (tier < 1 || tier > pools.size()) throw new IllegalArgumentException("Invalid tier: " + tier);
        BountyPool pool = pools.get(tier - 1);
        List<BountyEntry> entries = pool.entries();
        BountyEntry entry = entries.get(random.nextInt(entries.size()));
        int count = entry.count().getAsInt();

        return new BountyData(
                BuiltInRegistries.ITEM.getKey(entry.item()),
                count,
                tier,
                type.name().toLowerCase()
        );
    }

    /** Helper for static lambdas */
    private static int randomInt(int bound) {
        return RandomSource.create().nextInt(bound);
    }
}
