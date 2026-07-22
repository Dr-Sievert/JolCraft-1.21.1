package net.sievert.jolcraft.world.player.attachment.base;

import com.mojang.serialization.Codec;

public abstract class JolCraftPersistentAttachment<TSelf> {

    public abstract Codec<TSelf> codec();
}