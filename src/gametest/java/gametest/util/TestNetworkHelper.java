package gametest.util;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public final class TestNetworkHelper {

    public static BiConsumer<ServerPlayer, Packet<?>> PACKET_LISTENER = null;

    public static void onPacket(ServerPlayer player, Packet<?> packet) {
        if (PACKET_LISTENER != null) {
            PACKET_LISTENER.accept(player, packet);
        }
    }

    /**
     * Global registry of all packet listeners for the current JVM (shared across tests).
     */
    private static final Set<TestPacketListener> PACKET_LISTENERS = new LinkedHashSet<>();

    /**
     * Whether the packet hook has been registered.
     */
    private static boolean hooksRegistered = false;

    /**
     * Per-helper record of all packets sent to each tracked player UUID.
     */
    private final Map<UUID, List<Packet<?>>> sentPackets = new ConcurrentHashMap<>();

    /**
     * Listener used by this helper instance to record outgoing packets.
     */
    private final TestPacketListener recordingListener = this::recordSentPacket;

    /**
     * Registers the global packet hook once.
     */
    private static synchronized void registerTestHooks() {
        if (hooksRegistered) {
            return;
        }

        PACKET_LISTENER = TestNetworkHelper::notifyPacketListeners;
        hooksRegistered = true;
    }

    /**
     * Registers a packet listener.
     */
    public static void registerPacketListener(TestPacketListener listener) {
        synchronized (PACKET_LISTENERS) {
            PACKET_LISTENERS.add(listener);
        }
    }

    /**
     * Unregisters a packet listener.
     */
    public static void unregisterPacketListener(TestPacketListener listener) {
        synchronized (PACKET_LISTENERS) {
            PACKET_LISTENERS.remove(listener);
        }
    }

    /**
     * Enables packet recording for this helper instance.
     */
    public void enablePacketRecording() {
        registerTestHooks();
        registerPacketListener(this.recordingListener);
    }

    /**
     * Clears all recorded packets for this helper instance.
     */
    public void clearAllPackets() {
        this.sentPackets.clear();
    }

    /**
     * Disables packet recording for this helper instance.
     */
    public void disablePacketRecording() {
        unregisterPacketListener(this.recordingListener);
        clearAllPackets();
    }

    /**
     * Records a packet sent to a player.
     */
    private void recordSentPacket(ServerPlayer player, Packet<?> packet) {
        this.sentPackets
                .computeIfAbsent(player.getUUID(), ignored -> new CopyOnWriteArrayList<>())
                .add(packet);
    }

    /**
     * Returns all packets recorded for the given player.
     */
    public List<Packet<?>> getSentPackets(ServerPlayer player) {
        return List.copyOf(this.sentPackets.getOrDefault(player.getUUID(), List.of()));
    }

    public <T extends Packet<?>> List<T> getSentPackets(ServerPlayer player, Class<T> packetType) {
        return getSentPackets(player).stream()
                .filter(packetType::isInstance)
                .map(packetType::cast)
                .toList();
    }

    /**
     * Called by the mixin hook for every outgoing packet.
     */
    public static void notifyPacketListeners(ServerPlayer player, Packet<?> packet) {
        List<TestPacketListener> listenersSnapshot;
        synchronized (PACKET_LISTENERS) {
            listenersSnapshot = List.copyOf(PACKET_LISTENERS);
        }

        for (TestPacketListener listener : listenersSnapshot) {
            try {
                listener.onSend(player, packet);
            } catch (Exception exception) {
                System.err.println("[gametest.util.network.TestNetworkHelper] Exception in packet listener: " + exception);
                exception.printStackTrace(System.err);
            }
        }
    }

    @FunctionalInterface
    public interface TestPacketListener {
        void onSend(ServerPlayer player, Packet<?> packet);
    }
}