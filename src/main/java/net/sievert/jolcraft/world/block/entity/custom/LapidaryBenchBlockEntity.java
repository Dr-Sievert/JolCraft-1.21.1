package net.sievert.jolcraft.world.block.entity.custom;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sievert.jolcraft.world.player.JolCraftStats;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryRecipeInput;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.world.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.gui.menu.LapidaryBenchMenu;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchBlockEntity extends BaseContainerBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int SLOT_COUNT = 3;
    private boolean recipeValid;
    private int actionId = -1;

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public LapidaryBenchBlockEntity(BlockPos pos, BlockState state) {
        super(JolCraftBlockEntities.LAPIDARY_BENCH.get(), pos, state);
    }

    // ---------------------------------------------------------------------
    // ACTION
    // ---------------------------------------------------------------------

    public void handleAction(ServerPlayer player) {
        Level level = player.level();
        if (level.isClientSide) return;

        Optional<Resolved> resolved = resolveValidRecipe(player);
        if (resolved.isEmpty()) return;

        Resolved r = resolved.get();

        JolCraftItemInsertionHelper.tryInsertIntoSlotInventoryOrDrop(
                this,
                SLOT_OUTPUT,
                player.getInventory(),
                player,
                r.result
        );

        if (!player.isCreative()) {
            consumeAndDamage(player, r.recipe);
        }

        this.setChanged();
        refreshCachedState(player);

        if (r.wasGeode) {
            player.awardStat(JolCraftStats.GEODES_CRACKED.get());
        }
        if (r.wasUncutGem) {
            if (r.wasHammer) {
                player.awardStat(JolCraftStats.GEMS_CRUSHED.get());
            }
            if (r.wasChisel) {
                player.awardStat(JolCraftStats.GEMS_CUT.get());
            }
        }

        int xp = r.recipe.xp().roll(level.random);
        if (xp > 0) {
            player.giveExperiencePoints(xp);
        }

        SoundOutput sound = r.recipe.sound();
        JolCraftSoundHelper.player(
                player,
                Objects.requireNonNull(sound.resolveValue(player.registryAccess())),
                sound.volume(),
                sound.pitch()
        );

        JolCraftParticleHelper.spawn(
                level,
                ParticleTypes.CRIT,
                player.getX(),
                player.getY() + 1.1D,
                player.getZ(),
                1,
                (level.random.nextDouble() - 0.5D) * 0.24D,
                level.random.nextDouble() * 0.10D,
                (level.random.nextDouble() - 0.5D) * 0.24D,
                0.0D
        );
    }

    // ---------------------------------------------------------------------
    // MENU SYNC
    // ---------------------------------------------------------------------

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> recipeValid ? 1 : 0;
                case 1 -> actionId;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> recipeValid = value != 0;
                case 1 -> actionId = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ContainerData getContainerData() {
        return this.containerData;
    }


    // ---------------------------------------------------------------------
    // VALIDATION / UI GATING
    // ---------------------------------------------------------------------

    public void refreshCachedState(ServerPlayer player) {
        int nextActionId = computeActionId();
        boolean nextRecipeValid = resolveValidRecipe(player).isPresent();

        if (this.actionId == nextActionId && this.recipeValid == nextRecipeValid) {
            return;
        }

        this.actionId = nextActionId;
        this.recipeValid = nextRecipeValid;
        setChanged();
    }

    private int computeActionId() {
        ItemStack tool = this.items.get(SLOT_TOOL);

        if (tool.is(JolCraftTags.Items.ARTISAN_HAMMERS)) return 0;
        if (tool.is(JolCraftTags.Items.CHISELS)) return 1;

        return -1;
    }

    // ---------------------------------------------------------------------
    // INTERNALS
    // ---------------------------------------------------------------------

    private record Resolved(
            LapidaryBenchRecipe recipe,
            ItemStack result,
            boolean wasGeode,
            boolean wasUncutGem,
            boolean wasHammer,
            boolean wasChisel
    ) {}

    private Optional<Resolved> resolveValidRecipe(ServerPlayer player) {
        Level level = this.getLevel();
        if (level == null || level.isClientSide) return Optional.empty();

        ItemStack input = this.items.getFirst();
        ItemStack tool = this.items.get(SLOT_TOOL);
        if (input.isEmpty() || tool.isEmpty()) return Optional.empty();

        WorldContext ctx = new WorldContext(
                player,
                null
        );

        LapidaryRecipeInput in = new LapidaryRecipeInput(ctx, input, tool);

        var opt = player.serverLevel().getRecipeManager().getRecipeFor(
                JolCraftRecipes.LAPIDARY_BENCH_TYPE.get(),
                in,
                level
        );

        if (opt.isEmpty()) return Optional.empty();

        LapidaryBenchRecipe recipe = opt.get().value();
        if (!recipe.matches(in, level)) return Optional.empty();

        ItemStack result = recipe.assemble(in, level.registryAccess());
        if (result.isEmpty()) return Optional.empty();

        boolean wasGeode = input.is(JolCraftTags.Items.GEODES);
        boolean wasUncutGem = input.is(JolCraftTags.Items.GEMS_UNCUT);
        boolean wasHammer = tool.is(JolCraftTags.Items.ARTISAN_HAMMERS);
        boolean wasChisel = tool.is(JolCraftTags.Items.CHISELS);

        return Optional.of(new Resolved(recipe, result, wasGeode, wasUncutGem, wasHammer, wasChisel));
    }

    private void consumeAndDamage(ServerPlayer player, LapidaryBenchRecipe recipe) {
        Level level = this.getLevel();
        if (level == null || level.isClientSide) return;

        ItemStack input = this.items.getFirst();
        ItemStack tool = this.items.get(SLOT_TOOL);

        input.shrink(1);
        this.items.set(SLOT_INPUT, input.isEmpty() ? ItemStack.EMPTY : input);

        int damage = recipe.toolDamage().roll(level.random);
        if (damage > 0 && !tool.isEmpty()) {
            tool.hurtAndBreak(
                    damage,
                    player.serverLevel(),
                    player,
                    brokenItem -> player.serverLevel().playSound(
                            null,
                            this.worldPosition,
                            brokenItem.getBreakingSound(),
                            player.getSoundSource(),
                            1.0F,
                            1.0F
                    )
            );
        }

        this.items.set(SLOT_TOOL, tool.isEmpty() ? ItemStack.EMPTY : tool);
    }

    // ---------------------------------------------------------------------
    // VANILLA CONTAINER
    // ---------------------------------------------------------------------

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