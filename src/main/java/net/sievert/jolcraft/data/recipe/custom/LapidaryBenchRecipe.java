package net.sievert.jolcraft.data.recipe.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.input.LapidaryRecipeInput;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchRecipe implements Recipe<LapidaryRecipeInput> {

    private final Ingredient input;
    private final ToolType toolType;

    @Nullable
    private final ItemStack result;
    @Nullable
    private final TagKey<Item> resultTag;

    private final int xp;
    private final int minCount;
    private final int maxCount;

    public enum ToolType {
        HAMMER,
        CHISEL;

        public boolean matchesTool(ItemStack stack) {
            return switch (this) {
                case HAMMER -> stack.is(JolCraftTags.Items.ARTISAN_HAMMERS);
                case CHISEL -> stack.is(JolCraftTags.Items.CHISELS);
            };
        }
    }

    private static final Codec<ToolType> TOOL_TYPE_CODEC =
            Codec.STRING.xmap(
                    s -> ToolType.valueOf(s.toUpperCase()),
                    t -> t.name().toLowerCase()
            );

    public LapidaryBenchRecipe(
            Ingredient input,
            ToolType toolType,
            ItemStack result,
            int minCount,
            int maxCount,
            int xp
    ) {
        this.input = input;
        this.toolType = toolType;
        this.result = result;
        this.resultTag = null;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.xp = xp;
    }

    public LapidaryBenchRecipe(
            Ingredient input,
            ToolType toolType,
            TagKey<Item> resultTag,
            int minCount,
            int maxCount,
            int xp
    ) {
        this.input = input;
        this.toolType = toolType;
        this.result = null;
        this.resultTag = resultTag;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.xp = xp;
    }

    public Ingredient input() {
        return input;
    }

    public int xp() {
        return xp;
    }

    public int minCount() {
        return minCount;
    }

    public int maxCount() {
        return maxCount;
    }

    public boolean usesResultTag() {
        return this.resultTag != null;
    }

    @Override
    public boolean matches(LapidaryRecipeInput in, Level level) {
        if (level.isClientSide) return false;
        if (!input.test(in.input())) return false;
        return toolType.matchesTool(in.tool());
    }

    @Override
    public ItemStack assemble(LapidaryRecipeInput in, HolderLookup.Provider registries) {
        if (result != null) {
            return result.copy();
        }

        if (resultTag == null) return ItemStack.EMPTY;

        var lookup = registries.lookupOrThrow(Registries.ITEM);
        var setOpt = lookup.get(resultTag);
        if (setOpt.isEmpty()) return ItemStack.EMPTY;

        var first = setOpt.get().stream().findFirst().orElse(null);
        if (first == null) return ItemStack.EMPTY;

        return new ItemStack(first.value(), Math.max(1, minCount));
    }

    public ItemStack rollResult(HolderLookup.Provider registries, RandomSource random) {
        int min = Math.max(1, minCount);
        int max = Math.max(min, maxCount);
        int count = (min == max) ? min : (min + random.nextInt(max - min + 1));

        if (result != null) {
            ItemStack out = result.copy();
            out.setCount(count);
            return out;
        }

        if (resultTag == null) return ItemStack.EMPTY;

        var lookup = registries.lookupOrThrow(Registries.ITEM);
        var setOpt = lookup.get(resultTag);
        if (setOpt.isEmpty()) return ItemStack.EMPTY;

        List<Item> values = setOpt.get().stream().map(Holder::value).toList();
        if (values.isEmpty()) return ItemStack.EMPTY;

        Item picked = values.get(random.nextInt(values.size()));
        return new ItemStack(picked, count);
    }

    @Override
    public RecipeSerializer<? extends Recipe<LapidaryRecipeInput>> getSerializer() {
        return JolCraftRecipes.LAPIDARY_BENCH_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<LapidaryRecipeInput>> getType() {
        return JolCraftRecipes.LAPIDARY_BENCH_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static class Serializer implements RecipeSerializer<LapidaryBenchRecipe> {

        public static final MapCodec<LapidaryBenchRecipe> CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        Ingredient.CODEC.fieldOf(JolCraftDictionary.INPUT).forGetter(r -> r.input),
                        TOOL_TYPE_CODEC.fieldOf(JolCraftDictionary.TOOL).forGetter(r -> r.toolType),
                        ItemStack.CODEC.optionalFieldOf(JolCraftDictionary.RESULT).forGetter(r -> Optional.ofNullable(r.result)),
                        TagKey.codec(Registries.ITEM).optionalFieldOf(JolCraftDictionary.RESULT_TAG).forGetter(r -> Optional.ofNullable(r.resultTag)),
                        Codec.INT.optionalFieldOf(JolCraftDictionary.MIN_COUNT).forGetter(r -> Optional.of(r.minCount)),
                        Codec.INT.optionalFieldOf(JolCraftDictionary.MAX_COUNT).forGetter(r -> Optional.of(r.maxCount)),
                        Codec.INT.optionalFieldOf(JolCraftDictionary.XP, 0).forGetter(r -> r.xp)
                ).apply(inst, (input, tool, resultOpt, tagOpt, minOpt, maxOpt, xp) -> {
                    if (resultOpt.isPresent() && tagOpt.isPresent()) {
                        throw new IllegalStateException("Lapidary recipe cannot define both 'result' and 'result_tag'");
                    }
                    if (resultOpt.isEmpty() && tagOpt.isEmpty()) {
                        throw new IllegalStateException("Lapidary recipe must define either 'result' or 'result_tag'");
                    }

                    int base = resultOpt.map(ItemStack::getCount).orElse(1);
                    int min = Math.max(1, minOpt.orElse(base));
                    int max = Math.max(min, maxOpt.orElse(min));

                    return resultOpt
                            .map(stack -> new LapidaryBenchRecipe(input, tool, stack, min, max, xp))
                            .orElseGet(() -> new LapidaryBenchRecipe(input, tool, tagOpt.get(), min, max, xp));
                }));


        public static final StreamCodec<RegistryFriendlyByteBuf, LapidaryBenchRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<LapidaryBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LapidaryBenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static LapidaryBenchRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ToolType toolType = ToolType.values()[buf.readVarInt()];

            boolean hasResultStack = buf.readBoolean();

            int minCount = buf.readVarInt();
            int maxCount = buf.readVarInt();
            int xp = buf.readVarInt();

            if (hasResultStack) {
                ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                return new LapidaryBenchRecipe(input, toolType, result, minCount, maxCount, xp);
            }

            var tagId = buf.readResourceLocation();
            TagKey<Item> resultTag = TagKey.create(Registries.ITEM, tagId);

            return new LapidaryBenchRecipe(input, toolType, resultTag, minCount, maxCount, xp);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, LapidaryBenchRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            buf.writeVarInt(recipe.toolType.ordinal());

            boolean hasResultStack = recipe.result != null;
            buf.writeBoolean(hasResultStack);

            buf.writeVarInt(recipe.minCount);
            buf.writeVarInt(recipe.maxCount);
            buf.writeVarInt(recipe.xp);

            if (hasResultStack) {
                ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                return;
            }

            if (recipe.resultTag == null) {
                throw new IllegalStateException("Lapidary recipe missing resultTag during network encode");
            }
            buf.writeResourceLocation(recipe.resultTag.location());
        }
    }
}