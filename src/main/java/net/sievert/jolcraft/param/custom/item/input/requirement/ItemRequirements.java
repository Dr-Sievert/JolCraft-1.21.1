package net.sievert.jolcraft.param.custom.item.input.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamMatching;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemRequirements(
        List<EnchantmentRequirement> enchantments,
        Optional<DataComponentRequirement> componentRequirement
) implements ParamData<ItemRequirements>, ParamMatching<ItemStack> {

    private static final String COMPONENTS_KEY =
            JolCraftStrings.plural(JolCraftParameterIds.DATA_COMPONENT);

    public static final ItemRequirements EMPTY = new ItemRequirements(List.of(), Optional.empty());

    private static final Codec<ItemRequirements> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    EnchantmentRequirement.MAP_CODEC
                            .optionalFieldOf(JolCraftParameterIds.ENCHANTMENTS, List.of())
                            .forGetter(ItemRequirements::enchantments),
                    DataComponentRequirement.CODEC
                            .optionalFieldOf(COMPONENTS_KEY)
                            .forGetter(ItemRequirements::componentRequirement)
            ).apply(inst, ItemRequirements::new));

    public static final Codec<ItemRequirements> CODEC =
            ParamCodecs.validated(RAW_CODEC, ItemRequirements::validate);

    private static final StreamCodec<RegistryFriendlyByteBuf, List<EnchantmentRequirement>> ENCHANTMENTS_STREAM =
            ByteBufCodecs.collection(ArrayList::new, EnchantmentRequirement.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemRequirements> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    ENCHANTMENTS_STREAM,
                    ItemRequirements::enchantments,
                    ByteBufCodecs.optional(DataComponentRequirement.STREAM_CODEC),
                    ItemRequirements::componentRequirement,
                    ItemRequirements::new
            ), ItemRequirements::validate);

    public ItemRequirements {
        enchantments = ParamValidations.sanitizeList(enchantments);
        componentRequirement = componentRequirement == null ? Optional.empty() : componentRequirement;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (isEmpty()) return true;
        if (stack == null || stack.isEmpty()) return false;

        return ParamMatching.allMatch(enchantments, stack)
                && componentRequirement.map(r -> r.matches(stack)).orElse(true);
    }

    public boolean isEmpty() {
        return enchantments.isEmpty() && componentRequirement.isEmpty();
    }

    @Override
    public DataResult<ItemRequirements> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.children(this, enchantments, JolCraftParameterIds.ENCHANTMENTS),
                () -> componentRequirement
                        .map(requirement -> ParamValidations.child(this, requirement, COMPONENTS_KEY))
                        .orElseGet(() -> ParamValidations.ok(this))
        );
    }

    @Override
    public Codec<ItemRequirements> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemRequirements> streamCodec() {
        return STREAM_CODEC;
    }
}