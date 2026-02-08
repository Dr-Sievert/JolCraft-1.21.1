package net.sievert.jolcraft.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.data.attachment.custom.player.AttributesAttachment;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.object.RadiantEntity;

import java.util.*;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftAttributeEvents {

    private static final ResourceLocation ASHFANG_ID = JolCraft.location("ashfang_attack_damage_increase");
    private static final ResourceLocation IRONHEART_ID = JolCraft.location("ironheart_armor_increase");
    private static final ResourceLocation FROSTVEIN_ID = JolCraft.location("frostvein_slow_resist");
    private static final ResourceLocation SKYBURROW_ID = JolCraft.location("skyburrow_day_speed");
    private static final ResourceLocation MOONSHARD_ID = JolCraft.location("moonshard_night_speed");

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        fullMarkDirty(event);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) cleanupPlayerState(sp);
        fullMarkDirty(event);
    }

    private static void fullMarkDirty(PlayerEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.getData(JolCraftAttachments.ATTRIBUTES.get()).markDirtyAll();
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) cleanupPlayerState(sp);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) cleanupPlayerState(sp);
    }

    private static void cleanupPlayerState(ServerPlayer sp) {
        UUID uuid = sp.getUUID();
        LAST_SLOWNESS_AMP.remove(uuid);
        CHEST_LOOT_TO_REROLL.remove(uuid);
        RadiantEntity existing = ACTIVE_RADIANT_ENTITIES.remove(uuid);
        if (existing != null && !existing.isRemoved()) {
            existing.discard();
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        AttributesAttachment attrs = player.getData(JolCraftAttachments.ATTRIBUTES.get());
        EquipmentSlot slot = event.getSlot();

        var keys = AttributesAttachment.getRefreshKeysForStack(event.getFrom(), slot);
        keys.addAll(AttributesAttachment.getRefreshKeysForStack(event.getTo(), slot));

        for (AttributesAttachment.RefreshKey key : keys) {
            attrs.markDirty(key);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        AttributesAttachment attrs = player.getData(JolCraftAttachments.ATTRIBUTES.get());

        trackFrostvein(player, attrs);

        EnumSet<AttributesAttachment.RefreshKey> pending = attrs.consumePending();
        if (pending.isEmpty()) return;

        if (pending.contains(AttributesAttachment.RefreshKey.FULL)) {
            refreshAttributes(player);
            return;
        }

        for (AttributesAttachment.RefreshKey key : pending) {
            switch (key) {
                case ASHFANG -> refreshAshfangAttribute(player);
                case IRONHEART -> refreshIronheartAttribute(player);
                case FROSTVEIN -> refreshFrostveinAttribute(player);
                case SKYBURROW -> refreshSkyburrowAttribute(player);
                case MOONSHARD -> refreshMoonshardAttribute(player);
                case FULL -> { /* handled above */ }
            }
        }
    }

    private static void refreshAttributes(ServerPlayer player) {
        refreshAshfangAttribute(player);
        refreshIronheartAttribute(player);
        refreshFrostveinAttribute(player);
        refreshSkyburrowAttribute(player);
        refreshMoonshardAttribute(player);
    }

    private static void refreshAshfangAttribute(ServerPlayer player) {
        AttributeInstance vanillaAttackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (vanillaAttackDamage == null) return;
        vanillaAttackDamage.removeModifier(ASHFANG_ID);
        double boost = player.getAttributeValue(JolCraftAttributes.ATTACK_DAMAGE_INCREASE);
        if (boost <= 0.0D) return;
        vanillaAttackDamage.addTransientModifier(new AttributeModifier(
                ASHFANG_ID,
                boost,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));
    }

    private static void refreshIronheartAttribute(ServerPlayer player) {
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor == null) return;

        armor.removeModifier(IRONHEART_ID);

        double percent = player.getAttributeValue(JolCraftAttributes.ARMOR_INCREASE);
        if (percent <= 0.0D) return;

        double baseArmor = armor.getBaseValue();
        for (AttributeModifier mod : armor.getModifiers()) {
            if (mod.operation() == AttributeModifier.Operation.ADD_VALUE) {
                baseArmor += mod.amount();
            }
        }

        double bonus = baseArmor * percent;
        if (bonus <= 0.0D) return;

        armor.addTransientModifier(new AttributeModifier(
                IRONHEART_ID, bonus, AttributeModifier.Operation.ADD_VALUE
        ));
    }


    private static void refreshFrostveinAttribute(ServerPlayer player) {
        var speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        speed.removeModifier(FROSTVEIN_ID);

        double resist = player.getAttributeValue(JolCraftAttributes.SLOW_RESISTANCE);
        if (resist <= 0.0D) return;

        resist = Math.max(0.0D, Math.min(1.0D, resist));

        MobEffectInstance slow = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (slow == null) return;

        int amp = slow.getAmplifier();

        double slowAmount = -0.15D * (amp + 1);

        double vanillaMultiplier = 1.0D + slowAmount;
        if (vanillaMultiplier <= 0.0D) return;

        double desiredSlowAmount = slowAmount * (1.0D - resist);
        double desiredMultiplier = 1.0D + desiredSlowAmount;

        double extra = (desiredMultiplier / vanillaMultiplier) - 1.0D;
        if (extra <= 0.0D) return;

        speed.addTransientModifier(new AttributeModifier(
                FROSTVEIN_ID,
                extra,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));
    }

    private static final Map<UUID, Integer> LAST_SLOWNESS_AMP = new HashMap<>();

    private static int getSlownessAmp(ServerPlayer player) {
        MobEffectInstance slow = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        return slow != null ? slow.getAmplifier() : -1;
    }

    private static void trackFrostvein(ServerPlayer player, AttributesAttachment attrs) {
        double resist = player.getAttributeValue(JolCraftAttributes.SLOW_RESISTANCE);
        int amp = getSlownessAmp(player);

        if (resist <= 0.0D && amp < 0) {
            LAST_SLOWNESS_AMP.remove(player.getUUID());
            return;
        }

        Integer prev = LAST_SLOWNESS_AMP.put(player.getUUID(), amp);
        if (prev == null || prev != amp) {
            attrs.markDirty(AttributesAttachment.RefreshKey.FROSTVEIN);
        }
    }

    private static void refreshSkyburrowAttribute(ServerPlayer player) {
        refreshTimeBasedSpeedAttribute(
                player,
                SKYBURROW_ID,
                player.getAttributeValue(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY),
                player.level().isDay()
        );
    }

    private static void refreshMoonshardAttribute(ServerPlayer player) {
        refreshTimeBasedSpeedAttribute(
                player,
                MOONSHARD_ID,
                player.getAttributeValue(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT),
                !player.level().isDay()
        );
    }

    private static void refreshTimeBasedSpeedAttribute(
            ServerPlayer player,
            ResourceLocation modifierId,
            double boost,
            boolean shouldApply
    ) {
        var speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        speed.removeModifier(modifierId);

        if (!shouldApply) return;
        if (boost <= 0.0D) return;

        speed.addTransientModifier(new AttributeModifier(
                modifierId,
                boost,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));
    }

    private static final Map<ResourceKey<Level>, Boolean> LAST_IS_DAY = new HashMap<>();

    @SubscribeEvent
    public static void onDayNightBoundaryTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ResourceKey<Level> dim = level.dimension();
        boolean isDay = level.isDay();

        Boolean prev = LAST_IS_DAY.put(dim, isDay);
        if (prev == null || prev == isDay) return;

        for (ServerPlayer player : level.players()) {
            AttributesAttachment attrs = player.getData(JolCraftAttachments.ATTRIBUTES.get());
            attrs.markDirty(AttributesAttachment.RefreshKey.SKYBURROW);
            attrs.markDirty(AttributesAttachment.RefreshKey.MOONSHARD);
        }
    }

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        double boost = player.getAttributeValue(JolCraftAttributes.XP_BOOST);
        if (boost > 0) {
            int baseAmount = event.getAmount();
            double raw = baseAmount * boost;
            int bonus = (int) raw;
            if (player.getRandom().nextDouble() < (raw - bonus)) bonus++;
            event.setAmount(baseAmount + bonus);
        }
    }

    public static final Map<UUID, RadiantEntity> ACTIVE_RADIANT_ENTITIES = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerRadiantTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        UUID uuid = player.getUUID();

        double radiant = player.getAttributeValue(JolCraftAttributes.RADIANT);
        int pieces = (int) Math.round(radiant * 4.0);
        int lightLevel = switch (pieces) {
            case 1 -> 9;
            case 2 -> 11;
            case 3 -> 13;
            case 4 -> 15;
            default -> 0;
        };

        RadiantEntity existing = ACTIVE_RADIANT_ENTITIES.get(uuid);
        if (existing != null && (existing.isRemoved() || existing.level() != level)) {
            ACTIVE_RADIANT_ENTITIES.remove(uuid);
            existing = null;
        }

        // No radiant -> discard entity
        if (lightLevel == 0) {
            if (existing != null) {
                existing.discard();
                ACTIVE_RADIANT_ENTITIES.remove(uuid);
            }
            return;
        }

        // Ensure we have a valid tracked radiant (recover if map desynced).
        if (existing == null) {
            RadiantEntity found = null;
            for (RadiantEntity e : level.getEntitiesOfClass(RadiantEntity.class, player.getBoundingBox().inflate(64.0))) {
                if (!e.isRemoved() && uuid.equals(e.getOwnerUUID()) && e.level() == level) {
                    found = e;
                    break;
                }
            }

            if (found != null) {
                existing = found;
                ACTIVE_RADIANT_ENTITIES.put(uuid, existing);
            } else {
                RadiantEntity created = new RadiantEntity(JolCraftEntities.RADIANT.get(), level);
                BlockPos spawnPos = player.blockPosition().above();
                created.moveTo(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5);
                created.setOwner(player);
                created.setRadiantLightLevel(lightLevel);

                // Only cache if spawn succeeded (prevents rapid spawn/remove loop)
                if (!level.addFreshEntity(created)) return;

                existing = created;
                ACTIVE_RADIANT_ENTITIES.put(uuid, existing);
            }
        }

        // Sync light level; entity itself handles follow/cooldown/teleport.
        existing.setOwner(player);
        existing.setRadiantLightLevel(lightLevel);
    }

    @SubscribeEvent
    public static void onLevelTickRadiantAura(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if ((level.getGameTime() % 10L) != 0L) return;

        Iterator<Map.Entry<UUID, RadiantEntity>> it = ACTIVE_RADIANT_ENTITIES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, RadiantEntity> entry = it.next();
            RadiantEntity radiant = entry.getValue();

            if (radiant == null || radiant.isRemoved() || radiant.level() != level) {
                it.remove();
                continue;
            }

            Entity ownerEntity = radiant.getOwner();
            if (!(ownerEntity instanceof Player owner)) continue;

            double radiantAttr = owner.getAttributeValue(JolCraftAttributes.RADIANT);
            if (radiantAttr < 0.25) continue;

            int percent = (int) (radiantAttr * 100);
            int nearest25 = (percent / 25) * 25;

            int radius = 1 + (nearest25 / 25);
            int amplifier = (nearest25 / 25) - 1;

            var box = radiant.getBoundingBox().inflate(radius, 4.0D, radius);
            for (Player player : level.getEntitiesOfClass(Player.class, box)) {
                double dy = radiant.getY() - player.getY();
                if (dy < 0 || dy > 4) continue;

                double dx = radiant.getX() - player.getX();
                double dz = radiant.getZ() - player.getZ();
                if ((dx * dx + dz * dz) > (radius * radius)) continue;

                MobEffectInstance existing = player.getEffect(JolCraftEffects.RADIANT);
                if (existing != null && existing.getAmplifier() == amplifier && existing.getDuration() >= 200) {
                    continue;
                }

                player.addEffect(new MobEffectInstance(JolCraftEffects.RADIANT, 400, amplifier, false, false, true));
            }
        }
    }

    @SubscribeEvent
    public static void onUndeadDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();

        if (!(target instanceof Player player)) return;
        if (!player.hasEffect(JolCraftEffects.RADIANT)) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;
        if (!livingAttacker.getType().is(EntityTypeTags.UNDEAD)) return;

        MobEffectInstance effect = player.getEffect(JolCraftEffects.RADIANT);
        int amplifier = effect != null ? effect.getAmplifier() : 0;

        float reductionFactor = 1.0f - (0.05f * (amplifier + 1));
        float newDamage = event.getOriginalDamage() * reductionFactor;
        event.setNewDamage(newDamage);
    }

    @SubscribeEvent
    public static void onArmorHurt(ArmorHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        double unbreakingChance = Mth.clamp(player.getAttributeValue(JolCraftAttributes.ARMOR_UNBREAKING), 0.0, 1.0);
        if (unbreakingChance <= 0.0) return;

        if (player.getRandom().nextDouble() < unbreakingChance) {
            event.setCanceled(true);
        }
    }

    private record PendingChestLoot(ResourceKey<Level> dim, BlockPos pos, ResourceKey<LootTable> table) {}

    private static final Map<UUID, PendingChestLoot> CHEST_LOOT_TO_REROLL = new HashMap<>();

    @SubscribeEvent
    public static void onRightContainerBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        Player player = event.getEntity();
        BlockPos pos = event.getPos();

        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (be instanceof RandomizableContainerBlockEntity lootable) {
            ResourceKey<LootTable> lootTable = lootable.getLootTable();
            if (lootTable != null) {
                CHEST_LOOT_TO_REROLL.put(
                        player.getUUID(),
                        new PendingChestLoot(serverLevel.dimension(), pos.immutable(), lootTable)
                );
            }
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        PendingChestLoot pending = CHEST_LOOT_TO_REROLL.remove(player.getUUID());
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
                CHEST_LOOT_TO_REROLL.remove(player.getUUID());

                double chance = player.getAttributeValue(JolCraftAttributes.EXTRA_CHEST_LOOT);
                MinecraftServer server = serverLevel.getServer();
                LootTable table = server.reloadableRegistries().getLootTable(pending.table());

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
                            break;
                        }
                    }
                }
            }

            return;
        }
        CHEST_LOOT_TO_REROLL.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        CHEST_LOOT_TO_REROLL.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        double chance = Mth.clamp(player.getAttributeValue(JolCraftAttributes.EXTRA_CROP), 0.0D, 1.0D);
        if (chance <= 0.0D) return;

        if (!isEligibleHarvestBlock(state)) return;

        List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, player.getMainHandItem());

        for (ItemStack stack : drops) {
            if (!isEligibleCropDrop(stack)) continue;

            if (level.random.nextDouble() < chance) {
                Block.popResource(level, pos, stack.copyWithCount(1));
            }
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

        if (block instanceof net.minecraft.world.level.block.CropBlock crop) {
            return crop.isMaxAge(state);
        }

        IntegerProperty ageProp = null;
        for (Property<?> prop : state.getProperties()) {
            if (prop instanceof IntegerProperty ip && prop.getName().equals("age")) {
                ageProp = ip;
                break;
            }
        }
        if (ageProp == null) return false;

        int age = state.getValue(ageProp);
        int maxAge = ageProp.getPossibleValues().stream().max(Integer::compareTo).orElse(age);
        return age >= maxAge;
    }

    @SubscribeEvent
    public static void onMagicDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player) || !source.is(Tags.DamageTypes.IS_MAGIC)) return;

        double resist = player.getAttributeValue(JolCraftAttributes.MAGIC_RESISTANCE);
        if (resist <= 0.0) return;

        float original = event.getOriginalDamage();
        float reduced = (float) (original * (1.0 - resist));
        event.setNewDamage(reduced);
    }
}
