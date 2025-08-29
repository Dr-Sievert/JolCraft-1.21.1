package net.sievert.jolcraft.item.custom.explorer;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.JolCraftDataComponents;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeepslateCompassItem extends Item {

    public DeepslateCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player.isCreative() && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            GlobalPos tracked = stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET);
            if (tracked != null) {
                BlockPos pos = tracked.pos();
                BlockPos source = player.blockPosition();

                int distance = (int) Math.round(Math.sqrt(
                        Math.pow(source.getX() - pos.getX(), 2) +
                                Math.pow(source.getZ() - pos.getZ(), 2)
                ));

                String yStr = "~";
                Component coord = ComponentUtils.wrapInSquareBrackets(
                        Component.translatable("chat.coordinates", pos.getX(), yStr, pos.getZ())
                ).withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/tp @s " + pos.getX() + " " + yStr + " " + pos.getZ()
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("chat.coordinates.tooltip")
                        ))
                );

                String structureId = stack.get(JolCraftDataComponents.STRUCTURE_GROUP);
                Component name = (structureId != null && !structureId.isEmpty())
                        ? Component.translatable("tooltip.jolcraft.structure." + structureId).withStyle(ChatFormatting.BLUE)
                        : Component.translatable("tooltip.jolcraft.structure.unknown").withStyle(ChatFormatting.BLUE);

                serverPlayer.sendSystemMessage(
                        Component.translatable(
                                "tooltip.jolcraft.deepslate_compass.locate",
                                name,
                                coord,
                                distance
                        )
                );
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(level, player, hand);
    }


    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.jolcraft.deepslate_compass").withStyle(ChatFormatting.GRAY));

        String structureId = stack.get(JolCraftDataComponents.STRUCTURE_GROUP);
        if (structureId != null && !structureId.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.jolcraft.structure." + structureId).withStyle(ChatFormatting.BLUE));

            // Show target coords for creative players
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative()) {
                var pos = stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET);
                if (pos != null) {
                    tooltip.add(Component.literal(
                                    "Tracked: " + "X: " + pos.pos().getX() + ", " + "Z: " + pos.pos().getZ())
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
        else {
            tooltip.add(Component.translatable("tooltip.jolcraft.structure.unknown").withStyle(ChatFormatting.DARK_GRAY));
        }
    }



}
