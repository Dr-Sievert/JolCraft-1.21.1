package net.sievert.jolcraft.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootTable.class)
public interface LootTableAccessor {

    @Accessor("pools")
    List<LootPool> jolcraft$getPools();

    @Accessor("functions")
    List<LootItemFunction> jolcraft$getFunctions();
}
