package net.sievert.jolcraft.world.loot.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AddLootTableModifier extends LootModifier {

    public static final MapCodec<AddLootTableModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    LootModifier.codecStart(inst).and(
                            inst.group(
                                    ResourceKey.codec(Registries.LOOT_TABLE)
                                            .fieldOf(JolCraftStrings.underscored(JolCraftDictionary.LOOT, JolCraftDictionary.TABLE))
                                            .forGetter(m -> m.lootTable),
                                    Codec.FLOAT
                                            .fieldOf(JolCraftDictionary.CHANCE)
                                            .forGetter(m -> m.chance),
                                    Codec.BOOL
                                            .optionalFieldOf(JolCraftDictionary.REPLACE, false)
                                            .forGetter(m -> m.replace)
                            )
                    ).apply(inst, AddLootTableModifier::new)
            );

    private final ResourceKey<LootTable> lootTable;
    private final float chance;
    private final boolean replace;

    public AddLootTableModifier(
            LootItemCondition[] conditionsIn,
            ResourceKey<LootTable> lootTable,
            float chance,
            boolean replace
    ) {
        super(conditionsIn);
        this.lootTable = lootTable;
        this.chance = Math.max(0.0F, Math.min(1.0F, chance));
        this.replace = replace;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext lootContext
    ) {
        if (lootContext.getRandom().nextFloat() >= this.chance) {
            return generatedLoot;
        }

        if (this.replace) {
            generatedLoot.clear();
        }

        LootTable table = lootContext.getLevel()
                .getServer()
                .reloadableRegistries()
                .getLootTable(this.lootTable);

        table.getRandomItemsRaw(lootContext, generatedLoot::add);

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}