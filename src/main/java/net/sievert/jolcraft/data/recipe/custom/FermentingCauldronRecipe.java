package net.sievert.jolcraft.data.recipe.custom;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.input.FermentingCauldronRecipeInput;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FermentingCauldronRecipe implements Recipe<FermentingCauldronRecipeInput> {

    private final Ingredient ingredient;

    @Nullable
    private final Ingredient validStates;

    private final int brewTicks;
    private final int bubbleTicks;
    private final int color;

    @Nullable
    private final EffectData effect;

    private final boolean finalize;

    @Nullable
    private final ItemStack extract;

    public FermentingCauldronRecipe(
            Ingredient ingredient,
            @Nullable Ingredient validStates,
            int brewTicks,
            int bubbleTicks,
            int color,
            @Nullable EffectData effect,
            boolean finalize,
            @Nullable ItemStack extract
    ) {
        this.ingredient = ingredient;
        this.validStates = validStates;
        this.brewTicks = Math.max(1, brewTicks);
        this.bubbleTicks = Math.max(1, bubbleTicks);
        this.color = color;
        this.effect = effect;
        this.finalize = finalize;
        this.extract = (extract == null || extract.isEmpty()) ? null : extract.copy();
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public int brewTicks() {
        return brewTicks;
    }

    public int bubbleTicks() {
        return bubbleTicks;
    }

    public int color() {
        return color;
    }

    @Nullable
    public EffectData effect() {
        return effect;
    }

    public boolean finalizeBrew() {
        return finalize;
    }

    @Nullable
    public ItemStack extract() {
        return extract;
    }

    public boolean isExtraction() {
        return extract != null;
    }

    @Override
    public boolean matches(FermentingCauldronRecipeInput in, Level level) {
        if (!ingredient.test(in.usedItem())) return false;

        if (extract != null) {
            return validStates == null || validStates.test(in.lastIngredient());
        }

        if (validStates == null) {
            return in.isVanillaFullWaterCauldron();
        }

        return !in.lastIngredient().isEmpty() && validStates.test(in.lastIngredient());
    }

    @Override
    public ItemStack assemble(FermentingCauldronRecipeInput in, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<FermentingCauldronRecipeInput>> getSerializer() {
        return JolCraftRecipes.FERMENTING_CAULDRON_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<FermentingCauldronRecipeInput>> getType() {
        return JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get();
    }

    public record EffectData(
            ResourceKey<MobEffect> id,
            int duration,
            int amplifier
    ) {
        public static final Codec<EffectData> CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        ResourceKey.codec(Registries.MOB_EFFECT).fieldOf("id").forGetter(EffectData::id),
                        Codec.INT.fieldOf("duration").forGetter(EffectData::duration),
                        Codec.INT.optionalFieldOf("amplifier", 0).forGetter(EffectData::amplifier)
                ).apply(inst, EffectData::new));

        public static EffectData fromHolder(Holder<MobEffect> effect, int duration, int amplifier) {
            ResourceKey<MobEffect> key = effect.unwrapKey()
                    .orElseThrow(() -> new IllegalStateException("Unregistered MobEffect holder: " + effect));
            return new EffectData(key, duration, amplifier);
        }
    }

    public static class Serializer implements RecipeSerializer<FermentingCauldronRecipe> {

        private static final Codec<Integer> COLOR_CODEC =
                Codec.either(Codec.INT, Codec.STRING).comapFlatMap(
                        either -> either.map(
                                DataResult::success,
                                Serializer::parseColorStringResult
                        ),
                        argb -> Either.right(toRgbString(argb))
                );

        private static DataResult<Integer> parseColorStringResult(String s) {
            try {
                return DataResult.success(parseColorString(s));
            } catch (RuntimeException e) {
                return DataResult.error(() -> "Invalid color: " + s + " (expected #RRGGBB or #AARRGGBB)");
            }
        }

        public static final MapCodec<FermentingCauldronRecipe> CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),

                        Ingredient.CODEC.optionalFieldOf("valid_states")
                                .forGetter(r -> Optional.ofNullable(r.validStates)),

                        Codec.INT.fieldOf("brew_ticks").forGetter(r -> r.brewTicks),
                        Codec.INT.fieldOf("bubble_ticks").forGetter(r -> r.bubbleTicks),

                        COLOR_CODEC.fieldOf("color").forGetter(r -> r.color),

                        EffectData.CODEC.optionalFieldOf("effect").forGetter(r -> Optional.ofNullable(r.effect)),
                        Codec.BOOL.optionalFieldOf("finalize", false).forGetter(r -> r.finalize),

                        ItemStack.CODEC.optionalFieldOf("extract")
                                .forGetter(r -> Optional.ofNullable(r.extract))
                ).apply(inst, (ingredient, validStatesOpt, brewTicks, bubbleTicks, color, effectOpt, finalize, extractOpt) ->
                        new FermentingCauldronRecipe(
                                ingredient,
                                validStatesOpt.orElse(null),
                                brewTicks,
                                bubbleTicks,
                                color,
                                effectOpt.orElse(null),
                                finalize,
                                extractOpt.orElse(null)
                        )
                ));

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingCauldronRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<FermentingCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FermentingCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static int parseColorString(String s) {
            String str = s.trim();
            if (str.startsWith("#")) str = str.substring(1);

            if (str.length() == 6) {
                int rgb = Integer.parseUnsignedInt(str, 16);
                return 0xFF000000 | rgb;
            }

            if (str.length() == 8) {
                return (int) Long.parseUnsignedLong(str, 16);
            }

            throw new IllegalArgumentException("Invalid color: " + s + " (expected #RRGGBB or #AARRGGBB)");
        }

        private static String toRgbString(int argb) {
            int rgb = argb & 0xFFFFFF;
            return String.format("#%06X", rgb);
        }

        private static FermentingCauldronRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);

            Ingredient validStates = null;
            boolean hasValidStates = buf.readBoolean();
            if (hasValidStates) {
                validStates = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            }

            int brewTicks = buf.readVarInt();
            int bubbleTicks = buf.readVarInt();
            int color = buf.readInt();

            EffectData effect = null;
            boolean hasEffect = buf.readBoolean();
            if (hasEffect) {
                var idLoc = buf.readResourceLocation();
                ResourceKey<MobEffect> id = ResourceKey.create(Registries.MOB_EFFECT, idLoc);

                int duration = buf.readVarInt();
                int amplifier = buf.readVarInt();
                effect = new EffectData(id, duration, amplifier);
            }

            boolean finalize = buf.readBoolean();

            ItemStack extract = null;
            boolean hasExtract = buf.readBoolean();
            if (hasExtract) {
                extract = ItemStack.STREAM_CODEC.decode(buf);
                if (extract.isEmpty()) extract = null;
            }

            return new FermentingCauldronRecipe(ingredient, validStates, brewTicks, bubbleTicks, color, effect, finalize, extract);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, FermentingCauldronRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);

            boolean hasValidStates = recipe.validStates != null;
            buf.writeBoolean(hasValidStates);
            if (hasValidStates) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.validStates);
            }

            buf.writeVarInt(recipe.brewTicks);
            buf.writeVarInt(recipe.bubbleTicks);
            buf.writeInt(recipe.color);

            boolean hasEffect = recipe.effect != null;
            buf.writeBoolean(hasEffect);
            if (hasEffect) {
                EffectData eff = recipe.effect;
                buf.writeResourceLocation(eff.id().location());
                buf.writeVarInt(eff.duration());
                buf.writeVarInt(eff.amplifier());
            }

            buf.writeBoolean(recipe.finalize);

            boolean hasExtract = recipe.extract != null;
            buf.writeBoolean(hasExtract);
            if (hasExtract) {
                ItemStack.STREAM_CODEC.encode(buf, recipe.extract);
            }
        }
    }
}