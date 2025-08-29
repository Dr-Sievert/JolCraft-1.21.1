package net.sievert.jolcraft.item.custom.food;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.block.custom.crop.HopsType;
import net.sievert.jolcraft.data.JolCraftDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarvenBrewItem extends Item {

    public DwarvenBrewItem(Properties properties) {
        super(properties);
    }

    /** Returns list of HopsType values parsed from the HOPS DataComponent string. */
    public static List<HopsType> getHopsFromItem(ItemStack stack) {
        String data = stack.get(JolCraftDataComponents.HOPS.get());
        if (data == null || data.isEmpty()) return Collections.emptyList();

        String[] split = data.split(",");
        List<HopsType> hops = new ArrayList<>();
        for (String s : split) {
            try {
                hops.add(HopsType.valueOf(s.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // Unknown hop name, skip
            }
        }
        return hops;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (Screen.hasShiftDown()) {
            List<HopsType> hops = getHopsFromItem(stack);
            if (!hops.isEmpty()) {
                tooltip.add(Component.translatable("tooltip.jolcraft.brew.hops_added").withStyle(ChatFormatting.GRAY));
                for (HopsType hop : hops) {
                    tooltip.add(Component.translatable("tooltip.jolcraft.hops." + hop.name().toLowerCase()).withStyle(ChatFormatting.DARK_GREEN));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.jolcraft.brew.no_hops_added").withStyle(ChatFormatting.GRAY));
            }
        } else {
            Component shiftKey = Component.literal("Shift").withStyle(ChatFormatting.BLUE);
            tooltip.add(Component.translatable("tooltip.jolcraft.shift", shiftKey)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }


    /** Applies effects from each hop type when consumed */
    private void applyHopEffects(ItemStack stack, Player player) {
        for (HopsType hop : getHopsFromItem(stack)) {
            switch (hop) {
                case ASGARNIAN -> player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 6000, 0));
                case DUSKHOLD -> player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0));
                case KRANDONIAN -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, 0));
                case YANILLIAN -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 6000, 0));
            }
        }
    }

    /** Triggers hop effects on consumption */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            applyHopEffects(stack, player);
        }
        return super.finishUsingItem(stack, level, entity);
    }
}
