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
import net.sievert.jolcraft.world.sound.JolCraftSounds;
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

    private static final Codec<DwarfProfessionConfig> CODEC =
            DwarfProfessionConfig.CODEC;

    private static final int SCRAPPER_GLOBAL_ROLLS = 4;

    private final JolCraftConfigProvider parent;

    public DwarfProfessionConfigProvider(
            @NotNull JolCraftConfigProvider parent
    ) {
        this.parent = parent;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(
                JolCraftDwarfIds.DWARF,
                JolCraftDictionary.PROFESSION
        );
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
            @Nullable CompletableFuture<
                    net.minecraft.core.HolderLookup.Provider
                    > lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        if (packOutput == null) {
            throw new IllegalStateException(
                    "PackOutput is required for config datagen"
            );
        }

        List<CompletableFuture<?>> futures =
                new ArrayList<>();

        for (DwarfProfession profession :
                DwarfProfession.values()) {

            if (profession == DwarfProfession.NONE) {
                continue;
            }

            String path =
                    JolCraftDataPathResolver.resolvePath(
                            this,
                            profession.getId()
                    );

            DwarfProfessionConfig config =
                    configFor(profession);

            futures.add(
                    ConfigCodecWriter.write(
                            target,
                            packOutput,
                            path,
                            CODEC,
                            config
                    )
            );

            tracking.record(
                    this,
                    path
            );
        }

        CompletableFuture.allOf(
                futures.toArray(
                        CompletableFuture[]::new
                )
        ).join();

        tracking.logTrackedOutputCount(
                this,
                JolCraftStrings.spaced(
                        JolCraftDwarfIds.DWARF,
                        JolCraftDictionary.PROFESSION,
                        JolCraftStrings.plural(
                                JolCraftDictionary.CONFIG
                        )
                )
        );
    }

    private static DwarfProfessionConfig configFor(
            @NotNull DwarfProfession profession
    ) {
        DwarfProfessionConfig defaults =
                DwarfProfessionConfig.DEFAULTS;

        return switch (profession) {

            case NONE ->
                    new DwarfProfessionConfig(
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
                            defaults.tradePools()
                    );

            case GUILDMASTER ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.82F,

                            false,
                            false,

                            false,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents
                                            .VILLAGER_WORK_CARTOGRAPHER
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case HISTORIAN ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            1.02F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents
                                            .VILLAGER_WORK_LIBRARIAN
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case MERCHANT ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            1.05F,

                            false,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    JolCraftSounds.COIN_STACK.get()
                            ),
                            defaults.attributes(),
                            merchantTradePools()
                    );

            case SCRAPPER ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            1.10F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.VILLAGER_WORK_TOOLSMITH
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case BREWMASTER ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            1.1F,

                            false,
                            true,

                            false,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.VILLAGER_WORK_FLETCHER
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case GUARD ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.85F,

                            false,
                            true,

                            false,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.VILLAGER_WORK_WEAPONSMITH
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case KEEPER ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            1.0F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.CROP_PLANTED
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case ARTISAN ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            1.04F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    JolCraftSounds.GEM_CUT.get()
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case EXPLORER ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.95F,

                            true,
                            true,

                            false,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.METAL_HIT
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case MINER ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.90F,

                            false,
                            true,

                            false,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.VILLAGER_WORK_MASON
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case ALCHEMIST ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            1.0F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.VILLAGER_WORK_CLERIC
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case ARCANIST ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.88F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.EVOKER_CAST_SPELL
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case PRIEST ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.80F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.BUNDLE_INSERT
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );

            case BLACKSMITH ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.92F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.VILLAGER_WORK_TOOLSMITH
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );


            case CHAMPION ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.85F,

                            false,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.VILLAGER_WORK_CARTOGRAPHER
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );


            case SMELTER ->
                    new DwarfProfessionConfig(
                            defaults.requiredTier(),
                            defaults.restockTicks(),
                            0.96F,

                            true,
                            true,

                            true,
                            true,

                            defaults.rules(),
                            soundsBoth(
                                    SoundEvents.GILDED_BLACKSTONE_HIT
                            ),
                            defaults.attributes(),
                            defaults.tradePools()
                    );
        };
    }

    private static DwarfProfessionTradePoolsConfig merchantTradePools() {
        return tradePools(
                pool(
                        TradePoolType.CUMULATIVE,
                        Map.of(
                                DwarfMerchantData.Level.NOVICE,
                                3,

                                DwarfMerchantData.Level.APPRENTICE,
                                3,

                                DwarfMerchantData.Level.JOURNEYMAN,
                                3,

                                DwarfMerchantData.Level.EXPERT,
                                3,

                                DwarfMerchantData.Level.MASTER,
                                3
                        ),
                        TradeRerollType.RESTOCK
                ),
                pool(
                        TradePoolType.EXACT_LEVEL,
                        Map.of(
                                DwarfMerchantData.Level.MASTER,
                                1
                        ),
                        TradeRerollType.RESTOCK
                )
        );
    }

    @SafeVarargs
    private static DwarfProfessionTradePoolsConfig tradePools(
            Map.Entry<
                    TradePoolType,
                    DwarfProfessionTradePoolConfig
                    >... entries
    ) {
        return new DwarfProfessionTradePoolsConfig(
                Map.ofEntries(entries)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static Map.Entry<
            TradePoolType,
            DwarfProfessionTradePoolConfig
            > pool(
            TradePoolType type,
            Map<DwarfMerchantData.Level, Integer> rolls,
            TradeRerollType rerollType
    ) {
        return Map.entry(
                type,
                new DwarfProfessionTradePoolConfig(
                        new DwarfProfessionTradePoolRolls(
                                rolls
                        ),
                        rerollType
                )
        );
    }

    private static DwarfProfessionSoundsConfig soundsBoth(
            SoundEvent sound
    ) {
        var id =
                sound.getLocation();

        return new DwarfProfessionSoundsConfig(
                Optional.of(id),
                Optional.of(id)
        );
    }
}