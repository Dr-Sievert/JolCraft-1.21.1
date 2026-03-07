package net.sievert.jolcraft.data.recipe.param.output.custom.item.transform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
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

    private static final Codec<ItemTransforms> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EnchantmentTransform.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.ENCHANTMENTS, List.of())
                            .forGetter(ItemTransforms::enchantments),

                    ComponentTransform.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.COMPONENTS, List.of())
                            .forGetter(ItemTransforms::components)
            ).apply(instance, ItemTransforms::new));

    public static final Codec<ItemTransforms> CODEC = ParamCodecs.validated(RAW_CODEC);

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
                        "invalid '" + JolCraftParameterIds.COMPONENTS + "': " + msg
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

        DifficultyInstance difficulty =
                ctx.level().getCurrentDifficultyAt(ctx.player().blockPosition());

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