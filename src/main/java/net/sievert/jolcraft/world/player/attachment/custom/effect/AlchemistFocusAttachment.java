package net.sievert.jolcraft.world.player.attachment.custom.effect;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftPersistentAttachment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AlchemistFocusAttachment
        extends JolCraftPersistentAttachment<AlchemistFocusAttachment> {

    private static final String TAG_EFFECTS =
            JolCraftStrings.plural(JolCraftDictionary.EFFECT);

    public static final Codec<AlchemistFocusAttachment> CODEC =
            ResourceLocation.CODEC.listOf()
                    .fieldOf(TAG_EFFECTS)
                    .xmap(
                            ids -> new AlchemistFocusAttachment(Set.copyOf(ids)),
                            attachment -> List.copyOf(attachment.boostedEffects)
                    )
                    .codec();

    private final Set<ResourceLocation> boostedEffects;

    public AlchemistFocusAttachment() {
        this(Set.of());
    }

    private AlchemistFocusAttachment(Set<ResourceLocation> boostedEffects) {
        this.boostedEffects = Set.copyOf(boostedEffects);
    }

    public boolean hasBoostedEffect(ResourceLocation effectId) {
        return effectId != null && boostedEffects.contains(effectId);
    }

    public AlchemistFocusAttachment withBoostedEffect(ResourceLocation effectId) {
        if (effectId == null || boostedEffects.contains(effectId)) {
            return this;
        }

        Set<ResourceLocation> updated = new HashSet<>(boostedEffects);
        updated.add(effectId);

        return new AlchemistFocusAttachment(updated);
    }

    public AlchemistFocusAttachment withoutBoostedEffect(ResourceLocation effectId) {
        if (effectId == null || !boostedEffects.contains(effectId)) {
            return this;
        }

        Set<ResourceLocation> updated = new HashSet<>(boostedEffects);
        updated.remove(effectId);

        return new AlchemistFocusAttachment(updated);
    }

    @Override
    public Codec<AlchemistFocusAttachment> codec() {
        return CODEC;
    }
}