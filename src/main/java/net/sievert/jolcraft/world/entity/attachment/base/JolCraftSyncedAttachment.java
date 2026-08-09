package net.sievert.jolcraft.world.entity.attachment.base;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class JolCraftSyncedAttachment<TSelf> extends JolCraftPersistentAttachment<TSelf> {

    public abstract StreamCodec<? super RegistryFriendlyByteBuf, TSelf> streamCodec();
}