package net.sievert.jolcraft.world.entity.client.util.dwarf.animation;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.world.entity.AnimationState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public final class DwarfAnimationHelper {

    private static final Map<DwarfActionType, AnimationDefinition> BY_TYPE =
            new EnumMap<>(DwarfActionType.class);

    private static final Map<DwarfActionType.Subtype, AnimationDefinition> BY_SUBTYPE =
            new EnumMap<>(DwarfActionType.Subtype.class);

    private static final Map<AbstractDwarfEntity, PersistentState> STATE_BY_ENTITY = new WeakHashMap<>();

    static {
        BY_TYPE.put(DwarfActionType.IDLE, DwarfAnimations.IDLE);
        BY_TYPE.put(DwarfActionType.ATTACK, DwarfAnimations.ATTACK);
        BY_TYPE.put(DwarfActionType.BLOCK, DwarfAnimations.BLOCK);
        BY_TYPE.put(DwarfActionType.DRINK, DwarfAnimations.DRINK);
        BY_TYPE.put(DwarfActionType.INSPECT, DwarfAnimations.INSPECT);

        BY_SUBTYPE.put(DwarfActionType.Subtype.ATTACK_HEAVY, DwarfAnimations.ATTACK_HEAVY);
    }

    private DwarfAnimationHelper() {}

    private static final class PersistentState {
        private final EnumMap<DwarfActionType, AnimationState> animationStates =
                new EnumMap<>(DwarfActionType.class);

        private DwarfActionType syncedType = DwarfActionType.IDLE;
        @Nullable
        private DwarfActionType.Subtype syncedSubtype = null;

        private DwarfActionType playingType = DwarfActionType.IDLE;
        @Nullable
        private DwarfActionType.Subtype playingSubtype = null;

        private int clipEndTick = 0;

        private PersistentState() {
            for (DwarfActionType type : DwarfActionType.values()) {
                this.animationStates.put(type, new AnimationState());
            }
        }
    }

    private static PersistentState getOrCreate(@Nullable AbstractDwarfEntity dwarf) {
        if (dwarf == null) {
            return new PersistentState();
        }
        return STATE_BY_ENTITY.computeIfAbsent(dwarf, ignored -> new PersistentState());
    }

    @Nullable
    public static AnimationDefinition resolve(
            @Nullable DwarfActionType type,
            @Nullable DwarfActionType.Subtype subtype
    ) {
        if (subtype != null) {
            AnimationDefinition def = BY_SUBTYPE.get(subtype);
            if (def != null) {
                return def;
            }
        }
        return type == null ? null : BY_TYPE.get(type);
    }

    private static int clipLengthTicks(@Nullable DwarfActionType type, @Nullable DwarfActionType.Subtype subtype) {
        AnimationDefinition def = resolve(type, subtype);
        if (def == null) {
            return 0;
        }

        // seconds -> ticks, rounded up, plus 1 safety tick so the last frame is visible
        return Math.max(1, (int) Math.ceil(def.lengthInSeconds() * 20.0F) + 1);
    }

    private static boolean sameAction(
            @Nullable DwarfActionType aType,
            @Nullable DwarfActionType.Subtype aSubtype,
            @Nullable DwarfActionType bType,
            @Nullable DwarfActionType.Subtype bSubtype
    ) {
        return aType == bType && aSubtype == bSubtype;
    }

    private static boolean isOneShot(@Nullable DwarfActionType type) {
        return type != null && type != DwarfActionType.IDLE;
    }

    private static void startPlaying(
            @NotNull PersistentState state,
            @NotNull DwarfActionType type,
            @Nullable DwarfActionType.Subtype subtype,
            int tick
    ) {
        state.playingType = type;
        state.playingSubtype = subtype;
        state.clipEndTick = tick + clipLengthTicks(type, subtype);

        for (DwarfActionType actionType : DwarfActionType.values()) {
            AnimationState anim = state.animationStates.get(actionType);
            if (anim == null) continue;

            if (actionType == type) {
                anim.start(tick);
            } else {
                anim.stop();
            }
        }
    }

    private static void stopAll(@NotNull PersistentState state) {
        for (AnimationState anim : state.animationStates.values()) {
            anim.stop();
        }
        state.playingType = DwarfActionType.IDLE;
        state.playingSubtype = null;
        state.clipEndTick = 0;
    }

    public static void animate(@NotNull AbstractDwarfEntity dwarf, @NotNull DwarfModel<?> model, float ageInTicks) {
        PersistentState state = getOrCreate(dwarf);

        DwarfActionType syncedType = dwarf.getCurrentActionType();
        DwarfActionType.Subtype syncedSubtype = dwarf.getCurrentActionSubtype();
        int tick = dwarf.tickCount;

        boolean syncedChanged = !sameAction(state.syncedType, state.syncedSubtype, syncedType, syncedSubtype);
        state.syncedType = syncedType;
        state.syncedSubtype = syncedSubtype;

        if (syncedChanged && syncedType != DwarfActionType.IDLE) {
            startPlaying(state, syncedType, syncedSubtype, tick);
        }

        // If the server already went back to IDLE, keep the one-shot clip alive until its local duration finishes.
        if (state.playingType != DwarfActionType.IDLE) {
            if (tick >= state.clipEndTick) {
                if (syncedType != DwarfActionType.IDLE) {
                    startPlaying(state, syncedType, syncedSubtype, tick);
                } else {
                    stopAll(state);
                }
            }
        } else if (syncedType != DwarfActionType.IDLE && isOneShot(syncedType)) {
            startPlaying(state, syncedType, syncedSubtype, tick);
        }

        AnimationDefinition def = resolve(state.playingType, state.playingSubtype);
        if (def == null) {
            return;
        }

        AnimationState anim = state.animationStates.get(state.playingType);
        if (anim != null) {
            model.forwardAnimation(anim, def, ageInTicks, 1.0F);
        }
    }
}