package gametest.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplAccessor {

    @Accessor(JolCraftParameterIds.PLAYER)
    ServerPlayer jolcraft$getPlayer();
}