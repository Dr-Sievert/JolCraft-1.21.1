package net.sievert.jolcraft.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.component.custom.BountyData;
import net.sievert.jolcraft.world.item.component.custom.RewardCrateSource;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.data.id.data_component.JolCraftDataComponentIds;

import java.util.function.UnaryOperator;

public final class JolCraftDataComponents {

    private JolCraftDataComponents() {}

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, JolCraft.MOD_ID);

    // -----------------
    // Language
    // -----------------

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> DWARF_LORE_KEY =
            register(JolCraftDataComponentIds.DWARF_LORE_KEY, builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    // -----------------
    // Reputation
    // -----------------

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> REPUTATION_OWNER =
            register(JolCraftDataComponentIds.REPUTATION_OWNER, builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REPUTATION_TIER =
            register(JolCraftDataComponentIds.REPUTATION_TIER, builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REPUTATION_ENDORSEMENTS =
            register(JolCraftDataComponentIds.REPUTATION_ENDORSEMENTS, builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    // -----------------
    // Bounty
    // -----------------

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUNTY_TIER =
            register(JolCraftDataComponentIds.BOUNTY_TIER, builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> BOUNTY_TYPE =
            register(JolCraftDataComponentIds.BOUNTY_TYPE, builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BountyData>> BOUNTY_DATA =
            register(JolCraftDataComponentIds.BOUNTY_DATA, builder -> builder
                    .persistent(BountyData.CODEC)
                    .networkSynchronized(BountyData.STREAM_CODEC)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUNTY_FILL =
            register(JolCraftDataComponentIds.BOUNTY_FILL, builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BOUNTY_COMPLETE =
            register(JolCraftDataComponentIds.BOUNTY_COMPLETE, builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RewardCrateSource>> REWARD_CRATE_SOURCE =
            register(JolCraftDataComponentIds.REWARD_CRATE_SOURCE, builder -> builder
                    .persistent(RewardCrateSource.CODEC)
                    .networkSynchronized(RewardCrateSource.STREAM_CODEC)
            );

    // -----------------
    // Compass
    // -----------------

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> STRUCTURE_GROUP =
            register(JolCraftDataComponentIds.STRUCTURE_GROUP, builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DeepslateCompassDialColor>> DEEPSLATE_COMPASS_DIAL_COLOR =
            register(JolCraftDataComponentIds.DEEPSLATE_COMPASS_DIAL_COLOR, builder -> builder
                    .persistent(DeepslateCompassDialColor.CODEC)
                    .networkSynchronized(DeepslateCompassDialColor.STREAM_CODEC)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> DEEPSLATE_COMPASS_TARGET =
            register(JolCraftDataComponentIds.DEEPSLATE_COMPASS_TARGET, builder -> builder
                    .persistent(GlobalPos.CODEC)
                    .networkSynchronized(GlobalPos.STREAM_CODEC)
            );

    // -----------------
    // Strongbox
    // -----------------

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<LootTable>>> LOOT_TABLE =
            register(JolCraftDataComponentIds.LOOT_TABLE, builder -> builder
                    .persistent(ResourceKey.codec(Registries.LOOT_TABLE))
                    .networkSynchronized(ResourceKey.streamCodec(Registries.LOOT_TABLE))
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LOOT_SEED =
            register(JolCraftDataComponentIds.LOOT_SEED, builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LOCKED =
            register(JolCraftDataComponentIds.LOCKED, builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
            );

    // -----------------
    // Items
    // -----------------

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COIN_POUCH_AMOUNT =
            register(JolCraftDataComponentIds.COIN_POUCH_AMOUNT, builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    // -----------------
    // Brewing
    // -----------------

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT =
            register(
                    JolCraftDataComponentIds.FLUID_CONTENT, builder -> builder
                            .persistent(SimpleFluidContent.CODEC)
                            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BREW_COLOR =
            register(JolCraftDataComponentIds.BREW_COLOR, builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> BREW_AGE =
            register(JolCraftDataComponentIds.BREW_AGE, builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
            );

    // -----------------
    // Register helper
    // -----------------

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String id,
            UnaryOperator<DataComponentType.Builder<T>> builderOperator
    ) {
        return DATA_COMPONENT_TYPES.register(id, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} data component types",
                DATA_COMPONENT_TYPES.getEntries().size()
        );
    }
}