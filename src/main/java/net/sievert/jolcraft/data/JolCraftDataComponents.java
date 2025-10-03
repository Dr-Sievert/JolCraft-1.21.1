package net.sievert.jolcraft.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.item.client.compass.DialItemColor;
import net.sievert.jolcraft.entity.util.dwarf.bounty.BountyData;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.UnaryOperator;

public class JolCraftDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, JolCraft.MOD_ID);


    //Language

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> LORE_KEY =
            register("lore_key", builder -> builder.persistent(Codec.STRING));

    //Reputation

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> REP_OWNER =
            register("rep_owner", builder -> builder.persistent(Codec.STRING));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REP_TIER =
            register("rep_tier", builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REP_ENDORSEMENTS =
            register("rep_endorsements", builder -> builder.persistent(Codec.INT));

    //Bounty

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUNTY_TIER =
            register("bounty_tier", builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> BOUNTY_TYPE =
            register("bounty_type", builder -> builder.persistent(Codec.STRING));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BountyData>> BOUNTY_DATA =
            register("bounty_data", builder -> builder.persistent(BountyData.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUNTY_FILL =
            register("bounty_fill", builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BOUNTY_COMPLETE =
            register("bounty_complete", builder -> builder.persistent(Codec.BOOL));

    //Compass

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> STRUCTURE_GROUP =
            register("structure_group", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DialItemColor>> DIAL_COLOR =
            register("dial_color", builder -> builder
                    .persistent(DialItemColor.CODEC)
                    .networkSynchronized(DialItemColor.STREAM_CODEC)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> DEEPSLATE_COMPASS_TARGET =
            register("deepslate_compass_target", builder -> builder
                    .persistent(GlobalPos.CODEC)
                    .networkSynchronized(GlobalPos.STREAM_CODEC)
            );

    //Brewing

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> HOPS =
            register("hops", builder -> builder.persistent(Codec.STRING));

    //Strongbox

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> LOOT_TABLE =
            register("loot_table", builder -> builder.persistent(Codec.STRING));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> LOOT_SEED =
            register("loot_seed", builder -> builder.persistent(Codec.STRING));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LOCKED =
            register("locked", builder -> builder.persistent(Codec.BOOL));

    //Items

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COIN_POUCH_AMOUNT =
            register("coin_pouch_amount", builder -> builder.persistent(Codec.INT));


    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
