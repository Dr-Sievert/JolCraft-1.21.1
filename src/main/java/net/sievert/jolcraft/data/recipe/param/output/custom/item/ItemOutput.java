package net.sievert.jolcraft.data.recipe.param.output.custom.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.ResolvedOutputParam;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ItemOutput(
        ItemSpec result,
        ItemTransforms transforms
) implements OutputParam, ResolvedOutputParam, SelfValidating<ItemOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.ITEM, JolCraftDictionary.OUTPUT));

    public static final ItemOutput EMPTY = new ItemOutput(
            ItemSpec.EMPTY,
            ItemTransforms.EMPTY
    );

    public static ItemOutput empty() {
        return EMPTY;
    }

    public static ItemOutput one(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return EMPTY;

        return new ItemOutput(
                ItemSpec.of(stack),
                ItemTransforms.EMPTY
        );
    }

    private static final Codec<ItemOutput> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ItemSpec.CODEC
                            .fieldOf(JolCraftParameterIds.RESULT)
                            .forGetter(ItemOutput::result),

                    ItemTransforms.CODEC
                            .optionalFieldOf(JolCraftParameterIds.TRANSFORMS, ItemTransforms.EMPTY)
                            .forGetter(ItemOutput::transforms)
            ).apply(instance, ItemOutput::new));

    public static final Codec<ItemOutput> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> STREAM_CODEC =
            StreamCodec.composite(
                    ItemSpec.STREAM_CODEC, ItemOutput::result,
                    ItemTransforms.STREAM_CODEC, ItemOutput::transforms,
                    ItemOutput::new
            );

    public ItemOutput(ItemSpec result, ItemTransforms transforms) {
        this.result = result != null ? result : ItemSpec.EMPTY;
        this.transforms = transforms != null ? transforms : ItemTransforms.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        return generateResolved(ctx, null);
    }

    @Override
    public @NotNull List<Output> generateResolved(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver
    ) {
        ItemStack stack = result.create(ctx);
        if (stack.isEmpty()) return List.of();

        if (resolver != null) {
            transforms.apply(ctx, resolver, stack);
        } else {
            transforms.apply(ctx, stack);
        }

        if (stack.isEmpty()) return List.of();

        return List.of(new Output.Items(List.of(stack)));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return RegistryIntrospectionSource.mergeByRegistry(List.of(
                result,
                transforms
        ));
    }

    @Override
    public @NotNull DataResult<ItemOutput> validate() {
        {
            DataResult<ItemSpec> r = result.validate();
            if (r.error().isPresent()) return SelfValidating.invalid(JolCraftParameterIds.RESULT + " invalid");
        }

        {
            DataResult<ItemTransforms> r = transforms.validate();
            if (r.error().isPresent()) return SelfValidating.invalid(JolCraftParameterIds.TRANSFORMS + " invalid");
        }

        return SelfValidating.ok(this);
    }
}