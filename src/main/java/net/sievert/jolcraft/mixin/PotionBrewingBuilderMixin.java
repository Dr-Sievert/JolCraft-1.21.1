package net.sievert.jolcraft.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionBrewing.Builder.class)
public abstract class PotionBrewingBuilderMixin {

    @SuppressWarnings("deprecation")
    @Inject(
            method = "addMix",
            at = @At("HEAD"),
            cancellable = true
    )
    private void jolcraft$removeVanillaFireResistanceRecipe(
            Holder<Potion> input,
            Item reagent,
            Holder<Potion> result,
            CallbackInfo callback
    ) {
        if (input.is(Potions.AWKWARD)
                && reagent == Items.MAGMA_CREAM
                && result.is(Potions.FIRE_RESISTANCE)) {
            callback.cancel();
        }
    }
}