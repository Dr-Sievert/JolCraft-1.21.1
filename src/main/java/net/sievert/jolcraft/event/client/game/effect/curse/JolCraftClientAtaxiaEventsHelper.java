package net.sievert.jolcraft.event.client.game.effect.curse;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public final class JolCraftClientAtaxiaEventsHelper {

    private JolCraftClientAtaxiaEventsHelper() {}

    private enum AtaxiaInput {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        JUMP,
        SHIFT
    }

    private static final Map<AtaxiaInput, AtaxiaInput> mappings =
            new EnumMap<>(AtaxiaInput.class);

    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) {
            return;
        }

        MobEffectInstance ataxia =
                player.getEffect(JolCraftEffects.ATAXIA_CURSE);

        if (ataxia == null) {
            mappings.clear();
            return;
        }

        AtaxiaInput key =
                resolveInput(mc, event);

        if (key == null) {
            return;
        }

        if (event.getAction() == InputConstants.PRESS) {
            double curseVulnerability = Math.max(
                    0.0D,
                    player.getAttributeValue(
                            JolCraftAttributes.CURSE_VULNERABILITY
                    )
            );

            double chance = Math.min(
                    (0.20D
                            + 0.20D * ataxia.getAmplifier())
                            * Math.pow(
                            2.0D,
                            curseVulnerability
                    ),
                    1.0D
            );

            if (player.getRandom().nextDouble() < chance) {
                mappings.put(
                        key,
                        randomDifferent(
                                player.getRandom(),
                                key
                        )
                );
            } else {
                mappings.remove(key);
            }
        }

        if (event.getAction() == InputConstants.RELEASE) {
            mappings.remove(key);
        }
    }

    public static void onMovement(
            MovementInputUpdateEvent event
    ) {
        Player player = event.getEntity();

        MobEffectInstance ataxia =
                player.getEffect(JolCraftEffects.ATAXIA_CURSE);

        if (ataxia == null) {
            mappings.clear();
            return;
        }

        Input input = event.getInput();

        for (var entry : mappings.entrySet()) {
            if (!isPressed(
                    entry.getKey(),
                    input
            )) {
                continue;
            }

            clear(
                    entry.getKey(),
                    input
            );

            apply(
                    entry.getValue(),
                    input
            );
        }

        rebuild(input);
    }

    private static AtaxiaInput randomDifferent(
            RandomSource random,
            AtaxiaInput source
    ) {
        AtaxiaInput[] values =
                AtaxiaInput.values();

        AtaxiaInput result;

        do {
            result = values[
                    random.nextInt(values.length)
                    ];
        } while (result == source);

        return result;
    }

    @Nullable
    private static AtaxiaInput resolveInput(
            Minecraft mc,
            InputEvent.Key event
    ) {
        if (mc.options.keyUp.matches(
                event.getKey(),
                event.getScanCode()
        )) {
            return AtaxiaInput.UP;
        }

        if (mc.options.keyDown.matches(
                event.getKey(),
                event.getScanCode()
        )) {
            return AtaxiaInput.DOWN;
        }

        if (mc.options.keyLeft.matches(
                event.getKey(),
                event.getScanCode()
        )) {
            return AtaxiaInput.LEFT;
        }

        if (mc.options.keyRight.matches(
                event.getKey(),
                event.getScanCode()
        )) {
            return AtaxiaInput.RIGHT;
        }

        if (mc.options.keyJump.matches(
                event.getKey(),
                event.getScanCode()
        )) {
            return AtaxiaInput.JUMP;
        }

        if (mc.options.keyShift.matches(
                event.getKey(),
                event.getScanCode()
        )) {
            return AtaxiaInput.SHIFT;
        }

        return null;
    }

    private static boolean isPressed(
            AtaxiaInput type,
            Input input
    ) {
        return switch (type) {
            case UP -> input.up;
            case DOWN -> input.down;
            case LEFT -> input.left;
            case RIGHT -> input.right;
            case JUMP -> input.jumping;
            case SHIFT -> input.shiftKeyDown;
        };
    }

    private static void clear(
            AtaxiaInput type,
            Input input
    ) {
        switch (type) {
            case UP -> input.up = false;
            case DOWN -> input.down = false;
            case LEFT -> input.left = false;
            case RIGHT -> input.right = false;
            case JUMP -> input.jumping = false;
            case SHIFT -> input.shiftKeyDown = false;
        }
    }

    private static void apply(
            AtaxiaInput type,
            Input input
    ) {
        switch (type) {
            case UP -> input.up = true;
            case DOWN -> input.down = true;
            case LEFT -> input.left = true;
            case RIGHT -> input.right = true;
            case JUMP -> input.jumping = true;
            case SHIFT -> input.shiftKeyDown = true;
        }
    }

    private static void rebuild(Input input) {
        input.forwardImpulse = 0.0F;
        input.leftImpulse = 0.0F;

        if (input.up) {
            input.forwardImpulse += 1.0F;
        }

        if (input.down) {
            input.forwardImpulse -= 1.0F;
        }

        if (input.left) {
            input.leftImpulse += 1.0F;
        }

        if (input.right) {
            input.leftImpulse -= 1.0F;
        }
    }
}