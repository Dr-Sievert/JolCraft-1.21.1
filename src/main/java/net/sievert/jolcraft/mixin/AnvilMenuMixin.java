package net.sievert.jolcraft.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.event.game.item.name.JolCraftItemNameHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Inject(method = "createResult", at = @At("TAIL"))
    private void jolcraft$applyStyledAnvilName(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack result = menu.getSlot(2).getItem();

        JolCraftItemNameHelper.applySpecialNameStyle(result);
    }
}