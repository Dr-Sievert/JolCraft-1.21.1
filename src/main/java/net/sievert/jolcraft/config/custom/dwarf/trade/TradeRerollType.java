package net.sievert.jolcraft.config.custom.dwarf.trade;

import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.NotNull;

public enum TradeRerollType implements JolCraftEnumHelper.StringId {
    RESTOCK(JolCraftDictionary.RESTOCK),
    REROLL(JolCraftDictionary.REROLL);

    private final String id;

    TradeRerollType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static @NotNull DataResult<TradeRerollType> fromSerialized(@NotNull String s) {
        TradeRerollType type =
                JolCraftEnumHelper.byStringIdNullable(TradeRerollType.class, s, null);

        return type != null
                ? DataResult.success(type)
                : DataResult.error(() -> "unknown reroll rule '" + s + "'");
    }
}