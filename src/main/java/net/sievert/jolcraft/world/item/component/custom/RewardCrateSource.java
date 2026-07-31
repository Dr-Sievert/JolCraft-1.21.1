package net.sievert.jolcraft.world.item.component.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

/**
 * Deferred loot source stored by reward crates.
 */
public sealed interface RewardCrateSource
        permits RewardCrateSource.LootTableSource,
                RewardCrateSource.RecipeSource {

    String LOOT_TABLE_TYPE = "loot_table";
    String RECIPE_TYPE = "recipe";

    MapCodec<Raw> RAW_CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.STRING
                                    .fieldOf(JolCraftDictionary.TYPE)
                                    .forGetter(Raw::type),
                            ResourceLocation.CODEC
                                    .fieldOf(JolCraftDictionary.ID)
                                    .forGetter(Raw::id)
                    ).apply(
                            instance,
                            Raw::new
                    )
            );

    Codec<RewardCrateSource> CODEC =
            RAW_CODEC.codec()
                    .flatXmap(
                            RewardCrateSource::decode,
                            RewardCrateSource::encode
                    );

    StreamCodec<RegistryFriendlyByteBuf, RewardCrateSource> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, source) -> {
                        if (source instanceof LootTableSource lootTable) {
                            buffer.writeEnum(Kind.LOOT_TABLE);
                            buffer.writeResourceLocation(
                                    lootTable.lootTable().location()
                            );
                            return;
                        }

                        RecipeSource recipe =
                                (RecipeSource) source;

                        buffer.writeEnum(Kind.RECIPE);
                        buffer.writeResourceLocation(recipe.recipeId());
                    },
                    buffer -> {
                        Kind kind = buffer.readEnum(Kind.class);
                        ResourceLocation id = buffer.readResourceLocation();

                        return kind == Kind.LOOT_TABLE
                                ? lootTable(id)
                                : recipe(id);
                    }
            );

    static @NotNull RewardCrateSource lootTable(
            @NotNull ResourceKey<LootTable> lootTable
    ) {
        return new LootTableSource(lootTable);
    }

    static @NotNull RewardCrateSource lootTable(
            @NotNull ResourceLocation lootTable
    ) {
        return lootTable(
                ResourceKey.create(
                        Registries.LOOT_TABLE,
                        lootTable
                )
        );
    }

    static @NotNull RewardCrateSource recipe(
            @NotNull ResourceLocation recipeId
    ) {
        return new RecipeSource(recipeId);
    }

    private static @NotNull DataResult<RewardCrateSource> decode(
            @NotNull Raw raw
    ) {
        String type =
                raw.type()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        if (type.equals(LOOT_TABLE_TYPE)) {
            return DataResult.success(
                    lootTable(raw.id())
            );
        }

        if (type.equals(RECIPE_TYPE)) {
            return DataResult.success(
                    recipe(raw.id())
            );
        }

        return DataResult.error(() ->
                "unknown reward crate source type '"
                        + raw.type()
                        + "'; expected '"
                        + LOOT_TABLE_TYPE
                        + "' or '"
                        + RECIPE_TYPE
                        + "'"
        );
    }

    private static @NotNull DataResult<Raw> encode(
            @NotNull RewardCrateSource source
    ) {
        if (source instanceof LootTableSource lootTable) {
            return DataResult.success(
                    new Raw(
                            LOOT_TABLE_TYPE,
                            lootTable.lootTable().location()
                    )
            );
        }

        if (source instanceof RecipeSource recipe) {
            return DataResult.success(
                    new Raw(
                            RECIPE_TYPE,
                            recipe.recipeId()
                    )
            );
        }

        return DataResult.error(
                () -> "unknown reward crate source"
        );
    }

    record LootTableSource(
            @NotNull ResourceKey<LootTable> lootTable
    ) implements RewardCrateSource {

        public LootTableSource {
            Objects.requireNonNull(lootTable, "lootTable");
        }
    }

    record RecipeSource(
            @NotNull ResourceLocation recipeId
    ) implements RewardCrateSource {

        public RecipeSource {
            Objects.requireNonNull(recipeId, "recipeId");
        }
    }

    record Raw(
            @NotNull String type,
            @NotNull ResourceLocation id
    ) {

        public Raw {
            Objects.requireNonNull(type, JolCraftDictionary.TYPE);
            Objects.requireNonNull(id, JolCraftDictionary.ID);
        }
    }

    enum Kind {
        LOOT_TABLE,
        RECIPE
    }
}
