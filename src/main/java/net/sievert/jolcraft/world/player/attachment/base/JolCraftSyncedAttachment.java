package net.sievert.jolcraft.world.player.attachment.base;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class JolCraftSyncedAttachment<TSelf> extends JolCraftPersistentAttachment<TSelf> {

    public abstract StreamCodec<? super RegistryFriendlyByteBuf, TSelf> streamCodec();
}