package net.sievert.jolcraft.world.item.custom.armor;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.armor.JolCraftArmorMaterials;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ArmorSetItem extends ArmorItem {

    private static final String NBT_ARMOR_SET_EFFECTS = JolCraftStrings.underscored(
            JolCraft.MOD_ID,
            JolCraftDictionary.ARMOR,
            JolCraftDictionary.SET,
            JolCraftStrings.plural(JolCraftDictionary.EFFECT)
    );

    private static final int EFFECT_DURATION = MobEffectInstance.INFINITE_DURATION;
    private static final boolean EFFECT_AMBIENT = false;
    private static final boolean EFFECT_PARTICLES = false;
    private static final boolean EFFECT_ICON = true;

    protected ArmorSetItem(
            Holder<ArmorMaterial> material,
            ArmorItem.Type type,
            Properties properties
    ) {
        super(material, type, properties);
    }

    protected abstract @NotNull JolCraftMaterials.Material material();
    protected abstract @NotNull List<ArmorSetEffect> effects();

    protected record ArmorSetEffect(
            Holder<MobEffect> effect,
            int amplifier
    ) {
        private MobEffectInstance createInstance() {
            return new MobEffectInstance(
                    this.effect,
                    EFFECT_DURATION,
                    this.amplifier,
                    EFFECT_AMBIENT,
                    EFFECT_PARTICLES,
                    EFFECT_ICON
            );
        }

        private String id() {
            return this.effect.unwrapKey()
                    .orElseThrow()
                    .location()
                    .toString();
        }
    }

    @Override
    public final void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        updateSetEffects(player, hasFullSet(player));
    }

    private boolean hasFullSet(Player player) {
        for (ArmorItem.Type type : JolCraftEquipmentHelper.PLAYER_ARMOR_TYPES) {
            ItemStack stack = player.getItemBySlot(type.getSlot());

            if (stack.isEmpty()) return false;

            var mat = JolCraftEquipmentHelper.armorMaterial(stack);
            if (mat == null || mat != JolCraftArmorMaterials.armorMaterial(material())) {
                return false;
            }
        }

        return true;
    }

    private void updateSetEffects(Player player, boolean hasFullSet) {
        for (ArmorSetEffect effect : effects()) {
            String id = effect.id();
            MobEffectInstance instance = player.getEffect(effect.effect());
            boolean hasEffect = instance != null;
            boolean owned = hasAppliedArmorSetEffect(player, id);

            if (hasFullSet) {
                if (!hasEffect) {
                    player.addEffect(effect.createInstance());
                    setAppliedArmorSetEffect(player, id);
                }

                continue;
            }

            if (owned && hasEffect) {
                if (instance.getDuration() == EFFECT_DURATION
                        && instance.getAmplifier() == effect.amplifier()) {
                    player.removeEffect(effect.effect());
                }

                clearAppliedArmorSetEffect(player, id);
                continue;
            }

            clearAppliedArmorSetEffect(player, id);
        }
    }

    private static CompoundTag getArmorSetEffects(Player player) {
        CompoundTag data = player.getPersistentData();

        if (!data.contains(NBT_ARMOR_SET_EFFECTS)) {
            data.put(NBT_ARMOR_SET_EFFECTS, new CompoundTag());
        }

        return data.getCompound(NBT_ARMOR_SET_EFFECTS);
    }

    private static boolean hasAppliedArmorSetEffect(Player player, String id) {
        return getArmorSetEffects(player).getBoolean(id);
    }

    private static void setAppliedArmorSetEffect(Player player, String id) {
        getArmorSetEffects(player).putBoolean(id, true);
    }

    private static void clearAppliedArmorSetEffect(Player player, String id) {
        CompoundTag effects = getArmorSetEffects(player);
        effects.remove(id);

        if (effects.isEmpty()) {
            player.getPersistentData().remove(NBT_ARMOR_SET_EFFECTS);
        }
    }
}