package net.sievert.jolcraft.entity.util.dwarf.trade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.sievert.jolcraft.item.JolCraftItems;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class DwarfTrades {

    // -- BUYING (player pays gold, gets item) --

    public static class ItemsForGold implements ItemListing {
        private final Item item;
        public final int minItemCount;
        public final int maxItemCount;
        public final int minGoldCost;
        public final int maxGoldCost;
        private final int maxUses, villagerXp;
        private final float priceMultiplier;
        private final Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider;

        // Full constructor (random item count, random gold cost)
        public ItemsForGold(Item item, int minGoldCost, int maxGoldCost, int minItemCount, int maxItemCount, int maxUses, int villagerXp) {
            this(item, minGoldCost, maxGoldCost, minItemCount, maxItemCount, maxUses, villagerXp, 0.05F, Optional.empty());
        }

        // Constructor: fixed item output, random gold cost
        public ItemsForGold(Item item, int minGoldCost, int maxGoldCost, int itemCount, int maxUses, int villagerXp) {
            this(item, minGoldCost, maxGoldCost, itemCount, itemCount, maxUses, villagerXp, 0.05F, Optional.empty());
        }

        // Backward compatibility: fixed item, fixed gold
        public ItemsForGold(Item item, int goldCost, int itemCount, int maxUses, int villagerXp) {
            this(item, goldCost, goldCost, itemCount, itemCount, maxUses, villagerXp);
        }

        // ItemStack overload for advanced cases
        public ItemsForGold(ItemStack stack, int minGoldCost, int maxGoldCost, int maxUses, int villagerXp) {
            this(stack.getItem(), minGoldCost, maxGoldCost, stack.getCount(), stack.getCount(), maxUses, villagerXp);
        }

        // Full constructor with price multiplier and enchantments
        public ItemsForGold(Item item, int minGoldCost, int maxGoldCost, int minItemCount, int maxItemCount, int maxUses, int villagerXp, float priceMultiplier, Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider) {
            this.item = item;
            this.minItemCount = minItemCount;
            this.maxItemCount = maxItemCount;
            this.minGoldCost = minGoldCost;
            this.maxGoldCost = maxGoldCost;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
            this.enchantmentProvider = enchantmentProvider;
        }

        @Override
        public DwarfMerchantOffer getOffer(Entity entity, RandomSource random) {
            // Randomly generate values for the items and gold cost
            int gold = Mth.nextInt(random, minGoldCost, maxGoldCost);
            int items = Mth.nextInt(random, minItemCount, maxItemCount);
            ItemStack stack = new ItemStack(item, items);

            // Apply enchantments if available
            if (enchantmentProvider.isPresent()) {
                Level level = entity.level();
                EnchantmentHelper.enchantItemFromProvider(stack, level.registryAccess(), enchantmentProvider.get(), level.getCurrentDifficultyAt(entity.blockPosition()), random);
            }

            // Handle the gold cost, check for CoinPouchItem
            DwarfItemCost itemCost;
            if (gold > 0) {
                // If the cost is gold, check if it's being handled via a coin pouch
                itemCost = new DwarfItemCost(JolCraftItems.GOLD_COIN.get(), gold);
            } else {
                // If there's no gold, no coin pouch, this part will be empty
                itemCost = new DwarfItemCost(JolCraftItems.GOLD_COIN.get(), 0);
            }

            // Return the DwarfMerchantOffer with a dynamic cost handling
            return new DwarfMerchantOffer(
                    itemCost,  // This will be used for the gold cost
                    stack,     // The item to return
                    maxUses,
                    villagerXp,
                    priceMultiplier
            );
        }
    }



    // -- BUYING + INPUT ITEM (player pays item+gold, gets item) --

    public static class ItemsAndGoldToItems implements ItemListing {
        private final Item inputItem;
        public final int minInputCount;
        public final int maxInputCount;
        public final int minGoldCost;
        public final int maxGoldCost;
        private final Item outputItem;
        public final int minOutputCount;
        public final int maxOutputCount;
        private final int maxUses, villagerXp;
        private final float priceMultiplier;
        private final Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider;

        // Full constructor (random everything)
        public ItemsAndGoldToItems(ItemLike fromItem, int minInputCount, int maxInputCount,
                                   int minGoldCost, int maxGoldCost,
                                   ItemLike toItem, int minOutputCount, int maxOutputCount,
                                   int maxUses, int villagerXp, float priceMultiplier) {
            this(fromItem, minInputCount, maxInputCount, minGoldCost, maxGoldCost, toItem, minOutputCount, maxOutputCount, maxUses, villagerXp, priceMultiplier, Optional.empty());
        }

        // Constructor with enchantments support
        public ItemsAndGoldToItems(ItemLike fromItem, int minInputCount, int maxInputCount,
                                   int minGoldCost, int maxGoldCost,
                                   ItemLike toItem, int minOutputCount, int maxOutputCount,
                                   int maxUses, int villagerXp, float priceMultiplier,
                                   Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider) {
            this.inputItem = fromItem.asItem();
            this.minInputCount = minInputCount;
            this.maxInputCount = maxInputCount;
            this.minGoldCost = minGoldCost;
            this.maxGoldCost = maxGoldCost;
            this.outputItem = toItem.asItem();
            this.minOutputCount = minOutputCount;
            this.maxOutputCount = maxOutputCount;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
            this.enchantmentProvider = enchantmentProvider;
        }

        // Single value constructor (backward compatibility)
        public ItemsAndGoldToItems(ItemLike fromItem, int fromCount, int goldCost, ItemLike toItem, int toCount, int maxUses, int villagerXp, float priceMultiplier) {
            this(fromItem, fromCount, fromCount, goldCost, goldCost, toItem, toCount, toCount, maxUses, villagerXp, priceMultiplier, Optional.empty());
        }

        // Fixed input/output, random gold cost
        public ItemsAndGoldToItems(
                ItemLike fromItem, int fromCount,
                int minGoldCost, int maxGoldCost,
                ItemLike toItem, int toCount,
                int maxUses, int villagerXp, float priceMultiplier
        ) {
            this(
                    fromItem, fromCount, fromCount, minGoldCost, maxGoldCost,
                    toItem, toCount, toCount, maxUses, villagerXp, priceMultiplier, Optional.empty()
            );
        }

        @Nullable
        @Override
        public DwarfMerchantOffer getOffer(Entity entity, RandomSource random) {
            // Generate random item counts for input and output, and random gold cost
            int inputCount = Mth.nextInt(random, minInputCount, maxInputCount);
            int outputCount = Mth.nextInt(random, minOutputCount, maxOutputCount);
            int gold = Mth.nextInt(random, minGoldCost, maxGoldCost);
            ItemStack input = new ItemStack(inputItem, inputCount);
            ItemStack output = new ItemStack(outputItem, outputCount);

            // Apply enchantments if available
            if (enchantmentProvider.isPresent()) {
                Level level = entity.level();
                EnchantmentHelper.enchantItemFromProvider(output, level.registryAccess(), enchantmentProvider.get(), level.getCurrentDifficultyAt(entity.blockPosition()), random);
            }

            // Return the offer with a dynamic cost handling
            return new DwarfMerchantOffer(
                    new DwarfItemCost(JolCraftItems.GOLD_COIN.get(), gold),  // Handle gold cost using DwarfItemCost
                    Optional.of(new DwarfItemCost(inputItem, inputCount)),  // Handle item input count
                    output,  // Item output
                    0,  // Set the default uses (can be set dynamically in other logic)
                    maxUses,  // Max uses for the trade
                    villagerXp,  // XP reward for trade
                    priceMultiplier  // Price multiplier for the trade
            );
        }
    }


    // -- WITH CUSTOM STACK MODIFIERS --

    public static class ItemsWithDataForGold implements ItemListing {
        private final Item item;
        public final int minItemCount;
        public final int maxItemCount;
        public final int minGoldCost;
        public final int maxGoldCost;
        private final int maxUses, villagerXp;
        private final float priceMultiplier;
        private final Consumer<ItemStack> stackModifier;

        // Constructor with random item count and gold cost
        public ItemsWithDataForGold(Item item, int minItemCount, int maxItemCount, int minGoldCost, int maxGoldCost, int maxUses, int villagerXp, Consumer<ItemStack> stackModifier) {
            this(item, minItemCount, maxItemCount, minGoldCost, maxGoldCost, maxUses, villagerXp, 0.05F, stackModifier);
        }

        // Full constructor with priceMultiplier and stackModifier
        public ItemsWithDataForGold(Item item, int minItemCount, int maxItemCount, int minGoldCost, int maxGoldCost, int maxUses, int villagerXp, float priceMultiplier, Consumer<ItemStack> stackModifier) {
            this.item = item;
            this.minItemCount = minItemCount;
            this.maxItemCount = maxItemCount;
            this.minGoldCost = minGoldCost;
            this.maxGoldCost = maxGoldCost;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
            this.stackModifier = stackModifier != null ? stackModifier : s -> {};  // Default no-op if null
        }

        // Legacy constructor for compatibility
        public ItemsWithDataForGold(Item item, int itemCount, int goldCost, int maxUses, int villagerXp, Consumer<ItemStack> stackModifier) {
            this(item, itemCount, itemCount, goldCost, goldCost, maxUses, villagerXp, stackModifier);
        }

        @Override
        public DwarfMerchantOffer getOffer(Entity trader, RandomSource random) {
            // Generate random values for the item and gold cost
            int itemCount = Mth.nextInt(random, minItemCount, maxItemCount);
            int gold = Mth.nextInt(random, minGoldCost, maxGoldCost);
            ItemStack stack = new ItemStack(item, itemCount);

            // Apply any stack modifications (e.g., enchantments, etc.)
            stackModifier.accept(stack);

            // Return the offer with the gold cost handled separately in the trade logic
            return new DwarfMerchantOffer(
                    new DwarfItemCost(JolCraftItems.GOLD_COIN.get(), gold),  // Gold cost for the trade
                    stack,     // Output item stack
                    maxUses,   // Max uses for the offer
                    villagerXp,// XP reward for trade
                    priceMultiplier // Price multiplier for the offer
            );
        }
    }

    public static class ItemsAndGoldToItemsWithData implements ItemListing {
        private final Item inputItem;
        public final int minInputCount;
        public final int maxInputCount;
        public final int minGoldCost;
        public final int maxGoldCost;
        private final Item outputItem;
        public final int minOutputCount;
        public final int maxOutputCount;
        private final int maxUses, villagerXp;
        private final float priceMultiplier;
        private final Consumer<ItemStack> stackModifier;

        // Constructor for random item counts and gold costs
        public ItemsAndGoldToItemsWithData(ItemLike input, int minInputCount, int maxInputCount, int minGoldCost, int maxGoldCost,
                                           Item output, int minOutputCount, int maxOutputCount,
                                           int maxUses, int villagerXp, float priceMultiplier, Consumer<ItemStack> stackModifier) {
            this.inputItem = input.asItem();
            this.minInputCount = minInputCount;
            this.maxInputCount = maxInputCount;
            this.minGoldCost = minGoldCost;
            this.maxGoldCost = maxGoldCost;
            this.outputItem = output.asItem();
            this.minOutputCount = minOutputCount;
            this.maxOutputCount = maxOutputCount;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
            this.stackModifier = stackModifier != null ? stackModifier : s -> {};  // Default no-op if null
        }

        // Legacy constructor for backward compatibility
        public ItemsAndGoldToItemsWithData(ItemLike input, int inputCount, int goldCost, Item output, int outputCount, int maxUses, int villagerXp, float priceMultiplier, Consumer<ItemStack> stackModifier) {
            this(input, inputCount, inputCount, goldCost, goldCost, output, outputCount, outputCount, maxUses, villagerXp, priceMultiplier, stackModifier);
        }

        @Override
        public DwarfMerchantOffer getOffer(Entity entity, RandomSource random) {
            // Randomly generate input, gold cost, and output counts
            int inputCount = Mth.nextInt(random, minInputCount, maxInputCount);
            int gold = Mth.nextInt(random, minGoldCost, maxGoldCost);
            int outputCount = Mth.nextInt(random, minOutputCount, maxOutputCount);

            // Create the output item stack
            ItemStack stack = new ItemStack(outputItem, outputCount);

            // Apply any stack modifications (e.g., enchantments)
            stackModifier.accept(stack);

            // Return the DwarfMerchantOffer with gold cost and item output
            return new DwarfMerchantOffer(
                    new DwarfItemCost(JolCraftItems.GOLD_COIN.get(), gold),  // Gold cost as DwarfItemCost
                    Optional.of(new DwarfItemCost(inputItem, inputCount)),  // Input item count
                    stack,  // Output item stack
                    0,  // Default uses, can be updated later
                    maxUses,  // Max uses for the trade
                    villagerXp,  // XP reward for the trade
                    priceMultiplier  // Price multiplier for the trade
            );
        }
    }

    // -- EXCHANGING (item for item, with ranges) --

    public static class ItemForItemWithData implements ItemListing {
        private final Item inputItem;
        public final int minInputCount;
        public final int maxInputCount;
        private final Item outputItem;
        public final int minOutputCount;
        public final int maxOutputCount;
        private final int maxUses, villagerXp;
        private final float priceMultiplier;
        private final Consumer<ItemStack> stackModifier;

        // Constructor for random item counts and item output, with stackModifier included
        public ItemForItemWithData(ItemLike input, int minInputCount, int maxInputCount, ItemLike output, int minOutputCount, int maxOutputCount, int maxUses, int villagerXp, float priceMultiplier, Consumer<ItemStack> stackModifier) {
            this.inputItem = input.asItem();
            this.minInputCount = minInputCount;
            this.maxInputCount = maxInputCount;
            this.outputItem = output.asItem();
            this.minOutputCount = minOutputCount;
            this.maxOutputCount = maxOutputCount;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
            this.stackModifier = stackModifier != null ? stackModifier : s -> {};  // Default no-op if null
        }

        // Legacy constructor for backward compatibility, with stackModifier
        public ItemForItemWithData(ItemLike input, int inputCount, ItemLike output, int outputCount, int maxUses, int villagerXp, float priceMultiplier, Consumer<ItemStack> stackModifier) {
            this(input, inputCount, inputCount, output, outputCount, outputCount, maxUses, villagerXp, priceMultiplier, stackModifier);
        }

        @Nullable
        @Override
        public DwarfMerchantOffer getOffer(Entity entity, RandomSource random) {
            // Randomly generate input and output counts
            int inputCount = Mth.nextInt(random, minInputCount, maxInputCount);
            int outputCount = Mth.nextInt(random, minOutputCount, maxOutputCount);

            // Create the output item stack
            ItemStack stack = new ItemStack(outputItem, outputCount);

            // Apply any stack modifications (e.g., enchantments, lore lines, bounty tier)
            stackModifier.accept(stack);

            // Create and return the trade offer
            return new DwarfMerchantOffer(
                    new DwarfItemCost(inputItem, inputCount),  // Input item and count
                    stack,  // Output item stack (modified with stackModifier)
                    maxUses,   // Max uses for the offer
                    villagerXp,// XP reward for trade
                    priceMultiplier // Price multiplier for the offer
            );
        }
    }


    // -- SELLING (player sells item, gets gold payout) --

    public static class GoldForItems implements ItemListing {
        private final ItemLike item;
        public final int minInputCount;
        public final int maxInputCount;
        private final int maxUses, villagerXp;
        public final int minGoldAmount;
        public final int maxGoldAmount;
        private final float priceMultiplier;
        // Full constructor
        public GoldForItems(ItemLike item, int minInputCount, int maxInputCount, int maxUses, int villagerXp, int minGoldAmount, int maxGoldAmount) {
            this.item = item;
            this.minInputCount = minInputCount;
            this.maxInputCount = maxInputCount;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.minGoldAmount = minGoldAmount;
            this.maxGoldAmount = maxGoldAmount;
            this.priceMultiplier = 0.05F;
        }
        // Single value constructor (backward compat)
        public GoldForItems(ItemLike item, int inputCount, int maxUses, int villagerXp, int goldAmount) {
            this(item, inputCount, inputCount, maxUses, villagerXp, goldAmount, goldAmount);
        }
        public GoldForItems(ItemLike item, int inputCount, int maxUses, int villagerXp, int minGold, int maxGold) {
            this(item, inputCount, inputCount, maxUses, villagerXp, minGold, maxGold);
        }
        @Override
        public DwarfMerchantOffer getOffer(Entity entity, RandomSource random) {
            int inputCount = Mth.nextInt(random, minInputCount, maxInputCount);
            int payout = Mth.nextInt(random, minGoldAmount, maxGoldAmount);
            return new DwarfMerchantOffer(
                    new DwarfItemCost(item.asItem(), inputCount),
                    new ItemStack(JolCraftItems.GOLD_COIN.get(), payout),
                    maxUses,
                    villagerXp,
                    priceMultiplier
            );
        }
    }


    // -- MAPS (special) --

    public static class TreasureMapForGold implements ItemListing {
        public final int goldCost;
        private final TagKey<Structure> destinationTag;
        private final String displayName;
        private final Holder<MapDecorationType> destinationType;
        private final int maxUses;
        private final int dwarfXp;

        public TreasureMapForGold(int goldCost, TagKey<Structure> destinationTag, String displayName, Holder<MapDecorationType> destinationType, int maxUses, int dwarfXp) {
            this.goldCost = goldCost;
            this.destinationTag = destinationTag;
            this.displayName = displayName;
            this.destinationType = destinationType;
            this.maxUses = maxUses;
            this.dwarfXp = dwarfXp;
        }

        @Nullable
        @Override
        public DwarfMerchantOffer getOffer(Entity trader, RandomSource random) {
            if (!(trader.level() instanceof ServerLevel serverLevel)) {
                return null;
            }
            BlockPos targetPos;
            try {
                targetPos = serverLevel.findNearestMapStructure(this.destinationTag, trader.blockPosition(), 100, true);
            } catch (Exception e) {
                return null;
            }
            if (targetPos == null) return null;
            ItemStack map = MapItem.create(serverLevel, targetPos.getX(), targetPos.getZ(), (byte) 2, true, true);
            MapItem.renderBiomePreviewMap(serverLevel, map);
            MapItemSavedData.addTargetDecoration(map, targetPos, "+", this.destinationType);
            map.set(DataComponents.ITEM_NAME, Component.translatable(this.displayName));
            return new DwarfMerchantOffer(
                    new DwarfItemCost(JolCraftItems.GOLD_COIN.get(), this.goldCost),
                    Optional.of(new DwarfItemCost(Items.MAP, 1)),
                    map,
                    0,
                    this.maxUses,
                    this.dwarfXp,
                    0.2F
            );
        }
    }

    public interface ItemListing {
        @Nullable
        DwarfMerchantOffer getOffer(Entity trader, RandomSource random);
    }

    //JEI
    public static ItemStack getExampleInputA(DwarfTrades.ItemListing listing) {
        // Revert to original logic without the alternating logic
        if (listing instanceof DwarfTrades.ItemsForGold) {
            return new ItemStack(JolCraftItems.GOLD_COIN.get());
        } else if (listing instanceof DwarfTrades.GoldForItems trade) {
            return new ItemStack(trade.item.asItem());
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItems || listing instanceof DwarfTrades.ItemsAndGoldToItemsWithData) {
            return new ItemStack(JolCraftItems.GOLD_COIN.get());
        } else if (listing instanceof ItemForItemWithData trade) {
            return new ItemStack(trade.inputItem);
        } else if (listing instanceof DwarfTrades.TreasureMapForGold) {
            return new ItemStack(JolCraftItems.GOLD_COIN.get());
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getExampleInputB(DwarfTrades.ItemListing listing) {
        if (listing instanceof DwarfTrades.ItemsAndGoldToItems trade) {
            return new ItemStack(trade.inputItem);
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItemsWithData) {
            return new ItemStack(((DwarfTrades.ItemsAndGoldToItemsWithData) listing).inputItem);
        } else if (listing instanceof DwarfTrades.TreasureMapForGold) {
            return new ItemStack(Items.MAP);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getExampleOutput(DwarfTrades.ItemListing listing) {
        if (listing instanceof DwarfTrades.ItemsForGold trade) {
            return new ItemStack(trade.item);
        } else if (listing instanceof DwarfTrades.GoldForItems) {
            return new ItemStack(JolCraftItems.GOLD_COIN.get());
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItems trade) {
            return new ItemStack(trade.outputItem);
        } else if (listing instanceof DwarfTrades.ItemsWithDataForGold trade) {
            ItemStack stack = new ItemStack(trade.item);
            trade.stackModifier.accept(stack);
            return stack;
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItemsWithData trade) {
            ItemStack stack = new ItemStack(trade.outputItem);
            trade.stackModifier.accept(stack);
            return stack;
        } else if (listing instanceof ItemForItemWithData trade) {
            ItemStack stack = new ItemStack(trade.outputItem);
            trade.stackModifier.accept(stack);
            return stack;
        } else if (listing instanceof DwarfTrades.TreasureMapForGold trade) {
            ItemStack stack = new ItemStack(Items.FILLED_MAP);
            stack.set(DataComponents.ITEM_NAME, Component.translatable(trade.displayName));
            return stack;
        }
        return ItemStack.EMPTY;
    }







}
