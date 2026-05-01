package gametest.mixin;

import gametest.util.TestNetworkHelper;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {

    @Inject(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
            at = @At("HEAD")
    )
    private void jolcraft$interceptPacket(
            Packet<?> packet,
            @Nullable PacketSendListener listener,
            CallbackInfo ci
    ) {
        if ((Object) this instanceof ServerGamePacketListenerImpl gamePacketListener) {
            ServerPlayer player = ((ServerGamePacketListenerImplAccessor) gamePacketListener).jolcraft$getPlayer();
            TestNetworkHelper.onPacket(player, packet);
        }
    }
}