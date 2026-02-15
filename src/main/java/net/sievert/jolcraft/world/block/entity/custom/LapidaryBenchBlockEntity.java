package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfTomeUnlockHelper;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.gui.custom.menu.LapidaryBenchMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import net.sievert.jolcraft.data.recipe.custom.lapidary_bench.LapidaryRecipeInput;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchBlockEntity extends BaseContainerBlockEntity  {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int SLOT_COUNT = 3;

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public LapidaryBenchBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.LAPIDARY_BENCH.get(), pos, state);
    }

    public void handleAction(ServerPlayer player, int actionId) {
        Level level = player.level();
        if (level.isClientSide) return;

        LapidaryBenchRecipe.ToolType toolType = switch (actionId) {
            case 0 -> LapidaryBenchRecipe.ToolType.HAMMER;
            case 1 -> LapidaryBenchRecipe.ToolType.CHISEL;
            default -> null;
        };
        if (toolType == null) return;

        ItemStack input  = this.items.getFirst();
        ItemStack tool   = this.items.get(SLOT_TOOL);
        ItemStack output = this.items.get(SLOT_OUTPUT);

        if (input.isEmpty() || tool.isEmpty() || !toolType.matchesTool(tool)) return;

        boolean isGeode = input.is(JolCraftTags.Items.GEODES);
        boolean isUncutGem = input.is(JolCraftTags.Items.GEMS_UNCUT);

        boolean geodeSmall  = isGeode && input.is(JolCraftItems.GEODE_SMALL.get());
        boolean geodeMedium = isGeode && input.is(JolCraftItems.GEODE_MEDIUM.get());
        boolean geodeLarge  = isGeode && input.is(JolCraftItems.GEODE_LARGE.get());

        if (toolType == LapidaryBenchRecipe.ToolType.HAMMER) {
            if (!isGeode && !isUncutGem) return;
        }

        if (toolType == LapidaryBenchRecipe.ToolType.CHISEL) {
            if (!isUncutGem) return;

            if (!DwarfTomeUnlockHelper.hasUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                player.displayClientMessage(
                        Component.translatable(JolCraftLanguageKeys.TOOLTIP_LAPIDARY_BENCH_CUT_GEMS_LOCKED)
                                .withStyle(ChatFormatting.RED),
                        true
                );
                player.closeContainer();
                return;
            }
        }

        var opt = player.serverLevel().recipeAccess().getRecipeFor(
                JolCraftRecipes.LAPIDARY_BENCH_TYPE.get(),
                new LapidaryRecipeInput(input, tool),
                level
        );
        if (opt.isEmpty()) return;

        LapidaryBenchRecipe recipe = opt.get().value();
        ItemStack rolled = recipe.rollResult(level.registryAccess(), level.random);
        if (rolled.isEmpty()) return;

        if (output.isEmpty()) {
            this.items.set(SLOT_OUTPUT, rolled.copy());
        } else if (ItemStack.isSameItemSameComponents(output, rolled)) {
            int maxSize = Math.min(output.getMaxStackSize(), this.getMaxStackSize());
            int space = maxSize - output.getCount();
            if (space <= 0) return;

            int add = Math.min(space, rolled.getCount());
            output.grow(add);
            this.items.set(SLOT_OUTPUT, output);

            if (add < rolled.getCount()) {
                ItemStack overflow = rolled.copy();
                overflow.setCount(rolled.getCount() - add);
                if (!player.getInventory().add(overflow)) player.drop(overflow, false);
            }
        } else {
            if (recipe.usesResultTag()) {
                ItemStack give = rolled.copy();
                if (!player.getInventory().add(give)) player.drop(give, false);
            } else {
                return;
            }
        }

        if (!player.isCreative()) {
            input.shrink(1);
            this.items.set(SLOT_INPUT, input.isEmpty() ? ItemStack.EMPTY : input);

            int damage;
            if (toolType == LapidaryBenchRecipe.ToolType.HAMMER) {
                if (isGeode) {
                    if (geodeSmall) damage = 1 + level.random.nextInt(10);
                    else if (geodeMedium) damage = 1 + level.random.nextInt(20);
                    else if (geodeLarge) damage = 1 + level.random.nextInt(30);
                    else damage = 1 + level.random.nextInt(10);
                } else {
                    damage = 1 + level.random.nextInt(10);
                }
            } else {
                damage = 1 + level.random.nextInt(50);
            }

            tool.hurtAndBreak(damage, player, EquipmentSlot.MAINHAND);
            this.items.set(SLOT_TOOL, tool.isEmpty() ? ItemStack.EMPTY : tool);
        }

        this.setChanged();

        if (isGeode) {
            player.awardStat(JolCraftStats.GEODES_CRACKED.get());
        }

        if (isUncutGem) {
            if (toolType == LapidaryBenchRecipe.ToolType.HAMMER) {
                player.awardStat(JolCraftStats.GEMS_CRUSHED.get());
            }

            if (toolType == LapidaryBenchRecipe.ToolType.CHISEL) {
                player.awardStat(JolCraftStats.GEMS_CUT.get());
            }
        }

        if (recipe.xp() > 0) {
            player.giveExperiencePoints(recipe.xp());
        }

        if (isGeode) {
            float pitch;
            if (geodeSmall) pitch = 1.3F;
            else if (geodeMedium) pitch = 1.0F;
            else if (geodeLarge) pitch = 0.8F;
            else pitch = 1.3F;

            JolCraftSoundHelper.player(
                    player,
                    SoundEvents.DEEPSLATE_BREAK,
                    1.3F,
                    pitch
            );
        }

        if (toolType == LapidaryBenchRecipe.ToolType.HAMMER) {
            JolCraftSoundHelper.player(
                    player,
                    SoundEvents.AMETHYST_BLOCK_BREAK,
                    0.8F,
                    1.5F
            );
        }

        if (toolType == LapidaryBenchRecipe.ToolType.CHISEL) {
            JolCraftSoundHelper.player(
                    player,
                    JolCraftSounds.GEM_CUT.get(),
                    1.0F,
                    1.9F
            );
        }

        JolCraftParticleHelper.spawn(
                level,
                ParticleTypes.CRIT,
                player.getX(),
                player.getY() + 1.1D,
                player.getZ(),
                (level.random.nextDouble() - 0.5D) * 0.24D,
                level.random.nextDouble() * 0.10D,
                (level.random.nextDouble() - 0.5D) * 0.24D
        );
    }

    public boolean isRecipeValid(ServerPlayer player) {
        Level level = this.getLevel();
        if (level == null || level.isClientSide) return false;

        ItemStack input = this.items.getFirst();
        ItemStack tool  = this.items.get(SLOT_TOOL);
        if (input.isEmpty() || tool.isEmpty()) return false;

        boolean isGeode = input.is(JolCraftTags.Items.GEODES);
        boolean isUncutGem = input.is(JolCraftTags.Items.GEMS_UNCUT);

        boolean isHammer = tool.is(JolCraftTags.Items.ARTISAN_HAMMERS);
        boolean isChisel = tool.is(JolCraftTags.Items.CHISELS);

        if (isHammer) {
            if (!isGeode && !isUncutGem) return false;
        } else if (isChisel) {
            if (!isUncutGem) return false;

            if (!DwarfTomeUnlockHelper.hasUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT)) {
                return false;
            }
        } else {
            return false;
        }

        LapidaryRecipeInput ri = new LapidaryRecipeInput(input, tool);
        var opt = player.serverLevel().recipeAccess().getRecipeFor(
                JolCraftRecipes.LAPIDARY_BENCH_TYPE.get(), ri, level
        );
        if (opt.isEmpty()) return false;

        LapidaryBenchRecipe recipe = opt.get().value();
        if (recipe.usesResultTag()) return true;

        ItemStack baseOut = recipe.assemble(ri, level.registryAccess());
        if (baseOut.isEmpty()) return false;

        ItemStack output = this.items.get(SLOT_OUTPUT);
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(output, baseOut)) return false;

        int maxSize = Math.min(output.getMaxStackSize(), this.getMaxStackSize());
        return (maxSize - output.getCount()) >= recipe.maxCount();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(JolCraftLanguageKeys.CONTAINER_LAPIDARY_BENCH);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new LapidaryBenchMenu(id, playerInventory, this);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }
}
