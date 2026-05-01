package net.sievert.jolcraft.world.recipe.param.output.hook.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.world.player.attachment.custom.compass.DiscoveredStructuresAttachmentHelper;
import net.sievert.jolcraft.world.player.attachment.custom.compass.DiscoveredStructuresAttachmentHelper.LocatedStructure;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.base.Output;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DeepslateCompassHook {

    private static final int SEARCH_RADIUS = 20;

    private DeepslateCompassHook() {}

    public static void apply(
            @NotNull WorldContext ctx,
            @NotNull ItemTransformSourceResolver resolver,
            @NotNull List<Output> outputs
    ) {
        ServerPlayer player = (ServerPlayer) ctx.player();

        if (player == null) {
            cancel(outputs, "missing player");
            return;
        }

        ItemStack dial = resolver.resolveItemTransformSource(HandInteractionRecipe.SOURCE_INGREDIENT_B);
        if (dial.isEmpty()) {
            cancel(outputs, "missing dial");
            return;
        }

        String groupId = dial.get(JolCraftDataComponents.STRUCTURE_GROUP.get());
        if (groupId == null || groupId.isBlank()) {
            cancel(outputs, "missing structure group");
            return;
        }

        TagKey<Structure> structureTag = DeepslateCompassStructureGroup.structureTag(groupId);
        if (structureTag == null) {
            cancel(outputs, "unknown structure group=" + groupId);
            return;
        }

        LocatedStructure located = DiscoveredStructuresAttachmentHelper.findNearestUndiscoveredStructure(
                player,
                structureTag,
                SEARCH_RADIUS
        );

        if (located == null) {
            cancel(outputs, "no valid target for group=" + groupId);
            return;
        }

        applyCompassComponents(player, dial, outputs, located);
    }

    private static void applyCompassComponents(
            @NotNull ServerPlayer player,
            @NotNull ItemStack dial,
            @NotNull List<Output> outputs,
            @NotNull LocatedStructure located
    ) {
        String structureId = located.structureId().toString();

        for (Output out : outputs) {
            if (!(out instanceof Output.Items items)) continue;

            for (ItemStack stack : items.stacksSafe()) {
                if (stack.isEmpty()) continue;

                stack.set(JolCraftDataComponents.STRUCTURE_GROUP.get(), structureId);

                var dialColor = dial.get(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get());
                if (dialColor != null) {
                    stack.set(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get(), dialColor);
                }

                stack.set(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET.get(), located.pos());

                JolCraftLogs.debug(
                        JolCraftLogTags.PLAYER,
                        "Deepslate compass crafted: player={}, dim={}, structure={}, pos={}",
                        player.getDisplayName(),
                        located.pos().dimension().location(),
                        structureId,
                        JolCraftLogs.roundedPos(located.pos().pos())
                );
            }
        }
    }

    private static void cancel(@NotNull List<Output> outputs, String reason) {
        outputs.clear();

        JolCraftLogs.debug(
                JolCraftLogTags.RECIPE,
                "Deepslate compass output cancelled: {}",
                reason
        );
    }
}