package net.sievert.jolcraft.world.item.custom.alchemy;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class EssenceItem extends Item {

    public EssenceItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        EssenceType type = stack.getOrDefault(
                JolCraftDataComponents.ESSENCE_TYPE.get(),
                EssenceType.INFUSED
        );

        return Component.translatable(
                JolCraftLanguageKeys.PREFIX_NAME,
                type.getName(),
                super.getName(stack)
        );
    }

    public ItemStack createStack(EssenceType type) {
        ItemStack stack = new ItemStack(this);
        stack.set(
                JolCraftDataComponents.ESSENCE_TYPE.get(),
                type
        );
        return stack;
    }
}
