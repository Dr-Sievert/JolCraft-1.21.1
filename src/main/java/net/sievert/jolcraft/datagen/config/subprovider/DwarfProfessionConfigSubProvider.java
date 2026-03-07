package net.sievert.jolcraft.datagen.config.subprovider;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.datagen.config.ConfigCodecWriter;
import net.sievert.jolcraft.datagen.config.ConfigSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameParameterValue")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfProfessionConfigSubProvider implements ConfigSubProvider {

    private static final String DIRECTORY = DwarfProfessionConfigManager.DIRECTORY;
    private static final Codec<DwarfProfessionConfig> CODEC = DwarfProfessionConfig.CODEC;

    @Override
    public CompletableFuture<?> run(CachedOutput cache, PackOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (DwarfProfession prof : DwarfProfession.values()) {
            if (prof == DwarfProfession.NONE) continue;

            DwarfProfessionConfig cfg = configFor(prof);
            ResourceLocation fileId = JolCraft.location(prof.getId());

            futures.add(ConfigCodecWriter.write(
                    cache,
                    output,
                    DIRECTORY,
                    fileId,
                    CODEC,
                    cfg
            ));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String name() {
        return "Dwarf profession configs";
    }

    private static DwarfProfessionConfig configFor(DwarfProfession prof) {
        DwarfProfessionConfig d = DwarfProfessionConfig.DEFAULTS;

        return switch (prof) {

            case GUILDMASTER -> new DwarfProfessionConfig(
                    d.requiredTier(),
                    d.restockTicks(),
                    0.8F,
                    false,
                    true,
                    false,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_CARTOGRAPHER),
                    d.attributes(),
                    merchantTradePools()
            );

            case ALCHEMIST -> new DwarfProfessionConfig(
                    3,
                    d.restockTicks(),
                    1.1F,
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_CLERIC),
                    d.attributes(),
                    d.tradePools()
            );

            case ARCANIST -> new DwarfProfessionConfig(
                    3,
                    d.restockTicks(),
                    0.85F,
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_LIBRARIAN),
                    d.attributes(),
                    d.tradePools()
            );

            case ARTISAN -> new DwarfProfessionConfig(
                    2,
                    d.restockTicks(),
                    0.9F,
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_TOOLSMITH),
                    d.attributes(),
                    d.tradePools()
            );

            case BREWMASTER -> new DwarfProfessionConfig(
                    1,
                    d.restockTicks(),
                    0.9F,
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_CLERIC),
                    d.attributes(),
                    d.tradePools()
            );

            case EXPLORER -> new DwarfProfessionConfig(
                    2,
                    d.restockTicks(),
                    d.voicePitch(),
                    true,
                    d.canEndorse(),
                    false,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_CARTOGRAPHER),
                    d.attributes(),
                    d.tradePools()
            );

            case GUARD -> new DwarfProfessionConfig(
                    1,
                    d.restockTicks(),
                    0.7F,
                    false,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    new DwarfProfessionConfig.Rules(
                            d.rules().canSign(),
                            d.rules().canEndorse(),
                            DwarfProfessionConfig.Rule.minMerchantLevel(5)
                    ),
                    soundsBoth(SoundEvents.VILLAGER_WORK_WEAPONSMITH),
                    d.attributes(),
                    d.tradePools()
            );

            case HISTORIAN -> new DwarfProfessionConfig(
                    d.requiredTier(),
                    d.restockTicks(),
                    1.1F,
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_LIBRARIAN),
                    d.attributes(),
                    d.tradePools()
            );

            case KEEPER -> new DwarfProfessionConfig(
                    1,
                    d.restockTicks(),
                    d.voicePitch(),
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_FARMER),
                    d.attributes(),
                    d.tradePools()
            );

            case MERCHANT -> new DwarfProfessionConfig(
                    d.requiredTier(),
                    d.restockTicks(),
                    d.voicePitch(),
                    false,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_FISHERMAN),
                    d.attributes(),
                    merchantTradePools()
            );

            case MINER -> new DwarfProfessionConfig(
                    2,
                    d.restockTicks(),
                    1.1F,
                    false,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_MASON),
                    d.attributes(),
                    d.tradePools()
            );

            case PRIEST -> new DwarfProfessionConfig(
                    3,
                    d.restockTicks(),
                    0.9F,
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_LIBRARIAN),
                    d.attributes(),
                    d.tradePools()
            );

            case SCRAPPER -> new DwarfProfessionConfig(
                    d.requiredTier(),
                    d.restockTicks(),
                    1.4F,
                    true,
                    d.canEndorse(),
                    true,
                    d.showLevel(),
                    d.rules(),
                    soundsBoth(SoundEvents.VILLAGER_WORK_TOOLSMITH),
                    d.attributes(),
                    scrapperTradePools()
            );

            default -> d;
        };
    }

    private static DwarfProfessionConfig.TradePools merchantTradePools() {
        return tradePools(
                pool(
                        DwarfProfessionConfig.PoolType.CUMULATIVE,
                        Map.of(
                                DwarfMerchantData.Level.NOVICE, 2,
                                DwarfMerchantData.Level.APPRENTICE, 2,
                                DwarfMerchantData.Level.JOURNEYMAN, 2,
                                DwarfMerchantData.Level.EXPERT, 2,
                                DwarfMerchantData.Level.MASTER, 2
                        ),
                        DwarfProfessionConfig.RerollRule.RESTOCK
                ),
                pool(
                        DwarfProfessionConfig.PoolType.EXACT_LEVEL,
                        Map.of(
                                DwarfMerchantData.Level.MASTER, 1
                        ),
                        DwarfProfessionConfig.RerollRule.RESTOCK
                )
        );
    }

    private static DwarfProfessionConfig.TradePools scrapperTradePools() {
        return tradePools(
                pool(
                        DwarfProfessionConfig.PoolType.GLOBAL,
                        Map.of(
                                DwarfMerchantData.Level.NOVICE, 2,
                                DwarfMerchantData.Level.APPRENTICE, 2,
                                DwarfMerchantData.Level.JOURNEYMAN, 2,
                                DwarfMerchantData.Level.EXPERT, 2,
                                DwarfMerchantData.Level.MASTER, 2
                        ),
                        DwarfProfessionConfig.RerollRule.RESTOCK
                )
        );
    }

    @SafeVarargs
    private static DwarfProfessionConfig.TradePools tradePools(
            Map.Entry<DwarfProfessionConfig.PoolType, DwarfProfessionConfig.PoolConfig>... entries
    ) {
        return new DwarfProfessionConfig.TradePools(Map.ofEntries(entries));
    }

    private static Map.Entry<DwarfProfessionConfig.PoolType, DwarfProfessionConfig.PoolConfig> pool(
            DwarfProfessionConfig.PoolType type,
            Map<DwarfMerchantData.Level, Integer> rolls,
            DwarfProfessionConfig.RerollRule rerollRule
    ) {
        return Map.entry(
                type,
                new DwarfProfessionConfig.PoolConfig(
                        new DwarfProfessionConfig.PoolRolls(rolls),
                        rerollRule
                )
        );
    }

    private static DwarfProfessionConfig.Sounds soundsBoth(SoundEvent sound) {
        ResourceLocation id = sound.location();
        return new DwarfProfessionConfig.Sounds(Optional.of(id), Optional.of(id));
    }
}