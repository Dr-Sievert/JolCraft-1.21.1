package net.sievert.jolcraft.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.custom.attachment.block.Hearth;
import net.sievert.jolcraft.block.custom.FermentingCauldronBlock;
import net.sievert.jolcraft.block.custom.FermentingStage;
import net.sievert.jolcraft.data.custom.attachment.unlock.TomeUnlock;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.attribute.JolCraftAttributes;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.custom.object.RadiantEntity;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.*;
import net.sievert.jolcraft.item.potion.JolCraftPotions;
import net.sievert.jolcraft.sound.JolCraftSoundHelper;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.entity.custom.dwarf.DwarfGuardEntity;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.custom.scrapper.SpannerItem;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.util.attachment.AncientDwarvenLanguageHelper;
import net.sievert.jolcraft.util.attachment.DwarvenLanguageHelper;
import net.sievert.jolcraft.util.attachment.DwarvenReputationHelper;
import net.sievert.jolcraft.util.compass.DeepslateCompassHelper;
import net.sievert.jolcraft.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.util.vanilla.JolCraftAnvilHelper;
import net.sievert.jolcraft.util.dwarf.SalvageLootHelper;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.util.attachment.DiscoveredStructuresHelper;
import java.util.*;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftGameEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        // Sync normal Dwarvish language
        boolean knowsLang = DwarvenLanguageHelper.knowsDwarvishBypassCreative(serverPlayer);
        JolCraftNetworking.sendToClient(serverPlayer, new ClientboundLanguagePacket(knowsLang));

        // Sync Ancient Dwarvish language
        boolean knowsAncient = AncientDwarvenLanguageHelper.knowsAncientDwarvishServerBypassCreative(serverPlayer);
        JolCraftNetworking.sendToClient(serverPlayer, new ClientboundAncientLanguagePacket(knowsAncient));

        // Sync Reputation tier
        int tier = DwarvenReputationHelper.getTierServerBypassCreative(serverPlayer);
        JolCraftNetworking.sendToClient(serverPlayer, new ClientboundReputationPacket(tier));

        // Sync Endorsements
        Set<ResourceLocation> endorsements = DwarvenReputationHelper.getAllEndorsementsServerBypassCreative(serverPlayer);
        JolCraftNetworking.sendToClient(serverPlayer, new ClientboundEndorsementsPacket(endorsements));

        // Sync Tome Unlocks
        Set<String> unlocks = TomeUnlock.get(serverPlayer).getUnlocks();
        JolCraftNetworking.sendToClient(serverPlayer, new ClientboundTomeUnlocksPacket(unlocks));
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack output = event.getCrafting();
        if (output.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())) {
            // Find the input compass (only one in a valid craft)
            ItemStack input = ItemStack.EMPTY;
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack stack = event.getInventory().getItem(i);
                if (stack.is(JolCraftItems.DEEPSLATE_COMPASS.get())) {
                    input = stack;
                    break;
                }
            }
            if (!input.isEmpty()) {
                var dye = input.get(DataComponents.DYED_COLOR);
                if (dye != null) {
                    output.set(DataComponents.DYED_COLOR, dye);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDialCombine(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack main = event.getItemStack();
        ItemStack offhand = player.getOffhandItem();

        boolean mainIsDial = main.is(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get());
        boolean offIsDial = offhand.is(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get());
        boolean mainIsEmpty = main.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());
        boolean offIsEmpty = offhand.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());

        if (!((mainIsDial && offIsEmpty) || (offIsDial && mainIsEmpty))) return;

        if (!level.isClientSide) {
            ItemStack dial = mainIsDial ? main : offhand;
            ItemStack empty = mainIsEmpty ? main : offhand;
            InteractionHand swingHand = mainIsDial ? event.getHand()
                    : (event.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

            // 1. Get group from dial
            String group = dial.get(JolCraftDataComponents.STRUCTURE_GROUP);
            if (group == null) return;

            TagKey<Structure> structureTag = DeepslateCompassHelper.getStructureTagForGroup(group);

            GlobalPos targetPos = null;
            String foundStructureFullId = "unknown";

            if (structureTag != null && player.level() instanceof ServerLevel serverLevel) {
                try {
                    targetPos = DiscoveredStructuresHelper.findNearestUndiscoveredStructure(
                            serverLevel,
                            structureTag,
                            player.blockPosition(),
                            100,
                            player
                    );

                    if (targetPos != null) {
                        // Find the full structure id for tooltip (match first structure at target position)
                        var registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
                        var allRefs = serverLevel.structureManager().getAllStructuresAt(targetPos.pos());

                        for (Structure structure : allRefs.keySet()) {
                            for (Holder<Structure> holder : registry.getTagOrEmpty(structureTag)) {
                                if (holder.value() == structure) {
                                    ResourceLocation id = registry.getKey(structure);
                                    if (id != null) {
                                        foundStructureFullId = id.toString();
                                    }
                                    break;
                                }
                            }
                            if (!foundStructureFullId.equals("unknown")) break;
                        }

                        // fallback: use group as the id if still unknown
                        if (foundStructureFullId.equals("unknown")) {
                            foundStructureFullId = group;
                        }
                    }
                } catch (Exception e) {
                    targetPos = null;
                }
            }

            if (targetPos == null) {
                // Optionally, notify player: no undiscovered structures found
                return;
            }

            // 3. Create new compass
            ItemStack result = new ItemStack(JolCraftItems.DEEPSLATE_COMPASS.get());

            // a) Dye color
            var dyeColor = empty.get(DataComponents.DYED_COLOR);
            if (dyeColor != null) {
                result.set(DataComponents.DYED_COLOR, dyeColor);
            }
            // b) Structure group (full id)
            result.set(JolCraftDataComponents.STRUCTURE_GROUP, foundStructureFullId);

            // c) Dial color
            var dialColor = dial.get(JolCraftDataComponents.DIAL_COLOR.get());
            if (dialColor != null) {
                result.set(JolCraftDataComponents.DIAL_COLOR, dialColor);
            }

            // d) Target pos (with correct Y)
            result.set(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET, targetPos);

            // 4. Consume and give result
            dial.shrink(1);
            empty.shrink(1);

            if (!player.addItem(result)) {
                player.drop(result, false);
            }

            player.swing(swingHand, true);
            level.playSound(null, player.blockPosition(), SoundEvents.METAL_HIT, SoundSource.PLAYERS, 1.0F, 1.4F);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        if (player.tickCount % 5 != 0) return;

        for (ItemStack stack : player.getInventory().items) {
            if (!stack.is(JolCraftItems.DEEPSLATE_COMPASS.get())) continue;

            GlobalPos tracked = stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET);
            String trackedStructureId = stack.get(JolCraftDataComponents.STRUCTURE_GROUP);
            if (tracked == null || trackedStructureId == null || trackedStructureId.isEmpty()) continue;
            if (!player.level().dimension().equals(tracked.dimension())) continue;

            var structureRegistry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            ResourceLocation trackedStructureKey = ResourceLocation.tryParse(trackedStructureId);
            if (trackedStructureKey == null) continue;
            var trackedStructureHolder = structureRegistry.get(trackedStructureKey).orElse(null);
            if (trackedStructureHolder == null) continue;

            // --- Use the *tracked* structure entrance X/Z, but use player Y for the check
            BlockPos trackedPosXZ = BlockPos.containing(tracked.pos().getX(), player.blockPosition().getY(), tracked.pos().getZ());
            StructureManager structureManager = serverLevel.structureManager();
            StructureStart start = structureManager.getStructureAt(trackedPosXZ, trackedStructureHolder.value());

            if (start == null || !start.isValid()) {
                continue;
            }
            BoundingBox box = start.getBoundingBox();

            // 1. Only trigger if player is inside this *specific* structure bounding box (all XYZ)
            if (!box.isInside(player.blockPosition())) {
                // Uncomment to debug position issues:
                // System.out.println("Player at " + player.blockPosition() + " not in bounding box " + box + " for structure at " + trackedPosXZ);
                continue;
            }

            // 2. Use entrance XZ and the bounding box's center Y for what gets saved (to prevent dupe)
            BlockPos patchedEntrance = BlockPos.containing(tracked.pos().getX(), box.getCenter().getY(), tracked.pos().getZ());

            boolean alreadyDiscovered = DiscoveredStructuresHelper.getDiscoveredStructures(player).stream()
                    .anyMatch(gp -> gp.dimension().equals(tracked.dimension()) && gp.pos().equals(patchedEntrance));
            if (alreadyDiscovered) continue;

            // --- Discovery action ---
            DiscoveredStructuresHelper.addDiscoveredStructureServer(player, GlobalPos.of(tracked.dimension(), patchedEntrance), trackedStructureKey);

            ItemStack emptyCompass = new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());
            var dye = stack.get(DataComponents.DYED_COLOR);
            if (dye != null) emptyCompass.set(DataComponents.DYED_COLOR, dye);

            int idx = player.getInventory().items.indexOf(stack);
            player.getInventory().items.set(idx, emptyCompass);

            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.structure.discovered")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.translatable("tooltip.jolcraft.structure." + trackedStructureId)
                                    .withStyle(ChatFormatting.BLUE)), true
            );

            if (player instanceof ServerPlayer serverPlayer) {
                JolCraftNetworking.sendToClient(serverPlayer,
                        new ClientboundPlaySoundPacket(SoundEvents.ITEM_BREAK.location(),
                                player.getX(), player.getY(), player.getZ(),
                                SoundSource.PLAYERS, 1.0F, 1.5F));
                JolCraftNetworking.sendToClient(serverPlayer,
                        new ClientboundPlaySoundPacket(JolCraftSounds.LEVEL_UP.get().location(),
                                player.getX(), player.getY(), player.getZ(),
                                SoundSource.PLAYERS, 1.0F, 1.0F));
            }

            break;
        }
    }


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


    @SubscribeEvent
    public static void onMovementTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        var movementAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementAttr == null) return;

        // Prepare ResourceLocations for unique modifiers
        ResourceLocation SKYBURROW_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "skyburrow_day_speed");
        ResourceLocation MOONSHARD_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "moonshard_night_speed");

        // Remove old modifiers every tick for consistency
        movementAttr.removeModifier(SKYBURROW_ID);
        movementAttr.removeModifier(MOONSHARD_ID);

        // Get boosts from your custom attributes
        double dayBoost = player.getAttributeValue(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY);
        double nightBoost = player.getAttributeValue(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT);

        // Time check (is it day or night?)
        boolean isDay = player.level().isDay();

        // Apply the correct boost as ADD_MULTIPLIED_BASE
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

    // Use this event for instant recalculation when armor changes
    @SubscribeEvent
    public static void onArmorChanged(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        recalcIronheartBonus(player);
    }

    // Fallback: On tick, only if value doesn't match (safety net for missed events/desync)
    @SubscribeEvent
    public static void onPlayerArmorTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        // Check if actual value matches expected, skip if equal
        if (!needsIronheartUpdate(player)) return;
        recalcIronheartBonus(player);
    }

    // --- Helper methods ---
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

        // Find if our modifier exists, and its amount matches
        var existing = attr.getModifier(IRONHEART_ID);
        if (existing == null && expectedBonus == 0) return false;
        if (existing != null && Math.abs(existing.amount() - expectedBonus) < 0.01) return false;
        return true;
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


    @SubscribeEvent
    public static void onMagicDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();

        // Only apply to magic damage
        if (!source.is(Tags.DamageTypes.IS_MAGIC)) return;

        double resist = entity.getAttributeValue(JolCraftAttributes.MAGIC_RESISTANCE);
        if (resist <= 0.0) return;

        float original = event.getOriginalDamage();
        float reduced = (float) (original * (1.0 - resist));
        event.setNewDamage(reduced);
    }

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

    @SubscribeEvent
    public static void onPlayerSlowedTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        double resist = player.getAttributeValue(JolCraftAttributes.SLOW_RESIST);
        MobEffectInstance slow = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;

        // Use a unique ResourceLocation for this mod's compensation
        ResourceLocation SLOW_RESIST_ID = ResourceLocation.fromNamespaceAndPath("jolcraft", "slow_resist");

        // Remove our compensation if present
        attr.removeModifier(SLOW_RESIST_ID);

        // If slowness is active, and we have any resist, counteract it
        // For MC 1.21.4 and later:
        if (slow != null && resist > 0) {
            int amp = slow.getAmplifier();
            double vanillaMultiplier = 1.0 - 0.15 * (amp + 1);

            // Resist: 0.8 (80%) means you get only 20% of the slow
            double desiredMultiplier = resist + (1 - resist) * vanillaMultiplier;

            double correction = (desiredMultiplier / vanillaMultiplier) - 1.0;

            attr.addTransientModifier(new AttributeModifier(
                    SLOW_RESIST_ID,
                    correction,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }

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
        int pieces = (int) Math.round(radiant * 4.0); // number of armor pieces
        int lightLevel = switch (pieces) {
            case 1 -> 9;
            case 2 -> 11;
            case 3 -> 13;
            case 4 -> 15;
            default -> 0;
        };

        RadiantEntity existing = ACTIVE_RADIANT_ENTITIES.get(uuid);

        // Fallback: search world for entity if map is out of sync (e.g. after relog)
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
                // Calculate aura radius
                int percent = (int) (radiant * 100);
                int nearest25 = (percent / 25) * 25;
                int radius = 1 + (nearest25 / 25); // 2–5

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
            int radius = 1 + (nearest25 / 25); // 2–5
            int amplifier = (nearest25 / 25) - 1; // 0–3

            double dx = radiant.getX() - player.getX();
            double dz = radiant.getZ() - player.getZ();
            double dy = radiant.getY() - player.getY(); // radiant center to player feet

            double horizontalDistSq = dx * dx + dz * dz;
            boolean withinY = dy >= 0 && dy <= 4;

            if (horizontalDistSq <= radius * radius && withinY) {
                MobEffectInstance existing = player.getEffect(JolCraftEffects.RADIANT);

                // Skip if already has same amplifier and decent remaining duration
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

        // Get RADIANT effect amplifier (0–3)
        MobEffectInstance effect = player.getEffect(JolCraftEffects.RADIANT);
        int amplifier = effect != null ? effect.getAmplifier() : 0;

        // 5% reduction per level (I–IV → 5%–20%)
        float reductionFactor = 1.0f - (0.05f * (amplifier + 1));
        float newDamage = event.getOriginalDamage() * reductionFactor;
        event.setNewDamage(newDamage);
    }


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
                // Save the loot table for reroll use (key by player UUID)
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
                // Skip if loot is still packed (hasn't generated yet)
                if (lootable.getLootTable() != null) {
                    // The loot hasn't been generated yet (locked, or special menu like LockMenu)
                    // Do NOT reroll, keep the entry for a real opening later
                    continue;
                }

                // Only now remove the pending reroll, since we're actually opening a loot-filled chest
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

    @SubscribeEvent
    public static void onCurseAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var instance = event.getEffectInstance();
        if (instance != null && instance.getEffect().is(JolCraftEffects.DELIRIUM_CURSE)) {
            if (!player.level().isClientSide()) {
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        JolCraftSounds.CURSE.get(),
                        player.getSoundSource(),
                        1.0F, 1.0F
                );
            }
        }
    }


    // ThreadLocal flag for tracking milk bucket effect clearing
    private static final ThreadLocal<Boolean> isMilkRemoval = ThreadLocal.withInitial(() -> false);


    @SubscribeEvent
    public static void onMilkStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if ((event.getItem().is(Items.MILK_BUCKET) || event.getItem().is(JolCraftItems.MUFFHORN_MILK_BUCKET.get())) && event.getEntity().hasEffect(JolCraftEffects.DELIRIUM_CURSE)) {
            isMilkRemoval.set(true);
        }
    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (isMilkRemoval.get() && event.getEffect().is(JolCraftEffects.DELIRIUM_CURSE)) {
            event.setCanceled(true);
            isMilkRemoval.set(false);

            if (!player.level().isClientSide()) {
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        JolCraftSounds.CURSE.get(),
                        player.getSoundSource(),
                        1.0F, 1.0F
                );
            }
        }
    }

    @SubscribeEvent
    public static void onMilkStopOrFinish(LivingEntityUseItemEvent.Stop event) {
        if (!(event.getEntity() instanceof Player) || isMilkRemoval.get() == false) {
            return;
        }
        isMilkRemoval.set(false);
    }

    @SubscribeEvent
    public static void onMilkFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player) || isMilkRemoval.get() == false) {
            return;
        }
        isMilkRemoval.set(false);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;
        Player player = event.getPlayer();
        if (player == null) return;
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        // Attribute chance
        double chance = player.getAttributeValue(JolCraftAttributes.EXTRA_CROP);
        if (level.random.nextDouble() >= chance) return;

        // Find first crop drop, if any
        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, null, player, player.getMainHandItem());
        ItemStack cropStack = null;
        for (ItemStack stack : drops) {
            if (stack.is(Tags.Items.CROPS)) {
                cropStack = stack;
                break;
            }
        }
        if (cropStack == null) return; // No crops

        // Check for age property
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
            if (age < maxAge) return; // Not mature
        } else {
            // No age property; if crop is a BlockItem, skip bonus (e.g. pumpkin)
            if (cropStack.getItem() instanceof BlockItem) return;
        }

        // Give exactly one extra crop
        Block.popResource(level, pos, new ItemStack(cropStack.getItem(), 1));
    }


    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.WATER, JolCraftItems.DEEPMARROW_DUST.get(), JolCraftPotions.ANCIENT_MEMORY);
        builder.addMix(JolCraftPotions.ANCIENT_MEMORY, Items.REDSTONE, JolCraftPotions.LONG_ANCIENT_MEMORY);

        builder.addMix(Potions.AWKWARD, JolCraftItems.SUNGLEAM_DUST.asItem(), JolCraftPotions.LOCKPICKING);
        builder.addMix(JolCraftPotions.LOCKPICKING, Items.REDSTONE, JolCraftPotions.LONG_LOCKPICKING);
        builder.addMix(JolCraftPotions.LOCKPICKING, Items.GLOWSTONE_DUST, JolCraftPotions.STRONG_LOCKPICKING);

        builder.addMix(Potions.AWKWARD, JolCraftItems.EARTHBLOOD_DUST.asItem(), JolCraftPotions.DWARVEN_HASTE);
        builder.addMix(JolCraftPotions.DWARVEN_HASTE, Items.REDSTONE, JolCraftPotions.LONG_DWARVEN_HASTE);
        builder.addMix(JolCraftPotions.DWARVEN_HASTE, Items.GLOWSTONE_DUST, JolCraftPotions.STRONG_DWARVEN_HASTE);

    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        ItemStack stack = event.getItemStack();

        //Cooldown gate
        if (player.getCooldowns().isOnCooldown(stack)) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.crate.cooldown").withStyle(ChatFormatting.GRAY),
                    true
            );
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        // Dwarf logic
        if (target instanceof AbstractDwarfEntity dwarf && !dwarf.isBaby() && dwarf.canTrade()) {

            // --- Language Check (block event if player can't interact) ---
            InteractionResult langResult = dwarf.languageCheck(player);
            if (langResult != InteractionResult.SUCCESS) {
                event.setCancellationResult(langResult);
                event.setCanceled(true);
                return;
            }

            // Restock Crate
            if (stack.is(JolCraftItems.RESTOCK_CRATE.get())) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (dwarf.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_dwarf").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                boolean needsRestock = dwarf.getOffers().stream().anyMatch(DwarfMerchantOffer::isOutOfStock);

                if (!needsRestock && !dwarf.hasRandomTrades()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.no_need").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.success").withStyle(ChatFormatting.GREEN),
                            true
                    );
                    dwarf.crateRestock();
                    JolCraftSoundHelper.playDwarfYes(dwarf);
                    if (!player.isCreative()) stack.shrink(1);
                    player.getCooldowns().addCooldown(stack, 60);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Reroll Crate
            if (stack.is(JolCraftItems.REROLL_CRATE.get())) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (dwarf.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_dwarf").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                if (!dwarf.canReroll()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.reroll_crate.fail").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.reroll_crate.success").withStyle(ChatFormatting.GREEN),
                            true
                    );
                    dwarf.rerollTrades();
                    JolCraftSoundHelper.playDwarfYes(dwarf);
                    if (!player.isCreative()) stack.shrink(1);
                    player.getCooldowns().addCooldown(stack, 60);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        // Villager logic
        if (target instanceof Villager villager) {


            // Restock Crate
            if (stack.is(JolCraftItems.RESTOCK_CRATE.get()) && !villager.isBaby() && villager.canRestock()) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (villager.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_villager").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(villager);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                boolean needsRestock = villager.getOffers().stream().anyMatch(MerchantOffer::isOutOfStock);

                if (!needsRestock) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.no_need").withStyle(ChatFormatting.GRAY),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(villager);
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.success").withStyle(ChatFormatting.GREEN),
                            true
                    );
                    JolCraftSoundHelper.playVillagerFisherman(villager);
                    JolCraftSoundHelper.playVillagerYes(villager);
                    villager.restock();
                    if (!player.isCreative()) stack.shrink(1);
                    player.getCooldowns().addCooldown(stack, 60);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Reroll Crate
            if (stack.is(JolCraftItems.REROLL_CRATE.get()) && !villager.isBaby()) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (villager.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_villager").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(villager);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                VillagerData data = villager.getVillagerData();
                int currentLevel = data.getLevel();

                MerchantOffers accumulated = new MerchantOffers();

                for (int level = 1; level <= currentLevel; level++) {
                    villager.setVillagerData(data.setLevel(level));
                    villager.setOffers(null); // clear to force repopulation
                    MerchantOffers thisLevelOffers = villager.getOffers();

                    for (MerchantOffer offer : thisLevelOffers) {
                        accumulated.add(offer);
                    }
                }

                villager.setOffers(accumulated);
                villager.setVillagerData(data.setLevel(currentLevel)); // restore

                JolCraftSoundHelper.playVillagerFisherman(villager);
                JolCraftSoundHelper.playVillagerYes(villager);
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.reroll_crate.success").withStyle(ChatFormatting.GREEN),
                        true
                );
                if (!player.isCreative()) stack.shrink(1);
                player.getCooldowns().addCooldown(stack, 60);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }

        }

    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getEntity();
        var level = event.getLevel();
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        var mainHandStack = player.getMainHandItem();

        // Only allow clicking on the top face
        if (mainHandStack.is(Items.ROTTEN_FLESH)) {
            BlockPos above = pos.above();

            boolean onLog = (event.getFace() == Direction.UP
                    && state.is(BlockTags.LOGS)
                    && state.hasProperty(BlockStateProperties.AXIS)
                    && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y);

            boolean onSoil = (event.getFace() == Direction.UP
                    && (state.is(JolCraftBlocks.VERDANT_SOIL.get())));

            boolean canPlant = onLog || onSoil;

            // Must be air above
            if (canPlant && level.getBlockState(above).isAir()) {
                level.setBlock(above, JolCraftBlocks.FESTERLING_CROP.get().defaultBlockState(), 3);
                level.playSound(null, above, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (!player.isCreative()) mainHandStack.shrink(1);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if (!level.isClientSide()) {
            // Check that block is full water cauldron
            if (state.is(Blocks.WATER_CAULDRON) && state.getValue(LayeredCauldronBlock.LEVEL) == 3) {

                // === SUGAR logic for yeast creation ===
                if (mainHandStack.is(Items.SUGAR)) {
                    BlockState fermentState = JolCraftBlocks.FERMENTING_CAULDRON.get().defaultBlockState()
                            .setValue(FermentingCauldronBlock.LEVEL, 3)
                            .setValue(FermentingCauldronBlock.STAGE, FermentingStage.YEAST_FERMENTING);
                    level.setBlock(pos, fermentState, 3);

                    level.playSound(null, pos, SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.3F, 1.7F);

                    if (!player.isCreative()) mainHandStack.shrink(1);

                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                // === BARLEY MALT logic for malted stage ===
                if (mainHandStack.is(JolCraftItems.BARLEY_MALT.get())) {
                    BlockState maltedState = JolCraftBlocks.FERMENTING_CAULDRON.get().defaultBlockState()
                            .setValue(FermentingCauldronBlock.LEVEL, 3)
                            .setValue(FermentingCauldronBlock.STAGE, FermentingStage.MALTED);
                    // Optionally set a malted boolean property if you want

                    level.setBlock(pos, maltedState, 3);

                    level.playSound(null, pos, SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.3F, 1.7F);

                    if (!player.isCreative()) mainHandStack.shrink(1);

                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }
            }

        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Hearth hearth = Hearth.get(player);
        if (hearth.hasLitThisDay()) {
            hearth.setLitThisDay(false);
        }
    }

    @SubscribeEvent
    public static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof DwarfGuardEntity dwarf &&
                event.getSource().getEntity() instanceof Monster &&
                dwarf.isBlockCooldownReady()) {

            // Check if the attack is a melee attack (within melee range)
            if (event.getSource().getEntity() instanceof Monster monster) {
                if (monster.isWithinMeleeAttackRange((LivingEntity) event.getEntity())) {
                    // Melee attack detected, mark for blocking
                    dwarf.markForBlocking();
                    event.setInvulnerable(true);
                    return;
                }

                // Check if the source is a projectile (like an arrow)
                if (event.getSource().getDirectEntity() instanceof Projectile) {
                    // Ranged attack (e.g., arrow) detected, mark for blocking
                    dwarf.markForBlocking();
                    event.setInvulnerable(true);
                }
            }
        }
    }


    @SubscribeEvent
    public static void registerCustomTrades(final VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(5).add((pTrader, pRandom) -> {
                int baseCost = 32 + pRandom.nextInt(33); // Random between 32 and 64
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, baseCost),
                        new ItemStack(JolCraftItems.DWARVEN_LEXICON.get(), 1),
                        1, 1, 0.05f
                );
            });
        }
    }

    @SubscribeEvent
    public static void onSpannerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack main = event.getItemStack();
        ItemStack offhand = player.getOffhandItem();

        boolean mainIsSpanner = main.getItem() instanceof SpannerItem;
        boolean offIsSpanner = offhand.getItem() instanceof SpannerItem;
        boolean mainIsScrap = main.is(JolCraftTags.Items.GLOBAL_SALVAGE);
        boolean offIsScrap = offhand.is(JolCraftTags.Items.GLOBAL_SALVAGE);

        if (!((mainIsSpanner && offIsScrap) || (offIsSpanner && mainIsScrap))) return;

        if (!level.isClientSide) {
            ItemStack scrap = mainIsScrap ? main : offhand;
            ItemStack spanner = mainIsSpanner ? main : offhand;
            EquipmentSlot spannerSlot = mainIsSpanner ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            InteractionHand swingHand = mainIsSpanner ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

            List<ItemStack> loot = SalvageLootHelper.generateSalvageLoot(scrap);
            loot.forEach(stack -> level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                    level,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    stack
            )));

            if (!player.isCreative()) {
                scrap.shrink(1);
                spanner.hurtAndBreak(1, player, spannerSlot);
            }
            player.swing(swingHand, true);
            level.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.5F);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }


    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        JolCraftCriteriaTriggers.HAS_ADVANCEMENT.trigger(player, event.getAdvancement().id());
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        // Check for your custom effect (replace 'DWARVEN_HASTE' with your actual effect)
        if (player.hasEffect(JolCraftEffects.DWARVEN_HASTE)) {
            MobEffectInstance effect = player.getEffect(JolCraftEffects.DWARVEN_HASTE);
            int amplifier = effect.getAmplifier();

            // +20% per level, like vanilla Haste
            float originalSpeed = event.getOriginalSpeed();
            float newSpeed = originalSpeed * (1.0F + 0.2F * (amplifier + 1));
            event.setNewSpeed(newSpeed);
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        String rename = event.getName();

        if (!left.isEmpty() && left.is(JolCraftTags.Items.LEGENDARY_ITEMS)) {
            var vanilla = JolCraftAnvilHelper.vanillaResult(left, right, rename, event.getPlayer());
            ItemStack result = vanilla.result();

            if (!result.isEmpty()) {
                // Determine the name to show: use rename, else use default name (but filter it, for safety)
                String baseName;
                if (rename != null && !rename.isEmpty()) {
                    baseName = net.minecraft.util.StringUtil.filterText(rename);
                } else {
                    baseName = left.getHoverName().getString();
                }

                // Remove any prior names to avoid conflicts
                result.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
                result.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);

                // Always set gold name, even if unchanged!
                result.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                        net.minecraft.network.chat.Component.literal(baseName).withStyle(net.minecraft.ChatFormatting.GOLD));
            }

            event.setOutput(result);
            event.setCost(vanilla.cost());
            event.setMaterialCost(vanilla.materialCost());
        }

        if (!left.isEmpty() && left.is(JolCraftTags.Items.MITHRIL_ITEMS)) {
            var vanilla = JolCraftAnvilHelper.vanillaResult(left, right, rename, event.getPlayer());
            ItemStack result = vanilla.result();

            if (!result.isEmpty()) {
                // Determine the name to show: use rename, else use default name (but filter it, for safety)
                String baseName;
                if (rename != null && !rename.isEmpty()) {
                    baseName = net.minecraft.util.StringUtil.filterText(rename);
                } else {
                    baseName = left.getHoverName().getString();
                }

                // Remove any prior names to avoid conflicts
                result.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
                result.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);

                // Always set gold name, even if unchanged!
                result.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                        net.minecraft.network.chat.Component.literal(baseName).withStyle(ChatFormatting.AQUA));
            }

            event.setOutput(result);
            event.setCost(vanilla.cost());
            event.setMaterialCost(vanilla.materialCost());
        }
    }


    @SubscribeEvent
    public static void onEnchantItem(PlayerEnchantItemEvent event) {
        ItemStack stack = event.getEnchantedItem();

        // Legendary naming logic
        if (!stack.isEmpty() && stack.is(JolCraftTags.Items.LEGENDARY_ITEMS)) {
            // Always set gold name (you may want to use base name logic as in your anvil event)
            String baseName = stack.getHoverName().getString();
            stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
            stack.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);
            stack.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                    Component.literal(baseName).withStyle(ChatFormatting.GOLD));
        }

        // Mithril naming logic
        if (!stack.isEmpty() && stack.is(JolCraftTags.Items.MITHRIL_ITEMS)) {
            String baseName = stack.getHoverName().getString();
            stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
            stack.remove(net.minecraft.core.component.DataComponents.ITEM_NAME);
            stack.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                    Component.literal(baseName).withStyle(ChatFormatting.AQUA));
        }
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Monster && !(entity instanceof Creeper) && !(entity instanceof EnderMan)) {
            Monster mob = (Monster) entity;
            mob.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, AbstractDwarfEntity.class, true));
        }
    }



}
