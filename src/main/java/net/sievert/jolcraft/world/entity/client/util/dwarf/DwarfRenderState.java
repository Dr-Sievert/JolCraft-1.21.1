package net.sievert.jolcraft.world.entity.client.util.dwarf;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.AnimationState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.entity.custom.dwarf.variant.DwarfBeardColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.variant.DwarfEyeColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.variant.DwarfVariant;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Holds per-frame state for rendering a dwarf entity.
 * Client-only. Use {@link #getOrCreate(AbstractDwarfEntity)} from renderer/model code.
 */
@OnlyIn(Dist.CLIENT)
public class DwarfRenderState extends HumanoidRenderState {

    /**
     * Client-side persistent render state per dwarf entity.
     */
    private static final Map<AbstractDwarfEntity, DwarfRenderState> STATES = new WeakHashMap<>();

    /**
     * Returns the persistent render state for the given dwarf entity, creating it if necessary.
     */
    public static DwarfRenderState getOrCreate(AbstractDwarfEntity entity) {
        return STATES.computeIfAbsent(entity, e -> new DwarfRenderState());
    }

    /**
     * Per-action animation states used to track and drive custom dwarf animations.
     */
    public final EnumMap<DwarfActionType, AnimationState> animationStates = new EnumMap<>(DwarfActionType.class);

    /**
     * The currently active top-level action type (e.g., ATTACK, BLOCK, DRINK).
     */
    public DwarfActionType currentActionType = DwarfActionType.IDLE;

    /**
     * The current action subtype, if one is active (e.g., ATTACK_AXE, BLOCK_SHIELD).
     */
    public DwarfActionType.Subtype currentActionSubtype = null;

    /** The dwarf entity being rendered (optional, rarely needed). */
    public AbstractDwarfEntity dwarf;

    /**
     * The visual variant of this dwarf (e.g., skin tone or body type).
     */
    public DwarfVariant variant;

    /**
     * The beard color of the dwarf, used in rendering facial layers.
     */
    public DwarfBeardColor beard;

    /**
     * The eye color of the dwarf, used in rendering overlays or glow effects.
     */
    public DwarfEyeColor eye;

    /**
     * Creates and initializes all action-specific animation states.
     * Called once per dwarf instance.
     */
    public DwarfRenderState() {
        for (DwarfActionType type : DwarfActionType.values()) {
            animationStates.put(type, new AnimationState());
        }
    }
}