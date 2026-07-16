package net.sievert.jolcraft.datagen.config.subprovider;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.sound.DwarfProfessionSoundsConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolRolls;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolsConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.TradePoolType;
import net.sievert.jolcraft.config.custom.dwarf.trade.TradeRerollType;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataPathResolver;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.config.ConfigCodecWriter;
import net.sievert.jolcraft.datagen.config.JolCraftConfigProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfProfessionConfigProvider
        implements JolCraftSubDataProvider<CachedOutput> {

    private static final Codec<DwarfProfessionConfig> CODEC = DwarfProfessionConfig.CODEC;

    private final JolCraftConfigProvider parent;

    public DwarfProfessionConfigProvider(@NotNull JolCraftConfigProvider parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDictionary.PROFESSION);
    }

    @Override
    public @NotNull String folder() {
        return id();
    }

    @Override
    public @NotNull JolCraftDataProvider<CachedOutput> parent() {
        return parent;
    }

    @Override
    public void run(
            @NotNull CachedOutput target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        if (packOutput == null) {
            throw new IllegalStateException("PackOutput is required for config datagen");
        }

        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (DwarfProfession prof : DwarfProfession.values()) {
            if (prof == DwarfProfession.NONE) {
                continue;
            }

            String path = JolCraftDataPathResolver.resolvePath(this, prof.getId());
            DwarfProfessionConfig cfg = configFor(prof);

            futures.add(ConfigCodecWriter.write(target, packOutput, path, CODEC, cfg));
            tracking.record(this, path);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        tracking.logTrackedOutputCount(
                this,
                JolCraftStrings.spaced(
                        JolCraftDwarfIds.DWARF,
                        JolCraftDictionary.PROFESSION,
                        JolCraftStrings.plural(JolCraftDictionary.CONFIG)
                )
        );
    }

    private static DwarfProfessionConfig configFor(
            @NotNull DwarfProfession profession
    ) {
        DwarfProfessionConfig defaults = DwarfProfessionConfig.DEFAULTS;

        return switch (profession) {
            case GUILDMASTER -> new DwarfProfessionConfig(
                    defaults.requiredTier(),
                    defaults.restockTicks(),
                    0.8F,

                    false,
                    true,

                    false,
                    defaults.showLevel(),

                    defaults.rules(),
                    soundsBoth(
                            SoundEvents.VILLAGER_WORK_CARTOGRAPHER
                    ),
                    defaults.attributes(),
                    defaults.tradePools()
            );

            case MERCHANT -> new DwarfProfessionConfig(
                    defaults.requiredTier(),
                    defaults.restockTicks(),
                    defaults.voicePitch(),

                    defaults.canReroll(),
                    defaults.canEndorse(),

                    defaults.showProgressBar(),
                    defaults.showLevel(),

                    defaults.rules(),
                    defaults.sounds(),
                    defaults.attributes(),
                    merchantTradePools()
            );

            default -> defaults;
        };
    }

    private static DwarfProfessionTradePoolsConfig merchantTradePools() {
        return tradePools(
                pool(
                        TradePoolType.CUMULATIVE,
                        Map.of(
                                DwarfMerchantData.Level.NOVICE, 2,
                                DwarfMerchantData.Level.APPRENTICE, 2,
                                DwarfMerchantData.Level.JOURNEYMAN, 2,
                                DwarfMerchantData.Level.EXPERT, 2,
                                DwarfMerchantData.Level.MASTER, 2
                        ),
                        TradeRerollType.RESTOCK
                ),
                pool(
                        TradePoolType.EXACT_LEVEL,
                        Map.of(DwarfMerchantData.Level.MASTER, 1),
                        TradeRerollType.RESTOCK
                )
        );
    }

    @SafeVarargs
    private static DwarfProfessionTradePoolsConfig tradePools(
            Map.Entry<TradePoolType, DwarfProfessionTradePoolConfig>... entries
    ) {
        return new DwarfProfessionTradePoolsConfig(Map.ofEntries(entries));
    }

    private static Map.Entry<TradePoolType, DwarfProfessionTradePoolConfig> pool(
            TradePoolType type,
            Map<DwarfMerchantData.Level, Integer> rolls,
            TradeRerollType rerollType
    ) {
        return Map.entry(
                type,
                new DwarfProfessionTradePoolConfig(
                        new DwarfProfessionTradePoolRolls(rolls),
                        rerollType
                )
        );
    }

    private static DwarfProfessionSoundsConfig soundsBoth(SoundEvent sound) {
        var id = sound.getLocation();
        return new DwarfProfessionSoundsConfig(Optional.of(id), Optional.of(id));
    }
}