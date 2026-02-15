package net.sievert.jolcraft.datagen.config.dwarf;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigs;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionSettings;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData.Level.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfProfessionConfigProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public DwarfProfessionConfigProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, DwarfProfessionConfigs.DIRECTORY);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        CompletableFuture<?> merchant = save(
                cache,
                JolCraft.location(DwarfProfession.MERCHANT.getId()),
                restockOnly(levelRolls(
                        NOVICE, 2,
                        APPRENTICE, 2,
                        JOURNEYMAN, 2,
                        EXPERT, 2,
                        MASTER, 1
                ))
        );

        CompletableFuture<?> scrapper = save(
                cache,
                JolCraft.location(DwarfProfession.SCRAPPER.getId()),
                poolOnly(levelRolls(
                        NOVICE, 2,
                        APPRENTICE, 2,
                        JOURNEYMAN, 2,
                        EXPERT, 2,
                        MASTER, 30
                ))
        );

        return CompletableFuture.allOf(merchant, scrapper);
    }

    @Override
    public String getName() {
        return "JolCraft Dwarf Profession Configs";
    }

    // -------------------------------------------------------------------------
    // Save
    // -------------------------------------------------------------------------

    private CompletableFuture<?> save(CachedOutput cache, ResourceLocation id, DwarfProfessionSettings settings) {
        Path path = pathProvider.json(id);
        return DataProvider.saveStable(cache, DwarfProfessionSettings.CODEC, settings, path);
    }

    // -------------------------------------------------------------------------
    // Settings constructors (call-site friendly)
    // -------------------------------------------------------------------------

    private static DwarfProfessionSettings poolOnly(Int2IntOpenHashMap poolRollsByLevel) {
        return DwarfProfessionSettings.trades(
                DwarfProfessionSettings.tradeSettingsPoolOnly(poolRollsByLevel)
        );
    }

    private static DwarfProfessionSettings restockOnly(Int2IntOpenHashMap restockRollsByLevel) {
        return DwarfProfessionSettings.trades(
                DwarfProfessionSettings.tradeSettingsRestockOnly(restockRollsByLevel)
        );
    }

    // -------------------------------------------------------------------------
    // Level -> rolls helper (enum-based)
    // -------------------------------------------------------------------------

    private static Int2IntOpenHashMap levelRolls(Object... pairs) {
        Int2IntOpenHashMap m = new Int2IntOpenHashMap();
        m.defaultReturnValue(0);

        for (int i = 0; i < pairs.length; i += 2) {
            DwarfMerchantData.Level level = (DwarfMerchantData.Level) pairs[i];
            int rolls = (int) pairs[i + 1];
            m.put(level.getId(), rolls);
        }

        return m;
    }
}