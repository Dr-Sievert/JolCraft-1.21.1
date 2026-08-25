package net.sievert.jolcraft.integration.jei.util.recipe;

import mezz.jei.api.recipe.RecipeType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.integration.jei.custom.bounty.reward.JeiBountyRewardRecipe;
import net.sievert.jolcraft.integration.jei.custom.bounty.task.JeiBountyTaskRecipe;
import net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_barrel.JeiFermentingBarrelRecipe;
import net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_cauldron.JeiFermentingCauldronRecipe;
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTradeRecipe;
import net.sievert.jolcraft.integration.jei.custom.hand_interaction.JeiHandInteractionRecipe;
import net.sievert.jolcraft.integration.jei.custom.info.JeiInfoPageRecipe;
import net.sievert.jolcraft.integration.jei.custom.lapidary_bench.JeiLapidaryBenchRecipe;
import net.sievert.jolcraft.integration.jei.custom.mortar.JeiMortarRecipe;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public final class JeiRecipeTypes {

    public static final RecipeType<JeiMortarRecipe> MORTAR =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.MORTAR,
                    JeiMortarRecipe.class
            );

    public static final RecipeType<JeiLapidaryBenchRecipe> LAPIDARY_BENCH =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.LAPIDARY_BENCH,
                    JeiLapidaryBenchRecipe.class
            );

    public static final RecipeType<JeiFermentingCauldronRecipe> FERMENTING_CAULDRON =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.FERMENTING_CAULDRON,
                    JeiFermentingCauldronRecipe.class
            );

    public static final RecipeType<JeiFermentingBarrelRecipe> FERMENTING_BARREL =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.FERMENTING_BARREL,
                    JeiFermentingBarrelRecipe.class
            );

    public static final RecipeType<JeiHandInteractionRecipe> HAND_INTERACTION =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.HAND_INTERACTION,
                    JeiHandInteractionRecipe.class
            );

    public static final RecipeType<JeiBountyTaskRecipe> BOUNTY_TASK =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.BOUNTY_TASK,
                    JeiBountyTaskRecipe.class
            );

    public static final RecipeType<JeiBountyRewardRecipe> BOUNTY_REWARD =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.BOUNTY_REWARD,
                    JeiBountyRewardRecipe.class
            );

    public static final RecipeType<JeiInfoPageRecipe> INFO_PAGE =
            RecipeType.create(
                    JolCraft.MOD_ID,
                    JolCraftJeiIds.INFO_PAGE,
                    JeiInfoPageRecipe.class
            );

    private static final Map<
            DwarfProfession,
            RecipeType<JeiDwarfTradeRecipe>
            > DWARF_TRADES =
            new EnumMap<>(
                    DwarfProfession.class
            );

    private JeiRecipeTypes() {
    }

    public static @NotNull RecipeType<JeiDwarfTradeRecipe> dwarfTrade(
            @NotNull DwarfProfession profession
    ) {
        return DWARF_TRADES.computeIfAbsent(
                profession,
                currentProfession ->
                        RecipeType.create(
                                JolCraft.MOD_ID,
                                JolCraftStrings.underscored(
                                        JolCraftJeiIds.DWARF_TRADE,
                                        currentProfession.getId()
                                ),
                                JeiDwarfTradeRecipe.class
                        )
        );
    }
}