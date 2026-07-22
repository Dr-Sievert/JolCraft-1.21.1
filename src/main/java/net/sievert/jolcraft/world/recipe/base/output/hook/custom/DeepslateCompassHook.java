package net.sievert.jolcraft.world.recipe.base.output.hook.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.world.player.attachment.custom.compass.DiscoveredStructuresAttachmentHelper;
import net.sievert.jolcraft.world.player.attachment.custom.compass.DiscoveredStructuresAttachmentHelper.LocatedStructure;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipeInput;
import net.sievert.jolcraft.world.recipe.base.output.hook.RecipeHook;
import org.jetbrains.annotations.NotNull;

public final class DeepslateCompassHook implements RecipeHook {

    private static final int SEARCH_RADIUS = 20;

    @Override
    public boolean apply(
            @NotNull LootContext context,
            @NotNull Object generatedOutput,
            @NotNull RecipeInput input
    ) {
        if (!(generatedOutput instanceof ItemStack output)) {
            return false;
        }

        if (output.isEmpty()) {
            return false;
        }

        if (!(input instanceof HandInteractionRecipeInput(
                ItemStack ingredientA,
                ItemStack ingredientB
        ))) {
            return false;
        }

        if (ingredientA.isEmpty() || ingredientB.isEmpty()) {
            return false;
        }

        ItemStack compass;
        ItemStack dial;

        if (ingredientA.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                && ingredientB.is(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get())) {

            compass = ingredientA;
            dial = ingredientB;

        } else if (ingredientB.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                && ingredientA.is(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get())) {

            compass = ingredientB;
            dial = ingredientA;

        } else {
            return false;
        }

        Entity entity =
                context.getParamOrNull(
                        LootContextParams.THIS_ENTITY
                );

        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        var dyedColor =
                compass.get(
                        DataComponents.DYED_COLOR
                );

        if (dyedColor != null) {
            output.set(
                    DataComponents.DYED_COLOR,
                    dyedColor
            );
        } else {
            output.remove(
                    DataComponents.DYED_COLOR
            );
        }

        String groupId =
                dial.get(
                        JolCraftDataComponents
                                .STRUCTURE_GROUP
                                .get()
                );

        if (groupId == null || groupId.isBlank()) {
            return false;
        }

        TagKey<Structure> structureTag =
                DeepslateCompassStructureGroup
                        .structureTag(groupId);

        if (structureTag == null) {
            return false;
        }

        LocatedStructure located =
                DiscoveredStructuresAttachmentHelper
                        .findNearestUndiscoveredStructure(
                                player,
                                structureTag,
                                SEARCH_RADIUS
                        );

        if (located == null) {
            return false;
        }

        output.set(
                JolCraftDataComponents
                        .STRUCTURE_GROUP
                        .get(),
                located.structureId().toString()
        );

        var dialColor =
                dial.get(
                        JolCraftDataComponents
                                .DEEPSLATE_COMPASS_DIAL_COLOR
                                .get()
                );

        if (dialColor != null) {
            output.set(
                    JolCraftDataComponents
                            .DEEPSLATE_COMPASS_DIAL_COLOR
                            .get(),
                    dialColor
            );
        } else {
            output.remove(
                    JolCraftDataComponents
                            .DEEPSLATE_COMPASS_DIAL_COLOR
                            .get()
            );
        }

        output.set(
                JolCraftDataComponents
                        .DEEPSLATE_COMPASS_TARGET
                        .get(),
                located.pos()
        );

        return true;
    }
}
