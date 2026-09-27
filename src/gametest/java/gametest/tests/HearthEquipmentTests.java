package gametest.tests;

import gametest.GameTestGroup;
import gametest.util.TestPlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.world.player.HearthEquipmentEvents;
import net.sievert.jolcraft.mixin.MobEffectInstanceAccessor;
import net.sievert.jolcraft.network.util.SyncHelper;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.HearthBlock;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.LapidaryBenchBlockEntity;
import net.sievert.jolcraft.world.entity.attachment.player.custom.hearth.HearthAttachment;
import net.sievert.jolcraft.world.entity.attachment.player.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.world.entity.attachment.player.custom.lore.DwarfLoreAttachmentHelper;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.player.JolCraftStats;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimAttributes;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.registry.JolCraftArmorItems;
import net.sievert.jolcraft.world.recipe.custom.vanilla.AttributeSmithingTrimRecipe;

@GameTestGroup
public final class HearthEquipmentTests {
    private HearthEquipmentTests() {}

    @GameTest(template = "basic")
    public static void armorEffectsExpireAfterClearingEveryPiece(GameTestHelper helper) {
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "armor_cleanup");
        try {
            for (var set : java.util.List.of(JolCraftArmorItems.DEEPSLATE, JolCraftArmorItems.MITHRIL)) {
                for (ArmorItem.Type type : java.util.List.of(ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE,
                        ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS)) {
                    player.setItemSlot(type.getSlot(), new ItemStack(set.get(type).get()));
                }
                set.helmet().get().inventoryTick(player.getItemBySlot(EquipmentSlot.HEAD),
                        player.level(), player, 0, false);
                tickEquipment(player);
                var effect = set == JolCraftArmorItems.DEEPSLATE ? MobEffects.DAMAGE_RESISTANCE : MobEffects.GLOWING;
                check(player.hasEffect(effect), "A complete armor set must grant its effect");
                player.getInventory().clearContent();
                tickEquipment(player);
                check(!player.hasEffect(effect), "Removing every piece must remove the owned set effect");
            }
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200));
            tickEquipment(player);
            check(player.hasEffect(MobEffects.DAMAGE_RESISTANCE), "Set cleanup must preserve unrelated potion effects");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void armorCleanupPreservesStrongerPotionEffects(GameTestHelper helper) {
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "armor_hidden_cleanup",
                helper.absolutePos(new BlockPos(1, 2, 1)));
        try {
            for (ArmorItem.Type type : java.util.List.of(ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE,
                    ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS)) {
                player.setItemSlot(type.getSlot(), new ItemStack(JolCraftArmorItems.DEEPSLATE.get(type).get()));
            }
            JolCraftArmorItems.DEEPSLATE.helmet().get().inventoryTick(player.getItemBySlot(EquipmentSlot.HEAD),
                    player.level(), player, 0, false);
            tickEquipment(player);
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 2));
            MobEffectInstance activePotion = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            MobEffectInstance hiddenPotion = ((MobEffectInstanceAccessor) activePotion).jolcraft$getHiddenEffect();
            helper.assertTrue(hiddenPotion != null && hiddenPotion.getAmplifier() == 1,
                    "The fixture must include a hidden external finite effect");
            // The stronger potions hide the armor's infinite Resistance I.
            tickEquipment(player);
            player.getInventory().clearContent();
            tickEquipment(player);
            MobEffectInstance current = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            helper.assertTrue(current != null && current.getAmplifier() == 2 && current.getDuration() == 5,
                    "Armor cleanup must preserve the active stronger finite effect");
            helper.assertTrue(current == activePotion
                            && ((MobEffectInstanceAccessor) current).jolcraft$getHiddenEffect() == hiddenPotion,
                    "Armor cleanup must preserve both external potion instances");
            // Mock connections are not registered in the server's network tick loop.
            // Advance the normal player tick explicitly so potion durations expire.
            for (int i = 0; i < 7; i++) player.doTick();
            MobEffectInstance remaining = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            helper.assertTrue(remaining != null && remaining.getAmplifier() == 1 && !remaining.isInfiniteDuration(),
                    "Armor cleanup must preserve the hidden external finite effect");
            for (int i = 0; i < 18; i++) player.doTick();
            helper.assertTrue(!player.hasEffect(MobEffects.DAMAGE_RESISTANCE),
                    "Owned hidden infinite armor resistance must not return after both potions expire");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void homesteadRequiresAnActiveNearbyHearth(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "hearth_cleanup", pos);
        try {
            HearthBlockEntity hearth = ownedHearth(helper, pos, player);
            HearthAttachmentHelper.setActiveHearthPos(player, helper.getLevel().dimension(), pos);
            hearth.tickServer();
            check(player.hasEffect(JolCraftEffects.HOMESTEAD), "An active nearby hearth must grant Homestead");
            player.setPos(pos.getX() + 18, pos.getY(), pos.getZ());
            tickEquipment(player);
            check(!player.hasEffect(JolCraftEffects.HOMESTEAD), "Leaving the hearth radius must remove Homestead");
            player.setPos(pos.getX(), pos.getY(), pos.getZ());
            hearth.tickServer();
            helper.getLevel().setBlock(pos, hearth.getBlockState().setValue(HearthBlock.LIT, false), 3);
            tickEquipment(player);
            check(!player.hasEffect(JolCraftEffects.HOMESTEAD), "An extinguished hearth must remove Homestead");
            hearth.tickServer();
            helper.getLevel().removeBlock(pos, false);
            tickEquipment(player);
            check(!player.hasEffect(JolCraftEffects.HOMESTEAD), "A destroyed hearth must remove Homestead");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void hearthLoginValidationKeepsTheOriginalDimension(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "hearth_dimension", pos);
        try {
            // New-player login defaults to the overworld regardless of the constructor's level.
            player.teleportTo(helper.getLevel().getServer().getLevel(Level.NETHER),
                    pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0, 0);
            check(player.level().dimension().equals(Level.NETHER), "The login fixture must be in another dimension");
            HearthBlockEntity hearth = ownedHearth(helper, pos, player);
            GlobalPos expected = GlobalPos.of(helper.getLevel().dimension(), pos);
            HearthAttachment original = new HearthAttachment(12L, expected);
            var encoded = HearthAttachment.CODEC.encodeStart(NbtOps.INSTANCE, original).getOrThrow();
            HearthAttachmentHelper.set(player, HearthAttachment.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow());
            SyncHelper.syncAll(player);
            check(expected.equals(HearthAttachmentHelper.activeHearthPos(player)), "Login in another dimension must retain the original hearth");
            hearth.tickServer();
            check(!player.hasEffect(JolCraftEffects.HOMESTEAD), "Matching coordinates in another dimension must not grant Homestead");

            check(HearthAttachmentHelper.lastLitDay(player) == 12L, "Serialization must retain the cooldown");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void vanillaRetrimmingRemovesOnlyGemModifiers(GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        ArmorTrim trim = new ArmorTrim(
                registries.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(
                        JolCraftTrimMaterials.attribute(JolCraftTrimMaterials.Attribute.AEGISCORE)),
                registries.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SENTRY));
        ItemStack base = new ItemStack(Items.IRON_CHESTPLATE);
        var otherId = JolCraft.location("test_other_attribute");
        base.set(DataComponents.ATTRIBUTE_MODIFIERS, base.getAttributeModifiers().withModifierAdded(
                Attributes.LUCK, new AttributeModifier(otherId, 2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST));
        base.set(DataComponents.TRIM, trim);
        JolCraftTrimAttributes.applyAttribute(base, trim);
        SmithingTrimRecipe recipe = new SmithingTrimRecipe(Ingredient.of(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE),
                Ingredient.of(Items.IRON_CHESTPLATE), Ingredient.of(Items.IRON_INGOT));
        ItemStack result = recipe.assemble(new SmithingRecipeInput(
                new ItemStack(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE), base, new ItemStack(Items.IRON_INGOT)), registries);
        check(!result.isEmpty(), "Vanilla retrimming must produce armor");
        var modifiers = result.getAttributeModifiers().modifiers();
        check(modifiers.stream().noneMatch(entry -> entry.modifier().id().getNamespace().equals(JolCraft.MOD_ID)
                && entry.modifier().id().getPath().startsWith("attribute_trim_")), "Vanilla retrimming must remove obsolete gem bonuses");
        check(modifiers.stream().anyMatch(entry -> entry.modifier().id().equals(otherId)), "Retrimming must preserve unrelated modifiers");
        AttributeSmithingTrimRecipe gemRecipe = new AttributeSmithingTrimRecipe(
                Ingredient.of(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE), Ingredient.of(Items.IRON_CHESTPLATE),
                Ingredient.of(JolCraftItems.AEGISCORE_CUT.get()));
        ItemStack gemResult = gemRecipe.assemble(new SmithingRecipeInput(
                new ItemStack(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE), result,
                new ItemStack(JolCraftItems.AEGISCORE_CUT.get())), registries);
        check(!gemResult.isEmpty(), "The custom gem recipe must still produce armor through the shared assembly hook");
        check(gemResult.getAttributeModifiers().modifiers().stream().filter(entry ->
                        entry.modifier().id().getNamespace().equals(JolCraft.MOD_ID)
                                && entry.modifier().id().getPath().startsWith("attribute_trim_")).count() == 2,
                "The custom gem recipe must add exactly the two Aegiscore modifiers");
        helper.succeed();
    }

    @GameTest(template = "basic")
    public static void finalLapidaryInputAndBrokenToolsStillAwardStats(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        helper.getLevel().setBlock(pos, JolCraftBlocks.LAPIDARY_BENCH.get().defaultBlockState(), 3);
        LapidaryBenchBlockEntity bench = (LapidaryBenchBlockEntity) helper.getLevel().getBlockEntity(pos);
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "lapidary_stats", pos);
        try {
            // GameTest's default creative abilities otherwise bypass ItemStack durability damage.
            player.setGameMode(GameType.SURVIVAL);
            DwarfLoreAttachmentHelper.addUnlock(player, DwarfLoreKey.ANCIENT_GEMCRAFT);
            ItemStack chisel = new ItemStack(JolCraftItems.WOODEN_CHISEL.get());
            chisel.setDamageValue(chisel.getMaxDamage() - 1);
            bench.setItem(LapidaryBenchBlockEntity.SLOT_INPUT, new ItemStack(JolCraftItems.AEGISCORE.get()));
            bench.setItem(LapidaryBenchBlockEntity.SLOT_TOOL, chisel);
            bench.handleAction(player);
            check(bench.getItem(LapidaryBenchBlockEntity.SLOT_INPUT).isEmpty(), "The final gem must be consumed");
            check(bench.getItem(LapidaryBenchBlockEntity.SLOT_TOOL).isEmpty(), "The final tool durability must be consumed");
            check(player.getStats().getValue(Stats.CUSTOM.get(JolCraftStats.GEMS_CUT.get())) == 1,
                    "Cutting the final gem with a breaking chisel must award the stat");
            bench.setItem(LapidaryBenchBlockEntity.SLOT_OUTPUT, ItemStack.EMPTY);
            bench.setItem(LapidaryBenchBlockEntity.SLOT_INPUT, new ItemStack(JolCraftItems.GEODE_SMALL.get()));
            bench.setItem(LapidaryBenchBlockEntity.SLOT_TOOL, new ItemStack(JolCraftItems.WOODEN_ARTISAN_HAMMER.get()));
            bench.handleAction(player);
            check(bench.getItem(LapidaryBenchBlockEntity.SLOT_INPUT).isEmpty(), "The final geode must be consumed");
            check(player.getStats().getValue(Stats.CUSTOM.get(JolCraftStats.GEODES_CRACKED.get())) == 1,
                    "Cracking the final geode must award the stat");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    private static HearthBlockEntity ownedHearth(GameTestHelper helper, BlockPos pos, ServerPlayer player) {
        var state = JolCraftBlocks.HEARTH.get().defaultBlockState()
                .setValue(HearthBlock.HALF, DoubleBlockHalf.LOWER).setValue(HearthBlock.LIT, true);
        helper.getLevel().setBlock(pos, state, 3);
        helper.getLevel().setBlock(pos.above(), state.setValue(HearthBlock.HALF, DoubleBlockHalf.UPPER), 3);
        HearthBlockEntity hearth = (HearthBlockEntity) helper.getLevel().getBlockEntity(pos);
        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", player.getUUID());
        tag.putBoolean("lit_creative", true);
        hearth.loadWithComponents(tag, helper.getLevel().registryAccess());
        return hearth;
    }

    private static void tickEquipment(ServerPlayer player) {
        HearthEquipmentEvents.onPlayerTick(new PlayerTickEvent.Post(player));
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
