package net.sievert.jolcraft.event.game.world.player.attribute;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class JolCraftPlayerAttributeEventsHelper {

    private static final float LUMINANCE_DAMAGE_PER_LIGHT_LEVEL = 0.05F;

    private static final Map<UUID, PendingLuminanceCritical> PENDING_LUMINANCE_CRITICALS = new HashMap<>();
    private static final Map<UUID, PendingChestLoot> CHEST_LOOT_TO_REROLL = new HashMap<>();
    private static final Map<UUID, Double> ITEM_USE_SPEED_PROGRESS = new HashMap<>();

    private JolCraftPlayerAttributeEventsHelper() {}

    public static void clearPlayerTracking(UUID uuid) {
        PENDING_LUMINANCE_CRITICALS.remove(uuid);
        CHEST_LOOT_TO_REROLL.remove(uuid);
        ITEM_USE_SPEED_PROGRESS.remove(uuid);
    }

    public static void applyXpIncrease(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();

        double boost = player.getAttributeValue(JolCraftAttributes.EXPERIENCE_INCREASE);
        if (boost <= 0.0D) return;

        int baseAmount = event.getAmount();
        double raw = baseAmount * boost;
        int bonus = (int) raw;
        if (player.getRandom().nextDouble() < (raw - bonus)) bonus++;

        if (bonus <= 0) return;

        event.setAmount(baseAmount + bonus);

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "XP boosted: player={}, base={}, bonus={}, boost={}%",
                player.getDisplayName().getString(),
                baseAmount,
                bonus,
                JolCraftLogs.pct1(boost)
        );
    }

    public static void applyLuminanceCritical(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        UUID uuid = player.getUUID();
        PENDING_LUMINANCE_CRITICALS.remove(uuid);

        if (!event.isCriticalHit() || !(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        int luminanceLevel = Mth.floor(player.getAttributeValue(JolCraftAttributes.LUMINANCE));
        if (luminanceLevel <= 0) return;

        int lightLevel = player.level().getMaxLocalRawBrightness(player.blockPosition());
        if (lightLevel <= 0) return;

        float bonusDamage = LUMINANCE_DAMAGE_PER_LIGHT_LEVEL * lightLevel * luminanceLevel;

        PENDING_LUMINANCE_CRITICALS.put(
                uuid,
                new PendingLuminanceCritical(
                        target.getId(),
                        player.level().getGameTime(),
                        luminanceLevel,
                        bonusDamage
                )
        );

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Luminance critical primed: player={}, target={}, luminance={}, light={}, bonusDmg={}, glowing={}s",
                player.getDisplayName().getString(),
                target.getDisplayName().getString(),
                luminanceLevel,
                lightLevel,
                bonusDamage,
                luminanceLevel
        );
    }

    public static void applyLuminanceCriticalDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        PendingLuminanceCritical pending = PENDING_LUMINANCE_CRITICALS.remove(player.getUUID());
        if (pending == null
                || pending.targetId() != event.getEntity().getId()
                || pending.gameTime() != event.getEntity().level().getGameTime()) {
            return;
        }

        event.getEntity().addEffect(new MobEffectInstance(
                MobEffects.GLOWING,
                20 * pending.luminanceLevel()
        ));

        float originalDamage = event.getAmount();
        event.setAmount(originalDamage + pending.bonusDamage());

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Luminance critical damage applied: player={}, target={}, original={}, bonus={}, new={}",
                player.getDisplayName().getString(),
                event.getEntity().getDisplayName().getString(),
                originalDamage,
                pending.bonusDamage(),
                event.getAmount()
        );
    }

    public static void trackChestLoot(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        Player player = event.getEntity();
        BlockPos pos = event.getPos();

        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof RandomizableContainerBlockEntity lootable)) return;

        ResourceKey<LootTable> lootTable = lootable.getLootTable();
        if (lootTable == null) return;

        CHEST_LOOT_TO_REROLL.put(
                player.getUUID(),
                new PendingChestLoot(serverLevel.dimension(), pos.immutable(), lootTable)
        );
    }

    public static void applyChestLootIncrease(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        PendingChestLoot pending = CHEST_LOOT_TO_REROLL.get(player.getUUID());
        if (pending == null) return;

        if (pending.dim() != serverLevel.dimension()) {
            CHEST_LOOT_TO_REROLL.remove(player.getUUID());
            return;
        }

        AbstractContainerMenu menu = event.getContainer();

        Set<RandomizableContainerBlockEntity> seen = new HashSet<>();
        for (Slot slot : menu.slots) {
            if (!(slot.container instanceof RandomizableContainerBlockEntity lootable)) continue;
            if (!seen.add(lootable)) continue;

            if (!lootable.getBlockPos().equals(pending.pos())) continue;

            if (lootable.getLootTable() == null) {
                int addedCount = addChestLoot(player, serverLevel, lootable, pending);
                double chance = player.getAttributeValue(JolCraftAttributes.CONTAINER_LOOT_INCREASE);

                JolCraftLogs.debug(
                        JolCraftLogTags.PLAYER,
                        "Chest loot added: player={}, chestPos={}, chance={}%, addedCount={}",
                        player.getDisplayName().getString(),
                        pending.pos(),
                        JolCraftLogs.pct1(chance),
                        addedCount
                );

                CHEST_LOOT_TO_REROLL.remove(player.getUUID());
            }

            return;
        }

        CHEST_LOOT_TO_REROLL.remove(player.getUUID());
    }

    private static int addChestLoot(
            Player player,
            ServerLevel serverLevel,
            RandomizableContainerBlockEntity lootable,
            PendingChestLoot pending
    ) {
        double chance = player.getAttributeValue(JolCraftAttributes.CONTAINER_LOOT_INCREASE);
        MinecraftServer server = serverLevel.getServer();
        LootTable table = server.reloadableRegistries().getLootTable(pending.table());

        int addedCount = 0;

        for (int i = 0; i < lootable.getContainerSize(); ++i) {
            if (!lootable.getItem(i).isEmpty()) continue;
            if (serverLevel.random.nextDouble() >= chance) continue;

            LootParams params = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(lootable.getBlockPos()))
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.CHEST);

            for (ItemStack rolled : table.getRandomItems(params)) {
                if (!rolled.isEmpty()) {
                    lootable.setItem(i, rolled.copy());
                    addedCount++;
                    break;
                }
            }
        }

        return addedCount;
    }

    public static void clearChestLootTracking(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        CHEST_LOOT_TO_REROLL.remove(player.getUUID());
    }

    @SuppressWarnings("deprecation")
    public static void applyCropLootIncrease(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Player player = event.getPlayer();
        if(player.isCreative()) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        double chance = Mth.clamp(player.getAttributeValue(JolCraftAttributes.CROP_LOOT_INCREASE), 0.0D, 1.0D);
        if (chance <= 0.0D) return;

        if (!isEligibleHarvestBlock(state)) return;

        List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, player.getMainHandItem());

        int extraCount = 0;
        Set<ResourceLocation> extraItems = new HashSet<>();

        for (ItemStack stack : drops) {
            if (!isEligibleCropDrop(stack)) continue;

            if (level.random.nextDouble() < chance) {
                Block.popResource(level, pos, stack.copyWithCount(1));
                extraCount++;
                stack.getItem().builtInRegistryHolder().unwrapKey().ifPresent(key -> extraItems.add(key.location()));
            }
        }

        if (extraCount > 0) {
            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Player {} got extra {} crop(s) ({}) at {} in {}. Chance: {}%. ",
                    player.getDisplayName().getString(),
                    extraCount,
                    extraItems,
                    JolCraftLogs.roundedPos(pos),
                    player.level().dimension().location(),
                    JolCraftLogs.pct1(chance)

            );
        }
    }

    private static boolean isEligibleCropDrop(ItemStack stack) {
        return !stack.isEmpty() && stack.is(Tags.Items.CROPS);
    }

    private static boolean isEligibleHarvestBlock(BlockState state) {
        return isFullyGrownCrop(state) || state.is(Blocks.MELON);
    }

    private static boolean isFullyGrownCrop(BlockState state) {
        Block block = state.getBlock();

        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }

        IntegerProperty ageProp = null;
        for (Property<?> prop : state.getProperties()) {
            if (prop instanceof IntegerProperty ip && prop.getName().equals(JolCraftDictionary.AGE)) {
                ageProp = ip;
                break;
            }
        }

        if (ageProp == null) return false;

        int age = state.getValue(ageProp);
        int maxAge = ageProp.getPossibleValues().stream().max(Integer::compareTo).orElse(age);
        return age >= maxAge;
    }

    public static void applyItemUseSpeedStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack stack = event.getItem();
        if (!isFoodOrDrink(player, stack)) return;

        double speed = getItemUseSpeed(player);
        if (speed <= 0.0D) return;

        int baseDuration = event.getDuration();
        int adjustedDuration = getAdjustedDuration(baseDuration, speed);
        event.setDuration(adjustedDuration);

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Item use speed applied: player={}, item={}, total speed={}%, baseTicks={}, adjustedTicks={}",
                player.getDisplayName().getString(),
                stack.getHoverName().getString(),
                JolCraftLogs.pct1(1.0D + speed),
                baseDuration,
                adjustedDuration
        );
    }

    public static void applyItemUseSpeedTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        ItemStack stack = event.getItem();
        UUID uuid = player.getUUID();

        if (!usesTickBasedAcceleration(stack)) {
            ITEM_USE_SPEED_PROGRESS.remove(uuid);
            return;
        }

        double speed = getItemUseSpeed(player);
        if (speed <= 0.0D) {
            ITEM_USE_SPEED_PROGRESS.remove(uuid);
            return;
        }

        accelerateTickUseDuration(event, uuid, speed);
    }

    public static void stopItemUseSpeed(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        ItemStack stack = event.getItem();
        UUID uuid = player.getUUID();

        if (!isFoodOrDrink(player, stack) &&
                !usesTickBasedAcceleration(stack) &&
                !(stack.getItem() instanceof BrushItem)) {
            ITEM_USE_SPEED_PROGRESS.remove(uuid);
            return;
        }

        double speed = getItemUseSpeed(player);
        if (speed > 0.0D) {
            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Item use speed applied: player={}, item={}, total speed={}%",
                    player.getDisplayName().getString(),
                    stack.getHoverName().getString(),
                    JolCraftLogs.pct1(1.0D + speed)
            );
        }

        ITEM_USE_SPEED_PROGRESS.remove(uuid);
    }

    public static void finishItemUseSpeed(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player
                && !player.level().isClientSide()) {
            ITEM_USE_SPEED_PROGRESS.remove(player.getUUID());
        }
    }

    private static void accelerateTickUseDuration(LivingEntityUseItemEvent.Tick event, UUID uuid, double speed) {
        double progress = ITEM_USE_SPEED_PROGRESS.getOrDefault(uuid, 0.0D) + speed;
        int extraReduction = Mth.floor(progress);

        if (extraReduction > 0) {
            progress -= extraReduction;
            event.setDuration(Math.max(1, event.getDuration() - extraReduction));
        }

        ITEM_USE_SPEED_PROGRESS.put(uuid, progress);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean usesTickBasedAcceleration(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem;
    }

    private static double getItemUseSpeed(Player player) {
        return Mth.clamp(player.getAttributeValue(JolCraftAttributes.ITEM_USE_SPEED), 0.0D, 1.0D);
    }

    private static int getAdjustedDuration(int baseDuration, double speed) {
        return Math.max(1, (int) Math.round(baseDuration / (1.0D + speed)));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isFoodOrDrink(Player player, ItemStack stack) {
        return (stack.getFoodProperties(player) != null || stack.getItem() instanceof PotionItem)
                && (stack.getUseAnimation() == UseAnim.DRINK || stack.getUseAnimation() == UseAnim.EAT);
    }

    private record PendingLuminanceCritical(int targetId, long gameTime, int luminanceLevel, float bonusDamage) {}

    private record PendingChestLoot(ResourceKey<Level> dim, BlockPos pos, ResourceKey<LootTable> table) {}
}