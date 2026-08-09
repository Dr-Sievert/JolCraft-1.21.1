package net.sievert.jolcraft.world.entity.attachment.player.custom.compass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.entity.attachment.base.JolCraftSyncedAttachment;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DiscoveredStructuresAttachment extends JolCraftSyncedAttachment<DiscoveredStructuresAttachment> {

    public static final Codec<DiscoveredStructuresAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    GlobalPos.CODEC.listOf()
                            .fieldOf(JolCraftDictionary.DISCOVERED)
                            .forGetter(DiscoveredStructuresAttachment::discoveredList)
            ).apply(instance, DiscoveredStructuresAttachment::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DiscoveredStructuresAttachment> STREAM_CODEC =
            StreamCodec.composite(
                    GlobalPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DiscoveredStructuresAttachment::discoveredList,
                    DiscoveredStructuresAttachment::new
            );

    private final Set<GlobalPos> discovered;

    public DiscoveredStructuresAttachment() {
        this(Set.of());
    }

    public DiscoveredStructuresAttachment(@NotNull List<GlobalPos> discovered) {
        this(new HashSet<>(discovered));
    }

    public DiscoveredStructuresAttachment(@NotNull Set<GlobalPos> discovered) {
        this.discovered = Set.copyOf(discovered);
    }

    public boolean isDiscovered(GlobalPos pos) {
        return pos != null && discovered.contains(pos);
    }

    public @NotNull Set<GlobalPos> getDiscovered() {
        return discovered;
    }

    public @NotNull DiscoveredStructuresAttachment withDiscovered(GlobalPos pos) {
        if (pos == null || discovered.contains(pos)) {
            return this;
        }

        Set<GlobalPos> updated = new HashSet<>(discovered);
        updated.add(pos);
        return new DiscoveredStructuresAttachment(updated);
    }

    private List<GlobalPos> discoveredList() {
        return List.copyOf(discovered);
    }

    @Override
    public Codec<DiscoveredStructuresAttachment> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DiscoveredStructuresAttachment> streamCodec() {
        return STREAM_CODEC;
    }
}
