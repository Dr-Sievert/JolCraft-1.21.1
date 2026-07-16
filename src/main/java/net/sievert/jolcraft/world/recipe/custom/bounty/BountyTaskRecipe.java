package net.sievert.jolcraft.world.recipe.custom.bounty;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
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
import net.sievert.jolcraft.world.recipe.output.EntityOutput;
import net.sievert.jolcraft.world.recipe.output.ItemOutput;
import net.sievert.jolcraft.world.recipe.output.JolCraftRecipeOutputTypes;
import net.sievert.jolcraft.world.recipe.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public record BountyTaskRecipe(
        DwarfProfession bountyType,
        DwarfMerchantData.Level tier,
        Item bounty,
        RecipeOutput objective,
        SoundOutput sound1,
        SoundOutput sound2
) implements CustomRecipe<BountyRecipeInput> {

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
                objective,
                JolCraftDictionary.OBJECTIVE
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

        ItemStack base = input.redeemStack();

        if (!isTaskBountyStack(base)) {
            return false;
        }

        return input.type() == bountyType
                && input.tier() == tier;
    }

    /**
     * Resolves the configured objective and creates the resulting bounty stack.
     *
     * The caller supplies the runtime LootContext because Recipe.assemble(...)
     * does not provide a ServerLevel or other runtime recipe context.
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

        ItemStack result = new ItemStack(bounty);

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
            LootContext context,
            BountyRecipeInput input
    ) {
        if (objective instanceof ItemOutput itemOutput) {
            AtomicReference<ItemStack> generated =
                    new AtomicReference<>(ItemStack.EMPTY);

            itemOutput.generate(
                    context,
                    input,
                    stack -> {
                        if (generated.get().isEmpty()) {
                            generated.set(stack.copy());
                        }
                    }
            );

            ItemStack stack = generated.get();

            if (stack.isEmpty()) {
                return null;
            }

            return new BountyData.BountyObjective.ItemObjective(
                    stack.getItemHolder(),
                    Math.max(
                            1,
                            stack.getCount()
                    )
            );
        }

        if (objective instanceof EntityOutput entityOutput) {
            AtomicReference<EntityOutput.GeneratedEntity> generated =
                    new AtomicReference<>();

            entityOutput.generate(
                    context,
                    input,
                    entity -> {
                        if (generated.get() == null) {
                            generated.set(entity);
                        }
                    }
            );

            EntityOutput.GeneratedEntity entity = generated.get();

            if (entity == null) {
                return null;
            }

            return new BountyData.BountyObjective.EntityObjective(
                    BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entity.entity()),
                    Math.max(1, entity.count())
            );
        }

        return null;
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
        return JolCraftRecipes.BOUNTY_TASK_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<
            ? extends Recipe<BountyRecipeInput>
            > getType() {
        return JolCraftRecipes.BOUNTY_TASK_TYPE.get();
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
                RecipeOutput
                > OBJECTIVE_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        JolCraftRecipeOutputTypes.CODEC
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

                                        JolCraftRecipeOutputTypes.CODEC
                                                .fieldOf(
                                                        JolCraftDictionary.OBJECTIVE
                                                )
                                                .forGetter(
                                                        BountyTaskRecipe::objective
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
                StreamCodec.composite(
                        BOUNTY_TYPE_STREAM_CODEC,
                        BountyTaskRecipe::bountyType,

                        BOUNTY_TIER_STREAM_CODEC,
                        BountyTaskRecipe::tier,

                        ITEM_STREAM_CODEC,
                        BountyTaskRecipe::bounty,

                        OBJECTIVE_STREAM_CODEC,
                        BountyTaskRecipe::objective,

                        SOUND_OUTPUT_STREAM_CODEC,
                        BountyTaskRecipe::sound1,

                        SOUND_OUTPUT_STREAM_CODEC,
                        BountyTaskRecipe::sound2,

                        BountyTaskRecipe::new
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

        public static DataResult<BountyTaskRecipe> validate(
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
                                    recipe.objective(),
                                    JolCraftDictionary.OBJECTIVE
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
                String message = infoResult.error()
                        .map(DataResult.Error::message)
                        .orElse("invalid bounty");

                return DataResult.error(
                        () -> message
                );
            }

            Item bounty = recipe.bounty();

            if (bounty == Items.AIR) {
                return DataResult.error(
                        () -> "result must not be air"
                );
            }

            if (bounty != JolCraftItems.BOUNTY.get()
                    && bounty != JolCraftItems.BOUNTY_CRATE.get()) {
                return DataResult.error(
                        () -> "result must be jolcraft:bounty "
                                + "or jolcraft:bounty_crate"
                );
            }

            RecipeOutput objective =
                    recipe.objective();

            if (!(objective instanceof ItemOutput)
                    && !(objective instanceof EntityOutput)) {
                return DataResult.error(
                        () -> "objective must be an item "
                                + "or entity recipe output"
                );
            }

            if (!objective.hooks().isEmpty()) {
                return DataResult.error(
                        () -> "bounty task objectives must not use hooks"
                );
            }

            return DataResult.success(recipe);
        }
    }
}