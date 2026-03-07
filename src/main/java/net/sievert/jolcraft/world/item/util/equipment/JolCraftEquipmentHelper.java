package net.sievert.jolcraft.world.item.util.equipment;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftEquipmentHelper {

    private JolCraftEquipmentHelper() {}

    // -------------------------------------------------------------------------
    // Canonical armor piece identity
    // -------------------------------------------------------------------------

    public enum ArmorPiece {
        HELMET(JolCraftDictionary.HELMET, EquipmentSlot.HEAD, ArmorType.HELMET),
        CHESTPLATE(JolCraftDictionary.CHESTPLATE, EquipmentSlot.CHEST, ArmorType.CHESTPLATE),
        LEGGINGS(JolCraftDictionary.LEGGINGS, EquipmentSlot.LEGS, ArmorType.LEGGINGS),
        BOOTS(JolCraftDictionary.BOOTS, EquipmentSlot.FEET, ArmorType.BOOTS);

        private final String suffix;
        private final EquipmentSlot slot;
        private final ArmorType armorType;

        ArmorPiece(String suffix, EquipmentSlot slot, ArmorType armorType) {
            this.suffix = suffix;
            this.slot = slot;
            this.armorType = armorType;
        }

        /** e.g. "helmet" used by name naming (models/trims/recipes). */
        public @NotNull String suffix() {
            return suffix;
        }

        public @NotNull EquipmentSlot slot() {
            return slot;
        }

        public @NotNull ArmorType armorType() {
            return armorType;
        }

        public static @Nullable ArmorPiece fromSlot(EquipmentSlot slot) {
            for (ArmorPiece p : values()) {
                if (p.slot == slot) return p;
            }
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Generic armor set grouping (no JolCraft items live here)
    // -------------------------------------------------------------------------

    /**
     * Generic grouping for a 4-piece armor set.
     * T can be DeferredItem<Item>, Supplier<Item>, ItemLike, etc.
     */
    public record ArmorSet<T>(
            @NotNull T helmet,
            @NotNull T chestplate,
            @NotNull T leggings,
            @NotNull T boots
    ) {

        public @NotNull T get(@NotNull ArmorPiece piece) {
            return switch (piece) {
                case HELMET -> helmet;
                case CHESTPLATE -> chestplate;
                case LEGGINGS -> leggings;
                case BOOTS -> boots;
            };
        }

        public @NotNull Stream<T> stream() {
            return Stream.of(helmet, chestplate, leggings, boots);
        }

        public @NotNull EnumMap<ArmorPiece, T> toMap() {
            EnumMap<ArmorPiece, T> out = new EnumMap<>(ArmorPiece.class);
            out.put(ArmorPiece.HELMET, helmet);
            out.put(ArmorPiece.CHESTPLATE, chestplate);
            out.put(ArmorPiece.LEGGINGS, leggings);
            out.put(ArmorPiece.BOOTS, boots);
            return out;
        }

        /**
         * Maps this set to another set (e.g. DeferredItem<Item> -> Item by calling .getEntityType()).
         */
        public <U> @NotNull ArmorSet<U> map(@NotNull Function<T, U> mapper) {
            return new ArmorSet<>(
                    mapper.apply(helmet),
                    mapper.apply(chestplate),
                    mapper.apply(leggings),
                    mapper.apply(boots)
            );
        }
    }

    public static <T> @NotNull ArmorSet<T> armorSet(T helmet, T chestplate, T leggings, T boots) {
        return new ArmorSet<>(helmet, chestplate, leggings, boots);
    }

    // -------------------------------------------------------------------------
    // Tiny component utilities (no policy)
    // -------------------------------------------------------------------------

    public static @Nullable Equippable equippable(ItemStack stack) {
        return stack.isEmpty() ? null : stack.get(DataComponents.EQUIPPABLE);
    }

    /** Returns the EQUIPPABLE slot if present, else null. */
    public static @Nullable EquipmentSlot equippableSlot(ItemStack stack) {
        Equippable eq = equippable(stack);
        return eq == null ? null : eq.slot();
    }

    /** Returns the armor slot if this stack is equippable AND the slot is armor, else null. */
    public static @Nullable EquipmentSlot armorSlot(ItemStack stack) {
        EquipmentSlot slot = equippableSlot(stack);
        return slot != null && slot.isArmor() ? slot : null;
    }

    /** Returns ArmorPiece if this stack is equippable armor, else null. */
    public static @Nullable ArmorPiece armorPiece(ItemStack stack) {
        EquipmentSlot slot = armorSlot(stack);
        return slot == null ? null : ArmorPiece.fromSlot(slot);
    }

    /** Returns true if stack is equippable and goes in an armor slot. */
    public static boolean isArmor(ItemStack stack) {
        return armorSlot(stack) != null;
    }

    /** Returns EQUIPPABLE.assetId() if present, else empty. */
    public static @NotNull Optional<?> equipmentAssetId(ItemStack stack) {
        Equippable eq = equippable(stack);
        return eq == null ? Optional.empty() : eq.assetId();
    }

    /**
     * Returns the armor slot if the stack matches a piece in the given armor set,
     * otherwise null.
     */
    public static @Nullable EquipmentSlot slotIfMatches(
            ItemStack stack,
            ArmorSet<DeferredItem<Item>> set
    ) {
        for (ArmorPiece piece : ArmorPiece.values()) {
            if (stack.is(set.get(piece).get())) {
                return piece.slot();
            }
        }
        return null;
    }
}