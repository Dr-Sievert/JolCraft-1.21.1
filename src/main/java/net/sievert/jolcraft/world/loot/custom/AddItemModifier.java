package net.sievert.jolcraft.world.loot.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AddItemModifier extends LootModifier {

    public static final MapCodec<AddItemModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    LootModifier.codecStart(inst).and(
                            inst.group(
                                    RegistryFixedCodec.create(Registries.ITEM)
                                            .fieldOf(JolCraftDictionary.ITEM)
                                            .forGetter(modifier -> modifier.item),
                                    Codec.FLOAT
                                            .fieldOf(JolCraftDictionary.CHANCE)
                                            .forGetter(modifier -> modifier.chance),
                                    Codec.BOOL
                                            .optionalFieldOf(JolCraftDictionary.REPLACE, false)
                                            .forGetter(modifier -> modifier.replace)
                            )
                    ).apply(inst, AddItemModifier::new)
            );

    private final Holder<Item> item;
    private final float chance;
    private final boolean replace;

    public AddItemModifier(
            LootItemCondition[] conditionsIn,
            Holder<Item> item,
            float chance,
            boolean replace
    ) {
        super(conditionsIn);
        this.item = item;
        this.chance = Math.max(0.0F, Math.min(1.0F, chance));
        this.replace = replace;
    }

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

        generatedLoot.add(new ItemStack(this.item.value()));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}