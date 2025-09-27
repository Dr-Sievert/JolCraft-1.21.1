package net.sievert.jolcraft.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.custom.object.RadiantEntity;

import java.util.*;

public class JolCraftAttributeEvents {

    //Ashfang

    @SubscribeEvent
    public static void onAttackDamageTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        double attackDamageBoost = player.getAttributeValue(JolCraftAttributes.ATTACK_DAMAGE_INCREASE);

        if (attackDamageBoost > 0) {
            var attackDamageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamageAttr == null) return;

            ResourceLocation ATTACK_DAMAGE_INCREASE_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "attack_damage_increase");

            attackDamageAttr.removeModifier(ATTACK_DAMAGE_INCREASE_ID);

            AttributeModifier mod = new AttributeModifier(
                    ATTACK_DAMAGE_INCREASE_ID,
                    attackDamageBoost,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );
            attackDamageAttr.addTransientModifier(mod);
        }
    }

    //Deepmarrow

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        double boost = player.getAttributeValue(JolCraftAttributes.XP_BOOST);
        if (boost > 0) {
            int baseAmount = event.getAmount();
            int bonus = (int) (baseAmount * boost);
            event.setAmount(baseAmount + bonus);
        }
    }

    //Frostvein

    @SubscribeEvent
    public static void onPlayerSlowedTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        double resist = player.getAttributeValue(JolCraftAttributes.SLOW_RESIST);
        MobEffectInstance slow = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;

        ResourceLocation SLOW_RESIST_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "slow_resist");

        attr.removeModifier(SLOW_RESIST_ID);

        if (slow != null && resist > 0) {
            int amp = slow.getAmplifier();
            double vanillaMultiplier = 1.0 - 0.15 * (amp + 1);

            double desiredMultiplier = resist + (1 - resist) * vanillaMultiplier;

            double correction = (desiredMultiplier / vanillaMultiplier) - 1.0;

            attr.addTransientModifier(new AttributeModifier(
                    SLOW_RESIST_ID,
                    correction,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }

    //Ironheart

    @SubscribeEvent
    public static void onArmorChanged(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        recalcIronheartBonus(player);
    }

    @SubscribeEvent
    public static void onPlayerArmorTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!needsIronheartUpdate(player)) return;
        recalcIronheartBonus(player);
    }

    private static boolean needsIronheartUpdate(Player player) {
        double percent = player.getAttributeValue(JolCraftAttributes.ARMOR_INCREASE);
        var attr = player.getAttribute(Attributes.ARMOR);
        if (attr == null || percent <= 0) return false;

        ResourceLocation IRONHEART_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "ironheart_armor_bonus");

        double baseArmor = 0;
        for (AttributeModifier mod : attr.getModifiers()) {
            if (!IRONHEART_ID.equals(mod.id())) {
                baseArmor += mod.amount();
            }
        }
        double expectedBonus = baseArmor * percent;

        var existing = attr.getModifier(IRONHEART_ID);
        if (existing == null && expectedBonus == 0) return false;
        return existing == null || !(Math.abs(existing.amount() - expectedBonus) < 0.01);
    }

    private static void recalcIronheartBonus(Player player) {
        double percent = player.getAttributeValue(JolCraftAttributes.ARMOR_INCREASE);
        var attr = player.getAttribute(Attributes.ARMOR);
        if (attr == null) return;

        ResourceLocation IRONHEART_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "ironheart_armor_bonus");
        attr.removeModifier(IRONHEART_ID);

        if (percent > 0) {
            double baseArmor = 0;
            for (AttributeModifier mod : attr.getModifiers()) {
                if (!IRONHEART_ID.equals(mod.id())) {
                    baseArmor += mod.amount();
                }
            }
            double bonus = baseArmor * percent;
            if (bonus > 0) {
                attr.addTransientModifier(new AttributeModifier(
                        IRONHEART_ID,
                        bonus,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }
    }

    //Lumiere

    public static final Map<UUID, RadiantEntity> ACTIVE_RADIANT_ENTITIES = new HashMap<>();
    private static final Map<UUID, BlockPos> LAST_PLAYER_POS = new HashMap<>();
    private static final Map<UUID, Integer> STATIONARY_TICKS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerRadiantTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) player.level();
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

        if ((existing == null || existing.isRemoved())) {
            for (RadiantEntity e : level.getEntitiesOfClass(RadiantEntity.class, player.getBoundingBox().inflate(32))) {
                if (uuid.equals(e.getOwnerUUID())) {
                    existing = e;
                    ACTIVE_RADIANT_ENTITIES.put(uuid, e);
                    break;
                }
            }
        }

        if (lightLevel == 0) {
            if (existing != null) {
                if (existing.oldPos != null && existing.level().getBlockState(existing.oldPos).is(Blocks.LIGHT)) {
                    existing.level().setBlock(existing.oldPos, Blocks.AIR.defaultBlockState(), 3);
                }
                existing.discard();
                ACTIVE_RADIANT_ENTITIES.remove(uuid);
            }
            LAST_PLAYER_POS.remove(uuid);
            STATIONARY_TICKS.remove(uuid);
            return;
        }

        if (existing == null || existing.isRemoved()) {
            RadiantEntity entity = new RadiantEntity(JolCraftEntities.RADIANT.get(), level);
            BlockPos spawnPos = player.blockPosition().above();
            entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5);
            entity.setOwner(player);
            entity.setRadiantLightLevel(lightLevel);
            level.addFreshEntity(entity);
            ACTIVE_RADIANT_ENTITIES.put(uuid, entity);
            LAST_PLAYER_POS.put(uuid, player.blockPosition());
            STATIONARY_TICKS.put(uuid, 0);
        } else {
            existing.setRadiantLightLevel(lightLevel);

            BlockPos current = player.blockPosition();
            BlockPos previous = LAST_PLAYER_POS.getOrDefault(uuid, current);
            int ticks = STATIONARY_TICKS.getOrDefault(uuid, 0);

            ticks = current.equals(previous) ? ticks + 1 : 0;
            STATIONARY_TICKS.put(uuid, ticks);
            LAST_PLAYER_POS.put(uuid, current);

            if (ticks >= 20 && player.onGround()) {
                int percent = (int) (radiant * 100);
                int nearest25 = (percent / 25) * 25;
                int radius = 1 + (nearest25 / 25);

                double dx = existing.getX() - player.getX();
                double dz = existing.getZ() - player.getZ();
                double dy = existing.getY() - player.getY();
                double horizontalDistSq = dx * dx + dz * dz;
                boolean withinY = dy >= 0 && dy <= 4;

                boolean withinRadius = horizontalDistSq <= radius * radius && withinY;

                if (!withinRadius) {
                    double px = player.getX();
                    double py = player.getY() + player.getBbHeight() + 0.5;
                    double pz = player.getZ();
                    BlockPos targetPos = BlockPos.containing(px, py, pz);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (targetState.isAir() || targetState.is(Blocks.WATER)) {
                        existing.setPos(px, py, pz);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void removeRadiantOnPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        RadiantEntity existing = ACTIVE_RADIANT_ENTITIES.remove(uuid);

        if (existing != null && !existing.isRemoved()) {
            existing.discard();
        }

        LAST_PLAYER_POS.remove(uuid);
        STATIONARY_TICKS.remove(uuid);
    }

    @SubscribeEvent
    public static void onPlayerTickRadiantAura(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide()) return;

        for (RadiantEntity radiant : ACTIVE_RADIANT_ENTITIES.values()) {
            if (radiant.isRemoved()) continue;

            Entity ownerEntity = radiant.getOwner();
            if (!(ownerEntity instanceof Player owner)) continue;

            double radiantAttr = owner.getAttributeValue(JolCraftAttributes.RADIANT);
            if (radiantAttr < 0.25) continue;

            int percent = (int) (radiantAttr * 100);
            int nearest25 = (percent / 25) * 25;
            int radius = 1 + (nearest25 / 25);
            int amplifier = (nearest25 / 25) - 1;

            double dx = radiant.getX() - player.getX();
            double dz = radiant.getZ() - player.getZ();
            double dy = radiant.getY() - player.getY();

            double horizontalDistSq = dx * dx + dz * dz;
            boolean withinY = dy >= 0 && dy <= 4;

            if (horizontalDistSq <= radius * radius && withinY) {
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

    //Moonshard & Skyburrow

    @SubscribeEvent
    public static void onMovementTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        var movementAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementAttr == null) return;

        ResourceLocation SKYBURROW_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "skyburrow_day_speed");
        ResourceLocation MOONSHARD_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "moonshard_night_speed");

        movementAttr.removeModifier(SKYBURROW_ID);
        movementAttr.removeModifier(MOONSHARD_ID);

        double dayBoost = player.getAttributeValue(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY);
        double nightBoost = player.getAttributeValue(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT);

        boolean isDay = player.level().isDay();

        if (isDay && dayBoost > 0) {
            AttributeModifier mod = new AttributeModifier(
                    SKYBURROW_ID,
                    dayBoost,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );
            movementAttr.addTransientModifier(mod);
        } else if (!isDay && nightBoost > 0) {
            AttributeModifier mod = new AttributeModifier(
                    MOONSHARD_ID,
                    nightBoost,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );
            movementAttr.addTransientModifier(mod);
        }
    }

    //Rustagate

    @SubscribeEvent
    public static void onArmorHurt(ArmorHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        double unbreakingChance = player.getAttributeValue(JolCraftAttributes.ARMOR_UNBREAKING);
        if (unbreakingChance <= 0.0) return;

        if (player.getRandom().nextDouble() < unbreakingChance) {
            event.setCanceled(true);
        }
    }

    //Skyburrow

    private static final Map<UUID, ResourceKey<LootTable>> CHEST_LOOT_TO_REROLL = new HashMap<>();

    @SubscribeEvent
    public static void onRightContainerBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        Player player = event.getEntity();
        BlockPos pos = event.getPos();

        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (be instanceof RandomizableContainerBlockEntity lootable) {
            ResourceKey<LootTable> lootTable = lootable.getLootTable();
            if (lootTable != null) {
                CHEST_LOOT_TO_REROLL.put(player.getUUID(), lootTable);
            }
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        AbstractContainerMenu menu = event.getContainer();

        Set<RandomizableContainerBlockEntity> seen = new HashSet<>();
        for (Slot slot : menu.slots) {
            if (slot.container instanceof RandomizableContainerBlockEntity lootable && seen.add(lootable)) {
                if (lootable.getLootTable() != null) {
                    continue;
                }

                ResourceKey<LootTable> lootTable = CHEST_LOOT_TO_REROLL.remove(player.getUUID());
                if (lootTable == null) continue;

                double chance = player.getAttributeValue(JolCraftAttributes.EXTRA_CHEST_LOOT);

                MinecraftServer server = serverLevel.getServer();
                LootTable table = server.reloadableRegistries().getLootTable(lootTable);

                for (int i = 0; i < lootable.getContainerSize(); ++i) {
                    ItemStack stack = lootable.getItem(i);
                    if (stack.isEmpty() && serverLevel.random.nextDouble() < chance) {
                        LootParams.Builder builder = new LootParams.Builder(serverLevel)
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(lootable.getBlockPos()))
                                .withParameter(LootContextParams.THIS_ENTITY, player);
                        LootParams params = builder.create(LootContextParamSets.CHEST);
                        List<ItemStack> rerolled = table.getRandomItems(params);

                        for (ItemStack rolled : rerolled) {
                            if (!rolled.isEmpty()) {
                                lootable.setItem(i, rolled.copy());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        CHEST_LOOT_TO_REROLL.remove(player.getUUID());
    }

    //Verdanite

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        double chance = player.getAttributeValue(JolCraftAttributes.EXTRA_CROP);
        if (level.random.nextDouble() >= chance) return;

        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, null, player, player.getMainHandItem());
        ItemStack cropStack = null;
        for (ItemStack stack : drops) {
            if (stack.is(Tags.Items.CROPS)) {
                cropStack = stack;
                break;
            }
        }
        if (cropStack == null) return;

        IntegerProperty ageProperty = null;
        for (Property<?> prop : state.getProperties()) {
            if (prop.getName().equals("age") && prop instanceof IntegerProperty iprop) {
                ageProperty = iprop;
                break;
            }
        }

        if (ageProperty != null) {
            int age = state.getValue(ageProperty);
            int maxAge = ageProperty.getPossibleValues().stream().max(Integer::compare).orElse(0);
            if (age < maxAge) return;
        } else {
            if (cropStack.getItem() instanceof BlockItem) return;
        }

        Block.popResource(level, pos, new ItemStack(cropStack.getItem(), 1));
    }

    //Woecrystal

    @SubscribeEvent
    public static void onMagicDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();

        if (!source.is(Tags.DamageTypes.IS_MAGIC)) return;

        double resist = entity.getAttributeValue(JolCraftAttributes.MAGIC_RESISTANCE);
        if (resist <= 0.0) return;

        float original = event.getOriginalDamage();
        float reduced = (float) (original * (1.0 - resist));
        event.setNewDamage(reduced);
    }

}
