package net.sievert.jolcraft.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {
    @Inject(
            method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN")
    )
    private void jolcraft$reconcileTrimAttributes(
            SmithingRecipeInput input,
            HolderLookup.Provider registries,
            CallbackInfoReturnable<ItemStack> callback
    ) {
        ItemStack result = callback.getReturnValue();
        if (result.isEmpty() || JolCraftTrimAttributes.getSlotForArmor(result) == null) return;
        ArmorTrim trim = result.get(DataComponents.TRIM);
        if (trim != null) JolCraftTrimAttributes.applyAttribute(result, trim);
    }
}
