package net.sievert.jolcraft.world.entity.attachment.player.custom.lore;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.world.entity.attachment.base.JolCraftSyncedAttachment;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class DwarfLoreAttachment extends JolCraftSyncedAttachment<DwarfLoreAttachment> {

    private static final String TAG_UNLOCKS = JolCraftStrings.plural(JolCraftDictionary.UNLOCK);

    public static final Codec<DwarfLoreAttachment> CODEC =
            Codec.STRING.listOf()
                    .fieldOf(TAG_UNLOCKS)
                    .xmap(DwarfLoreAttachment::fromStrings, DwarfLoreAttachment::unlockStrings)
                    .codec();

    public static final StreamCodec<? super RegistryFriendlyByteBuf, DwarfLoreAttachment> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8
                    .apply(ByteBufCodecs.list())
                    .map(DwarfLoreAttachment::fromStrings, DwarfLoreAttachment::unlockStrings);

    private final Set<DwarfLoreKey> unlocks;

    public DwarfLoreAttachment() {
        this(Set.of());
    }

    public DwarfLoreAttachment(Set<DwarfLoreKey> unlocks) {
        this.unlocks = Set.copyOf(unlocks);
    }

    public Set<DwarfLoreKey> getUnlocks() {
        return unlocks;
    }

    public boolean hasUnlock(DwarfLoreKey key) {
        return key != null && unlocks.contains(key);
    }

    public DwarfLoreAttachment withUnlock(DwarfLoreKey key) {
        if (key == null || unlocks.contains(key)) {
            return this;
        }

        EnumSet<DwarfLoreKey> updated = unlocks.isEmpty()
                ? EnumSet.of(key)
                : EnumSet.copyOf(unlocks);

        updated.add(key);
        return new DwarfLoreAttachment(updated);
    }

    private List<String> unlockStrings() {
        return unlocks.stream()
                .map(key -> key.name().toLowerCase(Locale.ROOT))
                .toList();
    }

    private static DwarfLoreAttachment fromStrings(List<String> list) {
        EnumSet<DwarfLoreKey> set = EnumSet.noneOf(DwarfLoreKey.class);

        for (String s : list) {
            if (s == null || s.isBlank()) {
                continue;
            }

            try {
                set.add(DwarfLoreKey.valueOf(s.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new DwarfLoreAttachment(set);
    }

    @Override
    public Codec<DwarfLoreAttachment> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, DwarfLoreAttachment> streamCodec() {
        return STREAM_CODEC;
    }
}