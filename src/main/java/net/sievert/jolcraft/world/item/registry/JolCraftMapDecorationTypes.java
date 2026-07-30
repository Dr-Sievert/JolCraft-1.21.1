package net.sievert.jolcraft.world.item.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.tag.JolCraftTagIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;

public final class JolCraftMapDecorationTypes {

    private JolCraftMapDecorationTypes() {}

    public static final DeferredRegister<MapDecorationType> MAP_DECORATION_TYPES =
            DeferredRegister.create(Registries.MAP_DECORATION_TYPE, JolCraft.MOD_ID);

    public static final DeferredHolder<MapDecorationType, MapDecorationType> DWARVEN =
            MAP_DECORATION_TYPES.register(
                    JolCraftTagIds.DWARVEN,
                    () -> new MapDecorationType(
                            JolCraft.location(JolCraftTagIds.DWARVEN),
                            true,
                            MapColor.COLOR_GRAY.col,
                            true,
                            false
                    )
            );

    public static void register(IEventBus eventBus) {
        MAP_DECORATION_TYPES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} map decoration types",
                MAP_DECORATION_TYPES.getEntries().size()
        );
    }
}