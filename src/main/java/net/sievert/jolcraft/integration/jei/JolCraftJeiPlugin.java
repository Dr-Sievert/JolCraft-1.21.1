package net.sievert.jolcraft.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.jei.JolCraftJeiIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.custom.bounty.reward.JeiBountyRewardCategory;
import net.sievert.jolcraft.integration.jei.custom.bounty.reward.JeiBountyRewardHelper;
import net.sievert.jolcraft.integration.jei.custom.bounty.task.JeiBountyTaskCategory;
import net.sievert.jolcraft.integration.jei.custom.bounty.task.JeiBountyTaskHelper;
import net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_barrel.JeiFermentingBarrelCategory;
import net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_barrel.JeiFermentingBarrelHelper;
import net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_cauldron.JeiFermentingCauldronCategory;
import net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_cauldron.JeiFermentingCauldronHelper;
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTradeCategory;
import net.sievert.jolcraft.integration.jei.custom.dwarf_trade.JeiDwarfTradeHelper;
import net.sievert.jolcraft.integration.jei.custom.hand_interaction.JeiHandInteractionCategory;
import net.sievert.jolcraft.integration.jei.custom.hand_interaction.JeiHandInteractionHelper;
import net.sievert.jolcraft.integration.jei.custom.info.JeiInfoPageCategory;
import net.sievert.jolcraft.integration.jei.custom.info.JeiInfoPageHelper;
import net.sievert.jolcraft.integration.jei.custom.lapidary_bench.JeiLapidaryBenchCategory;
import net.sievert.jolcraft.integration.jei.custom.lapidary_bench.JeiLapidaryBenchHelper;
import net.sievert.jolcraft.integration.jei.util.JeiCategoryDefinition;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeTypes;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

@JeiPlugin
@SuppressWarnings("removal")
public final class JolCraftJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID =
            JolCraft.location(
                    JolCraftJeiIds.JEI_PLUGIN
            );

    private static final List<JeiCategoryDefinition<?>> CATEGORY_DEFINITIONS =
            List.of(
                    new JeiCategoryDefinition<>(
                            JeiLapidaryBenchCategory::new,
                            JeiRecipeTypes.LAPIDARY_BENCH,
                            JeiLapidaryBenchHelper::getRecipes,
                            catalysts(
                                    () -> new ItemStack(
                                            JolCraftBlocks
                                                    .LAPIDARY_BENCH
                                                    .get()
                                    )
                            )
                    ),
                    new JeiCategoryDefinition<>(
                            JeiFermentingCauldronCategory::new,
                            JeiRecipeTypes.FERMENTING_CAULDRON,
                            JeiFermentingCauldronHelper::getRecipes,
                            catalysts(
                                    () -> new ItemStack(
                                            Blocks.CAULDRON
                                    )
                            )
                    ),
                    new JeiCategoryDefinition<>(
                            JeiFermentingBarrelCategory::new,
                            JeiRecipeTypes.FERMENTING_BARREL,
                            JeiFermentingBarrelHelper::getRecipes,
                            catalysts(
                                    () -> new ItemStack(
                                            Blocks.BARREL
                                    )
                            )
                    ),
                    new JeiCategoryDefinition<>(
                            JeiHandInteractionCategory::new,
                            JeiRecipeTypes.HAND_INTERACTION,
                            JeiHandInteractionHelper::getRecipes,
                            List.of()
                    ),
                    new JeiCategoryDefinition<>(
                            JeiBountyTaskCategory::new,
                            JeiRecipeTypes.BOUNTY_TASK,
                            JeiBountyTaskHelper::getRecipes,
                            catalysts(
                                    () -> new ItemStack(
                                            JolCraftItems.BOUNTY.get()
                                    )
                            )
                    ),
                    new JeiCategoryDefinition<>(
                            JeiBountyRewardCategory::new,
                            JeiRecipeTypes.BOUNTY_REWARD,
                            JeiBountyRewardHelper::getRecipes,
                            catalysts(
                                    () -> new ItemStack(
                                            JolCraftItems.BOUNTY.get()
                                    ),
                                    () -> new ItemStack(
                                            JolCraftItems.BOUNTY_CRATE.get()
                                    )
                            )
                    ),
                    new JeiCategoryDefinition<>(
                            JeiInfoPageCategory::new,
                            JeiRecipeTypes.INFO_PAGE,
                            JeiInfoPageHelper::getRecipes,
                            List.of()
                    )
            );

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(
            IRecipeCategoryRegistration registration
    ) {
        var guiHelper =
                registration
                        .getJeiHelpers()
                        .getGuiHelper();

        for (DwarfProfession profession : DwarfProfession.values()) {
            registration.addRecipeCategories(
                    new JeiDwarfTradeCategory(
                            guiHelper,
                            profession
                    )
            );
        }

        for (JeiCategoryDefinition<?> definition : CATEGORY_DEFINITIONS) {
            registration.addRecipeCategories(
                    definition.createCategory(
                            guiHelper
                    )
            );
        }
    }

    @Override
    public void registerRecipes(
            @NotNull IRecipeRegistration registration
    ) {
        for (DwarfProfession profession : DwarfProfession.values()) {
            var recipes =
                    JeiDwarfTradeHelper.getRecipes(
                            profession
                    );

            if (!recipes.isEmpty()) {
                registration.addRecipes(
                        JeiRecipeTypes.dwarfTrade(
                                profession
                        ),
                        recipes
                );
            }
        }

        for (JeiCategoryDefinition<?> definition : CATEGORY_DEFINITIONS) {
            definition.registerRecipes(
                    registration
            );
        }
    }

    @Override
    public void registerRecipeCatalysts(
            @NotNull IRecipeCatalystRegistration registration
    ) {
        for (JeiCategoryDefinition<?> definition : CATEGORY_DEFINITIONS) {
            definition.registerCatalysts(
                    registration
            );
        }
    }

    @SafeVarargs
    private static @NotNull List<Supplier<ItemStack>> catalysts(
            @NotNull Supplier<ItemStack>... catalysts
    ) {
        return List.of(
                catalysts
        );
    }

    @Override
    public void registerItemSubtypes(
            @NotNull ISubtypeRegistration registration
    ) {
        registration.registerSubtypeInterpreter(
                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(),
                (stack, context) -> {
                    String loreKey =
                            stack.get(
                                    JolCraftDataComponents.DWARF_LORE_KEY.get()
                            );

                    return loreKey != null
                            ? loreKey.toLowerCase(Locale.ROOT)
                            : JolCraftDictionary.EMPTY;
                }
        );

        registration.registerSubtypeInterpreter(
                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get(),
                (stack, context) -> {
                    String group =
                            stack.get(
                                    JolCraftDataComponents.STRUCTURE_GROUP.get()
                            );

                    if (group == null || group.isEmpty()) {
                        group = JolCraftDictionary.UNKNOWN;
                    } else {
                        group = group.toLowerCase(Locale.ROOT);
                    }

                    DeepslateCompassDialColor compassColor =
                            stack.get(
                                    JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get()
                            );

                    String rgb =
                            compassColor != null
                                    ? Integer.toString(compassColor.color())
                                    : JolCraftDictionary.DEFAULT;

                    return JolCraftStrings.underscored(
                            group,
                            rgb
                    );
                }
        );

        registration.registerSubtypeInterpreter(
                JolCraftItems.REWARD_CRATE.get(),
                (stack, context) -> {
                    RewardCrateSource source =
                            stack.get(
                                    JolCraftDataComponents.REWARD_CRATE_SOURCE.get()
                            );

                    if (source instanceof RewardCrateSource.LootTableSource(
                            ResourceKey<LootTable> lootTable
                    )) {
                        return JolCraftStrings.underscored(
                                JolCraftDictionary.LOOT,
                                JolCraftDictionary.TABLE
                        ) + ":" + lootTable.location();
                    }

                    if (source instanceof RewardCrateSource.RecipeSource(
                            ResourceLocation recipeId
                    )) {
                        return JolCraftDictionary.RECIPE + ":" + recipeId;
                    }

                    return JolCraftDictionary.EMPTY;
                }
        );

        registration.registerSubtypeInterpreter(
                JolCraftItems.TANNIN.get(),
                (stack, context) -> {
                    var handler =
                            stack.getCapability(
                                    Capabilities.FluidHandler.ITEM
                            );

                    if (handler == null) {
                        return JolCraftDictionary.EMPTY;
                    }

                    var fluid =
                            handler.drain(
                                    Integer.MAX_VALUE,
                                    IFluidHandler.FluidAction.SIMULATE
                            );

                    if (fluid.isEmpty()) {
                        return JolCraftDictionary.EMPTY;
                    }

                    var maxAge =
                            fluid.getOrDefault(
                                    JolCraftDataComponents.MAX_BREW_AGE.get(),
                                    DwarvenBrewAge.MATURED
                            );

                    return JolCraftStrings.underscored(
                            net.minecraft.core.registries.BuiltInRegistries.FLUID
                                    .getKey(fluid.getFluid())
                                    .getPath(),
                            maxAge.getId()
                    );
                }
        );

        registration.registerSubtypeInterpreter(
                JolCraftItems.YEAST.get(),
                (stack, context) -> {
                    var handler =
                            stack.getCapability(
                                    Capabilities.FluidHandler.ITEM
                            );

                    if (handler == null) {
                        return JolCraftDictionary.EMPTY;
                    }

                    var fluid =
                            handler.drain(
                                    Integer.MAX_VALUE,
                                    IFluidHandler.FluidAction.SIMULATE
                            );

                    if (fluid.isEmpty()) {
                        return JolCraftDictionary.EMPTY;
                    }

                    float speed =
                            fluid.getOrDefault(
                                    JolCraftDataComponents.BREWING_SPEED.get(),
                                    1.0F
                            );

                    return Float.toString(speed);
                }
        );

        registration.registerSubtypeInterpreter(
                JolCraftItems.YEAST_CULTURE.get(),
                (stack, context) -> Float.toString(
                        stack.getOrDefault(
                                JolCraftDataComponents.BREWING_SPEED.get(),
                                1.0F
                        )
                )
        );
    }
}