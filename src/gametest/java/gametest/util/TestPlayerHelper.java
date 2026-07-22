package gametest.util;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public final class TestPlayerHelper {

    private TestPlayerHelper() {}

    public static ServerPlayer createPlayer(ServerLevel level, String name, @Nullable BlockPos spawnPos) {
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), name), false);

        ServerPlayer player = new ServerPlayer(
                level.getServer(),
                level,
                cookie.gameProfile(),
                cookie.clientInformation()
        ) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);

        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);

        if (spawnPos != null) {
            player.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        }

        return player;
    }

    public static ServerPlayer createPlayer(ServerLevel level, String name) {
        return createPlayer(level, name, null);
    }

    public static ServerPlayer createPlayer(ServerLevel level) {
        return createPlayer(level, "test_player", null);
    }

    @Nullable
    public static ServerPlayer getOnlinePlayer(ServerLevel level, UUID playerUUID) {
        return level.getServer().getPlayerList().getPlayer(playerUUID);
    }

    public static void disconnect(ServerPlayer player) {
        player.connection.disconnect(Component.translatable("multiplayer.disconnect.generic"));
    }

    public static ServerPlayer reconnect(ServerLevel level, ServerPlayer oldPlayer) {
        BlockPos oldPos = oldPlayer.blockPosition();
        GameProfile profile = oldPlayer.getGameProfile();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);

        ServerPlayer newPlayer = new ServerPlayer(
                level.getServer(),
                level,
                cookie.gameProfile(),
                cookie.clientInformation()
        ) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);

        level.getServer().getPlayerList().placeNewPlayer(connection, newPlayer, cookie);
        newPlayer.setPos(oldPos.getX() + 0.5D, oldPos.getY(), oldPos.getZ() + 0.5D);

        return newPlayer;
    }

    public static void assertPlayerPresent(TestLoggingHelper log, ServerLevel level, UUID playerUUID) {
        ServerPlayer player = getOnlinePlayer(level, playerUUID);

        log.check(() -> {
            boolean present = !level.getPlayers(found -> found.getUUID().equals(playerUUID)).isEmpty();
            if (!present) {
                throw new AssertionError("Expected player to be present in the level, but was not found.");
            }
        }, (player != null ? "[%s]".formatted(player.getName().getString()) : "[%s]".formatted(playerUUID)) + " is present in the level");
    }

    public static void assertPlayerNotPresent(TestLoggingHelper log, ServerLevel level, UUID playerUUID) {
        ServerPlayer player = getOnlinePlayer(level, playerUUID);

        log.check(() -> {
            boolean present = !level.getPlayers(found -> found.getUUID().equals(playerUUID)).isEmpty();
            if (present) {
                throw new AssertionError("Expected player to NOT be present in the level, but they are.");
            }
        }, (player != null ? "[%s]".formatted(player.getName().getString()) : "[%s]".formatted(playerUUID)) + " is not present in the level");
    }
}