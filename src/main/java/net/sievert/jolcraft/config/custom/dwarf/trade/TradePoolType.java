package net.sievert.jolcraft.config.custom.dwarf.trade;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

public enum TradePoolType implements JolCraftEnumHelper.StringId {
    GLOBAL(JolCraftDictionary.GLOBAL),
    CUMULATIVE(JolCraftDictionary.CUMULATIVE),
    EXACT_LEVEL(JolCraftStrings.underscored(
            JolCraftDictionary.EXACT,
            JolCraftDictionary.LEVEL
    ));

    private final String id;

    TradePoolType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static @NotNull DataResult<TradePoolType> fromSerialized(@NotNull String s) {
        TradePoolType type = JolCraftEnumHelper.byStringIdNullable(TradePoolType.class, s, null);
        return type != null
                ? DataResult.success(type)
                : DataResult.error(() -> "unknown pool type '" + s + "'");
    }
}