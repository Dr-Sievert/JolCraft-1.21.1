package net.sievert.jolcraft.world.loot.custom;

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
import net.sievert.jolcraft.data.key.JolCraftDictionary;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AddItemModifier extends LootModifier {

    public static final MapCodec<AddItemModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    LootModifier.codecStart(inst).and(
                            RegistryFixedCodec.create(Registries.ITEM)
                                    .fieldOf(JolCraftDictionary.ITEM)
                                    .forGetter(e -> e.item)
                    ).apply(inst, AddItemModifier::new)
            );

    private final Holder<Item> item;

    public AddItemModifier(LootItemCondition[] conditionsIn, Holder<Item> item) {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
        for (LootItemCondition condition : this.conditions) {
            if (!condition.test(lootContext)) {
                return generatedLoot;
            }
        }
        generatedLoot.add(new ItemStack(this.item.value()));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}