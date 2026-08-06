package net.sievert.jolcraft.world.entity.effect.cure;

import net.neoforged.neoforge.common.EffectCure;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;

public final class JolCraftEffectCures {

    private JolCraftEffectCures() {}

    public static final EffectCure WAR_HORN = EffectCure.get(JolCraft.location(JolCraftItemIds.WAR_HORN).toString());
}