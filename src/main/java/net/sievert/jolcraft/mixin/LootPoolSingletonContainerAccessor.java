package net.sievert.jolcraft.mixin;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPoolSingletonContainer.class)
public interface LootPoolSingletonContainerAccessor {

    @Accessor("weight")
    int jolcraft$getWeight();

    @Accessor("functions")
    List<LootItemFunction> jolcraft$getFunctions();
}