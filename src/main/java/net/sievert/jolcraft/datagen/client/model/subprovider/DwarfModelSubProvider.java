package net.sievert.jolcraft.datagen.client.model.subprovider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreEntries;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelBuilder;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.client.property.custom.LoreKey;
import net.sievert.jolcraft.world.item.client.property.JolCraftItemProperties;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public record DwarfModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final String SUB_BOOK = JolCraftDictionary.BOOK;

    private static final String SUB_TOME = JolCraftStrings.slashed(
            JolCraftDictionary.BOOK,
            JolCraftDictionary.TOME
    );

    private static final String SUB_TABLET = JolCraftDictionary.TABLET;

    private static final String SUB_CONTRACT = JolCraftDictionary.CONTRACT;

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.DWARF;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.flatItem(JolCraftItems.DWARVEN_LEXICON.get(), SUB_BOOK);
        builder.flatItem(JolCraftItems.ANCIENT_DWARVEN_LEXICON.get(), SUB_BOOK);

        builder.flatItem(JolCraftItems.REPUTATION_TABLET_0.get(), SUB_TABLET);
        builder.flatItem(JolCraftItems.REPUTATION_TABLET_1.get(), SUB_TABLET);
        builder.flatItem(JolCraftItems.REPUTATION_TABLET_2.get(), SUB_TABLET);
        builder.flatItem(JolCraftItems.REPUTATION_TABLET_3.get(), SUB_TABLET);
        builder.flatItem(JolCraftItems.REPUTATION_TABLET_4.get(), SUB_TABLET);

        builder.flatItem(JolCraftItems.CONTRACT_BLANK.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_WRITTEN.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_SIGNED.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.GUILD_SIGIL.get(), SUB_CONTRACT);

        builder.flatItem(JolCraftItems.CONTRACT_GUILDMASTER.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_MERCHANT.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_HISTORIAN.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_SCRAPPER.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_GUARD.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_BREWMASTER.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_KEEPER.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_MINER.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_EXPLORER.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_ALCHEMIST.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_ARCANIST.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_PRIEST.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_ARTISAN.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_CHAMPION.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_BLACKSMITH.get(), SUB_CONTRACT);
        builder.flatItem(JolCraftItems.CONTRACT_SMELTER.get(), SUB_CONTRACT);

        builder.flatItem(JolCraftItems.DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get(), JolCraftItems.DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.DWARVEN_TOME_COMMON.get(), JolCraftItems.DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.DWARVEN_TOME_RARE.get(), JolCraftItems.DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.DWARVEN_TOME_EPIC.get(), JolCraftItems.DWARVEN_TOME.get(), SUB_TOME);

        builder.flatItem(JolCraftItems.ANCIENT_DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), SUB_TOME);
        builder.flatItem(JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), SUB_TOME);

        builder.flatItem(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME.get(), JolCraftItems.ANCIENT_DWARVEN_TOME.get(), SUB_TOME);

        generateLegendaryTomeModels(builder);
    }

    private static void generateLegendaryTomeModels(@NotNull JolCraftModelBuilder builder) {
        Item tomeItem = JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get();
        ResourceLocation baseModelLoc = ModelLocationUtils.getModelLocation(tomeItem);

        JsonArray overrides = new JsonArray();

        DwarfLoreEntries.ALL.entrySet().stream()
                .filter(entry -> entry.getValue().rarity() == JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Enum::name)))
                .forEach(entry -> {
                    DwarfLoreKey loreKey = entry.getKey();
                    String keyString = loreKey.name().toLowerCase(Locale.ROOT);

                    JolCraftItemProperties.registerKey(LoreKey.KEY, keyString);

                    ResourceLocation variantModelLoc = JolCraft.location(JolCraftStrings.slashed(
                            JolCraftDictionary.ITEM,
                            JolCraftDictionary.BOOK,
                            JolCraftDictionary.TOME,
                            JolCraftStrings.underscored(
                                    JolCraftItemIds.ANCIENT_DWARVEN_TOME_LEGENDARY,
                                    keyString
                            )
                    ));

                    ModelTemplates.FLAT_ITEM.create(
                            variantModelLoc,
                            TextureMapping.layer0(variantModelLoc),
                            builder::addModel
                    );

                    JsonObject predicate = new JsonObject();
                    predicate.addProperty(
                            LoreKey.KEY.toString(),
                            JolCraftItemProperties.value(LoreKey.KEY, keyString)
                    );

                    JsonObject override = new JsonObject();
                    override.add("predicate", predicate);
                    override.addProperty("model", variantModelLoc.toString());

                    overrides.add(override);
                });

        builder.addModel(baseModelLoc, () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");

            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", JolCraft.location("item/" + SUB_TOME + "/ancient_dwarven_tome").toString());
            json.add("textures", textures);

            json.add("overrides", overrides);
            return json;
        });
    }
}