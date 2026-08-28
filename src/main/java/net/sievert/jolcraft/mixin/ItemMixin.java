package net.sievert.jolcraft.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.alchemy.CorruptionData;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(
            method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void jolcraft$applyCorruptedPotionName(
            ItemStack stack,
            CallbackInfoReturnable<Component> cir
    ) {
        if (!(stack.getItem() instanceof PotionItem)) {
            return;
        }

        CorruptionData corruption =
                stack.get(
                        JolCraftDataComponents.CORRUPTION_DATA.get()
                );

        if (corruption == null) {
            return;
        }

        String potionName =
                Potion.getName(
                        Optional.of(
                                corruption.originalPotion()
                        ),
                        stack.getItem().getDescriptionId()
                                + ".effect."
                );

        cir.setReturnValue(
                Component.translatable(
                        JolCraftLanguageKeys.PREFIX_NAME,
                        EssenceType.CORRUPTED.getName(),
                        Component.translatable(
                                potionName
                        )
                )
        );
    }
}