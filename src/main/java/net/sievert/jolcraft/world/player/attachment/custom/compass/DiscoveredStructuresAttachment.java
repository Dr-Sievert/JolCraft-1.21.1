package net.sievert.jolcraft.world.player.attachment.custom.compass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftSyncedAttachment;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DiscoveredStructuresAttachment extends JolCraftSyncedAttachment<DiscoveredStructuresAttachment> {

    public static final Codec<DiscoveredStructuresAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    GlobalPos.CODEC.listOf()
                            .fieldOf(JolCraftDictionary.DISCOVERED)
                            .forGetter(DiscoveredStructuresAttachment::discoveredList),
                    Codec.INT
                            .fieldOf(JolCraftDictionary.SCORE)
                            .forGetter(DiscoveredStructuresAttachment::getScore)
            ).apply(instance, DiscoveredStructuresAttachment::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DiscoveredStructuresAttachment> STREAM_CODEC =
            StreamCodec.composite(
                    GlobalPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DiscoveredStructuresAttachment::discoveredList,
                    ByteBufCodecs.INT,
                    DiscoveredStructuresAttachment::getScore,
                    DiscoveredStructuresAttachment::new
            );

    private final Set<GlobalPos> discovered;
    private final int discoveryScore;

    public DiscoveredStructuresAttachment() {
        this(Set.of(), 0);
    }

    public DiscoveredStructuresAttachment(@NotNull List<GlobalPos> discovered, int discoveryScore) {
        this(new HashSet<>(discovered), discoveryScore);
    }

    public DiscoveredStructuresAttachment(@NotNull Set<GlobalPos> discovered, int discoveryScore) {
        this.discovered = Set.copyOf(discovered);
        this.discoveryScore = discoveryScore;
    }

    public boolean isDiscovered(GlobalPos pos) {
        return pos != null && discovered.contains(pos);
    }

    public @NotNull Set<GlobalPos> getDiscovered() {
        return discovered;
    }

    public int getScore() {
        return discoveryScore;
    }

    public @NotNull DiscoveredStructuresAttachment withDiscovered(GlobalPos pos) {
        if (pos == null || discovered.contains(pos)) {
            return this;
        }

        Set<GlobalPos> updated = new HashSet<>(discovered);
        updated.add(pos);
        return new DiscoveredStructuresAttachment(updated, discoveryScore);
    }

    public @NotNull DiscoveredStructuresAttachment withAddedScore(int amount) {
        if (amount == 0) {
            return this;
        }
        return new DiscoveredStructuresAttachment(discovered, discoveryScore + amount);
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