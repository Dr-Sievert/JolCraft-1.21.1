package net.sievert.jolcraft.world.recipe.custom.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.BountyData;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.JolCraftRecipeOutputTypes;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BountyTaskRecipe(
        DwarfProfession bountyType,
        DwarfMerchantData.Level tier,
        Item bounty,
        SimpleWeightedRandomList<RecipeOutput> objectives,
        SoundOutput sound1,
        SoundOutput sound2
) implements CustomRecipe<BountyRecipeInput> {

    private static final String OBJECTIVES_KEY =
            JolCraftStrings.plural(
                    JolCraftDictionary.OBJECTIVE
            );

    private static final String SOUND_1_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.SOUND,
                    "1"
            );

    private static final String SOUND_2_KEY =
            JolCraftStrings.underscored(
                    JolCraftDictionary.SOUND,
                    "2"
            );

    private static final LootContextParamSet OUTPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    public BountyTaskRecipe {
        Objects.requireNonNull(
                bountyType,
                BountyRecipe.TYPE_KEY
        );

        Objects.requireNonNull(
                tier,
                BountyRecipe.TIER_KEY
        );

        Objects.requireNonNull(
                bounty,
                JolCraftDictionary.RESULT
        );

        Objects.requireNonNull(
                objectives,
                OBJECTIVES_KEY
        );

        Objects.requireNonNull(
                sound1,
                SOUND_1_KEY
        );

        Objects.requireNonNull(
                sound2,
                SOUND_2_KEY
        );
    }

    @Override
    public boolean matches(
            @NotNull BountyRecipeInput input,
            @NotNull Level level
    ) {
        if (level.isClientSide) {
            return false;
        }

        ItemStack base =
                input.redeemStack();

        if (!isTaskBountyStack(base)) {
            return false;
        }

        return input.type() == bountyType
                && input.tier() == tier;
    }

    /**
     * Selects exactly one weighted objective and creates the resulting
     * configured bounty stack.
     */
    public @NotNull ItemStack createBounty(
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input
    ) {
        BountyData.BountyObjective resolved =
                resolveObjective(
                        context,
                        input
                );

        if (resolved == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result =
                new ItemStack(bounty);

        BountyRecipe.setType(
                result,
                bountyType
        );

        BountyRecipe.setTier(
                result,
                tier
        );

        result.set(
                JolCraftDataComponents.BOUNTY_DATA.get(),
                new BountyData(resolved)
        );

        return result;
    }

    private BountyData.BountyObjective resolveObjective(
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input
    ) {
        RecipeOutput selected =
                objectives.getRandomValue(
                        context.getRandom()
                ).orElse(null);

        return switch (selected) {
            case ItemOutput itemOutput -> resolveItemObjective(
                    itemOutput,
                    context,
                    input
            );
            case EntityOutput entityOutput -> resolveEntityObjective(
                    entityOutput,
                    context,
                    input
            );
            case null, default -> null;
        };

    }

    private static BountyData.BountyObjective resolveItemObjective(
            @NotNull ItemOutput itemOutput,
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input
    ) {
        List<ItemStack> generated = new ArrayList<>();

        itemOutput.generate(
                context,
                input,
                stack -> {
                    if (!stack.isEmpty()) {
                        generated.add(
                                stack.copy()
                        );
                    }
                }
        );

        if (generated.size() != 1) {
            return null;
        }

        ItemStack stack =
                generated.getFirst();

        return new BountyData.BountyObjective.ItemObjective(
                stack.getItemHolder(),
                Math.max(
                        1,
                        stack.getCount()
                )
        );
    }

    private static BountyData.BountyObjective resolveEntityObjective(
            @NotNull EntityOutput entityOutput,
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input
    ) {
        List<EntityOutput.GeneratedEntity> generated =
                new ArrayList<>();

        entityOutput.generate(
                context,
                input,
                entity -> {
                    if (entity != null) {
                        generated.add(entity);
                    }
                }
        );

        if (generated.size() != 1) {
            return null;
        }

        EntityOutput.GeneratedEntity entity =
                generated.getFirst();

        return new BountyData.BountyObjective.EntityObjective(
                BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(
                        entity.entity()
                ),
                Math.max(
                        1,
                        entity.count()
                )
        );
    }

    public void generateSound1(
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input,
            @NotNull java.util.function.Consumer<
                    SoundOutput.GeneratedSound
                    > output
    ) {
        sound1.generate(
                context,
                input,
                output
        );
    }

    public void generateSound2(
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input,
            @NotNull java.util.function.Consumer<
                    SoundOutput.GeneratedSound
                    > output
    ) {
        sound2.generate(
                context,
                input,
                output
        );
    }

    @Override
    public @NotNull ItemStack assemble(
            @NotNull BountyRecipeInput input,
            HolderLookup.@NotNull Provider registries
    ) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<
            ? extends Recipe<BountyRecipeInput>
            > getSerializer() {
        return JolCraftRecipes
                .BOUNTY_TASK_SERIALIZER
                .get();
    }

    @Override
    public @NotNull RecipeType<
            ? extends Recipe<BountyRecipeInput>
            > getType() {
        return JolCraftRecipes
                .BOUNTY_TASK_TYPE
                .get();
    }

    @Override
    public @NotNull ItemStack getResultItem(
            HolderLookup.@NotNull Provider registries
    ) {
        return new ItemStack(bounty);
    }

    public static boolean isTaskBountyStack(
            @NotNull ItemStack stack
    ) {
        if (!BountyRecipe.isValidBountyStack(stack)) {
            return false;
        }

        if (stack.has(
                JolCraftDataComponents.BOUNTY_DATA.get()
        )) {
            return false;
        }

        if (stack.has(
                JolCraftDataComponents.BOUNTY_FILL.get()
        )) {
            return false;
        }

        return !stack.has(
                JolCraftDataComponents.BOUNTY_COMPLETE.get()
        );
    }

    public static final class Serializer
            implements RecipeSerializer<BountyTaskRecipe> {

        private static final Codec<
                SimpleWeightedRandomList<RecipeOutput>
                > OBJECTIVES_CODEC =
                SimpleWeightedRandomList.wrappedCodec(
                        JolCraftRecipeOutputTypes.CODEC
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                DwarfProfession
                > BOUNTY_TYPE_STREAM_CODEC =
                StreamCodec.of(
                        (buffer, value) ->
                                buffer.writeUtf(
                                        value.professionName()
                                ),
                        buffer -> {
                            String raw =
                                    buffer.readUtf();

                            DwarfProfession type =
                                    BountyRecipe.parseType(raw);

                            if (type == null) {
                                throw new IllegalArgumentException(
                                        "unknown bounty type '"
                                                + raw
                                                + "'"
                                );
                            }

                            return type;
                        }
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                DwarfMerchantData.Level
                > BOUNTY_TIER_STREAM_CODEC =
                StreamCodec.of(
                        (buffer, value) ->
                                buffer.writeVarInt(
                                        value.getId()
                                ),
                        buffer -> {
                            int raw =
                                    buffer.readVarInt();

                            DwarfMerchantData.Level tier =
                                    BountyRecipe.parseTier(raw);

                            if (tier == null) {
                                throw new IllegalArgumentException(
                                        "unknown bounty tier '"
                                                + raw
                                                + "'"
                                );
                            }

                            return tier;
                        }
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                Item
                > ITEM_STREAM_CODEC =
                ByteBufCodecs.registry(
                        Registries.ITEM
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                SimpleWeightedRandomList<RecipeOutput>
                > OBJECTIVES_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        OBJECTIVES_CODEC
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                SoundOutput
                > SOUND_OUTPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        SoundOutput.CODEC.codec()
                );

        public static final MapCodec<BountyTaskRecipe> CODEC =
                RecordCodecBuilder
                        .<BountyTaskRecipe>mapCodec(instance ->
                                instance.group(
                                        BountyRecipe.BOUNTY_TYPE_CODEC
                                                .fieldOf(
                                                        BountyRecipe.TYPE_KEY
                                                )
                                                .forGetter(
                                                        BountyTaskRecipe::bountyType
                                                ),

                                        BountyRecipe.BOUNTY_TIER_CODEC
                                                .fieldOf(
                                                        BountyRecipe.TIER_KEY
                                                )
                                                .forGetter(
                                                        BountyTaskRecipe::tier
                                                ),

                                        BuiltInRegistries.ITEM
                                                .byNameCodec()
                                                .fieldOf(
                                                        JolCraftDictionary.RESULT
                                                )
                                                .forGetter(
                                                        BountyTaskRecipe::bounty
                                                ),

                                        OBJECTIVES_CODEC
                                                .fieldOf(
                                                        OBJECTIVES_KEY
                                                )
                                                .forGetter(
                                                        BountyTaskRecipe::objectives
                                                ),

                                        SoundOutput.CODEC
                                                .codec()
                                                .fieldOf(
                                                        SOUND_1_KEY
                                                )
                                                .forGetter(
                                                        BountyTaskRecipe::sound1
                                                ),

                                        SoundOutput.CODEC
                                                .codec()
                                                .fieldOf(
                                                        SOUND_2_KEY
                                                )
                                                .forGetter(
                                                        BountyTaskRecipe::sound2
                                                )
                                ).apply(
                                        instance,
                                        BountyTaskRecipe::new
                                )
                        )
                        .flatXmap(
                                Serializer::validate,
                                DataResult::success
                        );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                BountyTaskRecipe
                > STREAM_CODEC =
                StreamCodec.of(
                        Serializer::encode,
                        Serializer::decode
                );

        @Override
        public @NotNull MapCodec<BountyTaskRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<
                RegistryFriendlyByteBuf,
                BountyTaskRecipe
                > streamCodec() {
            return STREAM_CODEC;
        }

        public static @NotNull DataResult<BountyTaskRecipe> validate(
                BountyTaskRecipe recipe
        ) {
            DataResult<BountyTaskRecipe> base =
                    RecipeValidation.validate(recipe)
                            .require(
                                    recipe.bountyType(),
                                    BountyRecipe.TYPE_KEY
                            )
                            .require(
                                    recipe.tier(),
                                    BountyRecipe.TIER_KEY
                            )
                            .require(
                                    recipe.bounty(),
                                    JolCraftDictionary.RESULT
                            )
                            .require(
                                    recipe.objectives(),
                                    OBJECTIVES_KEY
                            )
                            .require(
                                    recipe.sound1(),
                                    SOUND_1_KEY
                            )
                            .require(
                                    recipe.sound2(),
                                    SOUND_2_KEY
                            )
                            .done();

            if (base.error().isPresent()) {
                return base;
            }

            DataResult<BountyRecipe.BountyInfo> infoResult =
                    BountyRecipe.validateInfo(
                            recipe.bountyType(),
                            recipe.tier()
                    );

            if (infoResult.error().isPresent()) {
                String message =
                        infoResult.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid bounty");

                return DataResult.error(
                        () -> message
                );
            }

            Item bounty =
                    recipe.bounty();

            if (bounty == Items.AIR) {
                return DataResult.error(() ->
                        "result must not be air"
                );
            }

            if (bounty != JolCraftItems.BOUNTY.get()
                    && bounty != JolCraftItems.BOUNTY_CRATE.get()) {
                return DataResult.error(() ->
                        "result must be jolcraft:bounty "
                                + "or jolcraft:bounty_crate"
                );
            }

            if (recipe.objectives().isEmpty()) {
                return DataResult.error(() ->
                        OBJECTIVES_KEY
                                + " must contain at least one entry"
                );
            }

            int index = 0;

            for (WeightedEntry.Wrapper<RecipeOutput> entry :
                    recipe.objectives().unwrap()) {

                RecipeOutput objective =
                        entry.data();

                if (!(objective instanceof ItemOutput)
                        && !(objective instanceof EntityOutput)) {
                    int invalidIndex =
                            index;

                    return DataResult.error(() ->
                            OBJECTIVES_KEY
                                    + "["
                                    + invalidIndex
                                    + "] must be an item "
                                    + "or entity recipe output"
                    );
                }

                if (!objective.hooks().isEmpty()) {
                    int invalidIndex =
                            index;

                    return DataResult.error(() ->
                            OBJECTIVES_KEY
                                    + "["
                                    + invalidIndex
                                    + "] must not use hooks"
                    );
                }

                DataResult<Void> objectiveValidation =
                        RecipeValidation.validateOutput(
                                objective,
                                OUTPUT_CONTEXT_PARAMS
                        );

                if (objectiveValidation.error().isPresent()) {
                    int invalidIndex =
                            index;

                    String message =
                            objectiveValidation.error()
                                    .map(DataResult.Error::message)
                                    .orElse(
                                            "invalid objective output"
                                    );

                    return DataResult.error(() ->
                            OBJECTIVES_KEY
                                    + "["
                                    + invalidIndex
                                    + "]: "
                                    + message
                    );
                }

                index++;
            }

            DataResult<Void> sound1Validation =
                    RecipeValidation.validateOutput(
                            recipe.sound1(),
                            OUTPUT_CONTEXT_PARAMS
                    );

            if (sound1Validation.error().isPresent()) {
                String message =
                        sound1Validation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid first sound");

                return DataResult.error(() ->
                        SOUND_1_KEY
                                + ": "
                                + message
                );
            }

            DataResult<Void> sound2Validation =
                    RecipeValidation.validateOutput(
                            recipe.sound2(),
                            OUTPUT_CONTEXT_PARAMS
                    );

            if (sound2Validation.error().isPresent()) {
                String message =
                        sound2Validation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid second sound");

                return DataResult.error(() ->
                        SOUND_2_KEY
                                + ": "
                                + message
                );
            }

            return DataResult.success(recipe);
        }

        private static void encode(
                RegistryFriendlyByteBuf buffer,
                BountyTaskRecipe recipe
        ) {
            BOUNTY_TYPE_STREAM_CODEC.encode(
                    buffer,
                    recipe.bountyType()
            );

            BOUNTY_TIER_STREAM_CODEC.encode(
                    buffer,
                    recipe.tier()
            );

            ITEM_STREAM_CODEC.encode(
                    buffer,
                    recipe.bounty()
            );

            OBJECTIVES_STREAM_CODEC.encode(
                    buffer,
                    recipe.objectives()
            );

            SOUND_OUTPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.sound1()
            );

            SOUND_OUTPUT_STREAM_CODEC.encode(
                    buffer,
                    recipe.sound2()
            );
        }

        private static BountyTaskRecipe decode(
                RegistryFriendlyByteBuf buffer
        ) {
            return new BountyTaskRecipe(
                    BOUNTY_TYPE_STREAM_CODEC.decode(
                            buffer
                    ),
                    BOUNTY_TIER_STREAM_CODEC.decode(
                            buffer
                    ),
                    ITEM_STREAM_CODEC.decode(
                            buffer
                    ),
                    OBJECTIVES_STREAM_CODEC.decode(
                            buffer
                    ),
                    SOUND_OUTPUT_STREAM_CODEC.decode(
                            buffer
                    ),
                    SOUND_OUTPUT_STREAM_CODEC.decode(
                            buffer
                    )
            );
        }
    }
}