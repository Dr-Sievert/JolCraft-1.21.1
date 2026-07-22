package net.sievert.jolcraft.world.player.attachment.custom.hearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftPersistentAttachment;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class HearthAttachment extends JolCraftPersistentAttachment<HearthAttachment> {

    private static final String TAG_LAST_LIT_DAY =
            JolCraftStrings.underscored(JolCraftDictionary.LAST, JolCraftDictionary.LIGHT, JolCraftDictionary.DAY);

    private static final String TAG_ACTIVE_HEARTH_POS =
            JolCraftStrings.underscored(JolCraftDictionary.ACTIVE, JolCraftDictionary.HEARTH, JolCraftDictionary.POSITION);

    public static final Codec<HearthAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.LONG.fieldOf(TAG_LAST_LIT_DAY).forGetter(HearthAttachment::lastLitDay),
                    BlockPos.CODEC.optionalFieldOf(TAG_ACTIVE_HEARTH_POS)
                            .forGetter(attachment -> Optional.ofNullable(attachment.activeHearthPos))
            ).apply(instance, (lastLitDay, activeHearthPos) -> new HearthAttachment(lastLitDay, activeHearthPos.orElse(null))));

    private final long lastLitDay;
    private final @Nullable BlockPos activeHearthPos;

    public HearthAttachment() {
        this(-1L, null);
    }

    public HearthAttachment(long lastLitDay, @Nullable BlockPos activeHearthPos) {
        this.lastLitDay = lastLitDay;
        this.activeHearthPos = activeHearthPos == null ? null : activeHearthPos.immutable();
    }

    public long lastLitDay() {
        return lastLitDay;
    }

    public HearthAttachment withLastLitDay(long day) {
        return lastLitDay == day ? this : new HearthAttachment(day, activeHearthPos);
    }

    public HearthAttachment clearLastLitDay() {
        return lastLitDay == -1L ? this : new HearthAttachment(-1L, activeHearthPos);
    }

    public @Nullable BlockPos activeHearthPos() {
        return activeHearthPos;
    }

    public boolean hasActiveHearth() {
        return activeHearthPos != null;
    }

    public boolean isActiveHearth(BlockPos pos) {
        return activeHearthPos != null && activeHearthPos.equals(pos);
    }

    public HearthAttachment withActiveHearthPos(@Nullable BlockPos pos) {
        BlockPos immutablePos = pos == null ? null : pos.immutable();
        return Objects.equals(activeHearthPos, immutablePos) ? this : new HearthAttachment(lastLitDay, immutablePos);
    }

    public HearthAttachment clearActiveHearthPos() {
        return activeHearthPos == null ? this : new HearthAttachment(lastLitDay, null);
    }

    @Override
    public Codec<HearthAttachment> codec() {
        return CODEC;
    }
}