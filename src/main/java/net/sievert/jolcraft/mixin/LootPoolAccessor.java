package net.sievert.jolcraft.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

    @Accessor("entries")
    List<LootPoolEntryContainer> jolcraft$getEntries();

    @Accessor("conditions")
    List<LootItemCondition> jolcraft$getConditions();

    @Accessor("functions")
    List<LootItemFunction> jolcraft$getFunctions();

    @Accessor("bonusRolls")
    NumberProvider jolcraft$getBonusRolls();
}
