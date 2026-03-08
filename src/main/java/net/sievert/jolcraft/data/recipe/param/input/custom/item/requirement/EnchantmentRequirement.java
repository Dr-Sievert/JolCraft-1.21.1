package net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement;

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
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Atomic item requirement: item must have an enchantment matching either:
 * - specific enchantment holder, or
 * - enchantment tag
 *
 * with a minimum level.
 *
 * JSON:
 * { "enchantment": "minecraft:sharpness", "min_level": 1 }
 * { "enchantment_tag": "minecraft:weapon_enchantable", "min_level": 1 }
 *
 * Exactly one of enchantment/enchantment_tag must be present.
 *
 * - No throws in JolCraft logic (ctor/stream decode/runtime). (Buffer IO may throw.)
 * - Invalid state representable; matches(...) is total and deterministic false on invalid.
 */
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

    private static final Codec<EnchantmentRequirement> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ENCHANTMENT_CODEC.optionalFieldOf(JolCraftParameterIds.ENCHANTMENT)
                            .forGetter(EnchantmentRequirement::enchantment),

                    ENCHANTMENT_TAG_CODEC.optionalFieldOf(ENCHANTMENT_TAG_KEY)
                            .forGetter(EnchantmentRequirement::enchantmentTag),

                    Codec.INT.optionalFieldOf(JolCraftParameterIds.MIN_LEVEL, 1)
                            .forGetter(EnchantmentRequirement::minLevel)
            ).apply(instance, EnchantmentRequirement::new));

    public static final Codec<EnchantmentRequirement> CODEC = ParamCodecs.validated(RAW_CODEC);

    /**
     * Stream stores:
     * - Optional enchantment holder
     * - Optional tag id (ResourceLocation)
     * - min level
     *
     * No validate() calls here (no allocations).
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        Optional<Holder<Enchantment>> ench = req.enchantment == null ? Optional.empty() : req.enchantment;
                        ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT))
                                .encode(buf, ench);

                        Optional<TagKey<Enchantment>> tag = req.enchantmentTag == null ? Optional.empty() : req.enchantmentTag;
                        buf.writeBoolean(tag.isPresent());
                        tag.ifPresent(enchantmentTagKey -> buf.writeResourceLocation(enchantmentTagKey.location()));

                        buf.writeVarInt(req.minLevel);
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

    /**
     * Canonical ctor: no throws. Null optionals are normalized.
     */
    public EnchantmentRequirement(
            Optional<Holder<Enchantment>> enchantment,
            Optional<TagKey<Enchantment>> enchantmentTag,
            int minLevel
    ) {
        this.enchantment = enchantment == null ? Optional.empty() : enchantment;
        this.enchantmentTag = enchantmentTag == null ? Optional.empty() : enchantmentTag;
        this.minLevel = minLevel;
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        boolean hasHolder = enchantment != null && enchantment.isPresent();
        boolean hasTag = enchantmentTag != null && enchantmentTag.isPresent();

        if (hasHolder == hasTag) {
            return RegistryIntrospection.mixed(Registries.ENCHANTMENT, 0, hasTag);
        }

        if (hasTag) {
            return RegistryIntrospection.singleTag(Registries.ENCHANTMENT, enchantmentTag.get());
        }

        return RegistryIntrospection.single(Registries.ENCHANTMENT, enchantment.get());
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

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (minLevel < 1) {
            return false;
        }

        boolean hasEnch = enchantment.isPresent();
        boolean hasTag = enchantmentTag.isPresent();
        if (hasEnch == hasTag) {
            return false;
        }

        if (hasEnch) {
            Holder<Enchantment> ench = enchantment.get();
            int lvl = stack.getEnchantmentLevel(ench);
            return lvl >= minLevel;
        }

        TagKey<Enchantment> tag = enchantmentTag.get();

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