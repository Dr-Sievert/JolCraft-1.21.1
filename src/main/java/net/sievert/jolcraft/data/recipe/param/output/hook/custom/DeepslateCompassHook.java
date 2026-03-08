package net.sievert.jolcraft.data.recipe.param.output.hook.custom;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.attachment.custom.compass.DiscoveredStructuresHelper;
import net.sievert.jolcraft.data.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.transform.ItemTransformSourceResolver;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.item.util.compass.DeepslateCompassHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DeepslateCompassHook {

    private DeepslateCompassHook() {}

    public static void apply(
            @NotNull WorldContext ctx,
            @NotNull ItemTransformSourceResolver resolver,
            @NotNull List<Output> outputs
    ) {
        ServerLevel serverLevel = ctx.level();
        Player player = ctx.player();
        if (player == null) {
            return;
        }

        ItemStack dial = resolver.resolveItemTransformSource(HandInteractionRecipe.SOURCE_INGREDIENT_B);
        if (dial.isEmpty()) {
            return;
        }

        String group = dial.get(JolCraftDataComponents.STRUCTURE_GROUP);
        if (group == null || group.isBlank()) {
            return;
        }

        TagKey<Structure> structureTag = DeepslateCompassHelper.getStructureTagForGroup(group);
        if (structureTag == null) {
            return;
        }

        GlobalPos targetPos = DiscoveredStructuresHelper.findNearestUndiscoveredStructure(
                serverLevel,
                structureTag,
                player.blockPosition(),
                100,
                player
        );
        if (targetPos == null) {
            return;
        }

        String foundStructureFullId = group;

        var registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        var allRefs = serverLevel.structureManager().getAllStructuresAt(targetPos.pos());

        outer:
        for (Structure structure : allRefs.keySet()) {
            for (Holder<Structure> holder : registry.getTagOrEmpty(structureTag)) {
                if (holder.value() == structure) {
                    ResourceLocation id = registry.getKey(structure);
                    if (id != null) {
                        foundStructureFullId = id.toString();
                    }
                    break outer;
                }
            }
        }

        for (Output out : outputs) {
            if (!(out instanceof Output.Items items)) {
                continue;
            }

            for (ItemStack stack : items.stacksSafe()) {
                if (stack.isEmpty()) {
                    continue;
                }

                stack.set(JolCraftDataComponents.STRUCTURE_GROUP, foundStructureFullId);

                var dialColor = dial.get(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get());
                if (dialColor != null) {
                    stack.set(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR, dialColor);
                }

                stack.set(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET, targetPos);

                JolCraftLogs.debug(
                        JolCraftLogTags.PLAYER,
                        "Deepslate compass crafted: player={}, structure={}, dim={}, pos={}",
                        player.getUUID(),
                        foundStructureFullId,
                        targetPos.dimension().location(),
                        JolCraftLogs.roundedPos(targetPos.pos())
                );
            }
        }
    }
}