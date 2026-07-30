package net.sievert.jolcraft.world.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.loot.JolCraftLootModifierIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.loot.custom.AddItemModifier;
import net.sievert.jolcraft.world.loot.custom.AddLootTableModifier;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class JolCraftLootModifiers {

    private JolCraftLootModifiers() {}

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, JolCraft.MOD_ID);

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIER_SERIALIZERS.register(
                    JolCraftLootModifierIds.ADD_ITEM,
                    () -> AddItemModifier.CODEC
            );

    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_LOOT_TABLE =
            LOOT_MODIFIER_SERIALIZERS.register(
                    JolCraftLootModifierIds.ADD_LOOT_TABLE,
                    () -> AddLootTableModifier.CODEC
            );

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} loot modifiers",
                LOOT_MODIFIER_SERIALIZERS.getEntries().size()
        );
    }
}