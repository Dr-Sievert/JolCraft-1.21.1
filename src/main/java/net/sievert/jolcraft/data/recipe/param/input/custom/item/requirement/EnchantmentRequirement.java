package net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record EnchantmentRequirement(
        Optional<Holder<Enchantment>> enchantment,
        Optional<TagKey<Enchantment>> enchantmentTag,
        int minLevel
) implements SelfValidating<EnchantmentRequirement>, RegistryIntrospectable {

    private static final String ENCHANTMENT_TAG_KEY =
            JolCraftStrings.underscored(JolCraftParameterIds.ENCHANTMENT, JolCraftParameterIds.TAG);

    private static final Codec<Holder<Enchantment>> ENCHANTMENT_CODEC =
            RegistryFixedCodec.create(Registries.ENCHANTMENT);

    private static final Codec<TagKey<Enchantment>> ENCHANTMENT_TAG_CODEC =
            TagKey.codec(Registries.ENCHANTMENT);

    private record VerboseRaw(
            Optional<Holder<Enchantment>> enchantment,
            Optional<TagKey<Enchantment>> enchantmentTag,
            int minLevel
    ) {}

    private static final Codec<VerboseRaw> VERBOSE_CODEC =
            Codec.either(
                    ENCHANTMENT_CODEC,
                    RecordCodecBuilder.<VerboseRaw>create(instance -> instance.group(
                            ENCHANTMENT_CODEC.optionalFieldOf(JolCraftParameterIds.ENCHANTMENT)
                                    .forGetter(VerboseRaw::enchantment),
                            ENCHANTMENT_TAG_CODEC.optionalFieldOf(ENCHANTMENT_TAG_KEY)
                                    .forGetter(VerboseRaw::enchantmentTag),
                            Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_LEVEL, 1)
                                    .forGetter(VerboseRaw::minLevel)
                    ).apply(instance, VerboseRaw::new))
            ).xmap(
                    either -> either.map(
                            ench -> new VerboseRaw(Optional.of(ench), Optional.empty(), 1),
                            full -> full
                    ),
                    raw -> {
                        if (raw.enchantment().isPresent() && raw.enchantmentTag().isEmpty() && raw.minLevel() == 1) {
                            return Either.left(raw.enchantment().orElseThrow());
                        }
                        return Either.right(raw);
                    }
            );

    public static final Codec<EnchantmentRequirement> CODEC =
            ParamCodecContract.create(VERBOSE_CODEC, EnchantmentRequirement::fromRaw, EnchantmentRequirement::toRaw);

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT))
                                .encode(buf, req.enchantment());

                        Optional<TagKey<Enchantment>> tag = req.enchantmentTag();
                        buf.writeBoolean(tag.isPresent());
                        tag.ifPresent(t -> buf.writeResourceLocation(t.location()));

                        buf.writeVarInt(req.minLevel());
                    },
                    buf -> {
                        Optional<Holder<Enchantment>> ench =
                                ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT))
                                        .decode(buf);

                        boolean hasTag = buf.readBoolean();
                        Optional<TagKey<Enchantment>> tag = hasTag
                                ? Optional.of(TagKey.create(Registries.ENCHANTMENT, buf.readResourceLocation()))
                                : Optional.empty();

                        int minLevel = buf.readVarInt();
                        return new EnchantmentRequirement(ench, tag, minLevel);
                    }
            );

    public EnchantmentRequirement(
            Optional<Holder<Enchantment>> enchantment,
            Optional<TagKey<Enchantment>> enchantmentTag,
            int minLevel
    ) {
        this.enchantment = enchantment == null ? Optional.empty() : enchantment;
        this.enchantmentTag = enchantmentTag == null ? Optional.empty() : enchantmentTag;
        this.minLevel = minLevel;
    }

    private static @NotNull DataResult<EnchantmentRequirement> fromRaw(@NotNull VerboseRaw raw) {
        return DataResult.success(new EnchantmentRequirement(
                raw.enchantment(),
                raw.enchantmentTag(),
                raw.minLevel()
        ));
    }

    private static @NotNull VerboseRaw toRaw(@NotNull EnchantmentRequirement req) {
        return new VerboseRaw(req.enchantment(), req.enchantmentTag(), req.minLevel());
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        boolean hasHolder = enchantment.isPresent();
        boolean hasTag = enchantmentTag.isPresent();

        if (hasHolder && !hasTag) {
            return RegistryIntrospection.single(Registries.ENCHANTMENT, enchantment.orElseThrow());
        }
        if (!hasHolder && hasTag) {
            return RegistryIntrospection.singleTag(Registries.ENCHANTMENT, enchantmentTag.orElseThrow());
        }
        if (!hasHolder) {
            return RegistryIntrospection.mixed(Registries.ENCHANTMENT, 0, false);
        }
        return RegistryIntrospection.mixed(Registries.ENCHANTMENT, 1, true);
    }

    @Override
    public @NotNull DataResult<EnchantmentRequirement> validate() {
        boolean hasEnch = enchantment.isPresent();
        boolean hasTag = enchantmentTag.isPresent();

        if (hasEnch == hasTag) {
            return SelfValidating.invalid(
                    "must specify exactly one of '" + JolCraftParameterIds.ENCHANTMENT + "' or '" + ENCHANTMENT_TAG_KEY + "'"
            );
        }

        if (minLevel < 1) {
            return SelfValidating.invalid("'" + JolCraftParameterIds.MIN_LEVEL + "' must be >= 1");
        }

        return SelfValidating.ok(this);
    }

    public boolean matches(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (minLevel < 1) return false;

        boolean hasEnch = enchantment.isPresent();
        boolean hasTag = enchantmentTag.isPresent();
        if (hasEnch == hasTag) return false;

        if (hasEnch) {
            Holder<Enchantment> ench = enchantment.orElseThrow();
            return stack.getEnchantmentLevel(ench) >= minLevel;
        }

        TagKey<Enchantment> tag = enchantmentTag.orElseThrow();
        for (Holder<Enchantment> ench : stack.getTagEnchantments().keySet()) {
            if (ench == null) continue;
            if (!ench.is(tag)) continue;
            if (stack.getEnchantmentLevel(ench) >= minLevel) {
                return true;
            }
        }

        return false;
    }
}