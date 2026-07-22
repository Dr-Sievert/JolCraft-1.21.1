package net.sievert.jolcraft.world.player.attachment.custom.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LanguageType implements JolCraftEnumHelper.StringId {
    DWARVEN(JolCraftDictionary.DWARVEN),
    ANCIENT_DWARVEN(JolCraftStrings.underscored(JolCraftDictionary.ANCIENT, JolCraftDictionary.DWARVEN));

    public static final Codec<LanguageType> CODEC =
            Codec.STRING.comapFlatMap(
                    id -> {
                        LanguageType type = JolCraftEnumHelper.byStringIdNullable(LanguageType.class, id, null);
                        return type != null
                                ? DataResult.success(type)
                                : DataResult.error(() -> "Unknown language type: " + id);
                    },
                    LanguageType::getId
            );

    public static final LanguageType DEFAULT = DWARVEN;

    private final String id;

    LanguageType(String id) {
        this.id = id;
    }

    public static LanguageType byId(@Nullable String id) {
        return JolCraftEnumHelper.byStringId(LanguageType.class, id, DEFAULT);
    }

    @Override
    public @NotNull String getId() {
        return id;
    }
}