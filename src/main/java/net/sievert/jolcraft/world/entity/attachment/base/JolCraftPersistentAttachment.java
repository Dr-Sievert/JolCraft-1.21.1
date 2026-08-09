package net.sievert.jolcraft.world.entity.attachment.base;

import com.mojang.serialization.Codec;

public abstract class JolCraftPersistentAttachment<TSelf> {

    public abstract Codec<TSelf> codec();
}