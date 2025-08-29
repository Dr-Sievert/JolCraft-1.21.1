package net.sievert.jolcraft.entity.client.dwarf;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.AnimationState;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfBeardColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfEyeColor;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfVariant;

import java.util.EnumMap;

public class DwarfRenderState extends HumanoidRenderState {
    public final AnimationState idleAnimationState = new AnimationState();

    public final EnumMap<DwarfAnimationType, AnimationState> animationStates = new EnumMap<>(DwarfAnimationType.class);

    public AbstractDwarfEntity dwarf;
    public DwarfVariant variant;
    public DwarfBeardColor beard;
    public DwarfEyeColor eye;

    public DwarfRenderState() {
        for (DwarfAnimationType type : DwarfAnimationType.values()) {
            animationStates.put(type, new AnimationState());
        }
    }

}
