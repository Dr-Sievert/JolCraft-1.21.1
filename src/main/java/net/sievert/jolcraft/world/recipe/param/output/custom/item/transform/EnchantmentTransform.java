package net.sievert.jolcraft.world.recipe.param.output.custom.item.transform;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed interface EnchantmentTransform
        extends SelfValidating<EnchantmentTransform>, RegistryIntrospectionSource
        permits EnchantmentTransform.Direct, EnchantmentTransform.Tagged, EnchantmentTransform.Provider, EnchantmentTransform.Invalid {

    void apply(@NotNull WorldContext ctx,
               @NotNull ItemStack stack,
               @Nullable DifficultyInstance difficulty);

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    default @NotNull List<RegistryIntrospection> introspections() {
        return switch (this) {
            case Direct d -> {
                Holder<Enchantment> h = d.enchantment();
                yield (h != null)
                        ? List.of(RegistryIntrospection.single(Registries.ENCHANTMENT, h))
                        : List.of(RegistryIntrospection.mixed(Registries.ENCHANTMENT, 0, false));
            }
            case Tagged t -> {
                TagKey<Enchantment> tag = t.tag();
                yield (tag != null)
                        ? List.of(RegistryIntrospection.singleTag(Registries.ENCHANTMENT, tag))
                        : List.of(RegistryIntrospection.mixed(Registries.ENCHANTMENT, 0, false));
            }
            case Provider p -> {
                Holder<EnchantmentProvider> h = p.provider();
                yield (h != null)
                        ? List.of(RegistryIntrospection.single(Registries.ENCHANTMENT_PROVIDER, h))
                        : List.of(RegistryIntrospection.mixed(Registries.ENCHANTMENT_PROVIDER, 0, false));
            }
            case Invalid ignored ->
                    List.of(RegistryIntrospection.mixed(Registries.ENCHANTMENT, 0, false));
        };
    }

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    Codec<Holder<Enchantment>> ENCHANTMENT_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.ENCHANTMENT);

    Codec<TagKey<Enchantment>> ENCHANTMENT_TAG_CODEC =
            TagKey.codec(Registries.ENCHANTMENT);

    Codec<Holder<EnchantmentProvider>> PROVIDER_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.ENCHANTMENT_PROVIDER);

    Codec<EnchantmentTransform> RAW_CODEC =
            Codec.either(
                    Direct.CODEC,
                    Codec.either(Tagged.CODEC, Provider.CODEC)
            ).xmap(
                    e -> e.map(d -> d, r -> r.map(t -> t, p -> p)),
                    t -> {
                        if (t instanceof Direct d) return Either.left(d);
                        if (t instanceof Tagged tg) return Either.right(Either.left(tg));
                        return Either.right(Either.right((Provider) t));
                    }
            );

    Codec<EnchantmentTransform> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    byte KIND_DIRECT = 1;
    byte KIND_TAGGED = 2;
    byte KIND_PROVIDER = 3;

    StreamCodec<RegistryFriendlyByteBuf, Holder<Enchantment>> ENCHANTMENT_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT);

    StreamCodec<RegistryFriendlyByteBuf, TagKey<Enchantment>> ENCHANTMENT_TAG_STREAM =
            StreamCodec.of(
                    (buf, tag) -> buf.writeResourceLocation(tag.location()),
                    buf -> TagKey.create(Registries.ENCHANTMENT, buf.readResourceLocation())
            );

    StreamCodec<RegistryFriendlyByteBuf, Holder<EnchantmentProvider>> PROVIDER_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT_PROVIDER);

    StreamCodec<RegistryFriendlyByteBuf, EnchantmentTransform> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        switch (value) {
                            case Direct(Holder<Enchantment> enchantment, IntRange level) -> {
                                buf.writeByte(KIND_DIRECT);
                                ENCHANTMENT_HOLDER_STREAM.encode(buf, enchantment);
                                IntRange.STREAM_CODEC.encode(buf, level);
                            }
                            case Tagged(TagKey<Enchantment> tag, IntRange level) -> {
                                buf.writeByte(KIND_TAGGED);
                                ENCHANTMENT_TAG_STREAM.encode(buf, tag);
                                IntRange.STREAM_CODEC.encode(buf, level);
                            }
                            case Provider(Holder<EnchantmentProvider> provider) -> {
                                buf.writeByte(KIND_PROVIDER);
                                PROVIDER_HOLDER_STREAM.encode(buf, provider);
                            }
                            default -> buf.writeByte(0);
                        }
                    },
                    buf -> {
                        byte kind = buf.readByte();
                        if (kind == KIND_DIRECT) {
                            Holder<Enchantment> h = ENCHANTMENT_HOLDER_STREAM.decode(buf);
                            IntRange lvl = IntRange.STREAM_CODEC.decode(buf);
                            return new Direct(h, lvl);
                        } else if (kind == KIND_TAGGED) {
                            TagKey<Enchantment> tag = ENCHANTMENT_TAG_STREAM.decode(buf);
                            IntRange lvl = IntRange.STREAM_CODEC.decode(buf);
                            return new Tagged(tag, lvl);
                        } else if (kind == KIND_PROVIDER) {
                            Holder<EnchantmentProvider> p = PROVIDER_HOLDER_STREAM.decode(buf);
                            return new Provider(p);
                        }
                        return Invalid.INSTANCE;
                    }
            );

    // ---------------------------------------------------------------------
    // VALIDATION (dispatch)
    // ---------------------------------------------------------------------

    @Override
    default @NotNull DataResult<EnchantmentTransform> validate() {
        return switch (this) {
            case Direct d -> d.validate();
            case Tagged t -> t.validate();
            case Provider p -> p.validate();
            case Invalid i -> i.validate();
        };
    }

    // ---------------------------------------------------------------------
    // VARIANTS
    // ---------------------------------------------------------------------

    record Invalid() implements EnchantmentTransform {
        public static final Invalid INSTANCE = new Invalid();

        @Override
        public void apply(@NotNull WorldContext ctx, @NotNull ItemStack stack, @Nullable DifficultyInstance difficulty) {
            // no-op
        }

        @Override
        public @NotNull DataResult<EnchantmentTransform> validate() {
            return SelfValidating.invalid("invalid enchantment transform");
        }
    }

    record Direct(Holder<Enchantment> enchantment, IntRange level) implements EnchantmentTransform {

        static final Codec<Direct> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        ENCHANTMENT_HOLDER_CODEC.fieldOf(JolCraftParameterIds.ENCHANTMENT)
                                .forGetter(Direct::enchantment),

                        IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.LEVEL, IntRange.ONE)
                                .forGetter(Direct::level)
                ).apply(instance, Direct::new));

        @Override
        public void apply(@NotNull WorldContext ctx, @NotNull ItemStack stack, @Nullable DifficultyInstance difficulty) {
            if (stack.isEmpty()) return;
            if (enchantment == null || level == null) return;

            int rolled = level.roll(ctx.random());
            if (rolled < 1) return;

            EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(enchantment, rolled));
        }

        @Override
        public @NotNull DataResult<EnchantmentTransform> validate() {
            if (enchantment == null) return SelfValidating.invalid(JolCraftParameterIds.ENCHANTMENT + " missing");
            if (level == null) return SelfValidating.invalid(JolCraftParameterIds.LEVEL + " missing");
            if (IntRange.validateRange(level).result().isEmpty()) return SelfValidating.invalid(JolCraftParameterIds.LEVEL + " invalid");
            return SelfValidating.ok(this);
        }
    }

    record Tagged(TagKey<Enchantment> tag, IntRange level) implements EnchantmentTransform {

        static final Codec<Tagged> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        ENCHANTMENT_TAG_CODEC.fieldOf(JolCraftParameterIds.TAG)
                                .forGetter(Tagged::tag),

                        IntRange.CODEC.optionalFieldOf(JolCraftParameterIds.LEVEL, IntRange.ONE)
                                .forGetter(Tagged::level)
                ).apply(instance, Tagged::new));

        @Override
        public void apply(@NotNull WorldContext ctx, @NotNull ItemStack stack, @Nullable DifficultyInstance difficulty) {
            if (stack.isEmpty()) return;
            if (tag == null || level == null) return;

            var lookupOpt = ctx.level().registryAccess().lookup(Registries.ENCHANTMENT);
            if (lookupOpt.isEmpty()) return;

            Holder<Enchantment> chosen = lookupOpt.get()
                    .get(tag)
                    .flatMap(set -> set.getRandomElement(ctx.random()))
                    .orElse(null);
            if (chosen == null) return;

            int rolled = level.roll(ctx.random());
            if (rolled < 1) return;

            EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(chosen, rolled));
        }

        @Override
        public @NotNull DataResult<EnchantmentTransform> validate() {
            if (tag == null) return SelfValidating.invalid(JolCraftParameterIds.TAG + " missing");
            if (level == null) return SelfValidating.invalid(JolCraftParameterIds.LEVEL + " missing");
            if (IntRange.validateRange(level).result().isEmpty()) return SelfValidating.invalid(JolCraftParameterIds.LEVEL + " invalid");
            return SelfValidating.ok(this);
        }
    }

    record Provider(Holder<EnchantmentProvider> provider) implements EnchantmentTransform {

        static final Codec<Provider> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        PROVIDER_HOLDER_CODEC.fieldOf(JolCraftParameterIds.PROVIDER)
                                .forGetter(Provider::provider)
                ).apply(instance, Provider::new));

        @Override
        public void apply(@NotNull WorldContext ctx, @NotNull ItemStack stack, @Nullable DifficultyInstance difficulty) {
            if (stack.isEmpty()) return;
            if (provider == null) return;
            if (difficulty == null) return;

            EnchantmentProvider p = provider.value();
            RandomSource random = ctx.random();

            EnchantmentHelper.updateEnchantments(stack, mutable -> p.enchant(stack, mutable, random, difficulty));
        }

        @Override
        public @NotNull DataResult<EnchantmentTransform> validate() {
            if (provider == null) return SelfValidating.invalid(JolCraftParameterIds.PROVIDER + " missing");
            provider.value();
            return SelfValidating.ok(this);
        }
    }
}