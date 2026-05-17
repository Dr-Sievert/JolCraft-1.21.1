package net.sievert.jolcraft.world.recipe.param.output.custom.item.transform;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldAnchor;
import net.sievert.jolcraft.param.runtime.WorldContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record ItemTransforms(
        List<EnchantmentTransform> enchantments,
        List<ComponentTransform> components
) implements SelfValidating<ItemTransforms>, RegistryIntrospectionSource {

    public static final ItemTransforms EMPTY =
            new ItemTransforms(List.of(), List.of());

    private record FullRaw(
            List<EnchantmentTransform> enchantments,
            List<ComponentTransform> components
    ) {}

    private record FlatSetRaw(
            DataComponentPatch set
    ) {}

    private static final Codec<FullRaw> FULL_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EnchantmentTransform.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.ENCHANTMENTS, List.of())
                            .forGetter(FullRaw::enchantments),

                    ComponentTransform.CODEC.listOf()
                            .optionalFieldOf(JolCraftStrings.plural(JolCraftParameterIds.DATA_COMPONENT), List.of())
                            .forGetter(FullRaw::components)
            ).apply(instance, FullRaw::new));

    private static final Codec<FlatSetRaw> FLAT_SET_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    DataComponentPatch.CODEC
                            .fieldOf(JolCraftDictionary.SET)
                            .forGetter(FlatSetRaw::set)
            ).apply(instance, FlatSetRaw::new));

    public static final Codec<ItemTransforms> CODEC =
            ParamCodecContract.create(
                    Codec.either(FLAT_SET_CODEC, FULL_CODEC),
                    ItemTransforms::fromRaw,
                    ItemTransforms::toRaw
            );

    private static @NotNull DataResult<ItemTransforms> fromRaw(@NotNull Either<FlatSetRaw, FullRaw> raw) {
        if (raw.left().isPresent()) {
            FlatSetRaw flat = raw.left().orElseThrow();
            ComponentTransform.Config config =
                    new ComponentTransform.Config(null, false, List.of(), List.of(), flat.set());
            return DataResult.success(new ItemTransforms(List.of(), List.of(config)));
        }

        FullRaw full = raw.right().orElseThrow();
        return DataResult.success(new ItemTransforms(full.enchantments(), full.components()));
    }

    private static boolean isPlainSingleSet(@NotNull ItemTransforms transforms) {
        if (!transforms.enchantments().isEmpty()) return false;
        if (transforms.components().size() != 1) return false;

        ComponentTransform only = transforms.components().getFirst();
        if (!(only instanceof ComponentTransform.Config(
                String source, boolean removeAll,
                List<net.minecraft.core.Holder<net.minecraft.core.component.DataComponentType<?>>> keep,
                List<net.minecraft.core.Holder<net.minecraft.core.component.DataComponentType<?>>> remove,
                DataComponentPatch set
        ))) return false;

        if (source != null) return false;
        if (removeAll) return false;
        if (!keep.isEmpty()) return false;
        if (!remove.isEmpty()) return false;

        return !set.isEmpty();
    }

    private static @NotNull Either<FlatSetRaw, FullRaw> toRaw(@NotNull ItemTransforms transforms) {
        if (isPlainSingleSet(transforms)) {
            ComponentTransform.Config config = (ComponentTransform.Config) transforms.components().getFirst();
            return Either.left(new FlatSetRaw(config.set()));
        }

        return Either.right(new FullRaw(
                transforms.enchantments(),
                transforms.components()
        ));
    }

    private static final int MAX_ENCHANTMENT_TRANSFORMS = 128;
    private static final int MAX_COMPONENT_TRANSFORMS = 128;

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemTransforms> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, EnchantmentTransform.STREAM_CODEC, MAX_ENCHANTMENT_TRANSFORMS),
                    ItemTransforms::enchantments,

                    ByteBufCodecs.collection(ArrayList::new, ComponentTransform.STREAM_CODEC, MAX_COMPONENT_TRANSFORMS),
                    ItemTransforms::components,

                    ItemTransforms::new
            );

    public ItemTransforms(List<EnchantmentTransform> enchantments,
                          List<ComponentTransform> components) {
        this.enchantments = sanitize(enchantments);
        this.components = sanitize(components);
    }

    private static <T> List<T> sanitize(List<T> in) {
        if (in == null || in.isEmpty()) {
            return List.of();
        }

        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) {
            if (t != null) {
                safe.add(t);
            }
        }

        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        ArrayList<RegistryIntrospectionSource> sources = new ArrayList<>(enchantments.size() + components.size());

        sources.addAll(enchantments);
        sources.addAll(components);

        if (sources.isEmpty()) {
            return List.of();
        }

        ArrayList<RegistryIntrospection> merged =
                new ArrayList<>(RegistryIntrospectionSource.mergeByRegistry(sources));

        merged.sort(Comparator.comparing(r -> r.registryKey().location()));

        return List.copyOf(merged);
    }

    @Override
    public @NotNull DataResult<ItemTransforms> validate() {
        for (EnchantmentTransform t : enchantments) {
            DataResult<EnchantmentTransform> result = t.validate();
            var error = result.error();
            if (error.isPresent()) {
                String msg = error.map(DataResult.Error::message).orElse("invalid enchantment transform");
                return SelfValidating.invalid(
                        "invalid '" + JolCraftParameterIds.ENCHANTMENTS + "': " + msg
                );
            }
        }

        for (ComponentTransform t : components) {
            DataResult<ComponentTransform> result = t.validate();
            var error = result.error();
            if (error.isPresent()) {
                String msg = error.map(DataResult.Error::message).orElse("invalid component transform");
                return SelfValidating.invalid(
                        "invalid '" + JolCraftStrings.plural(JolCraftParameterIds.DATA_COMPONENT) + "': " + msg
                );
            }
        }

        return SelfValidating.ok(this);
    }

    public void apply(
            @NotNull WorldContext ctx,
            @Nullable ItemTransformSourceResolver resolver,
            @NotNull ItemStack output
    ) {
        if (output.isEmpty()) {
            return;
        }

        BlockPos anchor = WorldAnchor.resolve(ctx);
        if (anchor == null) {
            return;
        }

        DifficultyInstance difficulty = ctx.level().getCurrentDifficultyAt(anchor);

        for (EnchantmentTransform t : enchantments) {
            t.apply(ctx, output, difficulty);
        }

        for (ComponentTransform t : components) {
            ItemStack input = ItemStack.EMPTY;

            if (resolver != null && t instanceof ComponentTransform.Config config) {
                String source = config.source();
                if (source != null) {
                    input = resolver.resolveItemTransformSource(source);
                }
            }

            if (t.requiresInput() && input.isEmpty()) {
                continue;
            }

            t.apply(input, output);
        }
    }

    public void apply(@NotNull WorldContext ctx, @NotNull ItemStack output) {
        apply(ctx, null, output);
    }

    public boolean requiresInputSource() {
        for (ComponentTransform t : components) {
            if (t.requiresInput()) {
                return true;
            }
        }
        return false;
    }
}