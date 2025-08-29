package net.sievert.jolcraft.gui.custom.lapidary_bench;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.gui.custom.slot.LapidarySlot;
import net.sievert.jolcraft.gui.JolCraftMenuTypes;
import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.world.SimpleContainer;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.util.attachment.TomeUnlockHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final SimpleContainer container;
    private final Level level;
    private final Player player;

    // Use your mod's registered MenuType here!
    public LapidaryBenchMenu(int windowId, Inventory playerInventory, ContainerLevelAccess access) {
        super(JolCraftMenuTypes.LAPIDARY_BENCH_MENU.get(), windowId); // Replace with your MenuType registry
        this.level = playerInventory.player.level();
        this.player = playerInventory.player;
        this.access = access;
        this.container = new SimpleContainer(1);

        // Add the single lapidary slot at (32, 16)
        this.addSlot(new LapidarySlot(container, 0, 32, 32));

        //Add player inventory + hotbar
        this.addStandardInventorySlots(playerInventory, 8, 68);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        int hammerDamage = 1;
        int chiselDamage = 1;
        RandomSource random = RandomSource.create();
        boolean isCreative = player.isCreative();
        ItemStack stack = getLapidarySlotItem();

        if (player.level().isClientSide) return false;

        if (stack.isEmpty()) return false;

        // Hammer button: 0
        if (buttonId == 0 && (hasGem() || hasGeode()) && hasHammer()) {
            // --- Handle geode opening ---
            if (hasGeode()) {
                int min, max, xpMin, xpMax;
                float breakPitch;

                if (stack.is(JolCraftItems.GEODE_SMALL.get())) {
                    min = 1; max = 2;
                    breakPitch = 1.3f;
                    xpMin = 1; xpMax = 2;
                    hammerDamage = 1 + random.nextInt(10); // 1–10
                } else if (stack.is(JolCraftItems.GEODE_MEDIUM.get())) {
                    min = 2; max = 3;
                    breakPitch = 1.0f;
                    xpMin = 2; xpMax = 4;
                    hammerDamage = 1 + random.nextInt(20); // 1–20
                } else if (stack.is(JolCraftItems.GEODE_LARGE.get())) {
                    min = 3; max = 5;
                    breakPitch = 0.8f;
                    xpMin = 3; xpMax = 7;
                    hammerDamage = 1 + random.nextInt(30); // 1–30
                } else {
                    min = 1; max = 1;
                    breakPitch = 1.3f;
                    xpMin = 1; xpMax = 1;
                }

                ItemStack reward = getRandomGemStack(player.getRandom());
                int amount = min + player.getRandom().nextInt(max - min + 1);
                reward.setCount(amount);

                if (!isCreative) {
                    stack.shrink(1);
                    if (stack.isEmpty()) this.slots.getFirst().set(ItemStack.EMPTY);
                    else this.slots.getFirst().setChanged();

                    // --- Hammer durability loss logic ---
                    ItemStack mainHand = player.getMainHandItem();
                    mainHand.hurtAndBreak(hammerDamage, player, EquipmentSlot.MAINHAND);
                }

                if (!player.getInventory().add(reward)) {
                    player.drop(reward, false);
                }

                if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                    if (player.getRandom().nextFloat() < 0.5f) {
                        int xp = xpMin + player.getRandom().nextInt(xpMax - xpMin + 1);
                        serverPlayer.giveExperiencePoints(xp);
                    }
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1.3F, breakPitch);

                this.access.execute((level, pos) -> {
                    spawnParticles(
                            player,
                            ParticleTypes.CRIT,
                            pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                            amount, 0.4, 0.3, 0.4, 0.12
                    );
                });

                return true;
            }

            // --- Handle gem hammering (if you want an effect) ---
            if (hasGem()) {
                Item gemItem = stack.getItem();
                Item dustItem = GEM_TO_DUST.get(gemItem);

                if (!isCreative) {
                    stack.shrink(1);
                    if (stack.isEmpty()) this.slots.getFirst().set(ItemStack.EMPTY);
                    else this.slots.getFirst().setChanged();

                    // --- Hammer durability loss logic ---
                    ItemStack mainHand = player.getMainHandItem();
                    mainHand.hurtAndBreak(1 + random.nextInt(10), player, EquipmentSlot.MAINHAND);

                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 0.8F, 1.5F);

                this.access.execute((level, pos) -> {
                    spawnParticles(
                            player,
                            ParticleTypes.CRIT,
                            pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                            1 + player.getRandom().nextInt(2), 0.4, 0.3, 0.4, 0.12
                    );
                });

                // Give dust if mapping exists
                if (dustItem != null) {
                    ItemStack dust = new ItemStack(dustItem, 1 + random.nextInt(3));
                    if (!player.getInventory().add(dust)) {
                        player.drop(dust, false);
                    }
                }
                return true;
            }
        }

        // Chisel button: 1 (only for gems)
        if (buttonId == 1 && hasGem() && hasChisel()) {
            // First, check if the player has the "Cutting Gems" unlock
            if (!TomeUnlockHelper.hasUnlock(player, TomeUnlockHelper.CUTTING_GEMS)) {
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.lapidary_bench.locked_cut_gems").withStyle(ChatFormatting.RED),
                        true
                );
                return true;
            }

            Item gemItem = stack.getItem();
            Item cutItem = GEM_TO_CUT.get(gemItem);

            if (!isCreative) {
                stack.shrink(1);
                if (stack.isEmpty()) this.slots.getFirst().set(ItemStack.EMPTY);
                else this.slots.getFirst().setChanged();

                // --- Chisel durability loss logic ---
                ItemStack mainHand = player.getMainHandItem();
                mainHand.hurtAndBreak(1 + random.nextInt(50), player, EquipmentSlot.MAINHAND);

            }

            if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                if (player.getRandom().nextFloat() < 0.5f) {
                    int xp = 3 + player.getRandom().nextInt(10);
                    serverPlayer.giveExperiencePoints(xp);
                }
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    JolCraftSounds.GEM_CUT.get(), SoundSource.BLOCKS, 1.0F, 1.9F);

            this.access.execute((level, pos) -> {
                spawnParticles(
                        player,
                        ParticleTypes.CRIT,
                        pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                        1 + player.getRandom().nextInt(1), 0.4, 0.3, 0.4, 0.12
                );
            });

            if (cutItem != null) {
                ItemStack cut = new ItemStack(cutItem, 1 + random.nextInt(2));
                if (!player.getInventory().add(cut)) {
                    player.drop(cut, false);
                }
            }

            return true;
        }

        return false;
    }


    // Used by NeoForge's auto-GUI opening
    public LapidaryBenchMenu(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, ContainerLevelAccess.NULL);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, JolCraftBlocks.LAPIDARY_BENCH.get());
    }

    // Shift-click (quick move) handling
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 0) {
                // Moving from lapidary slot to player inventory
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true))
                    return ItemStack.EMPTY;
            } else {
                // Moving from player inventory to lapidary slot
                if (!this.slots.getFirst().mayPlace(stackInSlot) || !this.moveItemStackTo(stackInSlot, 0, 1, false))
                    return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((p_39371_, p_39372_) -> this.clearContainer(player, this.container));
    }

    public ItemStack getLapidarySlotItem() {
        Slot lapidarySlot = this.slots.getFirst();
        return lapidarySlot.getItem();
    }

    public boolean hasGem() {
        ItemStack stack = getLapidarySlotItem();
        return !stack.isEmpty() && stack.is(JolCraftTags.Items.GEMS_UNCUT);
    }

    public boolean hasGeode() {
        ItemStack stack = getLapidarySlotItem();
        return !stack.isEmpty() && stack.is(net.sievert.jolcraft.data.JolCraftTags.Items.GEODES);
    }

    public boolean hasTool() {
        ItemStack playerhelditem = player.getMainHandItem();
        return playerhelditem.is(JolCraftTags.Items.ARTISAN_HAMMERS) || playerhelditem.is(JolCraftTags.Items.CHISELS);
    }

    public boolean hasHammer() {
        ItemStack playerhelditem = player.getMainHandItem();
        return playerhelditem.is(JolCraftTags.Items.ARTISAN_HAMMERS);
    }

    public boolean hasChisel() {
        ItemStack playerhelditem = player.getMainHandItem();
        return playerhelditem.is(JolCraftTags.Items.CHISELS);
    }

    private static final ItemStack[] ALL_GEMS = {
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.AEGISCORE.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.ASHFANG.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.DEEPMARROW.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.EARTHBLOOD.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.EMBERGLASS.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.FROSTVEIN.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.GRIMSTONE.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.IRONHEART.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.LUMIERE.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.MOONSHARD.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.RUSTAGATE.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.SKYBURROW.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.SUNGLEAM.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.VERDANITE.get()),
            new ItemStack(net.sievert.jolcraft.item.JolCraftItems.WOECRYSTAL.get())
    };

    private ItemStack getRandomGemStack(RandomSource random) {
        return ALL_GEMS[random.nextInt(ALL_GEMS.length)].copy();
    }

    private static final Map<Item, Item> GEM_TO_DUST = new HashMap<>();
    static {
        GEM_TO_DUST.put(JolCraftItems.AEGISCORE.get(),    JolCraftItems.AEGISCORE_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.ASHFANG.get(),      JolCraftItems.ASHFANG_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.DEEPMARROW.get(),   JolCraftItems.DEEPMARROW_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.EARTHBLOOD.get(),   JolCraftItems.EARTHBLOOD_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.EMBERGLASS.get(),   JolCraftItems.EMBERGLASS_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.FROSTVEIN.get(),    JolCraftItems.FROSTVEIN_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.GRIMSTONE.get(),    JolCraftItems.GRIMSTONE_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.IRONHEART.get(),    JolCraftItems.IRONHEART_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.LUMIERE.get(),      JolCraftItems.LUMIERE_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.MOONSHARD.get(),    JolCraftItems.MOONSHARD_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.RUSTAGATE.get(),    JolCraftItems.RUSTAGATE_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.SKYBURROW.get(),    JolCraftItems.SKYBURROW_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.SUNGLEAM.get(),     JolCraftItems.SUNGLEAM_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.VERDANITE.get(),    JolCraftItems.VERDANITE_DUST.get());
        GEM_TO_DUST.put(JolCraftItems.WOECRYSTAL.get(),   JolCraftItems.WOECRYSTAL_DUST.get());
    }

    private static final Map<Item, Item> GEM_TO_CUT = new HashMap<>();
    static {
        GEM_TO_CUT.put(JolCraftItems.AEGISCORE.get(),    JolCraftItems.AEGISCORE_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.ASHFANG.get(),      JolCraftItems.ASHFANG_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.DEEPMARROW.get(),   JolCraftItems.DEEPMARROW_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.EARTHBLOOD.get(),   JolCraftItems.EARTHBLOOD_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.EMBERGLASS.get(),   JolCraftItems.EMBERGLASS_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.FROSTVEIN.get(),    JolCraftItems.FROSTVEIN_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.GRIMSTONE.get(),    JolCraftItems.GRIMSTONE_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.IRONHEART.get(),    JolCraftItems.IRONHEART_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.LUMIERE.get(),      JolCraftItems.LUMIERE_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.MOONSHARD.get(),    JolCraftItems.MOONSHARD_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.RUSTAGATE.get(),    JolCraftItems.RUSTAGATE_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.SKYBURROW.get(),    JolCraftItems.SKYBURROW_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.SUNGLEAM.get(),     JolCraftItems.SUNGLEAM_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.VERDANITE.get(),    JolCraftItems.VERDANITE_CUT.get());
        GEM_TO_CUT.put(JolCraftItems.WOECRYSTAL.get(),   JolCraftItems.WOECRYSTAL_CUT.get());
    }

    public static void spawnParticles(Player player, ParticleOptions particle,
                                      double x, double y, double z, int count,
                                      double xOffset, double yOffset, double zOffset, double speed) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    particle, x, y, z, count, xOffset, yOffset, zOffset, speed
            );
        }
    }







}
