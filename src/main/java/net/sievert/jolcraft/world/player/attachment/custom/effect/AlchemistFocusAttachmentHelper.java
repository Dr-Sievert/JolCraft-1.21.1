package net.sievert.jolcraft.world.player.attachment.custom.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.world.player.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftAttachmentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AlchemistFocusAttachmentHelper
        extends JolCraftAttachmentHelper<AlchemistFocusAttachment> {

    private static final AlchemistFocusAttachmentHelper INSTANCE =
            new AlchemistFocusAttachmentHelper();

    private AlchemistFocusAttachmentHelper() {}

    @Override
    protected @NotNull AttachmentType<AlchemistFocusAttachment> type() {
        return JolCraftAttachments.ALCHEMIST_FOCUS.get();
    }

    public static AlchemistFocusAttachment get(Player player) {
        return INSTANCE.read(player);
    }

    public static void set(Player player, AlchemistFocusAttachment value) {
        INSTANCE.write(player, value);
    }

    public static boolean hasBoostedEffect(
            Player player,
            Holder<MobEffect> effect
    ) {
        ResourceLocation effectId = effectId(effect);

        return player != null
                && effectId != null
                && get(player).hasBoostedEffect(effectId);
    }

    public static void markBoostedEffect(
            Player player,
            Holder<MobEffect> effect
    ) {
        ResourceLocation effectId = effectId(effect);

        if (player == null
                || effectId == null
                || player.level().isClientSide()) {
            return;
        }

        AlchemistFocusAttachment current = get(player);
        AlchemistFocusAttachment updated =
                current.withBoostedEffect(effectId);

        if (updated != current) {
            set(player, updated);
        }
    }

    public static void clearBoostedEffect(
            Player player,
            Holder<MobEffect> effect
    ) {
        ResourceLocation effectId = effectId(effect);

        if (player == null
                || effectId == null
                || player.level().isClientSide()) {
            return;
        }

        AlchemistFocusAttachment current = get(player);
        AlchemistFocusAttachment updated =
                current.withoutBoostedEffect(effectId);

        if (updated != current) {
            set(player, updated);
        }
    }

    private static @Nullable ResourceLocation effectId(
            Holder<MobEffect> effect
    ) {
        if (effect == null) {
            return null;
        }

        return effect.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);
    }
}